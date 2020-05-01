/**
 *
 */
package ast;

import lexer.Symbol;

/** Represents false
 * @author José
 *
 */
public class ExprLiteralBooleanFalse extends ExprLiteralBoolean {

	/**
	 * @param symbol
	 */
	public ExprLiteralBooleanFalse(Symbol symbol, MethodDec currentMethod) {
		super(symbol, currentMethod);
	}


}
