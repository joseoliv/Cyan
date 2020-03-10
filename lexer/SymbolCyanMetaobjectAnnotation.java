package lexer;

import ast.CompilationUnitSuper;
import ast.Annotation;
import ast.AnnotationAt;
import meta.Token;
import meta.WrSymbolCyanMetaobjectAnnotation;

/**
 * Describes an compile-time metaobject annotation.
 *
 * @author José
 *
 */

public class SymbolCyanMetaobjectAnnotation extends Symbol {

	/**
	 * @param token  is Token.annotation
	 * @param annotationName  is the name of the metaobject, "color" in @color(blue)
	 * @param postfix is dpa, afti, or dsa. If it is "afti", for example, and the metaobject name, annotationName,
	 * is "init", then the annotation is "init#afti" instead of just "init" --- we did not
	 * show the parameters to this metaobject annotation.
	 * @param lineNumber
	 * @param columnNumber
	 */
	public SymbolCyanMetaobjectAnnotation(Token token, String annotationName, CompilerPhase postfix,
			int startLine, int lineNumber, int columnNumber, int offset, boolean leftParAfterMetaobjectAnnotation, CompilationUnitSuper compilationUnit) {
		super(token, annotationName, startLine, lineNumber, columnNumber, offset, compilationUnit);
		this.name = annotationName;
		this.postfix = postfix;
		this.leftParAfterMetaobjectAnnotation = leftParAfterMetaobjectAnnotation;
	}

	@Override
	public WrSymbolCyanMetaobjectAnnotation getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolCyanMetaobjectAnnotation(this);
		}
		return (WrSymbolCyanMetaobjectAnnotation ) isymbol;
	}





	public String getName() {
		return name;
	}

	public CompilerPhase getPostfix() {
		return postfix;
	}


	public boolean getLeftParAfterMetaobjectAnnotation() {
		return leftParAfterMetaobjectAnnotation;
	}

	public AnnotationAt getMetaobjectAnnotation() {
		return metaobjectAnnotation;
	}


	public void setMetaobjectAnnotation(AnnotationAt metaobjectAnnotation) {
		this.metaobjectAnnotation = metaobjectAnnotation;
	}
	@Override
	public int getColor() {
		return HighlightColor.cyanMetaobjectAnnotation;
	}

	@Override
	public Annotation getCyanMetaobjectAnnotation() {
		return this.metaobjectAnnotation;
	}

	/**
	 * the name of the metaobject. The same as this.getSymbolString()
	 */
	private final String name;

	/**
	 * the metaobject name may be postfixed by postfix.getName() which is "dpa", "afti", "dsa", or "cge".
	 */
	private final CompilerPhase postfix;

	/**
	 * true if there is a '(' just after the metaobject annotation as in <code>{@literal @}text(trim){* ... *}</code>.
	 * false otherwise as in <code>{@literal @}text (trim){* ... *}</code>. There is a space before the '('.
	 */
	private final boolean leftParAfterMetaobjectAnnotation;

	/**
	 * the metaobject annotation associated to this symbol
	 */
	private AnnotationAt metaobjectAnnotation;

}