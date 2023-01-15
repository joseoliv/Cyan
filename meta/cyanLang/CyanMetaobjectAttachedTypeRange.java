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
import meta.WrExprLiteralByte;
import meta.WrExprLiteralChar;
import meta.WrExprLiteralInt;
import meta.WrExprLiteralLong;
import meta.WrExprLiteralShort;
import meta.WrType;
import meta.WrTypeWithAnnotations;

public class CyanMetaobjectAttachedTypeRange
		extends CyanMetaobjectAtAnnot implements IActionAttachedType_semAn {

	public CyanMetaobjectAttachedTypeRange() {
		super("range", AnnotationArgumentsKind.OneOrMoreParameters, new AttachedDeclarationKind[] { AttachedDeclarationKind.TYPE });
	}


	@Override public void check() {
		final List<Object> paramList = this.getAnnotation().getJavaParameterList();
		if ( paramList.size() != 2 ) {
			this.addError("This metaobject should take two elements");
		}
		first = paramList.get(0);
		last = paramList.get(1);
		if ( first.getClass() != last.getClass() ) {
			this.addError("The two limits should be of the same type");
		}
		final Class<?> classLimit = first.getClass();
		String name = classLimit.getName();
		if ( name.startsWith("java.lang") ) name = name.substring("java.lang".length()+1);
		switch ( name ) {
		case "Byte" : typeNameLimits = "Byte"; break;
		case "Integer" : typeNameLimits = "Int"; break;
		case "Short" : typeNameLimits = "Short"; break;
		case "Long" : typeNameLimits = "Long"; break;
		case "Character" : typeNameLimits = "Char"; break;
		default:
			this.addError("The range limits should be of one of the following types: Int, Char, Byte, Short, Long");
		}
	}

	@Override
	public void checkAnnotation() {
		final WrAnnotationAt annot = this.getAnnotation();
		if ( ! typeNameLimits.equals(annot.getTypeAttached().getName()) ) {
			this.addError("The type of the limits is '" + this.typeNameLimits +
					"' and the type itself is " + annot.getTypeAttached().getName());
		}
	}

	@Override
	public StringBuffer semAn_checkLeftTypeChangeRightExpr(ICompiler_semAn compiler_semAn, WrType leftType, Object leftASTNode,
			LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {


		if ( rightExpr instanceof WrExprLiteralByte ) {
			final byte rightValue = (Byte ) ((WrExprLiteralByte ) rightExpr).getJavaValue();
			if ( rightValue < (Byte ) first || rightValue > (Byte ) last ) {
				this.addError(rightExpr.getFirstSymbol(), "The value '" + rightValue + "' "
						+ "is outside the range allowed for the type: " + first + ".." + last);
			}
		}
		else if ( rightExpr instanceof WrExprLiteralShort ) {
				final short rightValue = (Short ) ((WrExprLiteralShort ) rightExpr).getJavaValue();
				if ( rightValue < (Short ) first || rightValue > (Short ) last ) {
					this.addError(rightExpr.getFirstSymbol(), "The value '" + rightValue + "' "
							+ "is outside the range allowed for the type: " + first + ".." + last);
				}
			}
		else if ( rightExpr instanceof WrExprLiteralInt ) {
			final int rightValue = (Integer ) ((WrExprLiteralInt ) rightExpr).getJavaValue();
			if ( rightValue < (Integer ) first || rightValue > (Integer ) last ) {
				this.addError(rightExpr.getFirstSymbol(), "The value '" + rightValue + "' "
						+ "is outside the range allowed for the type: " + first + ".." + last);
			}
		}
		else if ( rightExpr instanceof WrExprLiteralLong ) {
			final long rightValue = (Long ) ((WrExprLiteralLong ) rightExpr).getJavaValue();
			if ( rightValue < (Long ) first || rightValue > (Long ) last ) {
				this.addError(rightExpr.getFirstSymbol(), "The value '" + rightValue + "' "
						+ "is outside the range allowed for the type: " + first + ".." + last);
			}
		}
		else if ( rightExpr instanceof WrExprLiteralChar ) {
			final char rightValue = (Character ) ((WrExprLiteralChar ) rightExpr).getJavaValue();
			if ( rightValue < (Character ) first || rightValue > (Character ) last ) {
				this.addError(rightExpr.getFirstSymbol(), "The value '" + rightValue + "' "
						+ "is outside the range allowed for the type: " + first + ".." + last);
			}
		}
		else {
			boolean foundRangeAnnotation = false;

			if ( rightType instanceof WrTypeWithAnnotations ) {
				// Type rawRightType = ((TypeWithAnnotations ) rightType).getInsideType();
				for ( final AnnotationAt annot : ((WrTypeWithAnnotations ) rightType).getAnnotationToTypeList() ) {
					if ( annot.getCyanMetaobject() instanceof CyanMetaobjectAttachedTypeRange ) {
						foundRangeAnnotation = true;
						final CyanMetaobjectAttachedTypeRange typeRange = (CyanMetaobjectAttachedTypeRange ) annot.getCyanMetaobject();
						final Object otherFirst = typeRange.first;
						final Object otherLast  = typeRange.last;

						final Class<?> classLimit = first.getClass();
						String name = classLimit.getName();
						if ( name.startsWith("java.lang") ) name = name.substring("java.lang".length()+1);
						boolean er = false;
						switch ( name ) {
						case "Byte" :
							if ( (Byte ) otherFirst < (Byte) first || (Byte ) otherLast > (Byte) last ) {
								er = true;
							}
							break;
						case "Integer" :
							if ( (Integer ) otherFirst < (Integer) first || (Integer ) otherLast > (Integer) last ) {
								er = true;
							}
							break;

						case "Short" :
							if ( (Short ) otherFirst < (Short) first || (Short ) otherLast > (Short) last ) {
								er = true;
							}
							break;

						case "Long" :
							if ( (Long ) otherFirst < (Long) first || (Long ) otherLast > (Long) last ) {
								er = true;
							}
							break;

						case "Character" :
							if ( (Character ) otherFirst < (Character) first || (Character ) otherLast > (Character) last ) {
								er = true;
							}
							break;
						}
						if ( er ) {
							this.addError(rightExpr.getFirstSymbol(), "The runtime value of the expression " + rightExpr.asString()
									+ " may be outside the range allowed: " + first + ".." + last);

						}
					}
				}
			}

			if ( ! foundRangeAnnotation ) {
				final StringBuffer sb = new StringBuffer("");
				final String tmpVar = MetaHelper.nextIdentifier();
				String firstStr = first.toString();
				String lastStr = last.toString();
				if ( this.typeNameLimits.equals("Char") ) {
					firstStr = "'" + first + "'";
					lastStr = "'" + last + "'";
				}

				final String msg = "In line " + rightExpr.getFirstSymbol().getLineNumber() + " of file '"
						 + CyanMetaobject.escapeString(compiler_semAn.getEnv().getCurrentCompilationUnit().getFullFileNamePath()) +
						 "' expression '" + rightExpr.asString() + "' should be between " + firstStr + " and " + lastStr +
						 " Its value is $" + tmpVar;
				sb.append("({ (: " + typeNameLimits + " " + tmpVar + " :) \r\n" +
						"              if " + tmpVar + " < " + firstStr + " || " + tmpVar + " > " + lastStr + " { \r\n" +
						"                  throw: ExceptionStr(\"" + msg + "\")\r\n" +
						"              } \r\n" +
						"              ^" + tmpVar + " \r\n" +
						"           } eval: (" + rightExpr.asString() + ")) ");
				return sb;
			}

		}
		return null;
	}




	private Object first, last;
	private String typeNameLimits;

}
