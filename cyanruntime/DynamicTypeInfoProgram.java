package cyanruntime;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DynamicTypeInfoProgram implements IGenDynInfo {
	public DynamicTypeInfoProgram(String fileNameToAddTypeInfo, String projectDir) {
		this.fileNameToAddTypeInfo = fileNameToAddTypeInfo;
		this.projectDir = projectDir;
		prototypeSet = new HashMap<>();
	}

	@Override
	public void genJSON(PrintWriter pw, boolean genCommaAfter) {
		pw.println("{");
		pw.println("  \"projectDirectory\": \"" + projectDir + "\", ");
		pw.println("  \"prototype list\": [");
		int size = this.prototypeSet.size();
		for ( Entry<String, DynamicTypeInfoPrototype> entry : this.prototypeSet.entrySet() ) {
			DynamicTypeInfoPrototype ip = entry.getValue();
			--size;
			ip.genJSON(pw, size > 0);
		}
		pw.println("  ]");
		pw.println("}");

	}

	public Map<String, DynamicTypeInfoPrototype> prototypeSet;
	public String fileNameToAddTypeInfo;
	public String projectDir;
}