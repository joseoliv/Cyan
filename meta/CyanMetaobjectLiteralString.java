package meta;

/**
 * This metaobject represents strings that are preceded by  a single letter such as
 *    <code>r"[A-Za-z_][A-Za-z_0-9]*"</code>
   @author José
 */
public class CyanMetaobjectLiteralString extends CyanMetaobjectLiteralObject {

	public CyanMetaobjectLiteralString(String []prefixNameList) {
		this.prefixNameList = prefixNameList;
	}

	public CyanMetaobjectLiteralString myClone() {
		try {
			return (CyanMetaobjectLiteralString ) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}
	/**
	 * return the letters or symbols that may start a string of a metaobject annotation
	 * of this metaobject. For example, getName would return
	 *      new String[] { "r" }
	 * in a metaobject that represent regular expressions such as
	 *     r"[A-Za-z_][A-Za-z_0-9]*"
	   @return
	 */
	final public String []getPrefixNames() { return this.prefixNameList; }

	/**
	 * if the string can have the prefixes "r" and "R" the name is "string(r, R)".
	 */

	@Override
	public String getName() {
		String name = "string(";
		String []prefixes = this.getPrefixNames();
		int size = prefixes.length;
		for( String s : prefixes ) {
			name += s;
			if ( --size > 0 )
				name += ", ";
		}
		return name + ")";
	}

	/**
	 * the prefixes that may appear before the first " of the string
	 */
	private String []prefixNameList;
}
