package meta;

import ast.CaseRecord;
import ast.Expr;
import ast.StatementList;

public class WrCaseRecord extends WrASTNode {

	public WrCaseRecord(CaseRecord hidden) {
		this.hidden = hidden;
	}

	CaseRecord hidden;

	@Override
	CaseRecord getHidden() {
		return hidden;
	}

	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getStatementList().accept(visitor, env);
		visitor.visit(this, env);
	}

	public WrExpr getExprType() {
		Expr e = hidden.getExprType();
		return e == null ? null : e.getI();
	}

	public WrStatementList getStatementList() {
		StatementList sl = hidden.getStatementList();
		return sl == null ? null : sl.getI();
	}



}
