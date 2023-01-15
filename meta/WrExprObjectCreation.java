package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.ExprObjectCreation;

public class WrExprObjectCreation extends WrExpr {

	public WrExprObjectCreation(ExprObjectCreation hidden) {
		super(hidden);
	}


	@Override
	ExprObjectCreation getHidden() {
		return (ExprObjectCreation ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getPrototype().accept(visitor, env);
		List<WrExpr> paramList = getParameterList();
		if ( paramList != null ) {
			for ( final WrExpr e : paramList ) {
				e.accept(visitor, env);
			}
		}
		visitor.visit(this, env);
	}

	List<WrExpr> iParameterList = null;
	boolean thisMethod_wasNeverCalled = true;

	public List<WrExpr> getParameterList() {
		if ( thisMethod_wasNeverCalled ) {
			thisMethod_wasNeverCalled = false;


			List<Expr> fromList = ((ExprObjectCreation ) hidden).getParameterList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iParameterList = null;
			}
			else {
				iParameterList = new ArrayList<>();
				for ( Expr from : fromList ) {
					iParameterList.add( from.getI() );
				}
			}

		}
		return iParameterList;
	}



	public WrExpr getPrototype() {
		ast.Expr e = ((ExprObjectCreation ) hidden).getPrototype();
		return e == null ? null : e.getI();
	}
}

