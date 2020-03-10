package meta;

import lexer.SymbolIdent;

public class WrSymbolIdent extends WrSymbol {

	public WrSymbolIdent(SymbolIdent hidden) {
		super(hidden);
	}

	SymbolIdent getHidden() {
		return (SymbolIdent ) hidden;
	}

}
