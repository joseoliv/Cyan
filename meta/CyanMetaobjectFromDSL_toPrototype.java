package meta;

import java.util.List;

/**
 * Subclasses of this class are used to parse code of DSLs that inside directory "--dsl" of the
 * package. The name of the directory is {@link DirectoryKindPPP#DSL}.<br>
 *
 * The file name extension should be the same as getName(). Before the compiler parses all files
 * of the program, it reads all files of directory "--dsl" of all packages. If a file has extension
 * "ext", the compiler searches for a metaobject class, subclass of  {@link CyanMetaobjectFromDSL_toPrototype},
 * whose method {@link CyanMetaobjectFromDSL_toPrototype#getName()} returns "ext". If none is found,
 * an error occurs. If one is found, method {@link CyanMetaobjectFromDSL_toPrototype#parsing_NewPrototype(char[])}
 * is called. It returns a prototype name (first tuple element) and a code (second tuple element).
 * The compiler then creates in the directory in which the DSL is a prototype with that name and code.
   @author jose
 */
abstract public class CyanMetaobjectFromDSL_toPrototype extends CyanMetaobject {

	public CyanMetaobjectFromDSL_toPrototype(String extensionName) {
		this.extensionName = extensionName;
		this.text = null;
		this.packageNameDSL = null;
		this.prototypeName = null;
	}


	@Override
	public CyanMetaobjectFromDSL_toPrototype clone() {
		try {
			return (CyanMetaobjectFromDSL_toPrototype ) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}



	@Override
	final public String getName() {
		return extensionName;
	}

	@Override
	public
	String getPackageOfType() { return packageNameDSL; }

	@Override
	public
	String getPrototypeOfType() { return prototypeName; }


	public char[] getText() {
		return text;
	}

	public String getExtensionName() {
		return extensionName;
	}


	public String getPrototypeName() {
		return prototypeName;
	}


	public void setPrototypeName(String prototypeName) {
		this.prototypeName = prototypeName;
	}


	public void setText(char[] text) {
		this.text = text;
	}

	public String getPackageNameDSL() {
		return packageNameDSL;
	}


	public void setPackageNameDSL(String packageNameDSL) {
		this.packageNameDSL = packageNameDSL;
	}


	public String getFileNameDSLSourceCode() {
		return fileNameDSLSourceCode;
	}


	public void setFileNameDSLSourceCode(String fileNameDSLSourceCode) {
		this.fileNameDSLSourceCode = fileNameDSLSourceCode;
	}

	/** return a list of tuples. Each one is composed of a prototype name (first tuple element),
	 * a file name in which this prototype should be (second tuple element),
	 *  and the prototype code (third tuple element). For each tuple, the compiler
	 * will create a file with that name with a prototype in the directory of the package in which the
	 * DSL code is. The prototype name could be a generic one. The file name should match the
	 * generic prototype. That is, if the file name is <code>MyMap(2).cyan</code>, the prototype
	 * name could be
	 * <code>MyMap{@literal <}T, E></code>
<br>
	 *
	 *
	 */
	public abstract List<Tuple3<String, String, char []>> parsing_NewPrototype(ICompiler_dsl compiler_dsl);

	/**
	 * the name of the metaobject and the name of the extension of the file with the DSL source code.
	 */
	private String extensionName;

	/**
	 * the text of file with the DSL source code
	 */
	private char []text;

	/**
	 * package name in which the file with extension getName() is. That is, there is a file
	 *        packageNameDSL\prototypeName.extensionName
	 * whose contents is 'text'.
	 */
	private String packageNameDSL;
	/**
	 * name of the file with the DSL source code. Without extension.
	 */
	private String prototypeName;
	/**
	 * the canonical path of the file with the DSL source code
	 */
	private String fileNameDSLSourceCode;
}
