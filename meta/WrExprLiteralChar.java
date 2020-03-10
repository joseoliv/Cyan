package meta;

import ast.ExprLiteralChar;

public class WrExprLiteralChar extends WrExprLiteral {

	public WrExprLiteralChar(ExprLiteralChar hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralChar getHidden() { return (ExprLiteralChar ) hidden; }


}


