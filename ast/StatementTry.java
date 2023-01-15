package ast;

import java.util.List;
import lexer.Symbol;
import meta.WrStatement;
import meta.WrStatementTry;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

public class StatementTry extends Statement {


	public StatementTry(Symbol trySymbol, StatementList statementList,
			List<Expr> catchExprList,
			StatementList finallyStatementList, MethodDec currentMethod) {
		super(currentMethod);
		this.trySymbol = trySymbol;
		this.statementList = statementList;
		this.catchExprList = catchExprList;
		this.finallyStatementList = finallyStatementList;
		if ( this.finallyStatementList != null ) {
			// no need of ';' after the try-catch-finally statement
			this.setShouldBeFollowedBySemicolon(false);
		}
	}

	@Override
	public boolean alwaysReturn(Env env) {

		if ( this.finallyStatementList != null ) {
			return this.finallyStatementList.alwaysReturn(env);
		}
		else {
			return this.statementList.alwaysReturn(env);
		}
    }

	@Override
	public boolean alwaysBreak(Env env) {

		if ( this.finallyStatementList != null ) {
			return this.finallyStatementList.alwaysBreak(env);
		}
		else {
			return this.statementList.alwaysBreak(env);
		}
    }



	@Override
	public void calcInternalTypes(Env env) {

		try {
			env.pushRepetitionStatStack('t');
			int numLocalVariables;
			int numLocalVariablesToPop;
			try {
				env.pushControlFlowStack(this);

				numLocalVariables = env.numberOfLocalVariables();

				statementList.calcInternalTypes(env);

				numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

				env.popNumLocalVariableDec(numLocalVariablesToPop); //parameterList.size());

				env.removeLocalVarInfoLastLevel();

				int i = 0;
				for ( Expr e : catchExprList ) {
					try {
						env.pushCheckUsePossiblyNonInitializedPrototype(true);
						e.calcInternalTypes(env);

						final Type t = e.getType();
						List<MethodSignature> emsList = null;

						emsList = t.searchMethodPublicPackageSuperPublicPackage("eval:1", env);
						checkCatchParameter(env, i, emsList, e.getFirstSymbol());

					}
					finally {
						env.popCheckUsePossiblyNonInitializedPrototype();
					}
					++i;
				}

			}
			finally {
				env.popControlFlowStack();
			}

			if ( this.finallyStatementList != null && this.finallyStatementList.getStatementList().size() > 0 ) {
				numLocalVariables = env.numberOfLocalVariables();

				finallyStatementList.calcInternalTypes(env);

				numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

				env.popNumLocalVariableDec(numLocalVariablesToPop); //parameterList.size());

				env.removeLocalVarInfoLastLevel();

			}

			super.calcInternalTypes(env);
		}
		finally {
			env.popRepetitionStatStack();
		}
	}


	private static void checkCatchParameter(Env env, int i, List<MethodSignature> emsList,
			Symbol errSymbol) {
		if ( emsList == null || emsList.size() == 0 ) {
			/*
			 * each parameter to a catch: keyword should have at least one 'eval:' method
			 */
			env.error(errSymbol, "This expression has a type that does not define an 'eval:' method");
			return ;
		}
		else {
			for ( final MethodSignature ems : emsList ) {
				final ParameterDec param = ems.getParameterList().get(0);
				/*
				 * each 'eval:' method should accept one parameter whose type is sub-prototype of CyException
				 */
				final Type paramType = param.getType();

				if ( paramType == null ) {
					param.getType();
				}

				final Type cyException = env.getCyException();
				if ( !cyException.isSupertypeOf(paramType, env) ) {

					boolean signalError = true;
					if ( paramType instanceof Prototype ) {
						final Prototype proto = (Prototype ) paramType;
						if ( proto.getGenericParameterListList() != null &&
								proto.getGenericParameterListList().size() == 1 ) {}
					}
					if ( signalError ) {
						env.error(errSymbol,
								"The type of this expression defines an 'eval:' method that accepts a parameter that is not subtype of '"
						+ cyException.getFullName() + "'");
					}
				}
			}
		}
	}


	public void addCatchExpr(Expr expr) {
		catchExprList.add(expr);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		this.statementList.accept(visitor);
		for ( Expr e : this.catchExprList ) {
			e.accept(visitor);
		}
		visitor.visit(this);
	}

	@Override
	public WrStatement getI() {
		if ( iStatementTry == null ) {
			iStatementTry = new WrStatementTry(this);
		}
		return iStatementTry;
	}

	/*

\p{TryStat} ::= ``try''\/ StatementList \{ ``catch''\/ Expr \} [ ``finally'' StatListBracket ]  	 *
	 */
	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
		pw.print("try ");
		pw.add();
		if ( statementList != null )
		    statementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.sub();
		if ( catchExprList != null ) {
			for ( Expr e : this.catchExprList ) {
				pw.printIdent("catch ");
				e.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions );
			}
		}
		if ( this.finallyStatementList != null ) {
			this.finallyStatementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		}
	}

	@Override
	public Symbol getFirstSymbol() {
		return this.trySymbol;
	}

	@Override
	public void genJava(PWInterface pw, Env env) {
		String exprTmpVar = NameServer.nextJavaLocalVariableName();

		pw.printlnIdent("try {");
		pw.add();
		this.statementList.genJava(pw, env);
		pw.sub();
		pw.printlnIdent("}");
		if ( this.catchExprList.size() > 0 ) {
			pw.printlnIdent("catch (ExceptionContainer__ t) {");
			pw.add();
			pw.printlnIdent("Object []" + exprTmpVar + " = new Object[" +
				    this.catchExprList.size() + "];");
				int i = 0;
				for ( Expr e : this.getCatchExprList() ) {
					String eCatch = e.genJavaExpr(pw, env);
					pw.printlnIdent(exprTmpVar + "[" + i + "] = " + eCatch + ";");
					++i;
				}

			pw.printlnIdent("CyanRuntime.catchException(" + exprTmpVar +
					",  t);");

			pw.sub();
			pw.printlnIdent("}");
		}
		if ( finallyStatementList != null && finallyStatementList.getStatementList() != null ) {
			pw.printlnIdent("finally {");
			pw.add();
			this.finallyStatementList.genJava(pw, env);
			pw.sub();
			pw.printlnIdent("}");
		}
	}

	public Symbol getTrySymbol() {
		return trySymbol;
	}

	public void setTrySymbol(Symbol trySymbol) {
		this.trySymbol = trySymbol;
	}

	public StatementList getStatementList() {
		return statementList;
	}

	public void setStatementList(StatementList statementList) {
		this.statementList = statementList;
	}

	public List<Expr> getCatchExprList() {
		return catchExprList;
	}

	public void setCatchExprList(List<Expr> catchExprList) {
		this.catchExprList = catchExprList;
	}

	public StatementList getFinallyStatementList() {
		return finallyStatementList;
	}

	private Symbol trySymbol;

//	private List<Symbol> catchSymbolList;
//	private Symbol finallySymbol;
//	private Symbol rightCB_finally;


	private StatementList statementList;
	private StatementList finallyStatementList;


	private List<Expr> catchExprList;
	private WrStatementTry iStatementTry;
}
