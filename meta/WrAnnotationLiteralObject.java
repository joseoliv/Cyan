package meta;

import ast.AnnotationLiteralObject;

public class WrAnnotationLiteralObject extends WrAnnotation {

	public WrAnnotationLiteralObject(AnnotationLiteralObject hidden) {
		super(hidden);
	}

	@Override
	AnnotationLiteralObject getHidden() {
		return (AnnotationLiteralObject ) hidden;
	}

	public String getUsefulString() {
		return ((AnnotationLiteralObject ) hidden).getUsefulString();
	}


}
