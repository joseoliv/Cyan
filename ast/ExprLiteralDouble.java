/**
 *
 */
package ast;

import lexer.Symbol;
import lexer.SymbolDoubleLiteral;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprLiteralDouble;
import saci.Env;

/**
 * represents a double literal such as  1.05D
 *
 * @author José
 *
 */
public class ExprLiteralDouble extends ExprLiteralNumber {

	/**
	 * @param symbol
	 */
	public ExprLiteralDouble(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
	}

	public ExprLiteralDouble(Symbol symbol, Symbol prefix, MethodDec currentMethod) {
		super(symbol, currentMethod);
		this.prefix = prefix;
	}


	@Override
	public WrExprLiteralDouble getI() {
		return new WrExprLiteralDouble(this);
	}




	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);
		type = Type.Double;
	}


	private String genJavaString(Env env) {

		return "(new " + MetaHelper.DoubleInJava + "( (double ) " + (prefix != null && prefix.token == Token.MINUS ? "-" : "") +
				((SymbolDoubleLiteral )symbol).getOriginalDoubleString() + "))";
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

		double n = ((SymbolDoubleLiteral ) symbol).getDoubleValue();
		/*
		double n = Double.parseDouble(symbol.getSymbolString());
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
		return "Double";
	}


	@Override
	public Object eval(EvalEnv ee) {

		return ExprLiteral.evalLiteral(this, "Double", ee, double.class);
	}



}
