package meta;

import ast.CastRecord;
import ast.Expr;
import ast.StatementLocalVariableDec;

public class WrCastRecord extends WrASTNode {

	public WrCastRecord(CastRecord hidden) {
		this.hidden = hidden;
	}

	CastRecord hidden;

	@Override
	CastRecord getHidden() {
		return hidden;
	}

	public WrExpr getTypeInDec() {
		Expr e = hidden.getTypeInDec();
		return e == null ? null : e.getI();
	}

	public WrExpr getExpr() {
		Expr e = hidden.getExpr();
		return e == null ? null : e.getI();
	}
	public WrStatementLocalVariableDec getLocalVar() {
		StatementLocalVariableDec v = hidden.getLocalVar();
		return v == null ? null : v.getI();
	}


	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}

}
