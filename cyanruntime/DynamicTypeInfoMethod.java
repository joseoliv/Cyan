package cyanruntime;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DynamicTypeInfoMethod implements IGenDynInfo {
	public DynamicTypeInfoMethod(String name) {
		this.name = name;
		this.parameterSet = new HashMap<>();
		this.localVariableSet = new HashMap<>();
	}
	@Override
	public void genJSON(PrintWriter pw, boolean genCommaAfter) {

		pw.println("        {");
		pw.println("          \"name\": \"" + this.name + "\",");
		if ( this.parameterSet.size() > 0 ) {
			pw.println("          \"parameters\": [");

			int size = this.parameterSet.size();
			for (Entry<String, DynamicTypeInfoVarParamField> entryPar : this.parameterSet.entrySet() ) {
				DynamicTypeInfoVarParamField p = entryPar.getValue();
				--size;
				p.genJSON(pw, size > 0);
			}
			pw.println("          ]" + (this.localVariableSet.size() > 0 ? "," : ""));
		}
		if ( this.localVariableSet.size() > 0 ) {
			pw.println("          \"local variables\": [");
			int size = this.localVariableSet.size();
			for (Entry<String, DynamicTypeInfoVarParamField> entryPar : this.localVariableSet.entrySet() ) {
				DynamicTypeInfoVarParamField p = entryPar.getValue();
				--size;
				p.genJSON(pw, size > 0);
			}
			pw.println("          ]");
		}
		pw.println("        }" + (genCommaAfter ? ", " : ""));
	}
	public String name;
	public Map<String, DynamicTypeInfoVarParamField> parameterSet;
	public Map<String, DynamicTypeInfoVarParamField> localVariableSet;
}