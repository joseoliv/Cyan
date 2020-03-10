/**
 *
 */
package lexer;

import ast.CompilationUnitSuper;
import meta.WrSymbolFloatLiteral;
import meta.Token;

/** This class represents float literals
 * @author José
 *
 */
public class SymbolFloatLiteral extends Symbol {

	/**
	 * @param symbolString
	 */
	public SymbolFloatLiteral(Token token, String symbolString, String originalFloatString, float floatLiteral,
            int startLine, int lineNumber, int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
		this.originalFloatString = originalFloatString;
		this.floatValue = floatLiteral;

	}


	@Override
	public WrSymbolFloatLiteral getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolFloatLiteral(this);
		}
		return (WrSymbolFloatLiteral ) isymbol;
	}



	public void setFloatValue(float floatLiteral) {
		this.floatValue = floatLiteral;
	}

	public float getFloatValue() {
		return floatValue;
	}

	public String getOriginalFloatString() {
		return originalFloatString;
	}

	@Override
	public int getColor() {
		return HighlightColor.floatLiteral;
	}

	private float floatValue;

	private final String originalFloatString;


}
