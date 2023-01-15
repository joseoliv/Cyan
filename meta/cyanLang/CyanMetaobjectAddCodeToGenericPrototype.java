package meta.cyanLang;

import java.util.List;
import ast.AnnotationAt;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFunction;
import meta.IAction_afterResTypes;
import meta.IStayPrototypeInterface;
import meta.Token;
import meta.Tuple3;

public class CyanMetaobjectAddCodeToGenericPrototype extends CyanMetaobjectAtAnnot
     implements IActionFunction,
                IAction_afterResTypes,  IStayPrototypeInterface {

	public CyanMetaobjectAddCodeToGenericPrototype() {
		super("addCodeToGenericPrototype", AnnotationArgumentsKind.TwoParameters, new AttachedDeclarationKind[] {
				AttachedDeclarationKind.PROTOTYPE_DEC }, Token.PUBLIC);
	}

	@Override
	public void check() {
		final AnnotationAt cyanAnnotation = meta.GetHiddenItem.getHiddenCyanMetaobjectWithAtAnnotation(this.getAnnotation());
		final List<Object> javaObjectList = cyanAnnotation.getJavaParameterList();
		if ( javaObjectList.size() != 2 || !(javaObjectList.get(0) instanceof String)
				|| !(javaObjectList.get(1) instanceof String)) {
			addError("A single identifier or a single string was expected as the first "
					+ "parameter to this metaobject followed by a string");
			return ;
		}
		tag = (String ) javaObjectList.get(0);
		tag = removeQuotes(tag);
		slotList = meta.MetaHelper.removeQuotes( (String ) javaObjectList.get(1));
	}

	@Override
	public boolean shouldTakeText() { return true; }

	/*
	@Override
	public Tuple2<String, Object> afterResTypes_getInfoFromReceiverMetaobject(
			CyanMetaobjectAtAnnot cyanMetaobject) {

		return new Tuple2<String, Object>( tag,
				new StringBuffer( new String(this.getAnnotation().getTextAttachedDSL())));
	}
	*/
	@Override
	public 	Object eval(Object input) {
		return new Tuple3<String, String, Object>( tag, slotList,
				new StringBuffer( new String(this.getAnnotation().getTextAttachedDSL())));

	}

	/**
	 * the tag of the code to be added. It may be "Array" or "Array2"
	 */
	private String tag;
	/**
	 * list of fields and methods to be added.
	 */
	private String slotList;
}
