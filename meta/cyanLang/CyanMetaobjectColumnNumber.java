package meta.cyanLang;


import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_parsing;
import meta.ICompilerAction_parsing;
import meta.WrAnnotationAt;

/**
 * The annotation of this metaobject produces the column number of symbol <code>{@literal @}</code> of
 * the annotation.
 *
   @author José
 */
public class CyanMetaobjectColumnNumber extends CyanMetaobjectAtAnnot
       implements IAction_parsing {

	public CyanMetaobjectColumnNumber() {
		super("columnNumber", AnnotationArgumentsKind.ZeroParameters);
	}



	@Override
	public StringBuffer parsing_codeToAdd( ICompilerAction_parsing compiler ) {
		final WrAnnotationAt cyanAnnotation = this.getAnnotation();

		return new StringBuffer("\"" +  "" +
		cyanAnnotation.getSymbolAnnotation().getColumnNumber() + "\"");

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

