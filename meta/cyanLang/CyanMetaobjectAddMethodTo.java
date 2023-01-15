package meta.cyanLang;

import java.util.List;
import java.util.Set;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICommunicateInPrototype_afterResTypes_semAn_afterSemAn;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple4;
import meta.WrAnnotation;
import meta.WrEnv;

/**
 * This is a demonstration metaobject. <br>
 *   {@literal @}addMethodTo("methodToAdd")
 *
   @author jose
 */
public class CyanMetaobjectAddMethodTo extends CyanMetaobjectAtAnnot
        implements IAction_afterResTypes, ICommunicateInPrototype_afterResTypes_semAn_afterSemAn {

    public CyanMetaobjectAddMethodTo() {
        super("addMethodTo", AnnotationArgumentsKind.OneOrMoreParameters,
                new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC });
        commentBeforeMethod = null;
    }

    @Override
    public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(ICompiler_afterResTypes compiler,
            List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {
        final List<Object> parameterList = getAnnotation().getJavaParameterList();
        if (parameterList.size() != 2) {
            compiler.error(annotation.getFirstSymbol(),
                    "This metaobject annotation should have exactly one string parameters");
        }
        /*
         * check if the parameters are strings.
         */
        for (final Object obj : parameterList) {
            if (!(obj instanceof String)) {
                compiler.error(annotation.getFirstSymbol(),
                        "The parameters to this metaobject annotation should all be strings");
            }
        }
        /*
        String prototypeName = (String ) parameterList.get(0);
        prototypeName = MetaHelper.removeQuotes(prototypeName);
        */
        String methodName = (String) parameterList.get(0);
        methodName = MetaHelper.removeQuotes(methodName);
        String code = (String) parameterList.get(1);
        code = MetaHelper.removeQuotes(code);

        /*
        final List<Tuple3<String, String, StringBuffer>> tupleList = new ArrayList<>();
        tupleList.add( new Tuple3<String, String, StringBuffer>(prototypeName,
        		methodName, new StringBuffer( commentBeforeMethod +code )));
        return tupleList;
        */
        Tuple2<StringBuffer, String> t = new Tuple2<>(new StringBuffer(commentBeforeMethod + code),
                methodName);
        return t;

    }

    @Override
    public Object afterResTypes_semAn_afterSemAn_shareInfoPrototype(WrEnv env) {
        return "addMethodTo of line " + annotation.getAnnotationNumber();
    }

    @Override
    public void afterResTypes_semAn_afterSemAn_receiveInfoPrototype(
            Set<Tuple4<String, Integer, Integer, Object>> moInfoSet, WrEnv env) {
        commentBeforeMethod = "/*\n";
        for (final Tuple4<String, Integer, Integer, Object> t : moInfoSet) {
            if (t.f4 instanceof String) {
                commentBeforeMethod += "{ name:'" + t.f1 + "', #proto = '" + t.f2 + "', #kind = '" + t.f3
                        + "', info = '" + t.f4 + "'" + "} \n";
            }
        }
        commentBeforeMethod += "*/\n";
    }

    private String commentBeforeMethod;
}
