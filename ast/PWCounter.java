/**
 *
 */
package ast;

import saci.NameServer;


public class PWCounter implements PWInterface {


	public PWCounter() {
		this.numChars = 0;
		this.numLines = 0;
	}

	
	static public StringBuffer toStringBuffer( GenCyan gc ) {
		PWCharArray pw = new PWCharArray();
		gc.genCyan(pw, false, NameServer.cyanEnv, true);
		return pw.getGeneratedString();
	}

	static public int getNumChars( GenCyan gc ) {
		PWCounter pw = new PWCounter();
		gc.genCyan(pw, false, NameServer.cyanEnv, true);
		return pw.getNumChars();
	}

	/**
	 * return true if the statements or expression gc, when the code is generated,
	 * fits in one line. It would return true for
	 *      [  Out println: "Olá" ]
	 *  and false for
	 *      	 (a == 0) ifTrue: [
                 	   Out writeln: "a is equal to zero";
		               a = In readInt;
		               [^ a == 0 ] whileTrue: [
		                     a = In readInt;
		               ]
                  ]

	 * @param gc
	 * @return
	 */
	static public boolean printInMoreThanOneLine( GenCyan gc ) {
		return getNumChars(gc) > MaxCharInOneLine;
	}

	@Override
	public void println() {
		numLines++;
		numChars++;
	}

	@Override
	public void print(char[] s) {
		numChars += s.length;
		for ( char ch : s )
			if ( ch == '\n' )
				numLines++;
	}

	@Override
	public void println(char[] s) {
		print(s);
		numLines++;
		numChars++;
	}

	@Override
	public void print(String s) {
		numChars += s.length();
		for (int i = 0; i < s.length(); i++)
			if ( s.charAt(i) == '\n' )
				numLines++;
	}

	@Override
	public void println(String s) {
		print(s);
		numLines++;
		numChars++;
	}

	@Override
	public void printIdent(String s) {
		//numChars += currentIndent;
		print(s);
	}

	@Override
	public void printlnIdent(String s) {
		//numChars += currentIndent;
		println(s);
	}

	@Override
	public void print(StringBuffer s) {
		numChars += s.length();
		for (int i = 0; i < s.length(); i++)
			if ( s.charAt(i) == '\n' )
				numLines++;
	}

	@Override
	public void println(StringBuffer s) {
		print(s);
		numLines++;
		numChars++;
	}

	@Override
	public void printIdent(StringBuffer s) {
		print(s);
	}

	@Override
	public void printlnIdent(StringBuffer s) {
		println(s);
	}
	
	public int getNumLines() {
		return numLines;
	}

	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}

	public void setNumChars(int numChars) {
		this.numChars = numChars;
	}

	public int getNumChars() {
		return numChars;
	}

	@Override
	public void add() {
		currentIndent += step;
	}

	@Override
	public void sub() {
		currentIndent -= step;
	}

	@Override
	public void set(int indent) {
		currentIndent = indent;
	}

	@Override
	public int getCurrentIndent() {
		return currentIndent;
	}
	

	int currentIndent = 0;
	private int step = 4;
	private int numChars;
	private int numLines;

	// number of characters in one line;
	final static public int MaxCharInOneLine = 20;
}
