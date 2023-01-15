/**
 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.WrExprMessageSend;

/** Represents a message send. The receiver may be super or an expression.
 * The message may be a chain of unary message as in
 *          name = club memberList first getName;
 * or a message with keywords as in
 *          circle f1: 10 y: 20  radius: 5;
 *          file read:  ;// without arguments
 * The receiver may be super or an expression. Each of the four possibilities is represented by a subclass
 * of ExprMessageSend
 * @author José
 *
 */
abstract public class ExprMessageSend  extends Expr implements ASTNode {

	public ExprMessageSend(Symbol nextSymbol, MethodDec method) {
		super(method);
		this.nextSymbol = nextSymbol;
		wasReplacedByExpr = false;
		javaReceiver = false;
	}

	public ExprMessageSend(MethodDec method) {
		super(method);
		this.nextSymbol = null;
		wasReplacedByExpr = false;
		javaReceiver = false;
	}

	@Override
	public
	abstract WrExprMessageSend getI();


	/**
	 * Given an array originalArray, this method returns all combinations of elements of this array
	 * with zero ou more replacements of each element by valueToReplace. Then if the array is<br>
	 * <code> [ 1, 2 ]</code><br>
	 * and valueToReplace is 0, the return would be<br>
	 * <code> [ [ 1, 2 ], [0, 2], [1, 0], [0, 0] ] </code>
	   @param valueToReplace
	   @param originalArray
	   @return
	 */
	public static Class<?> [][] allCombinations(Class<?> valueToReplace, Class<?> []originalArray) {
		if ( originalArray.length == 1 ) {
			return new Class<?>[][] { new Class<?>[] { originalArray[0] }, new Class<?>[] { valueToReplace  } };
		}
		Class<?> []newOriginalArray = new Class<?>[originalArray.length - 1];
		for (int i = 1; i < originalArray.length; ++i) {
			newOriginalArray[i-1] = originalArray[i];
		}
		Class<?> [][]partial = allCombinations(valueToReplace, newOriginalArray);
		Class<?> [][]total = new Class<?>[2*partial.length][newOriginalArray.length + 1];
		for (int line = 0; line < partial.length; ++line ) {
			total[line][0] = valueToReplace;
			for ( int column = 0; column < newOriginalArray.length; ++column ) {
				total[line][column + 1] = partial[line][column];
			}
		}
		for (int line = partial.length; line < 2*partial.length; ++line ) {
			total[line][0] = originalArray[0];
			for ( int column = 0; column < newOriginalArray.length; ++column ) {
				total[line][column + 1] = partial[line - partial.length][column];
			}
		}
		return total;
	}


	static Class<?>[] getAllJavaSuperTypes(Class<?> javaClass) {
		List<Class<?>> ret = new ArrayList<>();
		ret.add(javaClass);
		Class<?> javaClassUp = javaClass;
		while ( javaClassUp != Object.class ) {
			Class<?> aSuper = javaClassUp.getSuperclass();
			ret.add(aSuper);
			javaClassUp = aSuper;
		}
		for ( Class<?> anInterface : javaClass.getInterfaces() ) {
			ret.add(anInterface);
		}

		return ListToArray(ret);
	}


	static List<Class<?>> getAllJavaSuperTypes2(Class<?> javaClass) {
		List<Class<?>> ret = new ArrayList<>();
		if ( javaClass == null ) {
			return ret;
		}
		ret.add(javaClass);
		if ( javaClass != Object.class ) {
			for ( Class<?> anInterface : javaClass.getInterfaces() ) {
				if ( anInterface.getInterfaces().length != 0 ) {
					ret.addAll( getAllJavaSuperTypes2(anInterface) );
				}
				else {
					ret.add(anInterface);
				}
			}
			ret.addAll(getAllJavaSuperTypes2(javaClass.getSuperclass()));
		}

		return ret;
	}



	static Class<?>[] ListToArray(List<Class<?>> List) {
		if ( List == null || List.size() == 0 ) {
			return null;
		}
		Class<?>[] newArray = new Class<?>[List.size()];
		int i = 0;
		for ( Class<?> aClass : List ) {
			newArray[i] = aClass;
			++i;
		}
		return newArray;
	}

	/**
	 * Given an array originalArray, this method returns all combinations of elements of this array
	 * with zero ou more replacements of each element by one of its supertypes.
	   @return
	 */
	public static Class<?> [][] allCombinations2(Class<?> []originalArray) {
		Class<?> first = originalArray[0];
		Class<?> []allCombFirst = ListToArray(getAllJavaSuperTypes2(first));
		if ( originalArray.length == 1 ) {
			Class<?> [][]a2 = new Class<?>[allCombFirst.length][1];
			int line = 0;
			for ( Class<?> aClass : allCombFirst ) {
				a2[line][0] = aClass;
				++line;
			}
			return a2;
		}
		else {
			Class<?> []newOriginalArray = new Class<?>[originalArray.length - 1];
			for (int i = 1; i < originalArray.length; ++i) {
				newOriginalArray[i-1] = originalArray[i];
			}
			Class<?> [][]partial = allCombinations2(newOriginalArray);
			Class<?> [][]total = new Class<?>[allCombFirst.length*partial.length][newOriginalArray.length + 1];

			for ( Class<?> aClass : allCombFirst ) {
				for (int line = 0; line < partial.length; ++line ) {
					total[line][0] = aClass;
					for ( int column = 0; column < newOriginalArray.length; ++column ) {
						total[line][column + 1] = partial[line][column];
					}
				}
			}
			return total;
		}
	}



	/**
	 * true if this message send was replaced by an expression in the first parsing phase.
	 * This happens, for example, when the method to be called is a grammar method
	 */
	protected boolean wasReplacedByExpr;


	/**
	 * true if this is a message send to a Java object. That is, the receiver is a Java object
	 */
	protected boolean javaReceiver;

}
