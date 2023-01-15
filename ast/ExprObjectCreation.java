package ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAssignment_cge;
import meta.IdentStarKind;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.VariableKind;
import meta.WrAnnotation;
import meta.WrExprObjectCreation;
import saci.Compiler;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;


/**
 * Represents the creation of an object using ( and ) as in
 *     Person("Lívia")
 *     Manager("Carolina", 10000)
 *     Stack<Int>(10)
 *     Sum(v);
 *
 * @author José
 *
 */

public class ExprObjectCreation extends Expr {


	public ExprObjectCreation(Expr prototypeType,
			List<Expr> parameterList, Symbol rightParSymbol, MethodDec method) {
		super(method);
		this.prototype = prototypeType;
		this.parameterList = parameterList;
		this.rightParSymbol = rightParSymbol;
	}

	@Override
	public WrExprObjectCreation getI() {
		// TODO Auto-generated method stub
		return new WrExprObjectCreation(this);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		this.prototype.accept(visitor);
		if ( parameterList != null ) {
			for ( final Expr e : this.parameterList ) {
				e.accept(visitor);
			}
		}
		visitor.visit(this);
	}

	@Override
	@SuppressWarnings("unused")
	public Object eval(EvalEnv ee) {
		ee.error(this.getFirstSymbol(), "Creating of an object of prototype P using the syntax P(... args) is not allowed yet");
		return null;
	}


	@Override
	public boolean isNRE(Env env) {
		for ( final Expr e : parameterList ) {
			if ( !e.isNRE(env) )
				return false;
		}
		return true;
	}

	@Override
	public boolean isNREForInitShared(Env env) {
		final String name = MetaHelper.removeSpaces(prototype.asString());
		final Prototype pu = env.getProject().getCyanLangPackage().searchPublicNonGenericPrototype(name);
		if ( pu == null ) {
			return false;
		}
		for ( final Expr e : parameterList ) {
			if ( !e.isNREForInitShared(env) )
				return false;
		}
		return true;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		prototype.genCyan(pw, false, cyanEnv, genFunctions);
		pw.print("(");
		int size =  this.parameterList.size();
		for ( final Expr e : this.parameterList ) {
			e.genCyan(pw, false, cyanEnv, genFunctions);
			--size;
			if ( size > 0 )
				pw.print(", ");
		}
		pw.print(")");
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {

		final String tmpVar = NameServer.nextJavaLocalVariableName();


		if ( type instanceof TypeJavaRef ) {
			// creation of an object of Java


			final String []varTmpArray = new String[parameterList.size()];
			int i = 0;
			for ( final Expr paramExpr : parameterList ) {
				varTmpArray[i] = paramExpr.genJavaExpr(pw, env);
				final Type exprType = paramExpr.getType();


				final String exprParamTypeName = exprType.getName();
				final Class<?> paramType = constructor.getParameters()[i].getType();

				final String javaTypeName = paramType.getSimpleName();
				String possibleCyanNameFromWrapper = null;
				final String possibleCyanName_ofExprParam_LowerCase = Character.toLowerCase(exprParamTypeName.charAt(0)) + exprParamTypeName.substring(1);

				/*
				String paramName = paramType.getName();
				String paramCanonName = paramType.getCanonicalName();
				String paramTypeName = paramType.getTypeName();
				String paramPackage = paramType.getPackage().getName();
				*/

	   			if ( javaTypeName.equals(possibleCyanName_ofExprParam_LowerCase) ||
						javaTypeName.equals("String") && javaTypeName.equals("String") ||
						// NameServer.javaWrapperClassToCyanName(javaTypeName).equals(cyanTypeName)
						(possibleCyanNameFromWrapper = NameServer.javaWrapperClassToCyanName(javaTypeName)) != null &&
						possibleCyanNameFromWrapper.equals(exprParamTypeName)

						) {

					  // something like  "int = Int" or "Integer = Int"
	   				if ( ! javaTypeName.endsWith("[]") ) {
		   				varTmpArray[i] = varTmpArray[i] + "." + NameServer.getFieldBasicType(exprParamTypeName);
	   				}
				}
				else {
					boolean issueError = true;
					if ( exprType instanceof TypeJavaRef ) {
						final Class<?> javaRefExprType = ((TypeJavaRef) exprType).getClassLoad(env, paramExpr.getFirstSymbol());
						if ( paramType.isAssignableFrom(javaRefExprType) ) {
							issueError = false;
						}
					}
					if ( issueError ) {
						env.error(paramExpr.getFirstSymbol(),
								"Cannot cast this Cyan value of type '" + exprType.getFullName() + "' to "
								+ "Java, type '" + constructor.getParameters()[i].getType().getCanonicalName() + "'");
					}
				}

	   			/*
				if ( exprType.getInsideType() instanceof Prototype ) {
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
			final String resultTmpVar = NameServer.nextJavaLocalVariableName();
			pw.printIdent( type.getJavaName() + " " + resultTmpVar + " = ");
			pw.println( "new " + type.getJavaName() + "(" );
			int size = varTmpArray.length;
			for ( final String p : varTmpArray ) {
				pw.print(p);
				if ( --size > 0 ) {
					pw.print(", ");
				}
			}
			pw.println( "); " );
			return resultTmpVar;

		}

		//pw.printIdent(tmpVar + " = new " + NameServer.getJavaNameGenericPrototype(prototype.ifPrototypeReturnsItsName()) + "(");

		final List<ParameterDec> formalParamList = this.initMethodSignature.getParameterList();



		//***************************


		/*
		 * if the type of the real argument is Dyn and the type of the formal parameter is not Dyn,
		 * first generate code that cast the real argument to the correct type
		 */
		final List<String> stringArray = new ArrayList<String>();

		int ii = 0;
		final List<Expr> realParamExprList = new ArrayList<>();
		for ( final Expr e : parameterList ) {
			String strExpr = null;
			final ParameterDec formalParam = formalParamList.get(ii);

			realParamExprList.add(e);


			String tmpVar1 = null;


			if ( e instanceof ExprIdentStar || e instanceof ExprSelfPeriodIdent ) {
				if ( e instanceof ExprIdentStar ) {
					final VariableDecInterface rightSideVar = env.searchVariable( ((ExprIdentStar ) e).getName());
					if ( rightSideVar != null ) {
						if  ( rightSideVar.getRefType() && ! formalParam.getRefType() )
							strExpr = rightSideVar.getJavaName() + ".elem";
						else
							strExpr = rightSideVar.getJavaName();
					}
				}
				else {
					final FieldDec rightSideVar = env.searchField(
							((ExprSelfPeriodIdent ) e).getIdentSymbol().getSymbolString());
					if ( rightSideVar != null ) {
						if ( rightSideVar.getRefType() && ! formalParam.getRefType() )
							strExpr = rightSideVar.getJavaName() + ".elem";
						else
							strExpr = rightSideVar.getJavaName();
					}
				}
			}
			if ( strExpr == null ) {
				if ( e.getType() instanceof TypeJavaRef || formalParamList.get(ii).getType() instanceof TypeJavaRef ) {
					tmpVar1 = e.genJavaExpr(pw, env);
					tmpVar1 = Type.genJavaExpr_CastJavaCyan(env, tmpVar1, e.getType(),
							formalParamList.get(ii).getType(), formalParamList.get(ii).getFirstSymbol());

				}
				else {
					tmpVar1 = e.genJavaExpr(pw, env);
				}
			}
			else
				tmpVar1 = strExpr;



			if ( formalParamList.get(ii).getType() != Type.Dyn && e.getType() == Type.Dyn ) {
				final String otherTmpVar = NameServer.nextJavaLocalVariableName();
				pw.printlnIdent(parameterList.get(ii).getType().getJavaName() + " " + otherTmpVar + ";");
				pw.printlnIdent("if ( " + tmpVar1 + " instanceof " + formalParamList.get(ii).getType().getJavaName() + " ) ");
				pw.printlnIdent("    " + otherTmpVar + " = (" + formalParamList.get(ii).getType().getJavaName() + " ) " + tmpVar1 + ";");
				pw.printlnIdent("else");

				pw.printlnIdent("    throw new ExceptionContainer__("
						+ env.javaCodeForCastException(e, formalParamList.get(ii).getType()) + " );");

				stringArray.add(otherTmpVar);
			}
			else
				stringArray.add( tmpVar1 );
			++ii;

			/*
			tmpVarList[i] = strExpr;
			++i;
			 *
			 */

		}

		/*
		 * A metaobject attached to the type of the formal parameter may demand that the real argument be
		 * changed. The new argument is the return of method  changeRightHandSideTo
		 */
		final StringBuffer paramPassing = new StringBuffer();

		ii = 0;
		ParameterDec param;
		int size1 = stringArray.size();
		for ( String tmp : stringArray ) {
			param = formalParamList.get(ii);
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
							meta.GetHiddenItem.getHiddenSymbol(annotation.getFirstSymbol()), e.getMessage() + " " + NameServer.messageClassNotFoundException);
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
			if ( --size1 > 0 )
				paramPassing.append(", ");
			++ii;
		}

		pw.printIdent( type.getJavaName() + " " + tmpVar + " = ");
		pw.println( "new " + type.getJavaName() + "( " +
					paramPassing.toString()  + ");");

		//***************************

		/*
		pw.printIdent(type.getJavaName() + " " + tmpVar + " = new " + type.getJavaName() + "(");
		for ( String s : tmpVarList ) {
			pw.print(s);
			if ( --size1 > 0 )
			    pw.print(", ");
		}

		pw.println(");");
		*/
		return tmpVar;
	}

	@Override
	public Symbol getFirstSymbol() {
		return prototype.getFirstSymbol();
	}


	public void setRightParSymbol(Symbol rightParSymbol) {
		this.rightParSymbol = rightParSymbol;
	}


	public Symbol getRightParSymbol() {
		return rightParSymbol;
	}


	@Override
	public void calcInternalTypes(Env env) {


		for ( final Expr realParameter : parameterList ) {
			realParameter.calcInternalTypes(env);
		}

		try {
			env.pushCheckUsePossiblyNonInitializedPrototype(false);
			if ( prototype.getType(env) == null ) {
				prototype.calcInternalTypes(env);
			}
		}
		finally {
			env.popCheckUsePossiblyNonInitializedPrototype();
		}



		/*
		 * it is a profound mystery why method searchPackagePrototype appears here.
		 * It should be env.searchVisiblePrototype and env.searchVisibleJavaClass
		 */
		Type other = type;
		if ( prototype.getType() == null ) {
			if ( !(prototype instanceof ast.ExprGenericPrototypeInstantiation) ) {
				if ( prototype.getType(env) == null ) {
					prototype.calcInternalTypes(env);
				}

				NameServer.println("in ExprObjectCreation: not gen. proto");
			}
			else {
				type = Compiler.searchCreateTypeFromExpr(this.getFirstSymbol(),
						env.getCurrentCompilationUnit(), env,
						prototype);
			}
		}
		else {
			other = prototype.getType();
		}
		type = env.searchPackagePrototype(prototype.asString(), prototype.getFirstSymbol());
		if ( other != type ) {
			NameServer.println("other != type in ExprObjectCreation");
		}

		// prototype.ifRepresentsTypeReturnsType(env);
		/*
		if ( pu != type ) {
			env.error(prototype.getFirstSymbol(), "Internal error in ExprObjectCreation");
		}
		*/
		//type = prototype.getType();

		ExprIdentStar eis;
		if ( prototype instanceof ExprIdentStar ) {
			eis = (ExprIdentStar) prototype;
			final String fullName = prototype.asString();
			if ( env.searchVisiblePrototype(fullName, prototype.getFirstSymbol(), true) == null) {
				if ( env.searchVisibleJavaClass(fullName) == null) {
					env.error(prototype.getFirstSymbol(),  "Prototype '" + fullName + "' was not found");
				}
			}
		}
		else if ( prototype instanceof ExprGenericPrototypeInstantiation )  {
			eis = ((ExprGenericPrototypeInstantiation ) prototype ).getTypeIdent();
		}
		else {
			env.error(prototype.getFirstSymbol(), "A prototype expected in an expression 'P(...)' or 'P()'. Found " + prototype.asString());
			return ;
		}
		  // first character should be in upper case
		if ( ! Character.isUpperCase( eis.getIdentSymbolArray().get(eis.getIdentSymbolArray().size()-1).getSymbolString().charAt(0)) ) {
			env.error(prototype.getFirstSymbol(),  "A prototype was expected before '()' or '(...)'. Found " + prototype.asString());
		}

		/*
		String protoName = "";
		String packageName = "";
		List<Symbol> symList = packagePrototype.getIdentSymbolArray();
		int size = packagePrototype.getIdentSymbolArray().size();
		int i = 0;
		while ( i < size - 1 ) {
			packageName += symList.get(i).getSymbolString();
			++i;
		}
		protoName = symList.get(size-1).getSymbolString();
		Prototype pu;
		if ( packageName.length() == 0 ) {
			pu = env.searchVisiblePrototype(protoName, prototype.getFirstSymbol(), );
		}

		if ( protoName == null ) {
			env.error(prototype.getFirstSymbol(),  "A prototype was expected before '()' or '(...)'. Found " + prototype.asString());
			return ;
		}
		*/
		if ( type == null ) {
			env.error(prototype.getFirstSymbol(), "Prototype " + prototype.ifPrototypeReturnsItsName(env) + " was not found", true, true);
		}
		else {
			if ( ! (type instanceof ObjectDec ) ) {
				if ( type instanceof TypeJavaRef ) {
					final TypeJavaRef javaClass = (TypeJavaRef ) type;
					final Class<?> []parameterTypes = new Class<?>[parameterList.size()];
					int i = 0;
					for ( final Expr paramExpr : parameterList ) {
						final Type exprType = paramExpr.getType();
						if ( exprType.getInsideType() instanceof Prototype ) {
							final String puName = ((Prototype) exprType).getName();
							parameterTypes[i] = NameServer.getJavaClassFromCyanName(puName);
						}
						else if ( exprType instanceof TypeJava ) {
							if ( exprType instanceof TypeJavaRefArray ) {
								parameterTypes[i] = ((TypeJavaRefArray ) exprType).getClassLoad(env, this.getFirstSymbol());
							}
							else if ( exprType instanceof TypeJavaClass ) {
								parameterTypes[i] = ((TypeJavaClass) exprType).getClassLoad(env, this.getFirstSymbol());
							}
							else {
								env.error(paramExpr.getFirstSymbol(), "A parameter to a call to a Java constructor should be "
										+ "either a Java object or an object of a basic type in Cyan (Int, Char, ...)");
							}
						}
						else {
							env.error(paramExpr.getFirstSymbol(), "A parameter to a call to a Java constructor should be "
									+ "either a Java object or an object of a basic type in Cyan (Int, Char, ...)");
						}
						++i;
					}



					// a java class
					try {
						constructor = javaClass.getClassLoad(env, this.getFirstSymbol()).getConstructor(parameterTypes);
						type = javaClass;
						checkPublic_New(env);

						return ;
					}
					catch (NoSuchMethodException
							| SecurityException e) {

						/*
						 * if the execution is here, the method has not the types of the real parameters.
						 * If the message send is correct, some of the method parameters are Formal Parameters
						 * of a generic prototype. These should be replaced by 'Object.class' in the search
						 * for a method in 'Class<?>.getMethod(...)'. We could not find any other way of
						 * doing that. All combinations of replacement of the types of real parameters by 'Object.class'
						 * should be tried.
						 */

						boolean foundMethod = false;
						final Class<?> [][] allComb = ExprMessageSend.allCombinations2(parameterTypes);
						for ( final Class<?>[] classArray : allComb ) {
							try {
								constructor = javaClass.getClassLoad(env, this.getFirstSymbol()).getConstructor(classArray);
								type = javaClass;
								foundMethod = true;
								checkPublic_New(env);
								break;
							}
							catch ( NoSuchMethodException
									| SecurityException eee) {
							}

						}
						if ( ! foundMethod ) {
							env.error(this.getFirstSymbol(), "A Java constructor for this message send was not found "
									+ "or there was a security exception (class SecurityException)");
						}
					}

					return ;
				}
				else {
					String protoName;
					if ( prototype instanceof ExprIdentStar ) {
						protoName = ((ExprIdentStar) prototype).getName();
					}
					else if ( prototype instanceof ExprGenericPrototypeInstantiation ) {
						protoName = ((ExprGenericPrototypeInstantiation ) prototype).getName();
					}
					else
						protoName = prototype.getFirstSymbol().getSymbolString();
					env.error(prototype.getFirstSymbol(),  protoName + " should be a prototype", true, true);
					return ;
				}
			}
			else {

			}
			final ObjectDec proto = (ObjectDec ) type;
			if ( parameterList.size() == 0 ) {
				// search for init
				List<MethodSignature> methodSignatureList = proto.searchInitNewMethod("init");
				if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {
					methodSignatureList = proto.searchInitNewMethod("new");
					if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {
						env.error(getFirstSymbol(), "Since this object creation does not take parameters, prototype " + proto.getName() +
								" should have an 'init'  method. It does not", true, true);
						return ;
					}
				}
				this.initMethodSignature = methodSignatureList.get(0);
				final MethodDec aMethod = this.initMethodSignature.getMethod();

				if ( aMethod.getVisibility() == Token.PRIVATE ) {
					if ( aMethod.getDeclaringObject() != env.getCurrentOuterObjectDec() ) {
						env.error(getFirstSymbol(), "Method 'init' of '" + aMethod.getDeclaringObject().getFullName() +
								"' is private or it does not exist. ", true, true);
					}
				}
				else if ( aMethod.getVisibility() == Token.PACKAGE ) {
					if ( aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage() !=
							env.getCurrentObjectDec().getCompilationUnit().getCyanPackage() ) {
						env.error(getFirstSymbol(), "Method 'init' of '" + aMethod.getDeclaringObject().getFullName() +
								"' has 'package' visibility. It can only be called "
								+ "inside package '" +
								aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage().getName() + "'", true, true);
					}
				}
				else if ( aMethod.getVisibility() == Token.PROTECTED ) {
					if ( ! aMethod.getDeclaringObject().isSupertypeOf(env.getCurrentObjectDec(), env)  ) {
						env.error(getFirstSymbol(), "Method 'init' of '" + aMethod.getDeclaringObject().getFullName() +
								"' has 'protected' visibility. It can only be called "
								+ "in subprototypes of '" +
								aMethod.getDeclaringObject().getName() + "'", true, true);
					}
				}

			}
			else {
				// search for init:
				int sizeParamList = this.parameterList.size();
				List<MethodSignature> methodSignatureList = proto.searchInitNewMethod("new:" + sizeParamList);
				if ( methodSignatureList == null || methodSignatureList.size() == 0 )
					methodSignatureList = proto.searchInitNewMethod("init:" + sizeParamList);
				boolean foundMethod = false;
				for ( final MethodSignature methodSignature : methodSignatureList ) {
					boolean typeError = false;
					if ( methodSignature instanceof MethodSignatureWithKeywords ) {
						final List<MethodKeywordWithParameters> keywordWithParameters = ((MethodSignatureWithKeywords) methodSignature).getKeywordArray();
						  // keywordWithParameters.size() == 1
						if ( keywordWithParameters.get(0).getParameterList().size() == parameterList.size() ) {
							int indexSignature = 0;
							for ( final ParameterDec parameter : keywordWithParameters.get(0).getParameterList() ) {
								final Expr realParameter = parameterList.get(indexSignature);
								if ( ! parameter.getType().isSupertypeOf(
										realParameter.getType(env), env) ) {
									typeError = true;
									parameter.getType().isSupertypeOf(parameterList.get(indexSignature).getType(env), env);
									break;
								}
								/*
								if ( parameter.getRefType() && realParameter instanceof ExprIdentStar ) {
									ExprIdentStar e = (ExprIdentStar ) realParameter;
									if ( e.getIdentSymbolArray().size() == 1 ) {
										e.setRefType(true);
									}
								}
								*/
								++indexSignature;
							}
						}
						else
							typeError = true;
					}
					else
						typeError = true;
					if ( ! typeError ) {
						foundMethod = true;
						int indexSignature = 0;
						this.initMethodSignature =  methodSignature;
						final MethodDec aMethod = this.initMethodSignature.getMethod();
						if ( aMethod.getVisibility() == Token.PRIVATE ) {
							if ( aMethod.getDeclaringObject() != env.getCurrentOuterObjectDec() ) {
								env.error(getFirstSymbol(), "Method '" + initMethodSignature.getFullName(env) +
										"' of '" + initMethodSignature.getMethod().getDeclaringObject().getFullName() +
										"' is private or it does not exist. ", true, true);
							}
						}
						else if ( aMethod.getVisibility() == Token.PACKAGE ) {
							if ( aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage() !=
									env.getCurrentObjectDec().getCompilationUnit().getCyanPackage() ) {
								env.error(getFirstSymbol(), "Method '" + initMethodSignature.getFullName(env) +
										"' of '" + aMethod.getDeclaringObject().getFullName() +
										"' has 'package' visibility. It can only be called "
										+ "inside package '" +
										aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage().getName() + "'", true, true);
							}
						}
						else if ( aMethod.getVisibility() == Token.PROTECTED ) {
							if ( ! aMethod.getDeclaringObject().isSupertypeOf(env.getCurrentObjectDec(), env)  ) {
								env.error(getFirstSymbol(), "Method '" + initMethodSignature.getFullName(env) +
										"' of '" + aMethod.getDeclaringObject().getFullName() +
										"' has 'package' visibility. It can only be called "
										+ "in subprototypes of '" +
										aMethod.getDeclaringObject().getName() + "'", true, true);
							}
						}

						final List<MethodKeywordWithParameters> keywordWithParameters = ((MethodSignatureWithKeywords ) initMethodSignature).getKeywordArray();
						for ( final ParameterDec parameter : keywordWithParameters.get(0).getParameterList() ) {
							final Expr realParam = parameterList.get(indexSignature);
							++indexSignature;
							if ( parameter.getVariableKind() == VariableKind.LOCAL_VARIABLE_REF ) {
								if ( realParam instanceof ExprIdentStar ) {
									final ExprIdentStar e = (ExprIdentStar ) realParam;

									final VariableDecInterface varDec = e.getVarDeclaration();

									  // env.searchVariable( ((ExprIdentStar ) realParam).getName());
									if ( varDec == null ||
											! (varDec instanceof StatementLocalVariableDec) &&
											 ! (varDec instanceof FieldDec) &&
											 ! (varDec instanceof ParameterDec)  )
										env.error(realParam.getFirstSymbol(), "A local variable or field was expected because the formal parameter was declared with '&'", true, true);
									else {
										if ( varDec.isReadonly() ) {
											env.error(realParam.getFirstSymbol(), "A non-read only local variable or field was expected because the formal parameter was declared with '&'", true, true);
										}
										if ( varDec instanceof StatementLocalVariableDec ||
											 varDec instanceof FieldDec ) {
											varDec.setRefType(true);
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
							}
					        if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_6 ) {
					            MetaInfoServer.checkAssignmentPluggableTypeSystem(env,
					            		parameter.getType(),
					            		parameter, LeftHandSideKind.ParameterDec_LHS,
					            		realParam.getType(), realParam);
					        }


						}
						/*
							for (ParameterDec paramDec : selWithFormalParam.getParameterList() ) {
								Expr realParam = keywordWithRealParam.getExprList().get(parameterIndex);
								if ( ! paramDec.getType(env).isSupertypeOf(realParam.getType(env), env)) {
									typeErrorInParameterPassing = true;
									break;
								}


								if ( paramDec.getVariableKind() == VariableKind.LOCAL_VARIABLE_REF ) {
									if ( realParam instanceof ExprIdentStar ) {
										ExprIdentStar e = (ExprIdentStar ) realParam;

										VariableDecInterface varDec = e.getVarDeclaration();

										  // env.searchVariable( ((ExprIdentStar ) realParam).getName());
										if ( varDec == null ||
												(! (varDec instanceof StatementLocalVariableDec) &&
												 ! (varDec instanceof FieldDec) &&
												 ! (varDec instanceof ParameterDec)
												 ) )
											env.error(realParam.getFirstSymbol(), "A local variable or field was expected because the formal parameter was declared with '&'", true);
										else {
											if ( varDec instanceof StatementLocalVariableDec )
												((StatementLocalVariableDec ) varDec).setRefType(true);
											else if ( varDec instanceof FieldDec )
												((FieldDec ) varDec).setRefType(true);
											else {
												if ( ! ((ParameterDec ) varDec).getRefType() ) {
													env.error(realParam.getFirstSymbol(), "A parameter with reference type, declared with '&', was expected because the formal parameter was declared with '&'", true);
												}
											}
											realParam.calcInternalTypes(env);
										}
								    }
									else
										env.error(realParam.getFirstSymbol(), "A local variable or field was expected because the formal parameter was declared with '&'", true);
								    break;
								}
						 *
						 */
						break;
					}
				}
				if ( ! foundMethod ) {
					env.error(getFirstSymbol(), "No adequate 'init:' method was found in prototype " + proto.getName() +
							" for this object creation", true, true);
				}
			}
		}
		super.calcInternalTypes(env);

	}


	/**
	   @param env
	 */
	private void checkPublic_New(Env env) {
		final int modif = constructor.getModifiers();
		if ( this.prototype instanceof ExprIdentStar &&
				((ExprIdentStar) this.prototype).getIdentStarKind() != IdentStarKind.jvmClass_t &&
				!(this.prototype instanceof ast.ExprGenericPrototypeInstantiation) ) {
			env.error(getFirstSymbol(), "'new' messages can only be sent to a class receiver");
		}
		if ( ! Modifier.isPublic(modif) ) {
			env.error(getFirstSymbol(), "Constructor is not public");
		}
	}

	public List<Expr> getParameterList() {
		return parameterList;
	}

	public Expr getPrototype() {
		return prototype;
	}



	@Override
	public boolean warnIfStatement() {
		return true;
	}

	/**
	 * Real parameter list
	 */
	private final List<Expr> parameterList;
	/**
	 * the prototype such as Sum and Stack<Int>
	 */
	private final Expr prototype;
	private Symbol rightParSymbol;
	/**
	 * the 'init' or 'init:' method that should be used in this creation of object
	 */
	private MethodSignature initMethodSignature;

	private Constructor<?> constructor = null;



}
