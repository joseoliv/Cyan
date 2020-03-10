package meta;

import ast.ExprBooleanAnd;

public class WrExprBooleanAnd extends WrExpr {

	public WrExprBooleanAnd(ExprBooleanAnd hidden) {
		super(hidden);
	}


	@Override
	ExprBooleanAnd getHidden() {
		return (ExprBooleanAnd ) hidden;
	}

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getLeftExpr().accept(visitor, env);
		this.getRightExpr().accept(visitor, env);
		visitor.visit(this, env);
	}


	public WrExpr getRightExpr() {
		return ((ExprBooleanAnd ) hidden).getRightExpr().getI();
	}


	public WrExpr getLeftExpr() {
		return ((ExprBooleanAnd ) hidden).getLeftExpr().getI();
	}


}
