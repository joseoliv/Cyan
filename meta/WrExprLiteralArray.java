package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.ExprLiteralArray;

public class WrExprLiteralArray extends WrExprAnyLiteral {

	public WrExprLiteralArray(ExprLiteralArray hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralArray getHidden() {
		return (ExprLiteralArray ) hidden;
	}

	public List<WrExpr> getExprList() {
		List<Expr> exprList = ((ExprLiteralArray ) hidden).getExprList();
		List<WrExpr> wrExprList = new ArrayList<>();
		for ( Expr e : exprList ) {
			wrExprList.add(e.getI());
		}
		return wrExprList;
	}


}
