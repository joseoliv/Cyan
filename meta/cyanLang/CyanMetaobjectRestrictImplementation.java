package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckSubprototype_afsa;
import meta.ICompiler_dsa;
import meta.IDeclaration;
import meta.Token;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrProgramUnit;
import meta.WrType;

public class CyanMetaobjectRestrictImplementation extends CyanMetaobjectAtAnnot
		implements  ICheckSubprototype_afsa   {

	public CyanMetaobjectRestrictImplementation() {
		super("restrictImplementation", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.PROTOTYPE_DEC,
		}, Token.PUBLIC );
	}

	/**
	 * subprototypes of an immutable prototype should be immutable too
	 */

	@Override
	public void afsa_checkSubprototype(ICompiler_dsa compiler_dsa, WrProgramUnit subPrototype) {

		final IDeclaration dec = this.getAttachedDeclaration();
		WrProgramUnit puAnnot = (WrProgramUnit ) dec;

		WrEnv env = compiler_dsa.getEnv();
		if ( ! setAllowedList(env) ) { return ; }
		for ( WrProgramUnit pu : allowedList ) {
			if ( pu.isSupertypeOf(subPrototype, env) ) {
				return ;
			}
		}
		int size = allowedList.size();
		String s = "";
		for ( WrProgramUnit pu : allowedList ) {
			s += pu.getFullName();
			if ( --size > 0 ) {
				s += ", ";
			}
		}
		this.addError("Annotation '" + this.getName() + "' attached to interface '" + puAnnot.getFullName() +
				"' can only be implemented if the prototype also inherits from one of the following prototypes: " + s);

	}

	public boolean setAllowedList(WrEnv env) {

		final IDeclaration dec = this.getAttachedDeclaration();
		WrProgramUnit pu = (WrProgramUnit ) dec;
		if ( !pu.isInterface() ) {
			this.addError("Annotations '" + this.getName() + "' can only be attached to interfaces");
		}
		WrAnnotationAt mo = this.getMetaobjectAnnotation();
		List<Object> jpList = mo.getJavaParameterList();
		allowedList = new ArrayList<>();
		for ( Object po : jpList ) {
			if ( !(po instanceof String) ) {
				this.addError("Arguments to annotation '" + this.getName() + "' should be strings");
				return false;
			}
			String s = CyanMetaobject.removeQuotes( (String ) po);
			WrType t = env.searchPackagePrototype(s, mo.getFirstSymbol());
			if ( t == null ) {
				this.addError("Arguments '" + s + "' to annotation '" + this.getName() +
						"' should be a prototype. But it was not found");
				return false;
			}
			if ( !(t instanceof WrProgramUnit ) || ((WrProgramUnit ) t).isInterface()) {
				this.addError("Arguments '" + s + "' to annotation '" + this.getName() +
						"' should be a prototype. But it is not");
				return false;
			}
			allowedList.add( (WrProgramUnit ) t );
		}
		return true;
	}
	private List<WrProgramUnit> allowedList = null;

}

