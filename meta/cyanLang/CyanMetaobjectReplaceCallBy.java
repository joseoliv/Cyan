package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionMessageSend_semAn;
import meta.ICompiler_parsing;
import meta.IParseWithCyanCompiler_parsing;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple3;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprIdentStar;
import meta.WrExprMessageSendUnaryChainToExpr;
import meta.WrExprMessageSendWithKeywordsToExpr;
import meta.WrMessageKeywordWithRealParameters;
import meta.WrMessageWithKeywords;
import meta.WrMethodDec;
import meta.WrParameterDec;

/** This metaobject should be attached to a final method that does not override a super-prototype method.
 *  It replaces a message passing by the attached text, replacing the parameters.<br>
 *  <code>
 *      {@literal @}inline{* 2*n *}<br>
 *      func twice: Int n { return n + n }<br>
 *  </code>
 *  In a message passing in which the method to be called would be <code>twice</code>, the message send
 *  is replaced by <code>2*n</code> in which <code>n</code> is the parameter.
 *
   @author jose
 */
public class CyanMetaobjectReplaceCallBy extends CyanMetaobjectAtAnnot
         implements IActionMessageSend_semAn, IParseWithCyanCompiler_parsing {

	public CyanMetaobjectReplaceCallBy() {
		super("replaceCallBy", AnnotationArgumentsKind.ZeroOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC },
				Token.PUBLIC );
	}


	@Override
	public void check() {
		final List<Object> javaParamList = this.getAnnotation().getJavaParameterList();
		if ( javaParamList != null ) {
			if ( javaParamList.size() > 1 ) {
				this.addError("This annotation should take zero or one parameter");
			}
			else if ( javaParamList.size() == 1 ) {
				if ( !((String ) javaParamList.get(0)).equals("once") ) {
					this.addError("If this annotation takes a parameter, it should be 'once' without quotes");
				}
				else {
					this.once = true;
				}
			}
		}
	}


	@Override
	public Tuple3<StringBuffer, String, String> semAn_analyzeReplaceKeywordMessage(
			WrExprMessageSendWithKeywordsToExpr messageSendExpr, WrEnv env) {

		final WrAnnotationAt atAnnot = this.getAnnotation();
		final char []text = atAnnot.getTextAttachedDSL();
		final WrMessageWithKeywords message = messageSendExpr.getMessage();
		final WrMethodDec method = (WrMethodDec) atAnnot.getDeclaration();
		if ( !method.getIsFinal() ) {
			this.addError("This annotation should only be attached to 'final' methods");
		}
		if ( method.getHasOverride() ) {
			this.addError("This annotation should only be attached to non-'override' methods");
		}


		final List<String> realParam = new ArrayList<>();
		for ( final WrMessageKeywordWithRealParameters sel : message.getkeywordParameterList() ) {
			for ( final WrExpr e : sel.getExprList() ) {
				realParam.add(e.asString());
			}
		}
		String s;
		int i = 0;
		if ( once ) {
			/*
			 * use an anonymous function for that
			 *       receiver at: expr1  put: expr2
			 *
			 *  is changed to
			 *       { var tmp1 = expr1; var tmp2 = expr2; ^(receiver at: tmp1 put: tmp2) } eval
			 */
			s = " { ";
			final String []tmpVarList = new String[method.getMethodSignature().getParameterList().size()];
			for ( @SuppressWarnings("unused") final WrParameterDec param : method.getMethodSignature().getParameterList() ) {
				tmpVarList[i] = MetaHelper.nextIdentifier();
				s += "var " + tmpVarList[i] + " = " + realParam.get(i) + "; ";
				++i;
			}
			String s2 = new String(text);
			i = 0;
			for ( final WrParameterDec param : method.getMethodSignature().getParameterList() ) {
				s2 = s2.replace(param.getName(), tmpVarList[i]);
				++i;
			}
			s += "^" + s2 + " } eval ";
		}
		else {
			s = new String(text);
			for ( final WrParameterDec param : method.getMethodSignature().getParameterList() ) {
				s = s.replace(param.getName(), "(" + realParam.get(i) + ")");
				++i;
			}
		}

		// simulate implicits of Scala, sort of
//		WrStatementList slist = env.getCurrentMethod().getStatementList(env);
//		for ( WrStatement s1 : slist.getStatementList() ) {
//			if ( s1 instanceof WrStatementLocalVariableDecList ) {
//				if ( ((WrStatementLocalVariableDecList) s1).getLocalVariableDecList().get(0).getName().equals("k") ) {
//					s += " * k";
//				}
//			}
//		}

		/*
		 * code is not used. It will be some day
		return new Tuple2<StringBuffer, Type>(code, type);
		 */
		return new Tuple3<StringBuffer, String, String>(new StringBuffer(s), null, null);
		//return new Tuple2<StringBuffer, WrType>(new StringBuffer(s), type);
	}


	@Override
	public Tuple3<StringBuffer, String, String> semAn_analyzeReplaceUnaryMessage(
			WrExprMessageSendUnaryChainToExpr messageSendExpr, WrEnv env) {

		return replaceUnaryMessage();
	}


	/**
	   @return
	 */
	private Tuple3<StringBuffer, String, String> replaceUnaryMessage() {
		final List<Object> javaParamList = this.getAnnotation().getJavaParameterList();
		if ( javaParamList != null ) {
			if ( javaParamList.size() > 0 ) {
				this.addError("This annotation should not take parameters when used with a unary method");
			}
		}
		final WrAnnotationAt annotation = this.getAnnotation();
		final char []text = annotation.getTextAttachedDSL();
		final WrMethodDec method = (WrMethodDec) annotation.getDeclaration();
		if ( !method.getIsFinal() ) {
			this.addError("This annotation should only be attached to 'final' methods");
		}
		if ( method.getHasOverride() ) {
			this.addError("This annotation should only be attached to non-'override' methods");
		}
		String s;

		s = new String(text);
		return new Tuple3<StringBuffer, String, String>(new StringBuffer(" ( " + s + " ) "),
				null, null);
		//return new Tuple2<StringBuffer, WrType>(new StringBuffer(" ( " + s + " ) "), type);
	}


	@Override
	public
	Tuple3<StringBuffer, String, String> semAn_analyzeReplaceUnaryMessageWithoutSelf(
			WrExprIdentStar messageSendExpr, WrEnv env) {
		return replaceUnaryMessage();

	}


	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public void parsing_parse(ICompiler_parsing compiler_parsing) {
		compiler_parsing.next();
		compiler_parsing.expr();
		compiler_parsing.removeLastExprStat();
		if ( compiler_parsing.getSymbol().token != Token.EOLO ) {
			this.addError(compiler_parsing.getSymbol(), "A single expression was expected. After it, I found '"
					+ compiler_parsing.getSymbol().getSymbolString() + "'");
		}
	}

	/**
	 * true if the real parameters should be kept in a temporary variable before used
	 * in the expression of the DSL
	 */
	private boolean once;


}
