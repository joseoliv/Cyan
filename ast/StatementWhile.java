/**
 *
 */
package ast;

import java.lang.reflect.Field;
import lexer.Symbol;
import meta.BreakException;
import meta.WrStatementWhile;
import saci.CyanEnv;
import saci.Env;

/**
 * represents a while statement
 * @author José
 *
 */
public class StatementWhile extends Statement {

	public StatementWhile(Symbol whileSymbol, Expr booleanExpr,
			   StatementList statementList, Symbol rightCBEndsIf,
			   MethodDec method) {
		super(method);
		this.whileSymbol = whileSymbol;
		this.booleanExpr = booleanExpr;
		this.statementList = statementList;
		this.rightCBEndsIf = rightCBEndsIf;
		this.shouldBeFollowedBySemicolon = false;
	}

	@Override
	public WrStatementWhile getI() {
		if ( iStatementWhile == null ) {
			iStatementWhile = new WrStatementWhile(this);
		}
		return iStatementWhile;
	}

	private WrStatementWhile iStatementWhile = null;

	@Override
	public void accept(ASTVisitor visitor) {
		this.booleanExpr.accept(visitor);
		this.statementList.accept(visitor);
		visitor.visit(this);
	}


	@Override
	public Object eval(EvalEnv ee) {

		ee.incLoop();
		try {
			while ( true ) {
				Object t = booleanExpr.eval(ee);
				if ( t == null ) { return null; }
//				if ( t.getClass() != ee.getCyBoolean() ) {
//					ee.error(booleanExpr.getFirstSymbol(), "A Boolean expression was expected");
//				}
				try {
//					Field f;
//					f = t.getClass().getField("b");
//					if ( (Boolean ) f.get(t) ) {
//						ee.pushLexicalLevel();
//						try {
//							statementList.eval(ee);
//						}
//						finally {
//							ee.popLexicalLevel();
//						}
//					}
//					else {
//						break;
//					}

					//***************

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
						ee.pushLexicalLevel();
						try {
							statementList.eval(ee);
						}
						finally {
							ee.popLexicalLevel();
						}
					}
					else {
						break;
					}

					// ****************
				}
				catch (SecurityException | IllegalArgumentException e) {
					ee.error(booleanExpr.getFirstSymbol(), "Internal error in Boolean expression");
				}
			}

		}
		catch ( BreakException e ) {
			// a break exception was throw, return from loop
		}

		ee.decLoop();
		return null;
	}

	/* (non-Javadoc)
	 * @see ast.Statement#genCyan(ast.PWInterface, boolean)
	 */
	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		pw.print("while ");
		booleanExpr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions );
		if ( printInMoreThanOneLine )
		    pw.println(" {");
		else
			pw.print(" { ");
		pw.add();
		if ( statementList != null )
		    statementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.sub();
		if ( printInMoreThanOneLine )
		    pw.printIdent("}");
		else
			pw.print(" }");
	}

	@Override
	public Symbol getFirstSymbol() {
		return whileSymbol;
	}


	@Override
	public void genJava(PWInterface pw, Env env) {

		pw.printlnIdent("while ( true ) { ");
		pw.add();
	    String tmpVarString = booleanExpr.genJavaExpr(pw, env);

		if ( booleanExpr.getType() == Type.Dyn ) {
			// add convertion from Dyn
			pw.printlnIdent("if ( !(" + tmpVarString + " instanceof CyBoolean) ) {");
			pw.add();
			pw.printlnIdent("throw new ExceptionContainer__("
					+ env.javaCodeForCastException(booleanExpr, Type.Boolean) + " );");

			pw.sub();
			pw.println("}");
			pw.printlnIdent("if ( ! ((CyBoolean ) " + tmpVarString + ").b ) break;");
		}
		else {


			if ( booleanExpr.getType().getInsideType() instanceof Prototype ) {
		        pw.printlnIdent("if ( !" + tmpVarString + ".b ) break;");
			}
			else if ( booleanExpr.getType() instanceof TypeJavaRef ) {
		        pw.printlnIdent("if ( !" + tmpVarString + " ) break;");
			}

		}



 		if ( statementList != null )
		    statementList.genJava(pw, env);
		pw.sub();
		pw.printlnIdent("}");
	}


	@Override
	public void calcInternalTypes(Env env) {
		try {

			env.pushRepetitionStatStack('w');
			env.pushControlFlowStack(this);

			try {
				env.pushCheckUsePossiblyNonInitializedPrototype(true);
				booleanExpr.calcInternalTypes(env);
			}
			finally {
				env.popCheckUsePossiblyNonInitializedPrototype();
			}

			if ( booleanExpr.getType() != Type.Boolean && booleanExpr.getType() != Type.Dyn ) {

				if ( booleanExpr.getType() instanceof TypeJavaClass ) {
					TypeJavaRef javaClass = (TypeJavaRef ) booleanExpr.getType();
					if ( !javaClass.getName().equals("boolean") && !javaClass.getName().equals("Boolean") ) {
						env.error(booleanExpr.getFirstSymbol(), "A boolean or Dyn expression is expected in a 'while' statement");
					}
				}
				else {
					env.error(booleanExpr.getFirstSymbol(), "A boolean or Dyn expression is expected in a 'while' statement");
				}

			}
			if ( booleanExpr instanceof ExprWithParenthesis ) {
				env.warning(booleanExpr.getFirstSymbol(), "Parentheses are not necessary around the boolean expression of command 'while'");
			}


			int numLocalVariables = env.numberOfLocalVariables();

			statementList.calcInternalTypes(env);

			int numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

			env.popNumLocalVariableDec(numLocalVariablesToPop); //parameterList.size());

			env.removeLocalVarInfoLastLevel();

			super.calcInternalTypes(env);


		}
		finally {
			env.popRepetitionStatStack();
			env.popControlFlowStack();
		}
	}

	public StatementList getStatementList() {
		return statementList;
	}

	public Symbol getRightCBEndsIf() {
		return rightCBEndsIf;
	}

	public Expr getBooleanExpr() {
		return booleanExpr;
	}


	/**
	 * the symbol 'while'
	 */
	private Symbol whileSymbol;
	/**
	 * boolean expression of the while
	 */
	private Expr booleanExpr;
	/**
	 * list of statements
	 */
	private StatementList statementList;

	/**
	 * the '}' symbol that ends a while
	 */
	private Symbol rightCBEndsIf;


}
