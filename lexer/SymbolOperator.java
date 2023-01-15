/**
 *
 */
package lexer;

import ast.CompilationUnitSuper;
import meta.WrSymbolOperator;
import meta.Token;

/** This class represents a user defined operator, a normal operator, or
 * a sequence of special symbols delimiting a literal object or metaobject
 * such as
 *       (#    used for arrays
 *       [.    used for tuples
 *       ++    Cyan operator
 *       <<+   start of a user defined literal object
 *
 * @author José
 *
 */
public class SymbolOperator extends Symbol {

	/**
	 * @param token
	 * @param symbolString
	 * @param lineNumber
	 * @param columnNumber
	 */
	public SymbolOperator(Token token, String symbolString,
			int startLine, int lineNumber, int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
	}


	@Override
	public WrSymbolOperator getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolOperator(this);
		}
		return (WrSymbolOperator ) isymbol;
	}



}
