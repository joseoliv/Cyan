package meta.cyanLang;

import meta.CyanMetaobjectMacro;
import meta.ICompilerMacro_parsing;
import meta.ICompiler_semAn;
import meta.WrEnv;
import meta.WrExpr;
import meta.Token;
import meta.lexer.MetaLexer;


/**
 * Macro <code>printexpr</code> which is used as <br>
 * <code>printexpr table get: "name";</code><br>
 * At runtime, it will print <code>'table get: "name"' == value</code> in which <code>value</code> is
 * the runtime value of the expression.
   @author jose
 */
public class CyanMetaobjectMacroPrintexpr extends CyanMetaobjectMacro {

	public CyanMetaobjectMacroPrintexpr() {
		super(new String[] { "printexpr" }, new String[] { "printexpr" });
	}

	@Override
	public void parsing_parseMacro(ICompilerMacro_parsing compiler_parsing) {

		offsetStartLine = compiler_parsing.getSymbol().getColumnNumber();

		compiler_parsing.next();
		expr = compiler_parsing.expr();
		if ( compiler_parsing.getThereWasErrors() )
			return ;
		if ( compiler_parsing.getSymbol().token == Token.SEMICOLON ) {
			compiler_parsing.next();
		}

		// // ((AnnotationMacroCall ) this.getAnnotation()).setInfo_parsing( new Tuple2<Expr, Integer>(expr, offsetStartLine) );
		return ;
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		// // Tuple2<Expr, Integer> info = (Tuple2<Expr, Integer> ) ((AnnotationMacroCall ) this.getAnnotation()).getInfo_parsing();
		// // Expr expr = info.f1;
		WrEnv env = compiler_semAn.getEnv();
		if ( env.isThereWasError() )
			return null;

		// // int offsetStartLine = info.f2;
		StringBuffer s = new StringBuffer();
		if ( offsetStartLine > CyanMetaobjectMacro.sizeWhiteSpace )
			offsetStartLine = CyanMetaobjectMacro.sizeWhiteSpace;
		String identSpace = CyanMetaobjectMacro.whiteSpace.substring(0, offsetStartLine);
		String strExpr = expr.asString();
		//System.out.println(strExpr);
		s.append(identSpace + "Out println: \"'" + MetaLexer.escapeJavaString(expr.asString()) + "' == \" ++ (" + strExpr + ");\n");

		return s;
	}


	private WrExpr expr;
	private int offsetStartLine;
}
