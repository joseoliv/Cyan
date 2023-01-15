package meta.cyanLang;

import ast.AnnotationAt;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAttachedType_semAn;
import meta.ICompiler_semAn;
import meta.LeftHandSideKind;
import meta.WrExpr;
import meta.WrType;
import meta.WrTypeWithAnnotations;

public class CyanMetaobjectSecretValue
       extends CyanMetaobjectAtAnnot implements IActionAttachedType_semAn {

	public CyanMetaobjectSecretValue() {
		super("secretValue",
				AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.TYPE });
	}



	@Override
	public
	StringBuffer semAn_checkRightTypeChangeRightExpr(ICompiler_semAn compiler_semAn,
			WrType leftType, Object leftASTNode, LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {
		if ( leftType instanceof WrTypeWithAnnotations ) {
			WrTypeWithAnnotations typeAnnot = (WrTypeWithAnnotations ) leftType;
			for ( AnnotationAt annot : typeAnnot.getAnnotationToTypeList() ) {
				if ( annot.getCompleteName().equals(this.getAnnotation().getCompleteName()) ) {
					return null;
				}
			}
		}
		// left type has not an attached annotation 'secretValue'
		this.addError(rightExpr.getFirstSymbol(),
				"The object on the right-hand side should be assigned to a variable/field/parameter/return value whose type is annotated with @secretValue");
		return null;
	}

}
