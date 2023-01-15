package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.StatementAssignmentList;

public class WrStatementAssignmentList  extends WrStatement {

	public WrStatementAssignmentList(StatementAssignmentList hidden) {
		super(hidden);
	}


	@Override
	StatementAssignmentList getHidden() { return (StatementAssignmentList ) hidden; }


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		for ( WrExpr e : this.getExprList() ) {
			e.accept(visitor, env);
		}
		visitor.visit(this, env);
	}

	public List<WrExpr> getExprList() {
		if ( wrExprList == null ) {
			wrExprList = new ArrayList<>();
			for (Expr e : ((StatementAssignmentList ) hidden).getExprList() ) {
				wrExprList.add(e.getI());
			}
		}
		return wrExprList;
	}


	List<WrExpr> wrExprList = null;

}
