package meta;

import java.util.ArrayList;
import java.util.List;
import ast.ExprFunctionWithKeywords;
import ast.MethodKeywordWithParameters;

public class WrExprFunctionWithKeywords extends WrExprFunction {

	public WrExprFunctionWithKeywords(ExprFunctionWithKeywords hidden) {
		super(hidden);
	}


	@Override
	ExprFunctionWithKeywords getHidden() {
		return (ExprFunctionWithKeywords ) hidden;
	}


	public List<WrMethodKeywordWithParameters> getkeywordWithParametersList() {
		if ( iKeywordWithParametersList == null ) {
			iKeywordWithParametersList = new ArrayList<>();
			for ( MethodKeywordWithParameters m :
				((ExprFunctionWithKeywords ) hidden).getkeywordWithParametersList() ) {
				iKeywordWithParametersList.add(m.getI());
			}
		}

		return iKeywordWithParametersList;
	}


    @Override
	public void accept(WrASTVisitor visitor, WrEnv env) {


		visitor.preVisit(this, env);
		for ( WrMethodKeywordWithParameters p : this.getkeywordWithParametersList() ) {
			p.accept(visitor, env);
		}
		for ( WrStatement s : this.getStatementList().getStatementList() ) {
			s.accept(visitor, env);
		}
		visitor.visit(this, env);
	}


    List<WrMethodKeywordWithParameters> iKeywordWithParametersList = null;
}

