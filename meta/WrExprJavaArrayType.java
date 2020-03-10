package meta;

import ast.ExprJavaArrayType;

public class WrExprJavaArrayType extends WrExpr {

	public WrExprJavaArrayType(ExprJavaArrayType hidden) {
		super(hidden);
	}


	@Override
	ExprJavaArrayType getHidden() {
		return (ExprJavaArrayType ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}
}

