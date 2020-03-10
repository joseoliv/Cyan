package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afsa;
import meta.ICompiler_dsa;
import meta.WrProgramUnit;

public class CyanMetaobjectShouldNotHaveFields extends CyanMetaobjectAtAnnot implements ICheckDeclaration_afsa {

	public CyanMetaobjectShouldNotHaveFields() {
		super("shouldNotHaveFields", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind [] { AttachedDeclarationKind.PROTOTYPE_DEC} );
	}

	@Override
	public void afsa_checkDeclaration(ICompiler_dsa compiler) {
		WrProgramUnit pu = (WrProgramUnit ) this.getAttachedDeclaration();
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
