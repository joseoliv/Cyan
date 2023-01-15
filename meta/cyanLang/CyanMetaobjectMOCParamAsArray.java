package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICompilerAction_parsing;
import meta.MetaHelper;
import meta.WrAnnotationAt;
import meta.WrExprAnyLiteral;

/**
 * a demonstration metaobject. If used inside a generic prototype, <br>
 * <code>{@literal @}moCallParamAsArray(a, b, c)</code><br>
 * will create variable moParamList which is assigned an array with the parameters to the annotation (as strings)
 * and  variable genParamList to which is assigned an array with the generic prototype parameters (as strings).
   @author jose
 */
public class CyanMetaobjectMOCParamAsArray extends CyanMetaobjectAtAnnot  implements meta.IAction_parsing {

	public CyanMetaobjectMOCParamAsArray() {
		super("moCallParamAsArray", AnnotationArgumentsKind.OneOrMoreParameters);
	}


	@Override
	public StringBuffer parsing_codeToAdd(ICompilerAction_parsing compiler) {
		final WrAnnotationAt annotation = this.getAnnotation();
		final StringBuffer s = new StringBuffer();

		s.append("var moParamList = [ ");
		final List<WrExprAnyLiteral> parameterList =  annotation.getRealParameterList(); //annotation.getJavaParameterList();
		int size3 = parameterList.size();
		for ( final Object obj : parameterList ) {
			String cyanStr = ((WrExprAnyLiteral ) obj).asString();
			if ( cyanStr.charAt(0) == '#' )
				cyanStr = "\\" + cyanStr;
			cyanStr = MetaHelper.addQuotes(cyanStr);
			s.append( cyanStr );
			//s.append( ((ExprAnyLiteral ) obj).getStringJavaValue() );
			if ( --size3 > 0 )
				s.append(", ");
		}
		s.append(" ];\n");


		final List<List<String>> strListList = compiler.getGenericPrototypeArgListList();
		s.append(" var genParamList = [ ");
		int size = strListList.size();
		for ( final List<String> strList : strListList ) {
			s.append("[ ");
			int size2 = strList.size();
			for ( final String elem : strList ) {
				s.append("\"" + elem + "\"");
				if ( --size2 > 0 )
					s.append(", ");
			}
			s.append(" ] ");
			if ( --size > 0 )
				s.append(", ");
		}
		s.append(" ];\n");

		return s;
	}


}
