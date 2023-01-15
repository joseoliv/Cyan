/**
 *
 */
package ast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.LocalVarInfo;
import meta.WrStatementIf;
import saci.CyanEnv;
import saci.Env;

/**
 * represents an if statement. Its grammar is
 *
    IfStat} ::= ``if"\/ ``("\/ Expr ``)"\/ \{ StatementList \}   \\
\rr \{ ``else"\/ ``if"\/ ``("\/ Expr ``)"\/ \{ StatementList \}VoidVoid  \}\\
\rr [ ``else"\/ \{ StatementList \} ]

 * @author José
 *
 */
public class StatementIf extends Statement {

    // 	return new StatementIf(ifSymbol, ifExprList, ifStatementList, elseStatementList);

	public StatementIf(Symbol ifSymbol, List<Expr> ifExprList,
			           List<StatementList> ifStatementList,
                       StatementList elseStatementList, Symbol rightCBEndsIf,
                       Symbol lastElse, MethodDec method) {
		super(method);
		this.ifSymbol = ifSymbol;
		this.ifExprList = ifExprList;
		this.ifStatementList = ifStatementList;
		this.elseStatementList = elseStatementList;
		this.rightCBEndsIf = rightCBEndsIf;
		this.lastElse = lastElse;
		this.shouldBeFollowedBySemicolon = false;

	}


	@Override
	public WrStatementIf getI() {
		if ( iStatementIf == null ) {
			iStatementIf = new WrStatementIf(this);
		}
		return iStatementIf;
	}

	private WrStatementIf iStatementIf = null;

	@Override
	public void accept(ASTVisitor visitor) {

		for ( Expr ifExpr : this.ifExprList ) {
			ifExpr.accept(visitor);
		}
		for ( StatementList statList : this.ifStatementList ) {
			statList.accept(visitor);
		}
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
		int size = this.ifExprList.size();
		for (int i = 0; i < size; i++) {
			Expr booleanExpr = ifExprList.get(i);
			if ( i > 0 )
				pw.print("if ");
			else
				pw.printIdent("if ");
			booleanExpr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions );
			if ( printInMoreThanOneLine )
				pw.println(" {");
			else
				pw.print(" { ");
			pw.add();
			StatementList thenStatementList = this.ifStatementList.get(i);
			thenStatementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
			pw.sub();
			if ( printInMoreThanOneLine ) {
				if ( elseStatementList != null )
					pw.printlnIdent("}");
				else
					pw.printIdent("}");
			}
			else
				pw.print(" } ");
			if ( i < size - 1 )
				pw.printIdent("else ");
		}

		if ( elseStatementList != null ) {
			if ( printInMoreThanOneLine )
				pw.printlnIdent("else {");
			else
				pw.print(" else {");
			pw.add();
			if ( elseStatementList != null )
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
		return ifSymbol;
	}


	static int count = 0;

	@Override
	public void genJava(PWInterface pw, Env env) {
		int size = this.ifExprList.size();
		for (int i = 0; i < size; i++) {
			Expr booleanExpr = ifExprList.get(i);


			String tmpVarString = booleanExpr.genJavaExpr(pw, env);
			if ( booleanExpr.getType() == Type.Dyn ) {
				// add convertion from Dyn
				pw.printlnIdent("if ( !(" + tmpVarString + " instanceof CyBoolean) ) {");
				pw.add();

				pw.printlnIdent("throw new ExceptionContainer__("
						+ env.javaCodeForCastException(booleanExpr, Type.Boolean) + " );");

				pw.sub();
				pw.printlnIdent("}");
				pw.printlnIdent("if ( ((CyBoolean ) " + tmpVarString + ").b ) {");
			}
			else {
				if ( booleanExpr.getType().getInsideType() instanceof Prototype ) {
					pw.printlnIdent("if ( " + tmpVarString + ".b ) {");
				}
				else if ( booleanExpr.getType() instanceof TypeJavaRef ) {
					pw.printlnIdent("if ( " + tmpVarString + " ) {");
				}
			}
			pw.add();
			StatementList thenStatementList = this.ifStatementList.get(i);

			int localident = pw.getCurrentIndent();
			thenStatementList.genJava(pw, env);
			pw.set(localident);

			pw.sub();
			pw.printlnIdent("}");
			if ( i < size - 1 ) {
				pw.printlnIdent("else {");
				pw.add();
			}

		}

		if ( elseStatementList != null ) {
			pw.printlnIdent("else {");
			pw.add();
			elseStatementList.genJava(pw, env);
			pw.sub();
			pw.printlnIdent("}");
		}
		pw.sub();
		for ( int i = 0; i < size - 1; ++i ) {
			pw.printlnIdent("}");
			pw.sub();
		}
		pw.add();
		pw.printlnIdent("// end of if");
	}



	public List<Expr> getIfExprList() {
		return ifExprList;
	}

	public void setElseIfExprList(List<Expr> elseIfExprList) {
		this.ifExprList = elseIfExprList;
	}

	public List<StatementList> getElseIfStatementList() {
		return ifStatementList;
	}

	public void setElseIfStatementList(List<StatementList> elseIfStatementList) {
		this.ifStatementList = elseIfStatementList;
	}

	@Override
	public void calcInternalTypes(Env env) {


		for ( Expr e : ifExprList ) {
			try {
				env.pushCheckUsePossiblyNonInitializedPrototype(true);
				e.calcInternalTypes(env);
			}
			finally {
				env.popCheckUsePossiblyNonInitializedPrototype();
			}

			Type ifExprType = e.getType();
			if ( ifExprType != Type.Boolean && ifExprType != Type.Dyn ) {
				if ( ifExprType instanceof TypeJavaClass ) {
					TypeJavaRef javaClass = (TypeJavaRef ) ifExprType;
					if ( !javaClass.getName().equals("boolean") && !javaClass.getName().equals("Boolean") ) {
						env.error(e.getFirstSymbol(), "A Boolean or Dyn expression is expected in an 'if' statement");
					}
				}
				else {
					env.error(e.getFirstSymbol(), "A Boolean or Dyn expression is expected in an 'if' statement");
				}
			}

			if ( e instanceof ExprWithParenthesis ) {
				env.warning(e.getFirstSymbol(), "Parentheses are not necessary around the boolean expression of command 'if'");
			}

		}

		boolean breakIsLastStatement = false;
		boolean statListWithNoVariableAssigned = false;
		List<List<LocalVarInfo>> localVarInfoListList = null;
		for ( StatementList statement : ifStatementList ) {
			int numLocalVariables = env.numberOfLocalVariables();
			statement.calcInternalTypes(env);
			breakIsLastStatement = statement.isBreakLastStatement();
			if ( ! statement.alwaysReturn(env) ) {

				List<LocalVarInfo> list = env.getLocalVarInfoPreviousLevel();

				if ( ! breakIsLastStatement || (!env.isEmptyRepetitionStatStack() && env.peekRepetitionStatStack() == 'r') ) {
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


			int numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

			env.popNumLocalVariableDec(numLocalVariablesToPop); //parameterList.size());
		}


		int numLocalVariables = env.numberOfLocalVariables();

		if ( elseStatementList != null ) {
			elseStatementList.calcInternalTypes(env);
			int numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

			env.popNumLocalVariableDec(numLocalVariablesToPop); //parameterList.size());

			breakIsLastStatement = elseStatementList.isBreakLastStatement();

			if ( !this.elseStatementList.alwaysReturn(env) ) {


				List<LocalVarInfo> list = env.getLocalVarInfoPreviousLevel();
				if (  ! breakIsLastStatement || (!env.isEmptyRepetitionStatStack() && env.peekRepetitionStatStack() == 'r') ) {
					if ( list != null ) {
						if ( localVarInfoListList == null ) {
							localVarInfoListList = new ArrayList<>();
						}
						localVarInfoListList.add(list);

						if ( breakIsLastStatement ) {
							for ( LocalVarInfo localVarInfo : list ) {
								localVarInfo.breakIsLastStatement = true;
							}
						}

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
	public boolean alwaysReturn(Env env) {
		if ( elseStatementList == null )
			//  without 'else', may not return
			return false;
		else {
			// one or more 'if's. if one of the lists do not return, then do not return
			for ( StatementList statementList : ifStatementList ) {
				if ( ! statementList.alwaysReturn(env) ) {
					return false;
				}
			}
			return elseStatementList.alwaysReturn(env);
		}

	}

	@Override
	public boolean alwaysBreak(Env env) {
		if ( elseStatementList == null )
			return false;
		else {
			// one or more 'if's. if one of the lists do not 'break', then do not return
			for ( StatementList statementList : ifStatementList ) {
				if ( ! statementList.alwaysBreak(env) ) {
					return false;
				}
			}
			return elseStatementList.alwaysBreak(env);
		}


	}

	public boolean alwaysReturnFromFunction() {
		if ( elseStatementList == null )
			//  without 'else', may not return
			return false;
		else {
			// one or more 'if's. if one of the lists do not return, then do not return
			for ( StatementList statementList : ifStatementList ) {
				if ( ! statementList.alwaysReturnFromFunction() ) {
					return false;
				}
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

	@Override
	public Object eval(EvalEnv ee) {

		boolean oneWasTrue = false;
		int size = this.ifExprList.size();
		for (int i = 0; i < size; i++) {
			Expr booleanExpr = ifExprList.get(i);
			Object t = booleanExpr.eval(ee);
			if ( t == null ) { return null; }
			boolean exprValue;
			if ( t.getClass() == ee.getCyBoolean() ) {
				Field f;
				try {
					f = t.getClass().getField("b");
					exprValue = (Boolean ) f.get(t);
				}
				catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					ee.error(booleanExpr.getFirstSymbol(), "Internal error in Boolean expression");
					return null;
				}
			}
			else if ( t instanceof Boolean ) {
				exprValue = (Boolean ) t;
			}
			else {
				ee.error(booleanExpr.getFirstSymbol(), "A Boolean expression was expected");
				return null;
			}
			if ( exprValue ) {
				oneWasTrue = true;

				ee.pushLexicalLevel();
				try {
					this.ifStatementList.get(i).eval(ee);
				}
				finally {
					ee.popLexicalLevel();
				}
				return null;
			}

		}

		if ( elseStatementList != null && ! oneWasTrue ) {
			if ( elseStatementList != null ) {
				ee.pushLexicalLevel();
				elseStatementList.eval(ee);
				ee.popLexicalLevel();
			}
		}


		return null;
	}


	public List<StatementList> getIfStatementList() {
		return ifStatementList;
	}


	/**
	 * the symbol 'if'
	 */
	private Symbol ifSymbol;
	/**
	 * list of else statements
	 */
	private StatementList  elseStatementList;
	/**
	 * list of "else if" boolean expressions
	 */
	private List<Expr> ifExprList;
	/**
	 * list of "else if" statements
	 */
	private List<StatementList> ifStatementList;
	/**
	 * the '}' symbol that ends an if
	 */
	private Symbol rightCBEndsIf;
	/**
	 * the last 'else' symbol of an 'if' statement. Of null if none
	 */
	private Symbol lastElse;

	public Symbol getLastElse() {
		return lastElse;
	}



}
