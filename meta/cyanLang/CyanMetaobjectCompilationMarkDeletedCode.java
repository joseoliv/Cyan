package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.MetaHelper;

/**
	Annotations of this metaobject are only produced by the Cyan compiler. Users cannot use this annotation,
 * this would result in a compilation error. <br>
 *
 *  The annotations of this metaobject give information to later compiler phases on the
 *  code inserted by other metaobject.
 *  An annotation <br>
 *  <code>
 *      {@literal @}markDeletedCode(4)
 *  </code><br>
 *  means that a metaobject annotation in phase 6 (SEM_AN) that occupies 4 lines was replaced by code that the metaobject
 *  produced. That is, 4 lines were deleted and code was added by the metaobject.
 *
 *  See the Cyan manual for more information and {@link ast#CyanMetaobjectCompilationContextPush}.
 *   @author José
 */
public class CyanMetaobjectCompilationMarkDeletedCode extends meta.CyanMetaobjectAtAnnot {

	public CyanMetaobjectCompilationMarkDeletedCode() {
		super(MetaHelper.markDeletedCodeName, AnnotationArgumentsKind.OneParameter);
	}


	@Override
	public void check() {
		List<Object> javaObjectList = this.getAnnotation().getJavaParameterList();
		if ( javaObjectList == null || ! ( javaObjectList.get(0) instanceof Integer) ) {
			addError("Metaobject '" + getName() + "' should have exactly one Int parameter");
			return ;
		}
		this.numLinesDeleted = (Integer ) javaObjectList.get(0);
	}

	private int	numLinesDeleted;

	public int getNumLinesDeleted() {
		return numLinesDeleted;
	}



}
