package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_semAn;
import meta.WrAnnotationAt;
import meta.WrFieldDec;

public class CyanMetaobjectJavaPublic extends CyanMetaobjectAtAnnot implements ICheckDeclaration_afterSemAn {

	public CyanMetaobjectJavaPublic() {
		super("javaPublic", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.FIELD_DEC } );
	}



	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {
		final WrAnnotationAt annotation = (WrAnnotationAt  ) this.annotation;
		final WrFieldDec iv = (WrFieldDec ) annotation.getDeclaration();
		iv.setJavaPublic(true);
		if ( ! annotation.getPackageOfAnnotation().startsWith("cyan.") ) {
			addError("Metaobject '" + getName() + "' can only be used in package 'cyan.lang'");
		}
	}




}
