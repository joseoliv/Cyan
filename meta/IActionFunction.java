package meta;

/**
 * This interface should be implemented by every metaobject used
 * by other metaobjects as a function.
 * <br>
 * Other metaobjects can call method 'eval'.
 * But first the metaobject of this class has to be found. It may
 * be so through a call to the method<br>
 * <code>
 *      CyanMetaobject searchActionFunction(String name)
 * </code>
 * of interfaces <code>ICompiler_parsing</code>, <code>ICompiler_afterResTypes</code>,
 * and <code>ICompiler_semAn</code>. The semantics of method 'eval'
 * is application specific.
   @author jose
 */
public interface IActionFunction {

	/**
	 * user-defined semantics
	 */
	Object eval(Object input);

	default String getPackageOfType() {
		return null;
	}

	default String getPrototypeOfType() {
		return null;
	}


}
