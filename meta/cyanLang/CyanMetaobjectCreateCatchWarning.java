package meta.cyanLang;


import java.util.List;
import java.util.HashSet;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpa;
import meta.ICompilerAction_dpa;

/**
 * This metaobject create the  methods for the CatchWarning prototype
   @author José
 */

public class CyanMetaobjectCreateCatchWarning extends CyanMetaobjectAtAnnot 
	implements IAction_dpa {

		public CyanMetaobjectCreateCatchWarning() { 
			super("createCatchWarning", AnnotationArgumentsKind.ZeroParameters);
		}


		@Override
		public StringBuffer dpa_codeToAdd(ICompilerAction_dpa compiler) {
			
			StringBuffer s = new StringBuffer();
			
			if ( ! compiler.getCurrentPrototypeId().equals("CatchWarning") ) {
				compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), "Metaobject '" + getName() + 
						"' should only be used in prototype CatchWarning");
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
						"Prototype 'CatchWarning' should have just one pair of '<' and '>' with parameters (like 'CatchWarning<Exception1, Exception2>')");
				return null;
			}
			List<String> strList = strListList.get(0);
			HashSet<String> set = new HashSet<>();
			
			s.append("    overload\n");
			for ( String protoName : strList ) {
				s.append("    func eval: " + protoName + " e { \n");
				s.append("        (\"Exception \" ++ (e prototypeName) ++ \" was thrown\") println;\n" );
				s.append("    }\n");
				
				if ( set.contains(protoName) ) {
					compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), 
							"Prototype '" + protoName + "' has been used twice in the instantiation of 'CatchWarning'");
				}
				set.add(protoName);
			}
			return s;
		}

	}
