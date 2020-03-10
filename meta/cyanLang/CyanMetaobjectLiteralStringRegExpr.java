package meta.cyanLang;

import meta.CyanMetaobjectLiteralString;
import meta.ICompiler_dsa;
import meta.WrAnnotationAt;

/**
 * literal string that is a regular expression.
 *
 * Future work: compile the regular expression only once. See
 * https://stackoverflow.com/questions/19829892/java-regular-expressions-performance-and-alternative
 *
   @author jose
 */

public class CyanMetaobjectLiteralStringRegExpr extends CyanMetaobjectLiteralString {

	public CyanMetaobjectLiteralStringRegExpr() {
		super(new String[] { "r", "R" });
	}

	public void dpa_parse(String code) {

		try {
			java.util.regex.Pattern.compile(code);
		}
		catch (java.util.regex.PatternSyntaxException e) {
			addError("Pattern is not well defined");
		}
		codeToGenerate = new StringBuffer( "RegExpr(\"" + code + "\")");
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		String code = new String(((WrAnnotationAt ) this.metaobjectAnnotation).getTextAttachedDSL());
		dpa_parse(code);

		return this.codeToGenerate;
	}

	@Override
	public String getPackageOfType() {
		return "cyan.lang";
	}
	@Override
	public String getPrototypeOfType() {
		return "RegExpr";
	}

	/**
	 * code to be generated
	 */
	private StringBuffer codeToGenerate;
}
