package meta.cyanLang;

import java.util.List;
import java.util.HashMap;
import java.util.function.Function;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpa;
import meta.IAction_dsa;
import meta.ICompilerAction_dpa;
import meta.ICompiler_dsa;
import meta.MetaHelper;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.lexer.MetaLexer;

/**
 * A metaobject annotation of this metaobject supports the parameters filename, prototypename, packagename, linenumber,
 *  columnNumber, localvariablelist, fieldlist, signatureallmethodslist, currentmethodname,
 *  currentmethodfullname, currentmethodreturntypename. For example,<br>
 *  <code>     @compilationInfo("currentmethodreturntypename")<br>
 *  </code> is an expression, a string whose contents is the full name of the return value type of the current method.
 *
   @author José
 */
public class CyanMetaobjectCompilationInfo extends CyanMetaobjectAtAnnot
       implements IAction_dpa, IAction_dsa {

	private static HashMap<String, Function<WrAnnotationAt, String>> moToFuncMap_dpa = new HashMap<>();
	private static HashMap<String, Function<WrEnv, String>> moToFuncMap_dsa = new HashMap<>();
	private static HashMap<String, String> moToTypeMap = new HashMap<>();
	static {
		moToFuncMap_dpa.put("filename", (WrAnnotationAt cyanMetaobjectAnnotation) -> {
			return cyanMetaobjectAnnotation.getCompilationUnit().getFullFileNamePath(); });

		moToFuncMap_dpa.put("prototypename", (WrAnnotationAt cyanMetaobjectAnnotation) -> {
			return cyanMetaobjectAnnotation.getPrototypeOfAnnotation(); });

		moToFuncMap_dpa.put("packagename", (WrAnnotationAt cyanMetaobjectAnnotation) -> {
    		return cyanMetaobjectAnnotation.getPackageOfAnnotation(); });
		moToFuncMap_dpa.put("linenumber", (WrAnnotationAt cyanMetaobjectAnnotation) -> {
			return "" + cyanMetaobjectAnnotation.getSymbolMetaobjectAnnotation().getLineNumber(); });
		moToFuncMap_dpa.put("columnnumber", (WrAnnotationAt cyanMetaobjectAnnotation) -> {
			return "" + cyanMetaobjectAnnotation.getSymbolMetaobjectAnnotation().getColumnNumber(); });


		moToFuncMap_dsa.put("localvariablelist", (WrEnv env) -> {
			return env.getStringVisibleLocalVariableList(); });

		moToFuncMap_dsa.put("fieldlist", (WrEnv env) -> {
			return env.getStringFieldList(); });

		moToFuncMap_dsa.put("signatureallmethodslist", (WrEnv env) -> {
			return env.getStringSignatureAllMethods(); });

		moToFuncMap_dsa.put("currentmethodname", (WrEnv env) -> {
			return env.getCurrentMethod() != null ? env.getCurrentMethod().getName() : "no current method" ; });

		moToFuncMap_dsa.put("currentmethodfullname", (WrEnv env) -> {
			return env.getCurrentMethod() != null ? env.getCurrentMethod().getMethodSignature().getFullName(env) : "no current method" ; });
		moToFuncMap_dsa.put("currentmethodreturntypename", (WrEnv env) -> {
			return env.getCurrentMethod() != null ? env.getCurrentMethod().getMethodSignature().getReturnType(env).getFullName() : "no current method" ; });


		moToTypeMap.put("filename", "String");
		moToTypeMap.put("prototypename", "String");
		moToTypeMap.put("packagename", "String");
		moToTypeMap.put("linenumber", "Int");
		moToTypeMap.put("columnnumber", "Int");
		moToTypeMap.put("localvariablelist", "Array<String>");
		moToTypeMap.put("fieldlist", "Array<String>");
		moToTypeMap.put("signatureallmethodslist", "Array<String>");
		moToTypeMap.put("currentmethodname", "String");
		moToTypeMap.put("currentmethodfullname", "String");
		moToTypeMap.put("currentmethodreturntypename", "String");


	}

	public CyanMetaobjectCompilationInfo() {
		super("compilationInfo", AnnotationArgumentsKind.OneParameter);
	}


	@Override
	public void check() {
		final WrAnnotationAt cyanMetaobjectAnnotation = this.getMetaobjectAnnotation();
		final List<Object> javaObjectList = cyanMetaobjectAnnotation.getJavaParameterList();
		if ( javaObjectList.size() != 1 || !(javaObjectList.get(0) instanceof String)) {
			addError("A single identifier or a single string was expected as parameter to this metaobject");
			return ;
		}
		String param = (String ) javaObjectList.get(0);
		param = MetaHelper.removeQuotes(param).toLowerCase();
		param = param.replaceAll(" ", "");
		if ( moToFuncMap_dpa.get(param) == null && moToFuncMap_dsa.get(param) == null) {
			String supportedOptions = "";
			for (final String s : moToFuncMap_dpa.keySet() )
				supportedOptions += "'" + s + "' ";
			addError("Only the parameters " + supportedOptions + " are supported.");
		}
	}

	@Override
	public StringBuffer dpa_codeToAdd( ICompilerAction_dpa compiler ) {
		final WrAnnotationAt cyanMetaobjectAnnotation = this.getMetaobjectAnnotation();

		data = null;
		String param = (String ) cyanMetaobjectAnnotation.getJavaParameterList().get(0);
		param = MetaHelper.removeQuotes(param).toLowerCase();
		param = param.replaceAll(" ", "");
		final Function<WrAnnotationAt, String> f = moToFuncMap_dpa.get(param);
		if ( f == null ) {
			return null;
		}
		data = f.apply(cyanMetaobjectAnnotation);
		if ( data == null )
			return null;
		else {
			final String value = moToTypeMap.get(param);
			if ( value.equals("Int") ) {
				return new StringBuffer(data);
			}
			else {
				data = MetaLexer.escapeJavaString(data);
				return new StringBuffer("\"" + data + "\"");
			}

		}
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		if ( data != null ) {
			return null;
		}
		else {
			final WrAnnotationAt cyanMetaobjectAnnotation = this.getMetaobjectAnnotation();

			String param = (String ) cyanMetaobjectAnnotation.getJavaParameterList().get(0);
			param = MetaHelper.removeQuotes(param).toLowerCase();
			param = param.replaceAll(" ", "");

			final Function<WrEnv, String> f = moToFuncMap_dsa.get(param);
			if ( f == null )
				return null;
			data = f.apply(compiler_dsa.getEnv());
			if (  data != null ) {

				final String value = moToTypeMap.get(param);
				if ( value.equals("Int") ) {
					return new StringBuffer(data);
				}
				else {
					if ( value.startsWith("Array<") ) {
						return new StringBuffer(data);
					}
					else {
						data = MetaLexer.escapeJavaString(data);
						return new StringBuffer("\"" + data + "\"");
					}
				}
			}
			else
				return null;
		}
	}


	@Override
	public String getPackageOfType() { return "cyan.lang"; }
	/**
	 * If the metaobject annotation has type <code>packageName.prototypeName</code>, this method returns
	 * <code>prototypeName</code>.  See {@link CyanMetaobjectLiteralObject#getPackageOfType()}
	   @return
	 */

	@Override
	public String getPrototypeOfType() {
		final WrAnnotationAt cyanMetaobjectAnnotation = this.getMetaobjectAnnotation();
		final List<Object> jpList = cyanMetaobjectAnnotation.getJavaParameterList();

		if ( jpList == null || jpList.size() == 0 )
			return "Nil";

		String param = (String ) cyanMetaobjectAnnotation.getJavaParameterList().get(0);
		param = MetaHelper.removeQuotes(param).toLowerCase();
		param = param.replaceAll(" " , "");
		final String type = moToTypeMap.get(param);
		return type == null ? "Nil" : type;
	}

	@Override
	public boolean isExpression() {
		return true;
	}



	private String data;
}
