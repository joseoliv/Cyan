package meta;

import ast.StatementImport;

public class WrStatementImport extends WrStatement {

	public WrStatementImport(StatementImport hidden) {
		super(hidden);
	}

	@Override
	public Object eval(WrEvalEnv ee) {
		return ((StatementImport) hidden).eval(ee.hidden);
	}

	@Override
	StatementImport getHidden() { return (StatementImport ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		((StatementImport ) hidden).getI().accept(visitor, env);
	}

}
