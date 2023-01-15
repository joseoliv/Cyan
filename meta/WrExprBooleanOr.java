package meta;

import ast.ExprBooleanOr;

public class WrExprBooleanOr extends WrExpr {

	public WrExprBooleanOr(ExprBooleanOr hidden) {
		super(hidden);
	}


	@Override
	ExprBooleanOr getHidden() {
		return (ExprBooleanOr ) hidden;
	}

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getLeftExpr().accept(visitor, env);
		this.getRightExpr().accept(visitor, env);
		visitor.visit(this, env);
	}


	public WrExpr getRightExpr() {
		return ((ExprBooleanOr ) hidden).getRightExpr().getI();
	}


	public WrExpr getLeftExpr() {
		return ((ExprBooleanOr ) hidden).getLeftExpr().getI();
	}

}
