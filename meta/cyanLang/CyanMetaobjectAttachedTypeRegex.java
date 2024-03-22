package meta.cyanLang;

import java.util.List;
import ast.AnnotationAt;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAttachedType_semAn;
import meta.ICompiler_semAn;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.WrAnnotationAt;
import meta.WrExpr;
import meta.WrExprLiteralString;
import meta.WrType;
import meta.WrTypeWithAnnotations;


public class CyanMetaobjectAttachedTypeRegex extends CyanMetaobjectAtAnnot implements IActionAttachedType_semAn {

	public CyanMetaobjectAttachedTypeRegex() {
		super("regex", AnnotationArgumentsKind.OneOrMoreParameters, new AttachedDeclarationKind[] { AttachedDeclarationKind.TYPE });
	}


	@Override
	public void check() {
		final List<Object> paramList = this.getAnnotation().getJavaParameterList();
		if ( paramList.size() != 1 ) {
			this.addError("This metaobject should take two elements");
		}
		final Object first = paramList.get(0);
		if ( !(first instanceof String)) {
			addError("The argument to attached type '" + this.getName() + "' should be a regular expression (String)");
		}
		strPattern = MetaHelper.removeQuotes((String ) first);
		pattern = null;
		try {
			pattern = java.util.regex.Pattern.compile(strPattern);
		}
		catch ( final java.util.regex.PatternSyntaxException e ) {
			this.addError("The parameter to this attached metaobject, '" + strPattern + "' is not "
					+ "a valid regular expression. See java.util.regex.Pattern");
		}
	}





	@Override
	public StringBuffer semAn_checkLeftTypeChangeRightExpr(ICompiler_semAn compiler_semAn, WrType leftType, Object leftASTNode,
			LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {

		if ( rightExpr instanceof WrExprLiteralString ) {
			final String rightValue = MetaHelper.removeQuotes((String) ((WrExprLiteralString ) rightExpr).getJavaValue());
			if ( ! pattern.matcher(rightValue).matches() ) {
				this.addError(rightExpr.getFirstSymbol(), "Expression '" + rightValue +
						"' does not match the regular expression '" + strPattern +
						"' that is attached to the type of the left-hand side of the assignment or equivalent");
			}
		}

		else {

			if ( rightType instanceof WrTypeWithAnnotations ) {
				// Type rawRightType = ((TypeWithAnnotations ) rightType).getInsideType();
				for ( final AnnotationAt annot : ((WrTypeWithAnnotations ) rightType).getAnnotationToTypeList() ) {
					if ( annot.getCyanMetaobject() instanceof CyanMetaobjectAttachedTypeRegex) {
						final CyanMetaobjectAttachedTypeRegex mom = (CyanMetaobjectAttachedTypeRegex ) annot.getCyanMetaobject();
						if ( this.strPattern.equals(mom.strPattern) ) {
							return null;
						}

					}
				}
			}


			final StringBuffer sb = new StringBuffer("");
			final String tmpVar = MetaHelper.nextIdentifier();
			final String msg = "In line " + rightExpr.getFirstSymbol().getLineNumber() + " of file '"
					 + CyanMetaobject.escapeString(compiler_semAn.getEnv().getCurrentCompilationUnit().getFullFileNamePath()) +
					 "' String '" + rightExpr.asString() + "' did not match the regular expression '" +
					 CyanMetaobject.escapeString(strPattern) + "'. Its value is '$" + tmpVar + "'";
			sb.append("({ (: String " + tmpVar + " :) \r\n" +
					"              if ! (RegExpr(\"" + strPattern +  "\") ~= " + tmpVar + ") { \r\n" +
					"                  throw ExceptionStr(\"" + msg + "\")\r\n" +
					"              } \r\n" +
					"              ^" + tmpVar + " \r\n" +
					"           } eval: (" + rightExpr.asString() + ")) ");
			return sb;

		}
		return null;
	}


	@Override
	public void checkAnnotation() {
		final WrAnnotationAt annot = this.getAnnotation();
		if ( ! "String".equals(annot.getTypeAttached().getName()) ) {
			this.addError("The metaobject annotation '" + getName() + "' can only be attached to prototype String");
		}
	}



	private String strPattern;
	private java.util.regex.Pattern pattern;
}
