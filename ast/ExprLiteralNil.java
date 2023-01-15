/**
 *
 */
package ast;

import java.lang.reflect.Field;
import lexer.Symbol;
import meta.Function0;
import meta.MetaHelper;
import meta.WrExprLiteralNil;
import saci.Env;

/** represents the object nil of Cyan
 * @author José
 *
 */
public class ExprLiteralNil extends ExprLiteral {

	/**
	 * @param symbol
	 */
	public ExprLiteralNil(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
	}

	@Override
	public WrExprLiteralNil getI() {
		return new WrExprLiteralNil(this);
	}



	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);
		type = Type.Nil;
	}


	@SuppressWarnings("static-method")
	private String genJavaString(Env env) {
		return MetaHelper.NilInJava + ".prototype";
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		return genJavaString(env);
	}

	/*
	public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		pw.print(genJavaString(env));
	}
	*/


	@Override
	public Object getJavaValue() {
		return symbol.getSymbolString() ;
	}

	@Override
	public StringBuffer getStringJavaValue() {
		return new StringBuffer("\"null\"");
	}


	@Override
	public String getJavaType() {
		return "Object";
	}


	@Override
	public String metaobjectParameterAsString(Function0 inError) {
		// "\"" + t.f2.asString() + "\""
		return "\"" + asString() + "\"";
	}

	@Override
	public Object eval(EvalEnv ee) {
		//TypeJavaRef aClass = ee.getCyanLangPackage().getJvmTypeClassMap().get( MetaHelper.getJavaName("Nil") );
		//Class<?> nilClass = aClass.getaClass(ee.env, this.getFirstSymbol());
		Class<?> nilClass = EvalEnv.nil;
		Object nilValue = null;
		try {
			Field p = nilClass.getField("prototype");
			nilValue = p.get(nilClass);
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException|IllegalAccessException e) {
			return null;
		}
		return nilValue;
	}


}
