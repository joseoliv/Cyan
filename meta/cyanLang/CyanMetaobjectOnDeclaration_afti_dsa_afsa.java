package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afsa;
import meta.ICompiler_dsa;
import meta.InterpreterPrototype;
import meta.MetaHelper;

public class CyanMetaobjectOnDeclaration_afti_dsa_afsa extends CyanMetaobjectAtAnnot
				implements IInterpreterMethods_afti, ICheckDeclaration_afsa
//				IActionMethodMissing_dsa, IActionNewPrototypes_dsa, IActionNewPrototypes_afti,
//                IAction_afti, IParseWithCyanCompiler_dpa, ICommunicateInPrototype_afti_dsa_afsa, ICheckDeclaration_afsa
{

	public CyanMetaobjectOnDeclaration_afti_dsa_afsa() {
		super("onDeclaration_afti_dsa_afsa", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.METHOD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC,
						AttachedDeclarationKind.FIELD_DEC,
						AttachedDeclarationKind.LOCAL_VAR_DEC
						});
	}

//
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
//					case "afti_codeToAdd":
//					case "runUntilFixedPoint":
//					case "afti_beforeMethodCodeList":
//					case "afti_renameMethod":
//					case "dsa_missingKeywordMethod":
//					case "dsa_missingUnaryMethod":
//					case "dsa_NewPrototypeList":
//					case "afti_NewPrototypeList":
//					case "afti_dsa_afsa_shareInfoPrototype":
//					case "afti_dsa_afsa_receiveInfoPrototype":
//					case "afsa_checkDeclaration":
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
//	public List<Tuple2<String, StringBuffer>> afti_NewPrototypeList(ICompiler_afti compiler_afti) {
//		this.getMetaobjectAnnotation().getProgramUnit().getName();
//	}

	@Override
	public void afsa_checkDeclaration(ICompiler_dsa compiler) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				interpreterPrototype,
				this,
				"afsa_checkDeclaration",
				// "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler },
				null, null);
	}
//
//
//
//	@Override
//	public Object afti_dsa_afsa_shareInfoPrototype(WrEnv env) {
//
//		return MetaHelper.interpreterFor_MOPInterfaceMethod(
//				null,
//				env,
//				interpreterPrototype,
//				this,
//				"afti_dsa_afsa_shareInfoPrototype",
//				// "env" is added by the called method
//				new String [] { }, new Object [] { },
//				Object.class);
//	}
//
//	@Override
//	public void afti_dsa_afsa_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet, WrEnv env) {
//
//		MetaHelper.interpreterFor_MOPInterfaceMethod(
//				null,
//				env,
//				interpreterPrototype,
//				this,
//				"afti_dsa_afsa_receiveInfoPrototype",
//				// "env" is added by the called method
//				new String [] { "annotationInfoSet" }, new Object [] { annotationInfoSet  },
//				null);
//
//	}
//
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public List<Tuple2<String, StringBuffer>> afti_NewPrototypeList(ICompiler_afti compiler_afti) {
//
//		return (List<Tuple2<String, StringBuffer>> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler_afti,
//				compiler_afti.getEnv(),
//				interpreterPrototype,
//				this,
//				"afti_NewPrototypeList",
//				// "env" is added by the called method
//				new String [] { "compiler" }, new Object [] { compiler_afti },
//				List.class);
//
//	}
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public List<Tuple2<String, StringBuffer>> dsa_NewPrototypeList(ICompiler_dsa compiler_dsa) {
//		return (List<Tuple2<String, StringBuffer>> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler_dsa,
//				compiler_dsa.getEnv(),
//				interpreterPrototype,
//				this,
//				"dsa_NewPrototypeList",
//				// "env" is added by the called method
//				new String [] { "compiler" }, new Object [] { compiler_dsa },
//				List.class);
//	}

//	@Override
//	public Tuple2<StringBuffer, WrType> dsa_missingKeywordMethod(
//			WrExpr receiver, WrMessageWithKeywords message, WrEnv env) {
//
//		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
//				null,
//				env,
//				interpreterPrototype,
//				this,
//				"dsa_missingKeywordMethod",
//				// "env" is added by the called method
//				new String [] { "receiver", "message" }, new Object [] { receiver, message },
//				cyan.lang._Tuple_LT_GP_Object_GP_Object_GT.class, "Tuple<Dyn, Dyn>");
//		if ( r == null ) {
//			return null;
//		}
//		else {
//			_Tuple_LT_GP_Object_GP_Object_GT t = (_Tuple_LT_GP_Object_GP_Object_GT ) r;
//			if ( !(t._f1() instanceof CyString) || !(t._f2() instanceof WrType) ) {
//				this.addError("Method dsa_missingKeywordMethod should return an object of Tuple<String, WrType>."
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
//	public Tuple2<StringBuffer, WrType> dsa_analyzeReplaceUnaryMessage(
//			WrExpr receiver, WrSymbol unarySymbol, WrEnv env) {
//
//		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
//				null,
//				env,
//				interpreterPrototype,
//				this,
//				"dsa_analyzeReplaceUnaryMessage",
//				// "env" is added by the called method
//				new String [] { "receiver", "unarySymbol" }, new Object [] { receiver, unarySymbol },
//				_Tuple_LT_GP_Object_GP_Object_GT.class, "Tuple<String, WrType>");
//		if ( r == null ) {
//			return null;
//		}
//		else {
//			_Tuple_LT_GP_Object_GP_Object_GT t = (_Tuple_LT_GP_Object_GP_Object_GT ) r;
//			if ( !(t._f1() instanceof CyString) || !(t._f2() instanceof WrType) ) {
//				this.addError("Method dsa_analyzeReplaceUnaryMessage should return an object of Tuple<String, WrType>."
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
//	public Tuple2<StringBuffer, String> afti_codeToAdd(
//			ICompiler_afti compiler, List<Tuple2<WrAnnotation,
//			List<ISlotInterface>>> infoList) {
//
//
//		return (Tuple2<StringBuffer, String> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler,
//				compiler.getEnv(),
//				interpreterPrototype,
//				this,
//				"afti_codeToAdd",
//				new String [] { "compiler", "infoList" }, new Object [] { compiler, infoList },
//				Tuple2.class);
//
//	}
//
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Tuple2<String, StringBuffer>> afti_beforeMethodCodeList(
//			ICompiler_afti compiler) {
//
//		return (List<Tuple2<String, StringBuffer>> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler,
//				compiler.getEnv(),
//				interpreterPrototype,
//				this,
//				"afti_beforeMethodCodeList",
//				new String [] { "compiler" }, new Object [] { compiler },
//				List.class);
//
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Tuple2<String, String []>> afti_renameMethod(
//			ICompiler_afti compiler) {
//
//		return (List<Tuple2<String, String []>> ) MetaHelper.interpreterFor_MOPInterfaceMethod(
//				compiler,
//				compiler.getEnv(),
//				interpreterPrototype,
//				this,
//				"afti_renameMethod",
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
		return new String[] { "afti_codeToAdd", "dsa_codeToAdd", "dsa_NewPrototypeList",
				"afti_NewPrototypeList", "runUntilFixedPoint", "afti_beforeMethodCodeList",
				"afti_renameMethod", "afti_dsa_afsa_shareInfoPrototype", "afti_dsa_afsa_receiveInfoPrototype",
				"afsa_checkDeclaration" };
	}

}
