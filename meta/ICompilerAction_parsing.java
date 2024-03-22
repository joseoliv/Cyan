package meta;

import java.util.List;

public interface ICompilerAction_parsing extends IAbstractCyanCompiler {



	/**
	 * return a list with all arguments of the current generic prototype. If
	 * the current prototype is not generic, return null
	   @return
	 */
	List<List<String>> getGenericPrototypeArgListList();
	/**
	 * return the name of the current prototype. If the current symbol is outside a prototype, return null.
	 * This method cannot be called by a metaobject that is inside the prototype name as <code>{@literal @}wrong</code> in <br>
	 * <code>
	 * class G{@literal <}Int, {@literal @}wrong String{@literal >} <br>
	 * end<br>
	 * </code>
	 */
	String getCurrentPrototypeName();
	/**
	 * return the name of the current prototype without any generic parameters. That is, if the prototype
	 * is {@code "Int"}, the value returned is {@code "Int"}. If the prototype is {@code "Hashtable<String, Int>"},
	 * the value returned is {@code "Hashtable"}.
	 *
    */
	String getCurrentPrototypeId();
	/**
	 * get the current method
	 */
	WrMethodDec getCurrentMethod();

	/**
	 * return the feature list of the current prototype, if there is one. Otherwise return null
	 */

	List<Tuple2<String, WrExprAnyLiteral>> getFeatureList();
	/**
	 * return the current compilation unit, if there is one
	   @return
	 */

	WrCompilationUnit getCompilationUnit();

	/**
	 * return the text between offsetLeftCharSeq and offsetRightCharSeq - 1. The last character
	 * is followed by '\0'
	 */
	char[] getText(int offsetLeftCharSeq, int offsetRightCharSeq);
	WrPrototype searchPackagePrototype(String packageNameInstantiation, String prototypeNameInstantiation);
	void errorAtGenericPrototypeInstantiation(String errorMessage);
	//#$ {

//	String getPackageNameInstantiation();
//	String getPrototypeNameInstantiation();
//	int getLineNumberInstantiation();
//	int getColumnNumberInstantiation();
	//#$ }



	/** At the beginning of each instantiation of a generic prototype, the
	 * compiler adds an annotation <br>
	 * <code> {@literal @}genericPrototypeInstantiationInfo("main", "Program", 2457, 9) </code><br>
	 * that gives information on the package (main), prototype (Program), line, and column
	 * of the instantiation. Metaobject of annotation genericPrototypeInstantiationInfo uses
	 * the four methods below to set these information. The methods can also be
	 * used by metaobjects that create new prototypes. If there is an error in
	 * one of the created prototypes, the compiler will point out the annotation
	 * that created it.
	 */
	//#$ {
//	void setPrototypeNameInstantiation(String prototypeNameInstantiation);
//	void setPackageNameInstantiation(String packageNameInstantiation);
//	void setLineNumberInstantiation(int lineNumberInstantiation);
//	void setColumnNumberInstantiation(int columnNumberInstantiation);
	//#$ }


	void error(int lineNumber, String message);
	void error(WrSymbol sym, String message);
	void error(int lineNumber, int columnNumber, String message);


	WrEnv getEnv();
}
