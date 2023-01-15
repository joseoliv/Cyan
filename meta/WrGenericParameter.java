package meta;

import ast.Expr;
import ast.GenericParameter;
import ast.GenericParameter.GenericParameterKind;
import ast.Type;

public class WrGenericParameter extends WrASTNode {


	public WrGenericParameter(GenericParameter hiddenGP) {
		this.hidden = hiddenGP;
	}

	private GenericParameter hidden;



	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}

	/*
	public WrExpr getParameter(WrEnv env) {

		return hidden.getParameter().getI();
	}
	*/

//	public WrSymbol getFirstSymbol(WrEnv env) {
//		Expr e = hidden.getParameter();
//		if ( e == null ) return null;
//		return e.getFirstSymbol().getI();
//	}

	public WrType getType() {
		Expr e = hidden.getParameter();
		if ( e == null ) return null;
		Type t = e.getType();
		if ( t != null ) {
			return t.getI();
		}
		else {
			return null;
		}
	}

	public String getName() {
		return hidden.getName();
	}

	public GenericParameterKind getKind() {
		return hidden.getKind();
	}

	@Override
	GenericParameter getHidden() {
		return hidden;
	}
}
