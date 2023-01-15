package meta;

import java.util.List;

public class WrLocalVarInfo {

	public WrLocalVarInfo(LocalVarInfo hidden) {
		this.hidden = hidden;
	}
	LocalVarInfo hidden;

	/**
	 * the list of literals used for initializing this local variable.
	 * The elements may be objects of Integer, Boolean, Byte, etc.
	 */

	public List<Object> getLiteralList() {
		return hidden.literalList;
	}

	/**
	 * true if the variable has been initialized up to the current lexical point
	 */
	public boolean getInitialized() {
		return hidden.initialized;
	}
	/**
	 * true if the variable has been initialized with an expression that
	 * is not a literal up to the current lexical point
	 */
	public boolean getInitializedWithNonLiteral() {
		return hidden.initializedWithNonLiteral;
	}

	public WrStatementLocalVariableDec getLocalVar() {
		return hidden.localVar.getI();
	}

	public int getLexicalLevel() {
		return hidden.lexicalLevel;
	}

}
