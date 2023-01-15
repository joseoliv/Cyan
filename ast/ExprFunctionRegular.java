
package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprFunctionRegular;
import meta.WrParameterDec;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 * Represents the declaration of a anonymous function that does not have
 * explicit keywords such as { (: Int n -> Int :) ^ n * n } or { Out println:
 * "ok" }
 *
 * @see ExprFunctionWithKeywords
 * @author José
 *
 */

public class ExprFunctionRegular extends ExprFunction {

	public ExprFunctionRegular(Symbol startSymbol, MethodDec method) {
		super(startSymbol, method);
		parameterList = new ArrayList<ParameterDec>();
	}

	@Override
	public WrExprFunctionRegular getI() {
		return new WrExprFunctionRegular(this);
	}


	@Override
	public void accept(ASTVisitor visitor) {
		super.accept(visitor);
		for (ParameterDec p : this.parameterList)
			p.accept(visitor);
		visitor.visit(this);
	}

	/*
	 * public void addParamDec(ParameterDec parameterDec) {
	 * parameterList.add(parameterDec); }
	 */

	@Override
	public ParameterDec searchParameter(String name) {
		for (ParameterDec p : this.parameterList) {
			if ( name.equals(p.getName()) ) return p;
		}
		return null;
	}

	@Override
	public void genFunctionSignatureCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv,
			boolean genFunctions) {

		if ( (parameterList != null && parameterList.size() > 0)
				|| (returnTypeExpr != null && returnTypeExpr.ifPrototypeReturnsItsName().compareTo("Nil") != 0
						&& (MetaHelper.cyanLanguagePackageDirectory + "." + returnTypeExpr.ifPrototypeReturnsItsName())
								.compareTo("Nil") != 0) ) {
			if ( printInMoreThanOneLine ) {
				pw.println();
				pw.add();
				pw.printIdent("(: ");
			}
			else
				pw.print("(: ");
			if ( parameterList != null ) {
				int size = parameterList.size();
				for (ParameterDec p : parameterList) {
					p.genCyan(pw, false, cyanEnv, genFunctions);
					if ( --size > 0 ) pw.print(", ");
				}
			}
			if ( returnTypeExpr != null && returnTypeExpr.ifPrototypeReturnsItsName().compareTo("Nil") != 0 ) {
				pw.print(" -> ");
				returnTypeExpr.genCyan(pw, false, cyanEnv, genFunctions);
			}
			pw.print(" :) ");
			if ( printInMoreThanOneLine ) {
				pw.sub();
				pw.println();
			}
		}
	}

	@Override
	public StringBuffer genContextObjectForFunction(CyanEnv cyanEnv) {

		StringBuffer s = new StringBuffer();

		if ( !(currentPrototype instanceof ObjectDec) ) {
			return s;
		}

		this.accessedVariableParameters = new ArrayList<VariableDecInterface>();
		accessedVariableParameters.addAll(accessedParameterList);
		accessedVariableParameters.addAll(accessedLocalVariables);
		int size = accessedVariableParameters.size();
		ObjectDec currentObjectDec = (ObjectDec) currentPrototype;
		boolean contextFunction = this.isContextFunction();

		s.append("    object " + this.functionPrototypeName);
		boolean wroteLeftPar = true;
		if ( contextFunction ) {
			if ( size > 0 ) {
				s.append("(");
			}
			else
				wroteLeftPar = false;
		}
		else {
			s.append("(" + currentObjectDec.getName() + " " + NameServer.selfNameInnerPrototypes);

		}

		if ( (size > 0 && contextFunction) || !contextFunction ) {

			/*
			 * The correct return type will only be discovered at semantic
			 * analysis in the most general case. Use "Any" till then. In cases
			 * like var i = 1; the compiler do not know the type of 'i'. That
			 * is, p.getTypeInDec() returns 'null'.
			 */
			if ( size > 0 && !contextFunction ) s.append(", ");
			for (VariableDecInterface p : accessedParameterList) {
				s.append("Any  " + p.getName());
				if ( --size > 0 ) s.append(", ");
			}
			/*
			 * Use "Any" as type for every variable. The correct type will only
			 * be discovered at semantic analysis, in calcInternalTypes.
			 */
			for (VariableDecInterface v : accessedLocalVariables) {
				s.append("Any ");
				/*
				 * if v is readonly, it is not refType
				 */
				if ( !contextFunction && !v.isReadonly() ) s.append("& ");
				s.append(v.getName());
				if ( --size > 0 ) s.append(", ");
			}
		}
		if ( wroteLeftPar ) s.append(")");
		s.append(" ");
		String returnTypeName;
		if ( returnTypeExpr != null )
			returnTypeName = returnTypeExpr.ifPrototypeReturnsItsName();
		else
			/*
			 * The correct return type will only be discovered at semantic
			 * analysis. Use "Any" till then.
			 */
			returnTypeName = "Any";

		/*
		 * object CFun_14__(Any &outterSelf) implements ContextFunction<Person,
		 * Int, Char, String>
		 *
		 * fun bindToFunction: Person newSelf__ -> UFunction<, Int, CharString>
		 * { return { (: Int i, -> String :) name: "Carolina2";
		 *
		 */

		size = parameterList.size();
		if ( contextFunction ) {
			s.append("implements " + NameServer.contextFunctionPrototypeName + "<");
			int sizep = parameterList.size();
			String[] parameterTypeNames = new String[sizep];
			String ufunctionParamTypes = "";
			String ufunctionParam = "";
			int i = 0;
			for (ParameterDec p : parameterList) {
				parameterTypeNames[i] = p.getTypeInDec().asString();
				s.append(parameterTypeNames[i]);
				if ( i > 0 ) {
					ufunctionParamTypes += parameterTypeNames[i];
					ufunctionParam += parameterTypeNames[i] + " " + p.getName();
				}
				++i;
				if ( --sizep > 0 ) {
					s.append(", ");
					if ( i > 1 ) {
						ufunctionParamTypes += ", ";
						ufunctionParam += ", ";
					}
				}
			}
			s.append(", ");
			if ( parameterList.size() > 1 ) ufunctionParamTypes += ", ";
			ufunctionParamTypes += returnTypeName;
			s.append(returnTypeName + ">\n");
			s.append('\n');

			/*
			 * fun bindToFunction: (S newSelf) -> UFunction<T1, T2, ..., Tn, R>
			 * { return { (: T1 t1, T2 t2, ..., Tn tn -> R :) // body of the
			 * context function with // self replaced by newSelf ... } }
			 */
			s.append("        func bindToFunction: " + parameterTypeNames[0] + " " + NameServer.selfNameContextObject
					+ " -> UFunction<" + ufunctionParamTypes + "> {\n");
			s.append("            return { (: " + ufunctionParam);
			s.append(" -> " + returnTypeName + " :)\n");

			PWCharArray pwChar = new PWCharArray();
			pwChar.add();
			pwChar.add();
			pwChar.add();
			pwChar.add();

			statementList.genCyan(pwChar, true, new CyanEnv(false, true), false);
			s.append(pwChar.getGeneratedString());

			s.append("            }\n");
			s.append("        }\n");
			s.append("\n");

			/*
			 * object ContextFunction001(V1 v1, ..., Vk vk) implements
			 * ContextFunction<S, T1, T2, ..., Tn, R> fun bindToFunction: (S
			 * newSelf) -> UFunction<T1, T2, ..., Tn, R> { return { (: T1 t1, T2
			 * t2, ..., Tn tn -> R :) // body of the context function with //
			 * self replaced by newSelf ... } }
			 */

		}
		else {
			s.append(" extends Function<");
			if ( size > 0 ) {
				int sizep = parameterList.size();
				String[] parameterTypeNames = new String[sizep];
				int i = 0;
				if ( sizep > 0 ) {
					for (ParameterDec p : parameterList) {
						parameterTypeNames[i] = p.getTypeInDec().asString();
						s.append(parameterTypeNames[i]);
						++i;
						if ( --sizep > 0 ) s.append(", ");
					}
				}
				else {
					s.append("none");
				}
				s.append(", ");
				s.append(returnTypeName + ">\n");

				s.append('\n');
				s.append("        override\n");
				s.append("        func eval:");
				i = 0;
				for (ParameterDec parameter : parameterList) {
					String paramName = parameter.getName();
					if ( paramName.equals("self") ) paramName = NameServer.selfNameInnerPrototypes;
					s.append(" " + parameterTypeNames[i] + " " + paramName);
					++i;
					if ( --size > 0 ) s.append(", ");
				}

			}
			else {
				/*
				 * put Any as the return value type if not given explicitly. The
				 * correct return value type will replace "Any" during semantic
				 * analysis (calcInterfaceTypes).
				 */
				s.append("Nil>\n");
				s.append("        override\n");
				s.append("        func eval");

			}
			s.append(" -> " + returnTypeName);

			s.append("  { \n");
			PWCharArray pwChar = new PWCharArray();
			pwChar.add();
			pwChar.add();
			pwChar.add();
			cyanEnv.setCreatingInnerPrototypesInsideEval(true);
			statementList.genCyan(pwChar, true, cyanEnv, false);
			cyanEnv.setCreatingInnerPrototypesInsideEval(false);
			s.append(pwChar.getGeneratedString());
			s.append("        }\n");
			s.append("\n");

		}

		s.append("    end\n\n");
		return s;

	}



	@Override
	public void calcInternalTypes(Env env) {

		env.atBeginningFunctionDec();
		env.pushFunction(this);

		beforeCalcInternalTypes(env);

		int numLocalVariables = env.numberOfLocalVariables();

		// set the types of the parameters and push them into the list of
		// variables
		for (ParameterDec parameter : parameterList) {
			parameter.calcInternalTypes(env);
		}

		env.setHasJavaCode(false);

		if ( isContextFunction() ) {
			// a context function
			Prototype currentPrototype2 = env.getCurrentPrototype();
			env.setCurrentPrototype((Prototype) parameterList.get(0).getType());
			statementList.calcInternalTypes(env);
			env.setCurrentPrototype(currentPrototype2);
		}
		else
			statementList.calcInternalTypes(env);

		int numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

		env.popNumLocalVariableDec(numLocalVariablesToPop); // parameterList.size());
		env.popFunction();

		calcReturnType(env);

		if ( !env.getHasJavaCode() && !statementList.getFoundError() ) {
			/*
			 * if there is no annotation to metaobject javacode inside the function
			 * then the compiler can deduce whether or not the method always
			 * return
			 */
			if ( !statementList.alwaysReturnFromFunction() && this.getReturnType() != Type.Nil ) {
				List<Statement> statementArray = statementList.getStatementList();
				if ( statementArray.size() == 0 ) {
					env.error(this.getFirstSymbol(), "Function does not return a value");
				}
				else {
					statementList.alwaysReturnFromFunction();
					env.error(
							statementList.getStatementList().get(statementList.getStatementList().size() - 1)
									.getFirstSymbol(),
							"Statement does not return a value. Therefore this function does not return a value");
				}
			}
		}

		List<List<Expr>> realTypeListList = new ArrayList<List<Expr>>();
		List<Expr> realTypeList = new ArrayList<Expr>();

		if ( parameterList != null && parameterList.size() > 0 ) {
			for (ParameterDec p : parameterList) {
				Type paramType = p.getType();
				if ( paramType.getInsideType() instanceof Prototype ) {
					Expr typeAsExpr = ((Prototype) paramType).asExpr(this.getFirstSymbol());
					if ( typeAsExpr instanceof ExprGenericPrototypeInstantiation ) {
						((ExprGenericPrototypeInstantiation) typeAsExpr).setPrototype(env.getCurrentPrototype());
					}
					realTypeList.add(typeAsExpr);
				}
				else if ( paramType instanceof TypeDynamic ) {
					/*
					 * public SymbolIdent(Token token, String symbolString, int
					 * startOffsetLine, int lineNumber, int columnNumber, int
					 * offset) {
					 *
					 */
					Symbol first = p.getFirstSymbol();
					realTypeList.add(new ExprIdentStar( null,
							new SymbolIdent(Token.IDENT, MetaHelper.dynName,
							    first.getStartLine(), first.getLineNumber(), first.getColumnNumber(), first.getOffset(),
							    first.getCompilationUnit())) );
				}
				else {

					Expr typeAsExpr = paramType.asExpr(this.getFirstSymbol());
					if ( typeAsExpr instanceof ExprGenericPrototypeInstantiation ) {
						env.error(null, "This type cannot be the type of a function parameter", true, true);
					}
					realTypeList.add(typeAsExpr);

//					env.error(true, p.getTypeInDec().getFirstSymbol(),
//							"The type of this parameter should be a Cyan prototype", paramType.getName(),
//							ErrorKind.cyan_prototype_expected_as_type);
				}
			}
		}
		if ( returnType.getInsideType() instanceof Prototype ) {
			Expr typeAsExpr = ((Prototype) returnType).asExpr(this.getFirstSymbol());
			if ( typeAsExpr instanceof ExprGenericPrototypeInstantiation ) {
				((ExprGenericPrototypeInstantiation) typeAsExpr).setPrototype(env.getCurrentPrototype());
			}
			// add the return type
			realTypeList.add(typeAsExpr);
		}
		else if ( returnType instanceof TypeDynamic ) {
			// add the return type
			Symbol first = this.getFirstSymbol();
			realTypeList.add(
					new ExprIdentStar(null,
							new SymbolIdent(Token.IDENT, MetaHelper.dynName, first.getStartLine(),
					first.getLineNumber(), first.getColumnNumber(), first.getOffset(), first.getCompilationUnit())));
		}
		else {

			Expr typeAsExpr = returnType.getInsideType().asExpr(this.getFirstSymbol());
			if ( typeAsExpr instanceof ExprGenericPrototypeInstantiation ) {
				env.error(null, "This type cannot be the return value type of a function", true, true);
			}
			// add the return type
			realTypeList.add(typeAsExpr);

			//env.error(null, "Functions should return a Cyan prototype", true, true);
		}
		realTypeListList.add(realTypeList);
		/*
		 * if the function is a context function, an interface ContextFunction
		 * should be created. But this interface defines a method bindToFunction
		 * that returns an object of UFunction: interface ContextFunction<S, T1,
		 * T2, ..., Tn, R> fun bindToFunction: S -> UFunction<T1, T2, ..., Tn,
		 * R> end This UFunction prototype does not use the second parameter, S.
		 * That is the reason this parameter is removed from realTypeListList in
		 * the statements below.
		 */
		String functionString;
		if ( isContextFunction() ) {
			/*
			 * selfTypeExpr = realTypeList.get(0); realTypeList.remove(1);
			 */
			functionString = NameServer.contextFunctionPrototypeName;
			// # create function below
			/*
			 * object ContextFunction001(V1 v1, ..., Vk vk) implements
			 * ContextFunction<S, T1, T2, ..., Tn, R> fun bindToFunction: (S
			 * newSelf) -> UFunction<T1, T2, ..., Tn, R> { return { (: T1 t1, T2
			 * t2, ..., Tn tn -> R :) // body of the context function with //
			 * self replaced by newSelf ... } }
			 */
		}
		else
			functionString = "Function";

		type = createGenericPrototype(functionString, realTypeListList, env);
		super.calcInternalTypes(env);
		endCalcInternalTypesFunction();
	}

	@Override
	public boolean isContextFunction() {

		return parameterList != null && parameterList.size() > 0 && parameterList.get(0).getName().equals("self");
	}

	public List<ParameterDec> getParameterList() {
		return parameterList;
	}

	/**
	 * list of the parameters associated with this function
	 *
	 */
	private List<ParameterDec> parameterList;

	private List<WrParameterDec> iparameterList = null;


}
