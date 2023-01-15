package meta.cyanLang;

import java.util.List;
import ast.AnnotationAt;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAttachedType_semAn;
import meta.ICompiler_semAn;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrExpr;
import meta.WrType;
import meta.WrTypeWithAnnotations;

public class CyanMetaobjectAttachedTypeUntainted extends CyanMetaobjectAtAnnot implements IActionAttachedType_semAn {

	public CyanMetaobjectAttachedTypeUntainted() {
		super("untainted", AnnotationArgumentsKind.OneParameter, new AttachedDeclarationKind[] { AttachedDeclarationKind.TYPE });
	}


	@Override public void check() {
		List<Object> paramList = this.getAnnotation().getJavaParameterList();
		if ( !(paramList.get(0) instanceof String ) ) {
			this.addError("The sole parameter should be a string or identifier");
		}
	}


	@Override
	public StringBuffer semAn_checkLeftTypeChangeRightExpr(ICompiler_semAn compiler_semAn, WrType leftType, Object leftASTNode,
			LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {


		boolean foundUntainted = false;

		if ( rightType instanceof WrTypeWithAnnotations ) {
			// Type rawRightType = ((TypeWithAnnotations ) rightType).getInsideType();
			for ( AnnotationAt annot : ((WrTypeWithAnnotations ) rightType).getAnnotationToTypeList() ) {
				if ( annot.getCyanMetaobject() instanceof CyanMetaobjectAttachedTypeUntainted ) {
					// CyanMetaobjectAttachedTypeUntainted other = (CyanMetaobjectAttachedTypeUntainted ) annot.getCyanMetaobject();
					String thisParam = MetaHelper.removeQuotes( (String ) this.getAnnotation().getJavaParameterList().get(0) );
					String otherParam = MetaHelper.removeQuotes( (String ) annot.getJavaParameterList().get(0) );
					foundUntainted = thisParam.equals(otherParam);
				}
			}
		}

		if ( ! foundUntainted ) {
			List<Tuple2<String, String>> tupleArray = this.doNotCheckIn();
			String all = "";
			if ( tupleArray != null ) {
				int size = tupleArray.size();
				for ( Tuple2<String, String> t : tupleArray ) {
					all += t.f1 + "." + t.f2;
					if ( --size > 0 ) {
						all += ", ";
					}
				}
			}
			this.addError(rightExpr.getFirstSymbol(), "Illegal value transfer: the expression '" +
					rightExpr.asString() + "' has type " + rightType.getNameWithAttachedTypes() + " and it is being assigned/cast "
							+ "to a value of type " + leftType.getNameWithAttachedTypes() + ". The later is not a supertype of the former"
							+ (all.length() != 0 ? ". Use the methods of the following prototypes to do the conversion between the types, if possible: " + all
									: ""));
		}

		return null;
	}


	@Override
	public boolean allowDoNotCheckInList() { return true; }
}
