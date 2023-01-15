package ast;

import lexer.Symbol;

public interface GetNameAsInSourceCode {
	String getNameWithDeclaredTypes();
	Symbol getFirstSymbol();
}
