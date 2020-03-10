/**
 *
 */
package refactoring;

import java.io.PrintWriter;
import ast.CompilationUnitSuper;



/** insert a symbol in a certain position of the file corresponding to the
 *  compilation unit.
 * @author José
 *
 */
public class ActionDelete extends ActionInFile {

	/**
	 * insert toBeInserted in position posInFile of the compilationUnit
	 * @param toBeInserted
	 * @param posInFile
	 */
	public ActionDelete(CompilationUnitSuper compilationUnit,
			            int posInFile,
			            int numCharsToBeDeleted,
			            int line,
			            int column ) {
		super(compilationUnit, posInFile, line, column);
		this.numCharsToBeDeleted = numCharsToBeDeleted;
		this.posInFile = posInFile;
	}

	@Override
	public void print(PrintWriter printWriter) {
		printWriter.println("Delete '" + this.numCharsToBeDeleted +
				"' characters in line " +
			line + " column " + column + " of file " +
			compilationUnit.getFullFileNamePath() );
	}

	/* (non-Javadoc)
	 * @see refactoring.Action#doIt(saci.CompilationUnit)
	 */
	@Override
	public void doIt() {
		int i;
		char []oldText = compilationUnit.getText();
		int newSize = oldText.length - numCharsToBeDeleted;
		char []newText = new char[newSize];
		for(i = 0; i < posInFile; i++)
			newText[i] = oldText[i];
		int j = i;
		for(i = posInFile + numCharsToBeDeleted; i < oldText.length; i++) {
			newText[j] = oldText[i];
			j++;
		}
		newText[newSize-1] = '\0';
		compilationUnit.setText(newText);
	}

	@Override
	public int getNumberCharsInserted() {
		// TODO Auto-generated method stub
		return -numCharsToBeDeleted;
	}
	
	private int numCharsToBeDeleted;


}
