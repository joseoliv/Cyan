package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpp;
import meta.ICompiler_dpp;
import meta.WrAnnotationAt;
import meta.WrCyanPackage_dpp;
import meta.WrProgram_dpp;

/** This metaobject should be used in the Pyan project file only. It adds a pair (varName, value)
 * to a map associated to the project. <br>
 * Usage:<br>
 *  <code>    addToSet(varName, value)</code><br>
 *
 *
   @author jose
 */
public class CyanMetaobjectAddToProjectVariableSet extends CyanMetaobjectAtAnnot
		implements IAction_dpp {


	public CyanMetaobjectAddToProjectVariableSet() {
		super("addToSet", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind [] { AttachedDeclarationKind.PROGRAM_DEC,
						AttachedDeclarationKind.PACKAGE_DEC
		    } );
	}

	@Override
	public void check() {
		final WrAnnotationAt annotation = this.getAnnotation();
		final List<Object> paramList = annotation.getJavaParameterList();
		if ( paramList.size() < 2 ) {
			this.addError("This annotation should have at least two parameters. "
					+ "The first is the key and the following ones are the values to be associated to the key");
		}
		for ( Object param : paramList ) {
			if ( !(param instanceof String) ) {
				this.addError("All parameters to this metaobject annotation should be strings, which include identifiers");
				return ;
			}
		}

		final String varName = (String ) paramList.get(0);
		final int size = varName.length();
		for (int i = 0; i < size; ++i) {
			final char ch = varName.charAt(i);
			if ( !Character.isAlphabetic(ch) && !Character.isDigit(ch) && ch != '_' ) {
				this.addError("Character '" + ch + "' is not allowed in the key (first parameter). "
						+ "An identifier was expected as the first parameter to this metaobject annotation");
				return ;
			}
		}
		}

	@Override
	public void dpp_action(ICompiler_dpp compiler_dpp) {
		final WrAnnotationAt annotation = this.getAnnotation();
		final List<Object> paramList = annotation.getJavaParameterList();
		final String key = CyanMetaobject.removeQuotes( (String ) paramList.get(0) );
		final String value = CyanMetaobject.removeQuotes( (String ) paramList.get(1) );
		// compiler_dpp.addToProgramSet(varName, value);


		if ( this.getAttachedDeclaration() instanceof WrProgram_dpp ) {
			final WrProgram_dpp program = (WrProgram_dpp ) this.getAttachedDeclaration();
			program.addProgramKeyValueSet(key, value);
		}
		else if ( this.getAttachedDeclaration() instanceof WrCyanPackage_dpp ){
			// a package
			final WrCyanPackage_dpp cp = (WrCyanPackage_dpp ) this.getAttachedDeclaration();
			cp.addToPackageKeyValueSet(key, value);
		}
		else {
			this.addError("Error in metaobject class '" + this.getClass().getName() + "'");
		}

	}

}
