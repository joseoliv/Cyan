package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.ExprTypeIntersection;

public class WrExprTypeIntersection extends WrExpr {

	public WrExprTypeIntersection(ExprTypeIntersection hidden) {
		super(hidden);
	}

	@Override
	public ExprTypeIntersection getHidden() {
		return (ExprTypeIntersection ) hidden;
	}

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		for ( WrExpr e : iTypeList ) {
			e.accept(visitor, env);
		}
	}

	public List<WrExpr> getTypeList() {
		if ( iTypeList == null ) {
			iTypeList = new ArrayList<>();
			for ( Expr t : ((ExprTypeIntersection ) hidden).getTypeList() ) {
				iTypeList.add(t.getI());
			}
		}
		return iTypeList;
	}

	private List<WrExpr> iTypeList = null;

}
