package meta;

import java.util.List;
import java.util.Map;

public class InterpreterPrototype {

	public Map<String, List<WrStatement>> mapMethodName_Body;
	Map<String, Object> fieldMemory;
	List<WrStatementImport> importList;
}
