package meta;

import lexer.SymbolCharLiteral;

public class WrSymbolCharLiteral extends WrSymbol {

	public WrSymbolCharLiteral(SymbolCharLiteral hidden) {
		super(hidden);
	}

	SymbolCharLiteral getHidden() {
		return (SymbolCharLiteral ) hidden;
	}
}