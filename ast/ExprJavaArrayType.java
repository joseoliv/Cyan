package ast;

import lexer.Symbol;
import meta.IdentStarKind;
import meta.WrExprJavaArrayType;
import saci.CyanEnv;
import saci.Env;

public class ExprJavaArrayType extends Expr {


	public ExprJavaArrayType(ExprIdentStar identExpr, int numDimensions, MethodDec method) {
		super(method);
		this.identExpr = identExpr;
		this.numDimensions = numDimensions;
	}

	@Override
	public WrExprJavaArrayType getI() {
		return new WrExprJavaArrayType(this);
	}


	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void calcInternalTypes(Env env) {
		super.calcInternalTypes(env);
		this.identExpr.calcInternalTypes(env);
		if ( this.identExpr.getIdentStarKind() != IdentStarKind.jvmClass_t ) {
			env.error(getFirstSymbol(), "A Java type was expected before []. That is, you cannot use [] with a Cyan prototype");
		}
		// TypeJavaRef elemType = (TypeJavaRef ) identExpr.getType();
		type = new TypeJavaRefArray( (TypeJavaRef ) identExpr.getType(), 1);
	}

	@Override
	public void calcInternalTypes(Env env, boolean leftHandSideAssignment) {
		if ( leftHandSideAssignment ) {
			env.error(this.getFirstSymbol(), "This is a Java type. It cannot appear in the left-hand side of an assignment, "
					+ "be passed as parameter in a message send or be returned by a method");
		}
		calcInternalTypes(env);
	}

	@Override
	public String getJavaName() {
		if ( asStr == null ) {
			calcAsString();
		}
		return asStr;
	}


	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		if ( asStr == null ) {
			calcAsString();
		}
		return asStr;
	}
	/**

	 */
	private void calcAsString() {
		asStr = this.identExpr.getName() + "[]";
		int n = this.numDimensions - 1;
		while ( n > 0 ) {
			asStr = asStr + "[]";
			--n;
		}
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
		if ( asStr == null ) {
			calcAsString();
		}
		pw.print(" " + asStr + " ");
	}

	@Override
	public Symbol getFirstSymbol() {
		return identExpr.getFirstSymbol();
	}


	public ExprIdentStar getIdentExpr() {
		return identExpr;
	}

	public int getNumDimensions() {
		return numDimensions;
	}


	@Override
	public boolean warnIfStatement() {
		return true;
	}


	private ExprIdentStar identExpr;
	private int numDimensions;
	private String asStr = null;

}
