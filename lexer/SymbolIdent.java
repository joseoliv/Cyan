/**
 *
 */
package lexer;

import ast.CompilationUnitSuper;
import meta.WrSymbolIdent;
import meta.Token;

/** This class represents an identifier. Inherited field name is the
 * identifier name
 * @author José
 *
 */
public class SymbolIdent extends Symbol {

	/**
	 * @param symbolString
	 * @param lineNumber
	 * @param columnNumber
	 */
	public SymbolIdent(Token token, String symbolString, int startLine,
			 int lineNumber, int columnNumber, int offset, CompilationUnitSuper compilationUnit) {
		super(token, symbolString, startLine, lineNumber, columnNumber, offset, compilationUnit);
	}

	@Override
	public WrSymbolIdent getI() {
		if ( isymbol == null ) {
			isymbol = new WrSymbolIdent(this);
		}
		return (WrSymbolIdent ) isymbol;
	}



}
