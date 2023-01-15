package meta.cyanLang;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.CyanMetaobjectLiteralObject;
import meta.IAction_parsing;
import meta.IAction_semAn;
import meta.ICompilerAction_parsing;
import meta.ICompiler_semAn;
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
       implements IAction_parsing, IAction_semAn {

	private static HashMap<String, Function<WrAnnotationAt, String>> moToFuncMap_parsing = new HashMap<>();
	private static HashMap<String, Function<WrEnv, String>> moToFuncMap_semAn = new HashMap<>();
	private static HashMap<String, String> moToTypeMap = new HashMap<>();
	static {
		moToFuncMap_parsing.put("filename", (WrAnnotationAt cyanAnnotation) -> {
			return cyanAnnotation.getCompilationUnit().getFullFileNamePath(); });

		moToFuncMap_parsing.put("prototypename", (WrAnnotationAt cyanAnnotation) -> {
			return cyanAnnotation.getPrototypeOfAnnotation(); });

		moToFuncMap_parsing.put("packagename", (WrAnnotationAt cyanAnnotation) -> {
    		return cyanAnnotation.getPackageOfAnnotation(); });
		moToFuncMap_parsing.put("linenumber", (WrAnnotationAt cyanAnnotation) -> {
			return "" + cyanAnnotation.getSymbolAnnotation().getLineNumber(); });
		moToFuncMap_parsing.put("columnnumber", (WrAnnotationAt cyanAnnotation) -> {
			return "" + cyanAnnotation.getSymbolAnnotation().getColumnNumber(); });


		moToFuncMap_semAn.put("localvariablelist", (WrEnv env) -> {
			return env.getStringVisibleLocalVariableList(); });

		moToFuncMap_semAn.put("fieldlist", (WrEnv env) -> {
			return env.getStringFieldList(); });

		moToFuncMap_semAn.put("signatureallmethodslist", (WrEnv env) -> {
			return env.getStringSignatureAllMethods(); });

		moToFuncMap_semAn.put("currentmethodname", (WrEnv env) -> {
			return env.getCurrentMethod() != null ? env.getCurrentMethod().getName() : "no current method" ; });

		moToFuncMap_semAn.put("currentmethodfullname", (WrEnv env) -> {
			return env.getCurrentMethod() != null ? env.getCurrentMethod().getMethodSignature().getFullNameWithReturnType(env) : "no current method" ; });
		moToFuncMap_semAn.put("currentmethodreturntypename", (WrEnv env) -> {
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
		final WrAnnotationAt cyanAnnotation = this.getAnnotation();
		final List<Object> javaObjectList = cyanAnnotation.getJavaParameterList();
		if ( javaObjectList.size() != 1 || !(javaObjectList.get(0) instanceof String)) {
			addError("A single identifier or a single string was expected as parameter to this metaobject");
			return ;
		}
		String param = (String ) javaObjectList.get(0);
		param = MetaHelper.removeQuotes(param).toLowerCase();
		param = param.replaceAll(" ", "");
		if ( moToFuncMap_parsing.get(param) == null && moToFuncMap_semAn.get(param) == null) {
			String supportedOptions = "";
			for (final String s : moToFuncMap_parsing.keySet() )
				supportedOptions += "'" + s + "' ";
			addError("Only the parameters " + supportedOptions + " are supported.");
		}
	}

	@Override
	public StringBuffer parsing_codeToAdd( ICompilerAction_parsing compiler ) {
		final WrAnnotationAt cyanAnnotation = this.getAnnotation();

		data = null;
		String param = (String ) cyanAnnotation.getJavaParameterList().get(0);
		param = MetaHelper.removeQuotes(param).toLowerCase();
		param = param.replaceAll(" ", "");
		final Function<WrAnnotationAt, String> f = moToFuncMap_parsing.get(param);
		if ( f == null ) {
			return null;
		}
		data = f.apply(cyanAnnotation);
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
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		if ( data != null ) {
			return null;
		}
		else {
			final WrAnnotationAt cyanAnnotation = this.getAnnotation();

			String param = (String ) cyanAnnotation.getJavaParameterList().get(0);
			param = MetaHelper.removeQuotes(param).toLowerCase();
			param = param.replaceAll(" ", "");

			final Function<WrEnv, String> f = moToFuncMap_semAn.get(param);
			if ( f == null )
				return null;
			data = f.apply(compiler_semAn.getEnv());
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
		final WrAnnotationAt cyanAnnotation = this.getAnnotation();
		final List<Object> jpList = cyanAnnotation.getJavaParameterList();

		if ( jpList == null || jpList.size() == 0 )
			return "Nil";

		String param = (String ) cyanAnnotation.getJavaParameterList().get(0);
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
