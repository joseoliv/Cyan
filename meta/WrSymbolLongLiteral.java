package meta;

import lexer.SymbolLongLiteral;

public class WrSymbolLongLiteral extends WrSymbol {

	public WrSymbolLongLiteral(SymbolLongLiteral hidden) {
		super(hidden);
	}

	SymbolLongLiteral getHidden() {
		return (SymbolLongLiteral ) hidden;
	}

}
