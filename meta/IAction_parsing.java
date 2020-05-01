package meta;

/**
 * This interface should be implemented by all metaobjects that should add code at phase parsing.
 *
   @author José
 */
public interface IAction_parsing {


	/**
	 * Return Cyan code to be added after the metaobject annotation during parsing (phase 'parsing').
	 * If metaobject <code>foo</code> produces <code>i print;</code> (this method returns
	 * <code>"i print;"</code> )
	 * then the code<br>
	   <code>
	   i = 1;<br>
	   {@literal @}foo<br>
	   </code><br>
	   will be replaced by<br>
	   <code><br>
	   i = 1;<br>
	   {@literal @}foo#parsing<br>
	   i print;<br>
	   </code>	  <br>
	 */
	@SuppressWarnings("unused")
	default StringBuffer parsing_codeToAdd(ICompilerAction_parsing compiler) {
		return null;
	}


}
