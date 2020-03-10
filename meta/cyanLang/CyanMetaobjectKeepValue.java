package meta.cyanLang;

import java.util.Set;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.DirectoryKindPPP;
import meta.IAction_dsa;
import meta.ICommunicateInPrototype_afti_dsa_afsa;
import meta.ICompiler_dsa;
import meta.IVariableDecInterface;
import meta.MetaHelper;
import meta.Tuple4;
import meta.WrAnnotationAt;
import meta.WrEnv;


/**
 * This metaobject generates code to write to a file the value of a local or field that is its parameters.
 * In fact, the last five values of the variable are kept in the file, which is specific to every variable. The
 * file is <code>keepValue_varName.txt</code> in the directory <code>--tmp</code> of a directory whose name is the
 * prototype in which the annotation is.
   @author jose
 */
public class CyanMetaobjectKeepValue extends CyanMetaobjectAtAnnot
implements IAction_dsa,
ICommunicateInPrototype_afti_dsa_afsa {

	public CyanMetaobjectKeepValue() {
		super("keepValue", AnnotationArgumentsKind.OneParameter);
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler_dsa) {

		checkIfPackageWasImported(compiler_dsa, "cyan.io");
		final StringBuffer s = new StringBuffer();

		// AnnotationAt annotation = (ast.AnnotationAt ) this.metaobjectAnnotation;
		final WrAnnotationAt annotation = this.getMetaobjectAnnotation();
		final Object first = annotation.getJavaParameterList().get(0);
		boolean ok = true;
		if ( !(first instanceof String) ) {
			ok = false;
		}
		else {
			final String p = MetaHelper.removeQuotes((String ) first);
			IVariableDecInterface v = compiler_dsa.searchLocalVariableParameter(p);
			if ( v == null ) {
				v = compiler_dsa.searchField(p);
			}
			if ( v == null ) {
				ok = false;
			}
			else {
				final String filename = CyanMetaobject.escapeString(compiler_dsa.getPathFileHiddenDirectory(
						annotation.getPrototypeOfAnnotation(),
						annotation.getPackageOfAnnotation(),
						DirectoryKindPPP.TMP) +  "keepValue_" + v.getName() + ".txt");

				s.append("OutTextFile open: \"" + filename + "\"\n" +
						"   		  maxNumLines: 5\n" +
						"             write: " + p + " asString ++ \"\\n\"\n" +
						"             catch: CatchExceptionIOMessage;\n");
			}
		}

		if ( !ok ) {
			this.addError("A variable name was expected as parameter to this metaobject");
		}
		return s;
	}

	@Override
	public Object afti_dsa_afsa_shareInfoPrototype(WrEnv env)  {
		final WrAnnotationAt annotation = this.getMetaobjectAnnotation();
		final Object first = annotation.getJavaParameterList().get(0);
		return first;
	}

	@Override
	public void afti_dsa_afsa_receiveInfoPrototype(Set<Tuple4<String, Integer, Integer, Object>> moInfoSet, WrEnv env)  {
		final WrAnnotationAt annotation = this.getMetaobjectAnnotation();
		if ( annotation.getJavaParameterList() == null || annotation.getJavaParameterList().size() == 0 )
			return ;
		final Object first = annotation.getJavaParameterList().get(0);
		int count = 0;
		for ( final Tuple4<String, Integer, Integer, Object> t : moInfoSet ) {
			if ( t.f1.equals("keepValue") && t.f4.equals(first) ) {
				++count;
			}
		}

		if ( count > 1 && annotation.getMetaobjectAnnotationNumberByKind() == 1 ) {
			this.addError("There is more than one annotation of metaobject keepValue with the same parameters");
		}
	}
}