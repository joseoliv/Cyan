package meta.cyanLang;

import meta.CyanMetaobjectMacro;
import meta.ICompilerMacro_dpa;
import meta.ICompiler_dsa;
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
	public void dpa_parseMacro(ICompilerMacro_dpa compiler_dpa) {

		offsetStartLine = compiler_dpa.getSymbol().getColumnNumber();

		compiler_dpa.next();
		expr = compiler_dpa.expr();
		if ( compiler_dpa.getThereWasErrors() )
			return ;
		if ( compiler_dpa.getSymbol().token == Token.SEMICOLON ) {
			compiler_dpa.next();
		}

		// // ((AnnotationMacroCall ) this.getMetaobjectAnnotation()).setInfo_dpa( new Tuple2<Expr, Integer>(expr, offsetStartLine) );
		return ;
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		// // Tuple2<Expr, Integer> info = (Tuple2<Expr, Integer> ) ((AnnotationMacroCall ) this.getMetaobjectAnnotation()).getInfo_dpa();
		// // Expr expr = info.f1;
		WrEnv env = compiler_dsa.getEnv();
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
