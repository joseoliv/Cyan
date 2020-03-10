/**
 *
 */
package ast;

import lexer.Symbol;
import meta.WrStatementMetaobjectAnnotation;
import saci.CyanEnv;
import saci.Env;

/** Represents a metaobject annotation where a statement is expected. Example:
 *         c = @@color(blue);
 *         @javacode<<
 *              System.out.println("This is a Java code");
 *         >>
 *         ...
 *
 * Here, @@color and @javacode are metaobject annotations used where a statement
 * is expected. In general these metaobjects will produce Cyan or Java code.
 *
 * @author José
 *
 */
public class StatementMetaobjectAnnotation extends Statement {

	public StatementMetaobjectAnnotation(AnnotationAt annotation) {
		super();
		this.annotation = annotation;
	}

	@Override
	public WrStatementMetaobjectAnnotation getI() {
		if ( iStatementMetaobjectAnnotation == null ) {
			iStatementMetaobjectAnnotation = new WrStatementMetaobjectAnnotation(this);
		}
		return iStatementMetaobjectAnnotation;
	}

	private WrStatementMetaobjectAnnotation iStatementMetaobjectAnnotation = null;

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		this.annotation.accept(visitor);
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		annotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}

	/* (non-Javadoc)
	 * @see ast.Statement#getFirstSymbol()
	 */
	@Override
	public Symbol getFirstSymbol() {
		return annotation.getFirstSymbol();
	}


	/* (non-Javadoc)
	 * @see ast.Statement#genJava(ast.PWInterface, saci.Env)
	 */
	@Override
	public void genJava(PWInterface pw, Env env) {
		annotation.genJava(pw, env);
	}



	public void setMetaobjectAnnotation(AnnotationAt annotation) {
		this.annotation = annotation;
	}

	public AnnotationAt getMetaobjectAnnotation() {
		return annotation;
	}

	@Override
	public void calcInternalTypes(Env env) {

		annotation.calcInternalTypes(env);
		super.calcInternalTypes(env);
	}


	@Override
	public boolean demandSemicolon() { return false; }
	/**
	 * metaobject annotation of this statement
	 * @checkStyle object Proto
	 *    ...
	 * end
	 */
	private AnnotationAt annotation;



}
