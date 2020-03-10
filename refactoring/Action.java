/**
 *
 */
package refactoring;

import java.io.PrintWriter;

/** An action to be made on a source file such as inserting a ';',
 *  declaring a variable, etc.
 *
 * @author José
 *
 */
abstract public class Action implements Comparable<Action> {

	@Override
	public int compareTo(Action other) {

		if ( posInFile < other.posInFile )
			return -1;
		else if ( posInFile > other.posInFile)
			return 1;
		else
			return 0;
	}

	abstract public void print(PrintWriter printWriter);

	abstract public void doIt();
	
	/*
	 * add "offset" to the position in the file in which the action will 
	 * be taken. This is necessary when there are two insertions, for
	 * example. The first one inserts ';' at position 100. The second
	 * one inserts ';' at position 200 (which was the original index
	 * of the file). But because of the first insertion, the second should
	 * be made at position 201. 
	 * 
	 */
	public void addOffset(int offset) {
		posInFile += offset;
	}
	/**
	 * the number of characters inserted (positive number) or deleted (negative number)
	 * in the file by these action
	 */
	public abstract int getNumberCharsInserted();
	

	protected int posInFile;

}