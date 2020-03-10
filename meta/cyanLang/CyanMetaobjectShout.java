package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dsa;
import meta.ICompiler_dsa;
import meta.WrASTVisitor;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrExprLiteralString;
import meta.WrMethodDec;

public class CyanMetaobjectShout extends CyanMetaobjectAtAnnot implements IAction_dsa /*, ICheckDeclaration_afsa */ {

	public CyanMetaobjectShout() {
		super("shout", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.METHOD_DEC });
	}


	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {
		final WrAnnotationAt annot = this.getMetaobjectAnnotation();
		final WrMethodDec dec = (WrMethodDec ) annot.getDeclaration();

		dec.accept( new WrASTVisitor() {
			@Override
			public void visit(WrExprLiteralString node, WrEnv env) {
				final StringBuffer strUpper = new StringBuffer();
				final StringBuffer str = node.getStringJavaValue();
				for (int i = 0; i < str.length(); ++i) {
					strUpper.append(Character.toUpperCase(str.charAt(i)));
				}
				compiler_dsa.replaceStatementByCode(node, annot,
						strUpper, node.getType());
			}
		}, compiler_dsa.getEnv());

		return null;
	}
}