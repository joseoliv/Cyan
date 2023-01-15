package meta;

import ast.StatementThrow;

public class WrStatementThrow extends WrStatement {

	public WrStatementThrow(StatementThrow hidden) {
		super(hidden);
	}

	@Override
	StatementThrow getHidden() { return (StatementThrow ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getExpr().accept(visitor, env);
		visitor.visit(this, env);

	}

	private WrExpr getExpr() {
		return ((StatementThrow ) hidden).getExpr().getI();
	}

}
