package meta;

import ast.AnnotationAt;
import ast.StatementMetaobjectAnnotation;

public class WrStatementMetaobjectAnnotation extends WrStatement {

	public WrStatementMetaobjectAnnotation(StatementMetaobjectAnnotation hidden) {
		super(hidden);
	}

	@Override
	StatementMetaobjectAnnotation getHidden() { return (StatementMetaobjectAnnotation ) hidden; }

	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
		this.getAnnotation().accept(visitor, env);
	}

	private WrAnnotationAt getAnnotation() {
		AnnotationAt at = ((StatementMetaobjectAnnotation ) hidden).getMetaobjectAnnotation();
		return at == null ? null : at.getI();
	}

}
