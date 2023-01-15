package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.MethodDec;
import ast.MethodSignatureOperator;
import ast.ParameterDec;

public class WrMethodSignatureOperator extends WrMethodSignature {

	public WrMethodSignatureOperator(MethodSignatureOperator hiddenMethodSignature) {
		this.hidden = hiddenMethodSignature;
	}

    public WrMethodSignatureOperator(WrSymbol symbol, WrMethodDec currentMethod) {
		this( new MethodSignatureOperator(symbol.hidden, currentMethod) );
	}

	MethodSignatureOperator hidden;

    @Override
	public void accept(WrASTVisitor visitor, WrEnv env) {

		visitor.preVisit(this, env);
		visitor.visit(this, env);
	}

    @Override
    public String getName() {
        return hidden.getName();
    }

    @Override
    public AttachedDeclarationKind getKind(WrEnv env) {
        return hidden.getKind();
    }

    @Override
    public void addDocumentText(String doc, String docKind, WrEnv env) {
    	securityCheck(env);


        hidden.addDocumentText(doc, docKind);
    }

    @Override
    public void addFeature(Tuple2<String, WrExprAnyLiteral> feature, WrEnv env) {
    	securityCheck(env);

    	hidden.addFeature(feature);
    }

	/**
	   @param env
	 */
	private void securityCheck(WrEnv env) {
		if ( env.getCurrentPrototype().hidden != hidden.getMethod().getDeclaringObject() ) {
    		throw new MetaSecurityException();
    	}

    	CompilationStep step = env.getCompilationStep();
    	if ( step != CompilationStep.step_1 &&
             	 step != CompilationStep.step_4 &&
              	 step != CompilationStep.step_7  &&
              	step != CompilationStep.step_9 ) {
    		throw new MetaSecurityException();
    	}
	}

    @Override
    public void addDocumentExample(String example, String exampleKind, WrEnv env) {
    	securityCheck(env);
        hidden.addDocumentExample(example, exampleKind);
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

    private List<WrParameterDec> iParameterDecList = null;

    @Override
	public List<WrParameterDec> getParameterList() {
    	if ( iParameterDecList == null ) {
    		iParameterDecList = new ArrayList<>();
    		if ( hidden.getParameterList() != null ) {
        		for ( final ParameterDec p : this.hidden.getParameterList() ) {
        			this.iParameterDecList.add(p.getI());
        		}
    		}
    	}
    	return iParameterDecList;
    }
	@Override
	public WrType getReturnType(WrEnv env) {
		ast.Type t = this.hidden.getReturnType(env.hidden);
		return t == null ? null : t.getI();
	}

	@Override
	public WrMethodDec getMethod() {
		MethodDec m = hidden.getMethod();
		return m == null ? null : m.getI();
	}

	@Override
	public String getFullName(WrEnv env) {
		return hidden.getFullName(env.hidden);
	}


	@Override
	public String getFullName() {
		return hidden.getFullName();
	}

	@Override
	public String getFullNameWithReturnType(WrEnv env) {
		return hidden.getFullNameWithReturnType(env.hidden);
	}

	@Override
	public String getFullNameWithReturnType() {
		return hidden.getFullNameWithReturnType();
	}


	@Override
	public String getNameWithDeclaredTypes() {
		return hidden.getNameWithDeclaredTypes();
	}


	@Override
	public void calcInterfaceTypes(WrEnv env) {
		hidden.calcInterfaceTypes(env.hidden);
	}

	@Override
	public String asString() {
		return hidden.asString();
	}


	public WrSymbol getSymbolOperator() {
		return hidden.getSymbolOperator().getI();
	}

	public WrParameterDec getOptionalParameter() {
		ParameterDec p = hidden.getOptionalParameter();
		return p == null ? null : p.getI();
	}

	@Override
	public WrExpr getReturnTypeExpr() {
		Expr e = hidden.getReturnTypeExpr();
		return e == null ? null : e.getI();
	}

	public void setOptionalParameter(WrParameterDec parameterDec) {
		hidden.setOptionalParameter(parameterDec.hidden);
	}

	@Override
	public void setReturnTypeExpr(WrExpr returnType) {
		hidden.setReturnTypeExpr( (Expr ) returnType.hidden );
	}

	public String getNameWithoutParamNumber() {
		return hidden.getNameWithoutParamNumber();
	}

	@Override
	public WrSymbol getFirstSymbol() {
		return hidden.getFirstSymbol().getI();
	}

	@Override
	public String getFunctionNameWithSelf(String fullName) {
		return hidden.getFunctionNameWithSelf(fullName);
	}

	@Override
	MethodSignatureOperator getHidden() {
		return hidden;
	}

}
