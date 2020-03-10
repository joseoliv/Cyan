package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afti;
import meta.ICompiler_afti;
import meta.ISlotInterface;
import meta.Token;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrAnnotationAt;
import meta.WrFieldDec;

/**
 * This metaobject takes a list of field names as parameters. It creates a
 * method init that initializes each field with a parameter.
   @author jose
 */
public class CyanMetaobjectInit extends CyanMetaobjectAtAnnot
       implements IAction_afti {

	public CyanMetaobjectInit() {
		super("init", AnnotationArgumentsKind.OneOrMoreParameters ,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC },
				Token.PRIVATE
		);
	}



	@Override
	public Tuple2<StringBuffer, String> afti_codeToAdd(
			ICompiler_afti compiler_afti, List<Tuple2<WrAnnotation, List<ISlotInterface>>> infoList) {


		String strSlotList = "    func init: ";
		final StringBuffer methodCode = new StringBuffer();
		methodCode.append("    func init: ");
		final List<WrFieldDec> varList = new ArrayList<>();

		final List<Object> javaParameterList = ((WrAnnotationAt ) metaobjectAnnotation).getJavaParameterList();
		int n = 1;
		for (final Object p : javaParameterList) {
			if ( ! (p instanceof String) ) {
				addError("parameter number " + n + " to this metaobject annotation is not a string");

				return null;
			}
			final String strParam = (String ) p;
			final WrFieldDec instVarDec = compiler_afti.searchField(strParam);
			if ( instVarDec == null ) {
				addError(strParam + " is not a field");
				return null;
			}
			varList.add(instVarDec);
			++n;
		}
		int size = varList.size();
		for (final WrFieldDec varDec : varList ) {
			String ss = " " + varDec.getType().getName() + " " + varDec.getName();
			methodCode.append(ss);
			strSlotList += ss;
			if ( --size > 0 ) {
				methodCode.append(", ");
				strSlotList += ", ";
			}
		}
		methodCode.append(" {\n" );
		for (final WrFieldDec varDec : varList ) {
			methodCode.append("        self." + varDec.getName() + " = " + varDec.getName() + ";\n");
		}
		methodCode.append("    }\n " );
		/*
		 * addMethod(CyanMetaobjectAtAnnot, String, String, String, StringBuffer)
		final List<Tuple3<String, String, StringBuffer>> tupleList = new ArrayList<>();
		tupleList.add(
				new Tuple3<String, String, StringBuffer>(
				    this.getMetaobjectAnnotation().getProgramUnit().getName(), "init:" + varList.size(), methodCode) );
		return tupleList;
		 */
		final List<StringBuffer> tupleList = new ArrayList<>();
		tupleList.add( methodCode );
		return new Tuple2<StringBuffer, String>(methodCode, strSlotList + ";");

	}

}
