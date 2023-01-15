package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICompiler_semAn;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrFieldDec;
import meta.WrMethodDec;

public class CyanMetaobjectReadOnly extends CyanMetaobjectAtAnnot implements meta.ICheckDeclaration_afterSemAn {

	public CyanMetaobjectReadOnly() {
		super("readOnly", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC });
	}

	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {
		final WrAnnotationAt annot = this.getAnnotation();
		final WrMethodDec method = (WrMethodDec ) annot.getDeclaration();
		final WrEnv env = compiler.getEnv();
		if ( method.someNonSharedFieldAssignedTo(compiler.getEnv()) ||
				method.someSharedFieldAssignedTo(compiler.getEnv()) ) {
			int size = method.getAssignedToFieldList(env).size();
			String strList = "";
			for ( final WrFieldDec iv : method.getAssignedToFieldList(env) ) {
				strList += iv.getName();
				if ( --size > 0 ) {
					strList += ", ";
				}
			}
			this.addError(method.getFirstSymbol(env), "Annotation '@" + this.getName() +
					"' is attached to this method. The metaobject "
					+ "associated to it demands that no shared or non-shared field (field) be assigned to. " +
					"However, the following fields receive values in assignments: " + strList);
		}


	}

}
