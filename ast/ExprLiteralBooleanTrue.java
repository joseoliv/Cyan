/**
 *
 */
package ast;

import lexer.Symbol;

/** Represents true
 * @author Jos�
 *
 */
public class ExprLiteralBooleanTrue extends ExprLiteralBoolean {

	/**
	 * @param symbol
	 */
	public ExprLiteralBooleanTrue(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
	}

}
