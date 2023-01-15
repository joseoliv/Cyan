package meta.cyanLang;

import ast.AnnotationAt;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAttachedType_semAn;
import meta.ICompiler_semAn;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.WrExpr;
import meta.WrExprLiteralChar;
import meta.WrType;
import meta.WrTypeWithAnnotations;

public class CyanMetaobjectAttachedTypeLetter extends CyanMetaobjectAtAnnot implements IActionAttachedType_semAn {

	public CyanMetaobjectAttachedTypeLetter() {
		super("letter", AnnotationArgumentsKind.ZeroParameters, new AttachedDeclarationKind[] { AttachedDeclarationKind.TYPE });
	}

	@Override
	public StringBuffer semAn_checkLeftTypeChangeRightExpr(ICompiler_semAn compiler_semAn, WrType leftType, Object leftASTNode,
			LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {

		/*
		if ( rightExpr instanceof ast.ExprIdentStar ) {
			ast.ExprIdentStar eis = (ast.ExprIdentStar ) rightExpr;
			VariableDecInterface avar = eis.getVarDeclaration();
			avar.get
		}
		*/

		if ( rightExpr instanceof WrExprLiteralChar ) {
			final char rightValue = (Character ) ((WrExprLiteralChar ) rightExpr).getJavaValue();
			if ( ! Character.isLetter(rightValue) ) {
				this.addError(rightExpr.getFirstSymbol(), "The value '" + rightValue +
						"' should be a letter because it is being assigned to a "
						+ "variable/parameter/return value that has an attached annotation @letter ");
			}
		}
		else {
			boolean foundLetterAnnotation = false;

			if ( rightType instanceof WrTypeWithAnnotations ) {
				// Type rawRightType = ((TypeWithAnnotations ) rightType).getInsideType();
				for ( final AnnotationAt annot : ((WrTypeWithAnnotations ) rightType).getAnnotationToTypeList() ) {
					if ( annot.getCyanMetaobject() instanceof CyanMetaobjectAttachedTypeLetter ) {
						foundLetterAnnotation = true;
					}
				}
			}

			if ( ! foundLetterAnnotation ) {
				final StringBuffer sb = new StringBuffer("");
				final String tmpVar = MetaHelper.nextIdentifier();
				final String msg = "In line " + rightExpr.getFirstSymbol().getLineNumber() + " of file '"
						 + CyanMetaobject.escapeString(
								 compiler_semAn.getEnv().getCurrentCompilationUnit().getFullFileNamePath()) +
						 "' expression '" + rightExpr.asString() + "' should be a letter";
				sb.append("         { let Char " + tmpVar + " = "  + rightExpr.asString() + "; \r\n" +
						"              if ! (" + tmpVar + " isLetter) { \r\n" +
						"                  throw: ExceptionStr(\"" + msg + "\")\r\n" +
						"              } \r\n" +
						"              ^" + tmpVar + " \r\n" +
						"           } eval ");
				return sb;
			}

		}
		return null;
	}

}
