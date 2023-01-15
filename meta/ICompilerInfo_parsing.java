/**

 */
package meta;

/**
 * interface with methods to allow information to flow from the metaobjects to a
 * declaration such as a field, method, or prototype. The overridden method
 * action_parsing should cast the attached declaration to IDeclarationWritable and
 * then use methods of this interface to add features, documentation, and
 * examples to the declaration.
   @author jose

 */
public interface ICompilerInfo_parsing {
	void action_parsing(ICompilerAction_parsing compiler_parsing);
}
