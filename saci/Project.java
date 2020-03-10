package saci;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import ast.ASTNode;
import ast.ASTVisitor;
import ast.CompilationUnit;
import ast.CompilationUnitSuper;
import ast.CyanPackage;
import ast.Program;
import error.ProjectError;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCyanPackage;
import meta.WrProject;

public class Project implements ASTNode {


	public Project( Program program, String projectCanonicalPath, String execFileName ) {
		this(program, null, null, null, null, null, projectCanonicalPath, execFileName);

	}

	public Project( Program program ) {
		this(program, null, null, null, null, null, null, null);
	}

	@Override
	public WrProject getI() {
		if ( iProject == null ) {
			iProject = new WrProject(this);
		}
		return iProject;
	}

	@Override
	public void accept(ASTVisitor visitor) {
	}


	private WrProject iProject = null;

	private String projectCanonicalPath;
	private String execFileName;

	public Project( Program program, String mainPackage, String mainObject,
			        List<String> authorArray,
			        List<String> cyanPathArray,
			        List<String> importList, String projectCanonicalPath, String execFileName ) {

		this.program = program;
		this.setMainPackage(mainPackage);
		this.setMainObject(mainObject);
		this.authorArray = authorArray;
		this.setCyanPathArray(cyanPathArray);
		setCompilerManager(null);
		tmpPackageTable = new Hashtable<String, CyanPackage>();
		this.importList = importList;
		this.projectCanonicalPath = projectCanonicalPath;
		this.execFileName = execFileName;
	}

	public void addCyanPackage( CyanPackage aPackage ) {
		program.addCyanPackage(aPackage);
		tmpPackageTable.put(aPackage.getPackageName(),  aPackage);
	}

	/* (non-Javadoc)
	   @see saci.IProject#getPackageList()
	 */
	public List<CyanPackage> getPackageList() {
		return program.getPackageList();
	}


	public void setMainPackage(String mainPackage) {
		this.mainPackage = mainPackage;
	}

	public String getMainPackage() {
		return mainPackage;
	}


	public void setMainObject(String mainObject) {
		this.mainObject = mainObject;
	}

	public String getMainObject() {
		return mainObject;
	}

	public void setCompilerOptions(String compilerOptions) {
		this.compilerOptions = compilerOptions;
	}

	public String getCompilerOptions() {
		return compilerOptions;
	}

	/**
	 * prints all the project information (which was extracted from the project file)
	 */
	public void print() {
		for ( String author : authorArray )
		   System.out.println("author: " + author + "\n");
		System.out.println(
		  "Compiler Options: " + compilerOptions + "\n" +
		  "main package: " + mainPackage + "\n" +
		  "main object: " + mainObject + "\n"
		);
		for ( CyanPackage ps : this.getPackageList() )
			ps.print();
	}

	public void setAuthorArray(List<String> authorArray) {
		this.authorArray = authorArray;
	}

	public List<String> getAuthorArray() {
		return authorArray;
	}


	/**
	 * search the package with name "name"
	 */
	public CyanPackage searchPackage(String name) {
		for (CyanPackage aPackage : program.getPackageList() )
			if ( aPackage.getPackageName().compareTo(name) == 0 )
				return aPackage;
		return null;
	}



	/**
	 * search and returns the first package that has a prototype whose name  is the parameter
	 */
	public CyanPackage searchPackageOfCompilationUnit(String prototypeName) {
		for ( CyanPackage ps : program.getPackageList() ) {
			for ( CompilationUnit compilationUnit : ps.getCompilationUnitList() ) {
				if ( compilationUnit.getFileNameWithoutExtension().equals(prototypeName) )
					return ps;
			}
		}
		return null;
	}


	public void printErrorList(PrintWriter printWriter) {

		if ( projectErrorList != null )
			for ( ProjectError projectError : projectErrorList )
				projectError.print(printWriter);

	}

	public void error(String message) {
		if ( this.projectErrorList == null )
			this.projectErrorList = new ArrayList<>();
		this.projectErrorList.add( new ProjectError(message));
	}



	/* (non-Javadoc)
	   @see saci.IProject#getProjectDir()
	 */
	public String getProjectDir() {
		return projectDir;
	}

	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}


	public List<String> getCyanPathArray() {
		return cyanPathArray;
	}

	public void setCyanPathArray(List<String> cyanPathArray) {
		this.cyanPathArray = cyanPathArray;
	}


	public CyanPackage getCyanLangPackage() {
		return cyanLangPackage;
	}

	public void setCyanLangPackage(CyanPackage cyanLangPackage) {
		this.cyanLangPackage = cyanLangPackage;
	}


	public CompilerManager getCompilerManager() {
		return compilerManager;
	}

	public void setCompilerManager(CompilerManager compilerManager) {
		this.compilerManager = compilerManager;
	}


	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Hashtable<String, CyanPackage> getTmpPackageTable() {
		return tmpPackageTable;
	}


	public boolean hasErrors() {
		return projectErrorList != null && projectErrorList.size() > 0;
	}

	public List<String> getImportList() {
		return importList;
	}

	public void setImportList(List<String> importList) {
		this.importList = importList;
	}


	public void setProgramKeyValue(String key, Object value) {
		program.setProgramKeyValue(key, value);
	}


	public Object getProgramValueFromKey(String key) {
		return program.getProgramValueFromKey(key);
	}


	private List<String> authorArray;
	/**
	 * Compiler options applicable to the whole program. These options may be
	 * overridden in packages and in files.
	 */
	private String compilerOptions;
	private String mainPackage;
	private String mainObject;


	/**
	 * path of directories in which there are Cyan packages. These
	 * packages are not in the directory of the project as usual.
	 */
	private List<String> cyanPathArray;

	private List<ProjectError> projectErrorList;

	/**
	 * the directory in which the project is
	   @param projectDir
	 */
	private String projectDir;
	/**
	 * the package cyan.lang
	 */
	private CyanPackage	cyanLangPackage;

	private CompilerManager compilerManager;


	private Program program;

	  /*
	   * set with the "--tmp" packages that should be created. A prototype
	   * "Stack{@literal <}Int>" that is the instantiation of "Stack{@literal <}T>" of package "util"
	   * is put in a package "util.tmp". This package, created by the Compiler,
	   * is added to packageTable. All of these packages should be compiled
	   * after all generic instantiations like "Stack{@literal <}Int>" are created.
	   * Prototype "Stack{@literal <}Int>" is created in a file "util\--tmp\Stack(Int).cyan"
	   */
	private Hashtable<String, CyanPackage> tmpPackageTable;

	/**
	 * the import list of this project
	 */
	private List<String> importList;


	public Set<String> getProgramKeyValueSet(String variableName) {
		return program.getProgramKeyValueSet(variableName);
	}


	public List<ProjectError> getProjectErrorList() {
		return projectErrorList;
	}


	public boolean getCallJavac() {
		return callJavac;
	}

	public void setCallJavac(boolean callJavac) {
		this.callJavac = callJavac;
	}


	public boolean getExec() {
		return exec;
	}

	public void setExec(boolean exec) {
		this.exec = exec;
	}



	public String getParseOnlyFile() {
		return parseOnlyFile;
	}

	public void setParseOnlyFile(String parseOnlyFile) {
		this.parseOnlyFile = parseOnlyFile;
	}

	public List<String> getNamePackageImportList() {
		return namePackageImportList;
	}

	public List<String> getPathPackageImportList() {
		return pathPackageImportList;
	}


	public void addNamePackageImportList(String name) {
		namePackageImportList.add(name);
	}

	public void addPathPackageImportList(String path) {
		pathPackageImportList.add(path);
	}


	public String getCmdLineArgs() {
		return cmdLineArgs;
	}

	public void setCmdLineArgs(String cmdLineArgs) {
		this.cmdLineArgs = cmdLineArgs;
	}


	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName,
			String extension,
			String packageName,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return this.compilerManager.
				readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);

	}



	public Tuple4<FileError, char[], String, String> readTextFileFromProgram(
			String fileName,
			String extension,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return this.compilerManager.readTextFileFromProject(fileName, extension, hiddenDirectory, numParameters, realParamList);
	}

	public Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(String fileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return this.compilerManager.
				getAbsolutePathHiddenDirectoryFile(fileName, packageName, hiddenDirectory);
	}


	public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(String fileName, String packageName) {

		return compilerManager.
				readBinaryDataFileFromPackage(fileName, packageName);
	}


	public boolean deleteDirOfTestDir(String dirName) {
		String testPackageName = this.getPackageNameTest();
		return compilerManager.deleteDirOfTestDir(dirName, testPackageName);
	}


	/**
	 * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
	 * Return an object of FileError indicating any errors.
	 */
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		String testPackageName = this.getPackageNameTest();
		return compilerManager.writeTestFileTo(data, fileName, dirName, testPackageName);
	}


	/**
	 * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
	 * Return an object of FileError indicating any errors.
	 */

	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
		return compilerManager.writeTestFileTo(data, fileName, dirName, packageName);
	}


	public String getPackageNameTest() {
		return "project" + MetaHelper.suffixTestPackageName;
	}

	public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return compilerManager.writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	public FileError writeTextFile(
			String str,
			String fileName,
			String prototypeFileName,
			String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return compilerManager.writeTextFile(str, fileName, prototypeFileName, packageName, hiddenDirectory);
	}


		//return CompilerManager.writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);

	public String getPathFileHiddenDirectory(String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return compilerManager.getPathFileHiddenDirectory(prototypeFileName, packageName, hiddenDirectory);
	}


	/**
	 * the source code of the .pyan file that keeps the project. It should end with '\0'
	 */
	private char []text;

	/**
	 * true if the Java compiler should be called after compiling the Cyan code
	 */
	private boolean callJavac;
	/**
	 * true if the compiled Java code should be executed after it successfully compiles
	 */
	private boolean exec;

	/**
	 * contains the filename of the only file that should be parsed.
	 */
	private String parseOnlyFile = null;

	/* (non-Javadoc)
	   @see saci.IProject#getText()
	 */
	public char[] getText() {
		return text;
	}

	public void setText(char[] text) {
		this.text = text;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/* (non-Javadoc)
	   @see saci.IProject#getProjectName()
	 */
	public String getProjectName() {
		return this.projectName;
	}

	/* (non-Javadoc)
	   @see saci.IProject#getProjectCanonicalPath()
	 */
	public String getProjectCanonicalPath() {
		return projectCanonicalPath;
	}


	public CompilationUnitSuper getProjectCompilationUnit() {
		return projectCompilationUnit;
	}

	public void setProjectCompilationUnit(
			CompilationUnitSuper programCompilationUnit) {
		this.projectCompilationUnit = programCompilationUnit;
	}
	private CompilationUnitSuper projectCompilationUnit;


	/**
	 * the name of the project file without the extension. For example, it can be 'project'
	 */
	private String projectName;


	/**
	 * name of the imported packages (to be used when there is a metaobject annotation inside the .pyan file)
	 */
	List<String> namePackageImportList = new ArrayList<>();
	/**
	 * path of the imported packages (to be used when there is a metaobject annotation inside the .pyan file)
	 */
	List<String> pathPackageImportList = new ArrayList<>();


	/**
	 * the arguments that should be passed to the Cyan program
	 */
	private String cmdLineArgs = null;

	public String getExecFileName() {
		return execFileName;
	}


}