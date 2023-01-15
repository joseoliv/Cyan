/**

 */
package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.Tuple2;
import meta.WrAnnotation;

/**
 * This metaobject is related to a feature that has not yet been added to the Cyan language, Context Functions.
   @author José

 */
public class CyanMetaobjectCreateContextFunction extends CyanMetaobjectAtAnnot
    implements IAction_afterResTypes {



	public CyanMetaobjectCreateContextFunction() {
		super("createContextFunction", AnnotationArgumentsKind.ZeroParameters);
	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
			List<ISlotSignature>>> infoList) {

		StringBuffer s = new StringBuffer();
		String signatureList = "";

		List<List<String>> strListList = compiler.getGenericPrototypeArgListList();
		if ( strListList == null ) {
			compiler.error(this.getAnnotation().getFirstSymbol(), "Metaobject '" + getName() + "' should only be used in a generic prototype");
			return null;
		}


		strListList.size();


		if ( strListList.size() < 1 ) {
			compiler.error(this.getAnnotation().getFirstSymbol(), "This metaobject should only be used in generic prototype cyan.lang.ContextObject");
			return null;
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
		signatureList += s.toString();
		s.append("{ See class CyanMetaobjectCreateContextFunction}");

		return new Tuple2<StringBuffer, String>(s, signatureList);
	}

}
