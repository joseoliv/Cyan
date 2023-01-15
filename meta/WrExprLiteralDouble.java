package meta;

import ast.ExprLiteralDouble;

public class WrExprLiteralDouble extends WrExprLiteralNumber {

	public WrExprLiteralDouble(ExprLiteralDouble hidden) {
		super(hidden);
	}


	@Override
	ExprLiteralDouble getHidden() { return (ExprLiteralDouble ) hidden; }


}
