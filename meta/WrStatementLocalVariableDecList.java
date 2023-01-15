package meta;

import java.util.ArrayList;
import java.util.List;
import ast.StatementLocalVariableDec;
import ast.StatementLocalVariableDecList;

public class WrStatementLocalVariableDecList extends WrStatement {

	public WrStatementLocalVariableDecList(StatementLocalVariableDecList hidden) {
		super(hidden);
	}

	@Override
	StatementLocalVariableDecList getHidden() { return (StatementLocalVariableDecList ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		for ( WrStatementLocalVariableDec s : this.getLocalVariableDecList() ) {
			s.accept(visitor, env);
		}
		visitor.visit(this, env);
	}

	List<WrStatementLocalVariableDec> iLocalVariableDecList = null;
	boolean thisMethod_wasNeverCalled = true;

	public List<WrStatementLocalVariableDec> getLocalVariableDecList() {
		if ( thisMethod_wasNeverCalled ) {

			List<StatementLocalVariableDec> fromList = ((StatementLocalVariableDecList ) hidden)
					.getLocalVariableDecList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iLocalVariableDecList = null;
			}
			else {
				iLocalVariableDecList = new ArrayList<>();
				for ( StatementLocalVariableDec from : fromList ) {
					iLocalVariableDecList.add( from.getI() );
				}
			}
			thisMethod_wasNeverCalled = false;

		}
		return iLocalVariableDecList;
	}


}
