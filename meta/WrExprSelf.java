package meta;

import ast.ExprSelf;

public class WrExprSelf extends WrExpr {

	public WrExprSelf(ExprSelf hidden) {
		super(hidden);
	}


	@Override
	ExprSelf getHidden() {
		return (ExprSelf ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}

}