/**

 */

package ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import cyan.lang.CyInt;
import cyan.lang.CyString;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._CyanMetaobjectAtAnnot;
import cyan.reflect._IActionAttachedType__semAn;
import cyan.reflect._IActionMessageSend__semAn;
import cyan.reflect._ICheckMessageSend__afterSemAn;
import error.ErrorKind;
import lexer.Symbol;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.IActionAssignment_cge;
import meta.IActionAttachedType_semAn;
import meta.IActionMessageSend_semAn;
import meta.ICheckMessageSend_afterSemAn;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Timeout;
import meta.Tuple2;
import meta.Tuple3;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrMethodDec;
import meta.WrPrototype;
import meta.WrType;
import metaRealClasses.Compiler_semAn;
import saci.Env;
import saci.NameServer;
import saci.Saci;

/**
 * This class keeps information on everything related to metaobjects and supply
 * methods used in several other classes
 *
 * @author José
 *
 */
public class MetaInfoServer {

	/**
	 * replace the text of the expression 'iexpr' by 'sb'. The 'sb' string is
	 * the code of an expression that has type 'newType'. Of course, 'newType'
	 * should be subtype of 'expr'.
	 *
	 * @param expr
	 * @param sb
	 * @param env
	 * @param newType
	 * @return
	 */
	public static boolean replaceRightExpr(
			IActionAttachedType_semAn annotMetaobject, WrExpr iexpr,
			StringBuffer sb, WrEnv env, WrType newType) {

		CyanMetaobjectAtAnnot metaobject = (CyanMetaobjectAtAnnot) annotMetaobject;
		final AnnotationAt annotation = metaobject.getAnnotation().getHidden();
		final Expr expr = meta.GetHiddenItem.getHiddenExpr(iexpr);
		if ( expr.getCodeThatReplacesThisExpr() != null ) {
			/*
			 * this message send has already been replaced by another expression
			 */
			if ( expr.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
				metaobject.addError(expr.getFirstSymbol().getI(),
						"Metaobject annotation '" + metaobject.getName()
								+ "' is trying to replace expression '"
								+ expr.asString()
								+ "' by another expression. But this has already been asked by metaobject annotation '"
								+ expr.getCyanAnnotationThatReplacedMSbyExpr()
										.getCyanMetaobject().getName()
								+ "'" + " at line "
								+ expr.getCyanAnnotationThatReplacedMSbyExpr()
										.getFirstSymbol().getLineNumber()
								+ " of prototype "
								+ expr.getCyanAnnotationThatReplacedMSbyExpr()
										.getPackageOfAnnotation()
								+ "."
								+ expr.getCyanAnnotationThatReplacedMSbyExpr()
										.getPackageOfAnnotation());
			}
			else {
				metaobject.addError(expr.getFirstSymbol().getI(),
						"Metaobject annotation '" + metaobject.getName()
								+ " is trying to replace message send '"
								+ expr.asString()
								+ "' by an expression. But this has already been asked by someone else");
			}
			return false;
		}

		meta.GetHiddenItem.getHiddenEnv(env).replaceStatementByCode(expr,
				annotation, sb, meta.GetHiddenItem.getHiddenType(newType));

		// boolean b = !
		// metaobject.getCurrentPrototype().getCompilationUnit(env).getFullFileNamePath().equals(expr.getFirstSymbol().getCompilationUnit().getFullFileNamePath());
		expr.setCyanAnnotationThatReplacedMSbyExpr(annotation);
		return true;
	}

	private static String			metaobjectAnnotationMethodNameList[]	= {
			"writeCode" };
	public static HashSet<String>	metaobjectAnnotationMethodNameSet;
	static {
		metaobjectAnnotationMethodNameSet = new HashSet<>();
		for (final String methodName : metaobjectAnnotationMethodNameList) {
			metaobjectAnnotationMethodNameSet.add(methodName);
		}
	}

	/**
	 * check whether an non-unary message send is correct according to the
	 * metaobjects of non-unary methods that potentially could be called. These
	 * are collected in methodSignatureList. <code>expr</code> is the expression
	 * that received the unary message. It is null in case of calls to super.
	 * receiverType is the type <code>expr</code>. symForError is the symbol
	 * used when calling an error message.
	 *
	 * @param methodSignatureList
	 * @param exprType
	 * @param expr
	 * @param env
	 * @param symForError
	 */
	public static void checkMessageSendWithMethodMetaobject(
			List<MethodSignature> methodSignatureList, Type exprType, Expr expr,
			ExprReceiverKind receiverKind, MessageWithKeywords message, Env env,
			Symbol symForError) {
		/*
		 * call methods of metaobjects whose annotations are associated to the
		 * methods. First the lower in the hierarchy.
		 */
		if ( methodSignatureList != null && methodSignatureList.size() > 0 ) {
			boolean inSubprototype = true;
			for (final MethodSignature ms : methodSignatureList) {
				List<AnnotationAt> ctmetaobjectAnnotationList;
				if ( ms.getMethod() == null ) {
					// it is a method of an interface
					ctmetaobjectAnnotationList = ms.getAttachedAnnotationList();
				}
				else
					ctmetaobjectAnnotationList = ms.getMethod()
							.getAttachedAnnotationList();
				if ( ctmetaobjectAnnotationList != null ) {

					final int size = ctmetaobjectAnnotationList.size();
					// List<AnnotationAt> list =
					// ms.getMethod().getAnnotationList();
					for (int i = size - 1; i >= 0; --i) {
						final AnnotationAt annotation = ctmetaobjectAnnotationList
								.get(i);
						final CyanMetaobjectAtAnnot cyanMO = annotation
								.getCyanMetaobject();
						_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot) cyanMO
								.getMetaobjectInCyan();
						if ( cyanMO instanceof ICheckMessageSend_afterSemAn
								|| (other != null
										&& other instanceof _ICheckMessageSend__afterSemAn) ) {
							WrPrototype puExpr;
							if ( !(exprType instanceof Prototype) ) {
								if ( exprType instanceof TypeWithAnnotations ) {
									Type tt = ((TypeWithAnnotations) exprType)
											.getInsideTypeNoAnnotations();
									if ( tt instanceof Prototype ) {
										puExpr = ((Prototype) tt).getI();
									}
									else {
										env.error(symForError,
												"Internal error in MetaInfoServer: type is not a Program Unit");
										return;
									}
								}
								else {
									env.error(symForError,
											"Internal error in MetaInfoServer: type is not a Program Unit");
									return;
								}
							}
							else {
								puExpr = ((Prototype) exprType).getI();
							}
							try {
								int timeoutMilliseconds = Timeout
										.getTimeoutMilliseconds(env,
												env.getProject().getProgram()
														.getI(),
												env.getCurrentCompilationUnit()
														.getCyanPackage()
														.getI(),
												expr.getFirstSymbol());

								Timeout<Object> to = new Timeout<>();

								if ( other == null ) {

									if ( Saci.timeLimitForMetaobjects ) {
										to.run(Executors.callable(() -> {
											((ICheckMessageSend_afterSemAn) cyanMO)
													.afterSemAn_checkKeywordMessageSend(
															expr.getI(), puExpr,
															receiverKind,
															message.getI(),
															ms.getI(),
															env.getI());

										}), timeoutMilliseconds,
												"afterSemAn_checkKeywordMessageSend",
												cyanMO, env);

									}
									else {

										((ICheckMessageSend_afterSemAn) cyanMO)
												.afterSemAn_checkKeywordMessageSend(
														expr.getI(), puExpr,
														receiverKind,
														message.getI(),
														ms.getI(), env.getI());
									}

									// ((ICheckMessageSend_afterSemAn )
									// cyanMO).afterSemAn_checkKeywordMessageSend(
									// expr.getI(), puExpr, receiverKind,
									// message.getI(), ms.getI(), env.getI());
								}
								else {
									to.run(Executors.callable(() -> {
										((_ICheckMessageSend__afterSemAn) other)
												._afterSemAn__checkKeywordMessageSend_6(
														expr.getI(), puExpr,
														new CyString(
																receiverKind
																		.toString()),
														message.getI(),
														ms.getI(), env.getI());
									}), timeoutMilliseconds,
											"afterSemAn_checkKeywordMessageSend",
											cyanMO, env);
									// ((_ICheckMessageSend__afterSemAn )
									// other)._afterSemAn__checkKeywordMessageSend_6(
									// expr.getI(), puExpr, new
									// CyString(receiverKind.toString()),
									// message.getI(), ms.getI(), env.getI());

								}
							}
							catch (final error.CompileErrorException e) {
							}
							catch (final NoClassDefFoundError e) {
								env.error(annotation.getFirstSymbol(), e
										.getMessage() + " "
										+ NameServer.messageClassNotFoundException);
							}
							catch (final RuntimeException e) {
								// e.printStackTrace();
								env.thrownException(annotation,
										annotation.getFirstSymbol(), e);
							}
							finally {
								env.errorInMetaobject(cyanMO, symForError);
							}

							if ( inSubprototype ) {
								inSubprototype = false;
								MethodSignature mostSpecificMS = methodSignatureList
										.get(0);
								Prototype mostSpecificReceiver;
								if ( mostSpecificMS.getMethod() != null ) {
									mostSpecificReceiver = mostSpecificMS
											.getMethod().getDeclaringObject();
								}
								else {
									mostSpecificReceiver = mostSpecificMS
											.getDeclaringInterface();
								}

								try {
									int timeoutMilliseconds = Timeout
											.getTimeoutMilliseconds(env,
													env.getProject()
															.getProgram()
															.getI(),
													env.getCurrentCompilationUnit()
															.getCyanPackage()
															.getI(),
													expr.getFirstSymbol());

									Timeout<Object> to = new Timeout<>();

									if ( other == null ) {

										if ( Saci.timeLimitForMetaobjects ) {
											to.run(Executors.callable(() -> {
												((ICheckMessageSend_afterSemAn) cyanMO)
														.afterSemAn_checkKeywordMessageSendMostSpecific(
																expr.getI(),
																puExpr,
																receiverKind,
																message.getI(),
																ms.getI(),
																mostSpecificReceiver
																		.getI(),
																env.getI());
											}), timeoutMilliseconds,
													"afterSemAn_checkKeywordMessageSendMostSpecific",
													cyanMO, env);

										}
										else {
											((ICheckMessageSend_afterSemAn) cyanMO)
													.afterSemAn_checkKeywordMessageSendMostSpecific(
															expr.getI(), puExpr,
															receiverKind,
															message.getI(),
															ms.getI(),
															mostSpecificReceiver
																	.getI(),
															env.getI());
										}

										// ((ICheckMessageSend_afterSemAn )
										// cyanMO).afterSemAn_checkKeywordMessageSendMostSpecific(
										// expr.getI(), puExpr,
										// receiverKind, message.getI(),
										// ms.getI(),
										// mostSpecificReceiver.getI(),
										// env.getI());
									}
									else {
										to.run(Executors.callable(() -> {
											((_ICheckMessageSend__afterSemAn) other)
													._afterSemAn__checkKeywordMessageSendMostSpecific_7(
															expr.getI(), puExpr,
															new CyString(
																	receiverKind
																			.toString()),
															message.getI(),
															ms.getI(),
															mostSpecificReceiver
																	.getI(),
															env.getI());
										}), timeoutMilliseconds,
												"afterSemAn_checkKeywordMessageSendMostSpecific",
												cyanMO, env);
										// ((_ICheckMessageSend__afterSemAn )
										// other)._afterSemAn__checkKeywordMessageSendMostSpecific_7(
										// expr.getI(), puExpr,
										// new
										// CyString(receiverKind.toString()),
										// message.getI(),
										// ms.getI(),
										// mostSpecificReceiver.getI(),
										// env.getI());
									}
								}
								catch (final error.CompileErrorException e) {
								}
								catch (final NoClassDefFoundError e) {
									env.error(annotation.getFirstSymbol(), e
											.getMessage() + " "
											+ NameServer.messageClassNotFoundException);
								}
								catch (final RuntimeException e) {
									// e.printStackTrace();
									env.thrownException(annotation,
											annotation.getFirstSymbol(), e);
								}
								finally {
									env.errorInMetaobject(cyanMO, symForError);
								}

							}

						}

					}
				}
			}
		}
	}

	/**
	 * check whether an unary message send is correct according to the
	 * metaobjects of unary methods that potentially could be called. These are
	 * collected in methodSignatureList. <code>expr</code> is the expression
	 * that received the unary message. It is null in case of calls to super.
	 * receiverType is the type <code>expr</code>. symForError is the symbol
	 * used when calling an error message.
	 *
	 * @param methodSignatureList
	 * @param exprType
	 * @param expr
	 * @param env
	 * @param symForError
	 */
	public static void checkMessageSendWithMethodMetaobject(
			List<MethodSignature> methodSignatureList, Type exprType, Expr expr,
			ExprReceiverKind receiverKind, Env env, Symbol unarySymbol) {
		/*
		 * call methods of metaobjects whose annotations are associated to the
		 * methods. First the higher in the hierarchy.
		 */
		if ( methodSignatureList != null && methodSignatureList.size() > 0 ) {
			boolean inSubprototype = true;

			for (final MethodSignature ms : methodSignatureList) {
				List<AnnotationAt> list;
				if ( ms.getMethod() != null ) {
					// a method of a prototype
					list = ms.getMethod().getAttachedAnnotationList();
				}
				else {
					// a method of an interface
					list = ms.getAttachedAnnotationList();
				}

				if ( list != null ) {
					final int size = list.size();

					for (int i = size - 1; i >= 0; --i) {
						final AnnotationAt annotation = list.get(i);
						final CyanMetaobjectAtAnnot cyanMO = annotation
								.getCyanMetaobject();
						_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot) cyanMO
								.getMetaobjectInCyan();
						if ( cyanMO instanceof ICheckMessageSend_afterSemAn
								|| (other != null
										&& other instanceof _ICheckMessageSend__afterSemAn) ) {

							if ( !(exprType instanceof Prototype) ) {
								env.error(unarySymbol,
										"Internal error in MetaInfoServer: type is not a Program Unit");
								return;
							}
							WrPrototype puExpr = ((Prototype) exprType).getI();

							if ( inSubprototype ) {
								MethodSignature subMethodSignature = methodSignatureList
										.get(0);
								Prototype mostSpecificReceiver;
								if ( subMethodSignature.getMethod() == null ) {
									mostSpecificReceiver = subMethodSignature
											.getDeclaringInterface();
								}
								else {
									mostSpecificReceiver = subMethodSignature
											.getMethod().getDeclaringObject();
								}

								try {
									int timeoutMilliseconds = Timeout
											.getTimeoutMilliseconds(env,
													env.getProject()
															.getProgram()
															.getI(),
													env.getCurrentCompilationUnit()
															.getCyanPackage()
															.getI(),
													expr.getFirstSymbol());

									Timeout<Object> to = new Timeout<>();

									if ( other == null ) {
										if ( Saci.timeLimitForMetaobjects ) {

											to.run(Executors.callable(() -> {
												((ICheckMessageSend_afterSemAn) cyanMO)
														.afterSemAn_checkUnaryMessageSendMostSpecific(
																expr.getI(),
																puExpr,
																receiverKind,
																unarySymbol
																		.getI(),
																mostSpecificReceiver
																		.getI(),
																env.getI());
											}), timeoutMilliseconds,
													"afterSemAn_checkUnaryMessageSendMostSpecific",
													cyanMO, env);

										}
										else {
											((ICheckMessageSend_afterSemAn) cyanMO)
													.afterSemAn_checkUnaryMessageSendMostSpecific(
															expr.getI(), puExpr,
															receiverKind,
															unarySymbol.getI(),
															mostSpecificReceiver
																	.getI(),
															env.getI());
										}

										// ((ICheckMessageSend_afterSemAn )
										// cyanMO).afterSemAn_checkUnaryMessageSendMostSpecific(
										// expr == null ? null : expr.getI(),
										// puExpr, receiverKind,
										// unarySymbol.getI(),
										// mostSpecificReceiver.getI(),
										// env.getI() );
									}
									else {
										to.run(Executors.callable(() -> {
											((_ICheckMessageSend__afterSemAn) other)
													._afterSemAn__checkUnaryMessageSendMostSpecific_6(
															expr.getI(), puExpr,
															new CyString(
																	receiverKind
																			.toString()),
															unarySymbol.getI(),
															mostSpecificReceiver
																	.getI(),
															env.getI());
										}), timeoutMilliseconds,
												"afterSemAn_checkUnaryMessageSendMostSpecific",
												cyanMO, env);

										// ((_ICheckMessageSend__afterSemAn )
										// other)._afterSemAn__checkUnaryMessageSendMostSpecific_6(
										// expr == null ? null : expr.getI(),
										// puExpr, new
										// CyString(receiverKind.toString()),
										// unarySymbol.getI(),
										// mostSpecificReceiver.getI(),
										// env.getI() );
									}
								}
								catch (final error.CompileErrorException e) {
								}
								catch (final NoClassDefFoundError e) {
									env.error(annotation.getFirstSymbol(), e
											.getMessage() + " "
											+ NameServer.messageClassNotFoundException);
								}
								catch (final RuntimeException e) {
									// e.printStackTrace();
									env.thrownException(annotation,
											annotation.getFirstSymbol(), e);
								}
								finally {
									env.errorInMetaobject(cyanMO, unarySymbol);
								}

							}
							try {
								int timeoutMilliseconds = Timeout
										.getTimeoutMilliseconds(env,
												env.getProject().getProgram()
														.getI(),
												env.getCurrentCompilationUnit()
														.getCyanPackage()
														.getI(),
												expr.getFirstSymbol());

								Timeout<Object> to = new Timeout<>();

								if ( other == null ) {

									if ( Saci.timeLimitForMetaobjects ) {
										to.run(Executors.callable(() -> {
											((ICheckMessageSend_afterSemAn) cyanMO)
													.afterSemAn_checkUnaryMessageSend(
															expr.getI(), puExpr,
															receiverKind,
															unarySymbol.getI(),
															env.getI());
										}), timeoutMilliseconds,
												"afterSemAn_checkUnaryMessageSend",
												cyanMO, env);

									}
									else {
										((ICheckMessageSend_afterSemAn) cyanMO)
												.afterSemAn_checkUnaryMessageSend(
														expr.getI(), puExpr,
														receiverKind,
														unarySymbol.getI(),
														env.getI());
									}

									// ((ICheckMessageSend_afterSemAn ) cyanMO)
									// .afterSemAn_checkUnaryMessageSend(
									// expr == null ? null : expr.getI(),
									// puExpr,
									// receiverKind, unarySymbol.getI(),
									// env.getI());
								}
								else {
									to.run(Executors.callable(() -> {
										((_ICheckMessageSend__afterSemAn) other)
												._afterSemAn__checkUnaryMessageSend_5(
														expr.getI(), puExpr,
														new CyString(
																receiverKind
																		.toString()),
														unarySymbol.getI(),
														env.getI());
									}), timeoutMilliseconds,
											"afterSemAn_checkUnaryMessageSend",
											cyanMO, env);

									// ((_ICheckMessageSend__afterSemAn ) other)
									// ._afterSemAn__checkUnaryMessageSend_5(
									// expr == null ? null : expr.getI(),
									// puExpr,
									// new CyString(receiverKind.toString()),
									// unarySymbol.getI(), env.getI());
								}
							}
							catch (final error.CompileErrorException e) {
							}
							catch (final NoClassDefFoundError e) {
								env.error(annotation.getFirstSymbol(), e
										.getMessage() + " "
										+ NameServer.messageClassNotFoundException);
							}
							catch (final RuntimeException e) {
								// e.printStackTrace();
								env.thrownException(annotation,
										annotation.getFirstSymbol(), e);
							}
							finally {
								env.errorInMetaobject(cyanMO, unarySymbol);
							}
						}

					}
				}
				inSubprototype = false;
			}
		}
	}

	/**
	 * This method do the checking demanded by metaobjects whose annotations are
	 * attached to types. This is the Cyan implementation of pluggable type
	 * systems. The left and right types correspond to the types of the
	 * expressions of an 'assignment':<br>
	 * <code> left = right </code><br>
	 * rightExpr is the expression being assigned. Here 'assignment' means any
	 * statement in which an expression may change it type and transfered
	 * elsewhere. For example, in an assignment, return value of method,
	 * parameter passing, object creation (a form of method call), creation of a
	 * literal array, etc.
	 */
	public static void checkAssignmentPluggableTypeSystem(Env env,
			Type leftType, Object leftASTNode, LeftHandSideKind leftKind,
			Type rightType, Expr rightExpr) {
		ICompiler_semAn compiler_semAn = null;
		if ( leftType instanceof TypeWithAnnotations ) {
			final TypeWithAnnotations leftTypeWithAnnot = (TypeWithAnnotations) leftType;
			for (final AnnotationAt annot : leftTypeWithAnnot
					.getAnnotationToTypeList()) {
				final CyanMetaobjectAtAnnot cyanMetaobject = annot
						.getCyanMetaobject();
				_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
				if ( cyanMetaobject instanceof IActionAttachedType_semAn
						|| (other != null
								&& other instanceof _IActionAttachedType__semAn) ) {

					final String currentPackageName = env
							.getCurrentCompilationUnit().getPackageName();
					final String currentPrototypeName = env
							.getCurrentPrototype().getName();

					List<Tuple2<String, String>> tupleArray = null;
					if ( other == null ) {
						tupleArray = ((IActionAttachedType_semAn) cyanMetaobject)
								.doNotCheckIn();
					}
					else {
						_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array = ((_IActionAttachedType__semAn) other)
								._doNotCheckIn();
						int sizeArray = array._size().n;
						if ( sizeArray > 0 ) {
							tupleArray = new ArrayList<>();
							for (int i = 0; i < sizeArray; ++i) {
								_Tuple_LT_GP_CyString_GP_CyString_GT t = array
										._at_1(new CyInt(i));
								tupleArray.add(new Tuple2<String, String>(
										t._f1().s, t._f2().s));
							}
						}
						else {
							tupleArray = null;
						}

					}
					boolean foundProto = false;

					boolean allowDoNotCheckInList = false;
					if ( other == null ) {
						allowDoNotCheckInList = ((IActionAttachedType_semAn) cyanMetaobject)
								.allowDoNotCheckInList();
					}
					else {
						allowDoNotCheckInList = ((_IActionAttachedType__semAn) other)
								._allowDoNotCheckInList().b;
					}
					if ( allowDoNotCheckInList ) {
						Set<String> strSet = env.getProject()
								.getProgramKeyValueSet(cyanMetaobject.getName()
										+ "DoNotCheckIn");
						final Set<String> strSet2 = env
								.getCurrentCompilationUnit().getCyanPackage()
								.getPackageKeyValueSet(cyanMetaobject.getName()
										+ "DoNotCheckIn");
						if ( strSet2 != null ) {
							final Set<String> strSet3 = new HashSet<>();
							if ( strSet != null ) {
								strSet3.addAll(strSet);
							}
							strSet3.addAll(strSet2);
							strSet = strSet3;
						}
						if ( strSet != null ) {
							for (final String str : strSet) {
								final Tuple2<String, String> packagePrototypenName = NameServer
										.splitPackagePrototype(
												MetaHelper.removeQuotes(str));
								if ( tupleArray == null ) {
									tupleArray = new ArrayList<>();
								}
								tupleArray.add(packagePrototypenName);
							}
						}

					}

					if ( tupleArray != null ) {
						for (final Tuple2<String, String> t : tupleArray) {
							if ( t.f1.equals(currentPackageName)
									&& t.f2.equals(currentPrototypeName) ) {
								foundProto = true;
								break;
							}
						}
					}

					if ( !foundProto ) {
						/*
						 * if the package/prototype of pp is equal to the
						 * current package/prototype, do not check. If true,
						 * check
						 */
						if ( compiler_semAn == null ) {
							compiler_semAn = new Compiler_semAn(env);
						}
						try {
							/*
							 * ((IActionAttachedType_semAn )
							 * cyanMetaobject).semAn_checkTypeChangeLeft(
							 * compiler_semAn, leftTypeWithAnnot, leftASTNode,
							 * leftKind, rightType, rightExpr);
							 */
							if ( !(leftASTNode instanceof ASTNode) ) {
								env.error(rightExpr.getFirstSymbol(),
										"Internal compiler error in MetaInfoServer: leftASTNode or rightExpr "
												+ "should implement interface GetI");
							}
							StringBuffer sb = null;
							if ( other == null ) {
								sb = ((IActionAttachedType_semAn) cyanMetaobject)
										.semAn_checkLeftTypeChangeRightExpr(
												compiler_semAn,
												leftTypeWithAnnot.getI(),
												((ASTNode) leftASTNode).getI(),
												leftKind, rightType.getI(),
												rightExpr.getI());
							}
							else {
								sb = new StringBuffer(
										((_IActionAttachedType__semAn) other)
												._semAn__checkLeftTypeChangeRightExpr_6(
														compiler_semAn,
														leftTypeWithAnnot
																.getI(),
														((ASTNode) leftASTNode)
																.getI(),
														new CyString(leftKind
																.toString()),
														rightType.getI(),
														rightExpr.getI()).s);
								if ( sb.length() == 0 ) {
									sb = null;
								}
							}
							if ( sb != null ) {
								MetaInfoServer.replaceRightExpr(
										(IActionAttachedType_semAn) cyanMetaobject,
										rightExpr.getI(), sb, env.getI(),
										rightType.getI());
							}

						}
						catch (final error.CompileErrorException e) {
						}
						catch (final NoClassDefFoundError e) {
							env.error(annot.getFirstSymbol(), e.getMessage()
									+ " "
									+ NameServer.messageClassNotFoundException);
						}
						catch (final RuntimeException e) {
							env.thrownException(annot, annot.getFirstSymbol(),
									e);
						}
						finally {
							env.errorInMetaobjectCatchExceptions(
									cyanMetaobject);
						}

					}

				}
			}
		}

		if ( rightType instanceof TypeWithAnnotations ) {
			final TypeWithAnnotations rightTypeWithAnnot = (TypeWithAnnotations) rightType;
			for (final AnnotationAt annot : rightTypeWithAnnot
					.getAnnotationToTypeList()) {
				final CyanMetaobjectAtAnnot cyanMetaobject = annot
						.getCyanMetaobject();
				_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();

				if ( cyanMetaobject instanceof IActionAttachedType_semAn
						|| (other != null
								&& other instanceof _IActionAttachedType__semAn) ) {

					final String currentPrototypeName = env
							.getCurrentCompilationUnit().getPackageName();
					final String currentPackageName = env.getCurrentPrototype()
							.getName();

					List<Tuple2<String, String>> tupleArray = null;
					if ( other == null ) {
						tupleArray = ((IActionAttachedType_semAn) cyanMetaobject)
								.doNotCheckIn();
					}
					else {
						_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array = ((_IActionAttachedType__semAn) other)
								._doNotCheckIn();
						int sizeArray = array._size().n;
						if ( sizeArray > 0 ) {
							tupleArray = new ArrayList<>();
							for (int i = 0; i < sizeArray; ++i) {
								_Tuple_LT_GP_CyString_GP_CyString_GT t = array
										._at_1(new CyInt(i));
								tupleArray.add(new Tuple2<String, String>(
										t._f1().s, t._f2().s));
							}
						}
						else {
							tupleArray = null;
						}
					}
					boolean foundProto = false;
					if ( tupleArray != null ) {
						for (final Tuple2<String, String> t : tupleArray) {
							if ( t.f1.equals(currentPackageName)
									&& t.f2.equals(currentPrototypeName) ) {
								foundProto = true;
								break;
							}
						}
					}
					if ( !foundProto ) {

						if ( compiler_semAn == null ) {
							compiler_semAn = new Compiler_semAn(env);
						}

						try {
							StringBuffer sb = null;

							if ( other == null ) {
								sb = ((IActionAttachedType_semAn) cyanMetaobject)
										.semAn_checkRightTypeChangeRightExpr(
												compiler_semAn, leftType.getI(),
												((ASTNode) leftASTNode).getI(),
												leftKind, rightType.getI(),
												rightExpr.getI());
							}
							else {
								sb = new StringBuffer(
										((_IActionAttachedType__semAn) other)
												._semAn__checkRightTypeChangeRightExpr_6(
														compiler_semAn,
														leftType.getI(),
														((ASTNode) leftASTNode)
																.getI(),
														new CyString(leftKind
																.toString()),
														rightType.getI(),
														rightExpr.getI()).s);
								if ( sb.length() == 0 ) {
									sb = null;
								}
							}

							if ( sb != null ) {
								MetaInfoServer.replaceRightExpr(
										(IActionAttachedType_semAn) cyanMetaobject,
										rightExpr.getI(), sb, env.getI(),
										rightType.getI());
							}

						}
						catch (final error.CompileErrorException e) {
						}
						catch (final NoClassDefFoundError e) {
							env.error(annot.getFirstSymbol(), e.getMessage()
									+ " "
									+ NameServer.messageClassNotFoundException);
						}
						catch (final RuntimeException e) {
							env.thrownException(annot, annot.getFirstSymbol(),
									e);
						}
						finally {
							env.errorInMetaobjectCatchExceptions(
									cyanMetaobject);
						}

					}

				}
			}
		}

	}

	/**
	 * check whether there is a metaobject that implements IActionAssignment_cge
	 * associated to type {@code leftType}. A tuple is returned if a metaobject
	 * is found or null otherwise. This tuple has two elements. The first one is
	 * the first metaobject found in a search starting in prototype
	 * {@code leftType}. The second tuple element is the prototype in which the
	 * metaobject was found.
	 *
	 */
	public static Tuple2<IActionAssignment_cge, ObjectDec> getChangeAssignmentCyanMetaobject(
			Env env, Type leftType) {

		IActionAssignment_cge changeCyanMetaobject = null;
		if ( leftType instanceof ObjectDec ) {
			ObjectDec proto = (ObjectDec) leftType;
			while (proto != null) {
				final List<AnnotationAt> ctmetaobjectAnnotationList = proto
						.getAttachedAnnotationList();
				if ( ctmetaobjectAnnotationList != null ) {
					boolean found = false;
					for (final AnnotationAt annotation : ctmetaobjectAnnotationList) {
						final CyanMetaobject cyanMetaobject = annotation
								.getCyanMetaobject();
						if ( cyanMetaobject instanceof IActionAssignment_cge ) {
							if ( found ) {
								env.error(annotation.getFirstSymbol(),
										"There is more than one metaobject annotation attached to '"
												+ proto.getFullName()
												+ "' that implements interface IActionAssignment_cge. That is illegal",
										true, true);
							}
							changeCyanMetaobject = (IActionAssignment_cge) cyanMetaobject;
							found = true;
						}
					}
					if ( found ) {
						return new Tuple2<IActionAssignment_cge, ObjectDec>(
								changeCyanMetaobject, proto);
					}

				}
				proto = proto.getSuperobject();
			}
		}
		return null;
	}

	public static Type replaceMessageSendIfAsked(MethodSignature ms,
			Expr messageSendExpr, Env env, Symbol symForError,
			Type originalType) {

		List<AnnotationAt> list;
		if ( ms.getMethod() != null ) {
			// a method of a prototype
			list = ms.getMethod().getAttachedAnnotationList();
		}
		else {
			// a method of an interface
			list = ms.getAttachedAnnotationList();
		}

		if ( list != null ) {

			for (final AnnotationAt annotation : list) {
				final CyanMetaobjectAtAnnot cyanMetaobject = annotation
						.getCyanMetaobject();
				_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot) cyanMetaobject
						.getMetaobjectInCyan();
				if ( cyanMetaobject instanceof IActionMessageSend_semAn
						|| (other != null
								&& other instanceof _IActionMessageSend__semAn) ) {

					// MessageWithKeywords message = (MessageWithKeywords )
					// this.getMessage();
					// message.calcInternalTypes(env);

					Tuple3<StringBuffer, String, String> codeType = null;
					StringBuffer sb = null;
					try {
						int timeoutMilliseconds = Timeout
								.getTimeoutMilliseconds(env,
										env.getProject().getProgram().getI(),
										env.getCurrentCompilationUnit()
												.getCyanPackage().getI(),
										symForError);

						if ( other == null ) {
							Timeout<Tuple3<StringBuffer, String, String>> to = new Timeout<>();

							final IActionMessageSend_semAn doesNot = (IActionMessageSend_semAn) cyanMetaobject;
							if ( messageSendExpr instanceof ExprMessageSendWithKeywordsToExpr ) {
								codeType = to.run(() -> {
									return doesNot
											.semAn_analyzeReplaceKeywordMessage(
													((ExprMessageSendWithKeywordsToExpr) messageSendExpr)
															.getI(),
													env.getI());
								}, timeoutMilliseconds,
										"semAn_analyzeReplaceKeywordMessage",
										cyanMetaobject, env);
								// codeType =
								// doesNot.semAn_analyzeReplaceKeywordMessage(
								// ((ExprMessageSendWithKeywordsToExpr )
								// messageSendExpr).getI(), env.getI());
							}
							else if ( messageSendExpr instanceof ExprMessageSendUnaryChainToExpr ) {

								if ( Saci.timeLimitForMetaobjects ) {
									codeType = to.run(() -> {
										return doesNot
												.semAn_analyzeReplaceUnaryMessage(
														((ExprMessageSendUnaryChainToExpr) messageSendExpr)
																.getI(),
														env.getI());
									}, timeoutMilliseconds,
											"semAn_analyzeReplaceUnaryMessage",
											cyanMetaobject, env);

								}
								else {
									codeType = doesNot
											.semAn_analyzeReplaceUnaryMessage(
													((ExprMessageSendUnaryChainToExpr) messageSendExpr)
															.getI(),
													env.getI());
								}

								// codeType =
								// doesNot.semAn_analyzeReplaceUnaryMessage(
								// ((ExprMessageSendUnaryChainToExpr )
								// messageSendExpr).getI(), env.getI());
							}
							else if ( messageSendExpr instanceof ExprIdentStar ) {

								if ( Saci.timeLimitForMetaobjects ) {
									codeType = to.run(() -> {
										return doesNot
												.semAn_analyzeReplaceUnaryMessageWithoutSelf(
														((ExprIdentStar) messageSendExpr)
																.getI(),
														env.getI());
									}, timeoutMilliseconds,
											"semAn_analyzeReplaceUnaryMessageWithoutSelf",
											cyanMetaobject, env);

								}
								else {
									codeType = doesNot
											.semAn_analyzeReplaceUnaryMessageWithoutSelf(
													((ExprIdentStar) messageSendExpr)
															.getI(),
													env.getI());
								}

								// codeType =
								// doesNot.semAn_analyzeReplaceUnaryMessageWithoutSelf(
								// ((ExprIdentStar ) messageSendExpr).getI(),
								// env.getI());
							}
							else {
								env.error(symForError,
										"Internal error in MetaInfoServer.replaceMessageSendIfAsked: "
												+ "unknown type for messageSendExpr: "
												+ messageSendExpr.getClass()
														.getName());
							}
						}
						else {
							Timeout<_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT> to = new Timeout<>();

							if ( messageSendExpr instanceof ExprMessageSendWithKeywordsToExpr ) {
								_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd = to
										.run(() -> {
											return ((_IActionMessageSend__semAn) other)
													._semAn__analyzeReplaceKeywordMessage_2(
															((ExprMessageSendWithKeywordsToExpr) messageSendExpr)
																	.getI(),
															env.getI());
										}, timeoutMilliseconds,
												"semAn_analyzeReplaceKeywordMessage",
												cyanMetaobject, env);

								// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT
								// tdd =
								// ((_IActionMessageSend__semAn )
								// other)._semAn__analyzeReplaceKeywordMessage_2(
								// ((ExprMessageSendWithKeywordsToExpr )
								// messageSendExpr).getI(), env.getI());
								CyString f1 = tdd._f1();
								CyString f2 = tdd._f2();
								CyString f3 = tdd._f3();
								String f1s = f1.s;
								if ( f1s.length() != 0 ) {
									codeType = new Tuple3<StringBuffer, String, String>(
											new StringBuffer(f1s), f2.s, f3.s);
								}
							}
							else if ( messageSendExpr instanceof ExprMessageSendUnaryChainToExpr ) {
								_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd = to
										.run(() -> {
											return ((_IActionMessageSend__semAn) other)
													._semAn__analyzeReplaceUnaryMessage_2(
															((ExprMessageSendUnaryChainToExpr) messageSendExpr)
																	.getI(),
															env.getI());
										}, timeoutMilliseconds,
												"semAn_analyzeReplaceUnaryMessage",
												cyanMetaobject, env);

								// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT
								// tdd =
								// ((_IActionMessageSend__semAn )
								// other)._semAn__analyzeReplaceUnaryMessage_2(
								// ((ExprMessageSendUnaryChainToExpr )
								// messageSendExpr).getI(), env.getI());
								CyString f1 = tdd._f1();
								CyString f2 = tdd._f2();
								CyString f3 = tdd._f3();
								String f1s = f1.s;
								if ( f1s.length() != 0 ) {
									codeType = new Tuple3<StringBuffer, String, String>(
											new StringBuffer(f1s), f2.s, f3.s);
								}
							}
							else if ( messageSendExpr instanceof ExprIdentStar ) {
								_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd = to
										.run(() -> {
											return ((_IActionMessageSend__semAn) other)
													._semAn__analyzeReplaceUnaryMessageWithoutSelf_2(
															((ExprIdentStar) messageSendExpr)
																	.getI(),
															env.getI());
										}, timeoutMilliseconds,
												"semAn_analyzeReplaceUnaryMessageWithoutSelf",
												cyanMetaobject, env);

								// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT
								// tdd =
								// ((_IActionMessageSend__semAn )
								// other)._semAn__analyzeReplaceUnaryMessageWithoutSelf_2(
								// ((ExprIdentStar ) messageSendExpr).getI(),
								// env.getI());
								CyString f1 = tdd._f1();
								CyString f2 = tdd._f2();
								CyString f3 = tdd._f3();
								String f1s = f1.s;
								if ( f1s.length() != 0 ) {
									codeType = new Tuple3<StringBuffer, String, String>(
											new StringBuffer(f1s), f2.s, f3.s);
								}
							}
							else {
								env.error(symForError,
										"Internal error in MetaInfoServer.replaceMessageSendIfAsked: "
												+ "unknown type for messageSendExpr: "
												+ messageSendExpr.getClass()
														.getName());
							}

						}

					}
					catch (final error.CompileErrorException e) {
					}
					catch (final NoClassDefFoundError e) {
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (final RuntimeException e) {
						env.thrownException(annotation, symForError, e);
					}
					finally {
						env.errorInMetaobject(cyanMetaobject,
								messageSendExpr.getFirstSymbol());
					}

					if ( codeType != null ) {
						sb = codeType.f1;

						if ( messageSendExpr
								.getCodeThatReplacesThisExpr() != null ) {
							/*
							 * this message send has already been replaced by
							 * another expression
							 */
							if ( messageSendExpr
									.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
								env.warning(symForError,
										"Metaobject annotation '"
												+ cyanMetaobject.getName()
												+ "' at line "
												+ annotation.getFirstSymbol()
														.getLineNumber()
												+ " of prototype "
												+ annotation
														.getPackageOfAnnotation()
												+ "."
												+ annotation
														.getPackageOfAnnotation()
												+ " is trying to replace message send '"
												+ messageSendExpr.asString()
												+ "' by an expression. But this has already been asked by metaobject annotation '"
												+ messageSendExpr
														.getCyanAnnotationThatReplacedMSbyExpr()
														.getCyanMetaobject()
														.getName()
												+ "'" + " at line "
												+ messageSendExpr
														.getCyanAnnotationThatReplacedMSbyExpr()
														.getFirstSymbol()
														.getLineNumber()
												+ " of prototype "
												+ messageSendExpr
														.getCyanAnnotationThatReplacedMSbyExpr()
														.getPackageOfAnnotation()
												+ "."
												+ messageSendExpr
														.getCyanAnnotationThatReplacedMSbyExpr()
														.getPackageOfAnnotation());
							}
							else {
								env.warning(symForError,
										"Metaobject annotation '"
												+ cyanMetaobject.getName()
												+ "' at line "
												+ annotation.getFirstSymbol()
														.getLineNumber()
												+ " of prototype "
												+ annotation
														.getPackageOfAnnotation()
												+ "."
												+ annotation
														.getPackageOfAnnotation()
												+ " is trying to replace message send '"
												+ messageSendExpr.asString()
												+ "' by an expression. But this has already been asked by someone else");
							}
						}

						// if there is any errors, signals them
						env.errorInMetaobject(cyanMetaobject, symForError);

						Type typeOfCode;
						if ( codeType.f2 == null || codeType.f3 == null
								|| codeType.f2.length() == 0
								|| codeType.f3.length() == 0 ) {
							typeOfCode = messageSendExpr.getType();
						}
						else {
							typeOfCode = env.searchPackagePrototype(codeType.f2,
									codeType.f3);
							// GetHiddenItem.getHiddenType(codeType.f2);
							if ( typeOfCode == null ) {
								env.error(symForError,
										"This message send was replaced by an expression that has type '"
												+ codeType.f2 + "."
												+ codeType.f3
												+ "' which was not found");
							}
						}

						env.replaceStatementByCode(messageSendExpr, annotation,
								sb, typeOfCode);

						messageSendExpr.setCyanAnnotationThatReplacedMSbyExpr(
								annotation);

						return typeOfCode;
					}

					break;
				}
			}

		}

		return originalType;

	}

	// /**
	// * A metaobject attached to a method may implement
	// IActionMessageSend_semAn and
	// * a method of the list allMethodSignatureList may be called in a message
	// send
	// * <code>messageSendExpr</code>.
	// @param ms
	// @param messageSendExpr
	// @param env
	// @param symForError
	// @param originalType
	// @return
	// */
	// public static Type replaceMessageSendIfAsked(List<MethodSignature>
	// allMethodSignatureList,
	// ExprMessageSend messageSendExpr,
	// Env env, Symbol symForError, Type originalType) {
	//
	// List<AnnotationAt> list = new ArrayList<>();
	// /*
	// * collect into 'list' all annotations attached to all methods of
	// allMethodSignatureList
	// */
	// for ( MethodSignature ms : allMethodSignatureList ) {
	// List<AnnotationAt> otherList = null;
	// if ( ms.getMethod() != null ) {
	// // a method of a prototype
	// otherList = ms.getMethod().getAttachedAnnotationList();
	// }
	// else {
	// // a method of an interface
	// otherList = ms.getAttachedAnnotationList();
	// }
	// if ( otherList != null ) {
	// list.addAll(otherList);
	// }
	// }
	// for (final AnnotationAt annotation : list ) {
	// final CyanMetaobjectAtAnnot cyanMetaobject =
	// annotation.getCyanMetaobject();
	// _CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot )
	// cyanMetaobject.getMetaobjectInCyan();
	// if ( cyanMetaobject instanceof IActionMessageSend_semAn ||
	// (other != null && other instanceof _IActionMessageSend__semAn) ) {
	//
	//
	// // MessageWithKeywords message = (MessageWithKeywords )
	// this.getMessage();
	// // message.calcInternalTypes(env);
	//
	// Tuple3<StringBuffer, String, String> codeType = null;
	// StringBuffer sb = null;
	// try {
	// if ( other == null ) {
	// final IActionMessageSend_semAn doesNot = (IActionMessageSend_semAn )
	// cyanMetaobject;
	// if ( messageSendExpr instanceof ExprMessageSendWithKeywordsToExpr ) {
	// codeType = doesNot.semAn_analyzeReplaceKeywordMessage(
	// ((ExprMessageSendWithKeywordsToExpr ) messageSendExpr).getI(),
	// env.getI());
	// }
	// else if ( messageSendExpr instanceof ExprMessageSendUnaryChainToExpr ) {
	// codeType = doesNot.semAn_analyzeReplaceUnaryMessage(
	// ((ExprMessageSendUnaryChainToExpr ) messageSendExpr).getI(), env.getI());
	// }
	//
	// }
	// else {
	// if ( messageSendExpr instanceof ExprMessageSendWithKeywordsToExpr ) {
	//
	// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd =
	// ((_IActionMessageSend__semAn )
	// other)._semAn__analyzeReplaceKeywordMessage_2(
	// ((ExprMessageSendWithKeywordsToExpr ) messageSendExpr).getI(),
	// env.getI());
	// Object f1 = tdd._f1();
	// Object f2 = tdd._f2();
	// Object f3 = tdd._f3();
	// if ( !(f1 instanceof CyString) || !(f2 instanceof CyString) || !(f3
	// instanceof CyString) ) {
	// env.error(annotation.getFirstSymbol(), "This metaobject is returning a "
	// + "tuple with object of wrong type. It should be a Tuple<String, String,
	// String>");
	// }
	// String f1s = ((CyString ) f1).s;
	// if ( f1s.length() != 0 ) {
	// codeType = new Tuple3<StringBuffer, String, String>(
	// new StringBuffer(f1s), ((CyString ) f2).s,
	// ((CyString ) f3).s
	// );
	// }
	// }
	// else if ( messageSendExpr instanceof ExprMessageSendUnaryChainToExpr ) {
	// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd =
	// ((_IActionMessageSend__semAn )
	// other)._semAn__analyzeReplaceUnaryMessage_2(
	// ((ExprMessageSendUnaryChainToExpr ) messageSendExpr).getI(), env.getI());
	// CyString f1 = tdd._f1();
	// CyString f2 = tdd._f2();
	// CyString f3 = tdd._f3();
	// String f1s = f1.s;
	// if ( f1s.length() != 0 ) {
	// codeType = new Tuple3<StringBuffer, String, String>(
	// new StringBuffer(f1s), f2.s,
	// f3.s
	// );
	// }
	//
	// }
	//
	// }
	//
	// }
	// catch ( final error.CompileErrorException e ) {
	// }
	// catch ( final NoClassDefFoundError e ) {
	// env.error(annotation.getFirstSymbol(), e.getMessage() + " " +
	// NameServer.messageClassNotFoundException);
	// }
	// catch ( final RuntimeException e ) {
	// env.thrownException(annotation, symForError, e);
	// }
	// finally {
	// env.errorInMetaobject(cyanMetaobject, messageSendExpr.getFirstSymbol());
	// }
	//
	// if ( codeType != null ) {
	// sb = codeType.f1;
	//
	// if ( messageSendExpr.getCodeThatReplacesThisExpr() != null ) {
	// /*
	// * this message send has already been replaced by another expression
	// */
	// if ( messageSendExpr.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
	// env.warning(symForError, "Metaobject annotation '" +
	// cyanMetaobject.getName() +
	// "' at line " + annotation.getFirstSymbol().getLineNumber() +
	// " of prototype " + annotation.getPackageOfAnnotation() + "." +
	// annotation.getPackageOfAnnotation() +
	// " is trying to replace message send '" + messageSendExpr.asString() +
	// "' by an expression. But this has already been asked by metaobject
	// annotation '" +
	// messageSendExpr.getCyanAnnotationThatReplacedMSbyExpr().getCyanMetaobject().getName()
	// + "'" +
	// " at line " +
	// messageSendExpr.getCyanAnnotationThatReplacedMSbyExpr().getFirstSymbol().getLineNumber()
	// +
	// " of prototype " +
	// messageSendExpr.getCyanAnnotationThatReplacedMSbyExpr().getPackageOfAnnotation()
	// + "." +
	// messageSendExpr.getCyanAnnotationThatReplacedMSbyExpr().getPackageOfAnnotation());
	// }
	// else {
	// env.warning(symForError, "Metaobject annotation '" +
	// cyanMetaobject.getName() +
	// "' at line " + annotation.getFirstSymbol().getLineNumber() +
	// " of prototype " + annotation.getPackageOfAnnotation() + "." +
	// annotation.getPackageOfAnnotation() +
	// " is trying to replace message send '" + messageSendExpr.asString() +
	// "' by an expression. But this has already been asked by someone else");
	// }
	// }
	//
	// // if there is any errors, signals them
	// env.errorInMetaobject(cyanMetaobject, symForError);
	//
	//// WrType wrTypeOfCode = env.searchPackagePrototype(codeType.f2,
	//// codeType.f3);
	// Type typeOfCode;
	// if ( codeType.f2 == null || codeType.f3 == null || codeType.f2.length()
	// == 0 ||
	// codeType.f3.length() == 0 ) {
	// typeOfCode = messageSendExpr.getType();
	// }
	// else {
	// typeOfCode = env.searchPackagePrototype(codeType.f2,
	// codeType.f3);
	// if ( typeOfCode == null ) {
	// env.error(true,
	// symForError,
	// "This message send was replaced by an expression that has type '" +
	// codeType.f2 + "." +
	// codeType.f3 + "' which was not found",
	// cyanMetaobject.getPrototypeOfType(),
	// ErrorKind.prototype_was_not_found_inside_method);
	// }
	// }
	// //GetHiddenItem.getHiddenType(codeType.f2);
	//
	// env.replaceStatementByCode(messageSendExpr, annotation, sb, typeOfCode);
	//
	// messageSendExpr.setCyanAnnotationThatReplacedMSbyExpr(annotation);
	//
	// return typeOfCode;
	// }
	// }
	// }
	//
	//
	//
	// return originalType;
	// }
	//

	/**
	 * A metaobject attached to a method may implement IActionMessageSend_semAn
	 * and a method of the list allMethodSignatureList may be called in a
	 * message send <code>messageSendExpr</code>.
	 *
	 * @param ms
	 * @param messageSendExpr
	 * @param env
	 * @param symForError
	 * @param originalType
	 * @return
	 */
	public static Type replaceMessageSendIfAsked(
			List<MethodSignature> allMethodSignatureList, Expr messageSendExpr,
			Env env, Symbol symForError, Type originalType) {

		List<AnnotationAt> list = new ArrayList<>();
		/*
		 * collect into 'list' all annotations attached to all methods of
		 * allMethodSignatureList
		 */
		for (MethodSignature ms : allMethodSignatureList) {
			List<AnnotationAt> otherList = null;
			if ( ms.getMethod() != null ) {
				// a method of a prototype
				otherList = ms.getMethod().getAttachedAnnotationList();
			}
			else {
				// a method of an interface
				otherList = ms.getAttachedAnnotationList();
			}
			if ( otherList != null ) {
				list.addAll(otherList);
			}
		}
		Type toBeReturned = originalType;
		WrPrototype lastPrototypeWhereAnnotWithCodeReplacementWasFound = null;
		AnnotationAt lastAnnotWhereAnnotWithCodeReplacementWasFound = null;
		for (final AnnotationAt annotation : list) {
			final CyanMetaobjectAtAnnot cyanMetaobject = annotation
					.getCyanMetaobject();
			_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot) cyanMetaobject
					.getMetaobjectInCyan();
			if ( cyanMetaobject instanceof IActionMessageSend_semAn
					|| (other != null
							&& other instanceof _IActionMessageSend__semAn) ) {

				WrPrototype puWhereAnnotWasFound = null;

				IDeclaration dec = annotation.getDeclaration();
				if ( dec instanceof WrMethodDec ) {
					puWhereAnnotWasFound = ((WrMethodDec) dec)
							.getDeclaringObject();
				}
				else {
					env.error(symForError,
							"Internal error: annotations whose class implement "
									+ "IActionMessageSend_semAn should only be attached to methods");
					return null;
				}

				// MessageWithKeywords message = (MessageWithKeywords )
				// this.getMessage();
				// message.calcInternalTypes(env);

				Tuple3<StringBuffer, String, String> codeType = null;
				StringBuffer sb = null;
				try {
					int timeoutMilliseconds = Timeout
							.getTimeoutMilliseconds(env,
									env.getProject().getProgram().getI(),
									env.getCurrentCompilationUnit()
											.getCyanPackage().getI(),
									symForError);
					if ( other == null ) {

						Timeout<Tuple3<StringBuffer, String, String>> to = new Timeout<>();

						final IActionMessageSend_semAn doesNot = (IActionMessageSend_semAn) cyanMetaobject;
						if ( messageSendExpr instanceof ExprMessageSendWithKeywordsToExpr ) {

							if ( Saci.timeLimitForMetaobjects ) {
								codeType = to.run(() -> {
									return doesNot
											.semAn_analyzeReplaceKeywordMessage(
													((ExprMessageSendWithKeywordsToExpr) messageSendExpr)
															.getI(),
													env.getI());
								}, timeoutMilliseconds,
										"semAn_analyzeReplaceKeywordMessage",
										cyanMetaobject, env);

							}
							else {
								codeType = doesNot
										.semAn_analyzeReplaceKeywordMessage(
												((ExprMessageSendWithKeywordsToExpr) messageSendExpr)
														.getI(),
												env.getI());
							}

							// codeType =
							// doesNot.semAn_analyzeReplaceKeywordMessage(
							// ((ExprMessageSendWithKeywordsToExpr )
							// messageSendExpr).getI(), env.getI());
						}
						else if ( messageSendExpr instanceof ExprMessageSendUnaryChainToExpr ) {

							if ( Saci.timeLimitForMetaobjects ) {
								codeType = to.run(() -> {
									return doesNot
											.semAn_analyzeReplaceUnaryMessage(
													((ExprMessageSendUnaryChainToExpr) messageSendExpr)
															.getI(),
													env.getI());
								}, timeoutMilliseconds,
										"semAn_analyzeReplaceUnaryMessage",
										cyanMetaobject, env);

							}
							else {
								codeType = doesNot
										.semAn_analyzeReplaceUnaryMessage(
												((ExprMessageSendUnaryChainToExpr) messageSendExpr)
														.getI(),
												env.getI());
							}

							// codeType =
							// doesNot.semAn_analyzeReplaceUnaryMessage(
							// ((ExprMessageSendUnaryChainToExpr )
							// messageSendExpr).getI(), env.getI());
						}
						else if ( messageSendExpr instanceof ExprIdentStar ) {
							if ( Saci.timeLimitForMetaobjects ) {
								codeType = to.run(() -> {
									return doesNot
											.semAn_analyzeReplaceUnaryMessageWithoutSelf(
													((ExprIdentStar) messageSendExpr)
															.getI(),
													env.getI());
								}, timeoutMilliseconds,
										"semAn_analyzeReplaceUnaryMessageWithoutSelf",
										cyanMetaobject, env);

							}
							else {
								codeType = doesNot
										.semAn_analyzeReplaceUnaryMessageWithoutSelf(
												((ExprIdentStar) messageSendExpr)
														.getI(),
												env.getI());
							}

							// codeType =
							// doesNot.semAn_analyzeReplaceUnaryMessageWithoutSelf(
							// ((ExprIdentStar ) messageSendExpr).getI(),
							// env.getI());
						}
						else {
							env.error(symForError,
									"Internal error in MetaInfoServer.replaceMessageSendIfAsked: "
											+ "unknown type for messageSendExpr: "
											+ messageSendExpr.getClass()
													.getName());
							return null;
						}

					}
					else {
						Timeout<_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT> to = new Timeout<>();
						if ( messageSendExpr instanceof ExprMessageSendWithKeywordsToExpr ) {

							_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd = to
									.run(() -> {
										return ((_IActionMessageSend__semAn) other)
												._semAn__analyzeReplaceKeywordMessage_2(
														((ExprMessageSendWithKeywordsToExpr) messageSendExpr)
																.getI(),
														env.getI());
									}, timeoutMilliseconds,
											"semAn_analyzeReplaceKeywordMessage",
											cyanMetaobject, env);

							// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT
							// tdd =
							// ((_IActionMessageSend__semAn )
							// other)._semAn__analyzeReplaceKeywordMessage_2(
							// ((ExprMessageSendWithKeywordsToExpr )
							// messageSendExpr).getI(), env.getI());
							Object f1 = tdd._f1();
							Object f2 = tdd._f2();
							Object f3 = tdd._f3();
							if ( !(f1 instanceof CyString)
									|| !(f2 instanceof CyString)
									|| !(f3 instanceof CyString) ) {
								env.error(annotation.getFirstSymbol(),
										"This metaobject is returning a "
												+ "tuple with object of wrong type. It should be a Tuple<String, String, String>");
								return null;
							}
							String f1s = ((CyString) f1).s;
							if ( f1s.length() != 0 ) {
								codeType = new Tuple3<StringBuffer, String, String>(
										new StringBuffer(f1s),
										((CyString) f2).s, ((CyString) f3).s);
							}
						}
						else if ( messageSendExpr instanceof ExprMessageSendUnaryChainToExpr ) {
							_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd = to
									.run(() -> {
										return ((_IActionMessageSend__semAn) other)
												._semAn__analyzeReplaceUnaryMessage_2(
														((ExprMessageSendUnaryChainToExpr) messageSendExpr)
																.getI(),
														env.getI());

									}, timeoutMilliseconds,
											"semAn_analyzeReplaceUnaryMessage",
											cyanMetaobject, env);

							// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT
							// tdd =
							// ((_IActionMessageSend__semAn )
							// other)._semAn__analyzeReplaceUnaryMessage_2(
							// ((ExprMessageSendUnaryChainToExpr )
							// messageSendExpr).getI(), env.getI());
							CyString f1 = tdd._f1();
							CyString f2 = tdd._f2();
							CyString f3 = tdd._f3();
							String f1s = f1.s;
							if ( f1s.length() != 0 ) {
								codeType = new Tuple3<StringBuffer, String, String>(
										new StringBuffer(f1s), f2.s, f3.s);
							}

						}
						else if ( messageSendExpr instanceof ExprIdentStar ) {
							_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd = to
									.run(() -> {
										return ((_IActionMessageSend__semAn) other)
												._semAn__analyzeReplaceUnaryMessageWithoutSelf_2(
														((ExprIdentStar) messageSendExpr)
																.getI(),
														env.getI());

									}, timeoutMilliseconds,
											"semAn_analyzeReplaceUnaryMessageWithoutSelf",
											cyanMetaobject, env);

							// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT
							// tdd =
							// ((_IActionMessageSend__semAn )
							// other)._semAn__analyzeReplaceUnaryMessageWithoutSelf_2(
							// ((ExprIdentStar ) messageSendExpr).getI(),
							// env.getI());
							CyString f1 = tdd._f1();
							CyString f2 = tdd._f2();
							CyString f3 = tdd._f3();
							String f1s = f1.s;
							if ( f1s.length() != 0 ) {
								codeType = new Tuple3<StringBuffer, String, String>(
										new StringBuffer(f1s), f2.s, f3.s);
							}

						}
						else {
							env.error(symForError,
									"Internal error in MetaInfoServer.replaceMessageSendIfAsked: "
											+ "unknown type for messageSendExpr: "
											+ messageSendExpr.getClass()
													.getName());
							return null;
						}

					}

				}
				catch (final error.CompileErrorException e) {
				}
				catch (final NoClassDefFoundError e) {
					env.error(annotation.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (final RuntimeException e) {
					env.thrownException(annotation, symForError, e);
				}
				finally {
					env.errorInMetaobject(cyanMetaobject,
							messageSendExpr.getFirstSymbol());
				}

				if ( codeType != null ) {

					/*
					 * error when two annotations of the same prototype try to
					 * replace the message passing. That means
					 * lastPrototypeWhereAnnotWithCodeReplacementWasFound ==
					 * puWhereAnnotWasFound
					 *
					 */
					if ( lastPrototypeWhereAnnotWithCodeReplacementWasFound == puWhereAnnotWasFound ) {
						env.error(symForError,
								"Two annotations attached to the same method are "
										+ "trying to replace a message passing. They are:\n"
										+ "    a) annotation '"
										+ lastAnnotWhereAnnotWithCodeReplacementWasFound
												.getCyanMetaobject().getName()
										+ "' of line "
										+ lastAnnotWhereAnnotWithCodeReplacementWasFound
												.getFirstSymbol()
												.getLineNumber()
										+ " of prototype '"
										+ lastPrototypeWhereAnnotWithCodeReplacementWasFound
												.getFullName()
										+ "'\n" + "    b) annotation '"
										+ annotation
												.getCyanMetaobject().getName()
										+ "' of line "
										+ annotation.getFirstSymbol()
												.getLineNumber()
										+ " of prototype '"
										+ puWhereAnnotWasFound.getFullName()
										+ "'");
						return null;
					}
					if ( lastPrototypeWhereAnnotWithCodeReplacementWasFound == null ) {
						/*
						 * this is the first annotation that is trying to
						 * replace the message passing. Let it replace it. The
						 * second one is not allowed to replace the message
						 * passing. If it is in the same prototype as the first
						 * one, an error is issued just above. If it is in a
						 * superprototype, it is just ignored.
						 */
						lastPrototypeWhereAnnotWithCodeReplacementWasFound = puWhereAnnotWasFound;
						lastAnnotWhereAnnotWithCodeReplacementWasFound = annotation;
						sb = codeType.f1;

						if ( messageSendExpr
								.getCodeThatReplacesThisExpr() != null ) {
							/*
							 * this message send has already been replaced by
							 * another expression
							 */
							if ( messageSendExpr
									.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
								env.warning(symForError,
										"Metaobject annotation '"
												+ cyanMetaobject.getName()
												+ "' at line "
												+ annotation.getFirstSymbol()
														.getLineNumber()
												+ " of prototype "
												+ annotation
														.getPackageOfAnnotation()
												+ "."
												+ annotation
														.getPackageOfAnnotation()
												+ " is trying to replace message send '"
												+ messageSendExpr.asString()
												+ "' by an expression. But this has already been asked by metaobject annotation '"
												+ messageSendExpr
														.getCyanAnnotationThatReplacedMSbyExpr()
														.getCyanMetaobject()
														.getName()
												+ "'" + " at line "
												+ messageSendExpr
														.getCyanAnnotationThatReplacedMSbyExpr()
														.getFirstSymbol()
														.getLineNumber()
												+ " of prototype "
												+ messageSendExpr
														.getCyanAnnotationThatReplacedMSbyExpr()
														.getPackageOfAnnotation()
												+ "."
												+ messageSendExpr
														.getCyanAnnotationThatReplacedMSbyExpr()
														.getPackageOfAnnotation());
							}
							else {
								env.warning(symForError,
										"Metaobject annotation '"
												+ cyanMetaobject.getName()
												+ "' at line "
												+ annotation.getFirstSymbol()
														.getLineNumber()
												+ " of prototype "
												+ annotation
														.getPackageOfAnnotation()
												+ "."
												+ annotation
														.getPackageOfAnnotation()
												+ " is trying to replace message send '"
												+ messageSendExpr.asString()
												+ "' by an expression. But this has already been asked by someone else");
							}
						}

						// if there is any errors, signals them
						env.errorInMetaobject(cyanMetaobject, symForError);

						// WrType wrTypeOfCode =
						// env.searchPackagePrototype(codeType.f2,
						// codeType.f3);
						Type typeOfCode;
						if ( codeType.f2 == null || codeType.f3 == null
								|| codeType.f2.length() == 0
								|| codeType.f3.length() == 0 ) {
							typeOfCode = messageSendExpr.getType();
						}
						else {
							typeOfCode = env.searchPackagePrototype(codeType.f2,
									codeType.f3);
							if ( typeOfCode == null ) {
								env.error(true, symForError,
										"This message send was replaced by an expression that has type '"
												+ codeType.f2 + "."
												+ codeType.f3
												+ "' which was not found",
										cyanMetaobject.getPrototypeOfType(),
										ErrorKind.prototype_was_not_found_inside_method);
							}
						}
						// GetHiddenItem.getHiddenType(codeType.f2);

						env.replaceStatementByCode(messageSendExpr, annotation,
								sb, typeOfCode);

						messageSendExpr.setCyanAnnotationThatReplacedMSbyExpr(
								annotation);

						toBeReturned = typeOfCode;
						// return typeOfCode;
					}
				}
			}
		}

		return toBeReturned;
	}

}
