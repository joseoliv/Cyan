package ast;

import saci.CyanEnv;

/**
 * This class gives the visibility of a slot. 
 * 
 * @author José
 *
 */
public class SlotVisibility {
	
	public SlotVisibility() {
		isPrivate = isPublic = isProtected = false;
	}
	
	
	public void genCyan(PWInterface pw, CyanEnv cyanEnv) {
		if ( isPrivate )
			pw.print("private");
		else if ( isPublic )
			pw.print("public");
		else if ( isProtected )
			pw.print("protected");
		pw.print(" ");
	}
	
	
	public static final int publicVisibility = 1, 
	                        privateVisibility = 2, 
	                        protectedVisibility = 4;
	/**
	 * if isPrivate is true, no ( were used after the keyword private, as in
	 *      private proc search -> boolean [ ... ]
	 *  or
	 *      private :n int
	 */
	public boolean isPrivate, isPublic, isProtected;

}
