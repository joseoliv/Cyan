/**

 */
package ast;


import java.util.List;
import lexer.CompilerPhase;
import lexer.Symbol;
import meta.CyanMetaobject;
import meta.Tuple2;
import meta.Tuple4;
import meta.WrAnnotation;
import saci.Env;

/**
 * Represents a metaobject annotation such as
 *       {@literal @}annot("green")
 *  or
 *       r"[A-Z]+[0-9]$"
 *
   @author José

 */

abstract public class Annotation extends Expr {

	public Annotation( CompilationUnitSuper compilationUnit,
			boolean inExpr, MethodDec method) {
		super(method);
		this.compilationUnit = compilationUnit;
		this.inExpr = inExpr;
		metaobjectAnnotationNumberByKind = -1;
		exprStatList = null;
		codeAnnotationParseWithCompiler = null;
		insideProjectFile = false;
		localVariableNameList = null;
	}

	@Override
	public WrAnnotation getI() {
		if ( iCyanAnnotation == null ) {
			iCyanAnnotation = new WrAnnotation(this);
		}
		return iCyanAnnotation;
	}

	private WrAnnotation iCyanAnnotation = null;

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}


	/**
	 * the first symbol that starts the metaobject annotation
	   @return
	 */
	@Override
	abstract public Symbol getFirstSymbol();

	/**
	 * get the postfix associated to this metaobject annotation. For example, we can have<br>
	 * <code>
	 * var g = {@literal @}graph#afterResTypes{* 1:2, 2:3 *}<br>
	 * </code><br>
	 * The postfix is "AFTER_RES_TYPES".
	 */
	abstract public CompilerPhase getPostfix();


	abstract public CyanMetaobject getCyanMetaobject();


	/**
	 * symbol after the metaobject annotation. If the annotation is
	 *     <code>
	 *     @text(option)<** this is a text **>
	 *     i = 0;
	 *     </code>
	 *  then this symbol is "i"
	 */
	@Override
	public Symbol getNextSymbol()  {
		return nextSymbol;
	}

	@Override
	public void setNextSymbol(Symbol nextSymbol) {
		this.nextSymbol = nextSymbol;
		char []text = compilationUnit.getText();
		StringBuffer sb = new StringBuffer();
		int n = nextSymbol.getOffset();
		for ( int i = this.getFirstSymbol().getOffset(); i < n; ++ i ) {
			sb.append(text[i]);
		}
		this.originalText = sb.toString();
	}



	/**
	 * return true if this is the first metaobject annotation, in textual order, of the prototype in which the metaobject annotation is.
	 * It includes metaobject annotations put before the metaobject but attached to it.
	 */
	public boolean isFirstCall() {
		return this.metaobjectAnnotationNumber == Annotation.firstAnnotationNumber;
	}


	/**
	 * package and prototype in which the metaobject annotation is, separated by spaces such
	 * as in "cyan.lang Any". For literal objects the file name is used instead of the prototype name.
	 */

	public String getPackagePrototypeOfAnnotation() {
		return getPackageOfAnnotation() + " " + getPrototypeOfAnnotation();
	}

	/**
	 * return the package in which the metaobject annotation is OR the project name in which the annotation is
	   @return
	 */
	public String getPackageOfAnnotation() {
		return compilationUnit.getPackageName();
	}

	public String getPrototypeOfAnnotation() {
		return currentPrototype == null ? null : this.currentPrototype.getName();
	}



	public CompilationUnitSuper getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(CompilationUnitSuper compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public Prototype getCurrentPrototype() {
		return currentPrototype;
	}

	public void setCurrentPrototype(Prototype currentPrototype) {
		this.currentPrototype = currentPrototype;
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		return "";
	}

	abstract public boolean isParsedWithCompiler();

	@Override
	public void calcInternalTypes(Env env) {

		if ( isParsedWithCompiler() )
			env.pushAnnotationParseWithCompiler(this);
		if ( exprStatList != null ) {
			for ( ICalcInternalTypes es : exprStatList ) {
				es.calcInternalTypes(env.getI());
			}
		}
		super.calcInternalTypes(env);
	}

	public void finalizeCalcInternalTypes(Env env) {
		if ( this.isParsedWithCompiler()  )
			env.popAnnotationParseWithCompiler();
	}


	public boolean getInExpr() {
		return inExpr;
	}

	public int getAnnotationNumber() {
		return metaobjectAnnotationNumber;
	}

	public void setAnnotationNumber(int metaobjectAnnotationNumber) {
		this.metaobjectAnnotationNumber = metaobjectAnnotationNumber;
	}


	/**
	 * number of the metaobject annotation in the source code, in textual order.
	 * The first metaobject annotation has number 1.
	   @return
	 */
	public int getAnnotationNumberByKind() {
		return metaobjectAnnotationNumberByKind;
	}

	public void setAnnotationNumberByKind(int metaobjectAnnotationNumberByKind) {
		this.metaobjectAnnotationNumberByKind = metaobjectAnnotationNumberByKind;
	}

	public List<ICalcInternalTypes> getExprStatList() {
		return exprStatList;
	}

	public void setExprStatList(List<ICalcInternalTypes> exprStatList) {
		this.exprStatList = exprStatList;
	}


	public StringBuffer getCodeAnnotationParseWithCompiler() {
		return codeAnnotationParseWithCompiler;
	}

	public void setCodeAnnotationParseWithCompiler(StringBuffer codeAnnotationParseWithCompiler) {
		this.codeAnnotationParseWithCompiler = codeAnnotationParseWithCompiler;
	}

	/**
	 * true if this metaobject annotation was made inside the project (.pyan) file.
	 *
	   @return
	 */
	public boolean getInsideProjectFile() {
		return insideProjectFile;
	}


	public void setInsideProjectFile(boolean insideProjectFile) {
		this.insideProjectFile = insideProjectFile;
	}


	public List<Tuple2<String, String>> getLocalVariableNameList() {
		return localVariableNameList;
	}

	public void setLocalVariableNameList(List<Tuple2<String, String>> localVariableNameList2) {
		this.localVariableNameList = localVariableNameList2;
	}

	/**
	 * the stack of local variables visible where the metaobject annotation is.
	 */
	private List<Tuple2<String, String>> localVariableNameList;

	/**
	 * replace the annotation by newAnnotationText in the text of the compilation unit
	 * in which the annotation is.
	   @param newAnnotationText
	 */
	public void replaceAnnotationBy( String newAnnotationText ) {

	}



	/**
	 *
	 * This method should be called by a IDE plugin to show the text associated to this metaobject annotation
	 * in several colors (text highlighting).

	 *
	 * Each tuple (color number, line number, column number, size). <br>
	 * The characters starting at line number, column number till column number
	 * + size - 1 should be highlighted in color "color number".	 *
	 * @return
	 */
	abstract public List<Tuple4<Integer, Integer, Integer, Integer>> getColorTokenList();

	/**
	 * number of the first number of a metaobject annotation in a prototype
	 */
	public static final int firstAnnotationNumber = 1;

	/**
	 * the compilation unit in which this metaobject annotation is, null if none
	 */
	transient protected CompilationUnitSuper compilationUnit;


	/**
	 * the program unit in which this metaobject annotation is, null if none
	 */
	protected Prototype currentPrototype;

	/**
	 * the symbol just after this metaobject annotation
	 */
	// transient private Symbol nextSymbol;
	/**
	 * true if the metaobject annotation is inside an expression
	 */
	private boolean inExpr;

	/**
	 * The number of this metaobject annotation. To each metaobject annotation in a prototype is associated a number starting with 1.
	 * This number is used for metaobject annotation to communicate with each other. This number is for all kinds of
	 * metaobjects
	 */

	private int	metaobjectAnnotationNumber;

	/**
	 * The number of this metaobject annotation considering only metaobjects with the same name as this.
	 * To each metaobject annotation in a prototype is associated a number starting with 1.
	 * This number is used for metaobject annotation to communicate with each other.
	 */

	private int	metaobjectAnnotationNumberByKind;

	/**
	 * this metaobject annotation may refer to expressions and statements of Cyan. These are grouped in exprStatList. This list is necessary
	 * in order to do semantic analysis on them in step SEM_AN.
	 */
	private List<ICalcInternalTypes> exprStatList;


	/**
	 * if this metaobject annotation is from a metaobject that implement IParseWithCyanCompiler_parsing or IParseMacro_parsing, the code produced
	 * during phase SEM_AN is put in this field
	 */
	protected StringBuffer codeAnnotationParseWithCompiler;

	/**
	 * original text of some metaobject annotations. It is between first symbol and next symbol
	 */
	protected String originalText;

	/**
	 * a list of colors
	 */
	protected List<Tuple4<Integer, Integer, Integer, Integer>> colorTokenList;

	/**
	 * true if this metaobject annotation was made inside the project (.pyan) file.
	 */
	protected boolean insideProjectFile;


}

