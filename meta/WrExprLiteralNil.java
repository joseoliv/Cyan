package meta;

import ast.ExprLiteralNil;

public class WrExprLiteralNil extends WrExprLiteral {

	public WrExprLiteralNil(ExprLiteralNil hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralNil getHidden() { return (ExprLiteralNil ) hidden; }


}
