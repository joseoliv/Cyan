/**

 */
package meta.cyanLang;


import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_cge;
import meta.IAction_dsa;
import meta.ICompiler_dsa;
import meta.ReplacementPolicyInGenericInstantiation;
import meta.WrAnnotationAt;

/**
 *  This is the class of metaobject "javacode" that is used to generate code
 *  in Java. When the compiler finds<br>
 *  <code>
 *       {@literal @}javacode<<*    return _n; *>>
 *  </code><br>
 *
 *  It should generate <code>"    return _n; "</code>  when generating Java
 *  code.
 *
   @author José

 */
public class CyanMetaobjectJavaCode extends CyanMetaobjectAtAnnot
             implements IAction_cge, IAction_dsa {

	public CyanMetaobjectJavaCode() {
		super("javacode", AnnotationArgumentsKind.ZeroParameters, null);
	}

	@Override
	public boolean shouldTakeText() { return true; }


	/* @see ast.CyanMetaobject#dsa_javaCodeThatReplacesMetaobjectAnnotation()()
	 */
	@Override
	public StringBuffer cge_codeToAdd() {

		final WrAnnotationAt annot = (WrAnnotationAt ) metaobjectAnnotation;
		final StringBuffer sb = new StringBuffer();

		//char []text = ;
		String text = new String(annot.getTextAttachedDSL());
		text = text.replace("THISPROTOTYPE999", this.getCurrentProgramUnit().getJavaName());

		sb.append(text);
		return sb;
	}

	@Override
	public ReplacementPolicyInGenericInstantiation getReplacementPolicy() {
		return ReplacementPolicyInGenericInstantiation.REPLACE_BY_JAVA_VALUE;
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {
		final WrAnnotationAt annot = (WrAnnotationAt ) metaobjectAnnotation;
		char []charArray = annot.getTextAttachedDSL();
		if ( charArray == null ) {
			addError("Metaobject 'javacode' should take Java code between two sequences of symbols");
		}
		return null;
	}

}
