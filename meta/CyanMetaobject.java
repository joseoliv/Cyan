 package meta;

import java.util.ArrayList;
import java.util.List;
import ast.CompilationUnit;
import ast.CompilationUnitSuper;
import ast.CyanPackage;
import meta.lexer.MetaLexer;

/**
 * Represents a Cyan metaobject which may be:
 * - a macro
 * - a literal object
 * - a metaobject annotation with @ such as {@literal @}init(name, age)
 *
   @author José
 */
abstract public class CyanMetaobject implements Cloneable {
	/*
	 * the old name is in the left, the new class name in the right
	 *
	 *

        ast.CyanAnnotation                    Annotation
        ast.CyanMetaobjectLiteralObjectAnnotation       AnnotationLiteralObject
        ast.CyanMetaobjectMacroCall                     AnnotationMacroCall
        ast.CyanMetaobjectWithAtAnnotation              AnnotationAt

        meta.WrAnnotation                 WrAnnotation
        meta.WrAnnotationLiteralObject    WrAnnotationLiteralObject
        meta.WrAnnotationMacroCall                  WrAnnotationMacroCall
        meta.WrAnnotationAt           WrAnnotationAt


        meta.CyanMetaobject
        meta.CyanMetaobjectWithAt                       CyanMetaobjectAtAnnot
        meta.CyanMetaobjectWithAtCodeg                  CyanMetaobjectAtAnnotCodeg
        meta.CyanMetaobjectLiteralObject
        meta.CyanMetaobjectMacro


	 */

	public CyanMetaobject() {
		errorList = null;
	}

	public WrAnnotation getAnnotation() {
		return annotation;
	}

	/**
	 * the name of the metaobject. A regular metaobject such as <code>{@literal @}checkStyle</code> has name
	 * <code>"checkStyle"</code>. A macro that use keywords "k1" and "k2" has name <code>"macro(k1, k2)"</code>.
	 * The name of a literal object that starts with a
	 * sequence of symbols such as</p>
	 * <code>{* 1:2, 2:3 *}</code></p>
	 * is the left sequence of symbols, <code>"{{@literal *}"</code>. See comments for the names of
	 * literal strings and numbers.
	   @return
	 */
	abstract public String getName();

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setAnnotation(WrAnnotation metaobjectAnnotation) {
		this.annotation = metaobjectAnnotation;
	}

	/*
	public void setAnnotation(Annotation annotation, int inutil) {
		if ( annotation != this.metaobjectAnnotation ) {
			System.out.println("annotation != annotation");
			System.exit(1);
		}
	}
	*/

	/**
	 * the replacement policy when creating an instantiation of a generic prototype.
	 * See enumeration {@see ReplacementPolicyInGenericInstantiation}. The default value
	 * is ReplacementPolicyInGenericInstantiation.REPLACE_BY_CYAN_VALUE
	 */
	public ReplacementPolicyInGenericInstantiation getReplacementPolicy() {
		return ReplacementPolicyInGenericInstantiation.REPLACE_BY_CYAN_VALUE;
	}


	/**
	 * If the metaobject annotation is an expression and has type <code>packageName.prototypeName</code>, this method returns
	 * <code>packageName</code> which may be a list of identifiers separated by dots.  Not all metaobject
	 * annotations have types (return null in those cases). For example, the annotation of <code>checkStyle</code> does not have a type:
	 * <code> <br>
	 * {@literal @}checkStyle  <br>
	 * object Person <br>
	 * ...<br>
	 * end<br>
	 * </code><br>
	 * But all literal objects have types. So do some macro calls and some metaobject annotations with {@literal @}.
	 * <code> <br>
	 * var i = 101bin;<br>
	 * ok = neg notFound; // neg is a macro<br>
	 * var g = {@literal @}graph{* 1:2, 2:3 *};<br>
	 * <br>
	 * </code><br>
	 *
	 * This method may be called even before the metaobject annotation is set. Therefore it should not
	 * consider that the field cyanAnnotation is non-null.
	 * 	   @return
	 */
	abstract public String getPackageOfType(); // { return null; }
	/**
	 * If the metaobject annotation has type <code>packageName.prototypeName</code>, this method returns
	 * <code>prototypeName</code>.  See {@link CyanMetaobjectLiteralObject#getPackageOfType()}
	   @return
	 */

	abstract public String getPrototypeOfType();


	/**
	 *
	 * This method should be called by a IDE plugin to show the text associated
	 * to the metaobject annotation <code>annotation</code> in several colors
	 * (text highlighting).
	 *
	 * Each tuple (color number, line number, column number, size). <br>
	 * The characters starting at line number, column number till column number
	 * + size - 1 should be highlighted in color "color number". The first line
	 * of the annotation has number 0. Then the line number is relative to the
	 * start of the annotation, not the source file being edited.
	 *
	 * <code>annotation</code> is redundant nowadays because this class
	 * already has a field with the same contents.
	 *
	 * @return
	 */
	public List<Tuple4<Integer, Integer, Integer, Integer>> getColorList() {
		return null;
	}

	/**
	 * This method should be called by a IDE plugin to show a list of code
	 * completion alternatives when the cursor is in character at position <code>offset</code> of
	 * the metaobject annotation. <code>offset</code> is 0 at the first character of the annotation.
	 * Then <code>offset</code> is relative to the annotation, not the source file.
	 * The return value is a list of tuples, each one is composed of an option and what should be
	 * inserted in the text being edited if this option is chosen. For example,
	 * the first tuple element could be<br>
	 * <code> substring(int beginIndex, int endIndex)</code><br>
	 * and the second could be<br>
	 * <code> substring(beginIndex, endIndex)</code><br>
	 */

	@SuppressWarnings("unused")
	public List<Tuple2<String, String>> getCodeCompletionAlternatives(int offset) {
		return null;
	}
	/**
	 * add an error for a call to a method of this metaobject. This method should be called by
	 * methods of a metaobject that should signal an error.
	 */
	public void addError(String message) {
		if ( errorList == null ) {
			errorList = new ArrayList<>();
		}
		errorList.add(new CyanMetaobjectError(
				annotation.getFirstSymbol(), message));
	}



	/**
	 * add an error. This method is used when the error is NOT in the metaobject annotation but
	 * in symbol 'symbol'.
	 */
	public void addError(WrSymbol symbol, String message) {
		if ( errorList == null ) {
			errorList = new ArrayList<>();
		}

		errorList.add(new CyanMetaobjectError(symbol, message));
	}

	public boolean isExpression() {
		return true;
	}


	/**
	 * After each call to a metaobject method, this method returns
	 * the error messages associated to that call. The error list is cleaned.
	   @return
	 */

	@SuppressWarnings("unchecked")
	public final List<CyanMetaobjectError> getErrorMessageList_cleanAll() {
		List<CyanMetaobjectError> ret;
		if ( errorList != null ) {
			ret = (List<CyanMetaobjectError>) ((ArrayList<CyanMetaobjectError> ) errorList).clone();
			errorList.clear();
			return ret;
		}
		else {
			return null;
		}
	}

	public final List<CyanMetaobjectError> getErrorList() {
		return errorList;
	}



	/** check if all packages in the list shouldImportList are imported by the current compilation unit
	   @param compiler_semAn
	 */
	protected void checkIfPackageWasImported(ICompiler_afterResTypes compiler_afterResTypes, String ...shouldImportList) {
		for ( final String shouldImportName : shouldImportList ) {
			boolean found = false;
			final WrEnv  env = compiler_afterResTypes.getEnv();
			if ( compiler_afterResTypes.getEnv().getImportedPackageSet() != null ) {
				for ( final WrCyanPackage aPackage : env.getImportedPackageSet() ) {
					final String name = aPackage.getPackageName();
					if ( name.equals(shouldImportName) ) {
						found = true;
						break;
					}
				}
				if ( ! found ) {
					this.addError("This metaobject demands that package '" + shouldImportName +
							"' be imported. Then put \n    import " + shouldImportName + "\nbefore the prototype declaration. " +
							  "Do not forget to change the project file (.pyan) to include this package");
				}
			}
		}
	}



	/** check if all packages in the list shouldImportList are imported by the current compilation unit
	   @param compiler_semAn
	 */
	protected void checkIfPackageWasImported(ICompiler_semAn compiler_semAn, String ...shouldImportList) {
		for ( final String shouldImportName : shouldImportList ) {
			boolean found = false;
			if ( compiler_semAn.getEnv().getImportedPackageSet() != null ) {
				for ( final WrCyanPackage aPackage : compiler_semAn.getEnv().getImportedPackageSet() ) {
					final String name = aPackage.getPackageName();
					if ( name.equals(shouldImportName) ) {
						found = true;
						break;
					}
				}
				if ( ! found ) {
					this.addError("This metaobject demands that package '" + shouldImportName +
							"' be imported. Then put \n    import " + shouldImportName + "\nbefore the prototype declaration. " +
							  "Do not forget to change the project file (.pyan) to include this package");
				}
			}
		}
	}


	/** check if all packages in the list shouldImportList are imported by the current compilation unit
	   @param compiler_semAn
	 */
	protected void checkIfPackageWasImported(ICompiler_parsing compiler_parsing, String ...shouldImportList) {
		for ( final String shouldImportName : shouldImportList ) {
			boolean found = false;
			if ( compiler_parsing.getCompilationUnit().getImportPackageSet() != null ) {
				for ( final WrCyanPackage aPackage : compiler_parsing.getCompilationUnit().getImportPackageSet() ) {
					final String name = aPackage.getPackageName();
					if ( name.equals(shouldImportName) ) {
						found = true;
						break;
					}
				}
			}
			if ( ! found ) {
				this.addError("This metaobject demands that package '" + shouldImportName +
						"' be imported. Then put \n    import " + shouldImportName + "\nbefore the prototype declaration. " +
						  "Do not forget to change the project file (.pyan) to include this package");
			}
		}
	}


	static public String escapeString(String s) {
		return MetaLexer.escapeJavaString(s);
	}
	static public String unescapeString(String s) {
		return MetaLexer.unescapeJavaString(s);
	}

	static public String removeQuotes(String s) {
		return MetaHelper.removeQuotes(s);
	}

	public void setCanonicalPath(String canonicalPath) {
		this.canonicalPath = canonicalPath;
	}

	public String getCanonicalPath() {
		return canonicalPath;
	}

	/**
	 * return the program unit in which the annotation is. null if none.
	   @return
	 */
	public WrPrototype getCurrentPrototype() {
		final CompilationUnitSuper cunit =
				annotation.getFirstSymbol().hidden.getCompilationUnit();
		if ( cunit instanceof CompilationUnit ) {
			return ((CompilationUnit ) cunit).getPublicPrototype().getI();
		}
		else {
			return null;
		}
	}


	@Feature("nocopy")
	public cyan.reflect._CyanMetaobject getMetaobjectInCyan() {
		return metaobjectInCyan;
	}


	@Feature("nocopy")
	public void setMetaobjectInCyan(cyan.reflect._CyanMetaobject metaobjectInCyan) {
		this.metaobjectInCyan = metaobjectInCyan;
	}


	public CyanPackage getCyanPackage() {
		return cyanPackage;
	}

	public void setCyanPackage(CyanPackage cyanPackage) {
		this.cyanPackage = cyanPackage;
	}


	private List<CyanMetaobjectError> errorList;

	/**
	 * name of the ".class" file in which this metaobject is.
	 */
	private String fileName;

	/**
	 * canonical path of the metaobject '.class'
	 */
	protected String canonicalPath;
	/**
	 *  name of the package in which this metaobject was declared
	 */
	protected String packageName;

	private CyanPackage cyanPackage;
	/**
	 * the specific metaobject annotation of this metaobject. For each
	 * annotation an object of a class that inherits from CyanMetaobject is created.
	 * Then this field is set with the annotation.
	 */
	protected WrAnnotation annotation;

	/**
	 * a metaobject codified in Cyan has a companion metaobject in Java. This points to the companion
	 * Cyan metaobject
	 */
	private cyan.reflect._CyanMetaobject metaobjectInCyan = null;
}
