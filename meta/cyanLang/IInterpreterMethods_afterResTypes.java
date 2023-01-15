package meta.cyanLang;

import java.util.List;
import java.util.Set;
import cyan.lang.CyString;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import error.CompileErrorException;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionNewPrototypes_afterResTypes;
import meta.IActionNewPrototypes_semAn;
import meta.IAction_afterResTypes;
import meta.IAction_semAn;
import meta.ICommunicateInPrototype_afterResTypes_semAn_afterSemAn;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IParseWithCyanCompiler_parsing;
import meta.ISlotSignature;
import meta.InterpretationErrorException;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrSymbol;

public interface IInterpreterMethods_afterResTypes extends IAction_semAn, IActionNewPrototypes_semAn, IAction_afterResTypes,
    IActionNewPrototypes_afterResTypes, IParseWithCyanCompiler_parsing, ICommunicateInPrototype_afterResTypes_semAn_afterSemAn {


	void addError(String message);
	void addError(WrSymbol symbol, String message);

	@Override
	default void parsing_parse(ICompiler_parsing cp) {

		try {
			cp.next();   // if ( this instanceof meta.cyanLang.CyanMetaobjectOnOverride_afterResTypes_semAn_afterSemAn ) { }
			Tuple2<String, InterpreterPrototype> t = MetaHelper.parseCyanCode(cp);
			if ( t == null ) {
				cp.error(this.getCyanMetaobject().getAnnotation().getFirstSymbol(),
						"Internal error in metaobject '" + this.getClass().getName() + "'");
			}
			else {
				if ( t.f1 != null ) {
					cp.error(this.getCyanMetaobject().getAnnotation().getFirstSymbol(), t.f1);
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
	default Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
			List<ISlotSignature>>> infoList) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afterResTypes_codeToAdd",
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
//				this.addError("Method 'afterResTypes_codeToAdd' returned an object of type '"
//						+ r.getClass().getCanonicalName() + "'. It should have returned an object of the Java type " +
//						"Tuple2<StringBuffer, String> or of the Cyan type Tuple<String, String>");
//			}
//			return t;
//		}

	}



	@Override
	default List<Tuple3<String, StringBuffer, Boolean>> afterResTypes_beforeMethodCodeList(
			ICompiler_afterResTypes compiler) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afterResTypes_beforeMethodCodeList",
				new String [] { "compiler" }, new Object [] { compiler },
				_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT.class,
				"Array<Tuple<String, String, Boolean>>");

		if ( r != null ) {
			return MetaHelper.cyanArrayTupleStringStringBoolean_toJava(
					(_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT ) r);
		}
		else {
			return null;
		}

	}

	@Override
	default List<Tuple2<String, String []>> afterResTypes_renameMethod(
			ICompiler_afterResTypes compiler) {


		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afterResTypes_renameMethod",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler },
				_Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT.class,
				"Array<Tuple<String,Array<String>>>");

		if ( r != null ) {
			return MetaHelper.cyanTupleStringArrayString_tojava(
					(_Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT ) r);
		}
		return null;

	}

	@Override
	default boolean runUntilFixedPoint() {

		return MetaHelper.interpreterFor_runUntilFixedPoint(getInterpreterPrototype(), getCyanMetaobject());
	}


	@Override
	default Object afterResTypes_semAn_afterSemAn_shareInfoPrototype(WrEnv env) {

		return MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afterResTypes_semAn_afterSemAn_shareInfoPrototype",
				   // "env" is added by the called method
				new String [] { }, new Object [] { },
				null, null);
	}

	@Override
	default void afterResTypes_semAn_afterSemAn_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> annotationInfoSet, WrEnv env) {

		_Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT_GT tupleSet =
				MetaHelper.cyanSetTupleStringIntIntDyn_toJava(annotationInfoSet);

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				null,
				env,
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afterResTypes_semAn_afterSemAn_receiveInfoPrototype",
				   // "env" is added by the called method
				new String [] { "annotationInfoSet" }, new Object [] { tupleSet },
				null, null);

	}

	@Override
	default List<Tuple2<String, StringBuffer>> afterResTypes_NewPrototypeList(ICompiler_afterResTypes compiler_afterResTypes) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_afterResTypes,
				compiler_afterResTypes.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"afterResTypes_NewPrototypeList",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_afterResTypes },
				_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT.class,
				"Array<Tuple<String, String>>");

		if ( r != null ) {
			return MetaHelper.cyanArrayTupleStringString_toJava( (_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT ) r);
		}
		else {
			return null;
		}


	}

	@Override
	default StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_semAn,
				compiler_semAn.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"semAn_codeToAdd",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_semAn },
				CyString.class, "String");
		if ( r == null ) {
			return null;
		}
		else {
			return new StringBuffer( ((CyString ) r).s ) ;
		}
	}


	@Override
	default List<Tuple2<String, StringBuffer>> semAn_NewPrototypeList(ICompiler_semAn compiler_semAn) {


		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_semAn,
				compiler_semAn.getEnv(),
				getInterpreterPrototype(),
				getCyanMetaobject(),
				"semAn_NewPrototypeList",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_semAn },
				_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT.class, "Array<Tuple<String, String>>");
		if ( r != null ) {
			return MetaHelper.cyanArrayTupleStringString_toJava( (_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT ) r);
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
