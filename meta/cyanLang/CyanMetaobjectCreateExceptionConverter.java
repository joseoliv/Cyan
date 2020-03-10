package meta.cyanLang;


import java.util.List;
import java.util.HashSet;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpa;
import meta.ICompilerAction_dpa;

/**
 * This metaobject create the  methods for the CatchConverter prototype
   @author José
 */

public class CyanMetaobjectCreateExceptionConverter extends CyanMetaobjectAtAnnot
	implements IAction_dpa {

		public CyanMetaobjectCreateExceptionConverter() {
			super("createExceptionConverter", AnnotationArgumentsKind.ZeroParameters);
		}


		@Override
		public StringBuffer dpa_codeToAdd(ICompilerAction_dpa compiler) {

			StringBuffer s = new StringBuffer();

			if ( ! compiler.getCurrentPrototypeId().equals("ExceptionConverter") ) {
				compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
						"' should only be used in prototype ExceptionConverter");
				return null;
			}


			List<List<String>> strListList = compiler.getGenericPrototypeArgListList();
			/* if ( strListList == null || ! compiler.getCurrentPrototypeName().startsWith("Union") ) {
				compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), "Metaobject '" + getName() +
						"' should only be used in a generic prototype",
						this.getMetaobjectAnnotation().getFirstSymbol().getSymbolString(), ErrorKind.metaobject_error);
				return null;
			}
			*/

			int sizeListList = strListList.size();
			if ( sizeListList != 1 ) {
				compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(),
						"Prototype 'ExceptionConverter' should have just one pair of '<' and '>' with parameters (like 'ExceptionConverter<Exception1, Exception2>')");
				return null;
			}
			List<String> strList = strListList.get(0);
			int size = strList.size();
			if ( size < 2 || size%2 != 0 ) {
				compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(),
						"Prototype 'ExceptionConverter' should have at least two parameters and it should have an even number of parameters");
				return null;
			}

			s.append("    overload\n");
			HashSet<String> set = new HashSet<>();
			for (int i = 0; i < size; i += 2) {
				String source = strList.get(i);
				String target = strList.get(i+1);
				s.append("    func eval: " + source + " e { \n");
				s.append("        throw: " + target + "()\n" );
				s.append("    }\n");

				if ( set.contains(source) ) {
					compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(),
							"Prototype '" + source + "' has been used twice in the instantiation of 'ExceptionConverter'");
				}
				set.add(source);
			}
			return s;
		}

	}
