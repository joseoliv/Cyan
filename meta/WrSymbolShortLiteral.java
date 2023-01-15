package meta;

import lexer.SymbolShortLiteral;

public class WrSymbolShortLiteral extends WrSymbol {

	public WrSymbolShortLiteral(SymbolShortLiteral hidden) {
		super(hidden);
	}

	SymbolShortLiteral getHidden() {
		return (SymbolShortLiteral ) hidden;
	}

}
