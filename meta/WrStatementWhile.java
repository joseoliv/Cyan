package meta;

import ast.StatementWhile;

public class WrStatementWhile extends WrStatement {

	public WrStatementWhile(StatementWhile hidden) {
		super(hidden);
	}

	@Override
	StatementWhile getHidden() { return (StatementWhile ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getBooleanExpr().accept(visitor, env);
		this.getStatementList().accept(visitor, env);
		visitor.visit(this, env);
	}

	private WrStatementList getStatementList() {
		return ((StatementWhile ) hidden).getStatementList().getI();
	}

	private WrExpr getBooleanExpr() {
		return ((StatementWhile ) hidden).getBooleanExpr().getI();
	}
}
