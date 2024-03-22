
package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.WrASTVisitor;
import meta.WrEnv;
import meta.WrParameterDec;
import meta.WrPrototype;
import meta.WrStatementLocalVariableDec;

public class CyanMetaobjectFirstOther extends CyanMetaobjectAtAnnot
		implements IAction_afterResTypes, ICheckDeclaration_afterSemAn {

	public CyanMetaobjectFirstOther() {
		super("firstOther", AnnotationArgumentsKind.ZeroOrMoreParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.PROTOTYPE_DEC });
	}

	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {
		IDeclaration attachedDec = this.getAttachedDeclaration();
		final ICompiler_semAn compilerFinal = compiler;
		WrPrototype p = (WrPrototype) attachedDec;
		p = p.getSuperobject(compiler.getEnv());

		p.accept(new WrASTVisitor() {
			@Override
			public void visit(WrStatementLocalVariableDec node, WrEnv env) {
				if ( !node.getName().endsWith("__") ) {
					System.out.println("Found '" + node.getName() + "'");
				}
				WrPrototype p = (WrPrototype) node.getType();
				p.getFieldList(compilerFinal.getEnv());
			}

			@Override
			public void visit(WrParameterDec node, WrEnv env) {
				if ( !node.getName().endsWith("__") ) {
					System.out.println("Found '" + node.getName() + "'");
				}

			}
		}, compiler.getEnv());

	}
}
