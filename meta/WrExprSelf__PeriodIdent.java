package meta;

import ast.ExprSelf__PeriodIdent;

public class WrExprSelf__PeriodIdent extends WrExpr {

	public WrExprSelf__PeriodIdent(ExprSelf__PeriodIdent hidden) {
		super(hidden);
	}


	@Override
	ExprSelf__PeriodIdent getHidden() {
		return (ExprSelf__PeriodIdent ) hidden;
	}


	/*
	 * should not be called
	   @see ast.Expr#accept(ast.ASTVisitor)
	 */
	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
	}

}
