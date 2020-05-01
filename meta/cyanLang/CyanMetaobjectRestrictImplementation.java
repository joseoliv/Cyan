package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckSubprototype_afterSemAn;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.Token;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrPrototype;
import meta.WrType;

public class CyanMetaobjectRestrictImplementation extends CyanMetaobjectAtAnnot
		implements  ICheckSubprototype_afterSemAn   {

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
	public void afterSemAn_checkSubprototype(ICompiler_semAn compiler_semAn, WrPrototype subPrototype) {

		final IDeclaration dec = this.getAttachedDeclaration();
		WrPrototype puAnnot = (WrPrototype ) dec;

		WrEnv env = compiler_semAn.getEnv();
		if ( ! setAllowedList(env) ) { return ; }
		for ( WrPrototype pu : allowedList ) {
			if ( pu.isSupertypeOf(subPrototype, env) ) {
				return ;
			}
		}
		int size = allowedList.size();
		String s = "";
		for ( WrPrototype pu : allowedList ) {
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
		WrPrototype pu = (WrPrototype ) dec;
		if ( !pu.isInterface() ) {
			this.addError("Annotations '" + this.getName() + "' can only be attached to interfaces");
		}
		WrAnnotationAt mo = this.getAnnotation();
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
			if ( !(t instanceof WrPrototype ) || ((WrPrototype ) t).isInterface()) {
				this.addError("Arguments '" + s + "' to annotation '" + this.getName() +
						"' should be a prototype. But it is not");
				return false;
			}
			allowedList.add( (WrPrototype ) t );
		}
		return true;
	}
	private List<WrPrototype> allowedList = null;

}

