package meta;

import ast.StatementBreak;

public class WrStatementBreak extends WrStatement {


	public WrStatementBreak(StatementBreak hidden) {
		super(hidden);
	}

	@Override
	StatementBreak getHidden() { return (StatementBreak ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}


}
