package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.WrAnnotationAt;
import meta.IInformCompilationError;
import meta.MetaHelper;
import meta.Tuple2;

/** This annotation takes two parameters. The first is a number. There should be
 * an error from the line of the annotation plus this number. The second parameter is the error message
 * that the compiler should issue.
 * <br>
 * {@literal @}cep(3, "syntax error") <br>
 * The line with the error is 10 if this metaobject is in line 7.
   @author jose
 */
public class CyanMetaobjectTestCompilerCEP extends CyanMetaobjectAtAnnot implements IInformCompilationError {

	public CyanMetaobjectTestCompilerCEP() {
		super("cep", AnnotationArgumentsKind.TwoParameters);

	}


	@Override
	public void check() {
		final WrAnnotationAt annotation = (WrAnnotationAt ) this.annotation;
		final List<Object> paramList = annotation.getJavaParameterList();

		/**
		 * the compiler should point an error at line number lineNumber
		 */
		int lineNumber;
		/**
		 * the id of the message is <code>id</code>
		 */
		final String id;
		if ( !(paramList.get(0) instanceof Integer) || !(paramList.get(1) instanceof String)  ) {
			addError("The first parameter to this metaobject should be an Int and the second parameter should be an identifier or a literal string");
			return ;
		}
		lineNumber = (Integer ) paramList.get(0) + this.annotation.getFirstSymbol().getLineNumber();
		/**
		 * the message that the compiler should issue (or similar to this)
		 */
		final String errorMessage = MetaHelper.removeQuotes((String ) paramList.get(1));


		this.info =
		// // this.metaobjectAnnotation.setInfo_parsing(
				new Tuple2<Integer, String>(lineNumber, errorMessage);
		// //		);
	}

	@Override
	public int getLineNumber() {
		return info.f1;
	}


	@Override
	public String getErrorMessage() {
		return info.f2;
	}

	private Tuple2<Integer, String> info;
}

