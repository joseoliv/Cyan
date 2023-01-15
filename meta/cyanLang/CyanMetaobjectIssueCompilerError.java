package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICompilerAction_parsing;
import meta.WrAnnotationAt;

/**
 * This metaobject takes a string parameter and issues a compiler error with that string.
   @author jose
 */
public class CyanMetaobjectIssueCompilerError extends CyanMetaobjectAtAnnot
   implements meta.IAction_parsing {

	public CyanMetaobjectIssueCompilerError() {
		super("error", AnnotationArgumentsKind.OneParameter,
				null );
	}

	@Override
	public void check() {
		final WrAnnotationAt annot = (WrAnnotationAt ) this.annotation;
		if ( !(annot.getJavaParameterList().get(0) instanceof String) ) {
			addError("A string is expected as the parameter of this metaobject annotation");
		}
	}


	@Override
	public StringBuffer parsing_codeToAdd(ICompilerAction_parsing compiler) {
		final WrAnnotationAt annot = (WrAnnotationAt ) this.annotation;
		final String msg = (String ) annot.getJavaParameterList().get(0);
		compiler.error(this.annotation.getFirstSymbol().getLineNumber(), msg);
		return null;
	}

}
