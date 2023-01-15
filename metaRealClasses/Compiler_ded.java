package metaRealClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import ast.CompilationUnit;
import ast.CompilationUnitSuper;
import ast.CyanPackage;
import ast.FieldDec;
import ast.MethodDec;
import ast.ObjectDec;
import ast.Prototype;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.ICompiler_ded;
import meta.MetaHelper;
import meta.MethodComplexName;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCyanPackage;
import saci.Project;


public class Compiler_ded implements ICompiler_ded {


	public Compiler_ded(Project project, CompilationUnitSuper compilationUnit, List<Tuple2<String, String>> localVariableNameList) {
		this.project = project;
		this.compilationUnit = compilationUnit;
		this.localVariableNameList = localVariableNameList;
	}

	@Override
	public Object getProgramValueFromKey(String variableName) {
		return project.getProgramValueFromKey(variableName);
	}

	@Override
	public Set<String> getProgramKeyValueSet(String variableName) {
		return project.getProgramKeyValueSet(variableName);
	}


	@Override
	public String getCyanLangDir() {
		return project.getProgram().getCyanLangDir();
	}

	@Override
	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName,
			String extension,
			String packageName,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return project.getCompilerManager().
				readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);

	}



	@Override
	public Tuple4<FileError, char[], String, String> readTextFileFromProgram(
			String fileName,
			String extension,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return project.getCompilerManager().readTextFileFromProject(fileName, extension, hiddenDirectory, numParameters, realParamList);
	}


	@Override
	public Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(String fileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return project.getCompilerManager().
				getAbsolutePathHiddenDirectoryFile(fileName, packageName, hiddenDirectory);
	}



	@Override
	public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(String fileName, String packageName) {
		return project.getCompilerManager().
				readBinaryDataFileFromPackage(fileName, packageName);

	}

	@Override
	public FileError saveBinaryDataFileToPackage(byte[] data, String fileName, String packageName) {
		return project.getCompilerManager().
				saveBinaryDataFileToPackage(data, fileName, packageName);
	}


	@Override
	public boolean deleteDirOfTestDir(String dirName) {
		String testPackageName = this.getPackageNameTest();
		return project.getCompilerManager().deleteDirOfTestDir(dirName, testPackageName);
	}


	/**
	 * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		return project.getCompilerManager().
				writeTestFileTo(data, fileName, dirName, getPackageNameTest());
	}

	/**
	 * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
		return project.getCompilerManager().
				writeTestFileTo(data, fileName, dirName, packageName);
	}



	@Override
	public String getPackageNameTest() {
		return "project" + MetaHelper.suffixTestPackageName;
	}

	@Override
	public String pathDataFilePackage(String fileName, String packageName) {
		return project.getCompilerManager().pathDataFilePackage(fileName, packageName);
	}


	@Override
	public List<Tuple2<String, String>> getLocalVariableList() {
		return this.localVariableNameList;
	}

	@Override
	public List<Tuple2<String, String>> getFieldList() {
		if ( compilationUnit instanceof CompilationUnit ) {
			CompilationUnit cunit = (CompilationUnit ) compilationUnit;
			Prototype pu = cunit.getPublicPrototype();
			if ( pu == null || !(pu instanceof ObjectDec) ) {
				return null;
			}
			else {
				ObjectDec proto = (ObjectDec ) pu;
				List<Tuple2<String, String>> ivList = new ArrayList<>();
				for ( FieldDec iv : proto.getFieldList() ) {
					String strType = null;
					if ( iv.getTypeInDec() != null ) {
						strType = iv.getTypeInDec().asString();
					}
					ivList.add( new Tuple2<String, String>(iv.getName(), strType));
				}
				return ivList;
			}
		}
		else {
			return null;
		}
	}


	@Override
	public List<MethodComplexName> getMethodList() {
		if ( compilationUnit instanceof CompilationUnit ) {
			CompilationUnit cunit = (CompilationUnit ) compilationUnit;
			Prototype pu = cunit.getPublicPrototype();
			if ( pu == null || !(pu instanceof ObjectDec) ) {
				return null;
			}
			else {
				ObjectDec proto = (ObjectDec ) pu;
				List<MethodComplexName> methodList = new ArrayList<>();
				for ( MethodDec method : proto.getAllMethodDecList() ) {
					methodList.add( method.getMethodSignature().getNameParametersTypes() );
				}
				return methodList;
			}
		}
		else {
			return null;
		}
	}


	@Override
	public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return project.getCompilerManager().writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);
	}



	@Override
	public FileError writeTextFile(String str, String fileName, String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return project.getCompilerManager().writeTextFile(str, fileName, prototypeFileName, packageName, hiddenDirectory);
	}


	@Override
	public
	String getPathFileHiddenDirectory(String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return project.getCompilerManager().getPathFileHiddenDirectory(prototypeFileName, packageName, hiddenDirectory);

	}


	@Override
	public Tuple5<FileError, byte[], String, String, CyanPackage> readBinaryFileFromPackage(
			String fileName, String extension, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return project.getCompilerManager().readBinaryFileFromPackage(fileName, extension, packageName, hiddenDirectory);
	}



	@Override
	public Tuple4<FileError, byte[], String, String> readBinaryFileFromProject(
			String fileName, String extension,
			DirectoryKindPPP hiddenDirectory) {
		return project.getCompilerManager().readBinaryFileFromProject(fileName, extension, hiddenDirectory);
	}

    @Override
	public String getProjectDir() {
        return project.getProjectDir();
    }


	private Project project;

	private CompilationUnitSuper compilationUnit;

	private List<Tuple2<String, String>> localVariableNameList;
}
