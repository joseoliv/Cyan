/**
 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import error.ErrorKind;
import lexer.Symbol;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.IActionAssignment_cge;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrExprMessageSendWithKeywordsToSuper;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** Represents a message send to super.
 * @author José
 *
 */
public class ExprMessageSendWithKeywordsToSuper extends ExprMessageSendWithKeywords {

	/**
	 * @param message
	 */
	public ExprMessageSendWithKeywordsToSuper(Symbol superSymbol,
			MessageWithKeywords message, Symbol nextSymbol, MethodDec currentMethod) {
		super(message, nextSymbol, currentMethod);
		this.superSymbol = superSymbol;
	}

	@Override
	public WrExprMessageSendWithKeywordsToSuper getI() {
		if ( iExprMessageSendWithKeywordsToSuper == null ) {
			iExprMessageSendWithKeywordsToSuper = new WrExprMessageSendWithKeywordsToSuper(this);
		}
		return iExprMessageSendWithKeywordsToSuper;
	}

	private WrExprMessageSendWithKeywordsToSuper iExprMessageSendWithKeywordsToSuper = null;

	@Override
	public void accept(ASTVisitor visitor) {
		message.accept(visitor);
		visitor.visit(this);
	}



	public void setSuperSymbol(Symbol superSymbol) {
		this.superSymbol = superSymbol;
	}
	public Symbol getSuperSymbol() {
		return superSymbol;
	}

	public Expr getSuperobject() {
		return superobject;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.print("super ");
		message.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}


	/**
	 * MessageWithKeywords send
	 *      (expr) m: p1  k: p2
	 * should generate
	 *    tmp1 = expr;
	 *    tmp2 = p1;
	 *    tmp3 = p2;
	 *    tmp1.m_s_k(tmp2, tmp3);
	 *
	 */
	@Override
	public String genJavaExpr(PWInterface pw, Env env) {

		final List<String> stringArray = new ArrayList<String>();

		final String resultTmpVar = NameServer.nextJavaLocalVariableName();
		   /*
		    * the call is made to a private Java method in inner objects (object that have outer object != null).
		    * This method calls the method of the super class.
		    */

		boolean initCall = false;
		String javaCallToSuper = message.getJavaMethodName();
		if ( env.getCurrentObjectDec().getOuterObject() != null ) {
			javaCallToSuper = NameServer.getNamePrivateMethodForSuperclassMethod(javaCallToSuper);
			javaCallToSuper = type.getJavaName() + " " + resultTmpVar + " = " + javaCallToSuper + "( ";
		}
		else if ( env.getIsInsideInitMethod() && message.getMethodName().equals("init:") ) {
			initCall = true;
			javaCallToSuper = "super" +  "( ";
		}
		else {
			javaCallToSuper = type.getJavaName() + " " + resultTmpVar + " = " + "super." + javaCallToSuper + "( ";
		}

		/*
    	// super m: (Byte cast: n)
           CyInt tmp14565 = super._m_1(         CyByte tmp14566 = CyByte.prototype._cast_1( _n);
                 tmp14566);
		 *
		 */

		ParameterDec param;

		List<ParameterDec> paramDecList = null;
		if ( this.methodSignatureForMessage != null ) {
			paramDecList = this.methodSignatureForMessage.getParameterList();
		}

		if ( paramDecList == null ) {
			pw.printlnIdent(javaCallToSuper);
		}
		else {

			final List<ParameterDec> formalParamDecList = methodSignatureForMessage.getParameterList();

			/*
			 * if the type of the real argument is Dyn and the type of the formal parameter is not Dyn,
			 * first generate code that cast the real argument to the correct type
			 */
			final List<Expr> realParamExprList = new ArrayList<>();

			int ii = 0;
			for ( final MessageKeywordWithRealParameters keyword : message.getkeywordParameterList() ) {
				for ( final Expr e : keyword.getExprList() ) {
					realParamExprList.add(e);
					String tmpVar = null;
					if ( initCall && e instanceof ExprIdentStar ) {
						final ExprIdentStar id = (ExprIdentStar ) e;
						final VariableDecInterface varDec = id.getVarDeclaration();
						if ( varDec != null ) {
							final ParameterDec formalParamDec = formalParamDecList.get(ii);
							if ( formalParamDec.getRefType() ) {
								if ( varDec.getRefType() ) {
									tmpVar = varDec.getJavaName();
							    }
								else {
									// formal parameter is ref type and real parameter is not: internal error
									env.error(e.getFirstSymbol(), "Internal error: formal parameter is a ref type and the real parameter is not");
								}
							}
						}
					}
					if ( tmpVar == null ) {
						/*
						 * (new Object() { public int eval() { return n + s.length() + m.get(); } } ).eval();
						 *
						 *(new Object() { public CyString eval() {return CyString tmp1164 = _d._asString(); ; } }).eval()

						 */
						if ( initCall && !(e instanceof ExprLiteral) ) {
							final Tuple2<String, String> t2 = e.genJavaExprAsString(env);
							tmpVar = "(new Object() { public " + e.getType().getJavaName() + " eval() { " + t2.f2;
							tmpVar +=  " return " + t2.f1 + "; } }).eval()";
						}
						else {
							tmpVar = e.genJavaExpr(pw, env);
						}
					}
					if ( paramDecList.get(ii).getType() != Type.Dyn && e.getType() == Type.Dyn ) {
						final String otherTmpVar = NameServer.nextJavaLocalVariableName();
						pw.printlnIdent(paramDecList.get(ii).getType() + " " + otherTmpVar + " = ");
						pw.printlnIdent("if ( " + tmpVar + " instanceof " + paramDecList.get(ii).getType().getJavaName() + " ) ");
						pw.printlnIdent("    " + otherTmpVar + " = (" + paramDecList.get(ii).getType().getJavaName() + " ) " + tmpVar + ";");
						pw.printlnIdent("else");

						pw.printlnIdent("    throw new ExceptionContainer__("
								+ env.javaCodeForCastException(e, paramDecList.get(ii).getType()) + " );");

						stringArray.add(otherTmpVar);
					}
					else {
						if ( paramDecList.get(ii).getType() == Type.Any && e.getType() instanceof InterfaceDec ) {
							tmpVar = " (" + MetaHelper.AnyInJava + " ) " + tmpVar;
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

			pw.printlnIdent(javaCallToSuper);
			ii = 0;
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
						env.errorInMetaobject( (meta.CyanMetaobject) changeCyanMetaobject, this.getFirstSymbol());
					}

		        }
				pw.print(tmp);
				if ( --size > 0 )
					pw.print(", ");
				++ii;
			}
		}

		pw.println(");");

		// super.genJavaTestForReturn(pw, env);

		return resultTmpVar;
	}


	@Override
	public Symbol getFirstSymbol() {
		return superSymbol;
	}


	@Override
	public void calcInternalTypes(Env env) {


		final Token tokenFirstkeyword = message.getTokenFirstkeyword();

		message.calcInternalTypes(env);

		ObjectDec receiverType;
		final ObjectDec currentObj = env.getCurrentObjectDec();
		receiverType = currentObj.getSuperobject();
		if ( receiverType == null ) {
			env.error(true, getFirstSymbol(),
					"Prototype " + env.getCurrentPrototype().getName() + " does not have a super-prototype",
					env.getCurrentPrototype().getName(), ErrorKind.use_of_super_without_a_super_prototype);
			return ;
		}

		if ( message.getBackquote() ) {

			env.error(this.getFirstSymbol(), "backquote (`) with 'super' is illegal", true, true);
			return;

		}
		else if ( tokenFirstkeyword == Token.INTER_DOT_ID_COLON || tokenFirstkeyword == Token.INTER_ID_COLON ) {
			/*
			INTER_ID_COLON("~InterIdColon"),          // ?name:
			INTER_ID("~InterId"),                     // ?name
			INTER_DOT_ID_COLON("~InterDotIdColon"),   // ?.name:
			INTER_DOT_ID("~InterDotId"),              // ?.name
		   */
			env.error(this.getFirstSymbol(), "Dynamic message send with message starting with '?' is illegal when the 'receiver' is 'super'", true, true);
			return ;

		}
		else {

			final String methodNameWithParamNumber = message.getMethodNameWithParamNumber();
			final String methodName = message.getMethodName();
			List<MethodSignature> methodSignatureList;

			List<MethodSignature> allMethodSignatureList = new ArrayList<>();

			if ( methodName.equals("init:") ) {
				//## was "init:"
				methodSignatureList = receiverType.searchInitNewMethod(methodNameWithParamNumber);

				if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {
					env.error(true, this.getFirstSymbol(), "Method 'init' or 'init:' was not found in super-prototype",
					    methodName, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);
					return ;
				}
				if ( env.getCurrentMethod() != null ) {
					final String initName = env.getCurrentMethod().getNameWithoutParamNumber();
					if ( !initName.equals("init") && !initName.equals("init:") ) {
						env.error(this.getFirstSymbol(), "'init' and 'init:' methods can only be called inside other 'init' or 'init:' methods");
					}
					if ( env.getFunctionStack().size() > 0 ) {
						env.error(this.getFirstSymbol(), "'init' and 'init:' messages cannot be sent inside anonymous functions");
					}
				}
				else {
					env.error(this.getFirstSymbol(), "'init' and 'init:' messages cannot be sent outside a method 'init' or 'init:'");
				}

				/*
				 * calls to init: cannot be inside an expression as in
				 *     (super init: 0) println;
				 */
				if ( env.peekCode() != this ) {
					env.error(this.getFirstSymbol(),  "Calls to 'super init: args' cannot be inside another expression. That is, the return value should not be used for anything");
				}

				if ( ! env.getFirstMethodStatement() ) {
					env.error(this.getFirstSymbol(),  "Calls 'super init: params' should be the first statement of an 'init' or 'init:' method");
				}
			}
			else {
				methodSignatureList = receiverType.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(methodNameWithParamNumber, env);

				List<Prototype> superList = receiverType.get_this_and_all_superPrototypes();
				for ( Prototype current : superList ) {
					List<MethodSignature> currentMSList = current.searchMethodProtectedPublicPackage(methodName, env);
					if ( currentMSList != null ) {
						allMethodSignatureList.addAll(currentMSList);
					}
				}

			}


			if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {
				env.error(true, getFirstSymbol(),
						"Method " + methodName + " was not found in prototype " + receiverType.getName() + " or its super-prototypes",
						methodName, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes, "receiver = " + receiverType.getName());

			}
			else {
				methodSignatureForMessage = checkMessageSend(methodSignatureList, env);


				if ( methodSignatureForMessage == null) {
					env.error(true, getFirstSymbol(),
							"Method " + methodNameWithParamNumber + " was not found in prototype " + receiverType.getName() +
									" or its super-prototypes",
							methodName, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes, "receiver = " + receiverType.getName());
					checkMessageSend(methodSignatureList, env);
				}
				else {

					//if ( methodName.equals("new:") || methodName.equals("init:") ) {}

					final MethodDec aMethod = methodSignatureForMessage.getMethod();
					if ( aMethod != null ) {
						if ( aMethod.getVisibility() == Token.PRIVATE  ) {
							if ( aMethod.getDeclaringObject() != env.getCurrentOuterObjectDec() ) {
								env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessage.getFullName(env) +
										"' is private. It can only be called "
										+ "inside prototype '" + aMethod.getDeclaringObject().getFullName() + "'");
							}

						}
						else if ( aMethod.getVisibility() == Token.PACKAGE ) {
							if ( aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage() !=
									env.getCurrentObjectDec().getCompilationUnit().getCyanPackage() ) {
								env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessage.getFullName(env) +
										"'  has 'package' visibility. It can only be called "
										+ "inside package '" + aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage().getName() + "'");
							}
						}
						else if ( aMethod.getVisibility() == Token.PROTECTED ) {
							if ( !aMethod.getDeclaringObject().isSupertypeOf(env.getCurrentObjectDec(), env)  ) {
								env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessage.getFullName(env) +
										"'  has 'package' visibility. It can only be called "
										+ "in subprototypes of '" +
										aMethod.getDeclaringObject().getName() + "'", true, true);
							}
						}

					}




					if ( methodSignatureForMessage.getMethod().isAbstract() )
						env.error(this.getFirstSymbol(), "'super' used to call an abstract method");

//					MetaInfoServer.checkMessageSendWithMethodMetaobject(methodSignatureList,
//							receiverType,
//							null,
//							ExprReceiverKind.SUPER_R, env, this.message.getFirstSymbol());

					type = methodSignatureForMessage.getReturnType(env);

					final ExprSelf exprSelf = new ExprSelf(superSymbol,
							env.getCurrentPrototype(), currentMethod);
					if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_9 )  {
						MetaInfoServer.checkMessageSendWithMethodMetaobject(allMethodSignatureList, receiverType, exprSelf,
								ExprReceiverKind.SUPER_R, message, env, this.message.getFirstSymbol());
					}

					if ( env.getProject().getCompilerManager().getCompilationStep().ordinal() < CompilationStep.step_7.ordinal() ) {
						type = MetaInfoServer.replaceMessageSendIfAsked(allMethodSignatureList,
								this,
								env, this.getFirstSymbol(), type);
					}

				}

			}
		}
		doNotReturn = message.getMethodNameWithParamNumber().equals("throw:1");
		super.calcInternalTypes(env);

	}


	/**
	 * the superobject that owns the method that
	 * should be called.
	 */
	private Expr superobject;
	/**
	 * symbol representing "super"
	 */
	private Symbol superSymbol;

}
