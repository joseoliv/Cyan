package meta;

import java.util.List;
import java.util.Set;
import ast.ASTNode;
import ast.Program;

public class WrProgram extends WrASTNode implements IDeclaration {

    public WrProgram(Program hiddenProgram) {
        this.hidden = hiddenProgram;
    }
    Program hidden;



	@Override
	ASTNode getHidden() {
		return hidden;
	}

	/*
    public void accept(WrASTVisitor visitor) {
        hidden.accept(visitor);
    }
    */

    @Override
    public String getName() {
        return hidden.getName();
    }
    @Override
    public AttachedDeclarationKind getKind(WrEnv env) {
        return hidden.getKind();
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

	public Object getProgramValueFromKey(String key) {
		return hidden.getProgramValueFromKey(key);
	}

	public Set<String> getProgramKeyValueSet(String key) {
		return hidden.getProgramKeyValueSet(key);
	}


}
