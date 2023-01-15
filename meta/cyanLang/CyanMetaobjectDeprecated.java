package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.ICheckMessageSend_afterSemAn;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrMessageWithKeywords;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrPrototype;
import meta.WrSymbol;

/**
 * An annotation of this metaobject should be attached to method.
 * If there is a message send that can call the attached method, the metaobject issues
 * an error message saying that the method is deprecated.
 * <br>
 * Future versions of this metaobject will allow the annotations be attached to prototypes
 * and packages.
   @author jose
 */
public class CyanMetaobjectDeprecated extends CyanMetaobjectAtAnnot
    implements ICheckMessageSend_afterSemAn {

	public CyanMetaobjectDeprecated() {
		super("deprecated", AnnotationArgumentsKind.ZeroOrMoreParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.METHOD_DEC } );
	}


	@Override
	public void check() {
		final WrAnnotationAt withAt = (WrAnnotationAt ) this.annotation;

		final List<Object> javaParamList = withAt.getJavaParameterList();
		if ( javaParamList.size() == 0 ) {
			// // withAt.setInfo_parsing("This method is deprecated, you should not use it");

			this.errorMessage = null;
		}
		else if ( javaParamList.size() == 1 ) {
			if ( !(javaParamList.get(0) instanceof String) ) {
				addError("This metaobject annotation should take one string parameter");
			}
			else {
				// // withAt.setInfo_parsing( javaParamList.get(0) );
				errorMessage = (String ) javaParamList.get(0);
			}
			return ;
		}
		else {
			this.addError("This metaobject annotation should take just one string parameter");
		}
	}


	@Override
	public void afterSemAn_checkKeywordMessageSend(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message, WrMethodSignature ms, WrEnv env
			) {
		if ( errorMessage == null ) {
			this.errorMessage = "Method '" + ((WrMethodDec ) this.getAnnotation().getDeclaration()).getMethodSignature().getName() +
			"' is deprecated, you should not use it";

		}
		addError( receiverExpr.getFirstSymbol(),
				this.errorMessage
				);


//		WrType wt = receiverExpr.getType();
//		if ( wt instanceof WrPrototype ) {
//			((WrPrototype ) wt).accept(null);
//		}
//		receiverExpr.accept( new WrASTVisitor() {
//			@Override
//			public void visit(WrExprLiteralChar node) {
//
//			}
//			/*
//			 * node.getMethodSignatureList.get(0).getParameterList().get(0).getType().searchField(env)
//			 */
//		});
	}

	@Override
	public void afterSemAn_checkUnaryMessageSend(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrSymbol unarySymbol, WrEnv env)  {
		if ( errorMessage == null ) {
			this.errorMessage = "Method " + ((WrMethodDec ) this.getAnnotation().getDeclaration()).getMethodSignature().getName() +
			" is deprecated, you should not use it";

		}
		addError( receiverExpr.getFirstSymbol(),
				this.errorMessage
				);
	}


	private String errorMessage;
}
