/**

 */
package meta.cyanLang;

import ast.ExprIdentStar;
import ast.ObjectDec;
import ast.Type;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAssignment_cge;

/** This metaobject changes assignments related to union types. It will become obsolete soon.
   @author José

 */
public class CyanMetaobjectChangeAssignmentToUnion extends CyanMetaobjectAtAnnot implements IActionAssignment_cge {

	public CyanMetaobjectChangeAssignmentToUnion() {
		super("changeAssignmentToUnion", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC });
	}



	/**
	 * if the type of {@code intStr} is {@code Union<Int, String>}, an assignment <br>
	 * {@code intStr = 0}<br>
	 * should be changed in the Java code to<br>
	 * {@code _intStr = _UnionJavaName._assign( new CyInt(0) )}
	 */
	@Override
	public String cge_changeRightHandSideTo(Type leftType, String rightHandSide, Type rightType) {
		ObjectDec proto = (ObjectDec ) leftType;
		boolean isTaggedUnion = false;
		if (proto.getGenericParameterListList().get(0).get(0).getParameter() instanceof ExprIdentStar ) {
			ExprIdentStar eis = (ExprIdentStar ) proto.getGenericParameterListList().get(0).get(0).getParameter();
			if ( eis.getIdentSymbolArray().size() == 1 &&
					Character.isLowerCase(eis.getIdentSymbolArray().get(0).getSymbolString().charAt(0)) ) {
				// a tagged union: error
				isTaggedUnion = true;
			}
		}


		if ( leftType == rightType || isTaggedUnion )
			return rightHandSide;
		else
			return leftType.getJavaName() + ".assign(" + rightHandSide + ")";
	}
}
