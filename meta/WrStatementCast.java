package meta;

import java.util.ArrayList;
import java.util.List;
import ast.CastRecord;
import ast.StatementCast;
import ast.StatementList;

public class WrStatementCast extends WrStatement {

	public WrStatementCast(StatementCast hidden) {
		super(hidden);
	}


	@Override
	StatementCast getHidden() { return (StatementCast ) hidden; }


	List<WrCastRecord> icastRecordList = null;
	boolean thisMethod_wasNeverCalled = true;

	public List<WrCastRecord> getCastRecordList() {
		if ( thisMethod_wasNeverCalled ) {

			List<CastRecord> fromList = ((StatementCast ) hidden).getCastRecordList();
			if ( fromList == null ) {
					// unnecessary, just to document
				icastRecordList = null;
			}
			else {
				icastRecordList = new ArrayList<>();
				for ( CastRecord from : fromList ) {
					icastRecordList.add( from.getI() );
				}
			}
			thisMethod_wasNeverCalled = false;

		}
		return icastRecordList;
	}

	public WrStatementList getCastStatementList() {
		StatementList sl = ((StatementCast ) hidden).getCastStatementList();
		return sl == null ? null : sl.getI();
	}

	public WrStatementList getElseStatementList() {
		StatementList sl = ((StatementCast ) hidden).getElseStatementList();
		return sl == null ? null : sl.getI();
	}



	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {

		List<WrCastRecord> crList = this.getCastRecordList();
		for ( WrCastRecord castRecord : crList ) {
			if ( castRecord.getTypeInDec() != null ) {
				castRecord.getTypeInDec().accept(visitor, env);
			}
			castRecord.getLocalVar().accept(visitor, env);
			castRecord.getExpr().accept(visitor, env);
		}
		this.getCastStatementList().accept(visitor, env);

		WrStatementList elseList = getElseStatementList();
		if ( elseList != null )
			elseList.accept(visitor, env);

		visitor.visit(this, env);
	}



}
