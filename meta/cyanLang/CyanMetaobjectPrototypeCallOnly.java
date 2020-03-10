/**

 */
package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.ICheckDeclaration_afsa;
import meta.ICheckMessageSend_afsa;
import meta.ICompiler_dsa;
import meta.Token;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrFieldDec;
import meta.WrMessageWithKeywords;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrProgramUnit;
import meta.WrSymbol;

/** metaobject prototypeCallOnly should be attached to a method. The method
 * should only be called if the receiver of the message is a prototype in which the method is.
 * Therefore the method should be declared as final.
 *
   @author José
 */

public class CyanMetaobjectPrototypeCallOnly extends CyanMetaobjectAtAnnot
             implements ICheckMessageSend_afsa, ICheckDeclaration_afsa {

	public CyanMetaobjectPrototypeCallOnly() {
		super("prototypeCallOnly", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC },
				Token.PUBLIC );
	}


	@Override
	public void afsa_checkKeywordMessageSendMostSpecific(WrExpr receiverExpr, WrProgramUnit receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message, WrMethodSignature ms,
			WrProgramUnit mostSpecificReceiver, WrEnv env) {
		if ( receiverKind != ExprReceiverKind.PROTOTYPE_R ) {
			addError(receiverExpr.getFirstSymbol(), "The receiver of the message send '" + receiverExpr.asString() + " " +
		           message.asString()  +
					"' " +
					" should be a prototype");
			return ;
		}
		else {
			final WrMethodDec md = (WrMethodDec ) this.getAttachedDeclaration();
			if ( mostSpecificReceiver != receiverType ) {
				addError(receiverExpr.getFirstSymbol(), "It was expected that the receiver was '" + md.getDeclaringObject().getFullName() + "'");
				return ;
			}
		}
	}

	@Override
	public void afsa_checkUnaryMessageSendMostSpecific(WrExpr receiverExpr, WrProgramUnit receiverType,
			ExprReceiverKind receiverKind, WrSymbol unarySymbol, WrProgramUnit  mostSpecificReceiver,
			WrEnv env) {

		final WrMethodDec method = (WrMethodDec ) this.getAttachedDeclaration();
		/*
	    ObjectDec proto = method.getDeclaringObject();
		List<AnnotationAt> annotList = proto.getAttachedMetaobjectAnnotationList();
		for (AnnotationAt annot : annotList ) {
			// scans all prototype annotations
			if ( annot.getCyanMetaobject().getName().equals("curupira") ) {
				this.addError("Sorry, this is Saci, Curupiras are not allowed here");
			}
		}
		annotList = method.getAttachedMetaobjectAnnotationList();
		for (AnnotationAt annot : annotList ) {
			// scans all method annotations
			if ( annot.getCyanMetaobject().getName().equals("Sacuras") ) {
				this.addError("Sorry, only  'saciPerere' annotations are allowed here");
			}
		}

		 */

		final String methodName = method.getName();
		if ( receiverKind != ExprReceiverKind.PROTOTYPE_R ) {
			addError(unarySymbol, "The receiver of the message send, '" +
					(receiverExpr == null ? "self" : receiverExpr.asString()) + " " + methodName  +
					"' " +
					" should be a prototype");
			return ;
		}
		else {
			if ( mostSpecificReceiver != receiverType ) {
				addError(unarySymbol, "It was expected that the receiver was '" +
			        method.getDeclaringObject().getFullName() + "'");
				return ;
			}
		}

	}


//
//	@Override
//	public void ati3_checkDeclaration(ICompiler_dsa compiler_dsa) {
//
//		final WrMethodDec md = (WrMethodDec ) this.getAttachedDeclaration();
//		md.setAllowAccessToFields(false, compiler_dsa.getEnv());
//	}


	@Override
	public void afsa_checkDeclaration(ICompiler_dsa compiler) {
		if ( compiler.getEnv().getCurrentProgramUnit().isInterface() )
			compiler.error(this.metaobjectAnnotation.getFirstSymbol(),
					"This metaobject cannot be attached to a method of an interface");
		final WrMethodDec method = (WrMethodDec ) this.getAttachedDeclaration();
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
				this.addError("Annotation '" + this.getName() + "' is attached to this method. "
						+ "Therefore it should not access any non-shared field. It accesses the following fields: " + all);
			}
		}
		final WrProgramUnit proto = method.getDeclaringObject();
		if ( ! proto.getIsFinal(compiler.getEnv()) ) {
			// open prototype, may have non-final methods
			if ( ! method.getIsFinal() ) {
				this.addError("Annotation '" + this.getName() + "' is attached to this method. Therefore it should be declared with keyword 'final'");
			}
		}
		if ( method.getSelfLeak(compiler.getEnv()) ) {
			this.addError(method.getFirstSymbol(compiler.getEnv()), "Annotation '" + this.getName() +
					"' is attached to this method. Therefore it cannot use "
					+ "'self' fields and methods or pass 'self' as parameter");
		}
	}



}
