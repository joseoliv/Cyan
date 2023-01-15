package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.ExprLiteralTuple;

public class WrExprLiteralTuple extends WrExprAnyLiteral {

	public WrExprLiteralTuple(ExprLiteralTuple hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralTuple getHidden() {
		return (ExprLiteralTuple ) hidden;
	}


	public boolean getIsNamedTuple() {
		return ((ExprLiteralTuple ) hidden).getIsNamedTuple();
	}


	public List<WrExpr> getExprList() {
		List<Expr> exprList = ((ExprLiteralTuple ) hidden).getExprList();
		if ( wrExprList == null ) {
			wrExprList = new ArrayList<WrExpr>();
			for ( Expr e : exprList ) {
				wrExprList.add(e.getI());
			}
		}
		return wrExprList;
	}

	private List<WrExpr> wrExprList = null;
}
