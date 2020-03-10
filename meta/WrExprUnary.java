package meta;

import ast.Expr;
import ast.ExprUnary;
import lexer.SymbolOperator;

public class WrExprUnary extends WrExpr {

	public WrExprUnary(ExprUnary hidden) {
		super(hidden);
	}


	@Override
	ExprUnary getHidden() {
		return (ExprUnary ) hidden;
	}

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		getExpr().accept(visitor, env);
		visitor.visit(this, env);
	}


	public WrExpr getExpr() {
		Expr e = ((ExprUnary ) hidden).getExpr();
		return e == null ? null : e.getI();
	}


	public WrSymbolOperator getSymbolOperator() {
		SymbolOperator e = ((ExprUnary ) hidden).getSymbolOperator();
		return e == null ? null : e.getI();
	}

}

