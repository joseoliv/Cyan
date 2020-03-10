package lexer;

import ast.CompilationUnitSuper;
import meta.Token;

public class SymbolComment extends Symbol {

	public SymbolComment(Token token, String symbolString, int startOffsetLine, int lineNumber, int columnNumber,
			int offset,  CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startOffsetLine, lineNumber, columnNumber, offset, compilationUnit);
	}


}
