package meta;

import ast.ExprIdentStar;

public class WrImportStatement extends WrStatement {

	public WrImportStatement(WrExprIdentStar importPackage) {
		super(importPackage.hidden);
	}

	@Override
	public Object eval(WrEvalEnv ee) {
		ee.hidden.importPackage( ((ExprIdentStar ) hidden).asString());
		return null;
	}



	@Override
	ExprIdentStar getHidden() { return (ExprIdentStar ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		((ExprIdentStar ) hidden).getI().accept(visitor, env);
	}

}
