package ast;

import lexer.Symbol;
import meta.WrStatement;
import meta.WrStatementThrow;
import saci.CyanEnv;
import saci.Env;

public class StatementThrow extends Statement {


	public StatementThrow(Symbol throwSymbol, Expr expr, MethodDec currentMethod) {
		super(currentMethod);
		this.throwSymbol = throwSymbol;
		this.expr = expr;
	}

	@Override
	public void calcInternalTypes(Env env) {
		try {
			env.pushCheckUsePossiblyNonInitializedPrototype(true);
			expr.calcInternalTypes(env);
			Prototype cyException = env.getCyException();
			if ( !cyException.isSupertypeOf(expr.getType(), env) ) {
				env.error(expr.getFirstSymbol(), "This expression should be subtype of CyException");
			}
		}
		finally {
			env.popCheckUsePossiblyNonInitializedPrototype();
		}
	}



	@Override
	public void accept(ASTVisitor visitor) {
		this.expr.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public WrStatement getI() {
		if ( iStatementThrow == null ) {
			iStatementThrow = new WrStatementThrow(this);
		}
		return iStatementThrow;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
		pw.print("throw ");
		expr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions );
	}

	@Override
	public Symbol getFirstSymbol() {
		return this.throwSymbol;
	}

	@Override
	public boolean addSemicolonJavaCode() {
		return true;
	}

	@Override
	public boolean demandSemicolon() { return true; }

	@Override
	public void genJava(PWInterface pw, Env env) {
		String tmpVar = expr.genJavaExpr(pw, env);
		pw.printIdent("throw new ExceptionContainer__(" + tmpVar +")");
	}

	public Expr getExpr() {
		return expr;
	}

	private Expr expr;
	private WrStatementThrow iStatementThrow;
	private Symbol throwSymbol;

}
