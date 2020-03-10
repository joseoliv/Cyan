package meta;

import ast.ExprAnyLiteral;
import ast.ExprLiteralNil;
import lexer.Symbol;

abstract public class WrExprAnyLiteral extends WrExpr {

	public WrExprAnyLiteral(ExprAnyLiteral hidden) {
		super(hidden);
	}

	@Override
	abstract ExprAnyLiteral getHidden();


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}
	/**
	 * return the value of this literal as a Java object. For string
	 * literals, a Java string is returned. For a Cyan Int, an Integer
	 * is returned.
	   @return
	 */
	public Object getJavaValue() {
		return ((ExprAnyLiteral ) hidden).getJavaValue();
	}

	public StringBuffer getStringJavaValue() {
		return ((ExprAnyLiteral ) hidden).getStringJavaValue();
	}

	public String getJavaType() {
		return ((ExprAnyLiteral ) hidden).getJavaType();
	}

	public boolean mayBeStatement() {
		return ((ExprAnyLiteral ) hidden).mayBeStatement();
	}

	public String metaobjectParameterAsString(Function0 inError) {
		return ((ExprAnyLiteral ) hidden).metaobjectParameterAsString(inError);
	}

	public boolean isValidMetaobjectFeatureParameter() {
		return ((ExprAnyLiteral ) hidden).isValidMetaobjectFeatureParameter();
	}

	@Override
	public WrSymbol getFirstSymbol() {
		Symbol sym = ((ExprAnyLiteral ) hidden).getFirstSymbol();
		return sym == null ? null : sym.getI();
	}

	public boolean isExprLiteralNil() {
		return hidden instanceof ExprLiteralNil;
	}


	/*
	public void genCyanReplacingGenericParameters(PWInterface pw,
			boolean printInMoreThanOneLine, CyanEnv cyanEnv,
			boolean genFunctions) {
		((ExprAnyLiteral ) hidden).genCyanReplacingGenericParameters(
				pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}
	*/

}
