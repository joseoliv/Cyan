package meta;

import ast.ExprLiteralLong;

public class WrExprLiteralLong extends WrExprLiteralNumber {

	public WrExprLiteralLong(ExprLiteralLong hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralLong getHidden() { return (ExprLiteralLong ) hidden; }


}
