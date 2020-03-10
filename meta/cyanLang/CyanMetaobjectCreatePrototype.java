package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionNewPrototypes_afti;
import meta.IActionNewPrototypes_dpa;
import meta.IAction_afti;
import meta.IAction_dpa;
import meta.ICompilerAction_dpa;
import meta.ICompiler_afti;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrAnnotationAt;
import meta.lexer.MetaLexer;

/**
 * A demonstration metaobject. It creates one or two prototypes in the current package given their names. It can be used
 * as<br>
 * <code>
 * {@literal @}createPrototype(prototypeName1, codeForPrototypeName1)
 * </code><br>
 * or <br>
 * <code>
 * {@literal @}createPrototype(prototypeName1, codeForPrototypeName1, prototypeName2, codeForPrototypeName2)
 * </code><br>
 * <code>prototypeName1</code> is created in phase 1, dpa, and <code>prototypeName2</code> is created in phase
 * 3, afti.
   @author jose
 */
public class CyanMetaobjectCreatePrototype extends CyanMetaobjectAtAnnot
       implements IAction_dpa, IAction_afti, IActionNewPrototypes_afti,
       IActionNewPrototypes_dpa {

	public CyanMetaobjectCreatePrototype() {
		super("createPrototype", AnnotationArgumentsKind.OneOrMoreParameters);
	}


	@Override
	public void check() {
		final WrAnnotationAt annotation = (WrAnnotationAt  ) this.metaobjectAnnotation;
		final List<Object> javaParamList = annotation.getJavaParameterList();
		if ( javaParamList.size() !=  4 && javaParamList.size() != 2 ) {
			addError("This metaobject annotation should have two or four parameters");
			return ;
		}
		if ( !(javaParamList.get(0) instanceof String) ||  !(javaParamList.get(1) instanceof String)
				|| !(javaParamList.get(2) instanceof String) || !(javaParamList.get(3) instanceof String) ) {
			addError("All parameters to this metaobject annotation should be strings");
			return ;
		}
	}

	@Override
	public List<Tuple2<String, StringBuffer>> dpa_NewPrototypeList(ICompilerAction_dpa compiler) {
		final WrAnnotationAt annotation = (WrAnnotationAt  ) this.metaobjectAnnotation;
		final List<Object> javaParamList = annotation.getJavaParameterList();

		final List<Tuple2<String, StringBuffer>> protoCodeList = new ArrayList<>();
		final String prototypeName = MetaHelper.removeQuotes((String ) javaParamList.get(0));
		final StringBuffer code = new StringBuffer( MetaHelper.removeQuotes((String ) javaParamList.get(1)) );
		//String escape = Lexer.escapeJavaString(code.toString());
		final String unescape = MetaLexer.unescapeJavaString(code.toString());
		protoCodeList.add( new Tuple2<String, StringBuffer>( prototypeName, new StringBuffer(unescape)));
		return protoCodeList;
	}

	@Override
	public List<Tuple2<String, StringBuffer>> afti_NewPrototypeList(
			ICompiler_afti compiler_afti) {

		final WrAnnotationAt annotation = (WrAnnotationAt  ) this.metaobjectAnnotation;
		final List<Object> javaParamList = annotation.getJavaParameterList();

		if ( javaParamList.size() ==  4 ) {
			final List<Tuple2<String, StringBuffer>> protoCodeList = new ArrayList<>();
			final String prototypeName = MetaHelper.removeQuotes((String ) javaParamList.get(2));
			final StringBuffer code = new StringBuffer( MetaHelper.removeQuotes((String ) javaParamList.get(3)) );
			//String escape = Lexer.escapeJavaString(code.toString());
			final String unescape = MetaLexer.unescapeJavaString(code.toString());
			protoCodeList.add( new Tuple2<String, StringBuffer>( prototypeName, new StringBuffer(unescape)));
			return protoCodeList;
		}
		else
			return null;


	}

}
