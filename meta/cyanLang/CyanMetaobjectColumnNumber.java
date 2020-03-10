package meta.cyanLang;


import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpa;
import meta.ICompilerAction_dpa;
import meta.WrAnnotationAt;

/**
 * The annotation of this metaobject produces the column number of symbol <code>{@literal @}</code> of
 * the annotation.
 *
   @author José
 */
public class CyanMetaobjectColumnNumber extends CyanMetaobjectAtAnnot
       implements IAction_dpa {

	public CyanMetaobjectColumnNumber() {
		super("columnNumber", AnnotationArgumentsKind.ZeroParameters);
	}



	@Override
	public StringBuffer dpa_codeToAdd( ICompilerAction_dpa compiler ) {
		final WrAnnotationAt cyanMetaobjectAnnotation = this.getMetaobjectAnnotation();

		return new StringBuffer("\"" +  "" +
		cyanMetaobjectAnnotation.getSymbolMetaobjectAnnotation().getColumnNumber() + "\"");

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
			return "Int";
	}

	@Override
	public boolean isExpression() {
		return true;
	}
}

