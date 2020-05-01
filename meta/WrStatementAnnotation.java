package meta;

import ast.AnnotationAt;
import ast.StatementAnnotation;

public class WrStatementAnnotation extends WrStatement {

	public WrStatementAnnotation(StatementAnnotation hidden) {
		super(hidden);
	}

	@Override
	StatementAnnotation getHidden() { return (StatementAnnotation ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
		this.getAnnotation().accept(visitor, env);
	}

	private WrAnnotationAt getAnnotation() {
		AnnotationAt at = ((StatementAnnotation ) hidden).getAnnotation();
		return at == null ? null : at.getI();
	}

}
