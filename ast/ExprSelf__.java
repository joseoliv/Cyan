package ast;

import lexer.Symbol;
import meta.WrExprSelf__;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

public class ExprSelf__ extends Expr {

	public ExprSelf__(Symbol selfSymbol, MethodDec method) {
		super(method);
		this.selfSymbol = selfSymbol;
	}


	@Override
	public WrExprSelf__ getI() {
		return new WrExprSelf__(this);
	}


	    // should not be called
	@Override
	public void accept(ASTVisitor visitor) {

	}


	@Override
	public boolean mayBeStatement() {
		return false;
	}



	public void setSelfSymbol(Symbol selfSymbol) {
		this.selfSymbol = selfSymbol;
	}

	public Symbol getSelfSymbol() {
		return selfSymbol;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.print(NameServer.selfNameInnerPrototypes);
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		return NameServer.javaSelfNameInnerPrototypes;
	}

	@Override
	public Symbol getFirstSymbol() {
		return selfSymbol;
	}


	@Override
	public void calcInternalTypes(Env env) {


		FieldDec iv = env.searchField(NameServer.selfNameInnerPrototypes);
		if ( iv != null )
			type = iv.getType();
		else
			env.error(selfSymbol, "Internal error: '" + NameServer.selfNameInnerPrototypes + "' has no type", true, true);

		super.calcInternalTypes(env);

	}


	private Symbol selfSymbol;




}
