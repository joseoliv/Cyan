package meta;

import ast.ExprIdentStar;
import ast.StatementMinusMinusIdent;

public class WrStatementMinusMinusIdent extends WrStatement {

	public WrStatementMinusMinusIdent(StatementMinusMinusIdent hidden) {
		super(hidden);
	}

	@Override
	StatementMinusMinusIdent getHidden() { return (StatementMinusMinusIdent ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
		WrExprIdentStar varId = getVarId();
		if ( varId != null ) {
			varId.accept(visitor, env);
		}
	}

	public WrExprIdentStar getVarId() {
		ExprIdentStar e = ((StatementMinusMinusIdent ) hidden).getVarId();
		return e == null ? null : e.getI();
	}

}
