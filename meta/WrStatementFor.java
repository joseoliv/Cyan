package meta;

import ast.Expr;
import ast.StatementFor;
import ast.StatementList;
import ast.StatementLocalVariableDec;

public class WrStatementFor extends WrStatement {


	public WrStatementFor(StatementFor hidden) {
		super(hidden);
	}


	@Override
	StatementFor getHidden() { return (StatementFor ) hidden; }


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getForExpression().accept(visitor, env);
		this.getLocalVariableDec().accept(visitor, env);
		this.getStatementList().accept(visitor, env);
		visitor.visit(this, env);
	}


	private WrStatementList getStatementList() {
		StatementList sl = ((StatementFor ) hidden).getStatementList();
		return sl == null ? null : sl.getI();
	}


	private WrStatementLocalVariableDec getLocalVariableDec() {
		StatementLocalVariableDec sv = ((StatementFor ) hidden).getLocalVariableDec();
		return sv == null ? null : sv.getI();
	}


	private WrExpr getForExpression() {
		Expr e = ((StatementFor ) hidden).getForExpression();
		return e == null ? null : e.getI();
	}


}
