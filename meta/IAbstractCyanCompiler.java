package meta;

import java.util.List;
import java.util.Set;
import ast.CyanPackage;
import saci.CompilerManager;

public interface IAbstractCyanCompiler {


	/**
	 * return the value associated by the program to variable 'variableName'.
	 * This value can only be set in the project file (.pyan).
	   @param variableName
	   @return
	 */
	Object getProgramValueFromKey(String variableName);
	/**
	 * return a set of values associated by the program to variable 'variableName'.
	 * This value can only be set in the project file (.pyan).
	   @param variableName
	   @return
	 */
	Set<String> getProgramKeyValueSet(String variableName);

	/**
	 * load file <code>fileName</code> from the data directory of package <code>packageName</code> and
	 * returns it as a byte array. <br>
	 *
	 * Issue <code>errorMessage</code> in error with symbol <code>sym</code>.
	 *
	 * This method should check if the current compilation unit imported package <code>packageName</code>.
	 * Currently that is not checked.
	   @param fileName
	   @param packageName
	   @return
	 */
	Tuple2<FileError, byte []> readBinaryDataFileFromPackage(String fileName, String packageName);

	/**
	 * write <code>data</code> to a file <code>fileName</code> of directory <code>dirName</code>
	 * that is in the test directory of the current package.
	 * That is, the package where the metaobject annotation that called this method is.
	   @param data
	   @param fileName
	   @return
	 */
	FileError writeTestFileTo(StringBuffer data, String fileName, String dirName);

	/**
	 * write <code>data</code> to a file <code>fileName</code> of directory <code>dirName</code>
	 * that is in the test directory of packageName.
	   @param data
	   @param fileName
	   @return
	 */
	FileError writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName);



	Tuple5<FileError, byte[], String, String, CyanPackage> readBinaryFileFromPackage(
			String fileName,
			String extension,
			String packageName,
			DirectoryKindPPP hiddenDirectory
			);

	Tuple4<FileError, byte[], String, String> readBinaryFileFromProject(
			String fileName,
			String extension,
			DirectoryKindPPP hiddenDirectory
			);

	/**
	 * This method does the same as {@link CompilerManager#readTextFileFromPackage(String, String, String, DirectoryKindPPP, int, List)}
	 * except that the file extension is in the first parameter. Then the first parameter could be <code>"arith(T).concept"</code>.
	 */

	default Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName,
			String packageName,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {

		String extension = "";

		int i = fileName.lastIndexOf('.');
		if ( i > 0 ) {
			extension = fileName.substring(i+1);
			fileName = fileName.substring(0, i);
		}

		return readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);
	}

	/**
	 * load a text file from the hidden directory given by the enumerate value <code>hiddenDirectory</code>
	 * of package <code>packageName</code>. If numParameters is 0, the file read is fileName.extension.
	 * If numParameters is, for example, 2, the file read should have a name as <code>fileName(A,B).extension</code>.
	 * realParamList is a list of strings that should replace, in the file read, its parameters. That is,
	 * if realParamList is, in Cyan syntax, <br>
	 * <code>
	 *     [ "Company", "Client" ]<br>
	 * </code><br>
	 * and the complete file name is <code>Relation(A,B).rel</code>, this method searches for words <code>A</code>
	 * and <code>B</code> in the text read from <code>Relation(A,B).rel</code> and replaces them for <code>Company</code>
	 * and <code>Client</code>.
	 *
	 * This method returns a 5-tuple. The first element is an error messages. null if none. The second is
	 * the text read (with the replacements if realParamList is not null). The third tuple element is the complete file name, with
	 * the parameters. The fourth is the
	 * path of the directory and the fifth is the Cyan Package in which the file is.
	 *
	 */

	Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName,
			String extension,
			String packageName,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList);
	/**
	 * load a text file from the hidden directory given by the enumerate value <code>hiddenDirectory</code>
	 * of the program.  If numParameters is 0, the file read is fileName.extension.
	 * If numParameters is, for example, 2, the file read may be something as <code>fileName(A,B).extension</code>.
	 * realParamList is a list of strings that should replace, in the file read, its parameters. That is,
	 * if realParamList is, in Cyan syntax, <br>
	 * <code>
	 *     [ "Company", "Client" ]<br>
	 * </code><br>
	 * and the complete file name is <code>Relation(A,B).rel</code>, this method searches for words <code>A</code>
	 * and <code>B</code> in the text read from <code>Relation(A,B).rel</code> and replaces them for <code>Company</code>
	 * and <code>Client</code>.
	 *
	 * This method returns a 4-tuple. The first element is an error messages. null if none. The second is
	 * the text read (with the replacements if realParamList is not null). The third tuple element is the complete file name, with
	 * the parameters. The fourth is the
	 * path of the directory in which the file is.
	 *
	 */

	Tuple4<FileError, char[], String, String> readTextFileFromProgram(
			String fileName,
			String extension,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList);


	/**
	 * return a tuple consisting of: a) a file name b) a directory and c) a package.
	 *
	 *
	 * Parameter fileName contains a file name possibly with a directory such as <code>python/script0.py</code>.
	 * There should be a file <code>python/script0.py</code> in the hidden directory hiddenDirectory of package packageName.
	 *
	 * The file name returned does not contain the directory name. The directory returned is the full name of
	 * the directory.
	 *
	 * Return null if the file or directory does not exist
	   @param fileName
	 * @param packageName
	 * @param hiddenDirectory
	   @return
	 */
	Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(String fileName,
			String packageName, DirectoryKindPPP hiddenDirectory);
	/**
	 * return the package name for testing the current compilation unit
	   @return
	 */
	String getPackageNameTest();


	/**
	 *
	 */

	FileError writeTextFile(
			char[] charArray,
			String fileName,
			String prototypeFileName,
			String packageName,
			DirectoryKindPPP hiddenDirectory);

	FileError writeTextFile(
			String str,
			String fileName,
			String prototypeFileName,
			String packageName,
			DirectoryKindPPP hiddenDirectory);


		//return CompilerManager.writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);

	String getPathFileHiddenDirectory(String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory);

	/**
	 * delete all files of the directory dirName of the test directory of the current package
	 */
	boolean deleteDirOfTestDir(String dirName);


	default public String nextIdentifier() {
		return MetaHelper.nextIdentifier();
	}



}
