package meta;

import ast.Expr;
import ast.ExprMessageSendUnaryChainToSuper;

public class WrExprMessageSendUnaryChainToSuper extends WrExprMessageSendUnaryChain {

	public WrExprMessageSendUnaryChainToSuper(ExprMessageSendUnaryChainToSuper hidden) {
		super(hidden);
	}


	@Override
	ExprMessageSendUnaryChainToSuper getHidden() {
		return (ExprMessageSendUnaryChainToSuper ) hidden;
	}

	@Override
	public WrType getType() {
		ast.Type t = ((ExprMessageSendUnaryChainToSuper ) hidden).getType();
		return t == null ? null : t.getI();
	}


	public WrExpr getReceiver() {
		Expr e = ((ExprMessageSendUnaryChainToSuper ) hidden).getReceiver();
		if ( e != null ) {
			return e.getI();
		}
		else {
			return null;
		}
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}


	@Override
	public String getMessageName() {
		return ((ExprMessageSendUnaryChainToSuper ) hidden).getMessageName();
	}

}
