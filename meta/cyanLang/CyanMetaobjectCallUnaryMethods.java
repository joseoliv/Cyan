package meta.cyanLang;

import java.util.regex.Pattern;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dsa;
import meta.ICompiler_dsa;
import meta.WrAnnotationAt;
import meta.MetaHelper;


/**
 * This metaobject should be called as<br>
 * <code>callUnaryMethods(regexpr)</code><br>
 * It generates code to call all unary methods of the current prototype that match the
 * regular expression regexpr.
   @author jose
 */
public class CyanMetaobjectCallUnaryMethods extends CyanMetaobjectAtAnnot implements IAction_dsa {

	public CyanMetaobjectCallUnaryMethods() {
		super("callUnaryMethods", AnnotationArgumentsKind.OneParameter);
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		final WrAnnotationAt annotation = this.getMetaobjectAnnotation();
		final Object obj = annotation.getJavaParameterList().get(0);
		if ( !(obj instanceof String) ) {
			compiler_dsa.error(this.metaobjectAnnotation.getFirstSymbol(), "A string, symbol, or identifier was expected as parameter");
			return null;
		}

		String code = (String ) obj;
		code = MetaHelper.removeQuotes(code);
		/*
		if ( code.charAt(0) == '"' )
			code = code.substring(1);
		if ( code.endsWith("\"") )
			code = code.substring(0, code.length()-1);
		*/
		try {
			java.util.regex.Pattern.compile(code);
		}
		catch (final java.util.regex.PatternSyntaxException e) {
			compiler_dsa.error(this.metaobjectAnnotation.getFirstSymbol(),
					"Pattern is not well defined");
			return null;
		}


		final StringBuffer s = new StringBuffer();

		final Pattern pattern = Pattern.compile(code);

		final String currentMethodName = compiler_dsa.getEnv().getCurrentMethod().getName();
		for ( final String methodName : compiler_dsa.getUnaryMethodNameList() ) {
			if ( pattern.matcher(methodName).matches() && !currentMethodName.equals(methodName) ) {
				s.append("    " + methodName + ";\n");
			}
		}

		return s;
	}


}
