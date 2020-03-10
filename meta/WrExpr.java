package meta;

import ast.Expr;
import ast.Type;
import saci.TupleTwo;

/**
 * Represents a Cyan  expression
 */
abstract public class WrExpr extends WrStatement {
	public WrExpr(Expr hidden) {
		super(hidden);
	}

	@Override
	abstract Expr getHidden();


	public WrType getType() {
		Type t = ((Expr ) hidden).getType();
		return t == null ? null : t.getI();
	}

	public WrType getType(WrEnv env) {
		Type t = ((Expr ) hidden).getType(env.hidden);
		return t == null ? null : t.getI();
	}


	public String ifPrototypeReturnsItsName() {
		return ((Expr ) hidden).ifPrototypeReturnsItsName();
	}

//	@Override
//	public void calcInternalTypes(WrEnv env) {
//		((Expr ) hidden).calcInternalTypes(env.hidden);
//	}


	public String getJavaName() {
		return ((Expr ) hidden).getJavaName();
	}

	public String ifPrototypeReturnsItsName(WrEnv env) {
		return ((Expr) hidden).ifPrototypeReturnsItsName(meta.GetHiddenItem.getHiddenEnv(env));
	}



	public TupleTwo<String, WrType> ifPrototypeReturnsNameWithPackageAndType(
			WrEnv env) {
		final TupleTwo<String, Type> t =
		    ((Expr ) hidden).ifPrototypeReturnsNameWithPackageAndType(env.hidden);
		if ( t == null || t.f2 == null ) { return null; }
		final TupleTwo<String, WrType> it = new TupleTwo<>(t.f1, t.f2.getI());

		return it ;
	}

}
