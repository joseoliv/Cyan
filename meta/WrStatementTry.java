package meta;


import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.StatementTry;

public class WrStatementTry extends WrStatement {

	public WrStatementTry(StatementTry hidden) {
		super(hidden);
	}

	@Override
	StatementTry getHidden() { return (StatementTry ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getStatementList().accept(visitor, env);
		for ( WrExpr e : this.getCatchExprList() ) {
			e.accept(visitor, env);
		}
		visitor.visit(this, env);

	}

	private WrStatementList getStatementList() {
		return ((StatementTry ) hidden).getStatementList().getI();
	}

	private List<WrExpr> getCatchExprList() {
		List<Expr> exprList = ((StatementTry ) hidden).getCatchExprList();
		List<WrExpr> wExprList = new ArrayList<>();
		for ( Expr e : exprList) {
			wExprList.add(e.getI());
		}
		return wExprList;
	}

}
