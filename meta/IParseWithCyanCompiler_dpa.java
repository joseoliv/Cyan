package meta;


/**
 * this interface contains method interfaces to parse literal objects  
 * such as <code>{* "one":1, "two":2 *}</code> and <code>@graph{* 1:2, 2:3 *}</code>. The parsing can be made 
 * with the help of the Cyan compiler but the code to be parsed is limited. By "limited" we mean 
 * that the Cyan compiler will only supply to the methods of this interface tokens that are inside the 
 * metaobject annotation. For example, in <code>{* "one":1, "two":2 *}</code> the Cyan compiler will only
 * supply tokens that are between <code>{*</code> and <code>*}</code>.   </p>
 * 
   @author José
 */
public interface IParseWithCyanCompiler_dpa extends IParse_dpa  {
	/**
	 * parse the code inside the literal object.   
	 * 
	   @return  <code>false</code> if there was compilation errors
	 */
	void dpa_parse(ICompiler_dpa compiler_dpa);

}
