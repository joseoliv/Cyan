package meta.cyanLang;

import java.awt.Frame;
import meta.CyanMetaobjectNumber;
import meta.ICodeg;
import meta.ICompiler_ded;
import meta.ICompiler_semAn;

public class CyanMetaobjectNumberBinary extends CyanMetaobjectNumber implements ICodeg {

	public CyanMetaobjectNumberBinary() {
		super(new String[] { "bin", "Bin", "BIN" });
	}

	public void parsing_parse(String code) {

		number = 0;
		String numberStr = code.substring(0,  code.length() - 3).replace("_", "");
		try {
			number = Integer.valueOf(numberStr, 2);
		}
		catch (  NumberFormatException e ) {
			addError("Number is not binary");
			return ;
		}
		// // this.setInfo(new StringBuffer("" + n));
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		// // return (StringBuffer ) getInfo();
		String code = this.getUsefulString();
		parsing_parse(code);
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
	private int numberChoser;


	@Override
	public byte[] getUserInput(ICompiler_ded compiler_ded, byte[] previousCodegFileText) {
		numberChoser = 0;
		CodegNumberBinaryGUI codegGUI = new CodegNumberBinaryGUI(new Frame(), previousCodegFileText, null);
		byte[] userInput = codegGUI.getUserInput();
		numberChoser = Integer.parseInt(new String(userInput));

		codegGUI = null;
		return null;
	}


	@Override
	public boolean demandsLabel() {
		return false;
	}

	/**
	 * the text that should replace the Codeg annotation, including the attached DSL
	 */
	@Override
	public String newCodegAnnotation() {
		return numberChoser + "bin";
	}

}

