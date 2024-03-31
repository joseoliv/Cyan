
package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionMessageSend_semAn;
import meta.MetaHelper;
import meta.Tuple3;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprLiteralString;
import meta.WrExprMessageSendWithKeywordsToExpr;
import meta.WrMessageWithKeywords;
import meta.WrMethodDec;
import meta.WrMethodKeywordWithParameters;
import meta.WrMethodSignature;
import meta.WrMethodSignatureOperator;
import meta.WrMethodSignatureUnary;
import meta.WrMethodSignatureWithKeywords;
import meta.WrParameterDec;
import meta.WrPrototype;
import meta.WrType;

/**
 * This metaobject should only be attached to methods 'functionForMethod:1' and
 * 'functionForMethodWithSelf:1' of Any. It replaces method calls to these
 * methods by an anonymous function. If the parameter of 'functionForMethod:1'
 * is a literal string which is the name of method 'm:1', then the code that
 * replaces the method call is
 * <code>    { (: T elem :) receiver m: elem } </code> <br>
 * T is the type of the parameter of method 'm:1'.
 *
 * @author jose
 */
public class CyanMetaobjectChangeFunctionForMethod extends CyanMetaobjectAtAnnot
		implements IActionMessageSend_semAn {

	public CyanMetaobjectChangeFunctionForMethod() {
		super("changeFunctionForMethod", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.METHOD_DEC });
	}

	@Override
	public Tuple3<StringBuffer, String, String> semAn_analyzeReplaceKeywordMessage(
			WrExprMessageSendWithKeywordsToExpr messageSendExpr, WrEnv env) {

		final WrAnnotationAt annotation = this.getAnnotation();
		boolean ok = true;
		boolean withSelf = false;
		if ( !(annotation.getDeclaration() instanceof WrMethodDec) ) {
			ok = false;
		}
		else {
			final String attachedMethodName = ((WrMethodDec) annotation
					.getDeclaration()).getName();
			if ( !attachedMethodName.equals("functionForMethod:1")
					&& !(withSelf = attachedMethodName
							.equals("functionForMethodWithSelf:1")) ) {
				ok = false;
			}
		}

		if ( !ok ) {
			// String s = ((MethodDec) annotation.getDeclaration()).getName();
			this.addError(
					"This metaobject can only be attached to methods 'functionForMethod:' and 'functionForMethodWithSelf:' of prototype 'Any'");
			return null;
		}

		final WrMessageWithKeywords message = messageSendExpr.getMessage();
		final WrExpr paramExpr = message.getkeywordParameterList().get(0)
				.getExprList().get(0);
		String methodName;
		if ( paramExpr instanceof WrExprLiteralString ) {
			final WrExprLiteralString es = (WrExprLiteralString) paramExpr;
			methodName = es.getStringJavaValue().toString();

		}
		else {
			this.addError(messageSendExpr.getFirstSymbol(),
					"The parameter to the method 'functionForMethod:' or 'functionForMethodWithSelf:' "
							+ "should be a literal string");

			return null;
		}
		if ( env.getCurrentPrototype().isInterface() ) {
			return null;
		}

		methodName = MetaHelper.removeQuotes(methodName);

		String receiverAsString;

		final WrExpr receiverExpr = messageSendExpr.getReceiverExpr();
		WrPrototype receiverType = null;
		if ( receiverExpr == null ) {
			receiverType = env.getCurrentPrototype();
			receiverAsString = "self";
		}
		else {
			if ( !(receiverExpr.getType()
					.getInsideType() instanceof WrPrototype) ) {
				this.addError(receiverExpr.getFirstSymbol(),
						"Methods 'functionForMethod:' and 'functionForMethodWithSelf:' "
								+ "can only be applied to methods of Cyan prototypes. This is an internal error");
				return null;
			}
			receiverType = (WrPrototype) receiverExpr.getType().getInsideType();
			receiverAsString = receiverExpr.asString();
		}

		final List<WrMethodSignature> msList = receiverType
				.searchMethodPublicSuperPublicProto(methodName, env);
		if ( msList == null || msList.size() == 0 ) {
			this.addError(messageSendExpr.getFirstSymbol(), "Method '"
					+ methodName
					+ "' that is parameter to the message passing whose keyword is"
					+ " 'functionForMethod:' or 'functionForMethodWithSelf:' "
					+ "was not found. Make sure it is correctly spelled."
					+ " It should be something like 'with:2 param:1 do:1'. Note the spaces. The number is the number of parameters");
			return null;
		}
		final WrMethodSignature ms = msList.get(0);

		final StringBuffer code = new StringBuffer();
		code.append(" { ");
		if ( ms instanceof WrMethodSignatureUnary ) {
			/*
			 * x getMethod: "open" should be replaced by { ^x open }
			 */
			final WrMethodSignatureUnary msu = (WrMethodSignatureUnary) ms;
			if ( withSelf ) {
				final String myselfName = env.getNewUniqueVariableName();
				code.append(
						"(: " + receiverType.getFullName() + " " + myselfName
								+ " :) ^" + myselfName + " " + msu.getName());
			}
			else {
				code.append(" ^" + receiverAsString + " " + msu.getName());
			}
		}
		else if ( ms instanceof WrMethodSignatureOperator ) {
			final WrMethodSignatureOperator mso = (WrMethodSignatureOperator) ms;
			if ( mso.getOptionalParameter() == null ) {
				// unary method
				if ( withSelf ) {
					final String myselfName = env.getNewUniqueVariableName();
					code.append("(: " + receiverType.getFullName() + " "
							+ myselfName + " :) ^ "
							+ mso.getNameWithoutParamNumber() + " " + myselfName
							+ " ");
				}
				else {
					code.append(" ^ " + mso.getNameWithoutParamNumber() + " "
							+ receiverAsString);
				}
			}
			else {
				// with parameter
				final String paramName = env.getNewUniqueVariableName();
				if ( withSelf ) {
					final String myselfName = env.getNewUniqueVariableName();

					code.append(" (: eval: " + receiverType.getFullName() + " "
							+ myselfName + " eval: "
							+ mso.getOptionalParameter().getType().getFullName()
							+ " " + paramName + " " + ":) ^" + myselfName + " "
							+ mso.getNameWithoutParamNumber() + " "
							+ paramName);
				}
				else {
					code.append(" (: "
							+ mso.getOptionalParameter().getType().getFullName()
							+ " " + paramName + " :) ^" + receiverAsString
							+ "  " + mso.getNameWithoutParamNumber() + " "
							+ paramName);
				}
			}

		}
		else if ( ms instanceof WrMethodSignatureWithKeywords ) {
			final WrMethodSignatureWithKeywords mss = (WrMethodSignatureWithKeywords) ms;
			/*
			 * x getMethod: "at:1 put:2 with:1"
			 *
			 * { (: eval: Int p1 eval: Char p2, Float p3 eval: Int p4 :) ^x at:
			 * p1 put: p2, p3 with: p4 }
			 */
			code.append("(: ");
			int i = 0;
			int size2 = mss.getKeywordArray().size();
			final String myselfName = env.getNewUniqueVariableName();

			if ( withSelf ) {
				code.append("eval: " + receiverType.getFullName() + " "
						+ myselfName + " ");
			}
			final List<String> paramNameList = new ArrayList<>();
			for (final WrMethodKeywordWithParameters sel : mss
					.getKeywordArray()) {
				if ( mss.getKeywordArray().size() != 1 ) {
					code.append("eval: ");
				}
				int size = sel.getParameterList().size();
				for (final WrParameterDec param : sel.getParameterList()) {
					final String paramName = env.getNewUniqueVariableName();
					paramNameList.add(paramName);
					code.append(
							param.getType().getFullName() + " " + paramName);
					if ( --size > 0 ) code.append(", ");
					++i;
				}
				if ( --size2 > 0 ) {
					code.append(" ");
				}
			}
			code.append(" :) ^");
			if ( withSelf ) {
				code.append(myselfName + " ");
			}
			else {
				code.append(messageSendExpr.getReceiverExpr().asString() + " ");
			}
			i = 0;
			int sizeSA = mss.getKeywordArray().size();
			for (final WrMethodKeywordWithParameters sel : mss
					.getKeywordArray()) {
				code.append(sel.getName() + " ");
				int size = sel.getParameterList().size();

				for (@SuppressWarnings("unused")
				final WrParameterDec param : sel.getParameterList()) {
					code.append(" " + paramNameList.get(i));
					if ( --size > 0 ) code.append(", ");
					++i;
				}
				if ( --sizeSA > 0 ) {
					code.append(" ");
				}
			}

		}
		final String functionName = withSelf
				? ms.getFunctionNameWithSelf(receiverType.getFullName())
				: ms.getFunctionName();

		final WrType type = env.createNewGenericPrototype(
				annotation.getFirstSymbol(), env.getCurrentCompilationUnit(),
				env.getCurrentPrototype(),
				MetaHelper.cyanLanguagePackageName + "." + functionName,
				"Error caused by method semAn_codeToAddAtAnnotation of metaobject '"
						+ annotation.getCyanMetaobject().getName() + "' ");

		code.append(" } ");

		return new Tuple3<StringBuffer, String, String>(code,
				type.getPackageName(), type.getName());
		// return new Tuple2<StringBuffer, WrType>(code, type);
	}

}
