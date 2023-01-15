package ast;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectFromDSL_toPrototype;
import meta.IActionNewPrototypes_afterResTypes;
import meta.IAction_afterResTypes;
import meta.Tuple2;
import meta.WrCyanPackage;
import meta.WrCyanPackage_dpp;
import meta.WrExprAnyLiteral;
import saci.CollectError;
import saci.CompilerOptions;
import saci.LoadUtil;
import saci.NameServer;
import saci.Project;

public class CyanPackage implements ASTNode, Declaration {

	public CyanPackage(Program program, String packageName, Project project, String packageCanonicalPath,
	        List<AnnotationAt> attachedAnnotationList,
	        List<CyanMetaobject> metaobjectList,
	        List<CyanMetaobject> pyanBeforePackageMetaobjectList

			) {
		this.program = program;
		this.packageName = packageName;
		compilationUnitList = new ArrayList<CompilationUnit>();
		this.project = project;
		this.setPackageCanonicalPath(packageCanonicalPath);
		this.attachedAnnotationList = attachedAnnotationList;
		if ( this.attachedAnnotationList == null ) {
			this.attachedAnnotationList = new ArrayList<>();
		}
		hasGenericPrototype = false;
		compilerVersionLastSuccessfulCompilation = -1;
		this.metaobjectList = metaobjectList;
		this.compilationUnitDSLList= new ArrayList<CompilationUnitDSL>();
		packageKeyValueMap = new HashMap<>();
		packageKeyValueSet = new HashMap<>();
		if ( pyanBeforePackageMetaobjectList != null ) {
			this.loadCyanMetaobjects(pyanBeforePackageMetaobjectList);
		}
	}

	@Override
	public void accept(ASTVisitor visitor) {

		visitor.preVisit(this);
		for ( CompilationUnit cunit : this.compilationUnitList ) {
			cunit.accept(visitor);
		}
		visitor.visit(this);
	}


	/** prints all package information
	 *
	 */
	public void print() {
		System.out.println("    package name: " + packageName+ "\n" +  // ok
				  "    Compiler Options: " + compilerOptions + "\n" +
				  "    source files: "
				);
		for (int i = 0; i < compilationUnitList.size(); i++) {
			System.out.println("        source: " + compilationUnitList.get(i).getFilename()  + "\n");  // ok
			compilationUnitList.get(i).getCompilerOptions().print();
		}

	}

	public void addCompilationUnit(CompilationUnit compilationUnit) {
		compilationUnitList.add(compilationUnit);
	}

	public List<CompilationUnit> getCompilationUnitList() {
		return Collections.unmodifiableList(compilationUnitList);
	}


	public void setCompilationUnitList(
			List<CompilationUnit> compilationUnitList) {
		this.compilationUnitList = compilationUnitList;
	}

	public void addCompilationUnitDSL(CompilationUnitDSL compilationUnitDSL) {
		compilationUnitDSLList.add(compilationUnitDSL);
	}

	public List<CompilationUnitDSL> getCompilationUnitDSLList() {
		return compilationUnitDSLList;
	}



	public String getPackageName() {
		return packageName;
	}

	public void setCompilerOptions(CompilerOptions compilerOptions) {
		this.compilerOptions = compilerOptions;
	}


	public CompilerOptions getCompilerOptions() {
		return compilerOptions;
	}


	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Program getProgram() {
		return program;
	}

	//static long countIter = 0;
	/**
	 * search and returns a public program unit whose name is <code>"name"</code> in this package.
	 * It includes program units like <code>"Stack< main.Person >"</code> but not <code>Stack<T></code>. In
	 * the last case, <code>T</code> is a generic parameter
	 */
	public Prototype searchPublicNonGenericPrototype(String name) {

		// Use the code below. The // one does not replace some
		// compilation units by new ones
		for ( CompilationUnit compilationUnit : compilationUnitList ) {
			Prototype pu = compilationUnit.getPublicPrototype();
			if ( pu != null && pu.getName().equals(name)  && ! pu.getGenericPrototype() ) {
				return pu;
			}
		}
//		int i = 0;
//		try {
//
//		}
//		catch ( Exception e ) {
//		}
		return null;

//		if ( namePrototypeMap == null ) {
//			namePrototypeMap = new HashMap<>();
//			for ( CompilationUnit compilationUnit : compilationUnitList ) {
//				Prototype pu = compilationUnit.getPublicPrototype();
//				if ( pu != null && ! pu.getGenericPrototype() ) {
//					namePrototypeMap.put(pu.getName(), pu);
//				}
//			}
//		}
//		Prototype progUnit = namePrototypeMap.get(name);
//		if ( progUnit == null ) {
//			for ( CompilationUnit compilationUnit : compilationUnitList ) {
//				Prototype pu = compilationUnit.getPublicPrototype();
//				if ( pu != null && pu.getName().equals(name)  && ! pu.getGenericPrototype() ) {
//					progUnit = compilationUnit.getPublicPrototype();
//					this.namePrototypeMap.put(progUnit.getName(), progUnit);
//				}
//			}
//		}
//		return progUnit;
	}

	private Map<String, Prototype> namePrototypeMap = null;
	/**
	 * return prototype whose source file name is sourceFileName and that was declared in this package
	 */

	public Prototype searchPrototypeBySourceFileName(String sourceFileName) {

//		if ( sourceFileNamePrototypeMap == null ) {
//			sourceFileNamePrototypeMap = new HashMap<>();
//			for ( CompilationUnit compilationUnit : compilationUnitList ) {
//				Prototype pu = compilationUnit.getPublicPrototype();
//				sourceFileNamePrototypeMap.put(
//						compilationUnit.getFileNameWithoutExtension(), pu);
//			}
//		}
//		Prototype pu1 = sourceFileNamePrototypeMap.get(sourceFileName);
//		return pu1;
		for ( CompilationUnit compilationUnit : compilationUnitList ) {
			if ( compilationUnit.getFileNameWithoutExtension().equals(sourceFileName) ) {
				return compilationUnit.getPublicPrototype();
			}
		}
		return null;
	}

	private Map<String, Prototype> sourceFileNamePrototypeMap = null;


	/**
	 * return the compilation unit that has name 'fileName.cyan' of this package. null if none.
	   @return
	 */
	public CompilationUnit searchCompilationUnit(String fileName) {
		for ( CompilationUnit compilationUnit : compilationUnitList ) {
			if ( compilationUnit.getFileNameWithoutExtension().equals(fileName) )
				return compilationUnit;
		}
		return null;
	}

	public String getPackageCanonicalPath() {
		return packageCanonicalPath;
	}


	public void setPackageCanonicalPath(String packageCanonicalPath) {
		this.packageCanonicalPath = packageCanonicalPath;
	}

	public List<CyanMetaobject> getMetaobjectList() {
		return metaobjectList;
	}

	public boolean getHasGenericPrototype() {
		return hasGenericPrototype;
	}


	public void setHasGenericPrototype(boolean hasGenericPrototype) {
		this.hasGenericPrototype = hasGenericPrototype;
	}

	public List<AnnotationAt> getAttachedAnnotationList() {
		return attachedAnnotationList;
	}

	public List<AnnotationAt> getAttachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes() {
		if ( attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes == null ) {
			attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes = new ArrayList<>();
			if ( attachedAnnotationList != null ) {
				for ( AnnotationAt withAtAnnot : attachedAnnotationList ) {
					CyanMetaobject cyanMetaobject = withAtAnnot.getCyanMetaobject();
					if ( cyanMetaobject instanceof IAction_afterResTypes ||
						 cyanMetaobject instanceof IActionNewPrototypes_afterResTypes ) {
						attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes.add(withAtAnnot);
					}
				}
			}
		}
		return attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes;
	}
	/**
	 * the annotations of attachedAnnotationList that implement interfaces
	 * IAction_afterResTypes or IActionNewPrototypes_afterResTypes
	 */
	List<AnnotationAt>  attachedAnnotationList_IActionPrototype_afterResTypes_IActionNewPrototypes_afterResTypes = null;


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



	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		return featureList;
	}

	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature) {
		if ( featureList == null )
			featureList = new ArrayList<>();
		/*else {
			int size = featureList.size();
			for ( int i = 0; i < size; ++i) {
				if ( featureList.get(i).f1.equals(feature.f1) ) {
					// replace
					featureList.set(i, feature);
					return;
				}
			}
		}
		*/
		featureList.add(feature);
	}

	public void addFeatureList( List<Tuple2<String, WrExprAnyLiteral>> featureList1) {
		for ( Tuple2<String, WrExprAnyLiteral> t : featureList1 ) {
			this.addFeature(t);
		}
	}


	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}



	public int getCompilerVersionLastSuccessfulCompilation() {
		return compilerVersionLastSuccessfulCompilation;
	}

	public void setCompilerVersionLastSuccessfulCompilation(int compilerVersionLastSuccessfulCompilation) {
		this.compilerVersionLastSuccessfulCompilation = compilerVersionLastSuccessfulCompilation;
	}


	public void checkPublicNonGenericPrototype() {
		HashSet<String> unitNameSet = new HashSet<>();
		int numRepeated = 0;
		for ( CompilationUnit compilationUnit : compilationUnitList ) {
			Prototype pu = compilationUnit.getPublicPrototype();
			String name = pu.getName();
			if ( unitNameSet.contains(name) ) {
				System.out.println("already in set: " + unitNameSet);   // ok
				++numRepeated;
			}
			else {
				unitNameSet.add(name);
			}
		}
		System.out.println("cunitList size = " + compilationUnitList.size() + " repeated = " + numRepeated);  // ok
	}

	public String getName() {
		return this.getPackageName();
	}

	public AttachedDeclarationKind getKind() {
		return AttachedDeclarationKind.PACKAGE_DEC;
	}

	public boolean removeCompilationUnit(CompilationUnit cunit) {
		for (int i = this.compilationUnitList.size() - 1; i >= 0; --i) {
			if ( compilationUnitList.get(i) == cunit ) {
				this.compilationUnitList.remove(i);
				return true;
			}
		}
		return false;
	}


	public void setPackageKeyValue(String variableName, Object value) {
		packageKeyValueMap.put(variableName, value);
	}


	public Object getPackageValueFromKey(String key) {
		return packageKeyValueMap.get(key);
	}


	public void addToPackageKeyValueSet(String key, String value) {
		HashSet<String> set = packageKeyValueSet.get(key);
		if ( set == null ) {
			set = new HashSet<>();
		}
		set.add(value);
		packageKeyValueSet.put(key, set);
	}


	public Set<String> getPackageKeyValueSet(String variableName) {
		return packageKeyValueSet.get(variableName);
	}


	/**
	 * load all the metaobjects of list metaobjectList into this package. These metaobjects will be
	 * loaded only during the parsing of the project file.
	 */
	public void loadCyanMetaobjects(List<CyanMetaobject> metaobjectList1) {


		for ( CyanMetaobject cyanMetaobject : metaobjectList1 ) {
			if ( cyanMetaobject instanceof CyanMetaobjectFromDSL_toPrototype ) {

				CyanMetaobjectFromDSL_toPrototype cyanMetaobjectDSL = (CyanMetaobjectFromDSL_toPrototype ) cyanMetaobject;
				CyanMetaobjectFromDSL_toPrototype cyanMetaobjectDSLOther = (CyanMetaobjectFromDSL_toPrototype )
						this.pyanBeforePackageMetaobjectMap.get(cyanMetaobjectDSL.getName());
				if ( cyanMetaobjectDSLOther != null ) {
					project.error("Metaobject with name '" +
							cyanMetaobjectDSL.getName() + "' is imported from package "
						       + cyanMetaobjectDSLOther.getPackageName() + " and from package " +
							cyanMetaobjectDSL.getPackageName() + " The names of the .class files are '" +
								       cyanMetaobject.getCanonicalPath() + "' and '" +
								       cyanMetaobjectDSLOther.getCanonicalPath() + "'");

				}
				pyanBeforePackageMetaobjectMap.put( cyanMetaobjectDSL.getName(),
						cyanMetaobjectDSL);
			}
			/*
			 *  // currently only at-metaobjects can be used in the project file
			else if ( cyanMetaobject instanceof CyanMetaobjectLiteralString ) {
				CyanMetaobjectLiteralString litStr = (CyanMetaobjectLiteralString ) cyanMetaobject;
				for ( String name : litStr.getPrefixNames() ) {
					CyanMetaobjectLiteralString other = metaobjectLiteralStringTable.put( name, litStr );
					if ( other != null ) {
						error(importSymbol, importSymbol.getLineNumber(), "String prefix '" +
					       name + "' is imported from metaobject '" + other.getFileName() + " of package "
					       + other.getPackageName() + " and of metaobject " + cyanMetaobject.getFileName() +
					       " of package " + cyanMetaobject.getPackageName() + " The names of the .class files are '" +
							       cyanMetaobject.getCanonicalPath() + "' and '" +
							       other.getCanonicalPath() + "'", compiler, null);
					}
				}
			}
			else if ( cyanMetaobject instanceof CyanMetaobjectMacro ) {
				CyanMetaobjectMacro cyanMacro = (CyanMetaobjectMacro ) cyanMetaobject;
				for ( String startKeyword : cyanMacro.getStartKeywords() ) {
					CyanMetaobjectMacro other = metaObjectMacroTable.put(startKeyword, cyanMacro);
					if ( other != null )
						error(importSymbol, importSymbol.getLineNumber(), "Macro '" + startKeyword +
								"' is imported from metaobject '" + other.getFileName() + " of package "
							       + other.getPackageName() + " and from metaobject " + cyanMetaobject.getFileName() +
							       " of package " + cyanMetaobject.getPackageName() + " The names of the .class files are '" +
									       cyanMetaobject.getCanonicalPath() + "' and '" +
									       other.getCanonicalPath() + "'", compiler, null);
				}
			}
			*/

		}
		//return errorListMetaobject;
	}

	public boolean getCommunicateInPackage() {
		if ( this.communicateInPackage == null ) {
			List<WrExprAnyLiteral> any = searchFeature(NameServer.COMMUNICATE_IN_PACKAGE);
			if ( any == null || any.size() == 0 ) {
				communicateInPackage = false;
			}
			else {
				for ( WrExprAnyLiteral anyLit : any ) {
					if ( anyLit.getJavaValue().equals(NameServer.ON) ) {
						communicateInPackage = true;
					}
				}
			}
		}
		return communicateInPackage;
	}
	public HashMap<String, CyanMetaobject> getPyanBeforePackageMetaobjectMap() {
		return pyanBeforePackageMetaobjectMap;
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
      add jar files and the directory NameServer.metaobjectPackageName to the class
      path of the current class loader, execute toRun, and restore the previous class path.

   */
	@SuppressWarnings("resource")
	public void addPackageMetaToClassPath_and_Run(Runnable toRun) {

		if ( urlArray == null || urlArray.length == 0 ) {
			calculateURLs();
		}
		if ( urlArray != null && urlArray.length > 0 ) {
			LoadUtil.addURL_ToClassPathRun(urlArray, toRun);
		}
		else {
			toRun.run();
		}
	}



	/**
	 * the URL array of jar files of directory NameServer.metaobjectPackageName
	   @return
	 */
	private URL []urlArray = null;

	public CollectError calculateURLs() {


		String s = this.getPackageCanonicalPath();
		if ( !s.endsWith(NameServer.fileSeparatorAsString) ) {
			s += NameServer.fileSeparatorAsString;
		}
		s += NameServer.metaobjectPackageName;

		List<URL> urlList = new ArrayList<>();
		CollectError ce = LoadUtil.collectJarsFromPath(urlList, s, null);
		if ( ce != null ) {
			return ce;
		}
		urlArray = new URL[urlList.size()];
		int i = 0;
		for ( URL url : urlList ) {
			urlArray[i] = url;
			++i;
		}
		return null;
	}

	@Override
	public WrCyanPackage getI() {
		if ( iCyanPackage == null ) {
			iCyanPackage = new WrCyanPackage(this);
		}
		return iCyanPackage;
	}

	public WrCyanPackage_dpp get_dpp() {
		if ( iCyanPackage_dpp == null ) {
			iCyanPackage_dpp = new WrCyanPackage_dpp(this);
		}
		return iCyanPackage_dpp;
	}

	public char [] getInterfacesCompiledPrototypesText() {
		return interfacesCompiledPrototypesText;
	}

	public void setInterfacesCompiledPrototypesText(
			char [] interfacesCompiledPrototypesText) {
		this.interfacesCompiledPrototypesText = interfacesCompiledPrototypesText;
	}
	public boolean getUseCompiledPrototypes() {
		return useCompiledPrototypes;
	}

	public void setUseCompiledPrototypes(boolean useCompiledPrototypes) {
		this.useCompiledPrototypes = useCompiledPrototypes;
	}
	public boolean getCreateInterfaceFileForSeparateCompilation() {
		return createInterfaceFileForSeparateCompilation;
	}

	public void setCreateInterfaceFileForSeparateCompilation(
			boolean createInterfaceFileForSeparateCompilation) {
		this.createInterfaceFileForSeparateCompilation = createInterfaceFileForSeparateCompilation;
	}

	/**
	 * test if there is an updated jar file with the compiled code for
	 * this package. Two testes are made:
	 *    . if there is a jar file whose name is  'packagename.jar'
	 *      in the directory
	 *           this.packageCanonicalPath - packagePath
	 *      in which packagePath is packagename.replace('.', '/')
	 *      and '-' is string subtraction (this does not exist,
	 *      but even so it works like
	 *              "abcde" - "de"
	 *
	 *    . if all files in this.packageCanonicalPath were created
	 *      before packagename.jar
	   @return
	 */
	public boolean isThereJarFileForPackage() {

		String path;
		String packagePath = packageName.replace('.', NameServer.fileSeparator);
		String packageCanonicalPathNoEndSeparator;
		if ( packageCanonicalPath.charAt(packageCanonicalPath.length()-1) == File.separatorChar ) {
			packageCanonicalPathNoEndSeparator = packageCanonicalPath.substring(0,
					packageCanonicalPath.length()-1);
		}
		else {
			packageCanonicalPathNoEndSeparator = packageCanonicalPath;
		}
		if ( packageCanonicalPathNoEndSeparator.endsWith(
				packagePath) ) {
			path = packageCanonicalPathNoEndSeparator.substring(0,
					packageCanonicalPathNoEndSeparator.length() - packagePath.length() );
		}
		else {
			path = this.packageCanonicalPath;
		}
		String jarFilename = packageName + ".jar";
		path += jarFilename;
		File fpackage = new File(path);
		return fpackage.exists() && !fpackage.isDirectory();
//		File canonicalPath = new File(packageCanonicalPathNoEndSeparator);
//
//		return false;
	}

	public void addPrototypeNameAnnotationInfo(String prototypeName, Annotation annot) {
		if ( mapPrototypeNameAnnot == null ) {
			mapPrototypeNameAnnot = new HashMap<>();
		}
		mapPrototypeNameAnnot.put(this.packageName + "." + prototypeName, annot);
	}

	public Annotation searchAnnotationCreatedPrototype(String prototypeName) {
		if ( mapPrototypeNameAnnot == null ) {
			return null;
		}
		else {
			return mapPrototypeNameAnnot.get(prototypeName);
		}
	}

	public void setMetaobjectList(List<CyanMetaobject> metaobjectList) {
		this.metaobjectList = metaobjectList;
	}


	private WrCyanPackage iCyanPackage = null;
	private WrCyanPackage_dpp iCyanPackage_dpp = null;
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
	 * list of pairs (variableName, set). To variableName is associated the values of the set
	 */
	private HashMap<String, HashSet<String>> packageKeyValueSet;



	/**
	 * map of pairs (variableName, value) associated to the package
	 */
	private HashMap<String, Object> packageKeyValueMap;


	/**
	 * the directory in which will be put the generated Java code for this package
	 */
	private String outputDirectory;

	/**
	 * the list of features associated to this package
	 */
	private List<Tuple2<String, WrExprAnyLiteral>> featureList;


	private String packageName;
	/**
	 * Compiler options of this package.
	 */
	private CompilerOptions compilerOptions;
	/**
	 * list of compilation units of this package
	 */
	private List<CompilationUnit> compilationUnitList;


	/**
	 * list of compilation units for DSLs of this package
	 */
	private List<CompilationUnitDSL> compilationUnitDSLList;


	/**
	 * the project of the whole program
	 */
	private Project project;


	private String packageCanonicalPath;

	/**
	 * A list with the metaobjects defined in this package
	 */
	private List<CyanMetaobject> metaobjectList;


	/**
	 * list of metaobject annotations that precede this package in project .pyan
	 * and that are attached to the package.
	 */
	private List<AnnotationAt> attachedAnnotationList;

	/**
	 * true if this package has any generic prototype. If it has, then
	 * there is a directory "--tmp" inside it with the generic prototype
	 * instantiations
	 */
	private boolean hasGenericPrototype;

	/**
	 * ChooseFoldersCyanInstallation to which this Cyan package belongs to
	 */
	private Program program;

	/**
	 * compiler version of the last successful compilation of this package
	 */
	private int compilerVersionLastSuccessfulCompilation;

	/**
	 * true if the metaobjects of different prototypes should communicate with each other
	 */
	private Boolean communicateInPackage = null;


	/**
	 * a list of metaobjects that are imported before the package in the project (.pyan) file
	 */
	// private List<CyanMetaobject> pyanBeforePackageMetaobjectList;
	private HashMap<String, CyanMetaobject>	pyanBeforePackageMetaobjectMap;


	/**
	 * the text of file NameServer.fileName_of_interfacesCompiledPrototypes or
	 * null if there is no file with compiled interfaces for this package (or
	 * it should not be used)
	 */
	private char []interfacesCompiledPrototypesText;

	/**
	 * true if a file NameServer.fileName_of_interfacesCompiledPrototypes should
	 * be produced for this package. It contains the interfaces of all prototypes
	 * and in the next compilations the package need not to be compiled again
	 */
	private boolean createInterfaceFileForSeparateCompilation = false;
	/**
	 * true if this package should not be compiled again. That is, other packages
	 * that use it should use the compiled form of this package that is in
	 * file "allInterfaces.iyan"
	 */
	private boolean useCompiledPrototypes = false;

	/**
	 * map (key, value) in which the key is a prototype
	 * created by a metaobjects whose information is in 'value'
	 */
	private Map<String, Annotation> mapPrototypeNameAnnot;
	public URL[] getUrlArray() {
		return urlArray;
	}

}
