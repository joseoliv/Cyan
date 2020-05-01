/**
 *
 */
package ast;

import lexer.Symbol;
import meta.WrExprWithParenthesis;
import saci.CyanEnv;
import saci.Env;

/** Represents an expression surrounded by parenthesis such as
 *
 *   (a + 1)
 *   stack push (person getAge);
 *
 * @author José
 *
 */
public class ExprWithParenthesis extends Expr {

	/**
	 *
	 */
	public ExprWithParenthesis( Symbol leftParSymbol, Expr expr,
			                    Symbol rightParSymbol, MethodDec method) {
		super(method);
		this.leftParSymbol = leftParSymbol;
		this.expr = expr;
		this.rightParSymbol = rightParSymbol;
	}

	@Override
	public WrExprWithParenthesis getI() {
		return new WrExprWithParenthesis(this);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		expr.accept(visitor);
	}

	@Override
	public Object eval(EvalEnv ee) { return expr.eval(ee); }

	@Override
	public boolean mayBeStatement() {
		return expr.mayBeStatement();
	}


	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	public Expr getExpr() {
		return expr;
	}

	public void setLeftParSymbol(Symbol leftParSymbol) {
		this.leftParSymbol = leftParSymbol;
	}

	public Symbol getLeftParSymbol() {
		return leftParSymbol;
	}

	public void setRightParSymbol(Symbol rightParSymbol) {
		this.rightParSymbol = rightParSymbol;
	}

	public Symbol getRightParSymbol() {
		return rightParSymbol;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.print("(");
		expr.genCyan(pw, false, cyanEnv, genFunctions);
		pw.print(")");
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String s = expr.genJavaExpr(pw, env);
		return s;
	}

	/*public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		pw.print("( ");
		expr.genJavaExprWithoutTmpVar(pw, env);
		pw.print(" )");
	}
	*/

	@Override
	public Symbol getFirstSymbol() {
		return leftParSymbol;
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

		type = expr.getType(env);
		super.calcInternalTypes(env);
	}



	@Override
	public boolean warnIfStatement() {
		return expr.warnIfStatement();
	}


	private Expr expr;
	private Symbol leftParSymbol, rightParSymbol;

}
