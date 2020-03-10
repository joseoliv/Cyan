package meta;

import ast.ExprIdentStar;
import ast.StatementPlusPlusIdent;

public class WrStatementPlusPlusIdent extends WrStatement {

	public WrStatementPlusPlusIdent(StatementPlusPlusIdent hidden) {
		super(hidden);
	}

	@Override
	StatementPlusPlusIdent getHidden() { return (StatementPlusPlusIdent ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
		WrExprIdentStar varId = getVarId();
		if ( varId != null ) {
			varId.accept(visitor, env);
		}
	}

	public WrExprIdentStar getVarId() {
		ExprIdentStar e = ((StatementPlusPlusIdent ) hidden).getVarId();
		return e == null ? null : e.getI();
	}

}
