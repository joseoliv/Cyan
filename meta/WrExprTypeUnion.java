package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.ExprTypeUnion;

public class WrExprTypeUnion extends WrExpr {

	public WrExprTypeUnion(ExprTypeUnion hidden) {
		super(hidden);
	}

	@Override
	public ExprTypeUnion getHidden() {
		return (ExprTypeUnion ) hidden;
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
			for ( Expr t : ((ExprTypeUnion ) hidden).getTypeList() ) {
				iTypeList.add(t.getI());
			}
		}
		return iTypeList;
	}

	private List<WrExpr> iTypeList = null;

}
