package meta;

import lexer.Symbol;

/**
 * this class represents an error in a metaobject
   @author José
 */

public class CyanMetaobjectError {


	public CyanMetaobjectError(WrSymbol symbol, String message) {
		this.message = message;
		this.symbol = symbol == null ? null : symbol.hidden;
	}



	public String getMessage() {
		return message;
	}


	public WrSymbol getSymbol() {
		return symbol.getI();
	}

	/**
	 * the error message
	 */
	private final String message;


	private final Symbol symbol;
}
