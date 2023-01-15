package meta;

import java.util.List;

public interface IActionAttachedType_semAn {

	/**
<p>This method is called whenever:</p>
<ul>
<li>a type <code>T</code> has an attached metaobject whose class <code>C</code> implements interface <code>IActionAttachedType_semAn</code> and;</li>
<li>an expression changes its value to type <code>T</code>. A typical example is an assignment <code>x = value</code> in which the the type of <code>x</code> is <code>T@C</code>. It may be other statements than assignments, such as return value from methods, parameter passing, etc. See enum {@link meta.LeftHandSideKind} for a complete list.</li>
</ul>
<p>The AST node of the left-hand side (or equivalent) is <code>leftASTNode</code> whose kind is <code>leftKind</code> and type is <code>leftType</code>. The right-hand side expression is <code>rightExpr</code> and has type <code>rightType</code>.</p>
<p>The string returned by this method replaces the expression <code>rightExpr</code>.<br>
 */
	@SuppressWarnings("unused")
	default StringBuffer semAn_checkLeftTypeChangeRightExpr(
			ICompiler_semAn compiler_semAn,
			WrType leftType, Object leftASTNode, LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {
		return null;
	}

	/**

<p>This method is called whenever:</p>
<ul>
<li>a type <code>T</code> has an attached metaobject whose class <code>C</code> implements interface <code>IActionAttachedType_semAn</code> and;</li>
<li>an expression of type <code>T</code> may change its type. A typical example is an assignment <code>x = value</code> in which the the type of <code>value</code> is <code>T@C</code>. It may be other statements than assignments, such as return value from methods, parameter passing, etc. See enum {@link meta.LeftHandSideKind} for a complete list.</li>
</ul>
<p>The AST node of the left-hand side (or equivalent) is <code>leftASTNode</code> whose kind is <code>leftKind</code> and type is <code>leftType</code>. The right-hand side expression is <code>rightExpr</code> and has type <code>rightType</code>.</p>
<p>The string returned by this method replaces the expression <code>rightExpr</code>.<br>
	 */

	@SuppressWarnings("unused")
	default StringBuffer semAn_checkRightTypeChangeRightExpr(ICompiler_semAn compiler_semAn,
			WrType leftType, Object leftASTNode, LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {
		return null;
	}


	/**
	 * during the semantic analysis, this method is called in order to do checks
	 * in the metaobject annotation. It is assumed that the type to which
	 * the annotations are attached are already defined (field type
	 * of the expression that represents the type has already been set).
	 */
	default void checkAnnotation() { }
	/**
	 * Return an array of tuples of the format, in Cyan syntax:<br>
	 * <code> [. packageName, prototypeName .] </code><br>
	 * For each tuple, neither {@link meta.ICheckTypeWithAnnotations_semAn#semAn_checkTypeChangeLeft} nor
	 * {@link meta.ICheckTypeWithAnnotations_semAn#semAn_checkTypeChangeRight} are called inside
	 * packageName.prototypeName.
	 *
	 */
	default List<Tuple2<String, String>> doNotCheckIn() { return null; }

	/**
	 * if this method returns true, a variable of the program called<br>
	 *        metaobject name + "DoNotCheckIn"<br>
	 * is used for collection prototypes in which the assignments are not checked.
	 * That is, the left-hand side of the assignment will not demand any
	 * checking in the prototypes returned by method doNotCheckIn() and in
	 * the set of package.prototype  strings that are in a set like<br>
	 *        untaintedDoNotCheckIn<br>
	 *   or<br>
	 *        rangeDoNotCheckIn<br>
	 *
	   @return
	 */
	default boolean allowDoNotCheckInList() { return false; }
}
