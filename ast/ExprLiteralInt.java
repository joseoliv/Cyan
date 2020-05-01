/**
 *
 */
package ast;

import lexer.Symbol;
import lexer.SymbolIntLiteral;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprLiteralInt;
import saci.Env;

/** Represents an int literal such as
 *      1, 2, 3I, 5I
 * @author José
 *
 */
public class ExprLiteralInt extends ExprLiteralNumber {

	/**
	 * @param symbol
	 */
	public ExprLiteralInt(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
	}

	public ExprLiteralInt(Symbol symbol, Symbol prefix, MethodDec currentMethod) {
		super(symbol, currentMethod);
		this.prefix = prefix;
	}

	@Override
	public WrExprLiteralInt getI() {
		return new WrExprLiteralInt(this);
	}



	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);
		type = Type.Int;
	}


	private String genJavaString() {
		int n = ((SymbolIntLiteral ) symbol).getIntValue();

		if ( prefix != null && prefix.token == Token.MINUS  ) {
			return "(new " + MetaHelper.IntInJava + "( (int ) " + "-"
					+ ((SymbolIntLiteral) symbol).getIntValue() + "))";
		}
		else if ( n == 0 )
			return MetaHelper.IntInJava + ".zero";
		else if ( n == 1 )
			return MetaHelper.IntInJava + ".one";
		else if ( n == 2 )
			return MetaHelper.IntInJava + ".two";
		else
			return "(new " + MetaHelper.IntInJava + "( (int ) "
					+ ((SymbolIntLiteral) symbol).getIntValue() + "))";
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
		pw.print(genJavaString());
	}
	*/


	@Override
	public Object getJavaValue() {
		int n = ((SymbolIntLiteral ) symbol).getIntValue();

		/*
		int n = Integer.parseInt(symbol.getSymbolString());
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
		return "Integer";
	}

	@Override
	public Object eval(EvalEnv ee) {

		return ExprLiteral.evalLiteral(this, "Int", ee, int.class);
	}


}
