package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_parsing;
import meta.ICompilerAction_parsing;
import meta.WrAnnotationAt;
import meta.MetaHelper;

public class CyanMetaobjectSymbolToString extends CyanMetaobjectAtAnnot implements IAction_parsing {


	public CyanMetaobjectSymbolToString() {
		super("symbolToString", AnnotationArgumentsKind.OneParameter);
	}


	@Override
	public void check() {
		if ( !(getAnnotation().getJavaParameterList().get(0) instanceof String) ) {
			addError("Parameter to this metaobject " + getName() + " should be an identifier or a literal string");
		}
	}


	@Override
	public StringBuffer parsing_codeToAdd(ICompilerAction_parsing compiler) {
		String s = (String ) getAnnotation().getJavaParameterList().get(0);
		s = MetaHelper.removeQuotes(s);
		return new StringBuffer(MetaHelper.addQuotes(s));
	}


	@Override
	public String getPackageOfType() { return "cyan.lang"; }
	/**
	 * If the metaobject annotation has type <code>packageName.prototypeName</code>, this method returns
	 * <code>prototypeName</code>.  See {@link CyanMetaobjectLiteralObject#getPackageOfType()}
	   @return
	 */

	@Override
	public String getPrototypeOfType() {
		return "String";
	}

	@Override
	public boolean isExpression() {
		return true;
	}


}
