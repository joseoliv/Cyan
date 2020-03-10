package meta;

import lexer.SymbolKeyword;

public class WrSymbolKeyword extends WrSymbol {

	public WrSymbolKeyword(SymbolKeyword hidden) {
		super(hidden);
	}

	SymbolKeyword getHidden() {
		return (SymbolKeyword ) hidden;
	}

}
