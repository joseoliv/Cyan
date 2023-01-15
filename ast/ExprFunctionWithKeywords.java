/**
 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import error.ErrorKind;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprFunctionWithKeywords;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**  Represents a function with keywords such as
 *      :b = { (: eval: Int eval: Int :) ^height*width };
 *      area = b eval: 100 eval: 20;
 * @author jose
 *
 */
public class ExprFunctionWithKeywords extends ExprFunction {

	public ExprFunctionWithKeywords(Symbol startSymbol,
			                        List<MethodKeywordWithParameters> keywordWithParametersList, MethodDec method) {
		super(startSymbol, method);
		this.keywordWithParametersList = keywordWithParametersList;
	}


	@Override
	public WrExprFunctionWithKeywords getI() {
		return new WrExprFunctionWithKeywords(this);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		super.accept(visitor);
		for ( MethodKeywordWithParameters p : this.keywordWithParametersList )
			p.accept(visitor);
		visitor.visit(this);
	}


	@Override
	public ParameterDec searchParameter(String name) {
		for ( MethodKeywordWithParameters s : this.keywordWithParametersList ) {
			for ( ParameterDec p : s.getParameterList() ) {
				if ( name.equals(p.getName()) )
					return p;
			}
		}
		return null;
	}


@Override
public StringBuffer genContextObjectForFunction(CyanEnv cyanEnv) {

		StringBuffer s = new StringBuffer();

		if ( ! (currentPrototype instanceof ObjectDec ) ) {
			return s;
		}

		this.accessedVariableParameters = new ArrayList<VariableDecInterface>();
		accessedVariableParameters.addAll(accessedParameterList);
		accessedVariableParameters.addAll(accessedLocalVariables);
		int size = accessedVariableParameters.size();
		ObjectDec currentObjectDec = (ObjectDec ) currentPrototype;
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
			s.append("(" + currentObjectDec.getName() + " " + NameServer.selfNameInnerPrototypes );

		}

		if ( (size > 0 && contextFunction) || ! contextFunction)  {

			/* The correct return type will only be discovered at semantic analysis in the most general case.
			 * Use "Any" till then. In cases like
			 *       var i = 1;
			 * the compiler do not know the type of 'i'. That is, p.getTypeInDec() returns 'null'.
			 */
			if ( size > 0 && !contextFunction )
				s.append(", ");
			for ( VariableDecInterface p : accessedParameterList ) {
				s.append("Any  " + p.getName());
				if ( --size > 0 )
					s.append(", ");
			}
			/*
			 * Use "Any" as type for every variable. The correct type will only be
			 * discovered at semantic analysis, in calcInternalTypes.
			 */
			for ( VariableDecInterface v : accessedLocalVariables ) {
				s.append( "Any ");
				if ( ! contextFunction && !v.isReadonly())
					s.append("& ");
				s.append(v.getName());
				if ( --size > 0 )
					s.append(", ");
			}
		}
		if ( wroteLeftPar )
			s.append(")");
		s.append(" ");
		String returnTypeName;
		if ( returnTypeExpr != null )
			returnTypeName = returnTypeExpr.ifPrototypeReturnsItsName();
		else
			/* The correct return type will only be discovered at semantic analysis.
			 * Use "Any" till then.
			 */
			returnTypeName = "Any";


		// size = parameterList.size();

		s.append(" extends Function");
		int sizesp = keywordWithParametersList.size();

		for ( MethodKeywordWithParameters keyword :  keywordWithParametersList ) {
			int sizep = keyword.getParameterList().size();
			s.append("<");
			if ( sizep > 0 ) {
				for ( ParameterDec p : keyword.getParameterList() ) {
					s.append(p.getTypeInDec().ifPrototypeReturnsItsName());
					if ( --sizep > 0 )
						s.append(", ");
				}
			}
			else {
				s.append("none");
			}
			if ( --sizesp == 0 ) {
				s.append(", " + returnTypeName + ">");
			}
			else
				s.append(">");
		}

		s.append("\n\n");
		s.append("        func");

		for ( MethodKeywordWithParameters keyword :  keywordWithParametersList ) {
			s.append(" eval: ");
			int sizep = keyword.getParameterList().size();
			for ( ParameterDec p : keyword.getParameterList() ) {
				s.append(p.getTypeInDec().ifPrototypeReturnsItsName() + " " + p.getName());
				if ( --sizep > 0 )
					s.append(", ");
			}
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
		s.append("    end\n\n");
		return s;

	}



	public StringBuffer genContextObjectForFunction3(CyanEnv cyanEnv) {
		StringBuffer s = new StringBuffer();


		if ( ! (currentPrototype instanceof ObjectDec ) ) {
			return s;
		}
		ObjectDec currentObjectDec = (ObjectDec ) currentPrototype;

		s.append("    object " + this.functionPrototypeName + "(" + currentObjectDec.getFullName()
		+ " " + NameServer.selfNameInnerPrototypes );
		List<VariableDecInterface> varList = new ArrayList<VariableDecInterface>();
		varList.addAll(accessedParameterList);
		varList.addAll(accessedLocalVariables);
		int size = varList.size();
		if ( size > 0 ) {
			s.append(", ");

			/*
			 * Use "Any" as type for every variable. The correct type will only be
			 * discovered at semantic analysis, in calcInternalTypes.
			 */
			for ( VariableDecInterface v : varList ) {
				s.append( "Any &" + v.getName() );
				if ( --size > 0 )
					s.append(", ");
			}
		}
		s.append(") ");

		String returnTypeName;
		if ( returnTypeExpr != null )
			returnTypeName = returnTypeExpr.ifPrototypeReturnsItsName();
		else
			/* The correct return type will only be discovered at semantic analysis.
			 * Use "Any" till then.
			 */
			returnTypeName = "Any";


		s.append(" extends Function");

		int sizesp = keywordWithParametersList.size();

		for ( MethodKeywordWithParameters keyword :  keywordWithParametersList ) {
			int sizep = keyword.getParameterList().size();
			s.append("<");
			for ( ParameterDec p : keyword.getParameterList() ) {
				s.append(p.getTypeInDec().ifPrototypeReturnsItsName());
				if ( --sizep > 0 )
					s.append(", ");
			}
			if ( --sizesp == 0 ) {
				s.append(", " + returnTypeName + ">");
			}
			else
				s.append(">");
		}

		s.append("\n\n");
		s.append("        func");

		for ( MethodKeywordWithParameters keyword :  keywordWithParametersList ) {
			s.append(" eval: (");
			int sizep = keyword.getParameterList().size();
			for ( ParameterDec p : keyword.getParameterList() ) {
				s.append(p.getTypeInDec().ifPrototypeReturnsItsName() + " " + p.getName());
				if ( --sizep > 0 )
					s.append(", ");
			}
			s.append(")");
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
		s.append("    end\n\n");
		return s;

	}



	@Override
	public void genFunctionSignatureCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( keywordWithParametersList != null && keywordWithParametersList.size() > 0 ) {
			if ( printInMoreThanOneLine ) {
				pw.add();
				pw.printIdent("(: ");
			}
			else
				pw.print(" ");

			for ( MethodKeywordWithParameters keyword : keywordWithParametersList ) {
				keyword.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			}

			if ( returnTypeExpr != null ) {
				pw.print(" -> ");
				returnTypeExpr.genCyan(pw, false, cyanEnv, genFunctions);
			}
			pw.print(" :) ");
			if ( printInMoreThanOneLine )
				pw.sub();
		}

	}


	public List<MethodKeywordWithParameters> getkeywordWithParametersList() {
		return keywordWithParametersList;
	}

	public void setkeywordWithParametersList(
			List<MethodKeywordWithParameters> keywordWithParametersList) {
		this.keywordWithParametersList = keywordWithParametersList;
	}

	@Override
	public void calcInternalTypes(Env env) {

		env.atBeginningFunctionDec();
		env.pushFunction(this);

		beforeCalcInternalTypes(env);

		int numLocalVariables = env.numberOfLocalVariables();

		// set the types of the parameters and push them into the list of
		// variables

		// List<List<Expr>> exprListList = new ArrayList<List<Expr>>();
		// List<Expr> exprList;
		for (MethodKeywordWithParameters keyword : this.keywordWithParametersList) {
			for (ParameterDec parameter : keyword.getParameterList())
				parameter.calcInternalTypes(env);
		}

		env.setHasJavaCode(false);

		statementList.calcInternalTypes(env);

		int numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

		env.popNumLocalVariableDec(numLocalVariablesToPop);
		env.popFunction();

		calcReturnType(env);

		if ( ! env.getHasJavaCode() && ! statementList.getFoundError() ) {
			/*
			 * if there is no annotation to metaobject javacode inside the function then the compiler
			 * can deduce whether or not the method always return
			 */
			if ( ! statementList.alwaysReturnFromFunction() && this.getReturnType() != Type.Nil ) {
				List<Statement> statementArray = statementList.getStatementList();
				if ( statementArray.size() == 0 ) {
					env.error(this.getFirstSymbol(),
						"Function does not return a value");
				}
				else {
					env.error(statementList.getStatementList().get(statementList.getStatementList().size()-1).getFirstSymbol(),
							"Statement does not return a value. Therefore this function does not return a value");
				}
			}
		}



		List<List<Expr>> realTypeListList = new ArrayList<List<Expr>>();
		List<Expr> realTypeList;
		int sizekeywordList = keywordWithParametersList.size();


		for (MethodKeywordWithParameters keyword : this.keywordWithParametersList) {
			realTypeList = new ArrayList<Expr>();

			if ( keyword.getParameterList().size() == 0 ) {
				Symbol first = this.getFirstSymbol();
				realTypeList.add( new ExprIdentStar( null,
						new SymbolIdent(Token.IDENT, MetaHelper.noneArgumentNameForFunctions, first.getStartLine(),
								first.getLineNumber(), first.getColumnNumber(), first.getOffset(), first.getCompilationUnit())) );
			}
			else {
				for ( ParameterDec p : keyword.getParameterList() ) {
					Type paramType = p.getType();
					if ( paramType.getInsideType() instanceof Prototype ) {
						Expr typeAsExpr = ((Prototype ) paramType).asExpr(this.getFirstSymbol());
						if ( typeAsExpr instanceof ExprGenericPrototypeInstantiation ) {
							((ExprGenericPrototypeInstantiation ) typeAsExpr).setPrototype(env.getCurrentPrototype());
						}
						realTypeList.add( typeAsExpr );
					}
					else if ( paramType instanceof TypeDynamic ) {
						Symbol first = p.getFirstSymbol();
						realTypeList.add( new ExprIdentStar(null,
								new SymbolIdent(Token.IDENT, MetaHelper.dynName, first.getStartLine(),
										first.getLineNumber(), first.getColumnNumber(), first.getOffset(), first.getCompilationUnit())) );
					}
					else {
						env.error(true, p.getTypeInDec().getFirstSymbol(),
								"The type of this parameter should be a Cyan prototype", paramType.getName(), ErrorKind.cyan_prototype_expected_as_type);
					}

				}			}

			realTypeListList.add(realTypeList);
			if ( --sizekeywordList == 0 ) {
				if ( returnType.getInsideType() instanceof Prototype ) {
					Expr typeAsExpr = ((Prototype ) returnType).asExpr(this.getFirstSymbol());
					if ( typeAsExpr instanceof ExprGenericPrototypeInstantiation ) {
						((ExprGenericPrototypeInstantiation ) typeAsExpr).setPrototype(env.getCurrentPrototype());
					}
					   // add the return type
					realTypeList.add( typeAsExpr );
				}
				else if ( returnType instanceof TypeDynamic ) {
					   // add the return type
					   // add the return type
					Symbol first = this.getFirstSymbol();
					realTypeList.add( new ExprIdentStar( null,
							new SymbolIdent(Token.IDENT, MetaHelper.dynName, first.getStartLine(),
									first.getLineNumber(), first.getColumnNumber(), first.getOffset(), first.getCompilationUnit())) );
				}
				else {
					env.error(this.getFirstSymbol(), "Functions should return a Cyan prototype", true, true);
				}
			}
		}
		String functionString;
		if ( isContextFunction() ) {
			functionString = "ContextFunction";
		}
		else
			functionString = "Function";


		++count;


		type = createGenericPrototype(functionString, realTypeListList, env);
		super.calcInternalTypes(env);
		endCalcInternalTypesFunction();
	}


	static int count = 0;

	@Override
	public boolean isContextFunction() {
		if ( keywordWithParametersList != null && keywordWithParametersList.size() > 0 ) {
			if ( keywordWithParametersList.get(0).getParameterList() != null ) {
				List<ParameterDec> parameterList = keywordWithParametersList.get(0).getParameterList();
				if ( parameterList != null && parameterList.size() > 0 &&
					 parameterList.get(0).getName().equals("self") )
					return true;
			}
		}
		return false;
	}


	private List<MethodKeywordWithParameters> keywordWithParametersList;


}
