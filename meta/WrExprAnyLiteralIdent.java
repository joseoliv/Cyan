package meta;

import ast.ExprAnyLiteralIdent;

public class WrExprAnyLiteralIdent extends WrExprAnyLiteral {

	public WrExprAnyLiteralIdent(ExprAnyLiteralIdent hidden) {
		super(hidden);
	}


	@Override
	ExprAnyLiteralIdent getHidden() { return (ExprAnyLiteralIdent ) hidden; }


}
