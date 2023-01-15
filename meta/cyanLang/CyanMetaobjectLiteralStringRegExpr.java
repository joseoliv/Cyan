package meta.cyanLang;

import meta.CyanMetaobjectLiteralString;
import meta.ICompiler_semAn;

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

	public void parsing_parse(String code) {

		try {
			java.util.regex.Pattern.compile(code);
		}
		catch (java.util.regex.PatternSyntaxException e) {
			addError("Pattern is not well defined");
		}
		codeToGenerate = new StringBuffer( "RegExpr(\"" + code + "\")");
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		parsing_parse(this.getUsefulString());

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
