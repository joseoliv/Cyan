package lexer;

import ast.CompilationUnitSuper;
import meta.Token;

public class SymbolMacroKeyword extends SymbolKeyword {

	public SymbolMacroKeyword(String symbolString, int startLine, int lineNumber, int columnNumber, int offset,
			CompilationUnitSuper compilationUnit) {
		super(Token.MACRO_KEYWORD, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
	}
	@Override
	public int getColor() {
		return HighlightColor.macroKeyword;
	}


}
