package meta;

import java.util.List;

/**
 * An interface of the compiler as seen by metaobjects in the phase "after typing interfaces"
   @author José
 */
public interface ICompiler_afterResTypes extends IAbstractCyanCompiler {


	/**
	 * return a list of the fields of the current prototype
	 *
	   @return
	 */
	List<WrFieldDec> getFieldList();

	/**
	 * return the list of methods of the current prototype
	   @return
	 */
	List<WrMethodDec> getMethodDecList();

	/**
	 * return the field of the current prototype whose name is 'fieldName'
	   @param strParam
	   @return
	 */
	WrFieldDec searchField(String fieldName);


	/**
	 * Create and returns a unique name for a field of
	 * prototype prototypeName of package packageName.
	 *
	   @param packageName
	   @param prototypeName
	   @return the unique name such as "id1011"
	 */
	String getUniqueFieldName(String packageName, String prototypeName);
	/**
	 * Create and returns a unique method name for prototype prototypeName of package packageName
	 *
	   @param numberOfkeywords number of keywords of the method
	   @param packageName
	   @param prototypeName
	   @return an array with the keywords of the unique method name
	 */
	String []getUniqueMethodName(int numberOfkeywords, String packageName, String prototypeName);


	void error(WrSymbol sym, String message);
	/**
	 * the environment gives access to the data of the current compilation
	 *
	   @return the environment of the compilation
	 */

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
	 * return true if the current prototype is an interface. If getCurrentPrototypeName() returns null, this returns false
	   @return
	 */
	boolean isCurrentPrototypeInterface();

	WrEnv getEnv();


	/**
	 * return the feature list of the current prototype, if there is one. Otherwise return null
	 */

	List<Tuple2<String, WrExprAnyLiteral>> getFeatureList();

	/**
	 * return the current program unit
	   @return
	 */
	WrPrototype getPrototype();

	/**
	 * return the current compilation unit
	 */
	WrCompilationUnit getCompilationUnit();
	/**
	 * return dpaGeneric, dpaNonGeneric
	 */
	/**
	 * return the action function whose name is 'name'
	   @param name
	   @return
	 */
	IActionFunction searchActionFunction(String name);

}

