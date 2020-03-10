package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAttachedType_dsa;
import meta.ICompiler_dpa;
import meta.ICompiler_dsa;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Token;
import meta.WrAnnotationAt;
import meta.WrExpr;
import meta.WrType;

public class CyanMetaobjectAttachedTypeRestrictTo extends CyanMetaobjectAtAnnot
       implements meta.IParseWithCyanCompiler_dpa, IActionAttachedType_dsa  {

	public CyanMetaobjectAttachedTypeRestrictTo() {
		super("restrictTo", AnnotationArgumentsKind.ZeroParameters);
	}



	@Override
	public void checkAnnotation() {
		/*
		AnnotationAt annot = this.getMetaobjectAnnotation();
		if ( ! "Int".equals(annot.getTypeAttached().getName()) ) {
			this.addError("The type of the limits is '" + this.typeNameLimits + "' and the type itself is " + annot.getTypeAttached().getName());
		}
		*/
	}

	@Override
	public StringBuffer dsa_checkLeftTypeChangeRightExpr(ICompiler_dsa compiler_dsa, WrType leftType, Object leftASTNode,
			LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {

		/*
		 *    var Int@restrictTo{* self >= 0 && self <= 12 *} n;
		 *    n = k;
		 *
		 *    n = { Int tmp = k; if  !(Text with self replaced by tmp)  { throw: ExceptionStr() } ^tmp } eval
		 */

		final WrAnnotationAt annot = this.getMetaobjectAnnotation();
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
				+ CyanMetaobject.escapeString(compiler_dsa.getEnv().getCurrentCompilationUnit().getFullFileNamePath()) +
				"' expression '" + rightExpr.asString() + "' should obey the restriction '" + new String(charText) +
				"'.  The expression value is $" + tmpVar;
		sb.append("{ let " + attachedTypeName + " " + tmpVar + " = " + rightExpr.asString() + "; \r\n" +
				"              if !(" + text + ") { \r\n" +
				"                  throw: ExceptionStr(\"" + msg + "\")\r\n" +
				"              } \r\n" +
				"              ^" + tmpVar + " \r\n" +
				"           } eval ");

		return sb;
	}

	@Override
	public boolean shouldTakeText() { return true; }



	@Override
	public void dpa_parse(ICompiler_dpa compiler) {
		compiler.next();
		compiler.expr();
		if ( compiler.getSymbol().token != Token.EOLO ) {
			this.addError(compiler.getSymbol(), "This DSL should be composed of a single expression");
		}

	}


}
