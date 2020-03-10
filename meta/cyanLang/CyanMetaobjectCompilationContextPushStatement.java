package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.MetaHelper;

/**
 * See documentation on {@link meta#CyanMetaobjectCompilationContextPush}. 
 * When the code produced is a statement, use this class instead of  {@link meta#CyanMetaobjectCompilationContextPush}.
   @author jose
 */
public class CyanMetaobjectCompilationContextPushStatement extends CyanMetaobjectCompilationContextPush {

	public CyanMetaobjectCompilationContextPushStatement() {
		super(MetaHelper.pushCompilationContextStatementName, AnnotationArgumentsKind.OneOrMoreParameters);
	}
}
