package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Statement;
import ast.StatementList;

public class WrStatementList extends WrASTNode {

	public WrStatementList(StatementList hidden) {
		this.hidden = hidden;
	}

	StatementList hidden;

	@Override
	StatementList getHidden() {
		return hidden;
	}

	public void accept(WrASTVisitor visitor, WrEnv env) {
		if ( isList == null ) {
			isList = getStatementList();
		}
		if ( isList != null ) {
			for ( WrStatement stat : isList ) {
				stat.accept(visitor, env);
			}
		}
		visitor.visit(this, env);
	}

	List<WrStatement> iStatementList = null;
	boolean thisMethod_wasNeverCalled = true;

	public List<WrStatement> getStatementList() {
		if ( thisMethod_wasNeverCalled ) {
			thisMethod_wasNeverCalled = false;


			List<Statement> fromList = hidden.getStatementList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iStatementList = null;
			}
			else {
				iStatementList = new ArrayList<>();
				for ( Statement from : fromList ) {
					iStatementList.add( from.getI() );
				}
			}

		}
		return iStatementList;
	}


	private List<WrStatement> isList = null;
}
