package meta;

import java.util.List;
import ast.Expr;
import ast.FieldDec;
import ast.ProgramUnit;

public class WrFieldDec extends WrSlotDec
    implements IDeclarationWritable, IVariableDecInterface, ISlotInterface {

    public WrFieldDec(FieldDec hiddenFieldDec) {
        this.hidden = hiddenFieldDec;
    }
    FieldDec hidden;


	@Override
	FieldDec getHidden() {
		return hidden;
	}

    public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
    	if ( env.getCompilationStep().ordinal() > CompilationStep.step_5.ordinal() ) {
    		Expr e = hidden.getExpr();
    		if ( e != null ) {
    			e.getI().accept(visitor, env);
    		}
    	}

    }

    @Override
    public String getName() {
        return hidden.getName();
    }

	@Override
	public String getNameAsInSourceCode() {
		return hidden.getNameAsInSourceCode();
	}


    @Override
    public AttachedDeclarationKind getKind(WrEnv env) {
        return hidden.getKind();
    }

    @Override
    public void addDocumentText(String doc, String docKind, WrEnv env) {
    	CompilationStep step = env.getCompilationStep();
    	if ( env.getCurrentProgramUnit().hidden != hidden.getDeclaringObject() ||
    			(step != CompilationStep.step_1 &&
          		 step != CompilationStep.step_4 &&
          		 step != CompilationStep.step_7 )
    			) {
    		throw new MetaSecurityException();
    	}
        hidden.addDocumentText(doc, docKind);
    }

    @Override
    public void addDocumentExample(String example, String exampleKind, WrEnv env) {
    	CompilationStep step = env.getCompilationStep();
    	if ( env.getCurrentProgramUnit().hidden != hidden.getDeclaringObject() ||
    			(step != CompilationStep.step_1 &&
          		 step != CompilationStep.step_4 &&
          		 step != CompilationStep.step_7 )
       		) {
    		throw new MetaSecurityException();
    	}

        hidden.addDocumentExample(example, exampleKind);
    }

    @Override
    public void addFeature(Tuple2<String, WrExprAnyLiteral> feature, WrEnv env) {
    	CompilationStep step = env.getCompilationStep();
    	if ( env.getCurrentProgramUnit().hidden != hidden.getDeclaringObject() ||
          		 (step != CompilationStep.step_1 &&
           		 step != CompilationStep.step_4 &&
           		 step != CompilationStep.step_7 )
           		) {
    		throw new MetaSecurityException();
    	}
        hidden.addFeature(feature);
    }

    @Override
    public List<Tuple2<String, String>> getDocumentTextList(WrEnv env) {
        return hidden.getDocumentTextList();
    }

    @Override
    public List<Tuple2<String, String>> getDocumentExampleList(WrEnv env) {
        return hidden.getDocumentExampleList();
    }


    @Override
    public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList(WrEnv env) {
        return hidden.getFeatureList();
    }

    @Override
    public List<WrExprAnyLiteral> searchFeature(String name, WrEnv env) {
        return hidden.searchFeature(name);
    }

	public boolean isShared() {
		return hidden.isShared();
	}

	public WrProgramUnit getDeclaringObject() {
		ProgramUnit pu = hidden.getDeclaringObject();
		return pu == null ? null : pu.getI();
	}

	public boolean isReadonly() {
		return hidden.isReadonly();
	}

	public void setJavaPublic(boolean b) {
		hidden.setJavaPublic(b);
	}

	public WrSymbol getFirstSymbol(WrEnv env) {
    	if ( env.getCurrentProgramUnit().hidden != hidden.getDeclaringObject()) {
    		throw new MetaSecurityException();
    	}

		return hidden.getFirstSymbol().getI();
	}


	@Override
	public WrType getType() {
		return hidden.getType().getI();
	}


	public boolean createdByMetaobjects() {
		return hidden.createdByMetaobjects();
	}

}


