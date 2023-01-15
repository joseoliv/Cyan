package meta;

import lexer.SymbolDoubleLiteral;

public class WrSymbolDoubleLiteral extends WrSymbol {

	public WrSymbolDoubleLiteral(SymbolDoubleLiteral hidden) {
		super(hidden);
	}

	SymbolDoubleLiteral getHidden() {
		return (SymbolDoubleLiteral ) hidden;
	}
}
