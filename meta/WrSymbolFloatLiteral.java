package meta;

import lexer.SymbolFloatLiteral;

public class WrSymbolFloatLiteral extends WrSymbol {

	public WrSymbolFloatLiteral(SymbolFloatLiteral hidden) {
		super(hidden);
	}

	SymbolFloatLiteral getHidden() {
		return (SymbolFloatLiteral ) hidden;
	}
}
