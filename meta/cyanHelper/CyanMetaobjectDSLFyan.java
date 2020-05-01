package meta.cyanHelper;

import java.util.ArrayList;
import java.util.List;
import meta.CyanMetaobjectFromDSL_toPrototype;
import meta.ICompiler_dsl;
import meta.Tuple3;
/**
 * yet to be made. The .fyan file should consist of method declarations. For each
 * method the metaobject creates a prototype with the same name as the method but in upper-case.
 * The prototype would inherit from the adequate Function.
 * So for the code <br>
 * <code>
 * import math<br>
 * func sin: Double x -> Double { return Math sin: x }<br>
 * func cos: Double x -> Double { return Math cos: x }<br>
 * func tan: Double x -> Double { return Math tan: x }<br>
 * </code><br>
 * the metaobject would create prototypes <code>Sin</code>, <code>Cos</code>, and <code>Tan</code>.
 * The code for Sin, for example, would be
 * <code><br>
 * package packageName<br>
 * import math<br>
 * object Sin extends Function<Double, Double><br>
 *     func eval: Double x -> Double { return Math sin: x }<br>
 * end<br>
 * </code><br> *
   @author jose
 */
public class CyanMetaobjectDSLFyan extends CyanMetaobjectFromDSL_toPrototype {

	public CyanMetaobjectDSLFyan() {
		super("fyan");
	}

	/**
	 * yet to be made
	 */
	@Override
	public List<Tuple3<String, String, char[]>> parsing_NewPrototype(ICompiler_dsl compiler_dsl) {
		StringBuffer sb = new StringBuffer();
		sb.append("package " + this.getPackageNameDSL() + "\n\n");
		sb.append("object " + this.getPrototypeName() + "\n\n");
		//sb.append(this.getText());
		sb.append("\nend\n");
		List<Tuple3<String, String, char[]>> array = new ArrayList<>();
		array.add(new Tuple3<String, String, char[]>(this.getPrototypeName(),
				this.getPrototypeName() + ".cyan", new String(sb).toCharArray()) );
		return array;
	}


}
