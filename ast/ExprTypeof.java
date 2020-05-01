/**
 *
 */
package ast;

import lexer.Symbol;
import meta.MetaHelper;
import meta.WrExprTypeof;
import saci.CyanEnv;
import saci.Env;

/** Represents function typeof which gives the type of q variable or literal object:
 *      var Stack<Int> s;
 *      var typeof(s) myStack;
 *
 *      myStack = Stack<Int> new;   // ok
 *
 * Here "typeof(s)" is represented by an object of this class
 * @author José
 *
 */
public class ExprTypeof extends Expr {

	public ExprTypeof(Symbol typeofSymbol, Expr expr, MethodDec method) {
		super(method);
		this.typeofSymbol = typeofSymbol;
		this.expr = expr;
	}

	@Override
	public WrExprTypeof getI() {
		// TODO Auto-generated method stub
		return new WrExprTypeof(this);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		expr.accept(visitor);
		visitor.visit(this);
	}



	@Override
	public boolean mayBeStatement() {
		return false;
	}



	public Expr getArgument() {
		return expr;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		pw.print("typeof(");
		expr.genCyan(pw, false, cyanEnv, true);
		pw.print(")");

	}

	@Override
	public String getJavaName() {
		return type.getJavaName();
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		Type t = expr.getType(env);
		return MetaHelper.getJavaName(t.getFullName()) + ".prototype";
	}


	@Override
	public Symbol getFirstSymbol() {
		return typeofSymbol;
	}


	@Override
	public void calcInternalTypes(Env env) {

		expr.calcInternalTypes(env);
		type = expr.getType(env);
		super.calcInternalTypes(env);
	}

	@Override
	public Type ifRepresentsTypeReturnsType(Env env) {
		if ( type == null ) {
			try {
				env.pushCheckUsePossiblyNonInitializedPrototype(false);
				expr.calcInternalTypes(env);
			}
			finally {
				env.popCheckUsePossiblyNonInitializedPrototype();
			}
		}
		type = expr.getType(env);
		return type;
	}

	@Override
	public saci.TupleTwo<String, Type> ifPrototypeReturnsNameWithPackageAndType(Env env) {
		return expr.ifPrototypeReturnsNameWithPackageAndType(env);
	}

	public Expr getExpr() {
		return expr;
	}



	@Override
	public boolean warnIfStatement() {
		return true;
	}


	/**
	 * the symbol corresponding to "type" in "type(s)".
	 */
	private Symbol typeofSymbol;
	/**
	 * the parameter of the function. In type(s), it is "s". In
	 * type(HumanResources.Person), it is "HumanResources.Person"
	 */
	private Expr expr;


}
