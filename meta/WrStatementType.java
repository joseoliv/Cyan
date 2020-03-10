package meta;

import java.util.ArrayList;
import java.util.List;
import ast.CaseRecord;
import ast.StatementList;
import ast.StatementType;

public class WrStatementType extends WrStatement {

	public WrStatementType(StatementType hidden) {
		super(hidden);
	}

	@Override
	StatementType getHidden() { return (StatementType ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getExpr().accept(visitor, env);
		List<WrCaseRecord> caseList = this.getCaseList();
		for ( WrCaseRecord caseRecord : caseList ) {
			caseRecord.accept(visitor, env);
		}
		visitor.visit(this, env);
	}


	public WrSymbol getTypeSymbol() {
		return ((StatementType ) hidden).getTypeSymbol().getI();
	}

	public WrExpr getExpr() {
		ast.Expr e = ((StatementType ) hidden).getExpr();
		return e == null ? null : e.getI();
	}

	List<WrCaseRecord> icaseRecordList = null;
	boolean thisMethod_wasNeverCalled = true;

	public List<WrCaseRecord> getCaseList() {
		if ( thisMethod_wasNeverCalled ) {

			List<CaseRecord> fromList = ((StatementType ) hidden).getCaseList();
			if ( fromList == null ) {
					// unnecessary, just to document
				icaseRecordList = null;
			}
			else {
				icaseRecordList = new ArrayList<>();
				for ( CaseRecord from : fromList ) {
					icaseRecordList.add( from.getI() );
				}
			}
			thisMethod_wasNeverCalled = false;

		}
		return icaseRecordList;

		// return meta.CastList.fromTo( ((StatementType ) hidden).getCaseList());
	}

	public boolean getIsTaggedUnion() {
		return ((StatementType ) hidden).getIsTaggedUnion();
	}

	public List<Tuple2<String, String>> getLabelTypeList() {
		return ((StatementType ) hidden).getLabelTypeList();
	}

	public boolean getIsUnion() {
		return ((StatementType ) hidden).getIsUnion();
	}



	public WrStatementList getElseStatementList() {
		StatementList sl = ((StatementType ) hidden).getElseStatementList();
		return sl == null ? null : sl.getI();
	}

}
