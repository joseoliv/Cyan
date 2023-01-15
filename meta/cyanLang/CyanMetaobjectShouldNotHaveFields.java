package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_semAn;
import meta.WrPrototype;

public class CyanMetaobjectShouldNotHaveFields extends CyanMetaobjectAtAnnot implements ICheckDeclaration_afterSemAn {

	public CyanMetaobjectShouldNotHaveFields() {
		super("shouldNotHaveFields", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind [] { AttachedDeclarationKind.PROTOTYPE_DEC} );
	}

	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {
		WrPrototype pu = (WrPrototype ) this.getAttachedDeclaration();
		if ( pu.getFieldList(compiler.getEnv()).size() != 0 ) {
			if ( pu.getFullName().equals("cyan.lang.Any") ) {
				this.addError("Prototype Any cannot have fields because that would cause "
				+ "an infinite recursion in method 'asString'");

			}
			else {
				this.addError("This prototype should not have fields");
			}
		}
	}

}
