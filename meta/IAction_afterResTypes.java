
package meta;

import java.util.List;

/**
 * This interface should be implemented by all metaobjects that should add code at phase AFTER_RES_TYPES and that should
 * be attached to a program unit or inside a program unit.
   @author José
 */
public interface IAction_afterResTypes {

	/**
<p>This method should return code to be added to the prototype in which the metaobject annotation is.  The first tuple element should be the code to be added to the prototype and the second tuple element should be  the interfaces of the fields and methods to be added.</p>
<p>The interface of a method is its signature and the interface of a field is its own declaration but without any initialization (no <code>= expr</code> after it). A <code>;</code> should be put after each field/method interface. For example,<br>
a returned tuple could be, using Cyan syntax and literal strings instead of StringBuffer, the following.</p>
<pre><code>    [. &quot;&quot;&quot;var Int count = 0;
            func at: Int n with: String s -&gt; String {
                map at: n put: s;
            }
            shared let String str;
            func unary { ++count; }
          &quot;&quot;&quot;,
          &quot;&quot;&quot;
            var Int count;
            func at: Int with: String s -&gt; String;
            shared let String str;
            func unary;
          &quot;&quot;&quot; .]
</code></pre>
	 */

	@SuppressWarnings("unused")
	default Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
			List<ISlotSignature>>> infoList) { return null; }

	/**
	 * return true if the metaobject depend on fields and methods added by other metaobjects.
	 * The methods of this metaobject and all metaobjects of the same prototype
	 * whose method runUntilFixedPoint() return true will be called iteratively in a series
	 * of steps. This algorithm will only stop when all metaobjects return in the current
	 * step the same code as in the last step. At most five steps will be used.
	   @return
	 */
	default boolean runUntilFixedPoint() { return false; }


	/**
	 * list of statements to add to specific methods of the current prototype. Each tuple is composed of
	 * a method name, code of statements, and a boolean value. The statements should
	 * be added to all methods with that name in the prototype. The name means "keywords and number of parameters".
	 * Then a name could be <code>with:2 do:1</code>.<br>
	 *
	 * If the last tuple element is true, the metaobject is demanding exclusivity in the addition of
	 * code to every method. If two metaobjects try to add code to the beginning of the same
	 * method, the compiler issues an error
	 *
	 * @param compiler
	 */
	default List<Tuple3<String, StringBuffer, Boolean>> afterResTypes_beforeMethodCodeList(
			ICompiler_afterResTypes compiler) { return null; }

	// 	boolean addBeforeMethod(CyanMetaobjectAtAnnot cyanMetaobject, String packageName, String prototypeName, String methodName, StringBuffer statementCode);



	/**
	 * this method should return a list of tuples. Each tuple is composed of
	 * the old method name and an array with the keywords of the new method name.
	 * The method corresponding to the first tuple element is renamed according
	 * to the second tuple element.
	 * Of course, the number of the keywords should be the same.
	 * @param compiler_afterResTypes
	 */
	default List<Tuple2<String, String []>> afterResTypes_renameMethod(
			ICompiler_afterResTypes compiler_afterResTypes) { return null; }

}