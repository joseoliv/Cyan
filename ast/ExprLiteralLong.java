/**
 *
 */
package ast;

import lexer.Symbol;
import lexer.SymbolLongLiteral;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprLiteralLong;
import saci.Env;

/** Represents a long literal such as
 *       1L,  5636363636L
 * @author José
 *
 */
public class ExprLiteralLong extends ExprLiteralNumber {

	/**
	 * @param symbol
	 */
	public ExprLiteralLong(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
	}

	public ExprLiteralLong(Symbol symbol, Symbol prefix, MethodDec currentMethod) {
		super(symbol, currentMethod);
		this.prefix = prefix;
	}

	@Override
	public WrExprLiteralLong getI() {
		return new WrExprLiteralLong(this);
	}



	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);
		type = Type.Long;
	}


	private String genJavaString(Env env) {

		return "(new " + MetaHelper.LongInJava + "( (long ) " + (prefix != null && prefix.token == Token.MINUS ? "-" : "")
				+ ((SymbolLongLiteral ) symbol).getLongValue() + "))";
	}


	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		/*
		String s = genJavaString(env);
		String varName = NameServer.nextLocalVariableName();
		pw.printlnIdent(varName + " = " + s + ";");
		return varName;
		*/
		return genJavaString(env);
	}

	/*
	public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		pw.print(genJavaString(env));
	}
	*/


	@Override
	public Object getJavaValue() {
		long n = ((SymbolLongLiteral ) symbol).getLongValue();

		/*
		long n = Long.parseLong(symbol.getSymbolString());
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
		return "Long";
	}

	@Override
	public Object eval(EvalEnv ee) {

		return ExprLiteral.evalLiteral(this, "Long", ee, long.class);
	}



}
