package meta;

import ast.ExprMessageSend;

abstract public class WrExprMessageSend extends WrExpr {

	public WrExprMessageSend(ExprMessageSend hidden) {
		super(hidden);
	}


	@Override
	abstract ExprMessageSend getHidden();



	abstract public String getMessageName();

}
