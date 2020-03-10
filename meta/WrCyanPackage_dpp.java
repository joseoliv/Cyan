package meta;

import ast.CyanPackage;

public class WrCyanPackage_dpp extends WrCyanPackage {

	public WrCyanPackage_dpp(CyanPackage hidden) {
		super(hidden);
	}


	public void addDocumentText(String doc, String docKind) {
		hidden.addDocumentText(doc, docKind);
	}

	public void addDocumentExample(String example, String exampleKind) {
		hidden.addDocumentExample(example, exampleKind);
	}

	public void addFeature(Tuple2<String, WrExprAnyLiteral> feature) {
		hidden.addFeature(feature);
	}

	public void setPackageKeyValue(String varName, Object value) {
		hidden.setPackageKeyValue(varName, value);
	}

	public void addToPackageKeyValueSet(String varName, String value) {
		hidden.addToPackageKeyValueSet(varName, value);
	}


}
