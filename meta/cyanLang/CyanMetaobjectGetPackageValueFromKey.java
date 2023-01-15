package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_semAn;
import meta.ICompiler_semAn;
import meta.MetaHelper;

public class CyanMetaobjectGetPackageValueFromKey extends CyanMetaobjectAtAnnot
				implements IAction_semAn {

	public CyanMetaobjectGetPackageValueFromKey() {
		super("getPackageValueFromKey", AnnotationArgumentsKind.OneParameter);
	}

	@Override
	public void check() {
		Object p = this.getAnnotation().getJavaParameterList().get(0);
		if ( !(p instanceof String) ) {
			this.addError("The parameter to this annotation should be an identifier or literal string");
		}
		else {
			this.variableName = CyanMetaobject.removeQuotes((String ) p);
		}
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		Object value = compiler_semAn.getEnv().getCurrentCompilationUnit().getCyanPackage().getPackageValueFromKey(variableName);
		if ( value == null ) {
			return new StringBuffer( "\"\"" );
		}
		else if ( value instanceof cyan.lang._Any ) {
			return new StringBuffer( "\"" + CyanMetaobject.removeQuotes(((cyan.lang._Any) value)._asString().s) + "\"");
		}
		else {
			return new StringBuffer( "\"" + CyanMetaobject.removeQuotes(value.toString()) + "\"");
		}
	}

	@Override
	public boolean isExpression() {
		return true;
	}



	@Override
	public String getPackageOfType() { return MetaHelper.cyanLanguagePackageName; }

	@Override
	public String getPrototypeOfType() { return "String"; }

	private String variableName;
}

