package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionNewPrototypes_afterResTypes;
import meta.IActionNewPrototypes_parsing;
import meta.IAction_parsing;
import meta.ICompilerAction_parsing;
import meta.ICompiler_afterResTypes;
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
 * <code>prototypeName1</code> is created in phase 1, parsing, and <code>prototypeName2</code> is created in phase
 * 3, AFTER_RES_TYPES.
   @author jose
 */
public class CyanMetaobjectCreatePrototype extends CyanMetaobjectAtAnnot
       implements IAction_parsing, IActionNewPrototypes_afterResTypes,
       IActionNewPrototypes_parsing {

	public CyanMetaobjectCreatePrototype() {
		super("createPrototype", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROGRAM_DEC,
						AttachedDeclarationKind.METHOD_DEC
				});
	}


	@Override
	public void check() {
		final WrAnnotationAt annotation = (WrAnnotationAt  ) this.annotation;
		final List<Object> javaParamList = annotation.getJavaParameterList();
		if ( javaParamList.size() !=  4 && javaParamList.size() != 2 ) {
			addError("This metaobject annotation should have two or four parameters");
			return ;
		}
		boolean ok = true;
		if ( !(javaParamList.get(0) instanceof String) ||
				!(javaParamList.get(1) instanceof String)
				 ) {
		}
		if ( javaParamList.size() ==  4  ) {
			if (!(javaParamList.get(2) instanceof String) || !(javaParamList.get(3) instanceof String) ) {
				ok = false;
			}
		}
		if ( !ok ) {
			addError("All parameters to this metaobject annotation should be strings");
		}
	}

	@Override
	public List<Tuple2<String, StringBuffer>> parsing_NewPrototypeList(ICompilerAction_parsing compiler) {
		final WrAnnotationAt annotation = (WrAnnotationAt  ) this.annotation;
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
	public List<Tuple2<String, StringBuffer>> afterResTypes_NewPrototypeList(
			ICompiler_afterResTypes compiler_afterResTypes) {

		final WrAnnotationAt annotation = (WrAnnotationAt  ) this.annotation;
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
