package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFieldMissing_semAn;
import meta.Tuple3;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrExprAnyLiteral;
import meta.WrExprSelfPeriodIdent;
import saci.NameServer;

/**
 * Deprecated. See metaobject with this same name in cyan.reflect
   @author jose
 */
public class CyanMetaobjectCreateMissingField extends CyanMetaobjectAtAnnot
implements IActionFieldMissing_semAn {

	public CyanMetaobjectCreateMissingField() {
		super("createMissingField2", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC } );
	}

	@Override
	public
	Tuple3<String, String, StringBuffer> semAn_replaceGetMissingField(
			WrExprSelfPeriodIdent fieldToGet, WrEnv env) {

		String fieldSelfName = fieldToGet.asString().substring(5);
		int size = this.getAnnotation().getJavaParameterList().size();
		if ( size%2 != 0 ) {
			this.addError("This annotation should be used with a even number of parameters");
		}
		WrAnnotationAt  annot = this.getAnnotation();
		for (int i = 0; i < size; i+= 2) {
			String strparam = removeQuotes(annot.getJavaParameterList().get(i).toString());
			if ( strparam.equals(fieldSelfName) ) {
				WrExprAnyLiteral value = annot.getRealParameterList().get(i+1);
				String s = value.asString();
				String typeName = NameServer.cyanNameFromJavaBasicType(value.getJavaType());
				if ( typeName == null ) {
					this.addError("Type for parameter '" + s + "' of this annotation is not supported. Use only basic values");
					return null;
				}
				else {
					if ( typeName.startsWith("Cy") ) { typeName = typeName.substring(2); }
				}
				if ( typeName.equals("String") ) { s = "\"" + removeQuotes(s) + "\""; }

				return new Tuple3<String, String, StringBuffer>("cyan.lang", typeName,
						new StringBuffer(s) );

			}
		}
		return null;
	}





}
