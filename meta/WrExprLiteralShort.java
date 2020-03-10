package meta;

import ast.ExprLiteralShort;

public class WrExprLiteralShort extends WrExprLiteralNumber {

	public WrExprLiteralShort(ExprLiteralShort hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralShort getHidden() { return (ExprLiteralShort ) hidden; }


}
