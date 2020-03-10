package meta;

import ast.Expr;
import ast.ExprMessageSendWithKeywords;
import ast.ExprMessageSendWithKeywordsToExpr;
import ast.MessageWithKeywords;
import ast.Type;

public class WrExprMessageSendWithKeywordsToExpr extends WrExprMessageSendWithKeywords {

	public WrExprMessageSendWithKeywordsToExpr( ExprMessageSendWithKeywordsToExpr hidden ) {
		super(hidden);
	}

	@Override
	ExprMessageSendWithKeywords getHidden() {
		return  (ExprMessageSendWithKeywords ) hidden;
	}

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		WrExpr receiverExpr = this.getReceiverExpr();
		if ( receiverExpr != null ) {
			receiverExpr.accept(visitor, env);
		}
		MessageWithKeywords mk = ((ExprMessageSendWithKeywords ) hidden).getMessage();
		if ( mk != null ) {
			mk.getI().accept(visitor, env);
		}
		visitor.visit(this, env);
	}




	@Override
	public String asString() {
		return ((ExprMessageSendWithKeywordsToExpr ) hidden).asString();
	}

	public WrExpr getReceiverExpr() {
		if ( iexpr == null ) {
			Expr e = ((ExprMessageSendWithKeywordsToExpr ) hidden).getReceiverExpr();
			if ( e == null ) {
				return null;
			}
			iexpr = e.getI();
		}
		return iexpr;
	}

	private WrExpr iexpr = null;



	@Override
	public WrType getType() {
		Type t = ((ExprMessageSendWithKeywordsToExpr ) hidden).getType();
		return t == null ? null : t.getI();
	}

	@Override
	public String getMessageName() {
		return ((ExprMessageSendWithKeywordsToExpr ) hidden).getMessage().getMethodName();
	}


}
