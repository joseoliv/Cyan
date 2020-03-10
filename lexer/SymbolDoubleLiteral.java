/**
 *
 */
package lexer;

import ast.CompilationUnitSuper;
import meta.WrSymbolDoubleLiteral;
import meta.Token;

/** This class represents double literals
 * @author José
 *
 */
public class SymbolDoubleLiteral extends Symbol {

	/**
	 * @param symbolString
	 */
	public SymbolDoubleLiteral(Token token, String symbolString, String originalDoubleString, double doubleLiteral,
            int startLine, int lineNumber, int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
		this.originalDoubleString = originalDoubleString;
		this.doubleLiteral = doubleLiteral;
	}


	@Override
	public WrSymbolDoubleLiteral getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolDoubleLiteral(this);
		}
		return (WrSymbolDoubleLiteral ) isymbol;
	}



	public void setDoubleValue(double doubleValue) {
		this.doubleLiteral = doubleValue;
	}

	public double getDoubleValue() {
		return doubleLiteral;
	}

	private double doubleLiteral;


	public String getOriginalDoubleString() {
		return originalDoubleString;
	}

	@Override
	public int getColor() {
		return HighlightColor.doubleLiteral;
	}

	private final String originalDoubleString;
}
