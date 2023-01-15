package meta;

import ast.ExprLiteralNumber;

abstract public class WrExprLiteralNumber extends WrExprLiteral {

	public WrExprLiteralNumber(ExprLiteralNumber hidden) {
		super(hidden);
	}


	@Override
	abstract ExprLiteralNumber getHidden();
}
