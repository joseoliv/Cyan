package meta;

import lexer.SymbolOperator;

public class WrSymbolOperator extends WrSymbol {

	public WrSymbolOperator(SymbolOperator hidden) {
		super(hidden);
	}

	SymbolOperator getHidden() {
		return (SymbolOperator ) hidden;
	}

}
