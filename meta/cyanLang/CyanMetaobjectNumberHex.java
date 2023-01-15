package meta.cyanLang;

import meta.CyanMetaobjectNumber;
import meta.ICompiler_semAn;

public class CyanMetaobjectNumberHex extends CyanMetaobjectNumber {

	public CyanMetaobjectNumberHex() {
		super(new String[] { "hex", "Hex", "HEX" });
	}


	public void parsing_parse(String code) {

		number = 0;
		String numberStr = code.substring(0,  code.length() - 3);
		while ( numberStr.endsWith("_") ) {
			numberStr = numberStr.substring(0,  numberStr.length()-1);
		}
		try {
			number = Integer.valueOf(numberStr.replace("_", ""), 16);
		}
		catch (  NumberFormatException e ) {
			addError("Number is not in hexadecimal");
			return ;
		}
		// // this.setInfo(new StringBuffer("" + n));
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		parsing_parse(this.getUsefulString());
		// // return (StringBuffer ) getInfo();
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

