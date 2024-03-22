
package ast;

import java.util.ArrayList;
import java.util.List;
import error.ErrorKind;
import lexer.Lexer;
import lexer.Symbol;
import meta.CompilationInstruction;
import meta.CompilationStep;
import meta.ExprReceiverKind;
import meta.IdentStarKind;
import meta.LocalVarInfo;
import meta.MetaHelper;
import meta.WrExprIdentStar;
import meta.WrPrototype;
import meta.WrType;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
import saci.TupleTwo;

/**
 *
 *
 *
 * Represents a field, a local variable, a literal prototype, or an unary method
 * of the current object in an expression. In the following examples, both sides
 * of the assignment are represented by this class. n = size; // implicit self s
 * = Store; // Store is an object s = t; // t is a local variable s = pi; // pi
 * is a read only variable s = get; // get is a unary method.
 *
 * The literal prototypes may be preceded by a list of package names separated
 * by dots as in "cyan.lang.Int".
 *
 * More examples: var stack = util.ds.Stack; // util.ds is a package name and
 * Stack a literal object name var w = cyan.awt.lib.Window new;
 *
 * The literal object may be generic and therefore, in the source code, it may
 * be followed by type parameters such as in var intStack = util.ds.Stack<Int>;
 * In this case, "util.ds.Stack<Int>" will be represented by an object of class
 * ExprGenericType.
 *
 * @author José
 *
 */

public class ExprIdentStar extends Expr implements Identifier,
		LeftHandSideAssignment, IReceiverCompileTimeMessageSend {

	public ExprIdentStar(List<Symbol> identSymbolArray, Symbol nextSymbol,
			MethodDec method) {
		super(method);
		this.identSymbolArray = identSymbolArray;
		this.nextSymbol = nextSymbol;
		precededByRemaninder = false;
		this.identStarKind = null;
		nameWithPackageAndType = null;
		messageSendToAnnotation = null;
		originalJavaName = null;
	}

	@Override
	public WrExprIdentStar getI() {
		// TODO Auto-generated method stub
		return new WrExprIdentStar(this);
	}

	@Override
	public String asString() {
		if ( this.codeThatReplacesThisStatement != null ) {
			return this.codeThatReplacesThisStatement.toString();
		}
		else {
			return getNameWithAttachedTypes();
		}
	}

	public String getNameWithAttachedTypes() {
		String ret = getName();
		if ( annotationToTypeList != null ) {
			for (AnnotationAt annotType : annotationToTypeList) {
				ret += annotType.asString();
			}
		}
		return ret;
	}

	@Override
	public void accept(ASTVisitor visitor) {

		visitor.visit(this);
	}

	@Override
	public Object eval(EvalEnv ee) {
		/*
		 * possibilities: localVar unaryMS_toSelf field // not considered yet
		 * injectedVariable CyanPrototype cyan.lang.CyanPrototype
		 * JavaClassObject java.lang.JavaClass
		 */
		String id = this.getName();
		VariableDecInterface varDec = ee.searchLocalVar(id);
		if ( varDec != null ) {
			/*
			 * localVar
			 */
			this.identStarKind = IdentStarKind.variable_t;
			return varDec.getValueInInterpreter();
		}
		else {
			int indexDot = id.lastIndexOf('.');
			if ( indexDot < 0 ) {
				/*
				 * injectedVariable unaryMS_toSelf CyanPrototype JavaClassObject
				 */
				Object value = ee.getVariableValue(id);
				if ( value != null ) {
					/*
					 * injectedVariable
					 */
					this.identStarKind = IdentStarKind.injectedVariable_t;
					return value;
				}
				else {
					/*
					 * unaryMS_toSelf CyanPrototype JavaClassObject
					 */
					String javaNameId = MetaHelper.getJavaName(id);

					Object ret = ee.searchPrototypeAsExpression(javaNameId);

					if ( ret != null ) {
						/*
						 * CyanPrototype, return CyanPrototype.prototype
						 */
						this.identStarKind = IdentStarKind.prototype_t;
						if ( ret instanceof InterfaceDec ) {
							this.isInterface = true;
						}

						return ret;
					}
					else {
						/*
						 * unaryMS_toSelf JavaClassObject
						 */
						return evalUnaryMessageSendToSelf(ee);

					}
				}
			}
			else {
				/*
				 * possibilities: localVar field // not considered yet
				 * CyanPrototype cyan.lang.CyanPrototype JavaClassObject
				 * java.lang.JavaClass
				 */
				String packageName = id.substring(0, indexDot);
				String className = MetaHelper
						.getJavaName(id.substring(indexDot + 1));
				JVMPackage aPackage = ee.searchPackage(packageName);
				if ( aPackage != null ) {
					return aPackage.searchJVMClass(className);
				}
				else {
					return ee.searchJavaClass_MetaJavaLang(id);
				}
			}
		}
	}

	private Object evalUnaryMessageSendToSelf(EvalEnv ee) {
		Object receiverValue = ee.selfObject;
		// Class<?> receiverClass = receiverValue.getClass();

		String cyanMethodNameInJava = MetaHelper.getJavaName(this.getName());
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
				methodNameInJava, null, this.getName(),
				this.getFirstSymbol().getI(), ee);

		if ( ret != null ) {
			return ret;
		}
		return null;

	}

	public ExprIdentStar(MethodDec method, Symbol... symbolArray) {
		super(method);
		precededByRemaninder = false;
		this.identSymbolArray = new ArrayList<Symbol>(symbolArray.length);
		for (Symbol s : symbolArray)
			identSymbolArray.add(s);
	}

	@Override
	public boolean mayBeStatement() {
		return identStarKind == IdentStarKind.unaryMethod_t;
	}

	@Override
	public boolean addSemicolonJavaCode() {
		return true;
	}

	@Override
	public boolean isNRE(Env env) {
		return this.identSymbolArray.size() == 1 && saci.Compiler
				.isBasicType(this.identSymbolArray.get(0).token);
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {

		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			int size = identSymbolArray.size();
			for (Symbol s : identSymbolArray) {
				pw.print(Lexer.addSpaceAfterComma(cyanEnv
						.formalGenericParamToRealParam(s.getSymbolString())));
				--size;
				if ( size > 0 ) pw.print(".");
			}
			if ( annotationToTypeList != null ) {
				for (AnnotationAt annotType : annotationToTypeList) {
					pw.print(annotType.asString());
				}
			}

		}
		else {
			pw.print(this.getNameWithAttachedTypes());
		}

	}

	@Override
	public void genJavaCodeVariable(PWInterface pw, Env env) {
		// pw.print(genJavaExpr(pw, env));
		if ( this.varDeclaration == null )
			pw.print(genJavaExpr(pw, env));
		else {
			String jn = MetaHelper.getJavaName(this.varDeclaration.getName());
			if ( this.varDeclaration.getRefType() ) jn = jn + ".elem";
			pw.print(jn);
		}
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {

		if ( identStarKind == IdentStarKind.instance_variable_t
				|| identStarKind == IdentStarKind.variable_t ) {

			if ( this.varDeclaration.getTypeWasChanged() ) {
				javaName = varDeclaration.getJavaName();
			}
			else {
				javaName = varDeclaration.javaNameWithRef();
			}
		}
		return javaName;
	}

	@Override
	public final void genJava(PWInterface pw, Env env) {

		if ( identStarKind == IdentStarKind.instance_variable_t
				|| identStarKind == IdentStarKind.variable_t ) {

			if ( this.varDeclaration.getTypeWasChanged() ) {
				javaName = varDeclaration.getJavaName();
			}
			else {
				javaName = varDeclaration.javaNameWithRef();
			}
		}

		pw.printIdent(javaName);
	}

	@Override
	public Symbol getFirstSymbol() {
		return identSymbolArray.get(0);
	}

	public List<Symbol> getIdentSymbolArray() {
		return identSymbolArray;
	}

	public void setIdentSymbolArray(List<Symbol> identSymbolArray) {
		this.identSymbolArray = identSymbolArray;
	}

	@Override
	public String getName() {
		String ret = "";

		int size = identSymbolArray.size();
		for (Symbol s : identSymbolArray) {
			ret = ret + s.getSymbolString();
			--size;
			if ( size > 0 ) ret = ret + ".";
		}
		return ret;

	}

	/**
	 * the the last name of the identifier. If it is "main.util.Stack", this
	 * method returns "Stack"
	 *
	 * @return
	 */
	public String getLastName() {
		return identSymbolArray.get(identSymbolArray.size() - 1)
				.getSymbolString();
	}

	@Override
	public String getJavaName() {
		if ( this.identStarKind == IdentStarKind.jvmClass_t ) {
			return getName();
		}
		else {
			return NameServer.getJavaNameQualifiedIdentifier(identSymbolArray);
		}
	}

	public boolean getPrecededByRemaninder() {
		return precededByRemaninder;
	}

	public void setPrecededByRemaninder(boolean precededByRemaninder) {
		this.precededByRemaninder = precededByRemaninder;
	}

	@Override
	public void calcInternalTypes(Env env) {

		calcInternalTypes(env, false);
		super.calcInternalTypes(env);
	}

	/**
	 * an object of this class may represent a variable, parameter, unary method
	 * etc. In this case, calcType will set the type as the type of variable,
	 * parameter etc. However, an object of this class may also represent a
	 * prototype such as Person, Int, String, cyan.lang.Char. In this case, the
	 * type will be set as Person, Int, String etc.
	 *
	 * If leftHandSideAssignment is true, this identifier is in the left-hand
	 * side of a assignment as "f1" and "y" in f1 = y = other; In this case, f1
	 * and y should be local variables. They cannot be read only variables,
	 * unary methods, and parameters.
	 */
	@Override
	public void calcInternalTypes(Env env, boolean leftHandSideAssignment) {
		/*
		 *
		 * search to discover if this is a local variable, parameter, unary
		 * method, etc. If it is not, it may be a prototype
		 */

		Prototype currentPrototype = env.getCurrentPrototype();
		String name = getName();

		if ( name.equals(MetaHelper.dynName) ) {
			type = Type.Dyn;
			javaName = NameServer.javaDynName;
			identStarKind = IdentStarKind.prototype_t;
			if ( this.annotationToTypeList != null ) {
				// something like Char@letter
				TypeWithAnnotations twa;
				type = twa = new TypeWithAnnotations(type,
						annotationToTypeList);
				twa.checkAnnotation(env);
			}

		}
		else if ( identSymbolArray.size() == 1 ) {
			/*
			 * just one Id, no dots
			 */
			if ( currentPrototype == null ) {
				/**
				 * the identifier is outside a prototype or interface
				 * declaration.
				 */
				Prototype prototype = env.searchVisiblePrototype(name,
						this.getFirstSymbol(), false);
				// try to find a prototype
				if ( prototype != null ) {
					identStarKind = IdentStarKind.prototype_t;
					if ( prototype instanceof InterfaceDec ) {
						this.isInterface = true;
					}

					type = prototype;
					// foundIdent = true;
					if ( prototype instanceof InterfaceDec ) {
						javaName = MetaHelper
								.getJavaName(NameServer
										.prototypeFileNameFromInterfaceFileName(
												prototype.getName()))
								+ ".prototype";
					}
					else {
						javaName = MetaHelper.getJavaName(name) + ".prototype";
					}
					if ( this.annotationToTypeList != null ) {
						// something like Char@letter
						TypeWithAnnotations twa;
						type = twa = new TypeWithAnnotations(type,
								annotationToTypeList);
						twa.checkAnnotation(env);

					}
					if ( env.peekCheckUsePossiblyNonInitializedPrototype()
							&& !(prototype instanceof InterfaceDec)
							&& !env.getIsArgumentToIsA() ) {
						List<MethodSignature> initMSList = prototype
								.searchMethodPrivateProtectedPublicPackage(
										"init", env);
						if ( (initMSList == null || initMSList.size() == 0)
								&& type != Type.Nil ) {
							initMSList = prototype
									.searchMethodPrivateProtectedPublicPackage(
											"new", env);
							if ( (initMSList == null || initMSList.size() == 0)
									&& type != Type.Nil
									&& !env.getSemAnInUnionType() ) {
								/*
								 * It is illegal to use a prototype that does
								 * not have an 'init' method
								 */
								env.error(getFirstSymbol(), "Prototype '"
										+ this.getName() + "' does "
										+ "not have an 'init' method. Therefore its fields may not have"
										+ " been initialized (they are if an 'init' method does exist)");

								// initMSList =
								// prototype.searchMethodPrivateProtectedPublicPackage("new",
								// env);
								// if ( (initMSList == null || initMSList.size()
								// == 0) && type != Type.Nil
								// && ! env.getSemAnInUnionType() ) {
								// /*
								// * It is illegal to use a prototype that does
								// not have an
								// * 'init' method
								// */
								// env.error(getFirstSymbol(), "Prototype '" +
								// this.getName() + "' does "
								// + "not have an 'init' method. Therefore its
								// fields may not have"
								// + " been initialized (they are if an 'init'
								// method does exist)"
								// );
								// }

							}
						}
					}

				}
				else {
					type = Type.Dyn;
					env.error(getFirstSymbol(),
							"Identifier '" + name + "' was not found", true,
							true);
					// if ( !env.getProjectResTypes() &&
					// !name.equals(NameServer.THISPROTOTYPE999) ) {
					// }
					return;
				}

			}
			else if ( env.getCurrentMethod() == null ) { // &&
															// !name.equals(NameServer.THISPROTOTYPE999)
															// ) {
				calcInternalTypes_single_id_outside_method(env,
						currentPrototype, name);
			}
			else {
				calcInternalTypesSingleIdInsideMethod(env,
						leftHandSideAssignment, currentPrototype, name);
			}
		}
		else {
			// a composite identifier, with package as "math.Complex"
			if ( leftHandSideAssignment ) {
				type = Type.Dyn;
				env.error(getFirstSymbol(),
						"Identifier expected. Found '" + name + "'", true,
						true);
			}
			else {
				calcInternalTypesPackagePrototype(env, leftHandSideAssignment,
						currentPrototype, name);

			}
		}
	}

	/**
	 * @param env
	 * @param leftHandSideAssignment
	 * @param currentPrototype
	 * @param name
	 */
	private void calcInternalTypesPackagePrototype(Env env,
			boolean leftHandSideAssignment, Prototype currentPrototype,
			String name) {
		Prototype prototype;
		// something like "math.Complex", "cyan.lang.Function"
		// the last identifier should be a prototype name. The others are the
		// package name.
		// For example, in "cyan.lang.Function", "Function" is a prototype name
		// and "cyan.lang" is a package name
		int lastDot = name.lastIndexOf('.');
		String packageName = name.substring(0, lastDot);
		String prototypeName = name.substring(lastDot + 1);
		CyanPackage aPackage = env.getProject().searchPackage(packageName);
		if ( aPackage == null ) {
			// did not found a prototype
			// error(Symbol symbol, String specificMessage, String identifier,
			// ErrorKind errorKind ) {
			type = env.searchVisibleJavaClass(name);
			javaName = name;
			identStarKind = IdentStarKind.jvmClass_t;
			if ( type == null ) {
				type = Type.Dyn;
				env.error(getFirstSymbol(), "Package " + packageName
						+ " was not found. Make sure "
						+ "it is included in the project file (.pyan) or, "
						+ "if it is a Java package, its jar file is given after option -cp like in\n"
						+ "    saci projectFile.pyan -cp \"C:\\files\\lib\\sacilib.jar\"",
						true, true);
			}
			// experimental: Java class with attached annotations
			if ( this.annotationToTypeList != null ) {
				// something like Char@letter
				TypeWithAnnotations twa;
				type = twa = new TypeWithAnnotations(type,
						annotationToTypeList);
				twa.checkAnnotation(env);

			}

		}
		else {
			// found the package. Try to find the prototype
			prototype = aPackage.searchPublicNonGenericPrototype(prototypeName);
			if ( prototype != null ) {

				identStarKind = IdentStarKind.prototype_t;

				type = prototype;
				if ( prototype instanceof InterfaceDec ) {
					this.isInterface = true;
					javaName = MetaHelper.getJavaName(
							NameServer.prototypeFileNameFromInterfaceFileName(
									this.getName()))
							+ ".prototype";
				}
				else
					javaName = NameServer.getJavaNameQualifiedIdentifier(
							this.identSymbolArray) + ".prototype";
				if ( leftHandSideAssignment ) {
					env.error(true, getFirstSymbol(), "Prototype '" + name
							+ "' cannot be used in the left-hand side of an assignment",
							name,
							ErrorKind.prototype_cannot_be_used_in_the_left_hand_side_of_an_assignment);
				}
				if ( this.annotationToTypeList != null ) {
					// something like Char@letter
					TypeWithAnnotations twa;
					type = twa = new TypeWithAnnotations(type,
							annotationToTypeList);
					twa.checkAnnotation(env);

				}
				if ( env.peekCheckUsePossiblyNonInitializedPrototype()
						&& !(prototype instanceof InterfaceDec)
						&& !env.getIsArgumentToIsA() ) {
					List<MethodSignature> initMSList = prototype
							.searchMethodPrivateProtectedPublicPackage("init",
									env);
					if ( (initMSList == null || initMSList.size() == 0)
							&& type != Type.Nil ) {

						initMSList = prototype
								.searchMethodPrivateProtectedPublicPackage(
										"new", env);
						if ( initMSList == null
								|| initMSList.size() == 0 && type != Type.Nil
										&& !env.getSemAnInUnionType() ) {

							env.error(getFirstSymbol(), "Prototype '"
									+ this.getName() + "' does "
									+ "not have an 'init' method. Therefore its fields may not have"
									+ " been initialized (they are if an 'init' method does exist)");
							// /*
							// * It is illegal to use a prototype that does not
							// have an
							// * 'init' method
							// */
							// initMSList =
							// prototype.searchMethodPrivateProtectedPublicPackage("new",
							// env);
							// if ( (initMSList == null || initMSList.size() ==
							// 0) && type != Type.Nil
							// && ! env.getSemAnInUnionType() ) {
							// /*
							// * It is illegal to use a prototype that does not
							// have an
							// * 'init' method
							// */
							// env.error(getFirstSymbol(), "Prototype '" +
							// this.getName() + "' does "
							// + "not have an 'init' method. Therefore its
							// fields may not have"
							// + " been initialized (they are if an 'init'
							// method does exist)"
							// );
							// }
							//
							//
							// /*
							// * It is illegal to use a prototype that does not
							// have an
							// * 'init' method
							// */
							// env.error(getFirstSymbol(), "Prototype '" +
							// this.getName() + "' does "
							// + "not have an 'init' method. Therefore its
							// fields may not have"
							// + " been initialized (they are if an 'init'
							// method does exist)"
							// );
						}

					}
				}
				MethodDec currentMethod1 = env.getCurrentMethod();
				if ( currentMethod1 != null && currentPrototype != null
						&& currentMethod1.getName().equals("init")
						&& name.equals(currentPrototype.getName()) ) {
					env.error(getFirstSymbol(), "Prototype '" + name + "' "
							+ "uses itself in its 'init' method. However, fields from "
							+ "the prototype, considered as an object, are "
							+ "initialized using the same 'init' method. Therefore,"
							+ " in the first call to 'init', to initialize the "
							+ "prototype fields, there would be a NullPointerException in Java");
				}

			}
			else {

				prototype = env.searchPrivatePrototype(prototypeName);
				if ( prototype == null ) {
					// env.searchVisiblePrototype(name, this.getFirstSymbol(),
					// true);
					// currentPrototype.searchMethodPrivateProtectedPublicSuperProtectedPublic(name,
					// env);
					/*
					 * env.error(true, getFirstSymbol(), "Identifier '" + name +
					 * "' was not declared", name,
					 * ErrorKind.variable_was_not_declared);
					 */
					type = Type.Dyn;
					env.error(getFirstSymbol(),
							"Identifier '" + name + "' was not declared", true,
							true);

				}
				else {
					identStarKind = IdentStarKind.prototype_t;
					type = prototype;
					if ( prototype instanceof InterfaceDec ) {
						this.isInterface = true;
						javaName = MetaHelper.getJavaName(NameServer
								.prototypeFileNameFromInterfaceFileName(
										prototypeName))
								+ ".prototype";
					}
					else
						javaName = MetaHelper.getJavaName(prototypeName)
								+ ".prototype";
					if ( leftHandSideAssignment ) {
						env.error(true, getFirstSymbol(), "Prototype '" + name
								+ "' cannot be used in the left-hand side of an assignment",
								name,
								ErrorKind.prototype_cannot_be_used_in_the_left_hand_side_of_an_assignment);
					}
					if ( this.annotationToTypeList != null ) {
						// something like Char@letter
						TypeWithAnnotations twa;
						type = twa = new TypeWithAnnotations(type,
								annotationToTypeList);
						twa.checkAnnotation(env);

					}
					if ( env.peekCheckUsePossiblyNonInitializedPrototype()
							&& !(prototype instanceof InterfaceDec)
							&& !env.getIsArgumentToIsA() ) {
						List<MethodSignature> initMSList = prototype
								.searchMethodPrivateProtectedPublicPackage(
										"init", env);
						if ( (initMSList == null || initMSList.size() == 0)
								&& type != Type.Nil
								&& !env.getSemAnInUnionType() ) {
							env.error(getFirstSymbol(), "Prototype '"
									+ this.getName() + "' does "
									+ "not have an 'init' method. Therefore its fields may not have"
									+ " been initialized (they are if an 'init' method does exist)");

							// initMSList =
							// prototype.searchMethodPrivateProtectedPublicPackage("new",
							// env);
							// if ( initMSList == null || initMSList.size() == 0
							// ) {
							//
							// /*
							// * It is illegal to use a prototype that does not
							// have an
							// * 'init' method
							// */
							// initMSList =
							// prototype.searchMethodPrivateProtectedPublicPackage("new",
							// env);
							// if ( (initMSList == null || initMSList.size() ==
							// 0) && type != Type.Nil
							// && ! env.getSemAnInUnionType()) {
							// /*
							// * It is illegal to use a prototype that does not
							// have an
							// * 'init' method
							// */
							// env.error(getFirstSymbol(), "Prototype '" +
							// this.getName() + "' does "
							// + "not have an 'init' method. Therefore its
							// fields may not have"
							// + " been initialized (they are if an 'init'
							// method does exist)"
							// );
							// }
							//
							//
							// /*
							// * It is illegal to use a prototype that does not
							// have an
							// * 'init' method
							// */
							// }

						}
					}
					MethodDec currentMethod1 = env.getCurrentMethod();
					if ( currentMethod1 != null && currentPrototype != null
							&& currentMethod1.getName().equals("init")
							&& name.equals(currentPrototype.getName()) ) {
						env.error(getFirstSymbol(), "Prototype '" + name + "' "
								+ "uses itself in its 'init' method. However, fields from "
								+ "the prototype, considered as an object, are "
								+ "initialized using the same 'init' method. Therefore,"
								+ " in the first call to 'init', to initialize the "
								+ "prototype fields, there would be a NullPointerException in Java");
					}

				}

				/*
				 * type = Type.Dyn; env.error(getFirstSymbol(), "Prototype '" +
				 * prototypeName + "' was not found in package '" + packageName
				 * + "'", true, false); // prototype == null, prototype was not
				 * found
				 *
				 * if ( env.getCurrentMethod() == null ) {
				 *
				 * if ( env.getCurrentPrototype() == null ) { env.error(true,
				 * getFirstSymbol(), "Prototype '" + prototypeName +
				 * "' was not found in package '" + packageName + "'", name,
				 * ErrorKind.prototype_was_not_found_outside_prototype); } else
				 * { env.error(true, getFirstSymbol(), "Prototype '" +
				 * prototypeName + "' was not found in package '" + packageName
				 * + "'", name,
				 * ErrorKind.prototype_was_not_found_inside_prototyped); }
				 *
				 * } else { env.error(true, getFirstSymbol(), "Prototype " +
				 * name + " was not found", name,
				 * ErrorKind.prototype_was_not_found_inside_method); }
				 */

			}
		}
	}

	/**
	 * @param env
	 * @param leftHandSideAssignment
	 * @param currentPrototype
	 * @param name
	 */
	private void calcInternalTypesSingleIdInsideMethod(Env env,
			boolean leftHandSideAssignment, Prototype currentPrototype,
			String name) {

		Prototype prototype;
		// inside a method

		type = null;

		if ( env.getEnclosingObjectDec() == null ) {
			calcInternalTypes_single_id_inside_method_outer_prototype(env,
					leftHandSideAssignment, currentPrototype, name);
		}
		else {
			/*
			 * inside an inner prototype
			 */
			if ( NameServer.isMethodNameEval(
					env.getCurrentMethod().getNameWithoutParamNumber()) ) {
				calcInternalTypes_single_id_inside_method_inner_prototype_in_eval(
						env, leftHandSideAssignment, name);
			}
			else {
				calcInternalTypes_single_id_inside_method_inner_prototype_NOT_eval(
						env, leftHandSideAssignment, currentPrototype, name);
			}
		}
		if ( type == null ) {

			/*
			 * did not find an unary method. Search for a program unit
			 */
			prototype = env.searchPrivatePrototype(name);
			if ( prototype == null ) prototype = env
					.searchVisiblePrototype(name, this.getFirstSymbol(), true);
			if ( prototype == null ) {
				WrType newType = env.searchType(name);
				if ( newType != null ) {
					if ( !(newType instanceof WrPrototype) ) {
						env.error(this.getFirstSymbol(),
								"New types should be associated to Cyan prototypes");
						return;
					}
					else {
						prototype = meta.GetHiddenItem
								.getHiddenPrototype((WrPrototype) newType);
					}
				}
			}
			if ( prototype == null ) {

				TypeJavaRef javaClass = env.searchVisibleJavaClass(name);
				if ( javaClass == null ) {

					if ( this.identSymbolArray.size() == 1
							&& env.getCompInstSet().contains(
									CompilationInstruction.semAn_actions)
							&& super.lookForUnaryMethodAtCompileTime(null,
									identSymbolArray.get(0).getI(),
									env.getCurrentObjectDec(), env) ) {
						return;
					}

					type = Type.Dyn;
					env.error(getFirstSymbol(),
							"Identifier '" + name + "' was not declared", true,
							true);
					// if ( !name.equals(NameServer.THISPROTOTYPE999) ) {
					// }
				}
				else {
					this.identStarKind = IdentStarKind.jvmClass_t;
					type = javaClass;
					javaName = javaClass.getJavaName();
					if ( leftHandSideAssignment ) {
						env.error(getFirstSymbol(), "Java class '" + name
								+ "' cannot be used in the left-hand side of an assignment",
								true, true);
					}
					// experimental: Java class with attached annotations
					if ( this.annotationToTypeList != null ) {
						// something like Char@letter
						TypeWithAnnotations twa;
						type = twa = new TypeWithAnnotations(type,
								annotationToTypeList);
						twa.checkAnnotation(env);

					}

				}

			}
			else {
				identStarKind = IdentStarKind.prototype_t;
				type = prototype;
				// foundIdent = true;
				if ( prototype instanceof InterfaceDec ) {
					this.isInterface = true;
					javaName = MetaHelper.getJavaName(
							NameServer.prototypeFileNameFromInterfaceFileName(
									prototype.getName()))
							+ ".prototype";
				}
				else
					javaName = MetaHelper.getJavaName(name) + ".prototype";
				if ( leftHandSideAssignment ) {
					env.error(getFirstSymbol(), "Prototype '" + name
							+ "' cannot be used in the left-hand side of an assignment",
							true, true);
				}
				if ( this.annotationToTypeList != null ) {
					// something like Char@letter
					TypeWithAnnotations twa;
					type = twa = new TypeWithAnnotations(type,
							annotationToTypeList);
					twa.checkAnnotation(env);

				}
				if ( env.peekCheckUsePossiblyNonInitializedPrototype()
						&& !(prototype instanceof InterfaceDec)
						&& !env.getIsArgumentToIsA() ) {
					List<MethodSignature> initMSList = prototype
							.searchMethodPrivateProtectedPublicPackage("init",
									env);
					if ( (initMSList == null || initMSList.size() == 0)
							&& type != Type.Nil ) {

						initMSList = prototype
								.searchMethodPrivateProtectedPublicPackage(
										"new", env);
						if ( initMSList == null || initMSList.size() == 0 ) {

							/*
							 * It is illegal to use a prototype that does not
							 * have an 'init' method
							 */
							initMSList = prototype
									.searchMethodPrivateProtectedPublicPackage(
											"new", env);
							if ( (initMSList == null || initMSList.size() == 0)
									&& type != Type.Nil ) {
								/*
								 * It is illegal to use a prototype that does
								 * not have an 'init' method
								 */
								if ( !prototype
										.allFieldsInitializedInDeclaration() ) {
									env.error(getFirstSymbol(), "Prototype '"
											+ this.getName() + "' does "
											+ "not have an 'init' method. Therefore its fields may not have"
											+ " been initialized (they are if an 'init' method does exist)");
								}
							}

							/*
							 * It is illegal to use a prototype that does not
							 * have an 'init' method
							 */
							if ( !prototype
									.allFieldsInitializedInDeclaration() ) {
								env.error(getFirstSymbol(), "Prototype '"
										+ this.getName() + "' does "
										+ "not have an 'init' method. Therefore its fields may not have"
										+ " been initialized (they are if an 'init' method does exist)");
							}
						}

					}
				}
				MethodDec currentMethod1 = env.getCurrentMethod();
				if ( currentMethod1 != null && currentPrototype != null
						&& currentMethod1.getName().equals("init")
						&& name.equals(currentPrototype.getName()) ) {
					env.error(getFirstSymbol(), "Prototype '" + name + "' "
							+ "uses itself in its 'init' method. However, fields from "
							+ "the prototype, considered as an object, are "
							+ "initialized using the same 'init' method. Therefore,"
							+ " in the first call to 'init', to initialize the "
							+ "prototype fields, there would be a NullPointerException in Java");
				}

			}
		}

	}

	/**
	 * @param env
	 * @param leftHandSideAssignment
	 * @param currentPrototype
	 * @param name
	 */
	private void calcInternalTypes_single_id_inside_method_inner_prototype_NOT_eval(
			Env env, boolean leftHandSideAssignment, Prototype currentPrototype,
			String name) {
		/*
		 * inside a method of an inner prototype that is not 'eval', 'eval:eval:
		 * ...'
		 */

		VariableDecInterface varDec;
		if ( NameServer
				.isNameInnerProtoForContextFunction(currentPrototype.getName())
				&& env.getCurrentMethod().getName()
						.equals(NameServer.bindToFunctionWithParamNumber) ) {
			/*
			 * The identifiers visible inside the function body are those
			 * declared in the function itself, those accessible through {\tt
			 * T}, external parameters, and local variables (in this order ---
			 * the order is important if, for example, {\tt T} declares an unary
			 * method {\tt unMeth} and there is a local variable with this same
			 * name).
			 */
			varDec = env.searchLocalVariableParameter(name);
			if ( varDec != null ) {
				if ( varDec instanceof StatementLocalVariableDec ) {
					StatementLocalVariableDec vd = (StatementLocalVariableDec) varDec;
					if ( !vd.getDeclaringFunction().isContextFunction() ) {

					}
				}
			}
			List<MethodSignature> methodSignatureList;
			if ( varDec == null ) {
				String protoName = currentPrototype.getName();
				/*
				 * if the method is bindToFunction in a context function, all
				 * message sends to self are considered as message sends to
				 * newSelf__ whose type is the type of the first parameter of
				 * bindToFunction
				 */
				if ( NameServer.isNameInnerProtoForContextFunction(protoName)
						&& env.getCurrentMethod().getName().equals(
								NameServer.bindToFunctionWithParamNumber) ) {
					Type t = env.getCurrentMethod().getMethodSignature()
							.getParameterList().get(0).getType();
					if ( t == null ) {
						/*
						 * calculating the type of items of the return value
						 * type of method bindToFunction. So it is not necessary
						 * to search for methods. Look for prototypes (below)
						 */
						methodSignatureList = null;
					}
					else {
						methodSignatureList = t
								.searchMethodPublicPackageSuperPublicPackage(
										name, env);

						if ( name.equals("init") ) {
							env.error(this.getFirstSymbol(),
									"'init' and 'init:' messages can only be sent to 'super' inside an 'init' or 'init:' method",
									true, true);

							/*
							 * // message sends to 'init' inside any methods,
							 * including 'init:' or 'init:', are currently
							 * illegal
							 *
							 * if ( env.getCurrentMethod() != null ) { String
							 * initName =
							 * env.getCurrentMethod().getNameWithoutParamNumber(
							 * ); if ( !initName.equals("init") &&
							 * !initName.equals("init:") ) {
							 * env.error(this.getFirstSymbol(),
							 * "'init' and 'init:' methods can only be called inside other 'init' or 'init:' methods"
							 * ); } } if ( env.getFunctionStack().size() > 0 ) {
							 * env.error(this.getFirstSymbol(),
							 * "'init' and 'init:' messages cannot be sent inside anonymous functions"
							 * ); }
							 */
						}

						if ( methodSignatureList != null
								&& methodSignatureList.size() > 0 ) {
							methodSignatureForMessageSend = methodSignatureList
									.get(0);
							methodSignatureForMessageSend
									.calcInterfaceTypes(env);

							/*
							 * if ( env.getProject().getCompilerManager().
							 * getCompilationStep() == CompilationStep.step_9 )
							 * { MetaInfoServer.
							 * checkMessageSendWithMethodMetaobject(
							 * methodSignatureList, t, null,
							 * ExprReceiverKind.SELF_R, env,
							 * this.identSymbolArray.get(0)); }
							 */

							identStarKind = IdentStarKind.unaryMethod_t;
							MethodDec currentMethod1 = env.getCurrentMethod();
							currentMethod1.addSelfMessagePassing(
									methodSignatureForMessageSend);
							currentMethod1.setSelfLeak(true);

							type = methodSignatureForMessageSend
									.getReturnType(env);
							javaName = NameServer.selfNameContextObject + "."
									+ NameServer.getJavaNameOfUnaryMethod(name)
									+ "()";
							if ( leftHandSideAssignment ) {
								env.error(getFirstSymbol(), "Unary method '"
										+ name
										+ "' cannot be used in the left-hand side of an assignment",
										true, true);
							}
							if ( this.annotationToTypeList != null ) {
								// something like Char@letter
								env.error(this.getFirstSymbol(),
										"Metaobject annotation attached to an unary method keyword");
							}

							// if (
							// env.getProject().getCompilerManager().getCompilationStep()
							// == CompilationStep.step_9 ) {
							// MetaInfoServer.checkMessageSendWithMethodMetaobject(methodSignatureList,
							// currentPrototype, receiverExprOrFirstUnary,
							// receiverKind, env, unarySymbol);
							// }
							//
							//
							// if (
							// env.getProject().getCompilerManager().getCompilationStep().ordinal()
							// < CompilationStep.step_7.ordinal() ) {
							//
							// // this test should be redundant
							//
							// //TODO this search should not be made again. The
							// code below should replace
							// // the previous search
							// List<MethodSignature> allMethodSignatureList =
							// new ArrayList<>();
							// List<Prototype> superList =
							// currentPrototype.getAllSuperPrototypes();
							// for ( Prototype current : superList ) {
							// List<MethodSignature> currentMSList =
							// current.searchMethodPublicPackage(name, env);
							// if ( currentMSList != null ) {
							// allMethodSignatureList.addAll(currentMSList);
							// }
							// }
							// type =
							// MetaInfoServer.replaceMessageSendIfAsked(allMethodSignatureList,
							// this,
							// env, this.getFirstSymbol(), type);
							// }

						}

						/*
						 * if an unary method is not found, below there is a
						 * search for a prototype called 'name'
						 */

					}

					/*
					 * in fun bindToFunction: IColor newSelf__ ->
					 * UFunction<String> { return { (: -> String :) ^
					 * colorTable[newSelf__ color] } } this code is searching
					 * for String in the type of newSelf__. At least t should be
					 * != null. But it is equal to null.
					 *
					 */
				}

			}
			varDec = env.searchVariableInBindToFunction(name);

		}
		else {
			varDec = env.searchVariableIn_NOT_EvalOfInnerPrototypes(name);
		}

		if ( varDec != null ) {
			if ( varDec instanceof FieldDec ) {
				identStarKind = IdentStarKind.instance_variable_t;
			}
			else {
				identStarKind = IdentStarKind.variable_t;

				if ( varDec instanceof StatementLocalVariableDec ) {
					StatementLocalVariableDec localVar = (StatementLocalVariableDec) varDec;
					LocalVarInfo varInfo = env.getLocalVariableInfo(localVar);
					if ( !varInfo.initialized && !leftHandSideAssignment
							&& !env.getHasJavaCode() ) {
						env.error(this.getFirstSymbol(),
								"Variable '" + varDec.getName()
										+ "' may not have been initialized");
					}
				}
			}

			this.varDeclaration = varDec;

			type = varDec.getType();
			javaName = varDec.javaNameWithRef();
			setOriginalJavaName(varDec.getJavaName());

			if ( identStarKind == IdentStarKind.instance_variable_t
					&& !leftHandSideAssignment ) {
				MethodDec currentMethod1 = env.getCurrentMethod();
				if ( currentMethod1 != null ) {
					currentMethod1.addReadFromFieldList((FieldDec) varDec);
				}
				ExprSelfPeriodIdent.actionFieldAccess_semAn(env, this,
						(FieldDec) varDec);
			}

			if ( leftHandSideAssignment ) {

				if ( varDec instanceof FieldDec ) {
					if ( ((FieldDec) varDec).isReadonly()
							&& !env.getCurrentMethod().isInitMethod()
							&& !env.getCurrentMethod().isInitShared() ) {
						env.error(getFirstSymbol(), "Identifier '" + name
								+ "' cannot be used in the left-hand side of an assignment",
								true, true);
					}
				}
			}
			if ( this.annotationToTypeList != null ) {
				env.error(this.getFirstSymbol(),
						"Metaobject annotation attached to a variable");
			}

		}
		else {
			/*
			 * search for an unary method in the CURRENT prototype only, which
			 * is an inner prototype.
			 */
			List<MethodSignature> methodSignatureList;
			Prototype pu = currentPrototype;
			String protoName = currentPrototype.getName();
			/*
			 * if the method is bindToFunction in a context function, all
			 * message sends to self are considered as message sends to
			 * newSelf__ whose type is the type of the first parameter of
			 * bindToFunction
			 */
			if ( NameServer.isNameInnerProtoForContextFunction(protoName)
					&& env.getCurrentMethod().getName().equals(
							NameServer.bindToFunctionWithParamNumber) ) {
				Type t = env.getCurrentMethod().getMethodSignature()
						.getParameterList().get(0).getType();
				if ( t == null ) {
					/*
					 * calculating the type of items of the return value type of
					 * method bindToFunction. So it is not necessary to search
					 * for methods. Look for prototypes (below)
					 */
					methodSignatureList = null;
				}
				else {
					methodSignatureList = t
							.searchMethodPublicPackageSuperPublicPackage(name,
									env);

					if ( name.equals("init") ) {
						env.error(this.getFirstSymbol(),
								"'init' and 'init:' messages can only be sent to 'super' inside an 'init' or 'init:' method",
								true, true);

						/*
						 * // message sends to 'init' inside any methods,
						 * including 'init:' or 'init:', are currently illegal
						 *
						 *
						 * if ( env.getCurrentMethod() != null ) { String
						 * initName =
						 * env.getCurrentMethod().getNameWithoutParamNumber();
						 * if ( !initName.equals("init") &&
						 * !initName.equals("init:") ) {
						 * env.error(this.getFirstSymbol(),
						 * "'init' and 'init:' methods can only be called inside other 'init' or 'init:' methods"
						 * ); } if ( env.getFunctionStack().size() > 0 ) {
						 * env.error(this.getFirstSymbol(),
						 * "'init' and 'init:' messages cannot be sent inside anonymous functions"
						 * ); } }
						 */
					}

					if ( methodSignatureList == null
							&& (t instanceof ObjectDec) ) {
						methodSignatureList = ((ObjectDec) t)
								.searchInitNewMethod(name);
					}
					if ( methodSignatureList != null
							&& methodSignatureList.size() > 0 ) {
						methodSignatureForMessageSend = methodSignatureList
								.get(0);
						methodSignatureForMessageSend.calcInterfaceTypes(env);

						if ( env.getProject().getCompilerManager()
								.getCompilationStep() == CompilationStep.step_9 ) {
							MetaInfoServer.checkMessageSendWithMethodMetaobject(
									methodSignatureList, t, null,
									ExprReceiverKind.SELF_R, env,
									this.identSymbolArray.get(0));
						}

						identStarKind = IdentStarKind.unaryMethod_t;
						env.getCurrentMethod().setSelfLeak(true);

						type = methodSignatureList.get(0).getReturnType(env);
						javaName = NameServer.selfNameContextObject + "."
								+ NameServer.getJavaNameOfUnaryMethod(name)
								+ "()";

						if ( leftHandSideAssignment ) {
							env.error(getFirstSymbol(), "Unary method '" + name
									+ "' cannot be used in the left-hand side of an assignment",
									true, true);
							/*
							 * env.error(true, getFirstSymbol(),
							 * "Unary method '" + name +
							 * "' cannot be used in the left-hand side of an assignment"
							 * , name, ErrorKind.
							 * unary_method_cannot_be_used_in_the_left_hand_side_of_an_assignment
							 * );
							 */
						}
						if ( this.annotationToTypeList != null ) {
							env.error(this.getFirstSymbol(),
									"Metaobject annotation attached to an unary method keyword");
						}

						if ( env.getProject().getCompilerManager()
								.getCompilationStep()
								.ordinal() < CompilationStep.step_7
										.ordinal() ) {

							type = MetaInfoServer.replaceMessageSendIfAsked(
									methodSignatureList, this, env,
									this.getFirstSymbol(), type);
						}

					}
				}

				/*
				 * in fun bindToFunction: IColor newSelf__ -> UFunction<String>
				 * { return { (: -> String :) ^ colorTable[newSelf__ color] } }
				 * this code is searching for String in the type of newSelf__.
				 * At least t should be != null. But it is equal to null.
				 *
				 */
			}
			else {
				methodSignatureList = pu
						.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
								name, env);

				if ( name.equals("init") ) {
					env.error(this.getFirstSymbol(),
							"'init' and 'init:' messages can only be sent to 'super' inside an 'init' or 'init:' method",
							true, true);

					/*
					 * // message sends to 'init' inside any methods, including
					 * 'init:' or 'init:', are currently illegal
					 *
					 *
					 * if ( env.getCurrentMethod() != null ) { String initName =
					 * env.getCurrentMethod().getNameWithoutParamNumber(); if (
					 * !initName.equals("init") && !initName.equals("init:") ) {
					 * env.error(this.getFirstSymbol(),
					 * "'init' and 'init:' methods can only be called inside other 'init' or 'init:' methods"
					 * ); } if ( env.getFunctionStack().size() > 0 ) {
					 * env.error(this.getFirstSymbol(),
					 * "'init' and 'init:' messages cannot be sent inside anonymous functions"
					 * ); } }
					 */
				}

				if ( methodSignatureList == null
						&& (pu instanceof ObjectDec) ) {
					methodSignatureList = ((ObjectDec) pu)
							.searchInitNewMethod(name);
				}

				if ( methodSignatureList != null
						&& methodSignatureList.size() > 0 ) {
					methodSignatureForMessageSend = methodSignatureList.get(0);
					methodSignatureForMessageSend.calcInterfaceTypes(env);
					if ( env.getProject().getCompilerManager()
							.getCompilationStep() == CompilationStep.step_9 ) {
						MetaInfoServer.checkMessageSendWithMethodMetaobject(
								methodSignatureList, pu, null,
								ExprReceiverKind.SELF_R, env,
								this.getFirstSymbol());
					}

					identStarKind = IdentStarKind.unaryMethod_t;
					env.getCurrentMethod().setSelfLeak(true);

					type = methodSignatureForMessageSend.getReturnType(env);
					javaName = NameServer.getJavaNameOfUnaryMethod(name) + "()";
					if ( this.annotationToTypeList != null ) {
						env.error(this.getFirstSymbol(),
								"Metaobject annotation attached to an unary method keyword");
					}

					if ( env.getProject().getCompilerManager()
							.getCompilationStep()
							.ordinal() < CompilationStep.step_7.ordinal() ) {

						type = MetaInfoServer.replaceMessageSendIfAsked(
								methodSignatureList, this, env,
								this.getFirstSymbol(), type);
					}

				}
			}
			if ( methodSignatureList != null
					&& methodSignatureList.size() > 0 ) {
				if ( leftHandSideAssignment ) {
					env.error(getFirstSymbol(), "Unary method '" + name
							+ "' cannot be used in the left-hand side of an assignment",
							true, true);
					/*
					 * env.error(true, getFirstSymbol(), "Unary method '" + name
					 * +
					 * "' cannot be used in the left-hand side of an assignment"
					 * , name, ErrorKind.
					 * unary_method_cannot_be_used_in_the_left_hand_side_of_an_assignment
					 * );
					 */

				}
			}
			/*
			 * if an unary method is not found, below there is a search for a
			 * prototype called 'name'
			 */

		}
	}

	/**
	 * @param env
	 * @param leftHandSideAssignment
	 * @param name
	 */
	private void calcInternalTypes_single_id_inside_method_inner_prototype_in_eval(
			Env env, boolean leftHandSideAssignment, String name) {
		/*
		 * inside an 'eval' or 'eval:eval: ...' method of an inner prototype
		 */

		VariableDecInterface varDec = env
				.searchVariableInEvalOfInnerPrototypes(name);
		if ( varDec != null ) {

			if ( varDec instanceof FieldDec )
				identStarKind = IdentStarKind.instance_variable_t;
			else {
				identStarKind = IdentStarKind.variable_t;
				if ( varDec instanceof StatementLocalVariableDec ) {
					StatementLocalVariableDec localVar = (StatementLocalVariableDec) varDec;
					LocalVarInfo varInfo = env.getLocalVariableInfo(localVar);
					if ( !varInfo.initialized && !leftHandSideAssignment
							&& !env.getHasJavaCode() ) {
						env.error(this.getFirstSymbol(),
								"Variable '" + varDec.getName()
										+ "' may not have been initialized");
					}
				}

			}
			this.varDeclaration = varDec;
			type = varDec.getType();
			if ( varDec.getTypeWasChanged() ) {
				javaName = varDec.getJavaName(); // NameServer.getJavaName(varDec.getName());
			}
			else {
				javaName = varDec.javaNameWithRef();
			}
			setOriginalJavaName(varDec.getJavaName());

			if ( identStarKind == IdentStarKind.instance_variable_t
					&& !leftHandSideAssignment ) {
				MethodDec currentMethod1 = env.getCurrentMethod();
				if ( currentMethod1 != null ) {
					currentMethod1.addReadFromFieldList((FieldDec) varDec);
				}
				ExprSelfPeriodIdent.actionFieldAccess_semAn(env, this,
						(FieldDec) varDec);
			}
			if ( leftHandSideAssignment ) {

				if ( varDec instanceof FieldDec ) {
					if ( ((FieldDec) varDec).isReadonly()
							&& !env.getCurrentMethod().isInitMethod()
							&& !env.getCurrentMethod().isInitShared() ) {
						env.error(getFirstSymbol(), "Identifier '" + name
								+ "' cannot be used in the left-hand side of an assignment",
								true, true);
						/*
						 * env.error(true, getFirstSymbol(), "Identifier '" +
						 * name +
						 * "' cannot be used in the left-hand side of an assignment"
						 * , name, ErrorKind.
						 * identifier_cannot_be_used_in_the_left_hand_side_of_an_assignment
						 * );
						 */
					}
				}
			}
			if ( this.annotationToTypeList != null ) {
				env.error(this.getFirstSymbol(),
						"Metaobject annotation attached to a variable");
			}

		}
		else {
			/*
			 * search for an unary method in the outer prototype
			 */

			ObjectDec outer = env.getCurrentObjectDec().getOuterObject();
			List<MethodSignature> methodSignatureList = outer
					.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
							name, env);

			if ( name.equals("init") ) {

				env.error(this.getFirstSymbol(),
						"'init' and 'init:' messages can only be sent to 'super' inside an 'init' or 'init:' method",
						true, true);

				/*
				 * // message sends to 'init' inside any methods, including
				 * 'init:' or 'init:', are currently illegal
				 *
				 *
				 * if ( env.getCurrentMethod() != null ) { String initName =
				 * env.getCurrentMethod().getNameWithoutParamNumber(); if (
				 * !initName.equals("init") && !initName.equals("init:") ) {
				 * env.error(this.getFirstSymbol(),
				 * "'init' and 'init:' methods can only be called inside other 'init' or 'init:' methods"
				 * ); } if ( env.getFunctionStack().size() > 0 ) {
				 * env.error(this.getFirstSymbol(),
				 * "'init' and 'init:' messages cannot be sent inside anonymous functions"
				 * ); } }
				 */
			}

			if ( methodSignatureList == null
					|| methodSignatureList.size() == 0 ) {
				methodSignatureList = outer.searchInitNewMethod(name);
			}

			if ( methodSignatureList != null
					&& methodSignatureList.size() > 0 ) {

				methodSignatureForMessageSend = methodSignatureList.get(0);
				methodSignatureForMessageSend.calcInterfaceTypes(env);

				/*
				 * if (
				 * env.getProject().getCompilerManager().getCompilationStep() ==
				 * CompilationStep.step_9 ) {
				 * MetaInfoServer.checkMessageSendWithMethodMetaobject(
				 * methodSignatureList, outer, null, ExprReceiverKind.SELF_R,
				 * env, this.getFirstSymbol()); }
				 */

				identStarKind = IdentStarKind.unaryMethod_t;
				MethodDec currentMethod1 = env.getCurrentMethod();
				currentMethod1
						.addSelfMessagePassing(methodSignatureForMessageSend);
				currentMethod1.setSelfLeak(true);

				type = methodSignatureForMessageSend.getReturnType(env);
				javaName = NameServer.javaSelfNameInnerPrototypes + "."
						+ NameServer.getJavaNameOfUnaryMethod(name) + "()";

				if ( leftHandSideAssignment ) {
					env.error(getFirstSymbol(), "Unary method '" + name
							+ "' cannot be used in the left-hand side of an assignment",
							true, true);
					/*
					 * env.error(true, getFirstSymbol(), "Unary method '" + name
					 * +
					 * "' cannot be used in the left-hand side of an assignment"
					 * , name, ErrorKind.
					 * unary_method_cannot_be_used_in_the_left_hand_side_of_an_assignment
					 * );
					 */
				}
				if ( this.annotationToTypeList != null ) {
					env.error(this.getFirstSymbol(),
							"Metaobject annotation attached to an unary method keyword");
				}

				if ( env.getProject().getCompilerManager().getCompilationStep()
						.ordinal() < CompilationStep.step_7.ordinal() ) {

					type = MetaInfoServer.replaceMessageSendIfAsked(
							methodSignatureList, this, env,
							this.getFirstSymbol(), type);
				}

			}
			/*
			 * if an unary method is not found, below there is a search for a
			 * prototype called 'name'
			 */

		}
	}

	/**
	 * @param env
	 * @param leftHandSideAssignment
	 * @param currentPrototype
	 * @param name
	 */
	private void calcInternalTypes_single_id_inside_method_outer_prototype(
			Env env, boolean leftHandSideAssignment, Prototype currentPrototype,
			String name) {
		/*
		 * inside a regular prototype that is NOT inside another prototype
		 */
		VariableDecInterface varDec = env.searchVariable(name);
		if ( varDec != null ) {

			if ( varDec instanceof FieldDec ) {
				identStarKind = IdentStarKind.instance_variable_t;
				MethodDec currentMethod1 = env.getCurrentMethod();
				if ( currentMethod1 != null ) {
					currentMethod1.addToAccessedFieldSet((FieldDec) varDec);
					FieldDec fieldDec = (FieldDec) varDec;
					if ( currentMethod1.getShared() ) {
						if ( !fieldDec.isShared() ) {
							env.error(this.getFirstSymbol(),
									"Shared methods cannot access non-shared fields. Method '"
											+ currentMethod1.getName()
											+ "' is accessing field '"
											+ fieldDec.getName() + "'");
						}
					}

				}
			}
			else {
				identStarKind = IdentStarKind.variable_t;
				if ( varDec instanceof StatementLocalVariableDec ) {
					StatementLocalVariableDec localVar = (StatementLocalVariableDec) varDec;
					LocalVarInfo varInfo = env.getLocalVariableInfo(localVar);
					if ( !varInfo.initialized && !leftHandSideAssignment
							&& !env.getHasJavaCode() ) {
						env.error(this.getFirstSymbol(),
								"Variable '" + varDec.getName()
										+ "' may not have been initialized");
					}
				}

			}

			this.varDeclaration = varDec;
			type = varDec.getType();
			javaName = varDec.javaNameWithRef();

			if ( this.annotationToTypeList != null ) {
				env.error(this.getFirstSymbol(),
						"Metaobject annotation attached to a variable");
			}

			if ( (varDec instanceof FieldDec) && !((FieldDec) varDec).isShared()
					&& !env.getCurrentMethod().getAllowAccessToFields() ) {
				/*
				 * access to fields is not allowed
				 */
				env.error(this.getFirstSymbol(),
						"fields are not allowed in this method. Probable cause: "
								+ "metaobject 'prototypeCallOnly' is attached to it",
						true, true);
			}

			if ( identStarKind == IdentStarKind.instance_variable_t
					&& !leftHandSideAssignment ) {
				MethodDec currentMethod1 = env.getCurrentMethod();
				if ( currentMethod1 != null ) {
					currentMethod1.addReadFromFieldList((FieldDec) varDec);
				}
				ExprSelfPeriodIdent.actionFieldAccess_semAn(env, this,
						(FieldDec) varDec);
			}

			if ( leftHandSideAssignment ) {

				if ( varDec instanceof FieldDec ) {
					if ( ((FieldDec) varDec).isReadonly()
							&& !env.getCurrentMethod().isInitMethod()
							&& !env.getCurrentMethod().isInitShared() ) {
						env.error(getFirstSymbol(), "Identifier '" + name
								+ "' cannot be used in the left-hand side of an assignment",
								true, true);
						/*
						 * env.error(true, getFirstSymbol(), "Identifier '" +
						 * name +
						 * "' cannot be used in the left-hand side of an assignment"
						 * , name, ErrorKind.
						 * identifier_cannot_be_used_in_the_left_hand_side_of_an_assignment
						 * );
						 */
					}
				}
			}
			else if ( identStarKind == IdentStarKind.instance_variable_t
					&& !((FieldDec) varDec).isShared() ) {
				String currentMethodName = env.getCurrentMethod()
						.getNameWithoutParamNumber();

				if ( (currentMethodName.equals("init")
						|| currentMethodName.equals("init:"))
						&& !((FieldDec) this.varDeclaration)
								.getWasInitialized() ) {
					env.error(this.getFirstSymbol(), "Variable '"
							+ varDec.getName()
							+ "' may not have been initialized. "
							+ "The assignment to it should in the top level method statements. It cannot "
							+ "be inside an 'if' statement, for example", true,
							true);
				}
				else if ( currentMethodName.equals("initShared") ) {
					env.error(this.getFirstSymbol(),
							"Illegal access to a field in an expression inside an 'initShared' method",
							true, true);
				}

			}

		}
		else {
			/*
			 * search for an unary method
			 */
			List<MethodSignature> methodSignatureList = currentPrototype
					.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
							name, env);

			if ( name.equals("init") ) {
				env.error(this.getFirstSymbol(),
						"'init' and 'init:' messages can only be sent to 'super' inside an 'init' or 'init:' method",
						true, true);
			}

			if ( methodSignatureList == null ) {
				if ( currentPrototype instanceof ObjectDec ) {
					methodSignatureList = ((ObjectDec) currentPrototype)
							.searchInitNewMethod(name);
				}
			}
			if ( methodSignatureList != null
					&& methodSignatureList.size() > 0 ) {
				// found an unary method

				String currentMethodName = env.getCurrentMethod()
						.getNameWithoutParamNumber();
				if ( currentMethodName.equals("init")
						|| currentMethodName.equals("init:") ) {
					if ( !MethodDec.hasAttachedAnnotationNamed(
							methodSignatureList, "accessOnlySharedFields") ) {
						String nameiv = "_" + name;
						FieldDec fieldList = ((ObjectDec) currentPrototype)
								.searchField(nameiv);
						if ( fieldList != null ) {
							env.error(getFirstSymbol(),
									"Message send to 'self' inside an 'init' or 'init:' method."
											+ " Since there is a field '"
											+ nameiv
											+ "', you probably wanted to write it instead of '"
											+ name + "'",
									true, true);
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

				methodSignatureForMessageSend = methodSignatureList.get(0);
				methodSignatureForMessageSend.calcInterfaceTypes(env);

				if ( env.getProject().getCompilerManager()
						.getCompilationStep() == CompilationStep.step_9 ) {
					MetaInfoServer.checkMessageSendWithMethodMetaobject(
							methodSignatureList, currentPrototype, null,
							ExprReceiverKind.SELF_R, env,
							this.getFirstSymbol());
				}

				identStarKind = IdentStarKind.unaryMethod_t;
				MethodDec currentMethod1 = env.getCurrentMethod();
				MethodDec methodFound = methodSignatureForMessageSend
						.getMethod();

				if ( methodSignatureForMessageSend.getMethod().getShared() ) {
					if ( env.getCurrentObjectDec() != methodFound
							.getDeclaringObject() ) {
						env.error(this.getFirstSymbol(), "Method '"
								+ methodSignatureForMessageSend.getFullName(env)
								+ "' is shared. It can only be called "
								+ "if the receiver is a prototype. The receiver is 'self'");
					}
				}
				else {
					currentMethod1.addSelfMessagePassing(
							methodSignatureForMessageSend);
					currentMethod1.setSelfLeak(true);
				}
				type = methodSignatureForMessageSend.getReturnType(env);
				javaName = MetaHelper.getJavaName(name) + "()";

				if ( this.annotationToTypeList != null ) {
					env.error(this.getFirstSymbol(),
							"Metaobject annotation attached to an unary method keyword like 'func unary:', which is illegal");
				}

				if ( env.getProject().getCompilerManager().getCompilationStep()
						.ordinal() < CompilationStep.step_7.ordinal() ) {

					type = MetaInfoServer.replaceMessageSendIfAsked(
							methodSignatureList, this, env,
							this.getFirstSymbol(), type);
				}

			}

		}
	}

	/**
	 * @param env
	 * @param currentPrototype
	 * @param name
	 */
	private void calcInternalTypes_single_id_outside_method(Env env,
			Prototype currentPrototype, String name) {
		Prototype prototype;
		// outside a method

		VariableDecInterface varDec = env.searchVariable(name);
		if ( varDec != null ) {
			// found a field

			if ( varDec instanceof FieldDec )
				identStarKind = IdentStarKind.instance_variable_t;
			else {
				identStarKind = IdentStarKind.variable_t;
				if ( varDec instanceof StatementLocalVariableDec ) {
					StatementLocalVariableDec localVar = (StatementLocalVariableDec) varDec;
					LocalVarInfo varInfo = env.getLocalVariableInfo(localVar);
					if ( !varInfo.initialized && !env.getHasJavaCode() ) {
						env.error(this.getFirstSymbol(),
								"Variable '" + varDec.getName()
										+ "' may not have been initialized");
					}
				}

			}

			this.varDeclaration = varDec;

			type = varDec.getType();
			javaName = varDec.getJavaName();

			if ( identStarKind == IdentStarKind.instance_variable_t ) {
				MethodDec currentMethod1 = env.getCurrentMethod();
				if ( currentMethod1 != null ) {
					currentMethod1.addReadFromFieldList((FieldDec) varDec);
				}
				ExprSelfPeriodIdent.actionFieldAccess_semAn(env, this,
						(FieldDec) varDec);
			}

			if ( this.annotationToTypeList != null ) {
				env.error(this.getFirstSymbol(),
						"Metaobject annotation attached to a variable");
			}

		}
		else {

			/*
			 * search for an unary method
			 */
			List<MethodSignature> methodSignatureList;

			if ( !calcInternalTypesUnaryMethod(env, currentPrototype, name) ) {
				prototype = env.searchPrivatePrototype(name);
				if ( prototype == null )
					prototype = env.searchVisiblePrototype(name,
							this.getFirstSymbol(), false);

				if ( prototype == null ) {
					WrType newType = env.searchType(name);
					if ( newType != null ) {
						if ( !(newType instanceof WrPrototype) ) {
							env.error(this.getFirstSymbol(),
									"New types should be associated to Cyan prototypes");
							return;
						}
						else {
							prototype = meta.GetHiddenItem
									.getHiddenPrototype((WrPrototype) newType);
						}
					}
				}
				if ( prototype == null ) {

					TypeJavaRef javaClass = env.searchVisibleJavaClass(name);
					if ( javaClass != null ) {
						this.identStarKind = IdentStarKind.jvmClass_t;
						type = javaClass;
						javaName = javaClass.getJavaName();
						// experimental: Java class with attached annotations
						if ( this.annotationToTypeList != null ) {
							// something like Char@letter
							TypeWithAnnotations twa;
							type = twa = new TypeWithAnnotations(type,
									annotationToTypeList);
							twa.checkAnnotation(env);
						}
						return;
					}

				}

				// try to find a prototype
				if ( prototype != null ) {
					identStarKind = IdentStarKind.prototype_t;
					type = prototype;
					// foundIdent = true;
					if ( prototype instanceof InterfaceDec ) {
						this.isInterface = true;
						javaName = MetaHelper
								.getJavaName(NameServer
										.prototypeFileNameFromInterfaceFileName(
												prototype.getName()))
								+ ".prototype";
					}
					else {
						javaName = MetaHelper.getJavaName(name) + ".prototype";
					}
					if ( this.annotationToTypeList != null ) {
						// something like Char@letter
						TypeWithAnnotations twa;
						type = twa = new TypeWithAnnotations(type,
								annotationToTypeList);
						twa.checkAnnotation(env);
					}
					if ( env.peekCheckUsePossiblyNonInitializedPrototype()
							&& !(prototype instanceof InterfaceDec)
							&& !env.getIsArgumentToIsA() ) {
						List<MethodSignature> initMSList = prototype
								.searchMethodPrivateProtectedPublicPackage(
										"init", env);
						if ( (initMSList == null || initMSList.size() == 0)
								&& type != Type.Nil ) {
							/*
							 * It is illegal to use a prototype that does not
							 * have an 'init' method
							 */
							env.error(getFirstSymbol(), "Prototype '"
									+ this.getName() + "' does "
									+ "not have an 'init' method. Therefore its fields may not have"
									+ " been initialized (they are if an 'init' method does exist)");
						}
					}
					MethodDec currentMethod1 = env.getCurrentMethod();
					if ( currentMethod1 != null
							&& currentMethod1.getName().equals("init")
							&& name.equals(currentPrototype.getName()) ) {
						env.error(getFirstSymbol(), "Prototype '"
								+ this.getName() + "' "
								+ "uses itself in its 'init' method. However, fields from "
								+ "the prototype, considered as an object, are "
								+ "initialized using the same 'init' method. Therefore,"
								+ " in the first call to 'init', to initialize the "
								+ "prototype fields, there would be a NullPointerException in Java");
					}

				}
				else {
					if ( currentPrototype instanceof ObjectDec ) {
						ObjectDec proto = (ObjectDec) currentPrototype;
						methodSignatureList = proto
								.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
										name, env);

						if ( name.equals("init") ) {
							if ( env.getCurrentMethod() != null ) {
								String initName = env.getCurrentMethod()
										.getNameWithoutParamNumber();
								if ( !initName.equals("init")
										&& !initName.equals("init:") ) {
									env.error(this.getFirstSymbol(),
											"'init' and 'init:' methods can only be called inside other 'init' or 'init:' methods",
											true, true);
								}
								if ( env.getFunctionStack().size() > 0 ) {
									env.error(this.getFirstSymbol(),
											"'init' and 'init:' messages cannot be sent inside anonymous functions",
											true, true);
								}
							}
						}

						if ( methodSignatureList.size() > 0 ) {
							env.error(getFirstSymbol(),
									"Method '" + name
											+ "' cannot be called here",
									true, true);
							/*
							 * env.error(true, getFirstSymbol(), "Method '" +
							 * name + "' cannot be called here", name,
							 * ErrorKind.method_is_not_visible_here);
							 */
						}
						else {
							FieldDec instVar = proto
									.searchFieldPrivateProtectedSuperProtected(
											name);
							if ( instVar != null ) {
								env.error(getFirstSymbol(),
										"field '" + name
												+ "' is not visible here",
										true, true);
								/*
								 * env.error(true, getFirstSymbol(), "field '" +
								 * name + "' is not visible here", name,
								 * ErrorKind.
								 * instance_variable_is_not_visible_here);
								 */
							}
							else {
								env.error(getFirstSymbol(),
										"Identifier '" + name
												+ "' was not declared",
										true, true);
								/*
								 * env.error(true, getFirstSymbol(),
								 * "Identifier '" + name + "' was not declared",
								 * name, ErrorKind.identifier_was_not_declared);
								 *
								 *
								 */
							}
						}
					}
					else {
						env.error(getFirstSymbol(),
								"Identifier '" + name + "' was not declared",
								true, true);
						/*
						 * env.error(true, getFirstSymbol(), "Identifier '" +
						 * name + "' was not declared", name,
						 * ErrorKind.identifier_was_not_declared);
						 */
					}
				}
			}
		}
	}

	/**
	 * @param env
	 * @param currentPrototype
	 * @param name
	 */
	private boolean calcInternalTypesUnaryMethod(Env env,
			Prototype currentPrototype, String name) {
		ObjectDec currentObjectDec = null;
		List<MethodSignature> methodSignatureList = null;

		if ( currentPrototype instanceof ObjectDec ) {

			currentObjectDec = (ObjectDec) currentPrototype;
			methodSignatureList = currentObjectDec
					.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
							name, env);

			if ( name.equals("init") ) {
				env.error(this.getFirstSymbol(),
						"'init' and 'init:' messages can only be sent to 'super' inside an 'init' or 'init:' method",
						true, true);

				/*
				 * // message sends to 'init' inside any methods, including
				 * 'init:' or 'init:', are currently illegal
				 *
				 *
				 * if ( env.getCurrentMethod() != null ) { String initName =
				 * env.getCurrentMethod().getNameWithoutParamNumber(); if (
				 * !initName.equals("init") && !initName.equals("init:") ) {
				 * env.error(this.getFirstSymbol(),
				 * "'init' and 'init:' methods can only be called inside other 'init' or 'init:' methods"
				 * ); } if ( env.getFunctionStack().size() > 0 ) {
				 * env.error(this.getFirstSymbol(),
				 * "'init' and 'init:' messages cannot be sent inside anonymous functions"
				 * ); } }
				 */
			}

			if ( methodSignatureList == null ) {
				methodSignatureList = currentObjectDec
						.searchInitNewMethod(name);
			}
			if ( methodSignatureList != null
					&& methodSignatureList.size() > 0 ) {
				// found an unary method

				methodSignatureForMessageSend = methodSignatureList.get(0);
				methodSignatureForMessageSend.calcInterfaceTypes(env);

				if ( env.getProject().getCompilerManager()
						.getCompilationStep() == CompilationStep.step_9 ) {
					MetaInfoServer.checkMessageSendWithMethodMetaobject(
							methodSignatureList, currentObjectDec, null,
							ExprReceiverKind.SELF_R, env,
							this.getFirstSymbol());
				}

				identStarKind = IdentStarKind.unaryMethod_t;
				MethodDec currentMethod1 = env.getCurrentMethod();
				if ( currentMethod1 != null ) {
					currentMethod1.addSelfMessagePassing(
							methodSignatureForMessageSend);
					currentMethod1.setSelfLeak(true);
				}

				type = methodSignatureForMessageSend.getReturnType(env);
				javaName = MetaHelper.getJavaName(name) + "()";

				if ( this.annotationToTypeList != null ) {
					env.error(this.getFirstSymbol(),
							"Metaobject annotation attached to an unary method keyword");
				}

				if ( env.getProject().getCompilerManager().getCompilationStep()
						.ordinal() < CompilationStep.step_7.ordinal() ) {

					type = MetaInfoServer.replaceMessageSendIfAsked(
							methodSignatureList, this, env,
							this.getFirstSymbol(), type);
				}

				return true;
			}
		}
		return false;
	}

	@Override
	public saci.TupleTwo<String, CompilationUnit> returnsNameWithPackage(
			Env env) {
		TupleTwo<String, Type> t = ifPrototypeReturnsNameWithPackageAndType(
				env);
		if ( t == null || !(t.f2.getInsideType() instanceof Prototype) ) {
			return null;
		}
		else {
			Prototype pu = (Prototype) t.f2.getInsideType();
			return new TupleTwo<String, CompilationUnit>(t.f1,
					pu.getCompilationUnit());
		}
	}

	@Override
	public saci.TupleTwo<String, Type> ifPrototypeReturnsNameWithPackageAndType(
			Env env) {

		if ( nameWithPackageAndType == null ) {
			String name = getName();
			// if ( Character.isLowerCase(name.charAt(0)) )
			// return new saci.Tuple<String, CompilationUnit>(name, null);

			name = name.replace(MetaHelper.cyanLanguagePackageName + ".", "");
			int indexOfDot = name.lastIndexOf('.');
			if ( indexOfDot < 0 && Character.isLowerCase(name.charAt(0)) ) {
				// symbol as 'joule' in Union<joule, Float, calorie, Float>
				nameWithPackageAndType = new saci.TupleTwo<String, Type>(name,
						null);
				return nameWithPackageAndType;
			}

			// name has a package OR it starts with an upper-case letter

			if ( indexOfDot < 0 ) {
				// no package preceding the name. It should be a prototype
				// visible in
				// this compilation unit
				Prototype pu3 = env.searchVisiblePrototype(name,
						this.getFirstSymbol(), true);
				if ( pu3 == null ) {
					if ( name.equals(MetaHelper.dynName) ) {
						nameWithPackageAndType = new saci.TupleTwo<String, Type>(
								name, Type.Dyn);
					}
					else {
						TypeJavaRef javaClass = env
								.searchVisibleJavaClass(name);
						if ( javaClass == null ) {
							nameWithPackageAndType = null;
						}
						else {
							nameWithPackageAndType = new saci.TupleTwo<String, Type>(
									javaClass.getFullName(), javaClass);
						}

					}
				}
				else {
					if ( pu3.getOuterObject() != null ) {
						// name = pu3.getOuterObject().getName() + "." + name;
						// an inner prototype "Fun_7__" should have only the
						// name "Fun_7__", without any package
						nameWithPackageAndType = new saci.TupleTwo<String, Type>(
								name, pu3);
					}
					else {
						String packageName = pu3.getCompilationUnit()
								.getPackageName();
						if ( packageName
								.equals(MetaHelper.cyanLanguagePackageName) )
							// do not put cyan.lang in the return value
							nameWithPackageAndType = new saci.TupleTwo<String, Type>(
									name, pu3);
						else
							nameWithPackageAndType = new saci.TupleTwo<String, Type>(
									packageName + "." + name, pu3);
					}
				}
			}
			else {
				// package name
				String prototypeName = name.substring(indexOfDot + 1);
				String packageName = name.substring(0, indexOfDot);
				Prototype pu4 = env.searchPackagePrototype(packageName,
						prototypeName);
				if ( pu4 == null ) {
					TypeJavaRef javaClass = env
							.searchPackageJavaClass(packageName, prototypeName);
					if ( javaClass != null ) {
						nameWithPackageAndType = new saci.TupleTwo<String, Type>(
								packageName + "." + prototypeName, javaClass);
					}
					else {
						nameWithPackageAndType = null;
					}
				}
				else
					nameWithPackageAndType = new saci.TupleTwo<String, Type>(
							packageName + "." + prototypeName, pu4);
			}

		}
		return nameWithPackageAndType;
	}

	public IdentStarKind getIdentStarKind() {
		return identStarKind;
	}

	public MessageSendToAnnotation getMessageSendToAnnotation() {
		return messageSendToAnnotation;
	}

	public void setMessageSendToAnnotation(
			MessageSendToAnnotation messageSendToAnnotation) {
		this.messageSendToAnnotation = messageSendToAnnotation;
	}

	/**
	 * if this qualified name represents a prototype preceded by a package name,
	 * return the package name. Otherwise return null
	 */
	@Override
	public String getPackageName() {
		if ( this.identSymbolArray.size() <= 1 )
			return null;
		else {
			String s = "";
			int i = 0;
			while (i < identSymbolArray.size() - 1) {
				s = s + identSymbolArray.get(i).getSymbolString();
				++i;
			}
			return s;
		}
	}

	public String getOriginalJavaName() {
		if ( originalJavaName == null )
			return javaName;
		else
			return originalJavaName;
	}

	public void setOriginalJavaName(String originalJavaName) {
		this.originalJavaName = originalJavaName;
	}

	/**
	 * if this qualified name represents a prototype preceded by a package name,
	 * return the prototype name. Otherwise return null
	 */
	@Override
	public String getPrototypeName() {
		return this.identSymbolArray.get(this.identSymbolArray.size() - 1)
				.getSymbolString();

	}

	public void setRefType(boolean refType) {
		varDeclaration.setRefType(refType);
		javaName = varDeclaration.javaNameWithRef();
	}

	/**
	 * true if this is a variable that was preceded by symbol % used inside
	 * functions for "COPY_VAR access"
	 */
	private boolean						precededByRemaninder;

	/**
	 * the java name of this identifier
	 */
	private String						javaName;
	/**
	 * the original Java name for this identifier
	 */
	private String						originalJavaName;

	/**
	 * the kind of this qualified identifier: variable, prototype, or unary
	 * method
	 */
	private IdentStarKind				identStarKind;

	/**
	 * true if the identifier is an Interface. Therefore, identStarKind is
	 * prototype_t
	 */
	private boolean						isInterface	= false;

	/**
	 * name with package and type. Only used if this expression is a type
	 */
	private saci.TupleTwo<String, Type>	nameWithPackageAndType;
	/**
	 * if this expression is a field, parameter, or local variable,
	 * varDeclaration points to it
	 */
	private VariableDecInterface		varDeclaration;

	public VariableDecInterface getVarDeclaration() {
		return varDeclaration;
	}

	protected List<Symbol>			identSymbolArray;

	/**
	 * message send at compile time attached to this qualified expression. It is
	 * only valid if this expression represents a prototype. Example: <br>
	 * {@code var Person.#writeCode  p;}<br>
	 */
	private MessageSendToAnnotation	messageSendToAnnotation;

	public List<AnnotationAt> getAnnotationToTypeList() {
		return annotationToTypeList;
	}

	public void setAnnotationToTypeList(List<AnnotationAt> annotationList) {
		this.annotationToTypeList = annotationList;
	}

	public MethodSignature getMethodSignatureForMessageSend() {
		return methodSignatureForMessageSend;
	}

	/**
	 * a list of metaobject annotations that is attached to this generic
	 * prototype instantiation
	 */
	private List<AnnotationAt>	annotationToTypeList;

	/**
	 * if identStarKind == IdentStarKind.unaryMethod_t then this field refers to
	 * the method that this object represents
	 */
	private MethodSignature		methodSignatureForMessageSend;
}
