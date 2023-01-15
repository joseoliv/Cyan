package ast;

import lexer.Symbol;
import meta.CompilationStep;
import meta.LeftHandSideKind;
import meta.WrStatementReturnFunction;
import saci.CyanEnv;
import saci.Env;

/**
 * represents a return statement, like
 * 	   public proc get -> int {
 *         return n;
 *     }
 * @author José
 *
 */
public class StatementReturnFunction extends Statement {

	private ExprFunction currentFunction;

	public StatementReturnFunction(Symbol returnSymbol, Expr expr, ExprFunction currentFunction,
			MethodDec currentMethod) {
		super(currentMethod, false);
		this.expr = expr;
		this.returnSymbol = returnSymbol;
		this.currentFunction = currentFunction;
	}

	@Override
	public WrStatementReturnFunction getI() {
		if ( iStatementReturnFunction == null ) {
			iStatementReturnFunction = new WrStatementReturnFunction(this);
		}
		return iStatementReturnFunction;
	}

	private WrStatementReturnFunction iStatementReturnFunction = null;

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		if ( expr != null ) {
			expr.accept(visitor);
		}
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( cyanEnv.getCreatingInnerPrototypeFromFunction() ) {
			pw.print("return ");
		}
		else
			pw.print("^ ");
		expr.genCyan(pw, false, cyanEnv, genFunctions);
	}

	@Override
	public void genJava(PWInterface pw, Env env) {
		env.error(this.getFirstSymbol(), "Internal error: genJava of StatementReturnFunction should never be called", true, true);
	}

	@Override
	public Symbol getFirstSymbol() {
		return returnSymbol;
	}


	@Override
	public boolean statementDoReturn() {
		return true;
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

		super.calcInternalTypes(env);

        if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_6 ) {
        	//Type returnType = env.getCurrentMethod().getMethodSignature().getReturnType(env);
        	Type returnType = currentFunction.getReturnType();
        	if ( returnType != null ) {
                MetaInfoServer.checkAssignmentPluggableTypeSystem(env,
                		returnType, this, LeftHandSideKind.FunctionReturn_LHS,
                		expr.getType(), expr);
        	}
        	else {
        		returnType = expr.getType();
        		// consider the return type as the same type as the expression after '^'
                MetaInfoServer.checkAssignmentPluggableTypeSystem(env,
                		returnType, this, LeftHandSideKind.FunctionReturn_LHS,
                		returnType, expr);
        	}

            /*
             * 	public static void checkAssignmentPluggableTypeSystem(Env env,
			Type leftType, Object leftASTNode, LeftHandSideKind leftKind,
			Type rightType, Expr rightExpr) {

             */
        }

	}

	public Expr getExpr() {
		return expr;
	}


	private Symbol returnSymbol;
	private Expr expr;

}
