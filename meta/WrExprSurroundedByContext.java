package meta;

import ast.ExprSurroundedByContext;

public class WrExprSurroundedByContext extends WrExpr {

	public WrExprSurroundedByContext(ExprSurroundedByContext hidden) {
		super(hidden);
	}


	@Override
	ExprSurroundedByContext getHidden() {
		return (ExprSurroundedByContext ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		getExpr().accept(visitor, env);
		visitor.visit(this, env);
	}



	public WrExpr getExpr() {
		return ((ExprSurroundedByContext ) hidden).getExpr().getI();
	}

}
