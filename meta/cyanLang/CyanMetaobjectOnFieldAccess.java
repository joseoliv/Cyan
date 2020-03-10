package meta.cyanLang;

import cyan.lang.CyString;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFieldAccess_dsa;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrStatement;

public class CyanMetaobjectOnFieldAccess extends CyanMetaobjectAtAnnot
		implements IActionFieldAccess_dsa, IInterpreterMethods_afti
//		IActionNewPrototypes_dsa, IActionNewPrototypes_afti,
//		IAction_afti, IParseWithCyanCompiler_dpa, ICommunicateInPrototype_afti_dsa_afsa
		{

	public CyanMetaobjectOnFieldAccess() {
		super("onFieldAccess", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.FIELD_DEC
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
//					case "afti_codeToAdd":
//					case "runUntilFixedPoint":
//					case "afti_beforeMethodCodeList":
//					case "afti_renameMethod":
//					case "dsa_replaceGetField":
//					case "dsa_replaceSetField":
//					case "dsa_replaceMinusMinusField":
//					case "dsa_replacePlusPlusField":
//					case "dsa_NewPrototypeList":
//					case "afti_NewPrototypeList":
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
//				   // "env" is added by the called method
//				new String [] { }, new Object [] { },
//				Object.class);
//		}
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
//				   // "env" is added by the called method
//				new String [] { "annotationInfoSet" }, new Object [] { annotationInfoSet },
//				null);
//
//	}
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
//				   // "env" is added by the called method
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
//				   // "env" is added by the called method
//				new String [] { "compiler" }, new Object [] { compiler_dsa },
//				List.class);
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
//
//		// return MetaHelper.interpreterFor_afti_codeToAdd(compiler, infoList, this.interpreterPrototype, this);
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


	@Override
	public
	StringBuffer dsa_replaceGetField(WrExpr fieldToGet, WrEnv env) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"dsa_replaceGetField",
				   // "env" is added by the called method
				new String [] { "fieldToGet" }, new Object [] { fieldToGet },
				CyString.class, "String");

		if ( r == null ) {
			return null;
		}
		else {
			String f1 = ((CyString ) r).s;
			if ( f1.length() == 0 ) {
				return null;
			}
			else {
				return new StringBuffer(f1);
			}
		}
	}

	/**
	 * return the code that replaces the assignment to a field. fieldToSet may be an
	 * object of ExprIdentStar ('n' in 'k = n') or an object of ExprSelfPeriodIdent
	 *
	   @param fieldToGet
	   @return
	 */

	@Override
	public StringBuffer dsa_replaceSetField(WrExpr fieldToSet, WrExpr rightHandSideAssignment, WrEnv env) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"dsa_replaceSetField",
				   // "env" is added by the called method
				new String [] { "fieldToSet", "rightHandSideAssignment" },
				new Object [] { fieldToSet, rightHandSideAssignment },
				CyString.class, "String");

		if ( r == null ) {
			return null;
		}
		else {
			String f1 = ((CyString ) r).s;
			if ( f1.length() == 0 ) {
				return null;
			}
			else {
				return new StringBuffer(f1);
			}
		}
	}


	@Override
	public StringBuffer dsa_replacePlusPlusField(WrStatement plusPlusField, WrEnv env) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"dsa_replacePlusPlusField",
				   // "env" is added by the called method
				new String [] { "plusPlusField" },
				new Object [] { plusPlusField },
				CyString.class, "String");
		if ( r == null ) {
			return null;
		}
		else {
			String f1 = ((CyString ) r).s;
			if ( f1.length() == 0 ) {
				return null;
			}
			else {
				return new StringBuffer(f1);
			}
		}
	}

	@Override
	public StringBuffer dsa_replaceMinusMinusField(WrStatement minusMinusField, WrEnv env) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				interpreterPrototype,
				this,
				"dsa_replaceMinusMinusField",
				   // "env" is added by the called method
				new String [] { "minusMinusField" },
				new Object [] { minusMinusField },
				CyString.class, "String");

		if ( r == null ) {
			return null;
		}
		else {
			String f1 = ((CyString ) r).s;
			if ( f1.length() == 0 ) {
				return null;
			}
			else {
				return new StringBuffer(f1);
			}
		}
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
		return new String[] { "afti_codeToAdd", "dsa_NewPrototypeList",
				"afti_NewPrototypeList", "runUntilFixedPoint", "afti_beforeMethodCodeList",
				"afti_renameMethod", "afti_dsa_afsa_shareInfoPrototype", "afti_dsa_afsa_receiveInfoPrototype",
				"dsa_replaceGetField", "dsa_replaceSetField", "dsa_replaceMinusMinusField",
				"dsa_replacePlusPlusField"
		};
	}

}
