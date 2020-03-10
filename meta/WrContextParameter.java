package meta;

import ast.ContextParameter;
import ast.Expr;

public class WrContextParameter extends WrFieldDec {

	public WrContextParameter(ContextParameter hidden) {
		super(hidden);
	}

	@Override
	ContextParameter getHidden() {
		return (ContextParameter ) hidden;
	}

    @Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
    	if ( env.getCompilationStep().ordinal() > CompilationStep.step_5.ordinal() ) {
    		Expr e = hidden.getExpr();
    		if ( e != null ) {
    			e.getI().accept(visitor, env);
    		}
    	}

    }


}
