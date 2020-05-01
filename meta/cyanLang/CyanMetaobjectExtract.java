package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_semAn;
import meta.ICompiler_semAn;
import meta.MetaHelper;
import meta.WrAnnotationAt;

/**
 * this metaobject should be used as<br>
 * <code>
 *      {@literal @}extract(int100) <br>
 *      {@literal @}extract(int_500) <br>
 * </code>
 * These values are transformed in 100 and 500 by extract. Generic prototypes
 * may pretend they accept Int numbers as parameters:<br>
 * <code>MyList{@literal<String, int_100>}<br>
 * </code>
 *
   @author jose
 */
public class CyanMetaobjectExtract extends CyanMetaobjectAtAnnot implements IAction_semAn {

	public CyanMetaobjectExtract() {
		super("extract", AnnotationArgumentsKind.OneParameter);
	}


	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn)  {
		final WrAnnotationAt annotation = this.getAnnotation();
		final Object first = annotation.getJavaParameterList().get(0);
		boolean error = false;
		if ( ! (first instanceof String) ) {
			error = true;
		}
		Integer n = -1;
		final String s = MetaHelper.removeQuotes((String ) first);
		if ( s.charAt(0) == '\"' || s.charAt(s.length()-1) == '\"' || ! s.startsWith(strInt) || s.length() < sizeInt + 1 ) {
			error = true;
		}
		else {
			String strNum;
			if ( s.charAt(sizeInt) == '_' ) {
				strNum = s.substring(sizeInt + 1);
			}
			else {
				strNum = s.substring(sizeInt);
			}
			try {
				n = Integer.valueOf(strNum);
			}
			catch ( final NumberFormatException e ) {
				error = true;
			}
		}
		if ( error ) {
			this.addError("The first parameter to this metaobject annotation should be an identifier in the format '" + strInt +
					"x' or '" + strInt + "_x' in which 'x' is a number");
			return null;
		}
		else {
			return new StringBuffer("" + n);
		}
	}

	private final static String strInt = "int";
	private final static int sizeInt = strInt.length();
	@Override
	public boolean isExpression() {
		return true;
	}



	@Override
	public String getPackageOfType() { return MetaHelper.cyanLanguagePackageName; }
	/**
	 * If the metaobject annotation has type <code>packageName.prototypeName</code>, this method returns
	 * <code>prototypeName</code>.  See {@link CyanMetaobjectLiteralObject#getPackageOfType()}
	   @return
	 */

	@Override
	public String getPrototypeOfType() { return "Int"; }
}
