package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrAnnotationAt;

/**
    Annotation 'renameMethod' takes at least four parameters.
    The first one is the name of a method to be created got from
    method getNameWithoutParamNumber of WrMethodSignature or WrMethodDec.
    Something like <br>
    <code>
    """ func one: Int n <br>
         two: Int nn, Double d<br>
         three: String a, String b, String  c -> Int"""
    </code><br>
    The second is the code of this method and the third is the method
    name, something like <br>
    <code>"one:1 two:2 three:3"</code><br>
    The rest of the parameters are the new selectors for the method. Example:<br>
        <code>    {@literal @}renameMethod("at:1 with:2", "myAt:", "myWith:")</code><br>
    This annotation will rename method "at:1 with: 2" to "myAt:1 myWith:2".
    Overloaded methods cannot be renamed.
   @author jose
 */
public class CyanMetaobjectRenameMethod extends CyanMetaobjectAtAnnot implements IAction_afterResTypes
{

	public CyanMetaobjectRenameMethod() {
		super("renameMethod", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC });
	}

	@Override
	public void check() {
		final List<Object> parameterList = getAnnotation().getJavaParameterList();
		if ( parameterList.size() < 4 ) {
			this.addError("This metaobject should take at least four string parameters");
		}
		for ( Object obj : parameterList ) {
			if ( !(obj instanceof String) ) {
				this.addError("This metaobject should take only string parameters");
			}
		}

	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {
		final List<Object> parameterList = getAnnotation().getJavaParameterList();
		/*
		String prototypeName = (String ) parameterList.get(0);
		prototypeName = MetaHelper.removeQuotes(prototypeName);
		*/
		String methodName = (String ) parameterList.get(0);
		methodName = MetaHelper.removeQuotes(methodName);
		String code =  (String) parameterList.get(1);
		code = MetaHelper.removeQuotes(code);


		/*
		final List<Tuple3<String, String, StringBuffer>> tupleList = new ArrayList<>();
		tupleList.add( new Tuple3<String, String, StringBuffer>(prototypeName,
				methodName, new StringBuffer( commentBeforeMethod +code )));
		return tupleList;
		*/
		Tuple2<StringBuffer, String> t = new Tuple2<StringBuffer, String>(new StringBuffer( code ), methodName);
		return t;

	}



	@Override
	public List<Tuple2<String, String []>> afterResTypes_renameMethod(
			ICompiler_afterResTypes compiler_afterResTypes) {
		final List<Object> parameterList = getAnnotation().getJavaParameterList();
		final String []strList = new String[parameterList.size()-2];
		int j = 0;
		for (int i = 2; i < parameterList.size(); ++i) {
			strList[j] = meta.MetaHelper.removeQuotes((String ) parameterList.get(i));
			++j;
		}
		final String oldMethodName = strList[0];
		final String newMethodName[] = new String[strList.length-1];
		j = 0;
		for (int i = 1; i < strList.length; ++i) {
			newMethodName[j] = strList[i];
			++j;
		}
		final List<Tuple2<String, String []>> tupleList = new ArrayList<>();
		tupleList.add( new Tuple2<String, String []>(oldMethodName, newMethodName));
		return tupleList;
	}


}
