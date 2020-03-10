package meta;

import ast.ExprMessageSendUnaryChain;
import ast.MethodSignature;

abstract public class WrExprMessageSendUnaryChain extends WrExprMessageSend {

	public WrExprMessageSendUnaryChain(ExprMessageSendUnaryChain hidden) {
		super(hidden);
	}



	@Override
	abstract ExprMessageSendUnaryChain getHidden();


	public WrMethodSignature getMethodSignatureForMessageSend() {
		MethodSignature ms = ((ExprMessageSendUnaryChain ) hidden).getMethodSignatureForMessageSend();
		return ms == null ? null : ms.getI();
	}

}
