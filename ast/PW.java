/**
 *
 */
package ast;

/** Used to generate code using a PrintWriter object.
 *
 * @author José
 *
 */
import java.io.PrintWriter;

public class PW implements PWInterface {

	@Override
	public void add() {
		currentIndent += step;
		if ( currentIndent > sizeSpace ) {
			//System.out.println("Error in PW: current index is " + currentIndent);
			currentIndent = 0;
		}
	}

	@Override
	public void sub() {
		currentIndent -= step;
		if ( currentIndent < 0 ) {
			//System.out.println("Error in PW: current index is " + currentIndent);
			currentIndent = 0;
		}
			
	}

	public void set(PrintWriter out) {
		this.out = out;
		currentIndent = 0;
	}

	@Override
	public void set(int indent) {
		currentIndent = indent;
	}

	@SuppressWarnings("static-method")
	final void check(String ss) {
		if ( ss == null )
			System.out.println("contains null");
		
	}
	
	@Override
	public void println() {
		out.println();
	}

	@Override
	public void print(char[] s) {
		int i = 0;
		while ( i < s.length && s[i] != '\0' ) {
			out.print(s[i]);
			++i;
		}
	}

	@Override
	public void println(char[] s) {
		print(s);
		out.println("");
		//check(new String(s));
	}

	@Override
	public void print(String s) {
		out.print(s);
		//check(s);
	}

	@Override
	public void println(String s) {
		out.println(s);
		//check(s);
	}

	@Override
	public void printIdent(String s) {
		out.print(space.substring(0, currentIndent));
		out.print(s);
		//check(s);
	}

	@Override
	public void printlnIdent(String s) {
		out.print(space.substring(0, currentIndent));
		out.println(s);
		//check(s);
	}
	
	
	@Override
	public void print(StringBuffer s) {
		out.println(s);
	}

	@Override
	public void println(StringBuffer s) {
		out.println(s);
	}

	@Override
	public void printIdent(StringBuffer s) {
		out.print(space.substring(0, currentIndent));
		out.print(s);
	}

	@Override
	public void printlnIdent(StringBuffer s) {
		out.print(space.substring(0, currentIndent));
		out.println(s);
	}
	

	public PrintWriter getPrintWriter() {
		return out;
	}

	@Override
	public int getCurrentIndent() {
		return currentIndent;
	}
	
	
	int currentIndent = 0;

	private int step = 4;

	private PrintWriter out;

	static final private String space = "                                                                                                                                                                                                                                                                                                                                                                                                    ";
	static final private int sizeSpace = space.length();
}
