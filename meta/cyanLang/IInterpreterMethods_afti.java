package meta.cyanLang;

import java.util.List;
import java.util.Set;
import cyan.lang.CyString;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT__GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT__GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT__GT__GT;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT__GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import error.CompileErrorException;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionNewPrototypes_afti;
import meta.IActionNewPrototypes_dsa;
import meta.IAction_afti;
import meta.IAction_dsa;
import meta.ICommunicateInPrototype_afti_dsa_afsa;
import meta.ICompiler_afti;
import meta.ICompiler_dpa;
import meta.ICompiler_dsa;
import meta.IParseWithCyanCompiler_dpa;
import meta.ISlotInterface;
import meta.InterpretationErrorException;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrSymbol;

public interface IInterpreterMethods_afti extends IAction_dsa, IActionNewPrototypes_dsa, IAction_afti,
    IActionNewPrototypes_afti, IParseWithCyanCompiler_dpa, ICommunicateInPrototype_afti_dsa_afsa {


	void addError(String message);
	void addError(WrSymbol symbol, String message);

	@Override
	default void dpa_parse(ICompiler_dpa cp) {

		try {
			cp.next();   // if ( this instanceof meta.cyanLang.CyanMetaobjectOnOverride_afti_dsa_afsa ) { }
			Tuple2<String, InterpreterPrototype> t = MetaHelper.parseCyanCode(cp);
			if ( t == null ) {
				cp.error(this.getCyanMetaobject().getMetaobjectAnnotation().getFirstSymbol(),
						"Internal error in metaobject '" + this.getClass().getName() + "'");
			}
			else {
				if ( t.f1 != null ) {
					cp.error(this.getCyanMetaobject().getMetaobjectAnnotation().getFirstSymbol(), t.f1);
				}
				else {
					this.setInterpreterPrototype(t.f2);
					for ( String key : this.getInterpreterPrototype().mapMethodName_Body.keySet() ) {
						boolean ok = false;
						for (String methodName : this.methodToInterpertList() ) {
							if ( key.equals(methodName) ) {
								ok = true;
								break;
							}
						}
						if ( ! ok ) {
							getCyanMetaobject().addError("Unidentified method name: '" + key + "'");
							setInterpreterPrototype(null);
							return ;
						}
					}
				}
			}

		}
		catch (InterpretationErrorException e) {
			addError(e.getMessage());
		}
		catch (CompileErrorException e) {
			addError(e.getMessage());
		}


	}


	@Override
	default Tuple2<StringBuffer, String> afti_codeToAdd(
			ICompiler_afti compiler, List<Tuple2<WrAnnotation,
			List<ISlotInterface>>> infoList) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afti_codeToAdd",
				new String [] { "compiler", "infoList" }, new Object [] { compiler, infoList },
				_Tuple_LT_GP_CyString_GP_CyString_GT.class, "Tuple<String, String>");

		if ( r != null ) {
			_Tuple_LT_GP_CyString_GP_CyString_GT t = (_Tuple_LT_GP_CyString_GP_CyString_GT ) r;
			if ( t._f2().s.length() != 0  ) {
				return new Tuple2<StringBuffer, String>( new StringBuffer(t._f1().s), t._f2().s);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
//			Tuple2<StringBuffer, String> t = null;
//			try {
//				t = (Tuple2<StringBuffer, String> ) r;
//			}
//			catch (ClassCastException e ) {
//				this.addError("Method 'afti_codeToAdd' returned an object of type '"
//						+ r.getClass().getCanonicalName() + "'. It should have returned an object of the Java type " +
//						"Tuple2<StringBuffer, String> or of the Cyan type Tuple<String, String>");
//			}
//			return t;
//		}

	}



	@Override
	default List<Tuple3<String, StringBuffer, Boolean>> afti_beforeMethodCodeList(
			ICompiler_afti compiler) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afti_beforeMethodCodeList",
				new String [] { "compiler" }, new Object [] { compiler },
				_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT__GT.class,
				"Array<Tuple<String, String, Boolean>>");

		if ( r != null ) {
			return MetaHelper.cyanArrayTupleStringStringBoolean_toJava(
					(_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT__GT ) r);
		}
		else {
			return null;
		}

	}

	@Override
	default List<Tuple2<String, String []>> afti_renameMethod(
			ICompiler_afti compiler) {


		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afti_renameMethod",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler },
				_Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT__GT__GT.class,
				"Array<Tuple<String,Array<String>>>");

		if ( r != null ) {
			return MetaHelper.cyanTupleStringArrayString_tojava(
					(_Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT__GT__GT ) r);
		}
		return null;

	}

	@Override
	default boolean runUntilFixedPoint() {

		return MetaHelper.interpreterFor_runUntilFixedPoint(getInterpreterPrototype(), getCyanMetaobject());
	}


	@Override
	default Object afti_dsa_afsa_shareInfoPrototype(WrEnv env) {

		return MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afti_dsa_afsa_shareInfoPrototype",
				   // "env" is added by the called method
				new String [] { }, new Object [] { },
				null, null);
	}

	@Override
	default void afti_dsa_afsa_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet, WrEnv env) {

		_Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT__GT tupleSet =
				MetaHelper.cyanSetTupleStringIntIntDyn_toJava(annotationInfoSet);

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afti_dsa_afsa_receiveInfoPrototype",
				   // "env" is added by the called method
				new String [] { "annotationInfoSet" }, new Object [] { tupleSet },
				null, null);

	}

	@Override
	default List<Tuple2<String, StringBuffer>> afti_NewPrototypeList(ICompiler_afti compiler_afti) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_afti,
				compiler_afti.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afti_NewPrototypeList",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_afti },
				_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT__GT.class,
				"Array<Tuple<String, String>>");

		if ( r != null ) {
			return MetaHelper.cyanArrayTupleStringString_toJava( (_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT__GT ) r);
		}
		else {
			return null;
		}


	}

	@Override
	default StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_dsa,
				compiler_dsa.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"dsa_codeToAdd",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_dsa },
				CyString.class, "String");
		if ( r == null ) {
			return null;
		}
		else {
			return new StringBuffer( ((CyString ) r).s ) ;
		}
	}


	@Override
	default List<Tuple2<String, StringBuffer>> dsa_NewPrototypeList(ICompiler_dsa compiler_dsa) {


		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_dsa,
				compiler_dsa.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"dsa_NewPrototypeList",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_dsa },
				_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT__GT.class, "Array<Tuple<String, String>>");
		if ( r != null ) {
			return MetaHelper.cyanArrayTupleStringString_toJava( (_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT__GT ) r);
		}
		else {
			return null;
		}


	}


	InterpreterPrototype getInterpreterPrototype();
	void setInterpreterPrototype(InterpreterPrototype interpreterPrototype);
	CyanMetaobjectAtAnnot getCyanMetaobject();
	String []methodToInterpertList();
}
