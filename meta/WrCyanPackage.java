package meta;

import java.util.List;
import java.util.Set;
import ast.CyanPackage;

public class WrCyanPackage extends WrASTNode implements IDeclaration {

	public WrCyanPackage(CyanPackage hidden) {
		this.hidden = hidden;
	}
	CyanPackage hidden;

	@Override
	CyanPackage getHidden() {
		return hidden;
	}


	@Override
	public String getName() {
		return hidden.getName();
	}

	public String getPackageName() {
		return hidden.getPackageName();
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
	public AttachedDeclarationKind getKind(WrEnv env) {
		return hidden.getKind();
	}
	@Override
	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList(WrEnv env) {
		return hidden.getFeatureList();
	}
	@Override
	public List<WrExprAnyLiteral> searchFeature(String name, WrEnv env) {
		return hidden.searchFeature(name);
	}

	public Object getPackageValueFromKey(String key) {
		return hidden.getPackageValueFromKey(key);
	}

	public Set<String> getPackageKeyValueSet(String key) {
		return hidden.getPackageKeyValueSet(key);
	}


}

