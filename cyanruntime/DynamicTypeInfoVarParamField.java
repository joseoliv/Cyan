package cyanruntime;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class DynamicTypeInfoVarParamField implements IGenDynInfo {
	public DynamicTypeInfoVarParamField(String name, int line, int column,
			int fileOffset, boolean wasTypeSupplied) {
		super();
		this.name = name;
		this.line = line;
		this.column = column;
		this.fileOffset = fileOffset;
		this.runtimeType = new HashSet<>();
		this.wasTypeSupplied = wasTypeSupplied;
	}
	@Override
	public void genJSON(PrintWriter pw, boolean genCommaAfter) {
		pw.println("              {");
		pw.println("                \"name\": \"" + name + "\",");
		pw.println("                \"line\": " + line + ",");
		pw.println("                \"column\": " + column + ",");
		pw.println("                \"fileOffset\": " + fileOffset + ",");
		pw.println("                \"wasTypeSupplied\": " + wasTypeSupplied + ",");
		pw.print  ("                \"runtimeType\": [ ");
		String s = "";
		int size = runtimeType.size();
		for ( String rt : runtimeType ) {
			s += "\"" + rt.replace("cyan.lang.", "") + "\"";
			if ( --size > 0 ) {
				s += ", ";
			}
		}
		if ( s.length() > 50 ) {
			pw.println("");
			s.replace(", ", ",\r\n");
			pw.println(s);
			pw.println("              ]");
		}
		else {
			pw.println(s + " ]");
		}

		pw.println("              }" + (genCommaAfter ? "," : ""));

	}

	public String name;
	public int line;
	public int column;
	public int fileOffset;
	public boolean wasTypeSupplied;
	public Set<String> runtimeType;
}