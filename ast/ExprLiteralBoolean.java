/**
 *
 */
package ast;


import lexer.Symbol;
import meta.Function0;
import meta.MetaHelper;
import meta.WrExprLiteralBoolean;
import saci.Env;
import saci.NameServer;

/**
 * @author José
 *
 */
public class ExprLiteralBoolean extends ExprLiteral {


	@Override
	public WrExprLiteralBoolean getI() {
		return new WrExprLiteralBoolean(this);
	}

	/**
	 * @param symbol
	 */
	public ExprLiteralBoolean(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
	}


	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);

		type = Type.Boolean;
	}


	private String genJavaString() {
		return MetaHelper.BooleanInJava + "." + (symbol.getSymbolString().equals("true") ? "cyTrue" : "cyFalse") ;
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String s = genJavaString();
		String varName = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent(MetaHelper.getJavaName("Boolean") + " " + varName + " = " + s + ";");
		return varName;
	}

	/*public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		pw.print(genJavaString());
	}*/


	@Override
	public Object getJavaValue() {
		return symbol.getSymbolString().compareTo("true") == 0 ? true : false;
	}


	@Override
	public StringBuffer getStringJavaValue() {
		return new StringBuffer(symbol.getSymbolString().compareTo("true") == 0 ? "true" : "false");
	}


	@Override
	public String getJavaType() {
		return "Boolean";
	}

	@Override
	public String metaobjectParameterAsString(Function0 inError) {
		return symbol.getSymbolString().compareTo("true") == 0 ? "true" : "false";
	}

	@Override
	public Object eval(EvalEnv ee) {

		return ExprLiteral.evalLiteral(this, "Boolean", ee, boolean.class);
	}



}
