package meta;

abstract public class CyanMetaobjectAtAnnotCodeg extends CyanMetaobjectAtAnnot implements ICodeg {

	public CyanMetaobjectAtAnnotCodeg(String name,
			AnnotationArgumentsKind parameterKind) {
		super(name, parameterKind);
	}

	public CyanMetaobjectAtAnnotCodeg(String name, AnnotationArgumentsKind parameterKind,
			AttachedDeclarationKind []decKindList) {
		super(name, parameterKind, decKindList);
	}

	@Override public int getStartOffset() {
		return this.metaobjectAnnotation.getFirstSymbol().getOffset();
	}
	@Override public int getEndOffset() {
		final WrSymbol last = this.metaobjectAnnotation.getLastSymbol();
		return last.getOffset() + last.getSymbolString().length() - 1;
	}

}
