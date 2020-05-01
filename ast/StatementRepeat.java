package ast;

import java.lang.reflect.Field;
import java.util.List;
import lexer.Symbol;
import meta.BreakException;
import meta.LocalVarInfo;
import meta.WrStatementRepeat;
import saci.CyanEnv;
import saci.Env;

public class StatementRepeat extends Statement {

	public StatementRepeat(Symbol repeatSymbol, Expr booleanExpr,
			   StatementList statementList, Symbol untilSymbol, MethodDec method) {
		super(method);
		this.repeatSymbol = repeatSymbol;
		this.booleanExpr = booleanExpr;
		this.statementList = statementList;
		this.untilSymbol = untilSymbol;
		this.shouldBeFollowedBySemicolon = false;
	}


	@Override
	public WrStatementRepeat getI() {
		if ( iStatementRepeat == null ) {
			iStatementRepeat = new WrStatementRepeat(this);
		}
		return iStatementRepeat;
	}

	private WrStatementRepeat iStatementRepeat = null;

	@Override
	public void accept(ASTVisitor visitor) {
		this.booleanExpr.accept(visitor);
		this.statementList.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public Object eval(EvalEnv ee) {

		ee.incLoop();
		while ( true ) {
			ee.pushLexicalLevel();
			try {
				statementList.eval(ee);
			}
			catch ( BreakException e ) {
				// a break exception was throw, return from loop
				ee.decLoop();
				return null;
			}
			finally {
				ee.popLexicalLevel();
			}
			Object t = booleanExpr.eval(ee);
			if ( t == null ) { return null; }
//			if ( t.getClass() != ee.getCyBoolean() ) {
//				ee.error(booleanExpr.getFirstSymbol(), "A Boolean expression was expected");
//			}
//			try {
//				f = t.getClass().getField("b");
//				if ( (Boolean ) f.get(t) ) {
//					break;
//				}
//			}
//			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//				ee.error(booleanExpr.getFirstSymbol(), "Internal error in Boolean expression");
//			}

			boolean exprValue;
			if ( t.getClass() == ee.getCyBoolean() ) {
				try {
					Field f;
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
				break;
			}


		}
		ee.decLoop();
		return null;
	}


	/* (non-Javadoc)
	 * @see ast.Statement#genCyan(ast.PWInterface, boolean)
	 */
	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		pw.print("repeat");
		if ( printInMoreThanOneLine )
		    pw.println("");
		pw.add();
		if ( statementList != null )
		    statementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.sub();
		if ( printInMoreThanOneLine )
		    pw.printIdent("until ");
		else
			pw.print("until ");
		booleanExpr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions );
	}

	@Override
	public Symbol getFirstSymbol() {
		return repeatSymbol;
	}


	@Override
	public void genJava(PWInterface pw, Env env) {

		pw.printlnIdent("while ( true ) { ");
		pw.add();
 		if ( statementList != null )
		    statementList.genJava(pw, env);

	    String tmpVarString = booleanExpr.genJavaExpr(pw, env);

		if ( booleanExpr.getType() == Type.Dyn ) {
			// add convertion from Dyn
			pw.printlnIdent("if ( !(" + tmpVarString + " instanceof CyBoolean) ) {");
			pw.add();
			pw.printlnIdent("throw new ExceptionContainer__("
					+ env.javaCodeForCastException(booleanExpr, Type.Boolean) + " );");

			pw.sub();
			pw.println("}");
			pw.printlnIdent("if ( ((CyBoolean ) " + tmpVarString + ").b ) break;");
		}
		else {


			if ( booleanExpr.getType().getInsideType() instanceof Prototype ) {
		        pw.printlnIdent("if ( " + tmpVarString + ".b ) break;");
			}
			else if ( booleanExpr.getType() instanceof TypeJavaRef ) {
		        pw.printlnIdent("if ( " + tmpVarString + " ) break;");
			}

		}



		pw.sub();
		pw.printlnIdent("}");
	}


	@Override
	public void calcInternalTypes(Env env) {
		try {

			env.pushRepetitionStatStack('r');


			int numLocalVariables = env.numberOfLocalVariables();

			statementList.calcInternalTypes(env);

			List<LocalVarInfo> list = env.getLocalVarInfoPreviousLevel();



			int numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

			env.popNumLocalVariableDec(numLocalVariablesToPop); //parameterList.size());


			env.removeLocalVarInfoLastLevel();

			if ( list != null && list.size() > 0 ) {
				env.transferLocalVarInitializedPreviousLevel(list);
			}

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
						env.error(booleanExpr.getFirstSymbol(), "A boolean or Dyn expression is expected in a 'repeat-until' statement");
					}
				}
				else {
					env.error(booleanExpr.getFirstSymbol(), "A boolean or Dyn expression is expected in a 'repeat-until' statement");
				}



			}
			if ( booleanExpr instanceof ExprWithParenthesis ) {
				env.warning(booleanExpr.getFirstSymbol(), "Parentheses are not necessary around the boolean expression of command 'repeat-until'");
			}


			super.calcInternalTypes(env);
		}
		finally {
			env.popRepetitionStatStack();
		}

	}

	public StatementList getStatementList() {
		return statementList;
	}

	public Symbol getUntilSymbol() {
		return untilSymbol;
	}

	public Expr getBooleanExpr() {
		return booleanExpr;
	}


	/**
	 * the symbol 'while'
	 */
	private Symbol repeatSymbol;
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
	private Symbol untilSymbol;


}
