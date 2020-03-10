package meta;

import java.util.List;
import java.util.Set;
import ast.CyanPackage;
import saci.Project;

public class Compiler_dpp implements ICompiler_dpp {

	private Project project;

	public Compiler_dpp(Project project) {
		this.project = project;
	}

	@Override
	public Object getProgramValueFromKey(String key) {
		return project.getProgramValueFromKey(key);
	}


	@Override
	public Set<String> getProgramKeyValueSet(String variableName) {
		return project.getProgramKeyValueSet(variableName);
	}


	@Override
	public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(String fileName, String packageName) {
		return project.readBinaryDataFileFromPackage(fileName, packageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
	 * Return an object of FileError indicating any errors.
	 */

	@Override
	public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		return project.writeTestFileTo(data, fileName, dirName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
	 * Return an object of FileError indicating any errors.
	 */


	@Override
	public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
		return project.writeTestFileTo(data, fileName, dirName, packageName);
	}



	@Override
	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName,
			String extension,
			String packageName,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return project.readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);
	}

	@Override
	public Tuple4<FileError, char[], String, String> readTextFileFromProgram(String fileName, String extension,
			DirectoryKindPPP hiddenDirectory, int numParameters, List<String> realParamList) {
		return project.readTextFileFromProgram(fileName, extension, hiddenDirectory, numParameters, realParamList);

	}


	@Override
	public Tuple3<String, String, WrCyanPackage>
	getAbsolutePathHiddenDirectoryFile(String fileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return project.getAbsolutePathHiddenDirectoryFile(fileName, packageName, hiddenDirectory);
	}

	@Override
	public String getPackageNameTest() {
		return project.getPackageNameTest();
	}

	@Override
	public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return project.writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	@Override
	public FileError writeTextFile(String str, String fileName, String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return project.writeTextFile(str, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	@Override
	public String getPathFileHiddenDirectory(String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return project.getPathFileHiddenDirectory(prototypeFileName, packageName, hiddenDirectory);
	}

	@Override
	public boolean deleteDirOfTestDir(String dirName) {
		return project.deleteDirOfTestDir(dirName);
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


}
