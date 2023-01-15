package meta.cyanLang;

import java.util.Set;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT_GT;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.ICheckMessageSend_afterSemAn;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.Tuple4;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrMessageWithKeywords;
import meta.WrMethodSignature;
import meta.WrPrototype;
import meta.WrSymbol;

public class CyanMetaobjectOnMessageSend_afterSemAn extends CyanMetaobjectAtAnnot
			implements ICheckMessageSend_afterSemAn, IInterpreterMethods_afterResTypes {

	public CyanMetaobjectOnMessageSend_afterSemAn() {
		super("onMessageSend_afterSemAn", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC, AttachedDeclarationKind.METHOD_SIGNATURE_DEC
		});
	}


//	@Override
//	public void parsing_parse(ICompiler_parsing cp) {
//
//		cp.next();
//		Tuple2<String, InterpreterPrototype> t = MetaHelper.parseCyanCode(cp);
//		if ( t == null ) {
//			cp.error(this.getAnnotation().getFirstSymbol(),
//					"Internal error in metaobject '" + this.getClass().getName() + "'");
//		}
//		else {
//			if ( t.f1 != null ) {
//				cp.error(this.getAnnotation().getFirstSymbol(), t.f1);
//			}
//			else {
//				interpreterPrototype = t.f2;
//				for ( String key : interpreterPrototype.mapMethodName_Body.keySet() ) {
//					switch ( key ) {
//					case "afterSemAn_checkUnaryMessageSend":
//					case "afterSemAn_checkUnaryMessageSendMostSpecific":
//					case "afterSemAn_checkKeywordMessageSend":
//					case "afterSemAn_checkKeywordMessageSendMostSpecific":
//					case "afterResTypes_semAn_afterSemAn_shareInfoPrototype":
//					case "afterResTypes_semAn_afterSemAn_receiveInfoPrototype":
//						break;
//					default:
//						this.addError("Unidentified method name: '" + key + "'");
//						interpreterPrototype = null;
//						return ;
//					}
//				}
//
//
//			}
//		}
//
//	}


	@Override
	public Object afterResTypes_semAn_afterSemAn_shareInfoPrototype(WrEnv env) {

		return MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afterResTypes_semAn_afterSemAn_shareInfoPrototype",
				   // "env" is added by the called method
				new String [] { }, new Object [] { },
				null, null);
		}

	@Override
	public void afterResTypes_semAn_afterSemAn_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet, WrEnv env) {


		_Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT_GT tupleSet =
				MetaHelper.cyanSetTupleStringIntIntDyn_toJava(annotationInfoSet);

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afterResTypes_semAn_afterSemAn_receiveInfoPrototype",
				   // "env" is added by the called method
				new String [] { "annotationInfoSet" }, new Object [] { tupleSet },
				null, null);

	}


	@Override
	public void afterSemAn_checkUnaryMessageSend(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrSymbol unarySymbol, WrEnv env) {


		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afterSemAn_checkUnaryMessageSend",
				   // "env" is added by the called method
				new String [] { "receiverExpr", "receiverType", "receiverKind", "unarySymbol" },
				new Object [] { receiverExpr, receiverType, receiverKind, unarySymbol },
				null, null);


	}

	@Override
	public void afterSemAn_checkUnaryMessageSendMostSpecific(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrSymbol unarySymbol, WrPrototype mostSpecificReceiver, WrEnv env) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afterSemAn_checkUnaryMessageSendMostSpecific",
				   // "env" is added by the called method
				new String [] { "receiverExpr", "receiverType", "receiverKind", "unarySymbol",
						        "mostSpecificReceiver" },
				new Object [] { receiverExpr, receiverType, receiverKind, unarySymbol,
						        mostSpecificReceiver },
				null, null);


	}

	@Override
	public void afterSemAn_checkKeywordMessageSend(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message,
			WrMethodSignature methodSignature, WrEnv env
			) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afterSemAn_checkKeywordMessageSend",
				   // "env" is added by the called method
				new String [] { "receiverExpr", "receiverType", "receiverKind", "message",
						"methodSignature" },
				new Object [] { receiverExpr, receiverType, receiverKind, message, methodSignature },
				null, null);

	}

	@Override
	public void afterSemAn_checkKeywordMessageSendMostSpecific(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message, WrMethodSignature methodSignature,
			WrPrototype mostSpecificReceiver, WrEnv env) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afterSemAn_checkKeywordMessageSendMostSpecific",
				   // "env" is added by the called method
				new String [] { "receiverExpr", "receiverType", "receiverKind", "message",
						"methodSignature", "mostSpecificReceiver" },
				new Object [] { receiverExpr, receiverType, receiverKind, message,
						methodSignature, mostSpecificReceiver },
				null, null);

	}


	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public String[] methodToInterpertList() {
		return new String[] { "afterSemAn_checkUnaryMessageSend", "afterSemAn_checkUnaryMessageSendMostSpecific",
				"afterSemAn_checkKeywordMessageSend", "afterSemAn_checkKeywordMessageSendMostSpecific",
				"afterResTypes_semAn_afterSemAn_shareInfoPrototype", "afterResTypes_semAn_afterSemAn_receiveInfoPrototype"
		};
	}



	@Override
	public CyanMetaobjectAtAnnot getCyanMetaobject() {
		return this;
	}

	@Override
	public InterpreterPrototype getInterpreterPrototype() {
		return interpreterPrototype;
	}

	@Override
	public void setInterpreterPrototype(InterpreterPrototype interpreterPrototype) {
		this.interpreterPrototype = interpreterPrototype;
	}

	InterpreterPrototype interpreterPrototype = null;

}
