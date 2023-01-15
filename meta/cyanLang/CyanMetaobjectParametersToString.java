package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_semAn;
import meta.ICompiler_semAn;
import meta.WrAnnotationAt;
import meta.MetaHelper;
import meta.lexer.MetaLexer;

public class CyanMetaobjectParametersToString extends CyanMetaobjectAtAnnot implements IAction_semAn {

	public CyanMetaobjectParametersToString() {
		super("parametersToString", AnnotationArgumentsKind.OneOrMoreParameters);
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn)  {
		final WrAnnotationAt annotation = this.getAnnotation();
		final StringBuffer s = new StringBuffer("\"");
		for ( final Object obj : annotation.getJavaParameterList() ) {
			s.append(MetaLexer.escapeJavaString(convert(obj)));
			s.append("\\n");
		}
		s.append("\\n" + MetaLexer.escapeJavaString(new String(annotation.getTextAttachedDSL())) + "\\n");
		s.append("\"");
		return s;
	}

	private String convert(Object top) {
		if ( top instanceof Object [] ) {
			String s = "[ ";
			final Object []objArray = (Object []) top;
			int size = objArray.length;
			for ( final Object obj : objArray ) {
				s += convert(obj);
				if ( --size > 0 ) {
					s += ", ";
				}
			}
			return s + " ]";
		}
		else
			return "" + top;
	}

	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public boolean isExpression() {
		return true;
	}



	@Override
	public String getPackageOfType() { return MetaHelper.cyanLanguagePackageName; }
	/**
	 * If the metaobject annotation has type <code>packageName.prototypeName</code>, this method returns
	 * <code>prototypeName</code>.  See {@link CyanMetaobjectLiteralObject#getPackageOfType()}
	   @return
	 */

	@Override
	public String getPrototypeOfType() { return "String"; }
}