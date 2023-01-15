package lexer;
/**
 * This are the phase of the compiler as seen by the metaobject protocol
 *
   @author José
 */
public enum CompilerPhase {
	PARSING("parsing"),
	AFTER_RES_TYPES("afterResTypes"),
	SEM_AN("semAn"),
	AFTER_SEM_AN("afterSemAn"),
	CGE("cge");

	private CompilerPhase(String name) {
		this.name = name;
	}

	public static String phaseNames = "";
	static {
		int size = CompilerPhase.values().length;
		for ( CompilerPhase cp : CompilerPhase.values() ) {
			phaseNames += "'" + cp.getName() + "'";
			if ( --size > 0 )
				phaseNames += ", ";
		}
	}

	public static CompilerPhase search(String phaseName) {
		for ( CompilerPhase cp : CompilerPhase.values() ) {
			if ( cp.getName().equals(phaseName) )
				return cp;
		}
		return null;
	}
	public String getName() {
		return name;
	}

	public boolean lessThan(CompilerPhase other) {
		return this.ordinal() < other.ordinal();
	}

	private String name;
}
