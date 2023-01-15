
package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_cge;
import meta.IAction_dpp;
import meta.ICompilerAction_parsing;
import meta.ICompilerInfo_parsing;
import meta.ICompiler_dpp;
import meta.IDeclaration;
import meta.IDeclarationWritable;
import meta.ReplacementPolicyInGenericInstantiation;
import meta.WrAnnotationAt;
import meta.WrCyanPackage_dpp;
import meta.WrProgram_dpp;

/**
 * metaobject for documenting Cyan code. The metaobject annotation
 * <code><br>
 * {@literal @}doc{* this is a comment *}
 * </code><br>
 * is translated into <br>
 * <code><br>
 * /** this is a comment *{@literal /}
 * </code><br>
 * This is not useful at all. Future versions will help the Cyan compiler to generate
 * correct documentation for Cyan.

   @author jose
 */

public class CyanMetaobjectDoc extends CyanMetaobjectAtAnnot
		implements IAction_cge, ICompilerInfo_parsing, IAction_dpp {

	public CyanMetaobjectDoc() {
		super("doc", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.FIELD_DEC,
						AttachedDeclarationKind.METHOD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC,
						AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.PACKAGE_DEC,
						AttachedDeclarationKind.PROGRAM_DEC });
	}

	@Override
	public boolean shouldTakeText() {
		return true;
	}

	/*
	 * @see ast.CyanMetaobject#semAn_javaCodeThatReplacesAnnotation()()
	 */
	@Override
	public StringBuffer cge_codeToAdd() {
		final StringBuffer sb = new StringBuffer();


		sb.append("    /**");
		sb.append( ((WrAnnotationAt) annotation)
				.getTextAttachedDSL() );
		sb.append("    */");
		return sb;
	}

	@Override
	public ReplacementPolicyInGenericInstantiation getReplacementPolicy() {
		return ReplacementPolicyInGenericInstantiation.NO_REPLACEMENT;
	}


	@Override
	public void action_parsing(ICompilerAction_parsing compiler)	 {
		final WrAnnotationAt annot = this.getAnnotation();
		final IDeclaration dec = annot.getDeclaration();
		String text = new String( annot.getTextAttachedDSL() );
		if ( dec instanceof IDeclarationWritable ) {
			((IDeclarationWritable ) dec).addDocumentText(text, "text", compiler.getEnv());
		}
	}

	@Override
	public void dpp_action(ICompiler_dpp project) {

		String text = new String( ((WrAnnotationAt) annotation)
				.getTextAttachedDSL() );
		IDeclaration dec = this.getAttachedDeclaration();
		if ( dec instanceof WrProgram_dpp ) {
			final WrProgram_dpp program = (WrProgram_dpp ) dec;
			program.addDocumentText(text, "doc");
		}
		else if ( dec instanceof WrCyanPackage_dpp ){
			// a package
			final WrCyanPackage_dpp cp = (WrCyanPackage_dpp ) dec;
			cp.addDocumentText(text, "doc");
		}
		else {
			this.addError("Error in metaobject class '" + this.getClass().getName() + "'");
		}

	}

}
