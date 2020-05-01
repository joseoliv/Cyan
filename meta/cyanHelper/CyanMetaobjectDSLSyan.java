package meta.cyanHelper;

import java.util.ArrayList;
import java.util.List;
import meta.CyanMetaobjectFromDSL_toPrototype;
import meta.ICompiler_dsl;
import meta.Tuple3;

public class CyanMetaobjectDSLSyan extends CyanMetaobjectFromDSL_toPrototype {

	public CyanMetaobjectDSLSyan() {
		super("syan");
	}
	@Override
	public List<Tuple3<String, String, char[]>> parsing_NewPrototype(ICompiler_dsl compiler_dsl) {
		StringBuffer sb = new StringBuffer();
		sb.append("package " + this.getPackageNameDSL() + "\n\n");
		sb.append("object " + this.getPrototypeName() + "\n\n");
		sb.append(this.getText());
		sb.append("\nend\n");
		List<Tuple3<String, String, char[]>> array = new ArrayList<>();
		array.add(new Tuple3<String, String, char[]>(this.getPrototypeName(),
				this.getPrototypeName() + ".cyan", new String(sb).toCharArray()) );
		return array;
	}

}
