package ast;



import java.util.ArrayList;
import java.util.List;
import error.ErrorKind;
import lexer.Lexer;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.ICompiler_semAn;
import meta.Token;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 * Represents the declaration of an interface
 * @author José
 *
 */

public class InterfaceDec extends Prototype {

	public InterfaceDec() { }

	public InterfaceDec(ObjectDec outerObject, Symbol interfaceSymbol, SymbolIdent symbol,
			Token visibility,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList, Lexer lexer) {
		super(visibility, nonAttachedAnnotationList, attachedAnnotationList,
				outerObject);
		lexer.setPrototype(this);
		this.interfaceSymbol = interfaceSymbol;
		this.symbol = symbol;
		this.methodSignatureList = new ArrayList<MethodSignature>();
		this.allMethodSignatureList = null;
	}

	static public void initInterfaceDec(InterfaceDec newInterfaceDec,
			ObjectDec outerObject, Symbol interfaceSymbol, SymbolIdent symbol, Token visibility,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList, Lexer lexer) {
		Prototype.initPrototype(newInterfaceDec, visibility,
				nonAttachedAnnotationList,
				attachedAnnotationList,
				outerObject);
        lexer.setPrototype(newInterfaceDec);
        newInterfaceDec.interfaceSymbol = interfaceSymbol;
        newInterfaceDec.symbol = symbol;
        newInterfaceDec.methodSignatureList = new ArrayList<MethodSignature>();
        newInterfaceDec.allMethodSignatureList = null;

	}

	@Override
	public InterfaceDec clone() {
		return (InterfaceDec ) super.clone();
	}


	@Override
	public void accept(ASTVisitor visitor) {

		visitor.preVisit(this);

		if ( superInterfaceList != null ) {
			for ( InterfaceDec inter : this.superInterfaceList ) {
				inter.accept(visitor);
			}
		}
		if ( methodSignatureList != null ) {
			for ( MethodSignature ms : this.methodSignatureList ) {
				ms.accept(visitor);
			}
		}
		visitor.visit(this);
	}



	@Override
	public FieldDec searchFieldDec(String varName) {
		return null;
	}


	@Override
	public FieldDec searchField(String name) {
		return null;
	}

	@Override
	public FieldDec searchFieldPrivateProtectedSuperProtected(java.lang.String varName) {
		return null;
	}


	@Override
	public FieldDec searchFieldDecProtected(java.lang.String varName) {
		return null;
	}


//	@Override
//	public void genCompiledInterfaces(StringBuffer sb) {
//
//		CyanEnv cyanEnv = new CyanEnv(false, false);
//
//		PWCharArray pw = new PWCharArray(sb);
//		genCyan(pw, cyanEnv, false);
//		sb.append("\n");
//	}
//

//		sb.append("\r\n");
//		sb.append(NameServer.getVisibilityString(visibility) + " ");
//		sb.append("interface " + symbol.getSymbolString() + "\r\n");
//
//		if ( superInterfaceExprList != null ) {
//	    	int size = this.superInterfaceExprList.size();
//	    	sb.append("          ");
//		    if ( size > 0 )
//		    	sb.append("extends ");
//		    for ( Expr si : this.superInterfaceExprList ) {
//			    si.genCyan(pw, false, cyanEnv, genFunctions);
//			    if ( --size > 0 )
//			    	sb.append(", ");
//	    	}
//		}
//		sb.append("\r\n");



	@Override
	public void genCyan(PWInterface pw, CyanEnv cyanEnv, boolean genFunctions) {

		cyanEnv.atBeginningOfPrototype(this);


		ExprGenericPrototypeInstantiation exprGPI = cyanEnv.getExprGenericPrototypeInstantiation();

		super.genCyan(pw, cyanEnv, genFunctions);
		//#$ {

//		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
//			pw.println("@genericPrototypeInstantiationInfo(\"" + cyanEnv.getPackageNameInstantiation() + "\", \"" + cyanEnv.getPrototypeNameInstantiation()
//			  + "\", " + exprGPI.getFirstSymbol().getLineNumber() + ", " + exprGPI.getFirstSymbol().getColumnNumber() + ")");
//		}

		//#$ }

		pw.println("");
		pw.print(NameServer.getVisibilityString(visibility) + " ");
		pw.print("interface ");

		genCyanPrototypeName(pw, cyanEnv);

		if ( moListBeforeExtendsMixinImplements != null ) {
			for ( AnnotationAt annotation : moListBeforeExtendsMixinImplements ) {
				annotation.genCyan(pw, false, cyanEnv, genFunctions);
				pw.print(" ");
			}
		}


		if ( superInterfaceExprList != null ) {
    		pw.add();   pw.add();
	    	int size = this.superInterfaceExprList.size();
	    	pw.printIdent("  ");
		    if ( size > 0 )
			    pw.print("extends ");
		    for ( Expr si : this.superInterfaceExprList ) {
			    si.genCyan(pw, false, cyanEnv, genFunctions);
			    if ( --size > 0 )
    				pw.print(", ");
	    	}
		    pw.sub();   pw.sub();
		}
	    pw.println("");

		pw.add();


		for (MethodSignature ms : this.methodSignatureList) {
			ms.genCyanAnnotations(pw, true, cyanEnv, genFunctions);
			pw.printIdent("func ");
			ms.genCyan(pw, true, cyanEnv, genFunctions);
			pw.println();
		}
		pw.sub();

		if ( beforeEndNonAttachedAnnotationList != null ) {
			for ( AnnotationAt c : this.beforeEndNonAttachedAnnotationList )
				c.genCyan(pw, true, cyanEnv, genFunctions);
		}

		pw.printlnIdent("end");
		pw.println("");

		cyanEnv.atEndOfCurrentPrototype();

	}

	@Override
	public void calcInterfaceTypes(Env env) {


		env.atBeginningOfObjectDec(this);


		super.calcInternalTypesNONAttachedAnnotations(env);

		for ( List<GenericParameter> genericParameterList : genericParameterListList )
			for ( GenericParameter genericParameter : genericParameterList )
				genericParameter.calcInternalTypes(env);
		String thisInterfaceName = this.getName();
		if ( superInterfaceExprList != null && superInterfaceExprList.size() > 0 ) {
			superInterfaceList = new ArrayList<>();
			for ( Expr anInterface : superInterfaceExprList ) {
				anInterface.calcInternalTypes(env);
				if ( anInterface.getType(env) == null ) {
					env.error(anInterface.getFirstSymbol(), "Interface '" + anInterface.asString() + "' was not found");
					return;
				}
				Type anInterfaceType = anInterface.getType(env);
				if ( !(anInterfaceType instanceof InterfaceDec ) ) {
					env.error(anInterface.getFirstSymbol(), "This interface is extending a type that is not an interface: '" +
				            anInterface.asString() + "' was not found");
					return;
				}
				InterfaceDec superInterface = (InterfaceDec ) anInterfaceType;
				superInterfaceList.add( superInterface );

				String superInterfaceName = anInterface.ifPrototypeReturnsItsName(env);
				if ( !( anInterface.getType(env) instanceof InterfaceDec ) )
					env.error(anInterface.getFirstSymbol(), superInterfaceName + " is not an interface", true, true);

				if ( this == superInterface )
					env.error(anInterface.getFirstSymbol(), "Interface " + thisInterfaceName + " is inheriting from itself", true, true);
				/*
				for ( InterfaceDec superInter : superInterfaceList ) {
					if ( superInter == superInterface ) {
						env.error(anInterface.getFirstSymbol(), "Duplicate super interface", true);
						break;
					}
				}
				*/
				int i = 0;
				int size = superInterfaceList.size();
				while ( i < size - 1 ) {
					if ( superInterfaceList.get(i) == superInterface ) {
						env.error(anInterface.getFirstSymbol(), "Duplicate super interface '" + superInterface.getName() + "'", true, true);
						break;
					}
					++i;
				}
				if ( i != size - 1 )
					break;

				if ( anInterface.getType() instanceof TypeWithAnnotations ) {
					env.error(anInterface.getFirstSymbol(), "Implemented interface '" + anInterface.asString() +
							"' has an attached annotation."
							+ " This is illegal");
				}


			}
		}

		List<String> methodNameList = new ArrayList<String>();
		for ( MethodSignature methodSignature: methodSignatureList ) {
			   // atEndMethodDec clear the lists of parameters
			env.atEndMethodDec();
			methodSignature.calcInterfaceTypes(env);
			String methodName = methodSignature.getFullName(env);
			for ( int i = 0; i < methodNameList.size(); ++i ) {
				if ( methodName.compareTo(methodNameList.get(i)) == 0 )
					env.error(methodSignature.getFirstSymbol(), "Duplicate method signature with that of line "
							+ methodSignatureList.get(i).getFirstSymbol().getLineNumber(), true, true);
			}
			methodNameList.add(methodName);
		}
		if ( beforeEndNonAttachedAnnotationList != null ) {
			for ( AnnotationAt annotation : beforeEndNonAttachedAnnotationList ) {
				annotation.calcInternalTypes(env);
			}
		}


		super.calcInterfaceTypes(env);

		env.atEndOfObjectDec();
	}


	@Override
	public void calcInternalTypes(ICompiler_semAn compiler_semAn, Env env) {


		env.atBeginningOfObjectDec(this);
		meta.GetHiddenItem.getHiddenEnv(compiler_semAn.getEnv()).atBeginningOfObjectDec(this);

		List<Annotation> metaobjectAnnotationList = new ArrayList<>();
		metaobjectAnnotationList.addAll(completeAnnotationList);

		metaobjectAnnotationList.addAll(
				this.getCompilationUnit().getCyanPackage().getAttachedAnnotationList());

		metaobjectAnnotationList.addAll(
				this.getCompilationUnit().getCyanPackage().getProgram().getAttachedAnnotationList());

		makeAnnotationsCommunicateInPrototype(metaobjectAnnotationList, env);


		super.calcInternalTypes(compiler_semAn, env);

		for ( MethodSignature ms : this.methodSignatureList ) {
			String name = ms.getNameWithoutParamNumber();
			if ( name.compareTo("init") == 0 || name.compareTo("init:") == 0 || name.compareTo("new") == 0 ||
					name.compareTo("new:") == 0 ) {
				env.error(true, ms.getFirstSymbol(), "'init', 'init:', 'new', or 'new:' methods cannot be declared in interfaces",
						ms.getNameWithoutParamNumber(), ErrorKind.init_new_methods_cannot_be_declared_in_interfaces);
			}
		}
		env.atEndOfObjectDec();
		meta.GetHiddenItem.getHiddenEnv(compiler_semAn.getEnv()).atEndOfObjectDec();

	}

	@Override
	public void genJava(PWInterface pw, Env env) {

		env.atBeginningOfObjectDec(this);


		genJavaCodeBeforeClassAnnotations(pw, env);


		pw.println();

		if ( this.visibility == Token.PRIVATE )
			pw.print("private ");
		else if ( this.visibility != Token.PACKAGE )
			pw.print("public ");

		pw.printIdent("interface " + getJavaNameWithoutPackage());

		if ( moListBeforeExtendsMixinImplements != null ) {
			for ( AnnotationAt annotation : moListBeforeExtendsMixinImplements ) {
				annotation.genJava(pw, env);
				pw.print(" ");
			}
		}


		if ( superInterfaceExprList == null ) {
		    pw.print(" extends " + NameServer.IAny);
		}
		else {
    		pw.add();   pw.add();  pw.add();
	    	int size = this.superInterfaceExprList.size();
		    if ( size > 0 )
			    pw.print(" extends ");
		    for ( Expr si : this.superInterfaceExprList ) {
		    	pw.print(si.getType().getJavaName());
			    pw.print(" ");
			    if ( --size > 0 )
    				pw.print(", ");
	    	}
		    pw.sub();   pw.sub();  pw.sub();
		}
		pw.println(" {");
		pw.add();
		for (MethodSignature ms : this.methodSignatureList) {
			pw.printIdent("default ");
			ms.genJava(pw, env);
			pw.println("{ throw new ExceptionContainer__(_ExceptionCannotCallInterfaceMethod.prototype); } ");
		}

		pw.sub();
		pw.printlnIdent("}");

		env.atEndOfObjectDec();

	}


	public void genCyanProtoInterface(PW pw) {

		String name = getName();
		pw.println("");
		pw.println("object " + NameServer.prototypeFileNameFromInterfaceFileName(Lexer.addSpaceAfterComma(name)) +
				" implements " + Lexer.addSpaceAfterComma(name) );
		pw.println("");
		/*
		pw.add();

		for (MethodSignature ms : this.methodSignatureList) {
			pw.printIdent("func ");
			ms.genCyan(pw, true, cyanEnv, true);
			pw.println(" {");
			pw.add();
			pw.printlnIdent("throw ExceptionCannotCallInterfaceMethod");
			pw.sub();
			pw.printlnIdent("}");
			pw.println();
		}

		pw.sub();
		*/
		pw.println("end");

	}


	public void setSuperInterfaceExprList(List<Expr> superInterfaceList) {
		this.superInterfaceExprList = superInterfaceList;
	}

	public List<Expr> getSuperInterfaceExprList() {
		return superInterfaceExprList;
	}

	public void addMethodSignature(MethodSignature ms) {
		methodSignatureList.add(ms);
	}

	public void setMethodSignatureList(List<MethodSignature> methodSignatureList) {
		this.methodSignatureList = methodSignatureList;
	}

	public List<MethodSignature> getMethodSignatureList() {
		return methodSignatureList;
	}

	public List<MethodSignature> getAllMethodSignatureList() {
		if ( allMethodSignatureList == null ) {
			allMethodSignatureList = new ArrayList<MethodSignature>();
			this.allMethodSignatureList.addAll(this.methodSignatureList);
			if ( superInterfaceList != null ) {
				for ( InterfaceDec superInterface : this.superInterfaceList ) {
					allMethodSignatureList.addAll(superInterface.getAllMethodSignatureList());
				}
			}
		}
		return this.allMethodSignatureList;
	}


	@Override
	public Symbol getFirstSymbol() {
		return interfaceSymbol;
	}

	@Override
	public List<MethodSignature> searchMethodPublicPackageSuperPublicPackage(
			String methodName, Env env) {
		List<MethodSignature> foundMethodSignatureList = searchMethodPublicSuperPublicOnlyInterfaces(methodName, env);
		if ( foundMethodSignatureList.size() == 0 ) {
			/*
			 * search in Any
			 */
			ObjectDec any = (ObjectDec ) Type.Any;
			return any.searchMethodPublicPackageSuperPublicPackage(methodName, env);
		}
		return foundMethodSignatureList;
	}

	@Override
	public List<MethodSignature> searchMethodPublicPackage(String methodName, Env env) {

		List<MethodSignature> foundMethodSignatureList = new ArrayList<MethodSignature>();
		for ( MethodSignature m : methodSignatureList ) {
			if ( m.getName().equals(methodName) )
				foundMethodSignatureList.add(m);
		}
		return foundMethodSignatureList;
	}



	public List<MethodSignature> searchMethodPublicSuperPublicOnlyInterfaces(
			String methodName, Env env) {
		List<MethodSignature> foundMethodSignatureList = new ArrayList<MethodSignature>();
		for ( MethodSignature m : methodSignatureList ) {
			if ( m.getName().equals(methodName) )
				foundMethodSignatureList.add(m);
		}
		if ( foundMethodSignatureList.size() != 0 )
			return foundMethodSignatureList;
		else {

			if ( superInterfaceExprList != null ) {
				for ( Expr expr : superInterfaceExprList ) {
					Prototype superInterface = (Prototype ) expr.getType(env).getInsideType();
					foundMethodSignatureList = superInterface.searchMethodPublicPackageSuperPublicPackage(methodName, env);
					if ( foundMethodSignatureList.size() > 0 )
						return foundMethodSignatureList;
				}
			}
			return foundMethodSignatureList;
		}
	}

	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
			String methodName, Env env) {
		return searchMethodPublicPackageSuperPublicPackage(methodName, env);
	}


	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackage(String methodName, Env env) {
		return searchMethodPublicPackage(methodName, env);
	}

	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackage(String methodName, Env env) {
		return searchMethodPublicPackage(methodName, env);
	}

	@Override
	public List<MethodSignature> searchMethodProtected(String methodName, Env env) {
		return new ArrayList<MethodSignature>();
	}

	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
			String methodName, Env env) {
		return searchMethodPublicPackage(methodName, env);
	}


	public List<MethodSignature> searchMethodPrivateProtectedPublicSuperProtectedPublicOnlyInterfaces(
			String methodName, Env env) {
		return searchMethodPublicSuperPublicOnlyInterfaces(methodName, env);
	}



	public List<MethodSignature> searchMethodProtectedPublicSuperProtectedPublicOnlyInterfaces(
			String methodName, Env env) {
		return searchMethodPublicSuperPublicOnlyInterfaces(methodName, env);
	}




	@Override
	public boolean isSupertypeOf(Type otherType, Env env) {

		if ( otherType instanceof TypeDynamic )
			return true;
		// String thisName = this.getName();
		// String otherTypeName = otherType.getName();
		if ( this == otherType )
			return true;
		if ( otherType instanceof InterfaceDec ) {
			InterfaceDec otherInter = (InterfaceDec ) otherType;
			if ( otherInter.getSuperInterfaceExprList() != null ) {
				for ( Expr superInterfaceExpr : otherInter.getSuperInterfaceExprList() ) {
					Prototype superInterface = (Prototype ) superInterfaceExpr.ifRepresentsTypeReturnsType(env).getInsideType();
					if ( this.isSupertypeOf(superInterface, env) )
						return true;
				}
			}
		}
		else if ( otherType instanceof ObjectDec ) {
			ObjectDec otherProto = (ObjectDec ) otherType;
			if ( otherProto.getInterfaceList() != null ) {
				for ( Expr superInterfaceExpr : otherProto.getInterfaceList() ) {
					Prototype superInterface = (Prototype ) superInterfaceExpr.ifRepresentsTypeReturnsType(env).getInsideType();
					if ( this.isSupertypeOf(superInterface, env) )
						return true;
				}
			}
			if ( otherProto.getSuperobject() == null )
				return false;
			else
				return isSupertypeOf(otherProto.getSuperobject(), env);
		}
		else if ( otherType instanceof TypeUnion ) {
			TypeUnion tu = (TypeUnion ) otherType;
			for ( Type t : tu.getTypeList() ) {
				if ( ! this.isSupertypeOf(t, env) ) {
					return false;
				}
			}
			return true;
		}
		else if ( otherType instanceof TypeIntersection ) {
			TypeIntersection ti = (TypeIntersection ) otherType;
			for ( Type t : ti.getTypeList() ) {
				if ( this.isSupertypeOf(t, env) ) {
					return true;
				}
			}
			return false;

		}
		else
			env.error(null,  "Internal error in InterfaceDec::isSupertypeOf: unknown type", true, true);
		return false;
	}


	@Override
	public boolean getIsFinal() {
		return false;
	}






	public List<InterfaceDec> getSuperInterfaceList() {
		return superInterfaceList;
	}





	/**
	 * the symbol of keyword 'interface'
	 */
	private Symbol interfaceSymbol;


	/**
	 * the super-interfaces of this interface as Expr
	 */
	private List<Expr> superInterfaceExprList;

	/**
	 * the super-interfaces of this interface.
	 */
	private List<InterfaceDec> superInterfaceList;
	/**
	 * list of method signatures
	 */
	private List<MethodSignature> methodSignatureList;


	/**
	 * list of all method signatures, including of the super interfaces
	 */
	private List<MethodSignature> allMethodSignatureList;


}
