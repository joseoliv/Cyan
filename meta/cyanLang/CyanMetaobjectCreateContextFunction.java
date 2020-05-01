/**

 */
package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICompilerAction_parsing;

/**
 * This metaobject is related to a feature that has not yet been added to the Cyan language, Context Functions.
   @author José

 */
public class CyanMetaobjectCreateContextFunction extends CyanMetaobjectAtAnnot
    implements meta.IAction_parsing {



	public CyanMetaobjectCreateContextFunction() {
		super("createContextFunction", AnnotationArgumentsKind.ZeroParameters);
	}

	@Override
	public StringBuffer parsing_codeToAdd(ICompilerAction_parsing compiler) {
		StringBuffer s = new StringBuffer();

		List<List<String>> strListList = compiler.getGenericPrototypeArgListList();
		if ( strListList == null ) {
			compiler.error(this.getAnnotation().getFirstSymbol(), "Metaobject '" + getName() + "' should only be used in a generic prototype");
			return null;
		}


		strListList.size();


		if ( strListList.size() < 1 ) {
			compiler.error(this.getAnnotation().getFirstSymbol(), "This metaobject should only be used in generic prototype cyan.lang.ContextObject");
			return s;
		}

		int count = 0;
		s.append("    func bindToFunction: " + strListList.get(0).get(0) + " -> UFunction");

		for ( List<String> strList  : strListList ) {
			strList.size();
			s.append("<");
			int sizeStr = strList.size();
			if ( count == 0 ) {
				int sizeCount = sizeStr;
				for ( int k = 1; k < sizeStr; ++k ) {
					s.append(strList.get(k));
					if ( --sizeCount > 1 )
						s.append(", ");
				}
				count = 1;
			}
			else {
				for ( String str : strList ) {
					s.append(str);
					if ( --sizeStr > 0 )
						s.append(", ");
				}
			}
			s.append(">");
		}
		s.append("\n");


		return s;
	}

}
