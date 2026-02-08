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
import java.util.concurrent.Executors;
import cyan.lang.CyInt;
import cyan.lang.CyString;
import cyan.lang._Array_LT_GP_CyString_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_meta_p_WrAnnotation_GP__Array_LT_GP_meta_p_ISlotSignature_GT_GT_GT;
import cyan.lang._Array_LT_GP_meta_p_ISlotSignature_GT;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT;
import cyan.lang._Tuple_LT_GP_meta_p_WrAnnotation_GP__Array_LT_GP_meta_p_ISlotSignature_GT_GT;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._IActionNewPrototypes__afterResTypes;
import cyan.reflect._IAction__afterResTypes;
import cyan.reflect._ICheckDeclaration__afterSemAn;
import cyan.reflect._ICheckOverride__afterSemAn;
import cyan.reflect._ICheckPrototype__bsa;
import cyan.reflect._ICheckSubprototype__afterSemAn;
import cyan.reflect._ICommunicateInPrototype__afterResTypes__semAn__afterSemAn;
import error.ErrorKind;
import error.UnitError;
import lexer.CompilerPhase;
import lexer.Lexer;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.IActionNewPrototypes_afterResTypes;
import meta.IAction_afterResTypes;
import meta.IAction_cge;
import meta.IAction_dpp;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICheckOverride_afterSemAn;
import meta.ICheckPrototype_bsa;
import meta.ICheckSubprototype_afterSemAn;
import meta.ICommunicateInPrototype_afterResTypes_semAn_afterSemAn;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_semAn;
import meta.ISlotSignature;
import meta.MetaHelper;
import meta.Timeout;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.WrAnnotation;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrExprAnyLiteral;
import meta.WrMethodSignature;
import meta.WrProgram;
import meta.WrPrototype;
import saci.Compiler;
import saci.CompilerManager;
import saci.CompilerManager_afterResTypes;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
import saci.Saci;

/**
 * This class is a superclass of ObjectDec and InterfaceDec
 *
 * @author José
 *
 */
public abstract class Prototype extends Type
		implements Declaration, ASTNode, Cloneable {

	public Prototype(Token visibility,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList, ObjectDec outerObject) {

		Prototype.initPrototype(this, visibility, nonAttachedAnnotationList,
				attachedAnnotationList, outerObject);
		// this.visibility = visibility;
		// this.nonAttachedAnnotationList = nonAttachedAnnotationList;
		// this.attachedAnnotationList = attachedAnnotationList;
		// this.genericParameterListList = new
		// ArrayList<List<ast.GenericParameter>>();
		// this.outerObject = outerObject;
		//
		// // genericProtoInstantiationList = new
		// ArrayList<ExprGenericPrototypeInstantiation>();
		// this.prototypeIsNotGeneric = true;
		// this.completeAnnotationList = new ArrayList<Annotation>();
		// this.genericPrototype = false;
		// this.metaobjectAnnotationNumber = Annotation.firstAnnotationNumber;
		// this.moListBeforeExtendsMixinImplements = null;
		//
		// this.messageSendWithkeywordsToSuperList = new ArrayList<>() ;
		// this.nextFunctionNumber = 0;
		// this.innerPrototypeList = new ArrayList<ObjectDec>();
		// this.beforeEndNonAttachedAnnotationList = new ArrayList<>();
		// this.dslCompilationUnit = null;
		// this.immutable = false;
		// this.iPrototype = null;
		// this.this_and_all_superPrototypes = null;
	}

	public Prototype() {
	}

	public static void initPrototype(Prototype newPU, Token visibility,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList, ObjectDec outerObject) {

		newPU.visibility = visibility;
		newPU.nonAttachedAnnotationList = nonAttachedAnnotationList;
		newPU.attachedAnnotationList = attachedAnnotationList;
		newPU.genericParameterListList = new ArrayList<List<ast.GenericParameter>>();
		newPU.outerObject = outerObject;

		// genericProtoInstantiationList = new
		// ArrayList<ExprGenericPrototypeInstantiation>();
		newPU.prototypeIsNotGeneric = true;
		newPU.completeAnnotationList = new ArrayList<Annotation>();
		newPU.genericPrototype = false;
		newPU.metaobjectAnnotationNumber = Annotation.firstAnnotationNumber;
		newPU.moListBeforeExtendsMixinImplements = null;

		newPU.messageSendWithkeywordsToSuperList = new ArrayList<>();
		newPU.nextFunctionNumber = 0;
		newPU.innerPrototypeList = new ArrayList<ObjectDec>();
		newPU.beforeEndNonAttachedAnnotationList = new ArrayList<>();
		newPU.dslCompilationUnit = null;
		newPU.immutable = false;
		newPU.iPrototype = null;
		newPU.this_and_all_superPrototypes = null;
	}

	@Override
	public Prototype clone() {

		try {
			return (Prototype) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getCompilationUnit().getPackageName(),
				this.getName());
	}

	public boolean allFieldsInitializedInDeclaration() {
		return true;
	}

	public List<AnnotationAt> getAttachedAnnotationList() {
		return attachedAnnotationList;
	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void genCyan(PWInterface pw, CyanEnv cyanEnv, boolean genFunctions) {
		if ( nonAttachedAnnotationList != null ) {
			for (AnnotationAt c : nonAttachedAnnotationList)
				c.genCyan(pw, true, cyanEnv, genFunctions);
		}
		if ( attachedAnnotationList != null ) {
			for (AnnotationAt c : attachedAnnotationList)
				c.genCyan(pw, true, cyanEnv, genFunctions);
		}
	}

	/**
	 * prints in pw the program unit name in Cyan. It is Person if the prototype
	 * name is "Person" and "Stack<Person>" if the prototype is generic and it
	 * is being instantiated with a real parameter "Person"
	 *
	 * @param pw
	 * @param cyanEnv
	 */
	protected void genCyanPrototypeName(PWInterface pw, CyanEnv cyanEnv) {

		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			// this is a generic object instantiation. Then genCyan was called
			// to create a
			// prototype Stack<Int> because there is a "Stack<Int>" use in the
			// program
			// pw.print(cyanEnv.getPrototypeName());
			pw.print(symbol.getSymbolString());

			// if ( genericParameterListList.get(0).get(0).getPlus() ) {

			if ( genericParameterListList.size() > 0 ) {
				/**
				 * generic prototype with varying number of parameters
				 */
				for (List<String> realParamList : cyanEnv
						.getRealParamListList()) {
					pw.print("<");
					int size = realParamList.size();
					if ( size > 0 ) {
						for (String realParam : realParamList) {

							pw.print(Lexer.addSpaceAfterComma(realParam));
							--size;
							if ( size > 0 ) pw.print(", ");
						}
					}
					else {
						pw.print(MetaHelper.noneArgumentNameForFunctions);
					}
					pw.print(">");

				}
			}
			else {
				cyanEnv.error("Internal error at Prototype::genCyan");
			}

		}
		else {
			pw.print(symbol.getSymbolString());
			if ( genericParameterListList.size() > 0 ) {
				for (List<GenericParameter> gtList : genericParameterListList) {
					pw.print("<");
					int size = gtList.size();
					for (GenericParameter p : gtList) {
						p.genCyan(pw, false, cyanEnv, true);
						--size;
						if ( size > 0 ) pw.print(", ");
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

	abstract public FieldDec searchFieldPrivateProtectedSuperProtected(
			String varName);

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
	 * return true if this program unit is generic like "object
	 * Stack{@literal <}T> ... end". Return false otherwise, including if this
	 * program unit declares an instantiation of a generic prototype like
	 * "object Stack{@literal <}Int> ... end".
	 *
	 * @return
	 */
	public boolean isGeneric() {
		return !getPrototypeIsNotGeneric()
				&& genericParameterListList.size() > 0;
	}

	public void setVisibility(Token visibility) {
		this.visibility = visibility;
	}

	public Token getVisibility() {
		return visibility;
	}

	@Override
	public String getFullName() {
		return getCompilationUnit().getPackageIdent().getName() + "."
				+ getName();
	}

	@Override
	public String getFullName(Env env) {

		if ( this.fullName == null ) {
			String realName = symbol.getSymbolString();

			if ( genericParameterListList.size() > 0 ) {
				for (List<GenericParameter> gtList : genericParameterListList) {
					realName = realName + "<";
					int size = gtList.size();
					for (GenericParameter p : gtList) {
						String s = p.getFullName(env);
						if ( s == null ) env.error(
								p.getParameter().getFirstSymbol(),
								"Type was not found: '" + p.getName() + "'");
						realName = realName + s;
						--size;
						if ( size > 0 ) realName = realName + ",";
					}
					realName = realName + ">";
				}
			}
			CyanPackage cyanPackage = this.getCompilationUnit()
					.getCyanPackage();
			if ( cyanPackage.getPackageName()
					.equals(MetaHelper.cyanLanguagePackageName) )
				fullName = realName;
			else
				fullName = cyanPackage.getPackageName() + "." + realName;
		}
		return fullName;
	}

	/**
	 * return the name of the program unit. If it is a generic prototype, return
	 * the name without the parameters. Then if the program unit is
	 * <code>Stack{@literal <}Int></code>, this method returns
	 * <code>Stack</code>.
	 *
	 * @return
	 */
	public String getSimpleName() {
		return this.symbol.getSymbolString();
	}

	/**
	 * returns the prototype name. If it is "Person", "Person" is returned. If
	 * it is a generic prototype "Stack{@literal <}T>", "Stack{@literal <}T>" is
	 * returned. If it is an instantiated generic prototype
	 * "Stack{@literal <}main.Person>", "Stack{@literal <}main.Person>" is
	 * returned. <br>
	 * This method should not be called during semantic analysis because it does
	 * not give the correct name, with the packages.
	 *
	 * @return
	 */
	@Override
	public String getName() {
		if ( realName == null ) {
			realName = symbol.getSymbolString();

			if ( genericParameterListList.size() > 0 ) {
				for (List<GenericParameter> gtList : genericParameterListList) {
					realName = realName + "<";
					int size = gtList.size();
					for (GenericParameter p : gtList) {
						realName = realName + p.getName();
						--size;
						if ( size > 0 ) realName = realName + ",";
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
	 * return the name of this compilation unit preceded by its outer prototype,
	 * if any. The inner and outer prototypes are separated by '.'
	 */
	public String getNameWithOuter() {
		String protoName = getName();
		if ( getOuterObject() != null ) {
			protoName = getOuterObject().getName() + "." + protoName;
		}
		return protoName;
	}

	/**
	 * Assuming that this program unit is public, this method returns the name
	 * of the source file, without ".cyan", in which this program unit should
	 * be. Package cyan.lang is never used in the source file name.
	 *
	 *
	 * Examples:
	 *
	 * object Test<Int, main.Person> ... end returns
	 * "Test(Int,main.Person).cyan" object Test<cyan.lang.Int, main.Person> ...
	 * end returns "Test(Int,main.Person).cyan" object
	 * Test<cyan.lang.Tuple<cyan.lang.Int>,
	 * util.Stack<cyan.lang.Tuple<cyan.lang.Int, main.Person>>> ... end returns
	 * "Test(Tuple(Int),util.Stack(Tuple(Int,main.Person))).cyan" object
	 * Test<Int, main.Person> ... end returns "Test(Int,main.Person).cyan"
	 * object Test<Int, main.Person> ... end returns
	 * "Test(Int,main.Person).cyan" object Test<Int, main.Person> ... end
	 * returns "Test(Int,main.Person).cyan"
	 *
	 * @return
	 */

	// # stopped here.

	public String getNameSourceFile() {
		String realName = symbol.getSymbolString();

		if ( genericParameterListList.size() > 0 ) {
			for (List<GenericParameter> gtList : genericParameterListList) {
				int size = gtList.size();

				if ( gtList.get(0).isRealPrototype() ) {
					realName = realName + "(";
					for (GenericParameter p : gtList) {
						realName = realName + p.getNameSourceFile();
						--size;
						if ( size > 0 ) realName = realName + ",";
					}
					realName = realName + ")";
				}
				else {
					realName = realName + "(" + gtList.size();
					if ( gtList.get(0).getPlus() ) realName = realName + "+";
					realName = realName + ")";
				}

				/*
				 * String prototypeName =
				 * prototypeNameFromNameSourceFile(gtList.get(0).
				 * getNameSourceFile()); if (
				 * compilationUnit.getProgram().isInPackageCyanLang(
				 * prototypeName) || prototypeName.contains(".") ||
				 * Character.isLowerCase(prototypeName.charAt(0))) { // the
				 * first parameter is either a prototype of package cyan.lang or
				 * a prototype with its package as "main.Person" realName =
				 * realName + "("; for ( GenericParameter p : gtList ) { String
				 * s = prototypeNameFromNameSourceFile(p.getNameSourceFile());
				 * if ( compilationUnit.getProgram().isInPackageCyanLang(s) ||
				 * s.contains(".") ) { realName = realName +
				 * p.getNameSourceFile(); } else { // a symbol such as key in
				 * Tuple<key, String, value, Int> realName = realName + s; }
				 * --size; if ( size > 0 ) realName = realName + ","; } realName
				 * = realName + ")";
				 *
				 * } else { realName = realName + "(" + gtList.size() + ")"; }
				 */
			}

		}

		/*
		 * if ( isGeneric() ) { if ( genericParameterListList.size() > 0 ) { for
		 * ( List<GenericParameter> gtList : genericParameterListList ) {
		 * realName = realName + "(" + gtList.size() + ")"; } } } else { if (
		 * genericParameterListList.size() > 0 ) { // this is a generic object
		 * instantiation. Then genCyan was called to create a // prototype
		 * Stack<Int> because there is a "Stack<Int>" use in the program for (
		 * List<GenericParameter> gtList : genericParameterListList ) { realName
		 * = realName + "("; int size = gtList.size(); for ( GenericParameter p
		 * : gtList ) { realName = realName + p.getNameSourceFile(); --size; if
		 * ( size > 0 ) realName = realName + ","; } realName = realName + ")";
		 * } }
		 *
		 * }
		 */
		return realName.replace('|', '-');
	}

	@Override
	public String getJavaName() {

		if ( javaName == null ) {
			String name1 = getName();
			if ( this.compilationUnit.getPackageName()
					.equals(MetaHelper.cyanLanguagePackageName) ) {
				if ( this instanceof InterfaceDec ) {
					name1 = NameServer
							.prototypeFileNameFromInterfaceFileName(name1);
				}
				javaName = MetaHelper.getJavaName(name1);
			}
			else if ( this.outerObject == null ) {
				if ( this instanceof InterfaceDec ) {
					name1 = NameServer
							.prototypeFileNameFromInterfaceFileName(name1);
				}
				javaName = compilationUnit.getPackageName() + "."
						+ MetaHelper.getJavaName(name1);
			}
			else {
				javaName = MetaHelper.getJavaName(name1);
			}
		}
		return javaName;
	}

	public String getJavaNameWithoutPackage() {
		if ( javaNameWithoutPackage == null ) {
			javaNameWithoutPackage = MetaHelper.getJavaName(getName());
		}
		return javaNameWithoutPackage;
	}

	private String javaNameWithoutPackage;

	public Type getType() {
		return this;
	}

	/**
	 * calculates the type of all method parameters, all return values of
	 * methods of this program unit, and all fields. The types depends on the
	 * packages imported by the compilation unit of this program unit
	 */

	public void calcInternalTypes(ICompiler_semAn compiler_semAn, Env env) {

		if ( nonAttachedAnnotationList != null ) {
			for (AnnotationAt annotation : nonAttachedAnnotationList)
				annotation.calcInternalTypes(env);
		}
		WrPrototype wrpu = this.getI();
		for (AnnotationAt annotation : this.getCompilationUnit()
				.getCyanPackage().getAttachedAnnotationList()) {
			annotation.setDeclaration(wrpu);
			annotation.semAnActions(env);
		}
		for (AnnotationAt annotation : this.getCompilationUnit()
				.getCyanPackage().getProgram().getAttachedAnnotationList()) {
			annotation.setDeclaration(wrpu);
			annotation.semAnActions(env);
		}
		if ( this.getPrototypePackageProgramAnnotationList().size() > 0 ) {

			if ( attachedAnnotationList != null ) {
				for (AnnotationAt annotation : attachedAnnotationList) {
					annotation.calcInternalTypes(env);
				}

			}

			ObjectDec objDec = null;
			if ( this instanceof ObjectDec ) {
				objDec = (ObjectDec) this;

				// compiler_semAn.getEnv()
				meta.GetHiddenItem.getHiddenEnv(compiler_semAn.getEnv())
						.atBeginningOfObjectDec(objDec);
			}
			List<Annotation> annotList = this
					.getPrototypePackageProgramAnnotationList();
			this.setDeclarationImportedFromPackageProgram();
			for (Annotation annotation : annotList) {

				CyanMetaobject metaobject = annotation.getCyanMetaobject();
				_CyanMetaobject other = metaobject.getMetaobjectInCyan();
				// // metaobject.setAnnotation(annotation, 0);
				if ( metaobject instanceof ICheckPrototype_bsa || (other != null
						&& other instanceof _ICheckPrototype__bsa) ) {

					try {
						if ( other == null ) {
							ICheckPrototype_bsa fp = (ICheckPrototype_bsa) metaobject;
							fp.bsa_checkPrototype(compiler_semAn);
						}
						else {
							_ICheckPrototype__bsa fp = (_ICheckPrototype__bsa) metaobject;
							fp._bsa__checkPrototype_1(compiler_semAn);
						}
					}
					catch (error.CompileErrorException e) {
					}
					catch (NoClassDefFoundError e) {
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (RuntimeException e) {
						env.thrownException(annotation,
								annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobjectCatchExceptions(metaobject);
					}

				}

			}
			if ( objDec != null ) {
				// compiler_semAn.getEnv()
				meta.GetHiddenItem.getHiddenEnv(compiler_semAn.getEnv())
						.atEndOfObjectDec();
			}

		}

	}

	public void calcInternalTypesNONAttachedAnnotations(Env env) {
		if ( gpiList != null ) {
			for (ExprGenericPrototypeInstantiation elem : gpiList) {
				elem.calcInternalTypes(env);
			}
		}

		if ( nonAttachedAnnotationList != null ) {
			for (AnnotationAt annotation : nonAttachedAnnotationList)
				annotation.calcInternalTypes(env);
		}

		if ( this.outerObject == null )
			javaName = MetaHelper.getJavaName(this.getFullName(env));
		else
			javaName = MetaHelper.getJavaName(this.getName());

	}

	public void calcInterfaceTypes(Env env) {

		if ( attachedAnnotationList != null ) {
			for (AnnotationAt annotation : attachedAnnotationList)
				annotation.calcInternalTypes(env);
		}

		/*
		 * String otherName = ""; if ( this instanceof InterfaceDec ) { if (
		 * this.outerObject == null ) otherName =
		 * NameServer.getJavaName(this.getFullName(env)); else otherName =
		 * NameServer.getJavaName(this.getName());
		 *
		 * }
		 *
		 * // just to set javaName this.getJavaName();
		 */
	}

	public boolean getPrototypeIsNotGeneric() {
		return prototypeIsNotGeneric;
	}

	public void setPrototypeIsNotGeneric(boolean hasPrototypeInstantiation) {
		this.prototypeIsNotGeneric = hasPrototypeInstantiation;

	}

	public void addAnnotation(Annotation annotation) {
		if ( this.completeAnnotationList == null )
			completeAnnotationList = new ArrayList<Annotation>();
		completeAnnotationList.add(annotation);
	}

	/**
	 * a list of all metaobject annotations inside and before this program unit.
	 * This list includes attachedAnnotationList and
	 * beforeEndNonAttachedAnnotationList. Every metaobject annotation inside
	 * the program unit is in this list.
	 */
	public List<Annotation> getCompleteAnnotationList() {
		return completeAnnotationList;
	}

	public List<Annotation> getPrototypePackageProgramAnnotationList() {
		if ( prototypePackageProgramAnnotationList == null ) {
			prototypePackageProgramAnnotationList = new ArrayList<>();
			prototypePackageProgramAnnotationList
					.addAll(completeAnnotationList);
			List<AnnotationAt> annotList = this.getCompilationUnit()
					.getCyanPackage().getAttachedAnnotationList();
			if ( annotList != null ) {
				for (AnnotationAt withAt : annotList) {
					if ( !(withAt instanceof IAction_dpp) ) {
						prototypePackageProgramAnnotationList.add(withAt);
					}
				}
				// prototypePackageProgramAnnotationList.addAll( annotList );
			}
			annotList = this.getCompilationUnit().getCyanPackage().getProgram()
					.getAttachedAnnotationList();
			if ( annotList != null ) {
				for (AnnotationAt withAt : annotList) {
					if ( !(withAt instanceof IAction_dpp) ) {
						prototypePackageProgramAnnotationList.add(withAt);
					}
				}
				// prototypePackageProgramAnnotationList.addAll(annotList);
			}

		}
		return this.prototypePackageProgramAnnotationList;
	}

	public void setDeclarationImportedFromPackageProgram() {
		List<Annotation> annotList = this
				.getPrototypePackageProgramAnnotationList();
		WrPrototype wrpu = this.getI();

		for (Annotation withAtAnnot : annotList) {
			if ( withAtAnnot instanceof AnnotationAt
					&& ((AnnotationAt) withAtAnnot)
							.getOriginalDeclaration() != null ) {
				((AnnotationAt) withAtAnnot).setDeclaration(wrpu);
			}
		}
	}

	public void afterSemAn_checkDeclaration(
			ICompiler_semAn compiler_afterResTypes, Env env) {

		// this.checkInheritance_afterSemAn(env);

		List<Annotation> annotList = this
				.getPrototypePackageProgramAnnotationList();
		this.setDeclarationImportedFromPackageProgram();

		for (Annotation annotation : annotList) {

			CyanMetaobject metaobject = annotation.getCyanMetaobject();
			_CyanMetaobject other = metaobject.getMetaobjectInCyan();

			// // metaobject.setAnnotation(annotation, 0);
			if ( metaobject instanceof ICheckDeclaration_afterSemAn
					|| (other != null
							&& other instanceof _ICheckDeclaration__afterSemAn) ) {
				ICheckDeclaration_afterSemAn fp = (ICheckDeclaration_afterSemAn) metaobject;

				if ( annotation instanceof AnnotationAt ) {
					if ( ((AnnotationAt) annotation).getI() == null ) {
						env.error(annotation.getFirstSymbol(), "Metaobject '"
								+ metaobject.getName()
								+ "' should be attached to something like a method or prototype because it "
								+ "implements interface '"
								+ ICheckDeclaration_afterSemAn.class.getName()
								+ "'");
					}
				}

				int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(env,
						env.getProject().getProgram().getI(),
						env.getCurrentCompilationUnit().getCyanPackage().getI(),
						this.getFirstSymbol());

				Timeout<Object> to = new Timeout<>();

				try {
					if ( other == null ) {

						if ( Saci.timeLimitForMetaobjects ) {

							to.run(Executors.callable(() -> {
								fp.afterSemAn_checkDeclaration(
										compiler_afterResTypes);
							}), timeoutMilliseconds,
									"afterSemAn_checkDeclaration", metaobject,
									env);

						}
						else {

							fp.afterSemAn_checkDeclaration(
									compiler_afterResTypes);
						}

						// fp.afterSemAn_checkDeclaration(compiler_afterResTypes);
					}
					else {
						to.run(Executors.callable(() -> {
							((_ICheckDeclaration__afterSemAn) other)
									._afterSemAn__checkDeclaration_1(
											compiler_afterResTypes);
						}), timeoutMilliseconds, "afterSemAn_checkDeclaration",
								metaobject, env);
						// ((_ICheckDeclaration__afterSemAn )
						// other)._afterSemAn__checkDeclaration_1(compiler_afterResTypes);
					}
				}
				catch (error.CompileErrorException e) {
				}
				catch (NoClassDefFoundError e) {
					env.error(annotation.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (RuntimeException e) {
					// e.printStackTrace();
					env.thrownException(annotation, annotation.getFirstSymbol(),
							e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(metaobject);
				}

			}

		}

	}

	public void afterSemAn_check(ICompiler_semAn compiler_semAn, Env env) {

		List<Annotation> puAnnotationList = new ArrayList<>();
		if ( completeAnnotationList != null )
			puAnnotationList.addAll(completeAnnotationList);
		if ( nonAttachedAnnotationList != null )
			puAnnotationList.addAll(nonAttachedAnnotationList);

		env.atBeginningOfObjectDec(this);

		/**
		 * Meta: call method checkSubprototype for each metaobject that
		 * implements interface ICheckSubprototype_afterSemAn
		 */
		this.checkInheritance_afterSemAn(compiler_semAn, env);

		this.setDeclarationImportedFromPackageProgram();

		if ( this instanceof ObjectDec ) {
			ObjectDec proto = (ObjectDec) this;
			for (MethodDec method : proto.getMethodDecList()) {
				// meta.GetHiddenItem.getHiddenEnv(compiler_afterResTypes.getEnv()).atBeginningOfCurrentMethod(method);
				if ( method.getVisibility() == Token.PUBLIC
						&& method.getHasOverride() ) {
					env.setCurrentMethod(method);
					String methodName = method.getName();
					List<MethodSignature> superMS = new ArrayList<>();
					superMS.addAll(proto
							.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
									methodName, env));

					for (Expr interfaceExpr : proto.getInterfaceList()) {
						InterfaceDec interDec = (InterfaceDec) interfaceExpr
								.getType(env);
						List<MethodSignature> msL = interDec
								.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
										methodName, env);
						if ( msL != null && msL.size() > 0 ) {
							superMS.addAll(msL);
						}
					}
					for (MethodSignature ms : superMS) {
						List<AnnotationAt> superMSAttachedAnnotationList;
						MethodDec superMethod = ms.getMethod();
						if ( superMethod == null ) {
							/*
							 * method of an interface implemented by 'this' or
							 * 'proto' (they are equal)
							 */
							superMSAttachedAnnotationList = ms
									.getAttachedAnnotationList();
						}
						else {
							/*
							 * method of a superprototype of 'this' or 'proto'
							 */
							superMSAttachedAnnotationList = superMethod
									.getAttachedAnnotationList();
						}
						if ( superMSAttachedAnnotationList != null ) {
							for (AnnotationAt annotation : superMSAttachedAnnotationList) {
								CyanMetaobject metaobject = annotation
										.getCyanMetaobject();
								// // metaobject.setAnnotation(annotation, 0);
								_CyanMetaobject other = metaobject
										.getMetaobjectInCyan();
								if ( metaobject instanceof ICheckOverride_afterSemAn
										|| (other != null
												&& other instanceof _ICheckOverride__afterSemAn) ) {

									int timeoutMilliseconds = Timeout
											.getTimeoutMilliseconds(env,
													env.getProject()
															.getProgram()
															.getI(),
													env.getCurrentCompilationUnit()
															.getCyanPackage()
															.getI(),
													this.getFirstSymbol());

									Timeout<Object> to = new Timeout<>();

									try {
										if ( other == null ) {
											ICheckOverride_afterSemAn fp = (ICheckOverride_afterSemAn) metaobject;

											if ( Saci.timeLimitForMetaobjects ) {
												to.run(Executors
														.callable(() -> {
															fp.afterSemAn_checkOverride(
																	compiler_semAn,
																	method.getI());
														}), timeoutMilliseconds,
														"afterSemAn_checkOverride",
														metaobject, env);

											}
											else {
												fp.afterSemAn_checkOverride(
														compiler_semAn,
														method.getI());
											}

											// fp.afterSemAn_checkOverride(compiler_semAn,
											// method.getI());
										}
										else {
											_ICheckOverride__afterSemAn fp = (_ICheckOverride__afterSemAn) metaobject;
											to.run(Executors.callable(() -> {
												fp._afterSemAn__checkOverride_2(
														compiler_semAn,
														method.getI());
											}), timeoutMilliseconds,
													"afterSemAn_checkOverride",
													metaobject, env);
											// fp._afterSemAn__checkOverride_2(compiler_semAn,
											// method.getI());
										}
									}
									catch (error.CompileErrorException e) {
									}
									catch (NoClassDefFoundError e) {
										env.error(annotation.getFirstSymbol(), e
												.getMessage() + " "
												+ NameServer.messageClassNotFoundException);
									}
									catch (meta.InterpretationErrorException e) {
										// metaobject.addError(e.symbol,
										// e.message);
									}
									catch (RuntimeException e) {
										env.atEndOfObjectDec();
										env.thrownException(annotation,
												annotation.getFirstSymbol(), e);
									}
									finally {
										env.errorInMetaobject(metaobject,
												ms.getFirstSymbol());
									}
								}
							}
						}
					}
				}
				// meta.GetHiddenItem.getHiddenEnv(compiler_afterResTypes.getEnv()).atEndMethodDec();

			}
		}

		env.atEndOfObjectDec();

	}

	/**
	 * make the metaobject annotations "that acts" in this program unit
	 * communicate with each other any prototype whose name has a '<' in it, as
	 * <code>Set{@literal <}Int></code>, cannot communicate with other
	 * prototypes. The annotations "that act" in this program unit are those
	 * that are textually in this program unit and those attached to the package
	 * or the program unit and those of the program that implement interfaces
	 * {@link meta.IAction_afterResTypes} or
	 * {@link meta.IActionNewPrototypes_afterResTypes}
	 */
	protected void makeAnnotationsCommunicateInPrototype(
			List<Annotation> metaobjectAnnotationList, Env env) {

		/*
		 * every metaobject can supply information to other metaobjects. Every
		 * tuple in this set correspond to a metaobject annotation. Every tuple
		 * is composed of a metaobject name, the number of this metaobject
		 * considering all metaobjects in the prototype, the number of this
		 * metaobject considering only the metaobjects with the same name, and
		 * the information this metaobject annotation wants to share with other
		 * metaobject annotations.
		 */
		WrEnv wrEnv = env.getI();
		HashSet<Tuple4<String, Integer, Integer, Object>> moInfoSet = new HashSet<>();
		for (Annotation annotation : metaobjectAnnotationList) {
			CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			if ( cyanMetaobject instanceof ICommunicateInPrototype_afterResTypes_semAn_afterSemAn
					|| (other != null
							&& other instanceof _ICommunicateInPrototype__afterResTypes__semAn__afterSemAn) ) {
				// // cyanMetaobject.setAnnotation(annotation, 0);

				Object sharedInfo = null;

				if ( other == null ) {
					sharedInfo = ((ICommunicateInPrototype_afterResTypes_semAn_afterSemAn) cyanMetaobject)
							.afterResTypes_semAn_afterSemAn_shareInfoPrototype(
									wrEnv);
				}
				else {
					sharedInfo = ((_ICommunicateInPrototype__afterResTypes__semAn__afterSemAn) other)
							._afterResTypes__semAn__afterSemAn__shareInfoPrototype_1(
									wrEnv);
				}
				if ( sharedInfo != null ) {
					if ( this.genericParameterListList.size() == 0 ) {
						Tuple4<String, Integer, Integer, Object> t = new Tuple4<>(
								cyanMetaobject.getName(),
								annotation.getAnnotationNumber(),
								annotation.getAnnotationNumberByKind(),
								sharedInfo);
						moInfoSet.add(t);
					}
					else {
						env.error(true, annotation.getFirstSymbol(),
								"metaobject annotation of metaobject '"
										+ annotation.getCyanMetaobject()
												.getName()
										+ "' is trying to communicate with other metaobjects of the package. "
										+ "This is prohibit because this metaobject is a generic prototype instantiation or has a '<' in its name",
								null, ErrorKind.metaobject_error);
					}

				}

			}
		}
		/*
		 * send information to all annotations of this program unit. Let them
		 * communicate with each other
		 */
		if ( moInfoSet.size() > 0 ) {
			for (Annotation annotation : metaobjectAnnotationList) {
				CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
				_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();

				if ( cyanMetaobject instanceof ICommunicateInPrototype_afterResTypes_semAn_afterSemAn
						|| (other != null
								&& other instanceof _ICommunicateInPrototype__afterResTypes__semAn__afterSemAn) ) {
					// // cyanMetaobject.setAnnotation(annotation, 0);
					if ( other == null ) {
						((ICommunicateInPrototype_afterResTypes_semAn_afterSemAn) cyanMetaobject)
								.afterResTypes_semAn_afterSemAn_receiveInfoPrototype(
										moInfoSet, wrEnv);
					}
					else {
						_Set_LT_GP__Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT_GT tupleSet = new _Set_LT_GP__Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT_GT();

						for (Tuple4<String, Integer, Integer, Object> elem : moInfoSet) {
							tupleSet._add_1(
									new _Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT(
											new CyString(elem.f1),
											new CyInt(elem.f2),
											new CyInt(elem.f3), elem.f4));
						}
						((_ICommunicateInPrototype__afterResTypes__semAn__afterSemAn) other)
								._afterResTypes__semAn__afterSemAn__receiveInfoPrototype_2(
										tupleSet, wrEnv);
					}

				}
			}
		}
	}

	// private void afterResTypes_actions(ICompiler_afterResTypes
	// compiler_afterResTypes, CompilerManager_afterResTypes compilerManager) {
	//
	// Env env =
	// meta.GetHiddenItem.getHiddenEnv(compiler_afterResTypes.getEnv());
	// env.atBeginningOfObjectDec(this);
	//
	//
	// makeAnnotationsCommunicateInPrototype(env);
	//
	//
	// /*
	// * true if this prototype has a {@literal <} in its name
	// */
	// boolean hasLessThanInName = getName().indexOf('<') >= 0 ;
	//
	// List<Annotation> metaobjectAnnotationList = completeAnnotationList;
	//
	//
	// for ( Annotation cyanAnnotation : metaobjectAnnotationList ) {
	//
	// /*
	// * if the metaobject annotation has a suffix greater or equal to
	// "AFTER_RES_TYPES" then the actions below
	// * have already been taken (the compiler changed the suffix to
	// "AFTER_RES_TYPES" or greater) or
	// * they should not be taken (the original program uses a suffix in the
	// metaobject annotation
	// * of "AFTER_RES_TYPES" or greater).
	// */
	// if ( cyanAnnotation.getPostfix() == null ||
	// cyanAnnotation.getPostfix().lessThan(CompilerPhase.AFTER_RES_TYPES) ) {
	//
	// addCodeAndSlotsTo(compiler_afterResTypes, compilerManager, env,
	// hasLessThanInName, cyanAnnotation);
	//
	//
	// }
	//
	// }
	//
	// env.atEndOfObjectDec();
	// }

	public void afterResTypes_actions(
			ICompiler_afterResTypes compiler_afterResTypes,
			CompilerManager_afterResTypes compilerManager) {

		Env env = meta.GetHiddenItem
				.getHiddenEnv(compiler_afterResTypes.getEnv());
		env.atBeginningOfObjectDec(this);

		WrProgram program = compiler_afterResTypes.getEnv().getProject()
				.getProgram();
		WrCyanPackage cpackage = compiler_afterResTypes.getCompilationUnit()
				.getCyanPackage();
		int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(env, program,
				cpackage, this.getFirstSymbol());

		List<Annotation> metaobjectAnnotationList = this
				.getPrototypePackageProgramAnnotationList();
		this.setDeclarationImportedFromPackageProgram();

		makeAnnotationsCommunicateInPrototype(metaobjectAnnotationList, env);

		/*
		 * metaobjects may add code before the first statement of methods. This
		 * is a map containing pair of the form [. methodName, value .] in which
		 * some metaobject added code to the beginning of methodName. 'value' is
		 * true if the metaobject demanded exclusive rights for adding code.
		 */
		Map<String, Boolean> mapMethodNameExclusive = new HashMap<>();
		//
		// List<Annotation> metaobjectAnnotationList = new ArrayList<>();
		// metaobjectAnnotationList.addAll(completeAnnotationList);
		//
		// metaobjectAnnotationList.addAll(
		// this.getCompilationUnit().getCyanPackage().getAttachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes());
		//
		// metaobjectAnnotationList.addAll(
		// this.getCompilationUnit().getCyanPackage().getProgram().getAttachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes());
		//
		//
		//

		// boolean hasLessThanInName = getName().indexOf('<') >= 0 ;

		/**
		 * list of tuples, each composed by an annotation and a list of objects
		 * of WrMethodSignature and WrFieldDec. The metaobject associated to the
		 * annotation produces the methods and fields corresponding to the
		 * objects in the list. This list is passed as parameter to all
		 * metaobjects when method
		 * {@link IAction_afterResTypes.afterResTypes_codeToAdd} is called.
		 */
		List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList = null;

		/**
		 * list of tuples, each consisting of an annotation, code to be added to
		 * the prototype (fields and methods) and the list of field declarations
		 * and interfaces of methods to be added. This last list can be, for
		 * example,<br>
		 * <code>
		 *     var Int count;
		 *     func at: Int n put: String -> Int
		 *     func unary
		 *     var String output;
		 * </code>
		 */
		List<Tuple4<Annotation, StringBuffer, List<ISlotSignature>, String>> runManyList = null,
				runOnceList = null;

		List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoListFist = new ArrayList<>();

		for (Annotation annot : metaobjectAnnotationList) {

			/*
			 * if the metaobject annotation has a suffix greater or equal to
			 * "AFTER_RES_TYPES" then the actions below have already been taken
			 * (the compiler changed the suffix to "AFTER_RES_TYPES" or greater)
			 * or they should not be taken (the original program uses a suffix
			 * in the metaobject annotation of "AFTER_RES_TYPES" or greater).
			 */
			if ( annot.getPostfix() != null && !annot.getPostfix()
					.lessThan(CompilerPhase.AFTER_RES_TYPES) ) {
				continue;
			}

			CyanMetaobject cyanMetaobject = annot.getCyanMetaobject();
			// CyanMetaobjectAtAnnot cyanMetaobjectWithAt =
			// (CyanMetaobjectAtAnnot ) cyanMetaobject;

			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			if ( cyanMetaobject instanceof IActionNewPrototypes_afterResTypes
					|| (other != null
							&& other instanceof _IActionNewPrototypes__afterResTypes) ) {
				CompilerManager.createNewPrototypes(compiler_afterResTypes, env,
						annot, cyanMetaobject, this.compilationUnit);
			}

			if ( cyanMetaobject instanceof IAction_afterResTypes
					|| (other != null
							&& other instanceof _IAction__afterResTypes) ) {
				String packageName = this.compilationUnit.getCyanPackage()
						.getPackageName();

				List<Tuple2<String, String[]>> renameMethodList = null;
				List<ISlotSignature> slotList = null;

				/**
				 * add code to the current prototype, it may be composed of
				 * fields and methods
				 */
				Tuple2<StringBuffer, String> codeSlot = null;
				try {

					if ( other == null ) {
						Timeout<Tuple2<StringBuffer, String>> to = new Timeout<>();
						codeSlot = to.run(
								() -> ((IAction_afterResTypes) cyanMetaobject)
										.afterResTypes_codeToAdd(
												compiler_afterResTypes,
												infoListFist),
								timeoutMilliseconds, "afterResTypes_codeToAdd",
								cyanMetaobject, env);

						if ( Saci.timeLimitForMetaobjects ) {
							codeSlot = to.run(
									() -> ((IAction_afterResTypes) cyanMetaobject)
											.afterResTypes_codeToAdd(
													compiler_afterResTypes,
													infoListFist),
									timeoutMilliseconds,
									"afterResTypes_codeToAdd", cyanMetaobject,
									env);

						}
						else {
							codeSlot = ((IAction_afterResTypes) cyanMetaobject)
									.afterResTypes_codeToAdd(
											compiler_afterResTypes,
											infoListFist);

						}

						String errorMessage = to.getErrorMessage();
						if ( errorMessage != null ) {
							env.error(annot.getFirstSymbol(), errorMessage);
						}

						// codeSlot = ((IAction_afterResTypes ) cyanMetaobject)
						// .afterResTypes_codeToAdd(compiler_afterResTypes,
						// infoListFist);
					}
					else {
						// _Tuple_LT_GP_CyString_GP_CyString_GT t =
						// ((_IAction__afterResTypes ) other)
						// ._afterResTypes__codeToAdd_2(compiler_afterResTypes,
						// new
						// _Array_LT_GP__Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT_GT_GT());
						Timeout<_Tuple_LT_GP_CyString_GP_CyString_GT> to = new Timeout<>();
						_Tuple_LT_GP_CyString_GP_CyString_GT t = to.run(
								() -> ((_IAction__afterResTypes) other)
										._afterResTypes__codeToAdd_2(
												compiler_afterResTypes,
												new _Array_LT_GP__Tuple_LT_GP_meta_p_WrAnnotation_GP__Array_LT_GP_meta_p_ISlotSignature_GT_GT_GT()),
								timeoutMilliseconds, "afterResTypes_codeToAdd",
								cyanMetaobject, env);

						if ( t._f2().s.length() != 0 ) {
							codeSlot = new Tuple2<StringBuffer, String>(
									new StringBuffer(t._f1().s), t._f2().s);
						}
					}
					if ( codeSlot == null ) {

						Timeout<Boolean> to = new Timeout<>();
						if ( cyanMetaobject instanceof IAction_afterResTypes ) {
							boolean runUntilFixedPoint = to.run(
									() -> ((IAction_afterResTypes) cyanMetaobject)
											.runUntilFixedPoint(),
									timeoutMilliseconds, "runUntilFixedPoint",
									cyanMetaobject, env);
							// boolean runUntilFixedPoint =
							// ((IAction_afterResTypes )
							// cyanMetaobject).runUntilFixedPoint();
							if ( runUntilFixedPoint ) {
								env.error(annot.getFirstSymbol(),
										"Method for generating code in phase AFTER_RES_TYPES of "
												+ "metaobject of annotation '"
												+ cyanMetaobject.getName()
												+ "' returned null. This is illegal because"
												+ " method 'runUntilFixedPoint()' of this metaobject returned true");
							}
						}
						else if ( other != null ) {
							boolean runUntilFixedPoint = to.run(
									() -> ((_IAction__afterResTypes) other)
											._runUntilFixedPoint(),
									timeoutMilliseconds, "runUntilFixedPoint",
									cyanMetaobject, env);

							// boolean runUntilFixedPoint =
							// ((_IAction__afterResTypes )
							// other)._runUntilFixedPoint();
							if ( runUntilFixedPoint ) {
								env.error(annot.getFirstSymbol(),
										"Method for generating code in phase AFTER_RES_TYPES of "
												+ "metaobject of annotation '"
												+ cyanMetaobject.getName()
												+ "' returned null. This is illegal because"
												+ " method 'runUntilFixedPoint()' of this metaobject returned true");
							}
						}
					}
					else {
						slotList = extractSlotListFrom(codeSlot.f2, annot, env,
								annot.getFirstSymbol());

						Tuple4<Annotation, StringBuffer, List<ISlotSignature>, String> t = new Tuple4<Annotation, StringBuffer, List<ISlotSignature>, String>(
								annot, codeSlot.f1, slotList, codeSlot.f2);
						if ( cyanMetaobject instanceof IAction_afterResTypes ) {
							Timeout<Boolean> to = new Timeout<>();
							boolean runUntilFixedPoint = to.run(
									() -> ((IAction_afterResTypes) cyanMetaobject)
											.runUntilFixedPoint(),
									timeoutMilliseconds, "runUntilFixedPoint",
									cyanMetaobject, env);

							// boolean runUntilFixedPoint =
							// ((IAction_afterResTypes )
							// cyanMetaobject).runUntilFixedPoint();
							if ( runUntilFixedPoint ) {
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
							Timeout<Boolean> to = new Timeout<>();
							boolean runUntilFixedPoint = to.run(
									() -> ((_IAction__afterResTypes) other)
											._runUntilFixedPoint(),
									timeoutMilliseconds, "runUntilFixedPoint",
									cyanMetaobject, env);

							// boolean runUntilFixedPoint =
							// ((_IAction__afterResTypes )
							// other)._runUntilFixedPoint();

							if ( runUntilFixedPoint ) {
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
						infoList.add(
								new Tuple2<WrAnnotation, List<ISlotSignature>>(
										annot.getI(), slotList));
					}
				}
				catch (error.CompileErrorException e) {
				}
				catch (NoClassDefFoundError e) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (RuntimeException e) {
					env.thrownException(annot, annot.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(cyanMetaobject);
				}

				// boolean canCommunicateInPackage =
				// this.compilationUnit.getCyanPackage().getCommunicateInPackage();

				/**
				 * add statements to the beginning of methods. This was asked by
				 * the metaobjects of this program unit.
				 */
				List<Tuple3<String, StringBuffer, Boolean>> statsList = null;

				try {
					if ( cyanMetaobject instanceof IAction_afterResTypes ) {
						Timeout<List<Tuple3<String, StringBuffer, Boolean>>> to = new Timeout<>();

						if ( Saci.timeLimitForMetaobjects ) {
							statsList = to.run(
									() -> ((IAction_afterResTypes) cyanMetaobject)
											.afterResTypes_beforeMethodCodeList(
													compiler_afterResTypes),
									timeoutMilliseconds,
									"afterResTypes_beforeMethodCodeList",
									cyanMetaobject, env);

						}
						else {
							statsList = ((IAction_afterResTypes) cyanMetaobject)
									.afterResTypes_beforeMethodCodeList(
											compiler_afterResTypes);

						}

						// statsList = ((IAction_afterResTypes )
						// cyanMetaobject).afterResTypes_beforeMethodCodeList(compiler_afterResTypes);
					}
					else if ( other != null ) {
						Timeout<_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT> to = new Timeout<>();
						_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT array = to
								.run(() -> ((_IAction__afterResTypes) other)
										._afterResTypes__beforeMethodCodeList_1(
												compiler_afterResTypes),
										timeoutMilliseconds,
										"afterResTypes_beforeMethodCodeList",
										cyanMetaobject, env);

						// _Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT
						// array =
						// ((_IAction__afterResTypes )
						// other)._afterResTypes__beforeMethodCodeList_1(
						// compiler_afterResTypes);
						/*
						 * cast Array<Tuple<String, String>> to
						 * List<Tuple2<String, StringBuffer>>
						 */
						statsList = MetaHelper
								.cyanArrayTupleStringStringBoolean_toJava(
										array);
					}
				}
				catch (error.CompileErrorException e) {
				}
				catch (NoClassDefFoundError e) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (RuntimeException e) {
					env.thrownException(annot, annot.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(cyanMetaobject);
				}

				if ( statsList != null ) {
					for (Tuple3<String, StringBuffer, Boolean> t : statsList) {
						String prototypeName = this.getName();
						// if ( hasLessThanInName || prototypeName.indexOf('<')
						// >= 0 ) {
						// if ( !
						// this.getCompilationUnit().getPackageName().equals(packageName)
						// ||
						// ! this.getName().equals(prototypeName) ) {
						// env.error(true,
						// annot.getFirstSymbol(),
						// "This metaobject annotation is trying to code to "
						// + "a method of a prototype it does not have
						// permission "
						// + "to. Only a generic prototype can add code to
						// itself "
						// + "and only to itself", cyanMetaobject.getName(),
						// ErrorKind.metaobject_error);
						// }
						// }
						// String thisPrototypeName = this.getName();
						//
						// if ( !canCommunicateInPackage && !
						// prototypeName.equals(thisPrototypeName) ) {
						// env.error(annot.getFirstSymbol(), "This metaobject
						// annotation is in prototype '" + packageName + "." +
						// thisPrototypeName + "' and it is trying to add code
						// to another prototype, '" + packageName + "." +
						// prototypeName + "'. This is illegal because package
						// '" + packageName + "' does not allow that. To make
						// that " +
						// "legal, attach '@feature(communicateInPackage, #on)'
						// to the package in the project (.pyan) file", true,
						// false);
						// }
						// else {
						Boolean exclusive = mapMethodNameExclusive.get(t.f1);
						if ( exclusive != null && (exclusive || t.f3) ) {
							/*
							 * error: a method object demanded exclusive rights
							 * to add code at the beginning of method t.f1 but
							 * another metaobject is trying to add code too
							 *
							 */
							env.error(annot.getFirstSymbol(),
									"Two metaobjects are trying to add code at the "
											+ "beginning of method '" + t.f1
											+ "'. However, at least one of them demanded"
											+ " exclusive rights of doing so. This is an error. Either none "
											+ "should ask exclusive rights or only one metaobject should ask to add code "
											+ "at the beginning of this method");
							return;
						}
						compilerManager.addBeforeMethod(cyanMetaobject,
								packageName, prototypeName, t.f1, t.f2);
						mapMethodNameExclusive.put(t.f1, t.f3);
						// }
					}
				}

				/**
				 * rename methods
				 */

				try {
					if ( cyanMetaobject instanceof IAction_afterResTypes ) {
						Timeout<List<Tuple2<String, String[]>>> to = new Timeout<>();

						if ( Saci.timeLimitForMetaobjects ) {
							renameMethodList = to.run(
									() -> ((IAction_afterResTypes) cyanMetaobject)
											.afterResTypes_renameMethod(
													compiler_afterResTypes),
									timeoutMilliseconds,
									"afterResTypes_renameMethod",
									cyanMetaobject, env);

						}
						else {
							renameMethodList = ((IAction_afterResTypes) cyanMetaobject)
									.afterResTypes_renameMethod(
											compiler_afterResTypes);

						}
						// renameMethodList = ((IAction_afterResTypes )
						// cyanMetaobject).afterResTypes_renameMethod(compiler_afterResTypes);
					}
					else if ( other != null ) {
						Timeout<_Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT> to = new Timeout<>();

						_Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT array = to
								.run(() -> ((_IAction__afterResTypes) other)
										._afterResTypes__renameMethod_1(
												compiler_afterResTypes),
										timeoutMilliseconds,
										"afterResTypes_renameMethod",
										cyanMetaobject, env);

						// _Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT
						// array =
						// ((_IAction__afterResTypes )
						// other)._afterResTypes__renameMethod_1(compiler_afterResTypes);
						// List<Tuple2<String, String[]>>
						int size = array._size().n;
						if ( size != 0 ) {
							renameMethodList = new ArrayList<Tuple2<String, String[]>>();
							// Array<Tuple<String, Array<String>>>
							for (int k = 0; k < size; ++k) {
								_Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT t = array
										._at_1(new CyInt(k));
								_Array_LT_GP_CyString_GT strArray = t._f2();
								int sizeStrArray = strArray._size().n;
								String[] javaStrArray = null;
								if ( sizeStrArray != 0 ) {
									javaStrArray = new String[sizeStrArray];
									for (int j = 0; j < sizeStrArray; ++j) {
										javaStrArray[j] = strArray
												._at_1(new CyInt(j)).s;
									}
								}
								renameMethodList.add(
										new Tuple2<String, String[]>(t._f1().s,
												javaStrArray));
							}

						}
					}
				}
				catch (error.CompileErrorException e) {
				}
				catch (NoClassDefFoundError e) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (RuntimeException e) {
					env.thrownException(annot, annot.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(cyanMetaobject);
				}

				if ( renameMethodList != null ) {
					/*
					 * List<Tuple2<String, String []>> renameMethodList = null;
					 * List<ISlotSignature> slotList = null;
					 *
					 */
					for (Tuple2<String, String[]> t : renameMethodList) {
						String prototypeName = this.getName();
						/*
						 * check if the method to be renamed, t.f1, will be
						 * replaced by another method of the list slotList
						 */
						if ( slotList == null ) {
							env.error(annot.getFirstSymbol(),
									"Metaobject of annotation '"
											+ cyanMetaobject.getName()
											+ "' is renaming method '" + t.f1
											+ "'. It should create another method with this name. But it is not.");
						}
						else {
							boolean found = false;
							for (ISlotSignature slot : slotList) {
								if ( slot instanceof WrMethodSignature ) {
									if ( t.f1.equals(((WrMethodSignature) slot)
											.getName()) ) {
										found = true;
										break;
									}
								}
							}
							if ( !found ) {
								String s = "";
								int size = slotList.size();
								for (ISlotSignature slot : slotList) {
									if ( slot instanceof WrMethodSignature ) {
										s += ((WrMethodSignature) slot)
												.getName();
									}
									if ( --size > 0 ) {
										s += ", ";
									}
								}
								env.error(annot.getFirstSymbol(),
										"Metaobject of annotation '"
												+ cyanMetaobject.getName()
												+ "' is renaming method '"
												+ t.f1
												+ "'. It should create another method with this name. "
												+ "But it is not. The methods it is creating are: [ "
												+ s + " ]");

							}
						}
						// if ( hasLessThanInName || prototypeName.indexOf('<')
						// >= 0 ) {
						// if ( !
						// this.getCompilationUnit().getPackageName().equals(packageName)
						// ||
						// ! this.getName().equals(prototypeName) ) {
						// env.error(true,
						// annot.getFirstSymbol(),
						// "This metaobject annotation is trying to rename
						// methods of a "
						// + "prototype it does not have permission to. Only a
						// generic prototype "
						// + "can add code to itself and only to itself",
						// cyanMetaobject.getName(),
						// ErrorKind.metaobject_error);
						// }
						// }
						// String thisPrototypeName = this.getName();
						//
						// if ( !canCommunicateInPackage && !
						// prototypeName.equals(thisPrototypeName) ) {
						// env.error(annot.getFirstSymbol(), "This metaobject
						// annotation is in prototype '" + packageName + "." +
						// thisPrototypeName + "' and it is trying to rename a
						// method of another prototype, '" + packageName + "." +
						// prototypeName + "'. This is illegal because package
						// '" + packageName + "' does not allow that. To make
						// that" +
						// "legal, attach '@feature(communicateInPackage, #on)'
						// to the package in the project (.pyan) file", true,
						// false);
						// }
						// else {
						compilerManager.renameMethods(cyanMetaobject,
								packageName, prototypeName, t.f1, t.f2);
						// }

					}
				}

			}

		}
		/*
		 * if repeatLoop { somethingChanged = true; count = 0; while
		 * somethingChanged { somethingChanged = false; for each metaobject mo
		 * of runManyList { if addcode returns non-null { replace the
		 * information on runManyList by the new information. That includes the
		 * slotList and the code to be added. somethingChanged = true; } }
		 * ++count; if somethingChanged && count > 5 { error } } } insert the
		 * code of runManyList and runOnceList into the code.
		 */
		if ( runManyList != null && runManyList.size() > 0 ) {
			int count = 0;

			int maxNumRounds = getMaxNumRounds(env);

			// # env.getProject().getPackageList();

			boolean somethingChanged = true;
			while (somethingChanged) {
				somethingChanged = false;
				for (Tuple4<Annotation, StringBuffer, List<ISlotSignature>, String> elemRunManyList : runManyList) {
					Annotation annot = elemRunManyList.f1;
					CyanMetaobject cyanMetaobject = annot.getCyanMetaobject();
					_CyanMetaobject other = cyanMetaobject
							.getMetaobjectInCyan();

					Tuple2<StringBuffer, String> codeSlot = null;
					try {
						/*
						 *
						 */

						List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoListFinal = infoList;

						if ( other == null ) {
							Timeout<Tuple2<StringBuffer, String>> to = new Timeout<>();
							if ( Saci.timeLimitForMetaobjects ) {
								codeSlot = to.run(
										() -> ((IAction_afterResTypes) cyanMetaobject)
												.afterResTypes_codeToAdd(
														compiler_afterResTypes,
														infoListFist),
										timeoutMilliseconds,
										"afterResTypes_codeToAdd",
										cyanMetaobject, env);

							}
							else {
								codeSlot = ((IAction_afterResTypes) cyanMetaobject)
										.afterResTypes_codeToAdd(
												compiler_afterResTypes,
												infoListFist);

							} // codeSlot = ((IAction_afterResTypes )
								// cyanMetaobject)
								// .afterResTypes_codeToAdd(compiler_afterResTypes,
								// infoList);
						}
						else {
							// from List<Tuple2<WrAnnotation,
							// List<ISlotSignature>>>
							// to
							// _Array_LT_GP__Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT_GT_GT
							_Array_LT_GP__Tuple_LT_GP_meta_p_WrAnnotation_GP__Array_LT_GP_meta_p_ISlotSignature_GT_GT_GT x;

							_Array_LT_GP__Tuple_LT_GP_meta_p_WrAnnotation_GP__Array_LT_GP_meta_p_ISlotSignature_GT_GT_GT arrayTuple = new _Array_LT_GP__Tuple_LT_GP_meta_p_WrAnnotation_GP__Array_LT_GP_meta_p_ISlotSignature_GT_GT_GT();
							// _Array_LT_GP__Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT_GT_GT
							// arrayTuple =
							// new
							// _Array_LT_GP__Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT_GT_GT();

							if ( infoList != null ) {
								for (Tuple2<WrAnnotation, List<ISlotSignature>> t : infoList) {
									_Array_LT_GP_meta_p_ISlotSignature_GT arraySlot = new _Array_LT_GP_meta_p_ISlotSignature_GT();
									// _Array_LT_GP_Object_GT arraySlot = new
									// _Array_LT_GP_Object_GT();
									for (ISlotSignature slotInter : t.f2) {
										arraySlot._add_1(slotInter);
									}
									// _Tuple_LT_GP__meta_p_WrCyanAnnotation_GP__Array_LT_GP__meta_p_ISlotSignature_GT_GT
									arrayTuple._add_1(
											new _Tuple_LT_GP_meta_p_WrAnnotation_GP__Array_LT_GP_meta_p_ISlotSignature_GT_GT(
													t.f1, arraySlot));

									// arrayTuple._add_1(
									// new
									// _Tuple_LT_GP_Object_GP__Array_LT_GP_Object_GT_GT(t.f1,
									// arraySlot)
									// );
								}
							}
							Timeout<_Tuple_LT_GP_CyString_GP_CyString_GT> to = new Timeout<>();

							_Tuple_LT_GP_CyString_GP_CyString_GT t = to.run(
									() -> ((_IAction__afterResTypes) other)
											._afterResTypes__codeToAdd_2(
													compiler_afterResTypes,
													arrayTuple),
									timeoutMilliseconds,
									"afterResTypes_codeToAdd", cyanMetaobject,
									env);

							// _Tuple_LT_GP_CyString_GP_CyString_GT t =
							// ((_IAction__afterResTypes ) other)
							// ._afterResTypes__codeToAdd_2(compiler_afterResTypes,
							// arrayTuple);
							String codeReturned = t._f2().s;
							if ( codeReturned.length() != 0
									&& !codeReturned.matches("[ \r\n\t]*") ) {
								codeSlot = new Tuple2<StringBuffer, String>(
										new StringBuffer(t._f1().s),
										codeReturned);
							}
						}

						if ( codeSlot == null ) {
							env.error(annot.getFirstSymbol(),
									"Method to generate code in phase AFTER_RES_TYPES of "
											+ "metaobject of annotation '"
											+ cyanMetaobject.getName()
											+ "' returned null. This is illegal because"
											+ " method 'runUntilFixedPoint()' of this metaobject returned true");
						}
						else {
							if ( !elemRunManyList.f2.toString()
									.equals(codeSlot.f1.toString()) ) {
								// produces code that is different from the
								// previous call
								elemRunManyList.f2 = codeSlot.f1;
								List<ISlotSignature> slotList = extractSlotListFrom(
										codeSlot.f2, annot, env,
										annot.getFirstSymbol());
								elemRunManyList.f3 = slotList;
								somethingChanged = true;
								// update infoList
								if ( infoList != null ) {
									for (Tuple2<WrAnnotation, List<ISlotSignature>> t : infoList) {
										if ( t.f1 == elemRunManyList.f1
												.getI() ) {
											t.f2 = slotList;
											break;
										}
									}
								}
							}
						}
					}
					catch (error.CompileErrorException e) {
					}
					catch (NoClassDefFoundError e) {
						env.error(annot.getFirstSymbol(), e.getMessage() + " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (RuntimeException e) {
						env.thrownException(annot, annot.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobjectCatchExceptions(cyanMetaobject);
					}

				}
				++count;
				if ( somethingChanged && count > maxNumRounds ) {
					env.error(this.getFirstSymbol(),
							"The metaobjects of this compilation unit demand "
									+ "information on other metaobjects in a possible infinite way. For example, one cause a change "
									+ "in the code produced by other that cause a change in the code"
									+ " produced by the first and so on. If the compiled did not sign this"
									+ " error, there could be an infinite recursion");
				}

			}
		}

		String prototypeName = this.getName();
		String packageName = this.compilationUnit.getCyanPackage()
				.getPackageName();

		if ( runOnceList != null ) {
			for (Tuple4<Annotation, StringBuffer, List<ISlotSignature>, String> t : runOnceList) {
				compilerManager.addCode(t.f1.getCyanMetaobject(), packageName,
						prototypeName, t.f2, t.f4);
			}
		}
		if ( runManyList != null ) {
			for (Tuple4<Annotation, StringBuffer, List<ISlotSignature>, String> t : runManyList) {
				compilerManager.addCode(t.f1.getCyanMetaobject(), packageName,
						prototypeName, t.f2, t.f4);
			}
		}

		/*
		 * if repeatLoop { somethingChanged = true; count = 0; while
		 * somethingChanged { copy infoList into newInfoList; somethingChanged =
		 * false; for each metaobject mo { if runUntilFixedPoint returns true {
		 * if addcode returns non-null { replace the information on newInfoList
		 * by the new information. That includes the slotList and the code to be
		 * added. somethingChanged = true; } } } infoList = newInfoList;
		 * newInfoList = new List<>(); ++count; if somethingChanged && count > 5
		 * { error } } } insert the code of infoList into the code.
		 */

		env.atEndOfObjectDec();
	}

	/**
	 * @param env
	 * @param maxNumRounds
	 * @return
	 */
	private static int getMaxNumRounds(Env env) {
		int maxNumRounds = MetaHelper.maxNumRoundsFixMetaDefaultValue;

		Object obj = env.getCurrentCompilationUnit().getCyanPackage()
				.getPackageValueFromKey(MetaHelper.maxnumroundsfixmetastr);
		if ( obj != null ) {
			if ( obj instanceof Integer ) {
				maxNumRounds = (Integer) obj;
			}
			else {
				env.error(env.getCurrentPrototype().getFirstSymbol(),
						"Internal error: variable "
								+ MetaHelper.maxnumroundsfixmetastr + "'"
								+ " should have an Integer value");
			}
		}
		else {
			obj = env.getProject()
					.getProgramValueFromKey(MetaHelper.maxnumroundsfixmetastr);
			if ( obj != null ) {
				if ( obj instanceof Integer ) {
					maxNumRounds = (Integer) obj;
				}
				else {
					env.error(env.getCurrentPrototype().getFirstSymbol(),
							"Internal error: variable "
									+ MetaHelper.maxnumroundsfixmetastr + "'"
									+ " should have an Integer value");
				}
			}
		}
		return maxNumRounds;
	}

	static public void callCatch(Runnable runnable,
			CyanMetaobject cyanMetaobject, Annotation annot, Env env) {
		try {
			runnable.run();
		}
		catch (meta.MetaSecurityException e) {
			env.error(annot.getFirstSymbol(),
					"There was a security error when calling a method of metaobject  '"
							+ cyanMetaobject.getName()
							+ "' The metaobject may have tried to access a "
							+ "resource (the statements of a method or the list of fields) it does not "
							+ "have permission to access");
		}
		catch (error.CompileErrorException e) {
		}
		catch (NoClassDefFoundError e) {
			env.error(annot.getFirstSymbol(), e.getMessage() + " "
					+ NameServer.messageClassNotFoundException);
		}
		catch (RuntimeException e) {
			env.thrownException(annot, annot.getFirstSymbol(), e);
		}
		finally {
			env.errorInMetaobjectCatchExceptions(cyanMetaobject);
		}

	}

	/**
	 * return a list of objects of WrMethodSignature and WrFieldDec
	 *
	 * @param slotCode
	 * @return
	 */
	public List<ISlotSignature> extractSlotListFrom(String slotCode,
			Annotation annot, Env env, Symbol errSymbol) {
		/*
		 * public Compiler_dsl getCompilerToDSL_sourceFile(char []sourceCode,
		 * String sourceCodeFilename, String sourceCodeCanonicalPath,
		 * CyanPackage cyanPackage) {
		 *
		 */
		if ( this.dslCompilationUnit == null ) {
			this.dslCompilationUnit = new CompilationUnitDSL(
					this.compilationUnit.getFilename(),
					this.compilationUnit.getFullFileNamePath(),
					this.compilationUnit.getCyanPackage());
		}
		List<ISlotSignature> list = new ArrayList<>();
		char[] source = new char[slotCode.length() + 1];
		slotCode.getChars(0, slotCode.length(), source, 0);
		source[slotCode.length()] = '\0';
		saci.Compiler cp = this.getCompilationUnit().getProgram().getProject()
				.getCompilerManager()
				.getCompiler_sourceFile(source, dslCompilationUnit);

		if ( cp.getSymbol().token == Token.LITERALSTRING ) {
			env.error(annot.getFirstSymbol(), "Metaobject of annotation '"
					+ annot.getCyanMetaobject().getName()
					+ "' produced a list of fields and methods that is, in fact, the following literal string: \""
					+ cp.getSymbol().getSymbolString() + "\"\n"
					+ "Probably the metaobject returned a string with quotes. To correct the error, instead of"
					+ " returning string of variable s, for example, return \n"
					+ "    CyanMetaobject.removeQuotes(s)");
		}

		try {

			while (cp.getSymbol().token == Token.IDENT
					|| Compiler.isBasicType(cp.getSymbol().token)
					|| cp.getSymbol().token == Token.LET
					|| cp.getSymbol().token == Token.VAR
					|| cp.getSymbol().token == Token.FUNC
					|| cp.getSymbol().token == Token.ABSTRACT
					|| cp.getSymbol().token == Token.FINAL
					|| cp.getSymbol().token == Token.OVERLOAD
					|| cp.getSymbol().token == Token.OVERRIDE) {

				if ( cp.getSymbol().token == Token.OVERRIDE ) {
					cp.next();
					if ( cp.getSymbol().token != Token.FUNC
							&& cp.getSymbol().token != Token.ABSTRACT
							&& cp.getSymbol().token == Token.OVERLOAD ) {
						env.error(errSymbol,
								"Syntax error in the list of slots that metaobject '"
										+ annot.getCyanMetaobject().getName()
										+ "' of line "
										+ annot.getFirstSymbol().getLineNumber()
										+ " should produce. 'override' is followed by something that is not 'func', 'abstract', or 'overload'"
										+ "The list is '" + slotCode + "'",
								true);
						return null;
					}
				}
				if ( cp.getSymbol().token == Token.FUNC
						|| cp.getSymbol().token == Token.FINAL
						|| cp.getSymbol().token == Token.ABSTRACT
						|| cp.getSymbol().token == Token.OVERLOAD ) {

					boolean overloadKeyword = cp
							.getSymbol().token == Token.OVERLOAD;
					if ( overloadKeyword ) {
						cp.next();
					}
					boolean finalKeyword = cp.getSymbol().token == Token.FINAL;
					boolean abstractKeyword = cp
							.getSymbol().token == Token.ABSTRACT;
					if ( finalKeyword ) {
						cp.next();
						if ( cp.getSymbol().token != Token.FUNC ) {
							env.error(errSymbol,
									"Syntax error in the list of slots that metaobject '"
											+ annot.getCyanMetaobject()
													.getName()
											+ "' of line "
											+ annot.getFirstSymbol()
													.getLineNumber()
											+ " should produce. Remeber the list should use spaces as a regular code. That means \"var Int n;func check;\" is wrong. "
											+ "The list is '" + slotCode + "'",
									true);
							return null;
						}
					}
					else if ( abstractKeyword ) {
						cp.next();
						if ( cp.getSymbol().token != Token.FUNC ) {
							env.error(errSymbol,
									"Syntax error in the list of slots that metaobject '"
											+ annot.getCyanMetaobject()
													.getName()
											+ "' of line "
											+ annot.getFirstSymbol()
													.getLineNumber()
											+ " should produce. Remeber the list should use spaces as a regular code. That means \"var Int n;func check;\" is wrong. "
											+ "The list is '" + slotCode + "'",
									true);
							return null;
						}
					}

					cp.next();
					MethodSignature ms = null;
					try {
						ms = cp.methodSignature();
						ms.setFinalKeyword(finalKeyword);
						ms.setAbstractKeyword(abstractKeyword);
						if ( cp.getSymbol().token == Token.RETURN_ARROW ) {
							cp.next();
							Expr exprRet = cp.type();
							ms.setReturnTypeExpr(exprRet);
						}
						if ( cp.getSymbol().token == Token.SEMICOLON ) {
							cp.next();
						}
					}
					catch (Throwable e) {
						env.error(errSymbol,
								"Syntax error in the list of slots that metaobject '"
										+ annot.getCyanMetaobject().getName()
										+ "' of line "
										+ annot.getFirstSymbol().getLineNumber()
										+ " should produce. Remeber the list should use spaces as a regular code. That means \"var Int n;func check;\" is wrong. "
										+ "The list is '" + slotCode + "'",
								true);
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
					catch (Throwable e) {
						env.error(errSymbol,
								"Syntax error in the list of slots that metaobject '"
										+ annot.getCyanMetaobject().getName()
										+ "' should produce",
								true);
						return null;
					}
					Symbol id = null;
					if ( cp.getSymbol().token == Token.IDENT ) {
						id = cp.getSymbol();
						cp.next();
					}
					if ( id == null ) {
						if ( cp.getSymbol().token == Token.RETURN_ARROW ) {
							env.error(cp.getSymbol(),
									"This seems to be a function declaraction. But there is no 'func' keyword");

						}
						else if ( Character
								.isLowerCase(exprType.asString().charAt(0)) ) {
							env.error(cp.getSymbol(),
									"A type was expected in place of '"
											+ exprType.asString()
											+ "' or an identifier was expected in place of '"
											+ cp.getSymbol().getSymbolString()
											+ "'");
						}

					}
					/*
					 * FieldDec( ObjectDec currentObj, SymbolIdent
					 * variableSymbol, Expr typeInDec, Expr expr, Token
					 * visibility, boolean shared, List<AnnotationAt>
					 * nonAttachedSlotAnnotationList, List<AnnotationAt>
					 * attachedSlotAnnotationList, Symbol firstSymbol, boolean
					 * isReadonly, Stack<Tuple5<String, String, String, String,
					 * Integer>> annotContextStack)
					 */
					FieldDec field = new FieldDec(null, (SymbolIdent) id,
							exprType, null, Token.PRIVATE, isShared, null, null,
							null, isReadOnly, null);

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
			this.compilationUnit.error(annot.getFirstSymbol().getLineNumber(),
					annot.getFirstSymbol().getColumnNumber(), e.getMessage()
							+ " " + NameServer.messageClassNotFoundException);
		}
		catch (final RuntimeException e) {
			this.compilationUnit.error(annot.getFirstSymbol().getLineNumber(),
					annot.getFirstSymbol().getColumnNumber(),
					"Metaobject '" + annot.getCyanMetaobject().getName() + "' "
							+ "of annotation of line number "
							+ annot.getFirstSymbol().getLineNumber()
							+ " has thrown exception '" + e.getClass().getName()
							+ "'");
		}
		finally {

			final List<UnitError> errorList = dslCompilationUnit.getErrorList();
			if ( errorList != null ) {
				for (final UnitError moError : errorList) {
					this.compilationUnit.error(
							annot.getFirstSymbol().getLineNumber(),
							annot.getFirstSymbol().getColumnNumber(),
							"Metaobject of annotation '"
									+ annot.getCyanMetaobject().getName()
									+ "' issued the following error relating to the list of fields and methods: "
									+ moError.getMessage());
				}
			}
		}

		if ( cp.getSymbol().token != Token.EOF
				&& cp.getSymbol().token != Token.EOLO ) {
			env.error(annot.getFirstSymbol(), "Metaobject of annotation '"
					+ annot.getCyanMetaobject().getName()
					+ "' produced a list of fields and methods with syntax errors. We found the token '"
					+ cp.getSymbol().getSymbolString()
					+ "' where we expected the end of code");
		}
		return list;
	}

	private CompilationUnitDSL dslCompilationUnit;

	/*
	 * MethodSignature ms; if ( Compiler.isOperator(cp.getSymbol().token) ) {
	 * Symbol op = cp.getSymbol(); cp.next(); MethodSignatureOperator mso = new
	 * ast.MethodSignatureOperator(op, (MethodDec ) null); if ( !
	 * Compiler.startType(symbol.token) ) { env.error(errSymbol,
	 * "A type was expected after '" + op.getSymbolString() +
	 * "' in the list of slots that metaobject '" +
	 * annot.getCyanMetaobject().getName() + "' should produce", true);
	 *
	 * } Expr exprType = cp.type(); exprType.calcInternalTypes(env);
	 * mso.setOptionalParameter(new ParameterDec(null, exprType, null));
	 * list.add(mso.getI()); } else if ( cp.getSymbol().token == Token.IDENT ) {
	 * MethodSignatureUnary msu = new MethodSignatureUnary(cp.getSymbol(),
	 * (MethodDec ) null); list.add(msu.getI()); } else if (
	 * cp.getSymbol().token == Token.IDENTCOLON ) { MethodSignatureWithKeywords
	 * msk = new MethodSignatureWithKeywords(); } else { env.error(errSymbol,
	 * "An operator, identifier or identifier: was expected after 'func'" +
	 * "' in the list of slots that metaobject '" +
	 * annot.getCyanMetaobject().getName() + "' should produce", true);
	 *
	 * }
	 */

	// /**
	// @param compiler_afterResTypes
	// @param compilerManager
	// @param env
	// @param hasLessThanInName
	// @param annotation
	// */
	// private void addCodeAndSlotsTo(ICompiler_afterResTypes
	// compiler_afterResTypes, CompilerManager_afterResTypes compilerManager,
	// Env env,
	// boolean hasLessThanInName, Annotation annotation) {
	//
	//
	// CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
	//
	//
	//
	// String thisPrototypeName = this.getName();
	//
	// // // cyanMetaobject.setAnnotation(cyanAnnotation, 0);
	//
	//
	// CompilerManager.createNewPrototypes(compiler_afterResTypes, env,
	// annotation, cyanMetaobject, this.compilationUnit);
	//
	// if ( cyanMetaobject instanceof IAction_afterResTypes ) {
	//
	//
	// String packageName =
	// this.compilationUnit.getCyanPackage().getPackageName();
	// boolean canCommunicateInPackage =
	// this.compilationUnit.getCyanPackage().getCommunicateInPackage();
	//
	// /**
	// * add statements to methods. This was asked by the metaobjects of this
	// program unit.
	// */
	// List<Tuple2<String, StringBuffer>> statsList = null;
	//
	// try {
	// statsList = ((IAction_afterResTypes )
	// cyanMetaobject).afterResTypes_beforeMethodCodeList(compiler_afterResTypes);
	// }
	// catch ( error.CompileErrorException e ) {
	// }
	// catch ( NoClassDefFoundError e ) {
	// env.error(annotation.getFirstSymbol(), e.getMessage() + " " +
	// NameServer.messageClassNotFoundException);
	// }
	// catch ( RuntimeException e ) {
	// env.thrownException(annotation, annotation.getFirstSymbol(), e);
	// }
	// finally {
	// env.errorInMetaobjectCatchExceptions(cyanMetaobject);
	// }
	//
	//
	// if ( statsList != null ) {
	// for ( Tuple2<String, StringBuffer> t : statsList ) {
	// String prototypeName = this.getName();
	// if ( hasLessThanInName || prototypeName.indexOf('<') >= 0 ) {
	// if ( ! this.getCompilationUnit().getPackageName().equals(packageName) ||
	// ! this.getName().equals(prototypeName) ) {
	// env.error(true,
	// annotation.getFirstSymbol(),
	// "This metaobject annotation is trying to code to "
	// + "a method of a prototype it does not have permission "
	// + "to. Only a generic prototype can add code to itself "
	// + "and only to itself", cyanMetaobject.getName(),
	// ErrorKind.metaobject_error);
	// }
	// }
	// if ( !canCommunicateInPackage && !
	// prototypeName.equals(thisPrototypeName) ) {
	// env.error(annotation.getFirstSymbol(), "This metaobject annotation is in
	// prototype '" + packageName + "." +
	// thisPrototypeName + "' and it is trying to add code to another prototype,
	// '" + packageName + "." +
	// prototypeName + "'. This is illegal because package '" + packageName + "'
	// does not allow that. To make that " +
	// "legal, attach '@feature(communicateInPackage, #on)' to the package in
	// the project (.pyan) file", true, false);
	// }
	// else {
	// compilerManager.addBeforeMethod(cyanMetaobject, packageName,
	// prototypeName, t.f1, t.f2 );
	// }
	// }
	// }
	//
	//
	// /**
	// * add code after the metaobject annotation
	// * /
	// StringBuffer code = null;
	//
	// try {
	// code = ((IAction_afterResTypes )
	// cyanMetaobjectWithAt).afterResTypes_codeToAdd(compiler_afterResTypes);
	// }
	// catch ( error.CompileErrorException e ) {
	// }
	// catch ( NoClassDefFoundError e ) {
	// env.error(annotation.getFirstSymbol(), e.getMessage() + " " +
	// NameServer.messageClassNotFoundException);
	// }
	// catch ( RuntimeException e ) {
	// env.thrownException(annotation, annotation.getFirstSymbol(), e);
	// }
	// finally {
	// env.errorInMetaobjectCatchExceptions(cyanMetaobject);
	// }
	//
	//
	//
	// if ( code != null ) {
	// compilerManager.addCodeAtAnnotation(cyanMetaobjectWithAt, code );
	// }
	// */
	//
	//
	//
	// /**
	// * rename methods
	// */
	//
	// List<Tuple2<String, String []>> renameMethodList = null;
	//
	// try {
	// renameMethodList = ((IAction_afterResTypes )
	// cyanMetaobject).afterResTypes_renameMethod(compiler_afterResTypes);
	// }
	// catch ( error.CompileErrorException e ) {
	// }
	// catch ( NoClassDefFoundError e ) {
	// env.error(annotation.getFirstSymbol(), e.getMessage() + " " +
	// NameServer.messageClassNotFoundException);
	// }
	// catch ( RuntimeException e ) {
	// env.thrownException(annotation, annotation.getFirstSymbol(), e);
	// }
	// finally {
	// env.errorInMetaobjectCatchExceptions(cyanMetaobject);
	// }
	//
	//
	//
	// if ( renameMethodList != null ) {
	// for ( Tuple2<String, String []> t : renameMethodList ) {
	// String prototypeName = this.getName();
	// if ( hasLessThanInName || prototypeName.indexOf('<') >= 0 ) {
	// if ( ! this.getCompilationUnit().getPackageName().equals(packageName) ||
	// ! this.getName().equals(prototypeName) ) {
	// env.error(true,
	// annotation.getFirstSymbol(),
	// "This metaobject annotation is trying to rename methods of a "
	// + "prototype it does not have permission to. Only a generic prototype "
	// + "can add code to itself and only to itself",
	// cyanMetaobject.getName(), ErrorKind.metaobject_error);
	// }
	// }
	// if ( !canCommunicateInPackage && !
	// prototypeName.equals(thisPrototypeName) ) {
	// env.error(annotation.getFirstSymbol(), "This metaobject annotation is in
	// prototype '" + packageName + "." +
	// thisPrototypeName + "' and it is trying to rename a method of another
	// prototype, '" + packageName + "." +
	// prototypeName + "'. This is illegal because package '" + packageName + "'
	// does not allow that. To make that" +
	// "legal, attach '@feature(communicateInPackage, #on)' to the package in
	// the project (.pyan) file", true, false);
	// }
	// else {
	// compilerManager.renameMethods(cyanMetaobject,
	// packageName, prototypeName, t.f1, t.f2);
	// }
	//
	// }
	// }
	// }
	// }
	//

	public List<Prototype> get_this_and_all_superPrototypes() {
		if ( this_and_all_superPrototypes != null ) {
			return this_and_all_superPrototypes;
		}
		List<Prototype> superList = new ArrayList<>();
		superList.add(this);
		List<Prototype> superList2 = getAllSuperPrototypes();
		if ( superList2 != null ) {
			superList.addAll(superList2);
		}
		this_and_all_superPrototypes = Collections.unmodifiableList(superList);
		return this_and_all_superPrototypes;
	}

	/*
	 * return a list containing all super-prototypes of this prototype. That
	 * includes all super-interfaces (if this is an interface), all
	 * super-prototypes (if this is not an interface), and all implemented
	 * interfaces. The list is built by a breadth first search for the
	 * supertypes.
	 */
	public List<Prototype> getAllSuperPrototypes() {

		if ( allSuperPrototypes != null ) {
			return allSuperPrototypes;
		}

		HashSet<Prototype> puSet = new HashSet<>();
		Stack<Prototype> puStack = new Stack<>();
		puStack.add(this);
		while (!puStack.isEmpty()) {
			Prototype current = puStack.pop();
			// mark current as visited
			puSet.add(current);

			if ( current instanceof InterfaceDec ) {
				if ( ((InterfaceDec) current)
						.getSuperInterfaceList() != null ) {
					for (InterfaceDec inter : ((InterfaceDec) current)
							.getSuperInterfaceList()) {
						if ( !puSet.contains(inter) ) {
							puStack.add(inter);
						}
					}
				}
			}
			else if ( current instanceof ObjectDec ) {
				if ( ((ObjectDec) current).getSuperobject() != null ) {
					if ( !puSet.contains(
							((ObjectDec) current).getSuperobject()) ) {
						puStack.add(((ObjectDec) current).getSuperobject());
					}
				}
				if ( ((ObjectDec) current).getInterfaceList() != null ) {
					for (Expr inter : ((ObjectDec) current)
							.getInterfaceList()) {
						if ( !puSet.contains(inter.getType()) ) {
							puStack.add((Prototype) inter.getType()
									.getInsideType());
						}
					}
				}
			}
		}
		List<Prototype> other = new ArrayList<>();
		for (Prototype p : puSet) {
			if ( p != this ) {
				other.add(p);
			}
		}
		allSuperPrototypes = Collections.unmodifiableList(other);
		return allSuperPrototypes;
	}

	/**
	 * to be called only after step 9 of the compilation, AFTER_SEM_AN
	 *
	 * @param env
	 * @param superPrototypeList
	 */

	private void checkInheritance_afterSemAn(ICompiler_semAn compiler_semAn,
			Env env) {

		List<Prototype> allSuperPrototypes2 = this.getAllSuperPrototypes();
		for (Prototype pu : allSuperPrototypes2) {

			List<Annotation> annotList = pu
					.getPrototypePackageProgramAnnotationList();
			pu.setDeclarationImportedFromPackageProgram();

			for (Annotation cyanAnnotation : annotList) {
				CyanMetaobject cyanMetaobject = cyanAnnotation
						.getCyanMetaobject();

				_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
				if ( cyanMetaobject instanceof ICheckSubprototype_afterSemAn
						|| (other != null
								&& other instanceof _ICheckSubprototype__afterSemAn) ) {

					// // cyanMetaobject.setAnnotation(cyanAnnotation, 0);

					try {
						int timeoutMilliseconds = Timeout
								.getTimeoutMilliseconds(env,
										env.getProject().getProgram().getI(),
										env.getCurrentCompilationUnit()
												.getCyanPackage().getI(),
										this.getFirstSymbol());

						Timeout<Object> to = new Timeout<>();

						if ( other == null ) {

							if ( Saci.timeLimitForMetaobjects ) {
								to.run(Executors.callable(() -> {
									((ICheckSubprototype_afterSemAn) cyanMetaobject)
											.afterSemAn_checkSubprototype(
													compiler_semAn,
													this.getI());

								}), timeoutMilliseconds,
										"afterSemAn_checkSubprototype",
										cyanMetaobject, env);

							}
							else {
								((ICheckSubprototype_afterSemAn) cyanMetaobject)
										.afterSemAn_checkSubprototype(
												compiler_semAn, this.getI());
							}

							// ((ICheckSubprototype_afterSemAn )
							// cyanMetaobject).afterSemAn_checkSubprototype(
							// compiler_semAn, this.getI());
						}
						else {
							to.run(Executors.callable(() -> {
								((_ICheckSubprototype__afterSemAn) other)
										._afterSemAn__checkSubprototype_2(
												compiler_semAn, this.getI());
							}), timeoutMilliseconds,
									"afterSemAn_checkSubprototype",
									cyanMetaobject, env);
							// ((_ICheckSubprototype__afterSemAn )
							// other)._afterSemAn__checkSubprototype_2(
							// compiler_semAn, this.getI());
						}
					}
					catch (error.CompileErrorException e) {
					}
					catch (NoClassDefFoundError e) {
						env.error(cyanAnnotation.getFirstSymbol(), e
								.getMessage() + " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (RuntimeException e) {
						// e.printStackTrace();
						env.thrownException(cyanAnnotation,
								cyanAnnotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobject(cyanMetaobject,
								this.getSymbolObjectInterface());
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
	 * return the current program unit as an Expr. <code>seed</code> is used to
	 * create new Symbols with the same line number as <code>seed</code>
	 */
	@Override
	public Expr asExpr(Symbol seed) {

		List<List<Expr>> realTypeListList;
		List<Expr> realTypeList;
		List<Symbol> identSymbolArray = new ArrayList<>();

		if ( !getCompilationUnit().getPackageName()
				.equals(MetaHelper.cyanLanguagePackageName) ) {
			// insert package symbols first
			for (Symbol sym : getCompilationUnit().getPackageIdent()
					.getIdentSymbolArray()) {
				identSymbolArray.add(new SymbolIdent(Token.IDENT,
						sym.getSymbolString(), seed.getStartLine(),
						seed.getLineNumber(), seed.getColumnNumber(),
						seed.getOffset(), seed.getCompilationUnit()));
			}
		}
		// insert the program unit name
		identSymbolArray.add(new SymbolIdent(Token.IDENT,
				symbol.getSymbolString(), seed.getStartLine(),
				seed.getLineNumber(), seed.getColumnNumber(), seed.getOffset(),
				seed.getCompilationUnit()));
		ExprIdentStar newIdentStar = new ExprIdentStar(identSymbolArray, null,
				null);

		if ( genericParameterListList != null
				&& this.genericParameterListList.size() > 0 ) {
			/**
			 * an instantiation of a generic prototype such as "Stack<Int>".
			 */
			realTypeListList = new ArrayList<List<Expr>>();

			List<List<GenericParameter>> genParListList = getGenericParameterListList();

			for (List<GenericParameter> genParList : genParListList) {
				realTypeList = new ArrayList<Expr>();
				for (GenericParameter gp : genParList) {
					// realTypeList.add( new ExprIdentStar(new
					// SymbolIdent(Token.IDENT, gp.getName(), -1, -1, -1, -1)
					// ));
					realTypeList.add(gp.getParameter());
				}
				realTypeListList.add(realTypeList);
			}

			ExprGenericPrototypeInstantiation gpi = new ExprGenericPrototypeInstantiation(
					newIdentStar, realTypeListList, null, null, null);
			return gpi;
		}
		else {
			return newIdentStar;
		}

	}

	/**
	 * generate all Java code that the metaobject annotations of this program
	 * unit demand
	 */
	protected void genJavaClassBodyDemandedByAnnotations(PWInterface pw,
			Env env) {

		if ( this.completeAnnotationList != null ) {
			for (Annotation annotation : this.completeAnnotationList) {

				CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
				if ( cyanMetaobject != null
						&& cyanMetaobject instanceof IAction_cge ) {
					IAction_cge cyanMetaobject_cge = (IAction_cge) cyanMetaobject;
					// // cyanMetaobject.setAnnotation(annotation, 0);
					StringBuffer code = null;
					try {
						code = cyanMetaobject_cge.cge_javaCodeClassBody();
					}
					catch (error.CompileErrorException e) {
					}
					catch (NoClassDefFoundError e) {
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (RuntimeException e) {
						env.thrownException(annotation,
								annotation.getFirstSymbol(), e);
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
	 * return the number of the next metaobject annotation. This number is
	 * incremented so this method has side effects.
	 *
	 * @return
	 */
	public int getIncAnnotationNumber() {
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

	public List<AnnotationAt> getBeforeEndNonAttachedAnnotationList() {
		return beforeEndNonAttachedAnnotationList;
	}

	public void setBeforeEndNonAttachedAnnotationList(
			List<AnnotationAt> beforeEndNonAttachedAnnotationList) {
		this.beforeEndNonAttachedAnnotationList = beforeEndNonAttachedAnnotationList;
	}

	public List<AnnotationAt> getNonAttachedAnnotationList() {
		return nonAttachedAnnotationList;
	}

	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		return featureList;
	}

	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature) {
		if ( featureList == null ) featureList = new ArrayList<>();
		featureList.add(feature);
	}

	public void addFeatureList(
			List<Tuple2<String, WrExprAnyLiteral>> featureList1) {
		for (Tuple2<String, WrExprAnyLiteral> t : featureList1) {
			this.addFeature(t);
		}
	}

	public List<WrExprAnyLiteral> searchFeature(String name) {
		if ( featureList == null ) return null;

		List<WrExprAnyLiteral> eList = null;
		for (Tuple2<String, WrExprAnyLiteral> t : featureList) {
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
	 * @param pw
	 */
	protected void genJavaCodeBeforeClassAnnotations(PWInterface pw, Env env) {
		if ( this.attachedAnnotationList != null ) {
			for (AnnotationAt annotation : attachedAnnotationList) {
				CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
				if ( cyanMetaobject instanceof meta.IAction_cge ) {
					// //
					// annotation.getCyanMetaobject().setAnnotation(annotation,
					// 0);
					StringBuffer sb = null;

					try {
						sb = ((IAction_cge) annotation.getCyanMetaobject())
								.cge_codeToAdd();
					}
					catch (error.CompileErrorException e) {
					}
					catch (NoClassDefFoundError e) {
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (RuntimeException e) {
						env.thrownException(annotation,
								annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobject(cyanMetaobject,
								this.getFirstSymbol());
					}

					if ( sb != null ) {
						if ( sb.charAt(sb.length() - 1) == '\0' )
							sb.deleteCharAt(sb.length() - 1);
						pw.print(sb);
					}
				}
			}
		}
		if ( this.nonAttachedAnnotationList != null ) {
			for (AnnotationAt annotation : nonAttachedAnnotationList) {
				CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
				if ( cyanMetaobject instanceof meta.IAction_cge ) {
					// //
					// annotation.getCyanMetaobject().setAnnotation(annotation,
					// 0);
					StringBuffer sb = null;

					try {
						sb = ((IAction_cge) annotation.getCyanMetaobject())
								.cge_codeToAdd();
					}
					catch (error.CompileErrorException e) {
					}
					catch (NoClassDefFoundError e) {
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (RuntimeException e) {
						env.thrownException(annotation,
								annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobject(cyanMetaobject,
								this.getFirstSymbol());
					}

					if ( sb != null ) {
						if ( sb.charAt(sb.length() - 1) == '\0' )
							sb.deleteCharAt(sb.length() - 1);
						pw.print(sb);
					}
				}
			}
		}
		if ( this.completeAnnotationList != null ) {
			for (Annotation annotation : this.completeAnnotationList) {
				if ( annotation
						.getCyanMetaobject() instanceof meta.IAction_cge ) {
					// //
					// annotation.getCyanMetaobject().setAnnotation(annotation,
					// 0);
					StringBuffer sb = null;

					try {
						sb = ((IAction_cge) annotation.getCyanMetaobject())
								.cge_javaCodeBeforeClass();
					}
					catch (error.CompileErrorException e) {
					}
					catch (NoClassDefFoundError e) {
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (RuntimeException e) {
						env.thrownException(annotation,
								annotation.getFirstSymbol(), e);
					}
					finally {
						CyanMetaobject metaobject = annotation
								.getCyanMetaobject();
						env.errorInMetaobjectCatchExceptions(metaobject);
					}

					if ( sb != null ) {
						if ( sb.charAt(sb.length() - 1) == '\0' )
							sb.deleteCharAt(sb.length() - 1);
						pw.print(sb);
					}
				}
			}
		}
	}

	/**
	 * @param pw
	 */
	protected void genJavaCodeStaticSectionAnnotations(PWInterface pw,
			Env env) {
		/*
		 * insert all code for the static sections asked by the metaobjects of
		 * this program unit
		 */
		pw.println("    static {");
		pw.add();
		if ( completeAnnotationList != null ) {
			for (Annotation annotation : completeAnnotationList) {
				if ( annotation.getCyanMetaobject() instanceof IAction_cge ) {
					CyanMetaobject metaobject = annotation.getCyanMetaobject();
					// // metaobject.setAnnotation(annotation, 0);
					StringBuffer code = null;

					try {
						code = ((IAction_cge) metaobject)
								.cge_javaCodeStaticSection();
					}
					catch (error.CompileErrorException e) {
					}
					catch (NoClassDefFoundError e) {
						env.error(annotation.getFirstSymbol(), e.getMessage()
								+ " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (RuntimeException e) {
						env.thrownException(annotation,
								annotation.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobjectCatchExceptions(metaobject);
					}

					if ( code != null ) {
						pw.println(code);
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
		documentTextList.add(new Tuple2<String, String>(doc, docKind));
	}

	public void addDocumentExample(String example, String exampleKind) {
		if ( exampleTextList == null ) {
			exampleTextList = new ArrayList<>();
		}
		exampleTextList.add(new Tuple2<String, String>(example, exampleKind));

	}

	public List<Tuple2<String, String>> getDocumentTextList() {
		return documentTextList;
	}

	public List<Tuple2<String, String>> getDocumentExampleList() {
		return exampleTextList;
	}

	/*
	 * public IDeclaration getIDeclaration() { return new WrPrototype(this); }
	 */

	@Override
	public WrPrototype getI() {
		if ( iPrototype == null ) {
			iPrototype = WrPrototype.factory(this);
		}
		return iPrototype;
	}

	private WrPrototype iPrototype;

	public WrPrototype getiPrototype() {
		return iPrototype;
	}

	public void setiPrototype(WrPrototype iPrototype) {
		this.iPrototype = iPrototype;
	}

	public List<AnnotationAt> getBeforeInnerObjectNonAttachedAnnotationList() {
		return beforeInnerObjectNonAttachedAnnotationList;
	}

	public void setBeforeInnerObjectNonAttachedAnnotationList(
			List<AnnotationAt> beforeInnerObjectNonAttachedAnnotationList) {
		this.beforeInnerObjectNonAttachedAnnotationList = beforeInnerObjectNonAttachedAnnotationList;
	}

	public List<AnnotationAt> getBeforeInnerObjectAttachedAnnotationList() {
		return beforeInnerObjectAttachedAnnotationList;
	}

	public void setBeforeInnerObjectAttachedAnnotationList(
			List<AnnotationAt> beforeInnerObjectAttachedAnnotationList) {
		this.beforeInnerObjectAttachedAnnotationList = beforeInnerObjectAttachedAnnotationList;
	}

	public Map<String, Prototype> getDependentPrototypeList() {
		return dependentPrototypeMap;
	}

	public void addDependentPrototype(Prototype dependentPrototype) {
		if ( this.dependentPrototypeMap == null ) {
			this.dependentPrototypeMap = new HashMap<>();
		}
		this.dependentPrototypeMap.put(dependentPrototype.getFullName(),
				dependentPrototype);
	}

	public void genCompiledInterfaces(StringBuffer sb) {

		CyanEnv cyanEnv = new CyanEnv(this.compilationUnit.getProgram());
		cyanEnv.setGenInterfacesForCompiledCode(true);

		PWCharArray pw = new PWCharArray(sb);
		genCyan(pw, cyanEnv, false);
		sb.append("\n");
	}

	public boolean getHasOperatorMethod() {
		return hasOperatorMethod;
	}

	public void setHasOperatorMethod(boolean hasOperatorMethod) {
		this.hasOperatorMethod = hasOperatorMethod;
	}

	/**
	 * list of pairs (doc, docKind) of documentation for this declaration. See
	 * interface {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>>				documentTextList;
	/**
	 * list of pairs (example, exampleKind) of examples for this declaration.
	 * See interface {@link meta.IDeclaration} for more details.
	 */
	private List<Tuple2<String, String>>				exampleTextList;

	/**
	 * the compiler should assure that these generic instantiations are created
	 * in phase AFTER_RES_TYPES. These are the types of metaobject annotations
	 */
	private List<ExprGenericPrototypeInstantiation>		gpiList;

	/**
	 * the list of features associated to this program unit
	 */
	private List<Tuple2<String, WrExprAnyLiteral>>		featureList;

	/**
	 * symbol of the object name, "A" in object A ... end
	 */
	protected Symbol									symbol;

	/**
	 * list of generic parameters of this program unit if this program unit is a
	 * generic one. There is in fact one list for each part between < and >. For
	 * example, in object Proto<T1, T2><R> there are two list, the first with
	 * two elements and the second with one element.
	 */
	protected List<List<GenericParameter>>				genericParameterListList;

	/**
	 * The compilation unit corresponding to this program unit. For example,
	 * object Stack (program unit) should be declared in file Stack.cyan
	 * (compilation unit)
	 */
	protected CompilationUnit							compilationUnit;

	/**
	 * Visibility of this program unit. It may be PUBLIC, PROTECTED, or PRIVATE
	 */
	protected Token										visibility;

	/*
	 * true if this is an instantiation of generic prototype. Something like
	 * "object Stack<Int> ... end".
	 */
	private boolean										prototypeIsNotGeneric;

	/**
	 * a list of all metaobject annotations inside and before this program unit.
	 * This list includes attachedAnnotationList and
	 * beforeEndNonAttachedAnnotationList. Every metaobject annotation inside
	 * the program unit is in this list.
	 */
	protected List<Annotation>							completeAnnotationList;

	/**
	 * a list of all annotations that apply to this program unit. It includes
	 * all annotations inside the program unit and annotations of its package
	 * and the program.
	 *
	 */
	protected List<Annotation>							prototypePackageProgramAnnotationList;

	/**
	 * metaobject annotations placed just before this program unit such as
	 * {@literal @}checkStyle in <br>
	 * <code>
	 * {@literal @}checkStyle object Proto<br>
	 *    ...<br>
	 * end<br>
	 * These metaobject annotations are attached to the program unit declaration
	 * </code>
	 */
	protected List<AnnotationAt>						attachedAnnotationList;
	/**
	 * metaobject annotations placed just before this program unit such as
	 * {@literal @}javacode in <br>
	 * <code>
	 * {@literal @}javacode{* ... *} <br>
	 * object Proto<br>
	 *    ...<br>
	 * end<br>
	 * These metaobject annotations are NOT attached to the program unit declaration
	 * </code>
	 */
	protected List<AnnotationAt>						nonAttachedAnnotationList;

	/**
	 * metaobject annotations placed just before keyword 'end' of this program
	 * unit. These metaobject annotations are NOT attached to any declaration
	 * </code>
	 */
	protected List<AnnotationAt>						beforeEndNonAttachedAnnotationList;

	/**
	 * the symbol of 'end' at the end of the program unit
	 */
	protected Symbol									endSymbol;

	/**
	 * true if this prototype is generic. False in prototypes like
	 * Stack{@literal <}Int>
	 */
	protected boolean									genericPrototype;
	/**
	 * Each metaobject annotation in a prototype (not an interface) has a
	 * number. This field keeps the number of the next metaobject annotation.
	 * This number is used when metaobjects are communicating with each other.
	 */

	private int											metaobjectAnnotationNumber;

	/**
	 * list of metaobjects that can appear before keyword 'extends', 'mixin', or
	 * 'implements
	 */
	protected List<AnnotationAt>						moListBeforeExtendsMixinImplements;

	/**
	 * list of message sends to super in this program unit. It is used to
	 * generate code. Message sends to super are generated by calling a private
	 * Java method.
	 */
	protected List<ExprMessageSendWithKeywordsToSuper>	messageSendWithkeywordsToSuperList;

	/**
	 * the next number to the associated to an anonymous functions. Functions
	 * inside a prototype receive numbers in textual order. Starts with 0
	 */
	protected int										nextFunctionNumber;

	/**
	 * list of inner objects of this object. These inner objects are created by
	 * the compiler. For each anonymous functions in the object the compiler
	 * creates one prototype that is inserted in this list. For each method in
	 * the object the compiler creates one prototype that is also inserted in
	 * this list.
	 */
	protected List<ObjectDec>							innerPrototypeList;

	/**
	 * if this object is inside another object, outerObject points to this outer
	 * object. Otherwise it is null
	 */
	protected ObjectDec									outerObject;

	/**
	 * full name including the package name. It may be somethink like <br>
	 * {@code Tuple<main.Program, people.bank.Client>}<br>
	 * This fullName is only set in the semantic analysis and after method
	 * getFullName is called.
	 */
	private String										fullName;

	protected String									javaName;

	private Symbol										firstSymbol;

	/**
	 * the symbol 'object' or 'interface'
	 */
	private Symbol										symbolObjectInterface;

	/**
	 * real name of this prototype, including its generic parameters, if any
	 */
	private String										realName;

	/**
	 * all super-prototypes of this program unit. If this is ObjectDec, this
	 * list includes all super-prototypes and all implemented interfaces. If
	 * this is InterfaceDec, this list includes all super-interfaces.
	 */
	private List<Prototype>								allSuperPrototypes;

	/**
	 * this and all super-prototypes of this program unit. If this is ObjectDec,
	 * this list includes all super-prototypes and all implemented interfaces.
	 * If this is InterfaceDec, this list includes all super-interfaces.
	 */
	private List<Prototype>								this_and_all_superPrototypes;

	/**
	 * true if this program unit should be immutable
	 */
	private boolean										immutable;
	/**
	 * metaobject annotations placed before the declaration of inner objects.
	 * These metaobject annotations are NOT attached to any declaration. </code>
	 */
	protected List<AnnotationAt>						beforeInnerObjectNonAttachedAnnotationList;
	/**
	 * metaobject annotations placed before the declaration of inner objects.
	 * These metaobject annotations are attached to the next declaration.
	 * </code>
	 */
	protected List<AnnotationAt>						beforeInnerObjectAttachedAnnotationList;

	/**
	 * list of prototypes from which this program unit depends on. Whenever a
	 * prototype in the list is changed, this program unit should be compiled
	 * again. This program unit depends on prototype P if: - P is the type of a
	 * variable, parameter, or field that appear inside this program unit; - P
	 * is the superprototype or an implemented interface of this program unit; -
	 * P appears in an expression in this program unit; - some metaobject inside
	 * this program unit asks for information on P, which may be its methods,
	 * superprototype, or implemented interfaces.
	 */
	private Map<String, Prototype>						dependentPrototypeMap;

	/**
	 * true if the prototype has at least one operator method equal to +, -, *,
	 * /, %, <<, >>, or >.>> Currently, interfaces cannot define operator
	 * methods.
	 */
	private boolean										hasOperatorMethod	= false;

}
