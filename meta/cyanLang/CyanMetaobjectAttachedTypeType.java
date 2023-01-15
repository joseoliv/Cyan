package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import ast.AnnotationAt;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAttachedType_semAn;
import meta.ICompiler_semAn;
import meta.IVariableDecInterface;
import meta.IdentStarKind;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrExpr;
import meta.WrExprIdentStar;
import meta.WrExprLiteral;
import meta.WrLocalVarInfo;
import meta.WrStatementLocalVariableDec;
import meta.WrType;
import meta.WrTypeWithAnnotations;

/**
 * this metaobject class is for annotations attached to types. It worked much like a language-C typedef
 * but without the explicit declaration. Every type  T{@literal @}type(id)  is compatible only with literals
 * and types annotated with {@literal @}type(id). Example:<br>
 * <code><br>
 *     var Int{@literal @}type(inBytes) size = 100;<br>
 *     var Int{@literal @}type(inBytes) otherSize = size;<br>
 *     var Int{@literal @}type(inKbytes) sizeKb = size; // error<br>
 *     var Int n = size; // ok<br>
 *     size = n; // error<br>
 *     size = sizeKb; // error<br>
 * </code><br>
   @author jose
 */
public class CyanMetaobjectAttachedTypeType extends CyanMetaobjectAtAnnot implements IActionAttachedType_semAn {

	public CyanMetaobjectAttachedTypeType() {
		super("type", AnnotationArgumentsKind.OneParameter, new AttachedDeclarationKind[] { AttachedDeclarationKind.TYPE });
	}


	@Override
	public void check() {
		List<Object> paramList = this.getAnnotation().getJavaParameterList();
		Object first = paramList.get(0);
		if ( !(first instanceof String)) {
			this.addError("The argument to attached type '" + this.getName() + "' should be an identifier or string");
		}
		typeDefinition = MetaHelper.removeQuotes((String ) first);
	}





	@Override
	public StringBuffer semAn_checkLeftTypeChangeRightExpr(ICompiler_semAn compiler_semAn, WrType leftType, Object leftASTNode,
			LeftHandSideKind leftKind,
			WrType rightType, WrExpr rightExpr) {


		if ( rightExpr instanceof WrExprIdentStar ) {
			WrExprIdentStar eis = (WrExprIdentStar ) rightExpr;
			if ( eis.getIdentStarKind() == IdentStarKind.variable_t ) {
				IVariableDecInterface aVar = eis.getVarDeclaration();
				WrStatementLocalVariableDec varDec = (WrStatementLocalVariableDec ) aVar;
				WrLocalVarInfo varInfo = compiler_semAn.getEnv().getLocalVariableInfo(varDec);
				if ( ! varInfo.getInitializedWithNonLiteral() ) {
					return null;
				}
			}
		}

		if ( !(rightExpr instanceof WrExprLiteral) ) {
			// literals in the right-hand side are always allowed
			if ( rightType instanceof WrTypeWithAnnotations ) {
				// Type rawRightType = ((TypeWithAnnotations ) rightType).getInsideType();
				for ( AnnotationAt annot : ((WrTypeWithAnnotations ) rightType).getAnnotationToTypeList() ) {
					if ( annot.getCyanMetaobject() instanceof CyanMetaobjectAttachedTypeType) {
						CyanMetaobjectAttachedTypeType otherMO = (CyanMetaobjectAttachedTypeType ) annot.getCyanMetaobject();
						if ( this.typeDefinition.equals(otherMO.typeDefinition) ) {
							return null;
						}
					}
				}
			}
			this.addError(rightExpr.getFirstSymbol(), "The left-hand side is annotated with @type(" + this.typeDefinition +
					"). The right-hand side should be either a literal ('a', 0, literal array, map, tuple) "
					+ "or a value that is also annotated with @type(" + this.typeDefinition + ")");
		}
		return null;
	}


	@Override
	public List<Tuple2<String, String>> doNotCheckIn() {


		List<Tuple2<String, String>> list = new ArrayList<>();
		list.add( new Tuple2<String, String>("untainted", "TaintedToUntainted"));
		return list;
	}

	private String typeDefinition;
}
