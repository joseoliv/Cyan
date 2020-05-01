package meta;

import java.util.List;
import ast.AnnotationAt;
import ast.Expr;

/**
 * should not be used. Use interface {@link meta.IActionAttachedType_semAn} instead
   @author jose
 */
@Deprecated
public class CyanMetaobjectAttachedType extends CyanMetaobjectAtAnnot {

	public CyanMetaobjectAttachedType(String name, AnnotationArgumentsKind parameterKind) {
		super(name, parameterKind, new AttachedDeclarationKind[] { AttachedDeclarationKind.TYPE });
	}


	/**
	 * replace the text of the expression 'expr' by 'sb'. The 'sb' string is the code of an
	 * expression that has type 'newType'. Of course, 'newType' should be subtype of 'expr'.
	   @param expr
	   @param sb
	   @param env
	   @param newType
	   @return
	 */
	public static boolean replaceRightExpr(CyanMetaobjectAttachedType metaobject,
			 WrExpr iexpr, StringBuffer sb, WrEnv env, WrType newType) {

		final AnnotationAt annotation = metaobject.getAnnotation().getHidden();
		final Expr expr = meta.GetHiddenItem.getHiddenExpr(iexpr);
		if ( expr.getCodeThatReplacesThisExpr() != null ) {
			/*
			 * this message send has already been replaced by another expression
			 */
			if ( expr.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
				metaobject.addError(expr.getFirstSymbol().getI(),  "Metaobject annotation '" + metaobject.getName() +
						"' is trying to replace expression '" + expr.asString() +
						"' by another expression. But this has already been asked by metaobject annotation '" +
						expr.getCyanAnnotationThatReplacedMSbyExpr().getCyanMetaobject().getName() + "'" +
						" at line " + expr.getCyanAnnotationThatReplacedMSbyExpr().getFirstSymbol().getLineNumber() +
						" of prototype " + expr.getCyanAnnotationThatReplacedMSbyExpr().getPackageOfAnnotation() + "." +
						expr.getCyanAnnotationThatReplacedMSbyExpr().getPackageOfAnnotation());
			}
			else {
				metaobject.addError(expr.getFirstSymbol().getI(), "Metaobject annotation '" + metaobject.getName() +
						" is trying to replace message send '" + expr.asString() +
						"' by an expression. But this has already been asked by someone else");
			}
			return false;
		}

		meta.GetHiddenItem.getHiddenEnv(env)
		   .replaceStatementByCode(expr, annotation, sb, meta.GetHiddenItem.getHiddenType(newType) );

		// boolean b = ! metaobject.getCurrentPrototype().getCompilationUnit(env).getFullFileNamePath().equals(expr.getFirstSymbol().getCompilationUnit().getFullFileNamePath());
		expr.setCyanAnnotationThatReplacedMSbyExpr(annotation);
		return true;
	}


	/**
	 * This method is called whenever a value may change its type. The
	 * prime example is an assignment. There is a left and right hand.
	 * The expression of the right hand side may change its type to the
	 * type of the left-hand side. Other cases in which the type changes
	 * include return from method, parameter passing, and casts (type-case
	 * statement). See enum {@link meta.LeftHandSideKind} for a complete list.<br>
	 * The type of leftASTNode should be leftKind.getAstNodeClass().
	 *
	 * This method assumes that the left-hand side type has attached
	 * metaobjects. If a attached metaobject of the left-hand side type
	 * implements this interface, this method is called. It should
	 * do some checking on the assignment or whatever.   leftASTNode
	 * is the AST object that represents the left-hand side.
	 *
	 * The returned string replaces the expression rightExpr
	 */
	@SuppressWarnings("unused")
	public StringBuffer semAn_checkTypeChangeLeft(ICompiler_semAn compiler_semAn,
			WrType leftType, Object leftASTNode, LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {
		return null;
	}

	/**
	 * This method is called whenever a value may change its type. The
	 * prime example is an assignment. There is a left and right hand.
	 * The expression of the right hand side may change its type to the
	 * type of the left-hand side. Other cases in which the type changes
	 * include return from method, parameter passing, and casts (type-case
	 * statement). See enum {@link meta.LeftHandSideKind} for a complete list.<br><br>
	 * The type of leftASTNode should be leftKind.getAstNodeClass().
 	 *
	 * This method assumes that the right-hand side type has attached
	 * metaobjects. If a attached metaobject of the right-hand side type
	 * implements this interface, this method is called. It should
	 * do some checking on the assignment or whatever.
	 *
	 * The returned string replaces the expression rightExpr
	   @param env
	   @param leftType
	   @param leftExpr
	   @param rightType
	   @param rightExpr
	   @param methodOfExpr
	 */

	@SuppressWarnings("unused")
	public StringBuffer semAn_checkTypeChangeRight(ICompiler_semAn compiler_semAn,
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
	public void checkAnnotation() { }
	/**
	 * Return an array of tuples of the format, in Cyan syntax:<br>
	 * <code> [. packageName, prototypeName .] </code><br>
	 * For each tuple, neither {@link meta.ICheckTypeWithAnnotations_semAn#semAn_checkTypeChangeLeft} nor
	 * {@link meta.ICheckTypeWithAnnotations_semAn#semAn_checkTypeChangeRight} are called inside
	 * packageName.prototypeName.
	 *
	 */
	public List<Tuple2<String, String>> doNotCheckIn() { return null; }

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
	public boolean allowDoNotCheckInList() { return false; }

}
