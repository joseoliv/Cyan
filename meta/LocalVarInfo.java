package meta;

import java.util.List;
import ast.StatementLocalVariableDec;

/**
 * information on a local variable related to a lexical level. The
 * information stored in an object of this class is only valid
 * at that lexical level.
   @author jose
 */
public class LocalVarInfo implements Cloneable {

	@Override
	public LocalVarInfo clone() {
		LocalVarInfo newObj = new LocalVarInfo(localVar, lexicalLevel);
		newObj.literalList = this.literalList;
		newObj.initialized = this.initialized;
		newObj.initializedWithNonLiteral = this.initializedWithNonLiteral;
		newObj.breakIsLastStatement = this.breakIsLastStatement;
		return newObj;
	}

	public LocalVarInfo(StatementLocalVariableDec localVar, int lexicalLevel) {
		this.localVar = localVar;
		this.lexicalLevel = lexicalLevel;
		literalList = null;
		this.initialized = false;
		this.initializedWithNonLiteral = false;
		this.breakIsLastStatement = false;
	}

	public WrLocalVarInfo getI() {
		return new WrLocalVarInfo(this);
	}

	/**
	 * the list of literals used for initializing this local variable.
	 * The elements may be objects of Integer, Boolean, Byte, etc.
	 */
	public List<Object> literalList;

	/**
	 * true if the variable has been initialized up to the current lexical point
	 */
	public boolean initialized = false;
	/**
	 * true if the variable has been initialized with an expression that
	 * is not a literal up to the current lexical point
	 */
	public boolean initializedWithNonLiteral;
	public StatementLocalVariableDec localVar;
	public int lexicalLevel;

	public boolean breakIsLastStatement;

}
