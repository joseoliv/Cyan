package ast;

import java.util.ArrayList;
import java.util.List;
import error.CompileErrorException;
import error.ErrorKind;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.AttachedDeclarationKind;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAssignment_cge;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrExprAnyLiteral;
import meta.WrStatementLocalVariableDec;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 *    Represents a local variable, which include parameters (a subclass)
 * @author José
 *
 */
public class StatementLocalVariableDec extends Statement implements VariableDecInterface, Declaration {

	public StatementLocalVariableDec(SymbolIdent variableSymbol, Expr typeInDec,
			Expr expr, MethodDec declaringMethod, int level,
			boolean isReadonly, MethodDec method) {
		super(method);
		this.variableSymbol = variableSymbol;
		this.typeInDec = typeInDec;
		this.expr = expr;
		this.declaringFunction = null;
		this.declaringMethod = declaringMethod;
		this.level = level;
		innerObjectNumberList = new ArrayList<>();
		javaName = MetaHelper.getJavaName(this.getName());
		this.isReadonly = isReadonly;
		typeWasChanged = false;
	}

	@Override
	public WrStatementLocalVariableDec getI() {
		if ( iStatementLocalVariableDec == null ) {
			iStatementLocalVariableDec = new WrStatementLocalVariableDec(this);
		}
		return iStatementLocalVariableDec;
	}


	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		if ( expr != null ) {
			expr.accept(visitor);
		}
	}


	@Override
	public Expr getTypeInDec() {
		return typeInDec;
	}

	public void setVariableSymbol(SymbolIdent variableSymbol) {
		this.variableSymbol = variableSymbol;
	}

	@Override
	public SymbolIdent getVariableSymbol() {
		return variableSymbol;
	}

	public Expr getExpr() {
		return expr;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( typeInDec != null )
		    typeInDec.genCyan(pw, false, cyanEnv, genFunctions);
		if ( getName() != null )
			pw.print( (typeInDec != null ? " " : "") + getName());
		if ( expr != null ) {
			pw.print(" = ");
			expr.genCyan(pw, false, cyanEnv, genFunctions);
		}
	}

	@Override
	public void genJava(PWInterface pw, Env env) {


		final String variableName = getName();
		env.pushVariableDec(this);


		boolean typeIsJavaClass = false;

		if ( type instanceof TypeJavaRef ) {
			typeIsJavaClass = true;
		}

		String javaNameVar = MetaHelper.getJavaName(variableName);
		String javaTypeName;

		if ( typeInDec != null ) {
			javaTypeName =  this.typeInDec.getJavaName();
		}
		else {
			javaTypeName = type.getJavaName();
		}



		String tmpExpr = "";
		if ( expr != null ) {
			tmpExpr = expr.genJavaExpr(pw, env);
			pw.println();
		}


		if ( refType )
			pw.printIdent("Ref<" + javaTypeName + ">");
		else
			pw.printIdent(javaTypeName);
		pw.print(" " + javaNameVar);
		if ( refType ) {
			pw.print(" = new Ref<" + javaTypeName + ">()");
		}

		if ( expr == null )
			pw.println(";");
		else {
			pw.println(";");
			if ( refType )
				javaNameVar = javaNameVar + ".elem";
       		final Type rightType = expr.getType();

    		/*
    		 * A metaobject attached to the type of the formal parameter may demand that the real argument be
    		 * changed. The new argument is the return of method  changeRightHandSideTo
    		 */


    		final Tuple2<IActionAssignment_cge, ObjectDec> cyanMetaobjectPrototype = MetaInfoServer.getChangeAssignmentCyanMetaobject(env, type);
    		IActionAssignment_cge changeCyanMetaobject = null;
            ObjectDec prototypeFoundMetaobject = null;
            if ( cyanMetaobjectPrototype != null ) {
            	changeCyanMetaobject = cyanMetaobjectPrototype.f1;
            	prototypeFoundMetaobject = cyanMetaobjectPrototype.f2;

    				if ( changeCyanMetaobject != null ) {

    					try {
        					tmpExpr = changeCyanMetaobject.cge_changeRightHandSideTo( prototypeFoundMetaobject,
        							tmpExpr, expr.getType(env));
    					}
    					catch ( final error.CompileErrorException e ) {
    					}
    					catch ( final NoClassDefFoundError e ) {
    						final Annotation annotation = meta.GetHiddenItem.getHiddenCyanAnnotation(((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation());
    						env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
    					}
    					catch ( final RuntimeException e ) {
    						final Annotation annotation = meta.GetHiddenItem.getHiddenCyanAnnotation(((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation());
    						env.thrownException(annotation, annotation.getFirstSymbol(), e);
    					}
    					finally {
    	   					env.errorInMetaobject( (meta.CyanMetaobject ) changeCyanMetaobject, this.getFirstSymbol());
    					}



       				}


            }


    		if ( rightType == Type.Dyn && type != Type.Dyn ) {
    			final String javaNameType = type.getJavaName();
    			pw.printlnIdent("if ( " + tmpExpr + " instanceof " + javaNameType + " ) {");
    			pw.add();
    			pw.printlnIdent(javaNameVar + " = (" + javaNameType + " ) " + tmpExpr + ";");
    			pw.sub();
    			pw.printlnIdent("}");
    			pw.printlnIdent("else {");
    			pw.add();

				pw.printlnIdent("throw new ExceptionContainer__("
						+ env.javaCodeForCastException(expr, type) + " );");

    			pw.sub();
    			pw.printlnIdent("}");
    		}
    		else {

    			/*
    			if ( type instanceof TypeJavaRef && rightType.getInsideType() instanceof Prototype ) {
    				// java = cyan
    				String puName = rightType.getName();
    				if ( NameServer.isBasicType(puName) ) {
    					/*
    					 * cast Cyan basic type valueInInterpreter to Java basic type valueInInterpreter
    					 * /
    					tmpExpr = tmpExpr + "." + NameServer.getFieldBasicType(puName);
    				}
    			}
    			else if ( rightType instanceof TypeJavaRef && type.getInsideType() instanceof Prototype ) {
    				// cyan = java
    				String javaClass = rightType.getName();
    				tmpExpr = "new " + NameServer.cyanNameFromJavaBasicType(javaClass) + "(" + tmpExpr + ")";
    			}
    			*/
    			tmpExpr = Type.genJavaExpr_CastJavaCyan(env, tmpExpr, rightType, type, getFirstSymbol());

    			pw.printlnIdent(javaNameVar + " = " + tmpExpr + ";");


    		}
				/*
				if ( expr instanceof ExprIdentStar ) {
					VariableDecInterface rightSideVar = env.searchVariable( ((ExprIdentStar ) expr).getName());
					if ( rightSideVar != null && rightSideVar.getRefType() ) {
						pw.printlnIdent(javaNameVar + " = " + rightSideVar.getJavaName() + ";" );
					}
					else
						pw.printlnIdent(javaNameVar + ".elem = " + tmpExpr + ";");
				}
				else if ( expr instanceof ExprSelfPeriodIdent ) {
					FieldDec rightSideVar = env.searchField(
							((ExprSelfPeriodIdent ) expr).getIdentSymbol().getSymbolString());
					if ( rightSideVar != null && rightSideVar.getRefType() ) {
						pw.printlnIdent(javaNameVar + " = " + rightSideVar.getJavaName() + ";" );
					}
					else
						pw.printlnIdent(javaNameVar + ".elem = " + tmpExpr + ";");
				}
				else
					pw.printlnIdent(javaNameVar + ".elem = " + tmpExpr + ";");
				*/
		}
	}


	@Override
	public Symbol getFirstSymbol() {
		return variableSymbol;
	}



	@Override
	public String getName() {
		return variableSymbol.getSymbolString();
	}




	@Override
	public String getJavaName() {
		return javaName;
	}

	@Override
	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}


	@Override
	public boolean isReadonly() {
		return isReadonly;
	}


	private String javaName;



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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public void calcInternalTypes(Env env) {


		final String protoName = env.getCurrentPrototype().getName();
		/*
		 * if the method is bindToFunction  in a context function, all message sends
		 * to self are considered as message sends to newSelf__ whose type is
		 * the type of the first parameter of bindToFunction
		 */
		if ( NameServer.isNameInnerProtoForContextFunction(protoName) &&
				 env.getCurrentMethod().getName().equals(NameServer.bindToFunctionWithParamNumber)	) {
			/* t is the type of newSelf__, the first parameter of
                        func bindToFunction: IColor newSelf__ -> UFunction<String> {
                            return { (:  -> String :)
                                ^ colorTable[newSelf__  color]
                            }
                        }
			 */
			final Type t = env.getCurrentMethod().getMethodSignature().getParameterList().get(0).getType();
			if ( t.searchMethodPublicPackageSuperPublicPackage(this.variableSymbol.getSymbolString(), env) != null ) {
				env.error(true, variableSymbol,
						"Local variable '" + variableSymbol.getSymbolString() +
								"' has the same name as an unary method of the type of parameter 'self' of the enclosing context object", variableSymbol.getSymbolString(), ErrorKind.local_variable_has_same_name_method_context_object);
			}

		}

		boolean exceptionThrown = false;
		// bad way of doing that
		final int numErrorsCurrentCompilationUnit = env.getCurrentCompilationUnit().getErrorList().size();
		if ( expr != null ) {
			try {
				try {
					env.pushCheckUsePossiblyNonInitializedPrototype(true);
					expr.calcInternalTypes(env);
				}
				finally {
					env.popCheckUsePossiblyNonInitializedPrototype();
				}

				// env.pushVariableAndLevel(variableSymbol.symbolString);
			}
			catch ( final CompileErrorException e ) {
				exceptionThrown = true;
			}
		}
		env.pushVariableAndLevel(this, variableSymbol.symbolString);
		if ( typeInDec != null ) {
			try {
 				try {
 					env.pushCheckUsePossiblyNonInitializedPrototype(false);
 					typeInDec.calcInternalTypes(env);
 				}
 				finally {
 					env.popCheckUsePossiblyNonInitializedPrototype();
 				}

				type = typeInDec.ifRepresentsTypeReturnsType(env);
			}
			catch ( final CompileErrorException e ) {
				exceptionThrown = true;
			}

		}
		else {
			if ( expr == null || exceptionThrown ) {
				/*
				 * no expr and no type: declare as type Dyn
				 */
				type = Type.Dyn;
			}
			else if ( ! exceptionThrown && numErrorsCurrentCompilationUnit == env.getCurrentCompilationUnit().getErrorList().size() ) {
				type = expr.getType(env);
			}
		}
		if ( exceptionThrown || numErrorsCurrentCompilationUnit != env.getCurrentCompilationUnit().getErrorList().size()  ) {
	        env.addLocalVariableInfoToCurrentLexicalLevel(this, expr);
			env.pushVariableDec(this);
			throw new CompileErrorException();
		}
        if ( expr != null && env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_6 ) {
            MetaInfoServer.checkAssignmentPluggableTypeSystem(env, type, this, LeftHandSideKind.StatementLocalVariableDec_LHS,
            		expr.getType(), expr);
        }
        env.addLocalVariableInfoToCurrentLexicalLevel(this, expr);

		if ( exceptionThrown ) {
			type = Type.Dyn;
		}

		final String nameVar = this.getName();
		final VariableDecInterface otherVar = env.searchLocalVariableParameter(nameVar);
		if ( otherVar != null ) {
			env.error(this.getFirstSymbol(), "Variable '" + nameVar + "' is being redeclared. The other declaration is in line "
					+ otherVar.getVariableSymbol().getLineNumber());
		}
		/*
		for ( VariableDecInterface aVar : env.getVariableDecStack() ) {
			if ( aVar.getName().equals(nameVar) )
		}
		Stack<Tuple2<String, Integer>> stackVar = env.getStackVariableLevel();
		for ( Tuple2<String, Integer> t : stackVar ) {
			if ( t.f1.equals(nameVar) && t.f2 == env.getLexicalLevel() ) {
				VariableDecInterface varDec = env.searchLocalVariableParameter(this.getName());
				env.error(this.getFirstSymbol(), "Variable '" + nameVar + "' is being redeclared. The other declaration is in line "
						+ varDec.getVariableSymbol().getLineNumber());

			}
		}
		*/

		env.pushVariableDec(this);

		if ( expr != null ) {
			if ( ! type.isSupertypeOf(expr.getType(env), env) ) {
				String typeFullName = type.getFullName();
				type = Type.Dyn;
				env.error(true, variableSymbol,
			      "Type error: '" + expr.asString() + "' has type '" + expr.getType().getFullName() +
			       "' that is not subtype of '" + typeFullName + "'",
			       variableSymbol.getSymbolString(), ErrorKind.type_error_type_of_right_hand_side_of_assignment_is_not_a_subtype_of_the_type_of_left_hand_side
			      );
			}
		}
		/**
		 * see comments on  {@link #innerObjectNumberList}
		 */
		final CompilationStep cs = env.getProject().getCompilerManager().getCompilationStep();
		if ( (cs == CompilationStep.step_8 || cs == CompilationStep.step_9) &&
		 	  env.getCurrentObjectDec().getOuterObject() == null ) {
				// outer must be null because only outer objects cause the creation of inner objects

			/**
			 * change the context parameter types of an inner object that uses this variable. That is,
			 * the object is something like
                object Fun_0__(Program self__, Any &s12)  extends Function<Nil>
                    ...
    				func new: Program self__, Any &s12 -> Fun_0__ { ... }
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
				final String name = this.getName();
				for ( final Integer n : this.innerObjectNumberList ) {
					final ObjectDec toChange = innerObjectDecList.get(n);
					for ( final ContextParameter cp : toChange.getContextParameterArray() ) {
						if ( cp.getName().equals(name)  ) {
							cp.setTypeInDec(this.typeInDec);
							cp.setType(type);
							break;
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
		super.calcInternalTypes(env);

	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}


	@Override
	public void setTypeInDec(Expr typeInDec) {
		this.typeInDec = typeInDec;
	}

	@Override
	public boolean getRefType() {
		return refType;
	}

	@Override
	public void setRefType(boolean refType) {
		if ( refType ) {
			if ( this.isReadonly ) {
				this.refType = false;
			}
			else {
				this.refType = true;
			}
		}
		else {
			this.refType = refType;
		}
	}



	public List<Integer> getInnerObjectNumberList() {
		return innerObjectNumberList;
	}



	public void addInnerObjectNumberList(int innerObjectNumber) {

		for ( final Integer n : this.innerObjectNumberList ) {
			if ( n == innerObjectNumber )
				return;
		}
		innerObjectNumberList.add(innerObjectNumber);
		  /*
		   * if the variable is used inside any function, it should be a ref type. Unless it is a read only variable
		   */

		this.setRefType( ! this.isReadonly );
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
		if ( expr != null ) {
			valueInInterpreter = expr.eval(ee);

			if ( typeInDec != null ) {
				final String fullTypeName = typeInDec.asString();
				Class<?> aClass;
				aClass = ee.searchPrototypeAsType(fullTypeName);
				if ( aClass == null ) {
					aClass = searchCyanGenericProtoWithDyn(ee, fullTypeName);
					if ( aClass == null ) {
						ee.error(this.getFirstSymbol(), "Type '" + fullTypeName + "' was not found");
					}
				}
				// ee.searchPrototypeAsType(fullTypeName);
				/*
				Type t = typeInDec.getType();
				if ( t instanceof TypeJavaRef ) {
					aClass = ((TypeJavaRef ) t).getaClass(ee.env, this.getFirstSymbol());
				}
				else {
					aClass = t.getClass();
				}
				*/
				valueInInterpreter = Statement.castCyanJava(ee, aClass, valueInInterpreter);
			}

		}
		else {
			valueInInterpreter = null;
		}
		ee.addLocalVar(this);
		return valueInInterpreter;
	}

	/** fullTypeName should be a generic prototype name.
	 * This method replaces each real parameter by 'Dyn' and searches
	 * for a Java class corresponding to this prototype. The Java class
	 * object is returned.
	   @param ee
	   @param fullTypeName
	   @param aClass
	   @return
	 */
	public static Class<?> searchCyanGenericProtoWithDyn(EvalEnv ee,
			final String fullTypeName) {

		Class<?> aClass = null;
		int firstLessThan = fullTypeName.indexOf('<');
		if ( firstLessThan > 0 ) {
			int lastGreaterThan = fullTypeName.lastIndexOf('>');
			if ( lastGreaterThan > 0 ) {
				//String realParam = fullTypeName.substring(firstLessThan+1, lastGreaterThan);
				int numGenPar = meta.MetaHelper.numGenericParamInType(fullTypeName);
				String newFullTypeName = fullTypeName.substring(0, firstLessThan) + "<";
				for (int kk = 0; kk < numGenPar; ++kk) {
					newFullTypeName += "Dyn";
					if ( kk < numGenPar - 1 ) {
						newFullTypeName += ", ";
					}
				}
				newFullTypeName += ">";
				aClass = ee.searchPrototypeAsType(newFullTypeName);
				if ( aClass == null ) {
					aClass = ee.searchJavaClass_MetaJavaLang(newFullTypeName);
				}
			}
		}
		return aClass;
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



	private WrStatementLocalVariableDec iStatementLocalVariableDec = null;

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


	private SymbolIdent variableSymbol;
	/**
	 * object that is the type of the variable. It must be a basic type,
	 * an object, a generic object instantiation, or an array.
	 */
	private Expr typeInDec;
	/**
	 * type of the variable. An object of a subclass of Type
	 */
	private Type type;
	/**
	 * expression to which the variable is initialized
	 */
	private final Expr expr;

	/**
	 * the function that is declaring this variable. null if the declaration is outside any function
	 */
	private ExprFunction declaringFunction;
	/**
	 * the method that is declaring this variable. It is always non-null even in cases like
	 * the example below in which n is declared inside a function.
	 *     func test {
	 *         var b = { var Int n; n = 0; ^n }
	 *     }
	 */
	private final MethodDec declaringMethod;
	/**
	 * level of the variable as defined by the Cyan manual. In the example below, ai is of level i.
	 *
	 * public func test: (Int n) {
	 *    // scope level 1
	 *    var Int a1 = n;
	 *    (n < 0) ifFalse: {
	 *       // scope level 2
	 *       var Int a2 = -a1;
	 *       (n > 0) ifTrue: {
	 *           // scope level 3
	 *           var a3 = a2 + 1;
	 *           Out println: "> 0", a3
	 *       }
	 *       ifFalse: { Out println: "= 0" }
	 *    }
	 * } // a1 and n are removed from the stack here
	 */
	private int level;

	/**
	 * true if this variable was used as a reference type. That) is, refType is true for a
	 * variable p if p was used where a reference was expected. For example, suppose p
	 * was used in
	 *     Sum(p)
	 * in which Sum was declared as
	 *     object Sum(Int &s) ... end
	 * then refType should be true.
	 */
	private boolean refType;

	/**
	 * list of numbers of inner objects. Each inner object corresponds to a function that
	 * accesses this parameter. These inner objects have a parameter with name equal to this
	 * variable as a context parameter, as 'v1' in <br>
  	  <code>
      object F1(B self__, Any p1, Any v1) <br>
            extends Function{@literal <}Int, Int>  <br>
          func eval: Int a -> Int {  <br>
              ^a + p1 + v1 + iv1  <br>
          }<br>
      end<br>
      <code>
	 * The compiler should change the type of 'v1', which is initially 'Any' to the correct
	 * type, which is 'this.typeInDec'
	 */
	private final List<Integer> innerObjectNumberList;

	/**
	 * true if this variable is read only
	 */
	private final boolean isReadonly;

}
