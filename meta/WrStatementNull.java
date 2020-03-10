package meta;

import ast.StatementNull;

public class WrStatementNull extends WrStatement {

	public WrStatementNull(StatementNull hidden) {
		super(hidden);
	}

	@Override
	StatementNull getHidden() { return (StatementNull ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {

	}


}
