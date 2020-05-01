/**
 *
 */
package ast;

import lexer.Symbol;
import lexer.SymbolByteLiteral;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprLiteralByte;
import saci.Env;

/** Represents a byte literal such as 1B
 * @author José
 *
 */
public class ExprLiteralByte extends ExprLiteralNumber {

	/**
	 * @param symbol
	 */
	public ExprLiteralByte(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
		prefix = null;
	}
	public ExprLiteralByte(Symbol symbol, Symbol prefix, MethodDec currentMethod) {
		super(symbol, currentMethod);
		this.prefix = prefix;
	}


	@Override
	public WrExprLiteralByte getI() {
		return new WrExprLiteralByte(this);
	}



	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);

		type = Type.Byte;
	}

	private String genJavaString() {


		return "(new " + MetaHelper.ByteInJava + "( (byte ) " + (prefix != null && prefix.token == Token.MINUS ? "-" : "") +
		   ((SymbolByteLiteral ) symbol).getByteValue() + "))";
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

	/*public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		pw.print(genJavaString());
	}*/


	@Override
	public Object getJavaValue() {
		byte n = ((SymbolByteLiteral ) symbol).getByteValue();
		/*
		byte n = Byte.parseByte(symbol.getSymbolString());
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
		return "Byte";
	}


	@Override
	public Object eval(EvalEnv ee) {

		return ExprLiteral.evalLiteral(this, "Byte", ee, byte.class);
	}


}
