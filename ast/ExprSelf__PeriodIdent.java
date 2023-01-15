package ast;

import lexer.Symbol;
import meta.MetaHelper;
import meta.WrExprSelf__PeriodIdent;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
*   represents the access to a field of self__.
*
* @author José
*
*/
public class ExprSelf__PeriodIdent extends Expr implements LeftHandSideAssignment  {

	/**
	 *
	 */
	public ExprSelf__PeriodIdent(Symbol selfSymbol, Symbol identSymbol, MethodDec method) {
		super(method);
		this.selfSymbol = selfSymbol;
		this.identSymbol = identSymbol;
	}

	@Override
	public WrExprSelf__PeriodIdent getI() {
		return new WrExprSelf__PeriodIdent(this);
	}

	/*
	 * should not be called
	   @see ast.Expr#accept(ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) {
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


	public void setIdentSymbol(Symbol identSymbol) {
		this.identSymbol = identSymbol;
	}


	public Symbol getIdentSymbol() {
		return identSymbol;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {


		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			pw.print("self__." + cyanEnv.formalGenericParamToRealParam(identSymbol.getSymbolString()));
		}
		else {
			String name = identSymbol.getSymbolString();
			pw.print("self." + name);
		}
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String tmpVar;
		tmpVar = MetaHelper.getJavaName(identSymbol.getSymbolString())
				+ (fieldDec.getRefType() ? ".elem" : "");
		return tmpVar;
	}

	@Override
	public void genJavaCodeVariable(PWInterface pw, Env env) {
		pw.print(MetaHelper.getJavaName(identSymbol.getSymbolString()));
		if ( fieldDec.getRefType() )
			pw.print(".elem");
	}


	@Override
	public Symbol getFirstSymbol() {
		return selfSymbol;
	}


	@Override
	public void calcInternalTypes(Env env) {


		FieldDec iv = env.searchField(NameServer.selfNameInnerPrototypes);
		if ( iv != null ) {
			ObjectDec obj = (ObjectDec ) iv.getType();
			fieldDec = obj.searchField(identSymbol.getSymbolString());
			if ( fieldDec == null ) {
				env.error(this.selfSymbol, "field " + identSymbol.getSymbolString() + " was not found", true, true);
				type = Type.Dyn;
			}
			else
				type = fieldDec.getType();
		}
		else
			env.error(selfSymbol, "Internal error: '" + NameServer.selfNameInnerPrototypes + "' has no type", true, true);

		super.calcInternalTypes(env);

	}

	public FieldDec getFieldDec() {
		return fieldDec;
	}

	private FieldDec fieldDec;
	private Symbol selfSymbol;
	private Symbol identSymbol;
}
