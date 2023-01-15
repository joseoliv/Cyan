package meta;

import lexer.SymbolCyanAnnotation;

public class WrSymbolCyanAnnotation extends WrSymbol {

	public WrSymbolCyanAnnotation(SymbolCyanAnnotation hidden) {
		super(hidden);
	}

	SymbolCyanAnnotation getHidden() {
		return (SymbolCyanAnnotation ) hidden;
	}
}
