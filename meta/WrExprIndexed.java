package meta;

import ast.ExprIndexed;

public class WrExprIndexed extends WrExpr {

	public WrExprIndexed(ExprIndexed hidden) {
		super(hidden);
	}


	@Override
	ExprIndexed getHidden() {
		return (ExprIndexed ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getIndexedExpr().accept(visitor, env);
		this.getIndexOfExpr().accept(visitor, env);
		visitor.visit(this, env);
	}


	private WrExpr getIndexOfExpr() {
		return ((ExprIndexed ) hidden).getIndexOfExpr().getI();
	}


	private WrExpr getIndexedExpr() {
		return ((ExprIndexed ) hidden).getIndexedExpr().getI();
	}


}
