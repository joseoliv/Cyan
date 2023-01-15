package lexer;

import ast.CompilationUnitSuper;
import ast.Annotation;
import ast.AnnotationAt;
import meta.Token;
import meta.WrSymbolCyanAnnotation;

/**
 * Describes an compile-time metaobject annotation.
 *
 * @author José
 *
 */

public class SymbolCyanAnnotation extends Symbol {

	/**
	 * @param token  is Token.annotation
	 * @param annotationName  is the name of the metaobject, "color" in @color(blue)
	 * @param postfix is parsing, AFTER_RES_TYPES, or SEM_AN. If it is "AFTER_RES_TYPES", for example, and the metaobject name, annotationName,
	 * is "init", then the annotation is "init#afterResTypes" instead of just "init" --- we did not
	 * show the parameters to this metaobject annotation.
	 * @param lineNumber
	 * @param columnNumber
	 */
	public SymbolCyanAnnotation(Token token, String annotationName, CompilerPhase postfix,
			int startLine, int lineNumber, int columnNumber, int offset, boolean leftParAfterAnnotation, CompilationUnitSuper compilationUnit) {
		super(token, annotationName, startLine, lineNumber, columnNumber, offset, compilationUnit);
		this.name = annotationName;
		this.postfix = postfix;
		this.leftParAfterAnnotation = leftParAfterAnnotation;
	}

	@Override
	public WrSymbolCyanAnnotation getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolCyanAnnotation(this);
		}
		return (WrSymbolCyanAnnotation ) isymbol;
	}





	public String getName() {
		return name;
	}

	public CompilerPhase getPostfix() {
		return postfix;
	}


	public boolean getLeftParAfterAnnotation() {
		return leftParAfterAnnotation;
	}

	public AnnotationAt getAnnotation() {
		return metaobjectAnnotation;
	}


	public void setAnnotation(AnnotationAt metaobjectAnnotation) {
		this.metaobjectAnnotation = metaobjectAnnotation;
	}
	@Override
	public int getColor() {
		return HighlightColor.cyanAnnotation;
	}

	@Override
	public Annotation getCyanAnnotation() {
		return this.metaobjectAnnotation;
	}

	/**
	 * the name of the metaobject. The same as this.getSymbolString()
	 */
	private final String name;

	/**
	 * the metaobject name may be postfixed by postfix.getName() which is "parsing", "AFTER_RES_TYPES", "SEM_AN", or "cge".
	 */
	private final CompilerPhase postfix;

	/**
	 * true if there is a '(' just after the metaobject annotation as in <code>{@literal @}text(trim){* ... *}</code>.
	 * false otherwise as in <code>{@literal @}text (trim){* ... *}</code>. There is a space before the '('.
	 */
	private final boolean leftParAfterAnnotation;

	/**
	 * the metaobject annotation associated to this symbol
	 */
	private AnnotationAt metaobjectAnnotation;

}