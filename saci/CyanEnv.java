/**
 *
 */
package saci;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import ast.CompilationUnit;
import ast.Expr;
import ast.ExprGenericPrototypeInstantiation;
import ast.ExprIdentStar;
import ast.ExprLiteralString;
import ast.ExprTypeUnion;
import ast.GenericParameter;
import ast.JVMPackage;
import ast.MethodDec;
import ast.Program;
import ast.Prototype;
import ast.Type;
import ast.TypeJavaRef;
import error.CompileErrorException;
import error.ErrorKind;
import meta.MetaHelper;

/** Environment for code generation for Cyan. That is, the target language is Cyan itself. This
 * is necessary when creation an instance of a generic prototype and when adding code for
 * prototypes. The compiler adds inner prototypes for each prototypes. One for each function
 * in the prototype and one inner prototype for each method.
 *
 * @author jose
 *
 */
final public class CyanEnv {


	/**
	 * use this constructor when generating code for ".iyan" files
	 */
	public CyanEnv(Program program) {
		this(false, false);
		this.program = program;
	}

	/**
	 * use this constructor when generating code for inner prototypes (one for each function and method)
	 */
	public CyanEnv(boolean creatingInnerPrototypeFromFunction, boolean creatingContextObject) {
		currentPrototype = null;
		currentMethod = null;
		this.exprGenericPrototypeInstantiation = null;
		this.formalParamToRealParamTable = null;
		this.realParamListList = null;
		creatingInstanceGenericPrototype = false;
		this.creatingInnerPrototypesInsideEval = false;
		this.creatingInnerPrototypeFromFunction = creatingInnerPrototypeFromFunction;
		this.creatingContextObject = creatingContextObject;
	}

	/**
	 * use this constructor when generating code for instantiations of generic prototypes
	   @param genericPrototype
	   @param exprGenericPrototypeInstantiation
	   @param env
	 */
	public CyanEnv(Prototype genericPrototype, ExprGenericPrototypeInstantiation exprGenericType, Env env,
			String packageNameInstantiation, String prototypeNameInstantiation) {

		creatingInstanceGenericPrototype = true;
		creatingInnerPrototypesInsideEval = false;
		currentPrototype = null;
		realParamListList = new ArrayList<>();

		this.packageNameInstantiation = packageNameInstantiation;
		this.prototypeNameInstantiation = prototypeNameInstantiation;
		/*
		 * if getPlus of the first parameter is true then this is a generic prototype with varying number of
		 * parameters. The parameter types should not be associated to the real parameters given by exprGenericPrototypeInstantiation
		 */
		this.exprGenericPrototypeInstantiation = exprGenericType;
		// does the generic program unit supports a varying number of parameters?
		boolean isGPWithVaryingNumberParam = genericPrototype.getGenericParameterListList().get(0).get(0).getPlus();

		//this.genericPrototype = genericPrototype;
		//this.exprGenericType = exprGenericPrototypeInstantiation;
		formalParamToRealParamTable = new Hashtable<String, String>();
		//formalParamToRealParamFileNameTable = new Hashtable<String, String>();
		int i, j;
		String realParam; // realParamFileName;

		if ( isGPWithVaryingNumberParam ) {
			for ( List<Expr> exprList : exprGenericType.getRealTypeListList() ) {
				List<String> realParamList = new ArrayList<>();
				for ( Expr e : exprList ) {
					if ( e instanceof ExprGenericPrototypeInstantiation ) {
						ExprGenericPrototypeInstantiation genProtoInst = (ExprGenericPrototypeInstantiation) e;
						TupleTwo<String, Type> t = genProtoInst.ifPrototypeReturnsNameWithPackageAndType(env);
						if ( t == null || t.f2 == null ) {
							env.error(genProtoInst.getFirstSymbol(), "Prototype '" + genProtoInst.getName() + "' was not found", true, true);
							return ;
						}
						realParam = t.f1;
					}
					else if ( e instanceof ExprIdentStar ) {
						realParam = ((ExprIdentStar ) e).getName();
						realParam = extractGenericParam(env, realParam, e);
						if ( realParam == null )
							return ;
					}
					else if ( e instanceof ExprLiteralString ) {
						realParam = ((ExprLiteralString ) e).getSymbol().getSymbolString();
					}
					else if ( e instanceof ExprTypeUnion ) {
						realParam = ((ExprTypeUnion ) e).getType().getFullName().replace(NameServer.cyanLanguagePackageNameDot, "");
					}
					else {
						realParam = e.getFirstSymbol().getSymbolString();
						env.error(true, e.getFirstSymbol() ,
								"Formal parameter expected. Found '" + realParam + "'", realParam, ErrorKind.real_parameter_of_generic_prototype_expected);
					}

					realParamList.add(realParam);
				}
				realParamListList.add(realParamList);
			}
		}
		else {


			//usedGenericPrototypeSet = new HashSet<String>();
			i = 0;
			for ( List<GenericParameter> gpList : genericPrototype.getGenericParameterListList()  ) {
				j = 0;
				List<String> realParamList = new ArrayList<>();
				if ( i < exprGenericType.getRealTypeListList().size() ) {
					for ( GenericParameter gp : gpList ) {
						if ( j < exprGenericType.getRealTypeListList().get(i).size() ) {
							Expr e = exprGenericType.getRealTypeListList().get(i).get(j);
							if ( e instanceof ExprGenericPrototypeInstantiation ) {
								ExprGenericPrototypeInstantiation genProtoInst = (ExprGenericPrototypeInstantiation) e;

								TupleTwo<String, Type> t = genProtoInst.ifPrototypeReturnsNameWithPackageAndType(env);
								if ( t == null || t.f2 == null ) {
									env.error(genProtoInst.getFirstSymbol(), "Prototype '" + genProtoInst.getName() + "' was not found", true, true);
									return ;
								}
								realParam = t.f1;
							}
							else if ( e instanceof ExprIdentStar ) {
								// realParamFileName =
								realParam = ((ExprIdentStar ) e).getName();

								realParam = extractGenericParam(env, realParam, e);
								if ( realParam == null )
									return ;
							}
							else if ( e instanceof ExprLiteralString ) {
								realParam = ((ExprLiteralString ) e).getSymbol().getSymbolString();
							}
							else if ( e instanceof ExprTypeUnion ) {
								realParam = ((ExprTypeUnion ) e).getType(env).getFullName()
										.replace(NameServer.cyanLanguagePackageNameDot, "");
							}
							else {
								//realParamFileName =
								realParam = e.getFirstSymbol().getSymbolString();
								env.error(true, e.getFirstSymbol() ,
										"Formal parameter expected. Found '" + realParam + "'", realParam, ErrorKind.real_parameter_of_generic_prototype_expected);
							}
							formalParamToRealParamTable.put( gp.getName(), realParam );


							if ( realParam.contains("cyan.lang") ) {
								// should never happen
								realParam = extractGenericParam(env, realParam, e);
							}

							realParamList.add(realParam);
							//formalParamToRealParamFileNameTable.put( gp.getName(), realParamFileName );
							++j;
						}
					}
					++i;

				}
				realParamListList.add(realParamList);

			}
		}
	}


	/**
	 * returns realParam without package cyan.lang and with the real package.
	   @param env
	   @param realParam, a string with a prototype name possibly preceded by a package name. The prototype name is not
	   an instantiation of a generic prototype
	   @param e
	   @return
	 */
	private static String extractGenericParam(Env env, String realParam, Expr e) {
		String packageName;

		if ( Character.isUpperCase(realParam.charAt(0)) ) {
			if (  realParam.indexOf('.') >= 0  ) {
				env.error(true, e.getFirstSymbol(), "Parameter '" + realParam +
								"' is illegal. It should be either a symbol as 'name' or a prototype as 'main.Program'",
						realParam, ErrorKind.prototype_was_not_found_inside_method);
				return null;
			}
			else {
				if ( realParam.equals(MetaHelper.dynName) )
					return realParam;
				// a single prototype name without a package name
				Prototype pu = env.searchVisiblePrototype(realParam, e.getFirstSymbol(), false);
				if ( pu != null ) {
					CompilationUnit cunit = pu.getCompilationUnit();
					packageName = cunit.getPackageName();
					if ( packageName.equals(MetaHelper.cyanLanguagePackageName) )
						return realParam;
					else {
						return packageName + "." + realParam;
					}
				}
				else {

					TypeJavaRef javaClass = env.searchVisibleJavaClass(realParam);
					if ( javaClass == null ) {
						env.error(true, e.getFirstSymbol(), "Prototype '" + realParam + "' was not found",
								realParam, ErrorKind.prototype_was_not_found_inside_method);
						return null;
					}
					packageName = javaClass.getJavaPackage();
					return  packageName + "." + javaClass.getName();

				}

			}
		}
		else {
			int indexOfDot = realParam.lastIndexOf('.');
			if ( indexOfDot > 0 ) {
				// starts with a lower-case letter and has a package name preceding the prototype name
				String prototypeName = realParam.substring(indexOfDot + 1);
				packageName = realParam.substring(0, indexOfDot);
				String fullName;
				if ( packageName.equals(MetaHelper.cyanLanguagePackageName) )
					fullName =  prototypeName;
				else {
					// not in cyan.lang package
					fullName = packageName + "." + prototypeName;
				}
				Prototype pu = env.searchVisiblePrototype(fullName, e.getFirstSymbol(), false);
				if ( pu != null ) {
					return fullName;
				}
				else {
					JVMPackage jvmPackage = env.getProject().getProgram().searchJVMPackage(packageName);

					if ( jvmPackage != null && jvmPackage.searchJVMClass(prototypeName) != null ) {
						return fullName;
					}

					env.error(true, e.getFirstSymbol(), "Prototype '" + realParam + "' was not found",
							realParam, ErrorKind.prototype_was_not_found_inside_method);
					return null;
				}

			}
			else
				return realParam;
		}
	}


	/**
	 * This method should only be called when in a generic prototype instantiation
	 * such as when prototype Tuple<f1, Int> is being created from Tuple<F1, T1>.
	 *
	 * Given "T1" as parameter, this method would return "Int". If the parameter
	 * is "F1:", the return is "f1:". If it is "F1", the return is "f1".
	 *
	 * If formalParam is not a formal parameter, its value is returned.
	 *
	 * @param formalParam, the formal parameter name
	 * @return
	 */
	public String formalGenericParamToRealParam(String formalParam) {
		boolean colon = false;
		String s = formalParam;
		if ( s.charAt(s.length()-1) == ':' ) {
			s = s.substring(0, s.length()-1);
			colon = true;
		}
		String realParam = formalParamToRealParamTable.get(s);
		if ( realParam == null ) {
			return formalParam;
		}
		else {
			if ( colon ) realParam = realParam + ":";
			return realParam;

		}
	}


	public Hashtable<String, String> getFormalParamToRealParamTable() {
		return formalParamToRealParamTable;
	}


	/*public Hashtable<String, String> getFormalParamToRealParamFileNameTable() {
		return formalParamToRealParamFileNameTable;
	} */

	/* public Set<String> getUsedGenericPrototypeSet() {
		return usedGenericPrototypeSet;
	} */

	public void error(String message) {
		System.out.println(message);
		throw new CompileErrorException(message);
	}



	public List<List<String>> getRealParamListList() {
		return realParamListList;
	}



	public void atBeginningOfPrototype(Prototype currentObjectDec) {
		this.currentPrototype = currentObjectDec;
	}

	public void atEndOfCurrentPrototype() {
		currentPrototype = null;
	}

	public Prototype getCurrentPrototype() {
		return currentPrototype;
	}

	public boolean getCreatingInstanceGenericPrototype() {
		return creatingInstanceGenericPrototype;
	}

	public boolean getCreatingInnerPrototypesInsideEval() {
		return creatingInnerPrototypesInsideEval;
	}

	public void setCreatingInnerPrototypesInsideEval(boolean creatingInnerPrototypesInsideEval) {
		this.creatingInnerPrototypesInsideEval = creatingInnerPrototypesInsideEval;
	}


	public boolean getCreatingInnerPrototypeFromFunction() {
		return creatingInnerPrototypeFromFunction;
	}


	public void atEndOfMethodDec() {
		currentMethod = null;
	}

	public MethodDec getCurrentMethod() {
		return currentMethod;
	}


	public void atBeginningOfMethodDec(MethodDec methodDec) {
		this.currentMethod = methodDec;
	}

	public boolean getCreatingContextObject() {
		return creatingContextObject;
	}




	public ExprGenericPrototypeInstantiation getExprGenericPrototypeInstantiation() {
		return exprGenericPrototypeInstantiation;
	}

	public String getPackageNameInstantiation() {
		return packageNameInstantiation;
	}

	public String getPrototypeNameInstantiation() {
		return prototypeNameInstantiation;
	}


	public boolean getGenInterfacesForCompiledCode() {
		return genInterfacesForCompiledCode;
	}

	public void setGenInterfacesForCompiledCode(boolean interfacesOnly) {
		this.genInterfacesForCompiledCode = interfacesOnly;
	}



	public boolean getPrintNewLineAfterAnnotation() {
		return printNewLineAfterAnnotation;
	}

	public void setPrintNewLineAfterAnnotation(
			boolean printNewLineAfterAnnotation) {
		this.printNewLineAfterAnnotation = printNewLineAfterAnnotation;
	}

	public boolean isInPackageCyanLang(String name) {
		return program.isInPackageCyanLang(name);
	}

	/**
	 * the current Program unit
	 */
	private Prototype	currentPrototype;


	/**
	 * hastable with pairs Type/RealType. In Stack<T> and Stack<Int>, the only pair would be
	 * ("T", "Int"). In Stack<T> and Stack<Array<List<Int>>>, the only pair would be
	 * ("T", "Array<List<Int>>")
	 */
	private Hashtable<String, String> formalParamToRealParamTable;

	/**
	 * the expression with the generic prototype instantiation
	 */
	private ExprGenericPrototypeInstantiation exprGenericPrototypeInstantiation;

	/**
	 * list with strings with the real parameters
	 */
	private List<List<String>> realParamListList;

	/**
	 * true if this class is used for generating code for an instance of a generic prototype.
	 * false otherwise (of course).
	 */
	private boolean creatingInstanceGenericPrototype;


	/**
	 * true if CyanEnv is being used for generating code for method 'eval' or 'eval:' of an inner prototype
	 */
	private boolean	creatingInnerPrototypesInsideEval;
	/**
	 * true if CyanEnv is being used for generating code for an inner prototype from a function
	 */
	private boolean creatingInnerPrototypeFromFunction;
	/**
	 * true if CyanEnv is being used for generating code for the statements of a context object
	 */
	private boolean creatingContextObject;


	private MethodDec currentMethod;


	/**
	 * package in which the instantiation is
	 */
	private String packageNameInstantiation;
	/**
	 * prototype in which the instantiation is
	 */
	private String prototypeNameInstantiation;


	/**
	 * true if the Cyan code to be generated is composed of interfaces of
	 * already compiled prototypes
	 */
	private boolean genInterfacesForCompiledCode = false;
	/**
	 * true if \n should be generated after an annotation. Then, if this field is
	 * true, the result can be
	 *       {@literal @}prototypeCallOnly
	 *       {@literal @}anotherAnnot
	 *       func m { ... }
	 */
	private boolean printNewLineAfterAnnotation;
	private Program program;

}
