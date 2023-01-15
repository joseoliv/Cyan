/**
 *
 */
package ast;

import lexer.Symbol;
import meta.WrStatementAnnotation;
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
public class StatementAnnotation extends Statement {

	public StatementAnnotation(AnnotationAt annotation, MethodDec method) {
		super(method);
		this.annotation = annotation;
	}

	@Override
	public WrStatementAnnotation getI() {
		if ( iStatementAnnotation == null ) {
			iStatementAnnotation = new WrStatementAnnotation(this);
		}
		return iStatementAnnotation;
	}

	private WrStatementAnnotation iStatementAnnotation = null;

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



	public void setAnnotation(AnnotationAt annotation) {
		this.annotation = annotation;
	}

	public AnnotationAt getAnnotation() {
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
