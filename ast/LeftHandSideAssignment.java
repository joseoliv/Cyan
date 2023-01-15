package ast;

import saci.Env;

public interface LeftHandSideAssignment {
	/**
	 * generate code for the expression but without assigning it to
	 * a temporary variable. This method should be redefined for
	 * expressions that may appear at the left hand side of
	 * an expression such as ExprIdent and ExprIndexed. Or expressions
	 * that represent types, packages, and so on.
	 * @return
	 */

	void genJavaCodeVariable(PWInterface pw, Env env);
}
