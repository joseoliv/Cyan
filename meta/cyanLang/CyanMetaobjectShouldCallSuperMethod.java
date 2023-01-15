package meta.cyanLang;

import java.util.List;
import cyanruntime.Ref;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAbstractCyanCompiler;
import meta.IActionFunction;
import meta.IDeclaration;
import meta.Tuple6;
import meta.WrEnv;
import meta.WrExprMessageSendUnaryChainToSuper;
import meta.WrExprMessageSendWithKeywordsToSuper;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrStatement;
import meta.WrSymbol;

public class CyanMetaobjectShouldCallSuperMethod extends CyanMetaobject implements IActionFunction {

	@SuppressWarnings("unchecked")
	@Override
	public Object eval(Object input) {
		if ( input instanceof Tuple6<?,?,?,?,?,?>) {

			/*
			 * Tuple3<IAbstractCyanCompiler, CyanMetaobjectAtAnnot, List<String>>(
				compiler, cyanMetaobject, paramList )
			 */
			Tuple6<?,?,?,?,?,?> t = (Tuple6<?,?,?,?,?,?> ) input;
			if ( (t.f1 != null && !(t.f1 instanceof IAbstractCyanCompiler)) ||
					(t.f2 != null && !(t.f2 instanceof CyanMetaobjectAtAnnot)) ||
					(t.f3 != null && !(t.f3 instanceof List<?>)) ||
					(t.f4 != null && !(t.f4 instanceof WrSymbol)) ||
					(t.f5 != null && !(t.f5 instanceof WrMethodDec)) ||
					(t.f6 != null && !(t.f6 instanceof WrEnv))
					) {
				return null;
			}

			CyanMetaobjectAtAnnot mo = (CyanMetaobjectAtAnnot ) t.f2;
			List<Object> paramNameList = (List<Object> ) t.f3;
			WrSymbol firstSymbol = (WrSymbol ) t.f4;
			if ( paramNameList != null && paramNameList.size() > 0 ) {
				mo.addError(firstSymbol, "This call does not take parameters");
			}
			WrMethodDec currentMethod = (WrMethodDec ) t.f5;
			WrEnv wrEnv = (WrEnv ) t.f6;
			Ref<Boolean> ok = new Ref<>();
			ok.elem = false;
			String superMethodName;
			IDeclaration idec = mo.getAttachedDeclaration();
			if ( idec instanceof WrMethodDec ) {
				superMethodName = ((WrMethodDec ) idec).getName();
			}
			else if ( idec instanceof WrMethodSignature ) {
				superMethodName = ((WrMethodSignature ) idec).getName();
			}
			else {
				mo.addError(firstSymbol, "The superprototype of '" +
				          wrEnv.getCurrentPrototype().getFullName() +
				          "' uses an annotation '" + mo.getName() +
				          "' that should be attached to a method or method interface. It is attached to something else");
				return null;
			}
			List<WrStatement> wrStatList = currentMethod.getStatementList(wrEnv).getStatementList();
			if ( wrStatList != null && wrStatList.size() > 0 ) {
				WrStatement node = wrStatList.get(0);
				if ( superMethodName.indexOf(':') < 0 ) {
					// unary method
					if ( node instanceof WrExprMessageSendUnaryChainToSuper ) {
						WrMethodSignature wrms = ((WrExprMessageSendUnaryChainToSuper) node).getMethodSignatureForMessageSend();
						if ( wrms != null && wrms.getMethod() != null ) {
							ok.elem = wrms.getMethod().getName().equals(superMethodName);
						}
					}
				}
				else {
					// keyword method
					if ( node instanceof WrExprMessageSendWithKeywordsToSuper ) {
						WrMethodSignature wrms = ((WrExprMessageSendWithKeywordsToSuper) node).getMethodSignatureForMessage();
						if ( wrms != null && wrms.getMethod() != null ) {
							ok.elem = wrms.getMethod().getName().equals(superMethodName);
						}
					}
				}
			}
//			currentMethod.accept(new WrASTVisitor() {
//				@Override
//				public void visit(WrExprMessageSendUnaryChainToSuper node, WrEnv env) {
//					WrMethodSignature wrms = node.getMethodSignatureForMessage();
//					if ( wrms != null && wrms.getMethod() != null ) {
//						ok.elem = wrms.getMethod().getName().equals(superMethodName);
//					}
//				}
//				@Override
//				public void visit(WrExprMessageSendWithKeywordsToSuper node, WrEnv env) {
//					WrMethodSignature wrms = node.getMethodSignatureForMessage();
//					if ( wrms != null && wrms.getMethod() != null ) {
//						ok.elem = wrms.getMethod().getName().equals(superMethodName);
//					}
//				}
//
//			    }, wrEnv);
			if ( !ok.elem ) {
				mo.addError(currentMethod.getFirstSymbol(wrEnv), "Method '" + currentMethod.getName() + "' of prototype '" +
			          wrEnv.getCurrentPrototype().getFullName() +
			          "' should extend the superprototype method. That is, its first statement should be a call "
			          + "to the method '" + currentMethod.getName() + "' of the superprototype");
			}

		}


		return null;
	}

	@Override
	public String getName() {
		return "shouldCallSuperMethod";
	}

	@Override
	public String getPackageOfType() {
		return null;
	}

	@Override
	public String getPrototypeOfType() {
		return null;
	}

}
