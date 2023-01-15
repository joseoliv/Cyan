package meta.util;

import meta.CyanMetaobjectNumber;
import meta.ICompiler_semAn;

public class CyanMetaobjectKg  extends CyanMetaobjectNumber {


	public CyanMetaobjectKg() {
		super(new String[] { "kg", "KG", "Kg" });
	}

//
//	@Override
//	public void parsing_parse(ICompilerAction_parsing compilerAction, String code) {
//
//		String s = "";
//		int size = code.length();
//		for (int i = 0; i < size; ++i) {
//			char ch = code.charAt(i);
//			if ( Character.isDigit(ch) )
//				s += ch;
//			else if ( ch != '_' ) {
//				if ( !code.substring(i).equalsIgnoreCase("kg") ) {
//					addError("Wrong format for 'kg'");
//					return ;
//				}
//				break;
//			}
//		}
//		// // setInfo(new StringBuffer(s));
//		this.codeToGenerate = new StringBuffer(s);
//	}


	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {


		String code = this.getUsefulString();
		String s = "";
		int size = code.length();
		for (int i = 0; i < size; ++i) {
			char ch = code.charAt(i);
			if ( Character.isDigit(ch) )
				s += ch;
			else if ( ch != '_' ) {
				if ( !code.substring(i).equalsIgnoreCase("kg") ) {
					addError("Wrong format for 'kg'");
					return null;
				}
				break;
			}
		}
		// // setInfo(new StringBuffer(s));
		this.codeToGenerate = new StringBuffer(s);

		return this.codeToGenerate;
	}


	@Override
	public String getPackageOfType() {
		return "cyan.lang";
	}
	@Override
	public String getPrototypeOfType() {
		return "Int";
	}

	private StringBuffer codeToGenerate;

}
