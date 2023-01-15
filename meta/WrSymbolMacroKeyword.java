package meta;

import lexer.SymbolMacroKeyword;

public class WrSymbolMacroKeyword extends WrSymbolKeyword {

	public WrSymbolMacroKeyword(SymbolMacroKeyword hidden) {
		super(hidden);
	}

	@Override
	SymbolMacroKeyword getHidden() {
		return (SymbolMacroKeyword ) hidden;
	}

}
