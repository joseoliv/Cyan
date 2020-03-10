package ast;

import lexer.Symbol;

public interface INextSymbol {
	Symbol getNextSymbol();
	void setNextSymbol(Symbol nextSymbol);
	Symbol getFirstSymbol();
}
