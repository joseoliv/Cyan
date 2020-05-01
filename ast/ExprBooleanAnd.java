package ast;

import lexer.Symbol;
import lexer.SymbolOperator;
import meta.WrExprBooleanAnd;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 * represents a boolean 'and' expression like {@code i < 5 && v[i] == 0}
   @author jose
 */
public class ExprBooleanAnd extends Expr {

	public ExprBooleanAnd(Expr leftExpr, SymbolOperator andOp, Expr rightExpr, MethodDec method) {
		super(method);
		this.leftExpr = leftExpr;
		this.andOp = andOp;
		this.rightExpr = rightExpr;
	}

	@Override
	public WrExprBooleanAnd getI() {
		return new WrExprBooleanAnd(this);
	}


	@Override
	public void accept(ASTVisitor visitor) {
		leftExpr.accept(visitor);
		rightExpr.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public Object eval(EvalEnv ee) {
		Object leftValue = leftExpr.eval(ee);
		Object rightValue = rightExpr.eval(ee);
		if ( leftValue instanceof cyan.lang.CyBoolean ) {
			leftValue = ((cyan.lang.CyBoolean ) leftValue).b;
		}
		if ( rightValue instanceof cyan.lang.CyBoolean ) {
			rightValue = ((cyan.lang.CyBoolean ) rightValue).b;
		}
		if ( !(leftValue instanceof Boolean) || !(rightValue instanceof Boolean) ) {
			ee.error(this.getFirstSymbol(), "Both expressions of '&&' should have type 'Boolean'. "
					+ "In this case, the types are '" + leftValue.getClass().getName() + "' and '" +
					rightValue.getClass().getName() + "'");
		}
		return (Boolean ) leftValue && (Boolean ) rightValue;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		leftExpr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.print(" && ");
		rightExpr.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String tmpVar = NameServer.nextJavaLocalVariableName();
		String leftVar = leftExpr.genJavaExpr(pw, env);

		if ( leftExpr.getType() == Type.Dyn ) {
			String newTmpVar = NameServer.nextJavaLocalVariableName();
			pw.printlnIdent("if ( !(" + leftVar + " instanceof CyBoolean) ) {");
			pw.add();
			pw.printlnIdent("throw new ExceptionContainer__("
					+ env.javaCodeForCastException(leftExpr, Type.Dyn) + " );");
			pw.sub();
			pw.printlnIdent("}");
			pw.printlnIdent("CyBoolean " + newTmpVar + " = (CyBoolean ) " + leftVar + ";");
			leftVar = newTmpVar;
		}
		pw.printlnIdent("CyBoolean " + tmpVar + ";");

		/* String tmpBool = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("boolean " + tmpBool + ";"); */

		pw.printlnIdent("if ( " + leftVar + ".b ) { ");
		pw.add();
		String rightVar = rightExpr.genJavaExpr(pw, env);
		if ( rightExpr.getType() == Type.Dyn ) {
			String newTmpVar = NameServer.nextJavaLocalVariableName();
			pw.printlnIdent("if ( !(" + rightVar + " instanceof CyBoolean) ) {");
			pw.add();
			pw.printlnIdent("throw new ExceptionContainer__("
					+ env.javaCodeForCastException(rightExpr, Type.Dyn) + " );");

			pw.sub();
			pw.printlnIdent("}");
			pw.printlnIdent("CyBoolean " + newTmpVar + " = (CyBoolean ) " + rightVar + ";");
			rightVar = newTmpVar;
		}
		pw.printlnIdent(tmpVar + " = new CyBoolean(" + rightVar + ".b );");

		pw.sub();
		pw.printlnIdent("}");
		pw.printlnIdent("else {");
		pw.add();
		pw.printlnIdent(tmpVar + " = new CyBoolean(false);");
		pw.sub();
		pw.printlnIdent("}");



		return tmpVar;
	}

	@Override
	public void calcInternalTypes(Env env) {

		try {
			env.pushCheckUsePossiblyNonInitializedPrototype(true);;
			leftExpr.calcInternalTypes(env);
			rightExpr.calcInternalTypes(env);
		}
		finally {
			env.popCheckUsePossiblyNonInitializedPrototype();
		}

		if ( leftExpr.getType() != Type.Boolean && leftExpr.getType() != Type.Dyn )
			env.error(leftExpr.getFirstSymbol(), "Expression of type Boolean or Dyn expected in the left side of '&&'");
		if ( rightExpr.getType() != Type.Boolean && rightExpr.getType() != Type.Dyn )
			env.error(rightExpr.getFirstSymbol(), "Expression of type Boolean expected in the right side of '&&'");
		type = Type.Boolean;
		super.calcInternalTypes(env);
	}

	@Override
	public Symbol getFirstSymbol() {
		return leftExpr.getFirstSymbol();
	}


	public Expr getLeftExpr() {
		return leftExpr;
	}

	public Expr getRightExpr() {
		return rightExpr;
	}

	public SymbolOperator getAndOp() {
		return andOp;
	}


	@Override
	public boolean warnIfStatement() {
		return true;
	}


	private Expr leftExpr, rightExpr;
	private SymbolOperator andOp;
}
