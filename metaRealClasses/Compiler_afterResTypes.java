package metaRealClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import ast.CompilationUnit;
import ast.CyanPackage;
import ast.FieldDec;
import ast.GenericParameter;
import ast.InterfaceDec;
import ast.MethodDec;
import ast.ObjectDec;
import ast.Prototype;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.IActionFunction;
import meta.ICompiler_afterResTypes;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCompilationUnit;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrExprAnyLiteral;
import meta.WrFieldDec;
import meta.WrMethodDec;
import meta.WrPrototype;
import meta.WrSymbol;
import saci.Env;


public class Compiler_afterResTypes implements ICompiler_afterResTypes {

	public Compiler_afterResTypes(Env env) {
		this.env = env.clone();
	}



	@Override
	public List<WrFieldDec> getFieldList() {
		final ObjectDec obj = env.getCurrentObjectDec();
		if ( obj == null || obj.getFieldList() == null ) {
			return null;
		}
		else {
			final List<WrFieldDec> ret = new ArrayList<>();
			for ( final FieldDec iv : obj.getFieldList() ) {
				ret.add(iv.getI());
			}
			return ret;
		}
	}

	List<WrMethodDec> iMethodDecList = null;
	boolean thisMethod_wasNeverCalled = true;

	@Override
	public List<WrMethodDec> getMethodDecList() {
		if ( thisMethod_wasNeverCalled ) {
			final Prototype pu = env.getCurrentPrototype();

			if ( pu == null || ! (pu instanceof ObjectDec) ) {
				iMethodDecList = null;
			}
			else {
				final ObjectDec proto = (ObjectDec ) pu;
				List<MethodDec> fromList = proto.getMethodDecList();
				if ( fromList == null ) {
						// unnecessary, just to document
					iMethodDecList = null;
				}
				else {
					iMethodDecList = new ArrayList<>();
					for ( MethodDec from : fromList ) {
						iMethodDecList.add( from.getI() );
					}
				}
			}

			thisMethod_wasNeverCalled = false;

		}
		return iMethodDecList;
	}



	@Override
	public WrFieldDec searchField(String strParam) {
		final Prototype pu = env.getCurrentPrototype();
		if ( pu != null ) {
			if ( pu instanceof ObjectDec ) {
				final ObjectDec objDec = (ObjectDec ) pu;
				final FieldDec instVarDec = objDec.searchFieldDec(strParam);
				if ( instVarDec != null ) {
					return instVarDec.getI();
				}
			}
		}
		return null;
	}

	@Override
	public String getUniqueFieldName(String packageName,
			String prototypeName) {

		Integer N = mapPackagePrototypekeyword.get(packageName + " " + prototypeName);
		if ( N == null ) {
			N = 0;
			mapPackagePrototypekeyword.put(packageName + " " + prototypeName, N);
		}
		mapPackagePrototypekeyword.put(packageName + " " + prototypeName, N+1);
		return "__id" + N;
	}

	/**
	 * return a unique method name of <code>numberOfkeywords</code> keywords to
	 * prototype <code>prototypeName</code> of package <code>packageName</code>.
	 * The keywords start with "__". User identifiers cannot
	 * start with two underscores.
	   @param numberOfkeywords
	   @param packageName
	   @param prototypeName
	   @return an array with the keywords of the method
	 */
	@Override
	public String []getUniqueMethodName(int numberOfkeywords,
			String packageName, String prototypeName) {

		Integer N = mapPackagePrototypekeyword.get(packageName + " " + prototypeName);
		if ( N == null ) {
			N = 0;
			mapPackagePrototypekeyword.put(packageName + " " + prototypeName, N);
		}
		int numberId = N;
		final String []methodName = new String[numberOfkeywords];
		for ( int i = 0; i < numberOfkeywords; ++i ) {
			methodName[i] = "__s" + numberId++;
		}
		N += numberOfkeywords;
		mapPackagePrototypekeyword.put(packageName + " " + prototypeName, N);
		return methodName;
	}




	@Override
	public void error(WrSymbol symbol, String message) {
		env.error(meta.GetHiddenItem.getHiddenSymbol(symbol), message);
	}


	@Override
	public WrEnv getEnv() {
		return env.getI();
	}

	@Override
	public List<List<String>> getGenericPrototypeArgListList() {
		final Prototype pu = env.getCurrentPrototype();
		if ( pu == null || pu.getGenericParameterListList() == null || pu.getGenericParameterListList().size() == 0 )
			return null;
		else {
			// current prototype is generic
			final List<List<String>> strListList = new ArrayList<>();
			for ( final List<GenericParameter> gpList: pu.getGenericParameterListList() ) {
				final List<String> strList = new ArrayList<>();
				for ( final GenericParameter gp: gpList ) {
					strList.add(gp.getParameter().asString());
				}
				strListList.add(strList);
			}
			return strListList;
		}
	}

	@Override
	public String getCurrentPrototypeName() {
		if ( env.getCurrentPrototype() != null ) {
			return env.getCurrentPrototype().getName();
		}
		else
			return null;
	}

	@Override
	public Object getProgramValueFromKey(String variableName) {
		return this.env.getProject().getProgram().getProgramValueFromKey(variableName);
	}

	@Override
	public Set<String> getProgramKeyValueSet(String variableName) {
		return env.getProject().getProgramKeyValueSet(variableName);
	}

	@Override
	public boolean isCurrentPrototypeInterface() {
		final Prototype pu = env.getCurrentPrototype();
		if ( pu != null && pu instanceof InterfaceDec )
			return true;
		else
			return false;
	}

	/**
	 * return the feature list of the current prototype, if there is one. Otherwise return null
	 */
	@Override
	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		final Prototype pu = env.getCurrentPrototype();
		if ( pu != null ) {
			return pu.getFeatureList();
		}
		else
			return null;
	}



	@Override
	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName,
			String extension,
			String packageName,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return env.getProject().getCompilerManager().
				readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);

	}

	@Override
	public Tuple4<FileError, char[], String, String> readTextFileFromProgram(
			String fileName,
			String extension,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return env.getProject().getCompilerManager().readTextFileFromProject(fileName, extension, hiddenDirectory, numParameters, realParamList);
	}


	@Override
	public Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(String fileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().
				getAbsolutePathHiddenDirectoryFile(fileName, packageName, hiddenDirectory);
	}

	@Override
	public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(String fileName, String packageName) {
		return env.getProject().getCompilerManager().
				readBinaryDataFileFromPackage(fileName, packageName);

	}

	@Override
	public boolean deleteDirOfTestDir(String dirName) {
		final String testPackageName = this.getPackageNameTest();
		return env.getProject().getCompilerManager().deleteDirOfTestDir(dirName, testPackageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		final String testPackageName = this.getPackageNameTest();
		return env.getProject().getCompilerManager().
				writeTestFileTo(data, fileName, dirName, testPackageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
	 * Return an object of FileError indicating any errors.
	 */

	@Override
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
		return env.getProject().getCompilerManager().
				writeTestFileTo(data, fileName, dirName, packageName);
	}


	@Override
	public String getPackageNameTest() {
		return env.getCurrentCompilationUnit().getPackageName() + MetaHelper.suffixTestPackageName;
	}


	@Override
	public WrPrototype getPrototype() {
		Prototype pu = env.getCurrentPrototype();
		return pu == null ? null : pu.getI();
	}

	@Override
	public WrCompilationUnit getCompilationUnit() {
		CompilationUnit compilationUnit = env.getCurrentCompilationUnit();
		return compilationUnit == null ? null : compilationUnit.getI();
	}

	@Override
	public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	@Override
	public FileError writeTextFile(
			String str,
			String fileName,
			String prototypeFileName,
			String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().writeTextFile(str, fileName, prototypeFileName, packageName, hiddenDirectory);
	}


	@Override
	public String getPathFileHiddenDirectory(String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().getPathFileHiddenDirectory(prototypeFileName, packageName, hiddenDirectory);
	}


	@Override
	public 	IActionFunction searchActionFunction(String name) {
		return env.searchActionFunction(name);
	}


	@Override
	public Tuple5<FileError, byte[], String, String, CyanPackage> readBinaryFileFromPackage(
			String fileName, String extension, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().readBinaryFileFromPackage(fileName, extension, packageName, hiddenDirectory);
	}



	@Override
	public Tuple4<FileError, byte[], String, String> readBinaryFileFromProject(
			String fileName, String extension,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().readBinaryFileFromProject(fileName, extension, hiddenDirectory);
	}


	private final Env env;

	static {
		mapPackagePrototypekeyword = new HashMap<>();
	}
	private static HashMap<String, Integer> mapPackagePrototypekeyword;


}
