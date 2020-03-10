package meta;

import ast.ExprLiteralByte;

public class WrExprLiteralByte extends WrExprLiteralNumber {

	public WrExprLiteralByte(ExprLiteralByte hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralByte getHidden() { return (ExprLiteralByte ) hidden; }


}
