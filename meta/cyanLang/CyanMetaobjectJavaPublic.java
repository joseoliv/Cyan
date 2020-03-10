package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afsa;
import meta.ICompiler_dsa;
import meta.WrAnnotationAt;
import meta.WrFieldDec;

public class CyanMetaobjectJavaPublic extends CyanMetaobjectAtAnnot implements ICheckDeclaration_afsa {

	public CyanMetaobjectJavaPublic() {
		super("javaPublic", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.FIELD_DEC } );
	}



	@Override
	public void afsa_checkDeclaration(ICompiler_dsa compiler) {
		final WrAnnotationAt annotation = (WrAnnotationAt  ) this.metaobjectAnnotation;
		final WrFieldDec iv = (WrFieldDec ) annotation.getDeclaration();
		iv.setJavaPublic(true);
		if ( ! annotation.getPackageOfAnnotation().startsWith("cyan.") ) {
			addError("Metaobject '" + getName() + "' can only be used in package 'cyan.lang'");
		}
	}




}
