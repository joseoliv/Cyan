package meta;

import ast.ExprSelfPeriodIdent;

public class WrExprSelfPeriodIdent extends WrExpr {

	public WrExprSelfPeriodIdent(ExprSelfPeriodIdent hidden) {
		super(hidden);
	}

	public WrSymbol getIdentSymbol() {
		return ((ExprSelfPeriodIdent ) hidden).getIdentSymbol().getI();
	}

	@Override
	ExprSelfPeriodIdent getHidden() {
		return (ExprSelfPeriodIdent ) hidden;
	}

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}

}
