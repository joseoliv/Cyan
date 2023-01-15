/**
 * 
 */
package saci;

/** keeps the Compiler options. Each option is true or false and is 
 * represented by a public variable. Each source file may have a
 * different set of Compiler options.
 * 
 * @author José
 *
 */
public class CompilerOptions {
	
	/**
	 * true if the Compiler should emit only error messages with a single line
	 */
	public boolean singleLineErrors = false;
	/**
	 * true if the compilation should stop in the first error found.
	 */
	public boolean stopFirstError = false;
	/**
	 * true if the Compiler should not ask any questions to the user. Sometimes the
	 * Compiler can ask a questions such as "variable i is not declared. Should I
	 * declare is as int?" 
	 */
	public boolean noUserQuestion = false;
	
	public void print() {
		System.out.println( "        Compiler options: " + 
				            (singleLineErrors ? "-sle" : "") + 
				            (stopFirstError  ? "-sfe" : "") +
				            (noUserQuestion  ? "-nuq" : "")
				           );
	}
	/**
	 * set the Compiler options. An option starting with "-" means that the 
	 * variables representing the option is set true (with "+", false).
	 * 
	 * @param option, the options to be set
	 */
	public boolean setCompilerOptions(String options) {
		
		if ( options.length() == 0 )
			return true;
		String []optionArray = options.split(" ");
		for (String op : optionArray ) {
			if ( op.equals("-sle") )
				singleLineErrors = true;
			else if ( op.equals("+sle") )
				singleLineErrors = false;
			else if (op.equals("-sfe") )
				stopFirstError = true;
			else if (op.equals("+sfe") )
				stopFirstError = false;
			else if ( op.equals("-nuq") )
				noUserQuestion = true;
			else if ( op.equals("+nuq") )
				noUserQuestion = false;
			else
				return false;
		}
		return true;
	}

}
