/**
 *
 */
package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import cyan.lang.CyInt;
import cyan.lang.CyString;
import cyan.lang._Array_LT_GP_CyString_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT__GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT__GT__GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP__meta_p_WrAnnotation_GP__Array_LT_GP__meta_p_ISlotInterface_GT__GT__GT;
import cyan.lang._Array_LT_GP__meta_p_ISlotInterface_GT;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT__GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT__GT;
import cyan.lang._Tuple_LT_GP__meta_p_WrAnnotation_GP__Array_LT_GP__meta_p_ISlotInterface_GT__GT;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._IActionNewPrototypes__afti;
import cyan.reflect._IAction__afti;
import cyan.reflect._ICheckDeclaration__afsa;
import cyan.reflect._ICheckOverride__afsa;
import cyan.reflect._ICheckProgramUnit__bsa;
import cyan.reflect._ICheckSubprototype__afsa;
import cyan.reflect._ICommunicateInPrototype__afti__dsa__afsa;
import error.ErrorKind;
import error.UnitError;
import lexer.CompilerPhase;
import lexer.Lexer;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.IActionNewPrototypes_afti;
import meta.IAction_afti;
import meta.IAction_cge;
import meta.IAction_dpp;
import meta.ICheckDeclaration_afsa;
import meta.ICheckOverride_afsa;
import meta.ICheckProgramUnit_bsa;
import meta.ICheckSubprototype_afsa;
import meta.ICommunicateInPrototype_afti_dsa_afsa;
import meta.ICompiler_afti;
import meta.ICompiler_dsa;
import meta.ISlotInterface;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrExprAnyLiteral;
import meta.WrMethodSignature;
import meta.WrProgramUnit;
import saci.Compiler;
import saci.CompilerManager;
import saci.CompilerManager_afti;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;


/**
 * This class is a superclass of ObjectDec and InterfaceDec
 * @author José
 *
 */
public abstract class ProgramUnit extends Type implements Declaration, ASTNode, Cloneable {


	public ProgramUnit(Token visibility, List<AnnotationAt> nonAttachedMetaobjectAnnotationList,
			List<AnnotationAt> attachedMetaobjectAnnotationList,  ObjectDec	outerObject) {

		ProgramUnit.initProgramUnit(this, visibility, nonAttachedMetaobjectAnnotationList, attachedMetaobjectAnnotationList, outerObject);
//        this.visibility = visibility;
//        this.nonAttachedMetaobjectAnnotationList = nonAttachedMetaobjectAnnotationList;
//        this.attachedMetaobjectAnnotationList = attachedMetaobjectAnnotationList;
//        this.genericParameterListList = new ArrayList<List<ast.GenericParameter>>();
//        this.outerObject = outerObject;
//
//        // genericProtoInstantiationList = new ArrayList<ExprGenericPrototypeInstantiation>();
//        this.prototypeIsNotGeneric = true;
//        this.completeMetaobjectAnnotationList = new ArrayList<Annotation>();
//        this.genericPrototype = false;
//        this.metaobjectAnnotationNumber = Annotation.firstMetaobjectAnnotationNumber;
//        this.moListBeforeExtendsMixinImplements = null;
//
//        this.messageSendWithkeywordsToSuperList = new ArrayList<>() ;
//        this.nextFunctionNumber = 0;
//        this.innerPrototypeList = new ArrayList<ObjectDec>();
//        this.beforeEndNonAttachedMetaobjectAnnotationList = new ArrayList<>();
//        this.dslCompilationUnit = null;
//        this.immutable = false;
//        this.iProgramUnit = null;
//        this.this_and_all_superPrototypes = null;
	}

    public ProgramUnit() { }

    public static void initProgramUnit(
    			ProgramUnit newPU,
    		    Token visibility,
                List<AnnotationAt> nonAttachedMetaobjectAnnotationList,
                List<AnnotationAt> attachedMetaobjectAnnotationList,  ObjectDec  outerObject) {

        newPU.visibility = visibility;
        newPU.nonAttachedMetaobjectAnnotationList = nonAttachedMetaobjectAnnotationList;
        newPU.attachedMetaobjectAnnotationList = attachedMetaobjectAnnotationList;
        newPU.genericParameterListList = new ArrayList<List<ast.GenericParameter>>();
        newPU.outerObject = outerObject;

        // genericProtoInstantiationList = new ArrayList<ExprGenericPrototypeInstantiation>();
        newPU.prototypeIsNotGeneric = true;
        newPU.completeMetaobjectAnnotationList = new ArrayList<Annotation>();
        newPU.genericPrototype = false;
        newPU.metaobjectAnnotationNumber = Annotation.firstMetaobjectAnnotationNumber;
        newPU.moListBeforeExtendsMixinImplements = null;

        newPU.messageSendWithkeywordsToSuperList = new ArrayList<>() ;
        newPU.nextFunctionNumber = 0;
        newPU.innerPrototypeList = new ArrayList<ObjectDec>();
        newPU.beforeEndNonAttachedMetaobjectAnnotationList = new ArrayList<>();
        newPU.dslCompilationUnit = null;
        newPU.immutable = false;
        newPU.iProgramUnit = null;
        newPU.this_and_all_superPrototypes = null;
    }

	@Override
	public ProgramUnit clone() {

		try {
		    return (ProgramUnit ) super.clone();
		}
		catch ( CloneNotSupportedException e ) {
			return null;
		}
	}



	@Override
	public int hashCode() {
		return Objects.hash(this.getCompilationUnit().getPackageName(), this.getName());
	}

	public List<AnnotationAt> getAttachedMetaobjectAnnotationList() {
		return attachedMetaobjectAnnotationList;
	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}


	public void genCyan(PWInterface pw, CyanEnv cyanEnv, boolean genFunctions) {
		if ( nonAttachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt c : nonAttachedMetaobjectAnnotationList )
				c.genCyan(pw, true, cyanEnv, genFunctions);
		}
		if ( attachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt c : attachedMetaobjectAnnotationList )
				c.genCyan(pw, true, cyanEnv, genFunctions);
		}
	}

	/**
	 * prints in pw the program unit name in Cyan. It is  Person if
	 * the prototype name is "Person" and "Stack<Person>" if
	 * the prototype is generic and it is being instantiated with
	 * a real parameter "Person"
	 * @param pw
	 * @param cyanEnv
	 */
	protected void genCyanProgramUnitName(PWInterface pw, CyanEnv cyanEnv) {

		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			// this is a generic object instantiation. Then genCyan was called to create a
			// prototype Stack<Int> because there is a "Stack<Int>" use in the program
			//pw.print(cyanEnv.getPrototypeName());
		    pw.print(symbol.getSymbolString());

			//if ( genericParameterListList.get(0).get(0).getPlus() ) {

		    if ( genericParameterListList.size() > 0 ) {
				/**
				 * generic prototype with varying number of parameters
				 */
		    	for ( List<String> realParamList : cyanEnv.getRealParamListList() ) {
					pw.print("<");
			    	int size = realParamList.size();
			    	if ( size > 0 ) {
			    		for ( String realParam : realParamList ) {

			    			pw.print(Lexer.addSpaceAfterComma(realParam));
							--size;
							if ( size > 0 )
								pw.print(", ");
			    		}
			    	}
			    	else {
			    		pw.print(MetaHelper.noneArgumentNameForFunctions);
			    	}
		    		pw.print(">");

		    	}
			}
			else {
				cyanEnv.error("Internal error at ProgramUnit::genCyan");
			}


		}
		else {
		    pw.print(symbol.getSymbolString());
			if ( genericParameterListList.size() > 0  ) {
				for ( List<GenericParameter> gtList : genericParameterListList ) {
					pw.print("<");
					int size = gtList.size();
					for ( GenericParameter p : gtList ) {
						p.genCyan(pw, false, cyanEnv, true);
						--size;
						if ( size > 0 )
							pw.print(", ");
					}
					pw.print(">");
				}
			}
		}


	}

	abstract public void genJava(PWInterface pw, Env env);
	/**
	 * returns the field of this prototype whose name is varName
	 */
	abstract public FieldDec searchFieldDec(String varName);

	abstract public FieldDec searchFieldPrivateProtectedSuperProtected(String varName);
	abstract public FieldDec searchFieldDecProtected(String varName);


	/**
	 * return the field whose name is "name". null if not found
	 */
	abstract public FieldDec searchField(String name);


	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	public Symbol getSymbol() {
		return symbol;
	}


	public List<List<GenericParameter>> getGenericParameterListList() {
		return genericParameterListList;
	}

	public void addGenericParameterList(
			List<GenericParameter> genericParameterList) {
		genericParameterListList.add(genericParameterList);
	}

	/**
	 *  return true if this program unit is generic like "object Stack{@literal <}T> ... end".
	 *  Return false otherwise, including if this program unit declares an
	 *  instantiation of a generic prototype like "object Stack{@literal <}Int> ... end".
	 * @return
	 */
	public boolean isGeneric() {
		return ! getPrototypeIsNotGeneric() && genericParameterListList.size() > 0;
	}


	public void setVisibility(Token visibility) {
		this.visibility = visibility;
	}

	public Token getVisibility() {
		return visibility;
	}

	@Override
	public String getFullName() {
		return getCompilationUnit().getPackageIdent().getName() + "." + getName();
	}


	@Override
	public String getFullName(Env env) {

		if ( this.fullName == null ) {
			String realName = symbol.getSymbolString();

			if ( genericParameterListList.size() > 0  ) {
				for ( List<GenericParameter> gtList : genericParameterListList ) {
					realName = realName + "<";
					int size = gtList.size();
					for ( GenericParameter p : gtList ) {
						String s = p.getFullName(env);
						if ( s == null )
							env.error(p.getParameter().getFirstSymbol(), "Type was not found: '" + p.getName() + "'");
						realName = realName + s;
						--size;
						if ( size > 0 )
							realName = realName + ",";
					}
					realName = realName + ">";
				}
			}
			CyanPackage cyanPackage = this.getCompilationUnit().getCyanPackage();
			if ( cyanPackage.getPackageName().equals(MetaHelper.cyanLanguagePackageName) )
				fullName = realName;
			else
				fullName = cyanPackage.getPackageName() + "." + realName;
		}
		return fullName;
	}

	/**
	 * return the name of the program unit. If it is a generic prototype, return the name without the parameters.
	 * Then if the program unit is <code>Stack{@literal <}Int></code>, this method returns <code>Stack</code>.
	   @return
	 */
	public String getSimpleName() {
		return this.symbol.getSymbolString();
	}


	/**
	 * returns the prototype name. If it is "Person", "Person" is returned. If it is a generic prototype
	 * "Stack{@literal <}T>", "Stack{@literal <}T>" is returned. If it is an instantiated generic prototype "Stack{@literal <}main.Person>",
	 * "Stack{@literal <}main.Person>" is returned.
	 * <br>
	 * This method should not be called during semantic analysis because it does not give the correct name, with the packages.
	 * @return
	 */
	@Override
	public String getName() {
		if ( realName == null ) {
			realName = symbol.getSymbolString();

			if ( genericParameterListList.size() > 0  ) {
				for ( List<GenericParameter> gtList : genericParameterListList ) {
					realName = realName + "<";
					int size = gtList.size();
					for ( GenericParameter p : gtList ) {
						realName = realName + p.getName();
						--size;
						if ( size > 0 )
							realName = realName + ",";
					}
					realName = realName + ">";
				}
			}
		}
		return realName;
	}


	@Override
	public String getPackageName() {
		return this.getCompilationUnit().getPackageName();
	}
	/**
	 * return the name of this compilation unit preceded by its outer prototype, if any. The  inner
	 * and outer prototypes are separated by '.'	 */
	public String getNameWithOuter() {
		String protoName = getName();
		if ( getOuterObject() != null ) {
			protoName = getOuterObject().getName() + "." + protoName;
		}
		return protoName;
	}

	/** Assuming that this program unit is public, this method returns
	 * the name of the source file, without ".cyan", in which this
	 * program unit should be. Package cyan.lang is never used in the
	 * source file name.
	 *
	 *
	 * Examples:
	 *
	 *      object Test<Int, main.Person> ... end  returns "Test(Int,main.Person).cyan"
	 *      object Test<cyan.lang.Int, main.Person> ... end  returns "Test(Int,main.Person).cyan"
	 *      object Test<cyan.lang.Tuple<cyan.lang.Int>,
	 *                  util.Stack<cyan.lang.Tuple<cyan.lang.Int, main.Person>>> ... end
	 *      returns "Test(Tuple(Int),util.Stack(Tuple(Int,main.Person))).cyan"
	 *      object Test<Int, main.Person> ... end  returns "Test(Int,main.Person).cyan"
	 *      object Test<Int, main.Person> ... end  returns "Test(Int,main.Person).cyan"
	 *      object Test<Int, main.Person> ... end  returns "Test(Int,main.Person).cyan"
	 * @return
	 */

	//# stopped here.

	public String getNameSourceFile() {
		String realName = symbol.getSymbolString();

		if ( genericParameterListList.size() > 0 ) {
			for ( List<GenericParameter> gtList : genericParameterListList ) {
				int size = gtList.size();

				if ( gtList.get(0).isRealPrototype() ) {
					realName = realName + "(";
					for ( GenericParameter p : gtList ) {
						realName = realName + p.getNameSourceFile();
						--size;
						if ( size > 0 )
							realName = realName + ",";
					}
					realName = realName + ")";
				}
				else {
					realName = realName + "(" + gtList.size();
					if ( gtList.get(0).getPlus() )
						realName = realName + "+";
					realName = realName + ")";
				}

				/*
				String prototypeName = prototypeNameFromNameSourceFile(gtList.get(0).getNameSourceFile());
				if ( compilationUnit.getProgram().isInPackageCyanLang(prototypeName) ||
						prototypeName.contains(".") || Character.isLowerCase(prototypeName.charAt(0))) {
					// the first parameter is either a prototype of package cyan.lang or a prototype with its package as "main.Person"
					realName = realName + "(";
					for ( GenericParameter p : gtList ) {
						String s = prototypeNameFromNameSourceFile(p.getNameSourceFile());
						if ( compilationUnit.getProgram().isInPackageCyanLang(s) || s.contains(".") ) {
							realName = realName + p.getNameSourceFile();
						}
						else {
							// a symbol such as key in Tuple<key, String, value, Int>
							realName = realName + s;
						}
						--size;
						if ( size > 0 )
							realName = realName + ",";
					}
					realName = realName + ")";

				}
				else {
					realName = realName + "(" + gtList.size() + ")";
				} */
			}

		}

		/*
		if ( isGeneric() ) {
			if ( genericParameterListList.size() > 0  ) {
				for ( List<GenericParameter> gtList : genericParameterListList ) {
					realName = realName + "(" + gtList.size() + ")";
				}
			}
		}
		else {
			if ( genericParameterListList.size() > 0  ) {
				// this is a generic object instantiation. Then genCyan was called to create a
				// prototype Stack<Int> because there is a "Stack<Int>" use in the program
				for ( List<GenericParameter> gtList : genericParameterListList ) {
					realName = realName + "(";
					int size = gtList.size();
					for ( GenericParameter p : gtList ) {
						realName = realName + p.getNameSourceFile();
						--size;
						if ( size > 0 )
							realName = realName + ",";
					}
					realName = realName + ")";
				}
			}

		} */
		return realName;
	}


	@Override
	public String getJavaName() {

		if ( javaName == null ) {
			String name1 = getName();
			if ( this.compilationUnit.getPackageName().equals(MetaHelper.cyanLanguagePackageName) ) {
				if ( this instanceof InterfaceDec ) {
					name1 = NameServer.prototypeFileNameFromInterfaceFileName(name1);
				}
				javaName =  MetaHelper.getJavaName(name1);
			}
			else if ( this.outerObject == null ) {
				if ( this instanceof InterfaceDec ) {
					name1 = NameServer.prototypeFileNameFromInterfaceFileName(name1);
				}
				javaName = compilationUnit.getPackageName() + "." +  MetaHelper.getJavaName(name1);
				/*
				if ( this instanceof InterfaceDec ) {
					javaName = compilationUnit.getPackageName() + "." +  NameServer.javaPrototypeFileNameFromInterfaceFileName(NameServer.getJavaName(name1));
				}
				else {
					javaName = compilationUnit.getPackageName() + "." +  NameServer.getJavaName(name1);
				}
				*/
			}
			else {
				javaName = MetaHelper.getJavaName(name1);
			}

			// javaPrototypeFileNameFromInterfaceFileName
		}
		return javaName;
	}

	public String getJavaNameWithoutPackage() {
		if ( javaNameWithoutPackage == null ) {
			javaNameWithoutPackage =  MetaHelper.getJavaName(getName());
		}
		return javaNameWithoutPackage;
	}

	private String javaNameWithoutPackage;


	public Type getType() {
		return this;
	}



	/** calculates the type of all method parameters, all return values of methods of
	 * this program unit, and all fields.
	 * The types depends on the packages imported by the compilation unit
	 * of this program unit
	 * */

	public void calcInternalTypes(ICompiler_dsa compiler_dsa, Env env) {

		if ( nonAttachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : nonAttachedMetaobjectAnnotationList )
				annotation.calcInternalTypes(env);
		}
		WrProgramUnit wrpu = this.getI();
		for ( AnnotationAt annotation : this.getCompilationUnit().getCyanPackage().getAttachedMetaobjectAnnotationList() ) {
			annotation.setDeclaration(wrpu);
			annotation.dsaActions(env);
		}
		for ( AnnotationAt annotation : this.getCompilationUnit().getCyanPackage().getProgram().getAttachedMetaobjectAnnotationList() ) {
			annotation.setDeclaration(wrpu);
			annotation.dsaActions(env);
		}
		if ( this.getPrototypePackageProgramAnnotationList().size() > 0 ) {


//			for ( AnnotationAt packAnnot : this.getCompilationUnit().getCyanPackage().getAttachedMetaobjectAnnotationList() ) {
//				packAnnot.setDeclaration(this.getI());
//			}
//			metaobjectAnnotationListPU_Package_Program.addAll(
//					this.getCompilationUnit().getCyanPackage().getAttachedMetaobjectAnnotationList());
//
//			for ( AnnotationAt packAnnot :
//				   this.getCompilationUnit().getCyanPackage().getProgram().getAttachedMetaobjectAnnotationList() ) {
//				packAnnot.setDeclaration(this.getI());
//			}
			if ( attachedMetaobjectAnnotationList != null ) {
				for ( AnnotationAt annotation : attachedMetaobjectAnnotationList ) {
					annotation.calcInternalTypes(env);
				}

			}

			ObjectDec objDec = null;
			if ( this instanceof ObjectDec ) {
				objDec = (ObjectDec ) this;

				//compiler_dsa.getEnv()
				meta.GetHiddenItem.getHiddenEnv(compiler_dsa.getEnv()).atBeginningOfObjectDec(objDec);
			}
			List<Annotation> annotList = this.getPrototypePackageProgramAnnotationList();
			this.setDeclarationImportedFromPackageProgram();
			for ( Annotation annotation : annotList ) {

				CyanMetaobject metaobject = annotation.getCyanMetaobject();
				_CyanMetaobject other = metaobject.getMetaobjectInCyan();
				// // metaobject.setMetaobjectAnnotation(annotation, 0);
				if ( metaobject instanceof ICheckProgramUnit_bsa ||
						(other != null && other instanceof _ICheckProgramUnit__bsa) ) {


					try {
						if ( other == null ) {
							ICheckProgramUnit_bsa fp = (ICheckProgramUnit_bsa ) metaobject;
							fp.bsa_checkProgramUnit(compiler_dsa);
						}
						else {
							_ICheckProgramUnit__bsa fp = (_ICheckProgramUnit__bsa ) metaobject;
							fp._bsa__checkProgramUnit_1(compiler_dsa);
						}
					}
					catch ( error.CompileErrorException e ) {
					}
					catch ( NoClassDefFoundError e ) {
						env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( RuntimeException e ) {
						env.thrownException(annotation, annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobjectCatchExceptions(metaobject);
					}


				}

			}
			if ( objDec != null ) {
				//compiler_dsa.getEnv()
				meta.GetHiddenItem.getHiddenEnv(compiler_dsa.getEnv()).atEndOfObjectDec();
			}

		}


		/*
		if ( beforeEndNonAttachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : beforeEndNonAttachedMetaobjectAnnotationList )
				annotation.calcInternalTypes(env);
		}
		*/

	}

	public void calcInternalTypesNONAttachedAnnotations(Env env) {
		if ( gpiList != null ) {
			for ( ExprGenericPrototypeInstantiation elem : gpiList ) {
				elem.calcInternalTypes(env);
			}
		}

		if ( nonAttachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : nonAttachedMetaobjectAnnotationList )
				annotation.calcInternalTypes(env);
		}

		if ( this.outerObject == null )
			javaName = MetaHelper.getJavaName(this.getFullName(env));
		else
			javaName = MetaHelper.getJavaName(this.getName());

	}

	public void calcInterfaceTypes(Env env) {

		if ( attachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : attachedMetaobjectAnnotationList )
				annotation.calcInternalTypes(env);
		}


		/*
		String otherName = "";
		if ( this instanceof InterfaceDec ) {
			if ( this.outerObject == null )
				otherName = NameServer.getJavaName(this.getFullName(env));
			else
				otherName = NameServer.getJavaName(this.getName());

		}

		// just to set javaName
		this.getJavaName();
		*/
	}


	public boolean getPrototypeIsNotGeneric() {
		return prototypeIsNotGeneric;
	}

	public void setPrototypeIsNotGeneric(boolean hasPrototypeInstantiation) {
		this.prototypeIsNotGeneric = hasPrototypeInstantiation;

	}

	public void addMetaobjectAnnotation(Annotation annotation) {
		if ( this.completeMetaobjectAnnotationList == null )
			completeMetaobjectAnnotationList = new ArrayList<Annotation>();
		completeMetaobjectAnnotationList.add(annotation);
	}

	/**
	 * a list of all metaobject annotations inside and before this program unit.
	 * This list includes attachedMetaobjectAnnotationList and beforeEndNonAttachedMetaobjectAnnotationList.
	 * Every metaobject annotation inside the program unit is in this list.
	 */
	public List<Annotation> getCompleteMetaobjectAnnotationList() {
		return completeMetaobjectAnnotationList;
	}

	public List<Annotation> getPrototypePackageProgramAnnotationList() {
		if ( prototypePackageProgramAnnotationList == null ) {
			prototypePackageProgramAnnotationList = new ArrayList<>();
			prototypePackageProgramAnnotationList.addAll(completeMetaobjectAnnotationList);
			List<AnnotationAt> annotList =
					this.getCompilationUnit().getCyanPackage().getAttachedMetaobjectAnnotationList();
			if ( annotList != null ) {
				for (AnnotationAt withAt : annotList ) {
					if ( ! (withAt instanceof IAction_dpp) ) {
						prototypePackageProgramAnnotationList.add(withAt);
					}
				}
				// prototypePackageProgramAnnotationList.addAll( annotList );
			}
			annotList = this.getCompilationUnit().getCyanPackage().getProgram().getAttachedMetaobjectAnnotationList();
			if ( annotList != null ) {
				for (AnnotationAt withAt : annotList ) {
					if ( ! (withAt instanceof IAction_dpp) ) {
						prototypePackageProgramAnnotationList.add(withAt);
					}
				}
				// prototypePackageProgramAnnotationList.addAll(annotList);
			}

		}
		return this.prototypePackageProgramAnnotationList;
	}

	public void setDeclarationImportedFromPackageProgram() {
		List<Annotation> annotList = this.getPrototypePackageProgramAnnotationList();
		WrProgramUnit wrpu = this.getI();

		for ( Annotation withAtAnnot : annotList ) {
			if ( withAtAnnot instanceof AnnotationAt
					&& ((AnnotationAt) withAtAnnot).getOriginalDeclaration() != null )  {
				((AnnotationAt ) withAtAnnot).setDeclaration(wrpu);
			}
		}
	}

	public void afsa_checkDeclaration(ICompiler_dsa compiler_afti, Env env) {


		// this.checkInheritance_afsa(env);

		List<Annotation> annotList = this.getPrototypePackageProgramAnnotationList();
		this.setDeclarationImportedFromPackageProgram();


		for ( Annotation annotation : annotList ) {

			CyanMetaobject metaobject = annotation.getCyanMetaobject();
			_CyanMetaobject other = metaobject.getMetaobjectInCyan();

			// // metaobject.setMetaobjectAnnotation(annotation, 0);
			if ( metaobject instanceof ICheckDeclaration_afsa ||
					(other != null && other instanceof _ICheckDeclaration__afsa) ) {
				ICheckDeclaration_afsa fp = (ICheckDeclaration_afsa ) metaobject;


				if ( annotation instanceof AnnotationAt ) {
					if (((AnnotationAt) annotation).getI() == null ) {
						env.error(annotation.getFirstSymbol(),
								"Metaobject '" + metaobject.getName() +
								"' should be attached to something like a method or prototype because it "
								+ "implements interface '" + ICheckDeclaration_afsa.class.getName() + "'");
					}
				}


				try {
					if ( other == null ) {
						fp.afsa_checkDeclaration(compiler_afti);
					}
					else {
						((_ICheckDeclaration__afsa ) other)._afsa__checkDeclaration_1(compiler_afti);
					}
				}
				catch ( error.CompileErrorException e ) {
				}
				catch ( NoClassDefFoundError e ) {
					env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
				}
				catch ( RuntimeException e ) {
					//e.printStackTrace();
					env.thrownException(annotation, annotation.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(metaobject);
				}


			}

		}

	}


	public void afsa_check(ICompiler_dsa compiler_dsa, Env env ) {


		List<Annotation> puMetaobjectAnnotationList = new ArrayList<>();
		if ( completeMetaobjectAnnotationList != null )
			puMetaobjectAnnotationList.addAll(completeMetaobjectAnnotationList);
		if ( nonAttachedMetaobjectAnnotationList != null )
			puMetaobjectAnnotationList.addAll(nonAttachedMetaobjectAnnotationList);

		env.atBeginningOfObjectDec(this);

		/**
		 * Meta: call method checkSubprototype for each metaobject that implements interface
		 * ICheckSubprototype_afsa
		 */
		this.checkInheritance_afsa(compiler_dsa, env);

		this.setDeclarationImportedFromPackageProgram();



		if ( this instanceof ObjectDec ) {
			ObjectDec proto = (ObjectDec ) this;
			for ( MethodDec method : proto.getMethodDecList() ) {
				//meta.GetHiddenItem.getHiddenEnv(compiler_afti.getEnv()).atBeginningOfCurrentMethod(method);
				if ( method.getVisibility() == Token.PUBLIC && method.getHasOverride() ) {
					env.setCurrentMethod(method);
					String methodName = method.getName();
					List<MethodSignature> superMS = new ArrayList<>();
					superMS.addAll(	proto.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodName, env) );

					for ( Expr interfaceExpr :  proto.getInterfaceList() ) {
						InterfaceDec interDec = (InterfaceDec ) interfaceExpr.getType(env);
						List<MethodSignature> msL = interDec.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodName, env);
						if ( msL != null && msL.size() > 0 ) {
							superMS.addAll(msL);
						}
					}
					for ( MethodSignature ms : superMS ) {
						List<AnnotationAt> superMSAttachedAnnotationList;
						MethodDec superMethod = ms.getMethod();
						if ( superMethod == null ) {
							/*
							 * method of an interface implemented by 'this' or 'proto' (they are equal)
							 */
							superMSAttachedAnnotationList = ms.getAttachedMetaobjectAnnotationList();
						}
						else {
							/*
							 * method of a superprototype of 'this' or 'proto'
							 */
							superMSAttachedAnnotationList = superMethod.getAttachedMetaobjectAnnotationList();
						}
						if ( superMSAttachedAnnotationList != null ) {
							for ( AnnotationAt annotation : superMSAttachedAnnotationList ) {
								CyanMetaobject metaobject = annotation.getCyanMetaobject();
								// // metaobject.setMetaobjectAnnotation(annotation, 0);
								_CyanMetaobject other = metaobject.getMetaobjectInCyan();
								if ( metaobject instanceof ICheckOverride_afsa ||
										(other != null && other instanceof _ICheckOverride__afsa) ) {


									try {
										if ( other == null ) {
											ICheckOverride_afsa fp = (ICheckOverride_afsa ) metaobject;
											fp.afsa_checkOverride(compiler_dsa, method.getI());
										}
										else {
											_ICheckOverride__afsa fp = (_ICheckOverride__afsa ) metaobject;
											fp._afsa__checkOverride_2(compiler_dsa, method.getI());
										}
									}
									catch ( error.CompileErrorException e ) {
									}
									catch ( NoClassDefFoundError e ) {
										env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
									}
									catch ( meta.InterpretationErrorException e ) {
										//metaobject.addError(e.symbol, e.message);
									}
									catch ( RuntimeException e ) {
										env.atEndOfObjectDec();
										env.thrownException(annotation, annotation.getFirstSymbol(), e);
									}
									finally {
										env.errorInMetaobject(metaobject, ms.getFirstSymbol());
									}
								}
							}
						}
					}
				}
				//meta.GetHiddenItem.getHiddenEnv(compiler_afti.getEnv()).atEndMethodDec();

			}
		}

		env.atEndOfObjectDec();

	}




	/**
	 * make the metaobject annotations "that acts" in this program unit communicate with each other
	 * any prototype whose name has a '<' in it, as <code>Set{@literal <}Int></code>,
	 * cannot communicate with other prototypes. The annotations "that act" in this program unit
	 * are those that are textually in this program unit and those attached to the package
	 * or the program unit and those of the program that implement interfaces
	 * {@link meta.IAction_afti} or {@link meta.IActionNewPrototypes_afti}
	 */
	protected void makeMetaobjectAnnotationsCommunicateInPrototype(
			List<Annotation> metaobjectAnnotationList, Env env) {


		/*
		 * every metaobject can supply information to other metaobjects.
		 * Every tuple in this set correspond to a metaobject annotation.
		 * Every tuple is composed of a metaobject name, the number of this metaobject
		 * considering all metaobjects in the prototype, the number of this metaobject
		 * considering only the metaobjects with the same name, and the information
		 * this metaobject annotation wants to share with other metaobject annotations.
		 */
		WrEnv wrEnv = env.getI();
		HashSet<Tuple4<String, Integer, Integer, Object>> moInfoSet = new HashSet<>();
		for ( Annotation annotation : metaobjectAnnotationList ) {
			CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			if ( cyanMetaobject instanceof ICommunicateInPrototype_afti_dsa_afsa ||
					(other != null && other instanceof _ICommunicateInPrototype__afti__dsa__afsa)) {
				// // cyanMetaobject.setMetaobjectAnnotation(annotation, 0);

				Object sharedInfo;
				if ( other == null ) {
					sharedInfo = ((ICommunicateInPrototype_afti_dsa_afsa ) cyanMetaobject).afti_dsa_afsa_shareInfoPrototype(wrEnv);
				}
				else {
					sharedInfo = ((_ICommunicateInPrototype__afti__dsa__afsa ) other)._afti__dsa__afsa__shareInfoPrototype_1(wrEnv);
				}
				if (  sharedInfo != null ) {
					if ( this.genericParameterListList.size() == 0 ) {
						Tuple4<String, Integer, Integer, Object> t = new Tuple4<>(cyanMetaobject.getName(),
								annotation.getMetaobjectAnnotationNumber(), annotation.getMetaobjectAnnotationNumberByKind(),
								sharedInfo);
						moInfoSet.add(t);
					}
					else {
						env.error(true, annotation.getFirstSymbol(),
									"metaobject annotation of metaobject '" +
									           annotation.getCyanMetaobject().getName() + "' is trying to communicate with other metaobjects of the package. " +
											"This is prohibit because this metaobject is a generic prototype instantiation or has a '<' in its name", null, ErrorKind.metaobject_error);
					}

				}

			}
		}
		/*
		 * send information to all annotations of this program unit. Let them communicate with each other
		 */
		if ( moInfoSet.size() > 0 ) {
			for ( Annotation annotation : metaobjectAnnotationList ) {
				CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
				_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();

				if ( cyanMetaobject instanceof ICommunicateInPrototype_afti_dsa_afsa ||
						(other != null && other instanceof _ICommunicateInPrototype__afti__dsa__afsa)) {
					// // cyanMetaobject.setMetaobjectAnnotation(annotation, 0);
					if ( other == null ) {
						((ICommunicateInPrototype_afti_dsa_afsa ) cyanMetaobject).afti_dsa_afsa_receiveInfoPrototype(moInfoSet, wrEnv);
					}
					else {
						_Set_LT_GP__Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT__GT tupleSet =
								new _Set_LT_GP__Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT__GT();

						for ( Tuple4<String, Integer, Integer, Object> elem : moInfoSet ) {
							tupleSet._add_1( new _Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT(
									new CyString(elem.f1), new CyInt(elem.f2), new CyInt(elem.f3),
									elem.f4
									));
						}
						((_ICommunicateInPrototype__afti__dsa__afsa ) other)._afti__dsa__afsa__receiveInfoPrototype_2(
								tupleSet, wrEnv);
					}

				}
			}
		}
	}




//	private void afti_actions(ICompiler_afti compiler_afti, CompilerManager_afti compilerManager) {
//
//		Env env = meta.GetHiddenItem.getHiddenEnv(compiler_afti.getEnv());
//		env.atBeginningOfObjectDec(this);
//
//
//		makeMetaobjectAnnotationsCommunicateInPrototype(env);
//
//
//		/*
//		 * true if this prototype has a {@literal <} in its name
//		 */
//		boolean hasLessThanInName = getName().indexOf('<') >= 0 ;
//
//		List<Annotation> metaobjectAnnotationList = completeMetaobjectAnnotationList;
//
//
//		for ( Annotation cyanMetaobjectAnnotation : metaobjectAnnotationList ) {
//
//			/*
//			 * if the metaobject annotation has a suffix greater or equal to "afti" then the actions below
//			 * have already been taken (the compiler changed the suffix to "afti" or greater) or
//			 * they should not be taken (the original program uses a suffix in the metaobject annotation
//			 * of "afti" or greater).
//			 */
//			if ( cyanMetaobjectAnnotation.getPostfix() == null ||
//					cyanMetaobjectAnnotation.getPostfix().lessThan(CompilerPhase.AFTI) ) {
//
//				addCodeAndSlotsTo(compiler_afti, compilerManager, env, hasLessThanInName, cyanMetaobjectAnnotation);
//
//
//			}
//
//		}
//
//		env.atEndOfObjectDec();
//	}


	public void afti_actions(ICompiler_afti compiler_afti, CompilerManager_afti compilerManager) {

		Env env = meta.GetHiddenItem.getHiddenEnv(compiler_afti.getEnv());
		env.atBeginningOfObjectDec(this);


		List<Annotation> metaobjectAnnotationList =
				this.getPrototypePackageProgramAnnotationList();
		this.setDeclarationImportedFromPackageProgram();

		makeMetaobjectAnnotationsCommunicateInPrototype(metaobjectAnnotationList, env);

		/*
		 * metaobjects may add code before the first statement of methods.
		 * This is a map containing pair of the form [. methodName, value .] in which
		 * some metaobject added code to the beginning of methodName. 'value' is true
		 * if the metaobject demanded exclusive rights for adding code.
		 */
		Map<String, Boolean> mapMethodNameExclusive = new HashMap<>();
//
//		List<Annotation> metaobjectAnnotationList = new ArrayList<>();
//		metaobjectAnnotationList.addAll(completeMetaobjectAnnotationList);
//
//		metaobjectAnnotationList.addAll(
//				this.getCompilationUnit().getCyanPackage().getAttachedMetaobjectAnnotationList_IActionProgramUnit_afti_IActionNewPrototypes_afti());
//
//		metaobjectAnnotationList.addAll(
//				this.getCompilationUnit().getCyanPackage().getProgram().getAttachedMetaobjectAnnotationList_IActionProgramUnit_afti_IActionNewPrototypes_afti());
//
//
//


		// boolean hasLessThanInName = getName().indexOf('<') >= 0 ;



		/**
		 * list of tuples, each composed by an annotation and a list of objects of
		 * WrMethodSignature and WrFieldDec. The metaobject associated to the annotation
		 * produces the methods and fields corresponding to the objects in the list.
		 * This list is passed as parameter to all metaobjects when method
		 * {@link IAction_afti.afti_codeToAdd} is called.
		 */
		List<Tuple2<WrAnnotation, List<ISlotInterface>>> infoList = null;

		/**
         * list of tuples, each consisting of an annotation, code to be added to the prototype (fields
         * and methods) and the list of field declarations and interfaces of methods to be
         * added. This last list can be, for example,<br>
         * <code>
         *     var Int count;
         *     func at: Int n put: String -> Int
         *     func unary
         *     var String output;
         * </code>
         */
        List<Tuple4<Annotation, StringBuffer, List<ISlotInterface>, String>>
        	runManyList = null, runOnceList = null;



		for ( Annotation annot : metaobjectAnnotationList ) {

			/*
			 * if the metaobject annotation has a suffix greater or equal to "afti" then the actions below
			 * have already been taken (the compiler changed the suffix to "afti" or greater) or
			 * they should not be taken (the original program uses a suffix in the metaobject annotation
			 * of "afti" or greater).
			 */
			if ( annot.getPostfix() != null &&
				 !annot.getPostfix().lessThan(CompilerPhase.AFTI) ) {
				continue;
			}



			CyanMetaobject cyanMetaobject = annot.getCyanMetaobject();
			//CyanMetaobjectAtAnnot cyanMetaobjectWithAt = (CyanMetaobjectAtAnnot ) cyanMetaobject;

			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			if ( cyanMetaobject instanceof IActionNewPrototypes_afti ||
					(other != null && other instanceof _IActionNewPrototypes__afti)) {
				CompilerManager.createNewPrototypes(compiler_afti, env, annot, cyanMetaobject, this.compilationUnit);
			}


			if ( cyanMetaobject instanceof IAction_afti  || (other != null && other instanceof _IAction__afti) ) {
				String packageName = this.compilationUnit.getCyanPackage().getPackageName();

				List<Tuple2<String, String []>> renameMethodList = null;
				List<ISlotInterface> slotList = null;



				/**
				 * add code to the current prototype, it may be composed of fields and methods
				 */
				Tuple2<StringBuffer, String> codeSlot = null;
				try {
					if ( other == null ) {
						codeSlot = ((IAction_afti ) cyanMetaobject)
								.afti_codeToAdd(compiler_afti, null);
					}
					else {
//						_Tuple_LT_GP_CyString_GP_CyString_GT t = ((_IAction__afti ) other)
//								._afti__codeToAdd_2(compiler_afti,
//										new _Array_LT_GP__Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT__GT__GT());
						_Tuple_LT_GP_CyString_GP_CyString_GT t = ((_IAction__afti ) other)
								._afti__codeToAdd_2(compiler_afti,
										new _Array_LT_GP__Tuple_LT_GP__meta_p_WrAnnotation_GP__Array_LT_GP__meta_p_ISlotInterface_GT__GT__GT());


						if ( t._f2().s.length() != 0  ) {
							codeSlot = new Tuple2<StringBuffer, String>( new StringBuffer(t._f1().s), t._f2().s);
						}
					}
					if ( codeSlot == null ) {

						if ( cyanMetaobject instanceof IAction_afti )  {
							if ( ((IAction_afti ) cyanMetaobject).runUntilFixedPoint() ) {
								env.error(annot.getFirstSymbol(), "Method to generate code in phase afti of " +
							            "metaobject of annotation '"
										+ cyanMetaobject.getName() + "' returned null. This is illegal because"
												+ " method 'runUntilFixedPoint()' of this metaobject returned true");						}
						else {

							if ( other != null && ((_IAction__afti ) other)._runUntilFixedPoint() ) {
								env.error(annot.getFirstSymbol(), "Method to generate code in phase afti of " +
							            "metaobject of annotation '"
										+ cyanMetaobject.getName() + "' returned null. This is illegal because"
												+ " method 'runUntilFixedPoint()' of this metaobject returned true");						}
							}
						}
					}
					else
					{
						slotList = extractSlotListFrom(codeSlot.f2, annot, env, annot.getFirstSymbol());

						Tuple4<Annotation, StringBuffer, List<ISlotInterface>, String> t =
								new Tuple4<Annotation, StringBuffer, List<ISlotInterface>, String>(
										annot, codeSlot.f1, slotList, codeSlot.f2);
						if ( cyanMetaobject instanceof IAction_afti ) {
							if ( ((IAction_afti ) cyanMetaobject).runUntilFixedPoint() ) {
								if ( runManyList == null ) {
									runManyList = new ArrayList<>();
								}
								runManyList.add(t);
							}
							else {
								if ( runOnceList == null ) {
									runOnceList = new ArrayList<>();
								}
								runOnceList.add(t);
							}
						}
						else if ( other != null ) {

							if ( ((_IAction__afti ) other)._runUntilFixedPoint() ) {
								if ( runManyList == null ) {
									runManyList = new ArrayList<>();
								}
								runManyList.add(t);
							}
							else {
								if ( runOnceList == null ) {
									runOnceList = new ArrayList<>();
								}
								runOnceList.add(t);
							}

						}
						if ( infoList == null ) {
							infoList = new ArrayList<>();
						}
						infoList.add( new Tuple2<WrAnnotation, List<ISlotInterface>>(
								annot.getI(), slotList)
								);
					}
				}
				catch ( error.CompileErrorException e ) {
				}
				catch ( NoClassDefFoundError e ) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
				}
				catch ( RuntimeException e ) {
					env.thrownException(annot, annot.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(cyanMetaobject);
				}

				// boolean canCommunicateInPackage = this.compilationUnit.getCyanPackage().getCommunicateInPackage();

				/**
				 * add statements to the beginning of methods.
				 * This was asked by the metaobjects of this program unit.
				 */
				List<Tuple3<String, StringBuffer, Boolean>> statsList = null;

				try {
					if ( cyanMetaobject instanceof IAction_afti ) {
						statsList = ((IAction_afti ) cyanMetaobject).afti_beforeMethodCodeList(compiler_afti);
					}
					else if ( other != null ) {
						_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT__GT array =
								((_IAction__afti ) other)._afti__beforeMethodCodeList_1( compiler_afti);
						/*
						 * cast Array<Tuple<String, String>> to
						 *      List<Tuple2<String, StringBuffer>>
						 */
						statsList = MetaHelper.cyanArrayTupleStringStringBoolean_toJava(array);
					}
				}
				catch ( error.CompileErrorException e ) {
				}
				catch ( NoClassDefFoundError e ) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
				}
				catch ( RuntimeException e ) {
					env.thrownException(annot, annot.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(cyanMetaobject);
				}


				if ( statsList != null ) {
					for ( Tuple3<String, StringBuffer, Boolean> t : statsList ) {
						String prototypeName = this.getName();
//						if ( hasLessThanInName || prototypeName.indexOf('<') >= 0 ) {
//							if ( ! this.getCompilationUnit().getPackageName().equals(packageName)  ||
//								 ! this.getName().equals(prototypeName) ) {
//								env.error(true,
//										annot.getFirstSymbol(),
//										"This metaobject annotation is trying to code to "
//										+ "a method of a prototype it does not have permission "
//										+ "to. Only a generic prototype can add code to itself "
//										+ "and only to itself", cyanMetaobject.getName(), ErrorKind.metaobject_error);
//							}
//						}
//						String thisPrototypeName = this.getName();
//
//						if ( !canCommunicateInPackage && ! prototypeName.equals(thisPrototypeName) ) {
//							env.error(annot.getFirstSymbol(), "This metaobject annotation is in prototype '" + packageName + "." +
//						          thisPrototypeName + "' and it is trying to add code to another prototype, '" + packageName + "." +
//								          prototypeName + "'. This is illegal because package '" + packageName + "' does not allow that. To make that " +
//									 "legal, attach '@feature(communicateInPackage, #on)' to the package in the project (.pyan) file", true, false);
//						}
//						else {
						Boolean exclusive = mapMethodNameExclusive.get(t.f1);
						if ( exclusive != null && (exclusive || t.f3) ) {
							/* error: a method object demanded exclusive rights to add
							 * code at the beginning of method t.f1 but another metaobject is trying to add
							 * code too
							 *
							 */
							env.error(annot.getFirstSymbol(), "Two metaobjects are trying to add code at the "
									+ "beginning of method '" + t.f1 + "'. However, at least one of them demanded"
											+ " exclusive rights of doing so. This is an error. Either none "
											+ "should ask exclusive rights or only one metaobject should ask to add code "
											+ "at the beginning of this method");
							return ;
						}
						compilerManager.addBeforeMethod(cyanMetaobject, packageName, prototypeName, t.f1, t.f2 );
						mapMethodNameExclusive.put(t.f1, t.f3);
//						}
					}
				}

				/**
				 * rename methods
				 */


				try {
					if ( cyanMetaobject instanceof IAction_afti ) {
						renameMethodList = ((IAction_afti ) cyanMetaobject).afti_renameMethod(compiler_afti);
					}
					else if ( other != null ) {
						_Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT__GT__GT array =
						    ((_IAction__afti ) other)._afti__renameMethod_1(compiler_afti);
						// List<Tuple2<String, String[]>>
						int size = array._size().n;
						if ( size != 0 ) {
							renameMethodList = new ArrayList<Tuple2<String, String[]>>();
							// Array<Tuple<String, Array<String>>>
							for (int k = 0; k < size; ++k) {
								_Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT__GT t = array._at_1(new CyInt(k));
								_Array_LT_GP_CyString_GT strArray = t._f2();
								int sizeStrArray = strArray._size().n;
								String []javaStrArray = null;
								if ( sizeStrArray != 0 ) {
									javaStrArray = new String[sizeStrArray];
									for (int j = 0; j < sizeStrArray; ++j ) {
										javaStrArray[j] = strArray._at_1(new CyInt(j)).s;
									}
								}
								renameMethodList.add( new Tuple2<String, String[]>(
										t._f1().s, javaStrArray)
										);
							}

						}
					}
				}
				catch ( error.CompileErrorException e ) {
				}
				catch ( NoClassDefFoundError e ) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
				}
				catch ( RuntimeException e ) {
					env.thrownException(annot, annot.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(cyanMetaobject);
				}



				if ( renameMethodList != null ) {
					/*
					 * List<Tuple2<String, String []>> renameMethodList = null;
				       List<ISlotInterface> slotList = null;

					 */
					for ( Tuple2<String, String []> t : renameMethodList ) {
						String prototypeName = this.getName();
						/*
						 * check if the method to be renamed, t.f1, will be replaced by another
						 * method of the list slotList
						 */
						if ( slotList == null ) {
							env.error(annot.getFirstSymbol(), "Metaobject of annotation '"
									+ cyanMetaobject.getName() + "' is renaming method '"
									+ t.f1 + "'. It should create another method with this name. But it is not.");
						}
						else {
							boolean found = false;
							for ( ISlotInterface slot : slotList ) {
								if ( slot instanceof WrMethodSignature ) {
									if ( t.f1.equals( ((WrMethodSignature ) slot).getName() ) ) {
										found = true;
										break;
									}
								}
							}
							if ( !found ) {
								String s = "";
								int size = slotList.size();
								for ( ISlotInterface slot : slotList ) {
									if ( slot instanceof WrMethodSignature ) {
										s += ((WrMethodSignature ) slot).getName();
									}
									if ( --size > 0 ) {
										s += ", ";
									}
								}
								env.error(annot.getFirstSymbol(), "Metaobject of annotation '"
										+ cyanMetaobject.getName() + "' is renaming method '"
										+ t.f1 + "'. It should create another method with this name. "
												+ "But it is not. The methods it is creating are: [ "
										+ s + " ]"
										);

							}
						}
//						if ( hasLessThanInName || prototypeName.indexOf('<') >= 0 ) {
//							if ( ! this.getCompilationUnit().getPackageName().equals(packageName)  ||
//								 ! this.getName().equals(prototypeName) ) {
//								env.error(true,
//										annot.getFirstSymbol(),
//										"This metaobject annotation is trying to rename methods of a "
//										+ "prototype it does not have permission to. Only a generic prototype "
//										+ "can add code to itself and only to itself",
//										cyanMetaobject.getName(), ErrorKind.metaobject_error);
//							}
//						}
//						String thisPrototypeName = this.getName();
//
//						if ( !canCommunicateInPackage && ! prototypeName.equals(thisPrototypeName) ) {
//							env.error(annot.getFirstSymbol(), "This metaobject annotation is in prototype '" + packageName + "." +
//						          thisPrototypeName + "' and it is trying to rename a method of another prototype, '" + packageName + "." +
//								          prototypeName + "'. This is illegal because package '" + packageName + "' does not allow that. To make that" +
//									 "legal, attach '@feature(communicateInPackage, #on)' to the package in the project (.pyan) file", true, false);
//						}
//						else {
						compilerManager.renameMethods(cyanMetaobject,
								packageName, prototypeName, t.f1, t.f2);
//						}

					}
				}



			}




		}
		/*
        if repeatLoop {
           somethingChanged = true;
           count = 0;
           while somethingChanged {
               somethingChanged = false;
               for each metaobject mo of runManyList {
                   if addcode returns non-null {
                       replace the information on runManyList by
                         the new information. That includes the slotList and
                         the code to be added.
                       somethingChanged = true;
                   }
               }
               ++count;
               if somethingChanged && count > 5 {
                  error
               }
           }
        }
        insert the code of runManyList and runOnceList into the code.
		 */
		if ( runManyList != null && runManyList.size() > 0 ) {
			int count = 0;
			final int MaxRuns = 5;
			boolean somethingChanged = true;
			while ( somethingChanged ) {
	        	somethingChanged = false;
	    		for ( Tuple4<Annotation, StringBuffer, List<ISlotInterface>, String>
	    		        elemRunManyList : runManyList ) {
	    			Annotation annot = elemRunManyList.f1;
	    			CyanMetaobject cyanMetaobject = annot.getCyanMetaobject();
	    			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();


    				Tuple2<StringBuffer, String> codeSlot = null;
    				try {
    					/*
    					 *
    					 */
    					if ( other == null ) {
        					codeSlot = ((IAction_afti ) cyanMetaobject)
        							.afti_codeToAdd(compiler_afti, infoList);
    					}
    					else {
    						// from List<Tuple2<WrAnnotation, List<ISlotInterface>>>
    						// to   _Array_LT_GP__Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT__GT__GT
    						_Array_LT_GP__Tuple_LT_GP__meta_p_WrAnnotation_GP__Array_LT_GP__meta_p_ISlotInterface_GT__GT__GT x;

    						_Array_LT_GP__Tuple_LT_GP__meta_p_WrAnnotation_GP__Array_LT_GP__meta_p_ISlotInterface_GT__GT__GT arrayTuple =
    								new _Array_LT_GP__Tuple_LT_GP__meta_p_WrAnnotation_GP__Array_LT_GP__meta_p_ISlotInterface_GT__GT__GT();
//    						_Array_LT_GP__Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT__GT__GT arrayTuple =
//    								new _Array_LT_GP__Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT__GT__GT();

    						if ( infoList != null ) {
        						for (Tuple2<WrAnnotation, List<ISlotInterface>>  t : infoList ) {
        							_Array_LT_GP__meta_p_ISlotInterface_GT arraySlot = new _Array_LT_GP__meta_p_ISlotInterface_GT();
//        							_Array_LT_GP_Object_GT arraySlot = new _Array_LT_GP_Object_GT();
        							for ( ISlotInterface slotInter : t.f2 ) {
        								arraySlot._add_1(slotInter);
        							}
        							// _Tuple_LT_GP__meta_p_WrCyanMetaobjectAnnotation_GP__Array_LT_GP__meta_p_ISlotInterface_GT__GT
        							arrayTuple._add_1(
        									new _Tuple_LT_GP__meta_p_WrAnnotation_GP__Array_LT_GP__meta_p_ISlotInterface_GT__GT(t.f1, arraySlot)
        									);


//        							arrayTuple._add_1(
//        									new _Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT__GT(t.f1, arraySlot)
//        									);
        						}
    						}
    						_Tuple_LT_GP_CyString_GP_CyString_GT t = ((_IAction__afti ) other)
    								._afti__codeToAdd_2(compiler_afti, arrayTuple);
    						String codeReturned = t._f2().s;
    						if ( codeReturned.length() != 0 && ! codeReturned.matches("[ \r\n\t]*") ) {
    							codeSlot = new Tuple2<StringBuffer, String>( new StringBuffer(t._f1().s), codeReturned);
    						}
    					}


    					if ( codeSlot == null ) {
							env.error(annot.getFirstSymbol(), "Method to generate code in phase afti of " +
						            "metaobject of annotation '"
									+ cyanMetaobject.getName() + "' returned null. This is illegal because"
											+ " method 'runUntilFixedPoint()' of this metaobject returned true");
    					}
    					else  {
    						if ( ! elemRunManyList.f2.toString().equals(codeSlot.f1.toString()) ) {
    							// produces code that is different from the previous call
        						elemRunManyList.f2 = codeSlot.f1;
        						List<ISlotInterface> slotList =
        								extractSlotListFrom(codeSlot.f2, annot, env, annot.getFirstSymbol());
        						elemRunManyList.f3 = slotList;
	    						somethingChanged = true;
        						// update infoList
        						if ( infoList != null ) {
            						for ( Tuple2<WrAnnotation, List<ISlotInterface>> t : infoList ) {
            							if ( t.f1 == elemRunManyList.f1.getI() ) {
            								t.f2 = slotList;
            								break;
            							}
            						}
        						}
    						}
    					}
    				}
    				catch ( error.CompileErrorException e ) {
    				}
    				catch ( NoClassDefFoundError e ) {
    					env.error(annot.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
    				}
    				catch ( RuntimeException e ) {
    					env.thrownException(annot, annot.getFirstSymbol(), e);
    				}
    				finally {
    					env.errorInMetaobjectCatchExceptions(cyanMetaobject);
    				}

	    		}
	    		++count;
	            if ( somethingChanged && count > MaxRuns ) {
	            	env.error(this.getFirstSymbol(), "The metaobjects of this compilation unit demand "
	            			+ "information on other metaobjects in a possible infinite way. For example, one cause a change "
	            			+ "in the code produced by other that cause a change in the code"
	            			+ " produced by the first and so on. If the compiled did not sign this"
	            			+ " error, there could be an infinite recursion");
	            }

	        }
		}

		String prototypeName = this.getName();
		String packageName = this.compilationUnit.getCyanPackage().getPackageName();

		if ( runOnceList != null ) {
			for ( Tuple4<Annotation, StringBuffer, List<ISlotInterface>, String> t : runOnceList ) {
				compilerManager.addCode(t.f1.getCyanMetaobject(), packageName, prototypeName, t.f2, t.f4);
			}
		}
		if ( runManyList != null ) {
			for ( Tuple4<Annotation, StringBuffer, List<ISlotInterface>, String> t : runManyList ) {
				compilerManager.addCode(t.f1.getCyanMetaobject(), packageName, prototypeName, t.f2, t.f4);
			}
		}



		/*
        if repeatLoop {
           somethingChanged = true;
           count = 0;
           while somethingChanged {
               copy infoList into newInfoList;
               somethingChanged = false;
               for each metaobject mo {
                 if runUntilFixedPoint returns true {
                     if addcode returns non-null {
                         replace the information on newInfoList by
                           the new information. That includes the slotList and
                           the code to be added.
                         somethingChanged = true;
                     }
                 }
               }
               infoList = newInfoList;
               newInfoList = new List<>();
               ++count;
               if somethingChanged && count > 5 {
                  error
               }
           }
        }
        insert the code of infoList into the code.
	 */

		env.atEndOfObjectDec();
	}

	static public void callCatch( Runnable runnable,
			CyanMetaobject cyanMetaobject, Annotation annot, Env env) {
		try {
			runnable.run();
		}
        catch ( meta.MetaSecurityException e ) {
            env.error(annot.getFirstSymbol(),
                    "There was a security error when calling a method of metaobject  '" +
                    cyanMetaobject.getName() + "' The metaobject may have tried to access a " +
                    "resource (the statements of a method or the list of fields) it does not " +
                    "have permission to access"
              );
        }
		catch ( error.CompileErrorException e ) {
		}
		catch ( NoClassDefFoundError e ) {
			env.error(annot.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
		}
		catch ( RuntimeException e ) {
			env.thrownException(annot, annot.getFirstSymbol(), e);
		}
		finally {
			env.errorInMetaobjectCatchExceptions(cyanMetaobject);
		}

	}

	/**
	 * return a list of objects of WrMethodSignature and WrFieldDec
	   @param slotCode
	   @return
	 */
	public List<ISlotInterface> extractSlotListFrom(String slotCode, Annotation annot,
			Env env, Symbol errSymbol) {
		/*
		 * 	public Compiler_dsl getCompilerToDSL_sourceFile(char []sourceCode, String sourceCodeFilename,
			String sourceCodeCanonicalPath, CyanPackage cyanPackage) {

		 */
		if ( this.dslCompilationUnit == null ) {
			this.dslCompilationUnit = new CompilationUnitDSL(this.compilationUnit.getFilename(),
					this.compilationUnit.getFullFileNamePath(), this.compilationUnit.getCyanPackage());
		}
		List<ISlotInterface> list = new ArrayList<>();
		char []source = new char[slotCode.length() + 1];
		slotCode.getChars(0, slotCode.length(), source, 0);
		source[slotCode.length()] = '\0';
		saci.Compiler cp = this.getCompilationUnit().getProgram().getProject()
				.getCompilerManager().getCompiler_sourceFile(source, dslCompilationUnit);

		if ( cp.getSymbol().token == Token.LITERALSTRING ) {
			env.error(annot.getFirstSymbol(), "Metaobject of annotation '" + annot.getCyanMetaobject().getName() +
					"' produced a list of fields and methods that is, in fact, the following literal string: \"" +
					cp.getSymbol().getSymbolString() + "\"\n"
					+ "Probably the metaobject returned a string with quotes. To correct the error, instead of"
					+ " returning string of variable s, for example, return \n" +
					"    CyanMetaobject.removeQuotes(s)");
		}


		try {

			while ( cp.getSymbol().token == Token.IDENT || Compiler.isBasicType(cp.getSymbol().token) ||
					cp.getSymbol().token == Token.LET || cp.getSymbol().token == Token.VAR ||
					cp.getSymbol().token == Token.FUNC ) {
				if ( cp.getSymbol().token == Token.FUNC ) {
					cp.next();
					MethodSignature ms = null;
					try {
						ms = cp.methodSignature();
						if ( cp.getSymbol().token == Token.RETURN_ARROW ) {
							cp.next();
							Expr exprRet = cp.type();
							ms.setReturnTypeExpr(exprRet);
						}
						if ( cp.getSymbol().token == Token.SEMICOLON ) {
							cp.next();
						}
					}
					catch ( Throwable e ) {
						env.error(errSymbol, "Syntax error in the list of slots that metaobject '" +
					        annot.getCyanMetaobject().getName() +
								"' of line " + annot.getFirstSymbol().getLineNumber() + " should produce. Remeber the list should use spaces as a regular code. That means \"var Int n;func check;\" is wrong. "
					          + "The list is '" + slotCode + "'", true);
						return null;
					}
					list.add(ms.getI());
				}
				else {
					boolean isShared = false;
					if ( cp.getSymbol().token == Token.SHARED ) {
						isShared = true;
						cp.next();
					}
					boolean isReadOnly = true;
					if ( cp.getSymbol().token == Token.VAR ) {
						isReadOnly = false;
						cp.next();
					}
					else if ( cp.getSymbol().token == Token.LET ) {
						cp.next();
					}
					Expr exprType = null;
					try {
						exprType = cp.type();
					}
					catch ( Throwable e ) {
						env.error(errSymbol, "Syntax error in the list of slots that metaobject '" +
					        annot.getCyanMetaobject().getName() +
								"' should produce", true);
						return null;
					}
					Symbol id = null;
					if ( cp.getSymbol().token == Token.IDENT ) {
						id = cp.getSymbol();
						cp.next();
					}
					/*
					 *  FieldDec( ObjectDec currentObj, SymbolIdent variableSymbol,
			                    Expr typeInDec,
			                    Expr expr,
			                    Token visibility,
			                    boolean shared,
			                    List<AnnotationAt> nonAttachedSlotMetaobjectAnnotationList,
			                    List<AnnotationAt> attachedSlotMetaobjectAnnotationList,
			                    Symbol firstSymbol, boolean isReadonly,
			                    Stack<Tuple5<String, String, String, String, Integer>> annotContextStack)
					 */
					FieldDec field = new FieldDec(null, (SymbolIdent) id, exprType, null, Token.PRIVATE,
							isShared, null, null, null, isReadOnly, null);

					list.add(field.getI());
					if ( cp.getSymbol().token == Token.SEMICOLON ) {
						cp.next();
					}

				}
			}
		}
		catch (final error.CompileErrorException e) {
		}
		catch (final NoClassDefFoundError e) {
			this.compilationUnit.error(annot.getFirstSymbol().getLineNumber(), annot.getFirstSymbol().getColumnNumber(), e.getMessage() + " "
					+ NameServer.messageClassNotFoundException);
		}
		catch (final RuntimeException e) {
			this.compilationUnit.error(annot.getFirstSymbol().getLineNumber(), annot.getFirstSymbol().getColumnNumber(),
					"Metaobject '" + annot.getCyanMetaobject().getName() + "' "
							+ "of annotation of line number " + annot.getFirstSymbol().getLineNumber()
							+ " has thrown exception '" + e.getClass().getName()
							+ "'");
		}
		finally {

			final List<UnitError> errorList = dslCompilationUnit.getErrorList();
			if ( errorList != null ) {
				for (final UnitError moError : errorList) {
					this.compilationUnit.error(annot.getFirstSymbol().getLineNumber(), annot.getFirstSymbol().getColumnNumber(),
							"Metaobject of annotation '"+ annot.getCyanMetaobject().getName() +
							"' issued the following error relating to the list of fields and methods: " + moError.getMessage());
				}
			}
		}

		if ( cp.getSymbol().token != Token.EOF && cp.getSymbol().token != Token.EOLO ) {
			env.error(annot.getFirstSymbol(), "Metaobject of annotation '" + annot.getCyanMetaobject().getName() +
					"' produced a list of fields and methods with syntax errors. We found the token '"
					+ cp.getSymbol().getSymbolString() + "' where we expected the end of code");
		}
		return list;
	}

	private CompilationUnitDSL dslCompilationUnit;


	/*
	MethodSignature ms;
	if ( Compiler.isOperator(cp.getSymbol().token) ) {
		Symbol op = cp.getSymbol();
		cp.next();
		MethodSignatureOperator mso = new ast.MethodSignatureOperator(op, (MethodDec ) null);
		if ( ! Compiler.startType(symbol.token)  ) {
			env.error(errSymbol, "A type was expected after '" + op.getSymbolString() +
					"' in the list of slots that metaobject '" + annot.getCyanMetaobject().getName() +
					"' should produce", true);

		}
		Expr exprType = cp.type();
		exprType.calcInternalTypes(env);
		mso.setOptionalParameter(new ParameterDec(null, exprType, null));
		list.add(mso.getI());
	}
	else if ( cp.getSymbol().token == Token.IDENT ) {
		MethodSignatureUnary msu = new MethodSignatureUnary(cp.getSymbol(), (MethodDec ) null);
		list.add(msu.getI());
	}
	else if ( cp.getSymbol().token == Token.IDENTCOLON ) {
		MethodSignatureWithKeywords msk = new MethodSignatureWithKeywords();
	}
	else {
		env.error(errSymbol, "An operator, identifier or identifier: was expected after 'func'"  +
				"' in the list of slots that metaobject '" + annot.getCyanMetaobject().getName() +
				"' should produce", true);

	}
	*/


//	/**
//	   @param compiler_afti
//	   @param compilerManager
//	   @param env
//	   @param hasLessThanInName
//	   @param annotation
//	 */
//	private void addCodeAndSlotsTo(ICompiler_afti compiler_afti, CompilerManager_afti compilerManager, Env env,
//			boolean hasLessThanInName, Annotation annotation) {
//
//
//		CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
//
//
//
//		String thisPrototypeName = this.getName();
//
//		// // cyanMetaobject.setMetaobjectAnnotation(cyanMetaobjectAnnotation, 0);
//
//
//		CompilerManager.createNewPrototypes(compiler_afti, env, annotation, cyanMetaobject, this.compilationUnit);
//
//		if ( cyanMetaobject instanceof IAction_afti ) {
//
//
//			String packageName = this.compilationUnit.getCyanPackage().getPackageName();
//			boolean canCommunicateInPackage = this.compilationUnit.getCyanPackage().getCommunicateInPackage();
//
//			/**
//			 * add statements to methods. This was asked by the metaobjects of this program unit.
//			 */
//			List<Tuple2<String, StringBuffer>> statsList = null;
//
//			try {
//				statsList = ((IAction_afti ) cyanMetaobject).afti_beforeMethodCodeList(compiler_afti);
//			}
//			catch ( error.CompileErrorException e ) {
//			}
//			catch ( NoClassDefFoundError e ) {
//				env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
//			}
//			catch ( RuntimeException e ) {
//				env.thrownException(annotation, annotation.getFirstSymbol(), e);
//			}
//			finally {
//				env.errorInMetaobjectCatchExceptions(cyanMetaobject);
//			}
//
//
//			if ( statsList != null ) {
//				for ( Tuple2<String, StringBuffer> t : statsList ) {
//					String prototypeName = this.getName();
//					if ( hasLessThanInName || prototypeName.indexOf('<') >= 0 ) {
//						if ( ! this.getCompilationUnit().getPackageName().equals(packageName)  ||
//							 ! this.getName().equals(prototypeName) ) {
//							env.error(true,
//									annotation.getFirstSymbol(),
//									"This metaobject annotation is trying to code to "
//									+ "a method of a prototype it does not have permission "
//									+ "to. Only a generic prototype can add code to itself "
//									+ "and only to itself", cyanMetaobject.getName(), ErrorKind.metaobject_error);
//						}
//					}
//					if ( !canCommunicateInPackage && ! prototypeName.equals(thisPrototypeName) ) {
//						env.error(annotation.getFirstSymbol(), "This metaobject annotation is in prototype '" + packageName + "." +
//					          thisPrototypeName + "' and it is trying to add code to another prototype, '" + packageName + "." +
//							          prototypeName + "'. This is illegal because package '" + packageName + "' does not allow that. To make that " +
//								 "legal, attach '@feature(communicateInPackage, #on)' to the package in the project (.pyan) file", true, false);
//					}
//					else {
//						compilerManager.addBeforeMethod(cyanMetaobject, packageName, prototypeName, t.f1, t.f2 );
//					}
//				}
//			}
//
//
//			/**
//			 * add code after the metaobject annotation
//			 * /
//			StringBuffer code = null;
//
//			try {
//				code = ((IAction_afti ) cyanMetaobjectWithAt).afti_codeToAdd(compiler_afti);
//			}
//			catch ( error.CompileErrorException e ) {
//			}
//			catch ( NoClassDefFoundError e ) {
//				env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
//			}
//			catch ( RuntimeException e ) {
//				env.thrownException(annotation, annotation.getFirstSymbol(), e);
//			}
//			finally {
//				env.errorInMetaobjectCatchExceptions(cyanMetaobject);
//			}
//
//
//
//			if ( code != null ) {
//				compilerManager.addCodeAtMetaobjectAnnotation(cyanMetaobjectWithAt, code );
//			}
//			*/
//
//
//
//			/**
//			 * rename methods
//			 */
//
//			List<Tuple2<String, String []>> renameMethodList = null;
//
//			try {
//				renameMethodList = ((IAction_afti ) cyanMetaobject).afti_renameMethod(compiler_afti);
//			}
//			catch ( error.CompileErrorException e ) {
//			}
//			catch ( NoClassDefFoundError e ) {
//				env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
//			}
//			catch ( RuntimeException e ) {
//				env.thrownException(annotation, annotation.getFirstSymbol(), e);
//			}
//			finally {
//				env.errorInMetaobjectCatchExceptions(cyanMetaobject);
//			}
//
//
//
//			if ( renameMethodList != null ) {
//				for ( Tuple2<String, String []> t : renameMethodList ) {
//					String prototypeName = this.getName();
//					if ( hasLessThanInName || prototypeName.indexOf('<') >= 0 ) {
//						if ( ! this.getCompilationUnit().getPackageName().equals(packageName)  ||
//							 ! this.getName().equals(prototypeName) ) {
//							env.error(true,
//									annotation.getFirstSymbol(),
//									"This metaobject annotation is trying to rename methods of a "
//									+ "prototype it does not have permission to. Only a generic prototype "
//									+ "can add code to itself and only to itself",
//									cyanMetaobject.getName(), ErrorKind.metaobject_error);
//						}
//					}
//					if ( !canCommunicateInPackage && ! prototypeName.equals(thisPrototypeName) ) {
//						env.error(annotation.getFirstSymbol(), "This metaobject annotation is in prototype '" + packageName + "." +
//					          thisPrototypeName + "' and it is trying to rename a method of another prototype, '" + packageName + "." +
//							          prototypeName + "'. This is illegal because package '" + packageName + "' does not allow that. To make that" +
//								 "legal, attach '@feature(communicateInPackage, #on)' to the package in the project (.pyan) file", true, false);
//					}
//					else {
//						compilerManager.renameMethods(cyanMetaobject,
//								packageName, prototypeName, t.f1, t.f2);
//					}
//
//				}
//			}
//		}
//	}
//


	public List<ProgramUnit> get_this_and_all_superPrototypes() {
		if ( this_and_all_superPrototypes != null ) {
			return this_and_all_superPrototypes;
		}
		List<ProgramUnit> superList = new ArrayList<>();
		superList.add(this);
		List<ProgramUnit> superList2 = getAllSuperPrototypes();
		if ( superList2 != null ) {
			superList.addAll(superList2);
		}
		this_and_all_superPrototypes = Collections.unmodifiableList(superList);
		return this_and_all_superPrototypes;
	}

	/*
	 * return a list containing all super-prototypes of this prototype. That includes all super-interfaces
	 * (if this is an interface), all super-prototypes (if this is not an interface), and
	 * all implemented interfaces. The list is built by a breadth first search for the supertypes.
	 */
	public List<ProgramUnit> getAllSuperPrototypes() {

		if ( allSuperPrototypes != null ) {
			return allSuperPrototypes;
		}

		HashSet<ProgramUnit> puSet = new HashSet<>();
		Stack<ProgramUnit> puStack = new Stack<>();
		puStack.add(this);
		while ( ! puStack.isEmpty() ) {
			ProgramUnit current = puStack.pop();
			  // mark current as visited
			puSet.add(current);

			if ( current instanceof InterfaceDec ) {
				if (  ((InterfaceDec ) current).getSuperInterfaceList() != null ) {
					for ( InterfaceDec inter : ((InterfaceDec ) current).getSuperInterfaceList()  ) {
						if ( ! puSet.contains(inter) ) {
							puStack.add(inter);
						}
					}
				}
			}
			else if ( current instanceof ObjectDec ) {
				if ( ((ObjectDec ) current).getSuperobject() != null ) {
					if ( ! puSet.contains(((ObjectDec ) current).getSuperobject())) {
						puStack.add( ((ObjectDec ) current).getSuperobject() );
					}
				}
				if ( ((ObjectDec) current).getInterfaceList() != null ) {
					for ( Expr inter : ((ObjectDec) current).getInterfaceList() ) {
						if ( ! puSet.contains(inter.getType()) ) {
							puStack.add( (ProgramUnit ) inter.getType().getInsideType() );
						}
					}
				}
			}
		}
		List<ProgramUnit> other = new ArrayList<>();
		for ( ProgramUnit p : puSet ) {
			if ( p != this ) {
				other.add(p);
			}
		}
		allSuperPrototypes = Collections.unmodifiableList(other);
		return allSuperPrototypes;
	}

	/**
	 * to be called only after step 9 of the compilation, afsa
	   @param env
	   @param superPrototypeList
	 */

	private void checkInheritance_afsa(ICompiler_dsa compiler_dsa, Env env) {

		List<ProgramUnit> allSuperPrototypes2 = this.getAllSuperPrototypes();
		for ( ProgramUnit pu : allSuperPrototypes2 ) {

			List<Annotation> annotList = pu.getPrototypePackageProgramAnnotationList();
			pu.setDeclarationImportedFromPackageProgram();

			for ( Annotation cyanMetaobjectAnnotation : annotList ) {
				CyanMetaobject cyanMetaobject = cyanMetaobjectAnnotation.getCyanMetaobject();

				_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
				if ( cyanMetaobject instanceof ICheckSubprototype_afsa ||
						(other != null && other instanceof _ICheckSubprototype__afsa) ) {

					// // cyanMetaobject.setMetaobjectAnnotation(cyanMetaobjectAnnotation, 0);

					try {
						if ( other == null ) {
							((ICheckSubprototype_afsa ) cyanMetaobject).afsa_checkSubprototype(
									compiler_dsa, this.getI());
						}
						else {
							((_ICheckSubprototype__afsa ) other)._afsa__checkSubprototype_2(
									compiler_dsa, this.getI());
						}
					}
					catch ( error.CompileErrorException e ) {
					}
					catch ( NoClassDefFoundError e ) {
						env.error(cyanMetaobjectAnnotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( RuntimeException e ) {
						// e.printStackTrace();
						env.thrownException(cyanMetaobjectAnnotation, cyanMetaobjectAnnotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobject(cyanMetaobject, this.getSymbolObjectInterface());
					}


				}

			}
				}
	}



	public void setEndSymbol(Symbol endSymbol) {
		this.endSymbol = endSymbol;
	}
	public Symbol getEndSymbol() {
		return endSymbol;
	}

	public boolean getGenericPrototype() {
		return genericPrototype;
	}


	public void setGenericPrototype(boolean genericPrototype) {
		this.genericPrototype = genericPrototype;
	}




	/**
	 * return the current program unit as an Expr.
	 * <code>seed</code> is used to create new Symbols with the same line number as <code>seed</code>
	 */
	@Override
	public Expr asExpr(Symbol seed) {

		List<List<Expr>> realTypeListList;
		List<Expr> realTypeList;
		List<Symbol> identSymbolArray = new ArrayList<>();



		if ( ! getCompilationUnit().getPackageName().equals(MetaHelper.cyanLanguagePackageName) ) {
			// insert package symbols first
			for ( Symbol sym : getCompilationUnit().getPackageIdent().getIdentSymbolArray() ) {
				identSymbolArray.add(new SymbolIdent(Token.IDENT, sym.getSymbolString(), seed.getStartLine(),
						seed.getLineNumber(), seed.getColumnNumber(), seed.getOffset(), seed.getCompilationUnit()) );
			}
		}
		// insert the program unit name
		identSymbolArray.add( new SymbolIdent(Token.IDENT, symbol.getSymbolString(), seed.getStartLine(),
				seed.getLineNumber(), seed.getColumnNumber(), seed.getOffset(), seed.getCompilationUnit()) );
		ExprIdentStar newIdentStar = new ExprIdentStar(identSymbolArray, null);


		if ( genericParameterListList != null && this.genericParameterListList.size() > 0 ) {
			/**
			 * an instantiation of a generic prototype such as "Stack<Int>".
			 */
			realTypeListList = new ArrayList<List<Expr>>();

			List<List<GenericParameter>> genParListList = getGenericParameterListList();

			for ( List<GenericParameter> genParList :  genParListList ) {
				realTypeList = new ArrayList<Expr>();
				for ( GenericParameter gp : genParList ) {
					// realTypeList.add( new ExprIdentStar(new SymbolIdent(Token.IDENT, gp.getName(), -1, -1, -1, -1) ));
					realTypeList.add( gp.getParameter() );
				}
				realTypeListList.add(realTypeList);
			}

			ExprGenericPrototypeInstantiation gpi = new ExprGenericPrototypeInstantiation(
					newIdentStar,
					realTypeListList, null, null);
			return gpi;
		}
		else {
			return newIdentStar;
		}

	}



	/**
	 * generate all Java code that the metaobject annotations of this program unit
	 * demand
	 */
	protected void genJavaClassBodyDemandedByMetaobjectAnnotations(PWInterface pw, Env env) {

		if ( this.completeMetaobjectAnnotationList != null ) {
			for ( Annotation annotation : this.completeMetaobjectAnnotationList ) {

				CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
				if ( cyanMetaobject != null && cyanMetaobject instanceof IAction_cge ) {
					IAction_cge cyanMetaobject_cge = (IAction_cge ) cyanMetaobject;
					// // cyanMetaobject.setMetaobjectAnnotation(annotation, 0);
					StringBuffer code = null;
					try {
						code = cyanMetaobject_cge.cge_javaCodeClassBody();
					}
					catch ( error.CompileErrorException e ) {
					}
					catch ( NoClassDefFoundError e ) {
						env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( RuntimeException e ) {
						env.thrownException(annotation, annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobjectCatchExceptions(cyanMetaobject);
					}
					if ( code != null ) {
						pw.print(code);
						pw.print("\n");
					}
				}
			}

		}

	}

	/**
	 * return the number of the next metaobject annotation. This number is incremented so this method has side effects.
	   @return
	 */
	public int getIncMetaobjectAnnotationNumber() {
		++metaobjectAnnotationNumber;
		return metaobjectAnnotationNumber - 1;
	}

	public void setCyanMetaobjectListBeforeExtendsMixinImplements(
			List<AnnotationAt> moListBeforeExtends) {
		this.moListBeforeExtendsMixinImplements = moListBeforeExtends;
	}

	public void addMessageSendWithkeywordsToSuper(
			ExprMessageSendWithKeywordsToSuper msSuper) {
		messageSendWithkeywordsToSuperList.add(msSuper);
	}


	public int getNextFunctionNumber() {
		return nextFunctionNumber;
	}

	public void addNextFunctionNumber() {
		++nextFunctionNumber;
	}

	public List<ObjectDec> getInnerPrototypeList() {
		return innerPrototypeList;
	}


	public void addInnerPrototype(ObjectDec innerPrototype) {
		innerPrototypeList.add(innerPrototype);
	}


	public ObjectDec getOuterObject() {
		return outerObject;
	}


	public List<AnnotationAt> getBeforeEndNonAttachedMetaobjectAnnotationList() {
		return beforeEndNonAttachedMetaobjectAnnotationList;
	}



	public void setBeforeEndNonAttachedMetaobjectAnnotationList(List<AnnotationAt> beforeEndNonAttachedMetaobjectAnnotationList) {
		this.beforeEndNonAttachedMetaobjectAnnotationList = beforeEndNonAttachedMetaobjectAnnotationList;
	}

	public List<AnnotationAt> getNonAttachedMetaobjectAnnotationList() {
		return nonAttachedMetaobjectAnnotationList;
	}

	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		return featureList;
	}

	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature) {
		if ( featureList == null )
			featureList = new ArrayList<>();
		featureList.add(feature);
	}


	public void addFeatureList( List<Tuple2<String, WrExprAnyLiteral>> featureList1) {
		for ( Tuple2<String, WrExprAnyLiteral> t : featureList1 ) {
			this.addFeature(t);
		}
	}


	public List<WrExprAnyLiteral> searchFeature(String name) {
		if ( featureList == null ) return null;

		List<WrExprAnyLiteral> eList = null;
		for ( Tuple2<String, WrExprAnyLiteral> t : featureList ) {
			if ( t.f1.equals(name) ) {
				if ( eList == null ) {
					eList = new ArrayList<>();
				}
				eList.add(t.f2);
			}
		}
		return eList;
	}



	public AttachedDeclarationKind getKind() {
		return AttachedDeclarationKind.PROTOTYPE_DEC;
	}


	/**
	   @param pw
	 */
	protected void genJavaCodeBeforeClassMetaobjectAnnotations(PWInterface pw, Env env) {
		if ( this.attachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : attachedMetaobjectAnnotationList ) {
				CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
				if ( cyanMetaobject instanceof meta.IAction_cge ) {
					// // annotation.getCyanMetaobject().setMetaobjectAnnotation(annotation, 0);
					StringBuffer sb = null;

					try {
						sb =  ((IAction_cge) annotation.getCyanMetaobject()).cge_codeToAdd();
					}
					catch ( error.CompileErrorException e ) {
					}
					catch ( NoClassDefFoundError e ) {
						env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( RuntimeException e ) {
						env.thrownException(annotation, annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobject(cyanMetaobject, this.getFirstSymbol());
					}


					if ( sb != null ) {
						if ( sb.charAt(sb.length()-1) == '\0' )
							sb.deleteCharAt(sb.length()-1);
						pw.print( sb );
					}
				}
			}
		}
		if ( this.nonAttachedMetaobjectAnnotationList != null ) {
			for ( AnnotationAt annotation : nonAttachedMetaobjectAnnotationList ) {
				CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
				if ( cyanMetaobject instanceof meta.IAction_cge ) {
					// // annotation.getCyanMetaobject().setMetaobjectAnnotation(annotation, 0);
					StringBuffer sb = null;

					try {
						sb = ((IAction_cge) annotation.getCyanMetaobject()).cge_codeToAdd();
					}
					catch ( error.CompileErrorException e ) {
					}
					catch ( NoClassDefFoundError e ) {
						env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( RuntimeException e ) {
						env.thrownException(annotation, annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobject(cyanMetaobject, this.getFirstSymbol());
					}



					if ( sb != null ) {
						if ( sb.charAt(sb.length()-1) == '\0' )
							sb.deleteCharAt(sb.length()-1);
						pw.print( sb );
					}
				}
			}
		}
		if ( this.completeMetaobjectAnnotationList != null ) {
			for ( Annotation annotation : this.completeMetaobjectAnnotationList ) {
				if ( annotation.getCyanMetaobject() instanceof meta.IAction_cge ) {
					// // annotation.getCyanMetaobject().setMetaobjectAnnotation(annotation, 0);
					StringBuffer sb = null;

					try {
						sb = ((IAction_cge) annotation.getCyanMetaobject()).cge_javaCodeBeforeClass();
					}
					catch ( error.CompileErrorException e ) {
					}
					catch ( NoClassDefFoundError e ) {
						env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( RuntimeException e ) {
						env.thrownException(annotation, annotation.getFirstSymbol(), e);
					}
					finally {
						CyanMetaobject metaobject = annotation.getCyanMetaobject();
						env.errorInMetaobjectCatchExceptions(metaobject);
					}

					if ( sb != null ) {
						if ( sb.charAt(sb.length()-1) == '\0' )
							sb.deleteCharAt(sb.length()-1);
						pw.print( sb );
					}
				}
			}
		}
	}

	/**
	   @param pw
	 */
	protected void genJavaCodeStaticSectionMetaobjectAnnotations(PWInterface pw, Env env) {
		/*
		 * insert all code for the static sections asked by the metaobjects of this program unit
		 */
		pw.println("    static {");
		pw.add();
		if ( completeMetaobjectAnnotationList != null ) {
			for ( Annotation annotation : completeMetaobjectAnnotationList ) {
				if ( annotation.getCyanMetaobject() instanceof IAction_cge ) {
					CyanMetaobject metaobject = annotation.getCyanMetaobject();
					// // metaobject.setMetaobjectAnnotation(annotation, 0);
					StringBuffer code = null;

					try {
						code = ((IAction_cge) metaobject).cge_javaCodeStaticSection();
					}
					catch ( error.CompileErrorException e ) {
					}
					catch ( NoClassDefFoundError e ) {
						env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( RuntimeException e ) {
						env.thrownException(annotation, annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobjectCatchExceptions(metaobject);
					}

					if ( code != null ) {
						pw.println( code );
					}
				}
			}
		}


		pw.sub();
		pw.println("    }");
	}

	public Symbol getFirstSymbol() {
		return firstSymbol;
	}



	public void setFirstSymbol(Symbol firstSymbol) {
		this.firstSymbol = firstSymbol;
	}

	public List<ExprGenericPrototypeInstantiation> getGpiList() {
		return gpiList;
	}


	public void addGpiList(ExprGenericPrototypeInstantiation elem) {
		if ( gpiList == null ) {
			gpiList = new ArrayList<>();
		}
		gpiList.add(elem);
	}


	public String getRealName() {
		return realName;
	}


	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Symbol getSymbolObjectInterface() {
		return symbolObjectInterface;
	}

	public void setSymbolObjectInterface(Symbol symbolObjectInterface) {
		this.symbolObjectInterface = symbolObjectInterface;
	}



	public boolean getImmutable() {
		return immutable;
	}

	public void setImmutable(boolean immutable) {
		this.immutable = immutable;
	}


	public void addDocumentText(String doc, String docKind) {
		if ( documentTextList == null ) {
			documentTextList = new ArrayList<>();
		}
		documentTextList.add( new Tuple2<String, String>(doc, docKind));
	}

	public void addDocumentExample(String example, String exampleKind) {
		if ( exampleTextList == null ) {
			exampleTextList = new ArrayList<>();
		}
		exampleTextList.add( new Tuple2<String, String>(example, exampleKind));

	}

	public List<Tuple2<String, String>> getDocumentTextList() {
		return documentTextList;
	}

	public List<Tuple2<String, String>> getDocumentExampleList() {
		return exampleTextList;
	}



	/*
	public IDeclaration getIDeclaration() {
		return new WrProgramUnit(this);
	}
	*/



	@Override
	public WrProgramUnit getI() {
		if ( iProgramUnit == null ) {
			iProgramUnit = WrProgramUnit.factory(this);
		}
		return iProgramUnit;
	}

	private WrProgramUnit iProgramUnit;


	public WrProgramUnit getiProgramUnit() {
		return iProgramUnit;
	}


	public void setiProgramUnit(WrProgramUnit iProgramUnit) {
		this.iProgramUnit = iProgramUnit;
	}

	public List<AnnotationAt> getBeforeInnerObjectNonAttachedMetaobjectAnnotationList() {
		return beforeInnerObjectNonAttachedMetaobjectAnnotationList;
	}

	public void setBeforeInnerObjectNonAttachedMetaobjectAnnotationList(
			List<AnnotationAt> beforeInnerObjectNonAttachedMetaobjectAnnotationList) {
				this.beforeInnerObjectNonAttachedMetaobjectAnnotationList = beforeInnerObjectNonAttachedMetaobjectAnnotationList;
			}

	public List<AnnotationAt> getBeforeInnerObjectAttachedMetaobjectAnnotationList() {
		return beforeInnerObjectAttachedMetaobjectAnnotationList;
	}

	public void setBeforeInnerObjectAttachedMetaobjectAnnotationList(
			List<AnnotationAt> beforeInnerObjectAttachedMetaobjectAnnotationList) {
				this.beforeInnerObjectAttachedMetaobjectAnnotationList = beforeInnerObjectAttachedMetaobjectAnnotationList;
			}

	public Map<String, ProgramUnit> getDependentProgramUnitList() {
		return dependentProgramUnitMap;
	}

	public void addDependentProgramUnit( ProgramUnit dependentProgramUnit ) {
		if ( this.dependentProgramUnitMap == null ) {
			this.dependentProgramUnitMap = new HashMap<>();
		}
		this.dependentProgramUnitMap.put(dependentProgramUnit.getFullName(), dependentProgramUnit);
	}

	public void genCompiledInterfaces(StringBuffer sb) {

		CyanEnv cyanEnv = new CyanEnv(this.compilationUnit.getProgram());
		cyanEnv.setGenInterfacesForCompiledCode(true);

		PWCharArray pw = new PWCharArray(sb);
		genCyan(pw, cyanEnv, false);
		sb.append("\n");
	}



	/**
	 * list of pairs (doc, docKind) of documentation for this declaration. See interface
	 * {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>> documentTextList;
	/**
	 * list of pairs (example, exampleKind) of examples for this declaration. See interface
	 * {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>> exampleTextList;



	/**
	 * the compiler should assure that these generic instantiations are created in
	 * phase afti. These are the types of metaobject annotations
	 */
	private List<ExprGenericPrototypeInstantiation> gpiList;


	/**
	 * the list of features associated to this program unit
	 */
	private List<Tuple2<String, WrExprAnyLiteral>> featureList;

	/**
	 * symbol of the object name, "A" in
	 *     object A
	 *        ...
	 *     end
	 */
	protected Symbol symbol;

	/**
	 * list of generic parameters of this program unit if this program unit is a generic one.
	 * There is in fact one list
	 * for each part between < and >. For example, in
	 *       object Proto<T1, T2><R>
	 *  there are two list, the first with two elements and the second with one element.
	 */
	protected List<List<GenericParameter>> genericParameterListList;


	/**
	 * The compilation unit corresponding to this program unit. For
	 * example, object Stack (program unit) should be declared in file
	 * Stack.cyan (compilation unit)
	 */
	protected CompilationUnit compilationUnit;

	/**
	 * Visibility of this program unit. It may be PUBLIC, PROTECTED, or PRIVATE
	 */
	protected Token visibility;




	/*
	 * true if this is an instantiation of  generic prototype.
	 * Something like "object Stack<Int> ... end".
	 */
	private boolean prototypeIsNotGeneric;


	/**
	 * a list of all metaobject annotations inside and before this program unit.
	 * This list includes attachedMetaobjectAnnotationList and beforeEndNonAttachedMetaobjectAnnotationList.
	 * Every metaobject annotation inside the program unit is in this list.
	 */
	protected List<Annotation>  completeMetaobjectAnnotationList;

	/**
	 * a list of all annotations that apply to this program unit. It includes all
	 * annotations inside the program unit and annotations of its package and the
	 * program.
	 *
	 */
	protected List<Annotation>  prototypePackageProgramAnnotationList;


	/**
	 * metaobject annotations placed just before this program unit such as {@literal @}checkStyle in <br>
	 * <code>
	 * {@literal @}checkStyle object Proto<br>
	 *    ...<br>
	 * end<br>
	 * These metaobject annotations are attached to the program unit declaration
	 * </code>
	 */
	protected List<AnnotationAt> attachedMetaobjectAnnotationList;
	/**
	 * metaobject annotations placed just before this program unit such as {@literal @}javacode in <br>
	 * <code>
	 * {@literal @}javacode{* ... *} <br>
	 * object Proto<br>
	 *    ...<br>
	 * end<br>
	 * These metaobject annotations are NOT attached to the program unit declaration
	 * </code>
	 */
	protected List<AnnotationAt> nonAttachedMetaobjectAnnotationList;

	/**
	 * metaobject annotations placed just before keyword 'end' of this program unit.
	 * These metaobject annotations are NOT attached to any declaration
	 * </code>
	 */
	protected List<AnnotationAt> beforeEndNonAttachedMetaobjectAnnotationList;


	/**
	 * the symbol of 'end' at the end of the program unit
	 */
	protected Symbol endSymbol;

	/**
	 * true if this prototype is generic. False in prototypes like Stack{@literal <}Int>
	 */
	protected boolean genericPrototype;
	/**
	 * Each metaobject annotation in a prototype (not an interface) has a number. This field keeps the
	 * number of the next metaobject annotation. This number is used when metaobjects are communicating with
	 * each other.
	 */

	private int	metaobjectAnnotationNumber;

	/**
	 * list of metaobjects that can appear before keyword 'extends', 'mixin', or 'implements
	 */
	protected List<AnnotationAt>	moListBeforeExtendsMixinImplements;

	/**
	 * list of message sends to super in this program unit. It is used to generate code.
	 * Message sends to super are generated by calling a private Java method.
	 */
	protected List<ExprMessageSendWithKeywordsToSuper> messageSendWithkeywordsToSuperList;


	/**
	 * the next number to the associated to an anonymous functions. Functions inside a prototype receive numbers in textual order. Starts with 0
	 */
	protected int	nextFunctionNumber;

	/**
	 * list of inner objects of this object. These inner objects are created by the compiler.
	 * For each anonymous functions in the object the compiler creates one prototype that is inserted in
	 * this list. For each method in the object the compiler creates one prototype that is also
	 * inserted in this list.
	 */
	protected List<ObjectDec>	innerPrototypeList;

	/**
	 * if this object is inside another object, outerObject points to this outer object. Otherwise it is null
	 */
	protected ObjectDec	outerObject;

	/**
	 * full name including the package name. It may be somethink like <br>
	 * {@code Tuple<main.Program, people.bank.Client>}<br>
	 * This fullName is only set in the semantic analysis and after method getFullName is called.
	 */
	private String fullName;

	protected String javaName;

	private Symbol firstSymbol;

	/**
	 * the symbol 'object' or 'interface'
	 */
	private Symbol symbolObjectInterface;

	/**
	 * real name of this prototype, including its generic parameters, if any
	 */
	private String realName;

	/**
	 * all super-prototypes of this program unit. If this is ObjectDec, this list includes all super-prototypes and all
	 * implemented interfaces. If this is InterfaceDec, this list includes all super-interfaces.
	 */
	private List<ProgramUnit> allSuperPrototypes;

	/**
	 * this and all super-prototypes of this program unit. If this is ObjectDec, this list includes all super-prototypes and all
	 * implemented interfaces. If this is InterfaceDec, this list includes all super-interfaces.
	 */
	private List<ProgramUnit> this_and_all_superPrototypes;



	/**
	 * true if this program unit should be immutable
	 */
	private boolean immutable;
	/**
	 * metaobject annotations placed before the declaration of inner objects.
	 * These metaobject annotations are NOT attached to any declaration.
	 * </code>
	 */
	protected List<AnnotationAt> beforeInnerObjectNonAttachedMetaobjectAnnotationList;
	/**
	 * metaobject annotations placed before the declaration of inner objects.
	 * These metaobject annotations are attached to the next declaration.
	 * </code>
	 */
	protected List<AnnotationAt> beforeInnerObjectAttachedMetaobjectAnnotationList;


	/**
	 * list of prototypes from which this program unit depends on. Whenever a
	 * prototype in the list is changed, this program unit should be compiled again.
	 * This program unit depends on prototype P if:
	 *    - P is the type of a variable, parameter, or field that appear inside this program unit;
	 *    - P is the superprototype or an implemented interface of this program unit;
	 *    - P appears in an expression in this program unit;
	 *    - some metaobject inside this program unit asks for information on P, which may be
	 *      its methods, superprototype, or implemented interfaces.
	 */
	private Map<String, ProgramUnit> dependentProgramUnitMap;


}
