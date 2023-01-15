package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.MethodDec;
import ast.MethodKeywordWithParameters;
import ast.MethodSignatureWithKeywords;
import ast.ParameterDec;
import ast.Type;

public class WrMethodSignatureWithKeywords extends WrMethodSignature {

	public WrMethodSignatureWithKeywords(MethodSignatureWithKeywords hiddenMethodSignature) {
		this.hidden = hiddenMethodSignature;
	}
	/*
    public WrMethodSignatureWithKeywords(
			List<MethodKeywordWithParameters> keywordList,
			boolean indexingMethod, WrMethodDec currentMethod) {
		this(new MethodSignatureWithKeywords(
			       keywordList,
			       indexingMethod, currentMethod));
	}
	*/
	public WrMethodSignatureWithKeywords(
			List<WrMethodKeywordWithParameters> keywordList, WrMethodDec currentMethod,
			boolean indexingMethod ) {

		if ( keywordList != null ) {
			List<MethodKeywordWithParameters> kList = new ArrayList<>();
			for ( WrMethodKeywordWithParameters mk : keywordList ) {
				kList.add(mk.getHidden());
			}
			this.hidden = new MethodSignatureWithKeywords(
					kList,
				       indexingMethod, currentMethod == null ? null : currentMethod.hidden);
		}

	}

	MethodSignatureWithKeywords hidden;


	@Override
	public WrSymbol getFirstSymbol() {
		return hidden.getFirstSymbol().getI();
	}

    @Override
    public void accept(WrASTVisitor visitor, WrEnv env) {

		visitor.preVisit(this, env);
		List<WrMethodKeywordWithParameters> keywordArray = this.getKeywordArray();
		for ( WrMethodKeywordWithParameters sel : keywordArray ) {
			sel.accept(visitor, env);
		}
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

	private void securityCheck(WrEnv env) {
		if ( env.getCurrentPrototype().hidden != hidden.getMethod().getDeclaringObject() ) {
    		throw new MetaSecurityException();
    	}

    	CompilationStep step = env.getCompilationStep();
    	if ( step != CompilationStep.step_1 &&
       		 step != CompilationStep.step_4 &&
       		 step != CompilationStep.step_7 ) {
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
    public void addFeature(Tuple2<String, WrExprAnyLiteral> feature, WrEnv env) {
    	securityCheck(env);
        hidden.addFeature(feature);
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
	public String getNameWithoutParamNumber() {
		return hidden.getNameWithoutParamNumber();
	}
	public List<WrMethodKeywordWithParameters> getKeywordArray() {
		if ( iMessageKeywordWithParametersList == null ) {
			iMessageKeywordWithParametersList = new ArrayList<>();
			if ( hidden.getKeywordArray() != null ) {
				for ( final MethodKeywordWithParameters mk : hidden.getKeywordArray()) {
					iMessageKeywordWithParametersList.add( mk.getI());
				}
			}
		}
		return iMessageKeywordWithParametersList;
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


	private List<WrMethodKeywordWithParameters> iMessageKeywordWithParametersList = null;

	@Override
	public WrType getReturnType(WrEnv env) {
		Type type2 = hidden.getReturnType(env.hidden);
		return type2 == null ? null : type2.getI();
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


	@Override
	public WrExpr getReturnTypeExpr() {
		Expr e = hidden.getReturnTypeExpr();
		return e == null ? null : e.getI();
	}

	@Override
	public void setReturnTypeExpr(WrExpr returnType) {
		hidden.setReturnTypeExpr( returnType == null ? null : (Expr ) returnType.hidden);
	}


	@Override
	public String getFunctionNameWithSelf(String fullName) {
		return hidden.getFunctionNameWithSelf(fullName);
	}

	@Override
	MethodSignatureWithKeywords getHidden() {
		return hidden;
	}
}

