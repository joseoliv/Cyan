package meta;

import ast.StatementReturnFunction;

public class WrStatementReturnFunction extends WrStatement {

	public WrStatementReturnFunction(StatementReturnFunction hidden) {
		super(hidden);
	}

	@Override
	StatementReturnFunction getHidden() { return (StatementReturnFunction ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
		WrExpr expr = this.getExpr();
		if ( expr != null ) {
			expr.accept(visitor, env);
		}
	}


	public WrExpr getExpr() {
		ast.Expr e = ((StatementReturnFunction ) hidden).getExpr();
		return e == null ? null : e.getI();
	}


}
