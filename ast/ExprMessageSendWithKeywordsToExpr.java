package ast;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import cyan.lang.CyString;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobjectAtAnnot;
import cyan.reflect._IActionMethodMissing__semAn;
import error.ErrorKind;
import lexer.Symbol;
import meta.AttachedDeclarationKind;
import meta.CompilationInstruction;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.IActionAssignment_cge;
import meta.IActionMethodMissing_semAn;
import meta.IDeclaration;
import meta.IdentStarKind;
import meta.MetaHelper;
import meta.Timeout;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.WrAnnotation;
import meta.WrExprAnyLiteral;
import meta.WrExprMessageSendWithKeywordsToExpr;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrPrototype;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
import saci.TupleTwo;

/** Represents a message sent to an expression. Example:
 *     nil println;
 *     (get + 1) println;
 *
 *  The receiver may be an implicit self. In this case, the first parameter to the
 *  constructor should be null.
 * @author José
 *
 */
public class ExprMessageSendWithKeywordsToExpr extends ExprMessageSendWithKeywords {


	/**
	 * @param the receiver and message. If the receiver is an implicit self, it should be null
	 */
	public ExprMessageSendWithKeywordsToExpr(Expr receiverExpr, MessageWithKeywords message,
			Symbol nextSymbol, MethodDec currentMethod) {
		super(message, nextSymbol, currentMethod);
		this.receiverExpr = receiverExpr;
		methodSignatureForMessage = null;
	}


	@Override
	public WrExprMessageSendWithKeywordsToExpr getI() {
		if ( iExprMessageSendWithKeywordsToExpr == null ) {
			iExprMessageSendWithKeywordsToExpr = new WrExprMessageSendWithKeywordsToExpr(this);
		}
		return iExprMessageSendWithKeywordsToExpr;
	}

	private WrExprMessageSendWithKeywordsToExpr iExprMessageSendWithKeywordsToExpr = null;


	@Override
	public void accept(ASTVisitor visitor) {
		if ( receiverExpr != null ) {
			this.receiverExpr.accept(visitor);
		}
		message.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public boolean isNRE(Env env) {
		if ( ! message.getMethodName().equals("new:") ) {
			return false;
		}
		for ( final MessageKeywordWithRealParameters s : message.getkeywordParameterList() ) {
			for ( final Expr e : s.getExprList() ) {
				if ( !e.isNRE(env) )
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean isNREForInitShared(Env env) {
		final String name = receiverExpr.asString();
		final Prototype pu = env.getProject().getCyanLangPackage().searchPublicNonGenericPrototype(name);
		if ( pu == null || ! message.getMethodName().equals("new:") ) {
			return false;
		}
		for ( final MessageKeywordWithRealParameters s : message.getkeywordParameterList() ) {
			for ( final Expr e : s.getExprList() ) {
				if ( !e.isNREForInitShared(env) )
					return false;
			}
		}
		return true;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		/*
		if ( this.codeThatReplacesThisExpr != null ) {
			pw.print(this.codeThatReplacesThisExpr);
			return ;
		}
		*/

		if ( printInMoreThanOneLine ) {
			if ( receiverExpr != null ) {
			    receiverExpr.genCyan(pw, PWCounter.printInMoreThanOneLine(receiverExpr), cyanEnv, genFunctions );
			    pw.print(" ");
			}
			else if ( cyanEnv.getCreatingInnerPrototypesInsideEval() )
				pw.print(NameServer.selfNameInnerPrototypes + " ");
			else if ( cyanEnv.getCreatingContextObject() )
				pw.print(NameServer.selfNameContextObject + " ");
			message.genCyan(pw, true, cyanEnv, genFunctions);
		}
		else {
			  // print in just one line
			if ( receiverExpr != null ) {
    			receiverExpr.genCyan(pw, false, cyanEnv, genFunctions);
			    pw.print(" ");
			}
			else if ( cyanEnv.getCreatingInnerPrototypesInsideEval() )
				pw.print(NameServer.selfNameInnerPrototypes + " ");
			else if ( cyanEnv.getCreatingContextObject() )
				pw.print(NameServer.selfNameContextObject + " ");
			message.genCyan(pw, false, cyanEnv, genFunctions);
		}
	}



	/**
	 * MessageWithKeywords send<br>
	 * <code>
	 *      (receiverExpr) m: p1  k: p2
	 * </code><br>
	 * should generate<br>
	 * <code>
	 *    tmp1 = receiverExpr; <br>
	 *    tmp2 = p1;  <br>
	 *    tmp3 = p2;  <br>
	 *    tmp4 = tmp1.m_s_k(tmp2, tmp3); <br>
	 *</code>
	 * and return tmp4.
	 * <br>
	 */
	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String messageSendTmpVar;

//		if ( env.getCurrentMethod() != null && env.getCurrentMethod().getName().contains("eval") &&
//				env.getCurrentMethod().getMethodSignature().getParameterList().size() == 1 &&
//				message.getMethodName().contains("evalCyanCode") ) {
//			System.out.println("eval: evalCyanCode");
//		}
		final String methodName = message.getMethodName();
		final boolean isNew = methodName.equals("new:");
		final boolean isThrow = message.getMethodNameWithParamNumber().equals("throw:1");

		if ( this.javaReceiver ) {
			if ( this.messageIsEqEq_NotEq ) {
				return genJavaExprForEqEq_NotEq(pw, env, methodName);
			}
			final TypeJavaRef javaClass = (TypeJavaRef ) receiverExpr.getType();
			messageSendTmpVar = null;
			final String []varTmpArray = new String[message.getkeywordParameterList().get(0).getExprList().size()];
			int i = 0;
			for ( final Expr paramExpr : message.getkeywordParameterList().get(0).getExprList() ) {
				varTmpArray[i] = paramExpr.genJavaExpr(pw, env);
				final Type exprType = paramExpr.getType();


				final String cyanTypeName = exprType.getName();
				String javaTypeName = null;
				if ( javaMethod != null ) {
					javaTypeName = javaMethod.getParameters()[i].getType().getSimpleName();
				}
				else {
					javaTypeName = constructor.getParameters()[i].getType().getSimpleName();
				}
				String cyanNameFromWrapper = null;
				Type typeParamExpr = paramExpr.getType();
				boolean equalJavaTypes = false;
				if ( typeParamExpr instanceof TypeJavaClass &&
					 typeParamExpr instanceof TypeJavaClass &&
						(javaMethod != null && javaMethod.getParameters()[i].getType() == ((TypeJavaClass ) typeParamExpr).getTheClass()
						|| constructor != null && constructor.getParameters()[i].getType() == ((TypeJavaClass ) typeParamExpr).getTheClass())
						) {
					equalJavaTypes = true;
				}
				else if ( typeParamExpr instanceof TypeJavaRefArray ) {
					TypeJavaRefArray ta = (TypeJavaRefArray ) typeParamExpr;
					Class<?> classTypeArray = ta.getTheClass();
					if ( classTypeArray == null ) {
						try {
							if ( ta.loadJavaClass() != null ) {
								env.error(this.getFirstSymbol(), "Unable to load the Java class " + typeParamExpr.getFullName());
								return null;
							}
						}
						catch (ClassNotFoundException | IOException e) {
							env.error(this.getFirstSymbol(), "Unable to load the Java class " + typeParamExpr.getFullName());
							return null;
						}
					}
					classTypeArray = ta.getTheClass();
					if (
							javaMethod != null && javaMethod.getParameters()[i].getType() == classTypeArray
							|| constructor != null && constructor.getParameters()[i].getType() == classTypeArray
							) {
						equalJavaTypes = true;
					}
				}
	   			if (  !equalJavaTypes &&
	   					(javaTypeName.equals(Character.toLowerCase(cyanTypeName.charAt(0)) + cyanTypeName.substring(1)) ||
						javaTypeName.equals("String") && javaTypeName.equals("String") ||
						//NameServer.javaWrapperClassToCyanName(javaTypeName).equals(cyanTypeName)
						(cyanNameFromWrapper = NameServer.javaWrapperClassToCyanName(javaTypeName)) != null &&
						cyanNameFromWrapper.equals(cyanTypeName)

						)
	   					) {

					  // something like  "int = Int" or "Integer = Int"

	   				varTmpArray[i] = varTmpArray[i] + "." + NameServer.getFieldBasicType(cyanTypeName);
				}
				else {
					if ( javaMethod != null ) {
						javaTypeName = javaMethod.getParameters()[i].getType().getCanonicalName();
					}
					else {
						javaTypeName = constructor.getParameters()[i].getType().getCanonicalName();
					}


					if ( !javaTypeName.equals("java.lang.Object") ) {
						if ( ! (exprType instanceof TypeJavaRef) ) {
							env.error(paramExpr.getFirstSymbol(),
									"Cannot cast this Cyan value of type '" + exprType.getFullName() + "' to "
									+ "Java, type '" + javaTypeName + "'");
						}
						else {
							Class<?> paramType;
							if ( constructor != null ) {
								paramType = constructor.getParameters()[i].getType();
							}
							else {
								paramType = javaMethod.getParameters()[i].getType();
							}
						    final Class<?> exprJavaClass = ((TypeJavaRef ) exprType).getClassLoad(env, this.getFirstSymbol());

							//if ( ! paramType.isAssignableFrom(exprJavaClass) ) {
						    if ( !ClassUtils.isAssignable(exprJavaClass, paramType) ) {
								env.error(paramExpr.getFirstSymbol(),
										"Cannot cast this Cyan value of type '" + exprType.getFullName() + "' to "
										+ "Java, type '" + javaTypeName + "'");
							}
						}
					}
				}


				//varTmpArray[i] = Type.genJavaExpr_CastJavaCyan(env, varTmpArray[i], exprType, javaMethod.getParameters()[i], paramExpr.getType());
				/*
				if ( exprType.ge instanceof Prototype ) {
					String puName = ((Prototype) exprType).getName();
					if ( NameServer.isBasicType(puName) ) {
						/*
						 * cast Cyan basic type value to Java basic type value
						 * /
						varTmpArray[i] = varTmpArray[i] + "." + NameServer.getFieldBasicType(puName);
					}
				}
				*/




				++i;
			}
			Class<?>[] exceptionList = null;
			if ( javaMethod != null ) exceptionList = javaMethod.getExceptionTypes();
			else exceptionList = constructor.getExceptionTypes();

			final String resultTmpVar = NameServer.nextJavaLocalVariableName();
			if ( isNew ) {
				if ( exceptionList.length == 0 ) {
					pw.printIdent( type.getJavaName() + " " + resultTmpVar + " = ");
					pw.println( "new " + ((TypeJavaClass ) receiverExpr.getType()).getName() + "(" );
				}
				else {
					pw.printlnIdent( type.getJavaName() + " " + resultTmpVar + " = null;");
					pw.printlnIdent("try {");
					pw.add();
					pw.printlnIdent( resultTmpVar + " = new " + ((TypeJavaClass ) receiverExpr.getType()).getName() + "(" );
				}
			}
			else {
				final String javaTypeName = type.getJavaName();
				final boolean isVoid = javaTypeName.equals("void");
				if ( exceptionList.length == 0 ) {
					messageSendTmpVar = receiverExpr.genJavaExpr(pw, env);
					if ( isVoid ) {
						pw.printIdent("");
					}
					else {
						final String typeJavaName = type.getJavaName();
						pw.printIdent( typeJavaName + " " + resultTmpVar + " = (" + typeJavaName + " ) ");
					}

					pw.print(messageSendTmpVar + "." + message.getJavaMethodNameReceiverJavaObject()
					   + "(" );
				}
				else {
					messageSendTmpVar = receiverExpr.genJavaExpr(pw, env);
					if ( isVoid ) {
						pw.printlnIdent("");
					}
					else {
						pw.printlnIdent( type.getJavaName() + " " + resultTmpVar + " = null;");
					}
					pw.printlnIdent("try {");
					pw.add();

					if ( isVoid ) {
						pw.printIdent(messageSendTmpVar + "." + message.getJavaMethodNameReceiverJavaObject()
						   + "(" );
					}
					else {
						final String typeJavaName = type.getJavaName();

						pw.printIdent(resultTmpVar + " = (" + typeJavaName + " ) " +
						 messageSendTmpVar + "." + message.getJavaMethodNameReceiverJavaObject()
						   + "(" );
					}


				}
			}
			int size = varTmpArray.length;
			for ( final String p : varTmpArray ) {
				pw.print(p);
				if ( --size > 0 ) {
					pw.print(", ");
				}
			}
			pw.println( "); " );
			if ( exceptionList.length != 0 ) {
				pw.sub();
				pw.printlnIdent("} catch (Throwable e) {");
				pw.printlnIdent("    throw new ExceptionContainer__(new _ExceptionJavaException(e));");
				pw.printlnIdent("}");
			}
			return resultTmpVar;
		}

		if ( receiverExpr == null ) {
			/*
			 * inside inner prototypes (corresponding to functions and methods),
			 * calls to 'self' are changed to call to 'self__"
			 */
			if ( env.getCurrentObjectDec().getOuterObject() != null ) {
				messageSendTmpVar = NameServer.javaSelfNameInnerPrototypes;
			}
			else
				messageSendTmpVar = "this";
		}
		else {


			if ( isNew ) {
				messageSendTmpVar = "";
			}
			else {
				messageSendTmpVar = receiverExpr.genJavaExpr(pw, env);
			}
		}

		final Token tokenFirstkeyword = message.getTokenFirstkeyword();
		final boolean precededbyInter = tokenFirstkeyword == Token.INTER_DOT_ID_COLON || tokenFirstkeyword == Token.INTER_ID_COLON;

		Type receiverExprType = receiverExpr != null ? this.receiverExpr.getType() : null;
		// only messages == and != are allowed to union. Thus, any message to an union
		// can only be == and !=
		boolean messageSendEqualEqualToUnion = receiverExprType instanceof TypeUnion;

		final boolean isMessageToDynExpr = receiverExpr != null &&
				(receiverExprType == Type.Dyn || messageSendEqualEqualToUnion);
		final boolean checkParameters = !isMessageToDynExpr && ! this.getBackquote() && ! precededbyInter;
		List<ParameterDec> paramDecList = null;
		if ( this.methodSignatureForMessage != null ) {
			paramDecList = this.methodSignatureForMessage.getParameterList();
		}

		String resultTmpVar = NameServer.nextJavaLocalVariableName();

		final List<String> stringArray = new ArrayList<String>();
		final StringBuffer paramPassing = new StringBuffer();
		if ( paramDecList == null ) {
			/*
			 * a dynamic message send
			 */
			for ( final MessageKeywordWithRealParameters keyword : message.getkeywordParameterList() ) {
				for ( final Expr e : keyword.getExprList() ) {
					final String tmpVar = e.genJavaExpr(pw, env);
					stringArray.add( tmpVar );
				}
		   }
			int size = stringArray.size();
			for ( final String tmp : stringArray ) {
				paramPassing.append(tmp);
				if ( --size > 0 )
					paramPassing.append(", ");
			}
		}
		else {
			/*
			 * if the type of the real argument is Dyn and the type of the formal parameter is not Dyn,
			 * first generate code that cast the real argument to the correct type
			 */

			int ii = 0;
			final List<Expr> realParamExprList = new ArrayList<>();
			for ( final MessageKeywordWithRealParameters keyword : message.getkeywordParameterList() ) {
				for ( final Expr e : keyword.getExprList() ) {
					realParamExprList.add(e);
					String tmpVar = e.genJavaExpr(pw, env);
					final Type rightType = e.getType();
					if ( checkParameters && paramDecList.get(ii).getType() != Type.Dyn &&
							(rightType == Type.Dyn || rightType instanceof TypeUnion ) ) {
						final String otherTmpVar = NameServer.nextJavaLocalVariableName();
						String strFormalParamType = paramDecList.get(ii).getType().getJavaName();
						pw.printlnIdent(strFormalParamType + " " + otherTmpVar + ";");
						if ( rightType == Type.Dyn ) {
							pw.printlnIdent("if ( " + tmpVar + " instanceof " + strFormalParamType + " ) ");
							pw.printlnIdent("    " + otherTmpVar + " = (" + strFormalParamType + " ) " + tmpVar + ";");
							pw.printlnIdent("else");

							pw.printlnIdent("    throw new ExceptionContainer__("
									+ env.javaCodeForCastException(e, paramDecList.get(ii).getType()) + " );");
						}
						else {
							// union
							pw.printlnIdent(otherTmpVar + " = (" + strFormalParamType + " ) " + tmpVar + ";");
						}
						stringArray.add(otherTmpVar);
					}
					else {
						final Type leftType = paramDecList.get(ii).getType();
						if ( leftType == Type.Any && rightType instanceof InterfaceDec ) {
							tmpVar = " (" + MetaHelper.AnyInJava + " ) " + tmpVar;
						}
						else if ( rightType instanceof TypeJavaRef || leftType instanceof TypeJavaRef ) {
							tmpVar = Type.genJavaExpr_CastJavaCyan(env, tmpVar, rightType, leftType,
									e.getFirstSymbol() );
						}

						stringArray.add( tmpVar );
					}
					++ii;
				}
			}

			/*
			 * A metaobject attached to the type of the formal parameter may demand that the real argument be
			 * changed. The new argument is the return of method  changeRightHandSideTo
			 */
			ii = 0;
			ParameterDec param;
			int size = stringArray.size();
			for ( String tmp : stringArray ) {
				param = paramDecList.get(ii);
				final Type leftType = param.getType(env);
				final Tuple2<IActionAssignment_cge, ObjectDec> cyanMetaobjectPrototype = MetaInfoServer.getChangeAssignmentCyanMetaobject(env, leftType);
				IActionAssignment_cge changeCyanMetaobject = null;
		        ObjectDec prototypeFoundMetaobject = null;
		        if ( cyanMetaobjectPrototype != null ) {
		        	changeCyanMetaobject = cyanMetaobjectPrototype.f1;
		        	prototypeFoundMetaobject = cyanMetaobjectPrototype.f2;


					try {
						tmp = changeCyanMetaobject.cge_changeRightHandSideTo(
			        			prototypeFoundMetaobject,
			        			tmp, realParamExprList.get(ii).getType(env));
					}
					catch ( final error.CompileErrorException e ) {
					}
					catch ( final NoClassDefFoundError e ) {
						final WrAnnotation annotation = ((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation();
						env.error(
								meta.GetHiddenItem.getHiddenSymbol(annotation.getFirstSymbol()),
								e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( final RuntimeException e ) {
						final WrAnnotation annotation = ((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation();
						env.thrownException(
								meta.GetHiddenItem.getHiddenCyanAnnotation(annotation),
								meta.GetHiddenItem.getHiddenSymbol(annotation.getFirstSymbol()), e);
					}
					finally {
						env.errorInMetaobject( (meta.CyanMetaobject ) changeCyanMetaobject, this.getFirstSymbol());
					}
		        }


				paramPassing.append(tmp);
				//pw.print(tmp);
				if ( --size > 0 )
					paramPassing.append(", ");
				++ii;
			}

		}

		int numParamJavaMethod = 0;
		for ( final MessageKeywordWithRealParameters sel : this.message.getkeywordParameterList() ) {
			numParamJavaMethod += sel.getExprList().size();
		}
		final String paramListAsString = (stringArray.size() == 0 ? "" : ", ") +
				paramPassing.toString();


		if ( isNew && checkParameters ) {
			pw.printIdent( type.getJavaName() + " " + resultTmpVar + " = ");
			pw.print( "new " + receiverExpr.getType().getJavaName() + "( " +
					paramPassing.toString()  + ");");
		}
		else {
			final String javaNameMethod = message.getJavaMethodName();
			if (  this.getBackquote() ) {


				final String cyanMethodNameTmpVar = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent("String " + cyanMethodNameTmpVar + " = \"\";");

				String paramArray = "";
				int size2 = quotedVariableList.size();
				for ( final VariableDecInterface varDec :  quotedVariableList ) {
					String s0;
					if ( varDec.getType() == Type.Dyn ) {
						s0 = "((CyString ) " + varDec.javaNameWithRef() + ").s";
						// if ( varDec.getRefType() ) s0 += "._elem";
						// s0 = s0 + ").s";

						paramArray += s0;
						pw.printIdent("if ( ! (" + varDec.javaNameWithRef() + " instanceof CyString) ) ");

						pw.printlnIdent("    throw new ExceptionContainer__("
								+ env.javaCodeForCastException(varDec, Type.String) + " );");
					}
					else {
						s0 = "((CyString ) " + varDec.javaNameWithRef() + ").s";
						paramArray += s0;
					}
					pw.printlnIdent(cyanMethodNameTmpVar + " += " + s0 + ";");
					final String tmpChar = NameServer.nextJavaLocalVariableName();

					pw.printlnIdent("char " + tmpChar + " = " + cyanMethodNameTmpVar + ".charAt(0);");
					pw.printlnIdent("if ( (" + tmpChar + " == '_' || Character.isAlphabetic(" + tmpChar + ") ) && "
							+ "!" + cyanMethodNameTmpVar + ".endsWith(\":\") ) { " + cyanMethodNameTmpVar + " += \":\"; }" );
					if ( --size2 > 0 )
						paramArray += ", ";
				}
				String numParamList = "";
				int size3 = this.message.getkeywordParameterList().size();
				for ( final MessageKeywordWithRealParameters sel : this.message.getkeywordParameterList() ) {
					final int sizeExprList = sel.getExprList().size();
					numParamList += sizeExprList;
					if ( --size3 > 0 )
						numParamList += ", ";
				}



				final String javaNameMethodTmpVar = NameServer.nextJavaLocalVariableName();
				pw.printIdent("String " + javaNameMethodTmpVar + " = ");
				pw.println(" CyanRuntime.getJavaNameOfMethod(new String[] { " + paramArray + " }, new int[] {" + numParamList + "} );");

			    resultTmpVar = genJavaDynMessageSend(pw, messageSendTmpVar, "new CyString(" + cyanMethodNameTmpVar + ")",
				     javaNameMethodTmpVar, numParamJavaMethod, paramListAsString, stringArray, null,
				     messageSendEqualEqualToUnion
				     );

			}
			else {
				if ( isMessageToDynExpr || precededbyInter ) {
					final String cyanMethodName = message.getMethodName();

					String originalCyanMethodName;
					if ( message.getkeywordParameterList().size() == 1 && cyanMethodName.endsWith(":") ) {
						originalCyanMethodName = cyanMethodName.substring(0, cyanMethodName.length()-1);
					}
					else {
						originalCyanMethodName = null;
					}
					if ( isNew ) {
						messageSendTmpVar = receiverExpr.genJavaExpr(pw, env);
					}
  				    resultTmpVar = genJavaDynMessageSend(pw, messageSendTmpVar, "new CyString(\"" + cyanMethodName + "\")",
					    "\"" + javaNameMethod + "\"", numParamJavaMethod, paramListAsString, stringArray, originalCyanMethodName,
					    messageSendEqualEqualToUnion
					    );

				}
				else {
					Type receiverType = this.receiverExpr.getType();

					if ( receiverType instanceof TypeIntersection ) {
						InterfaceDec interDec;

						interDec = methodSignatureForMessage.getDeclaringInterface();
						if ( interDec == null ) {
							env.error( this.getFirstSymbol(),
									"Internal compiler error when generating code "
									+ "for a unary message passing, ExprMessageSendWithKeywordsToExpr, "
									+ "and the receiver is an intersection type" );
							return null;						}
						else {
							String methodPrototype = interDec.getJavaName();
							messageSendTmpVar = "((" + methodPrototype + ") " +
							messageSendTmpVar + ")";
						}
					}

					if ( methodSignatureForMessage.getReturnType(env) == Type.Nil ) {
						pw.printlnIdent( type.getJavaName() + " " + resultTmpVar + " = _Nil.prototype;");
						pw.printlnIdent(messageSendTmpVar + "." +
						javaNameMethod + "( " + paramPassing.toString()  + ");");
					}
					else {
						pw.printIdent( type.getJavaName() + " " + resultTmpVar + " = ");
						pw.println(messageSendTmpVar + "." + javaNameMethod + "( " + paramPassing.toString()  + ");");
					}

				}
			}

		}

		MethodDec currentMethod2 = env.getCurrentMethod();
		if ( currentMethod2 != null ) {
			final String currentMethodName = currentMethod2.getNameWithoutParamNumber();
			if ( isThrow && ! currentMethodName.equals("init:") &&
					!currentMethodName.equals("init") ) {
				/*
				 * to prevent the Java compiler from issuing an error message like 'method should return a value',
				 * a return statement is added every message send 'throw: obj'
				 */
				Type returnType = currentMethod2.getMethodSignature().getReturnType(env);
				if ( returnType == null || returnType == Type.Nil ) {
					pw.printlnIdent("return ;");
				}
				else {
					pw.printlnIdent("return null;");
				}
			}
		}
		// super.genJavaTestForReturn(pw, env);

		return resultTmpVar;
	}


	/**
	 * the message send has a Java object receiver and the method name is == or !=
	   @param pw
	   @param env
	   @return
	 */
	private String genJavaExprForEqEq_NotEq(PWInterface pw, Env env, String methodName) {
		String resultTmpVar = NameServer.nextJavaLocalVariableName();

		String messageSendTmpVar = null;
		Expr paramExpr = message.getkeywordParameterList().get(0).getExprList().get(0);

		String paramCode = paramExpr.genJavaExpr(pw, env);
		messageSendTmpVar = receiverExpr.genJavaExpr(pw, env);

		pw.printIdent( type.getJavaName() + " " + resultTmpVar + " = (Object ) ");
		pw.println(messageSendTmpVar + " " + methodName + " (Object ) " + paramCode + ";");

		return resultTmpVar;

	}


	@SuppressWarnings("static-method")
	private String genJavaDynMessageSend(PWInterface pw, String receiverAsString, String cyanMethodName,
			String keywordJavaName, int numParameters, String paramListAsString, List<String> paramStrList,
			String originalCyanMethodName, boolean messageSendEqualEqualToUnion ) {
		String tmp;
		final String aMethodTmp = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("java.lang.reflect.Method " + aMethodTmp + " = null;");
		//pw.printlnIdent("if ( " + keywordJavaName + " != null ) {");
		if ( keywordJavaName != null ) {
			pw.printlnIdent(aMethodTmp + " = CyanRuntime.getJavaMethodByName(" +
		            receiverAsString + ".getClass(), "
					+ keywordJavaName + ", " + numParameters +  ");");
			if ( originalCyanMethodName != null ) {
				pw.printlnIdent("    if ( " + aMethodTmp + " == null ) { " + aMethodTmp +
						" = CyanRuntime.getJavaMethodByName(" + receiverAsString + ".getClass(), \""
						+ originalCyanMethodName + "\", " + numParameters +  "); }");
			}
		}
		//pw.printlnIdent("}");
		tmp = NameServer.nextJavaLocalVariableName();
		if ( messageSendEqualEqualToUnion ) {
			pw.printlnIdent("cyan.lang.CyBoolean " + tmp + " = null;");
		}
		else {
			pw.printlnIdent("Object " + tmp + " = null;");
		}
		pw.printlnIdent("if ( " + aMethodTmp + " != null ) { ");
		pw.add();
		pw.printlnIdent("try {");
		pw.add();
		pw.printlnIdent(aMethodTmp + ".setAccessible(true);");


		if ( messageSendEqualEqualToUnion ) {
			pw.printlnIdent(tmp + " = " + (messageSendEqualEqualToUnion ? "(CyBoolean ) " : "") +
				    aMethodTmp + ".invoke(" + receiverAsString + paramListAsString + ");");		}
		else {
			pw.printlnIdent("if ( " + aMethodTmp + ".getReturnType() == void.class ) {");
			pw.add();
			pw.printlnIdent(tmp + " = _Nil.prototype;");
			pw.printlnIdent(aMethodTmp + ".invoke(" + receiverAsString + paramListAsString + ");");
			pw.sub();
			pw.printlnIdent("}");
			pw.printlnIdent("else {");
			pw.add();
			pw.printlnIdent(tmp + " = " +
				    aMethodTmp + ".invoke(" + receiverAsString + paramListAsString + ");");
			pw.sub();
			pw.printlnIdent("}");

		}




		pw.sub();
		pw.printlnIdent("}");

		String ep = NameServer.nextJavaLocalVariableName();

		pw.printlnIdent("catch ( java.lang.reflect.InvocationTargetException " + ep +" ) {");
		pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
		pw.printlnIdent("	if ( t__ instanceof ExceptionContainer__ ) {");
		pw.printlnIdent("    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
		pw.printlnIdent("	}");
		pw.printlnIdent("	else");
		pw.printlnIdent("		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
		pw.printlnIdent("}");
		pw.printlnIdent("catch (IllegalAccessException | IllegalArgumentException " + ep + ") {");
		pw.printlnIdent("        throw new ExceptionContainer__( new _ExceptionDoesNotUnderstand("
				+ cyanMethodName + " ) );");


		pw.printlnIdent("}");

		pw.sub();
		pw.printlnIdent("}");
		pw.printlnIdent("else { ");
		pw.add();
		final String dnuTmpVar = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("//	func doesNotUnderstand: (String methodName, Array<Array<Dyn>> args)");
		pw.printlnIdent("java.lang.reflect.Method " + dnuTmpVar + " = CyanRuntime.getJavaMethodByName(" + receiverAsString + ".getClass(), \"" +
		         NameServer.javaNameDoesNotUnderstand + "\", 2);");
		pw.printlnIdent("if ( " + dnuTmpVar + " == null ) {");
		pw.printlnIdent("    throw new ExceptionContainer__( new _ExceptionDoesNotUnderstand(new CyString(\"doesNotUnderstand\") ) );");
		pw.printlnIdent("}");


		pw.printlnIdent("try {");
		pw.add();
		pw.printlnIdent(NameServer.ArrayArrayDynInJava + " arrayArrayParam = new " + NameServer.ArrayArrayDynInJava + "();");
		pw.printlnIdent(NameServer.ArrayDynInJava + " arrayParam = new " + NameServer.ArrayDynInJava + "();");
		for ( final String param : paramStrList ) {
			pw.printlnIdent("arrayParam._add_1(" + param + ");");
		}
		pw.printlnIdent("arrayArrayParam._add_1( arrayParam );");
		pw.printlnIdent(dnuTmpVar + ".setAccessible(true);");

		pw.printlnIdent(tmp + " = " + (messageSendEqualEqualToUnion ? "(CyBoolean ) " : "") +
		    dnuTmpVar + ".invoke(" + receiverAsString + ", " + cyanMethodName + ", arrayArrayParam);");
		pw.sub();

		pw.printlnIdent("}");
		ep = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("catch ( java.lang.reflect.InvocationTargetException " + ep + " ) {");
		pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
		pw.printlnIdent("	if ( t__ instanceof ExceptionContainer__ ) {");
		pw.printlnIdent("    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
		pw.printlnIdent("	}");
		pw.printlnIdent("	else");
		pw.printlnIdent("		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
		pw.printlnIdent("}");
		pw.printlnIdent("catch (IllegalAccessException | IllegalArgumentException " + ep + ") {");

		pw.printlnIdent("        throw new ExceptionContainer__( new _ExceptionDoesNotUnderstand(new CyString("
				+ keywordJavaName + ") ) );");


		pw.printlnIdent("}");
		pw.sub();
		pw.printlnIdent("}");
		return tmp;
	}



	@Override
	public Symbol getFirstSymbol() {
		if ( receiverExpr == null )
			return super.getFirstSymbol();
		else
			return receiverExpr.getFirstSymbol();
	}

	@Override
	public void calcInternalTypes(Env env) {



		final Token tokenFirstkeyword = message.getTokenFirstkeyword();

		Type receiverType;

		boolean receiverIsPrototype = false;

		if ( receiverExpr != null ) {
			try {
				env.pushCheckUsePossiblyNonInitializedPrototype(false);
				if ( receiverExpr instanceof ExprSelf ) {
					((ExprSelf ) receiverExpr).calcInternalTypesDoNotCheckSelf(env);
				}
				else {
					receiverExpr.calcInternalTypes(env);
				}
			}
			finally {
				env.popCheckUsePossiblyNonInitializedPrototype();
			}

			if ( receiverExpr instanceof ExprIdentStar ) {
				receiverIsPrototype = ((ExprIdentStar ) receiverExpr)
						.getIdentStarKind() == IdentStarKind.prototype_t;
			}
			receiverType = receiverExpr.getType(env);
			if ( receiverType instanceof TypeJavaRef ) {
				this.javaReceiver = true;
			}
			else {
				if ( receiverType instanceof TypeWithAnnotations ) {
					String methodNameWithParamNumber = message.getMethodNameWithParamNumber();

					List<MethodSignature> mList = Type.Any.searchMethodPublicPackage(methodNameWithParamNumber, env);
					if ( mList == null || mList.size() == 0 ) {
						env.error(this.getFirstSymbol(), "The type of the receiver has an attached annotation. "
								+ "Hence, it can only receive messages corresponding to methods of prototype Any");
					}

				}
			}
		}
		else {
			receiverType = env.getCurrentPrototype();
			MethodDec currentMethod1 = env.getCurrentMethod();
			if ( currentMethod1 != null ) {
				currentMethod1.setSelfLeak(true);
			}
		}


		if ( this.javaReceiver ) {
			calcInternalTypesJavaReceiver(env, receiverType);
			return;

		}




		if ( message.getBackquote() ) {

			if ( receiverExpr == null || receiverExpr instanceof ExprSelf ) {
				MethodDec currentMethod2 = env.getCurrentMethod();
				if ( currentMethod2 != null ) {
					String currentMethodName = currentMethod2.getNameWithoutParamNumber();
					if ( currentMethodName.equals("init") || currentMethodName.equals("init:") ) {
						env.error(this.getFirstSymbol(),  "Message send to 'self' inside an 'init' or 'init:' method. "
								+ "This is illegal because it can access a non-initialized field or call a "
								+ " subprototype method and this method can access a non-initialized field", true, true);
								}
				}
			}

			if ( receiverIsPrototype ) {
				env.error(getFirstSymbol(), "Quoted message sends cannot be sent to prototypes. " +
			          "It does not make sense because you know the methods the prototype has at compile-time"
				 );
			}

			message.calcInternalTypes(env);
			if ( env.getCurrentMethod() != null ) {
				env.getCurrentMethod().setSelfLeak(true);
			}


			calcInternalTypesWithBackquote(env, tokenFirstkeyword);
			return;

		}
		else if ( tokenFirstkeyword == Token.INTER_DOT_ID_COLON || tokenFirstkeyword == Token.INTER_ID_COLON
				|| receiverType == Type.Dyn) {
			/*
			INTER_ID_COLON("~InterIdColon"),          // ?name:
			INTER_ID("~InterId"),                     // ?name
			INTER_DOT_ID_COLON("~InterDotIdColon"),   // ?.name:
			INTER_DOT_ID("~InterDotId"),              // ?.name
		   */
			if ( receiverExpr == null || receiverExpr instanceof ExprSelf ) {
				MethodDec cm2 = env.getCurrentMethod();
				if ( cm2 != null ) {
					String currentMethodName = cm2.getNameWithoutParamNumber();
					if ( currentMethodName.equals("init") || currentMethodName.equals("init:") ) {
						env.error(this.getFirstSymbol(),  "Message send to 'self' inside an 'init' or 'init:' method. "
								+ "This is illegal because it can access a non-initialized field or call a "
								+ " subprototype method and this method can access a non-initialized field", true, true);
								}
				}
			}

			if ( receiverIsPrototype ) {
				env.error(getFirstSymbol(), "The receiver of this message passing is a prototype. "
						+ "So it does not make sense to send a message to it using '?'. "
						+ "You already know its methods"
				 );
			}

		    type = Type.Dyn;


			if ( message.getMethodNameWithParamNumber().equals("isA:1") ) {
				try {
					env.setIsArgumentToIsA(true);
					message.calcInternalTypes(env);
				}
				finally {
					env.setIsArgumentToIsA(false);
				}
			}
			else {
				message.calcInternalTypes(env);
			}


			return ;

		}
		else {
			List<MethodSignature> methodSignatureList = null;
			String methodNameWithParamNumber = message.getMethodNameWithParamNumber();
			final String methodName = message.getMethodName();

			List<MethodSignature> allMethodSignatureList = new ArrayList<>();

			ExprReceiverKind receiverKind = ExprReceiverKind.EXPR_R;
			/*
			 * check if there is a method for this message send
			 */
			final boolean isNew = methodName.equals("new:");

			if ( isNew ) {
				final TupleTwo<String, Type> t = receiverExpr.ifPrototypeReturnsNameWithPackageAndType(env);

				if ( t == null || t.f2 == null ) {
					// is it a Java class ?
					env.error(true,
							this.message.getFirstSymbol(), "Message with keyword(s) '" + methodNameWithParamNumber + "'  can only be sent to prototypes",
							methodNameWithParamNumber, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);
				}
				else {
					receiverKind = ExprReceiverKind.PROTOTYPE_R;
					methodSignatureList = ((ObjectDec) receiverType).searchInitNewMethod(methodNameWithParamNumber);
				}
			}
			else if ( methodName.equals("init:") ) {
				env.error(this.getFirstSymbol(), "'init' and 'init:' messages have a restricted use. "
						+ "They can only appear inside an 'init' or 'init:' method and they currently can only be sent to 'super'");
			}
			else if ( receiverExpr == null || receiverExpr instanceof ExprSelf ) {
				/*
				 * message send to self
				 */

				if ( env.getEnclosingObjectDec() == null ||
						! ( env.getCurrentMethod() != null &&
								NameServer.isMethodNameEval(env.getCurrentMethod().getNameWithoutParamNumber()) )) {
					/*
					 * inside a regular prototype that is NOT inside another prototype  OR
					 * inside an inner prototype and inside a method that is not 'eval', 'eval:'
					 */
					if ( methodName.equals("init:") ) {
						//##   was "init:"
						methodSignatureList = env.getCurrentObjectDec().searchInitNewMethod(methodNameWithParamNumber);
					}
					else {
						// may call private and protected methods
						methodSignatureList = env.getCurrentObjectDec().searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
								methodNameWithParamNumber, env);

						Prototype pu = (Prototype ) receiverType.getInsideType();
						List<Prototype> superList = pu.get_this_and_all_superPrototypes();

						for ( Prototype current : superList ) {
							List<MethodSignature> currentMSList = current.searchMethodPrivateProtectedPublicPackage(methodNameWithParamNumber, env);
							if ( currentMSList != null ) {
								allMethodSignatureList.addAll(currentMSList);
							}
						}


					}
					receiverType = env.getCurrentPrototype();
				}
				else {
					/*
					 * inside a method 'eval', 'eval:' of an inner prototype
					 */
					final ObjectDec outer = env.getEnclosingObjectDec();
					//## was "init:"
					if ( methodName.equals(methodNameWithParamNumber) ) {
						//## was "init:"
						methodSignatureList = outer.searchInitNewMethod(methodNameWithParamNumber);
					}
					else {
						// may call private and protected methods
						methodSignatureList = outer.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
								methodNameWithParamNumber, env);


						Prototype pu = (Prototype ) receiverType.getInsideType();
						List<Prototype> superList = pu.get_this_and_all_superPrototypes();
						for ( Prototype current : superList ) {
							List<MethodSignature> currentMSList = current.searchMethodPrivateProtectedPublicPackage(methodNameWithParamNumber, env);
							if ( currentMSList != null ) {
								allMethodSignatureList.addAll(currentMSList);
							}
						}

					}
					receiverType = outer;
				}
				receiverKind = ExprReceiverKind.SELF_R;


				if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {

					if ( env.getCompInstSet().contains(CompilationInstruction.semAn_actions) && lookForMethodAtCompileTime(env, receiverType) ) {
						return ;
					}
					else {
						env.error(true, getFirstSymbol(),
								"Method " + methodNameWithParamNumber + " was not found in the current prototype or its super-prototypes",
								methodNameWithParamNumber, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes, receiverExpr == null ? "self" : receiverExpr.asString());
					}
				}

				if ( receiverExpr == null ) {
					ExprSelf newSelf = new ExprSelf(message.getFirstSymbol(),
							(ObjectDec ) receiverType, currentMethod);
					newSelf.setCreatedForMissingSelf(true);
					receiverExpr = newSelf;
				}
			}
			else {
				methodSignatureList = receiverExpr.getType(env).searchMethodPublicPackageSuperPublicPackage(methodNameWithParamNumber, env);
				/*
				 * if the receiverType is an union that is subtype of Any,
				 * then methods declared in Any can be called
				 */
				if ( receiverType instanceof TypeUnion || receiverType instanceof TypeIntersection
						//#### && Type.Any.isSupertypeOf(receiverType, env)
						//#### && !methodName.equals("==") && !methodName.equals("!=")
					 ) {
					// NameServer.println("receiver union");

//					env.error(this.getFirstSymbol(), "The type of the message receiver, '" + receiverExpr.asString() +
//						    "' is an union type. Then the receiver cannot receive messages as '" + methodName + "'", true, true);
				}
				else {
					Prototype pu = (Prototype ) receiverType.getInsideType();
					List<Prototype> superList = pu.get_this_and_all_superPrototypes();
					for ( Prototype current : superList ) {
						List<MethodSignature> currentMSList = current.searchMethodPublicPackage(methodNameWithParamNumber, env);
						if ( currentMSList != null ) {
							allMethodSignatureList.addAll(currentMSList);
						}
					}
					if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {


						final ObjectDec currentProto = env.getCurrentObjectDec();
						if ( currentProto != null && currentProto.getOuterObject() != null ) {
							/*
							 * inner prototypes can access private and protected members of the outer prototype
							 */
							methodSignatureList = receiverExpr.getType(env).searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodNameWithParamNumber, env);


							pu = (Prototype ) receiverType.getInsideType();
							superList = pu.get_this_and_all_superPrototypes();
							for ( Prototype current : superList ) {
								List<MethodSignature> currentMSList = current.searchMethodPrivateProtectedPublicPackage(methodNameWithParamNumber, env);
								if ( currentMSList != null ) {
									allMethodSignatureList.addAll(currentMSList);
								}
							}

						}
						if ( methodSignatureList == null || methodSignatureList.size() == 0 )  {
							if ( env.getCompInstSet().contains(CompilationInstruction.semAn_actions) && lookForMethodAtCompileTime(env, receiverType) ) {
								return ;
							}
							else {
								methodSignatureList = receiverExpr.getType(env).searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
										methodNameWithParamNumber, env);
								if ( methodSignatureList != null && methodSignatureList.size() > 0 &&
										methodSignatureList.get(0).getMethod().getVisibility() ==
										    Token.PROTECTED
										) {
									if ( methodSignatureList.get(0).getMethod().getShared() ) {
										env.error(true, getFirstSymbol(),
												"Method " + methodNameWithParamNumber +
												" is shared protected. It can only be called in",
												methodNameWithParamNumber, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes, receiverExpr.asString());

									}
									else {
										env.error(true, getFirstSymbol(),
												"Method " + methodNameWithParamNumber +
												" is protected. It can only be called in",
												methodNameWithParamNumber, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes, receiverExpr.asString());

									}
								}
								else {
									env.error(true, getFirstSymbol(),
											"Method " + methodNameWithParamNumber +
											" is protected. It can only be called in",
											methodNameWithParamNumber, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes, receiverExpr.asString());
								}

								methodSignatureList = receiverExpr.getType(env).searchMethodPublicPackageSuperPublicPackage(methodNameWithParamNumber, env);
							}


						}
					}
				}

				receiverType = receiverExpr.getType();
			}
			final TupleTwo<String, Type> t = receiverExpr.ifPrototypeReturnsNameWithPackageAndType(env);

			if ( t != null && t.f2 != null ) {
				receiverKind = ExprReceiverKind.PROTOTYPE_R;
			}


			if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {
				env.error(true, getFirstSymbol(),
						"Method " + methodNameWithParamNumber + " was not found in the type of the receiver object or in its super-prototypes",
						methodNameWithParamNumber, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes, receiverExpr == null ? "self" : receiverExpr.asString());
				return ;
			}


			/*
			MetaInfoServer.checkMessageSendWithMethodMetaobjectBeforeTypingMessage(methodSignatureList, receiverType, receiverExpr,
					receiverKind, message, env, this.message.getFirstSymbol());
			*/

			if ( receiverKind == ExprReceiverKind.SELF_R ) {
				//TODO replace currentMethod by this.currentMethod and remove local variable declaration
				final MethodDec currentMethod1 = env.getCurrentMethod();
				if ( currentMethod1 != null ) {
					currentMethod1.addSelfMessagePassing(methodSignatureList.get(0));
					currentMethod1.setSelfLeak(true);
				}

			}
			this.methodSignatureForMessage = methodSignatureList.get(0);
			boolean foundIsA_method = false;
			if ( methodSignatureForMessage instanceof MethodSignatureWithKeywords ) {
				MethodSignatureWithKeywords signature = (ast.MethodSignatureWithKeywords ) methodSignatureList.get(0);
				ObjectDec declObj = signature.getMethod().getDeclaringObject();
				if ( declObj == Type.Any && signature.getName().equals("isA:1") ) {
					foundIsA_method = true;
				}
			}
			if ( foundIsA_method ) {
				try {
					env.setIsArgumentToIsA(true);
					message.calcInternalTypes(env);
				}
				finally {
					env.setIsArgumentToIsA(false);
				}
			}
			else {
				message.calcInternalTypes(env);
			}
			if ( receiverExpr instanceof ExprIdentStar ) {
				final ExprIdentStar e = (ExprIdentStar ) receiverExpr;
				if ( e.getVarDeclaration() != null ) {
					e.getVarDeclaration().setTypeWasChanged(false);
				}
			}



			methodSignatureForMessage = checkMessageSend(methodSignatureList, env);
			if ( methodSignatureForMessage == null ) {
				checkMessageSend(methodSignatureList, env);
				env.error(message.getFirstSymbol(), "Type error in message send with method '" + message.getMethodName() + "'", true, true);
			}
			else {

				// if ( methodName.equals("new:") || methodName.equals("init:") ) {}

				final MethodDec aMethod = methodSignatureForMessage.getMethod();
				if ( aMethod != null ) {
					if ( aMethod.getShared() ) {
						/*
						 * check if the receiver is a prototype
						 * legal:
						 *        getMax: 0  (inside P that define getMax: )
						 *        P getMax: 0
						 * illegal:
						 *        self getMax: 0
						 */
						boolean missingSelf = false;
						ExprSelf exprSelf;
						if ( receiverExpr instanceof ExprSelf ) {
							exprSelf = (ExprSelf ) receiverExpr;
							missingSelf = exprSelf.getCreatedForMissingSelf();
							if ( missingSelf ) {
								if ( aMethod.getDeclaringObject() != env.getCurrentObjectDec() ) {
									env.error(this.getFirstSymbol(), "Method '" +
											methodSignatureForMessage.getFullName(env) +
											"' is shared. It can only be called without an explicit receiver in the "
											+ "prototype in which it was defined, which is '" +
											aMethod.getDeclaringObject().getFullName() + "'");
								}
							}
							else {
								env.error(this.getFirstSymbol(), "Method '" +
										methodSignatureForMessage.getFullName(env) +
										"' is shared. It can only be called "
										+ "if the receiver is a prototype. The receiver is '" +
										this.receiverExpr.asString() + "'");
							}
						}
						else if ( receiverExpr instanceof ExprIdentStar ) {
							/**
							 * check if the receiver has the method. The method should
							 * be declared in the receiver itself, there is no inheritance of
							 * shared methods
							 */
							ExprIdentStar eis = (ExprIdentStar ) receiverExpr;
							if ( eis.getIdentStarKind() != IdentStarKind.prototype_t ) {
								env.error(this.getFirstSymbol(), "Method '" +
										methodSignatureForMessage.getFullName(env) +
										"' is shared. It can only be called "
										+ "if the receiver is a prototype. The receiver is '" +
										this.receiverExpr.asString() + "'");
							}
							else {
								Prototype pt = (Prototype ) eis.getType();
								if ( aMethod.getDeclaringObject() != pt ) {
									env.error(this.getFirstSymbol(), "Method '" +
											methodSignatureForMessage.getFullName(env) +
											"' is shared. It can only be called "
											+ "if the receiver is the prototype in which the method is declared (it cannot be a superprototype)");

								}
							}

						}
						else {
							env.error(this.getFirstSymbol(), "Method '" +
									methodSignatureForMessage.getFullName(env) +
									"' is shared. It can only be called "
									+ "if the receiver is a prototype. The receiver is '" +
									this.receiverExpr.asString() + "'");
						}

					}

					Token visibility;
					visibility = aMethod.getVisibility();
					if ( visibility == Token.PRIVATE  ) {
						if ( aMethod.getDeclaringObject() != env.getCurrentOuterObjectDec() ) {
							env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessage.getFullName(env) +
									"' is private. It can only be called "
									+ "inside prototype '" + aMethod.getDeclaringObject().getFullName() + "'");
						}

					}
					else if ( visibility == Token.PACKAGE ) {
						if ( aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage() !=
								env.getCurrentObjectDec().getCompilationUnit().getCyanPackage() ) {
							env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessage.getFullName(env) +
									"'  has 'package' visibility. It can only be called "
									+ "inside package '" + aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage().getName() + "'");
						}
					}
					else if ( visibility == Token.PROTECTED ) {
						if ( !aMethod.getDeclaringObject().isSupertypeOf(env.getCurrentObjectDec(), env)  ) {
							env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessage.getFullName(env) +
									"'  has 'package' visibility. It can only be called "
									+ "in subprototypes of '" +
									aMethod.getDeclaringObject().getName() + "'", true, true);
						}
					}
					if ( !isNew && receiverIsPrototype ) {
						ObjectDec protoOfMethod = aMethod.getDeclaringObject();
						// protoOfMethod == Type.Any && aMethod.getIsFinal
						if ( receiverType != null && receiverType instanceof Prototype ) {
							Prototype rpu = (Prototype ) receiverType;
							List<MethodSignature> initMSList = rpu.searchMethodPrivateProtectedPublicPackage("init", env);
							if ( (initMSList == null || initMSList.size() == 0)
									&& rpu != Type.Nil ) {

								/*
								 * It is illegal to use a prototype that does not have an
								 * 'init' method
								 */
								boolean canBeCalledOnPrototypes = false;
								if ( protoOfMethod == Type.Any ) {
									List<Tuple2<String, WrExprAnyLiteral>> featureList =
											aMethod.getFeatureList();
									if ( featureList != null ) {
										for ( Tuple2<String, WrExprAnyLiteral> feature : featureList ) {
											if ( feature.f1.equals("annot") ) {
												Object obj = feature.f2.getJavaValue();
												if ( obj instanceof String ) {
													String strParam = meta.MetaHelper.removeQuotes( (String ) obj);
													if ( strParam.equals("canBeCalledOnPrototypes")) {
														canBeCalledOnPrototypes = true;
													}
												}
											}
										}
									}
								}
								if ( !canBeCalledOnPrototypes ) {
									env.error(getFirstSymbol(), "Prototype '" + this.receiverExpr.asString() + "' does "
											+ "not have an 'init' method. Therefore its fields may not have"
											+ " been initialized (they are if an 'init' method does exist). Then"
											+ " it is illegal to send a message to it"
									 );
								}


							}
						}
					}

				}


				type = methodSignatureForMessage.getReturnType(env);
				if ( isNew ) {
					//## was "init:"
					methodNameWithParamNumber = methodNameWithParamNumber.replace("new:", "init:");
					final List<MethodSignature> initMethodSignatureList =
							((ObjectDec) receiverType).searchInitNewMethod(methodNameWithParamNumber);
					if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_9 ) {
						MetaInfoServer.checkMessageSendWithMethodMetaobject(initMethodSignatureList, receiverType, receiverExpr,
								receiverKind, message, env, this.message.getFirstSymbol());
					}

					if ( env.getProject().getCompilerManager().getCompilationStep().ordinal() < CompilationStep.step_7.ordinal() ) {
						for ( final MethodSignature ms : initMethodSignatureList ) {
							type = MetaInfoServer.replaceMessageSendIfAsked(ms,
									this,
									env, this.getFirstSymbol(), type);
						}

					}
				}
				else {
					if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_9 )  {
						MetaInfoServer.checkMessageSendWithMethodMetaobject(allMethodSignatureList, receiverType, receiverExpr,
								receiverKind, message, env, this.message.getFirstSymbol());
					}

					if ( env.getProject().getCompilerManager().getCompilationStep().ordinal() < CompilationStep.step_7.ordinal() ) {


//							Prototype pu = (Prototype ) receiverType;
//							List<Prototype> superList = pu.getAllSuperPrototypes();
//							for ( Prototype current : superList ) {
//								List<MethodSignature> currentMSList = current.searchMethodPublicPackage(methodName, env);
//								if ( currentMSList != null ) {
//									allMethodSignatureList.addAll(currentMSList);
//								}
//							}
						type = MetaInfoServer.replaceMessageSendIfAsked(allMethodSignatureList,
								this,
								env, this.getFirstSymbol(), type);



//						type = MetaInfoServer.replaceMessageSendIfAsked(methodSignatureForMessage,
//								this,
//								env, this.getFirstSymbol(), type);
					}
				}
			}
			MethodDec cm = env.getCurrentMethod();
			if ( cm != null ) {
				String currentMethodName = cm.getNameWithoutParamNumber();
				if ( (currentMethodName.equals("init") || currentMethodName.equals("init:"))
						&& receiverKind == ExprReceiverKind.SELF_R ) {
					if ( ! MethodDec.hasAttachedAnnotationNamed(methodSignatureList,
							"accessOnlySharedFields") ) {
						env.error(this.getFirstSymbol(),  "Message send to 'self' inside an 'init' or 'init:' method. "
								+ "This is illegal because it can access a non-initialized field or call a "
								+ " subprototype method and this method can access a non-initialized field", true, true);
					}
				}
			}

		}
		doNotReturn = message.getMethodNameWithParamNumber().equals("throw:1");

		super.calcInternalTypes(env);

	}


	/**
	   @param env
	   @param receiverType
	 */
	private void calcInternalTypesJavaReceiver(Env env, Type receiverType) {
		final String methodName = message.getJavaMethodNameReceiverJavaObject();

		message.calcInternalTypes(env);
		if ( message.getkeywordParameterList().size() != 1 ) {
			env.error(this.getFirstSymbol(), "The receiver is a Java object. There should "
					+ "be just one message keyword like '" + message.getkeywordParameterList().get(0).getkeywordName() + "'");
			return ;
		}
		TypeJavaRef javaReceiverType = (TypeJavaClass ) this.receiverExpr.getType();
		final Class<?> []parameterTypes = new Class<?>[message.getkeywordParameterList().get(0).getExprList().size()];
		TypeVariable<?>[] arrayGenParamType = null;

		try {
			arrayGenParamType = javaReceiverType.getClassLoad(env, message.getFirstSymbol()).getTypeParameters();
		}
		catch ( final java.lang.reflect.GenericSignatureFormatError e ) {
		}


		final Token tokenFirstkeyword = message.getTokenFirstkeyword();

		if ( message.getBackquote() ) {
			MethodDec cm = env.getCurrentMethod();
			if ( cm != null ) {
				cm.setSelfLeak(true);
			}
			calcInternalTypesWithBackquote(env, tokenFirstkeyword);
			return;

		}
		else if ( tokenFirstkeyword == Token.INTER_DOT_ID_COLON ||
				   tokenFirstkeyword == Token.INTER_ID_COLON
				  ) {
			/*
			INTER_ID_COLON("~InterIdColon"),          // ?name:
			INTER_ID("~InterId"),                     // ?name
			INTER_DOT_ID_COLON("~InterDotIdColon"),   // ?.name:
			INTER_DOT_ID("~InterDotId"),              // ?.name
		   */
			type = Type.Dyn;

		}



		int i = 0;
		for ( final Expr paramExpr : message.getkeywordParameterList().get(0).getExprList() ) {
			final Type exprType = paramExpr.getType();
			if ( exprType.getInsideType() instanceof Prototype ) {
				final String puName = ((Prototype) exprType).getName();
				parameterTypes[i] = NameServer.getJavaClassFromCyanName(puName);
			}
			else if ( exprType instanceof TypeJavaClass ) {
				parameterTypes[i] = ((TypeJavaClass) exprType).getClassLoad(env, this.getFirstSymbol());
			}
			else if ( exprType instanceof TypeJavaRefArray ) {
				TypeJavaRefArray at = (TypeJavaRefArray ) exprType;
				Type et = at.getElementType();
				while ( et instanceof TypeJavaRefArray ) {
					et = ((TypeJavaRefArray ) et).getElementType();
				}
				if ( et instanceof TypeJavaClass ) {
					parameterTypes[i] = ((TypeJavaClass) et).getClassLoad(env, this.getFirstSymbol());
				}
				else {
					env.error(paramExpr.getFirstSymbol(), "A parameter to a call to a Java method should be "
							+ "either a Java object or an object of a basic type in Cyan (Int, Char, ...)");
				}
			}
			else {
				env.error(paramExpr.getFirstSymbol(), "A parameter to a call to a Java method should be "
						+ "either a Java object or an object of a basic type in Cyan (Int, Char, ...)");
			}
			++i;
		}


		if ( methodName.equals("new") ) {
			if ( !(receiverType instanceof TypeJavaClass) ) {
				env.error(this.getFirstSymbol(), "The receiver is a Java object. It should be a Java class name");
				return ;
			}

			try {
				type = javaReceiverType;
				constructor = javaReceiverType.getClassLoad(env, this.getFirstSymbol()).getConstructor(parameterTypes);

				if ( this.receiverExpr instanceof ExprIdentStar &&
						((ExprIdentStar) this.receiverExpr).getIdentStarKind() != IdentStarKind.jvmClass_t &&
						!(this.receiverExpr instanceof ast.ExprGenericPrototypeInstantiation) ) {
					env.error(getFirstSymbol(), "'new' messages can only be sent to a class receiver");
				}
				final int modif = constructor.getModifiers();
				if ( ! Modifier.isPublic(modif) ) {
					env.error(getFirstSymbol(), "Constructor is not public");
				}

				return ;
			}
			catch (NoSuchMethodException
					| SecurityException e) {
				boolean foundMethod = false;

				Class<?> javaClass = javaReceiverType.getTheClass();
				for ( Constructor<?> construc : javaClass.getConstructors()  ) {
					if ( construc.getParameterCount() != parameterTypes.length ) {
						continue;
					}
					final int modif = construc.getModifiers();
					if ( Modifier.isPublic(modif) ) {
						boolean paramOk = true;
						int k = 0;

						for ( Class<?> paramType : construc.getParameterTypes()) {
							if ( ! paramType.isAssignableFrom(parameterTypes[k]) ) {
								paramOk = false;
								break;
							}
							++k;
						}
						if ( paramOk ) {
							foundMethod = true;
							constructor = construc;
							break;
						}
					}

				}

//					/*
//					 * if the execution is here, the method has not exactly the types of the real parameters. Like in
//					 *
//        					var sb = StringBuffer new;
//        					//var Appendable ap = sb;
//        					var Formatter formatter = Formatter new: sb;
//					 *
//					 * The type of the contructor of Formatter is Appendable, not StringBuffer. Then we
//					 * have to try all possibilities of supertypes of StringBuffer. If there are more
//					 * than one parameter, we have to try all possibilities of the combinations of all
//					 * supertypes of the parameters. We consider that A is subtype of A
//					 *
//					 */
//
//					final Class<?> [][] allComb = ExprMessageSend.allCombinations2(parameterTypes);
//					for ( final Class<?>[] classArray : allComb ) {
//						try {
//							constructor = javaReceiverType.getClassLoad(env, this.getFirstSymbol()).getConstructor(classArray);
//							type = javaReceiverType;
//							foundMethod = true;
//							if ( this.receiverExpr instanceof ExprIdentStar &&
//									((ExprIdentStar) this.receiverExpr).getIdentStarKind() != IdentStarKind.jvmClass_t &&
//									!(this.receiverExpr instanceof ast.ExprGenericPrototypeInstantiation) ) {
//								env.error(getFirstSymbol(), "'new' messages can only be sent to a class receiver");
//							}
//							final int modif = constructor.getModifiers();
//							if ( ! Modifier.isPublic(modif) ) {
//								env.error(getFirstSymbol(), "Constructor is not public");
//							}
//
//							break;
//						}
//						catch ( NoSuchMethodException
//								| SecurityException eee) {
//						}
//
//					}
				if ( ! foundMethod ) {
					env.error(this.getFirstSymbol(), "A Java constructor for this message send was not found "
							+ "or there was a security exception (class SecurityException)");
				}
			}

		}
		else {
			// a message passing to a Java object

			if ( !(receiverType instanceof TypeJavaRef) ) {
				env.error(this.getFirstSymbol(), "A Java class was expected as receiver of the message '" +
			        message.getkeywordParameterList().get(0).getkeywordName() +
			        ": args'");
			}
			javaReceiverType = (TypeJavaRef ) receiverType;
			final Class<?> aClassJavaReceiverType = javaReceiverType.getClassLoad(env, this.getFirstSymbol());
			if ( arrayGenParamType == null || arrayGenParamType.length == 0  ) {
				/*
				 * receiver type is not a generic prototype instantiation
				 */
				try {
					// Class<?> aClassJavaReceiverType = javaReceiverType.getaClass(env, this.getFirstSymbol());
					// javaMethod = aClassJavaReceiverType.getMethod(methodName, parameterTypes);
					javaMethod = MethodUtils.getMatchingAccessibleMethod(aClassJavaReceiverType, methodName, parameterTypes);

					if ( javaMethod == null ) {
						if ( (aClassJavaReceiverType == int.class ||
								aClassJavaReceiverType == long.class)
								&& methodName.length() == 1 &&
								"+-*/".contains(methodName)
								) {
							final Class<?> methodReturnType = aClassJavaReceiverType;
							type = TypeJavaRef.classJavaToTypeJavaRef(methodReturnType, env, this.getFirstSymbol());

						}
						else {
							javaMethod = MethodUtils.getMatchingAccessibleMethod(Object.class, methodName, parameterTypes);
							if ( javaMethod == null ) {
								throw new SecurityException();
							}
						}
					}
					else {
						final Class<?> methodReturnType = javaMethod.getReturnType();
						type = TypeJavaRef.classJavaToTypeJavaRef(methodReturnType, env, this.getFirstSymbol());
						checkStaticPublicMethod(env);
					}
				}
				catch (SecurityException e) {

					if ( (methodName.equals("==") || methodName.equals("!=")) && parameterTypes.length == 1 ) {
						type = env.getProject().getProgram().searchJavaBasicType("boolean");
						if ( type != null ) {
							this.messageIsEqEq_NotEq = true;
							return ;
						}
					}
					boolean foundMethod = false;

					for ( Method mm : aClassJavaReceiverType.getMethods() ) {
						if ( mm.getName().equals(methodName) ) {
							if ( mm.getParameterCount() == parameterTypes.length ) {
								boolean typeError = false;
								int kk = 0;
								for (java.lang.reflect.Parameter pp : mm.getParameters() ) {
									ClassUtils.isAssignable(pp.getType(), parameterTypes[kk]);
									if ( ! pp.getType().isAssignableFrom(parameterTypes[kk]) ) {
										typeError = true;
										break;
									}
									//System.out.println(pp.getType().getName());
									++kk;
								}
								if ( !typeError ) {
									foundMethod = true;
									javaMethod = mm;
									final Class<?> methodReturnType = javaMethod.getReturnType();
									type = TypeJavaRef.classJavaToTypeJavaRef(methodReturnType, env, this.getFirstSymbol());
									checkStaticPublicMethod(env);
									break;
								}
							}
						}
					}

					SecurityException lastExceptionThrown = null;
					if ( ! foundMethod ) {
						foundMethod = false;

						javaMethod = MethodUtils.getMatchingAccessibleMethod(aClassJavaReceiverType, methodName, parameterTypes);

						/*
						 * if the execution is here, the method has not exactly the types of the real parameters. Like in
						 *
		    					//System.out.println(0);
						 *
						 * The type the parameter to println is Object, not int or Integer. Then we
						 * have to try all possibilities of supertypes of int. If there are more
						 * than one parameter, we have to try all possibilities of the combinations of all
						 * supertypes of the parameters. We consider that A is subtype of A
						 *
						 */
						//javaMethod = javaReceiverType.getClassLoad(env, this.getFirstSymbol()).getMethod(methodName, classArray);

						for (int ii = 0; ii < parameterTypes.length; ++ii ) {
							final Class<?> paramType = parameterTypes[ii];
							if ( paramType.isPrimitive() ) {
								parameterTypes[ii] = NameServer.javaPrimitiveTypeToWrapperClass(parameterTypes[ii].getName());
							}
						}
						final Class<?> [][] allComb = ExprMessageSend.allCombinations2(parameterTypes);
						for ( final Class<?>[] classArray : allComb ) {
							try {
								javaMethod = javaReceiverType.getClassLoad(env, this.getFirstSymbol()).getMethod(methodName, classArray);
								final Class<?> methodReturnType = javaMethod.getReturnType();

								final java.lang.reflect.Type aReflectGenericType = javaMethod.getGenericReturnType(); // E
								final String aReflectGenericTypeName = aReflectGenericType.getTypeName();
								// javaGenericClass.getTypeParameters()
								setTypeMaybeGenericParameter(env,
										aClassJavaReceiverType, methodReturnType,
										aReflectGenericTypeName);
								foundMethod = true;
								checkStaticPublicMethod(env);
								lastExceptionThrown = null;
								break;
							}
							catch ( NoSuchMethodException eee ) { }
							catch (SecurityException eee) {
								lastExceptionThrown = eee;
							}

						}

					}
					if ( ! foundMethod ) {
						String s = "";
						for (Method m : aClassJavaReceiverType.getMethods() ) {
							s += m.getName() + " ";
						}
						if ( lastExceptionThrown != null ) {
							env.error(this.getFirstSymbol(), "There was a security exception (class SecurityException) when searching for method " + methodName);
						}
						else {
							env.error(this.getFirstSymbol(), "A Java method for the message passing '" + methodName + "' was not found "
									+ " The available methods are '" + s + "'");
						}
					}


				}
			}
			else {
				/*
				 * receiver type is a generic prototype instantiation
				 */

				try {
					javaMethod = javaReceiverType.getClassLoad(env, this.getFirstSymbol()).getMethod(methodName, parameterTypes);
					final Class<?> methodReturnType = javaMethod.getReturnType();

					// java.lang.reflect.Type javaTypeDescriptor
					// javaMethod.getGenericReturnType()  -> E
					// aClassJavaReceiverType
					final java.lang.reflect.Type aReflectGenericType = javaMethod.getGenericReturnType(); // E
					final String aReflectGenericTypeName = aReflectGenericType.getTypeName();
					// javaGenericClass.getTypeParameters()
					setTypeMaybeGenericParameter(env,
							aClassJavaReceiverType, methodReturnType,
							aReflectGenericTypeName);
					checkStaticPublicMethod(env);

				}
				catch (NoSuchMethodException
						| SecurityException e) {


					if ( (methodName.equals("==") || methodName.equals("!=")) && parameterTypes.length == 1 ) {
						type = env.getProject().getProgram().searchJavaBasicType("boolean");
						if ( type != null ) {
							return ;
						}
					}
					boolean foundMethod = false;
					/*
					 * if the execution is here, the method has not exactly the types of the real parameters. Like in
					 *
							//System.out.println(0);
					 *
					 * The type the parameter to println is Object, not int or Integer. Then we
					 * have to try all possibilities of supertypes of int. If there are more
					 * than one parameter, we have to try all possibilities of the combinations of all
					 * supertypes of the parameters. We consider that A is subtype of A
					 *
					 */
					for (int ii = 0; ii < parameterTypes.length; ++ii ) {
						final Class<?> paramType = parameterTypes[ii];
						if ( paramType.isPrimitive() ) {
							parameterTypes[ii] = NameServer.javaPrimitiveTypeToWrapperClass(parameterTypes[ii].getName());
						}
					}
					final Class<?> [][] allComb = ExprMessageSend.allCombinations2(parameterTypes);
					SecurityException lastExceptionThrown = null;

					for ( final Class<?>[] classArray : allComb ) {
						try {
							javaMethod = javaReceiverType.getClassLoad(env, this.getFirstSymbol()).getMethod(methodName, classArray);
							final Class<?> methodReturnType = javaMethod.getReturnType();

							final java.lang.reflect.Type aReflectGenericType = javaMethod.getGenericReturnType(); // E
							final String aReflectGenericTypeName = aReflectGenericType.getTypeName();
							// javaGenericClass.getTypeParameters()
							setTypeMaybeGenericParameter(env,
									aClassJavaReceiverType, methodReturnType,
									aReflectGenericTypeName);
							foundMethod = true;
							checkStaticPublicMethod(env);
							lastExceptionThrown = null;
							break;
						}
						catch ( NoSuchMethodException eee ) {

						}
						catch (SecurityException eee) {
							lastExceptionThrown = eee;
						}

					}
					if ( ! foundMethod ) {
						String s = "";
						for (Method m : aClassJavaReceiverType.getMethods() ) {
							s += m.getName() + " ";
						}
						if ( lastExceptionThrown != null ) {
							env.error(this.getFirstSymbol(), "There was a security exception (class SecurityException) when searching for method " + methodName);
						}
						else {
							env.error(this.getFirstSymbol(), "A Java method for the message passing '" + methodName + "' was not found "
									+ " The available methods are '" + s + "'");
						}
					}



				}

			}
		}
		return ;
	}


	/**
	   @param env
	 */
	private void checkStaticPublicMethod(Env env) {
		final int modif = javaMethod.getModifiers();
		if ( Modifier.isStatic(modif) ) {
			// method  is static
			if ( this.receiverExpr instanceof ExprIdentStar &&
					((ExprIdentStar) this.receiverExpr).getIdentStarKind() != IdentStarKind.jvmClass_t &&
					!(this.receiverExpr instanceof ast.ExprGenericPrototypeInstantiation) ) {
				env.error(getFirstSymbol(), "Static methods can only be called with a class receiver");
			}
		}
		else if ( ! Modifier.isPublic(modif) ) {
			env.error(getFirstSymbol(), "Method '" + javaMethod.getName() + "' is not public");
		}
	}


	/**
	   @param env
	   @param aClassJavaReceiverType
	   @param realJavaReturnType
	   @param aReflectGenericTypeName
	 */
	private void setTypeMaybeGenericParameter(Env env,
			Class<?> aClassJavaReceiverType, Class<?> realJavaReturnType,
			String aReflectGenericTypeName) {
		boolean foundGenType = false;
		int tnIndex = 0;
		for ( final TypeVariable<?> aGenericTypeOfReceiverType : aClassJavaReceiverType.getTypeParameters() ) {
			if ( aReflectGenericTypeName.equals(aGenericTypeOfReceiverType.getTypeName()) ) {
				foundGenType = true;
				break;
			}
			++tnIndex;
		}
		if ( foundGenType ) {
			/*
			 * look in the generic prototype instantiation which is the type associated to
			 * tn
			 */

			final ExprGenericPrototypeInstantiation gpi = ((TypeJavaRef ) receiverExpr.getType()).getGPI();
			if ( gpi == null ) {
				/*
				 *  information on the generic prototype instantiation is not available. This happens in
				 *  code like
				 *  	(annot getRealParameterList) get: i
				 *  in which there is no type List<..> in the code. Then the compiler is not able
				 *  to discover the real type from a declaration. This happens in
				 *          var List<Integer> array;
				 *          var n = array get: 0;
				 *  The compiler knows the declaration of array and knows its parameter is 'Integer'.
				 *  In the first case, we consider that the type is Dyn
				 */
				type = Type.Dyn;
			}
			else {
				type = gpi.getRealTypeListList().get(0).get(tnIndex).getType();
			}
			/*
			if ( !(ttt instanceof TypeJavaRef) ) {
				env.error(this.getFirstSymbol(), "The " + tnIndex + "th real type parameter to '"
						+ gpi.getName() + "', '" + ttt.getFullName() + "' should be a Java type. It is not");

			}
			* /
			type = TypeJavaRef.classJavaToTypeJavaRef(
					((TypeJavaRef) ttt).getaClass(env, gpi.getFirstSymbol()),
					env, this.getFirstSymbol());
			*/
		}
		else {
			type = TypeJavaRef.classJavaToTypeJavaRef(realJavaReturnType, env, this.getFirstSymbol());
		}
	}

	/**
	 * javaGenericClass is like "List", a generic Java class.
	 * javaTypeGenericParam_or_NotGeneric is like
	   @param env
	   @param javaGenericClass
	   @param javaTypeGenericParam_or_NotGeneric
	   @return
	 */
	private boolean isReturnTypeGeneric(Env env, Class<?> javaGenericClass,
			Class<?> javaTypeGenericParam_or_NotGeneric,
			java.lang.reflect.Type javaTypeDescriptor) {

		//String tn = javaMethod.getGenericReturnType().getTypeName();
		final String tn = javaTypeDescriptor.getTypeName();
		boolean foundGenType = false;
		int tnIndex = 0;
		for ( final TypeVariable<?> t : javaGenericClass.getTypeParameters() ) {
			if ( tn.equals(t.getTypeName()) ) {
				foundGenType = true;
				break;
			}
			++tnIndex;
		}
		if ( foundGenType ) {
			/*
			 * look in the generic prototype instantiation which is the type associated to
			 * tn
			 */
			final ExprGenericPrototypeInstantiation gpi = (ExprGenericPrototypeInstantiation ) receiverExpr;
			final Type ttt = gpi.getRealTypeListList().get(0).get(tnIndex).getType();
			if ( !(ttt instanceof TypeJavaRef) ) {
				env.error(this.getFirstSymbol(), "The " + tnIndex + "th real type parameter to '"
						+ gpi.getName() + "', '" + ttt.getFullName() + "' should be a Java type. It is not");

			}
			type = TypeJavaRef.classJavaToTypeJavaRef(((TypeJavaRef) ttt).getClassLoad(env, gpi.getFirstSymbol()),
					env, this.getFirstSymbol());
		}
		else {
			type = TypeJavaRef.classJavaToTypeJavaRef(javaTypeGenericParam_or_NotGeneric, env, this.getFirstSymbol());
		}
		return true;
	}

	/**
	 * javaGenericClass is a Java class, the type of the receiver object in a message send:<br>
	 * <code>
	 *      var List<Int> intArray = List<Int> new;<br>
	 *      intArray get: i <br>
	 * </code>
	 * javaTypeGenericParam_or_NotGeneric is
	   @param env
	   @param javaGenericClass
	   @param javaTypeGenericParam_or_NotGeneric
	   @param javaTypeDescriptor
	   @return
	 */
	private boolean javaTypeFor_a_MaybeJavaType(Env env, Class<?> javaGenericClass,
			Class<?> javaTypeGenericParam_or_NotGeneric,
			java.lang.reflect.Type javaTypeDescriptor) {

		//String tn = javaMethod.getGenericReturnType().getTypeName();
		final String tn = javaTypeDescriptor.getTypeName();
		boolean foundGenType = false;
		int tnIndex = 0;
		for ( final TypeVariable<?> t : javaGenericClass.getTypeParameters() ) {
			if ( tn.equals(t.getTypeName()) ) {
				foundGenType = true;
				break;
			}
			++tnIndex;
		}
		if ( foundGenType ) {
			/*
			 * look in the generic prototype instantiation which is the type associated to
			 * tn
			 */
			final ExprGenericPrototypeInstantiation gpi = (ExprGenericPrototypeInstantiation ) receiverExpr;
			final Type ttt = gpi.getRealTypeListList().get(0).get(tnIndex).getType();
			if ( !(ttt instanceof TypeJavaRef) ) {
				env.error(this.getFirstSymbol(), "The " + tnIndex + "th real type parameter to '"
						+ gpi.getName() + "', '" + ttt.getFullName() + "' should be a Java type. It is not");

			}
			type = TypeJavaRef.classJavaToTypeJavaRef(((TypeJavaRef) ttt).getClassLoad(env, gpi.getFirstSymbol()),
					env, this.getFirstSymbol());
		}
		else {
			type = TypeJavaRef.classJavaToTypeJavaRef(javaTypeGenericParam_or_NotGeneric, env, this.getFirstSymbol());
		}
		return true;
	}



	/**
	   @param env
	   @param receiverType
	 */
	public boolean lookForMethodAtCompileTime(Env env, Type receiverType) {
		if ( !(receiverType instanceof ObjectDec) ) {
			return false;
		}

		boolean ret = false;

		message.calcInternalTypes(env);

		final ObjectDec proto = (ObjectDec ) receiverType;

		final List<AnnotationAt> metaobjectAnnotationList =
				proto.getAnnotationThisAndSuperCTDNUList();
		if ( metaobjectAnnotationList.size() == 0 ) {
			return false;
		}
		WrPrototype lastProtoWithAnnotReplacedCode = null;

		AnnotationAt lastAnnotWhichReplacedCode = null;

		int timeoutMilliseconds = Timeout.getTimeoutMilliseconds( env,
				env.getProject().getProgram().getI(),
				env.getCurrentCompilationUnit().getCyanPackage().getI(),
				this.getFirstSymbol());


		for ( final AnnotationAt annot : metaobjectAnnotationList ) {
			final CyanMetaobjectAtAnnot cyanMetaobject = annot.getCyanMetaobject();

			checkAnnotIActionMethodMissing_semAn(env, annot);

			_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot ) cyanMetaobject.getMetaobjectInCyan();

			boolean actionMissing = cyanMetaobject instanceof IActionMethodMissing_semAn
					||
			        (other != null && other instanceof _IActionMethodMissing__semAn)
			        ;

			if ( !actionMissing ) {
				env.error(this.getFirstSymbol(), "Internal error: metaobject '" + annot.getCyanMetaobject().getName() + "' should implement "
						+ meta.IActionMethodMissing_semAn.class.getName());
				return false;
			}

			// MessageWithKeywords  message = (MessageWithKeywords ) this.getMessage();
			// message.calcInternalTypes(env);

			Tuple3<StringBuffer, String, String> codeType = null;
			StringBuffer sb = null;
			try {
				if ( other == null ) {
					final IActionMethodMissing_semAn doesNot = (IActionMethodMissing_semAn ) cyanMetaobject;
					Timeout<Tuple3<StringBuffer, String, String>> to = new Timeout<>();

					codeType = to.run(
							() -> {
								return doesNot.semAn_missingKeywordMethod(
										receiverExpr == null ? null : receiverExpr.getI(),
										message == null ? null : message.getI(), env.getI());
							},
							timeoutMilliseconds, "semAn_missingKeywordMethod",
							cyanMetaobject, env);

//					codeType = doesNot.semAn_missingKeywordMethod(
//							receiverExpr == null ? null : receiverExpr.getI(),
//							message == null ? null : message.getI(), env.getI());
				}
				else {
					final _IActionMethodMissing__semAn doesNot = (_IActionMethodMissing__semAn ) other;
					Timeout<_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT> toCyan = new Timeout<>();

					_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd =
							toCyan.run(
									() -> {
										return doesNot._semAn__missingKeywordMethod_3(
												receiverExpr == null ? null : receiverExpr.getI(),
														message == null ? null : message.getI(), env.getI());
									},
									timeoutMilliseconds, "semAn_missingKeywordMethod",
									cyanMetaobject, env);
//					_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT tdd = doesNot._semAn__missingKeywordMethod_3(
//							receiverExpr == null ? null : receiverExpr.getI(),
//							message == null ? null : message.getI(), env.getI());

					CyString f1 = tdd._f1();
					CyString f2 = tdd._f2();
					CyString f3 = tdd._f3();

					String f1s = f1.s;
					if ( f1s.length() != 0 ) {
						codeType = new Tuple3<StringBuffer, String, String>(
								new StringBuffer(f1s), f2.s,
								f3.s
								);
					}

				}
			}
			catch ( final error.CompileErrorException e ) {
			}
			catch ( final NoClassDefFoundError e ) {
				env.error(annot.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
			}
			catch ( final RuntimeException e ) {
				env.thrownException(annot, this.getFirstSymbol(), e);
			}
			finally {
				env.errorInMetaobject(cyanMetaobject, message.getFirstSymbol());
			}




			if ( codeType != null  ) {
				WrPrototype protoOfAnnotWantsToReplaceMessagePassing = ExprMessageSendWithKeywordsToExpr.currentPrototypeFromAnnot(annot);

				if ( protoOfAnnotWantsToReplaceMessagePassing == lastProtoWithAnnotReplacedCode ) {
					// two annotations are trying to replace a message passing by code and
					// the two ones are in the same prototype. This is illegal

					env.error(this.getFirstSymbol(),
							"Two annotations of the same prototype are "
							+ "trying to replace a message passing. They are:\n" +
							"    a) annotation '" +
							lastAnnotWhichReplacedCode.getCyanMetaobject().getName() +
							"' of line " + lastAnnotWhichReplacedCode.getFirstSymbol().getLineNumber() +
							" of prototype '" +
							lastProtoWithAnnotReplacedCode.getFullName() +
							"'\n" +
							"    b) annotation '" +
							annot.getCyanMetaobject().getName() +
							"' of line " + annot.getFirstSymbol().getLineNumber() +
							" of prototype '" +
							protoOfAnnotWantsToReplaceMessagePassing.getFullName() + "'"
							);
					return false;

				}

				if ( lastProtoWithAnnotReplacedCode == null ) {
					lastAnnotWhichReplacedCode = annot;
					lastProtoWithAnnotReplacedCode = protoOfAnnotWantsToReplaceMessagePassing;
					sb = codeType.f1;

					if ( this.codeThatReplacesThisStatement != null ) {
						/*
						 * this message send has already been replaced by another expression
						 */
						if ( cyanAnnotationThatReplacedStatByAnotherOne != null ) {
							env.warning(this.getFirstSymbol(), "Metaobject annotation '" + cyanMetaobject.getName() +
									"' at line " + annot.getFirstSymbol().getLineNumber()  +
									" of prototype " + annot.getPackageOfAnnotation() + "." +
									annot.getPrototypeOfAnnotation() +
									" is trying to replace message send '" + this.asString() +
									"' by an expression. But this has already been asked by metaobject annotation '" +
									cyanAnnotationThatReplacedStatByAnotherOne.getCyanMetaobject().getName() + "'" +
									" at line " + cyanAnnotationThatReplacedStatByAnotherOne.getFirstSymbol().getLineNumber() +
									" of prototype " + cyanAnnotationThatReplacedStatByAnotherOne.getPackageOfAnnotation() + "." +
									cyanAnnotationThatReplacedStatByAnotherOne.getPrototypeOfAnnotation());
						}
						else {
							env.warning(this.getFirstSymbol(), "Metaobject annotation '" + cyanMetaobject.getName() +
									"' at line " + annot.getFirstSymbol().getLineNumber()  +
									" of prototype " + annot.getPackageOfAnnotation() + "." +
									annot.getPackageOfAnnotation() +
									" is trying to replace message send '" + this.asString() +
									"' by an expression. But this has already been asked by someone else");
						}
					}

					  // if there is any errors, signals them
					env.errorInMetaobject(cyanMetaobject, this.getFirstSymbol());

					//final Type typeOfCode = GetHiddenItem.getHiddenType(codeType.f2);

					Type typeOfCode;
					if ( codeType.f2 == null || codeType.f3 == null || codeType.f2.length() == 0 ||
							codeType.f3.length() == 0 ) {
						typeOfCode = Type.Dyn;
					}
					else {
						typeOfCode = env.searchPackagePrototype(codeType.f2,
								codeType.f3);
						if ( typeOfCode == null ) {
							env.error(true,
								this.getFirstSymbol(),
								"This message send was replaced by an expression that has type '" +
										codeType.f2 + "." +
										codeType.f3 + "' which was not found", cyanMetaobject.getPrototypeOfType(), ErrorKind.prototype_was_not_found_inside_method);
						}
					}


					env.replaceStatementByCode(this, annot, sb, typeOfCode);

					cyanAnnotationThatReplacedStatByAnotherOne = annot;

					if ( typeOfCode == null )
						env.error(true,
								this.getFirstSymbol(),
										"This message send was replaced by an expression that has type '" + cyanMetaobject.getPackageOfType() + "." +
												cyanMetaobject.getPrototypeOfType() + "' which was not found", cyanMetaobject.getPrototypeOfType(), ErrorKind.prototype_was_not_found_inside_method);
					else
						type = typeOfCode;
					ret = true;

				}

			}

		}


		return ret;
	}


	public static WrPrototype currentPrototypeFromAnnot(
			AnnotationAt firstMO) {
		IDeclaration dec = firstMO.getDeclaration();
		if ( dec instanceof WrPrototype ) {
			return (WrPrototype ) dec;
		}
		else if ( dec instanceof WrMethodDec ) {
			return ((WrMethodDec ) dec).getDeclaringObject();
		}
		else if ( dec instanceof WrMethodSignature ) {
			return ((WrMethodSignature ) dec).getDeclaringInterface();
		}
		else {
			return null;
		}
	}


	public static void checkAnnotIActionMethodMissing_semAn(Env env,
			final AnnotationAt annot
			) {
		final CyanMetaobjectAtAnnot cyanMetaobject = annot.getCyanMetaobject();
		if ( !cyanMetaobject.attachedDecKindListSubsetOf(AttachedDeclarationKind.PROTOTYPE_DEC,
				AttachedDeclarationKind.METHOD_DEC,
				AttachedDeclarationKind.METHOD_SIGNATURE_DEC ) ) {
			env.error(annot.getFirstSymbol(), "The metaobject class of this annotation implements interface "
					+ "'" + IActionMethodMissing_semAn.class.getCanonicalName() + "'. Then this annotation can only"
							+ " be attached to prototypes, methods, and method interfacees");
		}
	}



	public Expr getReceiverExpr() {
		return receiverExpr;
	}


	static Object []cast(EvalEnv ee, Object []fromArray, Class<?> []paramTypes) {

		Object []objectArgList = new Object[fromArray.length];
		if ( fromArray.length != paramTypes.length ) { return null; }

		int i = 0;
		for ( Object arg : fromArray ) {
			Object param = arg;
	    	if ( param instanceof cyanruntime.Ref<?> ) {
	    		param = ((cyanruntime.Ref<?> ) param).elem;
	    	}
			objectArgList[i] = Statement.castCyanJava(ee, paramTypes[i], param);
			++i;
		}
		return objectArgList;
	}


	@Override
	public Object eval(EvalEnv ee) {

		final String methodName = message.getMethodName();

		final boolean isNew = methodName.equals("new:");
		Object receiverValue = null;
		/*
		 * true if the receiver class is a Cyan prototype and
		 * the class was not found with the parameter types used in
		 * the program. For example,
		 *          Tuple<Int, String, Char>
		 * will not be found. This method will try to find
		 *          Tuple<Dyn, Dyn, Dyn>
		 * and therefore the Java class corresponding to this Cyan
		 * prototype should take Object parameters in its constructor.
		 */
		boolean cyanGenericProtoReceiver_thatDoesNotExist = false;
		boolean genericPrototypeReceiver_AndisNew = false;
		if ( receiverExpr == null ) {
			receiverValue = ee.selfObject;
		}
		else {
			if ( ! isNew ) {
				receiverValue = receiverExpr.eval(ee);
			}
		}
		Class<?> receiverClass;
		if ( isNew ) {
			/*
			 * instances of Cyan generic prototypes cannot be created by 'new'.
			 * Verify if the receiver is a Java class
			 */
			String rname = receiverExpr.asString();
			int indexLessThan = rname.indexOf('<');
			if ( indexLessThan >= 0 ) { genericPrototypeReceiver_AndisNew = true; }
//			String rnameStripped = rname;
			/*
			 * possibilities:
			 * 		String
			 *      java.lang.String
			 *      java.util.Set<String>
			 *      Set<String>
			 *      Set<java.lang.String>
			 *      java.util.Set<java.lang.String>
			 */
//			if ( indexLessThan >= 0 ) {
//				rnameStripped = rname.substring(0, indexLessThan);
//			}
			//receiverClass = ee.searchJavaClass_MetaJavaLang(rnameStripped);


			receiverClass = ee.searchPrototypeAsType(rname);
//			if ( receiverClass == null ) {
//				/*
//				 * search as if it were a Java class
//				 */
//				receiverClass = ee.searchJavaClass_MetaJavaLang(rnameStripped);
//				if ( receiverClass == null ) {
//					receiverClass = ee.searchPrototypeAsType(rnameStripped);
//				}
//			}
			if ( receiverClass == null ) {
				receiverClass = StatementLocalVariableDec.searchCyanGenericProtoWithDyn(ee, rname);
				cyanGenericProtoReceiver_thatDoesNotExist = true;
				if ( receiverClass == null ) {
					ee.error(this.getFirstSymbol(), "Type '" + rname + "' was not found");
					return null;
				}
			}


		}
		else {
			if ( receiverValue == null ) {
				return null;
			}
			receiverClass = receiverValue.getClass();
		}
		javaReceiver = !EvalEnv.any.isAssignableFrom(receiverClass);

		final List<Object> argList = new ArrayList<Object>();
		int size;
		if ( this.javaReceiver ) {
			if ( message.getkeywordParameterList().size() > 1 ) {
				ee.error(message.getFirstSymbol(),  "The receiver of this message passing is a "
						+ "Java object. Therefore there should be just one Cyan keyword in the message ('"
						+ message.getkeywordParameterList().get(0).getkeywordName() + "')");
			}
			size = message.getkeywordParameterList().get(0).getExprList().size();

//			List<MethodSignature> methodSignatureList = null;
//			final String methodNameWithParamNumber = message.getMethodNameWithParamNumber();
//
//			methodSignatureList = receiverExpr.getType(ee.env).searchMethodPublicPackageSuperPublicPackage(methodNameWithParamNumber, ee.env);
//			if ( methodSignatureList == null ||
//					methodSignatureList.get(0).getParameterList().size() !=
//					message.getkeywordParameterList().get(0).getExprList().size() ) {
//				ee.error(message.getFirstSymbol(), "Method '" + methodName +
//						"' not found in class '" + receiverClass.getName() + "'");
//				return null;
//
//			}
//			List<ParameterDec> paramList = methodSignatureList.get(0).getParameterList();
//			int i = 0;
			for ( final Expr paramExpr : message.getkeywordParameterList().get(0).getExprList() )  {
//
//				ParameterDec param = paramList.get(i);
//        		Object rightValue = paramExpr.eval(ee);
//        		rightValue = Statement.castCyanJava(ee, param.getClass(),
//        				rightValue, this.getFirstSymbol());

				argList.add(paramExpr.eval(ee));
        		// argList.add(rightValue);
				//++i;
			}
		}
		else {
			size = 0;
			for ( final MessageKeywordWithRealParameters sel : message.getkeywordParameterList() ) {
				size += sel.getExprList().size();
				for ( final Expr paramExpr : sel.getExprList() )  {
					argList.add(paramExpr.eval(ee));
				}
			}
		}
		final Class<?> []parameterTypes = new Class<?>[size];
		Object []objectArgList = new Object[size];
		int i = 0;
		for ( final Object arg : argList )  {
			if ( (javaReceiver && isNew && genericPrototypeReceiver_AndisNew)
					|| cyanGenericProtoReceiver_thatDoesNotExist ) {
				parameterTypes[i] = Object.class;
			}
			else {
				parameterTypes[i] = arg.getClass();
			}
			objectArgList[i] = arg;

			++i;
		}

		if ( this.javaReceiver ) {

			if ( isNew ) {
		        Object ret = null;

				try {
//					if ( newWithGenericJavaClass ) {
//						// Java replaces all generic type parameters by Object
//						int k = 0;
//						for ( @SuppressWarnings("unused") Object arg : argList ) {
//							parameterTypes[k] = Object.class;
//							++k;
//						}
//					}
					constructor = receiverClass.getConstructor(parameterTypes);
					constructor.setAccessible(true);
					Object []newObjArgList = ExprMessageSendWithKeywordsToExpr.cast(ee, objectArgList, constructor.getParameterTypes());

	                ret = constructor.newInstance(newObjArgList);
	                ee.addCreatedJavaObject(ret);
	                return ret;
				}
				catch ( NoSuchMethodException | SecurityException | InstantiationException |
						IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

					//System.out.println("not found!");
					for (Constructor<?> method : receiverClass.getConstructors()) {
						// System.out.println(method.getParameters().length > 0 ? method.getParameters()[0].getType().getName() : "");
						method.setAccessible(true);
						try {

							Object []newObjArgList = ExprMessageSendWithKeywordsToExpr.cast(ee, objectArgList, method.getParameterTypes());

							ret = method.newInstance(newObjArgList);
			                ee.addCreatedJavaObject(ret);

							return ret;
						}
						catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException | InstantiationException e1) {
						}
					}
				}
				ee.error(message.getFirstSymbol(), "Error when calling the constructor '" + receiverClass.getName() + "'");
				return null;
			}
			else {

		        Object ret = null;

				String javaMethodNameReceiverJavaObject = message.getJavaMethodNameReceiverJavaObject();
				String msgException = null;
				try {
					if ( !Character.isAlphabetic(methodName.charAt(0)) && methodName.charAt(0) != '_' ) {
						receiverValue = Statement.castJavaBasicToCyan(receiverValue);
						receiverClass = receiverValue.getClass();
						objectArgList[0] = Statement.castJavaBasicToCyan(objectArgList[0]);
						parameterTypes[0] = objectArgList[0].getClass();
						final String javaMethodName = message.getJavaMethodName();
						if ( methodName.equals("!=") || methodName.equals("==") ) {
							parameterTypes[0] = Object.class;
						}
						javaMethod = Statement.getMethod(receiverClass, javaMethodName, 1);
						if ( javaMethod == null ) {
							ee.error(this.getFirstSymbol(), "Method '" + methodName +
									"' was not found in class '" + receiverClass.getName() + "'");
						}
						//javaMethod = receiverClass.getMethod(javaMethodName, parameterTypes);
						javaMethod.setAccessible(true);
		                ret = javaMethod.invoke(receiverValue, objectArgList);
		                return ret;
					}
					else {
						javaMethod = receiverClass.getMethod(javaMethodNameReceiverJavaObject, parameterTypes);
						javaMethod.setAccessible(true);
		                ret = javaMethod.invoke(receiverValue, objectArgList);
		                return ret;
					}
				}
				catch (NoSuchMethodException | SecurityException | IllegalAccessException |
						IllegalArgumentException | InvocationTargetException e) {
					try {
						int sizeArgList = objectArgList.length;
						//System.out.println("not found!");
						for (Method method : receiverClass.getMethods()) {
							if ( javaMethodNameReceiverJavaObject.equals(method.getName()) && method.getParameterCount() == sizeArgList ) {
								// System.out.println(method.getParameters().length > 0 ? method.getParameters()[0].getType().getName() : "");
								method.setAccessible(true);
								try {

									Object []newObjArgList = null;
									if ( sizeArgList > 0 ) {
										newObjArgList = ExprMessageSendWithKeywordsToExpr.cast(ee, objectArgList, method.getParameterTypes());
										ret = method.invoke(receiverValue, newObjArgList);
									}
									else {
										ret = method.invoke(receiverValue);
									}

									return ret;
								}
								catch (IllegalAccessException
										| IllegalArgumentException e1) {

								}
								catch (	InvocationTargetException e1) {
									throw (RuntimeException ) e1.getTargetException();
								}
							}
							//System.out.println(method.getName());
						}
					}
					catch (SecurityException e1) {
						msgException = e1.getMessage();
					}

				}
				String []methodNameList = new String[receiverClass.getMethods().length];
				int ii = 0;
				for (Method method : receiverClass.getMethods()) {
					if (javaMethodNameReceiverJavaObject.equals(method.getName()) ) {
						methodNameList[ii] = method.getName() + "(";
						for ( java.lang.reflect.Parameter pp : method.getParameters() ) {
							methodNameList[ii] += pp.getType().getName() + " ";
						}
						methodNameList[ii] += ")\n";
						++ii;
					}
					//System.out.println(method.getName());
				}
				String all = "";
				for ( String every : methodNameList ) {
					if ( every == null ) { break; }
					all += every;
				}
				ee.error(message.getFirstSymbol(), "Error when calling method '" + methodName +
						"' of class '" + receiverClass.getName() + "'. The Java name is '"
						 + message.getJavaMethodNameReceiverJavaObject() +
						 (msgException != null ? "The exception message is " + msgException : "") +
						 "'. The known methods are: \n" + all);
				return null;
			}

		}
		else {
			// Cyan receiver


			if (  this.getBackquote() ) {
				ee.error(this.getFirstSymbol(), "backquotes are not supported yet");
				/*
				for ( VariableDecInterface varDec :  quotedVariableList ) {

				}
				*/
			}
			else {
				if ( isNew ) {
			        Object ret = null;

					try {
						constructor = receiverClass.getConstructor(parameterTypes);
						constructor.setAccessible(true);
		                ret = constructor.newInstance(objectArgList);
				        return ret;
					}
					catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
						for (Constructor<?> method : receiverClass.getConstructors()) {
							// System.out.println(method.getParameters().length > 0 ? method.getParameters()[0].getType().getName() : "");
							method.setAccessible(true);
							try {

								Object []newObjArgList = ExprMessageSendWithKeywordsToExpr.cast(ee, objectArgList, method.getParameterTypes());

								ret = method.newInstance(newObjArgList);
								return ret;
							}
							catch (IllegalAccessException
									| IllegalArgumentException
									| InstantiationException e1) {
							}
							catch (	InvocationTargetException e1) {
								throw (RuntimeException ) e1.getTargetException();
							}

						}
					}
					catch (	InvocationTargetException e1) {
						throw (RuntimeException ) e1.getTargetException();
					}

					ee.error(message.getFirstSymbol(), "Error when calling the constructor '" + receiverClass.getName() + "'");
					return null;
				}
				else {

			        Object ret = null;

			        /*
					List<MethodSignature> methodSignatureList = null;
					final String methodNameWithParamNumber = message.getMethodNameWithParamNumber();

					methodSignatureList = receiverExpr.getType(ee.env).searchMethodPublicPackageSuperPublicPackage(methodNameWithParamNumber, ee.env);
					if ( methodSignatureList == null ||
							methodSignatureList.get(0).getParameterList().size() !=
							message.getkeywordParameterList().get(0).getExprList().size() ||
							(!(methodSignatureList.get(0) instanceof MethodSignatureWithKeywords) &&
									!(methodSignatureList.get(0) instanceof MethodSignatureOperator) ) ) {
						ee.error(message.getFirstSymbol(), "Method '" + methodName +
								"' not found in class '" + receiverClass.getName() + "'");
						return null;

					}
					if ( methodSignatureList.get(0) instanceof MethodSignatureWithKeywords ) {
						MethodSignatureWithKeywords ms = (MethodSignatureWithKeywords ) methodSignatureList.get(0);
						int k = 0;
				        Object []newObjArgList = new Object[objectArgList.length];
						for ( ast.MethodKeywordWithParameters mk : ms.getKeywordArray() ) {
							List<ParameterDec> paramList = mk.getParameterList();
							if ( k >= objectArgList.length ) {
								ee.error(message.getFirstSymbol(), "Method '" + methodName +
										"' not found in class '" + receiverClass.getName() + "'");
							}
							for ( ParameterDec param : paramList ) {
				        		newObjArgList[k] = Statement.castCyanJava(ee, param.getType().getClass(),
				        				objectArgList[k], this.getFirstSymbol());
				        		++k;

							}
						}
						objectArgList = newObjArgList;

					}
					else {
						// operator
						MethodSignatureOperator ms = (MethodSignatureOperator ) methodSignatureList.get(0);
						ms.getParameterList().get(0).getType()
				        objectArgList[0] = Statement.castCyanJava(ee, ms.getParameterList().get(0).getType() ???,
		        				objectArgList[0], this.getFirstSymbol());
					}
					*/
					final String javaMethodName = message.getJavaMethodName();
					String methodNameInJava = null;

					if ( message.getkeywordParameterList().size() == 1 ) {
						methodNameInJava = message.getkeywordParameterList().get(0).getkeywordName();
						if ( methodNameInJava.endsWith(":") ) {
							methodNameInJava = methodNameInJava.substring(0, methodNameInJava.length()-1);
						}
					}
					final String receiverClassName = receiverClass.getName();
					try {
						if ( !Character.isAlphabetic(methodName.charAt(0)) && methodName.charAt(0) != '_' ) {

							objectArgList[0] = Statement.castJavaBasicToCyan(objectArgList[0]);
							parameterTypes[0] = objectArgList[0].getClass();
							//javaMethodName = message.getJavaMethodName();

			                try {
								javaMethod = receiverClass.getMethod(javaMethodName, parameterTypes);
								javaMethod.setAccessible(true);
								ret = javaMethod.invoke(receiverValue, objectArgList);
								return ret;
							}
							catch (IllegalAccessException
									| InvocationTargetException | NoSuchMethodException e) {
							}
						}
						ret = Statement.sendMessage(receiverValue, javaMethodName,  methodNameInJava,
								objectArgList, message.getMethodName(), this.getFirstSymbol().getI(), ee );

						return ret;
					}
					catch (SecurityException | IllegalArgumentException  e) {


						try {
							//System.out.println("not found!");
							for (Method method : receiverClass.getMethods()) {
								if ( javaMethodName.equals(method.getName()) ) {
									// System.out.println(method.getParameters().length > 0 ? method.getParameters()[0].getType().getName() : "");
									method.setAccessible(true);
									try {

										Object []newObjArgList = ExprMessageSendWithKeywordsToExpr.cast(ee, objectArgList, method.getParameterTypes());

										ret = method.invoke(receiverValue, newObjArgList);
										return ret;
									}
									catch (IllegalAccessException
											| IllegalArgumentException
											e1) {
									}
									catch (	InvocationTargetException e1) {
										throw (RuntimeException ) e1.getTargetException();
									}

								}
								//System.out.println(method.getName());
							}
						}
						catch (SecurityException e1) {
						}
						ee.error(message.getFirstSymbol(), "Error when calling method '" + methodName +
								"' of class '" + receiverClassName + "'");
						return null;
					}
				}
			}
		}
		return null;
	}


	private Expr receiverExpr;

	/**
	 * if the receiver is a Java object, this is the method to be called. Or constructor
	 */
	private Method javaMethod;
	/**
	 * Java construtor to be called
	 */
	private Constructor<?> constructor;

	/**
	 * true if the message is == or != and the receiver is a Java object
	 */
	private boolean messageIsEqEq_NotEq;

}
