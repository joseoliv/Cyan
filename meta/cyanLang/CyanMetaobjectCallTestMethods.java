package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_semAn;
import meta.ICompiler_semAn;

/**
 * The name of this metaobject is 'callTestMethods'.
 *
 * This metaobject generates code that call all methods of the current prototype
 * that end with 'Test'
 *
   @author jose
 */
public class CyanMetaobjectCallTestMethods extends CyanMetaobjectAtAnnot implements IAction_semAn {

	public CyanMetaobjectCallTestMethods() {
		super("callTestMethods", AnnotationArgumentsKind.ZeroParameters);
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		StringBuffer s = new StringBuffer();

		for ( String methodName : compiler_semAn.getUnaryMethodNameList() ) {
			if ( methodName.endsWith("Test") ) {
				s.append("    " + methodName + ";\n");
			}
		}

		return s;
	}

}
