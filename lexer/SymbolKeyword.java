package lexer;

import ast.CompilationUnitSuper;
import meta.WrSymbolKeyword;
import meta.Token;

/**
 * Keeps information on a symbol that is a keyword of Cyan such as
 * func, public or object. The name of the symbol is inherited from Symbol
 *
 * @author José
 *
 */
public class SymbolKeyword extends Symbol {

	public SymbolKeyword(Token token, String symbolString, int startLine, int lineNumber,
			int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
	}

	@Override
	public WrSymbolKeyword getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolKeyword(this);
		}
		return (WrSymbolKeyword ) isymbol;
	}


	@Override
	public int getColor() {
		return HighlightColor.keyword;
	}

	public SymbolKeyword newObject(Token token1, String symbolString1) {
		return new SymbolKeyword(token1, symbolString1, startOffsetLine, lineNumber, columnNumber,
				offset, compilationUnit);
	}

}
