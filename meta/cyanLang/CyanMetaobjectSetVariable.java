package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpp;
import meta.ICompiler_dpp;
import meta.MetaHelper;
import meta.WrAnnotationAt;
import meta.WrCyanPackage_dpp;
import meta.WrProgram_dpp;

/**
 * set program or package variable to a given value. Usage<br>
 *  *  <code>    {@literal @}setVariable(varName, value)</code><br>
 *
 *  This is the only metaobject of cyan.lang that has a name, 'setVariable', that does not is exactly the
 *  name after 'CyanMetaobject'.

   @author jose
 */
public class CyanMetaobjectSetVariable extends CyanMetaobjectAtAnnot
       implements IAction_dpp {

	public CyanMetaobjectSetVariable() {
		super("setVariable", AnnotationArgumentsKind.TwoParameters,
				new AttachedDeclarationKind [] { AttachedDeclarationKind.PROGRAM_DEC, AttachedDeclarationKind.PACKAGE_DEC
						} );
	}

	@Override
	public void check() {
		final WrAnnotationAt annotation = this.getAnnotation();
		final List<Object> paramList = annotation.getJavaParameterList();
		if ( ! (paramList.get(0) instanceof String) ) {
			addError("A variable name was expected as the first parameter to this metaobject annotation");
			return ;
		}
		else {
			final String varName = MetaHelper.removeQuotes((String ) paramList.get(0));
			final int size = varName.length();
			for (int i = 0; i < size; ++i) {
				final char ch = varName.charAt(i);
				if ( !Character.isAlphabetic(ch) && !Character.isDigit(ch) && ch != '_' ) {
					addError("Character '" + ch + "' is not allowed in a variable name. An identifier was expected as the first parameter to this metaobject annotation");
					return ;
				}
			}
		}
	}

	@Override
	public void dpp_action(ICompiler_dpp project) {

//		if ( this.getAttachedDeclaration() instanceof WrProgram ) {
//			WrProgram program = (WrProgram ) this.getAttachedDeclaration();
//			program.get
//		}
		final WrAnnotationAt annotation = this.getAnnotation();
		final List<Object> paramList = annotation.getJavaParameterList();
		final String key = MetaHelper.removeQuotes((String ) paramList.get(0));
		final Object value = paramList.get(1);
		if ( this.getAttachedDeclaration() instanceof WrProgram_dpp ) {
			final WrProgram_dpp program = (WrProgram_dpp ) this.getAttachedDeclaration();
			program.setProgramKeyValue(key, value);
		}
		else if ( this.getAttachedDeclaration() instanceof WrCyanPackage_dpp ){
			// a package
			final WrCyanPackage_dpp cp = (WrCyanPackage_dpp ) this.getAttachedDeclaration();
			cp.setPackageKeyValue(key, value);
		}
		else {
			this.addError("Error in metaobject class CyanMetaobjectSetVariable");
		}

	}

}
