package meta;

import ast.ExprSelf__;

public class WrExprSelf__ extends WrExpr {

	public WrExprSelf__(ExprSelf__ hidden) {
		super(hidden);
	}


	@Override
	ExprSelf__ getHidden() {
		return (ExprSelf__ ) hidden;
	}


    // should not be called
	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
	}

}
