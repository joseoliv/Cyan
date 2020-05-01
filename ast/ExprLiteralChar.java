/**
 *
 */
package ast;

import lexer.Symbol;
import meta.Function0;
import meta.MetaHelper;
import meta.WrExprLiteralChar;
import meta.lexer.MetaLexer;
import saci.CyanEnv;
import saci.Env;


/** Represents a character literal such as
 *      'a',  'T'
 *
 * @author José
 *
 */
public class ExprLiteralChar extends ExprLiteral {


	@Override
	public WrExprLiteralChar getI() {
		return new WrExprLiteralChar(this);
	}


	/**
	 * @param symbol
	 */
	public ExprLiteralChar(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
	}


	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);

		type = Type.Char;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

	    pw.print("'" + symbol.symbolString + "'");
	}

	private String genJavaString() {
		return "(new " + MetaHelper.CharInJava + "('" + symbol.getSymbolString() + "'))";
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		/*
		String s = genJavaString(env);
		String varName = NameServer.nextLocalVariableName();
		pw.printlnIdent(varName + " = " + s + ";");
		return varName;
		*/
		return genJavaString();
	}

	/*
	public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		pw.print(genJavaString(env));
	}
	*/


	@Override
	public Object getJavaValue() {
		return MetaLexer.unescapeJavaString(symbol.getSymbolString()).charAt(0);
		// return "\'" + Lexer.unescapeJavaString(symbol.getSymbolString()).charAt(0) + "\'" ;
	}

	@Override
	public StringBuffer getStringJavaValue() {
		return new StringBuffer("\"" + symbol.getSymbolString() + "\"");
	}


	@Override
	public String getJavaType() {
		return "Character";
	}


	@Override
	public String metaobjectParameterAsString(Function0 inError) {
		return asString();
	}

	@Override
	public Object eval(EvalEnv ee) {

		return ExprLiteral.evalLiteral(this, "Char", ee, char.class);
	}



}
