package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_cge;
import meta.WrAnnotationAt;

/**
 * add statements to check the call stack at runtime. If it is not the first parameter, issue an error.
   @author jose
 */
public class CyanMetaobjectTestCompilerCHS extends CyanMetaobjectAtAnnot
       implements IAction_cge {

	public CyanMetaobjectTestCompilerCHS() {
		super("chs", AnnotationArgumentsKind.TwoParameters );
	}

	/* @see ast.CyanMetaobject#dsa_javaCodeThatReplacesMetaobjectAnnotation()()
	 */
	@Override
	public StringBuffer cge_codeToAdd() {

		final StringBuffer sb = new StringBuffer();
		final WrAnnotationAt annotation = (WrAnnotationAt ) this.metaobjectAnnotation;
		final List<Object> paramList = annotation.getJavaParameterList();

		final String s1 = (String ) paramList.get(0);
		final String s2 = (String ) paramList.get(1);

		sb.append( "    if ( ! _System.checkStack.peek().equals(\"" + s1 + "\") ) { System.out.println(\"" + s2 + "\"); System.exit(1); } \n");
		sb.append("     else _System.checkStack.pop();\n");
		return sb;

	}


	@Override
	public void check() {
		final WrAnnotationAt annotation = (WrAnnotationAt ) this.metaobjectAnnotation;
		final List<Object> paramList = annotation.getJavaParameterList();
		if ( paramList.size() != 2 || ! ( paramList.get(0) instanceof String ) || ! ( paramList.get(1) instanceof String )) {
			addError("This metaobject takes exactly two parameters of type String");
		}
	}

}
