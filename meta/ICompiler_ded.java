package meta;

import java.util.List;

/**
 * compiler as seen by Codegs. "ded" is "during editing". This interface brings the view of the compiler after just
 * the parsing of the source code being edited.
   @author jose
 */
public interface ICompiler_ded extends IAbstractCyanCompiler {

	/**
	 * write <code>data</code> to file <code>fileName</code> of the data directory of package <code>packageName</code>.
	 * If there is any error, this is signaled in symbol <code>sym</code> with message <code>errorMessage</code>.
	 * <br>
	 *
	 * Return an object of type {@link meta.FileError} which may be checked to discover if there was any errors in the process.
	 *
	 * This method should check if the current compilation unit imported package <code>packageName</code>.
	 * Currently that is not checked.
	   @param fileName
	   @param packageName
	   @return
	 */
	FileError saveBinaryDataFileToPackage(byte[] data, String fileName, String packageName);

	/**
	 * return the absolute path in which the file <code>fileName</code> would be stored in the data directory of package <code>packageName</code>.
	 * If the package does not exist or the fileName is invalid, returns null
	   @param fileName
	   @param packageName
	   @return
	 */
	String pathDataFilePackage(String fileName, String packageName);

	/**
	 * return a list of pairs (name, type) of local variables visible where the metaobject annotation is. If the type is not explicitly given,
	 * the 'type' is null
	   @return
	 */
	List<Tuple2<String, String>> getLocalVariableList();
	/**
	 * return a list of pairs (name, type) of fields of the current prototype
	   @return
	 */
	List<Tuple2<String, String>> getFieldList();

	/**
	 * return a list of all methods of this object, including the inherited ones
	   @return
	 */
	List<MethodComplexName> getMethodList();
	/**
	 * return the directory of package cyan.lang
	 */
	String getCyanLangDir();

	/**
	 * return the directory of the project
	   @return
	 */
	String getProjectDir();
}
