package meta.cyanLang;

import java.util.Set;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT__GT;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.ICheckMessageSend_afsa;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.Tuple4;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrMessageWithKeywords;
import meta.WrMethodSignature;
import meta.WrProgramUnit;
import meta.WrSymbol;

public class CyanMetaobjectOnMessageSend_afsa extends CyanMetaobjectAtAnnot
			implements ICheckMessageSend_afsa, IInterpreterMethods_afti {

	public CyanMetaobjectOnMessageSend_afsa() {
		super("onMessageSend_afsa", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC, AttachedDeclarationKind.METHOD_SIGNATURE_DEC
		});
	}


//	@Override
//	public void dpa_parse(ICompiler_dpa cp) {
//
//		cp.next();
//		Tuple2<String, InterpreterPrototype> t = MetaHelper.parseCyanCode(cp);
//		if ( t == null ) {
//			cp.error(this.getMetaobjectAnnotation().getFirstSymbol(),
//					"Internal error in metaobject '" + this.getClass().getName() + "'");
//		}
//		else {
//			if ( t.f1 != null ) {
//				cp.error(this.getMetaobjectAnnotation().getFirstSymbol(), t.f1);
//			}
//			else {
//				interpreterPrototype = t.f2;
//				for ( String key : interpreterPrototype.mapMethodName_Body.keySet() ) {
//					switch ( key ) {
//					case "afsa_checkUnaryMessageSend":
//					case "afsa_checkUnaryMessageSendMostSpecific":
//					case "afsa_checkKeywordMessageSend":
//					case "afsa_checkKeywordMessageSendMostSpecific":
//					case "afti_dsa_afsa_shareInfoPrototype":
//					case "afti_dsa_afsa_receiveInfoPrototype":
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
	public Object afti_dsa_afsa_shareInfoPrototype(WrEnv env) {

		return MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afti_dsa_afsa_shareInfoPrototype",
				   // "env" is added by the called method
				new String [] { }, new Object [] { },
				null, null);
		}

	@Override
	public void afti_dsa_afsa_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet, WrEnv env) {


		_Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT__GT tupleSet =
				MetaHelper.cyanSetTupleStringIntIntDyn_toJava(annotationInfoSet);

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afti_dsa_afsa_receiveInfoPrototype",
				   // "env" is added by the called method
				new String [] { "annotationInfoSet" }, new Object [] { tupleSet },
				null, null);

	}


	@Override
	public void afsa_checkUnaryMessageSend(WrExpr receiverExpr, WrProgramUnit receiverType,
			ExprReceiverKind receiverKind, WrSymbol unarySymbol, WrEnv env) {


		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afsa_checkUnaryMessageSend",
				   // "env" is added by the called method
				new String [] { "receiverExpr", "receiverType", "receiverKind", "unarySymbol" },
				new Object [] { receiverExpr, receiverType, receiverKind, unarySymbol },
				null, null);


	}

	@Override
	public void afsa_checkUnaryMessageSendMostSpecific(WrExpr receiverExpr, WrProgramUnit receiverType,
			ExprReceiverKind receiverKind, WrSymbol unarySymbol, WrProgramUnit mostSpecificReceiver, WrEnv env) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afsa_checkUnaryMessageSendMostSpecific",
				   // "env" is added by the called method
				new String [] { "receiverExpr", "receiverType", "receiverKind", "unarySymbol",
						        "mostSpecificReceiver" },
				new Object [] { receiverExpr, receiverType, receiverKind, unarySymbol,
						        mostSpecificReceiver },
				null, null);


	}

	@Override
	public void afsa_checkKeywordMessageSend(WrExpr receiverExpr, WrProgramUnit receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message,
			WrMethodSignature methodSignature, WrEnv env
			) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afsa_checkKeywordMessageSend",
				   // "env" is added by the called method
				new String [] { "receiverExpr", "receiverType", "receiverKind", "message",
						"methodSignature" },
				new Object [] { receiverExpr, receiverType, receiverKind, message, methodSignature },
				null, null);

	}

	@Override
	public void afsa_checkKeywordMessageSendMostSpecific(WrExpr receiverExpr, WrProgramUnit receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message, WrMethodSignature methodSignature,
			WrProgramUnit mostSpecificReceiver, WrEnv env) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"afsa_checkKeywordMessageSendMostSpecific",
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
		return new String[] { "afsa_checkUnaryMessageSend", "afsa_checkUnaryMessageSendMostSpecific",
				"afsa_checkKeywordMessageSend", "afsa_checkKeywordMessageSendMostSpecific",
				"afti_dsa_afsa_shareInfoPrototype", "afti_dsa_afsa_receiveInfoPrototype"
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
