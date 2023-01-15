package meta.cyanLang;

import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionMethodMissing_semAn;
import meta.ICheckOverride_afterSemAn;
import meta.ICompiler_semAn;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple3;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrMessageWithKeywords;
import meta.WrMethodDec;
import meta.WrSymbol;

public class CyanMetaobjectOnOverride_afterResTypes_semAn_afterSemAn extends CyanMetaobjectAtAnnot
		implements IActionMethodMissing_semAn, IInterpreterMethods_afterResTypes, ICheckOverride_afterSemAn
//		IActionNewPrototypes_semAn, IActionNewPrototypes_afterResTypes,
//			IAction_afterResTypes, IParseWithCyanCompiler_parsing, ICommunicateInPrototype_afterResTypes_semAn_afterSemAn,
			{

	public CyanMetaobjectOnOverride_afterResTypes_semAn_afterSemAn() {
		super("onOverride_afterResTypes_semAn_afterSemAn", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC },
				Token.PUBLIC);
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
//					case "semAn_missingKeywordMethod":
//					case "semAn_missingUnaryMethod":
//					case "semAn_NewPrototypeList":
//					case "afterResTypes_NewPrototypeList":
//					case "afterResTypes_semAn_afterSemAn_shareInfoPrototype":
//					case "afterResTypes_semAn_afterSemAn_receiveInfoPrototype":
//					case "afterSemAn_checkOverride":
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
//				// "env" is added by the called method
//				new String [] { }, new Object [] { },
//				Object.class);
//	}
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
//				// "env" is added by the called method
//				new String [] { "annotationInfoSet" }, new Object [] { annotationInfoSet  },
//				null);
//
//	}
//
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
//				// "env" is added by the called method
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
//				// "env" is added by the called method
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
//				// "env" is added by the called method
//				new String [] { "compiler" }, new Object [] { compiler_semAn },
//				List.class);
//	}

	@Override
	public Tuple3<StringBuffer, String, String> semAn_missingKeywordMethod(
			WrExpr receiver, WrMessageWithKeywords message, WrEnv env) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"semAn_missingKeywordMethod",
				// "env" is added by the called method
				new String [] { "receiver", "message" }, new Object [] { receiver, message },
				_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT.class, "Tuple<String, String, String>");

		if ( r == null ) {
			return null;
		}
		else {
			_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT t = (_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT ) r;
			String f1 = t._f1().s;
			if ( f1.length() == 0 ) {
				return null;
			}
			else {
				return new Tuple3<StringBuffer, String, String>(new StringBuffer(f1) ,
						t._f2().s, t._f3().s);
				//return new Tuple2<StringBuffer, WrType>( new StringBuffer(f1), (WrType ) t._f2() );
			}
		}
	}

	@Override
	public Tuple3<StringBuffer, String, String> semAn_missingUnaryMethod(
			WrExpr receiver, WrSymbol unarySymbol, WrEnv env) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"semAn_missingUnaryMethod",
				// "env" is added by the called method
				new String [] { "receiver", "unarySymbol" }, new Object [] { receiver, unarySymbol },
				_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT.class, "Tuple<String, String, String>");
		if ( r == null ) {
			return null;
		}
		else {
			_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT t = (_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT ) r;
			String f1 = t._f1().s;
			if ( f1.length() == 0 ) {
				return null;
			}
			else {
				return new Tuple3<StringBuffer, String, String>(new StringBuffer(f1),
						t._f2().s, t._f3().s);
				//return new Tuple2<StringBuffer, WrType>( new StringBuffer(f1), (WrType ) t._f2() );
			}
		}

	}



	@Override
	public void afterSemAn_checkOverride(ICompiler_semAn compiler,
			WrMethodDec overridedMethod) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				interpreterPrototype,
				this,
				"afterSemAn_checkOverride",
				// "env" is added by the called method
				new String [] { "compiler", "method" }, new Object [] { compiler, overridedMethod },
				null, null);
	}





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
		return new String[] { "afterResTypes_codeToAdd",  "semAn_NewPrototypeList",
				"afterResTypes_NewPrototypeList", "runUntilFixedPoint", "afterResTypes_beforeMethodCodeList",
				"afterResTypes_renameMethod", "afterResTypes_semAn_afterSemAn_shareInfoPrototype", "afterResTypes_semAn_afterSemAn_receiveInfoPrototype",
				"semAn_missingKeywordMethod", "semAn_missingUnaryMethod",
				"afterSemAn_checkOverride" };
	}

}
