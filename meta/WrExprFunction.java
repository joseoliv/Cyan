package meta;

import ast.ExprFunction;
import ast.StatementList;

public class WrExprFunction extends WrExpr {

	public WrExprFunction( ExprFunction hidden ) {
		super(hidden);
	}

	@Override
	ExprFunction getHidden() {
		return (ExprFunction ) hidden;
	}

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getStatementList().accept(visitor, env);
	}

	public WrType getReturnType() {
		ast.Type type = ((ExprFunction ) hidden).getType();
		return type == null ? null : type.getI();
	}

	public WrStatementList getStatementList() {
		StatementList sl = ((ExprFunction ) hidden).getStatementList();
		return sl == null ? null : sl.getI();
	}

}
