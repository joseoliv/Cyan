/**
 *
 */
package lexer;

import ast.CompilationUnitSuper;
import meta.WrSymbolStringLiteral;
import meta.Token;

/** This class represents a literal string
 * @author José
 *
 */
public class SymbolStringLiteral extends Symbol {

	/**
	 * @param token
	 * @param symbolString
	 * @param lineNumber
	 * @param columnNumber
	 */
	public SymbolStringLiteral(Token token, String symbolString,
			int startLine, int lineNumber, int columnNumber, int offset, String javaString,
			CompilationUnitSuper compilationUnit, boolean tripleQuote) {
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
		this.javaString = javaString;
		this.tripleQuote = tripleQuote;
	}

	@Override
	public WrSymbolStringLiteral getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolStringLiteral(this);
		}
		return (WrSymbolStringLiteral ) isymbol;
	}



	public String getJavaString() {
		return javaString;
	}

	/**
	 * return true if this symbol represents a string that starts and ends with """
	   @return
	 */
	public boolean getTripleQuote() {
		return tripleQuote;
	}
	@Override
	public int getColor() {
		return HighlightColor.stringLiteral;
	}

	private final String javaString;
	private final boolean tripleQuote;
	public boolean getOctothorpe() {
		return octothorpe;
	}

	public void setOctothorpe(boolean octothorpe) {
		this.octothorpe = octothorpe;
	}

	/**
	 * true if this string originated from a # as in
	 *       #at
	 *       #at:put:
	 *
	 * false if the string is delimited by " or """
	 */
	private boolean octothorpe = false;

}
