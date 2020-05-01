/**
 *
 */
package ast;

import lexer.Symbol;
import lexer.SymbolFloatLiteral;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprLiteralFloat;
import saci.Env;

/** Represents a float literal such as 2.71F
 * @author José
 *
 */
public class ExprLiteralFloat extends ExprLiteralNumber {

	/**
	 * @param symbol
	 */
	public ExprLiteralFloat(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
	}

	public ExprLiteralFloat(Symbol symbol, Symbol prefix, MethodDec currentMethod) {
		super(symbol, currentMethod);
		this.prefix = prefix;
	}


	@Override
	public WrExprLiteralFloat getI() {
		return new WrExprLiteralFloat(this);
	}


	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);
		type = Type.Float;
	}


	private String genJavaString(Env env) {

		return "(new " + MetaHelper.FloatInJava + "( (float ) " + (prefix != null && prefix.token == Token.MINUS ? "-" : "") +
		((SymbolFloatLiteral ) symbol).getOriginalFloatString() + "))";
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		/*
		String s = genJavaString(env);
		String varName = NameServer.nextLocalVariableName();
		pw.printlnIdent(varName + " = " + s + ";");
		return varName; */
		return genJavaString(env);
	}

	/*
	public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		pw.print(genJavaString(env));
	}
	*/


	@Override
	public Object getJavaValue() {
		float n = ((SymbolFloatLiteral ) symbol).getFloatValue();

		/*
		float n = Float.parseFloat(symbol.getSymbolString());
		*/
		if ( prefix != null && prefix.token == Token.MINUS ) {
			return -n;
		}
		else
			return n;
	}

	@Override
	public StringBuffer getStringJavaValue() {
		return new StringBuffer("\"" + getJavaValue() + "\"");
	}


	@Override
	public String getJavaType() {
		return "Float";
	}

	@Override
	public Object eval(EvalEnv ee) {

		return ExprLiteral.evalLiteral(this, "Float", ee, float.class);
	}


}
