package meta;

/**
 * This interface should be implemented by all metaobject classes whose metaobjects should add code at phase SEM_AN.
   @author José
 */
public interface IAction_semAn {
	/**
	 * Return Cyan code to be added after the metaobject annotation during semantic analysis.
	 * It should return null if used only for checks.
		If
	 * metaobject <code>foo</code> produces <code>i print;</code> (this method returns
	 * <code>"i print;"</code> in the second tuple element)
	 * then the code
	   <code>
	   @foo
	   i = 1;
	   </code>
	   </p>
	   will be replaced by
	   </p>
	   <code>
	   @foo#semAn
	   i print;
	   i = 1;
	   </code>
	   </p>

	   @param compiler_semAn the compiler
	 */
	StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn);

}
