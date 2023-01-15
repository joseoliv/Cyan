package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICheckOverride_afterSemAn;
import meta.ICompiler_semAn;
import meta.Token;
import meta.WrASTVisitor;
import meta.WrEnv;
import meta.WrExprMessageSendUnaryChainToExpr;
import meta.WrExprMessageSendWithKeywordsToExpr;
import meta.WrExprSelf;
import meta.WrFieldDec;
import meta.WrMessageKeywordWithRealParameters;
import meta.WrMethodDec;
import meta.WrPrototype;
import meta.WrSymbolIdent;

public class CyanMetaobjectAccessOnlySharedFields extends CyanMetaobjectAtAnnot
			implements ICheckDeclaration_afterSemAn, ICheckOverride_afterSemAn {

	public CyanMetaobjectAccessOnlySharedFields() {
		super("accessOnlySharedFields", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC },
				Token.PUBLIC );
	}


	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {
		if ( compiler.getEnv().getCurrentPrototype().isInterface() ) {
			compiler.error(this.annotation.getFirstSymbol(),
					"This metaobject cannot be attached to a method of an interface");
		}
		final WrMethodDec method = (WrMethodDec ) this.getAttachedDeclaration();
		checkAccessFieldsSelfLeak(compiler, method);
	}


	/**
	   @param compiler
	   @param method
	 */
	private void checkAccessFieldsSelfLeak(ICompiler_semAn compiler,
			final WrMethodDec method) {
		if ( method.getAccessedFieldSet() != null ) {
			String all = "";
			boolean first = true;
			for ( WrFieldDec field : method.getAccessedFieldSet() ) {
				if ( ! field.isShared() ) {
					if ( !first ) { all += ", "; }
					all += field.getName();
					first = false;
				}
			}
			if ( all.length() != 0 ) {
				WrMethodDec superMethod = (WrMethodDec ) this.getAttachedDeclaration();
				WrPrototype superProto = superMethod.getDeclaringObject();
				String protoName = superProto.getFullName();
				WrPrototype subProto = method.getDeclaringObject();
				if ( superProto != subProto ) {
					this.addError(method.getFirstSymbol(compiler.getEnv()),
							"Annotation '" + this.getName() + "' is attached to method with the same name in the superprototype '" +
							superProto.getFullName() + "'. "
									+ "Therefore this method should not access any non-shared field. It accesses the following fields: " + all);
				}
				else {
					this.addError("Annotation '" + this.getName() + "' is attached to this method. "
									+ "Therefore, it should not access any non-shared field. It accesses the following fields: " + all);
				}
			}
		}
		method.accept(new WrASTVisitor() {

			@Override
			public void visit(WrExprMessageSendUnaryChainToExpr node, WrEnv env) {
				WrSymbolIdent si = node.getUnarySymbol();
				if ( (si.token == Token.INTER_ID || si.token == Token.INTER_DOT_ID ) &&
						node.getReceiver() instanceof WrExprSelf ) {
					env.error(node.getFirstSymbol(),
							"Annotation 'accessOnlySharedFields' is attached to this method. "
									+ "Therefore, it should not access any non-shared field. " +
									"By using '?self " + node.getMessageName() +
									"' the method called may access a non-shared field");
				}
			}
			@Override
			public void visit(WrExprMessageSendWithKeywordsToExpr node, WrEnv env) {
				WrMessageKeywordWithRealParameters msg = node.getMessage().getkeywordParameterList().get(0);
				Token t = msg.getkeyword().token;
				if ( (t == Token.INTER_ID_COLON || t == Token.INTER_DOT_ID_COLON ) &&
						node.getReceiverExpr() instanceof WrExprSelf ) {
					env.error(node.getFirstSymbol(),
							"Annotation 'accessOnlySharedFields' is attached to this method. "
									+ "Therefore, it should not access any non-shared field. " +
									"By using '" + node.asString() +
									"' the method called may access a non-shared field");
				}
			}

		}, compiler.getEnv());
		if ( method.getSelfLeak(compiler.getEnv()) ) {
			this.addError(method.getFirstSymbol(compiler.getEnv()), "Annotation '" + this.getName() +
					"' is attached to this method. Therefore it cannot use "
					+ "'self' fields and methods or pass 'self' as parameter");
		}
	}


	@Override
	public void afterSemAn_checkOverride(ICompiler_semAn compiler,
			WrMethodDec method) {
		checkAccessFieldsSelfLeak(compiler, method);
	}


}

