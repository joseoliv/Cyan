package meta.cyanLang;

import java.util.List;
import java.util.Locale;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpp;
import meta.ICompiler_dpp;
import meta.IDeclaration;
import meta.MetaHelper;
import meta.Token;
import meta.WrAnnotationAt;
import meta.WrCyanPackage_dpp;
import meta.WrProgram_dpp;

public class CyanMetaobjectOptions extends CyanMetaobjectAtAnnot
implements IAction_dpp {

	public CyanMetaobjectOptions() {
		super("options", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.PACKAGE_DEC,
						AttachedDeclarationKind.PROGRAM_DEC },
				Token.PUBLIC);
	}

	@Override
	public void check() {

		WrAnnotationAt annot = getAnnotation();
		int i = 0;
		List<Object> paramList = annot.getJavaParameterList();
		for ( Object param : paramList) {
			if ( !(param instanceof String) ) {
				addError("This metaobject annotation should have the first parameter of type 'String'");
				return ;
			}
			String op = (String ) param;
			switch ( op.toLowerCase(Locale.US) ) {
			case MetaHelper.maxnumroundsfixmetaStr:
				if ( i + 1 >= paramList.size() || !(paramList.get(i+1) instanceof Integer) ) {
					addError("After option '" + op + "', we expect an Int number");
					return ;
				}
				this.maxNumRoundsFixMeta = (Integer ) paramList.get(i+1);
				break;
			default:
				addError("Unknown option: '" + op + "'");
			}
			++i;
		}

	}


	@Override
	public void dpp_action(ICompiler_dpp compiler) {
		IDeclaration idec = this.getAttachedDeclaration();
		if (idec instanceof WrProgram_dpp ) {
			final WrProgram_dpp program = (WrProgram_dpp ) this.getAttachedDeclaration();
			program.setProgramKeyValue(MetaHelper.maxnumroundsfixmetaStr, this.maxNumRoundsFixMeta);
		}
		else if ( idec instanceof WrCyanPackage_dpp ){
			// a package
			final WrCyanPackage_dpp cp = (WrCyanPackage_dpp ) this.getAttachedDeclaration();
			cp.setPackageKeyValue(MetaHelper.maxnumroundsfixmetaStr, this.maxNumRoundsFixMeta);
		}
		else {
			this.addError("In metaobject 'options', there is probably an internal error");
		}

	}

	int maxNumRoundsFixMeta = 5;
}
