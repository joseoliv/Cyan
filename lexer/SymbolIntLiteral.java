/**
 *
 */
package lexer;

import ast.CompilationUnitSuper;
import meta.WrSymbolIntLiteral;
import meta.Token;

/** This class represents int literals
 * @author José
 *
 */
public class SymbolIntLiteral extends Symbol {

	/**
	 * @param symbolString
	 */
	public SymbolIntLiteral(Token token, String symbolString, int intLiteral,
            int startLine, int lineNumber, int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
		this.intLiteral = intLiteral;
	}

	@Override
	public WrSymbolIntLiteral getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolIntLiteral(this);
		}
		return (WrSymbolIntLiteral ) isymbol;
	}




	public void setIntValue(int intValue) {
		this.intLiteral = intValue;
	}

	public int getIntValue() {
		return intLiteral;
	}
	@Override
	public int getColor() {
		return HighlightColor.intLiteral;
	}

	private int intLiteral;

}
