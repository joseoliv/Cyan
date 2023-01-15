package lexer;

import java.awt.Color;
import ast.CompilationUnitSuper;
import ast.Annotation;
import meta.Token;
import meta.WrSymbol;

public class Symbol implements Cloneable {

	public Symbol(Token token, String symbolString, int startOffsetLine, int lineNumber,
			int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		this.token = token;
		this.symbolString = symbolString;
		this.startOffsetLine = startOffsetLine;
		this.setLineNumber(lineNumber);
		this.setColumnNumber(columnNumber);
		this.offset = offset;
		this.compilationUnit = compilationUnit;
	}

	@Override
	public Symbol clone() {
		try {
			return (Symbol ) super.clone();
		}
		catch ( Throwable e ) {
			return null;
		}
	}
	public WrSymbol getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbol(this);
		}
		return isymbol;
	}
	WrSymbol isymbol = null;

	final public void setSymbolString(String symbolString) {
		this.symbolString = symbolString;
	}

	final public String getSymbolString() {
		return symbolString;
	}

	/**
	 * get the Java name corresponding to this keyword. It is
	 * equal to symbolString except when there is a underscore.
	 * All underscore characters are duplicated. So,
	 *       Is_A_Number
	 * results in
	 *       Is__A__Number
	 * @param lineNumber

	public String getJavaName() {
		String s = "";
		for ( int i = 0; i < symbolString.length(); i++ ) {
			char ch = symbolString.charAt(i);
			if ( ch != ':' )
				s = s + ch;
			if ( ch == '_' )
				s = s + '_';
		}
		return s;

	}*/

	/**
	 * if the file is in the char array arrayChar, then arrayChar[startOffsetLine] is the
	 * first character of the line in which this symbol is.
	   @return
	 */
	public int getStartLine() {
		return startOffsetLine;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	/**
	 * number of characters from the beginning of the file to this symbol. Starts with 0.
	 * @return
	 */
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public CompilationUnitSuper getCompilationUnit() {
		return compilationUnit;
	}

	/**
	 * return the default color for the characters of this symbol.
	   @return
	 */
	public int getColor() {
		return Color.black.getRGB();
	}

	/**
	 *  this symbol may represent a metaobject annotation. If it does, return the metaobject annotation object associated to it.
	 *  Otherwise return null.
	 */
	public Annotation getCyanAnnotation() {
		return null;
	}

	public final Token token;

	public String symbolString;
	/**
	 * if the file is in the char array arrayChar, then arrayChar[startOffsetLine] is the
	 * first character of the line in which this symbol is.
	 */
	public int startOffsetLine;

	int lineNumber, columnNumber;

	/**
	 * number of characters from the beginning of the file to this symbol. Starts with 0.
	 */
	protected int offset;

	/**
	 * the compilation unit in which this symbol is
	 */
	final CompilationUnitSuper compilationUnit;

}
