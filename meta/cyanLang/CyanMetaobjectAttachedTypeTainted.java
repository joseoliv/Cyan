package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAttachedType_semAn;

public class CyanMetaobjectAttachedTypeTainted extends CyanMetaobjectAtAnnot implements IActionAttachedType_semAn {

	public CyanMetaobjectAttachedTypeTainted() {
		super("tainted", AnnotationArgumentsKind.OneParameter, new AttachedDeclarationKind[] { AttachedDeclarationKind.TYPE });
	}


	@Override public void check() {
		List<Object> paramList = this.getAnnotation().getJavaParameterList();
		if ( !(paramList.get(0) instanceof String ) ) {
			this.addError("The sole parameter should be a string or identifier");
		}
	}


}
