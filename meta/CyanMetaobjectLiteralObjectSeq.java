package meta;

/**
 * represents a user-defined literal object delimited by a sequence of symbols such as
 *       [% (1, 2), (2, 3), (3, 1) %]
 *
   @author José
 */
public class CyanMetaobjectLiteralObjectSeq extends CyanMetaobjectLiteralObject {


	public CyanMetaobjectLiteralObjectSeq(String leftCharSequence) {
		this.leftCharSequence = leftCharSequence;
	}

	public CyanMetaobjectLiteralObjectSeq myClone() {
		try {
			return (CyanMetaobjectLiteralObjectSeq ) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * return the left sequence of characters that should start this literal object. In the
	 * example  [| "one" -> 1, "two" -> 2 |],  this method should return "[|".
	   @return
	 */
	final public String leftCharSequence() { return leftCharSequence; }

	/**
	 * the name is the left char sequence that start the metaobject, a literal object
	 */
	@Override
	public String getName() {
		return this.leftCharSequence();
	}



	private String leftCharSequence;

}
