package meta;

import ast.CompilationUnitSuper;
import lexer.Symbol;

public class WrSymbol {

	public WrSymbol(Symbol hidden) {
		this.hidden = hidden;
		token = hidden.token;
	}

	Symbol hidden;
	public final Token token;


	public String getSymbolString() {
		return hidden.symbolString;
	}

	public int getStartLine() {
		return hidden.getStartLine();
	}

	public int getLineNumber() {
		return hidden.getLineNumber();
	}

	public int getColumnNumber() {
		return hidden.getColumnNumber();
	}

	/**
	 * number of characters from the beginning of the file to this symbol. Starts with 0.
	 * @return
	 */
	public int getOffset() {
		return hidden.getOffset();
	}

	public int getColor() {
		return hidden.getColor();
	}

	public WrCompilationUnitSuper getCompilationUnit() {
		CompilationUnitSuper cunit = hidden.getCompilationUnit();
		return cunit == null ? null : cunit.getI();
	}


}
