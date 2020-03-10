package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.StatementIf;
import ast.StatementList;

public class WrStatementIf extends WrStatement {

	public WrStatementIf(StatementIf hidden) {
		super(hidden);
	}


	@Override
	StatementIf getHidden() { return (StatementIf ) hidden; }


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {

		for ( WrExpr ifExpr : this.getIfExprList() ) {
			ifExpr.accept(visitor, env);
		}
		for ( WrStatementList statList : this.getIfStatementList() ) {
			statList.accept(visitor, env);
		}
		WrStatementList elseStatementList = this.getElseStatementList();
		if ( elseStatementList != null )
			elseStatementList.accept(visitor, env);
		visitor.visit(this, env);
	}



	List<WrExpr> isymbolList = null;
	boolean thisMethod_wasNeverCalled = true;

	public List<WrExpr> getIfExprList() {
		if ( thisMethod_wasNeverCalled ) {

			List<Expr> fromList = ((StatementIf ) hidden).getIfExprList();
			if ( fromList == null ) {
					// unnecessary, just to document
				isymbolList = null;
			}
			else {
				isymbolList = new ArrayList<>();
				for ( Expr from : fromList ) {
					isymbolList.add( from.getI() );
				}
			}
			thisMethod_wasNeverCalled = false;

		}
		return isymbolList;
	}



	List<WrStatementList> iIfStatementList = null;
	boolean thisMethod_wasNeverCalled2 = true;

	public List<WrStatementList> getIfStatementList() {
		if ( thisMethod_wasNeverCalled2 ) {

			List<StatementList> fromList = ((StatementIf ) hidden).getIfStatementList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iIfStatementList = null;
			}
			else {
				iIfStatementList = new ArrayList<>();
				for ( StatementList from : fromList ) {
					iIfStatementList.add( from.getI() );
				}
			}
			thisMethod_wasNeverCalled2 = false;

		}
		return iIfStatementList;
	}



	public WrStatementList getElseStatementList() {
		StatementList sl = ((StatementIf ) hidden).getElseStatementList();
		return sl == null ? null : sl.getI();
	}

}
