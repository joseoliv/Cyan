/**
 *
 */
package lexer;

import ast.CompilationUnitSuper;
import ast.Annotation;
import ast.AnnotationLiteralObject;
import meta.Token;
import meta.WrSymbolLiteralObject;
/**
 * Represents a literal object that is parsed with a user-defined compiler. That is,
 * the text inside the literal object is not parsed with the help of the Cyan compiler.
 * For example, usually literal strings that start with a letter and user-defined
 * literal numbers do not use the Cyan compiler.
 * <code><br>
 * var regexpr = r"0+[a-z]*"; <br>
 * var five = 101bin;<br>
 * </code>
 *
 * @author José
 *
 */
public class SymbolLiteralObject extends Symbol
             implements IWithCompilerPhase {

	public SymbolLiteralObject(Token token, AnnotationLiteralObject cyanMetaobjectLiteralObjectAnnotation,
			CompilerPhase compilerPhase,
			String symbolString,
			String usefullString,
			char []text,
			int startLine, int lineNumber, int columnNumber, int offset,
			CompilationUnitSuper compilationUnit
			) {
		  // the left sequence of symbols is used as the string of the symbol
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
		this.cyanMetaobjectLiteralObjectAnnotation = cyanMetaobjectLiteralObjectAnnotation;
		this.compilerPhase = compilerPhase;
		this.usefullString = usefullString;
		this.text = text;
	}

	public SymbolLiteralObject(Token token, AnnotationLiteralObject cyanMetaobjectLiteralObjectAnnotation,
			String symbolString,
			int startLine, int lineNumber, int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
		this.cyanMetaobjectLiteralObjectAnnotation = cyanMetaobjectLiteralObjectAnnotation;
		this.usefullString = null;
		this.text = null;
	}

	@Override
	public WrSymbolLiteralObject getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolLiteralObject(this);
		}
		return (WrSymbolLiteralObject ) isymbol;
	}


	public char [] getText() {
		return text;
	}



	public AnnotationLiteralObject getCyanMetaobjectLiteralObjectAnnotation() {
		return cyanMetaobjectLiteralObjectAnnotation;
	}

	public String getUsefulString() {
		return usefullString;
	}



	@Override
	public CompilerPhase getPostfix() {
		return compilerPhase;
	}

	@Override
	public int getColor() {
		return HighlightColor.literalObject;
	}

	@Override
	public Annotation getCyanAnnotation() {
		return this.cyanMetaobjectLiteralObjectAnnotation;
	}


	/**
	 * represents the metaobject associated to this literal object
	 */
	private final AnnotationLiteralObject cyanMetaobjectLiteralObjectAnnotation;
	/**
	 * text inside the literal object. In
	 *        @graph<<*  1:2, 2:1, 1:3 *>>
	 *  it is "  1:2, 2:1, 1:3 ". In
	 *       var v = (# 1, 2, 3 #)
	 *  it is " 1, 2, 3 ".
	 *
	 *  This is the same content of usefullString
	 */
	private final char []text;

	/**
	 * the useful part of the literal object. In
	 *        [* (1, 2), (2, 1) *]
	 * the useful part is " (1, 2), (2, 1) ". In
	 *       01011Bin
	 * the useful part is "01011"
	 *  This is the same content of field 'text'
	 */
	private final String usefullString;


	/**
	 * the metaobject name may be postfixed by compilerPhase.getName() which is "parsing", "AFTER_RES_TYPES", "SEM_AN", "AFTER_SEM_AN", or "cge".
	 */
	private CompilerPhase compilerPhase;
}