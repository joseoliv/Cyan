package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAttachedType_dsa;

public class CyanMetaobjectAttachedTypeTainted extends CyanMetaobjectAtAnnot implements IActionAttachedType_dsa {

	public CyanMetaobjectAttachedTypeTainted() {
		super("tainted", AnnotationArgumentsKind.OneParameter);
	}


	@Override public void check() {
		List<Object> paramList = this.getMetaobjectAnnotation().getJavaParameterList();
		if ( !(paramList.get(0) instanceof String ) ) {
			this.addError("The sole parameter should be a string or identifier");
		}
	}


}
