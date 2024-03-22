/**
 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import error.ErrorKind;
import lexer.Symbol;
import meta.CompilationStep;
import meta.LeftHandSideKind;
import meta.Token;
import meta.VariableKind;
import meta.WrExprMessageSendWithKeywords;
import saci.Env;
import saci.NameServer;


/**
 * @author jose
 *
 */
abstract public class ExprMessageSendWithKeywords extends ExprMessageSend {

	public ExprMessageSendWithKeywords(MessageWithKeywords message, Symbol nextSymbol,
			MethodDec currentMethod ) {
		super(nextSymbol, currentMethod);
		this.message = message;
		this.doNotReturn = false;
	}

	@Override
	abstract public WrExprMessageSendWithKeywords getI();

	@Override
	public Symbol getFirstSymbol() {
//		return message.getkeywordParameterList().get(0).getkeyword();
		return message.getkeywordParameterList().get(0).getFirstSymbol();
	}

	public boolean getBackquote() {
		return message.getBackquote();
	}
	/**
	 * returns the method found  if in methodSignatureList there is a method that can accept the keywords and
	 * parameters described in this message send (the receiver of this message)
	   @param methodSignatureList
	   @param env
	   @return
	 */
	public MethodSignature checkMessageSend(List<MethodSignature> methodSignatureList, Env env) {

		if ( message instanceof MessageBinaryOperator ) {
			final MessageKeywordWithRealParameters keywordWithRealParam = message.getkeywordParameterList().get(0);
			final Type typeRealParameter = keywordWithRealParam.getExprList().get(0).getType(env);
			for ( final MethodSignature ms : methodSignatureList ) {
				if ( ms instanceof MethodSignatureOperator )  {
					final MethodSignatureOperator mso = (MethodSignatureOperator ) ms;
					if (  mso.getOptionalParameter() == null ) {
						env.error(getFirstSymbol(), "Method '" + this.message.getMethodName() + "' does not take parameters");
					}
					else if ( mso.getOptionalParameter().getType(env).isSupertypeOf(typeRealParameter, env) ) {

				        if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_6 ) {
				            MetaInfoServer.checkAssignmentPluggableTypeSystem(env, mso.getOptionalParameter().getType(env),
				            		mso.getOptionalParameter(), LeftHandSideKind.ParameterDec_LHS,
				            		typeRealParameter, keywordWithRealParam.getExprList().get(0));
				        }


						return mso;
					}
					else {
						env.error(getFirstSymbol(), "The type of the real argument, '"+  typeRealParameter.getFullName()
								+ "', of expression '" + keywordWithRealParam.getExprList().get(0).asString() +  "' is not sub-prototype of the type of the formal parameter, '" + mso.getOptionalParameter().getType().getFullName() + "' in message send with keyword '"
								+ mso.getSymbolOperator().getSymbolString() + "'");
					}
				}
			}
		}
		else  {
			final List<MessageKeywordWithRealParameters> keywordWithRealParamList = message.getkeywordParameterList();
			/**
			 * keywordWithRealParamList has the seletors and parameters that were used in the message as
			 * 'key: "one"  value: 1' in the message send
			 *        hash key: "one"  value: 1
			 * methodSignatureList contains a list of method signatures of the receiver. Each
			 * one is from a method whose name is "key:value:". The code below checks whether
			 * there is one method in methodSignatureList that accepts the real parameters.
			 */
			for ( final MethodSignature ms : methodSignatureList ) {
				if ( ! (ms instanceof MethodSignatureWithKeywords) ) {
					env.error(message.getFirstSymbol(), "Internal error in ExprMessageSendWithKeywordsToExpr::genJavaExpr: a non-grammar method", true, true);
				}
				else {
					int keywordIndex = 0;
					boolean typeErrorInParameterPassing = false;
					final MethodSignatureWithKeywords gm = (MethodSignatureWithKeywords ) ms;
					final List<MethodKeywordWithParameters> selWithFormalParamList = gm.getKeywordArray();
					for ( final MethodKeywordWithParameters selWithFormalParam : selWithFormalParamList ) {
						if ( selWithFormalParam.getParameterList().size() != keywordWithRealParamList.get(keywordIndex).getExprList().size() )
							typeErrorInParameterPassing = true;
						else {
							final MessageKeywordWithRealParameters keywordWithRealParam = keywordWithRealParamList.get(keywordIndex);
							int parameterIndex = 0;
							for (final ParameterDec paramDec : selWithFormalParam.getParameterList() ) {
								final Expr realParam = keywordWithRealParam.getExprList().get(parameterIndex);
								if ( ! paramDec.getType(env).isSupertypeOf(realParam.getType(env), env)) {
									final Type t1 = paramDec.getType(env);
									final Type t2 = realParam.getType(env);
									typeErrorInParameterPassing = true;
									break;
								}


								if ( paramDec.getVariableKind() == VariableKind.LOCAL_VARIABLE_REF ) {
									if ( realParam instanceof ExprIdentStar ) {
										final ExprIdentStar e = (ExprIdentStar ) realParam;

										final VariableDecInterface varDec = e.getVarDeclaration();

										  // env.searchVariable( ((ExprIdentStar ) realParam).getName());
										if ( varDec == null ||
												! (varDec instanceof StatementLocalVariableDec) &&
												 ! (varDec instanceof FieldDec) &&
												 ! (varDec instanceof ParameterDec) )
											env.error(realParam.getFirstSymbol(), "A local variable or field was expected because the formal parameter was declared with '&'", true, true);
										else {
											if ( varDec instanceof StatementLocalVariableDec ) {
												if ( ! varDec.isReadonly() ) {
													varDec.setRefType(true);
												}
											}
											else if ( varDec instanceof FieldDec )
												varDec.setRefType(true);
											else {
												if ( ! ((ParameterDec ) varDec).getRefType() ) {
													env.error(realParam.getFirstSymbol(), "A parameter with reference type, declared with '&', was expected because the formal parameter was declared with '&'", true, true);
												}
											}
											try {
												env.pushCheckUsePossiblyNonInitializedPrototype(true);
												realParam.calcInternalTypes(env);
											}
											finally {
												env.popCheckUsePossiblyNonInitializedPrototype();
											}

										}
								    }
									else
										env.error(realParam.getFirstSymbol(), "A local variable or field was expected because the formal parameter was declared with '&'", true, true);
								    break;
								}




								++parameterIndex;
							}
							++keywordIndex;
							if ( typeErrorInParameterPassing )
								break;

						}
					}
					if ( ! typeErrorInParameterPassing ) {
						keywordIndex = 0;
	                    for ( final MethodKeywordWithParameters selWithFormalParam : selWithFormalParamList ) {
                            final MessageKeywordWithRealParameters keywordWithRealParam = keywordWithRealParamList.get(keywordIndex);
                            int parameterIndex = 0;
                            for (final ParameterDec paramDec : selWithFormalParam.getParameterList() ) {
								final Expr realParam = keywordWithRealParam.getExprList().get(parameterIndex);
        				        if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_6 ) {
        				            MetaInfoServer.checkAssignmentPluggableTypeSystem(env, paramDec.getType(),
        				            		paramDec, LeftHandSideKind.ParameterDec_LHS,
        				            		realParam.getType(), realParam);
        				        }
                            	++parameterIndex;
                            }
                            ++keywordIndex;
	                    }


						return ms;
					}
				}

			}
		}
		return null;
	}


	/**
	   @param env
	   @param tokenFirstkeyword
	 */
	protected void calcInternalTypesWithBackquote(Env env, Token tokenFirstkeyword) {
		if ( tokenFirstkeyword != Token.IDENTCOLON ) {
			env.error(getFirstSymbol(), "The hasBackquote ` should not be followed by '?' or '?.'", true, true);
		}

		type = Type.Dyn;
		quotedVariableList = new ArrayList<VariableDecInterface>();
		   // something like   f1 `first: p1 `second: p2, p3  in which first and second should be variables
		   // of type String
		for ( final MessageKeywordWithRealParameters sel : message.getkeywordParameterList() ) {
			final String varName = sel.getkeywordNameWithoutSpecialChars();

			VariableDecInterface varDec = env.searchVariable(varName);
			if ( varDec == null ) {
				if ( env.getEnclosingObjectDec() == null ) {
					/*
					 * inside a regular prototype that is NOT inside another prototype
					 */
					varDec = env.searchVariable(varName);
				}
				else {
					/*
					 * inside an inner prototype
					 */
					if ( NameServer.isMethodNameEval(env.getCurrentMethod().getNameWithoutParamNumber()) ) {
						/*
						 * inside an 'eval' or 'eval:eval: ...' method of an inner prototype
						 */

						varDec = env.searchVariableInEvalOfInnerPrototypes(varName);
					}
					else {
						/*
						 * inside a method of an inner prototype that is not 'eval', 'eval:eval: ...'
						 */


						varDec = env.searchVariableIn_NOT_EvalOfInnerPrototypes(varName);
					}
				}
			}
			if ( varDec == null )
				env.error(true, sel.getkeyword(),
						"Variable " + varName + " was not declared",
						varName, ErrorKind.variable_was_not_declared);
			else {
				if ( ! Type.String.isSupertypeOf(varDec.getType(), env)  )
					env.error(true, sel.getkeyword(),
							"Variable " + varName +
									" should be of type String. Only String variables can follow the hasBackquote ` character",
							varName, ErrorKind.backquote_not_followed_by_a_string_variable);

				quotedVariableList.add(varDec);
			}
			if ( sel.getExprList() != null ) {
				for ( final Expr e : sel.getExprList() ) {
					try {
						env.pushCheckUsePossiblyNonInitializedPrototype(true);
						e.calcInternalTypes(env);
					}
					finally {
						env.popCheckUsePossiblyNonInitializedPrototype();
					}
				}
			}
		}

		return ;
	}


	@Override
	public boolean alwaysReturn(Env env) {
		return doNotReturn;
	}

	@Override
	public boolean statementDoReturn() {
		return doNotReturn;
	}

	public MessageWithKeywords getMessage() {
		return message;
	}

	protected MessageWithKeywords message;



	@Override
	public boolean warnIfStatement() {
		if ( message instanceof MessageBinaryOperator ) {
			return true;
		}
		return false;
	}


	/**
	 * the method signature of the method found in a search for an adequate method for this message.
	 */

	public MethodSignature getMethodSignatureForMessage() {
		return methodSignatureForMessage;
	}

	/**
	 * if this is a message send with hasBackquote, this is the list of variables in the message send. That is,
	 * if the message is
	 *           f1 `first: 0  `second: "Hi"
	 * then quotedVariableList contains references to variables first and second.
	 */
	protected List<VariableDecInterface> quotedVariableList;

	/**
	 * true if this message send do not return. It is true if it is a message send
	 * to 'throw:' or other method that do not return
	 * Method 'throw:' has been deprecated.
	 */
	protected boolean doNotReturn;


	/**
	 * the method signature of the method found in a search for an adequate method for this message.
	 */
	protected MethodSignature methodSignatureForMessage;

}
