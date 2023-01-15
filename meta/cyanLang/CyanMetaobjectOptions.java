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
		int size = paramList.size();
		while ( i < size ) {
			Object param = paramList.get(i);
			if ( !(param instanceof String) ) {
				addError("The " + i + "th parameter of this annotation should have type 'String'");
				return ;
			}
			String op = (String ) param;
			switch ( op.toLowerCase(Locale.US) ) {
			case MetaHelper.maxnumroundsfixmetastr:
				++i;
				if ( i >= paramList.size() || !(paramList.get(i) instanceof Integer) ) {
					addError("After option '" + op + "', we expect an Int number");
					return ;
				}
				this.maxNumRoundsFixMeta = (Integer ) paramList.get(i);
				if ( this.maxNumRoundsFixMeta > maxNumRoundsFixMetaMax  ) {
					addError("Value of '" + MetaHelper.maxnumroundsfixmetastr + "' is too big."
							+ " Use a value less than " + maxNumRoundsFixMetaMax);
					return ;
				}
				break;
			case MetaHelper.timeoutMillisecondsMetaobjectsStr:
				if ( i >= paramList.size() || !(paramList.get(i) instanceof Integer) ) {
					addError("After option '" + op + "', we expect an Int number");
					return ;
				}
				this.timeoutMillisecondsMetaobjects = (Integer ) paramList.get(i);
				if ( this.timeoutMillisecondsMetaobjects > timeoutMillisecondsMetaobjectsMax ) {
					addError("Value of '" + MetaHelper.timeoutMillisecondsMetaobjectsStr + "' is too big."
							+ " Use a value less than " + timeoutMillisecondsMetaobjectsMax);
					return ;
				}
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
			if ( maxNumRoundsFixMeta > 0 ) {
				program.setProgramKeyValue(MetaHelper.maxnumroundsfixmetastr, this.maxNumRoundsFixMeta);
			}
			if ( this.timeoutMillisecondsMetaobjects >= 0 ) {
				program.setProgramKeyValue(MetaHelper.timeoutMillisecondsMetaobjectsStr, this.timeoutMillisecondsMetaobjects);
			}
		}
		else if ( idec instanceof WrCyanPackage_dpp ){
			// a package
			final WrCyanPackage_dpp cp = (WrCyanPackage_dpp ) this.getAttachedDeclaration();
			if ( maxNumRoundsFixMeta > 0 ) {
				cp.setPackageKeyValue(MetaHelper.maxnumroundsfixmetastr, this.maxNumRoundsFixMeta);
			}
			if ( this.timeoutMillisecondsMetaobjects >= 0 ) {
				cp.setPackageKeyValue(MetaHelper.timeoutMillisecondsMetaobjectsStr, this.timeoutMillisecondsMetaobjects);
			}
		}
		else {
			this.addError("In metaobject 'options', there is probably an internal error");
		}

	}

	public Integer getTimeoutMillisecondsMetaobjects() {
		return timeoutMillisecondsMetaobjects;
	}

	private int maxNumRoundsFixMeta = -1;
	private int timeoutMillisecondsMetaobjects = -1;

	private static final int maxNumRoundsFixMetaMax = 10;
	private static final int timeoutMillisecondsMetaobjectsMax = 20;

}
