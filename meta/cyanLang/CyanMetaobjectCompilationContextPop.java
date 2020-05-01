package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectError;
import meta.CyanMetaobjectAtAnnot;
import meta.MetaHelper;
import meta.WrAnnotationAt;

/**
 * Annotations of this metaobject are only produced by the Cyan compiler. Users cannot use this annotation,
 * this would result in a compilation error. <br>
 *
 *  The annotations of this metaobject give information to later compiler phases on the
 *  code inserted by other metaobject. See the Cyan manual for more information and {@link ast#CyanMetaobjectCompilationContextPush}.
 *
 *  <br>
 *  The first parameter to this annotation should be an identifier that should match the same identifier given in
 *  {@link ast#CyanMetaobjectCompilationContextPush}. If there is three parameters, the second should be a package name
 *  and the third a prototype name. That would mean that the code between the annotations <br>
 *  {@link ast#CyanMetaobjectCompilationContextPush}<br>
 *  and <br>
 *  {@link ast#CyanMetaobjectCompilationContextPop}<br>
 *  is an expression whose type is package.prototype, the second and third parameters.
 *
   @author jose
 */
public class CyanMetaobjectCompilationContextPop extends CyanMetaobjectAtAnnot {

	public CyanMetaobjectCompilationContextPop() {
		super(MetaHelper.popCompilationContextName, AnnotationArgumentsKind.OneOrMoreParameters);
	}


	@Override
	public void check() {
		final List<Object> javaObjectList = this.getAnnotation().getJavaParameterList();

		boolean ok = false;
		if ( javaObjectList != null ) {
			if ( javaObjectList.size() == 1 && javaObjectList.get(0) instanceof String ) {
				ok = true;
			}
			else if ( javaObjectList.size() == 3 && javaObjectList.get(0) instanceof String && javaObjectList.get(1) instanceof String
					&& javaObjectList.get(2) instanceof String ) {
				ok = true;
			}
		}
		if ( ! ok ) {
			final WrAnnotationAt cyanAnnotation = this.getAnnotation();
			final List<CyanMetaobjectError> errorList = new ArrayList<>();
			errorList.add(new CyanMetaobjectError(cyanAnnotation.getFirstSymbol(),
					"Metaobject '" + this.getName() + "' should have one or three parameters"));
		}

	}

}
