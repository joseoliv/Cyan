/**

 */
package ast;

import lexer.Symbol;
import meta.Function0;
import meta.MetaHelper;
import meta.WrExprAnyLiteralIdent;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** Represents a basic type or identifier in a list of argument of a metaobject annotation. For example, in
 *      @init(name, age)
 *
 *  both name and age will be represented by an object of this class. Both will
 *  be transformed into strings of Java.
   @author José

 */
public class ExprAnyLiteralIdent extends ExprAnyLiteral {


	@Override
	public WrExprAnyLiteralIdent getI() {
		if (iExprAnyLiteralIdent == null ) {
			iExprAnyLiteralIdent = new WrExprAnyLiteralIdent(this);
		}
		return iExprAnyLiteralIdent;
	}

	private WrExprAnyLiteralIdent iExprAnyLiteralIdent = null;

	/**
	 * @param symbol
	 */
	public ExprAnyLiteralIdent(ExprIdentStar identExpr, MethodDec method) {
		super(method);
		this.identExpr = identExpr;
	}


	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);

		type = Type.String;
	}



	private String genJavaString() {
		return identExpr.getName();
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String varName = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent(identExpr.getJavaName() + " " + varName + " = \"" + identExpr.getName() + "\";");
		return varName;
	}

	/*public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		pw.print(genJavaString());
	}*/

	@Override
	public Object getJavaValue() {
		return genJavaString();
	}

	/* @Override
	public void genCyan(PWInterface pw, CyanEnv cyanEnv) {
		identExpr.genCyan(pw, cyanEnv);
	} */


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
		identExpr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}


	@Override
	public void genCyanReplacingGenericParameters(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			String name = identExpr.getName();
			String value = cyanEnv.getFormalParamToRealParamTable().get(name);
			if ( value == null )
				pw.print(name);
			else {
				pw.print("\"" + MetaHelper.removeQuotes(value) + "\"");
				// pw.print(value);
			}
			/*
			char []charArray = AnnotationAt.replaceOnly(name.toCharArray(), cyanEnv.getFormalParamToRealParamTable(), ReplacementPolicyInGenericInstantiation.REPLACE_BY_CYAN_VALUE);
			if ( charArray[0] != '\"' )
				pw.print("\"");
			pw.print(charArray);
			if ( charArray[0] != '\"' )
				pw.print("\"");
			*/
		}
	}

	@Override
	public Symbol getFirstSymbol() {
		return identExpr.getFirstSymbol();
	}

	/*
	@Override
	public String asString() {
		return identExpr.asString();
	}

	*/

	private ExprIdentStar identExpr;


	@Override
	public StringBuffer getStringJavaValue() {
		return new StringBuffer("\"" + identExpr.getName() + "\"");
	}


	@Override
	public String getJavaType() {
		return "String";
	}


	public ExprIdentStar getIdentExpr() {
		return identExpr;
	}


	@Override
	public String metaobjectParameterAsString(Function0 inError) {
		return "\"" + identExpr.getName() + "\"";
	}


}
