package meta;

import lexer.SymbolBooleanLiteral;

public class WSymbolBooleanLiteral  extends WrSymbol {

	public WSymbolBooleanLiteral(SymbolBooleanLiteral hidden) {
		super(hidden);
	}

	SymbolBooleanLiteral getHidden() {
		return (SymbolBooleanLiteral ) hidden;
	}

}
