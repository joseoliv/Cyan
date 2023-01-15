package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import cyan.lang.CyString;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT_GT_GT;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.IAbstractCyanCompiler;
import meta.IActionNewPrototypes_afterResTypes;
import meta.IActionNewPrototypes_semAn;
import meta.IAction_afterResTypes;
import meta.IAction_parsing;
import meta.IAction_semAn;
import meta.ICommunicateInPrototype_afterResTypes_semAn_afterSemAn;
import meta.ICompilerAction_parsing;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_semAn;
import meta.ISlotSignature;
import meta.InterpretationErrorException;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrAnnotation;
import meta.WrAnnotationAt;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrExprAnyLiteral;
import meta.WrExprLiteralString;

/**
 *
<p>This metaobject works like CyanMetaobjectAction_afterResTypes_semAn but the <em>Myan</em> code is read from the file that is the first parameter. So, instead of putting the <em>Myan</em> code attached to the annotation, it is read from the file, which may be preceded by a package name. The file should have extension <code>myan</code> and be in the `–data´ directory of the package. Usage example:</p>
<pre><code>	{@literal @}runFile_afterResTypes_semAn("runFile_afterResTypes_semAn.afterResTypes_semAn_test",
	       "with:1 do:1", "unary", 10)
</code></pre>
<p>The <em>Myan</em> file can have parameters in its name. They are textually replaced by the parameters of the annotation (but the first, that is the file name). Then, in this example, there should be a file</p>
<pre><code>	afterResTypes_semAn_test(MetSig,UMS,Ret).myan
</code></pre>
<p>in directory <code>runFile_afterResTypes_semAn\--data</code>. Inside the file,  strings <code>Ret</code>, the third formal parameter, are replaced by <code>10</code>. The same with the other parameters.</p>
<blockquote>
<p>Written with <a href="https://stackedit.io/">StackEdit</a>.</p>
</blockquote>


 */
public class CyanMetaobjectRunFile extends CyanMetaobjectAtAnnot
			implements // IInterpreterMethods_afterResTypes,
			IAction_parsing,
			IAction_semAn, IActionNewPrototypes_semAn, IAction_afterResTypes, IActionNewPrototypes_afterResTypes,
				ICommunicateInPrototype_afterResTypes_semAn_afterSemAn
			{

	public CyanMetaobjectRunFile() {
		super("runFile", AnnotationArgumentsKind.OneOrMoreParameters );
	}

	@Override
	public void check() {
		if ( ! ( getAnnotation().getRealParameterList().get(0) instanceof WrExprLiteralString)
				// || getAnnotation().getRealParameterList().size() != 1
				)  {
			addError("This metaobject annotation should have at least one parameter");
			return ;
		}
//		for ( WrExprAnyLiteral p : getAnnotation().getRealParameterList() ) {
//			if ( !(p instanceof WrExprLiteralString) ) {
//				addError("The parameters to this metaobject annotation should have type String");
//			}
//		}
	}


	private Tuple3<String, String, List<String>> extractPackageFileNameParam(String currentPackageName) {
		String cyanFileName = MetaHelper.removeQuotes( (String )
				getAnnotation().getJavaParameterList().get(0));

		List<WrExprAnyLiteral> wrParamList = getAnnotation().getRealParameterList();
		List<String> paramNameList = new ArrayList<>();

		for ( int i = 1;  i < wrParamList.size(); ++i ) {
			WrExprAnyLiteral p = wrParamList.get(i);
			paramNameList.add(p.asString());
		}

		String packageName1 = "";
		String filename = cyanFileName;

		int lastDotIndex = cyanFileName.lastIndexOf('.');
		if ( lastDotIndex < 0 ) {
			// file should be in the current package
			packageName1 = currentPackageName;
		}
		else {
			packageName1 = cyanFileName.substring(0, lastDotIndex);
			filename = cyanFileName.substring(lastDotIndex+1);
		}
		return new Tuple3<String, String, List<String>>(packageName1, filename, paramNameList);
	}

	@Override
	public  StringBuffer parsing_codeToAdd(ICompilerAction_parsing cp) {

		Tuple3<String, String, List<String>> t = this.extractPackageFileNameParam(cp.getCompilationUnit().getPackageName());
		String packageName1 = t.f1;
		String filename = t.f2;
		List<String> paramNameList = t.f3;

		String msg = loadCyanInterpreterPrototypes(cp, packageName1, filename,
				paramNameList);
		if ( msg != null ) {
			this.addError(this.getAnnotation().getFirstSymbol(), msg);
		}
		return null;
	}

	/**
	   @param cp
	   @param packageName1
	   @param filename
	   @param paramNameList
	 */
	private String loadCyanInterpreterPrototypes(IAbstractCyanCompiler cp,
			String packageName1, String filename,
			List<String> paramNameList) {

		final Tuple5<FileError, char[], String, String, WrCyanPackage> t5 = cp.readTextFileFromPackage(
				filename, MetaHelper.extensionMyanFile, packageName1, DirectoryKindPPP.DATA,
				paramNameList == null ? 0 : paramNameList.size(), paramNameList);


		if ( t5 != null && t5.f1 == FileError.package_not_found ) {
			// this.addError( "Cannot find package '" + packageName1 + "'");
			return "Cannot find package '" + packageName1 + "'";
		}
		if ( t5 == null  || t5.f1 != FileError.ok_e ) {
//			this.addError( "Cannot read file '" + filename + ".mo" +
//		        "' from package '" + packageName1 + "'. This file should be in directory '" + DirectoryKindPPP.DATA +
//		        "' of the package directory"
//		         );
			return "Cannot read file '" + filename + ".mo" +
			        "' from package '" + packageName1 + "'. This file should be in directory '" + DirectoryKindPPP.DATA +
			        "' of the package directory";
		}

		final String cyanCode = new String(t5.f2);
		try {
			Tuple2<String, InterpreterPrototype> t = null;
			try {
				 t = MetaHelper.getMapMethodName_Body(cyanCode);
			}
			catch (CompileErrorException e) {
				return e.getMessage();
			}
			if ( t == null ) {
//				cp.error(this.getAnnotation().getFirstSymbol(),
//						"Internal error in metaobject '" + this.getClass().getName() + "'");
				return "Internal error in metaobject '" + this.getClass().getName() + "'";
			}
			else {
				if ( t.f1 != null ) {
					//cp.error(this.getAnnotation().getFirstSymbol(), t.f1);
					return t.f1;
				}
				else {
					interpreterPrototype = t.f2;
					//interpreterPrototype = t.f2;
					for ( String key : t.f2.mapMethodName_Body.keySet() ) {
						switch ( key ) {
						case "afterResTypes_codeToAdd":
						case "semAn_codeToAdd":
						case "semAn_NewPrototypeList":
						case "afterResTypes_NewPrototypeList":
						case "runUntilFixedPoint":
						case "afterResTypes_beforeMethodCodeList":
						case "afterResTypes_renameMethod":
						case "afterResTypes_semAn_afterSemAn_shareInfoPrototype":
						case "afterResTypes_semAn_afterSemAn_receiveInfoPrototype":
							break;
						default:
							//this.addError("Unidentified method name: '" + key + "'");
							interpreterPrototype = null;
							return "Unidentified method name: '" + key + "'";
						}
					}


				}
			}
		}
		catch (InterpretationErrorException e) {
			return e.getMessage();
		}
		catch (CompileErrorException e) {
			// return e.getMessage();
		}

		return null;
	}



	private boolean loadCode(ICompiler_semAn compiler_semAn) {
		if ( this.interpreterPrototype == null ) {
			Tuple3<String, String, List<String>> t = this.extractPackageFileNameParam(
					compiler_semAn.getEnv().getCurrentCompilationUnit().getPackageName());
			String packageName1 = t.f1;
			String filename = t.f2;
			List<String> paramNameList = t.f3;

			String msg = loadCyanInterpreterPrototypes(compiler_semAn, packageName1, filename,
					paramNameList);
			if ( msg != null ) {
				this.addError(this.getAnnotation().getFirstSymbol(), msg);
				return false;
			}
		}
		return true;
	}



	@Override
	public List<Tuple2<String, StringBuffer>> semAn_NewPrototypeList(ICompiler_semAn compiler_semAn) {

		if ( !loadCode(compiler_semAn) ) { return null; }

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_semAn,
				compiler_semAn.getEnv(),
				interpreterPrototype,
				this,
				"semAn_NewPrototypeList",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_semAn },
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
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {


		if ( !loadCode(compiler_semAn) ) { return null; }

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_semAn,
				compiler_semAn.getEnv(),
				interpreterPrototype,
				this,
				"semAn_codeToAdd",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_semAn },
				CyString.class, "String");
		if ( r == null ) {
			return null;
		}
		else {
			return new StringBuffer( ((CyString ) r).s);
		}
	}



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
	public List<Tuple2<String, StringBuffer>> afterResTypes_NewPrototypeList(ICompiler_afterResTypes compiler_afterResTypes) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_afterResTypes,
				compiler_afterResTypes.getEnv(),
				interpreterPrototype,
				this,
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
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
			List<ISlotSignature>>> infoList) {


		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				interpreterPrototype,
				this,
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
	}



	@Override
	public List<Tuple3<String, StringBuffer, Boolean>> afterResTypes_beforeMethodCodeList(
			ICompiler_afterResTypes compiler) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				interpreterPrototype,
				this,
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
	public List<Tuple2<String, String []>> afterResTypes_renameMethod(
			ICompiler_afterResTypes compiler) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				interpreterPrototype,
				this,
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
	public boolean runUntilFixedPoint() {

		return MetaHelper.interpreterFor_runUntilFixedPoint(interpreterPrototype, this);
	}

	InterpreterPrototype interpreterPrototype = null;

//	@Override
//	public InterpreterPrototype getInterpreterPrototype() {
//		return interpreterPrototype;
//	}
//
//	@Override
//	public void setInterpreterPrototype(InterpreterPrototype interpreterPrototype) {
//		this.interpreterPrototype = interpreterPrototype;
//	}
//
//
//	@Override
//	public CyanMetaobjectAtAnnot getCyanMetaobject() {
//		return this;
//	}
//
//
//	@Override
//	public String[] methodToInterpertList() {
//		return new String[] { "afterResTypes_codeToAdd", "semAn_codeToAdd", "semAn_NewPrototypeList",
//				"afterResTypes_NewPrototypeList", "runUntilFixedPoint", "afterResTypes_beforeMethodCodeList",
//				"afterResTypes_renameMethod", "afterResTypes_semAn_afterSemAn_shareInfoPrototype", "afterResTypes_semAn_afterSemAn_receiveInfoPrototype" };
//	}

}



