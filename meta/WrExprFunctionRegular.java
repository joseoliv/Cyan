package meta;

import java.util.ArrayList;
import java.util.List;
import ast.ExprFunctionRegular;
import ast.ParameterDec;

public class WrExprFunctionRegular extends WrExprFunction {

	public WrExprFunctionRegular(ExprFunctionRegular hidden) {
		super(hidden);
	}


	@Override
	ExprFunctionRegular getHidden() {
		return (ExprFunctionRegular ) hidden;
	}

    @Override
	public void accept(WrASTVisitor visitor, WrEnv env) {

		visitor.preVisit(this, env);
		for ( WrParameterDec p : this.getParameterList() ) {
			p.accept(visitor, env);
		}
		for ( WrStatement s : this.getStatementList().getStatementList() ) {
			s.accept(visitor, env);
		}
		visitor.visit(this, env);
	}

	public List<WrParameterDec> getParameterList() {
		List<ParameterDec> fromList = ((ExprFunctionRegular ) hidden).getParameterList();
		if ( fromList == null ) {
				// unnecessary, just to document
			iParameterList = null;
		}
		else {
			iParameterList = new ArrayList<>();
			for ( ParameterDec from : fromList ) {
				iParameterList.add( from.getI() );
			}
		}

		return iParameterList;
	}
	List<WrParameterDec> iParameterList = null;
}
