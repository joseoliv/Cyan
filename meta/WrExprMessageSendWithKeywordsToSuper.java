package meta;

import ast.Expr;
import ast.ExprMessageSendWithKeywords;
import ast.ExprMessageSendWithKeywordsToExpr;
import ast.ExprMessageSendWithKeywordsToSuper;
import ast.MessageWithKeywords;

public class WrExprMessageSendWithKeywordsToSuper extends WrExprMessageSendWithKeywords {

	public WrExprMessageSendWithKeywordsToSuper(ExprMessageSendWithKeywordsToSuper hidden) {
		super(hidden);
	}


	@Override
	ExprMessageSendWithKeywordsToSuper getHidden() {
		return (ExprMessageSendWithKeywordsToSuper ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		MessageWithKeywords mk = ((ExprMessageSendWithKeywords ) hidden).getMessage();
		if ( mk != null ) {
			mk.getI().accept(visitor, env);
		}
		visitor.visit(this, env);
	}


	@Override
	public WrType getType() {
		ast.Type t = ((ExprMessageSendWithKeywordsToSuper ) hidden).getType();
		return t == null ? null : t.getI();
	}


	public WrExpr getSuperobject() {
		Expr e = ((ExprMessageSendWithKeywordsToSuper ) hidden).getSuperobject();
		return e == null ? null : e.getI();
	}


	@Override
	public String getMessageName() {
		return ((ExprMessageSendWithKeywordsToExpr ) hidden).getMessage().getMethodName();
	}


}
