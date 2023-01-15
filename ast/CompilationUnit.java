package ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import error.CompileErrorException;
import error.ErrorKind;
import error.UnitError;
import lexer.Symbol;
import meta.IAction_cge;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_semAn;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple3;
import meta.WrCompilationUnit;
import saci.CompilerManager_afterResTypes;
import saci.CompilerOptions;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
import saci.Project;


/**
 * This class represents a source file in Cyan, a unit of compilation.
 * Each source file corresponds to at least an object or interface. It may be
 * more than one because there may be one public unit and several private ones.
 */
public class CompilationUnit extends CompilationUnitSuper implements ASTNode, Cloneable,
                 Declaration {


	public CompilationUnit(String filename, String packageCanonicalPath,
			               CompilerOptions compilerOptions,
			               CyanPackage cyanPackage) {

		super(filename, packageCanonicalPath);

		this.filename = filename.replace('|', '-');
		this.packageCanonicalPath = packageCanonicalPath;
		this.compilerOptions = compilerOptions;
		this.packageSymbol = null;
		/*
		errorList = new ArrayList<UnitError>();
		actionList = new ArrayList<Action>(); */
		this.cyanPackage = cyanPackage;

		// this.fullFileNamePath = this.packageCanonicalPath + this.filename;
		prototypeList = new ArrayList<Prototype>();

		importPackageList = null;
		importedPackageNameList = null;
		conflictPrototypeTable = null;
		importedCyanPackageSet = null;

		hasGenericPrototype = false;
		publicPrototype = null;
		isPrototypeInterface = false;
		alreadyCalcInterfaceTypes = false;
		alreadyCalcInternalTypes = false;
		interfaceCompilationUnit = null;
		lineMessageList = new ArrayList<>();
		isInterfaceAsObject = false;
		nonAttachedAnnotationListBeforePackage = null;
		prototypeIsNotGeneric = true;
		genericAndParsed = false;
	}

	@Override
	public CompilationUnit clone() {
		CompilationUnit clone = (CompilationUnit ) super.clone();
		return clone;
	}


	@Override
	public void accept(ASTVisitor visitor) {

		visitor.preVisit(this);

		for ( Prototype pu : this.prototypeList ) {
			pu.accept(visitor);
		}
		visitor.visit(this);
	}


	@Override
	public void reset() {
		if ( ! this.alreadPreviouslyCompiled && ! this.genericAndParsed ) {
			super.reset();
			importPackageList = null;
			importedPackageNameList = null;
			conflictPrototypeTable = null;
			importedCyanPackageSet = null;

			hasGenericPrototype = false;
			publicPrototype = null;
			isPrototypeInterface = false;
			interfaceCompilationUnit = null;
			lineMessageList.clear();

			if ( ! this.parsed ) {
				prototypeList.clear();
			}
			alreadyCalcInterfaceTypes = false;
			alreadyCalcInternalTypes = false;
		}
	}

	public void setImportPackageList(List<ExprIdentStar> importPackageList) {
		this.importPackageList = importPackageList;
		this.importedPackageNameList = new HashSet<String>();
		for ( ExprIdentStar e : importPackageList )
			importedPackageNameList.add(e.getName());

		// no conflicts yet
		conflictPrototypeTable = new HashMap<>();
	}

	public List<ExprIdentStar> getImportPackageList() {
		return importPackageList;
	}


	@Override
	public String getEntityName() {
		if ( publicObjectInterfaceName == null ) {
			String s = this.getFileNameWithoutExtension();

			int lastIndexBar = s.lastIndexOf(NameServer.fileSeparatorAsString);
			if ( lastIndexBar < 0 )
				lastIndexBar = -1;
			s = s.substring(lastIndexBar+1);


			String packageName = this.getPackageName();
			if ( packageName.length() > 0 )
				packageName += ".";
			publicObjectInterfaceName = packageName + NameServer.fileNameToPrototypeName(s);
		}
		return publicObjectInterfaceName;
	}


	public void setCompilerOptions(CompilerOptions compilerOptions) {
		this.compilerOptions = compilerOptions;
	}

	public CompilerOptions getCompilerOptions() {
		return compilerOptions;
	}

	public void setCyanPackage(CyanPackage cyanPackage) {
		this.cyanPackage = cyanPackage;
	}

	public CyanPackage getCyanPackage() {
		return cyanPackage;
	}

	public void genCompiledInterfaces(StringBuffer sb) {
		for ( Prototype prototype : prototypeList ) {
			if ( ! prototype.getSimpleName().endsWith("__") ) {
				prototype.genCompiledInterfaces(sb);
			}
		}

	}

	/**
	 * if cyanEnv.getCreatingInstanceGenericPrototype(), this is the instantiation of a generic prototype such as Stack<Int> from Stack<:T>
	 * In this case, the compilation unit is put in a package ended with ".tmp" such as "cyan.lang.tmp"
	 * @param pw
	 * @param cyanEnv
	 */

	public void genCyan(PWInterface pw, CyanEnv cyanEnv) {

		if ( nonAttachedAnnotationListBeforePackage != null ) {
			for ( AnnotationAt annotation : nonAttachedAnnotationListBeforePackage ) {
				annotation.genCyan(pw, false, cyanEnv, true);
			}
		}
		pw.print("package ");
		pw.print(this.getPackageIdent().getName());


		pw.println("");



		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			/** This is an instantiation of a generic prototype. That is, a real prototype is being created
			     from a generic prototype by replacing its formal parameters by real parameters. If a real
			     parameter is a prototype, its package should be imported inside the instantiation.
			     For example, suppose prototype A in a source file A.cyan uses "Stack<Tuple<Bank, RegExpr>>".
			     This source file imports Bank from package "bank" and RegExpr from package "util".
			     The creation of a real prototype for "Stack<Tuple<Bank, RegExpr>>" is made in this
			     genCyan method of class CompilationUnit of the Cyan Compiler. This real prototype is
			     put in a source file "Stack(Tuple(Bank,RegExpr)).cyan". This file imports packages
			     "bank" and "util" because "Bank" and "RegExpr" are used in the instantiation. Tuple
			     is always imported by any Cyan source file.


			for ( String usedGenericPrototype : cyanEnv.getUsedGenericPrototypeSet() ) {
				CyanPackage aPackage = getCyanPackage().getProject().searchPackageOfCompilationUnit(usedGenericPrototype);

				 // if aPackage == null, it should be a non-type such as "f1" in the generic prototype instantiation "Tuple<f1, Int>"

				if ( aPackage != null ) {
					String packageName = aPackage.getPackageName();
					if ( ! packageName.equals(NameServer.cyanLanguagePackageName) && ! packageName.equals(this.getPackageIdent().getName()) )
					    importPackageNameList.add(aPackage.getPackageName());

				}

			} */

			/*
			// genCyan is being called to create an instance of a generic prototype
			// currently only one program unit can be generic in a compilation unit.
			// So the next "for" is unnecessary
			for ( Prototype pu : prototypeList )
				if ( pu.isGeneric() ) {
					   // getGenericParameterListList() returns something like <Int, Int><Boolean>
					for ( List<GenericParameter> genParamList : pu.getGenericParameterListList() )
						   // genParamList is just one list of parameters like <Int, Int>
						for ( GenericParameter genParam : genParamList )  {
							// genParam is just like Int in <Int, Int>

							// all packages of all real parameters should be imported. That is,
							// if this compilation unit defines "object Stack<ChooseFoldersCyanInstallation> ... end" then
							// it should import package "main" in which "ChooseFoldersCyanInstallation" is declared

							//  realPrototypeName is something like  "Stack(1)"

							String realPrototypeName; // = cyanEnv.getFormalRealLimitedTable().get( genParam.getName() );
							realPrototypeName = NameServer.prototypeNameToFileName(cyanEnv.formalGenericParamToRealParam(genParam.getName()));

							 // if the real parameter starts with an upper case letter, it should be a real prototype.

							if ( Character.isUpperCase(realPrototypeName.charAt(0)) ) {
								   // get
								CyanPackage aPackage = getCyanPackage().getProject().searchPackageOfCompilationUnit(realPrototypeName);
								if ( aPackage == null ) {
									String s = NameServer.prototypeNameToFileName(cyanEnv.getFormalParamToRealParamFileNameTable().get(genParam.getName()));
									aPackage = getCyanPackage().getProject().searchPackageOfCompilationUnit(s);
								}

								 // if aPackage == null, it should be a non-type such as "f1" in the generic prototype instantiation "Tuple<f1, Int>"

								if ( aPackage == null ) {
									pu.getCompilationUnit().error(null,  "Prototype '" + realPrototypeName + "' was not found");
								}
								else {
									String packageName1 = aPackage.getPackageName();
									if ( ! packageName1.equals(NameServer.cyanLanguagePackageName) && ! packageName1.equals(this.getPackageIdent().getName()) )
									    importPackageNameList.add(aPackage.getPackageName());
								}

							}
						}
				}
			*/

		}
		//String currentPackageNameWithTMP = packageIdent.getName() + NameServer.dotTemporaryDirName;

		for ( String packageName : importedPackageNameList ) {
				pw.println("import " + packageName);
		}
		pw.println("");

		for ( Prototype prototype : prototypeList )
			prototype.genCyan(pw, cyanEnv, true);
	}


	public void genJava(PWInterface pw, Env env) {

		env.setCurrentCompilationUnit(this);

		if ( nonAttachedAnnotationListBeforePackage != null ) {
			for ( AnnotationAt annotation : nonAttachedAnnotationListBeforePackage ) {
				if ( annotation.getCyanMetaobject() instanceof IAction_cge ) {
					env.error(annotation.getFirstSymbol(), "Metaobject '" + annotation.getCyanMetaobject().getName() + "' implements interface "
							+ " IAction_cge. It cannot be called before 'package'");
				}
			}
		}

		ExprIdentStar packageIdent = getPackageIdent();
		String thisPackageName = packageIdent.getName();
		pw.print("package " + thisPackageName + ";");


		env.atBeginningOfCurrentCompilationUnit(this);


		pw.println("");
		pw.println("import cyanruntime.*;\n");
		/*
		if ( ! thisPackageName.equals(NameServer.cyanLanguagePackageName) ) {
			pw.println("import " + NameServer.cyanLanguagePackageName + ".*;\n");
		}
		*/
		for ( CyanPackage aPackage : importedCyanPackageSet ) {
			if ( aPackage != cyanPackage &&
				 ( aPackage.getCompilationUnitList().size() > 0 ||
				   (aPackage.getCompilationUnitDSLList() != null && aPackage.getCompilationUnitDSLList().size() > 0) ) ) {
				pw.println("import " + aPackage.getPackageName() + ".*;");  // caveat: aPackage name should already be in Java
			}
		}

		for (Map.Entry<String, TypeJavaRef> entry : this.importJVMJavaRefSet.entrySet() ) {
			pw.println("import " + entry.getValue().getFullName() + ";");
		}

		for (Map.Entry<String, JVMPackage> entry : this.importJVMPackageSet.entrySet() ) {
			pw.println("import " + entry.getValue().getPackageName() + ".*;");
		}


		for ( Prototype prototype : prototypeList ) {
			pw.println("");
			prototype.genJava(pw, env);
			//SerializeManager.writeJSON(prototype, nameSerializedFile);
		}
		env.atEndOfCurrentCompilationUnit();
	}



	public void addPrototype(Prototype prototype) {
		prototypeList.add(prototype);
	}

	public List<Prototype> getPrototypeList() {
		return prototypeList;
	}

	/**
	 * return the name of the sole public prototype declared in this compilation Unit.
	 * This name should be equal to the file name of the file.
	 * @param data.env
	 */
	public String getNamePublicPrototype() {
		return this.getPublicPrototype().getName();
	}

	public Prototype getPublicPrototype() {
		if ( publicPrototype == null ) {
			for ( Prototype p : prototypeList )
				if ( p.getVisibility() == Token.PUBLIC ) {
					publicPrototype = p;
					return p;
				}
		}
		return publicPrototype;
	}

	public boolean removePrototype(Prototype toBeRemoved) {
		for (int i = 0; i < this.prototypeList.size(); ++i) {
			Prototype p = this.prototypeList.get(i);
			if ( p == toBeRemoved ) {
				prototypeList.remove(i);
				return true;
			}
		}
		return false;
	}


	/**
	 * this method is only called when the compilation unit has a single prototype which is generic.
	 * This method creates an instance of the prototype with the data of parameter "e". That is,
	 * if "this" refer to a compilation unit which has prototype "Stack<T>" and "e"
	 * references "Stack<Int>", this method creates a prototype whose name is given by
	 *      NameServer.getJavaNameInterfaceObject(e)
	 * In this case, it will be created a prototype
	 *      Stack_left_gp_Int_right
	 * @param e
	 */
	@SuppressWarnings("resource")
	public CompilationUnit createInstanceGenericPrototype(
			ExprGenericPrototypeInstantiation e, Env env) {
		String fileName = null;
		String newFileName = null;
		String packageCanonicalPath1 = null;


		/*#
		make a new method for searching for I1(1), getting its package, "ga". Than add ".tmp". This should
		be recursive: the generic instantiations of I1 should suffer the same process.  */
		   // name of the file with the source code of the generic prototype instantiation
		String cyanSourceFileName = e.getSpecificSourceFileName(env) + MetaHelper.dotCyanSourceFileExtension;


		FileOutputStream fos = null;

		Prototype prototype = null;
		for ( Prototype pu : this.getPrototypeList() )
			if ( pu.getVisibility() == Token.PUBLIC ) {
				prototype = pu;
				break;
			}
		if ( prototype == null ) return null;

		PrintWriter printWriter = null;
		newFileName = fileName = getFullFileNamePath();

		int indexOfStartFileName = fileName.lastIndexOf(File.separator);
		if ( indexOfStartFileName > 0 ) {
			newFileName = fileName.substring(0, indexOfStartFileName);
			String dirName = newFileName + File.separator + NameServer.temporaryDirName;
			File dir = new File(dirName);
			if ( ! dir.exists() ) {
				dir.mkdirs();
			}
			packageCanonicalPath1 = dirName + File.separator;
			newFileName = packageCanonicalPath1 + cyanSourceFileName;
		}
		String prototypeNameInstantiation1 = null;
		String packageNameInstantiation1 = null;

		try {

			newFileName = newFileName.replace('|', '-');
			fos = new FileOutputStream(newFileName);
			printWriter = new PrintWriter(fos, true);
			PW pw = new PW();
			pw.set(printWriter);
			//if ( newFileName.contains("G1") )
				//System.gc();

			Prototype currentPrototype = env.getCurrentPrototype();

			if ( currentPrototype == null ) {
				// env.error(e.getFirstSymbol(),  "Prototype instantiation outside a prototype");
				// return null;
				prototypeNameInstantiation1 = env.getProject().getProjectName();
				packageNameInstantiation1 = "";
			}
			else {
				prototypeNameInstantiation1 = currentPrototype.getNameWithOuter();
				packageNameInstantiation1 = currentPrototype.getCompilationUnit().getPackageName();
			}


			CyanEnv cyanEnv = new CyanEnv(prototype, e, env, packageNameInstantiation1,
					prototypeNameInstantiation1);

			genCyan(pw, cyanEnv);
			//#$
//			ChooseFoldersCyanInstallation p = env.getProject().getProgram();
//			Prototype originProgUnit = env.getCurrentCompilationUnit().getPublicPrototype();
//			p.addInstantiatedPrototypeName_to_WhereInfo(
//					originProgUnit.getFullName(),
//					packageNameInstantiation1, prototypeNameInstantiation1,
//					e.getFirstSymbol().getLineNumber(), e.getFirstSymbol().getColumnNumber());

			// System.out.println("Creating: " + newFileName + " ");
		}
		catch ( FileNotFoundException e1 ) {
			System.out.println("Cannot create file " + newFileName + " The compiler is finished");
			System.exit(1);
			return null;
		}
		catch ( NullPointerException e3 ) {
			// e3.printStackTrace();
			this.error(null, 0, "Internal error in CompilationUnit when writing to file " + newFileName, null, env);
		}
		catch (Exception e2 ) {
			this.error(null, 0, "error in writing to file " + newFileName, null, env);
			return null;
		}
		finally {
			if ( fos != null ) {
				try {
					fos.close();
				}
				catch (IOException e1) {
					this.error(null, 0, "error in writing to file " + newFileName, null, env);
					return null;
				}
				finally {
					if ( printWriter != null ) {
						printWriter.flush();
						printWriter.close();
					}
				}
			}
		}


		CompilationUnit compilationUnit = new CompilationUnit(
				cyanSourceFileName,
				packageCanonicalPath1,
				getCompilerOptions(),
				null
				);

		//#$ {

		compilationUnit.setPackageNameInstantiation(packageNameInstantiation1);
		compilationUnit.setPrototypeNameInstantiation(prototypeNameInstantiation1);
		compilationUnit.setLineNumberInstantiation(e.getFirstSymbol().getLineNumber());
		compilationUnit.setColumnNumberInstantiation(e.getFirstSymbol().getColumnNumber());
		//#$  }

		return compilationUnit;

	}

	/**
	 * If the public program unit of this compilation unit is an interface, this method
	 * creates a Proto-interface for it. This object is used when the interface appears
	 * in an expression. So, if Shape is an interface, this method creates a Cyan
	 * object named Proto_Shape. In the code
	 *       var Shape sh;
	 * The Java interface created for Shape, which is _Shape, is used as the
	 * type of sh in the Java code. However, in
	 *       sh = Shape;
	 * Shape is used in an expression. Then this code is equivalent to
	 *       sh = Proto_Shape;
	 * Proto_Shape is a Cyan prototype that implements Shape and whose methods
	 * throw exception ExceptionCannotCallInterfaceMethod.
	 * @throws IOException
	 */
	public CompilationUnit createProtoInterface() {
		String newFileName = null;

		   // name of the file with the source code of the generic prototype instantiation
		String cyanSourceFileName = NameServer.prototypeFileNameFromInterfaceFileName(this.getFileNameWithoutExtension())
				+ "." + MetaHelper.cyanSourceFileExtension;

		FileOutputStream fos = null;
		String dirName;
		try {
			newFileName = getFullFileNamePath();
			int indexOfSlash = newFileName.lastIndexOf(File.separatorChar);
			if ( indexOfSlash < 0 ) indexOfSlash = 0;


			dirName = newFileName.substring(0, indexOfSlash);
			if ( dirName.endsWith(NameServer.temporaryDirName) ) {
				dirName += File.separatorChar;
			}
			else {
				dirName += File.separatorChar +
						NameServer.temporaryDirName + File.separatorChar;
			}
			File tmpDir = new File(dirName);
			if ( ! (tmpDir.exists() && tmpDir.isDirectory()) ) {
				tmpDir.mkdirs();
			}
			newFileName = dirName  +
					cyanSourceFileName;

			fos = new FileOutputStream(newFileName);
			PrintWriter printWriter = new PrintWriter(fos, true);
			PW pw = new PW();
			pw.set(printWriter);
			genCyanProtoInterface(pw);
			printWriter.close();
		}
		catch ( FileNotFoundException e ) {
			Env env = null;
			System.out.println("Cannot create file " + newFileName + " The compiler is finished");
			System.exit(1);
			return null;

		}
		catch (Exception e ) {
			Env env = null;
			this.error(null, 0, "error in writing to file " + newFileName, null, env);
			try {
				if ( fos != null )
					fos.close();
			}
			catch (IOException e1) {
			}
			return null;
		}
		CompilationUnit newCompilationUnit = new CompilationUnit(
				cyanSourceFileName,
				dirName,
				getCompilerOptions(),
				cyanPackage
				);
		newCompilationUnit.readSourceFile();
		newCompilationUnit.setIsPrototypeInterface(true);

		this.interfaceCompilationUnit = newCompilationUnit;
		return newCompilationUnit;

	}




	private void genCyanProtoInterface(PW pw) {
		pw.println("package " + this.getPackageIdent().getName());

		pw.println("");
		for ( ExprIdentStar expr : this.getImportPackageList() ) {
			pw.println("import " + expr.getName());
		}
		pw.println("");
		for ( Prototype prototype : this.getPrototypeList() )
			if ( prototype instanceof InterfaceDec )
				((InterfaceDec) prototype).genCyanProtoInterface(pw);
	}


	public String getPackageCanonicalPath() {
		return packageCanonicalPath;
	}


	public void setHasGenericPrototype(boolean hasGenericPrototype) {
		this.hasGenericPrototype = hasGenericPrototype;
	}
	public boolean getHasGenericPrototype() {
		return hasGenericPrototype;
	}

	/**
	 * true if this compilation unit has a generic prototype. The same
	 * as {@link #getHasGenericPrototype()}. However, this method relays
	 * on the file name of the compilation unit. It it has a '(' in the
	 * file name then it has a generic prototype
	   @return
	 */
	public boolean hasGenericPrototype() {
		int i = filename.indexOf('(');
		return i > 0 && Character.isDigit(filename.charAt(i+1));
	}

	public boolean getPrototypeIsNotGeneric() {
		return prototypeIsNotGeneric;
	}

	public void setPrototypeIsNotGeneric(boolean hasPrototypeInstantiation) {
		this.prototypeIsNotGeneric = hasPrototypeInstantiation;

	}


	public void calcInternalTypes(ICompiler_semAn compiler_semAn, Env env) {

		if ( ! alreadyCalcInternalTypes ) {
			alreadyCalcInternalTypes = true;
			env.atBeginningOfCurrentCompilationUnit(this);
			meta.GetHiddenItem.getHiddenEnv(compiler_semAn.getEnv()).atBeginningOfCurrentCompilationUnit(this);
			if ( nonAttachedAnnotationListBeforePackage != null ) {
				for ( AnnotationAt annotation : nonAttachedAnnotationListBeforePackage ) {
					annotation.calcInternalTypes(env);
				}
			}

			meta.GetHiddenItem.getHiddenEnv(compiler_semAn.getEnv())
			     .atBeginningOfCurrentCompilationUnit(this);
			// compiler_semAn.getEnv().atBeginningOfCurrentCompilationUnit(this);

			for ( Prototype prototype : prototypeList )
				prototype.calcInternalTypes(compiler_semAn, env);



			checkErrorMessages(env);
			//compiler_semAn.getEnv().atEndOfCurrentCompilationUnit();
			meta.GetHiddenItem.getHiddenEnv(compiler_semAn.getEnv())
			    .atEndOfCurrentCompilationUnit();

			env.atEndOfCurrentCompilationUnit();
			meta.GetHiddenItem.getHiddenEnv(compiler_semAn.getEnv()).atEndOfCurrentCompilationUnit();

		}
	}

	public void prepareGenericCompilationUnit(Env env) {
		this.importedPackageNameList = new HashSet<String>();
		for ( ExprIdentStar e : importPackageList )
			importedPackageNameList.add(e.getName());

		// no conflicts yet
		conflictPrototypeTable = new HashMap<>();
		prepareCompilationUnit(env);
		/*
		 * inserts all generic parameters as prototypes in the package of this compilation unit
		 */
		Prototype publicPU = this.getPublicPrototype();
		List<Prototype> puList = new ArrayList<>();
		for ( List<GenericParameter> gpList : this.getPublicPrototype().getGenericParameterListList() ) {
			for ( GenericParameter gp : gpList ) {
				Prototype obj = publicPU.clone(); // new ObjectDec(null, Token.PUBLIC, null, null,);
				if ( obj == null ) {
					env.error(publicPU.getFirstSymbol(), "Internal error: cannot clone this object");
					return ;
				}
				obj.setRealName(gp.getName());
				puList.add( obj );
			}
		}
		env.setPrototypeForGenericPrototypeList(puList);
	}

	public void prepareCompilationUnit(Env env) {


		if ( alreadyCalcInterfaceTypes )  return;
		alreadyCalcInterfaceTypes = true;

		Project project = cyanPackage.getProject();
		String packageNameOfThisCompilationUnit = cyanPackage.getPackageName();
		importedCyanPackageSet = new HashSet<CyanPackage>();
		if ( importedPackageNameList == null ) {
			importedPackageNameList = new HashSet<>();
		}
		int i = 0;
		for ( String packageName : this.importedPackageNameList ) {
			CyanPackage p = project.searchPackage(packageName);
			if ( p == null ) {
				ExprIdentStar importedPackage = this.importPackageList.get(i);
				env.error( true, importedPackage.getFirstSymbol(),
						   "Package " + packageName + " was not found. Make sure "
						   		+ "it is included in the project file (.pyan) or, "
								+ "if it is a Java package, its jar file is given after option -cp like in\n"
						   		+ "    saci projectFile.pyan -cp \"C:\\files\\lib\\sacilib.jar\"",
						   packageName, ErrorKind.package_was_not_found_outside_prototype);
			}
			else {
				if ( packageNameOfThisCompilationUnit.compareTo(packageName) == 0 ) {
				ExprIdentStar importedPackage = this.importPackageList.get(i);
					env.error(true, importedPackage.getFirstSymbol(), "Package cannot import itself",
							packageName, ErrorKind.package_is_importing_itself);
				}

				this.importedCyanPackageSet.add(p);
			}
		}

		String publicPrototypeNameThisCompilationUnit = this.getPublicPrototype().getSymbol().getSymbolString();

		  // import package of this compilation unit
		importedCyanPackageSet.add(this.cyanPackage);

		CyanPackage cyanLangPackage = project.getCyanLangPackage();
		importedCyanPackageSet.add(cyanLangPackage);

		HashMap<String, String> importedPrototypeSet = new HashMap<String, String>();
		for ( CyanPackage cp : importedCyanPackageSet ) {
			Set<String> onePackageRawPrototypeNameSet = new HashSet<String>();
			/*
			 * first collect all raw prototype names of package 'cp' in set onePackageRawPrototypeNameSet.
			 * Currently every compilation unit has just one program unit
			 */
			for ( CompilationUnit compUnit : cp.getCompilationUnitList() ) {
				for ( Prototype prototype : compUnit.getPrototypeList() ) {
					if ( prototype.getVisibility() == Token.PUBLIC ) {
						String puName = prototype.getSymbol().getSymbolString();
						onePackageRawPrototypeNameSet.add(puName);
					}
				}
			}
			for ( String rawPrototypeName : onePackageRawPrototypeNameSet ) {
				String oldPackageName = importedPrototypeSet.put(rawPrototypeName, cp.getPackageName() );
				if ( oldPackageName != null ) {
					/* importedPrototypeSet already contains a program unit with this name
					 * That is ok if this program unit is not the public program unit of
					 * this compilation unit.
					 * */
					if ( ! rawPrototypeName.equals(publicPrototypeNameThisCompilationUnit) ) {
						if ( this.conflictPrototypeTable == null ) {
							this.conflictPrototypeTable = new HashMap<>();
						}
						this.conflictPrototypeTable.put(rawPrototypeName, oldPackageName + ", " + cp.getPackageName());
					}
				}

			}
		}
	}



	public void calcInterfaceTypes(Env env) {


		if ( ! alreadyCalcInterfaceTypes ) {
			env.atBeginningOfCurrentCompilationUnit(this);
			this.prepareCompilationUnit(env);
//			if ( !this.alreadCompiled ) {
//			}
			alreadyCalcInterfaceTypes = true;

			boolean allowCreationOfPrototypesInLastCompilerPhases = env.getAllowCreationOfPrototypesInLastCompilerPhases();
			try {

				for ( Prototype prototype : prototypeList ) {
					if ( this.cyanPackage.getPackageName().equals(MetaHelper.cyanLanguagePackageName) ) {
						env.setAllowCreationOfPrototypesInLastCompilerPhases(true);
					}
					if ( prototype.getPrototypeIsNotGeneric() )
					    prototype.calcInterfaceTypes(env);
				}
			}
			catch (NullPointerException e ) {
				e.printStackTrace();
			}
			finally {

				env.setAllowCreationOfPrototypesInLastCompilerPhases(allowCreationOfPrototypesInLastCompilerPhases);
			}




			checkErrorMessages(env);
			env.atEndOfCurrentCompilationUnit();
		}
	}


	/**
	 *
	 * Check whether the error messages appointed by metaobject annotations to compilationError were
	 * signaled by the compiler. The messages checked are only those with the same line number
	 * in compilationError and in the compiler
	 *
	   @param env
	 */
	private void checkErrorMessages(Env env) {

		/*
		 * checking should be done if errorList.size() > 0
		 * At the end of compilation, before step 10, there should be
		 *
		 */
		if ( lineMessageList != null && lineMessageList.size() > 0 &&
				errorList != null && errorList.size() > 0  ) {
			/*
			 * metaobject <code>compilationErrro</code> was called in the current
			 * compilation unit. The we need to check whether the compiler really
			 * Signaled the errors appointed by the calls to this metaobject
			 */
			int ii = 0;
			/*
			 * metaobject compilationError has been called in this compilation unit
			 */
			List<Tuple3<Symbol, Integer, String>> moreErrorList = null;
			String expectedErrorMessage = null;
			for (UnitError ue : errorList ) {
				for ( Tuple3<Integer, String, Boolean> t: lineMessageList ) {
					if ( !t.f3 && t.f1 == ue.getLineNumber() ) {
						// found the correct line number  and the error has not been signaled
						expectedErrorMessage = t.f2;

						if ( moreErrorList == null )
							moreErrorList = new ArrayList<>();
						moreErrorList.add( new Tuple3<Symbol, Integer, String>(ue.getSymbol(), ue.getSymbol().getLineNumber(),
								"The expected error message at line " + ue.getLineNumber() + " was '" + expectedErrorMessage + "'. The message given by the compiler was '" +
						   ue.getMessage() + "'"));
	               /* 					this.warning(ue.getSymbol(), ue.getSymbol().getLineNumber(),
								"The expected error message at line " + ue.getLineNumber() + " was '" + expectedErrorMessage + "'. The message given by the compiler was '" +
						   ue.getMessage() + "'");  */

						lineMessageList.get(ii).f3 = true;

						expectedErrorMessage = null;

						break;
					}
					++ii;
				}
			}
			if ( moreErrorList != null ) {
				for ( Tuple3<Symbol, Integer, String> error : moreErrorList ) {
					this.warning(error.f1,  error.f2,  error.f3, null, env);
				}
			}
		}
	}



	public void afterResTypes_actions(ICompiler_afterResTypes compiler_afterResTypes, CompilerManager_afterResTypes compilerManager) {

		Env env = meta.GetHiddenItem.getHiddenEnv(compiler_afterResTypes.getEnv());

		env.atBeginningOfCurrentCompilationUnit(this);


		for ( Prototype prototype : prototypeList ) {
			if ( prototype.getPrototypeIsNotGeneric() ) {
			    // prototype.afterResTypes_actions(compiler_afterResTypes, compilerManager);
				try {
				    prototype.afterResTypes_actions(compiler_afterResTypes, compilerManager);
				}
				catch ( CompileErrorException e ) {

				}
			}
		}
		env.atEndOfCurrentCompilationUnit();
	}


//	public void ati3_check(ICompiler_afterResTypes compiler_afterResTypes) {
//		// Env env = (Env ) compiler_afterResTypes.getEnv();
//		Env env = meta.GetHiddenItem.getHiddenEnv(compiler_afterResTypes.getEnv());
//		env.atBeginningOfCurrentCompilationUnit(this);
//
//
//		for ( Prototype prototype : prototypeList ) {
//			if ( prototype.getPrototypeIsNotGeneric() )
//			    prototype.ati3_check(compiler_afterResTypes, env);
//		}
//		env.atEndOfCurrentCompilationUnit();
//
//	}


	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler_semAn) {
		// compiler_semAn.getEnv().atBeginningOfCurrentCompilationUnit(this);

		Env env = meta.GetHiddenItem.getHiddenEnv(compiler_semAn.getEnv());
        env.atBeginningOfCurrentCompilationUnit(this);



		for ( Prototype prototype : prototypeList ) {
			if ( prototype.getPrototypeIsNotGeneric() ) {
				env.atBeginningOfObjectDec(prototype);
			    prototype.afterSemAn_checkDeclaration(compiler_semAn, env);
			    if ( !NameServer.isPrototypeFromInterface(prototype.getName()) ) {
				    prototype.afterSemAn_check(compiler_semAn, env);
			    }
			    env.atEndOfObjectDec();
			}
		}
		// compiler_semAn.getEnv().atEndOfCurrentCompilationUnit();

		env.atEndOfCurrentCompilationUnit();

	}




	public boolean getIsPrototypeInterface() {
		return isPrototypeInterface;
	}


	public void setIsPrototypeInterface(boolean isPrototypeInterface) {
		this.isPrototypeInterface = isPrototypeInterface;
	}




	/*public HashMap<String, CyanMetaobjectLiteralObjectIdentSeq> getMetaObjectLiteralObjectIdentSeqTable() {
		return metaObjectLiteralObjectIdentSeqTable;
	} */


	public Program getProgram() {
		return program;
	}


	public void setProgram(Program program) {
		this.program = program;
	}


	public CompilationUnit getInterfaceCompilationUnit() {
		return interfaceCompilationUnit;
	}


	public void setInterfaceCompilationUnit(CompilationUnit interfaceCompilationUnit) {
		this.interfaceCompilationUnit = interfaceCompilationUnit;
	}

	public HashMap<String, String> getConflictPrototypeTable() {
		return conflictPrototypeTable;
	}

	public Set<CyanPackage> getImportedCyanPackageSet() {
		return importedCyanPackageSet;
	}



	public List<Tuple3<Integer, String, Boolean>> getLineMessageList() {
		return lineMessageList;
	}


	public void addLineMessageList(
			Tuple3<Integer, String, Boolean> lineMessage) {
		lineMessageList.add(lineMessage);
	}


	public boolean getIsInterfaceAsObject() {
		return isInterfaceAsObject;
	}

	public void setInterfaceAsObject(boolean isInterfaceAsObject) {
		this.isInterfaceAsObject = isInterfaceAsObject;
	}

	public List<AnnotationAt> getNonAttachedAnnotationListBeforePackage() {
		return nonAttachedAnnotationListBeforePackage;
	}

	public void setNonAttachedAnnotationListBeforePackage(
			List<AnnotationAt> nonAttachedAnnotationListBeforePackage) {
		this.nonAttachedAnnotationListBeforePackage = nonAttachedAnnotationListBeforePackage;
	}


	public Set<String> getImportedPackageNameList() {
		return importedPackageNameList;
	}


	/**
	 * if this program unit was created from a generic prototype instantiation,
	 * the instantiation is in package packageNameInstantiation, prototype prototypeNameInstantiation,
	 * line number lineNumberInstantiation, and column number columnNumberInstantiation.
	 */
	private String packageNameInstantiation, prototypeNameInstantiation;
	private int lineNumberInstantiation, columnNumberInstantiation;


	public String getPackageNameInstantiation() {
		return packageNameInstantiation;
	}


	public void setPackageNameInstantiation(String packageNameInstantiation) {
		this.packageNameInstantiation = packageNameInstantiation;
	}


	public String getPrototypeNameInstantiation() {
		return prototypeNameInstantiation;
	}


	public void setPrototypeNameInstantiation(String prototypeNameInstantiation) {
		this.prototypeNameInstantiation = prototypeNameInstantiation;
	}


	public int getLineNumberInstantiation() {
		return lineNumberInstantiation;
	}


	public void setLineNumberInstantiation(int lineNumberInstantiation) {
		this.lineNumberInstantiation = lineNumberInstantiation;
	}


	public int getColumnNumberInstantiation() {
		return columnNumberInstantiation;
	}


	public void setColumnNumberInstantiation(int columnNumberInstantiation) {
		this.columnNumberInstantiation = columnNumberInstantiation;
	}


	public Map<String, JVMPackage> getImportJVMPackageSet() {
		return importJVMPackageSet;
	}

	public void setImportJVMPackageSet(Map<String, JVMPackage> importJVMPackageSet) {
		this.importJVMPackageSet = importJVMPackageSet;
	}

	public Map<String, TypeJavaRef> getImportJVMJavaRefSet() {
		return importJVMJavaRefSet;
	}

	public void setImportJVMJavaRefSet(Map<String, TypeJavaRef> importJVMJavaRefSet) {
		this.importJVMJavaRefSet = importJVMJavaRefSet;
	}


	@Override
	public WrCompilationUnit getI() {
		if ( iCompilationUnit == null ) {
			iCompilationUnit = new WrCompilationUnit(this);
		}
		return (WrCompilationUnit ) iCompilationUnit;
	}




	public boolean getAlreadPreviouslyCompiled() {
		return alreadPreviouslyCompiled;
	}

	public void setAlreadPreviouslyCompiled(boolean alreadCompiled) {
		this.alreadPreviouslyCompiled = alreadCompiled;
	}

	public boolean containsGenericPrototype() {
		boolean foundDigit = false;
		int ifn = 0;
		final int sizeFilename = filename.length();
		while ( ifn < sizeFilename ) {
			if ( filename.charAt(ifn) == '('  && Character.isDigit(filename.charAt(ifn + 1)) ) {
				foundDigit = true;
				break;
			}
			++ifn;
		}
		return foundDigit;
	}
	public boolean getGenericAndParsed() {
		return genericAndParsed;
	}

	public void setGenericAndParsed(boolean genericAndParsed) {
		this.genericAndParsed = genericAndParsed;
	}

	/**
	 * true if this compilation unit has already been parsed and it is
	 * not generic. If 'parsed' is true after phase AFTER_RES_TYPES, the AST objects
	 * for ObjectDec and InterfaceDec will be reused.
	 */
	public boolean getParsed() {
		return parsed;
	}

	public void setParsed(boolean parsed) {
		this.parsed = parsed;
	}


	public CompilationUnit getInterfaceSourceCompilationUnit() {
		return interfaceSourceCompilationUnit;
	}

	public void setInterfaceSourceCompilationUnit(
			CompilationUnit interfaceSourceCompilationUnit) {
		this.interfaceSourceCompilationUnit = interfaceSourceCompilationUnit;
	}


	/*
	 * true if there is a declaration of a generic prototype in this program unit
	 */
	private boolean hasGenericPrototype;

	/**
	 * Compiler options of this class or interface
	 */
	private CompilerOptions compilerOptions;
	/**
	 * the package to which this compilation unit belong to
	 */
	private CyanPackage cyanPackage;

	/**
	 * Canonical path of the source file. This path is something like
	 *      D:\My Dropbox\art\programming languages\Cyan\
	 * the last character is always \ in Windows
	 */
	private String packageCanonicalPath;

	/**
	 * list of the program units of this compilation unit: all objects or
	 * interfaces declared in file "filename".
	 */
	private List<Prototype> prototypeList;

	/* Map with conflicts of program units. It consists of the name of the prototype
	 * and a string. This string contains the names, separated by spaces, of the
	 * packages that define the prototype and that are imported by this compilation unit.
	 */
	private HashMap<String, String> conflictPrototypeTable;

	/*
	 * true if there is a declaration of an instantiation of  generic prototype in this compilation unit
	 * Something like "object Stack<Int> ... end".
	 */
	private boolean prototypeIsNotGeneric;



	/**
	 * the public prototype of this compilation unit
	 */
	public Prototype publicPrototype;

	/**
	 * true if this compilation unit was created by the compiler from an interface.
	 * For each interface named "Inter" the compiler creates a prototype
	 * "Proto_Inter" which is used whenever the interface appears inside
	 * an expression in a Cyan program. Only these "Proto_Inter" prototypes
	 * are in compilation units that have isPrototypeInterface equal to true
	 */
	private boolean isPrototypeInterface;

	/**
	 * the program in which this compilation unit is
	 */
	private Program program;


	/**
	 * true if method calcInterfaceTypes has already been called.
	 */
	private boolean	alreadyCalcInterfaceTypes;

	/**
	 * true if method calcInterfaceTypes has already been called.
	 */
	private boolean	alreadyCalcInternalTypes;

	/**
	 * true if this compilation unit has already been compiled in a previous
	 * compilation and its compiled prototype, in a .class file format,
	 * is in a jar file
	 */
	private boolean alreadPreviouslyCompiled;

	/**
	 * For each interface I the compiler creates another prototype <code>Proto_I</code> that represents the interface
	 * in expressions. That is, when an interface I is used as in <br>
	 * <code>             s = I prototypeName;</code> <br>
	 * the compiler in fact uses  Proto_I:<br>
	 * <code>             s = Proto_I prototypeName</code><br>
	 *
	 * If this compilation unit contains the public interface I, interfaceCompilationUnit refers to
	 * a compilation unit with the public prototype <code>Proto_I</code>
	 */
	private CompilationUnit interfaceCompilationUnit;
	/**
	 * true if this compilation unit represents an interface when
	 * considered as an object. See {@link #interfaceCompilationUnit}
	 */
	private boolean isInterfaceAsObject;
	/**
	 * list of pairs <code>(lineNumber, errorMessage, used)</code>. A metaobject that is an instance of {@link meta#IInformCompilationError}
	 * signaled that there should be an error in this source file (being compiled) at line <code>lineNumber</code>. The possible
	 * error message is <code>errorMessage</code>. 'used' is true if this error has been signaled by the compiler
	 */
	private List<Tuple3<Integer, String, Boolean>> lineMessageList;


	/**
	 * symbols of the names of the imported packages
	 */
	protected List<ExprIdentStar>	importPackageList;
	protected Set<CyanPackage>	importedCyanPackageSet;
	/**
	 * names of the imported packages.
	 */
	protected Set<String>	importedPackageNameList;

	/**
	 * symbol of keyword 'package' that starts the compilation unit
	 */
	Symbol packageSymbol;

	/**
	 * list of metaobject annotations before keyword 'package'
	 */
	List<AnnotationAt> nonAttachedAnnotationListBeforePackage;

	/**
	 * set of JVM packages imported by this compilation unit
	 */
	private Map<String, JVMPackage>  importJVMPackageSet;
	/**
	 * set of JVM classes or interfaces imported by this compilation unit
	 */
	private Map<String, TypeJavaRef>  importJVMJavaRefSet;

	/**
	 * true if this compilation unit is generic and it has already been parsed
	 */
	private boolean genericAndParsed;

	/**
	 * true if this compilation unit has already been parsed and it is
	 * not generic. If 'parsed' is true after phase AFTER_RES_TYPES, the AST objects
	 * for ObjectDec and InterfaceDec will be reused.
	 */
	private boolean parsed;


	/**
	 * This compilation unit may have been previously compiled. It it is,
	 * its program unit is in a file called NameServer.fileName_of_interfacesCompiledPrototypes.
	 * If this is true, this field refers to the original compilation unit,
	 * that of file  NameServer.fileName_of_interfacesCompiledPrototypes
	 */
	private CompilationUnit interfaceSourceCompilationUnit;
}

