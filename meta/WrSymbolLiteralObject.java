package meta;

import lexer.SymbolLiteralObject;

public class WrSymbolLiteralObject extends WrSymbol {

	public WrSymbolLiteralObject(SymbolLiteralObject hidden) {
		super(hidden);
	}

	SymbolLiteralObject getHidden() {
		return (SymbolLiteralObject ) hidden;
	}

}

