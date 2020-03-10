/**
 *
 */
package lexer;

import ast.CompilationUnitSuper;
import meta.WrSymbolShortLiteral;
import meta.Token;

/**
 * This class represents symbols that are short literals
 * @author José
 *
 */
public class SymbolShortLiteral extends Symbol {

	/**
	 * @param symbolString
	 */
	public SymbolShortLiteral(Token token, String symbolString, short shortLiteral,
            int startLine, int lineNumber, int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startLine, lineNumber, columnNumber,offset, compilationUnit);
		this.shortValue = shortLiteral;
	}


	@Override
	public WrSymbolShortLiteral getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolShortLiteral(this);
		}
		return (WrSymbolShortLiteral ) isymbol;
	}



	public void setShortValue(short shortLiteral) {
		this.shortValue = shortLiteral;
	}

	public short getShortValue() {
		return shortValue;
	}
	@Override
	public int getColor() {
		return HighlightColor.shortLiteral;
	}

	private short shortValue;
}
