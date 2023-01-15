package meta;

import ast.ExprMessageSendWithKeywords;
import ast.MessageWithKeywords;
import ast.MethodSignature;

abstract public class WrExprMessageSendWithKeywords extends WrExprMessageSend {

	public WrExprMessageSendWithKeywords(ExprMessageSendWithKeywords hidden) {
		super(hidden);
	}



	@Override
	abstract ExprMessageSendWithKeywords getHidden();


	public WrMessageWithKeywords getMessage() {
		MessageWithKeywords mk = ((ExprMessageSendWithKeywords ) hidden).getMessage();
		return mk == null ? null : mk.getI();
	}


	/**
	 * the method signature of the method found in a search for an adequate method for this message.
	 */

	public WrMethodSignature getMethodSignatureForMessage() {
		MethodSignature ms = ((ExprMessageSendWithKeywords ) hidden).getMethodSignatureForMessage();
		return ms == null ? null : ms.getI();
	}
}

