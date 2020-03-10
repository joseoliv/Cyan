package meta;

import ast.StatementRepeat;

public class WrStatementRepeat extends WrStatement {

	public WrStatementRepeat(StatementRepeat hidden) {
		super(hidden);
	}

	@Override
	StatementRepeat getHidden() { return (StatementRepeat ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getBooleanExpr().accept(visitor, env);
		this.getStatementList().accept(visitor, env);
		visitor.visit(this, env);
	}

	private WrStatementList getStatementList() {
		return ((StatementRepeat ) hidden).getStatementList().getI();
	}

	private WrExpr getBooleanExpr() {
		return ((StatementRepeat ) hidden).getBooleanExpr().getI();
	}

}
