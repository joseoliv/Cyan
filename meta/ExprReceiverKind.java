/**

 */
package meta;

/** This enumerated type gives the kinds of receivers of message sends.
   @author José

 */
public enum ExprReceiverKind {
	SUPER_R,     // receiver is super
	SELF_R,      // receiver is self, even if implicit as in 'print;'
	/* receiver is an expression that is not super or self. It
	 * may be a prototype.
	 */
	EXPR_R,
	PROTOTYPE_R;      // receiver is a prototype

}
