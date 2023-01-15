package ast;


import java.lang.reflect.TypeVariable;
import java.util.List;
import error.ErrorKind;
import lexer.Symbol;
import meta.CompilationInstruction;
import meta.MetaHelper;
import meta.WrExprGenericPrototypeInstantiation;
import saci.CompilerManager;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
import saci.TupleTwo;
/**
 * represents a generic prototype instantiation as "{@code Stack<Int>}" and "{@code Queue<Process>}" which may appear
 * inside an expression or as a type. That is, ExprGenericPrototypeInstantiation
 * represents an instantiation of a generic prototype.
 *
 * @author José
 *
 */

public class ExprGenericPrototypeInstantiation extends Expr implements IReceiverCompileTimeMessageSend {

	public ExprGenericPrototypeInstantiation( ExprIdentStar typeIdent,
			List<List<Expr>> realTypeListList,
			Prototype prototype,
			MessageSendToAnnotation messageSendToAnnotation,
			MethodDec method) {
		super(method);
		this.typeIdent = typeIdent;
		this.realTypeListList = realTypeListList;
		this.prototype = prototype;
		this.messageSendToAnnotation = messageSendToAnnotation;
		compilationUnit = null;
		nameWithPackageAndType = null;
		annotationToTypeList = null;
	}

	@Override
	public WrExprGenericPrototypeInstantiation getI() {
		return new WrExprGenericPrototypeInstantiation(this);
	}


	@Override
	public void accept(ASTVisitor visitor) {
		for ( List<Expr> exprList : this.realTypeListList ) {
			for ( Expr expr : exprList ) {
				expr.accept(visitor);
			}
		}
		visitor.visit(this);
	}

	@Override
	public Object eval(EvalEnv ee) {
		return null;
	}


	@Override
	public boolean mayBeStatement() {
		return false;
	}

	public void setRealTypeListList(List<List<Expr>> realTypeListList) {
		this.realTypeListList = realTypeListList;
	}

	public List<List<Expr>> getRealTypeListList() {
		return realTypeListList;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		typeIdent.genCyan(pw, false, cyanEnv, genFunctions);
		int n;
		for ( List<Expr> arrayOfList : realTypeListList ) {
			pw.print("<");
			n = arrayOfList.size();
			for ( Expr t : arrayOfList ) {
				t.genCyan(pw, false, cyanEnv, genFunctions);
				--n;
				if ( n > 0 )
					pw.print(", ");
			}
			pw.print(">");
		}
		if ( messageSendToAnnotation != null ) {
			messageSendToAnnotation.genCyan(pw);
		}
	}

	/**
	 * return the unique name associated to this Generic object.
	 * Example:
	 *     Array<Int>   results in
	 *          Array_left_gp_Int_right
	 *     Array<Int, Person> results in
	 *          Array_left_gp_int_gp_Person_right
	 *     Function<Int, String><Boolean, Int> results in
	 *         Function_left_gp_Int_gp_CyString_right_left_gp_Boolean_gp_Int_right
	 */
	@Override
	public String getJavaName() {
		return javaName;
	}


	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String javaType = getJavaName();
		if ( type instanceof InterfaceDec ) {
			javaType = MetaHelper.getJavaName(NameServer.prototypeFileNameFromInterfaceFileName(type.getName()));
		}

		String tmpVar = NameServer.nextJavaLocalVariableName();
		pw.printIdent(javaType + " " + tmpVar + " = " + javaType + ".prototype");
		// genJavaExprWithoutTmpVar(pw, env);
		pw.println(";");
		return tmpVar;

	}

	/*
	public void genJavaExprWithoutTmpVar(PWInterface pw, Env env) {
		pw.print(this.getJavaName() + ".prototype");
	}
	*/


	public void setTypeIdent(ExprIdentStar typeIdent) {
		this.typeIdent = typeIdent;
	}

	public ExprIdentStar getTypeIdent() {
		return typeIdent;
	}

	@Override
	public Symbol getFirstSymbol() {
		return typeIdent.getFirstSymbol();
	}


	public void setPrototype(Prototype prototype) {
		this.prototype = prototype;
	}


	public Prototype getPrototype() {
		return prototype;
	}


	/**
	 * returns something like {@code "util.Stack<Int>"} or {@code "util.Stack<Function<Int,Float><String>>"}
	 * or {@code "util.Stack<people.Person>"}
	 * Note that there is no space after ","
	 * If the parameters are prototypes that do not belong to the package cyan.lang,
	 * their names are prefixed by their packages. <br>
	 * This method should not be called in the semantic analysis
	 */
	@Override
	public String getName() {
		String name = this.typeIdent.getName();
		for ( List<Expr> typeList : realTypeListList ) {
			name = name + "<";
			int size = typeList.size();
			for ( Expr e : typeList ) {
				if ( e == null )
					throw new RuntimeException("Internal error in ExprGenericPrototypeInstantiation");

				String s = e.ifPrototypeReturnsItsName();
				if ( s == null )
					throw new RuntimeException("Internal error in ExprGenericPrototypeInstantiation");
				name = name + s;
				if ( --size > 0 )
					name = name + ",";
			}
			name = name + ">";

		}
		return name;
	}


	public void clearNameWithPackageAndType() {
		this.nameWithPackageAndType = null;
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
			for ( AnnotationAt annotType : annotationToTypeList ) {
				ret += annotType.asString();
			}
		}
		return ret;
	}


	@Override
	public TupleTwo<String, Type> ifPrototypeReturnsNameWithPackageAndType(Env env) {

		if ( nameWithPackageAndType == null ) {
			String name = this.typeIdent.getName();
			String packageName;
			String prototypeName;

			String originalName = name.replace(MetaHelper.cyanLanguagePackageNameDot,  "");

			for ( List<Expr> typeList : realTypeListList ) {
				name = name + "<";
				int size = typeList.size();
				for ( Expr e : typeList ) {
					TupleTwo<String, Type> t = e.ifPrototypeReturnsNameWithPackageAndType(env);
					if ( t == null )
						return null;
					String s = t.f1;
					name = name + s;
					if ( --size > 0 )
						name = name + ",";
				}
				name = name + ">";

			}
			// name for searches
			//String protoNameNoPrecedingPackageWithArgsPackage = name;
			name = name.replace(NameServer.cyanLanguagePackageNameDot, "");
//			int i = name.indexOf(MetaHelper.cyanLanguagePackageName);
//
//			int sizeCyanPackageName = MetaHelper.cyanLanguagePackageName.length();
//			while ( i >= 0 ) {
//				if ( i > 0 ) {
//					char ch = name.charAt(i-1);
//					if ( ch != '.' )
//						name = name.substring(0, i) + name.substring(i + sizeCyanPackageName);
//				}
//				else if ( i == 0 ) {
//					name = name.substring(i + sizeCyanPackageName+1);
//				}
//				i = name.indexOf(MetaHelper.cyanLanguagePackageName);
//			}


			// name = name.replace(NameServer.cyanLanguagePackageName + ".", "");
			  // the name of the generic prototype, that that (this is not an error) comes before '<'
			// String originalName = name.substring(0, name.indexOf('<'));
			int indexOfDot = originalName.lastIndexOf('.');


			if ( indexOfDot < 0 ) {
				  // no package preceding the name. It should be a prototype visible in
				  // this compilation unit
				Prototype pu3 = env.searchVisiblePrototype(name, this.getFirstSymbol(), true);
				if ( pu3 == null ) {
					TypeJavaRef javaClass = env.searchVisibleJavaClass(name);
					if ( javaClass == null ) {
						return null;
					}
					else {
						nameWithPackageAndType = new saci.TupleTwo<String, Type>(name, javaClass);
					}
				}
				else {
					compilationUnit = pu3.getCompilationUnit();
					if ( pu3.getOuterObject() != null ) {
						  // currently no inner prototype should be generic. So this should never happens.
						  // an inner prototype "Fun_7__" should have only the name "Fun_7__", without any package
						nameWithPackageAndType = new saci.TupleTwo<String, Type>(name, pu3);
					}
					else {
						packageName = compilationUnit.getPackageName();
						if ( packageName.equals(MetaHelper.cyanLanguagePackageName) )
							nameWithPackageAndType = new saci.TupleTwo<String, Type>(name, pu3);
						else
							nameWithPackageAndType = new saci.TupleTwo<String, Type>(packageName + "." + name, pu3);
					}
				}
			}
			else {
				// package name
				prototypeName = name.substring(indexOfDot + 1);
				packageName = name.substring(0, indexOfDot);
				Prototype pu4 = env.searchPackagePrototype(packageName, prototypeName);
				if ( pu4 == null ) {
					prototypeName = prototypeName.substring(0, prototypeName.indexOf('<'));
					TypeJavaRef javaClass = env.searchPackageJavaClass(packageName, prototypeName);
					if ( javaClass != null ) {
						nameWithPackageAndType = new saci.TupleTwo<String, Type>(packageName + "." + prototypeName, javaClass);
					}
					else {
						nameWithPackageAndType = null;
					}
				}
				else {
					compilationUnit = pu4.getCompilationUnit();
					nameWithPackageAndType = new saci.TupleTwo<String, Type>(packageName + "." + prototypeName, pu4);
				}
			}

		}
		return this.nameWithPackageAndType;
	}


	/**
	 * return the prototype name and compilation unit of this generic prototype instantiation.
	 * This method should be called after calculation the interface types and before
	 * semantic analysis. It may be called outside the semantic analysis of a program unit.
	   @param env
	   @return
	 */

	@Override
	public saci.TupleTwo<String, CompilationUnit> returnsNameWithPackage(Env env) {
		String name = this.typeIdent.getName();
		   // if indexOfDot >= 0 the name of the generic prototype is preceded by a package
		String packageName;
		String prototypeName;

		for ( List<Expr> typeList : realTypeListList ) {
			name = name + "<";
			int size = typeList.size();
			for ( Expr e : typeList ) {
				String s;
				if ( e instanceof ast.ExprGenericPrototypeInstantiation ) {
					TupleTwo<String, CompilationUnit> t = ((ExprGenericPrototypeInstantiation ) e).returnsNameWithPackage(env);
					if ( t == null )
						return null;
					s = t.f1;
				}
				else if ( e instanceof ExprIdentStar )
					s = ((ExprIdentStar ) e).getName();
				else
					return null;

				name = name + s;
				if ( --size > 0 )
					name = name + ",";
			}
			name = name + ">";

		}


		name = name.replace(MetaHelper.cyanLanguagePackageName + ".", "");
		  // the name of the generic prototype, that that (this is not an error) comes before '<'
		String originalName = name.substring(0, name.indexOf('<'));
		int indexOfDot = originalName.lastIndexOf('.');


		if ( indexOfDot < 0 ) {
			  // no package preceding the name. It should be a prototype of package cyan.lang


			Prototype pu3 = env.getProject().getCyanLangPackage().searchPublicNonGenericPrototype(name); // env.searchVisiblePrototype(name, this.getFirstSymbol(), true);
			if ( pu3 == null ) {
				if ( name.equals(MetaHelper.dynName) )
					return new saci.TupleTwo<String, CompilationUnit>(name, null);
				else
					return null;
			}
			else {
				CompilationUnit compUnit3 = pu3.getCompilationUnit();
				packageName = compUnit3.getPackageName();
				compilationUnit = compUnit3;
				   // do not put cyan.lang in the return value
				if ( packageName.equals(MetaHelper.cyanLanguagePackageName) )
					return new saci.TupleTwo<String, CompilationUnit>(name, compUnit3);
				else
					return new saci.TupleTwo<String, CompilationUnit>(packageName + "." + name, compUnit3);
			}
		}
		else {
			// package name
			prototypeName = name.substring(indexOfDot + 1);
			packageName = name.substring(0, indexOfDot);
			Prototype pu4 = env.searchPackagePrototype(packageName, prototypeName);
			if ( pu4 == null ) {
				return null;
			}
			else {
				compilationUnit = pu4.getCompilationUnit();
				return new saci.TupleTwo<String, CompilationUnit>(packageName + "." + prototypeName, compilationUnit);
			}
		}
	}




	/** Suppose this object represents instantiation "Stack<Int>".
	 * The generic prototype to be used can be in a file "Stack(1).cyan",
	 * "Stack(Int).cyan", or "Stack(1+).cyan". This method returns "Stack(1)".
	 *
	 */
	public String getGenericSourceFileName() {
		String name = this.typeIdent.getLastName();
		for ( List<Expr> typeList : realTypeListList ) {
			name = name + "(" + typeList.size() + ")";
		}
		return name;
	}

	/** Suppose this object represents instantiation "Stack<Int>".
	 * The generic prototype to be used can be in a file "Stack(1).cyan",
	 * "Stack(Int).cyan", or "Stack(1+).cyan". This method returns "Stack(1+)".
	 *
	 */
	public String getGenericSourceFileNameWithVaryingNumberOfParameters() {
		String name = this.typeIdent.getLastName();


		return name + "(1+)";
	}



	private String specificSourceFileName = null;
	/** Suppose this object represents instantiation "{@code Stack<Int>}".
	 * The generic prototype to be used can be in a file "{@code Stack(1).cyan}"
	 * or "{@code Stack(Int).cyan}". This method returns "{@code Stack(Int)}".
	 * If the parameter represents the instantiation <br>
	 * {@code  One<G1<String>>} <br>
	 * with {@code G1} and {@code One} of package {@code generic02.ga}, this method
	 * returns <br>
	 * {@code One<generic02.ga.G1<String>>}<br>
	 * This method assumes that the expressions that are parameters to this
	 * generic prototype instantiation ({@code G1<String>} in the example) have
	 * already been typed. That is, method calcInternalTypes of them have already
	 * been called.
	 */
	public String getSpecificSourceFileName(Env env) {

		if ( specificSourceFileName != null )
			return specificSourceFileName;
		Program program = env.getProject().getProgram();

		String name = this.typeIdent.getLastName();
		for ( List<Expr> typeList : realTypeListList ) {
			name = name + "(";
			int size = typeList.size();
			if ( size > 0 ) {
				for ( Expr e : typeList ) {
					String packageName = "";
					if ( e instanceof ExprIdentStar ) {
						String realParameterName = ((ExprIdentStar ) e).getName();

						boolean precededByPackage = realParameterName.indexOf('.') > 0;

						if ( Character.isUpperCase(realParameterName.charAt(0)) ||
							 precededByPackage ) {
							/**
							 * only upper case letter parameters are considered prototypes. Lower case
							 * parameters are considered symbols as in
							 *     Tuple<f1, Int, f2, Char>
							 * except when there is a '.' in the parameter starting with a lower case
							 * letter. In this case, it is considered a prototype preceded by a
							 * package name.
							 */
							/*
							 * if realParameterName already has the package, as in "util.Person", do not look
							 * for the package name. realParameterName is prefixed by a path if there is a '.'
							 * in it
							 */
							boolean isInPackageCyanLang = program.isInPackageCyanLang(realParameterName);
							if ( isInPackageCyanLang || realParameterName.equals(MetaHelper.dynName) ) {
								/*
								 * if the parameter is like "cyan.lang.Int" or "cyan.lang.Function<Nil>",
								 * remove "cyan.lang" resulting in "Int" and "Function<Nil>".
								 */
								int indexOfDot = realParameterName.lastIndexOf('.');
								if ( indexOfDot > 0 )
									realParameterName = realParameterName.substring(indexOfDot + 1);
							}
							else if ( ! precededByPackage ) {

								Prototype pu = env.searchVisiblePrototype(realParameterName, e.getFirstSymbol(), false);
								if ( pu != null  ) {
									CompilationUnit cunit = pu.getCompilationUnit();
									packageName = cunit.getPackageName() + ".";
								}
								else {
									TypeJavaRef javaClass = env.searchVisibleJavaClass(realParameterName);
									if ( javaClass == null ) {
										env.error(true, e.getFirstSymbol(), "Prototype '" + realParameterName + "' was not found",
												realParameterName, ErrorKind.prototype_was_not_found_inside_method);
										return null;
									}
									packageName = javaClass.getJavaPackage() + ".";
								}

							}
							else {
								// parameter is a prototype that is not in cyan.lang and it is preceded
								// by a package
								int indexOfDot = realParameterName.lastIndexOf('.');
								String prototypeName;
								prototypeName = realParameterName.substring(indexOfDot + 1);
								packageName = realParameterName.substring(0, indexOfDot);
								realParameterName = prototypeName;
								if ( env.searchPackagePrototype(packageName, prototypeName) == null ) {

									TypeJavaRef javaClass = env.searchPackageJavaClass(packageName, prototypeName);
									if ( javaClass == null ) {
										env.error(true, e.getFirstSymbol(), "Prototype '" + realParameterName + "' was not found",
												realParameterName, ErrorKind.prototype_was_not_found_inside_method);
										return null;
									}
								}
								packageName += ".";

							}
						}
						name = name + packageName + realParameterName;
					}
					else if ( e instanceof ExprGenericPrototypeInstantiation ) {
						ExprGenericPrototypeInstantiation genProtoInst = (ExprGenericPrototypeInstantiation) e;

						String paramSourceName = genProtoInst.getType().getFullName()
								 .replace('<', '(')
								 .replace('>', ')')
								 .replace(MetaHelper.cyanLanguagePackageNameDot, "");
						name = name + paramSourceName;
					}
					else if ( e instanceof ExprTypeUnion ) {
						ExprTypeUnion typeUnion = (ExprTypeUnion) e;
						String typeUnionName = typeUnion.getType().getFullName();
						String paramSourceName = typeUnionName.replace('|', '-')
								 .replace('<', '(')
								 .replace('>', ')')
								 .replace(MetaHelper.cyanLanguagePackageNameDot, "");
						name = name + paramSourceName;
					}
					else {
						env.error(this.getFirstSymbol(), "Internal error in ExprGenericPrototypeInstantiation", true, true);
						return null;
					}
					if ( --size > 0 )
						name = name + ",";
				}
			}
			else {
				name += MetaHelper.noneArgumentNameForFunctions;
			}

			name = name + ")";

		}
		specificSourceFileName = name;
		return name;
	}



	private void analyzeJavaGenericInstantiation(Env env) {

		TypeJavaRef javaClass = (TypeJavaRef ) type;
		Class<?> jc = javaClass.getClassLoad(env, getFirstSymbol());
		TypeVariable<?>[] arrayGenParamType = jc.getTypeParameters();
		if ( this.realTypeListList.size() != 1 ) {
			env.error(getFirstSymbol(), "The generic type is a Java class. There should be just one set of '<' and '>'");
		}
		List<Expr> argList =  realTypeListList.get(0);
		javaName = javaClass.getJavaName() + "<";
		int i = 0;
		int size = argList.size();
		for ( Expr expr : argList ) {

			Type realParamType = expr.getType(env);

			java.lang.reflect.Type[] jt = arrayGenParamType[i].getBounds();
			if ( jt != null && jt.length != 0 ) {
				for ( java.lang.reflect.Type aRestrictionType : jt ) {
					if ( aRestrictionType != Object.class ) {
						if ( !(realParamType instanceof TypeJavaRef) ) {
							env.error(realTypeListList.get(0).get(i).getFirstSymbol(),
									"The " + i + "th parameter is not a Java type. It should because "
											+ "there are restrictions on this parameter by class '" + javaClass.getFullName() + "'" );
							return;
						}
						Class<?> theClass = ((TypeJavaRef) realParamType).getClassLoad(env, getFirstSymbol());
						if ( ! aRestrictionType.getClass().isAssignableFrom(theClass) )  {
							env.error(realTypeListList.get(0).get(i).getFirstSymbol(), "The " + i +
									"th parameter should be a subtype of '" + aRestrictionType.getClass().getCanonicalName() + "'");
						}
					}
				}
			}
			javaName += realParamType.getJavaName();
			if ( --size > 0 ) {
				javaName += ", ";
			}
			++i;
		}
		javaName += ">";
	}

	@Override
	public void calcInternalTypes(Env env) {


		try {
			env.pushCheckUsePossiblyNonInitializedPrototype(false);
			for ( List<Expr> realTypeList : realTypeListList ) {
				for ( Expr expr : realTypeList ) {
					/*
	    			   there are several kinds of real generic parameters:
			   					// main.Person             as in Stack<main.Person>
			   					// Tuple<main.Person>      as in Stack<Tuple<main.Person>>
			   					// Int                     as in Stack<Int>
			   					// cyan.lang.Int           as in Stack<cyan.lang.Int>
			   					// cyan.lang.Tuple<Int>    as in Stack<cyan.lang.Tuple<Int>>
	           					// add                     as in Inter<add>
					 */
					if ( expr instanceof ExprIdentStar ) {
						ExprIdentStar exprId = (ExprIdentStar) expr;
	    					 /*
			   					// main.Person             as in Stack<main.Person>
			   					// Int                     as in Stack<Int>
			   					// cyan.lang.Int           as in Stack<cyan.lang.Int>
	           					// add                     as in Inter<add>

	    					  */
						if ( ! (exprId.getIdentSymbolArray().size() == 1 &&
								Character.isLowerCase(exprId.getName().charAt(0)) ) )
							/*
							 * if the the symbol array has size 1 and it starts with a lower-case
							 * letter, then it is a symbol as 'add' in Inter<add>. In this
							 * case it is not necessary to calculate its type.
							 */
							expr.calcInternalTypes(env);
					}
					else
						/*
							Stack<Tuple<main.Person>>
							Stack<cyan.lang.Tuple<Int>>
							TypeFrom<DatabaseCustomer, "http://database.customer">
						*/
						expr.calcInternalTypes(env);
				}

			}
		}
		finally {
			env.popCheckUsePossiblyNonInitializedPrototype();
		}

		String name = getName();
//		if ( name.contains("Function<cyan.lang.Int|cyan.lang.Char,Nil>") ||
//				name.contains("Function<Int|Char,Nil>") ) {
//			name = getName();
//			NameServer.println(name);
//		}

		type = CompilerManager.createGenericPrototype(this, env);
		if ( type instanceof TypeJavaRef ) {
			this.analyzeJavaGenericInstantiation(env);
			((TypeJavaRef) type).setGPI(this);
			return;
		}

		if ( this.messageSendToAnnotation != null && env.getCompInstSet().contains(CompilationInstruction.semAn_actions) ) {
			/*
			 * the compilation unit of this generic prototype instantiation should be written to a file.
			 */
			switch ( messageSendToAnnotation.getMessage() ) {
			case "writeCode" :
				if ( compilationUnit == null ) {
					saci.TupleTwo<String, Type> t = ifPrototypeReturnsNameWithPackageAndType(env);
					if ( t != null ) {
						if ( t.f2.getInsideType() instanceof Prototype ) {
							compilationUnit = ((Prototype ) t.f2).getCompilationUnit();

						}
					}
				}
				if ( compilationUnit != null ) {
					boolean addToWrite = false;
					List<String> strList = messageSendToAnnotation.getParamList();
					if ( strList != null) {
						if ( strList.get(0).equals("SEM_AN") ) {
							addToWrite = true;
					    }
					}
					else
						addToWrite = true;

					if ( addToWrite )
						env.addCompilationUnitToWrite(compilationUnit);
				}
			}
		}
		javaName = type.getJavaName();
		// type = this.ifRepresentsTypeReturnsType(env);

		/*
		if ( type instanceof InterfaceDec ) {
			javaName = NameServer.getJavaName(NameServer.prototypeFileNameFromInterfaceFileName(type.getName()));
		}
		else
			javaName = NameServer.getJavaNameQualifiedIdentifier(this.identSymbolArray) + ".prototype";
		*/
		if ( type.getInsideType() instanceof Prototype && this.annotationToTypeList != null ) {
			// something like     Char@letter
			TypeWithAnnotations twa;
			type = twa = new TypeWithAnnotations(type, annotationToTypeList);
			twa.checkAnnotation(env);

		}

		if ( type instanceof Prototype ) {
			Prototype prototypeGenProto = (Prototype ) type;
			if ( env.peekCheckUsePossiblyNonInitializedPrototype()
					&& !(prototypeGenProto instanceof InterfaceDec)
					&& ! env.getIsArgumentToIsA() ) {
				Prototype thisPrototype = (Prototype ) type;
				List<MethodSignature> initMSList =
						thisPrototype.searchMethodPrivateProtectedPublicPackage("init", env);
				if ( (initMSList == null || initMSList.size() == 0) && type != Type.Nil ) {
					/*
					 * It is illegal to use a prototype that does not have an
					 * 'init' method
					 */
					initMSList =
							thisPrototype.searchMethodPrivateProtectedPublicPackage("new", env);
					if ( (initMSList == null || initMSList.size() == 0) ) {
						env.error(getFirstSymbol(), "Prototype '" + this.getName() + "' does "
								+ "not have an 'init' method. Therefore its fields may not have"
								+ " been initialized (they are if an 'init' method does exist)"
						 );
					}
				}
			}
			Prototype currentPrototype = env.getCurrentPrototype();
			//TODO  remove declaration of currentMethod1, use this.currentMethod instead
			MethodDec currentMethod1 = env.getCurrentMethod();
			if ( currentMethod1 != null && currentPrototype != null &&
					currentMethod1.getName().equals("init") &&
					name.equals(currentPrototype.getName()) ) {
				env.error(getFirstSymbol(), "Prototype '" + name + "' "
						+ "uses itself in its 'init' method. However, fields from "
						+ "the prototype, considered as an object, are "
						+ "initialized using the same 'init' method. Therefore,"
						+ " in the first call to 'init', to initialize the "
						+ "prototype fields, there would be a NullPointerException in Java"
				 );
			}

		}
		super.calcInternalTypes(env);

	}


	/**
	 * change the name of the prototype removing the prefix "Proto_". That is,
	 * if this object represents "cyan.lang.tmp.Proto_Interval<Int>", after this
	 * method is called this object will represent
	 * "cyan.lang.tmp.Interval<Int>"
	 */
	public void removeProtoPrefix() {
		/*
		if ( typeIdent.getLastName().startsWith(NameServer.prefixProtoInterface) ) {
			List<Symbol> symbolList = typeIdent.getIdentSymbolArray();
			int last = symbolList.size() - 1;
			symbolList.get(last).setSymbolString(symbolList.get(last).getSymbolString().substring(NameServer.prefixProtoInterface.length()));
		}
		*/
		if ( NameServer.isPrototypeFromInterface(typeIdent.getLastName()) ) {
			List<Symbol> symbolList = typeIdent.getIdentSymbolArray();
			int last = symbolList.size() - 1;
			String newName = NameServer.prototypeFileNameFromInterfaceFileName(symbolList.get(last).getSymbolString());
			symbolList.get(last).setSymbolString(
					newName
					);
		}
	}


	@Override
	public String asStringToCreateJavaName() {

		CyanEnv cyanEnv = NameServer.cyanEnv;
		PWCharArray pwChar = new PWCharArray();

		typeIdent.genCyan(pwChar, false, cyanEnv, false);
		int n;
		for ( List<Expr> arrayOfList : realTypeListList ) {
			pwChar.print("<");
			n = arrayOfList.size();
			for ( Expr t : arrayOfList ) {
				t.genCyan(pwChar, false, cyanEnv, false);
				--n;
				if ( n > 0 )
					pwChar.print(", ");
			}
			pwChar.print(">");
		}
		return pwChar.getGeneratedString().toString();
	}

	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	final public void setType(Type type) {
		this.type = type;
	}


	/**
	 * if this generic prototype instantiation is preceded by a package name, return it. Otherwise return null
	 */
	@Override
	public String getPackageName() {
		if ( typeIdent.getIdentSymbolArray().size() <= 1 )
			return null;
		else {
			String s = "";
			int i = 0;
			List<Symbol> symList = typeIdent.getIdentSymbolArray();
			while ( i < symList.size() - 1 ) {
				s = s + symList.get(i).getSymbolString();
				++i;
			}
			return s;
		}
	}

	/**
	 * return the prototype name.
	 */
	@Override
	public String getPrototypeName() {
		String name = this.typeIdent.getIdentSymbolArray().get(this.typeIdent.getIdentSymbolArray().size()-1).getSymbolString();
		for ( List<Expr> typeList : realTypeListList ) {
			name = name + "<";
			int size = typeList.size();
			for ( Expr e : typeList ) {
				String s = e.asString();
				name = name + s;
				if ( --size > 0 )
					name = name + ",";
			}
			name = name + ">";

		}
		return name;


	}

	public List<AnnotationAt> getAnnotationToTypeList() {
		return annotationToTypeList;
	}

	public void setAnnotationToTypeList(
			List<AnnotationAt> annotationList) {
		this.annotationToTypeList = annotationList;
	}

	/**
	 * symbol representes the type. In Stack<Int>, it is Stack. In
	 * DS.Stack<Int>, it is DS.Stack, which is represented by an object
	 * of ExprIdentStar.
	 */
	private ExprIdentStar typeIdent;
	/**
	 * the arguments of a generic object. null if the type is not
	 * a generic object or interface. It is a list of list because
	 * of generic objects like F<Int><Nil>. The first list
	 * contains "Int" and the second one just "Nil".
	 */
	private List<List<Expr>> realTypeListList;
	/**
	 * the program unit in which this generic object instantiation is.
	 * An instantiation of a generic object such as
	 *       Stack<Person>
	 * does not identify completely "Stack" and "Person". A program may have
	 * two or more "Stack" and two or more "Person" objects. However, they must be
	 * in different packages. Through the field prototype one can
	 * discover, using the list of imported packages (prototype.getCompilationUnit().getImportPackageList()),
	 * where is each object (Stack and Person).
	 */
	private Prototype prototype;

	/**
	 * the message send that follows the generic prototype instantiation. See comments on ast#MessageSendToAnnotation
	 */
	private MessageSendToAnnotation messageSendToAnnotation;
	/**
	 * the compilation Unit in which the prototype of this generic prototype instantiation is
	 */
	private CompilationUnit compilationUnit;

	/**
	 * name with package and type. Only used if this expression is a type
	 */
	private saci.TupleTwo<String, Type> nameWithPackageAndType;


	private String javaName;



	/**
	 * a list of metaobject annotations that is attached to this generic prototype instantiation
	 */
	private List<AnnotationAt> annotationToTypeList;
}
