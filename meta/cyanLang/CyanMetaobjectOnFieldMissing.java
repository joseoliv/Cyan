package meta.cyanLang;

import cyan.lang.CyString;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFieldMissing_semAn;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple3;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprSelfPeriodIdent;

public class CyanMetaobjectOnFieldMissing extends CyanMetaobjectAtAnnot
				implements IActionFieldMissing_semAn, IInterpreterMethods_afterResTypes
//				IActionNewPrototypes_semAn, IActionNewPrototypes_afterResTypes,
//				IAction_afterResTypes, IParseWithCyanCompiler_parsing, ICommunicateInPrototype_afterResTypes_semAn_afterSemAn
				{

	public CyanMetaobjectOnFieldMissing() {
		super("onFieldMissing", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC
		}, Token.PUBLIC );

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
//					case "semAn_replaceGetMissingField":
//					case "semAn_replaceSetMissingField":
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
	public Tuple3<String, String, StringBuffer> semAn_replaceGetMissingField(
			WrExprSelfPeriodIdent fieldToGet, WrEnv env) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"semAn_replaceGetMissingField",
				   // "env" is added by the called method
				new String [] { "fieldToGet" }, new Object [] { fieldToGet },
				_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT.class, "Tuple<String, String, String>");

		if ( r == null ) {
			return null;
		}
		else {
			_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT cyTuple = (_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT ) r;

			String f1 = cyTuple._f1().s;
			if ( f1.length() != 0 ) {
				return new Tuple3<String, String, StringBuffer>(f1, cyTuple._f2().s,
						new StringBuffer(cyTuple._f3().s));
			}
			return null;
		}

	}


	@Override
	@SuppressWarnings({ })
	public StringBuffer semAn_replaceSetMissingField(
			WrExprSelfPeriodIdent fieldToSet, WrExpr rightHandSideAssignment, WrEnv env) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"semAn_replaceSetMissingField",
				   // "env" is added by the called method
				new String [] { "fieldToSet", "rightHandSideAssignment" },
				new Object [] { fieldToSet, rightHandSideAssignment },
				CyString.class, "Tuple<String, String, String>");

		if ( r == null ) {
			return null;
		}
		else {
			CyString cyTuple = (CyString ) r;

			String f1 = cyTuple.s;
			if ( f1.length() != 0 ) {
				return new StringBuffer(f1);
			}
			return null;
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
//
//		// return MetaHelper.interpreterFor_afterResTypes_codeToAdd(compiler, infoList, this.interpreterPrototype, this);
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
//
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
				"semAn_replaceGetMissingField", "semAn_replaceSetMissingField" };
	}


}
