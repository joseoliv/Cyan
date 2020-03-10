package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.LocalVarInfo;
import meta.WrStatementCast;
import saci.CyanEnv;
import saci.Env;

public class StatementCast extends Statement {



	/*
		return new StatementCast(castSymbol, castVariableSymbolList, castTypeList,
				castStatementList,
                elseStatementList, rightCBEndsIf, lastElse);
	 *
	 */
	public StatementCast(Symbol castSymbol, List<CastRecord> castRecordList,
						StatementList castStatementList,
                        StatementList elseStatementList, Symbol rightCBEndsIf, Symbol lastElse) {
		this.castSymbol = castSymbol;
		this.castRecordList = castRecordList;
		this.castStatementList = castStatementList;
		this.elseStatementList = elseStatementList;
		this.rightCBEndsIf = rightCBEndsIf;
		this.lastElse = lastElse;
		this.shouldBeFollowedBySemicolon = false;
	}


	@Override
	public WrStatementCast getI() {
		if ( iStatementCast == null ) {
			iStatementCast = new WrStatementCast(this);
		}
		return iStatementCast;
	}

	private WrStatementCast iStatementCast = null;

	@Override
	public void accept(ASTVisitor visitor) {

		for ( CastRecord castRecord : castRecordList ) {
			if ( castRecord.typeInDec != null ) {
				castRecord.typeInDec.accept(visitor);
			}
			castRecord.localVar.accept(visitor);
			castRecord.expr.accept(visitor);
		}
		castStatementList.accept(visitor);

		if ( elseStatementList != null )
			elseStatementList.accept(visitor);

		visitor.visit(this);
	}


	/* (non-Javadoc)
	 * @see ast.Statement#genCyan(ast.PWInterface, boolean)
	 */
	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		printInMoreThanOneLine = true;
		pw.printIdent("cast ");
		int size = this.castRecordList.size();
		for ( CastRecord rec : this.castRecordList ) {
			if ( rec.typeInDec != null ) {
				rec.typeInDec.genCyanReal(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
				pw.print(" ");
			}
			pw.print(rec.localVar.getName() + " = ");
			rec.expr.genCyanReal(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			if ( --size > 0 ) {
				pw.print(", ");
			}
		}
		pw.println(" { ");
		pw.add();
		this.castStatementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.sub();
		pw.printlnIdent("}");
		if ( elseStatementList != null ) {
			if ( printInMoreThanOneLine )
				pw.printlnIdent("else {");
			else
				pw.print(" else {");
			pw.add();
			elseStatementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			pw.sub();
			if ( printInMoreThanOneLine )
				pw.printlnIdent("}");
			else
				pw.print(" }");
		}

	}


	@Override
	public Symbol getFirstSymbol() {
		return castSymbol;
	}



	@Override
	public void genJava(PWInterface pw, Env env) {
		/*
		 *   cast T1 v1 = e1, v2 = e2, T3 v3 = e3 {
                 S1
             }
             else {
                 S2
             }

			 {
                 T1 v1 = null; T2 v2 = null; T3 v3 = null;

                 tmp_e1 = e1.genJavaExpr(...);
                 if ( tmp_e1 != Nil ) {
                     v1 = tmp_e1._elem;
                     tmp_e2 = e2.genJavaExpr(...);
                     if ( tmp_e2 != Nil ) {
                         v2 = tmp_e2._elem;
                         tmp_e3 = e3.genJavaExpr(...);
                         if ( tmp_e3 != Nil ) {
                             v3 = tmp_e3._elem;
                         }
                     }
                 }
                 if ( v1 != null && v2 != null && v3 != null ) {
                     S1
                 else {
                     S2
                 }

			 }

		String boolTmp = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("boolean " + boolTmp + " = false;");
		 *
		 */

		pw.printlnIdent("{");
		pw.add();
		   //   T1 v1 = null; T2 v2 = null; T3 v3 = null;

		for ( CastRecord rec : this.castRecordList ) {
	        //String variableName = rec.localVar.getName();

	        String javaNameVar = rec.localVar.getJavaName(); // NameServer.getJavaName(variableName);
	        String javaTypeName = rec.localVar.getType().getJavaName();
	        if ( rec.localVar.getRefType() )
	            pw.printIdent("Ref<" + javaTypeName + ">");
	        else
	            pw.printIdent(javaTypeName);
	        pw.println(" " + javaNameVar + " = null;");

		}
		for ( CastRecord rec : this.castRecordList )  {

			/*
                 tmp_e1 = e1.genJavaExpr(...);
                 if ( tmp_e1 != Nil ) {
                     v1 = tmp_e1._elem;
			 *
			 */
			String tmpVarString = rec.expr.genJavaExpr(pw, env);

			pw.printlnIdent("if ( " + tmpVarString + "._elem != _Nil.prototype ) {");

			pw.add();
			//       v1 = tmp_e1._elem;

	        //String variableName = rec.localVar.getName();

	        String javaNameVar = rec.localVar.getJavaName(); // NameServer.getJavaName(variableName);
	        String javaTypeName = rec.localVar.getType().getJavaName();


	        // String tmpExpr = rec.expr.genJavaExpr(pw, env);

	        if ( rec.localVar.getRefType() ) {
	            pw.print(javaNameVar + " = new Ref<" + javaTypeName + ">();");
	        }

            if ( rec.localVar.getRefType() )
                javaNameVar = javaNameVar + ".elem";
            pw.printlnIdent(javaNameVar + " = (" + javaTypeName + " ) " + tmpVarString + "._elem;");


		}
		for ( CastRecord rec : this.castRecordList ) {
			pw.sub();
			pw.printlnIdent("}");
		}
		/*
                 if ( v1 != null && v2 != null && v3 != null ) {
                     S1
                 else {
                     S2
                 }
		 *
		 */
		pw.printIdent("if ( ");
		int size = castRecordList.size();
		for ( CastRecord rec : this.castRecordList ) {
	        //String variableName = rec.localVar.getName();

	        String javaNameVar = rec.localVar.getJavaName(); // NameServer.getJavaName(variableName);
			pw.print(javaNameVar + " != null ");
			if ( --size > 0 ) {
				pw.print("&& ");
			}

		}
		pw.println(" ) {");
		pw.add();
		this.castStatementList.genJava(pw, env);
		pw.sub();
		pw.printlnIdent("}");

		if ( elseStatementList != null ) {
			pw.printlnIdent("else {");
			pw.add();
			elseStatementList.genJava(pw, env);
			pw.sub();
			pw.printlnIdent("}");
		}

		pw.sub();
		pw.printlnIdent("}");
	}





	@Override
	public void calcInternalTypes(Env env) {
		/*
             cast T1 v1 = e1, v2 = e2, T3 v3 = e3 {
                 S1
             }
             else {
                 S2
             }
		 *
		 */

		env.addLexicalLevel();
		int numLocalVariables = env.numberOfLocalVariables();


 		for ( CastRecord rec : this.castRecordList ) {
 			// [ typeInDec ] localVar = expr


			try {
				env.pushCheckUsePossiblyNonInitializedPrototype(true);
	 			rec.expr.calcInternalTypes(env);
			}
			finally {
				env.popCheckUsePossiblyNonInitializedPrototype();
			}


			if ( ! (rec.expr.getType() instanceof ObjectDec) ) {
				env.error(rec.expr.getFirstSymbol(), "An expression of type union was expected. However, expression '"
						+ rec.expr.asString() + "' has type '" + rec.expr.getType().getFullName() + "'");
			}

			ObjectDec proto = (ObjectDec ) rec.expr.getType();
			if ( ! proto.getName().startsWith("Union<") ) {
				env.error(rec.expr.getFirstSymbol(), "An expression of type union was expected");
			}
			List<GenericParameter> gpList = proto.getGenericParameterListList().get(0);
			if ( gpList.size() != 2 ) {
				env.error(rec.expr.getFirstSymbol(), "An expression of type Union<T, Nil> or Union<Nil, T> was expected");
			}
			String first  = gpList.get(0).getName();
			String second = gpList.get(1).getName();

			boolean isTaggedUnion = (Character.isLowerCase(first.charAt(0)) && first.indexOf('.') < 0) ||
					(Character.isLowerCase(second.charAt(0)) && second.indexOf('.') < 0);
			if ( isTaggedUnion ) {
				env.error(rec.expr.getFirstSymbol(), "'cast' cannot be used with tagged unions");
			}


			boolean firstEqualsNil = first.equals("Nil");
			if ( !firstEqualsNil && !second.equals("Nil") ) {
				env.error(rec.expr.getFirstSymbol(), "An expression of type Union<T, Nil> or Union<Nil, T> was expected");
			}
 			Type exprcastType;
 			if ( firstEqualsNil ) {
 				exprcastType = gpList.get(1).getParameter().getType();
 			}
 			else {
 				exprcastType = gpList.get(0).getParameter().getType();
 			}


 			if ( rec.typeInDec != null ) {

 				try {
 					env.pushCheckUsePossiblyNonInitializedPrototype(false);
 	 				rec.typeInDec.calcInternalTypes(env);
 				}
 				finally {
 					env.popCheckUsePossiblyNonInitializedPrototype();
 				}

 				Type decTypeVar = rec.typeInDec.ifRepresentsTypeReturnsType(env);
 				if ( !exprcastType.isSupertypeOf(decTypeVar, env) ) {
 					String decTypeVarName = decTypeVar.getFullName();
 					env.error(rec.expr.getFirstSymbol(), "The type of this expression should be "
 							+ decTypeVarName + "|Nil or Nil|" + decTypeVarName );
 				}
// 				if ( exprcastType != decTypeVar ) {
// 					String decTypeVarName = decTypeVar.getFullName();
// 					env.error(rec.expr.getFirstSymbol(), "The type of this expression should be "
// 							+ decTypeVarName + "|Nil or Nil|" + decTypeVarName );
//
// 				}
 			}
 			rec.localVar.setType(exprcastType);

 			String nameVar = rec.localVar.getName();
 			VariableDecInterface otherVar = env.searchLocalVariableParameter(nameVar);
 			if ( otherVar != null ) {
 				env.error(this.getFirstSymbol(), "Variable '" + nameVar + "' is being redeclared. The other declaration is in line "
 						+ otherVar.getVariableSymbol().getLineNumber());
 			}

 			env.pushVariableDec(rec.localVar);
 			env.pushVariableAndLevel(rec.localVar, rec.localVar.getVariableSymbol().symbolString);
 	        env.addLocalVariableInfoToCurrentLexicalLevel(rec.localVar, rec.expr);

		}


		this.castStatementList.calcInternalTypes(env);

		List<List<LocalVarInfo>> localVarInfoListList = null;

		boolean statListWithNoVariableAssigned = false;
		List<LocalVarInfo> list;
		if ( ! this.castStatementList.alwaysReturn()  ) {
			list = env.getLocalVarInfoPreviousLevel();
			if (  ! castStatementList.isBreakLastStatement() || (!env.isEmptyRepetitionStatStack() && env.peekRepetitionStatStack() == 'r') ) {
				if ( list != null ) {
					localVarInfoListList = new ArrayList<>();
					localVarInfoListList.add(list);
				}
				else {
					statListWithNoVariableAssigned = true;
				}
			}
		}
		env.removeLocalVarInfoLastLevel();


		int numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

		env.popNumLocalVariableDec(numLocalVariablesToPop);

		env.removeVariablesLastLevel();
		//env.removeLocalVarInfoLastLevel();

		env.subLexicalLevel();

		if ( elseStatementList != null ) {
			elseStatementList.calcInternalTypes(env);
			numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

			env.popNumLocalVariableDec(numLocalVariablesToPop); //parameterList.size());

			if ( ! elseStatementList.alwaysReturn()  ) {
				list = env.getLocalVarInfoPreviousLevel();

				if (  ! elseStatementList.isBreakLastStatement() || (!env.isEmptyRepetitionStatStack() && env.peekRepetitionStatStack() == 'r')  ) {
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

			if ( !statListWithNoVariableAssigned && localVarInfoListList != null ) {
				env.setLocalVarInitializedThisLevel(localVarInfoListList);
			}



		}
		super.calcInternalTypes(env);

	}

	public StatementList getElseStatementList() {
		return elseStatementList;
	}


	@Override
	public boolean alwaysReturn() {
		if ( elseStatementList == null )
			//  without 'else', may not return
			return false;
		else {
			if ( ! this.castStatementList.alwaysReturn() ) {
				return false;
			}
			return elseStatementList.alwaysReturn();
		}

	}

	public boolean alwaysReturnFromFunction() {
		if ( elseStatementList == null )
			//  without 'else', may not return
			return false;
		else {
			if ( ! this.castStatementList.alwaysReturnFromFunction() ) {
				return false;
			}
			return elseStatementList.alwaysReturnFromFunction();
		}

	}


	@Override
	public boolean statementDoReturn() {
		return alwaysReturnFromFunction();
	}


	public Symbol getRightCBEndsIf() {
		return rightCBEndsIf;
	}

	public Symbol getLastElse() {
		return lastElse;
	}


	public List<CastRecord> getCastRecordList() {
		return castRecordList;
	}

	public StatementList getCastStatementList() {
		return castStatementList;
	}


	private List<CastRecord> castRecordList;
	/**
	 * the symbol 'cast'
	 */
	private Symbol castSymbol;
	/**
	 * list of statements to be executed if the expressions are not Nil
	 */
	private StatementList castStatementList;

	/**
	 * list of else statements
	 */
	private StatementList  elseStatementList;

	/**
	 * the '}' symbol that ends an cast
	 */
	private Symbol rightCBEndsIf;
	/**
	 * the last 'else' symbol of an 'if' statement. Of null if none
	 */
	private Symbol lastElse;

}
