/**
 *
 */
package ast;

import lexer.Symbol;
import meta.WrExprSelf;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** Represents an expression that is just "self" as in
 *     anObject = self;
 * @author José
 *
 */
public class ExprSelf extends Expr {

	/**
	 *
	 */
	public ExprSelf(Symbol selfSymbol, Prototype currentPrototype, MethodDec method) {
		super(method);
		this.selfSymbol = selfSymbol;
		type = currentPrototype;
		createdForMissingSelf = false;
	}


	@Override
	public WrExprSelf getI() {
		return new WrExprSelf(this);
	}

	@Override
	public Object eval(EvalEnv ee) {
		return ee.getSelfObject();
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}



	@Override
	public boolean mayBeStatement() {
		return false;
	}



	public void setSelfSymbol(Symbol selfSymbol) {
		this.selfSymbol = selfSymbol;
	}

	public Symbol getSelfSymbol() {
		return selfSymbol;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( cyanEnv.getCreatingInnerPrototypesInsideEval() ) {
			pw.print(NameServer.selfNameInnerPrototypes);
		}
		else if ( cyanEnv.getCreatingContextObject() )
			/*
			 * in this case, 'self' is being used inside a prototype created from a context function
			 */
			pw.print(NameServer.selfNameContextObject + " ");
		else
			/*
			 * in this case, 'self' is being used inside a prototype created from a function or a outer prototype method.
			 */
			pw.print("self");
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {


		if ( env.getCreatingInnerPrototypesInsideEval() ) {
			return NameServer.javaSelfNameInnerPrototypes;
		}
		else
			return "this";
	}

	@Override
	public Symbol getFirstSymbol() {
		return selfSymbol;
	}



	@Override
	public boolean warnIfStatement() {
		return true;
	}


	@Override
	public void calcInternalTypes(Env env) {

		Prototype currentPrototype;
		type = currentPrototype = env.getCurrentPrototype();


		MethodDec cm = env.getCurrentMethod();
		if ( cm != null ) {
			String currentMethodName = cm.getNameWithoutParamNumber();
			cm.setSelfLeak(true);
			if ( currentMethodName.equals("init") || currentMethodName.equals("init:") ) {

				if ( currentPrototype.getIsFinal() ) {
					/** in a final prototype and
					 * inside an init or init: method, it is illegal to access 'self' if some field has not been initialized
					 */
					for ( FieldDec iv : env.getCurrentObjectDec().getFieldList() ) {
						if ( !iv.isShared() && !iv.getWasInitialized() ) {
							env.error(this.getFirstSymbol(), "'self' cannot be used here because field '" + iv.getName() + "' has not been initialized");
						}
					}

				}
				else {
					env.error(this.getFirstSymbol(), "'self' cannot be used here because some fields "
							+ "of this prototype or possible subprototypes may have not been initialized");
				}
				/*
				env.error(this.getFirstSymbol(),  "Access to 'self' inside an 'init' or 'init:' method. This is illegal because some "
						+ "fields may not have been initialized");
				*/
			}		}


		super.calcInternalTypes(env);

	}

	public void calcInternalTypesDoNotCheckSelf(Env env) {

		type = env.getCurrentPrototype();
		super.calcInternalTypes(env);

	}

	public boolean getCreatedForMissingSelf() {
		return createdForMissingSelf;
	}

	public void setCreatedForMissingSelf(boolean createdForMissingSelf) {
		this.createdForMissingSelf = createdForMissingSelf;
	}


	private Symbol selfSymbol;

	/**
	 * true if the object of ExprSelf was created during the semantic analysis
	 * for a message sending that did not have receiver such as:
	 *     at: 0 put: 'a';
	 *     m;
	 *
	 * These are the equivalent to
	 *     self at: 0 put: 'a';
	 *     self m;
	 */
	boolean createdForMissingSelf;

}
