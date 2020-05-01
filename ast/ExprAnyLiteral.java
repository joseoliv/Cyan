/**

 */
package ast;

import meta.Function0;
import meta.WrExprAnyLiteral;
import saci.CyanEnv;

/** This is the superclass of all kinds of basic type literals
 * and array literals of basic type literals
 *
   @author José

 */
abstract public class ExprAnyLiteral extends Expr {



	public ExprAnyLiteral(MethodDec method) {
		super(method);
	}


	@Override
	abstract public WrExprAnyLiteral getI();


	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * return the value of this literal as a Java object. For string
	 * literals, a Java string is returned. For a Cyan Int, an Integer
	 * is returned.
	   @return
	 */
	abstract public Object getJavaValue();

	/**
	 *  generate Cyan code but replacing any formal generic parameters of a generic prototype by
	 *  its corresponding values. That is, suppose there is a generic prototype
	 *         @check(T, "T", #T) object Proto<T> ... end
	 *  In an instantiation Proto<Int> there should be produced
	 *         @check(Int, "Int", #Int) object Proto<Int> ... end
	 *
	 *  this method and method of the same name of subclasses are called to replace T, "T", and #T by
	 *  Int, "Int", and #Int
	 */
	public void genCyanReplacingGenericParameters(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}

	/**
	 * return a string in Java representing the creation of an object with value equal to this.
	 * For example, if 'this' represents '1', "1" is returned. If this represents
	 * an array with elements '1' and '2', it is returned a string with the following contents<br>
	 * <code>
	 * new Integer[] { 1, 2 }
	 * </code>
	   @return
	 */
	abstract public StringBuffer getStringJavaValue();
	/**
	 * the type in Java of this value
	 */
	abstract public String getJavaType();

	@Override
	public boolean mayBeStatement() {
		return false;
	}

	abstract public String metaobjectParameterAsString(Function0 inError);

	/**
	 * return true if this is a valid parameter for metaobjects feature and annot according to the following definition:
	 * a) a literal array is valid if all of its elements are of the same type;
	 * b) a literal map is valid if all the keys have the same type and all the values have the same type.
	 */
	public boolean isValidMetaobjectFeatureParameter() {
		return true;
	}
}
