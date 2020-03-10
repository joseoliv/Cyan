package meta;

import lexer.SymbolLiteralObjectParsedWithCompiler;

public class WrSymbolLiteralObjectParsedWithCompiler extends WrSymbol {

	public WrSymbolLiteralObjectParsedWithCompiler(SymbolLiteralObjectParsedWithCompiler hidden) {
		super(hidden);
	}

	SymbolLiteralObjectParsedWithCompiler getHidden() {
		return (SymbolLiteralObjectParsedWithCompiler ) hidden;
	}

}
