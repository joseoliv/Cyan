package meta;

/**
 * This interface should be implemented by all metaobjects that should add code at phase dpa.
 *
   @author José
 */
public interface IAction_dpa {


	/**
	 * Return Cyan code to be added after the metaobject annotation during parsing (phase dpa).
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
	   {@literal @}foo#dpa<br>
	   i print;<br>
	   </code>	  <br>
	 */
	@SuppressWarnings("unused")
	default StringBuffer dpa_codeToAdd(ICompilerAction_dpa compiler) {
		return null;
	}


}
