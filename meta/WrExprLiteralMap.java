package meta;

import ast.ExprLiteralMap;

public class WrExprLiteralMap extends WrExprAnyLiteral {

	public WrExprLiteralMap(ExprLiteralMap hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralMap getHidden() {
		return (ExprLiteralMap ) hidden;
	}

}
