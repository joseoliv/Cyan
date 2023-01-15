/**
 *
 */
package ast;

import java.util.List;
import error.CompileErrorException;
import error.ErrorInMetaobjectException;
import meta.IdentStarKind;
import meta.Tuple2;
import meta.WrStatementList;
import meta.cyanLang.CyanMetaobjectCompilationContextPop;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**  Represents a list of statements as those of a method or function
 *
 * @author José
 *
 */
public class StatementList implements GenCyan, ASTNode {

	public StatementList(List<Statement> statementList) {
		super();
		this.statementList = statementList;
	}

	@Override
	public WrStatementList getI() {
		if ( iStatementList == null ) {
			iStatementList = new WrStatementList(this);
		}
		return iStatementList;
	}

	private WrStatementList iStatementList = null;

	@Override
	public void accept(ASTVisitor visitor) {
		for ( Statement stat : this.statementList ) {
			stat.accept(visitor);
		}
		visitor.visit(this);
	}


	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		/*int numChars = 0;
		for ( Statement s : statementList ) {
			numChars += PWCounter.getNumChars(s);
		} */


		int size = statementList.size();
		if ( size > 1 )
			printInMoreThanOneLine = true;
		for ( Statement s : statementList ) {
			if ( printInMoreThanOneLine )
				pw.printIdent("");
			boolean moreThanOneLine = PWCounter.printInMoreThanOneLine(s);
			s.genCyan(pw, moreThanOneLine, cyanEnv, genFunctions );
			--size;
			if ( s.getShouldBeFollowedBySemicolon() ) {
				if ( size > 0 ) {
					if ( printInMoreThanOneLine )
						pw.println("; ");
					else
						pw.print("; ");
				}
			}
		}
		if ( printInMoreThanOneLine )
			pw.println("");
	}


	public void genJava(PWInterface pw, Env env) {
		for ( Statement s : statementList ) {
			env.pushCode(s);

			s.genJava(pw, env);
			if ( s.addSemicolonJavaCode() )
				pw.println(";");

			/*
			if ( s instanceof ExprIdentStar ) {
				pw.printlnIdent(((ExprIdentStar) s).genJavaExpr(pw, env) + ";");
			}
			else {
			}
			*/
			env.popCode();
		}
	}


	public void setStatementList(List<Statement> statementList) {
		this.statementList = statementList;
	}

	public List<Statement> getStatementList() {
		return statementList;
	}


	public void calcInternalTypes(Env env) {

		foundError = false;
		env.addLexicalLevel();
		boolean topLevelStatements = env.getTopLevelStatements();
		for ( Statement statement : statementList ) {

			try {
				env.pushCode(statement);
				env.setTopLevelStatements(false);
				statement.calcInternalTypes(env);

//				if ( statement.warnIfStatement() ) {
//					env.warning(statement.getFirstSymbol(),
//							"Probably this statement produces a result that is not used. Maybe the code considers that the "
//							+ "precedence of operators is different from the precedence given by Cyan");
//				}
				env.setTopLevelStatements(topLevelStatements);
				env.setFirstMethodStatement(false);

				/*
				 * if it is an initialization of a field inside
				 * an 'init' or 'init:' method, set the variable as such
				 */
				if ( env.getCurrentMethod() != null && env.getCurrentMethod().isInitMethod() ) {
					if ( statement instanceof ast.StatementAssignmentList ) {
						List<Expr> exprList = ((StatementAssignmentList) statement).getExprList();
						for (int j = 0; j < exprList.size() - 1; ++j) {
							Expr anExpr = exprList.get(j);
							if ( anExpr instanceof ExprIdentStar ) {
								ExprIdentStar id = (ExprIdentStar) anExpr;
								if ( id.getIdentStarKind() == IdentStarKind.instance_variable_t ) {
									FieldDec iv = (FieldDec ) id.getVarDeclaration();
									/*
									if ( iv.isShared() ) {
										env.error(statement.getFirstSymbol(), "Shared fields cannot be initialized in 'init' or 'init:' methods");
									}
									*/
									if ( topLevelStatements ) {
										iv.setWasInitialized(true);
									}
								}
							}
							else if (  anExpr instanceof ExprSelfPeriodIdent ) {
								ExprSelfPeriodIdent exprSelf = (ExprSelfPeriodIdent ) anExpr;
								FieldDec iv = exprSelf.getFieldDec();
								/*
								if ( iv.isShared() ) {
									env.error(statement.getFirstSymbol(), "Shared fields cannot be initialized in 'init' or 'init:' methods");
								}
								*/
								if ( topLevelStatements ) {
									iv.setWasInitialized(true);
								}
							}
						}
					}

				}

				if ( ! statement.mayBeStatement() ) {
					env.error(statement.getFirstSymbol(), "Statement does nothing");
				}
				/*
				if ( statement instanceof ExprIdentStar ) {
					ExprIdentStar e = (ExprIdentStar ) statement;
					if ( e.getIdentStarKind() != IdentStarKind.unaryMethod_t )
						env.error(e.getFirstSymbol(), "Statement does nothing");
				}
				*/

			}
			catch ( CompileErrorException e ) {
				foundError = true;
			}
			catch ( ErrorInMetaobjectException e ) {
				throw e;
			}
			catch (RuntimeException e) {
				e.printStackTrace();
				foundError = true;
				env.error(statement.getFirstSymbol(), "Internal error in StatementList");
			}
			finally {
				if ( env.getMapCompUnitErrorList() != null && env.getMapCompUnitErrorList().size() > 0 ) {
					foundError = true;
					for ( CompilationUnit cunit : env.getMapCompUnitErrorList().keySet() ) {
						// only the first is considered
						cunit.addError( env.getMapCompUnitErrorList().get(cunit).get(0));
						env.getMapCompUnitErrorList().clear();
						throw new CompileErrorException(cunit.getErrorList().get(0).getMessage());
						//throw new CompileErrorException( env.getMapCompUnitErrorList().get(cunit).get(0).getMessage() );
					}
				}

				env.popCode();
			}

		}
		env.removeVariablesLastLevel();
		env.subLexicalLevel();

		if ( ! foundError ) {
			for ( int i = 1; i < statementList.size(); ++i ) {
				if ( statementList.get(i-1).alwaysReturn(env) ||
					 statementList.get(i-1).alwaysBreak(env) ) {
					Statement other = statementList.get(i);
					if ( !( other instanceof StatementAnnotation &&
							((StatementAnnotation) other).getAnnotation().getCyanMetaobject()
						     instanceof CyanMetaobjectCompilationContextPop) ) {
						env.error(statementList.get(i).getFirstSymbol(), "unreachable statement");
					}
				}
			}

		}
	}

	public String asString(CyanEnv cyanEnv) {
		PWCharArray pwChar = new PWCharArray();
		genCyan(pwChar, true, cyanEnv, true);
		return pwChar.getGeneratedString().toString();
	}

	@Override
	public String asString() {
		return asString(NameServer.cyanEnv);
	}

	public boolean isBreakLastStatement() {
		if ( statementList.size() == 0 )
			return false;
		else
			return statementList.get(statementList.size()-1) instanceof StatementBreak;
	}


	public Boolean getAlwaysReturnMemoize() {
		if ( alwaysReturnMemoize == null ) {
			//System.out.println(((CompilationUnit) this.statementList.get(0).getFirstSymbol().getCompilationUnit()).getPublicPrototype().getFullName());
			return true;
		}

		return alwaysReturnMemoize;
	}

	public boolean alwaysReturn(Env env) {

		if ( alwaysReturnMemoize != null ) {
			return alwaysReturnMemoize;
		}
		if ( statementList.size() == 0 ) {
			alwaysReturnMemoize = false;
			return false;
		}
		else {
			int last = statementList.size()-1;
			while ( last > 0 ) {
				Statement other = statementList.get(last);
				if ( !( other instanceof StatementAnnotation &&
						((StatementAnnotation) other).getAnnotation().getCyanMetaobject()
					     instanceof CyanMetaobjectCompilationContextPop) ) {
					alwaysReturnMemoize = other.alwaysReturn(env);
					return alwaysReturnMemoize;
				}
				--last;
			}
			alwaysReturnMemoize = statementList.get(statementList.size()-1).alwaysReturn(env);
			return alwaysReturnMemoize;
		}
	}

	public boolean alwaysBreak(Env env) {


		if ( statementList.size() == 0 ) {
			return false;
		}
		else {
			int last = statementList.size() - 1;
			while ( last > 0 ) {
				Statement other = statementList.get(last);
				if ( !( other instanceof StatementAnnotation &&
						((StatementAnnotation) other).getAnnotation().getCyanMetaobject()
					     instanceof CyanMetaobjectCompilationContextPop) ) {
					return other.alwaysBreak(env);
				}
				--last;
			}
			return statementList.get(statementList.size()-1).alwaysBreak(env);
		}
    }


	public boolean alwaysReturnFromFunction() {

		if ( statementList.size() == 0 )
			return false;
		else
			return statementList.get(statementList.size()-1).statementDoReturn();
	}


	public boolean getFoundError() {
		return foundError;
	}


	public Tuple2<Class<?>, Object> eval(EvalEnv ee) {
		for ( Statement stat : this.statementList ) {
			stat.eval(ee);
		}
		return null;
	}

	private List<Statement> statementList;

	/**
	 * true if some semantic error was found in some statement
	 */
	private boolean foundError;

	private Boolean alwaysReturnMemoize = null;

}
