package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrMethodSignature;
import meta.WrPrototype;
import meta.WrType;
import meta.WrTypeUnion;

/**
 * An annotation of this metaobject should be attached to the generic prototype <code>Array{@literal<}T></code> in order to add
 * to it methods <code>sort</code> and <code>sortDescending</code> for sorting. The array is sorted in place and self is
 * returned.
   @author jose
 */

public class CyanMetaobjectCreateArrayMethods  extends CyanMetaobjectAtAnnot
	implements meta.IAction_afterResTypes {

	public CyanMetaobjectCreateArrayMethods() {
		super("createArrayMethods", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC });
	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler_afterResTypes, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {




		List<List<String>> strListList = compiler_afterResTypes.getGenericPrototypeArgListList();
		if ( strListList == null || strListList.get(0) == null || strListList.get(0).size() != 1 ) {
			compiler_afterResTypes.error(this.getAnnotation().getFirstSymbol(),
					"Metaobject '" + getName() + "' should only be used in a generic prototype with just one parameter");
			return null;
		}
		String paramTypeName = strListList.get(0).get(0);


		WrEnv env = compiler_afterResTypes.getEnv();

		String javaParamTypeName = MetaHelper.getJavaName(paramTypeName);

		WrType aType = null;
		WrType insideType = null;
		if ( paramTypeName.equals(MetaHelper.dynName) ) {
			paramTypeName = "Object";
		}
		else {
			// WrPrototype pu = compiler_afterResTypes.getCompilationUnit().getPublicPrototype();
			aType = env.searchPackagePrototype(paramTypeName, env.getCurrentPrototype().getFirstSymbol(env));
			insideType = aType.getInsideType();
		}

		String strSlotList = "";
		StringBuffer s = new StringBuffer();
		String tmp;

		if ( insideType != null && (insideType instanceof WrPrototype || insideType instanceof WrTypeUnion) ) {

			tmp = "    func init ";
			strSlotList += tmp;

			s.append("    @javacode{*\r\n" +
					"           // very inefficient, I know that\r\n" +
					"        public ArrayList<" + javaParamTypeName + "> array = new ArrayList<" + javaParamTypeName + ">();\r\n" +
					"        public THISPROTOTYPE999( " + javaParamTypeName + " []anArray ) {\r\n" +
					"            array = new ArrayList<" + javaParamTypeName + ">();\r\n" +
					"            for (int i = 0; i < anArray.length; ++i) {\r\n" +
					"                array.add(anArray[i]);\r\n" +
					"            }\r\n" +
					"        }\r\n" +
					"    *}\r\n" +
					"\r\n" +
					"    \r\n" +
					tmp + "{ \r\n" +
					"        @javacode[*{\r\n" +
					"            array = new ArrayList<" + javaParamTypeName + ">();\r\n" +
					"        }*]\r\n" +
					"    }\n");

			tmp = "    func init: Int intSizeArray ";
			strSlotList += tmp;
			s.append(tmp + "{\r\n" +
					"        @javacode[*{\r\n" +
					"            array = new ArrayList<" + javaParamTypeName + ">(_intSizeArray.n);\r\n" +
					"        }*]\r\n" +
					"    }\r\n" +
					"");

			tmp = "    func hashCode -> Int ";
			strSlotList += tmp;
			s.append("    override\r\n" +
					tmp + "{\r\n" +
					"        var hash = 0;\r\n" +
					"        for elem in self {\r\n" +
					"            hash = hash + elem ?hashCode\r\n" +
					"        }\r\n" +
					"        return hash;\r\n" +
					"    }\r\n" +
					"");
			tmp = "    func asString: Int ident -> String ";
			strSlotList += tmp;
			s.append("    override\r\n" +
					tmp + "{\r\n" +
					"        var String s = \"\";\r\n" +
					"        for i in 1..ident  { s = s ++ \" \" }\r\n" +
					"        s = s ++ \"[ \";\r\n" +
					"        var Int size2 = size;\r\n" +
					"        for elem in self {\r\n" +
					"            s = s ++ elem ?asStringQuoteIfString;\r\n" +
					"            --size2;\r\n" +
					"            if size2 > 0 { s = s ++ \", \" }\r\n" +
					"        }\r\n" +
					"        s = s ++ \" ]\";\r\n" +
					"        return s\r\n" +
					"    }    \r\n" +
					"");

			tmp = "    func apply: (String message) ";
			strSlotList += tmp;
			s.append(tmp + "{\r\n" +
					"\r\n" +
					"        self foreach: { (: " + paramTypeName + " elem :)\r\n" +
					"            elem `message\r\n" +
					"        };\r\n" +
					"    }\r\n" +
					"");
			tmp = "    func apply: (String message) select: (String slot) -> Dyn ";
			strSlotList += tmp;
			s.append(tmp + " {\r\n" +
					"        var Dyn sum = self[0] `slot;\r\n" +
					"        var Int i = 1;\r\n" +
					"        while i < size {\r\n" +
					"            let " + paramTypeName + " elem = self[i];\r\n" +
					"            sum = sum `message: (elem `slot);\r\n" +
					"            ++i\r\n" +
					"        }\r\n" +
					"        return sum\r\n" +
					"    }\r\n" +
					"\r\n" +
					"");

			tmp = "    func .+ (String message) -> Dyn ";
			strSlotList += tmp;
			s.append(tmp + "{\r\n" +
					"        var " + paramTypeName + " sum = self[0];\r\n" +
					"        1 ..< size foreach: { \r\n" +
					"                (: Int i :)\r\n" +
					"                 sum = sum `message: self[i]\r\n" +
					"             }; \r\n" +
					"        return sum\r\n" +
					"    }\r\n" +
					"");

			tmp = "    func .* (String message) ";
			strSlotList += tmp;
			s.append(tmp + "{\r\n" +
					"        self apply: message\r\n" +
					"    }\r\n" +
					"");

			if ( insideType instanceof WrPrototype ) {
				strSlotList = genSortMethos(paramTypeName, env, javaParamTypeName,
						insideType, strSlotList, s);

			}
				// _lessThan_equal_greaterThan
		}
		else {

			tmp = "    func init ";
			strSlotList += tmp;

			s.append("    @javacode{*\r\n" +
					"           // very inefficient, I know that\r\n" +
					"        public ArrayList<" + paramTypeName + "> array = new ArrayList<" +
					paramTypeName + ">();\r\n" +
					"        public THISPROTOTYPE999( " + paramTypeName + " []anArray ) {\r\n" +
					"            array = new ArrayList<" + paramTypeName + ">();\r\n" +
					"            for (int i = 0; i < anArray.length; ++i) {\r\n" +
					"                array.add(anArray[i]);\r\n" +
					"            }\r\n" +
					"        }\r\n" +
					"    *}\r\n" +
					"\r\n" +
					"    \r\n" +
					tmp + "{ \r\n" +
					"        @javacode[*{\r\n" +
					"            array = new ArrayList<" + paramTypeName + ">();\r\n" +
					"        }*]\r\n" +
					"    }\n");

			tmp = "    func init: Int intSizeArray ";
			strSlotList += tmp;
			s.append(tmp + "{\r\n" +
					"        @javacode[*{\r\n" +
					"            array = new ArrayList<" + paramTypeName + ">(_intSizeArray.n);\r\n" +
					"        }*]\r\n" +
					"    }\r\n" +
					"");

			tmp = "    func hashCode -> Int ";
			strSlotList += tmp;
			s.append("    override\r\n" +
					tmp + "{\r\n" +
					"        @javacode{*\r\n" +
					"            return new CyInt(System.identityHashCode(this));\r\n" +
					"        *}\r\n" +
					"    }\r\n" +
					"");

		}
		List<StringBuffer> tupleArray = new ArrayList<>();
		// WrPrototype thisProto = (WrPrototype ) this.getAnnotation().getDeclaration();


		tupleArray.add( s );
		return new Tuple2<StringBuffer, String>(s, strSlotList);
	}

	/**
	   @param paramTypeName
	   @param env
	   @param javaParamTypeName
	   @param insideType
	   @param strSlotList
	   @param s
	   @return
	 */
	private static String genSortMethos(String paramTypeName, WrEnv env,
			String javaParamTypeName, WrType insideType, String strSlotList,
			StringBuffer s) {
		WrPrototype pu = (WrPrototype ) insideType.getInsideType();

		List<WrMethodSignature> methodSignatureList = env.searchMethodPublicSuperPublic(pu, "<=>1");
		if ( methodSignatureList != null && methodSignatureList.size() > 0 ) {
			/**
			 * add method 'sort'
			 */

			s.append("    @javacode{*+\n");
			s.append("    public static java.util.Comparator<" + javaParamTypeName + "> lowToHighComparator\n");
			s.append("                              = new java.util.Comparator<" + javaParamTypeName + ">() {\n");
			s.append("\n");
			s.append("	        public int compare(" + javaParamTypeName + " elem1, " + javaParamTypeName + " elem2) {\n");
			s.append("\n");
			s.append("    	        return elem1._lessThan_equal_greaterThan(elem2).n;\n");
			s.append("\n");
			s.append("	        }\n");
			s.append("\n");
			s.append("	    };\n");

			s.append("    public static java.util.Comparator<" + javaParamTypeName + "> highToLowComparator\n");
			s.append("                              = new java.util.Comparator<" + javaParamTypeName + ">() {\n");
			s.append("\n");
			s.append("	        public int compare(" + javaParamTypeName + " elem1, " + javaParamTypeName + " elem2) {\n");
			s.append("\n");
			s.append("    	        return elem2._lessThan_equal_greaterThan(elem1).n;\n");
			s.append("\n");
			s.append("	        }\n");
			s.append("\n");
			s.append("	    };\n");
			s.append("    +*}\n");

			strSlotList += "    func sort -> Array<" + paramTypeName + ">;\n";
			s.append("    func sort -> Array<" + paramTypeName + "> {\n");
			s.append("        @javacode{*\n");
			s.append("            Collections.sort( array, lowToHighComparator );\n");
			s.append("        *}\n");
			s.append("        return self\n");
			s.append("    }\n");

			strSlotList += "    func sortDescending -> Array<" + paramTypeName + ">;\n";
			s.append("    func sortDescending -> Array<" + paramTypeName + "> {\n");
			s.append("        @javacode{*\n");
			s.append("            Collections.sort( array, highToLowComparator );\n");
			s.append("        *}\n");
			s.append("        return self\n");
			s.append("    }\n");
			s.append("");
		}
		return strSlotList;
	}

}
