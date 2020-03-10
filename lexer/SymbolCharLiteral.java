/**
 *
 */
package lexer;

import ast.CompilationUnitSuper;
import meta.WrSymbolCharLiteral;
import meta.Token;

/** This class represents char literals
 * @author José
 *
 */
public class SymbolCharLiteral extends Symbol {

	/**
	 * @param symbolString
	 */
	public SymbolCharLiteral(Token token, String symbolString, char charLiteral,
            int startLine, int lineNumber, int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
		this.charLiteral = charLiteral;
	}

	@Override
	public WrSymbolCharLiteral getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolCharLiteral(this);
		}
		return (WrSymbolCharLiteral ) isymbol;
	}


	public void setCharLiteral(char charLiteral) {
		this.charLiteral = charLiteral;
	}

	public char getCharLiteral() {
		return charLiteral;
	}
	@Override
	public int getColor() {
		return HighlightColor.charLiteral;
	}

	private char charLiteral;
}
