package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_cge;
import meta.WrAnnotationAt;

/**
 * Push Call Stack -- push an element in the call stack
   @author jose
 */
public class CyanMetaobjectTestCompilerPCS extends CyanMetaobjectAtAnnot
implements IAction_cge {

	public CyanMetaobjectTestCompilerPCS() {
		super( "pcs", AnnotationArgumentsKind.OneParameter );
	}


	/* @see ast.CyanMetaobject#semAn_javaCodeThatReplacesAnnotation()()
	 */
	@Override
	public StringBuffer cge_codeToAdd() {
		final StringBuffer sb = new StringBuffer();
		// // sb.append( "_System.checkStack.push(\"" + ((String ) annotation.getInfo_parsing()) + "\");\n");
		sb.append( "_System.checkStack.push(\"" + elem + "\");\n");
		return sb;

	}


	@Override
	public void check() {
		final WrAnnotationAt annotation = (WrAnnotationAt ) this.annotation;
		final List<Object> paramList = annotation.getJavaParameterList();
		if ( paramList.size() != 1 || ! ( paramList.get(0) instanceof String ) ) {
			addError("This metaobject takes exactly one parameter of type String");
			return ;
		}
		// // annotation.setInfo_parsing( paramList.get(0) );
		elem = (String ) paramList.get(0);
	}

	private String elem;
}
