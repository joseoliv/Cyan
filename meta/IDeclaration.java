/**

 */
package meta;

import java.util.List;

/** represents a declaration which may be a prototype declaration, a method, or a field.
 *
   @author José

 */
public interface IDeclaration  {


	String getName();
	AttachedDeclarationKind getKind(WrEnv env);

	/**
	 * return the texts for documenting this declaration, each tuple
	 * composed of a doc and a docKind
	 */
	List<Tuple2<String, String>> getDocumentTextList(WrEnv env);

	/**
	 * return the examples for documenting this declaration, each tuple
	 * composed of an example and an exampleKind
	 */
	List<Tuple2<String, String>> getDocumentExampleList(WrEnv env);



	List<Tuple2<String, WrExprAnyLiteral>> getFeatureList(WrEnv env);

	List<WrExprAnyLiteral> searchFeature(String name, WrEnv env);

}
