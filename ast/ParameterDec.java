package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.AttachedDeclarationKind;
import meta.CompilationStep;
import meta.GetHiddenItem;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.VariableKind;
import meta.WrExpr;
import meta.WrExprAnyLiteral;
import meta.WrMethodDec;
import meta.WrParameterDec;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** Represents a parameter of a method
 * @author José
 *
 */

public class ParameterDec implements VariableDecInterface, GenCyan, ASTNode {



	public ParameterDec(Symbol variableSymbol, Expr typeInDec, MethodDec declaringMethod) {
		this.variableSymbol = variableSymbol;
		this.typeInDec = typeInDec;
		this.declaringMethod = declaringMethod;
		this.declaringFunction = null;
		innerObjectNumberList = new ArrayList<>();
		name = this.getName();
		if ( name != null ) {
			javaName = MetaHelper.getJavaName(name);
		}
		else
			javaName = null;
		type = null;
		typeWasChanged = false;
	}

	public ParameterDec(Symbol parameterSymbol, WrExpr typeInDec2,
			WrMethodDec currentMethod) {
		this(parameterSymbol, GetHiddenItem.getHiddenExpr(typeInDec2),
				GetHiddenItem.getHiddenMethodDec(currentMethod));
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}


	@Override
	public WrParameterDec getI() {
		if ( iParameterDec == null ) {
			iParameterDec = new WrParameterDec(this);
		}
		return iParameterDec;
	}

	private WrParameterDec iParameterDec = null;


	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		    // variableSymbol is null in a method signature of an interface

		if ( cyanEnv.getGenInterfacesForCompiledCode() ) {
			if ( type != null ) {
				String typeName = type.getName();
				if ( cyanEnv.isInPackageCyanLang(typeName) ) {
					pw.print(typeName);
				}
				else {
					pw.print(type.getFullName());
				}
			    if ( variableSymbol != null )
			    	pw.print( " " );
			}
		}
		else {
			if ( typeInDec != null ) {
			    typeInDec.genCyan(pw, false, cyanEnv, genFunctions);
			    if ( variableSymbol != null )
			    	pw.print( " " );
			}

		}


		if ( variableSymbol != null )
			pw.print(variableSymbol.getSymbolString());
	}

	public void genJava(PWInterface pw, Env env) {

		String javaNameVar;
		if ( variableSymbol != null )
			javaNameVar = MetaHelper.getJavaName(getName());
		else
			javaNameVar = NameServer.nextJavaLocalVariableName();

		if ( this.getRefType() ) {
			pw.print("Ref<" + type.getJavaName() + "> " + javaNameVar);
		}
		else {
			pw.print(type.getJavaName() + " " + javaNameVar);
		}

	}


	public void genJavaForMultiMethod(PWInterface pw) {

		String javaNameVar;
		if ( variableSymbol != null )
			javaNameVar = MetaHelper.getJavaName(getName());
		else
			javaNameVar = NameServer.nextJavaLocalVariableName();

	    pw.print("Object " + javaNameVar);
	}


	@Override
	public Symbol getFirstSymbol() {
		if ( typeInDec != null )
			return typeInDec.getFirstSymbol();
		else
		    return variableSymbol;
	}


	@Override
	public String getName() {
		if ( variableSymbol == null ) {
			paramName = NameServer.nextJavaLocalVariableName();
			return paramName;
		}
		else
		    return variableSymbol.getSymbolString();
	}

	public void check(Env env) {
		env.error(null, "Internal error: method ParameterDec::check should not be used", true, true);
		if ( variableSymbol != null && variableSymbol.token != Token.SELF ) {
			final String varName = variableSymbol.getSymbolString();
			if ( env.searchVariable(varName) != null )
				env.error(variableSymbol, "Parameter " + varName + " is being redeclared", true, true);
		}
	}


	/**
	 * sets the function that is declaring this local variable.
	 * @param declaringFunction
	 */
	public void setDeclaringFunction(ExprFunction declaringFunction) {
		this.declaringFunction = declaringFunction;
	}


	public ExprFunction getDeclaringFunction() {
		return declaringFunction;
	}

	public MethodDec getDeclaringMethod() {
		return declaringMethod;
	}

	public void calcInternalTypes(Env env) {

		/**
		 * if variableSymbol is null then this is a "parameter" of a signature declared in an
		 * interface:
		 *     interface Code
		 *         fun setCode: String
		 *         fun getCode -> String
		 *     end
		 *
		 * That is, there is not really a parameter and so it should not be pushed into
		 * the stack of variables
		 */
		if ( variableSymbol != null && variableSymbol.token != Token.SELF )
			env.pushVariableDec(this);
		if ( typeInDec != null && type == null ) {

			try {
				env.pushCheckUsePossiblyNonInitializedPrototype(false);
				typeInDec.calcInternalTypes(env);
			}
			finally {
				env.popCheckUsePossiblyNonInitializedPrototype();
			}


			type = typeInDec.ifRepresentsTypeReturnsType(env);


			if ( type instanceof TypeWithAnnotations ) {
				((TypeWithAnnotations) type).checkAnnotation(env);
			}
			/*
			if ( typeInDec instanceof ExprGenericPrototypeInstantiation ) {
				if ( ((ExprGenericPrototypeInstantiation) typeInDec).getAnnotationToTypeList() != null ) {
					TypeWithAnnotations twa;
					type = twa = new TypeWithAnnotations(type, ((ExprGenericPrototypeInstantiation) typeInDec).getAnnotationToTypeList());
					twa.checkAnnotation(env);
				}
			} else if ( typeInDec instanceof ExprIdentStar ) {
				if ( ((ExprIdentStar) typeInDec).getAnnotationToTypeList() != null ) {
					TypeWithAnnotations twa;
					type = twa = new TypeWithAnnotations(type, ((ExprIdentStar) typeInDec).getAnnotationToTypeList());
					twa.checkAnnotation(env);
				}
			}
			*/

		}


		if ( type == null )
			type = Type.Dyn;

		/*
		String typeName = typeInDec.ifPrototypeReturnsItsName();
		if ( typeName == null ) {
			env.error(typeInDec.getFirstSymbol(), "Type expected in parameter declaration");
		}
		else {
			List<Prototype> prototypeList = env.searchVisiblePrototype(typeName);
			if ( prototypeList.size() == 1 ) {
				// found exactly one prototype
				type = prototypeList.get(0);
			}
			else if ( prototypeList.size() == 0 )
				env.error(typeInDec.getFirstSymbol(), "Type " + typeName + " was not found");
			else
				env.error(typeInDec.getFirstSymbol(), "Type " + typeName + " can be imported from more than one package");
		}
		*/
		if ( env.getCurrentMethod() != null ) {
			final String methodName = env.getCurrentMethod().getNameWithoutParamNumber();
			if ( ! methodName.equals("new:") &&  ! methodName.equals("init:") ) {
				if ( variableKind != null && variableKind != VariableKind.COPY_VAR )
					env.error( typeInDec == null ? variableSymbol : typeInDec.getFirstSymbol(),
							"Parameters prefixed by '&' or '%' can only be declared in 'new:' or 'init:' methods", true, true);
			}
		}

		/**
		 * see comments on  {@link #innerObjectNumberList}
		 */
		final CompilationStep cs = env.getProject().getCompilerManager().getCompilationStep();
		if ( (cs == CompilationStep.step_8 || cs == CompilationStep.step_9) &&
				env.getCurrentObjectDec() != null &&
		 	  env.getCurrentObjectDec().getOuterObject() == null ) {


			/**
			 * change the context parameter types of an inner object that uses this variable. That is,
			 * the object is something like
                object Fun_0__(Program self__, Any &s12)  extends Function<Nil>
                    ...
    				fun new: Program self__, Any &s12 -> Fun_0__ { ... }
    				...
    			end

    			in which the type of s12 is Any, which is not the real type. The code below
    			assigns to variables like s12 their correct type. Idem for parameters
    			to method 'new:'.

    			 *
			 */

			final ObjectDec currentObject = env.getCurrentObjectDec();
			if ( currentObject != null ) {
				final List<ObjectDec> innerObjectDecList = currentObject.getInnerPrototypeList();
				//String name = this.getName();
				for ( final Integer n : this.innerObjectNumberList ) {
					final ObjectDec toChange = innerObjectDecList.get(n);
					for ( final ContextParameter cp : toChange.getContextParameterArray() ) {
						if ( cp.getName().equals(name)  ) {
							cp.setTypeInDec(this.typeInDec);
							cp.setType(typeInDec.getType());
						}
					}
					/*
					 * change type of 'new:' parameters
					 */
					final List<MethodSignature> methodSignatureList = toChange.searchInitNewMethodBySelectorList("new:");
					for ( final MethodSignature ms: methodSignatureList ) {
						for ( final ParameterDec param : ms.getParameterList() ) {
							if ( param.getName().equals(name) ) {
								param.setTypeInDec(this.typeInDec);
								param.setType(type);
							}
						}
					}
				}
			}



		}
	}

	@Override
	public void setTypeInDec(Expr typeInDec) {
		this.typeInDec = typeInDec;
	}

	@Override
	public Expr getTypeInDec() {
		return typeInDec;
	}

	@Override
	public Symbol getVariableSymbol() {
		return variableSymbol;
	}


	@Override
	public Type getType() {
		if ( type != null )
			return type;
		if ( typeInDec == null )
			return Type.Dyn;
		else
			return typeInDec.getType();
	}

	public Type getType(Env env) {
		if ( type != null )
			return type;
		if ( typeInDec == null )
			return Type.Dyn;
		else
			return typeInDec.getType(env);
	}



	public VariableKind getVariableKind() {
		return variableKind;
	}


	public void setVariableKind(VariableKind variableKind) {
		this.variableKind = variableKind;
		this.setRefType(this.variableKind == VariableKind.LOCAL_VARIABLE_REF);
	}

	@Override
	public boolean getRefType() {
		return refType;
	}


	@Override
	public void setRefType(boolean refType) {
		this.refType = refType;
		if ( refType )
			variableKind = VariableKind.LOCAL_VARIABLE_REF;
		else
			variableKind = VariableKind.COPY_VAR;
	}



	@Override
	public String getJavaName() {
		return javaName;
	}

	@Override
	public void setJavaName(String javaName) {
		this.javaName = javaName;

	}

	private String javaName;


	public List<Integer> getInnerObjectNumberList() {
		return innerObjectNumberList;
	}



	public void addInnerObjectNumberList(int innerObjectNumber) {
		innerObjectNumberList.add(innerObjectNumber);
	}


	public String asString(CyanEnv cyanEnv) {
		final PWCharArray pwChar = new PWCharArray();
		genCyan(pwChar, true, cyanEnv, true);
		return pwChar.getGeneratedString().toString();
	}

	@Override
	public String asString() {
		return asString(NameServer.cyanEnv);
	}


	@Override
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public boolean isReadonly() {
		return true;
	}


	/**
	 * see {@link VariableDecInterface#setTypeWasChanged(boolean)}
	 */

	@Override
	public void setTypeWasChanged(boolean typeWasChanged) {
		this.typeWasChanged = typeWasChanged;
	}
	/**
	 * see {@link VariableDecInterface#setTypeWasChanged(boolean)}
	 */
	@Override
	public boolean getTypeWasChanged() {
		return typeWasChanged;
	}


	@Override
	public Object eval(EvalEnv ee) {
		return valueInInterpreter;
	}

	@Override
	public Object getValueInInterpreter() { return valueInInterpreter; }
	@Override
	public
	void setValueInInterpreter(Object value) { this.valueInInterpreter = value; }




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

	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		return featureList;
	}

	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature) {
		if ( featureList == null )
			featureList = new ArrayList<>();
		featureList.add(feature);
	}

	public List<WrExprAnyLiteral> searchFeature(String name) {
		if ( featureList == null ) return null;

		List<WrExprAnyLiteral> eList = null;
		for ( final Tuple2<String, WrExprAnyLiteral> t : featureList ) {
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
		return AttachedDeclarationKind.LOCAL_VAR_DEC;
	}



	private List<Tuple2<String, WrExprAnyLiteral>> featureList;
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
	 * when this class is used for interpreting Cyan code, this is the valueInInterpreter of the variable
	 */
	private Object valueInInterpreter;


	/**
	 * see {@link VariableDecInterface#setTypeWasChanged(boolean)}
	 */
	private boolean typeWasChanged;


	/**
	 * symbol of the name of the variable. It may be null when the parameter name does not appear
	 * as in interface declaration:
	 *
	 *     interface PrintInt
	 *         fun print: Int
	 *     end
	 */
	private final Symbol variableSymbol;
	/**
	 * object that is the type of the variable. It must be a basic type,
	 * an object, a generic object instantiation, or an array.
	 */
	private Expr typeInDec;


	/**
	 * the function that is declaring this variable. null if the declaration is outside any function
	 */
	private ExprFunction declaringFunction;
	/**
	 * the method that is declaring this variable. It is always non-null even in cases like
	 * the example below in which n is declared inside a function.
	 *     fun test {
	 *         var b = [ var Int n; n = 0; ^n ]
	 *     }
	 */
	private final MethodDec declaringMethod;

	/** the kind of variable. Regular variables are COPY_VAR variables. But when
	 * the compiler creates a regular prototype from the declaration of a context object,
	 * a field may be "a field parameter" or a "reference parameter".
	 * That is, from a context object
	 *     object Test(Int &f1, Char *y, Boolean %z) ... end
	 * the compiler will create a regular object that has fields f1, y, and z:
	 *
	 * object  Test
	 *     public init: (Int &f1, Char *y, Boolean %z) [ self.x = f1; self.y = y; self.z = z; ]
	 *
	 *     private Int &f1; Char *y;  Boolean z;
	 * end
	 *
	 */
	private VariableKind variableKind;

	/**
	 * list of numbers of inner objects. Each inner object corresponds to a function that
	 * accesses this parameter. These inner objects have a parameter with name equal to this
	 * one as a context parameter, as 'p1' in <br>
  	  <code>
      object F1(Any p1, Any v1) <br>
            extends Function{@literal <}Int, Int>  <br>
          fun eval: Int a -> Int {  <br>
              ^a + p1 + v1 + iv1  <br>
          }<br>
      end<br>
      <code>
	 * The compiler should change the type of 'p1', which is initially 'Any' to the correct
	 * type, which is 'this.typeInDec'
	 */
	private final List<Integer> innerObjectNumberList;


	private Type type;

	private final String name;

	private String paramName;


	/**
	 * true if this variable was used as a reference type. That is, refType is true for a
	 * variable p if p was used where a reference was expected. For example, suppose p
	 * was used in
	 *     Sum(p)
	 * in which Sum was declared as
	 *     object Sum(Int &s) ... end
	 * then refType should be true.
	 */
	private boolean refType;



}

