package meta;

import ast.StatementReturn;

public class WrStatementReturn extends WrStatement {

	public WrStatementReturn(StatementReturn hidden) {
		super(hidden);
	}

	@Override
	StatementReturn getHidden() { return (StatementReturn ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
		WrExpr expr = this.getExpr();
		if ( expr != null ) {
			expr.accept(visitor, env);
		}
	}


	public WrExpr getExpr() {
		ast.Expr e = ((StatementReturn ) hidden).getExpr();
		return e == null ? null : e.getI();
	}

}
