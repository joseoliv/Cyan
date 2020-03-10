package meta;

import ast.ExprLiteralInt;

public class WrExprLiteralInt extends WrExprLiteralNumber {

	public WrExprLiteralInt(ExprLiteralInt hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralInt getHidden() { return (ExprLiteralInt ) hidden; }


}
