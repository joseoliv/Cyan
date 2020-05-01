package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.MetaHelper;
import meta.Tuple3;

/** This is a demonstration metaobject. <br>
 * Usage of this metaobject: use it with parameters that are the prototype, method name, and code that should be
 * added before the method. <br>
 * {@code
 *     @addBeforeMethod("run", "\"calling 'run'\" println;")
 * }
   @author jose
 */
public class CyanMetaobjectAddBeforeMethod extends CyanMetaobjectAtAnnot implements IAction_afterResTypes
{

	public CyanMetaobjectAddBeforeMethod() {
		super("addBeforeMethod", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC, AttachedDeclarationKind.METHOD_DEC });
	}


	@Override
	public List<Tuple3<String, StringBuffer, Boolean>> afterResTypes_beforeMethodCodeList(
			ICompiler_afterResTypes compiler) {
		final List<Object> parameterList = getAnnotation().getJavaParameterList();
		final List<Tuple3<String, StringBuffer, Boolean>> tupleList = new ArrayList<>();
		tupleList.add( new Tuple3<String, StringBuffer, Boolean>(
				MetaHelper.removeQuotes((String ) parameterList.get(0)),
				new StringBuffer( MetaHelper.removeQuotes( (String) parameterList.get(1))), false) );
		return tupleList;
	}


}
