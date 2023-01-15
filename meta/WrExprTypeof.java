package meta;

import ast.ExprTypeof;

public class WrExprTypeof extends WrExpr {

	public WrExprTypeof(ExprTypeof hidden) {
		super(hidden);
	}

	@Override
	ExprTypeof getHidden() {
		return (ExprTypeof ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		getExpr().accept(visitor, env);
		visitor.visit(this, env);
	}


	public WrExpr getExpr() {
		return ((ExprTypeof ) hidden).getExpr().getI();
	}


}
