package meta;

import java.util.List;
import ast.Expr;
import ast.StatementLocalVariableDec;
import ast.Type;

public class WrStatementLocalVariableDec extends WrStatement implements IDeclarationWritable, IVariableDecInterface {

    public WrStatementLocalVariableDec(StatementLocalVariableDec hidden) {
        super(hidden);
    }



    @Override
	StatementLocalVariableDec getHidden() { return (StatementLocalVariableDec ) hidden; }

    @Override
    public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
		WrExpr expr = getExpr();
		if ( expr != null ) {
			expr.accept(visitor, env);
		}
	}

    public WrExpr getExpr() {
    	Expr e = ((StatementLocalVariableDec ) hidden).getExpr();
    	if ( e != null ) {
    		return e.getI();
    	}
		return null;
	}



	@Override
    public String getName() {
        return ((StatementLocalVariableDec ) hidden).getName();
    }

    @Override
    public AttachedDeclarationKind getKind(WrEnv env) {
        return ((StatementLocalVariableDec ) hidden).getKind();
    }

    /**
     * This method can only be called during parsing. Otherwise,
     * there is no need for checks because the checking has been done before

     */
    @Override
    public void addDocumentText(String doc, String docKind, WrEnv env) {
    	checkAdditionInfo(env);

        ((StatementLocalVariableDec ) hidden).addDocumentText(doc, docKind);
    }

    /**
     * This method can only be called during parsing. Otherwise,
     * there is no need for checks because the checking has been done before

     */
    @Override
    public void addFeature(Tuple2<String, WrExprAnyLiteral> feature, WrEnv env) {
    	checkAdditionInfo(env);

        ((StatementLocalVariableDec ) hidden).addFeature(feature);
    }

    /**
     * This method can only be called during parsing. Otherwise,
     * there is no need for checks because the checking has been done before

     */
    @Override
    public void addDocumentExample(String example, String exampleKind, WrEnv env) {
    	checkAdditionInfo(env);

        ((StatementLocalVariableDec ) hidden).addDocumentExample(example, exampleKind);
    }



	/**
	   @param env
	 */
	private static void checkAdditionInfo(WrEnv env) {
		CompilationStep step = env.getCompilationStep();
    	if ( step != CompilationStep.step_1 &&
       		 step != CompilationStep.step_4 &&
       		 step != CompilationStep.step_7  &&
          	step != CompilationStep.step_9 ) {
    		throw new MetaSecurityException();
    	}
	}

    @Override
    public List<Tuple2<String, String>> getDocumentTextList(WrEnv env) {
    	checkGetInfo(env);
        return ((StatementLocalVariableDec ) hidden).getDocumentTextList();
    }

    @Override
    public List<Tuple2<String, String>> getDocumentExampleList(WrEnv env) {
    	checkGetInfo(env);
        return ((StatementLocalVariableDec ) hidden).getDocumentExampleList();
    }


    @Override
    public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList(WrEnv env) {
    	checkGetInfo(env);
        return ((StatementLocalVariableDec ) hidden).getFeatureList();
    }

    @Override
    public List<WrExprAnyLiteral> searchFeature(String name, WrEnv env) {
    	checkGetInfo(env);
        return ((StatementLocalVariableDec ) hidden).searchFeature(name);
    }


	@Override
	public WrType getType() {
		Type t = ((StatementLocalVariableDec ) hidden).getType();
		return t == null ? null : t.getI();
	}

}

