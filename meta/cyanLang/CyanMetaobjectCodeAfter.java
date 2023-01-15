package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionVariableDeclaration_semAn;
import meta.WrAnnotationAt;
import meta.WrEnv;

/**
 * add the code attached to the annotation after a local variable declaration.
   @author jose
 */
public class CyanMetaobjectCodeAfter extends CyanMetaobjectAtAnnot
     implements IActionVariableDeclaration_semAn {

	public CyanMetaobjectCodeAfter() {
		super("codeAfter", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.LOCAL_VAR_DEC } );
	}


	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public

	StringBuffer semAn_codeToAddAfter(WrEnv env) {
		final WrAnnotationAt withAt = this.getAnnotation();

		return new StringBuffer(new String(withAt.getTextAttachedDSL()));
	}


}
