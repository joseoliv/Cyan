package meta.cyanLang;

import java.util.List;
import ast.GenericParameter.GenericParameterKind;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ExprReceiverKind;
import meta.ICheckMessageSend_afterSemAn;
import meta.MetaHelper;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrGenericParameter;
import meta.WrMessageKeywordWithRealParameters;
import meta.WrMessageWithKeywords;
import meta.WrMethodSignature;
import meta.WrParameterDec;
import meta.WrPrototype;
import meta.WrSymbol;
import meta.WrType;

/**
 * This metaobject checks whether each parameter to a catch: keyword has at least one 'eval:'
 * method,  each of them accepting one parameter whose type is sub-prototype of {\tt CyException}.
   @author José
 */
public class CyanMetaobjectCheckCatchParameter extends CyanMetaobjectAtAnnot
    implements ICheckMessageSend_afterSemAn {

	public CyanMetaobjectCheckCatchParameter() {
		super("checkCatchParameter", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC });
	}

	@Override
	public void afterSemAn_checkKeywordMessageSend(WrExpr receiverExpr, WrPrototype receiverType,
			ExprReceiverKind receiverKind, WrMessageWithKeywords message, WrMethodSignature ms, WrEnv env) {

		int i = 1;
		for ( final WrMessageKeywordWithRealParameters sel : message.getkeywordParameterList() ) {
			if ( sel.getkeywordNameWithoutSpecialChars().equals("catch") ) {
				final WrExpr expr = sel.getExprList().get(0);
				final WrType t = expr.getType();
				List<WrMethodSignature> emsList = null;

				emsList = t.searchMethodPublicPackageSuperPublicPackage("eval:1", env);
				checkCatchParameter(env, i, emsList, sel.getkeyword());
			}
			++i;
		}

	}

	/**
	   @param env
	   @param i
	   @param emsList
	 */
	private void checkCatchParameter(WrEnv env, int i, List<WrMethodSignature> emsList,
			WrSymbol errSymbol) {
		if ( emsList == null ) {
			/*
			 * each parameter to a catch: keyword should have at least one 'eval:' method
			 */
			addError(errSymbol, "Parameter to the 'catch:' keyword number " + i +
					" has a type that does not define an 'eval:' method");
			return ;
		}
		else {
			for ( final WrMethodSignature ems : emsList ) {
				final WrParameterDec param = ems.getParameterList().get(0);
				/*
				 * each 'eval:' method should accept one parameter whose type is sub-prototype of CyException
				 */
				final WrType paramType = param.getType();

				if ( paramType == null ) {
					param.getType();
				}

				final WrType cyException = env.getCyException();
				if ( !cyException.isSupertypeOf(paramType, env) ) {

					boolean signalError = true;
					if ( paramType instanceof WrPrototype ) {
						final WrPrototype proto = (WrPrototype ) paramType;
						if ( proto.getGenericParameterListList(env) != null && proto.getGenericParameterListList(env).size() == 1 ) {
							/*
							 * it may be an Union
							 */
							final String cyanName = proto.getSimpleName();
							if ( cyanName.equals("Union") ) {

								/*
								 * the catch parameter may be an union as in<br>
								 * {@code
								 * { <br>
								 *     ... <br>
								 * } <br>
        					 	 *	catch: { (: ExceptionTest1Int | ExceptionTest2Int e :) control = "GT0EQ0" }; <br>
								 */
								final List<WrGenericParameter> gpList = proto.getGenericParameterListList(env).get(0);
								for ( final WrGenericParameter gp : gpList ) {
									if ( gp.getKind() != GenericParameterKind.PrototypeCyanLang &&
										 gp.getKind() != GenericParameterKind.PrototypeWithPackage ) {
										addError(errSymbol, "The type of the parameter to the 'catch:' keyword number " + i +
												" defines an 'eval:' method that accepts a parameter that is not subtype of '" +
												MetaHelper.cyanLanguagePackageName + "." + MetaHelper.cyExceptionPrototype + "'");
										return ;
									}
									final WrType unionElemType = gp.getType();
									if ( !(unionElemType instanceof WrPrototype)  ) {
										addError(errSymbol, "The type of the parameter to the 'catch:' keyword number " + i +
												" defines an 'eval:' method that accepts a parameter that is not subtype of '" +
												MetaHelper.cyanLanguagePackageName + "." + MetaHelper.cyExceptionPrototype + "'");
										return ;
									}
									if ( !cyException.isSupertypeOf(unionElemType, env) ) {
										addError(errSymbol, "The type of the parameter to the 'catch:' keyword number " + i +
												" defines an 'eval:' method that accepts a parameter that is not subtype of '" +
												MetaHelper.cyanLanguagePackageName + "." + MetaHelper.cyExceptionPrototype + "'");
										return ;
									}
								}
								signalError = false;
							}
						}
					}


					if ( signalError ) {
						addError(errSymbol, "The type of the parameter to the 'catch:' keyword number " + i +
								" defines an 'eval:' method that accepts a parameter that is not subtype of '" +
								MetaHelper.cyanLanguagePackageName + "." + MetaHelper.cyExceptionPrototype + "'");
					}


				}
			}
		}
	}

}
