package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAttachedType_semAn;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Token;
import meta.WrAnnotationAt;
import meta.WrExpr;
import meta.WrType;

public class CyanMetaobjectAttachedTypeRestrictTo extends CyanMetaobjectAtAnnot
       implements meta.IParseWithCyanCompiler_parsing, IActionAttachedType_semAn  {

	public CyanMetaobjectAttachedTypeRestrictTo() {
		super("restrictTo", AnnotationArgumentsKind.ZeroParameters, new AttachedDeclarationKind[] { AttachedDeclarationKind.TYPE });
	}



	@Override
	public void checkAnnotation() {
		/*
		AnnotationAt annot = this.getAnnotation();
		if ( ! "Int".equals(annot.getTypeAttached().getName()) ) {
			this.addError("The type of the limits is '" + this.typeNameLimits + "' and the type itself is " + annot.getTypeAttached().getName());
		}
		*/
	}

	@Override
	public StringBuffer semAn_checkLeftTypeChangeRightExpr(ICompiler_semAn compiler_semAn, WrType leftType, Object leftASTNode,
			LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {

		/*
		 *    var Int@restrictTo{* self >= 0 && self <= 12 *} n;
		 *    n = k;
		 *
		 *    n = { Int tmp = k; if  !(Text with self replaced by tmp)  { throw ExceptionStr() } ^tmp } eval
		 */

		final WrAnnotationAt annot = this.getAnnotation();
		final char []charText = annot.getTextAttachedDSL();
		String text = new String(charText);

		final StringBuffer sb = new StringBuffer("");
		final String tmpVar = MetaHelper.nextIdentifier();

		text.replace("self", tmpVar);
		while ( true ) {
			final int index = text.indexOf("self");
			if ( index < 0 ) {
				break;
			}
			String newText = text.substring(0, index);
			newText = newText + tmpVar + text.substring(index + 4);
			text = newText;
		}

		final String attachedTypeName = annot.getTypeAttached().getName();

		final String msg = "In line " + rightExpr.getFirstSymbol().getLineNumber() + " of file '"
				+ CyanMetaobject.escapeString(compiler_semAn.getEnv().getCurrentCompilationUnit().getFullFileNamePath()) +
				"' expression '" + escapeString(rightExpr.asString()) + "' should obey the restriction '" + new String(charText) +
				"'.  The expression value is $" + escapeString(tmpVar);
		sb.append("{ let " + attachedTypeName + " " + tmpVar + " = " + rightExpr.asString() + "; \r\n" +
				"              if !(" + text + ") { \r\n" +
				"                  throw ExceptionStr(\"" + msg + "\")\r\n" +
				"              } \r\n" +
				"              ^" + tmpVar + " \r\n" +
				"           } eval ");

		return sb;
	}

	@Override
	public boolean shouldTakeText() { return true; }



	@Override
	public void parsing_parse(ICompiler_parsing compiler) {
		compiler.next();
		compiler.expr();
		if ( compiler.getSymbol().token != Token.EOLO ) {
			this.addError(compiler.getSymbol(), "This DSL should be composed of a single expression");
		}

	}


}
