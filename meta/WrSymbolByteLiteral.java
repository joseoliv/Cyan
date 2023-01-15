package meta;

import lexer.SymbolByteLiteral;

public class WrSymbolByteLiteral extends WrSymbol {

	public WrSymbolByteLiteral(SymbolByteLiteral hidden) {
		super(hidden);
	}

	SymbolByteLiteral getHidden() {
		return (SymbolByteLiteral ) hidden;
	}
}
