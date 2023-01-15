package ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lexer.Symbol;
import meta.LocalVarInfo;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrStatementType;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

public class StatementType extends Statement {


	public StatementType(Symbol typeSymbol, Expr expr, MethodDec method) {
		super(method);
		this.typeSymbol = typeSymbol;
		this.expr = expr;
		this.caseList = new ArrayList<>();
		this.shouldBeFollowedBySemicolon = false;
	}

	@Override
	public WrStatementType getI() {
		if ( iStatementType == null ) {
			iStatementType = new WrStatementType(this);
		}
		return iStatementType;
	}

	private WrStatementType iStatementType = null;

	@Override
	public void accept(ASTVisitor visitor) {
		this.expr.accept(visitor);
		for ( CaseRecord caseRecord : caseList ) {
			caseRecord.accept(visitor);
		}
		visitor.visit(this);
	}


	@Override
	public Object eval(EvalEnv ee) {
//		Object exprValue = expr.eval(ee);
//		Class<?> exprValueClass = exprValue.getClass();
//		for ( CaseRecord caseRecord : caseList ) {
//			Class<?> caseClass = caseRecord.exprType.eval(ee);
//			if ( exprValueClass.equals())
//		}
		return null;
	}



	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {


		pw.print("type " );
		expr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		for ( CaseRecord caseRecord : caseList ) {
			pw.add();
			pw.println();
			caseRecord.genCyanReal(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			pw.sub();
		}
		if ( this.elseStatementList != null ) {
			pw.printlnIdent("else {");
			pw.add();
			this.elseStatementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			pw.sub();
			pw.printlnIdent("}");
		}


	}

	@Override
	public Symbol getFirstSymbol() {
		return typeSymbol;
	}




	@Override
	public void calcInternalTypes(Env env) {

		try {
			env.pushCheckUsePossiblyNonInitializedPrototype(true);
			expr.calcInternalTypes(env);
		}
		finally {
			env.popCheckUsePossiblyNonInitializedPrototype();
		}

		Type typeOfExpr = expr.getType();

		if ( typeOfExpr instanceof TypeWithAnnotations ) {

			env.error(expr.getFirstSymbol(), "The type of expression '" + expr.asString()
			+ "' has an attached annotation. It cannot be used with statement type-case");
		}
		if ( !(typeOfExpr instanceof Prototype) &&
				typeOfExpr.getInsideType() != Type.Dyn && !(typeOfExpr instanceof TypeUnion)) {
			env.error(expr.getFirstSymbol(), "The type of the 'type' expression should be a prototype or a Union");
		}

		Set<String> typeNameSet = new HashSet<String>();
		Set<String> unionTypeSet = null;

		ObjectDec proto = null;
		this.isUnion = false;
		isTaggedUnion = false;

		if ( typeOfExpr instanceof TypeUnion ) {
			TypeUnion tu = (TypeUnion ) typeOfExpr;
			unionTypeSet = new HashSet<String>();
			for ( Type t : tu.getTypeList() ) {
				unionTypeSet.add(t.getFullName());
			}
			isUnion = true;
		}
		else if ( typeOfExpr instanceof ObjectDec ) {
			proto = (ObjectDec ) typeOfExpr;

			isUnion = proto.getName().startsWith("Union<") &&
					proto.getCompilationUnit().getPackageName().equals(NameServer.cyanLanguagePackageName);
			if ( isUnion ) {
				unionTypeSet = new HashSet<String>();
				isTaggedUnion = true;
//				if (proto.getGenericParameterListList().get(0).get(0).getParameter() instanceof ExprIdentStar ) {
//					ExprIdentStar eis = (ExprIdentStar ) proto.getGenericParameterListList().get(0).get(0).getParameter();
//					if ( eis.getIdentSymbolArray().size() == 1 &&
//							Character.isLowerCase(eis.getIdentSymbolArray().get(0).getSymbolString().charAt(0)) ) {
//						// a tagged union: error
//						this.isTaggedUnion = true;
//					}
//				}
//				if ( isTaggedUnion ) {
					List<GenericParameter> gpList = proto.getGenericParameterListList().get(0);
					int i = 0;
					String currentLabel = null;
					labelTypeList = new ArrayList<>();
					while ( i < gpList.size() ) {
						if ( i%2 == 0 ) {
							// label
							currentLabel = gpList.get(i).getName();
						}
						else {
							// type
							labelTypeList.add( new Tuple2<String, String>(currentLabel, gpList.get(i).getParameter().getType().getFullName()));
						}
						++i;
					}
					i = 0;
					if ( gpList.size()%2 != 0 || caseList.size() != gpList.size()/2 ) {
						env.error(this.getFirstSymbol(),
								"The number of 'case' clauses is different from the number of types in the union '" + proto.getFullName() + "'"
										);
					}


//				}
//				else {
//					for ( List<GenericParameter> gpList : proto.getGenericParameterListList() ) {
//						for ( GenericParameter gp :  gpList ) {
//							if ( gp.getParameter() != null ) {
//								String fullName = gp.getParameter().getType().getFullName();
//								unionTypeSet.add(fullName);
//							}
//						}
//					}
//				}


			}
		}

		boolean statListWithNoVariableAssigned = false;
		List<List<LocalVarInfo>> localVarInfoListList = null;

		int k = 0;
		for ( CaseRecord caseRecord : caseList ) {

			env.addLexicalLevel();

			int numLocalVariables = env.numberOfLocalVariables();


			String name = null;
			if ( caseRecord.caseVariable != null ) {
				name = caseRecord.caseVariable.getName();
				if ( env.searchLocalVariableParameter(name) != null ) {
					env.error(caseRecord.caseVariable.getFirstSymbol(), "Variable is being redeclared. A 'case' variable cannot have been previously declared in the same method");
				}
			}
			else if ( isTaggedUnion) {
				env.error(caseRecord.caseVariable.getFirstSymbol(), "The 'type' expression is a tagged union. "
						+ "Therefore every case should be composed of a type followed by a variable "
						+ "that should have the name of one of the tags of the union");
			}
			try {
				env.pushCheckUsePossiblyNonInitializedPrototype(false);
				caseRecord.exprType.calcInternalTypes(env);
			}
			finally {
				env.popCheckUsePossiblyNonInitializedPrototype();
			}

			Type t = caseRecord.exprType.getType();

			if ( t instanceof TypeWithAnnotations ) {
				env.error(caseRecord.exprType.getFirstSymbol(), "The type '" + caseRecord.exprType.asString()
				+ "' has an attached annotation. It cannot be used in a 'case' clause of statement type-case");
			}


			if ( caseRecord.exprType instanceof ast.ExprIdentStar ) {
				ExprIdentStar eis = (ExprIdentStar ) caseRecord.exprType;
				if ( eis.getVarDeclaration() != null ) {
					env.error(caseRecord.exprType.getFirstSymbol(), "A type was expected");
				}
			}
			else if ( ! (caseRecord.exprType instanceof ast.ExprGenericPrototypeInstantiation)
					&& !(caseRecord.exprType instanceof ast.ExprTypeUnion) ) {
				env.error(caseRecord.exprType.getFirstSymbol(), "A type was expected");
			}


			String fullTypeName = t.getFullName();

			if ( isTaggedUnion ) {
				if ( caseRecord.caseVariable == null || caseRecord.exprType == null ) {
					env.error(caseRecord.getFirstSymbol(), "This 'case' clause should have both a type and a variable "
							+ "because the type of the expression of the 'type' command is a tagged union");
				}
				Tuple2<String, String> labelType = this.labelTypeList.get(k);
				if ( ! labelType.f1.equals(caseRecord.caseVariable.getName()) ) {
					env.error(caseRecord.caseVariable.getFirstSymbol(), "This variable should have the name of "
							+ "one of tags of the union. It should have the name '" + labelType.f1 + "'");
				}
				if ( ! labelType.f2.equals(fullTypeName) ) {
					env.error(caseRecord.exprType.getFirstSymbol(), "This type should be one of the "
							+ "types of the union. It should be '" + labelType.f2 + "'");
				}

				if ( !caseRecord.caseVariable.getName().equals(labelType.f1) ) {
					env.error(caseRecord.caseVariable.getFirstSymbol(),
							"This case variable should be '" + labelType.f1 + "'");
				}
				if ( ! caseRecord.exprType.getType().getFullName().equals(labelType.f2)) {
					env.error(caseRecord.caseVariable.getFirstSymbol(),
							"This type of this case should be '" + labelType.f2 + "'");
				}
			}
			else {
				if ( ! typeOfExpr.isSupertypeOf(t, env) ) {
					env.error(caseRecord.exprType.getFirstSymbol(), "The expressions following 'type' should be subtype of '" + typeOfExpr.getFullName() + "'",
							true, false);
				}
				if ( ! typeNameSet.add(fullTypeName) ) {
					env.error(caseRecord.exprType.getFirstSymbol(), "This type already appears in a previous 'case' clause", true, false);
				}

				if ( isUnion && unionTypeSet != null ) {
					if ( ! unionTypeSet.contains(fullTypeName) ) {
						boolean foundSuper = false;
						for ( Object fullNameUnionCase : unionTypeSet.toArray() ) {
							Type caseType = env.searchPackagePrototype((String ) fullNameUnionCase,
									caseRecord.caseVariable.getFirstSymbol());
							if ( caseType.isSupertypeOf(t, env) ) {
								foundSuper = true;
							}

						}
						if ( !foundSuper ) {
							typeOfExpr.isSupertypeOf(t, env);
							env.error(caseRecord.exprType.getFirstSymbol(), "The expressions following 'type' should be subtype of '" + typeOfExpr.getFullName() + "'",
									true, false);
						}

//						env.error(caseRecord.exprType.getFirstSymbol(),
//								"This type does not belong to the union '" + typeOfExpr.getFullName() + "'", true, false);
					}
				}
			}


			if ( caseRecord.caseVariable != null ) {
				caseRecord.caseVariable.setType(t);
				env.pushVariableDec(caseRecord.caseVariable);

				env.pushVariableAndLevel(caseRecord.caseVariable, name);
	 	        env.addLocalVariableInfoToCurrentLexicalLevel(caseRecord.caseVariable, expr);

			}

			caseRecord.statementList.calcInternalTypes(env);


			int numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

			env.popNumLocalVariableDec(numLocalVariablesToPop);

			env.removeVariablesLastLevel();

			if ( ! caseRecord.statementList.alwaysReturn(env)  ) {
				List<LocalVarInfo> list = env.getLocalVarInfoPreviousLevel();

				if (  ! caseRecord.statementList.isBreakLastStatement() || (!env.isEmptyRepetitionStatStack() && env.peekRepetitionStatStack() == 'r')  ) {
					if ( list != null ) {
						if ( localVarInfoListList == null ) {
							localVarInfoListList = new ArrayList<>();
						}
						localVarInfoListList.add(list);
					}
					else {
						statListWithNoVariableAssigned = true;
					}
				}

			}
			env.removeLocalVarInfoLastLevel();

			env.subLexicalLevel();

			if ( ! isTaggedUnion ) {
				/*
				if ( ! this.expr.getType().isSupertypeOf(caseRecord.exprType.getType(), env) ) {
					env.error(caseRecord.getFirstSymbol(),  "The type of 'case' clause is not a subtype of the type of the 'type' expression", true, false);
				}
				*/
	 			for (int j = 0; j < k; ++j) {
	 				if ( this.caseList.get(j).exprType.getType().isSupertypeOf(caseRecord.exprType.getType(), env) &&
	 						caseRecord.exprType.getType() != Type.Dyn
	 						) {
	 					env.error(caseRecord.exprType.getFirstSymbol(), "The type of this 'case' clause is subtype of the type of 'case' clause "
	 							+ "of line " + this.caseList.get(j).exprType.getFirstSymbol().getLineNumber()
	 							+ ". That means this 'case' clause will never be used", true, false);
	 				}
	 			}
			}

			++k;

		}
		if ( this.elseStatementList != null ) {
			this.elseStatementList.calcInternalTypes(env);

			if ( ! this.elseStatementList.alwaysReturn(env)  ) {
				List<LocalVarInfo> list = env.getLocalVarInfoPreviousLevel();

				if ( ! elseStatementList.isBreakLastStatement() || (!env.isEmptyRepetitionStatStack() && env.peekRepetitionStatStack() == 'r')  ) {
					if ( list != null ) {
						if ( localVarInfoListList == null ) {
							localVarInfoListList = new ArrayList<>();
						}
						localVarInfoListList.add(list);
					}
					else {
						statListWithNoVariableAssigned = true;
					}
				}
			}

			env.removeLocalVarInfoLastLevel();

		}

		if ( isUnion && unionTypeSet != null && caseList.size() == unionTypeSet.size() && this.elseStatementList != null ) {
			env.error(this.getFirstSymbol(),
					"The statements in the 'else' clause will never be used because the 'type' expression always matches one of the 'case' types");
		}

		/*
		 * the if expression is true if one of the case clauses of the else clause will be
		 * executed
		 */
		if ( caseElseClausesAreExhaustive(env) ) {


			if ( !statListWithNoVariableAssigned && localVarInfoListList != null ) {
				env.setLocalVarInitializedThisLevel(localVarInfoListList);
			}

		}

		super.calcInternalTypes(env);

	}



	@Override
	public void genJava(PWInterface pw, Env env) {

		pw.add();
		pw.printlnIdent("{");

		/*
		 * type str
            case String str2 {
               str2[0] println
            }
            case Nil nil2 {
            }

		 *
		 * tmp = str;
		 * if ( tmp instanceof String ) {
		 *     String str2 = (String ) tmp;
		 * }
		 *
		 */
		String tmpExpr = expr.genJavaExpr(pw, env);

		Type typeOfExpr = expr.getType();

		// ObjectDec proto = (ObjectDec ) expr.getType();
		if ( this.isTaggedUnion ) {
			String fullJavaTypeExprName = MetaHelper.getJavaName(typeOfExpr.getFullName());

			int i = 0;
			for ( CaseRecord caseRecord : caseList ) {

				String fullTypeName = caseRecord.exprType.getType().getJavaName();
				if ( i == 0 ) {
					pw.printIdent("");
				}
				else {
					pw.printIdent("else ");
				}
				//         if ( _energy._which._equal_equal_1( Union.wattsHour ) )

				pw.println("if ( " + tmpExpr + "._which._equal_equal( " + fullJavaTypeExprName + "." +
						caseRecord.caseVariable.getName() + " ).b ) { ");
				pw.add();
				pw.printlnIdent(fullTypeName + " " + caseRecord.caseVariable.getJavaName() + " = (" +
						fullTypeName + " ) " + tmpExpr + "._elem;");
		 		if ( caseRecord.statementList != null )
		 			caseRecord.statementList.genJava(pw, env);
				pw.sub();
				pw.printlnIdent("}");
				++i;
			}
			if ( this.elseStatementList != null ) {
				pw.printlnIdent("else {");
				pw.add();
				this.elseStatementList.genJava(pw, env);
				pw.sub();
				pw.printlnIdent("}");
			}
			/*
			else if ( statementAlwaysReturn != null && statementAlwaysReturn ) {
				pw.printlnIdent("else {");
				pw.add();
				pw.printlnIdent("return null;");
				pw.sub();
				pw.printlnIdent("}");
			}
			*/

		}
		else {
//			if ( typeOfExpr == Type.Dyn ) {
//				// String dynTmp = NameServer.nextJavaLocalVariableName();
//				//pw.printlnIdent("Object " + dynTmp + ";");
//				pw.printlnIdent("if ( " + tmpExpr + " instanceof " +
//				       MetaHelper.AnyInJava + " &&"
//						+ " (("  + MetaHelper.AnyInJava +  " ) " +
//				       tmpExpr + ").getUnionElem() != null ) { "
//						);
//				pw.add();
//				pw.printlnIdent(tmpExpr + " = " + " (("  + MetaHelper.AnyInJava +  " ) " + tmpExpr + ").getUnionElem();");
//				pw.sub();
//				pw.printlnIdent("}");
//			}

			int i = 0;
			for ( CaseRecord caseRecord : caseList ) {
				String fullTypeName = caseRecord.exprType.getType().getJavaName();
				if ( i == 0 ) {
					pw.printIdent("");
				}
				else {
					pw.printIdent("else ");
				}
				pw.println("if ( " + tmpExpr + " instanceof " + fullTypeName + " ) { ");
				pw.add();
				if ( caseRecord.caseVariable != null ) {
					pw.printlnIdent(fullTypeName + " " + caseRecord.caseVariable.getJavaName() + " = (" +
							fullTypeName + " ) " + tmpExpr + ";");
				}
		 		if ( caseRecord.statementList != null )
		 			caseRecord.statementList.genJava(pw, env);
				pw.sub();
				pw.printlnIdent("}");
				++i;
			}
			if ( this.elseStatementList != null ) {
				pw.printlnIdent("else {");
				pw.add();
				this.elseStatementList.genJava(pw, env);
				pw.sub();
				pw.printlnIdent("}");
			}
			/*
			else if ( statementAlwaysReturn != null && statementAlwaysReturn ) {
				pw.printlnIdent("else {");
				pw.add();
				pw.printlnIdent("return null;");
				pw.sub();
				pw.printlnIdent("}");
			}
			*/

		}
		if ( (alwaysReturn(env) || caseElseClausesAreExhaustive(env)) && this.elseStatementList == null ) {
			pw.printlnIdent("else {");
			Type returnType = env.getCurrentMethod().getMethodSignature().getReturnType(env);
			pw.add();
			if ( returnType != Type.Nil && returnType != null ) {
				pw.printlnIdent("return null;");
			}
			else {
				pw.printlnIdent("return ;");
			}
			pw.sub();
			pw.printlnIdent("}");
		}
		pw.sub();
		pw.printlnIdent("}");
	}


	public void addCaseRecord(CaseRecord caseRecord) {
		caseList.add(caseRecord);
	}


	public Symbol getTypeSymbol() {
		return typeSymbol;
	}

	public Expr getExpr() {
		return expr;
	}

	public List<CaseRecord> getCaseList() {
		return caseList;
	}

	public boolean getIsTaggedUnion() {
		return isTaggedUnion;
	}

	public List<Tuple2<String, String>> getLabelTypeList() {
		return labelTypeList;
	}

	public boolean getIsUnion() {
		return isUnion;
	}



	public StatementList getElseStatementList() {
		return elseStatementList;
	}

	public void setElseStatementList(StatementList elseStatementList) {
		this.elseStatementList = elseStatementList;
	}


	private Boolean statementAlwaysReturn = null;

	@Override
	public boolean alwaysReturn(Env env) {
		if ( statementAlwaysReturn == null ) {
			statementAlwaysReturn = this.alwaysReturnAux(env);
		}
		return statementAlwaysReturn;
	}

	@Override
	public boolean alwaysBreak(Env env) {


		boolean ret = true;
		for ( CaseRecord caseRecord : caseList ) {
			ret = ret && caseRecord.statementList.alwaysBreak(env);
		}
		if ( this.elseStatementList != null ) {
			return ret && this.elseStatementList.alwaysBreak(env);
		}
		else {
			if ( ! ret ) {
				/*
				 * one of the case clauses does not ends with a 'break'
				 */
				return false;
			}
			/*
			 * there is no 'else' clause and all 'case' clauses end with a 'break' statement.
			 * We should test if the clauses are exhaustive
			 */
				// no else statement
			if ( this.isTaggedUnion ) {
				  // exhaustive: one of the cases will be executed
				return ret;
			}
			else {
				   // it is not guaranteed that one of the cases will be executed
				CaseRecord last = this.caseList.get(caseList.size() - 1);
				Type lastType = last.exprType.getType();
				Type typeOfExpr = expr.getType();

				/*
				 *

				 */
				if ( isUnion ) {
					// non-tagged union

					TypeUnion tu = (TypeUnion ) typeOfExpr;
					for ( Type t : tu.getTypeList() ) {
						boolean foundGenericParameterName = false;
						for ( CaseRecord acase : this.caseList ) {
							if ( acase.exprType.getType().isSupertypeOf(t, env) ) {
								foundGenericParameterName = true;
							}
						}
						if ( ! foundGenericParameterName ) {
							  /*
							   * one of the types of the union is not subtype of
							   * at least one type of a 'case' clause
							   */
							return false;
						}
					}
					return true;

				}
				else {
					if ( (lastType == Type.Dyn || (lastType == Type.Any && typeOfExpr == Type.Any)) ) {
						return true;
					}
					else {
						return false;
					}
				}
			}
		}


	}

	private boolean alwaysReturnAux(Env env) {

		boolean ret = true;
		for ( CaseRecord caseRecord : caseList ) {
			ret = ret && caseRecord.statementList.alwaysReturn(env);
		}
		if ( this.elseStatementList != null ) {
			return ret && this.elseStatementList.alwaysReturn(env);
		}
		else {
			if ( ! ret ) {
				/*
				 * one of the case clauses does not ends with a return
				 */
				return false;
			}
			/*
			 * there is no 'else' clause and all 'case' clauses end with a return statement.
			 * We should test if the clauses are exhaustive
			 */
				// no else statement
			if ( this.isTaggedUnion ) {
				  // exhaustive: one of the cases will be executed
				return ret;
			}
			else {
				   // it is not guaranteed that one of the cases will be executed
				CaseRecord last = this.caseList.get(caseList.size() - 1);
				Type lastType = last.exprType.getType();
				Type typeOfExpr = expr.getType();

				/*
				 *

				 */
				if ( isUnion ) {
					// non-tagged union

					TypeUnion tu = (TypeUnion ) typeOfExpr;
					for ( Type t : tu.getTypeList() ) {
						boolean foundGenericParameterName = false;
						for ( CaseRecord acase : this.caseList ) {
							if ( acase.exprType.getType().isSupertypeOf(t, env) ) {
								foundGenericParameterName = true;
							}
						}
						if ( ! foundGenericParameterName ) {
							  /*
							   * one of the types of the union is not subtype of
							   * at least one type of a 'case' clause
							   */
							return false;
						}
					}
					return true;

				}
				else {
					if ( (lastType == Type.Dyn || (lastType == Type.Any && typeOfExpr == Type.Any)) ) {
						return true;
					}
					else {
						return false;
					}
				}
			}
		}

	}



	private boolean caseElseClausesAreExhaustiveAux(Env env) {

		if ( this.elseStatementList != null || this.isTaggedUnion  )
			return true;
		   // it is not guaranteed that one of the cases will be executed
		CaseRecord last = this.caseList.get(caseList.size() - 1);
		Type lastType = last.exprType.getType();
		Type typeOfExpr = expr.getType();

		if ( isUnion ) {

			// non-tagged union

			TypeUnion tu = (TypeUnion ) typeOfExpr;
			for ( Type t : tu.getTypeList() ) {
				boolean foundGenericParameterName = false;
				for ( CaseRecord acase : this.caseList ) {
					if ( acase.exprType.getType().isSupertypeOf(t, env) ) {
						foundGenericParameterName = true;
					}
				}
				if ( ! foundGenericParameterName ) {
					  /*
					   * one of the types of the union is not subtype of
					   * at least one type of a 'case' clause
					   */
					return false;
				}
			}
			return true;
		}
		else {
			return lastType == Type.Dyn || (lastType == Type.Any && typeOfExpr == Type.Any);
		}

	}

	private boolean caseElseClausesAreExhaustive(Env env) {
		if ( clausesAreExhaustive == null ) {
			clausesAreExhaustive = this.caseElseClausesAreExhaustiveAux(env);
		}
		return this.clausesAreExhaustive;
	}

	private Boolean clausesAreExhaustive = null;

	private Symbol typeSymbol;
	private Expr expr;
	private List<CaseRecord> caseList;
	private StatementList elseStatementList;
	private boolean isTaggedUnion;
	private List<Tuple2<String, String> > labelTypeList;
	private boolean isUnion;

}
