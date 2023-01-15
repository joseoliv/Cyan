package meta;

import ast.ExprLiteralBoolean;

public class WrExprLiteralBoolean extends WrExprLiteral {

	public WrExprLiteralBoolean(ExprLiteralBoolean hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralBoolean getHidden() { return (ExprLiteralBoolean ) hidden; }


}
