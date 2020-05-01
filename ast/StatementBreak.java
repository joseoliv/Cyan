package ast;

import lexer.Symbol;
import meta.WrStatementBreak;
import saci.CyanEnv;
import saci.Env;

/**
 *  represents a 'break' statement
   @author jose
 */
public class StatementBreak extends Statement {

	public StatementBreak(Symbol breakSymbol, MethodDec method) {
		super(method);
		this.breakSymbol = breakSymbol;
	}
	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		if ( printInMoreThanOneLine )
			pw.printlnIdent("break;");
		else
			pw.printIdent("break;");
	}


	@Override
	public WrStatementBreak getI() {
		if ( iStatementBreak == null ) {
			iStatementBreak = new WrStatementBreak(this);
		}
		return iStatementBreak;
	}

	private WrStatementBreak iStatementBreak = null;

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Object eval(EvalEnv ee) {
		if ( ee.getCountLoop() <= 0 ) {
			ee.error(this.getFirstSymbol(), "'break' outside a 'while', 'for', or 'repeat' statement");
		}
		else {
			throw new meta.BreakException();
		}
		return null;
	}

	@Override
	public Symbol getFirstSymbol() {
		return breakSymbol;
	}

	@Override
	public void genJava(PWInterface pw, Env env) {
		pw.printlnIdent("break;");
	}

	public Symbol getBreakSymbol() {
		return breakSymbol;
	}

	@Override
	public boolean demandSemicolon() { return false; }

	private Symbol breakSymbol;

}
