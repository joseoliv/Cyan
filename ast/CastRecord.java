package ast;

import meta.WrCastRecord;

/**
 * represents each variable declaration in a cast statement. For example,
 * <code>
 *     cast T v = e { ... }
 * </code>
 * T is typeInDec, v is localVar, and e is expr
   @author jose
 */
public class CastRecord implements ASTNode {
	public CastRecord(Expr typeInDec, StatementLocalVariableDec localVar,
			Expr expr) {
		super();
		this.typeInDec = typeInDec;
		this.localVar = localVar;
		this.expr = expr;
		this.isUnionExpr = false;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public WrCastRecord getI() {
		if ( this.iCastRecord == null ) {
			this.iCastRecord = new WrCastRecord(this);
		}
		return this.iCastRecord;
	}

	private WrCastRecord iCastRecord = null;

	public Expr getTypeInDec() {
		return typeInDec;
	}

	public StatementLocalVariableDec getLocalVar() {
		return localVar;
	}

	public Expr getExpr() {
		return expr;
	}
	/**
	 * expression of the kind T|Nil or Nil|T such as e1 and e2 in<br>
	 * <code>
	 * cast b = e1, c = e2 { ... } <br>
	 * </code>
	 */
	public Expr typeInDec;
	public StatementLocalVariableDec localVar;

	/**
	 * the variable b, c in the example above.
	 */
	public Expr expr;

	/**
	 * true if the type of expr is not an union
	 */
	public boolean isUnionExpr;


}
