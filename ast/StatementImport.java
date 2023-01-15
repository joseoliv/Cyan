package ast;

import lexer.Symbol;
import meta.WrStatementImport;
import saci.CyanEnv;
import saci.Env;

/**
 * to be used only in interpreting Cyan code. It is not part of the Cyan AST
   @author jose
 */
public class StatementImport extends Statement {

	public StatementImport(ExprIdentStar importId, MethodDec method) {
		super(method);
		this.importId = importId;
	}

	@Override
	public void accept(ASTVisitor visitor) {
	}


	@Override
	public Object eval(EvalEnv ee) {
		ee.importPackage( importId.asString() );
		return null;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
	}

	@Override
	public Symbol getFirstSymbol() {
		return importId.getFirstSymbol();
	}

	@Override
	public void genJava(PWInterface pw, Env env) {
	}

	@Override
	public WrStatementImport getI() {
		if ( this.iStatementImport == null ) {
			iStatementImport = new WrStatementImport(this);
		}
		return this.iStatementImport;
	}

	@Override
	public boolean demandSemicolon() { return false; }


	WrStatementImport iStatementImport = null;
	ExprIdentStar importId;
}
