/**

 */
package meta.cyanLang;


import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_cge;
import meta.IAction_semAn;
import meta.ICompiler_semAn;
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
             implements IAction_cge, IAction_semAn {

	public CyanMetaobjectJavaCode() {
		super("javacode", AnnotationArgumentsKind.ZeroParameters, null);
	}

	@Override
	public boolean shouldTakeText() { return true; }


	/* @see ast.CyanMetaobject#semAn_javaCodeThatReplacesAnnotation()()
	 */
	@Override
	public StringBuffer cge_codeToAdd() {

		final WrAnnotationAt annot = (WrAnnotationAt ) annotation;
		final StringBuffer sb = new StringBuffer();

		//char []text = ;
		String text = new String(annot.getTextAttachedDSL());
		text = text.replace("THISPROTOTYPE999", this.getCurrentPrototype().getJavaName());

		sb.append(text);
		return sb;
	}

	@Override
	public ReplacementPolicyInGenericInstantiation getReplacementPolicy() {
		return ReplacementPolicyInGenericInstantiation.REPLACE_BY_JAVA_VALUE;
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		final WrAnnotationAt annot = (WrAnnotationAt ) annotation;
		char []charArray = annot.getTextAttachedDSL();
		if ( charArray == null ) {
			addError("Metaobject 'javacode' should take Java code between two sequences of symbols");
		}
		return null;
	}

}
