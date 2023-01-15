/**
 *
 */
package ast;

import lexer.Symbol;
import meta.WrStatementNull;
import saci.CyanEnv;
import saci.Env;

/** represents a null statement, one in which there is just the ";" as in
 * the second line of the following example:
 *         :a Int = 0;
 *         ;
 *         ++a;
 *
 * @author José
 *
 */
public class StatementNull extends Statement {

	public StatementNull(Symbol firstSymbol, MethodDec method) {
		super(method);
		this.firstSymbol = firstSymbol;
	}



	@Override
	public WrStatementNull getI() {
		if ( iStatementNull == null ) {
			iStatementNull = new WrStatementNull(this);
		}
		return iStatementNull;
	}

	private WrStatementNull iStatementNull = null;

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}


	/* (non-Javadoc)
	 * @see ast.Statement#genCyan(ast.PWInterface, boolean)
	 */
	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		if ( printInMoreThanOneLine )
			pw.println(";");

	}

	/* (non-Javadoc)
	 * @see ast.Statement#getFirstSymbol()
	 */
	@Override
	public Symbol getFirstSymbol() {
		return firstSymbol;
	}

	/* (non-Javadoc)
	 * @see ast.Statement#genJava(ast.PWInterface, saci.Env)
	 */
	@Override
	public void genJava(PWInterface pw, Env env) {
		pw.println(";");

	}


	@Override
	public boolean demandSemicolon() { return false; }


	private Symbol firstSymbol;
}
