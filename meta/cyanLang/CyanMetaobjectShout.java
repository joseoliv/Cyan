package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_semAn;
import meta.ICompiler_semAn;
import meta.WrASTVisitor;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrExprLiteralString;
import meta.WrMethodDec;

public class CyanMetaobjectShout extends CyanMetaobjectAtAnnot implements IAction_semAn /*, ICheckDeclaration_afterSemAn */ {

	public CyanMetaobjectShout() {
		super("shout", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.METHOD_DEC });
	}


	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		final WrAnnotationAt annot = this.getAnnotation();
		final WrMethodDec dec = (WrMethodDec ) annot.getDeclaration();

		dec.accept( new WrASTVisitor() {
			@Override
			public void visit(WrExprLiteralString node, WrEnv env) {
				final StringBuffer strUpper = new StringBuffer();
				final StringBuffer str = node.getStringJavaValue();
				for (int i = 0; i < str.length(); ++i) {
					strUpper.append(Character.toUpperCase(str.charAt(i)));
				}
				replaceStatementByCode(node,
						strUpper, node.getType(), env);
			}
		}, compiler_semAn.getEnv());

		return null;
	}
}