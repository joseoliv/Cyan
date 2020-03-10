package meta;

import lexer.SymbolStringLiteral;

public class WrSymbolStringLiteral extends WrSymbol {

	public WrSymbolStringLiteral(SymbolStringLiteral hidden) {
		super(hidden);
	}

	SymbolStringLiteral getHidden() {
		return (SymbolStringLiteral ) hidden;
	}

}

