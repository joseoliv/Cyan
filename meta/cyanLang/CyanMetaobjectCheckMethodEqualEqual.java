package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrMessageKeywordWithRealParameters;
import meta.WrMessageWithKeywords;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrMethodSignatureOperator;
import meta.WrPrototype;
import meta.WrType;

public class CyanMetaobjectCheckMethodEqualEqual extends CyanMetaobjectAtAnnot
    implements meta.ICheckMessageSend_afterSemAn {

	public CyanMetaobjectCheckMethodEqualEqual() {
		super("checkMethodEqualEqual", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC } );
	}


	@Override
	public void afterSemAn_checkKeywordMessageSend(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message, WrMethodSignature ms, WrEnv env
			) {
		//MethodSignature ms = ( (MethodDec ) this.getAnnotation().getDeclaration() )
			//	.getMethodSignature();
		WrMethodDec method = (WrMethodDec ) this.getAnnotation().getDeclaration();
		boolean equalEqEq = method.getName().equals("==1");
		if ( !(method.getMethodSignature() instanceof WrMethodSignatureOperator) || (
				! equalEqEq && !method.getName().equals("!=1")) ) {
			this.addError("This metaobject can only be attached to method '== Dyn other -> Boolean' or '!= Dyn other -> Boolean'");
		}
		//MethodSignatureWithKeywords mss = (MethodSignatureWithKeywords ) ms;

		List<WrMessageKeywordWithRealParameters> selList = message.getkeywordParameterList();
		WrType otherType = selList.get(0).getExprList().get(0).getType();

		if ( otherType instanceof WrPrototype  && ! ((WrPrototype) otherType).isInterface() ) {
			if ( ! receiverType.isInterface() ) {
				if ( !receiverType.isSupertypeOf(otherType, env) && !otherType.isSupertypeOf(receiverType, env) ) {
					this.addError(receiverExpr.getFirstSymbol(), "Illegal use of method '" + (equalEqEq ? "==" : "!=") +"' because it will always return '"
							+ !equalEqEq + "'");
				}
			}
			else if ( receiverType.isInterface() ) {
				equalToInterfaceTest(receiverExpr, equalEqEq, (WrPrototype ) otherType, env);
			}
		}
		else if ( !receiverType.isInterface() ) {
			if ( otherType instanceof WrPrototype && ((WrPrototype) otherType).isInterface() ) {
				equalToInterfaceTest(receiverExpr, equalEqEq, receiverType, env);
			}
		}
	}

	/**
	   @param receiverExpr
	   @param equalEqEq
	   @param otherType
	 */
	private void equalToInterfaceTest(WrExpr receiverExpr, boolean equalEqEq,
			WrPrototype otherType, WrEnv env) {
		if ( otherType.getIsFinal(env) ) {
			boolean implementsInterface = false;
			WrPrototype proto = otherType;
			while ( proto != null ) {
				List<WrPrototype> interExprList = proto.getInterfaceList(env);
				if ( interExprList != null && interExprList.size() != 0 ) {
					implementsInterface = true;
					break;
				}
				proto = proto.getSuperobject(env);
			}
			if ( ! implementsInterface ) {
				this.addError(receiverExpr.getFirstSymbol(),
						"Illegal use of method '==' because it will always return '"
						+ !equalEqEq + "'");
			}
		}
	}

}
