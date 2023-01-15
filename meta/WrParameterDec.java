package meta;

import java.util.List;
import ast.Expr;
import ast.ExprFunction;
import ast.ParameterDec;
import ast.Prototype;
import ast.Type;

public class WrParameterDec extends WrASTNode implements IDeclarationWritable, IVariableDecInterface {

	public WrParameterDec(ParameterDec hidden) {
		this.hidden = hidden;
	}

	public WrParameterDec(WrSymbolIdent parameterSymbol, WrExpr typeInDec,
			WrMethodDec currentMethod) {
		this( new ParameterDec(parameterSymbol == null ? null : parameterSymbol.hidden, typeInDec, currentMethod) );
	}

	ParameterDec hidden;

	@Override
	ParameterDec getHidden() { return hidden; }

	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}

	@Override
	public String getName() {
		return hidden.getName();
	}

	@Override
	public AttachedDeclarationKind getKind(WrEnv env) {
		return AttachedDeclarationKind.LOCAL_VAR_DEC;
	}

	@Override
	public void addDocumentText(String doc, String docKind, WrEnv env) {
		checkSecurity(env);

		hidden.addDocumentText(doc, docKind);
	}

	@Override
	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature, WrEnv env) {
		checkSecurity(env);

		hidden.addFeature(feature);
	}

	@Override
	public void addDocumentExample(String example, String exampleKind, WrEnv env) {
		checkSecurity(env);

		hidden.addDocumentExample(example, exampleKind);
	}

	/**
	   @param env
	 */
	private void checkSecurity(WrEnv env) {
    	CompilationStep step = env.getCompilationStep();
    	if ( step != CompilationStep.step_1 &&
       		 step != CompilationStep.step_4 &&
       		 step != CompilationStep.step_7  &&
          	step != CompilationStep.step_9 ) {
    		throw new MetaSecurityException();
    	}

		ExprFunction ef = hidden.getDeclaringFunction();
		Prototype pu;
		if ( ef != null ) {
			pu = ef.getCurrentPrototype();
		}
		else {
			pu = hidden.getDeclaringMethod().getDeclaringObject();
		}
    	if ( env.getCurrentPrototype().hidden != pu ) {
    		throw new MetaSecurityException();
    	}
	}


	@Override
	public List<Tuple2<String, String>> getDocumentTextList(WrEnv env) {
    	checkGetInfo(env);
		return hidden.getDocumentTextList();
	}

	@Override
	public List<Tuple2<String, String>> getDocumentExampleList(WrEnv env) {
    	checkGetInfo(env);
		return hidden.getDocumentExampleList();
	}


	@Override
	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList(WrEnv env) {
    	checkGetInfo(env);
		return hidden.getFeatureList();
	}

	@Override
	public List<WrExprAnyLiteral> searchFeature(String name, WrEnv env) {
    	checkGetInfo(env);
		return hidden.searchFeature(name);
	}

	public WrExpr getTypeInDec() {
		Expr e = hidden.getTypeInDec();
		return e == null ? null : e.getI();
	}

	@Override
	public WrType getType() {
		Type t = hidden.getType();
		return t == null ? null : t.getI();
	}

	public void setVariableKind(VariableKind parameterType) {
		hidden.setVariableKind(parameterType);
	}
}
