package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import cyan.lang.CyString;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GP_CyBoolean_GT__GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT__GT;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP__Array_LT_GP_CyString_GT__GT__GT;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_Object_GP_Object_GP_Object_GP_Object_GT__GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.IAbstractCyanCompiler;
import meta.IActionNewPrototypes_afti;
import meta.IActionNewPrototypes_dsa;
import meta.IAction_afti;
import meta.IAction_dpa;
import meta.IAction_dsa;
import meta.ICommunicateInPrototype_afti_dsa_afsa;
import meta.ICompilerAction_dpa;
import meta.ICompiler_afti;
import meta.ICompiler_dsa;
import meta.ISlotInterface;
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
<p>This metaobject works like CyanMetaobjectAction_afti_dsa but the <em>Myan</em> code is read from the file that is the first parameter. So, instead of putting the <em>Myan</em> code attached to the annotation, it is read from the file, which may be preceded by a package name. The file should have extension <code>myan</code> and be in the `–data´ directory of the package. Usage example:</p>
<pre><code>	{@literal @}runFile_afti_dsa("runFile_afti_dsa.afti_dsa_test",
	       "with:1 do:1", "unary", 10)
</code></pre>
<p>The <em>Myan</em> file can have parameters in its name. They are textually replaced by the parameters of the annotation (but the first, that is the file name). Then, in this example, there should be a file</p>
<pre><code>	afti_dsa_test(MetSig,UMS,Ret).myan
</code></pre>
<p>in directory <code>runFile_afti_dsa\--data</code>. Inside the file,  strings <code>Ret</code>, the third formal parameter, are replaced by <code>10</code>. The same with the other parameters.</p>
<blockquote>
<p>Written with <a href="https://stackedit.io/">StackEdit</a>.</p>
</blockquote>


 */
public class CyanMetaobjectRunFile extends CyanMetaobjectAtAnnot
			implements // IInterpreterMethods_afti,
			IAction_dpa,
			IAction_dsa, IActionNewPrototypes_dsa, IAction_afti, IActionNewPrototypes_afti,
				ICommunicateInPrototype_afti_dsa_afsa
			{

	public CyanMetaobjectRunFile() {
		super("runFile", AnnotationArgumentsKind.OneOrMoreParameters );
	}

	@Override
	public void check() {
		if ( ! ( ((WrAnnotationAt ) metaobjectAnnotation).getRealParameterList().get(0) instanceof WrExprLiteralString)
				// || ((WrAnnotationAt ) metaobjectAnnotation).getRealParameterList().size() != 1
				)  {
			addError("This metaobject annotation should have at least one parameter");
			return ;
		}
//		for ( WrExprAnyLiteral p : ((WrAnnotationAt ) metaobjectAnnotation).getRealParameterList() ) {
//			if ( !(p instanceof WrExprLiteralString) ) {
//				addError("The parameters to this metaobject annotation should have type String");
//			}
//		}
	}


	private Tuple3<String, String, List<String>> extractPackageFileNameParam(String currentPackageName) {
		String cyanFileName = MetaHelper.removeQuotes( (String )
				((WrAnnotationAt ) metaobjectAnnotation).getJavaParameterList().get(0));

		List<WrExprAnyLiteral> wrParamList = ((WrAnnotationAt ) metaobjectAnnotation).getRealParameterList();
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
	public  StringBuffer dpa_codeToAdd(ICompilerAction_dpa cp) {

		Tuple3<String, String, List<String>> t = this.extractPackageFileNameParam(cp.getCompilationUnit().getPackageName());
		String packageName1 = t.f1;
		String filename = t.f2;
		List<String> paramNameList = t.f3;

		String msg = loadCyanInterpreterPrototypes(cp, packageName1, filename,
				paramNameList);
		if ( msg != null ) {
			this.addError(this.getMetaobjectAnnotation().getFirstSymbol(), msg);
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
//				cp.error(this.getMetaobjectAnnotation().getFirstSymbol(),
//						"Internal error in metaobject '" + this.getClass().getName() + "'");
				return "Internal error in metaobject '" + this.getClass().getName() + "'";
			}
			else {
				if ( t.f1 != null ) {
					//cp.error(this.getMetaobjectAnnotation().getFirstSymbol(), t.f1);
					return t.f1;
				}
				else {
					interpreterPrototype = t.f2;
					//interpreterPrototype = t.f2;
					for ( String key : t.f2.mapMethodName_Body.keySet() ) {
						switch ( key ) {
						case "afti_codeToAdd":
						case "dsa_codeToAdd":
						case "dsa_NewPrototypeList":
						case "afti_NewPrototypeList":
						case "runUntilFixedPoint":
						case "afti_beforeMethodCodeList":
						case "afti_renameMethod":
						case "afti_dsa_afsa_shareInfoPrototype":
						case "afti_dsa_afsa_receiveInfoPrototype":
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



	private boolean loadCode(ICompiler_dsa compiler_dsa) {
		if ( this.interpreterPrototype == null ) {
			Tuple3<String, String, List<String>> t = this.extractPackageFileNameParam(
					compiler_dsa.getEnv().getCurrentCompilationUnit().getPackageName());
			String packageName1 = t.f1;
			String filename = t.f2;
			List<String> paramNameList = t.f3;

			String msg = loadCyanInterpreterPrototypes(compiler_dsa, packageName1, filename,
					paramNameList);
			if ( msg != null ) {
				this.addError(this.getMetaobjectAnnotation().getFirstSymbol(), msg);
				return false;
			}
		}
		return true;
	}



	@Override
	public List<Tuple2<String, StringBuffer>> dsa_NewPrototypeList(ICompiler_dsa compiler_dsa) {

		if ( !loadCode(compiler_dsa) ) { return null; }

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_dsa,
				compiler_dsa.getEnv(),
				interpreterPrototype,
				this,
				"dsa_NewPrototypeList",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_dsa },
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
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {


		if ( !loadCode(compiler_dsa) ) { return null; }

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_dsa,
				compiler_dsa.getEnv(),
				interpreterPrototype,
				this,
				"dsa_codeToAdd",
				   // "env" is added by the called method
				new String [] { "compiler" }, new Object [] { compiler_dsa },
				CyString.class, "String");
		if ( r == null ) {
			return null;
		}
		else {
			return new StringBuffer( ((CyString ) r).s);
		}
	}



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
	public List<Tuple2<String, StringBuffer>> afti_NewPrototypeList(ICompiler_afti compiler_afti) {
		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler_afti,
				compiler_afti.getEnv(),
				interpreterPrototype,
				this,
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
	public Tuple2<StringBuffer, String> afti_codeToAdd(
			ICompiler_afti compiler, List<Tuple2<WrAnnotation,
			List<ISlotInterface>>> infoList) {


		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				interpreterPrototype,
				this,
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
	}



	@Override
	public List<Tuple3<String, StringBuffer, Boolean>> afti_beforeMethodCodeList(
			ICompiler_afti compiler) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				interpreterPrototype,
				this,
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
	public List<Tuple2<String, String []>> afti_renameMethod(
			ICompiler_afti compiler) {

		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
				compiler,
				compiler.getEnv(),
				interpreterPrototype,
				this,
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
//		return new String[] { "afti_codeToAdd", "dsa_codeToAdd", "dsa_NewPrototypeList",
//				"afti_NewPrototypeList", "runUntilFixedPoint", "afti_beforeMethodCodeList",
//				"afti_renameMethod", "afti_dsa_afsa_shareInfoPrototype", "afti_dsa_afsa_receiveInfoPrototype" };
//	}

}



