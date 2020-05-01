package meta;

/**
 * interface with methods for checking message sends
 * inheritance etc. The annotations should be attached to a method
   @author José
 */
public interface ICheckMessageSend_afterSemAn extends ICheck_afterResTypes_afterSemAn, IStayPrototypeInterface {


	/**
	 * check unary message send. The receiver expression of the message is receiverExpr.
	 * The type of the receiver is receiverType. The metaobject that implements this
	 * interface is always attached to a method. Then it knows which message was sent
	 * to receiverExpr.
	 */
	@SuppressWarnings("unused")
	default void afterSemAn_checkUnaryMessageSend(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrSymbol unarySymbol, WrEnv env) { }
	/**
	 * check unary message send just like the above method. However,
	 * this method is called only when the metaobject annotation is attached to the most specific method
	 * in the hierarchy. That is, suppose method 'm -> Int' of prototype A is overridden in sub-prototype B
	 * of A. In both methods 'm -> Int', there is an attached metaobject that implements interface
	 * ICheckMessageSend_afterSemAn. In a message send <br>
	 * <code>
	 *     var b = B new; <br>
	 *     b m println;<br>
	 * </code>
	 * the compiler calls just method <code>semAn_checkkeywordMessageSendMostSpecific</code> of B.
	 *
	 */
	@SuppressWarnings("unused")
	default void afterSemAn_checkUnaryMessageSendMostSpecific(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrSymbol unarySymbol,
			WrPrototype mostSpecificReceiver, WrEnv env) { }
	/**
	 * check message send with keywords (not an unary message send). The receiver expression of the message is receiverExpr.
	 * The type of the receiver is receiverType. The metaobject that implements this
	 * interface is always attached to a method. Then it knows which message was sent
	 * to receiverExpr.
	 */
	@SuppressWarnings("unused")
	default void afterSemAn_checkKeywordMessageSend(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message,
			WrMethodSignature methodSignature, WrEnv env
			) {

	}
	/**
	 * check message send with keywords (not an unary message send) just like the above method. However,
	 * this method is called only when the metaobject annotation is attached to the most specific method
	 * in the hierarchy. That is, suppose method 'm: Int' of prototype A is overridden in sub-prototype B
	 * of A. In both methods 'm: Int', there is an attached metaobject that implements interface
	 * ICheckMessageSend_afterSemAn. In a message send <br>
	 * <code>
	 *     var b = B new; <b>
	 *     b m: 0;<br>
	 * </code>
	 * the compiler calls just method <code>semAn_checkkeywordMessageSendMostSpecific</code> of B.
	 *
	 */
	@SuppressWarnings("unused")
	default void afterSemAn_checkKeywordMessageSendMostSpecific(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message,
			WrMethodSignature methodSignature,
			WrPrototype mostSpecificReceiver, WrEnv env) {

	}

}
