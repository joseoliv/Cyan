/**
 *
 */
package refactoring;

import ast.CompilationUnitSuper;

/** an action that occurs inside a file such as inserting or deleting
 * symbols.
 *
 * @author José
 *
 */
abstract public class ActionInFile extends Action {

	public ActionInFile(CompilationUnitSuper compilationUnit,
            int posInFile,
            int line,
            int column ) {
		this.compilationUnit = compilationUnit;
		this.posInFile = posInFile;
		this.line = line;
		this.column = column;
	}
	
	
	protected CompilationUnitSuper compilationUnit;
    protected int line;
    protected int column;

}
