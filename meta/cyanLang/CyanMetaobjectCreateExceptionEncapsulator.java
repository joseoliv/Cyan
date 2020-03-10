package meta.cyanLang;


import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpa;
import meta.ICompilerAction_dpa;

/**
 * This metaobject create the  methods for the CatchConverter prototype
   @author José
 */

public class CyanMetaobjectCreateExceptionEncapsulator extends CyanMetaobjectAtAnnot 
	implements IAction_dpa {

		public CyanMetaobjectCreateExceptionEncapsulator() { 
			super("createExceptionEncapsulator", AnnotationArgumentsKind.ZeroParameters);
		}


		@Override
		public StringBuffer dpa_codeToAdd(ICompilerAction_dpa compiler) {
			
			StringBuffer s = new StringBuffer();
			
			if ( ! compiler.getCurrentPrototypeId().equals("ExceptionEncapsulator") ) {
				compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), "Metaobject '" + getName() + 
						"' should only be used in prototype ExceptionEncapsulator");
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
						"Prototype 'ExceptionEncapsulator' should have just one pair of '<' and '>' with parameters (like 'ExceptionEncapsulator<Exception1, Exception2>')");
				return null;
			}
			List<String> strList = strListList.get(0);
			int size = strList.size();
			if ( size < 2 ) {
				compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), 
						"Prototype 'ExceptionEncapsulator' should have at least two parameters");
				return null;
			}
			String encapsulator = strList.get(size-1);
			s.append("    overload\n");
			for (int i = 0; i < size - 1; ++i) {
				String source = strList.get(i);
				s.append("    func eval: " + source + " e { \n");
				s.append("        throw: " + encapsulator + "(e)" );
				s.append("    }\n");
				
			}
			
			return s;
		}

	}
