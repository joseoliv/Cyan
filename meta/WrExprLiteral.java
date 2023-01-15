package meta;

import ast.ExprLiteral;

abstract public class WrExprLiteral extends WrExprAnyLiteral {

	public WrExprLiteral(ExprLiteral hidden) {
		super(hidden);
	}


	@Override
	abstract ExprLiteral getHidden();


}
