package meta.cyanLang;


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

public class CyanMetaobjectCreateExceptionEncapsulator extends CyanMetaobjectAtAnnot
	implements IAction_afterResTypes {

		public CyanMetaobjectCreateExceptionEncapsulator() {
			super("createExceptionEncapsulator", AnnotationArgumentsKind.ZeroParameters,
					new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC } );
		}


		@Override
		public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
				ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
				List<ISlotSignature>>> infoList) {

			String signatureList = "";
			StringBuffer s = new StringBuffer();

			if ( ! compiler.getCurrentPrototypeName().startsWith("ExceptionEncapsulator<") ) {
				compiler.error(this.getAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
						"' should only be used in prototype ExceptionEncapsulator");
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
						"Prototype 'ExceptionEncapsulator' should have just one pair of '<' and '>' with parameters (like 'ExceptionEncapsulator<Exception1, Exception2>')");
				return null;
			}
			List<String> strList = strListList.get(0);
			int size = strList.size();
			if ( size < 2 ) {
				compiler.error(this.getAnnotation().getFirstSymbol(),
						"Prototype 'ExceptionEncapsulator' should have at least two parameters");
				return null;
			}
			String encapsulator = strList.get(size-1);
			signatureList += "    overload\n";
			s.append("    overload\n");
			for (int i = 0; i < size - 1; ++i) {
				String source = strList.get(i);
				signatureList += "    func eval: " + source + ";\n";
				s.append("    func eval: " + source + " e { \n");
				s.append("        throw: " + encapsulator + "(e)" );
				s.append("    }\n");

			}

			return new Tuple2<StringBuffer, String>(s, signatureList);
		}

	}
