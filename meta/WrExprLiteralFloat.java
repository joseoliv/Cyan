package meta;

import ast.ExprLiteralFloat;

public class WrExprLiteralFloat extends WrExprLiteralNumber {

	public WrExprLiteralFloat(ExprLiteralFloat hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralFloat getHidden() { return (ExprLiteralFloat ) hidden; }


}
