package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.WrStatementLocalVariableDecList;
import saci.CyanEnv;
import saci.Env;


/**
 * represents a list of declarations of local variables, as in
 * fun m {
 *    var Int a, b, c;
 *    ...
 * }
 * @author José
 *
 */

public class StatementLocalVariableDecList extends Statement // implements IDeclaration
{

	public StatementLocalVariableDecList(Symbol firstSymbol) {
		super();
		this.firstSymbol = firstSymbol;
		localVariableDecList = new ArrayList<StatementLocalVariableDec>();
	}

	@Override
	public WrStatementLocalVariableDecList getI() {

		if ( iStatementLocalVariableDec == null ) {
			iStatementLocalVariableDec = new WrStatementLocalVariableDecList(this);
		}
		return iStatementLocalVariableDec;
	}

	WrStatementLocalVariableDecList iStatementLocalVariableDec = null;

	@Override
	public void accept(ASTVisitor visitor) {
		for ( StatementLocalVariableDec s : this.localVariableDecList ) {
			s.accept(visitor);
		}
		visitor.visit(this);
	}



	@Override
	public Object eval(EvalEnv ee) {
		for ( StatementLocalVariableDec stat : this.localVariableDecList ) {
			stat.eval(ee);
		}
		return null;
	}

	public void add(StatementLocalVariableDec localVariableDec) {
		localVariableDecList.add(localVariableDec);
	}

	public void setLocalVariableDecList(List<StatementLocalVariableDec> localVariableDecList) {
		this.localVariableDecList = localVariableDecList;
	}

	public List<StatementLocalVariableDec> getLocalVariableDecList() {
		return localVariableDecList;
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		if ( beforeMetaobjectAnnotation != null ) {
			beforeMetaobjectAnnotation.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		}
		if ( localVariableDecList.get(0).isReadonly() )
			pw.print("let ");
		else
			pw.print("var ");
		int size = this.localVariableDecList.size();
		for ( StatementLocalVariableDec v : localVariableDecList ) {
			v.genCyan(pw, false, cyanEnv, genFunctions);
			if ( --size > 0 )
				pw.print(", ");
		}
	}


	@Override
	public void genJava(PWInterface pw, Env env) {
		for ( StatementLocalVariableDec v : localVariableDecList )
			v.genJava(pw, env);
	}


	@Override
	public Symbol getFirstSymbol() {
		if ( beforeMetaobjectAnnotation != null ) {
			return beforeMetaobjectAnnotation.getFirstSymbol();
		}
		else
			return firstSymbol;
	}


	@Override
	public void calcInternalTypes(Env env) {

		if ( beforeMetaobjectAnnotation != null ) {
			beforeMetaobjectAnnotation.calcInternalTypes(env);

		}
		for( StatementLocalVariableDec statementLocalVariableDec : localVariableDecList )
			statementLocalVariableDec.calcInternalTypes(env);
		super.calcInternalTypes(env);

	}

	public AnnotationAt getBeforeMetaobjectAnnotation() {
		return beforeMetaobjectAnnotation;
	}




	private List<StatementLocalVariableDec> localVariableDecList;


	public void setBeforeMetaobjectAnnotation(AnnotationAt beforeMetaobjectAnnotation) {
		this.beforeMetaobjectAnnotation = beforeMetaobjectAnnotation;
	}

	/**
	 * metaobject annotation that precedes this local variable declaration
	 */
	private AnnotationAt beforeMetaobjectAnnotation;




	private Symbol firstSymbol;

}
