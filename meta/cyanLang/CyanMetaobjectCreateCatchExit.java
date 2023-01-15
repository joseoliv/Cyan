package meta.cyanLang;


import java.util.HashSet;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.Tuple2;
import meta.WrAnnotation;

/**
 * This metaobject create the methods for the CatchExit prototype
   @author José
 */

public class CyanMetaobjectCreateCatchExit extends CyanMetaobjectAtAnnot
	implements IAction_afterResTypes {

		public CyanMetaobjectCreateCatchExit() {
			super("createCatchExit", AnnotationArgumentsKind.ZeroParameters,
					new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC });
		}


		@Override
		public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
				ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
				List<ISlotSignature>>> infoList) {

			StringBuffer s = new StringBuffer();

			if ( ! compiler.getCurrentPrototypeName().startsWith("CatchExit<") ) {
				compiler.error(this.getAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
						"' should only be used in prototype CatchExit");
				return null;
			}


			List<List<String>> strListList = compiler.getGenericPrototypeArgListList();
			/* if ( strListList == null || ! compiler.getCurrentPrototypeName().startsWith("Union") ) {
				compiler.error(this.getAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
						"' should only be used in a generic prototype",
						this.getAnnotation().getFirstSymbol().getSymbolString(), ErrorKind.metaobject_error);
				return null;
			}
			*/

			int sizeListList = strListList.size();
			if ( sizeListList != 1 ) {
				compiler.error(this.getAnnotation().getFirstSymbol(),
						"Prototype 'CatchExit' should have just one pair of '<' and '>' with parameters (like 'CatchExit<Exception1, Exception2>')");
				return null;
			}
			List<String> strList = strListList.get(0);
			HashSet<String> set = new HashSet<>();

			String signatureList = "";
			s.append("    overload\n");
			for ( String protoName : strList ) {
				signatureList += "    func eval: " + protoName + "; \n";
				s.append("    func eval: " + protoName + " e { exit: e prototypeName }\n ");
				if ( set.contains(protoName) ) {
					compiler.error(this.getAnnotation().getFirstSymbol(),
							"Prototype '" + protoName + "' has been used twice in the instantiation of 'CatchExit'");
				}
				set.add(protoName);
			}
			s.append("\n");
			s.append("    func exit: String protoName { \n");
			s.append("        (\"Fatal error: exception \" ++ protoName ++ \" was thrown\") println;\n" );
			s.append("        System exit\n");
			s.append("    }\n");
			s.append("\n");
			signatureList += "    func exit: String; \n";

			return new Tuple2<StringBuffer, String>(s, signatureList);
		}

	}

