package meta.cyanLang;

import java.util.List;
import java.util.Set;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afti;
import meta.ICommunicateInPrototype_afti_dsa_afsa;
import meta.ICompiler_afti;
import meta.ISlotInterface;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple4;
import meta.WrAnnotation;
import meta.WrAnnotationAt;
import meta.WrEnv;


/**
 * This is a demonstration metaobject. <br>
 *   {@literal @}addMethodTo("methodToAdd")
 *
   @author jose
 */
public class CyanMetaobjectAddMethodTo extends CyanMetaobjectAtAnnot
      implements IAction_afti, ICommunicateInPrototype_afti_dsa_afsa
{

	public CyanMetaobjectAddMethodTo() {
		super("addMethodTo", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC });
		commentBeforeMethod = null;
	}


	@Override
	public Tuple2<StringBuffer, String> afti_codeToAdd(
			ICompiler_afti compiler, List<Tuple2<WrAnnotation, List<ISlotInterface>>> infoList) {
		final List<Object> parameterList = ((WrAnnotationAt ) metaobjectAnnotation).getJavaParameterList();
		if ( parameterList.size() != 2 ) {
			compiler.error(metaobjectAnnotation.getFirstSymbol(),
					"This metaobject annotation should have exactly one string parameters");
		}
		/*
		 * check if the parameters are strings.
		 */
		for ( final Object obj : parameterList )
			if ( ! (obj instanceof String) ) {
				compiler.error(metaobjectAnnotation.getFirstSymbol(),
						"The parameters to this metaobject annotation should all be strings");
			}
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
		Tuple2<StringBuffer, String> t = new Tuple2<StringBuffer, String>(new StringBuffer( commentBeforeMethod +code ), methodName);
		return t;

	}


	@Override
	public
	Object afti_dsa_afsa_shareInfoPrototype(WrEnv env) {
		return "addMethodTo of line " + metaobjectAnnotation.getMetaobjectAnnotationNumber();
	}

	@Override
	public
	void afti_dsa_afsa_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> moInfoSet, WrEnv env) {
		commentBeforeMethod = "/*\n";
		for ( final Tuple4<String, Integer, Integer, Object> t : moInfoSet ) {
			if ( t.f4 instanceof String )
				commentBeforeMethod += "{ name:'" + t.f1 + "', #proto = '" + t.f2 + "', #kind = '" + t.f3 + "', info = '" + t.f4 + "'" + "} \n";
		}
		commentBeforeMethod += "*/\n";
	}




	private String commentBeforeMethod;
}
