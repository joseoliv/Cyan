package meta;

import ast.Expr;
import ast.ExprMessageSendUnaryChainToExpr;
import ast.Type;
import lexer.SymbolIdent;

public class WrExprMessageSendUnaryChainToExpr extends WrExprMessageSendUnaryChain {

	public WrExprMessageSendUnaryChainToExpr(ExprMessageSendUnaryChainToExpr hidden) {
		super(hidden);
	}


	@Override
	ExprMessageSendUnaryChainToExpr getHidden() {
		return (ExprMessageSendUnaryChainToExpr ) hidden;
	}


	@Override
	public String asString() {
		return hidden.asString();
	}



	@Override
	public WrType getType() {
		Type t = ((ExprMessageSendUnaryChainToExpr ) hidden).getType();
		return t == null ? null : t.getI();
	}


	@Override
	public String getMessageName() {
		return ((ExprMessageSendUnaryChainToExpr ) hidden).getMessageName();
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		this.getReceiver().accept(visitor, env);
		visitor.visit(this, env);
	}


	public WrExpr getReceiver() {
		Expr e = ((ExprMessageSendUnaryChainToExpr ) hidden).getReceiver();
		return e == null ? null : e.getI();
	}

	public WrSymbolIdent getUnarySymbol() {
		SymbolIdent e = ((ExprMessageSendUnaryChainToExpr ) hidden).getUnarySymbol();
		return e == null ? null : e.getI();
	}


}
