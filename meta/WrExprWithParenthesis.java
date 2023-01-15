package meta;

import ast.ExprWithParenthesis;

public class WrExprWithParenthesis extends WrExpr {

	public WrExprWithParenthesis(ExprWithParenthesis hidden) {
		super(hidden);
	}


	public WrExpr getExpr() {
		return ((ExprWithParenthesis ) hidden).getExpr().getI();
	}

	@Override
	ExprWithParenthesis getHidden() {
		return (ExprWithParenthesis ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {

		getExpr().accept(visitor, env);
		visitor.visit(this, env);
	}

}

