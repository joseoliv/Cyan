package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.ExprGenericPrototypeInstantiation;

public class WrExprGenericPrototypeInstantiation extends WrExpr {

	public WrExprGenericPrototypeInstantiation(ExprGenericPrototypeInstantiation hidden) {
		super(hidden);
	}


	@Override
	ExprGenericPrototypeInstantiation getHidden() {
		return (ExprGenericPrototypeInstantiation ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		realTypeListList = this.getRealTypeListList();
		if ( realTypeListList != null ) {
			for ( List<WrExpr> exprList : realTypeListList ) {
				for ( WrExpr expr : exprList ) {
					expr.accept(visitor, env);
				}
			}
		}
		visitor.visit(this, env);
	}


	private List<List<WrExpr>> getRealTypeListList() {
		if ( realTypeListList == null ) {
			realTypeListList = new ArrayList<>();
			List<List<Expr>> rtll = ((ExprGenericPrototypeInstantiation ) hidden).getRealTypeListList();
			for ( List<Expr> rtl : rtll ) {
				List<WrExpr> newrtl = new ArrayList<>();
				for ( Expr rt : rtl ) {
					newrtl.add(rt.getI());
				}
				this.realTypeListList.add(newrtl);
			}
		}
		return realTypeListList;
	}

	List<List<WrExpr>> realTypeListList = null;
}
