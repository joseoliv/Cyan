package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICompilerAction_dpa;
import meta.WrAnnotationAt;

/**
 * This metaobject takes a string parameter and issues a compiler error with that string.
   @author jose
 */
public class CyanMetaobjectIssueCompilerError extends CyanMetaobjectAtAnnot
   implements meta.IAction_dpa {

	public CyanMetaobjectIssueCompilerError() {
		super("error", AnnotationArgumentsKind.OneParameter,
				null );
	}

	@Override
	public void check() {
		final WrAnnotationAt annotation = (WrAnnotationAt ) this.metaobjectAnnotation;
		if ( !(annotation.getJavaParameterList().get(0) instanceof String) ) {
			addError("A string is expected as the parameter of this metaobject annotation");
		}
	}


	@Override
	public StringBuffer dpa_codeToAdd(ICompilerAction_dpa compiler) {
		final WrAnnotationAt annotation = (WrAnnotationAt ) this.metaobjectAnnotation;
		final String msg = (String ) annotation.getJavaParameterList().get(0);
		compiler.error(this.metaobjectAnnotation.getFirstSymbol().getLineNumber(), msg);
		return null;
	}

}
