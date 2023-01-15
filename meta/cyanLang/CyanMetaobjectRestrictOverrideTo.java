package meta.cyanLang;


import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckOverride_afterSemAn;
import meta.ICompiler_semAn;
import meta.MetaHelper;
import meta.WrMethodDec;

/**
 * The metaobject associated to this class checks if a method is overridden only in certain subprototypes. Its
 * usage is <br>
 * <code>
 * {@literal @}restrictOverrideTo(company.Manager, company.Employee)<br>
 * func getSalary -> Double { .. }<br>
 * </code><br>
 * Only the prototypes that are parameters to the annotation will be able to override
 * method getSalary
   @author jose
 */
public class CyanMetaobjectRestrictOverrideTo extends CyanMetaobjectAtAnnot
		implements ICheckOverride_afterSemAn {

	public CyanMetaobjectRestrictOverrideTo() {
		super("restrictOverrideTo", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC });
	}



	@Override
	public void check() {
		fullSubPrototypeNameList = new ArrayList<>();
		int n = 1;
		for (Object obj : this.getAnnotation().getJavaParameterList() ) {
			if ( !(obj instanceof String) ) {
				this.addError("Parameter number '" + n + "' is not a String. It should be");
				return ;
			}
			String str = MetaHelper.removeQuotes( (String ) obj );
			fullSubPrototypeNameList.add(str);
			++n;
		}
	}


	@Override
	public void afterSemAn_checkOverride(ICompiler_semAn compiler_semAn,
			WrMethodDec method) {
		String protoName = method.getDeclaringObject().getFullName();
		if ( protoName.startsWith(MetaHelper.cyanLanguagePackageNameDot) ) {
			protoName = protoName.substring(MetaHelper.cyanLanguagePackageNameDot.length());
		}
		boolean found = false;
		for ( String p : this.fullSubPrototypeNameList ) {
			String other = p;
			if ( p.equals(protoName) ) {
				if ( p.startsWith(MetaHelper.cyanLanguagePackageNameDot) ) {
					other = p.substring(MetaHelper.cyanLanguagePackageNameDot.length());
				}
			}
			if ( other.equals(protoName) ) {
				found = true;
				break;
			}

		}
		if ( !found ) {
			WrMethodDec attachedMethod = (WrMethodDec ) this.getAttachedDeclaration();
			this.addError(method.getFirstSymbol(compiler_semAn.getEnv()), "This method cannot override the "
					+ "superprototype method. See annotation " +
					this.getName() + " attached to method " +
					attachedMethod.getName() + " of prototype '" + attachedMethod.getDeclaringObject().getFullName() + "'");
		}
	}


	List<String> fullSubPrototypeNameList;
}
