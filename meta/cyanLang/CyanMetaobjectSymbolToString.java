package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpa;
import meta.ICompilerAction_dpa;
import meta.WrAnnotationAt;
import meta.MetaHelper;

public class CyanMetaobjectSymbolToString extends CyanMetaobjectAtAnnot implements IAction_dpa {


	public CyanMetaobjectSymbolToString() {
		super("symbolToString", AnnotationArgumentsKind.OneParameter);
	}


	@Override
	public void check() {
		if ( !(((WrAnnotationAt ) metaobjectAnnotation).getJavaParameterList().get(0) instanceof String) ) {
			addError("Parameter to this metaobject " + getName() + " should be an identifier or a literal string");
		}
	}


	@Override
	public StringBuffer dpa_codeToAdd(ICompilerAction_dpa compiler) {
		String s = (String ) ((WrAnnotationAt ) metaobjectAnnotation).getJavaParameterList().get(0);
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
