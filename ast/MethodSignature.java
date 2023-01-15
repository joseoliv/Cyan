/**
   Represents a method signature which may be:
     - a unary method such as
          fun getName -> String
          fun set
     - a regular keyword method such as
          fun width: (:w int)  height: (:h int) [ ... ]
     - a grammar method such as
          fun (add: int)+
     - a method signature of an interface, which may not have
       parameter names (only the types are demanded)

    There are subclasses representing each of these possibilities.
 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.AttachedDeclarationKind;
import meta.GetHiddenItem;
import meta.MethodComplexName;
import meta.Tuple2;
import meta.WrExpr;
import meta.WrExprAnyLiteral;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 * @author José
 *
 */
abstract public class MethodSignature implements GenCyan, ASTNode, GetNameAsInSourceCode {

	public MethodSignature(MethodDec method) {
		this.method = method;
		attachedAnnotationList = null;
		hasCalculatedInterfaceTypes = false;
		fullName = null;
	}

	public MethodSignature(WrMethodDec method) {
		this(meta.GetHiddenItem.getHiddenMethodDec(method));
	}


	@Override
	abstract public WrMethodSignature getI();

	public void setReturnTypeExpr(Expr returnType) {
		this.returnTypeExpr = returnType;
	}

	public void setReturnTypeExpr(WrExpr returnType) {
		this.returnTypeExpr = GetHiddenItem.getHiddenExpr(returnType);
	}


	public Expr getReturnTypeExpr() {
		return returnTypeExpr;
	}

	public Type getReturnType(Env env) {
		if ( returnType == null )
			calcInterfaceTypes(env);
		return returnType;
	}



	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}



	/**
	 * Does this method signature demands that the method should be immutable?
	   @return
	 */
	public boolean demandsImmutability() { return false; }


	abstract public String getJavaName();
	/**
	 * For each method there should be created an inner prototype that represents the method. After all, each
	 * method is an object too. This inner prototype should declare a single method called "eval", "eval:", or
	 * "eval: eval: ..." (if the method has multiple keywords such as "fun at: Int put: Int").
	 *
	 * This method returns the signature of this 'eval' method.
	   @param s
	 */

	abstract public void genCyanEvalMethodSignature(StringBuffer s);

	/**
	 * generates only the return value type
	 */
	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( returnTypeExpr != null ) {
			if ( cyanEnv.getGenInterfacesForCompiledCode() ) {
				String returnTypeName = returnType.getName();
				pw.print(" -> ");
				if ( cyanEnv.isInPackageCyanLang(returnTypeName) ) {
					pw.print(returnTypeName);
				}
				else {
					pw.print(returnType.getFullName());
				}
				pw.print(" ");
			}
			else {
				pw.print(" -> ");
				returnTypeExpr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
				pw.print(" ");
			}
		}
		else
			pw.print(" ");

	}

	final public void genCyanAnnotations(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		if ( nonAttachedAnnotationList != null ) {
			for ( AnnotationAt annotation : this.nonAttachedAnnotationList ) {
				annotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			}
		}
		if ( attachedAnnotationList != null ) {
			for ( AnnotationAt annotation : this.attachedAnnotationList ) {
				annotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			}
		}

	}


	@SuppressWarnings("unused")
	public void genJavaAsConstructor(PWInterface pw, Env env, String javaNameDeclaringObject) { }

	/**
	 * generates only the return value type
	 */
	@SuppressWarnings("unused")
	public void genJava(PWInterface pw, Env env, boolean isMultiMethod) {
		if ( returnTypeExpr != null ) {
			if ( returnType == Type.Nil ) {
				pw.print("void");
			}
			else {
				pw.print(returnType.getJavaName());
			}
			//pw.print(NameServer.getJavaNameGenericPrototype(returnTypeExpr.ifPrototypeReturnsItsName()));
		}
		else
			/*###
			pw.print(MetaHelper.getJavaName("Nil"));
			*/
			pw.print("void");

		pw.print(" ");
	}


	public void genJava(PWInterface pw, Env env) {
		genJava(pw, env, false);
	}

	abstract public String getSingleParameterType();

	//abstract public String getName();

	public void check(Env env) {
	}


	@Override
	abstract public Symbol getFirstSymbol();

	public void calcInterfaceTypes(Env env) {
		if ( nonAttachedAnnotationList != null ) {
			for ( AnnotationAt annotation : nonAttachedAnnotationList )
				annotation.calcInternalTypes(env);
		}
		if ( attachedAnnotationList != null ) {
			for ( AnnotationAt annotation : attachedAnnotationList )
				annotation.calcInternalTypes(env);
		}

		if ( returnTypeExpr != null ) {
			boolean ok = true;
			try {
				returnTypeExpr.calcInternalTypes(env);
			}
			catch ( error.CompileErrorException e ) {
				returnType = Type.Dyn;
				ok = false;
			}
			if ( ok && returnTypeExpr.getType() != null ) {
				returnType = returnTypeExpr.ifRepresentsTypeReturnsType(env);
			}
		}
		else {
			returnType = Type.Nil;
		}
	}


	/**
	 * just push the parameter in the list of declared variables
	   @param env
	 */
	public void calcInternalTypes(Env env) {
		env.checkSlot(this);
	}



	/**
	 * does this method is declared with [] before at: or at:put: ?
	 * @return
	 */
	public boolean isIndexingMethod() {
		return false;
	}


	/**
	 * the Java name of the method with this signature. For example, if the
	 * method is declared as
	 *      public fun run: [ ]
	 * the Java name is "run". If it is declared as
	 * 	    public fun at: (:index int) put: (:value  String) [ ... ]
	 * the Java name is
	 *         at_p_int_s_put_p_CyString
	 * If it is declared as
	 * 	    public fun person_name -> String
	 * its Java name is
	 *     person__name
	 * In the Java name, all _ are duplicated. That differentiated underscores
	 * put by the Compiler, as in the at_p_int_s_put_p_CyString example, and
	 * underscores that were in the original name.
	 *
	 * 	 * In the general case:
	 *         keywordName + ("_p_" + typeName)* +
	 *         ("_s_" + keywordName + ("_p_" + typeName)* )+
	 *
	 */
	//abstract public String getJavaName();

	/**
	 * return the method signature as a string. But with the parameter names
	 * @return
	 */
	public String getMethodSignatureWithParametersAsString() {
		PWCharArray pwChar = new PWCharArray();
        genCyan(pwChar,  false, NameServer.cyanEnv, true);
		return pwChar.getGeneratedString().toString();
	}




	public MethodDec getMethod() {
		return method;
	}

	public void setMethod(MethodDec method) {
		this.method = method;
	}


	/**
	 * return the method name. See specific help for each of the different method signatures: unary, operator, grammar, regular
	   @return
	 */
	abstract public String getNameWithoutParamNumber();
	/**
	 * For unary, grammar, and operator methods, it returns the same as {@link #getNameWithoutParamNumber()}. For regular methods, it
	 * returns the names of all keywords plus its number of parameters concatenated.
	 * That is, the return for method<br>
	 * <code>with: Int n, Char ch plus: Float f</code><br>
	 * would be <code>with:2 plus:1</code>
	 */
	public String getName() {
		return getNameWithoutParamNumber();
	}

	/**
	 * return the method signature with the parameter types as they are declared,
	 * not considering the import statements. Then, for a method<br>
	 * <code>
	 *     func at: Person p with: Table t -> String<br>
	 * </code>
	 * this method should return <code>at: Person p with: Table t -> String</code>
	 * The full name of the parameters is not given. This method can be
	 * called just after parsing
	   @return
	 */
	abstract public MethodComplexName getNameParametersTypes();

	/**
	 * return the name of the Cyan inner prototype that will represent this method.
	 * For each method the compiler creates an inner prototype, declared inside this prototype.
	 * The name of this prototype is returned by this method.
	 */
	abstract public String getPrototypeNameForMethod();
	/**
	 * methods are objects in Cyan that inherit from UFunction super-prototype. This method
	 * return the super-prototype as String. For example, method
	 *      fun with: Int i, Char ch put: String s -> Person
	 *  is an object that inherits from UFunction<Int, Char><String, Person>
	 */
	abstract public String getSuperprototypeNameForMethod();

	/**
	 * returns the full name of this method signature. That includes the
	 * full names of the parameter types as <br>
	 *        "at: cyan.lang.Int put: cyan.lang.String" <br>
	 *        "add: people.Person"<br>
	 * It does not include the return type
	   @return
	 */
	abstract public String getFullName(Env env);
	abstract public String getFullName();

	public String getFullNameWithReturnType(Env env) {
		return (this.getFullName(env) + " -> " +
	      this.getReturnType(env).getFullName()).replaceAll("cyan.lang.", "");
	}

	public String getFullNameWithReturnType() {
		Expr rt = this.getReturnTypeExpr();
		String strRet = "";
		if ( rt != null ) {
			Type t = rt.getType();
			if ( t == null ) { return null; }
			strRet =  " -> " + t.getFullName();
		}
		return (this.getFullName() + strRet).replaceAll("cyan.lang.", "");
	}


	/**
	 * return the list of parameters of this signature. It includes all parameters of all keywords.
	 * Unary methods return null. Grammar methods return their single parameter
	 */
	abstract public List<ParameterDec> getParameterList();

	public List<AnnotationAt> getAttachedAnnotationList() {
		return attachedAnnotationList;
	}

	public void setAnnotationNonAttachedAttached(List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList) {
		this.nonAttachedAnnotationList = nonAttachedAnnotationList;
		this.attachedAnnotationList = attachedAnnotationList;
	}

	public String asString(CyanEnv cyanEnv) {
		PWCharArray pwChar = new PWCharArray();
		genCyan(pwChar, false, cyanEnv, true);
		return pwChar.getGeneratedString().toString();
	}

	@Override
	public String asString() {
		return asString(NameServer.cyanEnv);

	}


	public InterfaceDec getDeclaringInterface() {
		return declaringInterface;
	}

	public void setDeclaringInterface(InterfaceDec declaringInterface) {
		this.declaringInterface = declaringInterface;
	}


	public boolean getHasCalculatedInterfaceTypes() {
		return hasCalculatedInterfaceTypes;
	}

	public void setHasCalculatedInterfaceTypes(boolean hasCalculatedTypes) {
		this.hasCalculatedInterfaceTypes = hasCalculatedTypes;
	}


	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		return featureList;
	}

	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature) {
		if ( featureList == null )
			featureList = new ArrayList<>();
		featureList.add(feature);
	}


	public void addFeatureList( List<Tuple2<String, WrExprAnyLiteral>> featureList1) {
		for ( Tuple2<String, WrExprAnyLiteral> t : featureList1 ) {
			this.addFeature(t);
		}
	}


	public List<WrExprAnyLiteral> searchFeature(String name) {
		if ( featureList == null ) return null;
		List<WrExprAnyLiteral> eList = null;
		for ( Tuple2<String, WrExprAnyLiteral> t : featureList ) {
			if ( t.f1.equals(name) ) {
				if ( eList == null ) {
					eList = new ArrayList<>();
				}
				eList.add(t.f2);
			}
		}
		return eList;
	}


	public AttachedDeclarationKind getKind() {
		return AttachedDeclarationKind.METHOD_SIGNATURE_DEC;
	}



	public void addDocumentText(String doc, String docKind) {
		if ( documentTextList == null ) {
			documentTextList = new ArrayList<>();
		}
		documentTextList.add( new Tuple2<String, String>(doc, docKind));
	}

	public void addDocumentExample(String example, String exampleKind) {
		if ( exampleTextList == null ) {
			exampleTextList = new ArrayList<>();
		}
		exampleTextList.add( new Tuple2<String, String>(example, exampleKind));

	}

	public List<Tuple2<String, String>> getDocumentTextList() {
		return documentTextList;
	}

	public List<Tuple2<String, String>> getDocumentExampleList() {
		return exampleTextList;
	}




	public boolean getFinalKeyword() {
		return finalKeyword;
	}

	public void setFinalKeyword(boolean finalKeyword) {
		this.finalKeyword = finalKeyword;
	}


	public boolean getAbstractKeyword() {
		return abstractKeyword;
	}

	public void setAbstractKeyword(boolean abstractKeyword) {
		this.abstractKeyword = abstractKeyword;
	}


	@Override
	abstract public String getNameWithDeclaredTypes();

	abstract public String getNameWithParamAndTypes();


	public boolean createdByMetaobjects() {
		return this.method.createdByMetaobjects();
	}


	/**
	 * list of pairs (doc, docKind) of documentation for this declaration. See interface
	 * {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>> documentTextList;
	/**
	 * list of pairs (example, exampleKind) of examples for this declaration. See interface
	 * {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>> exampleTextList;



	/**
	 * return the function name of a function that has the same parameters as this method signature. For example,
	 * if this  method signature is<br>
	 * <code>
	 *     func at: Int a put: Char ch, Float b  with: String s1, String s2 -> Array<Char>
	 * </code><br>
	 * then the function name returned is <br>
	 * <code>
	 *     Function{@literal <}Int>{@literal <}Char, Float>{@literal <}String, String, Array{@literal <}Char>>
	 * </code><br>
	   @return
	 */
	abstract public String getFunctionName();


	public abstract String getFunctionNameWithSelf(String fullName2);


	public abstract String getSignatureWithoutReturnType();


	/**
	 * the list of features associated to this signature
	 */
	private List<Tuple2<String, WrExprAnyLiteral>> featureList;


	protected String javaName;


	/** the return type of the method corresponding to this method signature */
	protected Type returnType;

	protected Expr returnTypeExpr;


	/**  method to which this signature belongs to
	 *
	 */
	private MethodDec method;

	/**
	 * list of metaobject annotations attached to this signature. It is only used with method signatures of interfaces
	 */
	private List<AnnotationAt> attachedAnnotationList;

	private List<AnnotationAt> nonAttachedAnnotationList;

	/**
	 * the interface in which this method signature is. null if this method signature is in a prototype
	 */
	private InterfaceDec declaringInterface;

	/**
	 * true if the type of this method signature have already been calculated
	 */
	protected boolean hasCalculatedInterfaceTypes;

	/**
	 * the full name of this signature
	 */
	protected String fullName;


	/**
	 * true if the signature has a 'final' keyword
	 */
	protected boolean finalKeyword;


	/**
	 * true if the signature has an 'abstract' keyword
	 */
	protected boolean abstractKeyword;


}
