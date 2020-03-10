package meta;

import ast.ExprLiteralString;

public class WrExprLiteralString extends WrExprLiteral {

	public WrExprLiteralString(ExprLiteralString hidden) {
		super(hidden);
	}

	@Override
	public StringBuffer getStringJavaValue() {
		return ((ExprLiteralString ) hidden).getStringJavaValue();
	}

	@Override
	ExprLiteralString getHidden() {
		return (ExprLiteralString ) hidden;
	}

}
