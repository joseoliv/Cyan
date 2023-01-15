package meta;

import lexer.SymbolCharSequence;

public class WrSymbolCharSequence extends WrSymbol {

	public WrSymbolCharSequence(SymbolCharSequence hidden) {
		super(hidden);
	}

	SymbolCharSequence getHidden() {
		return (SymbolCharSequence ) hidden;
	}
}