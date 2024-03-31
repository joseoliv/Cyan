/**
 *
 */

package ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._IListAfter__afterResTypes;
import error.CompileErrorException;
import error.ErrorInMetaobjectException;
import error.UnitError;
import lexer.Symbol;
import meta.AttachedDeclarationKind;
import meta.CompilationInstruction;
import meta.CompilationStep;
import meta.CyanMetaobject;
import meta.GetHiddenItem;
import meta.IActionNewPrototypes_afterResTypes;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_semAn;
import meta.IListAfter_afterResTypes;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCompilationUnit;
import meta.WrExprAnyLiteral;
import meta.WrExprLiteralBoolean;
import meta.WrExprLiteralString;
import meta.WrProgram;
import meta.WrProgram_dpp;
import meta.lexer.MetaLexer;
import metaRealClasses.Compiler_afterResTypes;
import metaRealClasses.Compiler_semAn;
import saci.Compiler;
import saci.CompilerManager_afterResTypes;
import saci.Env;
import saci.MyFile;
import saci.NameServer;
import saci.Project;
import saci.Saci;
import saci.TM;

/**
 * represents a Cyan program
 *
 * @author José
 *
 */
public class Program implements ASTNode, Declaration {

	public Program(boolean addTypeInfo, String typeInfoPath) {
		inCalcInterfaceTypes = false;
		packageList = new ArrayList<>();
		this.jvmPackageList = new ArrayList<>();
		jvmPackageMap = new HashMap<>();
		compilationUnitList = new ArrayList<>();
		receiverToWriteList = new HashSet<>();
		programKeyValueMap = new HashMap<>();
		programKeyValueSet = new HashMap<>();
		attachedAnnotationList = null;
		this.addTypeInfo = addTypeInfo;
		this.typeInfoPath = typeInfoPath;
	}

	@Override
	public void accept(ASTVisitor visitor) {

		visitor.preVisit(this);
		for (final CyanPackage cp : this.packageList) {
			cp.accept(visitor);
		}
		for (final JVMPackage cp : this.jvmPackageList) {
			cp.accept(visitor);
		}
		visitor.visit(this);
	}

	/**
	 * reset the state of all compilation units eliminating any traces they have
	 * already been compiled.
	 */
	public void reset() {
		init();
		for (final CompilationUnit compilationUnit : compilationUnitList) {
			if ( !compilationUnit.getAlreadPreviouslyCompiled() ) {
				compilationUnit.reset();
			}
		}
	}

	/**
	 * reset the state of all non-generic compilation units eliminating any
	 * traces they have already been compiled.
	 */
	public void resetNonGeneric() {
		init();
		for (final CompilationUnit compilationUnit : compilationUnitList) {
			if ( !compilationUnit.getHasGenericPrototype()
					&& !compilationUnit.getAlreadPreviouslyCompiled() ) {
				compilationUnit.reset();
			}
		}
	}

	/**
	 * prepare this program to be compiled
	 */
	public void init() {

		/**
		 * For each package, insert its compilation units (source code) into a
		 * list of compilation units.
		 */
		compilationUnitList.clear();
		for (final CyanPackage cyanPackage : packageList) {
			for (final CompilationUnit compilationUnit : cyanPackage
					.getCompilationUnitList()) {
				compilationUnitList.add(compilationUnit);
				compilationUnit.setProgram(this);
			}
			// boolean useCompiled = cyanPackage.getUseCompiledPrototypes();
			// if ( useCompiled ) {
			// for ( final CompilationUnit compilationUnit :
			// cyanPackage.getCompilationUnitList() ) {
			// if ( compilationUnit.hasGenericPrototype() ) {
			// compilationUnitList.add(compilationUnit);
			// compilationUnit.setProgram(this);
			// }
			// }
			// }
			// else {
			// }
		}
		if ( this.receiverToWriteList != null ) {
			this.receiverToWriteList.clear();
		}
	}

	public void addCompilationUnit(CompilationUnit compilationUnit) {
		compilationUnitList.add(compilationUnit);
		compilationUnit.setProgram(this);
	}

	/**
	 * Types appear in the AST as objects of ExprIdentStar or
	 * ExprGenericPrototypeInstantiation. This method sets the "type" field of
	 * several AST objects to the real Prototype (prototype, interface, or Java
	 * class) that is being represented. That is, if the ExprIdentStar object is
	 * "Person", this method sets the "type" field of this object to the program
	 * unit that is "Person". But not all AST objects are changed. This method
	 * only sets the "type" variable of objects of the AST representing method
	 * parameters and return values (including private and protected ones),
	 * inherited prototypes and interfaces (an interface may inherit from
	 * another interface), implemented interfaces, types of generic parameters
	 * (as Person in "object Stack<Person T> ... end"), mixin types (as Person
	 * in "mixin(Person) object Comparison ... end"), types of context
	 * parameters (as Int in "object Sum(Int s) ... end"), types in mixin
	 * inheritance (as Readable and Writable in "object File mixin Readable,
	 * Writable ... end"), and fields.
	 *
	 * Then not all expressions have their types calculated. For example, the
	 * types of local variables and expressions are not set.
	 *
	 * @param env
	 */
	public void calcInterfaceTypes(Env env) {

		this.inCalcInterfaceTypes = true;
		/*
		 * cannot use 'for' command here because compilation units are added to
		 * compilationUnitList
		 */
		boolean firstCalcInterfaceTypes = this.project.getCompilerManager()
				.getCompilationStep()
				.ordinal() < CompilationStep.step_3.ordinal();
		int i = 0;

		boolean inCompilationStep5 = env
				.getCompilationStep() == CompilationStep.step_5;
		boolean inCompilationStep8 = env
				.getCompilationStep() == CompilationStep.step_8;

		// for ( CompilationUnit cunit2 : compilationUnitList ) {
		// if ( cunit2.getPackageName().equals("cyan.reflect") ) {
		// System.out.println(cunit2.getFilename());
		// }
		// if ( cunit2.getFilename().contains("IActionFunction") ) {
		// System.out.println("found IActionFunction");
		// }
		// }

		while (i < compilationUnitList.size()) {
			// int mySize = compilationUnitList.size();
			final CompilationUnit compilationUnit = compilationUnitList.get(i);
			if ( !compilationUnit.getHasGenericPrototype() ) {
				// ((ObjectDec )
				// compilationUnitList.get(158).getPublicPrototype()).getMethodDecList().get(0).getMethodSignature().getParameterList().get(0).getType().getName()

				try {
					if ( compilationUnit.getAlreadPreviouslyCompiled() ) {
						// if it has been compiled, calculate the interfaces
						// only in phase 1 of the compilation
						if ( firstCalcInterfaceTypes ) {
							/*
							 * error are given based on the compilation unit of
							 * the offending symbols. In this case, the
							 * compilation unit is that of the interface, file
							 * 'allInterfaces.iyan'. The 'catch' code move the
							 * errors from the compilation unit of
							 * 'allInterfaces.iyan' to a *real* compilation unit
							 * like 'CyanMetaobjectAtAnnot'
							 */
							try {
								compilationUnit.getCyanPackage()
										.addPackageMetaToClassPath_and_Run(
												() -> {
													compilationUnit
															.calcInterfaceTypes(
																	env);
												});
							}
							catch (final CompileErrorException e) {
								for (UnitError anError : compilationUnit
										.getInterfaceSourceCompilationUnit()
										.getErrorList()) {
									compilationUnit.addError(anError);
								}
							}
						}
					}
					else {

						// if ( inCompilationStep5 ) {
						// if ( !
						// compilationUnit.getSourceCodeChanged_inPhaseafterResTypes()
						// ) {
						// ++i; // not ideal solution, of course
						// continue;
						// }
						// }
						// else if ( inCompilationStep8 ) {
						// if ( !
						// compilationUnit.getSourceCodeChanged_inPhasesemAn() )
						// {
						// ++i; // not ideal solution, of course
						// continue;
						// }
						// }

						compilationUnit.getCyanPackage()
								.addPackageMetaToClassPath_and_Run(() -> {
									compilationUnit.calcInterfaceTypes(env);
								});

					}

				}
				catch (final CompileErrorException e) {
				}
			}
			++i;
			// System.out.println(compilationUnit.getFullFileNamePath());
		}
		/*
		 * calculate the types of the expressions that are parameters to the
		 * metaobjects in the project file
		 */
		if ( this.getProject().getCompilerManager()
				.getCompilationStep() == CompilationStep.step_2 ) {
			env.setProjectResTypes(true);
			calcAnnotationsArgumentTypes_of_ProjectFile(env);
		}

		if ( env.getCompInstSet()
				.contains(CompilationInstruction.ati3_check) ) {
			/**
			 * check whether the overloaded methods were correctly defined.
			 */
			calculateInterfaceChecks(env);
		}

		// if ( env.getCompInstSet().contains(CompilationInstruction.ati2_check)
		// ) {
		//
		//
		// final ICompiler_afterResTypes compiler_afterResTypes = new
		// Compiler_afterResTypes(env);
		// //WrEnv newEnv = compiler_afterResTypes.getEnv();
		// final Env newEnv =
		// meta.GetHiddenItem.getHiddenEnv(compiler_afterResTypes.getEnv());
		//
		//
		// for ( final CompilationUnit cunit : compilationUnitList ) {
		// if ( cunit.hasGenericPrototype() ||
		// cunit.getAlreadPreviouslyCompiled() ) {
		// continue;
		// }
		// newEnv.atBeginningOfCurrentCompilationUnit(cunit);
		// for ( final Prototype pu : cunit.getPrototypeList() ) {
		// newEnv.atBeginningOfObjectDec(pu);
		// List<Annotation> annotList =
		// pu.getPrototypePackageProgramAnnotationList();
		// pu.setDeclarationImportedFromPackageProgram();
		//
		// // pu.ati2_checkDeclaration(compiler_afterResTypes, env);
		// newEnv.atEndOfObjectDec();
		// }
		// newEnv.atEndOfCurrentCompilationUnit();
		// }
		//
		// }

		/*
		 * if (
		 * env.getProject().getCompilerManager().getCompilationStep().ordinal()
		 * < CompilationStep.step_7.ordinal() ) {
		 * afterATImetaobjectAnnotationList = null; }
		 */
		if ( afterATImetaobjectAnnotationList != null
				&& env.getProject().getCompilerManager()
						.getCompilationStep() == CompilationStep.step_8 ) {

			final ICompiler_afterResTypes compiler_afterResTypes = new Compiler_afterResTypes(
					env);

			for (final CyanMetaobject metaobject : afterATImetaobjectAnnotationList) {
				final Env newEnv = meta.GetHiddenItem
						.getHiddenEnv(compiler_afterResTypes.getEnv());

				_CyanMetaobject other = metaobject.getMetaobjectInCyan();

				WrCompilationUnit compUnit = null;
				if ( other == null ) {
					compUnit = ((IListAfter_afterResTypes) metaobject)
							.getCompilationUnit();
				}
				else {
					compUnit = ((_IListAfter__afterResTypes) other)
							._getCompilationUnit();
				}
				final CompilationUnit cunit = GetHiddenItem
						.getHiddenCompilationUnit(compUnit);
				newEnv.atBeginningOfCurrentCompilationUnit(cunit);
				newEnv.atBeginningOfObjectDec(cunit.getPublicPrototype());

				try {
					cunit.prepareGenericCompilationUnit(newEnv);

					if ( other == null ) {
						((IListAfter_afterResTypes) metaobject)
								.after_afterResTypes_action(
										compiler_afterResTypes);
					}
					else {
						((_IListAfter__afterResTypes) other)
								._after__afterResTypes__action_1(
										compiler_afterResTypes);
					}

					newEnv.atEndOfCurrentCompilationUnit();
					newEnv.atEndOfObjectDec();
				}
				catch (final error.CompileErrorException e) {
				}
				catch (final NoClassDefFoundError e) {
					env.error(
							meta.GetHiddenItem.getHiddenSymbol(metaobject
									.getAnnotation().getFirstSymbol()),
							e.getMessage() + " "
									+ NameServer.messageClassNotFoundException);
				}
				catch (final RuntimeException e) {
					e.printStackTrace();
					env.thrownException(
							meta.GetHiddenItem.getHiddenCyanAnnotation(
									metaobject.getAnnotation()),
							meta.GetHiddenItem.getHiddenSymbol(metaobject
									.getAnnotation().getFirstSymbol()),
							e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(metaobject);
					newEnv.setPrototypeForGenericPrototypeList(null);
				}

			}
		}
		this.inCalcInterfaceTypes = false;

	}

	/**
	 * @param env
	 */
	private void calcAnnotationsArgumentTypes_of_ProjectFile(Env env) {
		int i;
		final int oldSizeList = compilationUnitList.size();
		final CompilationUnit anyCompilationUnit = ((Prototype) Type.Any)
				.getCompilationUnit();
		/*
		 * do all the typing in the context of Any. It does not matter which
		 * prototype of cyan.lang is chosen. This is just to avoid a NPE.
		 */

		CyanPackage cyanLangPackage = this.project.getCyanLangPackage();
		anyCompilationUnit.getImportedCyanPackageSet().add(cyanLangPackage);
		env.atBeginningOfCurrentCompilationUnit(anyCompilationUnit);
		for (final AnnotationAt annotation : this.attachedAnnotationList) {
			annotation.calcInternalTypes(env);
		}
		for (final CyanPackage cp : this.packageList) {
			if ( cp.getAttachedAnnotationList() != null ) {
				for (final AnnotationAt annotation : cp
						.getAttachedAnnotationList()) {
					cp.addPackageMetaToClassPath_and_Run(() -> {
						annotation.calcInternalTypes(env);
					});

				}
			}

		}
		env.atEndOfCurrentCompilationUnit();
		/*
		 * new generic prototype instantiations may have been introduced to this
		 * list. Then it is necessary to calculate their interfaces
		 */
		i = oldSizeList;
		while (i < compilationUnitList.size()) {
			// int mySize = compilationUnitList.size();
			final CompilationUnit compilationUnit = compilationUnitList.get(i);
			if ( !compilationUnit.getHasGenericPrototype() ) {
				try {

					compilationUnit.getCyanPackage()
							.addPackageMetaToClassPath_and_Run(() -> {
								compilationUnit.calcInterfaceTypes(env);
							});

				}
				catch (final CompileErrorException e) {
				}
			}
			++i;
		}
	}

	/**
	 * @param env
	 */
	private void calculateInterfaceChecks(Env env) {

		for (final CompilationUnit cunit : this.compilationUnitList) {

			if ( !cunit.getAlreadPreviouslyCompiled() ) {
				try {
					calculateInterfaceChecksEachCompilationUnit(env, cunit);
				}
				catch (final CompileErrorException e) {
				}
			}

		}
	}

	/*
	 * @SuppressWarnings("static-method") private void
	 * checkAbstractMethodsWereDeclared(Env env, CompilationUnit cunit) { for (
	 * Prototype pu : cunit.getPrototypeList() ) { if ( pu instanceof ObjectDec
	 * ) { ObjectDec proto = (ObjectDec ) pu;
	 * 
	 * } } }
	 */

	@SuppressWarnings("static-method")
	private void checkAbstractMethodsWereDeclared(Env env, ObjectDec proto,
			ObjectDec superProto) {
		// List<MethodDec> methodList = new ArrayList<MethodDec>();
		if ( (proto != Type.Any && proto != Type.Nil)
				&& (!proto.getIsAbstract() && superProto.getIsAbstract()) ) {
			/*
			 * 'proto' is not abstract but 'superProto' is. Then 'proto' should
			 * define all abstract methods inherited from 'superProto' and all
			 * its super-prototypes
			 */
			ObjectDec p = superProto;
			while (true) {
				final List<MethodDec> superAbstractMethodList = p
						.getAbstractMethodList();
				if ( superAbstractMethodList.size() > 0 ) {
					for (final MethodDec abstractMethod : superAbstractMethodList) {
						/*
						 * check if this abstract method is defined in 'proto'
						 */
						final String abstractMethodName = abstractMethod
								.getName();
						final List<MethodSignature> methodList = proto
								.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
										abstractMethodName, env);
						boolean found = false;
						if ( methodList != null && methodList.size() > 0 ) {
							// check if the any of the methods is not abstract
							for (int j = methodList.size() - 1; j >= 0; --j) {
								if ( !methodList.get(j).getMethod()
										.isAbstract() ) {
									found = true;
									break;
								}
							}
						}

						if ( !found ) {
							try {
								env.error(proto.getFirstSymbol(),
										"Abstract method '"
												+ abstractMethod.getName()
												+ "' inherited from "
												+ "prototype '"
												+ p.getFullName()
												+ "' is not defined in prototype '"
												+ proto.getName() + "'");
							}
							catch (final CompileErrorException e) {
							}
						}
					}
				}

				p = p.getSuperobject();
				if ( p == null || p == Type.Any ) {
					break;
				}
			}

		}

	}

	/**
	 * check whether unary methods are preceded by keyword 'override'
	 * 
	 * @param env
	 * @param proto
	 * @param superProto
	 */
	@SuppressWarnings("static-method")
	private void checkUnaryMethods(Env env, ObjectDec proto,
			ObjectDec superProto) {

		if ( superProto == null ) {
			return;
		}
		for (final MethodDec method : proto.getMethodDecList()) {

			final MethodSignature methodMS = method.getMethodSignature();
			if ( methodMS instanceof MethodSignatureUnary ) {
				final String methodName = methodMS.getName();
				final List<MethodSignature> msList = superProto
						.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
								methodName, env);
				if ( msList != null && msList.size() > 0 ) {
					final MethodDec superMethod = msList.get(0).getMethod();
					if ( superMethod != null && method.getShared()
							&& method.getHasOverride() ) {
						env.error(method.getFirstSymbol(), "The shared method '"
								+ methodName
								+ "' overrides a method of superprototype '"
								+ superMethod.getDeclaringObject().getFullName()
								+ "'. However, the superprototype method is not shared and this overridding is illegal");
					}
					// if ( superMethod != null && ! superMethod.getShared() &&
					// superMethod.getVisibility() == Token.PROTECTED ) {
					// if (
					// !superMethod.getDeclaringObject().getPackageName().equals(
					// method.getDeclaringObject().getPackageName() ) ) {
					// env.error(method.getFirstSymbol(), "Method '" +
					// methodName +
					// "' overrides a 'protected' method of superprototype '" +
					// superMethod.getDeclaringObject().getFullName() +
					// "'. This is illegal because the subprototype method is in
					// a different package.");
					//
					// }
					// }
					if ( !method.getHasOverride() ) {
						String superName;
						if ( superMethod != null ) {
							if ( !method.getShared() ) {
								superName = superMethod.getDeclaringObject()
										.getFullName();
								if ( superMethod.getShared() ) {
									env.error(method.getFirstSymbol(),
											"Method '" + methodName
													+ "' overrides a shared method of super-prototype '"
													+ superName
													+ "'. This is illegal");
								}
								else {
									env.error(method.getFirstSymbol(),
											"Method '" + methodName
													+ "' overrides a method of super-prototype '"
													+ superName
													+ "'. It should be preceded by keyword 'override'");
								}
							}
						}
						else {
							superName = msList.get(0).getDeclaringInterface()
									.getFullName();
							env.error(method.getFirstSymbol(), "Method '"
									+ methodName
									+ "' overrides a method of interface '"
									+ superName
									+ "'. It should be preceded by keyword 'override'");
						}
					}
					else if ( superMethod != null
							&& (superMethod.getVisibility() == Token.PACKAGE
									&& superMethod.getDeclaringObject() != null)
							&& (method.getDeclaringObject().getCompilationUnit()
									.getCyanPackage() != superMethod
											.getDeclaringObject()
											.getCompilationUnit()
											.getCyanPackage()) ) {
						/*
						 * the super method has visibility 'package'. The
						 * subprototype overrides it. Then the subprototype
						 * should be in the same package as the superprototype
						 */
						final String superName = superMethod
								.getDeclaringObject().getFullName();
						env.error(method.getFirstSymbol(), "Method '"
								+ methodName
								+ "' overrides a method of super-prototype '"
								+ superName
								+ "'. The visibility of the superprototype method is 'package'. "
								+ "Then in order to override it, the subprototype '"
								+ method.getDeclaringObject().getFullName()
								+ "' should be in package '"
								+ superMethod.getDeclaringObject()
										.getCompilationUnit().getPackageName()
								+ "'");
					}
				}
			}
		}
	}

	/**
	 * @param env
	 * @param cunit
	 */
	@SuppressWarnings("static-method")
	private void calculateInterfaceChecksEachCompilationUnit(Env env,
			CompilationUnit cunit) {

		if ( !cunit.getHasGenericPrototype() ) {

			env.atBeginningOfCurrentCompilationUnit(cunit);

			final HashSet<Integer> alreadCheckedList = new HashSet<>();
			for (final Prototype pu : cunit.getPrototypeList()) {
				if ( pu instanceof ObjectDec ) {
					final ObjectDec proto = (ObjectDec) pu;
					final ObjectDec superProto = proto.getSuperobject();

					checkAbstractMethodsWereDeclared(env, proto, superProto);
					checkUnaryMethods(env, proto, superProto);

					alreadCheckedList.clear();
					for (final MethodDec method : proto.getMethodDecList()) {

						final MethodSignature methodMS = method
								.getMethodSignature();
						/*
						 * unary methods need not to be checked for overloading
						 */
						if ( methodMS instanceof MethodSignatureUnary
								|| methodMS instanceof MethodSignatureOperator
										&& methodMS
												.getParameterList() == null ) {
							if ( superProto != null ) {
								String unaryMethodName = method.getName();
								List<MethodSignature> superProPubPacList = superProto
										.searchMethodProtectedPublicPackage(
												unaryMethodName);
								if ( superProPubPacList != null
										&& superProPubPacList.size() > 0 ) {
									if ( method.getShared() ) {
										/*
										 * there should be no super method with
										 * the same name
										 */
										env.error(method.getFirstSymbol(),
												"Method '" + unaryMethodName
														+ "' is shared and there is a method of superprototype '"
														+ superProPubPacList
																.get(0)
																.getMethod()
																.getDeclaringObject()
																.getFullName()
														+ "' with the same name. This is illegal");
									}
									else if ( superProPubPacList.get(0)
											.getMethod().getShared() ) {
										env.error(method.getFirstSymbol(),
												"Method '" + unaryMethodName
														+ "' is unary and there is a method of superprototype '"
														+ superProPubPacList
																.get(0)
																.getMethod()
																.getDeclaringObject()
																.getFullName()
														+ "' with the same name that is shared. This is illegal");
									}
								}
							}
							continue;
						}

						final int methodNumber = method.getMethodNumber();

						if ( !alreadCheckedList.contains(methodNumber) ) {
							alreadCheckedList.add(methodNumber);

							final String methodName = method.getName();
							final List<MethodSignature> mspppList = proto
									.searchMethodPrivateProtectedPublic(
											methodName);
							final List<Tuple2<Symbol, Integer>> tList = new ArrayList<>();
							for (final MethodSignature ms : mspppList) {
								final MethodDec other = ms.getMethod();
								final int otherNumber = other.getMethodNumber();
								tList.add(new Tuple2<>(other.getFirstSymbol(),
										otherNumber));
							}
							/*
							 * check whether all methods with the same name are
							 * textually near each other
							 */
							if ( tList.size() > 1 ) {
								for (final Tuple2<Symbol, Integer> t : tList) {
									boolean ok = false;
									for (final Tuple2<Symbol, Integer> w : tList) {
										if ( t.f2 + 1 == w.f2
												|| t.f2 - 1 == w.f2 ) {
											ok = true;
										}
									}
									if ( !ok ) {
										env.error(t.f1,
												"Method declared at line "
														+ t.f1.getLineNumber()
														+ " is part of a overloaded method. Therefore it should be declared "
														+ " adjacent to a method with the same name");
									}
								}
							}

							if ( method.getShared() && superProto != null ) {
								/*
								 * there should be no super method with the same
								 * name
								 */
								List<MethodSignature> superProPubPacList = superProto
										.searchMethodProtectedPublicPackage(
												methodName);
								if ( superProPubPacList != null
										&& superProPubPacList.size() > 0 ) {
									env.error(method.getFirstSymbol(),
											"Method '" + methodName
													+ "' is shared and there is a method of superprototype '"
													+ superProPubPacList.get(0)
															.getMethod()
															.getDeclaringObject()
															.getFullName()
													+ "' with the same name. This is illegal");
								}
							}
							final List<MethodSignature> msList_PPP_This_And_SuperProto = proto
									.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
											methodName, env);
							final List<MethodSignature> pmsList_PP_This_Proto = proto
									.searchMethodProtectedPublicPackage(
											methodName);
							final List<MethodSignature> interMSList_Impl_Interfaces = proto
									.searchMethodImplementedInterface(
											methodName, env);

							if ( method.getPrecededBy_overload() ) {
								// MethodSignature methodSig =
								// method.getMethodSignature();
								// if ( methodSig instanceof
								// MethodSignatureWithKeywords ) {
								// MethodSignatureWithKeywords methodSigKw =
								// (MethodSignatureWithKeywords ) methodSig;
								// List<MethodKeywordWithParameters> kwList =
								// methodSigKw.getKeywordArray();
								// for ( MethodKeywordWithParameters kwParams :
								// kwList ) {
								// for ( ParameterDec p :
								// kwParams.getParameterList() ) {
								// if ( p.getName() == null ) {
								//
								// }
								// }
								// }
								// }

								/*
								 * 
								 * \item no method with the same name should
								 * have been declared textually before it in the
								 * prototype hierarchy. That includes {\tt P}
								 * and its super-prototypes. That is, {\tt m}
								 * should not override a super-prototype method;
								 * \item no interface implemented by {\tt P}
								 * should declare a method with the same name as
								 * {\tt m}; \item no method with the same name
								 * in the prototype should be abstract; \item if
								 * the method is final, all methods with the
								 * same name should be final too. If it is not
								 * final, no method with the same name can be
								 * final; \item the return value type of all
								 * methods with the same name as {\tt m} in {\tt
								 * P} should be the same. *
								 */
								/*
								 * method declaration is preceded by 'overload'
								 * as in overload func print: String s { ... }
								 */
								final boolean isFinal = method.getIsFinal();
								final Type returnValueType = method
										.getMethodSignature()
										.getReturnType(env);
								final List<MethodDec> overloadMethodList = new ArrayList<>();
								for (final MethodSignature ms : mspppList) {
									final MethodDec other = ms.getMethod();

									// no method with the same name should have
									// been declared textually before it in
									// the prototype hierarchy. That includes
									// {\tt P} and its super-prototypes. That
									// is, {\tt m} should not override a
									// super-prototype method;

									other.setOverload(true);
									overloadMethodList.add(other);
									alreadCheckedList.add(
											ms.getMethod().getMethodNumber());

									final int otherNumber = other
											.getMethodNumber();
									if ( otherNumber < methodNumber ) {
										/*
										 * another method was textually declared
										 * before the method preceded by
										 * 'overload' as in object A fun print:
										 * String s { ... } overload fun print:
										 * Int n { ... } end
										 */
										env.error(method.getFirstSymbol(),
												"Keyword 'overload' should be used in the first textually "
														+ " declared method with this name in this prototype");
									}
									if ( otherNumber > methodNumber && other
											.getPrecededBy_overload() ) {
										/*
										 * another method with 'overload' was
										 * textually declared before the method
										 * preceded by 'overload' as in object A
										 * overload fun print: String s { ... }
										 * overload fun print: Int n { ... } end
										 */
										env.error(other.getFirstSymbol(),
												"Keyword 'overload' should be used ONLY in the first textually "
														+ " declared method with this name in this prototype");
									}

									if ( other
											.getVisibility() != Token.PUBLIC ) {
										env.error(other.getFirstSymbol(),
												"This method is part of a overloaded method. Therefore it should be 'public'");
									}
									// no method with the same name in the
									// prototype should be abstract
									if ( other.isAbstract() ) {
										env.error(other.getFirstSymbol(),
												"This method is part of a overloaded method. Therefore it cannot be abstract");
									}
									// if the method is final, all methods with
									// the same name should be final too.
									// If it is not final, no method with the
									// same name can be final;
									if ( other.getIsFinal() != isFinal ) {
										env.error(other.getFirstSymbol(),
												"This method is part of a overloaded method. Therefore either all methods of this "
														+ " prototype are 'final' or none is");
									}
									if ( other.getMethodSignature()
											.getReturnType(
													env) != returnValueType ) {
										env.error(other.getFirstSymbol(),
												"This method is part of a overloaded method. Therefore all methods with this name of this "
														+ " prototype should have the same return value type");
									}

								}

								proto.addOverloadMethodList(overloadMethodList);
								// no interface implemented by {\tt P} should
								// declare a method with the same
								// name as {\tt m};
								if ( interMSList_Impl_Interfaces != null
										&& interMSList_Impl_Interfaces
												.size() > 0 ) {
									env.error(method.getFirstSymbol(),
											"The overloaded method '"
													+ methodName
													+ "' is declared in this object "
													+ "and in one of the implemented interfaces, ");
								}
								if ( superProto != null ) {
									final List<MethodSignature> supermsList = superProto
											.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
													methodName, env);
									if ( supermsList != null
											&& supermsList.size() > 0 ) {
										env.error(method.getFirstSymbol(),
												"This method is preceded by 'overload'. It should be the first "
														+ " method in the prototype hierarchy. But it is not. This method overrides a method of prototype '"
														+ supermsList.get(0)
																.getMethod()
																.getDeclaringObject()
																.getFullName()
														+ "' ");
									}

								}

							}
							else {
								boolean oneMethodIn_pmsList = true;
								boolean allSameSignature = true;
								boolean isVisibilityOk = true;
								boolean somePrecededByOverload = false;

								// method is not preceded by 'overload'
								if ( msList_PPP_This_And_SuperProto
										.size() == 1 ) {
									/*
									 * If {\tt msList} has just method {\tt m}
									 * and {\tt interMSList} is empty, the
									 * declaration of {\tt m} is correct. It
									 * {\tt interMSList} is not empty, it should
									 * have just one method since interfaces
									 * cannot declare overloaded methods. If the
									 * single method signature of {\tt
									 * interMSList} is different from the
									 * signature of {\tt m} then the declaration
									 * of {\tt m} is incorrect;
									 */
									if ( (interMSList_Impl_Interfaces != null
											&& interMSList_Impl_Interfaces
													.size() > 0)
											&& !interMSList_Impl_Interfaces
													.get(0).getFullName(env)
													.equals(method
															.getMethodSignature()
															.getFullName(
																	env)) ) {
										// System.out.println(interMSList.get(0).getFullName()
										// + " != " +
										// method.getMethodSignature().getFullName());
										env.error(method.getFirstSymbol(),
												"This method implements a method of an interface but with different parameter types");
									}
								}
								else if ( msList_PPP_This_And_SuperProto
										.size() == pmsList_PP_This_Proto.size()
										&& pmsList_PP_This_Proto.size() > 1 ) {
									/*
									 * all methods with the same name are in the
									 * prototype and this one is note preceded
									 * by 'overload'. Then the first method
									 * should be preceded by 'overload'
									 * 
									 * 
									 * 
									 * 
									 * \item Suppose all methods of {\tt msList}
									 * are in {\tt P} ({\tt pmsList} is equal to
									 * {\tt msList}) and {\tt pmsList} has more
									 * than one element. The declaration of the
									 * methods of {\tt pmsList} are correct if:
									 * \begin{enumerate}[(i)] \item each two
									 * methods of {\tt pmsList} have different
									 * signatures; \item all methods of {\tt
									 * pmsList} have the same return value type;
									 * \item the first textually declared method
									 * is preceded by keyword {\tt overload}. No
									 * other method is preceded by this keyword;
									 * \item if one method of {\tt pmsList} is
									 * final, all methods of this list should be
									 * final too. If it is not final, no method
									 * of the list should be final;
									 * 
									 * \item no method of {\tt pmsList} is
									 * protected or abstract;
									 * 
									 * \item {\tt interMSList} should be empty.
									 * \end{enumerate}
									 *
									 */

									// each two methods of {\tt pmsList} have
									// different signatures ?
									final int size_pmsList = pmsList_PP_This_Proto
											.size();
									for (int ii = 0; ii < size_pmsList; ++ii) {
										for (int jj = ii
												+ 1; jj < size_pmsList; ++jj) {
											if ( pmsList_PP_This_Proto.get(ii)
													.getFullName(env)
													.equals(pmsList_PP_This_Proto
															.get(jj)
															.getFullName(
																	env)) ) {
												env.error(pmsList_PP_This_Proto
														.get(ii)
														.getFirstSymbol(),
														"Method '"
																+ pmsList_PP_This_Proto
																		.get(ii)
																		.getName()
																+ "' "
																+ "of line "
																+ pmsList_PP_This_Proto
																		.get(ii)
																		.getFirstSymbol()
																		.getLineNumber()
																+ " is being duplicated in line "
																+ pmsList_PP_This_Proto
																		.get(jj)
																		.getFirstSymbol()
																		.getLineNumber());
											}
										}
										// no method of {\tt pmsList} should be
										// protected or abstract;
										if ( pmsList_PP_This_Proto.get(ii)
												.getMethod()
												.getVisibility() == Token.PROTECTED ) {
											env.error(
													pmsList_PP_This_Proto.get(0)
															.getMethod()
															.getFirstSymbol(),
													"This method belongs to a overloaded method. It cannot be 'protected'");
										}
										if ( pmsList_PP_This_Proto.get(ii)
												.getMethod().isAbstract() ) {
											env.error(
													pmsList_PP_This_Proto.get(0)
															.getMethod()
															.getFirstSymbol(),
													"This method belongs to a overloaded method. It cannot be 'abstract'");
										}

									}
									// the first textually declared method is
									// preceded by keyword {\tt overload}. No
									// other method is preceded by this keyword
									if ( !pmsList_PP_This_Proto.get(0)
											.getMethod()
											.getPrecededBy_overload() ) {
										env.error(
												pmsList_PP_This_Proto.get(0)
														.getFirstSymbol(),
												"The first method of a overloaded method in a prototype should be preceded by keyword 'overload'");
									}

									final boolean isFirstFinal = pmsList_PP_This_Proto
											.get(0).getMethod().getIsFinal();

									// all methods of {\tt pmsList} have the
									// same return value type;
									final Type returnType = pmsList_PP_This_Proto
											.get(0).getReturnType(env);
									for (int ii = 1; ii < size_pmsList; ++ii) {
										if ( returnType != pmsList_PP_This_Proto
												.get(ii).getReturnType(env) ) {
											env.error(pmsList_PP_This_Proto
													.get(ii).getFirstSymbol(),
													"Methods of lines "
															+ pmsList_PP_This_Proto
																	.get(0)
																	.getFirstSymbol()
																	.getLineNumber()
															+ " and "
															+ pmsList_PP_This_Proto
																	.get(ii)
																	.getFirstSymbol()
																	.getLineNumber()
															+ " have different return types but equal names");
										}
										// No other method should b preceded by
										// 'overload'
										if ( pmsList_PP_This_Proto.get(ii)
												.getMethod()
												.getPrecededBy_overload() ) {
											env.error(
													pmsList_PP_This_Proto.get(0)
															.getFirstSymbol(),
													"Only the first method of a overloaded method in a prototype should be preceded by keyword 'overload'");
										}
										// if one method of {\tt pmsList} is
										// final, all methods of this list
										// should be
										// final too. If it is not final, no
										// method of the list should be final
										if ( pmsList_PP_This_Proto.get(ii)
												.getMethod()
												.getIsFinal() != isFirstFinal ) {
											env.error(pmsList_PP_This_Proto
													.get(ii).getFirstSymbol(),
													"Either all methods of a overloaded method of a prototype are 'final' or none is. "
															+ " However, methods of lines "
															+ pmsList_PP_This_Proto
																	.get(0)
																	.getFirstSymbol()
																	.getLineNumber()
															+ " and "
															+ pmsList_PP_This_Proto
																	.get(ii)
																	.getFirstSymbol()
																	.getLineNumber()
															+ " have different qualifiers");
										}
									}
									// {\tt interMSList} should be empty.
									if ( interMSList_Impl_Interfaces != null
											&& interMSList_Impl_Interfaces
													.size() > 0 ) {
										env.error(
												pmsList_PP_This_Proto.get(0)
														.getFirstSymbol(),
												"This method belongs to a overloaded method and "
														+ " there is a method signature with the same name being declared in interface "
														+ interMSList_Impl_Interfaces
																.get(0)
																.getDeclaringInterface()
																.getFullName()
														+ " that is implemented by "
														+ " the current object");
									}
									final List<MethodDec> overloadMethodList = new ArrayList<>();

									for (final MethodSignature ms : pmsList_PP_This_Proto) {
										overloadMethodList.add(ms.getMethod());
										ms.getMethod().setOverload(true);
										alreadCheckedList.add(ms.getMethod()
												.getMethodNumber());
									}
									proto.addOverloadMethodList(
											overloadMethodList);
								}
								else if ( msList_PPP_This_And_SuperProto
										.size() > pmsList_PP_This_Proto
												.size() ) {
									/*
									 * there is at least one method in the
									 * super-prototype. \item Suppose there is
									 * at least one method in {\tt msList} that
									 * is in a super-prototype and:
									 * \begin{enumerate}[(i)] \item there is
									 * just one method in {\tt pmsList} which is
									 * preceded by 'override'; \item the return
									 * value type of {\tt m} is a subtype of the
									 * return value type of {\tt m1}, which is
									 * the first method with name equal to {\tt
									 * m} found in a search starting in the
									 * super-prototype of {\tt P} and continuing
									 * upwards; \item all methods of {\tt
									 * msList} have the same signature except
									 * for the return value type. That would
									 * mean that each method of {\tt msList} is
									 * in a different prototype; \item if the
									 * method of {\tt pmsList} is protected, so
									 * are all the methods of the list {\tt
									 * msList}; \item no method of {\tt msList}
									 * is preceded by {\tt overload}.
									 * \end{enumerate} Then the declaration of
									 * the method of {\tt pmsList} is correct
									 * even if {\tt interMSList} contains an
									 * element.
									 */
									oneMethodIn_pmsList = pmsList_PP_This_Proto
											.size() == 1;
									final MethodDec superMethod = msList_PPP_This_And_SuperProto
											.get(msList_PPP_This_And_SuperProto
													.size() - 1)
											.getMethod();
									if ( superMethod.getShared()
											&& !method.getHasOverride() ) {
										env.error(method.getFirstSymbol(),
												"Method '" + methodName
														+ "' overrides a method of superprototype '"
														+ superMethod
																.getDeclaringObject()
																.getFullName()
														+ "'. However, the superprototype method is shared and this overridding is illegal");

									}
									if ( !method.getHasOverride() ) {

										env.error(method.getFirstSymbol(),
												"Method '" + method.getName()
														+ "' overrides a super-prototype method of prototype '"
														+ msList_PPP_This_And_SuperProto
																.get(msList_PPP_This_And_SuperProto
																		.size()
																		- 1)
																.getMethod()
																.getDeclaringObject()
																.getFullName()
														+ "'. It should be preceded by keyword 'override'");
									}
									else {
										if ( (superMethod
												.getVisibility() == Token.PACKAGE
												&& superMethod
														.getDeclaringObject() != null)
												&& (method.getDeclaringObject()
														.getCompilationUnit()
														.getCyanPackage() != superMethod
																.getDeclaringObject()
																.getCompilationUnit()
																.getCyanPackage()) ) {
											/*
											 * the super method has visibility
											 * 'package'. The subprototype
											 * overrides it. Then the
											 * subprototype should be in the
											 * same package as the
											 * superprototype
											 */
											final String superName = superMethod
													.getDeclaringObject()
													.getFullName();
											env.error(method.getFirstSymbol(),
													"Method '" + methodName
															+ "' overrides a method of super-prototype '"
															+ superName
															+ "'. The visibility of the superprototype method is 'package'. "
															+ "Then in order to override it the subprototype '"
															+ method.getDeclaringObject()
																	.getFullName()
															+ "' should be in package '"
															+ superMethod
																	.getDeclaringObject()
																	.getCompilationUnit()
																	.getPackageName()
															+ "'");
										}

									}
									if ( method.isAbstract()
											&& !msList_PPP_This_And_SuperProto
													.get(1).getMethod()
													.isAbstract() ) {
										env.error(method.getFirstSymbol(),
												"Method '" + method.getName()
														+ "' overrides a super-prototype method that is not abstract. This is illegal");
									}
									/*
									 * the return value type of {\tt m} is a
									 * subtype of the return value type of {\tt
									 * m1}, which is the first method with name
									 * equal to {\tt m} found in a search
									 * starting in the super-prototype of {\tt
									 * P} and continuing upwards;
									 */
									// superProto must be different from null
									// because msList.size() > pmsList.size()
									ObjectDec nextObj = superProto;
									List<MethodSignature> firstEqualNameList = nextObj
											.searchMethodProtectedPublicPackage(
													methodName);
									while (firstEqualNameList == null
											|| firstEqualNameList.size() == 0) {
										nextObj = nextObj.getSuperobject();
										firstEqualNameList = nextObj
												.searchMethodProtectedPublicPackage(
														methodName);
									}
									if ( !firstEqualNameList.get(0)
											.getReturnType(env).isSupertypeOf(
													method.getMethodSignature()
															.getReturnType(env),
													env) ) {
										env.error(method.getFirstSymbol(),
												"This method should have a return value type that is subtype of the return type of "
														+ "method of line "
														+ firstEqualNameList
																.get(0)
																.getMethod()
																.getFirstSymbol()
																.getLineNumber()
														+ " of prototype '"
														+ firstEqualNameList
																.get(0)
																.getMethod()
																.getDeclaringObject()
																.getFullName());
									}
									/*
									 * all methods of {\tt msList} have the same
									 * signature except for the return value
									 * type. That would mean that each method of
									 * {\tt msList} is in a different prototype;
									 *
									 */

									final String fullNameFirst = msList_PPP_This_And_SuperProto
											.get(0).getFullName(env);
									for (int ii = 1; ii < msList_PPP_This_And_SuperProto
											.size(); ++ii) {
										if ( !msList_PPP_This_And_SuperProto
												.get(ii).getFullName(env)
												.equals(fullNameFirst) ) {
											allSameSignature = false;
										}
									}
									final Token firstVisibility = pmsList_PP_This_Proto
											.get(0).getMethod().getVisibility();
									for (final MethodSignature ms : msList_PPP_This_And_SuperProto) {
										if ( ms.getMethod()
												.getVisibility() != firstVisibility ) {
											isVisibilityOk = false;
										}
										if ( ms.getMethod()
												.getPrecededBy_overload() ) {
											somePrecededByOverload = true;
										}
									}
									if ( !allSameSignature
											&& interMSList_Impl_Interfaces != null
											&& interMSList_Impl_Interfaces
													.size() > 0 ) {
										env.error(method.getFirstSymbol(),
												"This is a overloaded method and its signature is being defined in interface '"
														+ interMSList_Impl_Interfaces
																.get(0)
																.getDeclaringInterface()
																.getFullName()
														+ "'. This is illegal. This method is a overloaded method "
														+ "because there is a method in the same prototype or in super-prototypes with the same name but with different "
														+ "types for the parameters");
									}

									if ( somePrecededByOverload ) {
										final List<MethodDec> overloadMethodList = new ArrayList<>();
										for (final MethodSignature ms : pmsList_PP_This_Proto) {
											overloadMethodList
													.add(ms.getMethod());
											alreadCheckedList.add(ms.getMethod()
													.getMethodNumber());
											ms.getMethod().setOverload(true);
										}
										proto.addOverloadMethodList(
												overloadMethodList);
									}
									else if ( !allSameSignature
											&& pmsList_PP_This_Proto
													.size() == 1 ) {
										env.error(method.getFirstSymbol(),
												"Method " + method.getName()
														+ " overrides a super-prototype method but it has a different signature. That is "
														+ "the type of at least one parameter is different from the type of the corresponding parameter of "
														+ "one method of one of the super-prototypes.");
									}
								}
								else if ( !oneMethodIn_pmsList
										|| !allSameSignature || !isVisibilityOk
										|| somePrecededByOverload ) {
									/*
									 * \item Suppose there is at least one
									 * method in {\tt msList} that is in a
									 * super-prototype and there are at least
									 * two methods of {\tt msList} that have
									 * different signatures. That includes the
									 * case in which {\tt pmsList} has two
									 * methods (since they have the same name,
									 * they must have different signatures). The
									 * declaration of the methods of {\tt
									 * pmsList} are correct if:
									 * \begin{enumerate}[(i)] \item each two
									 * methods of {\tt pmsList} have different
									 * signatures. Note that {\tt pmsList} may
									 * have just one element, {\tt m}, although
									 * {\tt msList} should have at least two
									 * elements; \item all methods of {\tt
									 * pmsList} are preceded by keyword ``{\tt
									 * override}"\/; \item all methods of {\tt
									 * pmsList} have the same return value type;
									 * \item let {\tt Q} be the first direct or
									 * indirect super-prototype of {\tt P} that
									 * declares a method with the same name as
									 * {\tt m} and {\tt directSMList} be the
									 * list of methods of {\tt Q} that have the
									 * same name as {\tt m}. All methods of {\tt
									 * directSMList} have the same return value
									 * type {\tt R}. The return type of all
									 * methods of {\tt pmsList} should be
									 * subtype of {\tt R};
									 * 
									 * \item let {\tt T} be the super-prototype
									 * of {\tt P} that declares a method with
									 * the same name as {\tt m} and that is
									 * higher in the {\tt P} hierarchy. That is,
									 * no super-prototype of {\tt T} declares a
									 * method with the same name as {\tt m}.
									 * Then the first textually declared method
									 * of {\tt T} should be preceded by keyword
									 * {\tt overload}. No other method in the
									 * {\tt P} hierarchy should be preceded by
									 * this keyword;
									 * 
									 * \item either none or all methods of {\tt
									 * pmsList} are final; \item no method of
									 * {\tt pmsList} is protected or abstract;
									 * 
									 * \item {\tt interMSList} should be empty.
									 * \end{enumerate}
									 *
									 */

									final int size_pmsList = pmsList_PP_This_Proto
											.size();
									final boolean areMethodsFinal = pmsList_PP_This_Proto
											.get(0).getMethod().getIsFinal();
									final Type returnValueType = pmsList_PP_This_Proto
											.get(0).getReturnType(env);
									// each two methods of {\tt pmsList} have
									// different signatures. Note that {\tt
									// pmsList} may have just one element, {\tt
									// m}, although {\tt msList} should
									// have at least two elements;
									for (int ii = 0; ii < size_pmsList; ++ii) {
										for (int jj = ii
												+ 1; jj < size_pmsList; ++jj) {
											if ( pmsList_PP_This_Proto.get(ii)
													.getFullName(env)
													.equals(pmsList_PP_This_Proto
															.get(jj)
															.getFullName(
																	env)) ) {
												env.error(pmsList_PP_This_Proto
														.get(ii).getMethod()
														.getFirstSymbol(),
														"Methods of lines "
																+ pmsList_PP_This_Proto
																		.get(ii)
																		.getMethod()
																		.getFirstSymbol()
																		.getLineNumber()
																+ " and "
																+ pmsList_PP_This_Proto
																		.get(jj)
																		.getMethod()
																		.getFirstSymbol()
																		.getLineNumber()
																+ " have equal signatures. The method is then being redeclared");
											}
										}
										if ( !pmsList_PP_This_Proto.get(ii)
												.getMethod()
												.getHasOverride() ) {
											env.error(method.getFirstSymbol(),
													"This method overrides a super-prototype method. It should be preceded"
															+ " by keyword 'override'");
										}
										if ( returnValueType != pmsList_PP_This_Proto
												.get(ii).getReturnType(env) ) {
											env.error(
													pmsList_PP_This_Proto
															.get(ii).getMethod()
															.getFirstSymbol(),
													"Methods of lines "
															+ pmsList_PP_This_Proto
																	.get(0)
																	.getMethod()
																	.getFirstSymbol()
																	.getLineNumber()
															+ " and "
															+ pmsList_PP_This_Proto
																	.get(ii)
																	.getMethod()
																	.getFirstSymbol()
																	.getLineNumber()
															+ " have different return value types. They should be equal");
										}
										// no method of {\tt pmsList} should be
										// protected or abstract;
										if ( pmsList_PP_This_Proto.get(ii)
												.getMethod()
												.getVisibility() == Token.PROTECTED ) {
											env.error(
													pmsList_PP_This_Proto.get(0)
															.getMethod()
															.getFirstSymbol(),
													"This method belongs to a overloaded method. It cannot be 'protected'");
										}
										if ( pmsList_PP_This_Proto.get(ii)
												.getMethod().isAbstract() ) {
											env.error(
													pmsList_PP_This_Proto.get(0)
															.getMethod()
															.getFirstSymbol(),
													"This method belongs to a overloaded method. It cannot be 'abstract'");
										}
										if ( pmsList_PP_This_Proto.get(ii)
												.getMethod()
												.getIsFinal() != areMethodsFinal ) {
											env.error(method.getFirstSymbol(),
													"This method belong to a overloaded method. Either all methods with this "
															+ "same name in this prototype should be 'final' or none should be");
										}

									}
									if ( interMSList_Impl_Interfaces != null
											&& interMSList_Impl_Interfaces
													.size() > 0 ) {
										env.error(method.getFirstSymbol(),
												"This is a overloaded method and its signature is being defined in interface '"
														+ interMSList_Impl_Interfaces
																.get(0)
																.getDeclaringInterface()
																.getFullName()
														+ "'. This is illegal");
									}
									/*
									 * \item let {\tt Q} be the first direct or
									 * indirect super-prototype of {\tt P} that
									 * declares a method with the same name as
									 * {\tt m} and {\tt directSMList} be the
									 * list of methods of {\tt Q} that have the
									 * same name as {\tt m}. All methods of {\tt
									 * directSMList} have the same return value
									 * type {\tt R}. The return type of all
									 * methods of {\tt pmsList} should be
									 * subtype of {\tt R};
									 * 
									 */
									ObjectDec Q = superProto;
									while (Q != null) {
										final List<MethodSignature> directSMList = Q
												.searchMethodProtectedPublicPackage(
														methodName);
										if ( directSMList != null
												&& directSMList.size() > 0 ) {
											if ( !directSMList.get(0)
													.getReturnType(env)
													.isSupertypeOf(
															returnValueType,
															env) ) {
												env.error(
														method.getFirstSymbol(),
														"The return value type of this method should be equal or a subtype of "
																+ "the return value type of the first method with the same name found in a super-prototype. "
																+ "Then the return value type of this method should be equal or a subtype of "
																+ directSMList
																		.get(0)
																		.getReturnType(
																				env)
																		.getFullName());
											}
											break;
										}
										Q = Q.getSuperobject();
									}
									/*
									 * \item let {\tt T} be the super-prototype
									 * of {\tt P} that declares a method with
									 * the same name as {\tt m} and that is
									 * higher in the {\tt P} hierarchy. That is,
									 * no super-prototype of {\tt T} declares a
									 * method with the same name as {\tt m}.
									 * Then the first textually declared method
									 * of {\tt T} should be preceded by keyword
									 * {\tt overload}. No other method in the
									 * {\tt P}
									 * 
									 * hierarchy should be preceded by this
									 * keyword;
									 *
									 */
									ObjectDec scanSuperProtos = superProto;
									ObjectDec lastWithSameNameAsMethod = null;
									List<MethodSignature> lastMSList = null;
									while (scanSuperProtos != null) {
										final List<MethodSignature> directSMList = scanSuperProtos
												.searchMethodProtectedPublicPackage(
														methodName);
										if ( directSMList != null
												&& directSMList.size() > 0 ) {
											lastWithSameNameAsMethod = scanSuperProtos;
											lastMSList = directSMList;
										}
										scanSuperProtos = scanSuperProtos
												.getSuperobject();
									}
									if ( lastWithSameNameAsMethod == null
											|| lastMSList == null ) {
										env.error(method.getFirstSymbol(),
												"Internal error in calcInterfaceTypes of Program");
									}
									else {
										int n = lastMSList.get(0).getMethod()
												.getMethodNumber();
										int index = 0;
										for (int ii = 1; ii < lastMSList
												.size(); ++ii) {
											if ( lastMSList.get(ii).getMethod()
													.getMethodNumber() < n ) {
												index = ii;
												n = lastMSList.get(ii)
														.getMethod()
														.getMethodNumber();
											}
										}
										if ( !lastMSList.get(index).getMethod()
												.getPrecededBy_overload() ) {
											env.error(
													lastMSList.get(index)
															.getMethod()
															.getFirstSymbol(),
													"This method should be preceded by keyword 'overload'");
										}
									}
									final List<MethodDec> overloadMethodList = new ArrayList<>();
									for (final MethodSignature ms : pmsList_PP_This_Proto) {
										overloadMethodList.add(ms.getMethod());
										alreadCheckedList.add(ms.getMethod()
												.getMethodNumber());
										ms.getMethod().setOverload(true);
									}
									proto.addOverloadMethodList(
											overloadMethodList);
								}

							}
							/*
							 * boolean overloadedMethod = false; if ( superProto
							 * != null ) { /* if some method with the same name
							 * of any of the super-prototypes has been preceded
							 * by 'overloadedMethod' then all methods are
							 * multi-methods. Just set variable overloadedMethod
							 * of each method in the hierarchy from the current
							 * prototype onwards / mspppList = superProto.
							 * searchMethodProtectedPublicSuperProtectedPublic(
							 * methodName, env); for ( MethodSignature ms :
							 * mspppList ) { MethodDec other = ms.getMethod();
							 * if ( other.getPrecededBy_overloadedMethod() )
							 * overloadedMethod = true; } } if (
							 * overloadedMethod ) { for ( MethodSignature ms :
							 * mspppList ) { MethodDec other = ms.getMethod();
							 * other.setoverloadedMethod(true); } }
							 */

						}
					}
				}
				else {
					// an interface
					final InterfaceDec inter = (InterfaceDec) pu;
					final List<InterfaceDec> superInterfaceList = inter
							.getSuperInterfaceList();

					for (final MethodSignature ms : inter
							.getMethodSignatureList()) {
						final String nameMS = ms.getName();
						for (final MethodSignature ms2 : inter
								.getMethodSignatureList()) {
							if ( nameMS == ms2.getName() && ms != ms2 ) {
								/*
								 * two equal method signatures
								 */
								env.error(cunit, ms.getFirstSymbol(),
										"There are two equal method signatures in this interface in lines "
												+ ms.getFirstSymbol()
														.getLineNumber()
												+ " and " + ms2.getFirstSymbol()
														.getLineNumber());
							}
						}
						if ( superInterfaceList != null ) {
							for (final InterfaceDec superInterface : superInterfaceList) {

								final List<MethodSignature> msList = superInterface
										.searchMethodPublicSuperPublicOnlyInterfaces(
												nameMS, env);
								/*
								 * each method superMS of one of the super
								 * interfaces with the same name as nameMS
								 * should have exactly the same parameter types
								 * and the same return value type.
								 */
								for (final MethodSignature superMS : msList) {
									if ( ms.getReturnType(env) != superMS
											.getReturnType(env) ) {
										env.error(cunit, ms.getFirstSymbol(),
												"This signature has a return type different from the signature declared "
														+ " in super interface '"
														+ superInterface
																.getFullName()
														+ "'");
									}
									if ( ms instanceof MethodSignatureWithKeywords ) {
										/**
										 * the parameter types should be equal
										 */
										final MethodSignatureWithKeywords msng = (MethodSignatureWithKeywords) ms;
										final List<MethodKeywordWithParameters> keywordList = msng
												.getKeywordArray();
										final MethodSignatureWithKeywords msngSuper = (MethodSignatureWithKeywords) superMS;
										final List<MethodKeywordWithParameters> keywordListSuper = msngSuper
												.getKeywordArray();
										int i = 0;
										for (final MethodKeywordWithParameters sel : keywordList) {
											final MethodKeywordWithParameters selSuper = keywordListSuper
													.get(i);
											int j = 0;
											for (final ParameterDec p : sel
													.getParameterList()) {
												final ParameterDec pSuper = selSuper
														.getParameterList()
														.get(j);
												Type sub = p.getType();
												if ( sub == null ) {
													sub = Type.Dyn;
												}
												Type superT = pSuper.getType();
												if ( superT == null ) {
													superT = Type.Dyn;
												}
												if ( sub != superT ) {
													env.error(cunit,
															ms.getFirstSymbol(),
															"This signature is different from the signature declared "
																	+ " in super interface '"
																	+ superInterface
																			.getFullName()
																	+ "'");

												}
												++j;
											}
											++i;
										}
									}
									else if ( ms instanceof MethodSignatureOperator ) {
										final MethodSignatureOperator msop = (MethodSignatureOperator) ms;
										final MethodSignatureOperator msopSuper = (MethodSignatureOperator) superMS;
										if ( msop.getParameterList() == null
												|| msop.getParameterList()
														.size() == 0 ) {
											if ( msopSuper
													.getParameterList() != null ) {
												env.error(cunit,
														ms.getFirstSymbol(),
														"This signature is different from the signature declared "
																+ " in super interface '"
																+ superInterface
																		.getFullName()
																+ "'");
											}
										}
										else if ( msopSuper
												.getParameterList() == null
												|| msopSuper.getParameterList()
														.size() == 0 ) {
											env.error(cunit,
													ms.getFirstSymbol(),
													"This signature is different from the signature declared "
															+ " in super interface '"
															+ superInterface
																	.getFullName()
															+ "'");
										}
										else // both non-null
										if ( msop.getParameterList().get(0)
												.getType() != msopSuper
														.getParameterList()
														.get(0).getType() ) {
											env.error(cunit,
													ms.getFirstSymbol(),
													"This signature is different from the signature declared "
															+ " in super interface '"
															+ superInterface
																	.getFullName()
															+ "'");
										}
									}
								}
							}
						}
					}
				}
			}
			env.atEndOfCurrentCompilationUnit();
		}
	}

	/**
	 * There is not more communication in Package. This method should not be
	 * used
	 *
	 * make the metaobject annotations of packages communicate with each other
	 */
	protected void makeAnnotationsCommunicateInPackage(Env env) {

		/*
		 * every metaobject can supply information to other metaobjects. Every
		 * tuple in this set correspond to a metaobject annotation. Every tuple
		 * is composed of a metaobject name, the number of this metaobject
		 * considering all metaobjects in the prototype, the number of this
		 * metaobject considering only the metaobjects with the same name, the
		 * package name, the prototype name, and the information this metaobject
		 * annotation wants to share with other metaobject annotations.
		 */

		for (final CyanPackage cp : this.packageList) {

			final HashSet<Tuple5<String, Integer, Integer, String, Object>> moInfoSet = new HashSet<>();
			for (final CompilationUnit compUnit : cp.getCompilationUnitList()) {

				if ( !compUnit.getHasGenericPrototype() ) {
					for (final Prototype pu : compUnit.getPrototypeList()) {

						/*
						 * only prototypes that do not have a '<' in its name
						 * can communicate. For example, <code>Set{@literal
						 * <}Int></code> cannot communicate with anyone.
						 */
						final List<Annotation> allAnnotationList = new ArrayList<>();
						final List<Annotation> metaobjectAnnotationList = pu
								.getCompleteAnnotationList();
						if ( metaobjectAnnotationList != null ) {
							allAnnotationList.addAll(metaobjectAnnotationList);
						}
						final List<AnnotationAt> metaobjectWithAtAnnotationList = pu
								.getNonAttachedAnnotationList();
						if ( metaobjectWithAtAnnotationList != null ) {
							allAnnotationList
									.addAll(metaobjectWithAtAnnotationList);
						}
						for (final Annotation annotation : allAnnotationList) {
							final CyanMetaobject cyanMetaobject = annotation
									.getCyanMetaobject();
							// // cyanMetaobject.setAnnotation(annotation, 0);
							/*
							 * there is not communication in packages anymore
							 */
							/*
							 * if ( cyanMetaobject instanceof
							 * ICommunicateInPackage_afterResTypes_semAn ) {
							 * final Object sharedInfo =
							 * ((ICommunicateInPackage_afterResTypes_semAn)
							 * cyanMetaobject).
							 * afterResTypes_semAn_shareInfoPackage();
							 * //annotation.shareInfoPackage(); if ( sharedInfo
							 * != null ) { if ( !cp.getCommunicateInPackage() )
							 * { env.error(annotation.getFirstSymbol(),
							 * "This metaobject annotation is trying to communicate with "
							 * + "other prototypes. " +
							 * "This is illegal because package '" +
							 * cp.getPackageName() +
							 * "' does not allow that. To make that " +
							 * "legal, attach '@feature(communicateInPackage, #on)' to the package in the project (.pyan) file"
							 * , true, false); }
							 * 
							 * if ( pu.getGenericParameterListList().size() == 0
							 * ) { final Tuple5<String, Integer, Integer,
							 * String, Object> t = new Tuple5<String, Integer,
							 * Integer, String, Object>(
							 * annotation.getCyanMetaobject().getName(),
							 * annotation.getAnnotationNumber(),
							 * annotation.getAnnotationNumberByKind(),
							 * annotation.getPrototypeOfAnnotation(),
							 * sharedInfo); moInfoSet.add(t); } else {
							 * env.error(true, annotation.getFirstSymbol(),
							 * "metaobject annotation of metaobject '" +
							 * annotation.getCyanMetaobject().getName() +
							 * "' is trying to communicate with other metaobjects of the package."
							 * +
							 * "This is prohibit because this metaobject is a generic prototype instantiation or has a '<' in its name"
							 * , null, ErrorKind.metaobject_error); } } }
							 */
						}
					}
				}
			}

			/*
			 * send information to all metaobjects of the package that want to
			 * receive information. Let them communicate with each other
			 */
			if ( moInfoSet.size() > 0 ) {
				for (final CompilationUnit compUnit : cp
						.getCompilationUnitList()) {
					if ( !compUnit.getHasGenericPrototype() ) {
						for (final Prototype pu : compUnit.getPrototypeList()) {
							final List<Annotation> allAnnotationList = new ArrayList<>();
							final List<Annotation> metaobjectAnnotationList = pu
									.getCompleteAnnotationList();
							if ( metaobjectAnnotationList != null ) {
								allAnnotationList
										.addAll(metaobjectAnnotationList);
							}
							final List<AnnotationAt> metaobjectWithAtAnnotationList = pu
									.getNonAttachedAnnotationList();
							if ( metaobjectWithAtAnnotationList != null ) {
								allAnnotationList
										.addAll(metaobjectWithAtAnnotationList);
							}

							/*
							 * there is not communication in packages anymore
							 */
							/*
							 * 
							 * for ( final Annotation annotation :
							 * allAnnotationList) { final CyanMetaobject
							 * cyanMetaobject = annotation.getCyanMetaobject();
							 * // // cyanMetaobject.setAnnotation(annotation,
							 * 0);
							 * 
							 * if ( cyanMetaobject instanceof
							 * ICommunicateInPackage_afterResTypes_semAn ) {
							 * ((ICommunicateInPackage_afterResTypes_semAn )
							 * cyanMetaobject).
							 * afterResTypes_semAn_receiveInfoPackage(moInfoSet)
							 * ; } }
							 */

						}
					}
				}
			}
		}
	}

	// /**
	// * call an action method of all metaobjects that should be called after
	// typing
	// the prototype interfaces
	// */
	// public boolean ati3_check(Env env) {
	//
	// final ICompiler_afterResTypes compiler_afterResTypes = new
	// Compiler_afterResTypes(env);
	// // makeAnnotationsCommunicateInPackage(env);
	// for ( final CompilationUnit compilationUnit : compilationUnitList ) {
	// compilationUnit.getCyanPackage().addPackageMetaToClassPath_and_Run( () ->
	// {
	// compilationUnit.ati3_check(compiler_afterResTypes);
	// } );
	// }
	// return true;
	// }

	public boolean afterSemAn_check(Env env) {

		final ICompiler_semAn compiler_semAn = new Compiler_semAn(env);
		for (final CompilationUnit compilationUnit : compilationUnitList) {
			try {

				compilationUnit.getCyanPackage()
						.addPackageMetaToClassPath_and_Run(() -> {
							compilationUnit.afterSemAn_checkDeclaration(
									compiler_semAn);
						});
			}
			catch (final error.CompileErrorException e) {
			}
		}
		return true;
	}

	/**
	 * call a method of all metaobjects. This method should be called after
	 * typing the prototype interfaces
	 */
	public boolean afterResTypes_actions(Env env) {
		boolean ret = true;
		try {

			final ICompiler_afterResTypes compiler_afterResTypes = new Compiler_afterResTypes(
					env);
			final CompilerManager_afterResTypes compilerManager = new CompilerManager_afterResTypes(
					env);

			// no more communication in packages
			// makeAnnotationsCommunicateInPackage(env);

			int i = 0;
			while (i < compilationUnitList.size()) {
				final CompilationUnit compilationUnit = compilationUnitList
						.get(i);
				if ( compilationUnit.getAlreadPreviouslyCompiled() ) {
					++i;
					continue;
				}
				try {

					compilationUnit.getCyanPackage()
							.addPackageMetaToClassPath_and_Run(() -> {
								compilationUnit.afterResTypes_actions(
										compiler_afterResTypes,
										compilerManager);
							});

					if ( compilationUnit.getErrorList() != null
							&& compilationUnit.getErrorList().size() > 0 ) {
						env.setThereWasError(true);
					}
				}
				catch (final Throwable e) {
					compilationUnit.error(0, 0, "Exception '" + e.getClass()
							+ "' was thrown and not caught. Its message is '"
							+ e.getMessage() + "'");
					ret = false;
				}
				++i;
			}

			/*
			 * all changes demanded by metaobject annotations collected above
			 * are made in the call to
			 * CompilerManager_afterResTypes#changeCheckProgram.
			 */
			if ( !ret ) {
				return ret;
			}
			compilerManager.changeCheckProgram();
			if ( env.isThereWasError() ) {
				return false;
			}
			return true;
		}
		catch (final Throwable e) {
			return false;
		}
	}

	/**
	 *
	 * 
	 * @param env
	 */
	public void genJava(Env env) {

		// Prototype mainPrototype = env.searchPackagePrototype("main",
		// "Program");
		// before generating code,

		final String mainPackageName = project.getMainPackage();
		final CyanPackage mainPackage = project.searchPackage(mainPackageName);
		if ( mainPackage == null ) {
			try {
				env.error(null,
						"According to the project file (.pyan) for this program, the main package is '"
								+ mainPackageName
								+ "'. This package was not found");
			}
			catch (final error.CompileErrorException e) {
			}
			return;
		}
		final String mainPrototypeName = project.getMainObject();
		final Prototype mainPrototype = mainPackage
				.searchPublicNonGenericPrototype(mainPrototypeName);
		env.setAddTypeInfo(addTypeInfo);
		if ( addTypeInfo ) {
			fileNameToAddTypeInfo = NameServer.generateFileNameToAddTypeInfo(
					mainPackageName, mainPrototypeName);
			if ( this.typeInfoPath != null ) {
				if ( !typeInfoPath.endsWith(File.separator) ) {
					typeInfoPath += File.separator;
				}
				fileNameToAddTypeInfo = typeInfoPath + fileNameToAddTypeInfo;
			}
			env.setFileNameToAddTypeInfo(fileNameToAddTypeInfo);
		}
		if ( mainPrototype == null ) {
			try {
				env.error(null,
						"According to the project file (.pyan) for this program, the main prototype is '"
								+ mainPackageName + "." + mainPrototypeName
								+ "'. This prototype was not found");
			}
			catch (final CompileErrorException e) {
			}
			return;
		}
		else if ( !(mainPrototype instanceof ObjectDec) ) {
			try {
				env.error(null,
						"According to the project file (.pyan) for this program, the main prototype is '"
								+ mainPackageName + "." + mainPrototypeName
								+ "'. But this is illegal because this is an interface");
			}
			catch (final CompileErrorException e) {
			}
			return;
		}
		else if ( ((ObjectDec) mainPrototype).getIsAbstract() ) {
			try {
				env.error(null,
						"According to the project file (.pyan) for this program, the main prototype is '"
								+ mainPackageName + "." + mainPrototypeName
								+ "'. But this is illegal because this prototype is abstract");
			}
			catch (final CompileErrorException e) {
			}
			return;
		}

		final File javaLib = new File(javaLibDir);
		if ( !javaLib.exists() || !javaLib.isDirectory() ) {
			env.error(null, "Directory '" + javaLibDir
					+ "' does not exist. This was declared "
					+ "as the directory of the Cyan runtime libraries, option -javalib of the compiler");
		}
		// boolean foundCyanruntime = false;
		// for ( final File f : javaLib.listFiles() ) {
		// if ( f.getName().toLowerCase().endsWith("cyanruntime") ) {
		// foundCyanruntime = true;
		// break;
		// }
		// }
		// if ( ! foundCyanruntime ) {
		// env.error(null, "Directory '" + javaLibDir +
		// NameServer.fileSeparatorAsString
		// + "cyanruntime' does not exist. " +
		// "Directory '" + javaLibDir + "' is the directory of the Cyan runtime
		// libraries, option -javalib of the compiler");
		// }
		/*
		 * File []runtimeList = javaLib.listFiles(new FilenameFilter() {
		 * 
		 * @Override public boolean accept(File dir, String name) { return
		 * name.toLowerCase().endsWith("cyanruntime"); } }); if ( runtimeList ==
		 * null || runtimeList.length == 0 ) { env.error(null, "Directory '" +
		 * javaLibDir + NameServer.fileSeparatorAsString +
		 * "cyanruntime' does not exist. " + "Directory '" + javaLibDir +
		 * "' is the directory of the Cyan runtime libraries, option -javalib of the compiler"
		 * ); }
		 */

		if ( !createPackageDirectories(env, project.getProjectDir()) ) {
			return;
		}

		if ( !createMainJavaClass(mainPackageName, mainPrototypeName,
				(ObjectDec) mainPrototype, mainPackage, env) ) {
			return;
		}
		// String projectName = project.getProjectName();
		javac += "\" \"" + this.javaForProjectPathLessSlash
				+ NameServer.fileSeparator
				+ mainPackageName.replace('.', NameServer.fileSeparator)
				+ NameServer.fileSeparator + mainJavaClassWithoutExtensionName
				+ ".java\"";
		cmdArray.add(this.javaForProjectPathLessSlash + NameServer.fileSeparator
				+ mainPackageName.replace('.', NameServer.fileSeparator)
				+ NameServer.fileSeparator + mainJavaClassWithoutExtensionName
				+ ".java");
		execCode += " " + mainPackageName + "."
				+ mainJavaClassWithoutExtensionName;
		execCodeList
				.add(mainPackageName + "." + mainJavaClassWithoutExtensionName);
		final String cmdLineArgs = this.project.getCmdLineArgs();
		if ( cmdLineArgs != null ) {
			execCode += " " + cmdLineArgs;
			String[] cmdLineArgsList = cmdLineArgs.split(" ");
			Collections.addAll(execCodeList, cmdLineArgsList);
		}

		// MyFile.writeFileText(this.project.getProjectDir() +
		// NameServer.fileSeparatorAsString + "1.bat", javac.toCharArray());

		String newFileName = null;
		for (final CyanPackage cyanPackage : this.packageList) {
			boolean useCompiledPrototypes = cyanPackage
					.getUseCompiledPrototypes();
			String outputDirectory = cyanPackage.getOutputDirectory();

			if ( outputDirectory.charAt(outputDirectory.length()
					- 1) != NameServer.fileSeparator ) {
				outputDirectory += NameServer.fileSeparatorAsString;
			}

			int numJavaClasses = 0;
			StringBuffer allInter = null;

			if ( cyanPackage.getName()
					.equals(NameServer.cyanLanguagePackageName) ) {
				if ( isFeatureTrue(NameServer.compilePackageCyanLang) ) {
					/*
					 * something like
					 *
					 * @feature("compilePackageCyanLang", true) program
					 * 
					 * in the project file
					 */
					allInter = new StringBuffer();
				}
			}
			else if ( cyanPackage
					.getCreateInterfaceFileForSeparateCompilation() ) {
				allInter = new StringBuffer();
			}
			// List<WrExprAnyLiteral> awe = cyanPackage.searchFeature("annot");
			// if ( awe != null && awe.size() > 0 ) {
			// for ( WrExprAnyLiteral we : awe ) {
			// if ( we instanceof WrExprLiteralString ) {
			// WrExprLiteralString wls = (WrExprLiteralString ) we;
			// if ( wls.getJavaValue().equals("compilePackage") ) {
			// /*
			// * something like
			// *
			// *
			// * program
			// * @annot("compilePackage")
			// * package cyan.io
			//
			// in the project file
			// */
			// allInter = new StringBuffer();
			// break;
			// }
			// }
			// }
			// }
			if ( allInter != null ) {
				if ( useCompiledPrototypes ) {
					env.error(null, "Internal error when generaling Java code: "
							+ "the compiler should both produce code for "
							+ "package '" + cyanPackage.getName() + "' and use "
							+ "code already compiled for it");
				}
				allInter.append("package " + cyanPackage.getName() + "\n\n");
			}
			// # put all compilation units in an array, save them all at the
			// same time
			for (final CompilationUnit compilationUnit : cyanPackage
					.getCompilationUnitList()) {
				if ( compilationUnit.getAlreadPreviouslyCompiled() ) {
					continue;
				}

				// if there is a generic program unit in the compilation unit,
				// then code is not generated for this compilation unit.
				// a generic program unit should be the sole program unit in a
				// compilation unit.
				if ( !compilationUnit.getHasGenericPrototype()
						&& compilationUnit.getErrorList().size() == 0 ) {
					++numJavaClasses;
					FileOutputStream fos = null;
					String filenameWithoutExt = outputDirectory
							+ compilationUnit.getPublicPrototype()
									.getJavaNameWithoutPackage();
					newFileName = filenameWithoutExt + ".java";
					try {
						// newFileName = outputDirectory +
						// compilationUnit.getPublicPrototype().getJavaNameWithoutPackage()
						// + ".java";

						fos = new FileOutputStream(newFileName);

						PrintWriter printWriter = new PrintWriter(fos, true);
						PW pw = new PW();
						pw.set(printWriter);

						compilationUnit.genJava(pw, env);
						printWriter.close();

						if ( allInter != null ) {
							compilationUnit.genCompiledInterfaces(allInter);
						}

						if ( compilationUnit
								.getPublicPrototype() == Type.Any ) {
							// generate the interface for Any

							newFileName = outputDirectory + NameServer.IAny
									+ ".java";

							fos = new FileOutputStream(newFileName);

							printWriter = new PrintWriter(fos, true);
							pw = new PW();
							pw.set(printWriter);
							((ObjectDec) compilationUnit.getPublicPrototype())
									.generateInterface(pw, env);
							printWriter.close();

						}
					}
					catch (final CompileErrorException e) {
					}
					catch (final FileNotFoundException e) {
						// e.printStackTrace();
						System.out.println("Cannot create file " + newFileName
								+ " The compiler is finished");
						System.exit(1);
						// env.error(null,"Cannot create file " + newFileName +
						// " The compiler is finished");

					}
					catch (final NullPointerException e) {
						e.printStackTrace();
						env.error(null,
								"Internal error in Program::genJava. NPE in "
										+ newFileName);
					}
					catch (final ClassCastException e) {
						env.error(null, "ClassCastException in Program::genJava"
								+ newFileName);
					}
					catch (final Exception e) {
						env.error(null, "Error in writing to file "
								+ newFileName
								+ " the message of the exception throw was:\n"
								+ e.getMessage());
					}
					finally {
						try {
							if ( fos != null ) {
								fos.close();
							}
						}
						catch (final IOException e) {
							env.error(null,
									"Error in closing file " + newFileName);
						}
					}
				}
			}
			/*
			 * write to file packageFilenameInter the interfaces of all
			 * prototypes of this package
			 */
			if ( allInter != null ) {
				String packageFilenameInter = cyanPackage
						.getPackageCanonicalPath();
				if ( packageFilenameInter.charAt(packageFilenameInter.length()
						- 1) != NameServer.fileSeparator ) {
					packageFilenameInter += NameServer.fileSeparatorAsString;
				}

				packageFilenameInter = packageFilenameInter
						+ NameServer.fileName_of_interfacesCompiledPrototypes;

				char[] charArray = new char[allInter.length()];
				allInter.getChars(0, allInter.length(), charArray, 0);
				if ( !MyFile.writeFileText(packageFilenameInter, charArray) ) {
					env.setThereWasError(true);
					this.project.error("Error when writing file '"
							+ packageFilenameInter + "' with"
							+ " the interfaces of all prototypes of package '"
							+ cyanPackage.getName() + "'");
				}
			}

			if ( numJavaClasses == 0 ) {

				/*
				 * create a single Java file for this package. A Cyan package
				 * can be empty because there may be a single Generic prototype
				 * in it that was not instantiated. A Java package cannot be
				 * empty. Then we create a Java class ReadMe_Number
				 */
				final String className = "ReadMe_"
						+ Math.abs(cyanPackage.getName().hashCode());
				newFileName = outputDirectory + className + ".java";

				FileOutputStream fos;
				try {
					fos = new FileOutputStream(newFileName);
					final PrintWriter printWriter = new PrintWriter(fos, true);
					printWriter.append("package " + cyanPackage.getName()
							+ ";\r\n\r\nclass " + className + " { }");
					printWriter.close();
				}
				catch (final FileNotFoundException e) {

					try {
						env.error(null, "Cannot create file '" + newFileName
								+ "'. This file was being "
								+ "created because there was a Cyan package without any non-generic prototype. "
								+ "The compiler was ended");
					}
					finally {
						System.exit(1);
					}

				}
			}

		}
		/*
		 * if ( ! foundError ) {
		 * 
		 * compileGeneratedJavaCode(env); }
		 */
	}

	/**
	 * compile the generated Java code if the compiler option calljavac is on.
	 * After that, if compiler option 'exec' is on, execute the generated code.
	 */
	public void compileGeneratedJavaCode(Env env) {
		try {
			if ( this.project.getCallJavac() ) {
				// System.out.println("Calling the Java compiler");
				final Runtime rt = Runtime.getRuntime();
				saci.OSType ostype = saci.OsCheck.getOperatingSystemType();
				Process proc = null;
				try {
					/*
					 * javac -cp
					 * "C:/Program Files/Java/jdk1.8.0_201/jre/lib/rt.jar"
					 * ;"C:\Dropbox\Cyan\lib\cyan.lang.jar" -sourcepath
					 * "C:\Dropbox\Cyan\lib";
					 * "C:\Dropbox\Cyan\cyanTests\java-for-master"
					 * "C:\Dropbox\Cyan\cyanTests\java-for-master\main\P.java"
					 */
					for (CyanPackage cp : this.packageList) {
						if ( cp.getCreateInterfaceFileForSeparateCompilation() ) {
							StringBuilder sb = new StringBuilder();
							File f = new File(cp.getOutputDirectory());
							String outDir = cp.getOutputDirectory()
									+ File.separator;
							String outDir2Slash = outDir.replace(File.separator,
									File.separator + File.separator);
							for (File jf : f.listFiles()) {
								String jfname = jf.getName();
								if ( jfname.endsWith(".java") ) {
									sb.append(" \"" + outDir2Slash + jfname
											+ "\"");
								}
							}
							String outFilename = outDir + "javaFileList.txt";
							MyFile.write(outFilename, sb);
							javac += " @" + outFilename;
							cmdArray.add("@" + outFilename);
							// System.out.println("javac == " + javac);
						}
					}
					TM.endTime("just before 'rt.exec(javac)'");
					switch (ostype) {
					case Windows:
						// System.out.println("\r\n\r\njavac var = " + javac +
						// "\r\n\r\n");
						// proc = rt.exec(javac);
						// break;
					case Linux:
						String[] cmdStrArray = new String[cmdArray.size()];
						int ii = 0;
						// System.out.println("Command line for javac");
						cmdStrArray[0] = cmdArray.get(0);
						int jj = 1;
						String cpstr = "";
						for (String aCmd : cmdArray) {
							if ( ii > 0 && aCmd.startsWith("-cp") ) {
								if ( cpstr.length() == 0 ) {
									cpstr = cmdArray.get(ii + 1);
								}
								else {
									cpstr += File.pathSeparator
											+ cmdArray.get(ii + 1);
								}
							}
							++ii;
						}
						if ( cpstr.length() > 0 ) {
							cmdStrArray[jj] = "-cp";
							++jj;
							cmdStrArray[jj] = cpstr;
							++jj;
						}
						cpstr = "";
						ii = 0;
						for (String aCmd : cmdArray) {
							if ( ii > 0 && aCmd.startsWith("-sourcepath") ) {
								if ( cpstr.length() == 0 ) {
									cpstr = cmdArray.get(ii + 1);
								}
								else {
									cpstr += File.pathSeparator
											+ cmdArray.get(ii + 1);
								}
							}
							++ii;
						}
						if ( cpstr.length() > 0 ) {
							cmdStrArray[jj] = "-sourcepath";
							++jj;
							cmdStrArray[jj] = cpstr;
							++jj;

						}
						cmdStrArray[jj] = cmdArray.get(cmdArray.size() - 1);
						String[] realCmdArray = new String[jj + 1];
						for (int kk = 0; kk <= jj; ++kk) {
							realCmdArray[kk] = cmdStrArray[kk];
						}
						// System.out.println("\n\njavac = " + javac + " \n\n");
						// for ( String ss : realCmdArray ) {
						// System.out.print(ss + " ");
						// }
						proc = rt.exec(realCmdArray);
						break;
					case MacOS:
						System.out.println(
								"I am very sorry but the Cyan compiler does not run in Apple Operating Systems. Yet");
						break;
					case Other:
						System.out.println(
								"Unknow operation system. The Cyan compiler does not run in this OS");
						break;
					}
					TM.endTime("after 'rt.exec(javac)'");

				}
				catch (final SecurityException e) {
					env.error(null,
							"Error in calling 'javac'. Probably this program is not in the PATH variable");
				}
				catch (final IOException e) {
					env.error(null,
							"Error in calling 'javac'. There was an input/output error. Message: "
									+ e.getMessage());
				}
				catch (final NullPointerException e) {
					e.printStackTrace();
					env.error(null,
							"Error in calling 'javac'. Probably an internal error of this program");
				}
				catch (final IllegalArgumentException e) {
					env.error(null, "Internal error in '"
							+ this.getClass().getName()
							+ "'. Arguments to 'javac' are not well built");
				}
				if ( proc == null ) {
					return;
				}
				final InputStream stderr = proc.getErrorStream();
				final InputStreamReader isr = new InputStreamReader(stderr);
				final BufferedReader br = new BufferedReader(isr);
				String line = null;
				final List<String> outList = new ArrayList<>();
				while ((line = br.readLine()) != null) {
					outList.add(line);
				}
				br.close();
				int exitVal = proc.waitFor();
				if ( exitVal != 0 ) {
					System.out.println(
							"Error when compiling the Java code generated by the Cyan compiler (exit code "
									+ exitVal + ")");
					for (final String s : outList) {
						System.out.println(s);
					}
				}
				else {
					String allPaths = "";
					cpPathList.add("\"" + this.javaLibDir + File.separator
							+ "cyanruntime.jar\"");
					int size = cpPathList.size();
					for (final String path : this.cpPathList) {
						allPaths += path;
						if ( --size > 0 ) {
							allPaths += File.pathSeparator;
						}
					}
					// ###
					execCode = "\"" + Saci.javaHome + File.separator + "bin"
							+ File.separator + "java\" -cp " + allPaths + " "
							+ execCode;

					List<String> tmp = new ArrayList<>();
					tmp.add(Saci.javaHome + File.separator + "bin"
							+ File.separator + "java");
					tmp.add("-cp");
					tmp.add(allPaths);
					tmp.addAll(execCodeList);
					execCodeList = tmp;
					execCode = "@echo off\r\n" + execCode;
					MyFile.writeFileText(
							this.project.getProjectCanonicalPath()
									+ this.project.getExecFileName(),
							execCode.toCharArray());
					if ( this.project.getExec() ) {
						// call the program
						// System.out.println("Executing the code");

						Process p = null;

						try {
							// System.out.println(execCode);
							TM.endTime("before '.exec(execCode)'");

							switch (ostype) {
							case Windows:
								// execCode = execCode.replace('\\', '/'); //
								// ('/', File.separatorChar)
								// p = Runtime.getRuntime().exec(execCode);
								// break;
							case Linux:
								String[] execArray = new String[execCodeList
										.size()];
								int ij = 0;
								for (String ex : execCodeList) {
									execArray[ij] = ex.replace('\\', '/');
									++ij;
								}
								p = rt.exec(execArray);
								break;
							case MacOS:
								System.out.println(
										"I am very sorry but the Cyan compiler does not run in Apple Operating Systems. Yet");
								break;
							case Other:
								System.out.println(
										"Unknow operation system. The Cyan compiler does not run in this OS");
								break;
							}
							TM.endTime("after '.exec(execCode)'");

						}
						catch (final SecurityException e) {
							env.error(null,
									"Error in calling the compiled Cyan program. Probably 'java.exe' is not in the PATH variable");
						}
						catch (final IOException e) {
							env.error(null,
									"Input/output error when running the compiled Cyan program. Maybe 'java.exe' is not in the PATH variable");
						}
						catch (final NullPointerException e) {
							env.error(null,
									"NPE when calling the compiled Cyan program");
						}
						catch (final IllegalArgumentException e) {
							env.error(null, "Internal error in '"
									+ this.getClass().getName()
									+ "'. Arguments to 'java.exe' are not well built");
						}

						if ( p == null ) {
							return;
						}

						exitVal = p.waitFor();
						final BufferedReader error = new BufferedReader(
								new InputStreamReader(p.getErrorStream()));
						while ((line = error.readLine()) != null) {
							System.out.println(line);
						}
						error.close();

						TM.endTime("after error.close");
						final BufferedReader input = new BufferedReader(
								new InputStreamReader(p.getInputStream()));
						while ((line = input.readLine()) != null) {
							System.out.println(line);
						}

						input.close();
						TM.endTime("after input.close");

						final OutputStream outputStream = p.getOutputStream();
						final PrintStream printStream = new PrintStream(
								outputStream);
						printStream.println();
						printStream.flush();
						printStream.close();

						if ( exitVal != 0 ) {
							System.out.println(
									"Error when executing the code generated by the Java compiler (exit code "
											+ exitVal + ")");
						}
						TM.endTime("after printing program output");
					}
				}

			}
		}
		catch (final Throwable t) {
			t.printStackTrace();
			env.error(null,
					"Internal error when compiling or executing the generated Java code."
							+ " Exception " + t.getClass().getName()
							+ " was thrown. Its message is '" + t.getMessage()
							+ "'. Its cause is exception '"
							+ (t.getCause() != null ? "null"
									: t.getCause().getClass().getName())
							+ "'");
		}
	}

	private boolean createPackageDirectories(Env env, String projectDir) {

		final char separator = NameServer.fileSeparatorAsString.charAt(0);

		String partialProjectDir;
		if ( projectDir.charAt(projectDir.length() - 1) == separator ) {
			partialProjectDir = projectDir.substring(0,
					projectDir.length() - 1);
		}
		else {
			partialProjectDir = projectDir;
		}
		int lastSlash = partialProjectDir.lastIndexOf(separator);
		if ( lastSlash < 0 ) {
			env.error(null,
					"The project directory cannot be the root directory. There should be at least one "
							+ separator + " in it");
			return false;
		}
		// partialProjectDir = partialProjectDir.substring(0,
		// partialProjectDir.length()
		// - 1);
		lastSlash = partialProjectDir.lastIndexOf(separator);
		if ( lastSlash < 0 ) {
			env.error(null,
					"The project directory cannot be the root directory. There should be at least one "
							+ separator + " in it");
			return false;
		}
		// here partialProjectDir is the directory in which the project dir is.
		final String projectDirName = partialProjectDir
				.substring(lastSlash + 1);
		partialProjectDir = partialProjectDir.substring(0, lastSlash);
		final String javaForProjectPath = partialProjectDir + separator
				+ NameServer.startDirNameOutputJavaCode + projectDirName
				+ separator;

		/*
		 * javac -sourcepath
		 * "C:\Dropbox\Cyan\lib\\" + NameServer.startDirNameOutputJavaCode +  "
		 * cyan_lang"; "C:\Dropbox\Cyan\lib\javalib";
		 * "C:\Dropbox\Cyan\cyanTests\java-for-master"
		 * java-for-master/main/*.java *
		 */
		String javacCall = this.javac = Saci.javaHome + File.separator + "bin"
				+ File.separator + "javac";
		this.javac = javacCall + " ";
		cmdArray = new ArrayList<>();
		cmdArray.add(javacCall);
		if ( this.sourcePathList != null && this.sourcePathList.size() > 0 ) {
			for (String sourcePath : this.sourcePathList) {
				sourcePath = sourcePath.replace('\\', '/');
				if ( sourcePath.length() > 0 ) {
					javac += " -sourcepath \"" + sourcePath + "\" ";
					cmdArray.add("-sourcepath");
					cmdArray.add(sourcePath);
				}
			}
		}
		// execCode = "java ";
		cpPathList = new ArrayList<>();
		String s = "";
		ArrayList<String> classPathArray = new ArrayList<>();
		if ( classPathList != null ) {
			// execCode += "-cp ";
			// javac += "-cp ";
			// int size = classPathList.size();
			for (String classPath : classPathList) {
				classPath = classPath.replace('\\', '/');
				if ( classPath.length() != 0 ) {
					if ( s.length() != 0 ) {
						s += NameServer.pathSeparatorAsString;
					}
					s += "\"" + classPath + "\"";
					classPathArray.add(s);
					this.cpPathList.add("\"" + classPath + "\"");
				}
			}
			// javac += " ";
		}
		for (final CyanPackage p : this.packageList) {
			if ( p.getUseCompiledPrototypes() ) {

				String classPath = p.getPackageCanonicalPath();
				String packageNameDot = p.getName().replace('.', separator);
				int classPathLen = classPath.length();
				if ( classPath.charAt(classPath.length() - 1) == separator ) {
					classPath = classPath.substring(0, classPathLen - 1);
					--classPathLen;
				}
				String packageJar;
				if ( classPath.endsWith(packageNameDot) ) {
					packageJar = classPath.substring(0,
							classPathLen - packageNameDot.length());
				}
				else {
					packageJar = classPath;
				}
				packageJar += p.getName() + ".jar";
				if ( s.length() != 0 ) {
					s += NameServer.pathSeparatorAsString;
				}
				s += "\"" + packageJar + "\"";
				classPathArray.add(packageJar);
				this.cpPathList.add("\"" + packageJar + "\"");
			}
		}
		if ( s.length() != 0 ) {
			// execCode += " -cp " + s + " ";
			s += NameServer.pathSeparatorAsString + "\"" + this.javaLibDir
					+ File.separator + "cyanruntime.jar\"";
			javac += " -cp " + s + " ";
			for (String apath : classPathArray) {
				this.cmdArray.add("-cp");
				cmdArray.add(meta.MetaHelper.removeQuotes(apath));
			}
		}
		else {
			javac += " -cp \"" + this.javaLibDir + File.separator
					+ "cyanruntime.jar\"";
		}
		javac += " -sourcepath ";
		final File projectOutputDir = new File(javaForProjectPath);
		if ( projectOutputDir.exists()
				&& !MyFile.deleteFileDirectory(projectOutputDir) ) {
			env.error(null, "Unable to delete directory " + javaForProjectPath
					+ " or some of its files");
		}
		if ( !projectOutputDir.mkdirs() ) {
			env.error(null, "Unable to create output directory '"
					+ javaForProjectPath + "'");
		}

		execCode = "";
		execCodeList = new ArrayList<>();
		for (final CyanPackage p : this.packageList) {
			String outputDir;
			final String packageName = p.getPackageName();
			final String localPackagePath = packageName.replace('.', separator);
			if ( p.getPackageName().equals(MetaHelper.cyanLanguagePackageName)
					&& p.getCreateInterfaceFileForSeparateCompilation() ) {
				String pcp = p.getPackageCanonicalPath();
				if ( pcp.charAt(pcp.length() - 1) == separator ) {
					pcp = pcp.substring(0, pcp.length() - 1);
				}
				int indexSlash = pcp.lastIndexOf(separator);
				pcp = pcp.substring(0, indexSlash);
				indexSlash = pcp.lastIndexOf(separator);
				pcp = pcp.substring(0, indexSlash);
				final String partialOutDir = pcp + separator
						+ NameServer.startDirNameOutputJavaCode
						+ packageName.replace('.', '_');
				outputDir = partialOutDir + separator + localPackagePath;
				p.setOutputDirectory(outputDir);
				javac += "\"" + partialOutDir + "\";";
				cmdArray.add("-sourcepath");
				cmdArray.add(partialOutDir);

				// execCode += "\"" + partialOutDir + "\";";
				cpPathList.add("\"" + partialOutDir + "\"");
			}
			else {
				outputDir = javaForProjectPath + localPackagePath;
				p.setOutputDirectory(outputDir);
			}

			final File f = new File(outputDir);
			boolean ok;
			final String errorMessage = MyFile.deleteNonDirFiles(f);
			if ( errorMessage != null ) {
				env.error(null, "Cannot delete the files of directory '"
						+ outputDir + "'");
			}
			if ( !f.exists() ) {
				ok = f.mkdirs();
				if ( !ok ) {
					env.error(null, "Unable to create output directory '"
							+ outputDir + "'");
				}
			}
		}
		javaForProjectPathLessSlash = javaForProjectPath;
		if ( javaForProjectPath
				.charAt(javaForProjectPath.length() - 1) == separator ) {
			javaForProjectPathLessSlash = javaForProjectPathLessSlash
					.substring(0, javaForProjectPath.length() - 1);
		}
		javac += "\"" + this.javaLibDir + "\""
				+ NameServer.pathSeparatorAsString + "\""
				+ javaForProjectPathLessSlash; // +
												// ""
												// +
												// "
												// java-for-"
												// +
												// projectDirName;
		cmdArray.add("-sourcepath");
		cmdArray.add(this.javaLibDir);
		cmdArray.add("-cp");
		cmdArray.add(this.javaLibDir + File.separator + "cyanruntime.jar");
		cmdArray.add("-sourcepath");
		cmdArray.add(javaForProjectPathLessSlash);

		// execCode += "\"" + this.javaLibDir + "\";\"" +
		// javaForProjectPathLessSlash +
		// "\""; // + "" + " java-for-" + projectDirName;
		this.cpPathList.add("\"" + this.javaLibDir + "\"");
		this.cpPathList.add("\"" + javaForProjectPathLessSlash + "\"");
		return true;

	}

	private List<String> topologicalSortingPrototypeList(Env env) {
		/*
		 * program unit, super-prototype list, sub-prototype list
		 */
		final HashMap<Prototype, Tuple2<List<Prototype>, List<Prototype>>> protoNameAdjList = new HashMap<>();
		List<Prototype> noSuperList = new ArrayList<>();
		final List<Prototype> prototypeList = new ArrayList<>();
		/**
		 * collect the program units
		 */
		for (final CompilationUnit cunit : compilationUnitList) {
			if ( !cunit.hasGenericPrototype() ) {
				for (final Prototype pu : cunit.getPrototypeList()) {
					prototypeList.add(pu);
					final List<Prototype> superList = new ArrayList<>();
					final List<Prototype> subList = new ArrayList<>();
					protoNameAdjList.put(pu, new Tuple2<>(superList, subList));
				}
			}
		}
		final ObjectDec anyPrototype = (ObjectDec) Type.Any;
		final Tuple2<List<Prototype>, List<Prototype>> anySuperSub = protoNameAdjList
				.get(anyPrototype);
		/**
		 * build the graph of sub-type and super-type relationships
		 */
		for (final Prototype pu : prototypeList) {
			final Tuple2<List<Prototype>, List<Prototype>> t = protoNameAdjList
					.get(pu);
			if ( t == null ) {
				env.error(null, "Internal error: program unit '"
						+ pu.getFullName()
						+ "' was not found in topological sorting (Program.java)");
				return null;
			}
			final List<Prototype> superList = t.f1;
			if ( pu instanceof InterfaceDec ) {
				/*
				 * add Any in the list of super-prototypes of the interface
				 */
				superList.add(anyPrototype);

				/*
				 * add interface to the list of sub-prototypes of Any
				 */
				anySuperSub.f2.add(pu);

				/*
				 * an interface only has super-interfaces
				 */
				final InterfaceDec inter = (InterfaceDec) pu;
				final List<InterfaceDec> superInterList = inter
						.getSuperInterfaceList();
				if ( superInterList != null && superInterList.size() > 0 ) {
					superList.addAll(superInterList);
					for (final InterfaceDec superInter : superInterList) {
						final Tuple2<List<Prototype>, List<Prototype>> superT = protoNameAdjList
								.get(superInter);
						/*
						 * add pu as a sub-type of superInter, one of the
						 * super-interfaces of pu
						 */
						if ( superT == null ) {
							env.error(null, "Internal error: program unit '"
									+ superInter.getFullName()
									+ "' was not found in topological sorting (Program.java)");
							return null;
						}
						superT.f2.add(pu);
					}
				}
			}
			else {
				final ObjectDec proto = (ObjectDec) pu;
				final ObjectDec superProto = proto.getSuperobject();
				if ( superProto != null ) {
					superList.add(superProto);
					final Tuple2<List<Prototype>, List<Prototype>> superT = protoNameAdjList
							.get(superProto);
					/*
					 * add pu as a sub-type of superProto, the super-prototype
					 * of pu
					 */
					if ( superT == null ) {
						env.error(null, "Internal error: program unit '"
								+ superProto.getFullName()
								+ "' was not found in topological sorting (Program.java)");
						return null;
					}
					superT.f2.add(pu);

				}
				else {
					// no super-prototype, first in the list
					noSuperList.add(proto);
				}
				final List<Expr> implInterExprList = proto.getInterfaceList();
				if ( implInterExprList != null
						&& implInterExprList.size() > 0 ) {
					for (final Expr implInterExpr : implInterExprList) {
						final InterfaceDec superInter = (InterfaceDec) implInterExpr
								.getType();
						superList.add(superInter);
						final Tuple2<List<Prototype>, List<Prototype>> superT = protoNameAdjList
								.get(superInter);
						/*
						 * add pu as a sub-type of superInter, one of the
						 * super-interfaces of pu
						 */
						if ( superT == null ) {
							env.error(null, "Internal error: program unit '"
									+ superInter.getFullName()
									+ "' was not found in topological sorting (Program.java)");
							return null;
						}
						superT.f2.add(pu);
					}
				}
			}

		}
		/**
		 * make sure the basic types and Any and Nil are put first in the list
		 */
		final List<Prototype> noSuperListBasicCyanLangFirst = new ArrayList<>();
		noSuperListBasicCyanLangFirst.add((Prototype) Type.Any);
		noSuperListBasicCyanLangFirst.add((Prototype) Type.Nil);
		noSuperListBasicCyanLangFirst.add((Prototype) Type.Byte);
		noSuperListBasicCyanLangFirst.add((Prototype) Type.Int);
		noSuperListBasicCyanLangFirst.add((Prototype) Type.Long);
		noSuperListBasicCyanLangFirst.add((Prototype) Type.Float);
		noSuperListBasicCyanLangFirst.add((Prototype) Type.Double);
		noSuperListBasicCyanLangFirst.add((Prototype) Type.Char);
		noSuperListBasicCyanLangFirst.add((Prototype) Type.Boolean);
		// noSuperListBasicCyanLangFirst.add( (Prototype ) Type.CySymbol );
		noSuperListBasicCyanLangFirst.add((Prototype) Type.String);
		/*
		 * prototypes of package cyan.lang are put next in the list
		 */
		for (final Prototype pu : noSuperList) {
			if ( MetaHelper.cyanLanguagePackageName
					.equals(pu.getCompilationUnit().getPackageName())
					&& !MetaHelper.isBasicType(pu.getName()) ) {
				noSuperListBasicCyanLangFirst.add(pu);
			}
		}
		/*
		 * then all other prototypes
		 */
		for (final Prototype pu : noSuperList) {
			if ( !MetaHelper.cyanLanguagePackageName
					.equals(pu.getCompilationUnit().getPackageName()) ) {
				noSuperListBasicCyanLangFirst.add(pu);
			}
		}
		noSuperList = noSuperListBasicCyanLangFirst;
		/**
		 * do the topological sorting
		 */
		final List<Prototype> sortedPrototype = new ArrayList<>();
		while (noSuperList.size() > 0) {
			final Prototype pu = noSuperList.get(0);
			noSuperList.remove(0);
			sortedPrototype.add(pu);

			final Tuple2<List<Prototype>, List<Prototype>> t = protoNameAdjList
					.get(pu);
			final List<Prototype> subPUList = t.f2;
			/*
			 * remove all edges from sub-types to the super-type pu
			 */
			for (final Prototype subProto : subPUList) {
				final Tuple2<List<Prototype>, List<Prototype>> subT = protoNameAdjList
						.get(subProto);
				/*
				 * remove pu from the list of super-types of subT
				 */
				int i = 0;
				for (final Prototype superSubProto : subT.f1) {
					if ( superSubProto == pu ) {
						subT.f1.remove(i);
						break;
					}
					++i;
				}
				/*
				 * pu must have been found in list subT.f1 --- no test if it was
				 * really found
				 */
				if ( subT.f1.size() == 0 ) {
					/*
					 * now subProto has no super-types (the edge was removed).
					 */
					noSuperList.add(subProto);
				}

			}
			/*
			 * remove all edges from the super-type pu to its sub-types
			 */
			t.f2.clear();
		}

		for (final Prototype pu : prototypeList) {
			final Tuple2<List<Prototype>, List<Prototype>> t = protoNameAdjList
					.get(pu);
			final List<Prototype> superList = t.f1;
			if ( superList.size() > 0 ) {
				final String puName = pu.getFullName();
				StringBuilder s = new StringBuilder(puName);
				for (final Prototype superPU : superList) {
					final String superFullName = superPU.getFullName();
					if ( superFullName.equals(puName) ) {
						break;
					}
					s.append(", ").append(superFullName);
				}
				s.append(", ").append(puName);
				env.error(pu.getFirstSymbol(),
						"This program unit is part of a circular inheritance/interface implementation: '"
								+ s.append("'").toString());
			}

		}
		final List<String> sortedStrPrototypeList = new ArrayList<>();
		for (final Prototype pu : sortedPrototype) {
			String protoJavaName;
			if ( pu instanceof InterfaceDec ) {
				protoJavaName = MetaHelper.getJavaName(
						NameServer.prototypeFileNameFromInterfaceFileName(
								((InterfaceDec) pu).getFullName()));
			}
			else {
				protoJavaName = pu.getJavaName();
			}
			sortedStrPrototypeList.add(protoJavaName);

		}
		return sortedStrPrototypeList;
	}

	private static void print_addCYAN_HOME_classPath(PW pw) {
		pw.println(
				"	public static String cyanLangJar = \"cyan.lang.jar\";\r\n"
						+ "	public static String cyanRuntime = \"cyanruntime.jar\";\r\n"
						+ "" + "	@SuppressWarnings(\"resource\")\r\n"
						+ "	private static void addCYAN_HOME_classPath() {\r\n"
						+ "		String cyanHome = System.getenv(\"CYAN_HOME\");\r\n"
						+ "		if ( cyanHome == null ) {\r\n"
						+ "			System.out.println(\"cyan home is null\");\r\n"
						+ "			return ;\r\n" + "		}\r\n"
						+ "		File f = new File(cyanHome);\r\n"
						+ "		if ( ! f.exists() || !f.isDirectory() ) {\r\n"
						+ "			System.out.println(\"directory '\" + cyanHome +\r\n"
						+ "					\"' does not exist. It is as the value of the System variable CYAN_HOME\");\r\n"
						+ "			return ;\r\n" + "		}\r\n"
						+ "		File cyanLangJarFile = new File(cyanHome + File.separator + cyanLangJar);\r\n"
						+ "		File cyanRuntimeFile = new File(cyanHome + File.separator + cyanRuntime);\r\n"
						+ "		if ( !cyanLangJarFile.exists() || cyanLangJarFile.isDirectory() ||\r\n"
						+ "			 !cyanRuntimeFile.exists() || cyanRuntimeFile.isDirectory() ) {\r\n"
						+ "			System.out.println(\"One or two of the following files are missing from directory '\"\r\n"
						+ "					+ cyanHome + \"': \" + cyanLangJar + \", \" + cyanRuntime);\r\n"
						+ "			System.exit(1);\r\n" + "		}\r\n"
						+ "		URL []urlArray = new URL[3];\r\n"
						+ "		try {\r\n"
						+ "			urlArray[0] = f.toURI().toURL();\r\n"
						+ "			urlArray[1] = cyanLangJarFile.toURI().toURL();\r\n"
						+ "			urlArray[2] = cyanRuntimeFile.toURI().toURL();\r\n"
						+ "\r\n"
						+ "			ClassLoader prevCl = Thread.currentThread().getContextClassLoader();\r\n"
						+ "			ClassLoader urlCl = URLClassLoader.newInstance(urlArray, prevCl);\r\n"
						+ "			Thread.currentThread().setContextClassLoader(urlCl);\r\n"
						+ "           @SuppressWarnings(\"unused\")"
						+ "			cyan.lang._ExceptionReadFormat notUsed_var;\r\n"
						+
						// " Class<?> aClass =
						// Thread.currentThread().getContextClassLoader().loadClass(\"cyan.lang._ExceptionReadFormat\");\r\n"
						// +
						// " System.out.println(\"************\" +
						// aClass.getName());\r\n" +
						// | ClassNotFoundException
						"		}\r\n"
						+ "		catch (MalformedURLException  e) {\r\n"
						+ "			System.out.println(\"Error in transforming the directory \"\r\n"
						+ "					+ \"given by environment variable CYAN_HOME into a URL\");\r\n"
						+ "			System.exit(1);\r\n" + "		}\r\n"
						+ "\r\n" + "	}\r\n" + "\r\n" + "");
	}

	private boolean createMainJavaClass(String mainPackageName,
			String mainPrototypeName, ObjectDec mainPrototype,
			CyanPackage mainPackage, Env env) {

		String newFileName = null;

		FileOutputStream fos = null;
		try {
			// newFileName = project.getProjectDir();

			final String projectName = this.project.getProjectName();
			for (int i = 0; i < projectName.length(); ++i) {
				final char ch = projectName.charAt(i);
				if ( !Character.isLetterOrDigit(ch) && ch != '_' ) {
					env.error(null, "The project name, '" + projectName
							+ "' has a character, '" + ch
							+ "' that is not allowed. Use only letters, digits, and underscore");
					return false;
				}
			}

			final int sizeProjectName = projectName.length();
			mainJavaClassWithoutExtensionName = Character
					.toUpperCase(projectName.charAt(0))
					+ projectName.substring(1, sizeProjectName);

			newFileName = mainPackage.getOutputDirectory()
					+ NameServer.fileSeparator
					+ mainJavaClassWithoutExtensionName + ".java";

			fos = new FileOutputStream(newFileName);

			final PrintWriter printWriter = new PrintWriter(fos, true);
			final PW pw = new PW();
			pw.set(printWriter);

			pw.println("package " + mainPackageName + ";\n");
			pw.println("import cyan.lang.*;");
			pw.println("import cyanruntime.*;");
			pw.println("import java.io.FileWriter ;\r\n"
					+ "import java.io.PrintWriter;\r\n"
					+ "import java.io.File;\r\n"
					+ "import java.io.IOException;\r\n"
					+ "import java.net.MalformedURLException;\r\n"
					+ "import java.net.URL;\r\n"
					+ "import java.net.URLClassLoader;");
			pw.println("");
			pw.println("class " + mainJavaClassWithoutExtensionName + " { ");
			pw.add();

			Program.print_addCYAN_HOME_classPath(pw);
			pw.println("");
			pw.printlnIdent("public static void main(String []args) { ");
			pw.add();
			String projectDir = this.project.getProjectDir();
			if ( projectDir.endsWith(File.separator) ) {
				projectDir = projectDir.substring(0, projectDir.length() - 1);
			}
			projectDir = projectDir.replace('\\', '/');

			if ( this.addTypeInfo ) {
				pw.printlnIdent(
						"cyanruntime.CyanRuntime.dynamicTypeInfoProgram = new DynamicTypeInfoProgram("
								+ "\""
								+ MetaLexer.escapeJavaString(
										this.fileNameToAddTypeInfo)
								+ "\", " + "\""
								+ MetaLexer.escapeJavaString(projectDir) + "\""
								+ ");");
			}
			pw.printlnIdent("addCYAN_HOME_classPath();");

			String realParameter = "";
			List<MethodSignature> runMSList = mainPrototype
					.searchMethodPrivateProtectedPublic("run:1");

			if ( runMSList != null && runMSList.size() > 0 ) {
				if ( runMSList.size() > 1 ) {
					try {
						env.error(null,
								"According to the project file of this program (.pyan), the main prototype is '"
										+ mainPackageName + "."
										+ mainPrototypeName
										+ "'. However, the 'run' method of this prototype is a overloaded method, "
										+ "which is illegal");
					}
					catch (final CompileErrorException e) {
					}
					return false;
				}
				final MethodSignatureWithKeywords msRun = (MethodSignatureWithKeywords) runMSList
						.get(0);
				final String fullNameType = msRun.getKeywordArray().get(0)
						.getParameterList().get(0).getType().getFullName();
				if ( !fullNameType.equals("cyan.lang.Array<String>") ) {
					env.error(msRun.getMethod().getFirstSymbol(),
							"This is the main method, the one in which "
									+ "the execution will start. It should have a parameter of type 'Array<String>' or no parameter at all (a unary method)");
				}

			}

			final List<MethodSignature> unaryRunMSList = mainPrototype
					.searchMethodPrivateProtectedPublic("run");
			boolean ok = true;
			if ( runMSList == null || runMSList.size() == 0 ) {
				runMSList = unaryRunMSList;
				if ( runMSList == null || runMSList.size() == 0 ) {
					ok = false;
				}
			}
			else if ( unaryRunMSList != null && unaryRunMSList.size() > 0 ) {
				/*
				 * both 'run:' and 'run' in the same main prototype
				 */
				try {
					env.error(null,
							"According to the project file of this program (.pyan), the main prototype is '"
									+ mainPackageName + "." + mainPrototypeName
									+ "'. This prototype has both a 'run' and an 'run:' method");
				}
				catch (final CompileErrorException e) {
				}
				return false;
			}
			if ( !ok || runMSList == null ) {
				try {
					env.error(null,
							"According to the project file of this program (.pyan), the main prototype is '"
									+ mainPackageName + "." + mainPrototypeName
									+ "'. However, this prototype does not have a appropriate 'run' "
									+ "method. It should be without parameters or with just parameter 'Array<String>'");
				}
				catch (final CompileErrorException e) {
				}
				catch (final Throwable e) {
					System.out.println("Internal error in Program. Class name: "
							+ e.getClass().getName());
				}
				return false;
			}
			final List<MethodSignature> initMSList = mainPrototype
					.searchInitNewMethod("init");
			if ( initMSList == null || initMSList.size() == 0 ) {
				try {
					env.error(null,
							"According to the project file of this program (.pyan), the main prototype is '"
									+ mainPackageName + "." + mainPrototypeName
									+ "'. However, there is no 'init' method in this prototype, "
									+ "which is illegal");
				}
				catch (final CompileErrorException e) {
				}
				return false;
			}
			final MethodSignature runMethodSignature = runMSList.get(0);
			final List<ParameterDec> paramDecList = runMethodSignature
					.getParameterList();
			pw.printlnIdent("try { ");
			pw.add();
			if ( paramDecList != null && paramDecList.size() > 0 ) {
				// 'run: T t', with one parameter
				if ( !paramDecList.get(0).getType().getFullName()
						.equals(MetaHelper.cyanLanguagePackageName
								+ ".Array<String>") ) {
					ok = false;
				}
				else {
					final String javaAddName = MetaHelper
							.getJavaNameOfMethodWith("add:", 1);
					final String arrayStringName = MetaHelper
							.getJavaName("Array<String>");
					pw.printlnIdent(arrayStringName + " cyanArgs = new "
							+ arrayStringName + "();");

					pw.printlnIdent("for (int i = 0; i < args.length; ++i) {");
					pw.add();
					pw.printlnIdent("cyanArgs." + javaAddName
							+ "( new CyString(args[i]) );");
					pw.sub();
					pw.printlnIdent("}");
					realParameter = "cyanArgs";
				}
				// if ( env.getAddTypeInfo() ) {
				// genInitCodeAddTypeInfo(pw);
				// }
				// _Program.prototype._run(cyanArgs);
				pw.printlnIdent(mainPrototype.getJavaName() + ".prototype."
						+ MetaHelper.getJavaNameOfMethodWith("run:", 1) + "("
						+ realParameter + ");");
				// if ( env.getAddTypeInfo() ) {
				// genFinalCodeAddTypeInfo(pw);
				// }
			}
			else {
				// if ( env.getAddTypeInfo() ) {
				// genInitCodeAddTypeInfo(pw);
				// }
				pw.println("        (new " + mainPrototype.getJavaName()
						+ "())._run();");
				// if ( env.getAddTypeInfo() ) {
				// genFinalCodeAddTypeInfo(pw);
				// }
			}
			pw.sub();
			pw.printlnIdent("}");
			pw.printlnIdent("catch ( IndexOutOfBoundsException  e ) {\n");
			pw.add();
			pw.printlnIdent("e.printStackTrace();");
			pw.printlnIdent(
					"System.out.println(\"Index of array out of bounds. Remember that you cannot add \"\n");
			pw.printlnIdent(
					"  + \" an element to an array using indexing like in\\n\"\n");
			pw.printlnIdent(
					"  + \"    var v = Array<Int> new: 10;\\n    v[0] = 5;\\n\"\n");
			pw.printlnIdent(
					"  + \"This results in the exception 'ExceptionIndexOutOfBounds'. You should use method 'add:' instead:\\n\"\n");
			pw.printlnIdent(
					" + \"    var v = Array<Int> new: 10;\\n    v add: 5;\\n\");\n");
			pw.printlnIdent(
					"System.out.println(\"This same error occurs when using method 'add:' as in\\n    v add: 5, 0;\\n\");");
			pw.sub();
			pw.printlnIdent("}");

			pw.println("        catch (Throwable e) {");
			pw.println("            System.out.flush();");
			pw.println(
					"            if ( e instanceof ExceptionContainer__ ) {");
			pw.println("                String messageToWrite = null;");
			pw.println(
					"                if ( ((ExceptionContainer__) e).elem instanceof _ExceptionMethodNotFound ) {");
			pw.println(
					"                    _ExceptionMethodNotFound e1 = (_ExceptionMethodNotFound ) ((ExceptionContainer__) e).elem;");
			pw.println(
					"                    if ( e1._message() != null && e1._message().s.length() > 0 ) {");
			pw.println(
					"                        messageToWrite = e1._message().s;");
			pw.println("                    }");
			pw.println("                    else {");
			pw.println(
					"                        System.out.println(\"Method was not found. Exception "
							+ "_ExceptionMethodNotFound was thrown but not caught\");");
			pw.println("                    }");
			pw.println("                }");
			pw.println(
					"                else if ( ((ExceptionContainer__) e).elem instanceof _ExceptionCast) {");
			pw.println(
					"                    _ExceptionCast e1 = (_ExceptionCast ) ((ExceptionContainer__) e).elem;");
			pw.println(
					"                    if ( e1._message() != null && e1._message().s.length() > 0 ) {");
			pw.println(
					"                        messageToWrite = e1._message().s;");
			pw.println("                    }");
			pw.println("                }");
			pw.println("                if ( messageToWrite != null )");
			pw.println(
					"                    System.out.println(messageToWrite);");
			pw.println("                else");
			pw.println(
					"                    System.out.println(\"Exception \" + ((ExceptionContainer__)  e).elem.getClass().getName() + \" was thrown but not caught\");");
			pw.println("            }");
			pw.println("            else {");
			pw.println(
					"                System.out.println(\"Java exception \" + e.getClass().getName() + \" was thrown but not caught\");");
			pw.println("            }");
			pw.println("            System.out.flush();");
			pw.println("            e.printStackTrace();");

			pw.println("        }");

			if ( this.addTypeInfo ) {
				pw.println(
						"    cyanruntime.CyanRuntime.genJSON_DynamicTypeInfo();");
			}

			pw.sub();
			pw.printlnIdent("}");
			pw.println("}");
			pw.sub();

			printWriter.close();

		}
		catch (final FileNotFoundException e) {
			System.out.println("Cannot create file " + newFileName
					+ " The compiler is finished");
			System.exit(1);
			// env.error(null,"Cannot create file " + newFileName + " The
			// compiler is finished");
		}
		catch (final NullPointerException e) {
			// e.printStackTrace();
			env.error(null, "null pointer exception");
		}
		catch (final Exception e) {
			env.error(null, "error in writing to file " + newFileName);
		}
		finally {
			try {
				if ( fos != null ) {
					fos.close();
				}
			}
			catch (final IOException e) {
				// e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * @param pw
	 */
	private void genFinalCodeAddTypeInfo(final PW pw) {

		pw.printlnIdent("cyan.lang._System.writeToFileToAddTypeInfo("
				+ "\"  ]\\r\\n\", \r\n" + "        \"" + fileNameToAddTypeInfo
				+ "\");");
		pw.printlnIdent("cyan.lang._System.writeToFileToAddTypeInfo("
				+ "\"}\\r\\n\", \r\n" + "        \"" + fileNameToAddTypeInfo
				+ "\");");
		// pw.printlnIdent("}");
		pw.sub();
	}

	/**
	 * @param pw
	 */
	private void genInitCodeAddTypeInfo(final PW pw) {
		pw.printlnIdent(
				"cyanruntime.CyanRuntime.dynamicTypeInfoProgram = new cyanruntime.DynamicTypeInfoProgram("
						+ ");");
		/*
		 * pw.printlnIdent("" + "try (FileWriter fosFileToAddTypeInfo = " +
		 * "new FileWriter(\"" + fileNameToAddTypeInfo + "\");\r\n");
		 * pw.printlnIdent( "     PrintWriter fileToAddTypeInfo = " +
		 * "new PrintWriter(fosFileToAddTypeInfo)) {\r\n"); pw.add(); pw.
		 * printlnIdent("cyan.lang._System.fileToAddTypeInfo = fileToAddTypeInfo;"
		 * ); String projectDir = this.project.getProjectDir(); if (
		 * projectDir.endsWith(File.separator) ) { projectDir =
		 * projectDir.substring(0, projectDir.length()-1); } projectDir =
		 * projectDir.replace('\\', '/');
		 * pw.printlnIdent("cyan.lang._System.writeToFileToAddTypeInfo(" +
		 * "\"{\\r\\n\" + \r\n" + "  \"  \\\"project Directory\\\": \\\"" +
		 * projectDir + "\\\",\\r\\n\" +\r\n" + "  \"  \\\"data\\\": [ \",\r\n"
		 * + "\"" + fileNameToAddTypeInfo + "\");");
		 */
	}

	public Project getProject() {
		return project;
	}

	public List<CompilationUnit> getCompilationUnitList() {
		return compilationUnitList;
	}

	/**
	 * calculates the type of all method parameters, all return values of
	 * methods, and all fields of all compilation units of the program. The
	 * types depends on the packages imported each compilation unit
	 */

	public void calcInternalTypes(Env env) {

		Type.IMapName = env.searchPackagePrototype(
				MetaHelper.cyanLanguagePackageName, NameServer.IMapName);
		Type.ISetName = env.searchPackagePrototype(
				MetaHelper.cyanLanguagePackageName, NameServer.ISetName);

		// no more communication in packages
		// makeAnnotationsCommunicateInPackage(env);

		final ICompiler_semAn compiler_semAn = new Compiler_semAn(env);

		boolean inCompilationStep9 = env
				.getCompilationStep() == CompilationStep.step_9;

		int i = 0;
		while (i < compilationUnitList.size()) {
			// int mySize = compilationUnitList.size();
			final CompilationUnit compilationUnit = compilationUnitList.get(i);

			if ( !compilationUnit.getHasGenericPrototype()
					&& compilationUnit.getErrorList().size() == 0
					&& !compilationUnit.getAlreadPreviouslyCompiled() ) {
				try {

					// if ( inCompilationStep9 ) {
					// /*
					// * ICheckSubprototype_afterSemAn
					// ICheckOverride_afterSemAn
					// ICheckMessageSend_afterSemAn
					// */
					// Prototype pu = compilationUnit.getPublicPrototype();
					// List<Annotation> annotList =
					// pu.getCompleteAnnotationList();
					// boolean found_afterSemAn = false;
					// if ( annotList != null ) {
					// for ( Annotation annot : annotList ) {
					// CyanMetaobject mo = annot.getCyanMetaobject();
					// if ( mo instanceof ICheckSubprototype_afterSemAn ||
					// mo instanceof ICheckOverride_afterSemAn ||
					// mo instanceof ICheckMessageSend_afterSemAn ) {
					// found_afterSemAn = true;
					// break;
					// }
					// }
					// }
					// /*
					// * this phase is only necessary if there is a metaobject
					// * that implements checks in it. These metaobject
					// implement
					// * one of the following interfaces:
					// * ICheckSubprototype_afterSemAn
					// ICheckOverride_afterSemAn
					// ICheckMessageSend_afterSemAn
					// ICheckDeclaration_afterSemAn
					//
					// The last interface, ICheckDeclaration_afterSemAn, is not
					// activated in this method. It is in a separate method,
					// afterSemAn_check of this same class. See that Saci::run
					// calls afterSemAn_check.
					//
					// ICommunicateInPrototype_afterResTypes_semAn_afterSemAn
					// does not need
					// to be considered because it is only used in this
					// phase 9 if one of the previous interfaces is used
					// */
					// if ( ! found_afterSemAn ) {
					// ++i;
					// continue;
					// }
					// }

					compilationUnit.getCyanPackage()
							.addPackageMetaToClassPath_and_Run(() -> {
								compilationUnit
										.calcInternalTypes(compiler_semAn, env);
							});

				}
				catch (ErrorInMetaobjectException e) {
				}
				catch (final CompileErrorException e) {
				}
				catch (final Throwable e) {
					e.printStackTrace();
					env.error(null,
							"Compiler internal error: exception '"
									+ e.getClass() + "' was thrown "
									+ "but it was not caught. Its message is '"
									+ e.getMessage() + "'");
				}
			}
			++i;
		}

		if ( env.getCompInstSet().contains(
				CompilationInstruction.matchExpectedCompilationErrors) ) {
			checkErrorMessages(env);
		}
	}

	/**
	 * check whether all error messages demanded by calls to metaobject
	 * compilationError were really signaled. A metaobject may have implemented
	 * interface {@link meta#IInformCompilationError} and informed the compiler
	 * that an error should be signaled. If any did, this method checks whether
	 * the error message passed as parameter was foreseen. If it wasn´t, a
	 * warning is signaled.
	 * 
	 */
	public void checkErrorMessages(Env env) {

		for (final CompilationUnit compUnit : compilationUnitList) {

			// Check whether
			// the compiler signaled any errors. It should.
			if ( (compUnit.getLineMessageList() != null
					&& compUnit.getLineMessageList().size() > 0)
					&& (compUnit.getErrorList() != null) ) {
				for (final UnitError unitError : compUnit.getErrorList()) {
					final int line = unitError.getLineNumber();
					Tuple3<Integer, String, Boolean> found = null;
					for (final Tuple3<Integer, String, Boolean> t : compUnit
							.getLineMessageList()) {
						if ( t.f1 == line ) {
							if ( found != null ) {
								env.error(null,
										"More than one metaobject implementing interface 'IInformCompilationError' points that "
												+ "there should be an error in line "
												+ line,
										false, true);
							}
							found = t;
						}
					}
					if ( found == null ) {
						env.error(null, "The compiler points an error at line "
								+ line
								+ " although no metaobject implementing interface 'IInformCompilationError' points an "
								+ "error at this line " + line, false, true);
					}
					else {
						found.f3 = true;
					}
				}
			}
		}
	}

	public void checkErrorMessagesAllCompilationUnits(Env env) {
		for (final CompilationUnit compUnit : compilationUnitList) {
			for (final Tuple3<Integer, String, Boolean> t : compUnit
					.getLineMessageList()) {
				if ( !t.f3 ) {
					env.error(null,
							"A metaobject implementing interface 'IInformCompilationError' points an error at line "
									+ t.f1 + " with message '" + t.f2
									+ "' although this error is not signaled by the compiler",
							false, true);
				}
			}
		}

	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setCyanLangPackage(CyanPackage cyanLangPackage) {
		cyanLangPrototypeNameTable = new HashSet<>();
		for (final CompilationUnit compilationUnit : cyanLangPackage
				.getCompilationUnitList()) {
			final String s = compilationUnit.getFileNameWithoutExtension();
			int i = s.indexOf('(');
			if ( i < 0 ) {
				i = s.length();
			}
			final String name = s.substring(0, i);
			cyanLangPrototypeNameTable.add(name);
		}
	}

	/**
	 * return true if 'name' is a prototype name of package cyan.lang
	 * 
	 * @param name
	 * @return
	 */

	public boolean isInPackageCyanLang(String name) {
		/*
		 * name can be something like "Tuple<main.Person, Int>" or
		 * "cyan.lang.tmp.Tuple<main.Person, Int>"
		 */
		String s = name;
		/*
		 * if name is something like "Proto_Interval<Int>", interfaceName is
		 * "Interval<Int>"
		 */
		if ( NameServer.isPrototypeFromInterface(name) ) {
			final String interfaceName = NameServer
					.interfaceNameFromPrototypeName(name);
			return isInPackageCyanLang(interfaceName);
		}

		final int indexLessThan = name.indexOf('<');
		if ( indexLessThan >= 0 ) {
			// eliminates the parameters to the generic prototype
			// "Tuple<main.Person, Int>" becomes "Tuple" and
			// "cyan.lang.tmp.Tuple<main.Person, Int>" becomes
			// "cyan.lang.tmp.Tuple"
			s = name.substring(0, indexLessThan);
		}
		final int i = s.lastIndexOf('.');
		if ( i >= 0 ) {
			String packageName = s.substring(0, i);
			if ( !packageName.equals("cyan.lang") ) {
				return false;
			}
			s = s.substring(i + 1);
		}
		return cyanLangPrototypeNameTable.contains(s);
	}

	/*
	 * I do not know why this was useful. It was removed, maybe forever public
	 * void setCyanMetaobjectTable() {
	 * 
	 * cyanMetaobjectTable = new HashMap<String, Class<?>>();
	 * 
	 * 
	 * LinkedList<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
	 * classLoadersList.add(ClasspathHelper.contextClassLoader());
	 * classLoadersList.add(ClasspathHelper.staticClassLoader()); Reflections
	 * reflections = new Reflections("meta", new SubTypesScanner(false));
	 * 
	 * Set<Class<? extends CyanMetaobject>> cyanMetaobjectSubclassList =
	 * reflections.getSubTypesOf(CyanMetaobject.class); for ( Class<? extends
	 * CyanMetaobject> cyanMetaobjectSubclass : cyanMetaobjectSubclassList ) {
	 * String s = cyanMetaobjectSubclass.getName(); int i = s.lastIndexOf('.');
	 * 
	 * String name = s; if ( i >= 0 ) name = s.substring(i+1);
	 * cyanMetaobjectTable.put(name, cyanMetaobjectSubclass); }
	 * cyanMetaobjectTable.put("CyanMetaobject", CyanMetaobject.class); //
	 * cyanMetaobjectTable.put("CyanMetaobjectAttachTo",
	 * CyanMetaobjectAttachTo.class); }
	 * 
	 */

	public boolean getInCalcInterfaceTypes() {
		return inCalcInterfaceTypes;
	}

	public void addCyanPackage(CyanPackage aPackage) {
		packageList.add(aPackage);
	}

	public List<CyanPackage> getPackageList() {
		return packageList;
	}

	public void addCompilationUnitToWrite(
			IReceiverCompileTimeMessageSend exprPrototype) {
		if ( receiverToWriteList == null ) {
			receiverToWriteList = new HashSet<>();
		}
		this.receiverToWriteList.add(exprPrototype);
	}

	/**
	 * can only be called after step 5
	 */
	public void writePrototypesToFile(Env env) {
		if ( receiverToWriteList != null ) {
			MyFile.writePrototypesToFile(receiverToWriteList, env);
		}
		receiverToWriteList = null;
	}

	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		return featureList;
	}

	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature) {
		if ( featureList == null ) {
			featureList = new ArrayList<>();
		}
		featureList.add(feature);
	}

	public void addFeatureList(
			List<Tuple2<String, WrExprAnyLiteral>> featureList1) {
		for (final Tuple2<String, WrExprAnyLiteral> t : featureList1) {
			this.addFeature(t);
		}
	}

	public List<WrExprAnyLiteral> searchFeature(String name) {
		if ( featureList == null ) {
			return null;
		}

		List<WrExprAnyLiteral> eList = null;
		for (final Tuple2<String, WrExprAnyLiteral> t : featureList) {
			if ( t.f1.equals(name) ) {
				if ( eList == null ) {
					eList = new ArrayList<>();
				}
				eList.add(t.f2);
			}
		}
		return eList;
	}

	public List<AnnotationAt> getAttachedAnnotationList() {
		return attachedAnnotationList;
	}

	public void setAttachedAnnotationList(
			List<AnnotationAt> attachedAnnotationList) {
		if ( attachedAnnotationList == null ) {
			attachedAnnotationList = new ArrayList<>();
		}
		this.attachedAnnotationList = attachedAnnotationList;
	}

	public List<AnnotationAt> getAttachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes() {
		if ( attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes == null ) {
			attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes = new ArrayList<>();

			for (AnnotationAt withAtAnnot : attachedAnnotationList) {
				CyanMetaobject cyanMetaobject = withAtAnnot.getCyanMetaobject();
				if ( cyanMetaobject instanceof IAction_afterResTypes
						|| cyanMetaobject instanceof IActionNewPrototypes_afterResTypes ) {
					attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes
							.add(withAtAnnot);
				}
			}
		}
		return attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes;
	}

	/**
	 * the annotations of attachedAnnotationList that implement interfaces
	 * IAction_afterResTypes or IActionNewPrototypes_afterResTypes
	 */
	List<AnnotationAt> attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes = null;

	/*
	 * public void addProjectError(String message) { if ( this.projectErrorList
	 * == null ) this.projectErrorList = new ArrayList<>();
	 * this.projectErrorList.add( new ProjectError(message)); }
	 * 
	 * public List<ProjectError> getProjectErrorList() { return
	 * this.projectErrorList; }
	 * 
	 */

	public String getName() {
		return this.project.getProjectName();
	}

	public AttachedDeclarationKind getKind() {
		return AttachedDeclarationKind.PROGRAM_DEC;
	}

	public void setCyanLangDir(String cyanLangDir) {
		this.cyanLangDir = cyanLangDir;
	}

	public String getCyanLangDir() {
		return this.cyanLangDir;
	}

	public void addToListAfter_afterResTypes(CyanMetaobject annotation) {
		if ( this.afterATImetaobjectAnnotationList == null ) {
			this.afterATImetaobjectAnnotationList = new ArrayList<>();
		}
		this.afterATImetaobjectAnnotationList.add(annotation);
	}

	public List<String> getClassPathList() {
		return classPathList;
	}

	public void setClassPathList(List<String> classPathList) {
		this.classPathList = classPathList;
	}

	public boolean trimCompilationUnitList(int numCompilationUnitsProgram,
			HashSet<String> nameSet) {
		if ( numCompilationUnitsProgram >= this.compilationUnitList.size() ) {
			return false;
		}
		int n = this.compilationUnitList.size();
		while (n != numCompilationUnitsProgram) {
			final CompilationUnit cunit = this.compilationUnitList.get(n - 1);
			if ( !cunit.getCyanPackage().removeCompilationUnit(cunit) ) {
				return false;
			}
			nameSet.remove(cunit.getFullFileNamePath());
			this.compilationUnitList.remove(n - 1);
			--n;
		}
		return true;
	}

	/**
	 * list of errors that are outside any compilation unit
	 * 
	 * private List<ProjectError> projectErrorList;
	 */

	/**
	 * the list of features associated to this package
	 */
	private List<Tuple2<String, WrExprAnyLiteral>>		featureList;

	/**
	 * set of all classes that inherit from CyanMetaobject that were compiled
	 * with the compiler
	 */
	HashMap<String, Class<?>>							cyanMetaobjectTable;

	/**
	 * table of names of all prototypes of package cyan.lang
	 */
	private Set<String>									cyanLangPrototypeNameTable;

	/**
	 * a list of all compilation units of this program
	 */
	private final List<CompilationUnit>					compilationUnitList;

	/**
	 * the project of the program. It has links to all packages of the program,
	 * including cyan.lang.
	 */
	private Project										project;
	/**
	 * a list of packages of the program
	 */
	private final List<CyanPackage>						packageList;

	/**
	 * a list of JVM package of the program
	 */
	private final List<JVMPackage>						jvmPackageList;

	private final Map<String, JVMPackage>				jvmPackageMap;

	/*
	 * true if the method calcInterfaceTypes is executing
	 */
	private boolean										inCalcInterfaceTypes;

	/**
	 * A compile-time message passing to a prototype can be made with {@code .#}
	 * such as in <br>
	 * <code>
	 * Function<String>.#writeCode
	 * </code> this demand that the source code of {@code Function<String>} be
	 * written in the directory of the project. The set below keeps information
	 * on which prototypes should be written. Of course, this only makes sense
	 * because metaobjects and the Cyan compiler adds code to prototypes.
	 */

	private HashSet<IReceiverCompileTimeMessageSend>	receiverToWriteList;

	public void setJavaLibDir(String javaLibDir) {
		this.javaLibDir = javaLibDir;
	}

	public List<String> getSourcePathList() {
		return sourcePathList;
	}

	public void setSourcePathList(List<String> sourcePathList) {
		this.sourcePathList = sourcePathList;
	}

	public List<JVMPackage> getJvmPackageList() {
		return jvmPackageList;
	}

	public void addJvmPackageList(JVMPackage jvmPackage) {
		this.jvmPackageList.add(jvmPackage);
	}

	public JVMPackage searchJVMPackage(String packageName) {
		return this.jvmPackageMap.get(packageName);
	}

	public TypeJavaRef searchJavaBasicType(String name) {
		if ( java_lang_Package == null ) {
			java_lang_Package = this.jvmPackageMap.get("java.lang");
		}
		if ( java_lang_Package.searchJVMClass(name) == null ) {
			final TypeJavaClass javaClass = new TypeJavaClass(name,
					java_lang_Package);
			switch (name) {
			case "boolean":
				javaClass.setaClass(boolean.class);
				java_lang_Package.getJvmTypeClassMap().put("boolean",
						javaClass);
				break;
			case "byte":
				javaClass.setaClass(byte.class);
				java_lang_Package.getJvmTypeClassMap().put("byte", javaClass);
				break;
			case "short":
				javaClass.setaClass(short.class);
				java_lang_Package.getJvmTypeClassMap().put("short", javaClass);
				break;
			case "int":
				javaClass.setaClass(int.class);
				java_lang_Package.getJvmTypeClassMap().put("int", javaClass);
				break;
			case "long":
				javaClass.setaClass(long.class);
				java_lang_Package.getJvmTypeClassMap().put("long", javaClass);
				break;
			case "float":
				javaClass.setaClass(float.class);
				java_lang_Package.getJvmTypeClassMap().put("float", javaClass);
				break;
			case "double":
				javaClass.setaClass(double.class);
				java_lang_Package.getJvmTypeClassMap().put("double", javaClass);
				break;
			case "String":
				javaClass.setaClass(String.class);
				java_lang_Package.getJvmTypeClassMap().put("String", javaClass);
				break;
			}
			return javaClass;
		}
		return java_lang_Package.searchJVMClass(name);
	}

	/**
	 * load the Java packages of this project. Return a list of error messages
	 * 
	 * @return
	 */

	public List<String> loadJavaPackages(List<String> class_Path_List) {

		final List<String> errorMessageList = new ArrayList<>();
		for (final String classPath : class_Path_List) {
			for (final String pathToJar : classPath.split(";")) {
				try {
					final File fpath = new File(pathToJar);
					loadPackagesDirectoryOrJarFile(errorMessageList, fpath);
				}
				catch (final ClassNotFoundException e) {
					StringBuilder msg = new StringBuilder(
							"A class of the file '").append(pathToJar)
									.append("' was not found");
					if ( e.getMessage() != null ) {
						msg.append(". The detailed message is '")
								.append(e.getMessage()).append("'");
					}
					errorMessageList.add(msg.toString());
				}
				catch (final IOException e) {
					StringBuilder s = new StringBuilder();
					for (String cp : class_Path_List) {
						s.append(cp).append("\n");
					}
					errorMessageList.add("Error in reading file '" + pathToJar
							+ "'. The error message is \n    " + e.getMessage()
							+ "\nThe list of class paths to be added is \n"
							+ s.toString());
				}
			}
		}
		return errorMessageList;

	}

	/*
	 * public String loadSingleJarFile(String pathToJar) throws IOException,
	 * ClassNotFoundException { JarFile jarFile = null; try { jarFile = new
	 * JarFile(pathToJar); boolean isRT = pathToJar.endsWith("lib" +
	 * NameServer.fileSeparatorAsString + "rt.jar");
	 * //System.out.println("jar size = " + jarFile.size());
	 * Enumeration<JarEntry> e = jarFile.entries();
	 * 
	 * URL[] urls = { new URL("jar:file:" + pathToJar+"!/") }; URLClassLoader cl
	 * = URLClassLoader.newInstance(urls); while ( e.hasMoreElements() ) {
	 * JarEntry je = e.nextElement(); if ( je.isDirectory() ||
	 * !je.getName().endsWith(".class") ) { continue; }
	 * 
	 * 
	 * // -6 because of .class String className =
	 * je.getName().substring(0,je.getName().length()-6); className =
	 * className.replace('/', '.'); if ( ! className.startsWith("java.") && !
	 * className.startsWith("javax.") ) { continue; } Class<?> aClass =
	 * cl.loadClass(className); TypeJavaClass javaClass = new
	 * TypeJavaClass(aClass); this.jvmTypeJavaList.put(aClass.getName(),
	 * javaClass); JVMPackage jvmPackage =
	 * this.searchJVMPackage(aClass.getPackage().getName()); if ( jvmPackage ==
	 * null ) { jvmPackage = new JVMPackage(aClass.getPackage().getName(),
	 * aClass.getPackage()); this.jvmPackageList.add(jvmPackage); } else { if (
	 * jvmPackage.searchJVMClass(aClass.getName()) != null ) { jarFile.close();
	 * cl.close(); return "There are two classes with name '" + aClass.getName()
	 * + "' in package '" + jvmPackage.getPackageName() + "'"; } }
	 * jvmPackage.addJVMClass(javaClass); } cl.close();
	 * 
	 * } finally { if ( jarFile != null ) jarFile.close(); } return null; }
	 */

	/**
	 * load all classes of the Java package jvmPackage
	 * 
	 * @param jvmPackage
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public String loadClassesFromJavaPackage(JVMPackage jvmPackage)
			throws IOException, ClassNotFoundException {
		JarFile jarFile = null;
		try {
			final String pathToJar = jvmPackage.getPathToJar();
			jarFile = new JarFile(pathToJar);
			final URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
			final URLClassLoader cl = URLClassLoader.newInstance(urls);

			final String packagePath = jvmPackage.getPackageName() + '.';
			for (final Map.Entry<String, TypeJavaRef> elem : jvmPackage
					.getJvmTypeClassMap().entrySet()) {
				final String aClassName = packagePath + elem.getKey();
				final Class<?> aClass = cl.loadClass(aClassName);
				elem.getValue().setaClass(aClass);
			}
			cl.close();

		}
		finally {
			if ( jarFile != null ) {
				jarFile.close();
			}
		}
		return null;
	}

	/**
	 * jarPath is the path of a Jar file. This method creates an object of
	 * {@link ast#JVMPackage} for each package of this jar file. It is added to
	 * field jvmPackageList. The classes of the package are not loaded.
	 * 
	 * @param jarPath
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public String loadPackagesInJarFile(String jarPath,
			Map<String, JVMPackage> jvm_Package_Map,
			List<JVMPackage> jvm_Package_List)
			throws IOException, ClassNotFoundException {
		try (JarFile jarFile = new JarFile(jarPath)) {
			// System.out.println("jar size = " + jarFile.size());
			final Enumeration<JarEntry> e = jarFile.entries();

			final URL[] urls = { new URL("jar:file:" + jarPath + "!/") };
			final URLClassLoader cl = URLClassLoader.newInstance(urls);
			while (e.hasMoreElements()) {
				final JarEntry je = e.nextElement();
				final String entryName = je.getName();
				if ( !je.isDirectory() ) {
					final int lastSlash = entryName.lastIndexOf('/');
					if ( lastSlash > 0 && entryName.endsWith(".class") ) {
						final String packageNameSlash = entryName
								.substring(0, lastSlash).replace('/', '.');
						if ( !packageNameSlash.startsWith("META-INF")
								&& !packageNameSlash.contains(".internal.") ) {
							JVMPackage jvmPackage = jvm_Package_Map
									.get(packageNameSlash);
							if ( jvmPackage == null ) {
								jvmPackage = new JVMPackage(jarPath,
										packageNameSlash);
								jvm_Package_List.add(jvmPackage);
								jvm_Package_Map.put(packageNameSlash,
										jvmPackage);
								jvmPackage.setUrls(urls);
							}
							final String className = entryName.substring(
									lastSlash + 1, entryName.length() - 6);
							final TypeJavaClass javaClass = new TypeJavaClass(
									className, jvmPackage);
							jvmPackage.put(className, javaClass);
						}

					}
				}
			}
			cl.close();
		}
		return null;
	}

	/**
	 * @param errorMessageList
	 * @param f
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void loadPackagesDirectoryOrJarFile(List<String> errorMessageList,
			File f) throws IOException, ClassNotFoundException {

		if ( f.isDirectory() ) {
			for (final File inner : f.listFiles()) {
				loadPackagesDirectoryOrJarFile(errorMessageList, inner);
			}

		}
		else if ( f.getName().endsWith(".jar") ) {
			final String msg = loadPackagesInJarFile(f.getCanonicalPath(),
					this.jvmPackageMap, this.jvmPackageList);
			if ( msg != null ) {
				errorMessageList.add(msg);
			}
		}
	}

	public void setProgramKeyValue(String variableName, Object value) {
		programKeyValueMap.put(variableName, value);
	}

	public Object getProgramValueFromKey(String key) {
		return programKeyValueMap.get(key);
	}

	public void addProgramKeyValueSet(String key, String value) {
		HashSet<String> set = programKeyValueSet.get(key);
		if ( set == null ) {
			set = new HashSet<>();
		}
		set.add(value);
		programKeyValueSet.put(key, set);
	}

	public Set<String> getProgramKeyValueSet(String variableName) {
		return programKeyValueSet.get(variableName);
	}

	public void addDocumentText(String doc, String docKind) {
		if ( documentTextList == null ) {
			documentTextList = new ArrayList<>();
		}
		documentTextList.add(new Tuple2<>(doc, docKind));
	}

	public void addDocumentExample(String example, String exampleKind) {
		if ( exampleTextList == null ) {
			exampleTextList = new ArrayList<>();
		}
		exampleTextList.add(new Tuple2<>(example, exampleKind));

	}

	public List<Tuple2<String, String>> getDocumentTextList() {
		return documentTextList;
	}

	public List<Tuple2<String, String>> getDocumentExampleList() {
		return exampleTextList;
	}

	/**
	 * return true if feature 'featureName' is defined in the program and at
	 * least one of its value is true. Return false otherwise
	 */
	public boolean isFeatureTrue(String featureName) {
		List<WrExprAnyLiteral> awe = this.searchFeature(featureName);
		if ( awe != null && awe.size() > 0 ) {
			for (WrExprAnyLiteral we : awe) {
				if ( we instanceof WrExprLiteralBoolean ) {
					WrExprLiteralBoolean wls = (WrExprLiteralBoolean) we;
					if ( (Boolean) wls.getJavaValue() ) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void loadCompiledPackages() {
		CyanPackage cyanLangPackage = project.getCyanLangPackage();
		if ( isFeatureTrue(NameServer.compilePackageCyanLang) ) {
			cyanLangPackage.setCreateInterfaceFileForSeparateCompilation(true);
			cyanLangPackage.setUseCompiledPrototypes(false);
		}
		else {
			// no annotation
			// @feature("compilePackageCyanLang", true)
			// for program in the project file
			// verify if there is a file
			// NameServer.fileName_of_interfacesCompiledPrototypes
			loadCompiledPrototypes(cyanLangPackage);
			cyanLangPackage.setCreateInterfaceFileForSeparateCompilation(false);
			cyanLangPackage.setUseCompiledPrototypes(true);
		}
		for (CyanPackage cyanPackage : this.packageList) {
			if ( cyanPackage == cyanLangPackage ) {
				continue;
			}

			List<WrExprAnyLiteral> awe = cyanPackage.searchFeature("annot");
			boolean found_compilePackage_option = false;
			if ( awe != null && awe.size() > 0 ) {
				for (WrExprAnyLiteral we : awe) {
					if ( we instanceof WrExprLiteralString ) {
						WrExprLiteralString wls = (WrExprLiteralString) we;
						Object value = MetaHelper
								.removeQuotes((String) wls.getJavaValue());

						if ( value.equals("compilePackage") ) {
							/*
							 * something like
							 *
							 *
							 * program
							 *
							 * @annot("compilePackage") package cyan.io
							 * 
							 * in the project file
							 */
							cyanPackage
									.setCreateInterfaceFileForSeparateCompilation(
											true);
							found_compilePackage_option = true;
						}
						else if ( value.equals("useCompiled") ) {
							/*
							 * something like
							 *
							 *
							 * program
							 *
							 * @annot("useCompiled") package cyan.io
							 * 
							 * in the project file
							 */
							cyanPackage.setUseCompiledPrototypes(true);
						}
					}
				}
			}
			if ( !cyanPackage.getUseCompiledPrototypes()
					&& cyanPackage.isThereJarFileForPackage()
					&& !found_compilePackage_option ) {
				cyanPackage.setUseCompiledPrototypes(true);
			}
			if ( cyanPackage.getUseCompiledPrototypes() ) {
				this.loadCompiledPrototypes(cyanPackage);
			}
		}

	}

	public boolean loadCompiledPrototypes(CyanPackage cyanPackage) {
		String path = cyanPackage.getPackageCanonicalPath();
		String canonicalPath = path;
		if ( path.charAt(path.length() - 1) != NameServer.fileSeparator ) {
			path += NameServer.fileSeparatorAsString;
		}
		path += NameServer.fileName_of_interfacesCompiledPrototypes;
		File f = new File(path);
		if ( !f.exists() || f.isDirectory() ) {
			return true;
		}
		// load file with the compiled prototype interfaces
		final MyFile myFile = new MyFile(path);
		final char[] input = myFile.readFile();
		if ( input == null ) {
			this.project.error(
					"Error when reading the compiled interfaces of package '"
							+ cyanPackage.getName()
							+ "'. The file that caused the error is '" + path
							+ "'");
			return false;
		}

		final CompilationUnit interfaceSourceCompilationUnit = new CompilationUnit(
				NameServer.fileName_of_interfacesCompiledPrototypes,
				canonicalPath, null, cyanPackage);
		interfaceSourceCompilationUnit.setText(input);
		interfaceSourceCompilationUnit.setOriginalText(input);
		interfaceSourceCompilationUnit.setProgram(this);

		final HashSet<CompilationInstruction> compInstSet = new HashSet<>();
		compInstSet.add(CompilationInstruction.pyanSourceCode);
		final Compiler pc = new Compiler(interfaceSourceCompilationUnit,
				compInstSet, CompilationStep.step_1, this.project, null);
		pc.setCheckSpaceAfterComma(false);
		pc.setParsingPackageInterfaces(true);
		// if (cyanPackage.getName().contains("reflect") ) {
		// System.out.println("reflect text: " + new String(input));
		// }
		pc.parse();
		interfaceSourceCompilationUnit.setParsed(true);
		// cyanPackage.getCompilationUnitList().clear();
		List<CompilationUnit> newCompUnitList = new ArrayList<>();
		for (CompilationUnit cUnit : cyanPackage.getCompilationUnitList()) {
			if ( cUnit.containsGenericPrototype() ) {
				newCompUnitList.add(cUnit);
				// System.out.println(cUnit.getFilename());
			}
		}
		cyanPackage.setCompilationUnitList(newCompUnitList);
		ExprIdentStar packageIdent = interfaceSourceCompilationUnit
				.getPackageIdent();
		for (Prototype pu : interfaceSourceCompilationUnit.getPrototypeList()) {
			CompilationUnit cUnit = new CompilationUnit(pu.getName(), path,
					null, cyanPackage);
			// if ( cyanPackage.getName().contains("reflect")) {
			// System.out.println("reflect::" + pu.getName());
			// }
			cUnit.setText(input);
			cUnit.addPrototype(pu);
			pu.setCompilationUnit(cUnit);
			cUnit.setAlreadPreviouslyCompiled(true);
			cUnit.setPackageIdent(packageIdent);
			cyanPackage.addCompilationUnit(cUnit);
			cUnit.setInterfaceSourceCompilationUnit(
					interfaceSourceCompilationUnit);
		}
		return true;
	}

	@Override
	public WrProgram getI() {
		if ( iProgram == null ) {
			iProgram = new WrProgram(this);
		}
		return iProgram;
	}

	public WrProgram getI_dpp() {
		if ( iProgram_dpp == null ) {
			iProgram_dpp = new WrProgram_dpp(this);
		}
		return iProgram_dpp;
	}

	public void addInstantiatedPrototypeName_to_WhereInfo(
			String fullInstantiatedPrototypeName, String packageName,
			String prototypeName, int lineNumber, int columnNumber) {
		// Map<String, Tuple4<String, String, Integer, Integer>>
		// mapOriginProtoInstantiation;
		if ( mapOriginProtoInstantiation == null ) {
			mapOriginProtoInstantiation = new HashMap<>();
		}
		mapOriginProtoInstantiation.put(fullInstantiatedPrototypeName,
				new Tuple4<>(packageName, prototypeName, lineNumber,
						columnNumber));
	}

	public Tuple4<String, String, Integer, Integer> searchAnnotationCreatedPrototype(
			String fullInstantiatedPrototypeName) {
		if ( mapOriginProtoInstantiation == null ) {
			return null;
		}
		else {
			return mapOriginProtoInstantiation
					.get(fullInstantiatedPrototypeName);
		}
	}

	private WrProgram												iProgram							= null;
	private WrProgram_dpp											iProgram_dpp						= null;

	/**
	 * list of pairs (doc, docKind) of documentation for this declaration. See
	 * interface {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>>							documentTextList;
	/**
	 * list of pairs (example, exampleKind) of examples for this declaration.
	 * See interface {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>>							exampleTextList;

	/**
	 * list of pairs (variableName, set). To variableName is associated the
	 * values of the set
	 */
	private final HashMap<String, HashSet<String>>					programKeyValueSet;

	/**
	 * map of pairs (variableName, value) associated to the program
	 */
	private final HashMap<String, Object>							programKeyValueMap;

	/**
	 * the directory of the Cyan runtime libraries
	 */
	private String													javaLibDir;

	/**
	 * directory of the package cyan.lang
	 */
	private String													cyanLangDir;
	/**
	 * contains the call to the Java compiler
	 */
	private String													javac;
	/**
	 * contains the command array that is argument to the Java compiler. The
	 * same as this.javac but separated in strings
	 */
	private ArrayList<String>										cmdArray;
	/**
	 * contains the call to the compiled Java code
	 */
	private String													execCode;
	/**
	 * contains the array of arguments to call the Java interpreter
	 *
	 */
	private List<String>											execCodeList;
	/**
	 * contains the list that comes after option -cp of the calling to
	 * 'java.exe'
	 */
	private List<String>											cpPathList;

	private String													javaForProjectPathLessSlash;

	private String													mainJavaClassWithoutExtensionName;

	private List<AnnotationAt>										attachedAnnotationList;

	private List<CyanMetaobject>									afterATImetaobjectAnnotationList	= null;

	/**
	 * list of class path to be passed to the Java interpreter
	 */
	private List<String>											classPathList;
	/**
	 * list of source paths to be passed to the Java compiler
	 */
	private List<String>											sourcePathList;

	private JVMPackage												java_lang_Package					= null;

	/**
	 * 
	 * A map from a generic prototype instantiation name (package + "." + name)
	 * to a tuple containing: a) the package and prototype name of the
	 * instantiation b) the line and column of the instantiation
	 */
	private Map<String, Tuple4<String, String, Integer, Integer>>	mapOriginProtoInstantiation;

	/**
	 * true if this program should be compiled to be later used by program
	 * addType that adds types to otherwise Dyn parameters and local variables
	 */
	boolean															addTypeInfo;
	/**
	 * if addTypeInfo is true, the type info should be put in the path
	 * typeInfoPath unless it is null
	 */
	String															typeInfoPath;

	/**
	 * if addTypeInfo is true, this is the file name of the file to which type
	 * information will be added at runtime of the program execution
	 */
	String															fileNameToAddTypeInfo;

}
