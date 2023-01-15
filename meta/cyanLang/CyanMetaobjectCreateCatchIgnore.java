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
 * This metaobject create the  methods for the CatchIgnore prototype
   @author José
 */

public class CyanMetaobjectCreateCatchIgnore extends CyanMetaobjectAtAnnot
	implements IAction_afterResTypes {

		public CyanMetaobjectCreateCatchIgnore() {
			super("createCatchIgnore", AnnotationArgumentsKind.ZeroParameters,
					new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC } );
		}

		@Override
		public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
				ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
				List<ISlotSignature>>> infoList) {


			String signatureList = "";
			StringBuffer s = new StringBuffer();
			String currentProtoName = compiler.getCurrentPrototypeName();

			if ( ! currentProtoName.startsWith("CatchIgnore<") ) {
				compiler.error(this.getAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
						"' should only be used in prototype CatchIgnore");
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
						"Prototype 'CatchIgnore' should have just one pair of '<' and '>' with parameters (like 'CatchIgnore<Exception1, Exception2>')");
				return null;
			}
			List<String> strList = strListList.get(0);
			HashSet<String> set = new HashSet<>();


			s.append("    overload\n");
			for ( String protoName : strList ) {
				signatureList += "    func eval: " + protoName + " ;\n";
				s.append("    func eval: " + protoName + " e { }\n ");
				if ( set.contains(protoName) ) {
					compiler.error(this.getAnnotation().getFirstSymbol(),
							"Prototype '" + protoName + "' has been used twice in the instantiation of 'CatchIgnore'");
				}
				set.add(protoName);
			}
			return new Tuple2<StringBuffer, String>(s, signatureList);

		}



	}
