package meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ast.CompilationUnit;
import ast.ContextParameter;
import ast.AnnotationAt;
import ast.Expr;
import ast.FieldDec;
import ast.GenericParameter;
import ast.InterfaceDec;
import ast.MethodDec;
import ast.MethodSignature;
import ast.ObjectDec;
import ast.ProgramUnit;
import ast.Type;

/**
 * A program unit, which is a prototype. It may be an interface declared with keyword 'interface'
 * or a non-interface declared with keyword 'object'.
 *
 * All methods that take a WrEnv parameter are restricted. They can only be called inside
 * the program unit itself. Otherwise an exception MetaSecurityException is thrown.
   @author jose
 */
public class WrProgramUnit extends WrType implements IDeclarationWritable {

    private WrProgramUnit(ProgramUnit hidden) {
        super(hidden);
        hidden.setiProgramUnit(this);
    }


    static public WrProgramUnit factory(ProgramUnit hidden) {
    	if ( hidden.getiProgramUnit() != null ) {
    		return hidden.getiProgramUnit();
    	}
    	else {
    		WrProgramUnit iProgramUnit = new WrProgramUnit(hidden);
    		hidden.setiProgramUnit(iProgramUnit);
    		return iProgramUnit;
    	}
    }

    @Override
    ProgramUnit getHidden() { return (ProgramUnit ) hidden; }

    /**
     * return the compilation unit of this program unit
       @param env
       @return
     */
    public WrCompilationUnit getCompilationUnit(WrEnv env) {
    	securityCheck(env);

    	checkDependenteProgramUnit(env);
    	CompilationUnit cunit = ((ProgramUnit ) hidden).getCompilationUnit();
    	return cunit == null ? null : cunit.getI();
    }


    /**
     * part of the implementation of the visitor pattern
       @param visitor
       @param env
     */

    public void accept(WrASTVisitor visitor, WrEnv env) {

    	// securityCheck(env);
		visitor.preVisit(this, env);

		if ( hidden instanceof ObjectDec ) {
			ObjectDec objDec = (ObjectDec ) hidden;
			List<ContextParameter> contextParameterList = objDec.getSuperContextParameterList();
			if ( contextParameterList != null ) {
				for ( ContextParameter cp : contextParameterList ) {
					cp.getI().accept(visitor, env);
				}
			}
		}

		List<WrMethodDec> imList = this.getMethodDecList(env);
		if ( imList != null ) {
			for ( final WrMethodDec m : imList ) {
				m.accept(visitor, env);
			}
		}
		List<WrMethodSignature> msList = this.getMethodSignatureList(env);
		if ( msList != null ) {
			for ( final WrMethodSignature ms : msList ) {
				ms.accept(visitor, env);
			}
		}
		WrProgramUnit currentProgramUnit = env.getCurrentProgramUnit();
		boolean thisEqCurrent = currentProgramUnit == this;
		if ( thisEqCurrent ) {
			List<WrFieldDec> ifList = this.getFieldList(env);
			if ( ifList != null ) {
				for ( final WrFieldDec iv : ifList ) {
					iv.accept(visitor, env);
				}
			}
		}
		visitor.visit(this, env);
	}

    List<WrContextParameter> iSuperContextParameterList = null;
	boolean thisMethod_wasNeverCalled2 = true;

	/**
	 * return a list of context parameters of the super prototype
	   @param env
	   @return
	 */
	public List<WrContextParameter> getSuperContextParameterList(WrEnv env) {

    	securityCheck(env);
    	checkDependenteProgramUnit(env);


		if ( thisMethod_wasNeverCalled2 ) {
			thisMethod_wasNeverCalled2 = false;

			if ( !(hidden instanceof ObjectDec) ) {
				return null;
			}
			List<ContextParameter> fromList = ((ObjectDec ) hidden).getSuperContextParameterList() ;
			if ( fromList == null ) {
					// unnecessary, just to document
				iSuperContextParameterList = null;
			}
			else {
				iSuperContextParameterList = new ArrayList<>();
				for ( ContextParameter from : fromList ) {
					iSuperContextParameterList.add( from.getI() );
				}
			}

		}
		return iSuperContextParameterList;
	}


	/**
	   @param env
	 */
	private void securityCheck(WrEnv env) {
		if ( env.getCurrentProgramUnit() != null && env.getCurrentProgramUnit().hidden != this.hidden ) {
    		throw new MetaSecurityException();
    	}
   	}


	/**
	 * returns the prototype name. If it is "Person", "Person" is returned. If it is a generic prototype
	 * "Stack{@literal <}T>", "Stack{@literal <}T>" is returned. If it is an instantiated generic prototype "Stack{@literal <}main.Person>",
	 * "Stack{@literal <}main.Person>" is returned.
	 * <br>
	 * This method should not be called during semantic analysis if the prototype is generic
	 * because it does not give the correct name, with the packages.
	 * @return
	 */
    @Override
    public String getName() {
        return ((ProgramUnit ) hidden).getName();
    }

    /**
     * return AttachedDeclarationKind.PROTOTYPE_DEC
     */
    @Override
    public AttachedDeclarationKind getKind(WrEnv env) {
        return ((ProgramUnit ) hidden).getKind();
    }

    /**
     * add a document text to the documentation of this prototype. docKind is
     * the kind of document, a user-defined field with no pre-defined meaning.
     */

    @Override
    public void addDocumentText(String doc, String docKind, WrEnv env) {
    	securityCheck(env);

    	CompilationStep step = env.getCompilationStep();
    	if ( step != CompilationStep.step_1 &&
       		 step != CompilationStep.step_4 &&
       		 step != CompilationStep.step_7 ) {
    		throw new MetaSecurityException();
    	}

        ((ProgramUnit ) hidden).addDocumentText(doc, docKind);
    }
    /**
     * add a feature to the list of features of this prototype.
     */

    @Override
    public void addFeature(Tuple2<String, WrExprAnyLiteral> feature, WrEnv env) {
    	securityCheck(env);
    	CompilationStep step = env.getCompilationStep();
    	if ( step != CompilationStep.step_1 &&
       		 step != CompilationStep.step_4 &&
       		 step != CompilationStep.step_7 ) {
    		throw new MetaSecurityException();
    	}

        ((ProgramUnit ) hidden).addFeature(feature);
    }

    /**
     * add a code example to the documentation of this prototype. exampleKind is
     * the kind of example, an user-defined field with no pre-defined meaning.
     */

    @Override
    public void addDocumentExample(String example, String exampleKind, WrEnv env) {

    	securityCheck(env);
    	CompilationStep step = env.getCompilationStep();
    	if ( step != CompilationStep.step_1 &&
       		 step != CompilationStep.step_4 &&
       		 step != CompilationStep.step_7 ) {
    		throw new MetaSecurityException();
    	}

        ((ProgramUnit ) hidden).addDocumentExample(example, exampleKind);
    }

    /**
     * return the list of documents associated to this prototype. Each tuple
     * is composed of a document text and an user-defined document kind.
     */

    @Override
    public List<Tuple2<String, String>> getDocumentTextList(WrEnv env) {

    	checkDependenteProgramUnit(env);
        return ((ProgramUnit ) hidden).getDocumentTextList();
    }

    /**
     * return the list of examples associated to this prototype. Each tuple
     * is composed of a code example and an user-defined example kind
     */

    @Override
    public List<Tuple2<String, String>> getDocumentExampleList(WrEnv env) {

    	checkDependenteProgramUnit(env);
        return ((ProgramUnit ) hidden).getDocumentExampleList();
    }


	/**
	 * return the list of features associated to this prototype
	 */

    @Override
    public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList(WrEnv env) {

    	checkDependenteProgramUnit(env);
    	return ((ProgramUnit ) hidden).getFeatureList();
    }

    /**
     * search for feature 'name' in the list of features of this method
     */
    @Override
    public List<WrExprAnyLiteral> searchFeature(String name, WrEnv env) {

    	checkDependenteProgramUnit(env);
    	return ((ProgramUnit ) hidden).searchFeature(name);
    }

    /**
     * return the name of this prototype with the package. The prototype
     * name is got from method getName()
     */
	@Override
	public String getFullName() {
		return ((ProgramUnit ) hidden).getFullName();
	}

	/**
	 * return true if this prototype is supertype of 'other'
	 */
	@Override
	public boolean isSupertypeOf(WrType other, WrEnv env) {

    	checkDependenteProgramUnit(env);
		return ((ProgramUnit ) hidden).isSupertypeOf(other.hidden, env.hidden);
	}

	/**
	 * return this prototype
	 */
	@Override
	public WrType getInsideType() {
		ast.Type t = ((ProgramUnit ) hidden).getInsideType();
		return t == null ? null : t.getI();
	}

	/**
	 * return true if this prototype is not declared with 'open'
	   @return
	 */
	public boolean getIsFinal(WrEnv env) {

    	checkDependenteProgramUnit(env);
		return ((ProgramUnit ) hidden).getIsFinal();
	}

	/**
	 * return true if this prototype is generic
	   @return
	 */
	public boolean isGeneric(WrEnv env) {

    	checkDependenteProgramUnit(env);
		return ((ProgramUnit ) hidden).isGeneric();
	}

	/**
	 * return the list of list of generic parameters
	   @return
	 */
	public List<List<WrGenericParameter>> getGenericParameterListList(WrEnv env) {

    	checkDependenteProgramUnit(env);
		if ( igpListList == null ) {
			if ( ((ProgramUnit ) hidden).getGenericParameterListList() == null ) {
				return null;
			}
			this.igpListList = new ArrayList<>();
			for ( final List<GenericParameter> gpList : ((ProgramUnit ) hidden).getGenericParameterListList() ) {
				final List<WrGenericParameter> igpList = new ArrayList<>();
				igpListList.add(igpList);
				for ( final GenericParameter gp : gpList ) {
					igpList.add( gp.getI() );
				}
			}
		}
		return igpListList;
	}

	/**
	 * return the list of methods of this prototype. If the current prototype of
	 * the environment is the same as this prototype,  all methods are returned.
	 * Otherwise, if the current prototype of the environment is in the same package
	 * the same as this prototype,  public and package methods are returned. If the
	 * current prototype is subprototype of this, protected methods are included.
	 * Otherwise, only public methods are returned.
	 *
	 * In any case, init and init: methods are not included
	   @param env
	   @return
	 */
	public List<WrMethodDec> getMethodDecList(WrEnv env) {

    	checkDependenteProgramUnit(env);

		if ( hidden instanceof InterfaceDec ) {
			return null;
		}
		final List<MethodDec> methodList = ((ObjectDec ) hidden).getMethodDecList();

		WrProgramUnit currentProgramUnit = env.getCurrentProgramUnit();
		boolean thisEqCurrent = currentProgramUnit == this;
		if ( thisEqCurrent ) {

			// all methods
			if ( iAllMethodDecList == null ) {
				iAllMethodDecList = new ArrayList<>();
				if ( methodList != null ) {
					for ( final MethodDec m : methodList ) {
						iAllMethodDecList.add(m.getI());
					}
				}
			}
			return iAllMethodDecList;
		}
		else {
			final List<WrMethodDec> retMethodList = new ArrayList<>();

			// only public methods
			if ( iPublicMethodDecList == null ) {
				iPublicMethodDecList = new ArrayList<>();
				if ( methodList != null ) {
					for ( final MethodDec m : methodList ) {
						if ( m.getVisibility() == Token.PUBLIC ) {
							iPublicMethodDecList.add(m.getI());
						}
					}
				}
			}
			retMethodList.addAll(iPublicMethodDecList);

			if ( this.isSupertypeOf(currentProgramUnit, env) ) {
				// current, the client, is superprototype of this
				if ( iProtectedMethodDecList == null ) {
					iProtectedMethodDecList = new ArrayList<>();
					if ( methodList != null ) {
						for ( final MethodDec m : methodList ) {
							Token visibility = m.getVisibility();
							if ( visibility == Token.PROTECTED ) {
								iProtectedMethodDecList.add(m.getI());
							}
						}
					}
				}
				retMethodList.addAll(this.iProtectedMethodDecList);
			}
			if ( env.getCurrentCompilationUnit().getPackageName().equals(
						((ObjectDec ) hidden).getCompilationUnit().getPackageName()) ) {
				// current and this are on the same package
				if ( iPackageMethodDecList == null ) {
					iPackageMethodDecList = new ArrayList<>();
					if ( methodList != null ) {
						for ( final MethodDec m : methodList ) {
							Token visibility = m.getVisibility();
							if ( visibility == Token.PACKAGE ) {
								WrMethodDec iMethod = m.getI();
								iPackageMethodDecList.add(m.getI());
								retMethodList.add(iMethod);
							}
						}
					}
				}
				retMethodList.addAll(iPackageMethodDecList);
			}
			return retMethodList;
		}

	}


	/*
	 * return 'init' and 'init:' methods. See the documentation of getMethodDecList.
	 * The same observations relating to visibility and returned methods apply here.
	 */
	public List<WrMethodDec> getInitDecList(WrEnv env) {

    	checkDependenteProgramUnit(env);

		if ( hidden instanceof InterfaceDec ) {
			return null;
		}
		final List<MethodDec> methodList = ((ObjectDec ) hidden).getInitNewMethodDecList();
		final List<WrMethodDec> wrMethodList = new ArrayList<>();
		if ( methodList == null ) {
			return wrMethodList;
		}

		WrProgramUnit currentProgramUnit = env.getCurrentProgramUnit();
		boolean thisEqCurrent = currentProgramUnit == this;
		if ( thisEqCurrent ) {
			for ( final MethodDec m : methodList ) {
				wrMethodList.add(m.getI());
			}
		}
		else {

			for ( final MethodDec m : methodList ) {
				if ( m.getVisibility() == Token.PUBLIC ) {
					wrMethodList.add(m.getI());
				}
			}

			if ( this.isSupertypeOf(currentProgramUnit, env) ) {
				// current, the client, is superprototype of this

				for ( final MethodDec m : methodList ) {
					Token visibility = m.getVisibility();
					if ( visibility == Token.PROTECTED ) {
						wrMethodList.add(m.getI());
					}
				}
			}
			if ( env.getCurrentCompilationUnit().getPackageName().equals(
						((ObjectDec ) hidden).getCompilationUnit().getPackageName()) ) {
				// current and this are on the same package

				for ( final MethodDec m : methodList ) {
					Token visibility = m.getVisibility();
					if (visibility == Token.PACKAGE ) {
						wrMethodList.add(m.getI());
					}
				}
			}
		}
    	checkDependenteProgramUnit(env);

		return wrMethodList;
	}


	/**
	 * makes the semantic analysis of the supertypes. Only for
	 * super-programmers
	   @param env
	 */
	public void calcInterfaceSuperTypes(WrEnv env) {

    	//securityCheck(env);

		if ( hidden instanceof ObjectDec ) {
			((ObjectDec ) hidden).calcInterfaceSuperTypes(env.hidden);
		}
	}

	/**
	 * return the annotations attached to this prototype. If the current prototype
	 * of 'env' is equal to the receiver, all annotations are returned. If it is not,
	 * only the public annotations are returned.
	   @param env
	   @return
	 */
	public List<WrAnnotationAt> getAttachedMetaobjectAnnotationList(WrEnv env) {
		List<AnnotationAt> annotList = ((ProgramUnit) hidden).getAttachedMetaobjectAnnotationList();
		List<WrAnnotationAt> wrAnnotList = new ArrayList<>();
		boolean restrited = env.getCurrentProgramUnit() != this;

		if ( annotList != null ) {
				for ( final AnnotationAt m : annotList ) {
					if ( restrited ) {
						if ( m.getCyanMetaobject().getVisibility() == Token.PUBLIC ) {
							wrAnnotList.add(m.getI());
						}
					}
					else {
						wrAnnotList.add(m.getI());
					}
				}
				if ( annotList.size() > 0 ) {
					checkDependenteProgramUnit(env);
				}
		}
		return wrAnnotList;

	}


	private List<List<WrGenericParameter>> igpListList = null;

	private List<WrMethodDec> iAllMethodDecList = null;
	private List<WrMethodDec> iPublicMethodDecList = null;
	private List<WrMethodDec> iProtectedMethodDecList = null;
	private List<WrMethodDec> iPackageMethodDecList = null;
	private List<WrFieldDec> iFieldList = null;

	/**
	 * return the name of the prototype. If it is a generic prototype, return the
	 * name without the parameters. Then if the prototype is
	 * <code>Stack{@literal <}Int></code>, this method returns <code>Stack</code>.
	   @return
	 */

	public String getSimpleName() {
		return ((ProgramUnit ) hidden).getSimpleName();
	}

	/**
	 * return the list of fields of this prototype.
	   @param env
	   @return
	 */
	public List<WrFieldDec> getFieldList(WrEnv env) {

    	securityCheck(env);

		if ( iFieldList == null ) {
			iFieldList = new ArrayList<>();
			if ( hidden instanceof ObjectDec ) {
				List<FieldDec> fieldList = ((ObjectDec ) hidden).getFieldList();
				if ( fieldList != null ) {
					for ( final FieldDec f : fieldList )  {
						this.iFieldList.add( f.getI());
					}
				}
			}
		}
		return iFieldList;
	}

//	/**
//	 * add the Java interface 'param' to the list of Java interfaces implemented
//	 * by the Java class produced from this prototype. To be used only by
//	 * prototypes of cyan.lang.
//	   @param param
//	   @param env
//	   @return
//	 */
//	public boolean addJavaInterface(String param, WrEnv env) {
//    	securityCheck(env);
//
//		if ( hidden instanceof ObjectDec ) {
//			final ObjectDec proto = (ObjectDec) hidden;
//			return proto.addJavaInterface(param);
//		}
//		return false;
//	}


	/**
	 * return the first symbol of this prototype
	   @param env
	   @return
	 */
	public WrSymbol getFirstSymbol(WrEnv env) {
    	securityCheck(env);

		return ((ProgramUnit ) hidden).getFirstSymbol().getI();
	}


	/**
	 * return the superprototype
	   @return
	 */
	public WrProgramUnit getSuperobject(WrEnv env) {

    	checkDependenteProgramUnit(env);
		if ( hidden instanceof ObjectDec ) {
			ProgramUnit pu = ((ObjectDec ) hidden).getSuperobject();
			return pu == null ? null : pu.getI();
		}
		return null;
	}

	/**
	 * return the list of interfaces that this prototype implements
	   @return
	 */
	public List<WrProgramUnit> getInterfaceList(WrEnv env) {
    	checkDependenteProgramUnit(env);

		if ( hidden instanceof ObjectDec ) {
			if ( iExprInterfaceList == null ) {
				iExprInterfaceList = new ArrayList<>();
				final List<Expr> iList = ((ObjectDec ) hidden).getInterfaceList();
				if ( iList != null ) {
					for ( final Expr e : iList ) {
						this.iExprInterfaceList.add( (WrProgramUnit ) e.getType().getI());
					}
				}
			}
			return iExprInterfaceList;
		}
		return null;
	}


	/**
	 * return the list of method signatures found in a search in the public and package
	 * methods of this prototype and its super-prototypes.
	 */
	@Override
	public List<WrMethodSignature> searchMethodPublicPackageSuperPublicPackage(
			String methodName, WrEnv env) {

		boolean packageView = env.getCurrentCompilationUnit().getPackageName().equals(
				((ProgramUnit ) hidden).getCompilationUnit().getPackageName());

		List<MethodSignature> fromList;
		if ( packageView ) {
			fromList =  ((ProgramUnit ) hidden)
					.searchMethodPublicPackageSuperPublicPackage(methodName, env.hidden);
		}
		else  {
			if ( this.isObjectDec() ) {
				fromList =  ((ObjectDec ) hidden) .searchMethodPublicSuperPublicProto(methodName, env.hidden);
			}
			else {
				fromList =  ((InterfaceDec ) hidden).searchMethodPublicSuperPublicOnlyInterfaces(methodName, env.hidden);
			}

		}
    	checkDependenteProgramUnit(env);

		if ( fromList == null ) {
			return null;
		}
		else {
			List<WrMethodSignature> toList = new ArrayList<>();
			for ( MethodSignature from : fromList ) {
				toList.add( from.getI() );
			}
			return toList;
		}
	}

	public List<WrMethodSignature> searchMethodPublicSuperPublicProto(
			String methodName, WrEnv env) {

		if ( this.isInterface() ) { return null; }

		List<MethodSignature> fromList =  ((ObjectDec ) hidden)
				.searchMethodPublicSuperPublicProto(methodName, env.hidden);

    	checkDependenteProgramUnit(env);

		if ( fromList == null ) {
			return null;
		}
		else {
			List<WrMethodSignature> toList = new ArrayList<>();
			for ( MethodSignature from : fromList ) {
				toList.add( from.getI() );
			}
			return toList;
		}

	}

	List<WrProgramUnit> iExprInterfaceList = null;



	/**
	 * return the list of method signatures found in a search in the protected, public, and package
	 * methods of this prototype and its super-prototypes.
	 */

	public List<WrMethodSignature> searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
			String methodName, WrEnv env) {

    	if ( env.getCurrentProgramUnit() != this ) {
    		WrProgramUnit wrpu = env.getCurrentProgramUnit();
    		if ( !this.isSupertypeOf(wrpu, env) ) {
        		throw new MetaSecurityException();
    		}
    	}
    	checkDependenteProgramUnit(env);

		//return ((ProgramUnit ) hidden).searchMethodProtectedPublicPackageSuperProtectedPublicPackage(methodName, ienv.hidden);


		final List<MethodSignature> msList = ((ProgramUnit ) hidden).searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
				methodName, env.hidden);
		if ( msList != null ) {
			final List<WrMethodSignature> imsList = new ArrayList<>();
			for ( final MethodSignature ms : msList ) {
				imsList.add(ms.getI());
			}
			return imsList;
		}
		else {
			return null;
		}

	}

	/**
	 * return true if the program unit is abstract
	   @return
	 */
	public boolean getIsAbstract(WrEnv env) {
    	checkDependenteProgramUnit(env);

		if ( hidden instanceof ObjectDec ) {
			return ((ObjectDec ) hidden).getIsAbstract();
		}
		return false;
	}

	List<WrProgramUnit> allSuperPrototypes;

	/**
	 * return a list of all direct and indirect super prototypes of
	 * this program unit.
	   @return
	 */
	public List<WrProgramUnit> getAllSuperPrototypes(WrEnv env) {
		if ( allSuperPrototypes == null ) {
			List<ProgramUnit> fromList = ((ProgramUnit ) hidden).getAllSuperPrototypes();
			if ( fromList == null ) {
				return null;
			}
			else {
				List<WrProgramUnit> superList = new ArrayList<>();
				for ( ProgramUnit from : fromList ) {
					superList.add(from.getI());
				}
				allSuperPrototypes = Collections.unmodifiableList(superList);
			}
		}
		checkDependenteProgramUnit(env);
		return allSuperPrototypes;
	}


	/**
	   @param env
	 */
	private void checkDependenteProgramUnit(WrEnv env) {
		if ( env.getCurrentProgramUnit() != null ) {
			Type cpu = env.getCurrentProgramUnit().hidden;
			if ( cpu instanceof ProgramUnit ) {
				ProgramUnit pu = (ProgramUnit ) cpu;
				if ( pu != this.hidden ) {
					pu.addDependentProgramUnit( (ProgramUnit ) this.hidden);
				}
			}
		}
	}


	List<WrMethodSignature> iMethodSignatureList = null;
	List<WrMethodSignature> iPublicMethodSignatureList = null;
	List<WrMethodSignature> iPackageMethodSignatureList = null;
	boolean thisMethod_wasNeverCalled = true;

	/**
	 * If this program unit is an interface, return a list of all of its method
	 * signatures. All of them are public and therefore parameter env is not used.
	 *
	 * If this program unit is not an interface, return null
	 *
	   @param env
	   @return
	 */
	public List<WrMethodSignature> getMethodSignatureList(WrEnv env) {

    	checkDependenteProgramUnit(env);

		if ( thisMethod_wasNeverCalled ) {
			thisMethod_wasNeverCalled = false;

			if ( ! (hidden instanceof InterfaceDec) ) {
				return null;
			}
			List<MethodSignature> fromList = ((InterfaceDec ) hidden).getMethodSignatureList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iMethodSignatureList = null;
			}
			else {

				iMethodSignatureList = new ArrayList<>();

				// all methods
				for ( MethodSignature from : fromList ) {
					iMethodSignatureList.add( from.getI() );
				}

				return iMethodSignatureList;
			}


		}
		return iMethodSignatureList;
	}


	/**
	 * return the list of method signatures found in a search in the private, protected, and public
	 * methods of this prototype only. Methods of superprototypes are not considered.
	 * @param methodName with the keywords and number of parameters as <code>"with:2 do:1"</code>
	 */

	public List<WrMethodSignature> searchMethodPrivateProtectedPublic(
			String methodName, WrEnv env) {

    	securityCheck(env);
    	checkDependenteProgramUnit(env);

		if ( hidden instanceof ObjectDec ) {
			List<MethodSignature> fromList =  ((ObjectDec ) hidden).searchMethodPrivateProtectedPublic(methodName) ;
			if ( fromList == null ) {
				return null;
			}
			else {
				List<WrMethodSignature> toList = new ArrayList<>();
				for ( MethodSignature from : fromList ) {
					toList.add( from.getI() );
				}
				return toList;
			}
		}
		else {
			return null;
		}
	}

	/**
	 * return the list of method signatures found in a search in the private, protected, package,
	 * and public methods of this prototype and protected, package, and public methods
	 * of superprototypes.
	 * If the program unit is an interface, return null
	 * @param methodName with the keywords and number of parameters as <code>"with:2 do:1"</code>
	 */

	public List<WrMethodSignature> searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
			String methodNameGet, WrEnv env) {

    	securityCheck(env);
    	checkDependenteProgramUnit(env);

		if ( hidden instanceof ObjectDec ) {
//			return CastList.fromTo( hidden
//					.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodNameGet, env.hidden) );

			List<MethodSignature> fromList =  hidden
					.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodNameGet, env.hidden) ;
			if ( fromList == null ) {
				return null;
			}
			else {
				List<WrMethodSignature> toList = new ArrayList<>();
				for ( MethodSignature from : fromList ) {
					toList.add( from.getI() );
				}
				return toList;
			}

		}
		else {
			return null;
		}
	}

	/**
	 * If this program unit is an interface, return null. Otherwise,
	 * return the method found in a search in the private methods of the prototype. Return
	 * null if no method was found.
	 * @param methodName with the keywords and number of parameters as <code>"with:2 do:1"</code>
	 */

	public WrMethodDec searchMethodPrivate(String methodName, WrEnv env) {
    	securityCheck(env);

		if ( hidden instanceof ObjectDec ) {
			MethodDec m = ((ObjectDec ) hidden).searchMethodPrivate(methodName);
			return m == null ? null : m.getI();
		}
		return null;
	}

	/**
	 * return null if this program unit is an interface. Otherwise, return the field
	 * with name 'fieldName', null if none.
	 *
	   @param fieldName
	   @param env
	   @return
	 */
	public WrFieldDec searchField(java.lang.String fieldName, WrEnv env) {

    	securityCheck(env);

		if ( hidden instanceof ObjectDec ) {
			FieldDec f = ((ObjectDec ) hidden).searchField(fieldName);
			return f == null ? null : f.getI();
		}
		return null;
	}

	/**
	 * return the Java name of this prototype. This method is only for
	 * very specialized users.
	 */
	@Override
	public String getJavaName() {
		return hidden.getJavaName();
	}



}

