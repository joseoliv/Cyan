package meta;

import ast.ExprLiteralArray;

public class WrExprLiteralArray extends WrExprAnyLiteral {

	public WrExprLiteralArray(ExprLiteralArray hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralArray getHidden() {
		return (ExprLiteralArray ) hidden;
	}

}
