
package ast;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import cyan.lang.CyInt;
import cyan.lang.CyString;
import cyan.reflect._CyanMetaobjectAtAnnot;
import cyan.reflect._IActionFieldAccess__semAn;
import cyan.reflect._IActionFieldMissing__semAn;
import error.ErrorKind;
import lexer.Symbol;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAssignment_cge;
import meta.IActionFieldAccess_semAn;
import meta.IActionFieldMissing_semAn;
import meta.IdentStarKind;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Timeout;
import meta.Token;
import meta.Tuple2;
import meta.WrStatementAssignmentList;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
import saci.Saci;

/**
 * Represents a list of assignments such as a = b[0] = self.c = 0;
 *
 * A single assignment also represented by an object of StatementAssignmentList.
 *
 * exprList will contain "a", "b[0]", "self.c", and "0" in the above statement.
 *
 * @author José
 *
 */
public class StatementAssignmentList extends Statement {

	public StatementAssignmentList(MethodDec method) {
		super(method);
		exprList = new ArrayList<Expr>();
	}

	@Override
	public WrStatementAssignmentList getI() {
		if ( iStatementAssignmentList == null ) {
			iStatementAssignmentList = new WrStatementAssignmentList(this);
		}
		return iStatementAssignmentList;
	}

	private WrStatementAssignmentList iStatementAssignmentList = null;

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	public void add(Expr expr) {
		exprList.add(expr);
	}

	public void setExprList(List<Expr> exprList) {
		this.exprList = exprList;
	}

	public List<Expr> getExprList() {
		return exprList;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
		int n = exprList.size();
		for (final Expr e : exprList) {
			e.genCyan(pw, false, cyanEnv, genFunctions);
			n--;
			if ( n > 0 ) pw.print(" = ");
		}
	}

	@Override
	public void genJava(PWInterface pw, Env env) {

		pw.printIdent("/* ");
		genCyan(pw, false, NameServer.cyanEnv, true);
		pw.println(" */");

		final Expr rightExpr = exprList.get(exprList.size() - 1);
		final Type rightType = rightExpr.getType();
		final Expr leftSide = exprList.get(exprList.size() - 2);
		final Type leftType = leftSide.getType();

		final Tuple2<IActionAssignment_cge, ObjectDec> cyanMetaobjectPrototype = MetaInfoServer
				.getChangeAssignmentCyanMetaobject(env, leftType);
		IActionAssignment_cge changeCyanMetaobject = null;
		ObjectDec prototypeFoundMetaobject = null;
		if ( cyanMetaobjectPrototype != null ) {
			changeCyanMetaobject = cyanMetaobjectPrototype.f1;
			prototypeFoundMetaobject = cyanMetaobjectPrototype.f2;
		}

		String rightExprTmpVar = rightExpr.genJavaExpr(pw, env);
		final String rightExprTmpVarOriginal = rightExprTmpVar;

		/*
		 * cases to consider: a = expr; // a non-ref a = expr; // a is a
		 * ref-variable a[i] = expr; // a non-ref a[i] = expr; // a is a
		 * ref-variable
		 *
		 *
		 * a = expr; // a non-ref, 'a' has type Dyn a = expr; // a is a
		 * ref-variable, 'a' has type Dyn a[i] = expr; // a non-ref, 'a' has
		 * type Dyn a[i] = expr; // a is a ref-variable, 'a' has type Dyn a[i] =
		 * expr; // a non-ref, 'i' has type Dyn a[i] = expr; // a is a
		 * ref-variable, 'i' has type Dyn
		 *
		 * In any way, expr may have type Dyn or not and i may be ref or non-ref
		 *
		 */

		if ( leftSide instanceof ExprIndexed ) {
			/*
			 * cases to consider: a[i] = expr; // a non-ref a[i] = expr; // a is
			 * a ref-variable a[i] = expr; // a non-ref, 'a' has type Dyn. The
			 * type of 'i' does not matter a[i] = expr; // a is a ref-variable,
			 * 'a' has type Dyn. The type of 'i' does not matter a[i] = expr; //
			 * a non-ref, 'i' has type Dyn a[i] = expr; // a is a ref-variable,
			 * 'i' has type Dyn In any way, expr may have type Dyn or not
			 */
			final ExprIndexed leftSideIndexedExpr = (ExprIndexed) leftSide;
			final Expr indexOfExpr = leftSideIndexedExpr.getIndexOfExpr();
			final Expr indexedExpr = leftSideIndexedExpr.getIndexedExpr();

			final String indexedExprTmpVar = indexedExpr.genJavaExpr(pw, env);
			String indexOfExprTmpVar = indexOfExpr.genJavaExpr(pw, env);

			/*
			 * boolean nilSafeIndexing =
			 * exprIndexed.getFirstIndexOperator().token == Token.INTER_LEFTSB;
			 * if ( nilSafeIndexing ) { pw.printlnIdent("if ( " +
			 * indexedExprTmpVar + " != " + NameServer.NilInJava + " && " +
			 * indexedExprTmpVar + " != null ) {"); pw.add(); }
			 */
			// MethodSignatureWithKeywords ms =
			// leftSideIndexedExpr.getIndexingMethod();
			final Type mustBeTypeIndex = leftSideIndexedExpr
					.getDeclaredType_at_Parameter();
			final Type mustBeRightExprType = leftSideIndexedExpr
					.getDeclaredType_put_Parameter();

			/*
			 * a[i] = expr; a at: i put: expr; The method to be called is
			 * leftSideIndexedExpr.getIndexingMethod() whose declared type of
			 * parameter of at: is
			 * leftSideIndexedExpr.getIndexingMethod().getParameterList().get(0)
			 * .getType(env) The declared type of parameter of put: is
			 * leftSideIndexedExpr.getIndexingMethod().getParameterList().get(1)
			 * .getType(env)
			 */

			// a[i] = expr, 'a' non-Dyn, 'i' non-Dyn,
			if ( indexedExpr.getType() != Type.Dyn
					&& indexOfExpr.getType() != Type.Dyn ) {
				String s = rightExprTmpVar;
				if ( rightType == Type.Dyn ) {
					// rightType != Type.Dyn
					pw.printlnIdent("if ( !(" + rightExprTmpVar + " instanceof "
							+ mustBeRightExprType.getJavaName() + " ) ) ");

					pw.printlnIdent("    throw new ExceptionContainer__("
							+ env.javaCodeForCastException(rightExpr,
									mustBeRightExprType)
							+ " );");

					s = "((" + mustBeRightExprType.getJavaName() + " ) " + s
							+ ")";
				}

				if ( changeCyanMetaobject != null ) {

					try {
						s = changeCyanMetaobject.cge_changeRightHandSideTo(
								prototypeFoundMetaobject, s,
								rightExpr.getType(env));

					}
					catch (final error.CompileErrorException e) {
					}
					catch (final NoClassDefFoundError e) {
						final Annotation annotation = meta.GetHiddenItem
								.getHiddenCyanAnnotation(
										((CyanMetaobjectAtAnnot) changeCyanMetaobject)
												.getAnnotation());
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (final RuntimeException e) {
						final Annotation annotation = meta.GetHiddenItem
								.getHiddenCyanAnnotation(
										((CyanMetaobjectAtAnnot) changeCyanMetaobject)
												.getAnnotation());
						env.thrownException(annotation,
								annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobject(
								(meta.CyanMetaobject) changeCyanMetaobject,
								this.getFirstSymbol());
					}

				}

				/*
				 * if ( leftType == Type.Any && rightType instanceof
				 * InterfaceDec ) { pw.println(" = (" + NameServer.AnyInJava +
				 * " ) " + rightExprTmpVar + ";"); } else { pw.println(" = " +
				 * rightExprTmpVar + ";"); }
				 *
				 */

				if ( mustBeTypeIndex == Type.Any
						&& indexOfExpr.getType() instanceof InterfaceDec ) {
					indexOfExprTmpVar = " (" + MetaHelper.AnyInJava + " ) "
							+ indexOfExprTmpVar;
				}

				if ( mustBeRightExprType == Type.Any
						&& rightType instanceof InterfaceDec ) {
					s = " (" + MetaHelper.AnyInJava + " ) " + s;
				}

				/*
				 * if ( indexOfExpr.getType() instanceof TypeJavaClass &&
				 * mustBeTypeIndex.getInsideType() instanceof Prototype ) { //
				 * v[java] = expr, convert java to cyan String javaClass =
				 * indexOfExpr.getType().getName(); indexOfExprTmpVar = "new " +
				 * NameServer.cyanNameFromJavaBasicType(javaClass) + "(" +
				 * indexOfExprTmpVar + ")";
				 *
				 * }
				 */
				indexOfExprTmpVar = Type.genJavaExpr_CastJavaCyan(env,
						indexOfExprTmpVar, indexOfExpr.getType(),
						mustBeTypeIndex, getFirstSymbol());
				/*
				 * if ( mustBeRightExprType.getInsideType() instanceof Prototype
				 * && rightType instanceof TypeJavaClass ) { // v[expr] = java;
				 * // convert java to cyan String javaClass =
				 * rightType.getName(); s = "new " +
				 * NameServer.cyanNameFromJavaBasicType(javaClass) + "(" + s +
				 * ")";
				 *
				 * }
				 */
				s = Type.genJavaExpr_CastJavaCyan(env, s, rightType,
						mustBeRightExprType, getFirstSymbol());

				pw.printlnIdent(
						indexedExprTmpVar + "." + NameServer.javaNameAtPutMethod
								+ "(" + indexOfExprTmpVar + ", " + s + ");");

			}
			else if ( indexedExpr.getType() != Type.Dyn
					&& indexOfExpr.getType() == Type.Dyn ) {
				// a[i] = expr, 'a' non-Dyn, 'i' Dyn

				/*
				 * try to cast the index 'i' to the correct type
				 */

				pw.printlnIdent("if ( !(" + indexOfExprTmpVar + " instanceof "
						+ mustBeTypeIndex.getJavaName() + " ) ) ");

				pw.printlnIdent("    throw new ExceptionContainer__(" + env
						.javaCodeForCastException(indexOfExpr, mustBeTypeIndex)
						+ " );");

				/*
				 * cast rightExpr to the correct type
				 */
				String s = rightExprTmpVar;
				if ( rightType == Type.Dyn ) {
					// rightType != Type.Dyn
					pw.printlnIdent("if ( !(" + rightExprTmpVar + " instanceof "
							+ mustBeRightExprType.getJavaName() + " ) ) ");

					pw.printlnIdent("    throw new ExceptionContainer__("
							+ env.javaCodeForCastException(rightExpr,
									mustBeRightExprType)
							+ " );");

					s = "((" + mustBeRightExprType.getJavaName() + " ) " + s
							+ ")";
				}

				if ( changeCyanMetaobject != null ) {

					try {
						s = changeCyanMetaobject.cge_changeRightHandSideTo(
								prototypeFoundMetaobject, s, rightType);
					}
					catch (final error.CompileErrorException e) {
					}
					catch (final NoClassDefFoundError e) {
						final Annotation annotation = meta.GetHiddenItem
								.getHiddenCyanAnnotation(
										((CyanMetaobjectAtAnnot) changeCyanMetaobject)
												.getAnnotation());
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (final RuntimeException e) {
						final Annotation annotation = meta.GetHiddenItem
								.getHiddenCyanAnnotation(
										((CyanMetaobjectAtAnnot) changeCyanMetaobject)
												.getAnnotation());
						env.thrownException(annotation,
								annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobject(
								(meta.CyanMetaobject) changeCyanMetaobject,
								this.getFirstSymbol());
					}

				}

				if ( mustBeTypeIndex == Type.Any
						&& indexOfExpr.getType() instanceof InterfaceDec ) {
					indexOfExprTmpVar = " (" + MetaHelper.AnyInJava + " ) "
							+ indexOfExprTmpVar;
				}

				if ( mustBeRightExprType == Type.Any
						&& rightType instanceof InterfaceDec ) {
					s = " (" + MetaHelper.AnyInJava + " ) " + s;
				}

				pw.printlnIdent(
						indexedExprTmpVar + "." + NameServer.javaNameAtPutMethod
								+ "( (" + mustBeTypeIndex.getJavaName() + " ) "
								+ indexOfExprTmpVar + ", " + s + ");");
			}
			else {
				// indexedExpr.getType() == Type.Dyn

				final String aMethodTmp = NameServer
						.nextJavaLocalVariableName();

				pw.printlnIdent("java.lang.reflect.Method " + aMethodTmp
						+ " = CyanRuntime.getJavaMethodByName("
						+ indexedExprTmpVar + ".getClass(), \""
						+ NameServer.javaNameAtPutMethod + "\", 2);");
				final int lineNumber = indexedExpr.getFirstSymbol()
						.getLineNumber();
				pw.printlnIdent("if ( " + aMethodTmp
						+ " == null ) throw new ExceptionContainer__( new _ExceptionMethodNotFound( new CyString(\"Method called at line \" + "
						+ lineNumber + "+ \" of prototype '"
						+ env.getCurrentPrototype().getFullName()
						+ "' was not found\") ) );");
				String resultTmpVar = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent("Object " + resultTmpVar + " = null;");
				pw.printlnIdent("try {");
				pw.add();

				if ( changeCyanMetaobject != null ) {

					try {
						rightExprTmpVar = changeCyanMetaobject
								.cge_changeRightHandSideTo(
										prototypeFoundMetaobject,
										rightExprTmpVar,
										rightExpr.getType(env));

					}
					catch (final error.CompileErrorException e) {
					}
					catch (final NoClassDefFoundError e) {
						final Annotation annotation = meta.GetHiddenItem
								.getHiddenCyanAnnotation(
										((CyanMetaobjectAtAnnot) changeCyanMetaobject)
												.getAnnotation());
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (final RuntimeException e) {
						final Annotation annotation = meta.GetHiddenItem
								.getHiddenCyanAnnotation(
										((CyanMetaobjectAtAnnot) changeCyanMetaobject)
												.getAnnotation());
						env.thrownException(annotation,
								annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobject(
								(meta.CyanMetaobject) changeCyanMetaobject,
								this.getFirstSymbol());
					}

				}
				pw.printlnIdent(aMethodTmp + ".setAccessible(true);");

				pw.printlnIdent(resultTmpVar + " = " + aMethodTmp + ".invoke("
						+ indexedExprTmpVar + ", " + indexOfExprTmpVar + ", "
						+ rightExprTmpVar + ");");
				pw.sub();
				pw.printlnIdent("}");

				String ep = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent(
						"catch ( java.lang.reflect.InvocationTargetException "
								+ ep + " ) {");
				pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
				pw.printlnIdent(
						"	if ( t__ instanceof ExceptionContainer__ ) {");
				pw.printlnIdent(
						"    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
				pw.printlnIdent("	}");
				pw.printlnIdent("	else");
				pw.printlnIdent(
						"		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
				pw.printlnIdent("}");

				ep = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent(
						"catch (IllegalAccessException | IllegalArgumentException "
								+ ep + ") {");
				pw.add();

				final String dnuTmpVar = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent(
						"//	func doesNotUnderstand: (String methodName, Array<Array<Dyn>> args)");
				pw.printlnIdent("java.lang.reflect.Method " + dnuTmpVar
						+ " = CyanRuntime.getJavaMethodByName("
						+ indexedExprTmpVar + ".getClass(), \""
						+ NameServer.javaNameDoesNotUnderstand + "\", 2);");
				resultTmpVar = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent("Object " + resultTmpVar + " = null;");
				pw.printlnIdent("try {");
				pw.add();
				pw.printlnIdent(aMethodTmp + ".setAccessible(true);");

				pw.printlnIdent(resultTmpVar + " = " + aMethodTmp + ".invoke("
						+ indexedExprTmpVar + ", " + indexOfExprTmpVar + ", "
						+ rightExprTmpVar + ");");
				pw.sub();

				pw.printlnIdent("}");
				ep = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent(
						"catch ( java.lang.reflect.InvocationTargetException "
								+ ep + " ) {");
				pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
				pw.printlnIdent(
						"	if ( t__ instanceof ExceptionContainer__ ) {");
				pw.printlnIdent(
						"    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
				pw.printlnIdent("	}");
				pw.printlnIdent("	else");
				pw.printlnIdent(
						"		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
				pw.printlnIdent("}");
				pw.printlnIdent(
						"catch (IllegalAccessException | IllegalArgumentException "
								+ ep + ") {");
				pw.printlnIdent(
						"        throw new ExceptionContainer__( new _ExceptionMethodNotFound( new CyString(\"Method called at line \" + "
								+ lineNumber + "+ \" of prototype '"
								+ env.getCurrentPrototype().getFullName()
								+ "' was not found\") ) );");
				pw.printlnIdent("}");
				pw.sub();
				pw.printlnIdent("}");

			}

		}
		else {

			if ( changeCyanMetaobject != null ) {
				/*
				 * assignment is changed by the metaobject attached to the
				 * prototype that is the type of the right-hand side
				 */

				try {
					rightExprTmpVar = changeCyanMetaobject
							.cge_changeRightHandSideTo(prototypeFoundMetaobject,
									rightExprTmpVar, rightExpr.getType(env));
				}
				catch (final error.CompileErrorException e) {
				}
				catch (final NoClassDefFoundError e) {
					final Annotation annotation = meta.GetHiddenItem
							.getHiddenCyanAnnotation(
									((CyanMetaobjectAtAnnot) changeCyanMetaobject)
											.getAnnotation());
					env.error(annotation.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (final RuntimeException e) {
					final Annotation annotation = meta.GetHiddenItem
							.getHiddenCyanAnnotation(
									((CyanMetaobjectAtAnnot) changeCyanMetaobject)
											.getAnnotation());
					env.thrownException(annotation, annotation.getFirstSymbol(),
							e);
				}
				finally {
					env.errorInMetaobject(
							(meta.CyanMetaobject) changeCyanMetaobject,
							this.getFirstSymbol());
				}

			}

			/*
			 * cases to consider: a = expr; // a non-ref a = expr; // a is a
			 * ref-variable
			 */

			if ( rightType == Type.Dyn && leftType != Type.Dyn ) {
				/*
				 * it is necessary a conversion if the assignment if of the kind
				 * nonDyn = Dyn;
				 */

				pw.printlnIdent("if ( " + rightExprTmpVarOriginal
						+ " instanceof " + leftType.getJavaName() + " ) {");
				pw.add();

				pw.printIdent("");
				((LeftHandSideAssignment) leftSide).genJavaCodeVariable(pw,
						env);
				pw.println(" = (" + leftType.getJavaName() + " ) "
						+ rightExprTmpVar + ";");

				pw.sub();
				pw.printlnIdent("}");
				pw.printlnIdent("else {");
				pw.add();

				pw.printlnIdent("throw new ExceptionContainer__("
						+ env.javaCodeForCastException(rightExpr, leftType)
						+ " );");

				pw.sub();
				pw.printlnIdent("}");
			}
			else {
				// regular assignment
				pw.printIdent("");

				rightExprTmpVar = Type.genJavaExpr_CastJavaCyan(env,
						rightExprTmpVar, rightType, leftType, getFirstSymbol());

				((LeftHandSideAssignment) leftSide).genJavaCodeVariable(pw,
						env);
				if ( leftType == Type.Any
						&& rightType instanceof InterfaceDec ) {
					pw.println(" = (" + MetaHelper.AnyInJava + " ) "
							+ rightExprTmpVar + ";");
				}
				else {
					if ( rightType == Type.Nil ) {
						pw.println(" = _Nil.prototype;");
					}
					else {
						pw.println(" = " + rightExprTmpVar + ";");
					}
				}

				/*
				 * if ( leftType instanceof TypeJavaRef &&
				 * rightType.getInsideType() instanceof Prototype ) { // java =
				 * cyan String puName = rightType.getName(); if (
				 * NameServer.isBasicType(puName) ) { //cast Cyan basic type
				 * value to Java basic type value rightExprTmpVar =
				 * rightExprTmpVar + "." + NameServer.getFieldBasicType(puName);
				 * } ((LeftHandSideAssignment )
				 * leftSide).genJavaCodeVariable(pw, env); pw.println(" = " +
				 * rightExprTmpVar + ";"); } else if ( rightType instanceof
				 * TypeJavaRef && leftType.getInsideType() instanceof Prototype
				 * ) {
				 *
				 * boolean arrayLeftRight = false; // ********************** //
				 * Array<T> = T[] if ( leftType instanceof ObjectDec ) {
				 * ObjectDec leftObjType = (ObjectDec ) leftType; String
				 * thisName = leftType.getName(); int indexOfLessThan =
				 * thisName.indexOf('<'); if ( indexOfLessThan > 0 ) { if (
				 * thisName.subSequence(0, indexOfLessThan).equals("Array") ) {
				 * // an array in the left hand side. The right-hand side may be
				 * a Java Array String otherCanonicalName = ((TypeJavaClass)
				 * rightType).getaClass(env,
				 * getFirstSymbol()).getCanonicalName(); if (
				 * otherCanonicalName.endsWith("[]") ) { String arrayElementName
				 * = otherCanonicalName.substring(0,
				 * otherCanonicalName.length()-2); if (
				 * arrayElementName.startsWith("java.lang") ) { arrayElementName
				 * = arrayElementName.substring("java.lang".length()); } Type
				 * thisArrayElementType =
				 * leftObjType.getGenericParameterListList().get(0).get(0).
				 * getParameter().getType(); if (
				 * TypeJavaRef.cyanTypeIsSupertypeofJavaTypeByName(
				 * thisArrayElementType.getName(), arrayElementName) ) { String
				 * tmpCyanArray = NameServer.nextJavaLocalVariableName();
				 * pw.printlnIdent(leftType.getJavaName() + " " + tmpCyanArray +
				 * " = " + leftType.getJavaName() + "();"); String
				 * tmpCyanArrayElem = NameServer.nextJavaLocalVariableName();
				 * pw.printlnIdent("for ( " + thisArrayElementType.getJavaName()
				 * + " " + tmpCyanArrayElem + " : " + rightExprTmpVar + ") {");
				 * pw.add(); pw.printlnIdent(tmpCyanArray + "._add(" +
				 * tmpCyanArrayElem + ");"); pw.sub(); pw.printlnIdent("}");
				 * arrayLeftRight = true; rightExprTmpVar = tmpCyanArray; } } }
				 * } } if ( !arrayLeftRight ) { // cyan = java String javaClass
				 * = rightType.getName(); rightExprTmpVar = "(new " +
				 * NameServer.cyanNameFromJavaBasicType(javaClass) + "(" +
				 * rightExprTmpVar + "))";
				 *
				 * }
				 *
				 * // **********************
				 *
				 *
				 * ((LeftHandSideAssignment ) leftSide).genJavaCodeVariable(pw,
				 * env); pw.println(" = " + rightExprTmpVar + ";");
				 *
				 *
				 *
				 *
				 *
				 *
				 *
				 *
				 * } else { ((LeftHandSideAssignment )
				 * leftSide).genJavaCodeVariable(pw, env); if ( leftType ==
				 * Type.Any && rightType instanceof InterfaceDec ) {
				 * pw.println(" = (" + NameServer.AnyInJava + " ) " +
				 * rightExprTmpVar + ";"); } else { pw.println(" = " +
				 * rightExprTmpVar + ";"); } }
				 */
			}

		}

		if ( env.getAddTypeInfo() && leftType == Type.Dyn
				&& leftSide instanceof ExprIdentStar ) {
			addLocalVariableTypeInfo(pw, env, leftSide);
		}

		// }

		/*
		 * multiple assignment are no longer allowed
		 */
		/*
		 * for (int i = size - 3; i >= 1; i--) {
		 * exprList.get(i).genJavaExprWithoutTmpVar(pwChar, env);
		 * pwChar.printlnIdent(" = " + varTmpName + ";"); }
		 */
	}

	/**
	 * @param pw
	 * @param env
	 * @param leftSide
	 */
	static void addLocalVariableTypeInfo(PWInterface pw, Env env,
			final Expr leftSide) {
		ExprIdentStar e = (ExprIdentStar) leftSide;
		if ( e.getIdentSymbolArray().size() == 1
				&& e.getVarDeclaration() != null ) {
			VariableDecInterface vdi = e.getVarDeclaration();

			pw.println(vdi.codeAddsRuntimeTypeInfo());
		}
	}

	@Override
	public Symbol getFirstSymbol() {
		// TODO Auto-generated method stub
		return exprList.get(0).getFirstSymbol();
	}

	@Override
	public void calcInternalTypes(Env env) {

		boolean leftSide_IsFieldMissing = false;
		boolean check = true;
		int size = exprList.size();
		final int originalSize = size;
		for (final Expr expr : exprList) {

			/*
			 * ugly, but works
			 */
			if ( expr instanceof ExprSelfPeriodIdent && originalSize == size ) {
				final ExprSelfPeriodIdent selfDotIdent = (ExprSelfPeriodIdent) expr;
				final FieldDec fieldDec = env.getCurrentObjectDec().searchField(
						selfDotIdent.getIdentSymbol().getSymbolString());
				if ( fieldDec == null ) {
					leftSide_IsFieldMissing = true;
					check = false;
				}
				else {
					MethodDec cm = env.getCurrentMethod();
					if ( cm != null && cm.getShared() ) {
						if ( !fieldDec.isShared() ) {
							env.error(expr.getFirstSymbol(),
									"Shared methods cannot access non-shared fields. Method '"
											+ cm.getName()
											+ " is accessing field '"
											+ fieldDec.getName() + "'");
						}
					}
				}
			}
			if ( check ) {
				expr.calcInternalTypes(env, --size > 0);
			}
			check = true;
		}

		size = exprList.size();
		for (int i = size - 1; i >= 1; i--) {
			final Expr leftExpr = exprList.get(i - 1);

			if ( leftExpr instanceof ExprIdentStar
					&& ((ExprIdentStar) leftExpr)
							.getIdentStarKind() == IdentStarKind.variable_t
					&& (((ExprIdentStar) leftExpr)
							.getVarDeclaration() instanceof ParameterDec) ) {
				env.error(true, leftExpr.getFirstSymbol(),
						"Attempt to assign a value to the parameter '"
								+ leftExpr.asString()
								+ "'. Parameters are read-only in Cyan",
						leftExpr.asString(),
						ErrorKind.attempt_to_assign_a_value_to_a_readonly_variable);
			}

			boolean leftHandSideIsValid = true;
			if ( !(leftExpr instanceof LeftHandSideAssignment)
					&& !(leftExpr instanceof ExprIndexed) ) {
				leftHandSideIsValid = false;
			}
			final MethodDec m = env.getCurrentMethod();
			if ( leftExpr instanceof ExprIdentStar ) {
				final IdentStarKind kind = ((ExprIdentStar) leftExpr)
						.getIdentStarKind();
				leftHandSideIsValid = kind == IdentStarKind.instance_variable_t
						|| kind == IdentStarKind.variable_t;
				/*
				 * if ( kind == IdentStarKind.instance_variable_t &&
				 * m.getReadOnly() ) { if ( m.getMethodSignature() instanceof
				 * ast.MethodSignatureOperator ) {
				 * env.error(leftExpr.getFirstSymbol(),
				 * "This is a read-only method. " +
				 * "fields cannot be changed. All methods whose names are operators (such as +) are read-only"
				 * ); } else { env.error(leftExpr.getFirstSymbol(),
				 * "This is a read-only method. fields cannot be changed"); } }
				 */
			}
			if ( !leftHandSideIsValid ) {
				env.error(leftExpr.getFirstSymbol(),
						"The left-hand side of '=' is not valid. It should be a variable or an indexed expression");
			}
			boolean isInit = false;
			boolean isInitShared = false;
			if ( m != null ) {
				final String methodName = m.getNameWithoutParamNumber();
				isInit = methodName.equals("init")
						|| methodName.equals("init:");
				isInitShared = methodName.equals("initShared");
			}
			if ( !isInit && !isInitShared ) {
				/*
				 * inside 'init' or 'init:' methods you can assign values to
				 * fields that are declared with 'let'. Outside these methods
				 * you cannot.
				 */
				if ( leftExpr instanceof ExprIdentStar ) {
					final ExprIdentStar varId = (ExprIdentStar) leftExpr;
					if ( varId.getVarDeclaration() != null
							&& varId.getVarDeclaration().isReadonly() ) {
						final VariableDecInterface iv = varId
								.getVarDeclaration();
						if ( iv instanceof StatementLocalVariableDec
								&& ((StatementLocalVariableDec) iv)
										.getCastLocalVariable() ) {

							StatementLocalVariableDec svar = (StatementLocalVariableDec) iv;
							env.error(true, leftExpr.getFirstSymbol(),
									"Attempt to assign a value to the read only variable '"
											+ iv.getName()
											+ "'. A variable declared in a 'cast' statement, between 'cast' and '{', "
											+ "is always read only",
									iv.getName(),
									ErrorKind.attempt_to_assign_a_value_to_a_readonly_variable);
						}
						else {
							env.error(true, leftExpr.getFirstSymbol(),
									"Attempt to assign a value to a read only variable. To make this variable writable, declare it as 'var "
											+ iv.getType().getFullName() + " "
											+ iv.getName() + "'",
									iv.getName(),
									ErrorKind.attempt_to_assign_a_value_to_a_readonly_variable);
						}
					}
				}
				else if ( leftExpr instanceof ExprSelf__PeriodIdent ) {
					final FieldDec iv = ((ExprSelf__PeriodIdent) leftExpr)
							.getFieldDec();
					if ( iv.isReadonly() ) {
						env.error(true, leftExpr.getFirstSymbol(),
								"Attempt to assign a value to a read only variable. To make this variable writable, declare it as 'var "
										+ iv.getType().getFullName() + " "
										+ iv.getName() + "'",
								iv.getName(),
								ErrorKind.attempt_to_assign_a_value_to_a_readonly_variable);
					}
				}
				else if ( leftExpr instanceof ExprSelfPeriodIdent
						&& !leftSide_IsFieldMissing ) {
					final FieldDec iv = ((ExprSelfPeriodIdent) leftExpr)
							.getFieldDec();
					if ( iv.isReadonly() ) {
						env.error(true, leftExpr.getFirstSymbol(),
								"Attempt to assign a value to a read only variable. To make this variable writable, declare it as 'var "
										+ iv.getType().getFullName() + " "
										+ iv.getName() + "'",
								iv.getName(),
								ErrorKind.attempt_to_assign_a_value_to_a_readonly_variable);
					}

				}
			}
			final Expr rightExpr = exprList.get(i);
			final Type rightTypeExpr = rightExpr.getType(env);

			if ( !leftSide_IsFieldMissing ) {

				final Type leftTypeExpr = exprList.get(i - 1).getType(env);
				if ( !leftTypeExpr.isSupertypeOf(rightTypeExpr, env) ) {
					// leftTypeExpr.isSupertypeOf(rightTypeExpr, env);
					rightTypeExpr.getFullName();
					leftTypeExpr.getFullName();
					env.error(true, exprList.get(i - 1).getFirstSymbol(),
							"The type of the right-hand side of this assignment, "
									+ rightTypeExpr.getFullName()
									+ " is not a subtype of the left-hand side, "
									+ leftTypeExpr.getFullName(),
							null,
							ErrorKind.type_error_type_of_right_hand_side_of_assignment_is_not_a_subtype_of_the_type_of_left_hand_side);
				}

				if ( env.getProject().getCompilerManager()
						.getCompilationStep() == CompilationStep.step_6 ) {
					MetaInfoServer.checkAssignmentPluggableTypeSystem(env,
							leftTypeExpr, leftExpr,
							LeftHandSideKind.LeftSideAssignment_LHS,
							rightTypeExpr, rightExpr);

					if ( leftExpr instanceof ExprIdentStar
							&& ((ExprIdentStar) leftExpr)
									.getIdentStarKind() == IdentStarKind.instance_variable_t ) {
						// FieldDec iv = (FieldDec ) ((ExprIdentStar )
						// leftExpr).getVarDeclaration();
						StatementAssignmentList.replaceSetFieldAccess(env,
								leftExpr, rightExpr, this,
								(FieldDec) ((ExprIdentStar) leftExpr)
										.getVarDeclaration());
					}
					else if ( leftExpr instanceof ExprSelfPeriodIdent ) {
						// FieldDec iv = (FieldDec ) ((ExprIdentStar )
						// leftExpr).getVarDeclaration();
						StatementAssignmentList.replaceSetFieldAccess(env,
								leftExpr, rightExpr, this,
								((ExprSelfPeriodIdent) leftExpr).getFieldDec());
					}

				}
			}
			else {
				if ( env.getProject().getCompilerManager()
						.getCompilationStep() == CompilationStep.step_6 ) {

					if ( replaceSetFieldMissingAccess(env,
							(ExprSelfPeriodIdent) leftExpr, rightExpr) ) {
						/*
						 * field is virtual, therefore no other checks apply to
						 * it.
						 */
						continue;
					}

					leftExpr.calcInternalTypes(env, true);
					if ( !leftExpr.getType().isSupertypeOf(rightTypeExpr,
							env) ) {
						env.error(true, exprList.get(i - 1).getFirstSymbol(),
								"The type of the right-hand side of this assignment, "
										+ rightTypeExpr.getFullName()
										+ " is not a subtype of the left-hand side, "
										+ leftExpr.getType().getFullName(),
								null,
								ErrorKind.type_error_type_of_right_hand_side_of_assignment_is_not_a_subtype_of_the_type_of_left_hand_side);
					}

				}

			}

			if ( leftExpr instanceof ExprIdentStar && ((ExprIdentStar) leftExpr)
					.getIdentStarKind() == IdentStarKind.instance_variable_t ) {
				env.getCurrentMethod().addAssignedToFieldList(
						(FieldDec) ((ExprIdentStar) leftExpr)
								.getVarDeclaration());
			}
			else if ( leftExpr instanceof ast.ExprSelfPeriodIdent ) {
				env.getCurrentMethod().addAssignedToFieldList(
						((ExprSelfPeriodIdent) leftExpr).getFieldDec());
			}

			if ( leftExpr instanceof ExprIdentStar && ((ExprIdentStar) leftExpr)
					.getIdentStarKind() == IdentStarKind.variable_t ) {
				final StatementLocalVariableDec varDec = (StatementLocalVariableDec) ((ExprIdentStar) leftExpr)
						.getVarDeclaration();
				env.setLocalVariableAsInitializedWith(varDec, rightExpr);
			}

		}
		// type = exprList.get(0).getType(env);

		/*
		 * push variables and their levels into a stack of initialized variables
		 *
		 * for ( int i = 0; i < size - 1; ++i) { Expr expr = exprList.get(i); if
		 * ( expr instanceof ExprIdentStar ) { ExprIdentStar eis =
		 * (ExprIdentStar ) expr; if ( eis.getIdentSymbolArray().size() == 1 ) {
		 * // a variable is being initialized String name =
		 * eis.getIdentSymbolArray().get(0).symbolString;
		 * env.pushVariableAndLevel(name); } } }
		 */
		super.calcInternalTypes(env);
	}

	public static void replaceSetFieldAccess(Env env, Expr leftExpr,
			Expr rightExpr, Statement object, FieldDec varDec) {

		final List<AnnotationAt> annotList = varDec.getAttachedAnnotationList();

		if ( annotList == null ) {
			return;
		}
		int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(env,
				env.getProject().getProgram().getI(),
				env.getCurrentCompilationUnit().getCyanPackage().getI(),
				object.getFirstSymbol());

		for (final AnnotationAt annot : annotList) {

			final CyanMetaobjectAtAnnot cyanMetaobject = annot
					.getCyanMetaobject();
			_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot) cyanMetaobject
					.getMetaobjectInCyan();

			if ( cyanMetaobject instanceof IActionFieldAccess_semAn
					|| (other != null
							&& other instanceof _IActionFieldAccess__semAn) ) {

				StringBuffer sb = null;
				try {
					Timeout<StringBuffer> to = new Timeout<>();

					if ( other == null ) {
						final IActionFieldAccess_semAn access = (IActionFieldAccess_semAn) cyanMetaobject;

						if ( Saci.timeLimitForMetaobjects ) {
							sb = to.run(() -> {
								return access.semAn_replaceSetField(
										leftExpr.getI(), rightExpr.getI(),
										env.getI());
							}, timeoutMilliseconds, "semAn_replaceSetField",
									cyanMetaobject, env);

						}
						else {
							sb = access.semAn_replaceSetField(leftExpr.getI(),
									rightExpr.getI(), env.getI());
						}

						// sb = access.semAn_replaceSetField(leftExpr.getI(),
						// rightExpr.getI(), env.getI());
					}
					else {
						sb = to.run(() -> {
							return new StringBuffer(
									((_IActionFieldAccess__semAn) other)
											._semAn__replaceSetField_3(
													leftExpr.getI(),
													rightExpr.getI(),
													env.getI()).s);
						}, timeoutMilliseconds, "semAn_replaceSetField",
								cyanMetaobject, env);

						// sb = new StringBuffer( ((_IActionFieldAccess__semAn )
						// other)._semAn__replaceSetField_3(
						// leftExpr.getI(), rightExpr.getI(), env.getI()).s);
					}
				}
				catch (final error.CompileErrorException e) {
				}
				catch (final NoClassDefFoundError e) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (final RuntimeException e) {
					env.thrownException(annot, object.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobject(cyanMetaobject,
							object.getFirstSymbol());
				}

				if ( sb != null && sb.length() != 0 ) {
					final Symbol symForError = object.getFirstSymbol();

					if ( leftExpr.getCodeThatReplacesThisExpr() != null ) {
						/*
						 * this field access has already been replaced by
						 * another expression
						 */
						if ( object
								.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
							env.warning(symForError, "Metaobject annotation '"
									+ cyanMetaobject.getName() + "' at line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ annot.getPackageOfAnnotation() + "."
									+ annot.getPackageOfAnnotation()
									+ " is trying to replace the field access '"
									+ leftExpr.asString()
									+ "' by an expression. But this has already been asked by metaobject annotation '"
									+ leftExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getCyanMetaobject().getName()
									+ "'" + " at line "
									+ leftExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ leftExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getPackageOfAnnotation()
									+ "."
									+ leftExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getPackageOfAnnotation());
						}
						else {
							env.warning(symForError, "Metaobject annotation '"
									+ cyanMetaobject.getName() + "' at line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ annot.getPackageOfAnnotation() + "."
									+ annot.getPackageOfAnnotation()
									+ " is trying to replace the field access '"
									+ leftExpr.asString()
									+ "' by an expression. But this has already been asked by someone else");
						}
					}

					// if there is any errors, signals them
					env.errorInMetaobject(cyanMetaobject, symForError);

					// Type typeOfCode = leftExpr.type;

					int offsetNext = object.nextSymbol.getOffset();
					if ( rightExpr.getNextSymbol() != null && rightExpr
							.getNextSymbol().token == Token.SEMICOLON ) {
						offsetNext = rightExpr.getNextSymbol().getOffset() + 1;
					}
					env.removeAddCodeFromToOffset(object, offsetNext, annot, sb,
							null);
					// env.removeAddCodeStatement(this, annot, sb, null);
					/*
					 *
					 */
					// removeCodeFromToOffset(int offsetStart, int offsetNext,
					// CompilationUnitSuper compilationUnit,
					// AnnotationAt annotation)

					object.setCyanAnnotationThatReplacedMSbyExpr(annot);

					return;
				}
			}
		}
	}

	/**
	 * return true if a metaobject replaced the assignment, false otherwise
	 *
	 * @param env
	 * @param leftExpr
	 * @param rightExpr
	 * @return
	 */
	private boolean replaceSetFieldMissingAccess(Env env,
			ExprSelfPeriodIdent leftExpr, Expr rightExpr) {

		final List<AnnotationAt> annotList = env.getCurrentPrototype()
				.getAttachedAnnotationList();

		if ( annotList == null ) {
			return false;
		}
		for (final AnnotationAt annot : annotList) {

			final CyanMetaobjectAtAnnot cyanMetaobject = annot
					.getCyanMetaobject();

			_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot) cyanMetaobject
					.getMetaobjectInCyan();

			if ( cyanMetaobject instanceof IActionFieldMissing_semAn
					|| (other != null
							&& other instanceof _IActionFieldMissing__semAn) ) {

				StringBuffer sb = null;
				try {
					int timeoutMilliseconds = Timeout
							.getTimeoutMilliseconds(env,
									env.getProject().getProgram().getI(),
									env.getCurrentCompilationUnit()
											.getCyanPackage().getI(),
									this.getFirstSymbol());
					if ( other == null ) {
						final IActionFieldMissing_semAn access = (IActionFieldMissing_semAn) cyanMetaobject;
						Timeout<StringBuffer> to = new Timeout<>();
						if ( Saci.timeLimitForMetaobjects ) {
							sb = to.run(() -> {
								return access.semAn_replaceSetMissingField(
										leftExpr.getI(), rightExpr.getI(),
										env.getI());
							}, timeoutMilliseconds,
									"semAn_replaceSetMissingField",
									cyanMetaobject, env);

						}
						else {
							sb = access.semAn_replaceSetMissingField(
									leftExpr.getI(), rightExpr.getI(),
									env.getI());
						}

						// sb =
						// access.semAn_replaceSetMissingField(leftExpr.getI(),
						// rightExpr.getI(), env.getI());
					}
					else {
						Timeout<CyString> to = new Timeout<>();
						CyString cyTuple = to.run(() -> {
							return ((_IActionFieldMissing__semAn) other)
									._semAn__replaceSetMissingField_3(
											leftExpr.getI(), rightExpr.getI(),
											env.getI());
						}, timeoutMilliseconds, "semAn_replaceSetMissingField",
								cyanMetaobject, env);

						// CyString cyTuple = ((_IActionFieldMissing__semAn)
						// other )
						// ._semAn__replaceSetMissingField_3(leftExpr.getI(),
						// rightExpr.getI(), env.getI());

						String f1 = cyTuple.s;
						if ( f1.length() != 0 ) {
							sb = new StringBuffer(f1);
						}
					}
				}
				catch (final error.CompileErrorException e) {
				}
				catch (final NoClassDefFoundError e) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (final RuntimeException e) {
					env.thrownException(annot, this.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobject(cyanMetaobject,
							this.getFirstSymbol());
				}

				if ( sb != null && sb.length() != 0 ) {
					final Symbol symForError = this.getFirstSymbol();

					if ( leftExpr.getCodeThatReplacesThisExpr() != null ) {
						/*
						 * this field access has already been replaced by
						 * another expression
						 */
						if ( this
								.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
							env.warning(symForError, "Metaobject annotation '"
									+ cyanMetaobject.getName() + "' at line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ annot.getPackageOfAnnotation() + "."
									+ annot.getPackageOfAnnotation()
									+ " is trying to replace the field access '"
									+ leftExpr.asString()
									+ "' by an expression. But this has already been asked by metaobject annotation '"
									+ leftExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getCyanMetaobject().getName()
									+ "'" + " at line "
									+ leftExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ leftExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getPackageOfAnnotation()
									+ "."
									+ leftExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getPackageOfAnnotation());
						}
						else {
							env.warning(symForError, "Metaobject annotation '"
									+ cyanMetaobject.getName() + "' at line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ annot.getPackageOfAnnotation() + "."
									+ annot.getPackageOfAnnotation()
									+ " is trying to replace the field access '"
									+ leftExpr.asString()
									+ "' by an expression. But this has already been asked by someone else");
						}
					}

					// if there is any errors, signals them
					env.errorInMetaobject(cyanMetaobject, symForError);

					// Type typeOfCode = leftExpr.type;

					// final Type codeType = env.searchPackagePrototype(t.f1,
					// t.f2);
					// if ( codeType == null ) {
					// env.error(leftExpr.getFirstSymbol(), "field '" +
					// leftExpr.getIdentSymbol().getSymbolString() +
					// "' has type '" + t.f1 + "." + t.f2 + "' according to
					// metaobject '"
					// + cyanMetaobject.getName() + "' attached to this
					// prototype. But this type does not exist", true, true);
					// }
					// leftExpr.setFieldType(codeType);
					int offsetNext = this.nextSymbol.getOffset();
					if ( rightExpr.getNextSymbol() != null && rightExpr
							.getNextSymbol().token == Token.SEMICOLON ) {
						offsetNext = rightExpr.getNextSymbol().getOffset() + 1;
					}
					env.removeAddCodeFromToOffset(this, offsetNext, annot, sb,
							null);

					this.setCyanAnnotationThatReplacedMSbyExpr(annot);

					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Object eval(EvalEnv ee) {

		final boolean oneWasTrue = false;
		final int size = exprList.size();
		for (int i = size - 1; i > 0; i--) {

			final Expr rightExpr = exprList.get(i);
			final Type rightType = rightExpr.getType();
			final Expr leftSide = exprList.get(i - 1);
			final Type leftType = leftSide.getType();

			Object rightValue = rightExpr.eval(ee);
			/*
			 * cases to consider: a = expr; // a non-ref a = expr; // a is a
			 * ref-variable a[i] = expr; // a non-ref a[i] = expr; // a is a
			 * ref-variable
			 *
			 *
			 * a = expr; // a non-ref, 'a' has type Dyn a = expr; // a is a
			 * ref-variable, 'a' has type Dyn a[i] = expr; // a non-ref, 'a' has
			 * type Dyn a[i] = expr; // a is a ref-variable, 'a' has type Dyn
			 * a[i] = expr; // a non-ref, 'i' has type Dyn a[i] = expr; // a is
			 * a ref-variable, 'i' has type Dyn
			 *
			 * In any way, expr may have type Dyn or not and i may be ref or
			 * non-ref
			 *
			 */

			if ( leftSide instanceof ExprIndexed ) {
				/*
				 * cases to consider: a[i] = expr; // a non-ref a[i] = expr; //
				 * a is a ref-variable a[i] = expr; // a non-ref, 'a' has type
				 * Dyn. The type of 'i' does not matter a[i] = expr; // a is a
				 * ref-variable, 'a' has type Dyn. The type of 'i' does not
				 * matter a[i] = expr; // a non-ref, 'i' has type Dyn a[i] =
				 * expr; // a is a ref-variable, 'i' has type Dyn In any way,
				 * expr may have type Dyn or not
				 */
				final ExprIndexed leftSideIndexedExpr = (ExprIndexed) leftSide;
				final Expr indexOfExpr = leftSideIndexedExpr.getIndexOfExpr();
				final Expr indexedExpr = leftSideIndexedExpr.getIndexedExpr();
				Object indexedExprValue = indexedExpr.eval(ee);
				Object indexOfExprValue = indexOfExpr.eval(ee);
				if ( indexedExprValue == null || indexOfExprValue == null ) {
					return null;
				}

				if ( indexedExprValue instanceof cyanruntime.Ref<?> ) {
					indexedExprValue = ((cyanruntime.Ref<?>) indexedExprValue).elem;
				}
				if ( indexOfExprValue instanceof cyanruntime.Ref<?> ) {
					indexOfExprValue = ((cyanruntime.Ref<?>) indexOfExprValue).elem;
				}
				Class<?> indexedValueClass = indexedExprValue.getClass();

				if ( EvalEnv.any.isAssignableFrom(indexedValueClass) ) { // cyan.lang._Any
					// a Cyan value
					// java.lang.reflect.Method method;
					//
					// try {
					//
					// method =
					// indexedExprValue.getClass().getMethod(NameServer.javaNameAtPutMethod,
					// new Class<?> [] { indexOfExprValue.getClass(),
					// rightValue.getClass() } );
					// method.invoke(indexedExprValue, indexOfExprValue,
					// rightValue);
					//
					// }
					// catch ( IllegalAccessException | IllegalArgumentException
					// | InvocationTargetException | NoSuchMethodException |
					// SecurityException e ) {
					// ee.error(this.getFirstSymbol(), "Error when calling
					// indexing method for assignment");
					// }

					boolean found = false;
					for (Method method : indexedValueClass.getMethods()) {
						if ( method.getName()
								.equals(NameServer.javaNameAtPutMethod) ) {
							method.setAccessible(true);
							try {
								method.invoke(indexedExprValue, new Object[] {
										indexOfExprValue, rightValue });
								found = true;
								break;
							}
							catch (IllegalAccessException
									| IllegalArgumentException
									| InvocationTargetException e) {
							}
						}
					}
					if ( !found ) {
						ee.error(this.getFirstSymbol(),
								"Method '" + NameServer.javaNameAtPutMethod
										+ "' was not found in class '"
										+ indexedValueClass.getName() + "'");
						return null;
					}

				}
				else {
					// a Java value
					if ( indexOfExprValue instanceof CyInt ) {
						indexOfExprValue = ((CyInt) indexOfExprValue).n;
					}

					if ( !(indexOfExprValue instanceof Integer) ) {
						ee.error(this.getFirstSymbol(),
								"Attempt to index a Java array with an object whose class"
										+ " is not Integer or int. Its class is '"
										+ indexOfExprValue.getClass().getName()
										+ "'");
						return null;
					}
					Array.set(indexedValueClass, (Integer) indexOfExprValue,
							rightValue);
				}

			}
			else {
				if ( rightValue instanceof cyanruntime.Ref<?> ) {
					rightValue = ((cyanruntime.Ref<?>) rightValue).elem;
				}

				if ( leftSide instanceof ExprIdentStar ) {
					// VariableDecInterface

					// rightValue = Statement.castCyanJava(ee,
					// leftSide.getClass(), rightValue, this.getFirstSymbol());

					// public static Object castCyanJava(EvalEnv ee, Class<?>
					// leftSideClass, Object rightValue, Symbol symbolForError)
					Object leftSideObj = leftSide.eval(ee);
					if ( leftSideObj != null ) {
						rightValue = Statement.castCyanJava(ee,
								leftSideObj.getClass(), rightValue);
					}

					String varName = ((ExprIdentStar) leftSide).getName();
					VariableDecInterface varI = ee.searchLocalVar(varName);
					if ( varI != null ) {
						varI.setValueInInterpreter(rightValue);
					}
					else if ( !ee.setValueInInterpreter(varName, rightValue) ) {
						ee.error(getFirstSymbol(),
								"Variable '" + varName + "' was not found");
					}
					// ((ExprIdentStar )
					// leftSide).getVarDeclaration().setValueInInterpreter(rightValue);
				}
				else {
					ee.error(leftSide.getFirstSymbol(),
							"Left-hand side of this assignment, '"
									+ leftSide.asString()
									+ "' is not assignable");
				}

			}
		}

		return null;
	}

	private List<Expr> exprList;

}
