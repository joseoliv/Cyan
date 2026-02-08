/**

 */

package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_semAn;

/**
 * This metaobject checks the style of its attached declaration. If the
 * declaration is a program, all the packages are checked. If it is a package,
 * all of its prototypes are checked. If it is a prototype, all of its methods
 * are checked. If it is a method or field, the name is checked. The checking is
 * rather arbitrary, just for explaining the idea. Change it at your will.<br>
 *
 * @author José
 *
 */
public class CyanMetaobjectCheckEmpty

		extends CyanMetaobjectAtAnnot implements ICheckDeclaration_afterSemAn {

	public CyanMetaobjectCheckEmpty() {
		super("checkEmpty", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.METHOD_DEC,
						AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.FIELD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC });
	}

	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler_semAn) {
	}

}
