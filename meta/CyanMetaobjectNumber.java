package meta;

/**
 * This metaobject represents a literal number that ends with letters.
 * That is, something that starts with a digit but ends with letters.
   @author José
 */
public class CyanMetaobjectNumber extends CyanMetaobjectLiteralObject {


	public CyanMetaobjectNumber myClone() {
		try {
			return (CyanMetaobjectNumber ) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public CyanMetaobjectNumber(String []suffixNameList) {
		super();
		this.suffixNameList = suffixNameList;
	}
	/**
	 * return the names that may end the number. For example, a metaobject for binary
	 * numbers of the kind 0101bin or 011Bin should return an array containing
	 * "bin" and "Bin". The name is found by starting at the end of the number and
	 * collecting all letters till a underscore or number is found. So, the name
	 * of  0A5CF_Hex is "Hex".
	   @return
	 */
	public final String []getSuffixNameList() { return suffixNameList; }

	/**
	 * if the number can have the suffixes "bin" and "Bin" the name is "number(bin, Bin)".
	 */
	@Override
	public String getName() {
		String name = "number(";
		String []suffixes = this.getSuffixNameList();
		int size = suffixes.length;
		for( String s : suffixes ) {
			name += s;
			if ( --size > 0 )
				name += ", ";
		}
		return name + ")";
	}

	public int getStartOffset() {
		return this.getAnnotation().getFirstSymbol().getOffset();
	}

	public int getEndOffset() {
		WrSymbol sym = this.getAnnotation().getLastSymbol();
		return sym.getOffset() + sym.getSymbolString().length() - 1 ;
	}
	/**
	 * the suffixes that should appear after the number
	 */
	private String []suffixNameList;

}
