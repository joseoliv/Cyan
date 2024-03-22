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
 * This metaobject create the  methods for the CatchConverter prototype
   @author José
 */

public class CyanMetaobjectCreateExceptionConverter extends CyanMetaobjectAtAnnot
	implements IAction_afterResTypes {

		public CyanMetaobjectCreateExceptionConverter() {
			super("createExceptionConverter", AnnotationArgumentsKind.ZeroParameters,
					new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC } );
		}


		@Override
		public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
				ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
				List<ISlotSignature>>> infoList) {

			String signatureList = "";
			StringBuffer s = new StringBuffer();

			if ( ! compiler.getCurrentPrototypeName().startsWith("ExceptionConverter<") ) {
				compiler.error(this.getAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
						"' should only be used in prototype ExceptionConverter");
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
						"Prototype 'ExceptionConverter' should have just one pair of '<' and '>' with parameters (like 'ExceptionConverter<Exception1, Exception2>')");
				return null;
			}
			List<String> strList = strListList.get(0);
			int size = strList.size();
			if ( size < 2 || size%2 != 0 ) {
				compiler.error(this.getAnnotation().getFirstSymbol(),
						"Prototype 'ExceptionConverter' should have at least two parameters and it should have an even number of parameters");
				return null;
			}

			signatureList += "    overload\n";
			s.append("    overload\n");
			HashSet<String> set = new HashSet<>();
			for (int i = 0; i < size; i += 2) {
				String source = strList.get(i);
				String target = strList.get(i+1);
				signatureList += "    func eval: " + source + " ;\n";
				s.append("    func eval: " + source + " e { \n");
				s.append("        throw " + target + "()\n" );
				s.append("    }\n");

				if ( set.contains(source) ) {
					compiler.error(this.getAnnotation().getFirstSymbol(),
							"Prototype '" + source + "' has been used twice in the instantiation of 'ExceptionConverter'");
				}
				set.add(source);
			}
			return new Tuple2<StringBuffer, String>(s, signatureList);
		}

	}
