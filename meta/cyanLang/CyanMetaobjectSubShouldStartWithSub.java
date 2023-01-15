package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckSubprototype_afterSemAn;
import meta.ICompiler_semAn;
import meta.Token;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrPrototype;

public class CyanMetaobjectSubShouldStartWithSub extends CyanMetaobjectAtAnnot
	implements ICheckSubprototype_afterSemAn {

    public CyanMetaobjectSubShouldStartWithSub() {
		super("demandsSub",AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.PROTOTYPE_DEC },
				Token.PUBLIC);
	}

	@Override
	public void afterSemAn_checkSubprototype(ICompiler_semAn compiler_semAn,
			WrPrototype subPrototype) {

		if ( ! subPrototype.getName().startsWith("Sub") ) {
			this.addError("Error at prototype '" + subPrototype.getFullName() + "'." +
					"This prototype name should start with 'Sub' because it"
					+ " inherits from a prototype annotated with 'demandsSub'");
		}
		WrEnv env = compiler_semAn.getEnv();

		List<WrAnnotationAt> annotList =
				subPrototype.getAttachedAnnotationList(env);
		if ( annotList.size() != 0 ) {
			this.addError("Error at prototype '" + subPrototype.getFullName() + "'." +
					"This prototype should not have any attached annotation");
		}
		if ( ! subPrototype.getIsFinal(env) ) {
			this.addError("Error at prototype '" + subPrototype.getFullName() + "'." +
					"This prototype should be final");
		}
//		List<WrMethodSignature> m = subPrototype.searchMethodPrivateProtectedPublic("run", env);
//		if ( m == null || m.size() == 0 ) {
//			this.addError("Error at prototype '" + subPrototype.getFullName() + "'." +
//					"This prototype should declare a 'run' method");
//		}
//		else {
//			final Ref<Boolean> r = new Ref<Boolean>();
//			r.elem = false;
//			WrMethodSignature met = m.get(0);
//			met.getMethod().accept( new WrASTVisitor() {;
//				@Override
//				public void visit(WrExprMessageSendUnaryChainToSuper node,
//						WrEnv env) {
//					if ( node.getMessageName().equals("run") ) {
//						r.elem = true;
//					}
//				}
//				}, compiler_semAn.getEnv());
//			if ( !r.elem ) {
//				this.addError(met.getMethod().getFirstSymbol(env), "Error at prototype '" + subPrototype.getFullName() + "'. " +
//						"Method 'run' of this prototype should call method 'run' of the superprototype");
//			}
//		}

	}



}
