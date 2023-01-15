package meta;


/**
 * this interface contains methods to parse macros.  The methods
 * of this interface can retrieve any number of tokens, using class {@link ICompilerMacro_parsing}, from 
 * the start of the macro call onwards. This method decides when to stop retrieving tokens through
 * method <code>next</code>. This is necessary because there is no token or keyword that ends a macro call. 
 * Only the macro grammar can decide when the call ended. And the macro grammar is implemented by
 * the methods of this interface. 
 *  
 * </p> Other compiler interfaces such as {@link IParseWithCyanCompiler_parsing} are
 * very different in this aspect. They only allow one to retrieve tokens inside a literal object.
 * </p>
 * 
   @author José
 */
public interface IParseMacro_parsing extends IParse_parsing {
	/**
	 * parse the macro call. Using the methods of <code>compiler_parsing</code> this method may parse the macro call
	 * and built an AST for the macro. The AST classes for this macro call are not those of Cyan. These are 
	 * specific to this macro. Of course, the AST classes of the Cyan compiler can also be used. Method
	 *  {@link #semAn_codeToAdd(ICompiler_semAn)} can use this AST together with semantic information
	 *  (e.g. the type of a local variable) to generate the Cyan code corresponding to the macro call. 
	 * 
	   @param compiler_parsing, the compiler as seen by the macro metaobject
	 */
	void parsing_parseMacro(ICompilerMacro_parsing compiler_parsing);

}
