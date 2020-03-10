/**

 */
package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.ICheckMessageSend_afsa;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrMessageWithKeywords;
import meta.WrMethodSignature;
import meta.WrProgramUnit;
import meta.WrType;
import saci.TupleTwo;

/** checks whether the parameter to the method "isA: Any -> Boolean" of  Any
 * is correct. It should be a prototype.
 *
   @author José
 */
public class CyanMetaobjectCheckIsA extends CyanMetaobjectAtAnnot
    implements ICheckMessageSend_afsa {

	public CyanMetaobjectCheckIsA() {
		super("checkIsA", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC } );
	}



	@Override
	public void afsa_checkKeywordMessageSend(WrExpr receiverExpr, WrProgramUnit receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message, WrMethodSignature ms, WrEnv env) {

		WrExpr paramExpr = message.getkeywordParameterList().get(0).getExprList().get(0);
		TupleTwo<String, WrType> t = paramExpr.ifPrototypeReturnsNameWithPackageAndType(env);
		if ( t == null || t.f2 == null ) {
			addError(paramExpr.getFirstSymbol(), "The parameter to this message send should be a prototype");
		}
	}


}
