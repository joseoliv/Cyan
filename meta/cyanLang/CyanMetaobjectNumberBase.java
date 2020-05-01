package meta.cyanLang;

import meta.CyanMetaobjectNumber;
import meta.ICompiler_semAn;

public class CyanMetaobjectNumberBase extends CyanMetaobjectNumber {


	public CyanMetaobjectNumberBase() {
		super(new String[] { "base", "BASE", "Base" });
	}

	public void parsing_parse(String code) {

		int len = code.length();
		int i = len - 1;
		while ( Character.isDigit(code.charAt(i)) )
			--i;
		if ( i == len ) {
            addError("After 'base' there should appear the number of the base");
			return ;
		}
		int base = 10;
		try {
		    base = Integer.valueOf(code.substring(i+1));
		}
		catch (  NumberFormatException e ) {
			addError("Base was not recognized");
			return ;
		}
		if ( base < 2 || base > 36 ) {
			addError("Base should be between 2 and 36 (included)");
			return ;
		}
		while ( i > 0 && code.charAt(i) == '_' ) { --i; }
		while ( Character.isLetter(code.charAt(i)) )
			--i;
		number = 0;
//		code = code.substring(0, i+1);
//		if ( ! code.endsWith("base") && ! code.endsWith("Base") && ! code.endsWith("BASE")  ) {
//			addError("Wrong format in number literal ending with 'base', 'Base' or 'BASE'");
//			return ;
//		}
//		i = i - 4;
		String numberStr = code.substring(0,  i + 1);
		String numberWithoutUnderscore = "";
		for (int k = 0; k < numberStr.length(); ++k) {
			char ch = numberStr.charAt(k);
			if ( ch != '_' )
			    numberWithoutUnderscore = numberWithoutUnderscore + numberStr.charAt(k);
		}
		try {
			number = Integer.valueOf(numberWithoutUnderscore, base);
		}
		catch (  NumberFormatException e ) {
			addError("Number is not in base'" + base + "' or number is too big");
			return ;

		}
		// // setInfo(new StringBuffer("" + n));
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		// return (StringBuffer ) getInfo();
		//String code = new String(((WrAnnotationAt ) this.metaobjectAnnotation).getTextAttachedDSL());

		parsing_parse(this.getUsefulString());
		return new StringBuffer("" + number);
	}


	@Override
	public String getPackageOfType() {
		return "cyan.lang";
	}
	@Override
	public String getPrototypeOfType() {
		return "Int";
	}

	/**
	 * the number!
	 */
	private int number;
}
