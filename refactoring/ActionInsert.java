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
public class ActionInsert extends ActionInFile {

	/**
	 * insert toBeInserted in position posInFile of the compilationUnit
	 * @param toBeInserted
	 * @param posInFile
	 */
	public ActionInsert(String toBeInserted,
			            CompilationUnitSuper compilationUnit,
			            int posInFile,
			            int line,
			            int column ) {
		super(compilationUnit, posInFile, line, column);
		this.toBeInserted = toBeInserted;
		this.posInFile = posInFile;
	}

	@Override
	public void print(PrintWriter printWriter) {
		printWriter.println("Insert '" + toBeInserted + "' in line " +
			line + " column " + column + " of file " +
			compilationUnit.getFullFileNamePath() );
	}

	/* (non-Javadoc)
	 * @see refactoring.Action#doIt(saci.CompilationUnit)
	 */
	@Override
	public void doIt() {
		int i;
		int size = toBeInserted.length();
		char []oldText = compilationUnit.getText();
		int newSize = oldText.length + size;
		char []newText = new char[newSize];
		for(i = 0; i < posInFile; i++)
			newText[i] = oldText[i];
		int k = i;
		for(int j = 0; j < toBeInserted.length(); j++) {
			newText[i] = toBeInserted.charAt(j);
			i++;
		}
		while ( i < newSize ) {
			newText[i] = oldText[k];
			i++;
			k++;
		}
		compilationUnit.setText(newText);
	}

	@Override
	public int getNumberCharsInserted() {
		// TODO Auto-generated method stub
		return toBeInserted.length();
	}
	
	private String toBeInserted;


}
