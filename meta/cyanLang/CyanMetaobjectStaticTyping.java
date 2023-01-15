package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IStaticTyping;

/**
 * See comments on {@link meta#IStaticTyping}
   @author José
 */
public class CyanMetaobjectStaticTyping extends CyanMetaobjectAtAnnot implements IStaticTyping {

	public CyanMetaobjectStaticTyping() {
		super("staticTyping", AnnotationArgumentsKind.ZeroParameters);
	}

}
