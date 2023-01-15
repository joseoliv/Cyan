package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_semAn;
import meta.InterpreterPrototype;
import meta.MetaHelper;

public class CyanMetaobjectOnDeclaration_afterResTypes_semAn_afterSemAn extends CyanMetaobjectAtAnnot
				implements IInterpreterMethods_afterResTypes, ICheckDeclaration_afterSemAn
//				IActionMethodMissing_semAn, IActionNewPrototypes_semAn, IActionNewPrototypes_afterResTypes,
//                IAction_afterResTypes, IParseWithCyanCompiler_parsing, ICommunicateInPrototype_afterResTypes_semAn_afterSemAn, ICheckDeclaration_afterSemAn
{

	public CyanMetaobjectOnDeclaration_afterResTypes_semAn_afterSemAn() {
		super("onDeclaration_afterResTypes_semAn_afterSemAn", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.METHOD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC,
						AttachedDeclarationKind.FIELD_DEC,
						AttachedDeclarationKind.LOCAL_VAR_DEC
						});
	}

//
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
//					case "afterSemAn_checkDeclaration":
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

//	@Override
//	public List<Tuple2<String, StringBuffer>> afterResTypes_NewPrototypeList(ICompiler_afterResTypes compiler_afterResTypes) {
//		this.getAnnotation().getPrototype().getName();
//	}

	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				interpreterPrototype,
				this,
				"afterSemAn_checkDeclaration",
				// "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler },
				null, null);
	}
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

//	@Override
//	public Tuple2<StringBuffer, WrType> semAn_missingKeywordMethod(
//			WrExpr receiver, WrMessageWithKeywords message, WrEnv env) {
//
//		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
//				null,
//				env,
//				interpreterPrototype,
//				this,
//				"semAn_missingKeywordMethod",
//				// "env" is added by the called method
//				new String [] { "receiver", "message" }, new Object [] { receiver, message },
//				cyan.lang._Tuple_LT_GP_Object_GP_Object_GT.class, "Tuple<Dyn, Dyn>");
//		if ( r == null ) {
//			return null;
//		}
//		else {
//			_Tuple_LT_GP_Object_GP_Object_GT t = (_Tuple_LT_GP_Object_GP_Object_GT ) r;
//			if ( !(t._f1() instanceof CyString) || !(t._f2() instanceof WrType) ) {
//				this.addError("Method semAn_missingKeywordMethod should return an object of Tuple<String, WrType>."
//						+ " It did return a tuple, but one of the tuple elements has a wrong type");
//				return null;
//			}
//			String f1 = ((CyString ) t._f1()).s;
//			if ( f1.length() == 0 ) {
//				return null;
//			}
//			else {
//				return new Tuple2<StringBuffer, WrType>( new StringBuffer(f1), (WrType ) t._f2() );
//			}
//		}
//
//
//	}

//	@Override
//	public Tuple2<StringBuffer, WrType> semAn_analyzeReplaceUnaryMessage(
//			WrExpr receiver, WrSymbol unarySymbol, WrEnv env) {
//
//		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
//				null,
//				env,
//				interpreterPrototype,
//				this,
//				"semAn_analyzeReplaceUnaryMessage",
//				// "env" is added by the called method
//				new String [] { "receiver", "unarySymbol" }, new Object [] { receiver, unarySymbol },
//				_Tuple_LT_GP_Object_GP_Object_GT.class, "Tuple<String, WrType>");
//		if ( r == null ) {
//			return null;
//		}
//		else {
//			_Tuple_LT_GP_Object_GP_Object_GT t = (_Tuple_LT_GP_Object_GP_Object_GT ) r;
//			if ( !(t._f1() instanceof CyString) || !(t._f2() instanceof WrType) ) {
//				this.addError("Method semAn_analyzeReplaceUnaryMessage should return an object of Tuple<String, WrType>."
//						+ " It did return a tuple, but one of the tuple elements has a wrong type");
//				return null;
//			}
//			String f1 = ((CyString ) t._f1()).s;
//			if ( f1.length() == 0 ) {
//				return null;
//			}
//			else {
//				return new Tuple2<StringBuffer, WrType>( new StringBuffer(f1), (WrType ) t._f2() );
//			}
//		}
//
//
//	}


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
		return new String[] { "afterResTypes_codeToAdd", "semAn_codeToAdd", "semAn_NewPrototypeList",
				"afterResTypes_NewPrototypeList", "runUntilFixedPoint", "afterResTypes_beforeMethodCodeList",
				"afterResTypes_renameMethod", "afterResTypes_semAn_afterSemAn_shareInfoPrototype", "afterResTypes_semAn_afterSemAn_receiveInfoPrototype",
				"afterSemAn_checkDeclaration" };
	}

}
