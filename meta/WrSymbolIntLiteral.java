package meta;

import lexer.SymbolIntLiteral;

public class WrSymbolIntLiteral extends WrSymbol {

	public WrSymbolIntLiteral(SymbolIntLiteral hidden) {
		super(hidden);
	}

	SymbolIntLiteral getHidden() {
		return (SymbolIntLiteral ) hidden;
	}

	public int getIntValue() {
		return ((SymbolIntLiteral ) hidden).getIntValue();
	}

}
