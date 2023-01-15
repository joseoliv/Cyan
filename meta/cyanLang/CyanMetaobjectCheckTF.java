/**

 */
package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;

/**
 * Checks whether the method t:f: of Boolean has the correct parameters. Not implemented yet. Probably it will be removed.
   @author José

 */
public class CyanMetaobjectCheckTF extends CyanMetaobjectAtAnnot {

	public CyanMetaobjectCheckTF() {
		super("checkTF", AnnotationArgumentsKind.ZeroParameters, new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC });
	}




}
