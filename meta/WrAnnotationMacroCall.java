package meta;

import ast.AnnotationMacroCall;

public class WrAnnotationMacroCall extends WrAnnotation {

	public WrAnnotationMacroCall(AnnotationMacroCall hidden) {
		super(hidden);
	}

	@Override
	AnnotationMacroCall getHidden() {
		return (AnnotationMacroCall ) hidden;
	}


}
