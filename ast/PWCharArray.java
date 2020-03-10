package ast;

/**
 *  PWCharArray is used to output generated code to a StringBuffer
 */

public class PWCharArray implements PWInterface {

	public PWCharArray() {
		set();
	}

	public PWCharArray(StringBuffer out) {
		this.out = out;
	}


	public void set() {
		out = new StringBuffer();
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
	public void println() {
		out.append("\n");
	}

	@Override
	public void print(char[] s) {
		out.append(s);
	}

	@Override
	public void println(char[] s) {
		out.append(s);
		out.append("\n");
	}

	@Override
	public void print(String s) {
		out.append(s);
	}

	@Override
	public void println(String s) {
		out.append(s + "\n");
	}

	@Override
	public void printIdent(String s) {
		print(space.substring(0, currentIndent));
		print(s);
	}

	@Override
	public void printlnIdent(String s) {
		print(space.substring(0, currentIndent));
		println(s);
	}

	@Override
	public void print(StringBuffer s) {
		out.append(s);
	}

	@Override
	public void println(StringBuffer s) {
		out.append(s + "\n");
	}

	@Override
	public void printIdent(StringBuffer s) {
		print(space.substring(0, currentIndent));
		print(s);
	}

	@Override
	public void printlnIdent(StringBuffer s) {
		print(space.substring(0, currentIndent));
		println(s);
	}


	public StringBuffer getGeneratedString() {
		return out;
	}
	int currentIndent = 0;

	private int step = 4;
	private StringBuffer out;

	static final private String space = "                                                                                                        ";

	@Override
	public int getCurrentIndent() {
		return currentIndent;
	}

	public StringBuffer getOut() {
		return out;
	}


}
