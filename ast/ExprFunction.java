/**
 *
 */
package ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import error.ErrorKind;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.CompilationStep;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple6;
import meta.WrExprFunction;
import saci.CompilerManager;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** Represents the declaration of an anonymous function, including context functions such as
 * { (: Box self :) Out println: get ]
 * @author José
 *
 */
abstract public class ExprFunction extends Expr {

	public ExprFunction(Symbol startSymbol, MethodDec method) {
		super(method);
		this.startSymbol = startSymbol;
		returnTypeExpr = null;
		accessedParameterList = new ArrayList<ParameterDec>();
		accessedLocalVariables = new ArrayList<StatementLocalVariableDec>();
		localVariableDecList = new ArrayList<VariableDecInterface>();
		statementReturnFunctionList = new ArrayList<StatementReturnFunction>();
		statementReturnList = new ArrayList<StatementReturn>();
		hasMethodReturnStatement = false;
		varNameNewCodeOldCodeList = null;
	}


	@Override
	abstract public WrExprFunction getI();

	@Override
	public Object eval(EvalEnv ee) {
		ee.error(this.getFirstSymbol(), "Anonymous functions are not supported yet by the Cyan interpreter");
		return null;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		this.statementList.accept(visitor);
	}


	public void setStatementList(StatementList statementList) {
		this.statementList = statementList;
	}
	public StatementList getStatementList() {
		return statementList;
	}

	public void setEndSymbol(Symbol endSymbol) {
		this.endSymbol = endSymbol;
	}

	public Symbol getEndSymbol() {
		return endSymbol;
	}

	public void setReturnTypeExpr(Expr returnType) {
		this.returnTypeExpr = returnType;
	}

	public Expr getReturnTypeExpr() {
		return returnTypeExpr;
	}

	public void setStartSymbol(Symbol startSymbol) {
		this.startSymbol = startSymbol;
	}

	public Symbol getStartSymbol() {
		return startSymbol;
	}

	/*@Override
	public void genCyan(PWInterface pw, CyanEnv cyanEnv) {
		genCyan(pw, false, cyanEnv, true);
	} */


	@Override
	public Symbol getFirstSymbol() {
		return startSymbol;
	}



	public boolean isContextFunction() {
		return false;
	}


	public Expr getSelfType() {
		return selfType;
	}

	public void setSelfType(Expr selfType) {
		this.selfType = selfType;
	}


	abstract public void genFunctionSignatureCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions);

	abstract public StringBuffer genContextObjectForFunction(CyanEnv cyanEnv);

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( ! genFunctions ) {

			int size = this.accessedLocalVariables.size() + this.accessedParameterList.size();

			pw.print(this.functionPrototypeName);
			pw.print("(");

			if ( ! this.isContextFunction() ) {
				// pw.print(NameServer.selfNameInnerPrototypes);
				pw.print(NameServer.selfNameInnerPrototypes);
			}

			if ( size > 0 ) {
				if ( ! this.isContextFunction() )
					pw.print(", ");

				for ( ParameterDec p : accessedParameterList ) {
					pw.print(p.getName());
					if ( --size > 0 )
						pw.print(", ");
				}
				for ( StatementLocalVariableDec s : accessedLocalVariables ) {
					pw.print(s.getName());
					if ( --size > 0 )
						pw.print(", ");
				}
			}
			pw.print(")");
		}
		else {
			pw.print("{ ");
			genFunctionSignatureCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			pw.add();
			statementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			pw.sub();
			if ( printInMoreThanOneLine ) {
				pw.printIdent(" }");
			}
			else {
				pw.print(" }");
			}
		}
	}


	public void setFunctionPrototypeName(String functionPrototypeName) {
		this.functionPrototypeName = functionPrototypeName;
	}


	@Override
	public MethodDec getCurrentMethod() {
		return currentMethod;
	}

	public void setCurrentMethod(MethodDec currentMethod) {
		this.currentMethod = currentMethod;
	}


	public Prototype getCurrentPrototype() {
		return currentPrototype;
	}

	public void setCurrentPrototype(Prototype currentPrototype) {
		this.currentPrototype = currentPrototype;
	}

	public int getFunctionLevel() {
		return functionLevel;
	}

	public void setFunctionLevel(int functionLevel) {
		this.functionLevel = functionLevel;
	}

	/**
	 * Restricted functions have a 'return' statement or access local variables.
	   @return
	 */
	public boolean isRestricted() {
		return this.hasMethodReturnStatement || (accessedLocalVariables != null && accessedLocalVariables.size() > 0 ) ;
	}



	public void addAccessedParameter(ParameterDec paramDec) {
		String name = paramDec.getName();
		for ( ParameterDec c : accessedParameterList )
			if ( name.equals( c.getName()) )
				return ;
		this.accessedParameterList.add(paramDec);
	}


	public void addAccessedVariableDec(StatementLocalVariableDec variableDec) {
		String name = variableDec.getName();
		for ( StatementLocalVariableDec c : accessedLocalVariables )
			if ( name.equals( c.getName()) )
				return ;
		this.accessedLocalVariables.add(variableDec);
	}


	public void addLocalVariableDec( VariableDecInterface localVariableDec ) {
		localVariableDecList.add(localVariableDec);
	}

	public VariableDecInterface searchLocalVariableDec(String name) {
		for ( VariableDecInterface localDec : localVariableDecList ) {
			if ( localDec.getName().equals(name) )
				return localDec;
		}
		return null;
	}

	public int getSizeLocalVariableDecList() {
		return this.localVariableDecList.size();
	}

	public void trimLocalVariableDecListToSize(int trimSize) {
		int size = this.localVariableDecList.size();
		int n = size - trimSize;
		while ( n > 0 ) {
			this.localVariableDecList.remove(--size);
			--n;
		}
	}

	abstract public ParameterDec searchParameter(String name);

	public List<StatementReturnFunction> getStatementReturnFunctionList() {
		return statementReturnFunctionList;
	}

	public void addStatementReturnFunction(StatementReturnFunction statementReturnFunction) {
		statementReturnFunctionList.add(statementReturnFunction);
	}

	public List<StatementReturn> getStatementReturnList() {
		return statementReturnList;
	}

	public void addStatementReturn(StatementReturn statementReturn) {
		statementReturnList.add(statementReturn);
	}


	/**
	   @param env
	 */
	protected void calcReturnType(Env env) {

		returnType = null;
		if ( returnTypeExpr != null )
			returnType = returnTypeExpr.ifRepresentsTypeReturnsType(env);


		List<Type> returnTypeList = new ArrayList<Type>();
		List<Expr> returnTypeExprList = new ArrayList<Expr>();

		for ( StatementReturnFunction statReturnFunction : statementReturnFunctionList ) {
			Expr retExpr = statReturnFunction.getExpr();
			returnTypeExprList.add( retExpr );
			returnTypeList.add( retExpr.getType(env) );
		}

		// collectFunctionReturn( returnTypeList, returnTypeExprList, statementList, env );

		int sizeTypeList = returnTypeList.size();
		if ( returnType == null ) {
			/**
			 * a return type was not explicitly declared as in
			 *     { (: Int n :)  ^ 2*n }
			 */
			if ( sizeTypeList == 0 ) {
				returnType = Type.Nil;
			}
			else {
				int i = 1;
				Type first = returnTypeList.get(0);
				while ( i < sizeTypeList ) {
					if ( first != returnTypeList.get(i) )
						//Symbol symbol, String specificMessage, String identifier, ErrorKind errorKind
						env.error(true, returnTypeExprList.get(i).getFirstSymbol(),
								"This function is returning values of different types in different return statements", first.getName(), ErrorKind.function_returning_values_of_different_types);
					++i;
				}
				returnType = first;
			}
			if ( returnTypeExpr == null )
				returnTypeExpr = returnType.asExpr(this.getFirstSymbol());
		}
		else {
			/*
			 * there is a explicit type in the function as in { (: Int n -> String :) ... }
			 * all function return statements should return an expression that is subtype
			 * of the function type.
			 */
			int i = 0;
			for ( Type t : returnTypeList ) {
				if ( ! returnType.isSupertypeOf(t, env) )
					env.error( true, returnTypeExprList.get(i).getFirstSymbol(), "Expression should be subtype of '" +
							        returnType.getName() + "'", null, ErrorKind.type_error_return_value_type_is_not_a_subtype_of_the_function_return_type);
				++i;
			}

		}
	}

	protected Prototype createGenericPrototype(String prototypeName, List<List<Expr>> realTypeListList,
			Env env) {

		Symbol sym = this.getFirstSymbol();

		SymbolIdent symbolIdent = new SymbolIdent(Token.IDENT, prototypeName, sym.getStartLine(),
				sym.getLineNumber(), sym.getColumnNumber(), sym.getOffset(), sym.getCompilationUnit() );
		ExprIdentStar typeIdent = new ExprIdentStar(null, symbolIdent);

		ExprGenericPrototypeInstantiation gpi = new ExprGenericPrototypeInstantiation( typeIdent,
				realTypeListList, env.getCurrentPrototype(), null, null);
		return (Prototype )  CompilerManager.createGenericPrototype(gpi, env);
	}


	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public ObjectDec getInnerObjectForThisFunction() {
		return innerObjectForThisFunction;
	}

	public void setInnerObjectForThisFunction(ObjectDec innerObjectForThisFunction) {
		this.innerObjectForThisFunction = innerObjectForThisFunction;
	}

	@Override
	public void calcInternalTypes(Env env) {


		if ( innerObjectForThisFunction != null && env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_9 ) {
			if ( innerObjectForThisFunction.getSuperobjectExpr() instanceof ast.ExprGenericPrototypeInstantiation ) {
				// this should always be true

				ExprGenericPrototypeInstantiation e = (ExprGenericPrototypeInstantiation ) innerObjectForThisFunction.getSuperobjectExpr();
				// e should be Function<T1, ... Tn, R>
				List<List<Expr>> rtListList = e.getRealTypeListList();
				int sizeListList = rtListList.size();
				List<Expr> rtList = rtListList.get(sizeListList - 1);
				  /* change the return type to the return type of this function. Then
				   *    object F1(B self__, Int p1, Int v1) extends Function<Int, Any>  // the return type is initially 'Any'
				   *        fun eval: Int a -> Int { ^a + p1 + v1 + iv1 }
				   *    end
				   *
				   * is changed to
				   *    object F1(B self__, Int p1, Int v1) extends Function<Int, Int>   // replaced 'Any' by 'Int'
				   *        fun eval: Int a -> Int { ^a + p1 + v1 + iv1 }
				   *    end
				   *
				   * but just in the AST
				  */
				rtList.set(rtList.size()-1, this.returnTypeExpr);

			}
			/*
			 * now change the return type of 'eval' or 'eval:'
			 */
			for ( MethodDec m : innerObjectForThisFunction.getMethodDecList() ) {
				if ( NameServer.isMethodNameEval(m.getNameWithoutParamNumber()) ) {
					m.getMethodSignature().setReturnType(this.getReturnType());
					break;
				}
			}
		}
		env.removeLocalVarInfoLastLevel();

		super.calcInternalTypes(env);

	}

	public void beforeCalcInternalTypes(Env env) {
		/**
		 * change the type of some variables just for this literal function. At the end of the
		 * function the previous types are restored.
		 */
		if ( varNameNewCodeOldCodeList != null ) {
			for ( Tuple6<String, VariableDecInterface, Type, Type, String, String> t : this.varNameNewCodeOldCodeList ) {
				String varName = t.f1;
				Type newType = t.f3;
				String newCode = t.f5;
				VariableDecInterface v = env.searchVariable(varName);
				if ( v == null ) {
					env.error(this.getFirstSymbol(), "Attempt to change the type of '" + varName + "' that was not found", true, true);
				}
				else {
					t.f2 = v;
					t.f4 = v.getType();
					t.f6 = v.getJavaName();
					v.setType(newType);
					v.setJavaName(newCode);
				}
			}
		}
	}


	public void endCalcInternalTypesFunction() {
		/**
		 * restore the types and java code for the selected variables
		 */
		if ( varNameNewCodeOldCodeList != null ) {
			for ( Tuple6<String, VariableDecInterface, Type, Type, String, String> t : this.varNameNewCodeOldCodeList ) {
				VariableDecInterface vt = t.f2;
				vt.setType(t.f4);
				vt.setJavaName(t.f6);
			}
		}

	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String functionType = MetaHelper.getJavaName(this.functionPrototypeName);
		String tmp = NameServer.nextJavaLocalVariableName();
		pw.printIdent(functionType + " " + tmp + " = new " + functionType + "(" );
		if ( this.getCurrentPrototype().getOuterObject() == null )
			  // function is in a regular method. It is not in the prototype that was created for some method or other function
			pw.print("this");
		else
			pw.print(NameServer.javaSelfNameInnerPrototypes);

		int size = accessedParameterList.size() +
				   accessedLocalVariables.size();

		if ( size > 0 )
			pw.print(", ");


		for ( ParameterDec p : accessedParameterList ) {
			pw.print(p.getJavaName());
			if ( --size > 0 )
				pw.print(", ");
		}
		for ( VariableDecInterface p : accessedLocalVariables ) {
			pw.print(p.getJavaName());
			if ( --size > 0 )
				pw.print(", ");
		}
	    pw.println(");");
		return tmp;
	}

	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}


	public boolean getHasMethodReturnStatement() {
		return hasMethodReturnStatement;
	}

	public void setHasMethodReturnStatement(boolean hasMethodReturnStatement) {
		this.hasMethodReturnStatement = hasMethodReturnStatement;
	}

	/**
	 * add a variable to a list of variables whose type should be changed inside
	 * this literal function
	   @param varName
	   @param newType
	   @param newCode
	 */
	public void addNewVarInfo(String varName, Type newType, String newCode) {

		if ( varNameNewCodeOldCodeList == null )
			varNameNewCodeOldCodeList = new ArrayList<>();
		varNameNewCodeOldCodeList.add(new Tuple6<String, VariableDecInterface, Type, Type, String, String>(
				varName, null, newType, null, newCode, null));

	}

	public List<Tuple6<String, VariableDecInterface, Type, Type, String, String>> getVarNameNewCodeOldCodeList() {
		return varNameNewCodeOldCodeList;
	}

	public List<VariableDecInterface> getAccessedVariableParameters() {
		return accessedVariableParameters;
	}

	public void addAccessedUsingRemainderVariableSet( VariableDecInterface varDec ) {
		if ( accessedUsingRemainderVariableSet == null ) {
			accessedUsingRemainderVariableSet = new HashSet<>();
		}
		accessedUsingRemainderVariableSet.add(varDec);
	}

	public Set<VariableDecInterface> getAccessedUsingRemainderVariableSet() {
		return accessedUsingRemainderVariableSet;
	}

	/**
	 * the parameters of the outer method that are accessed by this function
	 */
	protected List<ParameterDec> accessedParameterList;


	/**
	 * the local variables of the outer method and outer functions that are accessed by this function
	 */
	protected List<StatementLocalVariableDec> accessedLocalVariables;


	/**
	 * symbol corresponding to { that starts a function
	 */
	protected Symbol startSymbol;
	/**
	 * symbol corresponding to } that ends a function
	 */
	protected Symbol endSymbol;
	/**
	 * return type of this function
	 */
	protected Expr returnTypeExpr;
	/**
	 * the return type of this function. This variable is set
	 * even if the function does not declare explicitly the return
	 * value type.
	 */
	protected Type returnType;
	/**
	 * list of statements of this function
	 */
	protected StatementList statementList;


	/**
	 * if isContextFunction is true, there is a declared type for self as "Person" in
	 * the example
	 *       { (: Person self :) name print }
	 * This variable holds this type
	 */
	protected Expr selfType;

	/**
	 * name of the prototype that was created for this function. This prototype
	 * contains an eval method with the function body or a method with another name
	 * if this is a function with keywords such as
	 *   { (: eval: (Int f1)  eval: (String y) :) ... }
	 */
	protected String functionPrototypeName;
//	/**
//	 * the method in which this function is declared
//	 */
//	protected MethodDec currentMethod;

	/**
	 * the prototype in which this function is declared
	 */
	protected Prototype currentPrototype;

	/**
	 * functionLevel is the number of nested functions in which this one is inside.
	 *      fun test {
	 *         var b = {  // functionLevel = 0
	 *                   var c = {  // functionLevel = 1
	 *                   };
	 *             };
	 *      }
	 *
	 * @param size
	 */
	private int functionLevel;


	/**
	 * list of local variable declarations of this function
	 */
	private List<VariableDecInterface> localVariableDecList;

	/**
	 * list of statements of the kind "^ expr". That is, return
	 * statement from this function
	 */
	protected List<StatementReturnFunction> statementReturnFunctionList;

	/**
	 * list of statements of the kind "return expr". That is, return
	 * statement from a method
	 *
	 */
	protected List<StatementReturn> statementReturnList;

	/**
	 * the number associated to this function. This number is 0 for the first function defined in the prototype and increases by 1 in textual order.
	 */
	private int number;

	/**
	 * the inner object created from this function.
	 * function that accesses parameters p1 and local variable v1 causes the creation of<br>

  	  <code>
      object F1(Int p1, Int v1) <br>
            extends Function{@literal <}Int, Int>  <br>
          fun eval: Int a -> Int {  <br>
              ^a + p1 + v1 + iv1  <br>
          }<br>
      end<br>
      <br>
      k = F1(p1, v1) eval: 0<br>
      <br>
      <code>
      The compiler associate a number for each function that it founds during
      compilation: 0, 1, ... When it enconters the objects created from functions,
      such as the above, it puts a pointer from the function to the object that
      represents that function. In calcInternalTypes each function goes to
      the object representing it and changes the types of parameters to the object
      (type of p1 and v1 in the example) and the return value of the eval method
      (and the second "Int" in Function<Int, Int> in the example above).
      	 *
	 */

	private ObjectDec innerObjectForThisFunction;
	/**
	 * true if this function has a statement 'return' (for methods) in it or in nested functions.
	 */
	private boolean hasMethodReturnStatement;
	/**
	 * the java code produced by some variables should be changed but just inside this literal function.
	 * The list below has tuples (varName, variable, newType, oldType, newJavaCode, oldJavaCode). The Java code for variable varName should
	 * be newJavaCode inside the literal function. After it the Java code for the variables is
	 * restored to oldJavaCode. The type of the variable inside the function is newType. The old type is oldType
	 */
	private List<Tuple6<String, VariableDecInterface, Type, Type, String, String>> varNameNewCodeOldCodeList;

	/**
	 * contains all external accessed variables and parameters by this function
	 */

	protected List<VariableDecInterface> accessedVariableParameters;

	/**
	 * contains all variables used inside the function that are accessed using %
	 */
	protected Set<VariableDecInterface> accessedUsingRemainderVariableSet;

}
