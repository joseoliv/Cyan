/**
 *
 */

package ast;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import error.CompileErrorException;
import error.ErrorKind;
import lexer.Symbol;
import meta.AttachedDeclarationKind;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAssignment_cge;
import meta.IdentStarKind;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.Tuple5;
import meta.WrAnnotation;
import meta.WrMethodDec;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 * Represents the declaration of a method
 *
 * @author José
 *
 */
public class MethodDec extends SlotDec {

	public MethodDec(ObjectDec currentObject, Token visibility, boolean isFinal, boolean shared,
			List<AnnotationAt> nonAttachedSlotAnnotationList, List<AnnotationAt> attachedSlotAnnotationList,
			int methodNumber, boolean compilerCreatedMethod,
			Stack<Tuple5<String, String, String, String, Integer>> annotContextStack) {

		super(currentObject, visibility, attachedSlotAnnotationList, nonAttachedSlotAnnotationList);
		// this.declaringObject = currentObject;
		this.isFinal = isFinal;
		this.shared = shared;
		hasOverride = false;
		leftCBsymbol = null;
		rightCBsymbol = null;
		firstSymbolExpr = null;
		lastSymbolExpr = null;
		this.methodNumber = methodNumber;
		this.compilerCreatedMethod = compilerCreatedMethod;
		expr = null;
		statementList = null;
		overload = false;
		setAllowAccessToFields(true);
		hasJavaCode = false;
		shouldInsertCallToConstructorWithoutParametes = false;
		this.readFromFieldList = null;
		this.assignedToFieldList = null;
		selfLeak = false;
		if ( annotContextStack != null && annotContextStack.size() > 0 ) {
			// Stack<Tuple5<String, String, String, String, Integer>> stack = new Stack<>();
			this.annotContextStack = new Stack<>();
			for (final Tuple5<String, String, String, String, Integer> t : annotContextStack) {
				final Tuple5<String, String, String, String, Integer> newT = new Tuple5<>(t.f1, t.f2, t.f3, t.f4, t.f5);

				this.annotContextStack.push(newT);
			}
			// this.annotContextStack = (Stack<Tuple5<String, String, String, String,
			// Integer>>) annotContextStack.clone();
		}
		else {
			this.annotContextStack = annotContextStack;
		}

		selfMessagePassingList = null;
	}

	@Override
	public void accept(ASTVisitor visitor) {

		visitor.preVisit(this);

		this.methodSignature.accept(visitor);
		if ( statementList != null ) {
			this.statementList.accept(visitor);
		}
		if ( this.expr != null ) {
			expr.accept(visitor);
		}
		visitor.visit(this);
	}

	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	public Expr getExpr() {
		return expr;
	}

	public void setStatementList(StatementList statementList) {
		this.statementList = statementList;
	}

	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		//		if ( cyanEnv.getGenInterfacesForCompiledCode() ) {
		//			String methodName = this.getName();
		//			if ( visibility == Token.PRIVATE || methodName.endsWith("__") ||
		//					methodName.equals("new") || methodName.startsWith("new:") ) {
		//				return ;
		//			}
		//		}
		cyanEnv.atBeginningOfMethodDec(this);
		pw.println("");
		super.genCyan(pw, false, cyanEnv, genFunctions);
		// pw.printlnIdent(" // " + methodSignature.getSingleParameterType());
		pw.printIdent("");
		if ( visibility != null && visibility != Token.PUBLIC ) {
			pw.print(NameServer.getVisibilityString(visibility));
			pw.print(" ");
		}
		if ( isFinal && !this.getDeclaringObject().getIsFinal() ) {
			pw.print("final ");
		}
		if ( shared ) {
			pw.print("shared ");
		}
		if ( hasOverride ) {
			pw.print("override ");
		}
		if ( isAbstract ) {
			pw.print("abstract ");
		}
		pw.print("func ");
		methodSignature.genCyan(pw, PWCounter.printInMoreThanOneLine(methodSignature), cyanEnv, genFunctions);

		if ( !cyanEnv.getGenInterfacesForCompiledCode() ) {
			if ( expr != null ) {
				pw.print(" = ");
				expr.genCyan(pw, false, cyanEnv, genFunctions);
				pw.println(";");
			}
			else if ( statementList != null ) {
				if ( !PWCounter.printInMoreThanOneLine(statementList)
						&& statementList.getStatementList().size() <= 2 ) {
					pw.print(" { ");
					statementList.genCyan(pw, false, cyanEnv, genFunctions);
					pw.printlnIdent("} ");
				}
				else {
					pw.println(" {");
					pw.add();
					statementList.genCyan(pw, true, cyanEnv, genFunctions);
					pw.sub();
					pw.printlnIdent("} ");
				}
			}
		}
		cyanEnv.atEndOfMethodDec();
	}

	@Override
	public void genJava(PWInterface pw, Env env) {

		env.atBeginningOfCurrentMethod(this);
		final String name = getNameWithoutParamNumber();

		super.genJava(pw, env);
		pw.println("");
		// pw.printlnIdent(" // " + methodSignature.getSingleParameterType());

		pw.printIdent("");
		/*
		 * if ( name.equals("new") || name.equals("new:") ) pw.print("static ");
		 */
		String strVisibility;

		final boolean isInit1 = this.isInitMethod();

		if ( visibility == null || isInit1 || name.equals("new") || name.equals("new:") ) {
			strVisibility = "public";
			visibility = Token.PUBLIC;
		}
		else if ( visibility == Token.PACKAGE ) {
			strVisibility = "";
		}
		else {
			strVisibility = visibility.toString();
		}

		if ( shared ) {
			pw.print("static ");
		}

		if ( this.overload ) {
			strVisibility = "private";
		}
		pw.print(strVisibility +
				" ");

		if ( hasOverride && !overload ) {
			pw.print("@Override ");
		}

		if ( name.equals("initShared") ) {
			pw.printlnIdent("static ");
		}
		if ( isInit1 ) {
			methodSignature.genJavaAsConstructor(pw, env, this.declaringObject.getJavaNameWithoutPackage());
		}
		else {
			methodSignature.genJava(pw, env, this.overload);
		}

		final Type returnType = this.getMethodSignature().getReturnType(env);

		if ( isAbstract ) {
			pw.println("{");
			pw.add();
			pw.printlnIdent("throw new ExceptionContainer__(new _ExceptionCannotCallAbstractMethod());");
			pw.sub();
			pw.println("}");
		}
		else {
			pw.println(" {");
			pw.add();

			if ( isInit1 && shouldInsertCallToConstructorWithoutParametes && !this.hasJavaCode ) {
				/*
				 * call super constructor without parameters
				 */
				pw.printlnIdent("super();");
			}

			if ( this.declaringObject.outerObject != null && NameServer.isMethodNameEval(name) ) {
				env.setCreatingInnerPrototypesInsideEval(true);
			}

			if ( expr != null ) {
				if ( !( (returnType == Type.Nil || returnType == null) && !expr.mayBeStatement() ) ) {
					addParameterTypeInfo(pw, env);
					String tmpVar = expr.genJavaExpr(pw, env);

					/*
					 * A metaobject attached to the type of the formal parameter may demand that the real argument be
					 * changed. The new argument is the return of method  changeRightHandSideTo
					 */

					final Tuple2<IActionAssignment_cge, ObjectDec> cyanMetaobjectPrototype = MetaInfoServer
							.getChangeAssignmentCyanMetaobject(env, returnType);
					IActionAssignment_cge changeCyanMetaobject = null;
					ObjectDec prototypeFoundMetaobject = null;
					if ( cyanMetaobjectPrototype != null ) {
						changeCyanMetaobject = cyanMetaobjectPrototype.f1;
						prototypeFoundMetaobject = cyanMetaobjectPrototype.f2;

						if ( changeCyanMetaobject != null ) {

							try {
								tmpVar = changeCyanMetaobject.cge_changeRightHandSideTo(prototypeFoundMetaobject, tmpVar,
										expr.getType(env));

							} catch (final error.CompileErrorException e) {
							} catch (final NoClassDefFoundError e) {
								final WrAnnotation annotation = ((CyanMetaobjectAtAnnot) changeCyanMetaobject)
										.getAnnotation();
								env.error(meta.GetHiddenItem.getHiddenSymbol(annotation.getFirstSymbol()), e.getMessage() +
										" " +
										NameServer.messageClassNotFoundException);
							} catch (final RuntimeException e) {
								final WrAnnotation annotation = ((CyanMetaobjectAtAnnot) changeCyanMetaobject)
										.getAnnotation();
								env.thrownException(meta.GetHiddenItem.getHiddenCyanAnnotation(annotation),
										meta.GetHiddenItem.getHiddenSymbol(annotation.getFirstSymbol()), e);
							} finally {
								env.errorInMetaobject((meta.CyanMetaobject) changeCyanMetaobject, this.getFirstSymbol());
							}
						}
					}

					if ( expr.getType(env) == Type.Dyn && returnType != Type.Dyn ) {
						// first case
						/*
						 *
						 */
						pw.printlnIdent("if ( " +
								tmpVar +
								" instanceof " +
								returnType.getJavaName() +
								" ) {");
						pw.add();
						pw.printlnIdent("return (" +
								returnType.getJavaName() +
								" ) " +
								tmpVar +
								";");
						pw.sub();
						pw.printlnIdent("}");
						pw.printlnIdent("else {");
						pw.add();

						pw.printlnIdent("throw new ExceptionContainer__(" +
								env.javaCodeForCastException(expr, returnType) +
								" );");

						pw.sub();
						pw.printlnIdent("}");
					}
					else {

						if ( returnType == Type.Any && expr.getType(env) instanceof InterfaceDec ) {
							tmpVar = " (" +
									MetaHelper.AnyInJava +
									" ) " +
									tmpVar;
						}

						// genJavaExpr_CastJavaCyan(Env env, PW pw, String rightTmpVar, Type rightType,
						// Type leftType, Symbol symForErrorMessage)
						tmpVar = Type.genJavaExpr_CastJavaCyan(env, tmpVar, expr.getType(), returnType,
								expr.getFirstSymbol());
						/*
	                    if ( expr.getType() instanceof TypeJavaRef ) {
	                    	if ( returnType.getInsideType() instanceof Prototype ) {
	                    		// cast Java to Cyan
	                    		String javaClass = expr.getType().getName();
	                    		tmpVar = "new " + NameServer.cyanNameFromJavaBasicType(javaClass) + "(" + tmpVar + ")";
	                    	}
	                    }
	                    else if ( returnType instanceof TypeJavaRef ) {
	                    	if ( expr.getType().getInsideType() instanceof Prototype ) {
	                    		// cast Cyan to Java
	                    		tmpVar = tmpVar + "." + NameServer.getFieldBasicType( expr.getType().getName() );
	                    	}
	                    }


	                    if ( returnType == Type.Any && expr.getType() instanceof InterfaceDec ) {
	                    	tmpVar = "(" + NameServer.AnyInJava + " ) " + tmpVar;
	                    }
						 */
						if ( returnType == Type.Nil || returnType == null ) {
							/*### */
//							if ( expr instanceof ast.ExprMessageSendUnaryChain ||
//									expr instanceof ast.ExprMessageSendWithKeywords ) {
//								pw.printlnIdent("return ;");
//							}
							pw.printlnIdent("return ;");
						}
						else {
							pw.printlnIdent("return " +
									tmpVar +
									";");
						}
					}

				}

			}
			else {
				addParameterTypeInfo(pw, env);

				final int ident = pw.getCurrentIndent();
				int count = 0;
				boolean firstStatementIsSuperInit = false;
				final List<Statement> statList = statementList.getStatementList();
				if ( statList.size() > 0 && isInit1 && env.getStrInitRefVariables() != null ) {
					final Statement firstStat = statList.get(0);
					if ( firstStat instanceof ast.ExprMessageSendUnaryChainToSuper
							|| firstStat instanceof ast.ExprMessageSendWithKeywordsToSuper ) {
						firstStatementIsSuperInit = true;
					}

				}
				/*
				 * insert initialization of fields at the start of the method 'init' or 'init:'. There
				 * is no call to super 'init' or 'init:'
				 * These fields were initialized in their declarations
				 * /
				 */

				final String strInitRefVariable = env.getStrInitRefVariables();
				if ( !firstStatementIsSuperInit && isInit1 && strInitRefVariable != null ) {
					pw.println(strInitRefVariable);
				}

				for (final Statement s : statList) {
					env.pushCode(s);

					s.genJava(pw, env);
					if ( s.addSemicolonJavaCode() ) {
						pw.println(";");
					}
					env.popCode();
					/*
					 * insert initialization of fields after call to super in an
					 * 'init' or 'init:' method.
					 * These fields were initialized in their declarations
					 */
					if ( firstStatementIsSuperInit && count == 0 ) {
						pw.println(strInitRefVariable);
					}
					++count;
				}

				pw.set(ident);
				// method returns Nil but it does not have a return
				// statement.
				if ( (returnType == Type.Nil && !isInit1 &&
						!statementList.getAlwaysReturnMemoize()) && !hasJavaCode ) {
					pw.printlnIdent("return ;");
					/* ###

					pw.printlnIdent("return " +
							MetaHelper.NilInJava +
							".prototype" +
							";");
					*/
				}
			}
			env.setCreatingInnerPrototypesInsideEval(false);

			pw.printlnIdent("} ");
			pw.sub();

		}
		env.atEndMethodDec();

		// }
	}

	/**
	 * generated code that will, at runtime, store in a file
	 * information on each parameter type that has type Dyn
	 * @param pw
	 */
	private void addParameterTypeInfo(PWInterface pw, Env env) {
		if ( ! env.getAddTypeInfo() ) {
			return; // bad !
		}
		if ( this.isInitMethod() && this.getDeclaringObject().getOuterObject() != null) {
			return;
		}
		List<ParameterDec> paramList;
		if ( methodSignature instanceof MethodSignatureWithKeywords ) {
			MethodSignatureWithKeywords ms = (MethodSignatureWithKeywords) this.methodSignature;
			paramList = ms.getParameterList();
		}
		else if ( methodSignature instanceof MethodSignatureOperator ) {
			MethodSignatureOperator ms = (MethodSignatureOperator ) this.methodSignature;
			paramList = ms.getParameterList();
			if ( paramList == null || paramList.size() == 0 ) {
				return ;
			}
		}
		else {
			return ;
		}

		boolean foundDyn = false;
		for (ParameterDec p : paramList) {
			if ( p.getType() == Type.Dyn ) {
				foundDyn = true;
				break;
			}
		}
		if ( !foundDyn ) { return; }

		/*
		pw.printlnIdent("if ( cyan.lang._System.numberEntriesTypeInfo > 0 ) {");
		pw.printlnIdent("cyan.lang._System.writeToFileToAddTypeInfo( \",\", \"" +
				env.getFileNameToAddTypeInfo() + "\");");
		pw.printlnIdent("}");
		pw.printlnIdent("++cyan.lang._System.numberEntriesTypeInfo;");


		pw.printlnIdent("cyan.lang._System.writeToFileToAddTypeInfo( " +
				"      \"    {\\r\\n\" +\r\n" +
				"        \"      \\\"prototype\\\": \\\"" + env.getCurrentPrototype().getFullName() + "\\\"," +
				"\\r\\n\" + \r\n"
				+ "        \"      \\\"method\\\": \\\"" + this.getNameWithParamAndTypes() + "\\\",\\r\\n\" + \r\n"
				+ "        \"      \\\"line\\\": " + this.getFirstSymbol().getLineNumber() + ",\\r\\n\" + \r\n"
				+ "        \"      \\\"column\\\": " + this.getFirstSymbol().getColumnNumber() + ",\\r\\n\" + \r\n"
				+ "        \"      \\\"arguments\\\": [\",\r\n" +
				"        \"" + env.getFileNameToAddTypeInfo() + "\"" +
				");"
				);
		*/
		int dynCount = 0;
		for (ParameterDec p : paramList) {
			if ( p.getType() == Type.Dyn ) {
				++dynCount;
			}
		}
		pw.add();
		ObjectDec proto = this.getDeclaringObject();
		String fullName = proto.getFullName();
		String methodName = this.getMethodSignature().getFullNameWithReturnType();
		for (ParameterDec p : paramList) {
			if ( p.getType() == Type.Dyn ) {
				int offsetToInsertType;
				boolean wasTypeSupplied;
				if ( p.getTypeInDec() == null ) {
					// type was not supplied
					offsetToInsertType = p.getFirstSymbol().getOffset();
					wasTypeSupplied = false;
				}
				else {
					offsetToInsertType = p.getTypeInDec().getFirstSymbol().getOffset();
					wasTypeSupplied = true;
				}
//				DynamicTypeInfoPrototype ip =
//						cyanruntime.CyanRuntime.dynamicTypeInfoProgram.prototypeSet.get(fullName);
//				if ( ip == null ) {
//					ip = new DynamicTypeInfoPrototype();
//					cyanruntime.CyanRuntime.dynamicTypeInfoProgram.prototypeSet.put(fullName, ip);
//				}
//				DynamicTypeInfoMethod im = ip.methodSet.get(methodName);
//				if ( im == null ) {
//					im = new DynamicTypeInfoMethod(methodName);
//					ip.methodSet.put(methodName, im);
//				}
				Symbol first = p.getFirstSymbol();
//				DynamicTypeInfoVarParamField par = im.parameterSet.get(p.getName());
//				if ( par == null ) {
//					par = new DynamicTypeInfoVarParamField(p.getName(), first.getLineNumber(),
//							first.getColumnNumber(), offsetToInsertType, wasTypeSupplied);
//				}

				String fullJavaName = p.getJavaName() + (p.getRefType() ? ".elem" : "");
				pw.println("cyanruntime.CyanRuntime.addDynParameterInfo(\"" + fullName + "\", \""
						+ proto.getSHA256Code() + "\", "
						+ "\"" + methodName + "\", " +
						"\"" + p.getName() + "\", " + first.getLineNumber() + ", " +
						first.getColumnNumber()  + ", " + offsetToInsertType + ", " +
						wasTypeSupplied + ", " +
						fullJavaName + " instanceof cyan.lang._Nil ? \"Nil\" : (" +
						"((cyan.lang._Any ) " + fullJavaName + ")._prototypePackageName().s + \".\" +" +
		                 "((cyan.lang._Any ) " + fullJavaName + ")._prototypeName().s"
		                 + " ) );"
		                 );

				/*
				pw.printlnIdent("");
				pw.printlnIdent(
						"cyan.lang._System.writeToFileToAddTypeInfo( " +
								"\"" + p.getName() + "\", " + wasTypeSupplied + ", " +
								offsetToInsertType + ",");
				pw.printlnIdent(
						p.getJavaName() + " instanceof cyan.lang._Nil ? \"Nil\" : (" +
								"((cyan.lang._Any ) " + p.getJavaName() + ")._prototypePackageName().s + \".\" +");
				pw.printlnIdent(  "((cyan.lang._Any ) " + p.getJavaName() + ")._prototypeName().s ),");
				pw.printlnIdent( (--dynCount > 0 ? "true" : "false") + ",\r\n");
				pw.printlnIdent( "\"" + env.getFileNameToAddTypeInfo() + "\"" +
						");");
				*/

			}
		}
//		pw.printlnIdent("cyan.lang._System.writeToFileToAddTypeInfo( " +
//				"         \"      ]\", \r\n" +
//				"        \"" + env.getFileNameToAddTypeInfo() + "\"" +
//				");");
//
//		pw.printlnIdent("        cyan.lang._System.writeToFileToAddTypeInfo( " +
//				"\"    }\", \r\n" +
//				"        \"" + env.getFileNameToAddTypeInfo() + "\"" +
//				");");

		pw.sub();
//		pw.printlnIdent("cyan.lang._System.write_ln_only_ToFileToAddTypeInfo();");


	}

	public void genJavaOverloadedMethod(PWInterface pw, Env env, List<MethodDec> overloadMethodList) {

		env.atBeginningOfCurrentMethod(this);

		boolean firstPrecededOverload = false;
		for (final MethodDec md : overloadMethodList) {
			if ( md.getPrecededBy_overload() ) {
				firstPrecededOverload = true;
				break;
			}
		}
		pw.printIdent("");
		if ( !firstPrecededOverload ) {
			pw.print("@Override ");
		}
		pw.print("public ");

		final List<ParameterDec> allParam = new ArrayList<>();

		if ( this.methodSignature instanceof MethodSignatureWithKeywords ) {
			final MethodSignatureWithKeywords ms = (MethodSignatureWithKeywords) methodSignature;
			ms.genJavaOverloadMethod(pw, env);
			for (final MethodKeywordWithParameters s : ms.getKeywordArray()) {
				final List<ParameterDec> parameterList = s.getParameterList();
				allParam.addAll(parameterList);
			}
		}
		else if ( this.methodSignature instanceof MethodSignatureOperator ) {
			final MethodSignatureOperator ms = (MethodSignatureOperator) methodSignature;
			ms.genJavaOverloadMethod(pw, env);
			if ( ms.getParameterList() != null ) {
				allParam.add(ms.getParameterList().get(0));
			}
		}

		pw.print(" ");
		pw.println(" {");

		pw.add();

		Type returnType = this.methodSignature.getReturnType(env);

		int sizeOverloadMethodList = overloadMethodList.size();
		for (final MethodDec md : overloadMethodList) {
			/*
            CyBoolean  _ampersand_ampersand(CyBoolean _other) _ampersand_ampersand(Object _other) )  {
            if ( _other instanceof cyan.lang.Boolean  ) {
            return _ampersand_ampersand(_other);
            } else         if ( _other instanceof cyan.lang.Boolean  ) {
            return _ampersand_ampersand__Function_LT_GP_CyBoolean_GT(_other);
            }
            }

			 */
			final List<ParameterDec> methodParamList = md.getMethodSignature().getParameterList();

			pw.printIdent("if ( ");
			int i = 0;
			int size = allParam.size();
			for (final ParameterDec param : methodParamList) {
				pw.print(allParam.get(i).getJavaName() +
						" instanceof " +
						param.getType().getJavaName() +
						" ");
				if ( --size > 0 ) {
					pw.print("&& ");
				}
				++i;
			}
			pw.println(" ) { ");
			pw.add();
			String nameOverloadMethod = "";
			final MethodSignature methodSignature2 = md.getMethodSignature();
			if ( methodSignature2 instanceof MethodSignatureWithKeywords ) {
				nameOverloadMethod = ((MethodSignatureWithKeywords) methodSignature2).getJavaNameOverloadMethod();
			}
			else if ( methodSignature2 instanceof MethodSignatureOperator ) {
				nameOverloadMethod = ((MethodSignatureOperator) methodSignature2).getJavaNameOverloadMethod();
			}
			pw.printlnIdent("/*### returnType is " + ((returnType == null) ? "null" : returnType.getFullName()) + " */");
			if ( returnType == null || returnType == Type.Nil ) {
				pw.printIdent(nameOverloadMethod +
						"(");
			}
			else {
				pw.printIdent("return " +
						nameOverloadMethod +
						"(");
			}

			int indexMPL = 0;

			size = allParam.size();
			for (final ParameterDec param : allParam) {
				pw.print("(" +
						methodParamList.get(indexMPL).getType().getJavaName() +
						") " +
						param.getJavaName());
				++indexMPL;
				if ( --size > 0 ) {
					pw.print(", ");
				}
			}
			pw.println(");");
			pw.sub();
			pw.printIdent("}");
			if ( --sizeOverloadMethodList == 0 ) {
				if ( !firstPrecededOverload ) {
					pw.println();
					pw.printlnIdent("else");
					pw.add();
					if ( returnType == null || returnType == Type.Nil ) {
						pw.printIdent(this.methodSignature.getJavaName() +
								"(");
					}
					else {
						pw.printIdent("return super." +
								this.methodSignature.getJavaName() +
								"(");
					}

					size = allParam.size();
					indexMPL = 0;
					for (final ParameterDec param : allParam) {
						pw.print( /* "(" + methodParamList.get(indexMPL).getType().getJavaName() + ") " + */ param
								.getJavaName());
						++indexMPL;
						if ( --size > 0 ) {
							pw.print(", ");
						}
					}

					pw.println(");");
					pw.sub();

				}
				else {
					pw.println("");
					if ( returnType != null && returnType != Type.Nil ) {
						pw.printlnIdent("return null;");
					}
				}
			}
			pw.println("");
		}
		pw.sub();

		pw.printlnIdent("} ");

		env.atEndMethodDec();

	}

	public void setMethodSignature(MethodSignature methodSignature) {
		this.methodSignature = methodSignature;
	}

	public MethodSignature getMethodSignature() {
		return methodSignature;
	}

	/**
	 * For unary, grammar, and operator methods, it returns the same as {@link #getNameWithoutParamNumber()}. For regular methods, it
	 * returns the names of all keywords plus its number of parameters concatenated.
	 * That is, the return for method<br>
	 * <code>with: Int n, Char ch plus: Float f</code><br>
	 * would be <code>with:2 plus:1</code>
	 */

	@Override
	public String getName() {
		return methodSignature.getName();
	}

	public String getNameWithoutParamNumber() {
		return methodSignature.getNameWithoutParamNumber();
	}

	/**
	 * return a special signature (ss) of the method as given as in the source code.
	 * The ss of a unary method "func name -> R" is "name -> R". The ss
	 * of an operator method "func + Int n -> R" is "+ Int". The ss of
	 * a keyword method<br>
	 * <code> func s1: people.Person p, Int kk s2: String aa, cyan.lang.String bb -> R</code><br>
	 * is <br>
	 * <code>s1: people.Person, Int s2: String, cyan.lang.String -> R</code><br>
	 * A type T is exactly as it appears in the code.
	 *
       @return
	 */
	public String getNameWithDeclaredTypes() {
		return this.methodSignature.getNameWithDeclaredTypes();
	}

	public String getNameWithParamAndTypes() {
		return this.methodSignature.getNameWithParamAndTypes();
	}


	public void setHasOverride(boolean hasOverride) {
		this.hasOverride = hasOverride;
	}

	public boolean getHasOverride() {
		return hasOverride;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/**
	 * returns true if this method is an "init" or "init:" method.
	 */
	public boolean isInitMethod() {
		if ( isInit == null ) {
			final String s = getNameWithoutParamNumber();
			isInit = s.compareTo("init") == 0 || s.compareTo("init:") == 0;
		}
		return isInit;
	}

	/**
	 * return true if this method has name "initShared"
       @return
	 */
	public boolean isInitShared() {
		final String s = getNameWithoutParamNumber();
		return s.equals("initShared");
	}

	@Override
	public Symbol getFirstSymbol() {
		return firstSymbol;
	}

	/**
	 * return the method name with the parameter types. If the method is
	 * declared as add: (Int i, String s) with: (Char ch, Float other) ->
	 * Boolean the return value is "add: Int, String with: Char, Float" there is
	 * a single space after ':' or ',' and before every keyword (except for the
	 * first)
	 *
	 * @return
	 */
	public String getMethodInterface(Env env) {
		return methodSignature.getFullName(env);
	}

	/**
	 * return the method signature as a string.
	 *
	 * @return
	 */
	public String getMethodSignatureAsString() {
		return methodSignature.getMethodSignatureWithParametersAsString();
	}

	public String getSignatureWithoutReturnType() {
		return this.methodSignature.getSignatureWithoutReturnType();
	}

	/**
	 * calculates the types of everything internal to a method, excluding its
	 * signature
	 *
	 * @param env
	 */
	@Override
	public void calcInternalTypes(Env env) {

		env.atBeginningOfCurrentMethod(this);

		super.calcInternalTypesNONAttachedAnnotations(env);

		methodSignature.calcInternalTypes(env);

		// super.calcInternalTypes(env);

		if ( methodSignature.demandsImmutability() ) {

		}

		try {

			if ( expr != null ) {
				if ( this.isInitMethod() || this.isInitShared() ) {
					env.error(this.getFirstSymbol(),
							"'init', 'init:', and 'initShared' methods cannot be set with an expression");
				}

				try {
					env.pushCheckUsePossiblyNonInitializedPrototype(true);
					expr.calcInternalTypes(env);
				} finally {
					env.popCheckUsePossiblyNonInitializedPrototype();
				}

				if ( !methodSignature.getReturnType(env).isSupertypeOf(expr.getType(), env) ) {
					env.error(expr.getFirstSymbol(),
							"The type of this expression is not subtype of the type of the method return type");
				}
				if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_6 ) {
					MetaInfoServer.checkAssignmentPluggableTypeSystem(env, this.methodSignature.getReturnType(env),
							this.methodSignature, LeftHandSideKind.MethodSignatureEqualTo_LHS, expr.getType(), expr);
				}

			}

			if ( statementList != null ) {
				env.setHasJavaCode(false);

				env.setLexicalLevel(0);
				env.clearStackVariableLevel();

				/*
				 * inside an 'init' or 'init:' method, only the first statement
				 * can be a call 'super init' or 'super init: a, b, c'. The flag
				 * firstMethodStatment is used to check this.
				 *
				 */
				env.setFirstMethodStatement(true);
				env.setTopLevelStatements(true);
				statementList.calcInternalTypes(env);
				env.setTopLevelStatements(false);
				/*
				 * not a beautiful thing to do ...
				 */
				if ( statementList.getFoundError() ) {
					throw new CompileErrorException();
				}
				this.hasJavaCode = env.getHasJavaCode();
				/*
				 * if there is no call to metaobject javacode inside the
				 * method then the compiler can deduce whether or not the
				 * method always return
				 */
				if ( !hasJavaCode && (!statementList.alwaysReturn(env)
						&& this.getMethodSignature().getReturnType(env) != Type.Nil) ) {
					final List<Statement> statementArray = statementList.getStatementList();
					if ( statementArray.size() == 0 ) {
						env.error(methodSignature.getFirstSymbol(), "Method does not return a value");
					}
					else {
						env.error(
								statementList.getStatementList().get(statementList.getStatementList().size() - 1)
								.getFirstSymbol(),
								"Statement does not return a value. Therefore this method does not return a value");
					}
				}
				/*
				 * search for variables that are used before being
				 * initialized
				 */
				// doLiveAnalysis();
				if ( this.isInitMethod() ) {
					/*
					 * check whether all non-shared fields are initialized
					 */
					final ObjectDec currentObject = env.getCurrentObjectDec();
					final Set<FieldDec> wasInitializedSet = new HashSet<>();
					for (final Statement s : this.statementList.getStatementList()) {
						if ( s instanceof ast.StatementAssignmentList ) {
							final List<Expr> exprList = ((StatementAssignmentList) s).getExprList();
							for (int j = 0; j < exprList.size() - 1; ++j) {
								final Expr anExpr = exprList.get(j);
								if ( anExpr instanceof ExprIdentStar ) {
									final ExprIdentStar id = (ExprIdentStar) anExpr;
									if ( id.getIdentStarKind() == IdentStarKind.instance_variable_t ) {
										final FieldDec iv = (FieldDec) id.getVarDeclaration();
										if ( iv.isShared() ) {
											env.error(s.getFirstSymbol(),
													"Shared fields cannot be initialized in 'init' or 'init:' methods");
										}
										wasInitializedSet.add(iv);
									}
								}
								else if ( anExpr instanceof ExprSelfPeriodIdent ) {
									final ExprSelfPeriodIdent exprSelf = (ExprSelfPeriodIdent) anExpr;
									final FieldDec iv = exprSelf.getFieldDec();
									if ( iv.isShared() ) {
										env.error(s.getFirstSymbol(),
												"Shared fields cannot be initialized in 'init' or 'init:' methods");
									}
									wasInitializedSet.add(iv);
								}
							}
						}
					}
					for (final FieldDec v : currentObject.getFieldList()) {
						if ( !v.isShared() && v.getExpr() == null && !wasInitializedSet.contains(v)
								&& !this.hasJavaCode ) {
							String nameiv = v.getName();
							nameiv = nameiv.substring(1);
							if ( nameiv.length() > 0 ) {
								final List<MethodSignature> unaryMethodList = currentObject
										.searchMethodPrivateProtectedPublic(nameiv);
								if ( unaryMethodList != null && unaryMethodList.size() > 0
										&& unaryMethodList.get(0).getMethod().getVisibility() == Token.PUBLIC ) {
									env.error(getFirstSymbol(), "field '" +
											v.getName() +
											"' is not being initialized in this 'init' or 'init:' method. " +
											"Probably the reason is that there is a public field named '" +
											nameiv +
											"'." +
											" You should initialize '" +
											v.getName() +
											"'");
								}
							}
							env.error(getFirstSymbol(), "field '" +
									v.getName() +
									"' is not being initialized in this 'init' or 'init:' method");
						}
					}
					final ObjectDec superObject = currentObject.getSuperobject();
					if ( superObject != null && superObject != Type.Any ) {
						/*
						 * the call 'super init' or 'super init: a' should only be the first statement of the method.
						 */
						/**
						 * if there is a super-prototype, check whether one of the super init method is called.
						 */
						final List<Statement> statList = this.statementList.getStatementList();
						final List<MethodSignature> superInitMethodList = superObject.searchInitNewMethod("init");
						MethodSignature superInitMethod = null;
						if ( superInitMethodList != null && superInitMethodList.size() > 0 ) {
							superInitMethod = superInitMethodList.get(0);
						}
						if ( statList == null || statList.size() == 0 ) {
							/*
							 * this init or 'init:' method does not have statements. If there is an 'init'
							 * method in the super-prototype, it will be called by code introduced  by
							 * the compiler ( shouldInsertCallToConstructorWithoutParametes = true ).
							 * Otherwise, an error occurs because this init method should call the
							 * super init or init: method.
							 */
							if ( superInitMethod != null ) {
								this.shouldInsertCallToConstructorWithoutParametes = true;
							}
							else {
								env.error(firstSymbol,
										"This method should call an 'init' or 'init:' method of the super-prototype");
								return;
							}
						}
						else {
							/*
							 * this 'init' or 'init:' method has statements.
							 */
							final Statement firstStat = statList.get(0);
							if ( !(firstStat instanceof ast.ExprMessageSendUnaryChainToSuper)
									&& !(firstStat instanceof ast.ExprMessageSendWithKeywordsToSuper) ) {
								/*
								 * first statement of this 'init' or 'init:' method is not a message send to super
								 */
								if ( superInitMethod != null ) {
									this.shouldInsertCallToConstructorWithoutParametes = true;
								}
								else if ( !this.hasJavaCode ) {
									env.error(firstSymbol,
											"This method should call an 'init' or 'init:' method of the super-prototype");
									return;
								}
							}
							else if ( firstStat instanceof ast.ExprMessageSendUnaryChainToSuper ) {
								final ExprMessageSendUnaryChainToSuper ms = (ExprMessageSendUnaryChainToSuper) firstStat;
								if ( !ms.getUnarySymbol().getSymbolString().equals("init") ) {
									env.error(firstSymbol,
											"This method should call an 'init' or 'init:' method of the super-prototype");
								}
							}
							else if ( firstStat instanceof ast.ExprMessageSendWithKeywordsToSuper ) {
								final ExprMessageSendWithKeywordsToSuper ms = (ExprMessageSendWithKeywordsToSuper) firstStat;
								if ( !ms.getMessage().getMethodName().equals("init:") ) {
									env.error(firstSymbol,
											"This method should call an 'init' or 'init:' method of the super-prototype");
								}
							}
						}

					}

				}
				if ( this.isInitShared() ) {
					/*
					 * check whether all shared fields are initialized
					 */
					final ObjectDec currentObject = env.getCurrentObjectDec();
					final Set<FieldDec> wasInitializedSet = new HashSet<>();
					for (final Statement s : this.statementList.getStatementList()) {
						if ( s instanceof ast.StatementAssignmentList ) {
							final List<Expr> exprList = ((StatementAssignmentList) s).getExprList();
							if ( exprList.size() != 2 ) {
								// error(Symbol symbol, String message, boolean checkMessage, boolean
								// throwException)
								env.error(s.getFirstSymbol(),
										"Only one '=' symbol is allowed per statement in an 'initShared' method", true,
										false);
							}
							for (int j = 0; j < exprList.size() - 1; ++j) {
								final Expr anExpr = exprList.get(j);
								if ( anExpr instanceof ExprIdentStar ) {
									final ExprIdentStar id = (ExprIdentStar) anExpr;
									if ( id.getIdentStarKind() == IdentStarKind.instance_variable_t ) {
										final FieldDec iv = (FieldDec) id.getVarDeclaration();
										if ( !iv.isShared() ) {
											env.error(s.getFirstSymbol(),
													"Non-shared fields cannot be initialized in 'initShared' methods",
													true, false);
										}
										wasInitializedSet.add(iv);
									}
									else {
										env.error(s.getFirstSymbol(), "field expected in the left side of '='", true,
												false);
									}
								}
								else if ( anExpr instanceof ExprSelfPeriodIdent ) {
									final ExprSelfPeriodIdent exprSelf = (ExprSelfPeriodIdent) anExpr;
									final FieldDec iv = exprSelf.getFieldDec();
									if ( !iv.isShared() ) {
										env.error(s.getFirstSymbol(),
												"Non-shared fields cannot be initialized in 'initShared' methods", true,
												false);
									}
									wasInitializedSet.add(iv);
								}
							}
							final Expr right = exprList.get(exprList.size() - 1);
							if ( !right.isNREForInitShared(env) ) {
								right.isNREForInitShared(env);
								env.error(right.getFirstSymbol(),
										"The expression is not valid for initializing a shared variable. It should be" +
												" a literal value or the creation of an object of a prototype of package cyan.lang." +
												" See the Cyan manual for more information.",
												true, false);

							}
						}
						else {
							// not an assignment
							env.error(s.getFirstSymbol(), "Only assignments are allowed inside method 'initShared'", true,
									false);
						}
					}
					for (final FieldDec v : currentObject.getFieldList()) {
						if ( v.isShared() && v.getExpr() == null && !wasInitializedSet.contains(v) ) {
							env.error(getFirstSymbol(), "Shared field '" +
									v.getName() +
									"' is not being initialized in the 'initShared' method", true, false);
						}
					}

				}

			}
		} catch (final CompileErrorException e) {
			return;
		}
		super.calcInternalTypesAttachedAnnotations(env);
		env.atEndMethodDec();

		final List<MethodDec> equalMethodDecList = declaringObject
				.search_Method_Private_Protected_Public_By_Interface(env, this.getMethodInterface(env));
		if ( equalMethodDecList.size() > 1 ) {
			// there is at least this method in the list. If there is two, then
			// this one is duplicated.

			MethodDec anotherMethod = null;
			for (final MethodDec aMethod : equalMethodDecList) {
				if ( aMethod != this ) {
					anotherMethod = aMethod;
					break;
				}
			}
			if ( anotherMethod == null ) {
				env.error(true, null, "Internal error at MethodDec::calcInternalTypes", null, ErrorKind.internal_error);
				return;
			}

			/*env.error(true, methodSignature.getFirstSymbol(), "Duplicated method", methodSignature.getFullName(env),
            		ErrorKind.duplicate_method); */

			StringBuilder thisStr = new StringBuilder("Method '").append(this.getName()).append("' of line ")
					.append(this.getFirstSymbol().getLineNumber());
			if ( this.annotContextStack != null && this.annotContextStack.size() > 0 ) {
				thisStr.append(" created by the following stack of annotations\n");
				for (final Tuple5<String, String, String, String, Integer> t : this.annotContextStack) {
					thisStr.append("    annotation '").append(t.f2).append("' of line ").append(t.f5)
					.append(" of file ").append(t.f4).append("\n");
				}
			}
			StringBuilder anotherStr = new StringBuilder("Method '").append(anotherMethod.getName())
					.append("' of line ").append(anotherMethod.getFirstSymbol().getLineNumber());
			if ( anotherMethod.getAnnotContextStack() != null && anotherMethod.getAnnotContextStack().size() > 0 ) {
				anotherStr.append(" created by the following stack of annotations\n");
				for (final Tuple5<String, String, String, String, Integer> t : anotherMethod.annotContextStack) {
					anotherStr.append("    annotation '").append(t.f2).append("' of line ").append(t.f5)
					.append(" of file ").append(t.f4).append("\n");
				}
			}
			env.error(this.getFirstSymbol(), "Duplicate methods:\n" +
					thisStr.append("\n").append(anotherStr.toString()).toString());
		}
		final String name = getNameWithoutParamNumber();

		if ( name.compareTo("init") == 0 || name.compareTo("init:") == 0 ) {
			if ( this.overload ) {
				env.error(this.methodSignature.getFirstSymbol(), "'init' or 'init:' methods cannot be overloaded");
			}
			final Expr returnTypeExpr = methodSignature.getReturnTypeExpr();
			if ( returnTypeExpr != null ) {
				// # String returnName = returnTypeExpr.ifPrototypeReturnsItsName();
				final String returnName = returnTypeExpr.getType(env).getFullName();
				if ( returnName.compareTo("Nil") != 0 && !returnName.equals(MetaHelper.cyanLanguagePackageName +
						".Nil") ) {
					env.error(true, methodSignature.getFirstSymbol(),
							"constructor 'init' or 'init:' with a return type different from 'Nil'",
							methodSignature.getFullName(env), ErrorKind.init_should_return_Nil);
				}

			}
		}

		if ( name.compareTo("init") != 0 && name.compareTo("init:") != 0 ) {
			// not init or init:

			if ( isAbstract && !declaringObject.getIsAbstract() ) {

				env.error(true, this.getFirstSymbol(), "Abstract method cannot belong to a non-abstract prototype",
						methodSignature.getFullName(env), ErrorKind.abstract_method_in_a_non_abstract_prototype);
			}

			/**
			 * non-grammar method check whether the return value type is the
			 * same as the method of the super-prototype (if any)
			 */
			final ObjectDec superPrototype = this.declaringObject.getSuperobject();
			if ( superPrototype != null ) {

				final List<MethodSignature> methodSignatureList = superPrototype
						.searchMethodProtectedPublicPackage(this.getName());
				if ( methodSignatureList.size() > 0 ) {
					Type superReturnType;
					if ( methodSignatureList.get(0).getReturnTypeExpr() == null ) {
						superReturnType = Type.Nil;
					}
					else {

						superReturnType = methodSignatureList.get(0).getReturnTypeExpr()
								.ifRepresentsTypeReturnsType(env);
					}

					Type returnType;
					if ( methodSignature.getReturnTypeExpr() == null ) {
						returnType = Type.Nil;
					}
					else {
						returnType = methodSignature.getReturnTypeExpr().ifRepresentsTypeReturnsType(env);
					}

					if ( !superReturnType.isSupertypeOf(returnType, env) ) {

						final String stringMethodSignatureSuper = methodSignatureList.get(0)
								.getMethodSignatureWithParametersAsString();

						final String stringMethodSignatureSub = this.getMethodSignatureAsString();

						superReturnType.isSupertypeOf(returnType, env);
						final String s = methodSignatureList.get(0).getReturnType(env).getFullName();
						env.error(true, this.getFirstSymbol(), "Incompatible return type in sub-prototype method",
								stringMethodSignatureSub, ErrorKind.incompatible_return_type_in_subprototype_method,
								"method0 = \"" +
										stringMethodSignatureSub +
										"\"",
										"method1 = \"" +
												stringMethodSignatureSuper +
								"\"");
					}

				}

			}
		}
	}

	/**
	 * do a live analysis of the method code. It should issue errors if some
	 * variable is used before being initialized
	 */
	private void doLiveAnalysis() {
		/*
		 *
		 * for all n, in[n] = out[n] = emptyset w = set of all nodes of the
		 * method repeat until w is empty n = w.pop() out[n] = union of in[n']
		 * for each n' that is sucessor of n in[n] = use[n] union with (out[n] -
		 * def[n]) if in[n] was changed then for all predecessors m of n,
		 * w.push(m)
		 *
		 */
		int i = 0;
		final Queue<Tuple2<Statement, Integer>> w = new LinkedList<>();
		for (final Statement s : this.statementList.getStatementList()) {
			// empty sets
			s.prepareLiveAnalysis();
			w.add(new Tuple2<>(s, i));
			++i;
		}

		while (w.isEmpty()) {
			final Tuple2<Statement, Integer> n = w.remove();
			// n.f1.outLiveAnalysis =
		}
	}

	@Override
	public void calcInterfaceTypes(Env env) {
		env.atBeginningOfCurrentMethod(this);

		methodSignature.calcInterfaceTypes(env);

		env.atEndMethodDec();
	}

	/**
	 * returns the signature of the current method
	 *
	 * @return
	 */
	public String stringSignature() {
		final PWCharArray pwChar = new PWCharArray();
		if ( getHasOverride() ) {
			pwChar.print("override ");
		}
		if ( getVisibility() == Token.PUBLIC ) {
			pwChar.print("public ");
		}
		else if ( getVisibility() == Token.PRIVATE ) {
			pwChar.print("private ");
		}
		else if ( getVisibility() == Token.PROTECTED ) {
			pwChar.print("protected ");
		}
		if ( isAbstract() ) {
			pwChar.print("abstract ");
		}
		pwChar.print("func ");

		pwChar.print(getMethodSignatureAsString());
		return pwChar.getGeneratedString().toString();
	}

	public boolean isIndexingMethod() {
		return methodSignature.isIndexingMethod();
	}

	public Symbol getLeftCBsymbol() {
		return leftCBsymbol;
	}

	public void setLeftCBsymbol(Symbol leftCBsymbol) {
		this.leftCBsymbol = leftCBsymbol;
	}

	public Symbol getRightCBsymbol() {
		return rightCBsymbol;
	}

	public void setRightCBsymbol(Symbol rightCBsymbol) {
		this.rightCBsymbol = rightCBsymbol;
	}

	public Symbol getFirstSymbolExpr() {
		return firstSymbolExpr;
	}

	public void setFirstSymbolExpr(Symbol firstSymbolExpr) {
		this.firstSymbolExpr = firstSymbolExpr;
	}

	public Symbol getLastSymbolExpr() {
		return lastSymbolExpr;
	}

	public void setLastSymbolExpr(Symbol lastSymbolExpr) {
		this.lastSymbolExpr = lastSymbolExpr;
	}

	public int getMethodNumber() {
		return methodNumber;
	}

	public boolean getCompilerCreatedMethod() {
		return compilerCreatedMethod;
	}

	public void setCompilerCreatedMethod(boolean compilerCreatedMethod) {
		this.compilerCreatedMethod = compilerCreatedMethod;
	}

	public boolean getIsFinal() {
		return isFinal;
	}

	public void genProtoForMethod(StringBuffer s, CyanEnv cyanEnv) {
		s.append("\n");
		prototypeNameForMethod = NameServer.methodProtoName + methodSignature.getPrototypeNameForMethod()
		+ NameServer.endsInnerProtoName;
		s.append("    object " +
				prototypeNameForMethod +
				"(" +
				declaringObject.getName() +
				" " +
				NameServer.selfNameInnerPrototypes +
				")" +
				" extends " +
				methodSignature.getSuperprototypeNameForMethod() +
				"\n\n");
		s.append("        func ");
		methodSignature.genCyanEvalMethodSignature(s);
		s.append(" {\n");

		final PWCharArray pwChar = new PWCharArray();
		pwChar.add();
		pwChar.add();
		pwChar.add();

		if ( statementList == null ) {
			if ( expr == null ) {
				// an abstract method

				pwChar.printlnIdent("throw ExceptionCannotCallAbstractMethod(\"" +
						this.declaringObject.getFullName() +
						"::" +
						this.getMethodSignatureAsString() +
						"\")");

			}
			else {
				pwChar.printIdent("return ");
				expr.genCyan(pwChar, true, cyanEnv, false);
				pwChar.println(";");
			}
		}
		else {
			statementList.genCyan(pwChar, true, cyanEnv, false);
		}
		s.append(pwChar.getGeneratedString().toString());
		s.append("        }\n\n");

		s.append("    end\n");
	}

	public String getPrototypeNameForMethod() {
		return prototypeNameForMethod;
	}

	public StatementList getStatementList() {
		return statementList;
	}

	public AttachedDeclarationKind getKind() {
		return AttachedDeclarationKind.METHOD_DEC;
	}

	public boolean getOverload() {
		return overload;
	}

	public void setOverload(boolean overload) {
		this.overload = overload;
	}

	public boolean getPrecededBy_overload() {
		return precededBy_overload;
	}

	public void setPrecededBy_overload(boolean precededBy_overload) {
		this.precededBy_overload = precededBy_overload;
	}

	public boolean getAllowAccessToFields() {
		return allowAccessToFields;
	}

	public void setAllowAccessToFields(boolean allowAccessToFields) {
		this.allowAccessToFields = allowAccessToFields;
	}

	public void setFirstSymbol(Symbol firstSymbol) {
		this.firstSymbol = firstSymbol;
	}

	public void addReadFromFieldList(FieldDec iv) {
		if ( readFromFieldList == null ) {
			readFromFieldList = new ArrayList<>();
		}
		readFromFieldList.add(iv);
	}

	public void addAssignedToFieldList(FieldDec iv) {
		if ( assignedToFieldList == null ) {
			assignedToFieldList = new ArrayList<>();
		}
		assignedToFieldList.add(iv);
	}

	/**
	 * return true if some non-shared field was read in this method. That is,
	 * if some field appears in some expression
       @return
	 */
	public boolean someNonSharedFieldWasRead() {
		if ( readFromFieldList == null || readFromFieldList.size() == 0 ) {
			return false;
		}
		else {

			for (final FieldDec iv : this.readFromFieldList) {
				if ( !iv.isShared() ) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * return true if some shared field was read in this method. That is,
	 * if some shared field appears in some expression
       @return
	 */
	public boolean someSharedFieldWasRead() {
		if ( readFromFieldList == null || readFromFieldList.size() == 0 ) {
			return false;
		}
		else {

			for (final FieldDec iv : this.readFromFieldList) {
				if ( iv.isShared() ) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * return true if some non-shared field was assigned to inside this method.
	 * That is, if some non-shared field appears in the left-hand side of
	 * an assignment
	 */
	public boolean someNonSharedFieldAssignedTo() {
		if ( this.assignedToFieldList == null || assignedToFieldList.size() == 0 ) {
			return false;
		}
		else {

			for (final FieldDec iv : this.assignedToFieldList) {
				if ( !iv.isShared() ) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * return true if some non-shared field was assigned to inside this method.
	 * That is, if some non-shared field appears in the left-hand side of
	 * an assignment
	 */
	public boolean someSharedFieldAssignedTo() {
		if ( this.assignedToFieldList == null || assignedToFieldList.size() == 0 ) {
			return false;
		}
		else {

			for (final FieldDec iv : this.assignedToFieldList) {
				if ( iv.isShared() ) {
					return true;
				}
			}
			return false;
		}
	}

	public List<FieldDec> getAssignedToFieldList() {
		return assignedToFieldList;
	}

	public List<FieldDec> getReadFromFieldList() {
		return readFromFieldList;
	}

	public boolean getSelfLeak() {
		return selfLeak;
	}

	public void setSelfLeak(boolean selfLeak) {
		this.selfLeak = selfLeak;
	}

	public Stack<Tuple5<String, String, String, String, Integer>> getAnnotContextStack() {
		return annotContextStack;
	}

	public void addSelfMessagePassing(MethodSignature ms) {
		if ( selfMessagePassingList == null ) {
			selfMessagePassingList = new ArrayList<>();
		}
		selfMessagePassingList.add(ms);
	}

	public List<MethodSignature> getSelfMessagePassingList() {
		return selfMessagePassingList;
	}

	@Override
	public WrMethodDec getI() {
		if ( iMethodDec == null ) {
			iMethodDec = new WrMethodDec(this);
		}
		return iMethodDec;
	}

	public Set<FieldDec> getAccessedFieldSet() {
		return accessedFieldSet;
	}

	public void addToAccessedFieldSet(FieldDec accessedField) {
		if ( accessedFieldSet == null ) {
			accessedFieldSet = new HashSet<>();
		}
		this.accessedFieldSet.add(accessedField);
	}

	public boolean createdByMetaobjects() {
		return annotContextStack != null && !this.annotContextStack.isEmpty();
	}

	public boolean isUnary() {
		return this.methodSignature instanceof MethodSignatureUnary;
	}

	/**
	 * return true if there is an attached annotation 'annotName' in this method or in
	 * any superprototype method with the same name
       @param annotName
       @return
	 */
	public boolean hasAttachedAnnotationNamed(String annotName) {
		if ( attachedAnnotationList != null ) {
			for (AnnotationAt annot : attachedAnnotationList) {
				if ( annot.getCyanMetaobject().getName().equals(annotName) ) {
					return true;
				}
			}
		}
		return false;
	}

	static public boolean hasAttachedAnnotationNamed(List<MethodSignature> methodSignatureList, String annotName) {
		if ( methodSignatureList != null ) {
			for (MethodSignature ms : methodSignatureList) {
				if ( ms.getMethod().hasAttachedAnnotationNamed(annotName) ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean getShared() {
		return shared;
	}

	/**
	 * list of fields accessed by this method
	 */
	private Set<FieldDec> accessedFieldSet;

	private WrMethodDec iMethodDec = null;

	private MethodSignature methodSignature;
	/**
	 * if the method is assigned an expression, it is in variable expr declared
	 * below. Example: public proc get -> int = anotherObject.{get -> int}.
	 */
	private Expr expr;
	/**
	 * if the method has a body, the following variable points to it. Therefore
	 * expr == null if and only if statementList != null
	 */
	private StatementList statementList;

	/**
	 * true if there is keyword "override" in the declaration of this method
	 */
	private boolean hasOverride;

	/**
	 * true if this method is final
	 */
	private final boolean isFinal;

	/**
	 * true if this method is abstract
	 */
	private boolean isAbstract;

	/**
	 * the symbol '{' that opens the method declaration. It is null if the
	 * method is declared with an expression as in func zero -> Int = 0
	 *
	 */
	private Symbol leftCBsymbol;

	/**
	 * the symbol '}' that closes the method declaration. It is null if the
	 * method is declared with an expression as in func zero -> Int = 0
	 *
	 */
	private Symbol rightCBsymbol;

	/**
	 * if the method is declared with an expression as in func zero -> Int = 0
	 *
	 * then firstSymbolExpr is the first symbol of the expression
	 */
	private Symbol firstSymbolExpr;
	/**
	 * if the method is declared with an expression as in func zero -> Int = 0
	 *
	 * then lastSymbolExpr is the last symbol of the expression
	 */
	private Symbol lastSymbolExpr;

	/**
	 * the number of this method. The first method of an object has number 0.
	 * This numbering is used for init, init:, new, and new: methods too.
	 */
	private final int methodNumber;

	/**
	 * true if this method was created by the compiler
	 */
	private boolean compilerCreatedMethod;

	/**
	 * methods are objects in Cyan. This is the name of the prototype created to
	 * represent this method.
	 */
	private String prototypeNameForMethod;

	/**
	 * symbol of the 'func' keyword that starts this method
	 */
	private Symbol firstSymbol;

	/**
	 * true if this method has been overloaded
	 */
	private boolean overload;

	/**
	 * true if this method is preceded by keyword 'overload'
	 */
	private boolean precededBy_overload;

	/**
	 * true if this method can access to non-shared fields. Of course, 'true' is
	 * the default value. However, if metaobject prototypeCallOnly is attached
	 * to this method, it cannot access non-shared fields
	 */
	private boolean allowAccessToFields;
	/**
	 * true if this method has any metaobject @javacode inside it
	 */
	private boolean hasJavaCode;
	/**
	 * true if this is an init or init: method. null if it has not been initialized
	 */
	private Boolean isInit;
	/**
	 * the name says it all. If this method is an 'init' or 'init:' method, if
	 * this variable is true a call to the constructor should be inserted in the
	 * generated Java code to call the super constructor that does not take parameters.
	 */
	private boolean shouldInsertCallToConstructorWithoutParametes;

	/**
	 * list of fields that appear in at least one assignment inside this method.
	 * null if none. The list includes both shared and non-shared fields
	 */
	private List<FieldDec> assignedToFieldList;
	/**
	 * list of fields that appear in any expression inside this method.
	 * That is, fields whose value is read inside this method. null if none.
	 * The list includes both shared and non-shared fields
	 */
	private List<FieldDec> readFromFieldList;

	/**
	 * list of methods of self called inside this method
	 */
	private List<MethodSignature> selfMessagePassingList;

	/**
	 * true if 'self' is passed as parameter in a message send
	 */
	private boolean selfLeak;
	/**
	 * the stack of metaobject annotations that created this method
	 */
	private Stack<Tuple5<String, String, String, String, Integer>> annotContextStack;

	/**
	 * true if this is a shared method
	 */
	private boolean shared;

}
