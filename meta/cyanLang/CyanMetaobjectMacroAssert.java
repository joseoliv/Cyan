package meta.cyanLang;

import meta.CyanMetaobjectMacro;
import meta.ICompilerMacro_parsing;
import meta.ICompiler_semAn;
import meta.WrAnnotationMacroCall;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprIdentStar;
import meta.WrExprMessageSendWithKeywordsToExpr;
import meta.WrMessageBinaryOperator;
import meta.WrType;
import meta.Token;
import meta.lexer.MetaLexer;

/**
 * This class represents macro 'assert' which is used as<br>
 * <code>    assert boolExpr;<code><br>
 * At runtime, if <code>boolExpr</code> is false, an error message is issued. The program is NOT terminated.
 *
   @author José
 */
public class CyanMetaobjectMacroAssert extends CyanMetaobjectMacro {


	public CyanMetaobjectMacroAssert() {
		/*
		 * there is only one macro keyword
		 */
		super(new String[] { "assert" }, new String[] { "assert" });
	}


	/**
	 * parse the macro call
	 */
	@Override
	public void parsing_parseMacro(ICompilerMacro_parsing compiler_parsing) {

    	  /*
    	   *   compiler_parsing.getSymbol() is the lexical symbol for 'assert'
    	   *   from this symbol we get its line number and column
    	   */
		lineNumberStartMacro = compiler_parsing.getSymbol().getLineNumber();
		offsetStartLine = compiler_parsing.getSymbol().getColumnNumber();
		   // get past symbol 'assert'
		compiler_parsing.next();
		   // calls the compiler to parse the expression that should come
           // after 'assert'. The expression is kept in field 'assertExpr'
		assertExpr = compiler_parsing.expr();
		   // if there was any errors when parsing the expression, returns
		   // Any errors will be reported back to the Cyan compiler
		if ( compiler_parsing.getThereWasErrors() )
			return ;
		  // does the macro ends with ';' ?
		if ( compiler_parsing.getSymbol().token == Token.SEMICOLON ) {
			compiler_parsing.next();
		}
	}

	/**
	 * generate code for the macro. The string returned will replace the macro call
	 */
	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		  // the annotation is the macro call, an object of AnnotationMacroCall
		final WrAnnotationMacroCall annotation = (WrAnnotationMacroCall ) this.getAnnotation();
		   // env keeps all the environment of the call: the current method, prototype, etc
		final WrEnv env = compiler_semAn.getEnv();
		  // if there was any errors before the macro call, return
		if ( env.isThereWasError() )
			return null;
		  // the  type of the assert expression should be Boolean or Dyn
		WrType exprType = assertExpr.getType(env);
		if ( exprType != WrType.Boolean && exprType != WrType.Dyn ) {
			compiler_semAn.error(assertExpr.getFirstSymbol(), "Expression of type Boolean or Dyn expected");
			return null;
		}

		// the if statement below is not really necessary. It just
		// adds a gentle message send if the expression is false
		WrExpr firstExpr = null;
		if ( assertExpr instanceof WrExprMessageSendWithKeywordsToExpr ) {
			if (  ((WrExprMessageSendWithKeywordsToExpr) assertExpr).getMessage() instanceof WrMessageBinaryOperator ) {
				final WrMessageBinaryOperator mso = (WrMessageBinaryOperator ) ((WrExprMessageSendWithKeywordsToExpr) assertExpr).getMessage();
				if ( mso.getkeywordParameterList().get(0).getkeyword().token == Token.EQ ) {
					/*
					 * something as
					 *     assert s == "a";
					 */
					firstExpr = ((WrExprMessageSendWithKeywordsToExpr) assertExpr).getReceiverExpr();
					if ( !(firstExpr instanceof WrExprIdentStar) ) {
						firstExpr = null;
					}
				}
			}
		}
          // the generated code is put in string 's'.
		  // the line number of the assert statement is recovered
		  // from field 'lineNumberStartMacro'
		  // Note that method 'asString' of 'assertExpr' is used
		  // for getting the expression code.
		final StringBuffer s = new StringBuffer();
		if ( offsetStartLine > CyanMetaobjectMacro.sizeWhiteSpace )
			offsetStartLine = CyanMetaobjectMacro.sizeWhiteSpace;
		final String identSpace = CyanMetaobjectMacro.whiteSpace.substring(0, offsetStartLine);
		s.append("\n");
		s.append(identSpace + "if !(");
		s.append(assertExpr.asString() + ") {\n");
		s.append(identSpace + identSpace + "\"Assert failed in line " + lineNumberStartMacro + " of prototype '" + annotation.getPackageOfAnnotation() +
				"." + annotation.getPrototypeOfAnnotation() + "'\" println;\n");
		final String str = MetaLexer.escapeJavaString(assertExpr.asString());
		s.append(identSpace + identSpace + "\"Assert expression: '" + str + "'\" println;\n");
		if ( firstExpr != null ) {
			s.append(identSpace + identSpace + "\"'" + firstExpr.asString() + "' = \" print;\n");
			s.append(identSpace + identSpace + firstExpr.asString() + " println;\n");

		}
		s.append(identSpace + identSpace + "};\n");
		return s;
	}

	private WrExpr assertExpr;
	private int offsetStartLine;
	private int lineNumberStartMacro;
}
