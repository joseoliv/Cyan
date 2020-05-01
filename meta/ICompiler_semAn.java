package meta;

import java.util.List;

/**
 *
   @author José
 */
public interface ICompiler_semAn extends IAbstractCyanCompiler  {

	boolean isInPackageCyanLang(String name);



	List<WrFieldDec> getFieldList();

	List<WrMethodDec> getMethodDecList();

	WrFieldDec searchField(String strParam);
	/**
	 * search for local variable <code>varName</code>. Return null if not found.
	   @param varName
	   @return
	 */
	IVariableDecInterface searchLocalVariable(String varName);
	/**
	 * search for local variable or parameter <code>varName</code>. Return null if not found.
	   @param varName
	   @return
	 */
	IVariableDecInterface searchLocalVariableParameter(String varName);
	/**
	 * search for parameter <code>varName</code>. Return null if not found. The parameter may
	 * be of the method or any function that is in the scope.
	   @param varName
	   @return
	 */

	IVariableDecInterface searchParameter(String varName);

	/**
	 * return the list of all unary methods declared in the current prototype. Returns null if
	 * the metaobject annotation is not inside a prototype
	 */
	List<String> getUnaryMethodNameList();
	/**
	 * signal an error
	   @param sym
	   @param specificMessage
	   @param identifier
	   @param errorKind
	   @param furtherArgs
	 */

	void error(WrSymbol sym, String message);
	/**
	 * line number is 1 if the error is in the line of the metaobject annotation:<br>
	 * {@literal @}myDSL{* // line 1<br>
	 *      ...  // line 2 <br>
	 *      ...  // line 3 <br>
	 *      *}<br>
	 *
	   @param lineNumber
	   @param message
	 */
	void error(int lineNumber, int columnNumber, String message);

	/**
	 * the environment gives access to the data of the current compilation
	 *
	   @return the environment of the compilation
	 */

	WrEnv getEnv();
	/**
	 * return the column number of the metaobject annotation
	   @return
	 */
	int getColumnNumberCyanAnnotation();
	/**
	 * return the line number of the metaobject annotation
	   @return
	 */
	int getLineNumberCyanAnnotation();

	/**
	 * signs an error at the site of the generic prototype instantiation. That is,
	 * there should be an error in an generic prototype and the error is shown to the
	 * compiler user in the  generic prototype instantiation which is something like<br>
	 * <code> var SortedList<Person> personList;</code><br>
	 * The error, in this example, could be "Person' does not support the comparison methods such as '<=>'"
	   @param errorMessage
	 */
	void errorAtGenericPrototypeInstantiation(String errorMessage);

	/**
	 * return the feature list of the current prototype, if there is one. Otherwise return null
	 */

	List<Tuple2<String, WrExprAnyLiteral>> getFeatureList();

	/**
	 * remove the code of 'stat' and replace it by 'code'.
	 * This is being asked by metaobject annotation 'annot'.
	 * The type of the expression 'code' is 'codeType'
	 */
	boolean replaceStatementByCode(WrStatement stat, WrAnnotationAt annotation,
			StringBuffer code,
			WrType codeType);



	/**
	 * create a new prototype in phase SEM_AN of the compilation. This method should be used if
	 * code is produced in this phase that uses a generic prototype that does not appear in
	 * previous code. For example, suppose some metaobject in phase SEM_AN produces the anonymous function<br>
	 * <code>
	 *     { (: Float a, Double b :) ^a ++ " " ++ bb }<br>
	 * </code><br>
	 * There will be an error in step 7 of the compilation if this function does not appear
	 * in previous compilation steps: the compiler cannot create a new generic prototype instantiation
	 * in this step. Then you should use the method below to create, in step 6, phase SEM_AN, a
	 * instantiation <br>
	 * <code>     Function{@literal <}Float, Double, String></code><br>
	 * @return
	 */
	WrType createNewGenericPrototype(WrSymbol symUsedInError, WrCompilationUnitSuper compUnit,
			WrPrototype currentPU,
			String fullPrototypeName, String errorMessage);


	/**
	 * return a list with all arguments of the current generic prototype. If
	 * the current prototype is not generic, return null
	   @return
	 */
	public List<List<String>> getGenericPrototypeArgListList();


	void addNewTypeDef(String typename, WrType newtype );


}
