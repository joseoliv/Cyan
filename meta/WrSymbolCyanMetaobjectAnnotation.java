package meta;

import lexer.SymbolCyanMetaobjectAnnotation;

public class WrSymbolCyanMetaobjectAnnotation extends WrSymbol {

	public WrSymbolCyanMetaobjectAnnotation(SymbolCyanMetaobjectAnnotation hidden) {
		super(hidden);
	}

	SymbolCyanMetaobjectAnnotation getHidden() {
		return (SymbolCyanMetaobjectAnnotation ) hidden;
	}
}
