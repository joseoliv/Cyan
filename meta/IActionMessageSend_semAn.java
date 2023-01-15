package meta;

/** Methods of this interface are called at compile-time when the compiler
 * finds a message passing linked to a method that has an annotation
 * that implements this interface. A message passing is 'linked' to a method
 * if that message passing may call, at runtime, the method.
 * This interface is similar to {@link meta.IActionMethodMissing_semAn}. It is only
 * used when a method for a message send WAS FOUND at compile-time. The metaobject
 * annotation should be attached to a method.
   @author jose
 */
public interface IActionMessageSend_semAn extends IStayPrototypeInterface {

	/**
	 * analyze the message send with keywords and return a triple composed of:
	 *    a) the source code that should replace the message send and
	 *    b) the package of the type of the value returned
	 *    c) the prototype name of the type of the value returned
	 *
	 * if any of b) or c) is null or the empty string, the return type of
	 * the method corresponding to the message send is considered
	 * the type of the expression that replaced the message send; that is a).
	 * For short, the message type is not changed if b) or c) is null or the
	 * empty string
	 */
	@SuppressWarnings("unused")
	default Tuple3<StringBuffer, String, String> semAn_analyzeReplaceKeywordMessage(
			WrExprMessageSendWithKeywordsToExpr messageSendExpr, WrEnv env) {
		return null;
	}


	/**
	 * analyze the unary message send  and return a tuple composed of:
	 *    a) the source code that should replace the message send and
	 *    b) the package of the type of the value returned
	 *    c) the prototype name of the type of the value returned
	 *
	 * if any of b) or c) is null or the empty string, the return type of
	 * the method corresponding to the message send is considered
	 * the type of the expression that replaced the message send; that is a).
	 * For short, the message type is not changed if b) or c) is null or the
	 * empty string.
	 *
	 *
	 * Not that this method is not called when the message is unary
	 * AND there is no explicit receiver like
	 *          x = unaryMethod;
	 * in which there is a method in the current prototype declared as
	 *         func unaryMethod -> Int
	 * However, method semAn_analyzeReplaceUnaryMessage is called when
	 * there is a receiver as in
	 *          x = self unaryMethod;
	 */
	@SuppressWarnings("unused")
	default Tuple3<StringBuffer, String, String> semAn_analyzeReplaceUnaryMessage(
			WrExprMessageSendUnaryChainToExpr messageSendExpr, WrEnv env) {
		return null;
	}

	/**
	 * called when the compiler finds a message sends like
            unary;
       or
            x = unary;
       in which 'self ' was not used but it could be.

	 * analyze the unary message send  and return a tuple composed of:
	 *    a) the source code that should replace the message send and
	 *    b) the type of the source code returned
	 */
	@SuppressWarnings("unused")
	default Tuple3<StringBuffer, String, String> semAn_analyzeReplaceUnaryMessageWithoutSelf(
			WrExprIdentStar messageSendExpr, WrEnv env) {
		return null;
	}


}
