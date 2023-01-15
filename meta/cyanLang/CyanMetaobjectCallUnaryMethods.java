package meta.cyanLang;

import java.util.regex.Pattern;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_semAn;
import meta.ICompiler_semAn;
import meta.WrAnnotationAt;
import meta.MetaHelper;


/**
 * This metaobject should be called as<br>
 * <code>callUnaryMethods(regexpr)</code><br>
 * It generates code to call all unary methods of the current prototype that match the
 * regular expression regexpr.
   @author jose
 */
public class CyanMetaobjectCallUnaryMethods extends CyanMetaobjectAtAnnot implements IAction_semAn {

	public CyanMetaobjectCallUnaryMethods() {
		super("callUnaryMethods", AnnotationArgumentsKind.OneParameter);
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		final WrAnnotationAt annotation = this.getAnnotation();
		final Object obj = annotation.getJavaParameterList().get(0);
		if ( !(obj instanceof String) ) {
			compiler_semAn.error(this.annotation.getFirstSymbol(), "A string, symbol, or identifier was expected as parameter");
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
			compiler_semAn.error(this.annotation.getFirstSymbol(),
					"Pattern is not well defined");
			return null;
		}


		final StringBuffer s = new StringBuffer();

		final Pattern pattern = Pattern.compile(code);

		final String currentMethodName = compiler_semAn.getEnv().getCurrentMethod().getName();
		for ( final String methodName : compiler_semAn.getUnaryMethodNameList() ) {
			if ( pattern.matcher(methodName).matches() && !currentMethodName.equals(methodName) ) {
				s.append("    " + methodName + ";\n");
			}
		}

		return s;
	}


}
