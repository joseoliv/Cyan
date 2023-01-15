package ast;

import java.util.ArrayList;
import java.util.List;
import error.ErrorKind;
import lexer.Symbol;
import meta.CompilationStep;
import meta.ExprReceiverKind;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprMessageSendUnaryChainToSuper;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

public class ExprMessageSendUnaryChainToSuper extends ExprMessageSendUnaryChain {

	public ExprMessageSendUnaryChainToSuper(Symbol superSymbol, Symbol nextSymbol,
			MethodDec currentMethod) {
		super(nextSymbol, currentMethod);
		this.superSymbol = superSymbol;

	}

	public ExprMessageSendUnaryChainToSuper(Symbol superSymbol, MethodDec currentMethod) {
		super(currentMethod);
		this.superSymbol = superSymbol;

	}

	@Override
	public WrExprMessageSendUnaryChainToSuper getI() {
		if ( iExprMessageSendUnaryChainToSuper == null ) {
			iExprMessageSendUnaryChainToSuper = new WrExprMessageSendUnaryChainToSuper(this);
		}
		return iExprMessageSendUnaryChainToSuper;
	}

	private WrExprMessageSendUnaryChainToSuper iExprMessageSendUnaryChainToSuper = null;

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.print("super ");
		if ( hasBackquote )
			pw.print("`");

		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			pw.print(cyanEnv.formalGenericParamToRealParam(unarySymbol.getSymbolString()));
		}
		else {
			pw.print(unarySymbol.getSymbolString());
		}


	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {

		if ( hasBackquote ) {
			env.error(this.getFirstSymbol(), "code generation for hasBackquote with 'super' is illegal", true, true);
		}
		String tmp;

		String javaCallToSuper = MetaHelper.getJavaNameOfkeyword(unarySymbol.getSymbolString());
		if ( env.getCurrentObjectDec().getOuterObject() != null ) {
			javaCallToSuper = NameServer.getNamePrivateMethodForSuperclassMethod(javaCallToSuper);
			tmp = NameServer.nextJavaLocalVariableName();
			pw.printlnIdent(type.getJavaName() + " " + tmp + " = " +
			    javaCallToSuper + "()");
		}
		else if ( env.getIsInsideInitMethod() && unarySymbol.getSymbolString().equals("init") ) {
			tmp = "";
			pw.printlnIdent("super();");
		}
		else {
			if ( this.getMethodSignatureForMessageSend().getReturnType(env) == Type.Nil ) {
				// receiver is a unary method that returns Nil
				javaCallToSuper = "super." + javaCallToSuper + "();";
				pw.printlnIdent(javaCallToSuper);

				tmp = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent(type.getJavaName() + " " + tmp + " = _Nil.prototype;");
			}
			else {
				javaCallToSuper = "super." + javaCallToSuper;
				tmp = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent(type.getJavaName() + " " + tmp + " = " +
				    javaCallToSuper + "();");
			}

		}
		return tmp;
	}


	/*
	@Override
	public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {

		if ( hasBackquote ) {
			env.error(this.getFirstSymbol(), "code generation for hasBackquote with 'super' has not been implemented", true);
		}

		if ( env.getCurrentObjectDec().getOuterObject() != null ) {
			pw.printIdent("__super_");
		}
		pw.print(NameServer.getJavaNameOfkeyword(unarySymbol.getSymbolString()) + "()");
	}
	*/

	@Override
	public void calcInternalTypes(Env env) {
		String methodName;
		List<MethodSignature> methodSignatureList = null;

		Type receiverType;

		Token tokenFirstkeyword = unarySymbol.token;



		ObjectDec currentObj = env.getCurrentObjectDec();
		ObjectDec superObjectDec;
		receiverType = superObjectDec = currentObj.getSuperobject();
		if ( receiverType == null ) {
			env.error(true, getFirstSymbol(),
					"Prototype " + env.getCurrentPrototype().getName() + " does not have a super-prototype",
					env.getCurrentPrototype().getName(), ErrorKind.use_of_super_without_a_super_prototype);
			return ;
		}
		methodName = unarySymbol.getSymbolString();

		MethodDec currentMethod1 = env.getCurrentMethod();

		if ( currentMethod1 != null ) {
			String currentMethodName = currentMethod1.getNameWithoutParamNumber();
			if ( currentMethodName.equals("init") || currentMethodName.equals("init:") ) {
				/**
				 * inside an init or init: method it is illegal to access 'self' using 'super'.
				 * The only message send allowed is 'super init' or 'super init:' as the first statement.
				 */
				if ( ! methodName.equals("init") && ! methodName.equals("init:") ) {
						env.error(this.getFirstSymbol(),  "Message send to 'super' inside an 'init' or 'init:' method and the method to be called "
								+ " is not 'init' or 'init:'");
				}
			}
			if (  currentMethodName.equals("initShared") ) {
				/**
				 * inside an initShared method it is illegal to access 'self' using 'super'.
				 */
				env.error(this.getFirstSymbol(),  "Message send to 'super' inside an 'initShared' method");
			}

		}


		// if `  was used, there is no search for the method at compile-time.
		if ( hasBackquote ) {
			env.error(this.getFirstSymbol(), "hasBackquote (`) with 'super' is illegal", true, true);
			return;
		}
		else if ( tokenFirstkeyword == Token.INTER_DOT_ID || tokenFirstkeyword == Token.INTER_ID ) {
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

			List<MethodSignature> allMethodSignatureList = new ArrayList<>();

			methodSignatureList = receiverType.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(methodName, env);

			List<Prototype> superList = currentObj.get_this_and_all_superPrototypes();

			for ( Prototype current : superList ) {
				List<MethodSignature> currentMSList = current.searchMethodProtectedPublicPackage(methodName, env);
				if ( currentMSList != null ) {
					allMethodSignatureList.addAll(currentMSList);
				}
			}


			if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {
				env.error(true, getFirstSymbol(), "Method " + methodName + " was not found in prototype " + receiverType.getName() +
						        " or its super-prototypes",
						methodName, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);

			}
			else {
				if ( methodName.equals("new") ) {
					env.error(true, unarySymbol, "Message '" + methodName + "'  can only be sent to prototypes",
							methodName, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);
					return ;
				}
				if ( methodName.equals("init") ) {
					if ( methodSignatureList.get(0).getMethod().getDeclaringObject() != superObjectDec ) {
						env.error(true, unarySymbol, "'init' and 'init:' methods can only be called by the direct sub-prototypes. " +
					       "The 'init' or 'init:' method was found in prototype '" +
					       methodSignatureList.get(0).getMethod().getDeclaringObject().getFullName() +
									 "'",
								methodName, ErrorKind.method_was_not_found_in_prototype_or_super_prototypes);
						return ;
					}
					MethodSignature ms = methodSignatureList.get(0);
					methodSignatureList.clear();
					methodSignatureList.add(ms);  // only the first one, of the super-prototype, counts.

					if ( env.getCurrentMethod() != null ) {
						String initName = env.getCurrentMethod().getNameWithoutParamNumber();
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
					if ( env.peekCode() != this ) {
						env.error(this.getFirstSymbol(),  "Calls 'super init' cannot be inside another expression. That is, the return value should not be used for anything");
					}
					if ( ! env.getFirstMethodStatement() ) {
						env.error(this.getFirstSymbol(),  "Calls 'super init' should be the first statement of an 'init' or 'init:' method");
					}

				}
				methodSignatureForMessageSend = methodSignatureList.get(0);

				methodSignatureForMessageSend.calcInterfaceTypes(env);
				if ( methodSignatureForMessageSend.getMethod().isAbstract() )
					env.error(this.getFirstSymbol(), "'super' used to call an abstract method");
				receiverType = methodSignatureForMessageSend.getReturnType(env);


				MethodDec aMethod = methodSignatureForMessageSend.getMethod();
				if ( aMethod != null ) {
					if ( aMethod.getShared() ) {
						env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessageSend.getFullName(env) +
								"' is shared. It can only be called "
								+ "if the receiver is a prototype. The receiver is 'super'");
					}

					if ( aMethod.getVisibility() == Token.PRIVATE  ) {
						if ( aMethod.getDeclaringObject() != env.getCurrentOuterObjectDec() ) {
							env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessageSend.getFullName(env) +
									"' is private. It can only be called "
									+ "inside prototype '" + aMethod.getDeclaringObject().getFullName() + "'");
						}

					}
					else if ( aMethod.getVisibility() == Token.PACKAGE ) {
						if ( aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage() !=
								env.getCurrentObjectDec().getCompilationUnit().getCyanPackage() ) {
							env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessageSend.getFullName(env) +
									"'  has 'package' visibility. It can only be called "
									+ "inside package '" + aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage().getName() + "'");
						}
					}
					else if ( aMethod.getVisibility() == Token.PROTECTED ) {
						if ( !aMethod.getDeclaringObject().isSupertypeOf(env.getCurrentObjectDec(), env)  ) {
							env.error(this.getFirstSymbol(), "Method '" + methodSignatureForMessageSend.getFullName(env) +
									"'  has 'package' visibility. It can only be called "
									+ "in subprototypes of '" +
									aMethod.getDeclaringObject().getName() + "'", true, true);
						}
					}
				}


			}

//			MetaInfoServer.checkMessageSendWithMethodMetaobject(methodSignatureList, currentObj, null,
//					ExprReceiverKind.SUPER_R, env, unarySymbol);
			type = receiverType;


			ExprSelf exprSelf = new ExprSelf(superSymbol, env.getCurrentPrototype(), currentMethod1);

			if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_9 ) {
				MetaInfoServer.checkMessageSendWithMethodMetaobject(allMethodSignatureList, receiverType,
						exprSelf,
						ExprReceiverKind.SUPER_R, env, unarySymbol);
			}


			if ( env.getProject().getCompilerManager().getCompilationStep().ordinal() < CompilationStep.step_7.ordinal()
					&& methodSignatureList != null
					) {



				type = MetaInfoServer.replaceMessageSendIfAsked(allMethodSignatureList,
						this,
						env, this.getFirstSymbol(), type);


//				type = MetaInfoServer.replaceMessageSendIfAsked(methodSignatureList.get(0),
//						this,
//						env, this.getFirstSymbol(), type);
			}





		}
		super.calcInternalTypes(env);

	}


	@Override
	public Symbol getFirstSymbol() {
		return superSymbol;
	}

	public Expr getReceiver() {
		return null;
	}



	private Symbol superSymbol;




}
