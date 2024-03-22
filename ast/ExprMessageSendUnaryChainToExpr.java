
package ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import cyan.lang.CyString;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobjectAtAnnot;
import cyan.reflect._IActionMethodMissing__semAn;
import error.ErrorKind;
import lexer.Symbol;
import meta.CompilationInstruction;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.IActionMethodMissing_semAn;
import meta.IdentStarKind;
import meta.MetaHelper;
import meta.Timeout;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.WrExprAnyLiteral;
import meta.WrExprMessageSendUnaryChainToExpr;
import meta.WrPrototype;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
import saci.TupleTwo;

/**
 *
 * @author jose
 */
public class ExprMessageSendUnaryChainToExpr extends ExprMessageSendUnaryChain {

	public ExprMessageSendUnaryChainToExpr(Expr expr, MethodDec currentMethod) {
		super(currentMethod);
		this.receiverExprOrFirstUnary = expr;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		this.receiverExprOrFirstUnary.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public WrExprMessageSendUnaryChainToExpr getI() {
		if ( iExprMessageSendUnaryChainToExpr == null ) {
			iExprMessageSendUnaryChainToExpr = new WrExprMessageSendUnaryChainToExpr(
					this);
		}
		return iExprMessageSendUnaryChainToExpr;
	}

	private WrExprMessageSendUnaryChainToExpr iExprMessageSendUnaryChainToExpr = null;

	@Override
	public boolean isNRE(Env env) {
		return this.unarySymbol.symbolString.equals("new");
	}

	/**
	 * return true if this expression is non-recursive considering the point of
	 * view of method initShared.
	 *
	 * @param env
	 * @return
	 */
	@Override
	public boolean isNREForInitShared(Env env) {
		String name = receiverExprOrFirstUnary.asString();
		Prototype pu = env.getProject().getCyanLangPackage()
				.searchPublicNonGenericPrototype(name);
		return pu != null && this.unarySymbol.symbolString.equals("new");
	}

	/**
	 *
	 */
	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {

		receiverExprOrFirstUnary.genCyan(pw, printInMoreThanOneLine, cyanEnv,
				genFunctions);
		pw.print(" ");
		if ( hasBackquote ) pw.print("`");

		String str = unarySymbol.getSymbolString();
		String prefix = "";
		Token token = unarySymbol.token;
		if ( token == Token.INTER_ID )
			prefix = "?";
		else if ( token == Token.INTER_DOT_ID ) prefix = "?.";

		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			pw.print(prefix + cyanEnv.formalGenericParamToRealParam(str));
		}
		else {
			pw.print(prefix + str);
		}

	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String receiverAsString;
		String tmp = NameServer.nextJavaLocalVariableName();
		boolean isNew = false;

		if ( type == null ) {
			this.calcInternalTypes(env);
		}

		if ( this.javaReceiver ) {
			String messageSendTmpVar = null;
			String resultTmpVar = NameServer.nextJavaLocalVariableName();

			Class<?>[] exceptionList = null;
			if ( javaMethod != null )
				exceptionList = javaMethod.getExceptionTypes();
			else if ( !isJavaField ) {
				if ( constructor != null ) {
					// either ` or ? was used:
					// elem ?hashCode
					// elem `s
					exceptionList = constructor.getExceptionTypes();
				}
			}

			if ( !isJavaField && unarySymbol.getSymbolString().equals("new") ) {

				if ( exceptionList != null && exceptionList.length == 0 ) {
					pw.printIdent(
							type.getJavaName() + " " + resultTmpVar + " = ");
					pw.println("new "
							+ ((TypeJavaClass) this.receiverExprOrFirstUnary
									.getType()).getName()
							+ "();");
				}
				else {
					pw.printlnIdent(type.getJavaName() + " " + resultTmpVar
							+ " = null;");
					pw.printlnIdent("try {");
					pw.add();

					pw.printlnIdent(resultTmpVar + " = new "
							+ ((TypeJavaClass) this.receiverExprOrFirstUnary
									.getType()).getName()
							+ "();");
				}
			}
			else {
				String javaTypeName = type.getJavaName();
				boolean isVoid = javaTypeName.equals("void");

				if ( exceptionList == null || exceptionList.length == 0 ) {
					messageSendTmpVar = receiverExprOrFirstUnary.genJavaExpr(pw,
							env);

					if ( isVoid ) {
						pw.printIdent("");
					}
					else {
						pw.printIdent(type.getJavaName() + " " + resultTmpVar
								+ " = ");
					}

					pw.print(messageSendTmpVar + "."
							+ this.unarySymbol.getSymbolString());
					if ( isJavaField ) {
						pw.println(";");
					}
					else {
						pw.println("();");
					}
				}
				else {
					messageSendTmpVar = receiverExprOrFirstUnary.genJavaExpr(pw,
							env);

					if ( isVoid ) {
						pw.printIdent("");
					}
					else {
						pw.printlnIdent(type.getJavaName() + " " + resultTmpVar
								+ " = null;");
					}
					pw.printlnIdent("try {");
					pw.add();

					if ( isVoid ) {
						pw.printlnIdent(messageSendTmpVar + "."
								+ this.unarySymbol.getSymbolString() + "();");
					}
					else {
						pw.printlnIdent(resultTmpVar + " = " + messageSendTmpVar
								+ "." + this.unarySymbol.getSymbolString()
								+ "();");
					}
				}
			}
			if ( exceptionList != null && exceptionList.length != 0 ) {
				pw.sub();
				pw.printlnIdent("} catch (Throwable e) {");
				pw.printlnIdent(
						"    throw new ExceptionContainer__(new _ExceptionJavaException(e));");
				pw.printlnIdent("}");
			}

			return resultTmpVar;
		}

		Token tokenFirstkeyword = this.unarySymbol.token;
		boolean precededbyInter = tokenFirstkeyword == Token.INTER_ID
				|| tokenFirstkeyword == Token.INTER_DOT_ID;

		if ( hasBackquote ) {

			String paramArray = "";
			/*
			 * this for is not necessary. quotedVariableList must have just one
			 * element.
			 */
			VariableDecInterface varDec = quotedVariableList.get(0);
			String javaNameVariable = varDec.javaNameWithRef();
			if ( varDec.getType() == Type.Dyn ) {
				paramArray += "((CyString ) " + varDec.javaNameWithRef()
						+ ").s";
				pw.printIdent("if ( ! (" + javaNameVariable
						+ " instanceof CyString) ) ");

				pw.println("throw new ExceptionContainer__("
						+ env.javaCodeForCastException(varDec, Type.Dyn)
						+ " );");

			}
			else
				paramArray += "((CyString ) " + varDec.javaNameWithRef()
						+ ").s";

			String keywordJavaName = NameServer.nextJavaLocalVariableName();
			pw.printIdent("String " + keywordJavaName + " = ");
			pw.println(" CyanRuntime.getJavaNameOfUnaryMethod(" + paramArray
					+ ");");

			receiverAsString = receiverExprOrFirstUnary.genJavaExpr(pw, env);

			String resultTmp = genJavaDynUnaryMessageSend(pw, receiverAsString,
					javaNameVariable, keywordJavaName, javaNameVariable);

			return resultTmp;
		}
		else {
			if ( !precededbyInter
					&& unarySymbol.getSymbolString().equals("new") ) {
				// in "Person new" the generated code should be "new _Person()"
				Type aType = receiverExprOrFirstUnary
						.ifRepresentsTypeReturnsType(env);
				receiverAsString = aType.getJavaName();
				// t =
				// NameServer.getJavaName(receiverExprOrFirstUnary.asString());
				isNew = true;
			}
			else {
				receiverAsString = receiverExprOrFirstUnary.genJavaExpr(pw,
						env);
				/*
				 * ###
				 *
				 */
				// boolean alreadyGeneratedCode = false;
				// if ( this.receiverExprOrFirstUnary instanceof ExprIdentStar )
				// {
				// ExprIdentStar eis = (ExprIdentStar )
				// this.receiverExprOrFirstUnary;
				// if ( eis.getIdentStarKind() == IdentStarKind.unaryMethod_t )
				// {
				// if (
				// eis.getMethodSignatureForMessageSend().getReturnType(env) ==
				// Type.Nil ) {
				// // receiver is a unary method that returns Nil
				// pw.printlnIdent(receiverAsString + ";");
				// pw.printlnIdent("_Nil.prototype." + keywordJavaName + "();");
				// alreadyGeneratedCode = true;
				// }
				// }
				// }

			}
			if ( isNew ) {
				pw.printIdent(type.getJavaName() + " " + tmp + " = ");
				pw.println("new " + receiverAsString + "();");
			}
			else {
				String cyanMethodName = unarySymbol.getSymbolString();
				String keywordJavaName = MetaHelper
						.getJavaNameOfkeyword(cyanMethodName);
				String originalCyanMethodName = cyanMethodName;
				Type receiverType = receiverExprOrFirstUnary.getType();
				if ( receiverType == Type.Dyn || precededbyInter ) {
					cyanMethodName = "new CyString(\"" + cyanMethodName + "\")";
					keywordJavaName = "\"" + keywordJavaName + "\"";
					tmp = genJavaDynUnaryMessageSend(pw, receiverAsString,
							cyanMethodName, keywordJavaName,
							originalCyanMethodName);
				}
				else {
					String methodName = this.unarySymbol.getSymbolString();
					List<MethodSignature> methodSignatureList = receiverType
							.searchMethodPublicPackageSuperPublicPackage(
									methodName, env);
					if ( methodSignatureList == null
							|| methodSignatureList.size() == 0 ) {
						methodSignatureList = receiverType
								.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
										methodName, env);
					}
					if ( methodSignatureList == null
							|| methodSignatureList.size() == 0 ) {
						// System.out.println("receiver Type: " +
						// receiverType.getFullName() + " method name: " +
						// methodName);
						// receiverType.searchMethodPublicPackageSuperPublicPackage(methodName,
						// env);
						env.error(this.getFirstSymbol(),
								"Internal compiler error when generating code "
										+ "for a unary message passing, ExprMessageSendUnaryChainToExpr");
						return null;
					}

					if ( receiverType instanceof TypeIntersection ) {

						InterfaceDec interDec;

						interDec = methodSignatureList.get(0)
								.getDeclaringInterface();
						if ( interDec == null ) {
							env.error(this.getFirstSymbol(),
									"Internal compiler error when generating code "
											+ "for a unary message passing, ExprMessageSendUnaryChainToExpr, "
											+ "and the receiver is an intersection type");
							return null;
						}
						else {
							String methodPrototype = interDec.getJavaName();
							receiverAsString = "((" + methodPrototype + ") "
									+ receiverAsString + ")";
						}
					}
					boolean alreadyGeneratedCode = false;
					if ( this.receiverExprOrFirstUnary instanceof ExprIdentStar ) {
						ExprIdentStar eis = (ExprIdentStar) this.receiverExprOrFirstUnary;
						if ( eis.getIdentStarKind() == IdentStarKind.unaryMethod_t ) {
							if ( eis.getMethodSignatureForMessageSend()
									.getReturnType(env) == Type.Nil ) {
								// receiver is a unary method that returns Nil
								pw.printlnIdent(receiverAsString + ";");
								pw.printlnIdent("_Nil.prototype."
										+ keywordJavaName + "();");
								alreadyGeneratedCode = true;
							}
						}
					}
					if ( !alreadyGeneratedCode ) {
						if ( methodSignatureList.get(0)
								.getReturnType(env) == Type.Nil ) {
							pw.printlnIdent(type.getJavaName() + " " + tmp
									+ " = _Nil.prototype;");
							pw.printlnIdent(receiverAsString + "."
									+ keywordJavaName + "();");
						}
						else {
							pw.printIdent(
									type.getJavaName() + " " + tmp + " = ");
							pw.print(receiverAsString + ".");
							pw.println(keywordJavaName + "();");
						}
					}
				}
			}
			return tmp;
		}
	}

	/**
	 * @param pw
	 * @param receiverAsString
	 * @param cyanMethodName
	 * @param keywordJavaName
	 * @return
	 */
	@SuppressWarnings("static-method")
	private String genJavaDynUnaryMessageSend(PWInterface pw,
			String receiverAsString, String cyanMethodName,
			String keywordJavaName, String originalCyanMethodName) {

		java.lang.reflect.Method mmm;

		String tmp;
		String aMethodTmp = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("java.lang.reflect.Method " + aMethodTmp
				+ " = CyanRuntime.getJavaMethodByName(" + receiverAsString
				+ ".getClass(), " + keywordJavaName + ", 0);");
		pw.printlnIdent("if ( " + aMethodTmp + " == null ) { " + aMethodTmp
				+ " = CyanRuntime.getJavaMethodByName(" + receiverAsString
				+ ".getClass(), \"" + originalCyanMethodName + "\", 0); }");
		tmp = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("Object " + tmp + " = null;");
		pw.printlnIdent("if ( " + aMethodTmp + " != null ) { ");
		pw.add();
		pw.printlnIdent("try {");
		pw.add();
		pw.printlnIdent(aMethodTmp + ".setAccessible(true);");
		pw.printlnIdent(
				"if ( " + aMethodTmp + ".getReturnType() == void.class ) {");
		pw.add();
		pw.printlnIdent(tmp + " = _Nil.prototype;");
		pw.printlnIdent(aMethodTmp + ".invoke(" + receiverAsString + ");");
		pw.sub();
		pw.printlnIdent("}");
		pw.printlnIdent("else {");
		pw.add();
		pw.printlnIdent(tmp + " = " + aMethodTmp + ".invoke(" + receiverAsString
				+ ");");
		pw.sub();
		pw.printlnIdent("}");
		pw.sub();
		pw.printlnIdent("}");

		String ep = NameServer.nextJavaLocalVariableName();

		pw.printlnIdent("catch ( java.lang.reflect.InvocationTargetException "
				+ ep + " ) {");
		pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
		pw.printlnIdent("	if ( t__ instanceof ExceptionContainer__ ) {");
		pw.printlnIdent(
				"    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
		pw.printlnIdent("	}");
		pw.printlnIdent("	else");
		pw.printlnIdent(
				"		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
		pw.printlnIdent("}");
		pw.printlnIdent(
				"catch (IllegalAccessException | IllegalArgumentException " + ep
						+ ") {");
		pw.printlnIdent(
				"        throw new ExceptionContainer__( new _ExceptionDoesNotUnderstand("
						+ cyanMethodName + " ) );");

		pw.printlnIdent("}");

		pw.sub();
		pw.printlnIdent("}");
		pw.printlnIdent("else { ");
		pw.add();
		String dnuTmpVar = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent(
				"//	func doesNotUnderstand: (String methodName, Array<Array<Dyn>> args)");
		pw.printlnIdent("java.lang.reflect.Method " + dnuTmpVar
				+ " = CyanRuntime.getJavaMethodByName(" + receiverAsString
				+ ".getClass(), \"" + NameServer.javaNameDoesNotUnderstand
				+ "\", 2);");
		pw.printlnIdent("if ( " + dnuTmpVar + " == null ) {");
		pw.printlnIdent(
				"    throw new ExceptionContainer__( new _ExceptionDoesNotUnderstand(new CyString(\"doesNotUnderstand\") ) );");
		pw.printlnIdent("}");

		pw.printlnIdent("try {");
		pw.add();
		pw.printlnIdent(
				NameServer.ArrayArrayDynInJava + " arrayArrayParam = new "
						+ NameServer.ArrayArrayDynInJava + "();");
		pw.printlnIdent(NameServer.ArrayDynInJava + " arrayParam = new "
				+ NameServer.ArrayDynInJava + "();");
		pw.printlnIdent("arrayArrayParam._add_1( arrayParam );");
		pw.printlnIdent(dnuTmpVar + ".setAccessible(true);");

		pw.printlnIdent(tmp + " = " + dnuTmpVar + ".invoke(" + receiverAsString
				+ ", " + cyanMethodName + ", arrayArrayParam);");
		pw.sub();

		pw.printlnIdent("}");
		ep = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("catch ( java.lang.reflect.InvocationTargetException "
				+ ep + " ) {");
		pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
		pw.printlnIdent("	if ( t__ instanceof ExceptionContainer__ ) {");
		pw.printlnIdent(
				"    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
		pw.printlnIdent("	}");
		pw.printlnIdent("	else");
		pw.printlnIdent(
				"		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
		pw.printlnIdent("}");
		pw.printlnIdent(
				"catch (IllegalAccessException | IllegalArgumentException " + ep
						+ ") {");

		pw.printlnIdent(
				"        throw new ExceptionContainer__( new _ExceptionDoesNotUnderstand(new CyString("
						+ keywordJavaName + ") ) );");

		pw.printlnIdent("}");
		pw.sub();
		pw.printlnIdent("}");
		return tmp;
	}

	@Override
	public Symbol getFirstSymbol() {
		if ( backquoteSymbol != null
				&& (receiverExprOrFirstUnary instanceof ast.ExprMessageSendUnaryChainToExpr) ) {
			return backquoteSymbol;
		}
		else {
			return receiverExprOrFirstUnary.getFirstSymbol();
		}
	}

	public Expr getReceiver() {
		return receiverExprOrFirstUnary;
	}

	@Override
	public void calcInternalTypes(Env env) {
		String methodName;
		List<MethodSignature> methodSignatureList = null;

		Type exprType;

		Token tokenFirstkeyword = unarySymbol.token;

		boolean receiverIsPrototype = false;
		try {
			env.pushCheckUsePossiblyNonInitializedPrototype(false);
			if ( receiverExprOrFirstUnary instanceof ExprSelf ) {
				((ExprSelf) receiverExprOrFirstUnary)
						.calcInternalTypesDoNotCheckSelf(env);
			}
			else {
				receiverExprOrFirstUnary.calcInternalTypes(env);
			}

		}
		finally {
			env.popCheckUsePossiblyNonInitializedPrototype();
		}
		if ( this.receiverExprOrFirstUnary instanceof ExprIdentStar ) {
			receiverIsPrototype = ((ExprIdentStar) this.receiverExprOrFirstUnary)
					.getIdentStarKind() == IdentStarKind.prototype_t;
		}

		exprType = receiverExprOrFirstUnary.getType(env);
		if ( exprType == null ) {
			exprType = receiverExprOrFirstUnary.getType(env);
		}
		methodName = this.unarySymbol.getSymbolString();

		if ( exprType instanceof TypeJavaRef ) {
			this.javaReceiver = true;

			if ( methodName.equals("new") ) {

				if ( receiverExprOrFirstUnary instanceof ExprIdentStar ) {
					ExprIdentStar eis = (ExprIdentStar) receiverExprOrFirstUnary;
					if ( eis.getIdentStarKind() != IdentStarKind.jvmClass_t ) {
						env.error(this.getFirstSymbol(),
								"The receiver is a Java object. It should be a Java class name");
					}
					else {
						// a java class
						if ( !(exprType instanceof TypeJavaClass) ) {
							env.error(this.getFirstSymbol(),
									"A Java class was expected as receiver of the message 'new'");
						}
						TypeJavaClass javaClass = (TypeJavaClass) exprType;
						try {
							constructor = javaClass
									.getClassLoad(env, eis.getFirstSymbol())
									.getConstructor();
							type = javaClass;
							return;
						}
						catch (NoSuchMethodException | SecurityException e) {
							env.error(this.getFirstSymbol(),
									"A Java constructor for this message send was not found "
											+ "or there was a security exception (class SecurityException)");
						}
					}

				}
				else if ( receiverExprOrFirstUnary instanceof ExprGenericPrototypeInstantiation ) {

					ExprGenericPrototypeInstantiation gpi = (ExprGenericPrototypeInstantiation) receiverExprOrFirstUnary;
					if ( !(exprType instanceof TypeJavaClass) ) {
						env.error(this.getFirstSymbol(),
								"The receiver is a Java object. It should be a Java class name");
					}
					else {
						TypeJavaClass javaClass = (TypeJavaClass) exprType;
						try {
							constructor = javaClass
									.getClassLoad(env, gpi.getFirstSymbol())
									.getConstructor();
							type = javaClass;
							return;
						}
						catch (NoSuchMethodException | SecurityException e) {
							env.error(this.getFirstSymbol(),
									"A Java constructor for this message send was not found "
											+ "or there was a security exception (class SecurityException)");
						}
					}

				}
				else {
					env.error(this.getFirstSymbol(),
							"The receiver is a Java object. It should be a Java class name");
				}
			}
			else {
				// a message passing to a Java object

				if ( !(exprType instanceof TypeJavaRef) ) {
					env.error(this.getFirstSymbol(),
							"A Java class was expected as receiver of the message 'new: args'");
				}
				if ( hasBackquote ) {
					if ( receiverIsPrototype ) {
						env.error(getFirstSymbol(),
								"Quoted message sends cannot be sent to prototypes. "
										+ "It does not make sense because you know the methods the prototype has at compile-time");
					}
					calcInternaltTypesWithBackquote(env, unarySymbol);
					type = Type.Dyn;
					return;
				}
				else if ( tokenFirstkeyword == Token.INTER_DOT_ID
						|| tokenFirstkeyword == Token.INTER_ID
						|| exprType == Type.Dyn ) {
					/*
					 * INTER_ID_COLON("~InterIdColon"), // ?name:
					 * INTER_ID("~InterId"), // ?name
					 * INTER_DOT_ID_COLON("~InterDotIdColon"), // ?.name:
					 * INTER_DOT_ID("~InterDotId"), // ?.name
					 */
					type = Type.Dyn;
					if ( receiverIsPrototype ) {
						env.error(getFirstSymbol(),
								"The receiver of this message passing is a prototype. "
										+ "So it does not make sense to send a message to it using '?'. "
										+ "You already know its methods");
					}

					return;

				}
				else {
					TypeJavaRef javaClass = (TypeJavaRef) exprType;
					try {

						javaMethod = javaClass
								.getClassLoad(env, this.getFirstSymbol())
								.getMethod(methodName);

						Class<?> methodReturnType = javaMethod.getReturnType();
						type = TypeJavaRef.classJavaToTypeJavaRef(
								methodReturnType, env, unarySymbol);
						checkPublicStatic(env, methodName, javaClass);
					}
					catch (NoSuchMethodException | SecurityException e) {
						try {
							if ( javaClass instanceof TypeJavaRefArray ) {
								if ( methodName.equals("length") ) {
									// the 'length' field of arrays is not found
									// by getField. Wierd!
									type = TypeJavaRef.classJavaToTypeJavaRef(
											int.class, env, unarySymbol);
									this.isJavaField = true;
									return;
								}
							}
							Field f = javaClass.aClass.getField(methodName);
							type = TypeJavaRef.classJavaToTypeJavaRef(
									f.getType(), env, unarySymbol);
							this.isJavaField = true;
							checkPublicStatic(env, methodName, javaClass);
						}
						catch (NoSuchFieldException | SecurityException e1) {
						}
						env.error(this.getFirstSymbol(),
								"A Java method for the message send '"
										+ methodName + "' was not found "
										+ "or there was a security exception (class SecurityException)");
					}

				}
			}
			return;

		}
		else {
			if ( exprType instanceof TypeWithAnnotations ) {
				List<MethodSignature> mList = Type.Any
						.searchMethodPublicPackage(methodName, env);
				if ( mList == null || mList.size() == 0 ) {
					env.error(this.getFirstSymbol(),
							"The type of the receiver has an attached annotation. "
									+ "Hence, it can only receive messages corresponding to methods of prototype Any");
				}
			}

		}

		if ( env.getCurrentMethod() != null ) {
			String currentMethodName = env.getCurrentMethod()
					.getNameWithoutParamNumber();
			if ( currentMethodName.equals("init")
					|| currentMethodName.equals("init:") ) {
				/**
				 * inside an init or init: method it is illegal to access 'self'
				 * as in the following statements. 'iv' is used for a field and
				 * 'im' for instance method. iv m1; im; im m1 m2; self im;
				 * self.iv m1 m2;
				 *
				 */
				if ( this.receiverExprOrFirstUnary instanceof ExprIdentStar ) {
					ExprIdentStar e = (ExprIdentStar) this.receiverExprOrFirstUnary;
					if ( e.getIdentStarKind() == IdentStarKind.instance_variable_t
							&& !((FieldDec) e.getVarDeclaration())
									.isShared() ) {
						if ( !((FieldDec) e.getVarDeclaration())
								.getWasInitialized() ) {
							env.error(this.getFirstSymbol(),
									"Access to a non-initialized field");
						}
						/*
						 * env.error(this.getFirstSymbol(),
						 * "Access to a field in an expression inside an 'init' or 'init:' method. This is illegal because the "
						 * +
						 * "Cyan compiler is not able yet to discover if the field have been initialized or not"
						 * );
						 */

					}
					else if ( e
							.getIdentStarKind() == IdentStarKind.unaryMethod_t ) {
						env.error(this.getFirstSymbol(),
								"Message send to 'self' inside an 'init' or 'init:' method. This is illegal because it can call a "
										+ " subprototype method and this method can access a field that has not been initialized");
					}
				}
				// else if ( this.receiverExprOrFirstUnary instanceof
				// ast.ExprSelf ) {
				// env.error(this.getFirstSymbol(), "Message send to 'self'
				// inside an 'init' or 'init:' method. This is illegal because
				// it can call a "
				// + " subprototype method and this method can access a field
				// that has not been initialized");
				// }
				else if ( this.receiverExprOrFirstUnary instanceof ExprSelfPeriodIdent
						&& !(((ExprSelfPeriodIdent) this.receiverExprOrFirstUnary)
								.getFieldDec().isShared()) ) {

					if ( !((ExprSelfPeriodIdent) this.receiverExprOrFirstUnary)
							.getFieldDec().getWasInitialized() ) {
						env.error(this.getFirstSymbol(),
								"Access to a non-initialized field");

					}
					/*
					 * env.error(this.getFirstSymbol(),
					 * "Access to a field in an expression inside an 'init' or 'init:' method. This is illegal because the "
					 * +
					 * "Cyan compiler is not able yet to discover if the field have been initialized or not"
					 * );
					 *
					 */
				}
			}
			else if ( currentMethodName.equals("initShared") ) {
				/**
				 * inside an initShared method it is illegal to access 'self' as
				 * in the following statements. 'iv' is used for a field and
				 * 'im' for instance method. iv m1; im; im m1 m2; self im;
				 * self.iv m1 m2;
				 *
				 */
				if ( this.receiverExprOrFirstUnary instanceof ExprIdentStar ) {
					ExprIdentStar e = (ExprIdentStar) this.receiverExprOrFirstUnary;
					if ( e.getIdentStarKind() == IdentStarKind.instance_variable_t ) {
						env.error(this.getFirstSymbol(),
								"Access to a field in an expression inside an 'initShared'. This is illegal because the "
										+ "the field has not been initialized");

					}
					else if ( e
							.getIdentStarKind() == IdentStarKind.unaryMethod_t ) {
						env.error(this.getFirstSymbol(),
								"Message send to 'self' inside an 'initShared' method. This is illegal because 'self' is being used and "
										+ "through it some field that has not been initialized may be accessed");
					}
				}
				else if ( this.receiverExprOrFirstUnary instanceof ast.ExprSelf ) {
					env.error(this.getFirstSymbol(),
							"Message send to 'self' inside an 'initShared' or 'init:' method. This is illegal because "
									+ "through 'self' some field that has not been initialized may be accessed");
				}
				else if ( this.receiverExprOrFirstUnary instanceof ExprSelfPeriodIdent
						&& !(((ExprSelfPeriodIdent) this.receiverExprOrFirstUnary)
								.getFieldDec().isShared()) ) {
					env.error(this.getFirstSymbol(),
							"Access to a field in an expression inside an 'initShared'. This is illegal because the "
									+ "the field has not been initialized");
				}
			}

		}

		// if ` was used, there is no search for the method at compile-time.
		if ( hasBackquote ) {

			if ( receiverExprOrFirstUnary == null
					|| receiverExprOrFirstUnary instanceof ExprSelf ) {
				MethodDec currentMethod = env.getCurrentMethod();
				if ( currentMethod != null ) {
					String currentMethodName = currentMethod
							.getNameWithoutParamNumber();
					if ( currentMethodName.equals("init")
							|| currentMethodName.equals("init:") ) {
						env.error(this.getFirstSymbol(),
								"Message send to 'self' inside an 'init' or 'init:' method. "
										+ "This is illegal because it can access a non-initialized field or call a "
										+ " subprototype method and this method can access a non-initialized field",
								true, true);
					}

				}
			}

			if ( receiverIsPrototype ) {
				env.error(getFirstSymbol(),
						"Quoted message sends cannot be sent to prototypes. "
								+ "It does not make sense because you know the methods the prototype has at compile-time");
			}
			calcInternaltTypesWithBackquote(env, unarySymbol);
			type = Type.Dyn;
			return;
		}
		else if ( tokenFirstkeyword == Token.INTER_DOT_ID
				|| tokenFirstkeyword == Token.INTER_ID
				|| exprType == Type.Dyn ) {
			/*
			 * INTER_ID_COLON("~InterIdColon"), // ?name: INTER_ID("~InterId"),
			 * // ?name INTER_DOT_ID_COLON("~InterDotIdColon"), // ?.name:
			 * INTER_DOT_ID("~InterDotId"), // ?.name
			 */
			if ( receiverExprOrFirstUnary == null
					|| receiverExprOrFirstUnary instanceof ExprSelf ) {
				MethodDec currentMethod = env.getCurrentMethod();
				if ( currentMethod != null ) {
					String currentMethodName = currentMethod
							.getNameWithoutParamNumber();
					if ( currentMethodName.equals("init")
							|| currentMethodName.equals("init:") ) {
						env.error(this.getFirstSymbol(),
								"Message send to 'self' inside an 'init' or 'init:' method. "
										+ "This is illegal because it can access a non-initialized field or call a "
										+ " subprototype method and this method can access a non-initialized field",
								true, true);
					}
				}
			}

			type = Type.Dyn;
			if ( receiverIsPrototype ) {
				env.error(getFirstSymbol(),
						"The receiver of this message passing is a prototype. "
								+ "So it does not make sense to send a message to it using '?'. "
								+ "You already know its methods");
			}

			return;

		}
		else {
			methodName = unarySymbol.getSymbolString();
			ExprReceiverKind receiverKind = ExprReceiverKind.EXPR_R;

			List<MethodSignature> allMethodSignatureList = new ArrayList<>();

			boolean isInit = methodName.equals("init");
			boolean isNew = methodName.equals("new");
			if ( isInit ) {
				env.error(this.getFirstSymbol(),
						"'init' and 'init:' messages have a restricted use. "
								+ "They can only appear inside an 'init' or 'init:' method and they currently can only be sent to 'super'");
			}

			Type receiverType = this.receiverExprOrFirstUnary.getType(env);
			if ( receiverType instanceof TypeUnion ) {
				env.error(this.getFirstSymbol(),
						"The type of the message receiver, '"
								+ receiverExprOrFirstUnary.asString()
								+ "'  is an union type. Then the receiver cannot receive messages",
						true, true);
			}

			if ( isNew ) {

				boolean ok = false;
				if ( receiverExprOrFirstUnary instanceof ExprTypeof ) {
					if ( ((ExprTypeof) receiverExprOrFirstUnary)
							.getType() instanceof ObjectDec )
						ok = true;
				}
				else {
					TupleTwo<String, Type> t = receiverExprOrFirstUnary
							.ifPrototypeReturnsNameWithPackageAndType(env);
					ok = t != null && t.f2 != null;
				}

				if ( !ok ) {
					env.error(true, unarySymbol,
							"Message '" + methodName
									+ "'  can only be sent to prototypes",
							methodName,
							ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);
				}
				else {
					receiverKind = ExprReceiverKind.PROTOTYPE_R;
					methodSignatureList = ((ObjectDec) exprType)
							.searchInitNewMethod(methodName);
					if ( methodSignatureList != null
							&& methodSignatureList.size() > 0 ) {
						MethodDec aMethod = methodSignatureList.get(0)
								.getMethod();
						if ( aMethod != null ) {
							if ( aMethod.getVisibility() == Token.PRIVATE ) {
								if ( aMethod.getDeclaringObject() != env
										.getCurrentOuterObjectDec() ) {
									env.error(this.getFirstSymbol(),
											"This 'new' method is private. It can only be called "
													+ "inside prototype '"
													+ aMethod
															.getDeclaringObject()
															.getFullName()
													+ "'",
											true, true);
								}
							}
							else if ( aMethod
									.getVisibility() == Token.PACKAGE ) {
								if ( aMethod.getDeclaringObject()
										.getCompilationUnit()
										.getCyanPackage() != env
												.getCurrentObjectDec()
												.getCompilationUnit()
												.getCyanPackage() ) {
									env.error(this.getFirstSymbol(),
											"This 'new' method has 'package' visibility. It can only be called "
													+ "inside package '"
													+ aMethod
															.getDeclaringObject()
															.getCompilationUnit()
															.getCyanPackage()
															.getName()
													+ "'",
											true, true);
								}
							}
							else if ( aMethod
									.getVisibility() == Token.PROTECTED ) {
								if ( !aMethod.getDeclaringObject()
										.isSupertypeOf(
												env.getCurrentObjectDec(),
												env) ) {
									env.error(this.getFirstSymbol(),
											"This 'new' method has 'protected' visibility. It can only be called "
													+ "in subprototypes of '"
													+ aMethod
															.getDeclaringObject()
															.getName()
													+ "'",
											true, true);
								}
							}
						}
					}
					// MetaInfoServer.checkMessageSendWithMethodMetaobject(methodSignatureList,
					// receiverType, e, env, unarySymbol.get(i));
				}

			}
			else if ( receiverExprOrFirstUnary instanceof ExprSelf ) {
				if ( methodName.equals("init") ) {
					methodSignatureList = ((ObjectDec) exprType)
							.searchInitNewMethod(methodName);

					if ( methodSignatureList != null
							&& methodSignatureList.size() > 0 ) {
						MethodDec aMethod = methodSignatureList.get(0)
								.getMethod();
						if ( aMethod != null ) {
							if ( aMethod.getVisibility() == Token.PRIVATE ) {
								if ( aMethod.getDeclaringObject() != env
										.getCurrentOuterObjectDec() ) {
									env.error(this.getFirstSymbol(),
											"This 'init' method is private. It cannot be called "
													+ "inside prototype '"
													+ env.getCurrentPrototype()
															.getFullName()
													+ "'");
								}
							}
							else if ( aMethod
									.getVisibility() == Token.PACKAGE ) {
								if ( aMethod.getDeclaringObject()
										.getCompilationUnit()
										.getCyanPackage() != env
												.getCurrentObjectDec()
												.getCompilationUnit()
												.getCyanPackage() ) {
									env.error(this.getFirstSymbol(),
											"This 'init' method has 'package' visibility. It can only be called "
													+ "inside package '"
													+ aMethod
															.getDeclaringObject()
															.getCompilationUnit()
															.getCyanPackage()
															.getName()
													+ "'");
								}
							}
							else if ( aMethod
									.getVisibility() == Token.PROTECTED ) {
								if ( !aMethod.getDeclaringObject()
										.isSupertypeOf(
												env.getCurrentObjectDec(),
												env) ) {
									env.error(this.getFirstSymbol(),
											"This 'init' method has 'protected' visibility. It can only be called "
													+ "in subprototypes of '"
													+ aMethod
															.getDeclaringObject()
															.getName()
													+ "'",
											true, true);
								}
							}
						}
					}

				}
				else {
					// searches method in the prototype of 'self', the current
					// prototype
					methodSignatureList = exprType
							.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
									methodName, env);

					Prototype pu = (Prototype) exprType.getInsideType();
					List<Prototype> superList = pu
							.get_this_and_all_superPrototypes();
					for (Prototype current : superList) {
						List<MethodSignature> currentMSList = current
								.searchMethodPrivateProtectedPublicPackage(
										methodName, env);
						if ( currentMSList != null ) {
							allMethodSignatureList.addAll(currentMSList);
						}
					}

				}
				receiverKind = ExprReceiverKind.SELF_R;
			}
			else {
				if ( methodName.equals("init") ) {
					env.error(true, unarySymbol,
							"Message 'init' can only be sent to 'self' or 'super'",
							methodName,
							ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);
				}

				methodSignatureList = exprType
						.searchMethodPublicPackageSuperPublicPackage(methodName,
								env);

				if ( !(receiverType instanceof TypeUnion)
						&& !(receiverType instanceof TypeIntersection) ) {
					Prototype pu = (Prototype) exprType.getInsideType();
					List<Prototype> superList = pu
							.get_this_and_all_superPrototypes();
					for (Prototype current : superList) {
						List<MethodSignature> currentMSList = current
								.searchMethodPublicPackage(methodName, env);
						if ( currentMSList != null ) {
							allMethodSignatureList.addAll(currentMSList);
						}
					}
				}

			}

			if ( isNew || isInit ) {
				if ( methodSignatureList == null
						|| methodSignatureList.size() == 0 ) {
					env.error(getFirstSymbol(),
							"Method " + methodName
									+ " was not found in prototype "
									+ exprType.getName());
					return;
				}
			}
			else {
				if ( methodSignatureList == null
						|| methodSignatureList.size() == 0 ) {

					methodSignatureList = exprType
							.searchMethodPublicPackageSuperPublicPackage(
									methodName, env);

					Prototype pu = (Prototype) exprType.getInsideType();
					List<Prototype> superList = pu
							.get_this_and_all_superPrototypes();

					List<MethodSignature> currentMSList = pu
							.searchMethodPublicPackage(methodName, env);
					if ( currentMSList != null ) {
						allMethodSignatureList.addAll(currentMSList);
					}
					for (Prototype current : superList) {
						currentMSList = current
								.searchMethodPublicPackage(methodName, env);
						if ( currentMSList != null ) {
							allMethodSignatureList.addAll(currentMSList);
						}
					}

					ObjectDec currentProto = env.getCurrentObjectDec();
					if ( currentProto != null
							&& currentProto.getOuterObject() != null ) {
						/*
						 * inner prototypes can access private and protected
						 * members of the outer prototype
						 */
						methodSignatureList = exprType
								.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
										methodName, env);
						pu = (Prototype) exprType.getInsideType();
						superList = pu.get_this_and_all_superPrototypes();
						for (Prototype current : superList) {
							currentMSList = current
									.searchMethodPrivateProtectedPublicPackage(
											methodName, env);
							if ( currentMSList != null ) {
								allMethodSignatureList.addAll(currentMSList);
							}
						}

					}

					if ( methodSignatureList == null
							|| methodSignatureList.size() == 0 ) {

						if ( exprType == env.getCurrentObjectDec() ) {
							// in a prototype 'Program', there may be something
							// like
							// 'Program sgetCount' in which sgetCount is a
							// private shared method
							methodSignatureList = exprType
									.searchMethodPrivateProtectedPublicPackage(
											methodName, env);
							if ( methodSignatureList != null
									&& methodSignatureList.size() > 0 ) {
								MethodSignature ms = methodSignatureList.get(0);
								if ( !ms.getMethod().getShared() ) {
									env.error(true, getFirstSymbol(), "Method "
											+ methodName
											+ " was not found in prototype "
											+ exprType.getName()
											+ " or its super-prototypes",
											methodName,
											ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);
								}
							}
						}

						if ( env.getCompInstSet()
								.contains(CompilationInstruction.semAn_actions)
								&& super.lookForUnaryMethodAtCompileTime(
										this.receiverExprOrFirstUnary.getI(),
										this.unarySymbol.getI(),
										this.receiverExprOrFirstUnary.getType(),
										env) ) {

							return;
						}
						else {
							if ( receiverExprOrFirstUnary instanceof ExprIdentStar
									&& ((ExprIdentStar) receiverExprOrFirstUnary)
											.getName().equals("new") ) {
								env.error(true, getFirstSymbol(),
										"It seems you are trying to create an object of "
												+ methodName
												+ " . Since 'new' is a method name, put it after '"
												+ methodName + "', not before",
										methodName,
										ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);
							}
							else {
								// if ( env.getCurrentObjectDec() != exprType )
								// {
								// }
								if ( methodSignatureList == null
										|| methodSignatureList.size() == 0 ) {
									env.error(true, getFirstSymbol(), "Method "
											+ methodName
											+ " was not found in prototype "
											+ exprType.getName()
											+ " or its super-prototypes",
											methodName,
											ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);
								}
							}
							return;
						}
					}
				}

			}
			methodSignatureForMessageSend = methodSignatureList.get(0);
			// methodSignatureForMessageSend.calcInterfaceTypes(env);

			MethodDec aMethod = methodSignatureForMessageSend.getMethod();
			if ( aMethod != null ) {
				if ( aMethod.getShared() ) {
					/*
					 * check if the receiver is a prototype
					 */
					if ( !(this.receiverExprOrFirstUnary instanceof ExprIdentStar)
							|| ((ExprIdentStar) this.receiverExprOrFirstUnary)
									.getIdentStarKind() != IdentStarKind.prototype_t ) {
						env.error(this.getFirstSymbol(), "Method '"
								+ methodSignatureForMessageSend.getFullName(env)
								+ "' is shared. It can only be called "
								+ "if the receiver is a prototype. The receiver is '"
								+ this.receiverExprOrFirstUnary.asString()
								+ "'");
					}
					/**
					 * check if the receiver has the method. The method should
					 * be declared in the receiver itself, there is no
					 * inheritance of shared methods
					 */
					ExprIdentStar eis = (ExprIdentStar) receiverExprOrFirstUnary;
					if ( !(eis.getType() instanceof Prototype) ) {
						env.error(this.getFirstSymbol(), "Method '"
								+ aMethod.getMethodSignature().getFullName(env)
								+ "' is shared. It can only be called "
								+ "if the receiver is a prototype. The receiver is '"
								+ this.receiverExprOrFirstUnary.asString()
								+ "'");
					}
					else {
						Prototype pt = (Prototype) eis.getType();
						if ( aMethod.getDeclaringObject() != pt ) {
							env.error(this.getFirstSymbol(), "Method '"
									+ aMethod.getMethodSignature()
											.getFullName(env)
									+ "' is shared. It can only be called "
									+ "if the receiver is the prototype in which the method is declared (it cannot be a superprototype)");

						}
					}

				}
				if ( aMethod.getVisibility() == Token.PRIVATE ) {
					if ( aMethod.getDeclaringObject() != env
							.getCurrentOuterObjectDec() ) {
						env.error(this.getFirstSymbol(), "Method '"
								+ methodSignatureForMessageSend.getFullName(env)
								+ "' is private. It can only be called "
								+ "inside prototype '"
								+ aMethod.getDeclaringObject().getFullName()
								+ "'");
					}

				}
				else if ( aMethod.getVisibility() == Token.PACKAGE ) {
					if ( aMethod.getDeclaringObject().getCompilationUnit()
							.getCyanPackage() != env.getCurrentObjectDec()
									.getCompilationUnit().getCyanPackage() ) {
						env.error(this.getFirstSymbol(), "Method '"
								+ methodSignatureForMessageSend.getFullName(env)
								+ "'  has 'package' visibility. It can only be called "
								+ "inside package '"
								+ aMethod.getDeclaringObject()
										.getCompilationUnit().getCyanPackage()
										.getName()
								+ "'");
					}
				}
				else if ( aMethod.getVisibility() == Token.PROTECTED ) {
					if ( !aMethod.getDeclaringObject()
							.isSupertypeOf(env.getCurrentObjectDec(), env) ) {
						env.error(this.getFirstSymbol(), "Method '"
								+ methodSignatureForMessageSend.getFullName(env)
								+ "'  has 'package' visibility. It can only be called "
								+ "in subprototypes of '"
								+ aMethod.getDeclaringObject().getName() + "'",
								true, true);
					}
				}

				if ( !isNew && receiverIsPrototype ) {
					ObjectDec protoOfMethod = aMethod.getDeclaringObject();
					// protoOfMethod == Type.Any && aMethod.getIsFinal
					if ( receiverType instanceof Prototype ) {
						Prototype rpu = (Prototype) receiverType;
						List<MethodSignature> initMSList = rpu
								.searchMethodPrivateProtectedPublicPackage(
										"init", env);
						if ( (initMSList == null || initMSList.size() == 0)
								&& rpu != Type.Nil ) {

							/*
							 * It is illegal to use a prototype that does not
							 * have an 'init' method
							 */
							boolean canBeCalledOnPrototypes = false;
							if ( protoOfMethod == Type.Any ) {
								List<Tuple2<String, WrExprAnyLiteral>> featureList = aMethod
										.getFeatureList();
								if ( featureList != null ) {
									for (Tuple2<String, WrExprAnyLiteral> feature : featureList) {
										if ( feature.f1.equals("annot") ) {
											Object obj = feature.f2
													.getJavaValue();
											if ( obj instanceof String ) {
												String strParam = meta.MetaHelper
														.removeQuotes(
																(String) obj);
												if ( strParam.equals(
														"canBeCalledOnPrototypes") ) {
													canBeCalledOnPrototypes = true;
												}
											}
										}
									}
								}
							}
							if ( !canBeCalledOnPrototypes ) {
								if ( !(receiverType instanceof InterfaceDec)
										&& !protoOfMethod
												.allFieldsInitializedInDeclaration() ) {
									env.error(getFirstSymbol(), "Prototype '"
											+ this.receiverExprOrFirstUnary
													.asString()
											+ "' does "
											+ "not have an 'init' method. Therefore its fields may not have"
											+ " been initialized (they are if an 'init' method does exist). Then"
											+ " it is illegal to send a message to it");
								}
							}

						}
					}
				}

			}

			/**
			 * if the method is 'new', then the checking of metaobjects attached
			 * to 'init' should be used.
			 *
			 * methodSignatureList = ((ObjectDec)
			 * exprType).searchInitNewMethod(methodName);
			 */
			MethodDec currentMethod1 = env.getCurrentMethod();
			if ( currentMethod1 != null ) {
				String currentMethodName = currentMethod1
						.getNameWithoutParamNumber();
				if ( (currentMethodName.equals("init")
						|| currentMethodName.equals("init:"))
						&& receiverKind == ExprReceiverKind.SELF_R ) {
					if ( !MethodDec.hasAttachedAnnotationNamed(
							methodSignatureList, "accessOnlySharedFields") ) {
						if ( env.getCurrentPrototype().getIsFinal() ) {
							/**
							 * in a final prototype and inside an init or init:
							 * method, it is illegal to access 'self' if some
							 * field has not been initialized
							 */
							for (FieldDec iv : env.getCurrentObjectDec()
									.getFieldList()) {
								if ( !iv.isShared()
										&& !iv.getWasInitialized() ) {
									env.error(this.getFirstSymbol(),
											"'self' cannot be used here because field '"
													+ iv.getName()
													+ "' has not been initialized");
								}
							}

						}
						else {
							env.error(this.getFirstSymbol(),
									"Message send to 'self' inside an 'init' or 'init:' method. "
											+ "This is illegal because it can access a non-initialized field or call a "
											+ " subprototype method and this method can access a non-initialized field",
									true, true);
						}
					}
				}
			}

			type = methodSignatureForMessageSend.getReturnType(env);
			List<MethodSignature> initMethodSignatureList;
			if ( isNew ) {
				initMethodSignatureList = ((ObjectDec) exprType)
						.searchInitNewMethod("init");
				if ( env.getProject().getCompilerManager()
						.getCompilationStep() == CompilationStep.step_9 ) {
					MetaInfoServer.checkMessageSendWithMethodMetaobject(
							initMethodSignatureList, exprType,
							receiverExprOrFirstUnary, receiverKind, env,
							this.unarySymbol);
				}

				if ( env.getProject().getCompilerManager().getCompilationStep()
						.ordinal() < CompilationStep.step_7.ordinal() ) {
					type = MetaInfoServer.replaceMessageSendIfAsked(
							initMethodSignatureList.get(0), this, env,
							this.getFirstSymbol(), type);

				}

			}
			else {
				if ( receiverExprOrFirstUnary instanceof ExprIdentStar ) {
					ExprIdentStar ies = (ExprIdentStar) receiverExprOrFirstUnary;
					if ( ies.getIdentStarKind() == IdentStarKind.prototype_t ) {
						receiverKind = ExprReceiverKind.PROTOTYPE_R;
					}
				}

				if ( env.getProject().getCompilerManager()
						.getCompilationStep() == CompilationStep.step_9 ) {
					MetaInfoServer.checkMessageSendWithMethodMetaobject(
							allMethodSignatureList, exprType,
							receiverExprOrFirstUnary, receiverKind, env,
							unarySymbol);
				}

				if ( env.getProject().getCompilerManager().getCompilationStep()
						.ordinal() < CompilationStep.step_7.ordinal() ) {
					if ( exprType instanceof Prototype ) {
						// this test should be redundant

						type = MetaInfoServer.replaceMessageSendIfAsked(
								allMethodSignatureList, this, env,
								this.getFirstSymbol(), type);
					}
				}

			}

		}
		super.calcInternalTypes(env);

	}

	/**
	 * @param env
	 * @param methodName
	 * @param javaClass
	 */
	private void checkPublicStatic(Env env, String methodName,
			TypeJavaRef javaClass) {
		int modif = javaMethod.getModifiers();
		if ( Modifier.isStatic(modif) ) {
			// method is static
			if ( (this.receiverExprOrFirstUnary instanceof ExprIdentStar
					&& ((ExprIdentStar) this.receiverExprOrFirstUnary)
							.getIdentStarKind() != IdentStarKind.jvmClass_t)
					&& !(this.receiverExprOrFirstUnary instanceof ast.ExprGenericPrototypeInstantiation) ) {
				env.error(getFirstSymbol(),
						"Static fields can only be used with a class as in '"
								+ receiverExprOrFirstUnary.getType()
										.getFullName()
								+ "." + methodName + "'");
			}
		}
		else if ( !Modifier.isPublic(modif) ) {
			env.error(getFirstSymbol(),
					"The field '" + methodName + "' of the Java class '"
							+ javaClass.getFullName() + "' is not public");
		}
	}

	public boolean lookForMethodAtCompileTime(Env env, Type receiverType) {
		if ( !(receiverType instanceof ObjectDec) ) {
			return false;
		}
		boolean ret = false;

		ObjectDec proto = (ObjectDec) receiverType;

		List<AnnotationAt> metaobjectAnnotationList = proto
				.getAnnotationThisAndSuperCTDNUList();

		if ( metaobjectAnnotationList.size() == 0 ) {
			return false;
		}
		WrPrototype lastProtoWithAnnotReplacedCode = null;

		AnnotationAt lastAnnotWhichReplacedCode = null;

		int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(env,
				env.getProject().getProgram().getI(),
				env.getCurrentCompilationUnit().getCyanPackage().getI(),
				this.getFirstSymbol());

		for (AnnotationAt annot : metaobjectAnnotationList) {

			ExprMessageSendWithKeywordsToExpr
					.checkAnnotIActionMethodMissing_semAn(env, annot);

			CyanMetaobjectAtAnnot cyanMetaobject = annot.getCyanMetaobject();
			_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot) cyanMetaobject
					.getMetaobjectInCyan();

			boolean actionMissing = cyanMetaobject instanceof IActionMethodMissing_semAn
					|| (other != null
							&& other instanceof _IActionMethodMissing__semAn);

			if ( !actionMissing ) {
				env.error(this.getFirstSymbol(), "Internal error: metaobject '"
						+ annot.getCyanMetaobject().getName()
						+ "' should implement "
						+ meta.IActionMethodMissing_semAn.class.getName());
				return false;
			}

			Tuple3<StringBuffer, String, String> codeType = null;
			StringBuffer sb = null;
			try {
				if ( other == null ) {
					IActionMethodMissing_semAn doesNot = (IActionMethodMissing_semAn) cyanMetaobject;
					Timeout<Tuple3<StringBuffer, String, String>> to = new Timeout<>();

					codeType = to.run(() -> {
						return doesNot.semAn_missingUnaryMethod(
								this.receiverExprOrFirstUnary.getI(),
								this.unarySymbol.getI(), env.getI());

					}, timeoutMilliseconds, "semAn_missingUnaryMethod",
							cyanMetaobject, env);

					// codeType = doesNot.semAn_missingUnaryMethod(
					// this.receiverExprOrFirstUnary.getI(),
					// this.unarySymbol.getI(), env.getI());
				}
				else {
					final _IActionMethodMissing__semAn doesNot = (_IActionMethodMissing__semAn) other;
					Timeout<_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT> toCyan = new Timeout<>();
					_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd = toCyan
							.run(() -> {
								return doesNot._semAn__missingUnaryMethod_3(
										this.receiverExprOrFirstUnary.getI(),
										this.unarySymbol.getI(), env.getI());
							}, timeoutMilliseconds, "semAn_missingUnaryMethod",
									cyanMetaobject, env);

					// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd =
					// doesNot._semAn__missingUnaryMethod_3(
					// this.receiverExprOrFirstUnary.getI(),
					// this.unarySymbol.getI(), env.getI());

					Object f1 = tdd._f1();
					Object f2 = tdd._f2();
					Object f3 = tdd._f3();

					if ( !(f1 instanceof CyString) || !(f2 instanceof CyString)
							|| !(f3 instanceof CyString) ) {
						env.error(annot.getFirstSymbol(),
								"This metaobject is returning a "
										+ "tuple with object of wrong type. It should be a Tuple<String, String, String>");
					}
					String f1s = ((CyString) f1).s;
					if ( f1s.length() != 0 ) {
						codeType = new Tuple3<StringBuffer, String, String>(
								new StringBuffer(f1s), ((CyString) f2).s,
								((CyString) f3).s);

					}
				}
			}
			catch (error.CompileErrorException e) {
			}
			catch (NoClassDefFoundError e) {
				env.error(annot.getFirstSymbol(), e.getMessage() + " "
						+ NameServer.messageClassNotFoundException);
			}
			catch (RuntimeException e) {
				env.thrownException(annot, this.getFirstSymbol(), e);
			}
			finally {
				env.errorInMetaobject(cyanMetaobject, this.unarySymbol);
			}

			if ( codeType != null ) {

				WrPrototype protoOfAnnotWantsToReplaceMessagePassing = ExprMessageSendWithKeywordsToExpr
						.currentPrototypeFromAnnot(annot);

				if ( protoOfAnnotWantsToReplaceMessagePassing == lastProtoWithAnnotReplacedCode ) {
					// two annotations are trying to replace a message passing
					// by code and
					// the two ones are in the same prototype. This is illegal

					env.error(this.getFirstSymbol(),
							"Two annotations of the same prototype are "
									+ "trying to replace a message passing. They are:\n"
									+ "    a) annotation '"
									+ lastAnnotWhichReplacedCode
											.getCyanMetaobject().getName()
									+ "' of line "
									+ lastAnnotWhichReplacedCode
											.getFirstSymbol().getLineNumber()
									+ " of prototype '"
									+ lastProtoWithAnnotReplacedCode
											.getFullName()
									+ "'\n" + "    b) annotation '"
									+ annot.getCyanMetaobject().getName()
									+ "' of line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype '"
									+ protoOfAnnotWantsToReplaceMessagePassing
											.getFullName()
									+ "'");
					return false;

				}

				if ( lastProtoWithAnnotReplacedCode == null ) {
					lastAnnotWhichReplacedCode = annot;
					lastProtoWithAnnotReplacedCode = protoOfAnnotWantsToReplaceMessagePassing;
					sb = codeType.f1;

					if ( this.codeThatReplacesThisStatement != null ) {
						/*
						 * this message send has already been replaced by
						 * another expression
						 */
						if ( cyanAnnotationThatReplacedStatByAnotherOne != null ) {
							env.warning(this.getFirstSymbol(),
									"Metaobject annotation '"
											+ cyanMetaobject.getName()
											+ "' at line "
											+ annot.getFirstSymbol()
													.getLineNumber()
											+ " of prototype "
											+ annot.getPackageOfAnnotation()
											+ "."
											+ annot.getPackageOfAnnotation()
											+ " is trying to replace message send '"
											+ this.asString()
											+ "' by an expression. But this has already been asked by metaobject annotation '"
											+ cyanAnnotationThatReplacedStatByAnotherOne
													.getCyanMetaobject()
													.getName()
											+ "'" + " at line "
											+ cyanAnnotationThatReplacedStatByAnotherOne
													.getFirstSymbol()
													.getLineNumber()
											+ " of prototype "
											+ cyanAnnotationThatReplacedStatByAnotherOne
													.getPackageOfAnnotation()
											+ "."
											+ cyanAnnotationThatReplacedStatByAnotherOne
													.getPackageOfAnnotation());
						}
						else {
							env.warning(this.getFirstSymbol(),
									"Metaobject annotation '"
											+ cyanMetaobject.getName()
											+ "' at line "
											+ annot.getFirstSymbol()
													.getLineNumber()
											+ " of prototype "
											+ annot.getPackageOfAnnotation()
											+ "."
											+ annot.getPackageOfAnnotation()
											+ " is trying to replace message send '"
											+ this.asString()
											+ "' by an expression. But this has already been asked by someone else");
						}
					}

					// if there is any errors, signals them
					env.errorInMetaobject(cyanMetaobject,
							this.getFirstSymbol());

					Type typeOfCode;
					if ( codeType.f2 == null || codeType.f3 == null
							|| codeType.f2.length() == 0
							|| codeType.f3.length() == 0 ) {
						typeOfCode = Type.Dyn;
					}
					else {
						typeOfCode = env.searchPackagePrototype(codeType.f2,
								codeType.f3);
						if ( typeOfCode == null ) {
							env.error(true, this.getFirstSymbol(),
									"This message send was replaced by an expression that has type '"
											+ codeType.f2 + "." + codeType.f3
											+ "' which was not found",
									cyanMetaobject.getPrototypeOfType(),
									ErrorKind.prototype_was_not_found_inside_method);
						}
					}
					// Type typeOfCode =
					// GetHiddenItem.getHiddenType(codeType.f2);

					env.replaceStatementByCode(this, annot, sb, typeOfCode);

					cyanAnnotationThatReplacedStatByAnotherOne = annot;

					if ( typeOfCode == null )
						env.error(true, this.getFirstSymbol(),
								"This message send was replaced by an expression that has type '"
										+ cyanMetaobject.getPackageOfType()
										+ "."
										+ cyanMetaobject.getPrototypeOfType()
										+ "' which was not found",
								cyanMetaobject.getPrototypeOfType(),
								ErrorKind.prototype_was_not_found_inside_method);
					else
						type = typeOfCode;

					ret = true;
				}

			}

		}

		return ret;
	}

	private Type getJavaTypeForMessageSend(Class<?> methodReturnType, Env env) {
		String javaReturnTypeName = methodReturnType.getSimpleName();
		if ( NameServer.isJavaBasicType(javaReturnTypeName) ) {
			return env.getProject().getProgram()
					.searchJavaBasicType(javaReturnTypeName);
		}
		else {
			String rtPackageName = null;
			String rtClassName = null;
			// System.out.println(methodReturnType.getName());
			String retTypeCanonicalName = methodReturnType.getCanonicalName();
			boolean isArray = false;
			if ( retTypeCanonicalName.endsWith("[]") ) {
				int lastIndexOfDot = retTypeCanonicalName.lastIndexOf('.');
				if ( lastIndexOfDot > 0 ) {
					rtPackageName = retTypeCanonicalName.substring(0,
							lastIndexOfDot);
				}
				else {
					// should be int, char, long, etc.
					rtPackageName = "java.lang";
					/*
					 * env.error(this.getFirstSymbol(),
					 * "The Java class of the return type of this method call is '"
					 * + methodReturnType.getName() + "'. Its package is '" +
					 * rtPackageName + "' which was not found");
					 */
				}
				rtClassName = retTypeCanonicalName.substring(lastIndexOfDot + 1,
						retTypeCanonicalName.length() - 2);
				isArray = true;
			}
			else {
				rtPackageName = methodReturnType.getPackage().getName();
				rtClassName = methodReturnType.getSimpleName();
			}

			JVMPackage jvmPackage = env.getProject().getProgram()
					.searchJVMPackage(rtPackageName);
			if ( jvmPackage == null ) {
				env.error(this.getFirstSymbol(),
						"The Java class of the return type of this method call is '"
								+ methodReturnType.getName()
								+ "'. Its package is '" + rtPackageName
								+ "' which was not found");
				return null;
			}
			else {
				Type t = jvmPackage.getJvmTypeClassMap().get(rtClassName);
				if ( t == null ) {
					env.error(this.getFirstSymbol(),
							"The Java class of the return type of this method call is '"
									+ methodReturnType.getName()
									+ "'. It was not found in its package. Maybe the package was not imported");
					return null;
				}
				else {
					if ( isArray ) {
						return new TypeJavaRefArray((TypeJavaRef) t, 1);
					}
					return t;
				}
			}
		}
	}

	@Override
	public Object eval(EvalEnv ee) {
		Object receiverValue = receiverExprOrFirstUnary.eval(ee);
		String messageName = this.getMessageName();
		boolean isNew = messageName.equals("new");
		/*
		 * cases: cyanValue unary; CyanProto unary; cyan.lang.CyanProto unary;
		 *
		 * javaValue unary; StringBuffer unary; java.lang.StringBuffer unary;
		 * expr unary; // expr is none of the above
		 */
		Class<?> receiverClass;

		if ( receiverValue == null ) {
			/*
			 * should only occur if the receiver is a Java class
			 */
			if ( isNew ) {
				String rname = receiverExprOrFirstUnary.asString();
				receiverClass = ee.searchPrototypeAsType(rname);
				if ( receiverClass == null ) {
					receiverClass = StatementLocalVariableDec
							.searchCyanGenericProtoWithDyn(ee, rname);
					if ( receiverClass == null ) {
						// receiverClass = ee.searchPrototypeAsType(rname);
						// receiverClass =
						// StatementLocalVariableDec.searchCyanGenericProtoWithDyn(ee,
						// rname);

						ee.error(this.getFirstSymbol(),
								"Type '" + rname + "' was not found");
						return null;
					}
				}
				try {
					Constructor<?> cons = receiverClass.getConstructor();
					Object ret = cons.newInstance();
					ee.addCreatedJavaObject(ret);

					return ret;
				}
				catch (NoSuchMethodException | SecurityException
						| InstantiationException | IllegalAccessException
						| IllegalArgumentException
						| InvocationTargetException e) {
					ee.error(this.getFirstSymbol(),
							"Error when trying to create object from '" + rname
									+ "'");
				}
			}
			else {
				ee.error(this.getFirstSymbol(), "Error in evaluating  '"
						+ receiverExprOrFirstUnary.asString() + "'");
				return null;
			}
		}
		else {
			if ( receiverValue instanceof Class<?> ) {
				// call to a static method of a Java class
				receiverClass = (Class<?>) receiverValue;
			}
			else {
				receiverClass = receiverValue.getClass();
			}
		}

		String cyanMethodNameInJava = MetaHelper.getJavaName(messageName);
		String methodNameInJava = cyanMethodNameInJava;
		if ( cyanMethodNameInJava.charAt(0) == '_' ) {
			if ( cyanMethodNameInJava.endsWith(":") ) {
				methodNameInJava = cyanMethodNameInJava.substring(1,
						cyanMethodNameInJava.length() - 1);
			}
			else {
				methodNameInJava = cyanMethodNameInJava.substring(1);
			}
		}
		Object ret = null;
		ret = Statement.sendMessage(receiverValue, cyanMethodNameInJava,
				methodNameInJava, null, this.unarySymbol.getSymbolString(),
				this.getFirstSymbol().getI(), ee);

		/*
		 * java.lang.reflect.Method unaryMethod =
		 * EvalEnv.getJavaMethodByName(receiverClass, cyanMethodNameInJava, 0);
		 * if ( unaryMethod != null ) { try { unaryMethod.setAccessible(true);
		 * ret = unaryMethod.invoke(receiverValue); } catch (
		 * java.lang.reflect.InvocationTargetException tmp2359 ) {
		 * ee.error(this.getFirstSymbol(),
		 * "InvocationTargetException exception " +
		 * "thrown by unary message passing '" + this.asString() + "'"); } catch
		 * (IllegalAccessException | IllegalArgumentException tmp2359) {
		 * ee.error(this.getFirstSymbol(),
		 * "IllegalAccessException or IllegalArgumentException exception " +
		 * "thrown by unary message passing '" + this.asString() + "'"); } }
		 */
		if ( ret != null ) {
			return ret;
		}

		return null;
	}

	/**
	 * unary message send chain sent to receiverExprOrFirstUnary. However,
	 * receiverExprOrFirstUnary may be a unary method of the current prototype
	 * as in var memberName = memberList first name; In this case, memberList is
	 * packed as "receiverExprOrFirstUnary" and "first" and "name" as unary
	 * messages. But memberList is also a unary message to self.
	 */
	private Expr			receiverExprOrFirstUnary;

	/**
	 * if the receiver is a Java object, this is the method to be called. Or
	 * constructor
	 */
	private Method			javaMethod;
	/**
	 * Java construtor to be called
	 */
	private Constructor<?>	constructor;
	private boolean			isJavaField	= false;

}
