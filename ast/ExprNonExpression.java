/**
 *
 */
package ast;

import lexer.Symbol;
import meta.WrExpr;
import saci.CyanEnv;
import saci.Env;

/** represents a non-expression. Used only when the Compiler finds an error
 * during the parsing of an expression. In this case, the Compiler may
 * return an object of ExprNonExpression instead of just null
 *
 * @author José
 *
 */
public class ExprNonExpression extends Expr {


	public ExprNonExpression(MethodDec method) {
		super(method);
	}


	@Override
	public WrExpr getI() {
		return null;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.println("Internal error: this should not have been printed. Look at ExprNonExpression class of package ast of the Compiler");

	}


	@Override
	public void accept(ASTVisitor visitor) {

	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		return "";
	}


	@Override
	public Symbol getFirstSymbol() {
		return null;
	}


	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);

	}




}
