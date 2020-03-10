package ast;

public interface PWInterface {

	public abstract void add();

	public abstract void sub();

	public abstract void set(int indent);

	public abstract void println();

	public abstract void print(char[] s);

	public abstract void println(char[] s);

	public abstract void print(String s);

	public abstract void println(String s);

	public abstract void printIdent(String s);

	public abstract void printlnIdent(String s);


	public abstract void print(StringBuffer s);

	public abstract void println(StringBuffer s);

	public abstract void printIdent(StringBuffer s);

	public abstract void printlnIdent(StringBuffer s);

	public int getCurrentIndent();

}
