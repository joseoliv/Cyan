package meta;

/**
 * All metaobject classes that implement this interface are codegs. The class should
 * be sub-prototype of {@link meta#CyanMetaobjectAtAnnot}.  They can implement interfaces
 * {@link meta#IAction_parsing}, {@link meta#IAction_afterResTypes}, and {@link meta#IAction_semAn}.
 * The compiler checks whether the metaobject annotation has at least one parameter.
   @author José
 */
public interface ICodeg {
	/**
	 * return the extension of the file that keeps information on the codeg
	   @return
	 */
	default String getFileInfoExtension() { return "txt"; }
	/**
	 * method that is called when the user hover the mouse pointer over a metaobject annotation in a Cyan source file.
	 * The metaobject should, of course, implements this interface. Method {@link getUserInput()}
	 * should convert any user input made through the keyboard, mouse, etc into a byte array that is returned by this method.
	 * This text will be written by the compiler into a file. Usually this text will be a source code of a Domain Specific Language.
	 * But in simple codegs it may be just the code that will be added to the Cyan source code after
	 * the metaobject annotation. Then Codeg <code>color</code> may just return the color number in the appropriate format.
	 * It could be <code>"44545"</code> in the format of a byte array, for example.
	 *
	 * @param previousCodegFileText is the byte array of the file that keeps the codeg annotation info.
	 * If this method has been previously called
	 * the value returned by this method was stored in a file. This value is now passed as parameter to
	 * this method. If this method has never been called, null is passed as parameter.
	 *
	 */
	byte []getUserInput(ICompiler_ded compiler_ded, byte []previousCodegFileText);

	/**
	 * true if this Codeg demands a label. The default is true. If it does not, the Codeg should always
	 * be replaced by a text produced at editing time.
	 */
	default boolean demandsLabel() { return true; }
	/**
	 * the text that should replace the Codeg annotation, including the attached DSL
	 */
	default String newCodegAnnotation() { return null; }

	/**
	 * return the new first parameter of the Codeg or null if it is the same as before
	 */
	default String getNewFirstParameter() { return null; }

	int getStartOffset();
	int getEndOffset();

}
