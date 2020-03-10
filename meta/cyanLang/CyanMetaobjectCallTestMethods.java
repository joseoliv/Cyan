package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dsa;
import meta.ICompiler_dsa;

/**
 * The name of this metaobject is 'callTestMethods'.
 *
 * This metaobject generates code that call all methods of the current prototype
 * that end with 'Test'
 *
   @author jose
 */
public class CyanMetaobjectCallTestMethods extends CyanMetaobjectAtAnnot implements IAction_dsa {

	public CyanMetaobjectCallTestMethods() {
		super("callTestMethods", AnnotationArgumentsKind.ZeroParameters);
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		StringBuffer s = new StringBuffer();

		for ( String methodName : compiler_dsa.getUnaryMethodNameList() ) {
			if ( methodName.endsWith("Test") ) {
				s.append("    " + methodName + ";\n");
			}
		}

		return s;
	}

}
