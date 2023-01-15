package cyanruntime;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DynamicTypeInfoPrototype implements IGenDynInfo {
	public DynamicTypeInfoPrototype(String name, String sha256) {
		this.name = name;
		this.sha256 = sha256;
		this.fieldSet = new HashMap<>();
		this.methodSet = new HashMap<>();
	}
	@Override
	public void genJSON(PrintWriter pw, boolean genCommaAfter) {
		pw.println("    {");
		pw.println("      \"name\": \"" + name + "\",");
		pw.println("      \"sha256\": \"" + sha256 + "\", ");
		if ( fieldSet.size() > 0 ) {
			pw.println("      \"field list\": [");
			int size = fieldSet.size();
			for (Entry<String, DynamicTypeInfoVarParamField> fieldEntry : fieldSet.entrySet() ) {
				DynamicTypeInfoVarParamField v = fieldEntry.getValue();
				--size;
				v.genJSON(pw, size > 0 );
			}
			pw.println("      ]" + (methodSet.size() > 0 ? "," : ""));
		}
		if ( methodSet.size() > 0 ) {
			pw.println("      \"method list\": [");
			int size = methodSet.size();
			for ( Entry<String, DynamicTypeInfoMethod> entryMethod : methodSet.entrySet() ) {
				DynamicTypeInfoMethod m = entryMethod.getValue();
				--size;
				m.genJSON(pw, size > 0);
			}
			pw.println("      ]");
		}
		pw.println("    }" + (genCommaAfter ? "," : ""));
	}

	public String name;
	public String sha256;
	public Map<String, DynamicTypeInfoVarParamField> fieldSet;
	public Map<String, DynamicTypeInfoMethod> methodSet;
	// hash do arquivo fonte onde está o protótipo
}