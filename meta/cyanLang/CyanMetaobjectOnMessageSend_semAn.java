package meta.cyanLang;

import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionMessageSend_semAn;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.Tuple3;
import meta.WrEnv;
import meta.WrExprIdentStar;
import meta.WrExprMessageSendUnaryChainToExpr;
import meta.WrExprMessageSendWithKeywordsToExpr;

public class CyanMetaobjectOnMessageSend_semAn extends CyanMetaobjectAtAnnot
				implements IActionMessageSend_semAn, IInterpreterMethods_afterResTypes
//				IActionNewPrototypes_semAn, IActionNewPrototypes_afterResTypes,
//					IAction_afterResTypes, IParseWithCyanCompiler_parsing, ICommunicateInPrototype_afterResTypes_semAn_afterSemAn
					{

	public CyanMetaobjectOnMessageSend_semAn() {
		super("onMessageSend_semAn", AnnotationArgumentsKind.ZeroParameters,
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
//					case "afterResTypes_codeToAdd":
//					case "runUntilFixedPoint":
//					case "afterResTypes_beforeMethodCodeList":
//					case "afterResTypes_renameMethod":
//					case "semAn_analyzeReplaceKeywordMessage":
//					case "semAn_analyzeReplaceUnaryMessage":
//					case "semAn_NewPrototypeList":
//					case "afterResTypes_NewPrototypeList":
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
//
//
//	@Override
//	public Object afterResTypes_semAn_afterSemAn_shareInfoPrototype(WrEnv env) {
//
//		return MetaHelper.interpreterFor_MOPInterfaceMethod(
//				null,
//				env,
//				interpreterPrototype,
//				this,
//				"afterResTypes_semAn_afterSemAn_shareInfoPrototype",
//				   // "env" is added by the called method
//				new String [] { }, new Object [] { },
//				Object.class);
//		}
//
//	@Override
//	public void afterResTypes_semAn_afterSemAn_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet, WrEnv env) {
//
//		MetaHelper.interpreterFor_MOPInterfaceMethod(
//				null,
//				env,
//				interpreterPrototype,
//				this,
//				"afterResTypes_semAn_afterSemAn_receiveInfoPrototype",
//				   // "env" is added by the called method
//				new String [] { "annotationInfoSet" }, new Object [] { annotationInfoSet },
//				null);
//
//	}
//
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public List<Tuple2<String, StringBuffer>> afterResTypes_NewPrototypeList(ICompiler_afterResTypes compiler_afterResTypes) {
//
//		return (List<Tuple2<String, StringBuffer>> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler_afterResTypes,
//				compiler_afterResTypes.getEnv(),
//				interpreterPrototype,
//				this,
//				"afterResTypes_NewPrototypeList",
//				   // "env" is added by the called method
//				new String [] { "compiler" }, new Object [] { compiler_afterResTypes },
//				List.class);
//
//	}
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public List<Tuple2<String, StringBuffer>> semAn_NewPrototypeList(ICompiler_semAn compiler_semAn) {
//		return (List<Tuple2<String, StringBuffer>> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler_semAn,
//				compiler_semAn.getEnv(),
//				interpreterPrototype,
//				this,
//				"semAn_NewPrototypeList",
//				   // "env" is added by the called method
//				new String [] { "compiler" }, new Object [] { compiler_semAn },
//				List.class);
//	}

	@Override
	public Tuple3<StringBuffer, String, String> semAn_analyzeReplaceKeywordMessage(
			WrExprMessageSendWithKeywordsToExpr messageSendExpr, WrEnv env) {
//		WrExpr e = messageSendExpr.getMessage().getkeywordParameterList().get(0).getExprList().get(0);
//		if ( e instanceof WrExprLiteralDouble ) {
//			WrExprLiteralDouble ed = (WrExprLiteralDouble ) e;
//			ed.getJavaValue()
//		}
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"semAn_analyzeReplaceKeywordMessage",
				   // "env" is added by the called method
				new String [] { "messageSendExpr" }, new Object [] { messageSendExpr },
				cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT.class, "Tuple<String, String, String>");
		if ( r == null ) {
			return null;
		}
		else {
			_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT t = (_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT ) r;
//			if ( !(t._f1() instanceof CyString) || !(t._f2() instanceof CyString)
//					|| !(t._f3() instanceof CyString)
//					) {
//				this.addError("Method semAn_analyzeReplaceKeywordMessage should return an object of Tuple<String, String, String>."
//						+ " It did return a 3-tuple, but one of the tuple elements has a wrong type");
//				return null;
//			}
			String f1 = t._f1().s;
			if ( f1.length() == 0 ) {
				return null;
			}
			else {
				String packageName1 = t._f2().s;
				String prototypeName = t._f3().s;

				return new Tuple3<StringBuffer, String, String>(new StringBuffer(f1),
						packageName1, prototypeName);
				//return new Tuple2<StringBuffer, WrType>( new StringBuffer(f1), (WrType ) t._f2() );
			}
		}

	}


	@Override
	@SuppressWarnings({ })
	public Tuple3<StringBuffer, String, String> semAn_analyzeReplaceUnaryMessage(
			WrExprMessageSendUnaryChainToExpr messageSendExpr, WrEnv env) {


		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"semAn_analyzeReplaceUnaryMessage",
				   // "env" is added by the called method
				new String [] { "messageSendExpr" }, new Object [] { messageSendExpr },
				cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT.class, "Tuple<String, String, String>");
		if ( r == null ) {
			return null;
		}
		else {
			_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT t = (_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT ) r;
//			if ( !(t._f1() instanceof CyString) || !(t._f2() instanceof CyString)
//					|| !(t._f3() instanceof CyString)
//					) {
//				this.addError("Method semAn_analyzeReplaceUnaryMessage should return an object of Tuple<String, String, String>."
//						+ " It did return a 3-tuple, but one of the tuple elements has a wrong type");
//				return null;
//			}
			String f1 = t._f1().s;
			if ( f1.length() == 0 ) {
				return null;
			}
			else {
				String packageName1 = t._f2().s;
				String prototypeName = t._f3().s;

				return new Tuple3<StringBuffer, String, String>(new StringBuffer(f1),
						packageName1, prototypeName);

				//return new Tuple3<StringBuffer, String, String>(new StringBuffer(f1), null, null);
				//return new Tuple2<StringBuffer, WrType>( new StringBuffer(f1), (WrType ) t._f2() );
			}
		}

	}


	@Override
	public Tuple3<StringBuffer, String, String> semAn_analyzeReplaceUnaryMessageWithoutSelf(
			WrExprIdentStar messageSendExpr, WrEnv env) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"semAn_analyzeReplaceUnaryMessageWithoutSelf",
				   // "env" is added by the called method
				new String [] { "messageSendExpr" }, new Object [] { messageSendExpr },
				cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT.class, "Tuple<String, String, String>");
		if ( r == null ) {
			return null;
		}
		else {
			_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT t = (_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT ) r;
//			if ( !(t._f1() instanceof CyString) || !(t._f2() instanceof WrType) ) {
//				this.addError("Method analyzeReplaceUnaryMessageWithoutSelf should return an object of Tuple<String, String, String>."
//						+ " It did return a 3-tuple, but one of the tuple elements has a wrong type");
//				return null;
//			}
			String f1 = t._f1().s;
			if ( f1.length() == 0 ) {
				return null;
			}
			else {
				String packageName1 = t._f2().s;
				String prototypeName = t._f3().s;

				return new Tuple3<StringBuffer, String, String>(new StringBuffer(f1),
						packageName1, prototypeName);

//				return new Tuple3<StringBuffer, String, String>(new StringBuffer(f1),
//						null, null);
//				return new Tuple2<StringBuffer, WrType>( new StringBuffer(f1),
//						(WrType ) t._f2() );
			}
		}

	}


//
//	@SuppressWarnings("unchecked")
//	@Override
//	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
//			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
//			List<ISlotSignature>>> infoList) {
//
//
//		return (Tuple2<StringBuffer, String> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler,
//				compiler.getEnv(),
//				interpreterPrototype,
//				this,
//				"afterResTypes_codeToAdd",
//				new String [] { "compiler", "infoList" }, new Object [] { compiler, infoList },
//				Tuple2.class);
//
//	}
//
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Tuple2<String, StringBuffer>> afterResTypes_beforeMethodCodeList(
//			ICompiler_afterResTypes compiler) {
//
//		return (List<Tuple2<String, StringBuffer>> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler,
//				compiler.getEnv(),
//				interpreterPrototype,
//				this,
//				"afterResTypes_beforeMethodCodeList",
//				new String [] { "compiler" }, new Object [] { compiler },
//				List.class);
//
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Tuple2<String, String []>> afterResTypes_renameMethod(
//			ICompiler_afterResTypes compiler) {
//
//		return (List<Tuple2<String, String []>> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler,
//				compiler.getEnv(),
//				interpreterPrototype,
//				this,
//				"afterResTypes_renameMethod",
//				   // "env" is added by the called method
//				new String [] { "compiler" }, new Object [] { compiler },
//				List.class);
//	}
//
//	@Override
//	public boolean runUntilFixedPoint() {
//
//		return MetaHelper.interpreterFor_runUntilFixedPoint(interpreterPrototype, this);
//	}
//


	@Override
	public boolean shouldTakeText() { return true; }

	InterpreterPrototype interpreterPrototype = null;

	@Override
	public InterpreterPrototype getInterpreterPrototype() {
		return interpreterPrototype;
	}

	@Override
	public void setInterpreterPrototype(InterpreterPrototype interpreterPrototype) {
		this.interpreterPrototype = interpreterPrototype;
	}


	@Override
	public CyanMetaobjectAtAnnot getCyanMetaobject() {
		return this;
	}


	@Override
	public String[] methodToInterpertList() {
		return new String[] { "afterResTypes_codeToAdd", "semAn_NewPrototypeList",
				"afterResTypes_NewPrototypeList", "runUntilFixedPoint", "afterResTypes_beforeMethodCodeList",
				"afterResTypes_renameMethod", "afterResTypes_semAn_afterSemAn_shareInfoPrototype", "afterResTypes_semAn_afterSemAn_receiveInfoPrototype",
				"semAn_analyzeReplaceKeywordMessage", "semAn_analyzeReplaceUnaryMessage",
				"semAn_analyzeReplaceUnaryMessageWithoutSelf" };
	}


}
