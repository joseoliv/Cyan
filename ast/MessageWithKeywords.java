/**
 *
 */
package ast;

import java.util.List;
import lexer.Symbol;
import lexer.SymbolOperator;
import meta.Token;
import meta.WrMessageWithKeywords;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;


/** Represents a message send, but without any data refering to
 *  the receiver (just the message).
 *  That is, in a message send
 *        r filter: f  add: (n + 1), 10
 *  an object of this class would represent "filter: f  add: (n + 1), 10".
 *  The complete message send would be represented by an object of
 *  ExprMessageSendWithKeywordsToExpr.
 *  This class also represents message sends with a keyword ended with ':' but
 *  that does not take parameters such as
 *       file open: read: ;
 *
 * @author José
 *
 */
public class MessageWithKeywords implements ASTNode {

	public MessageWithKeywords() { }

	public MessageWithKeywords(
			List<MessageKeywordWithRealParameters> keywordParameterList) {
		super();
		this.keywordParameterList = keywordParameterList;
	}

	@Override
	public WrMessageWithKeywords getI() {
		if ( iMessageWithKeywords == null ) {
			iMessageWithKeywords = new WrMessageWithKeywords(this);
		}
		return iMessageWithKeywords;
	}

	private WrMessageWithKeywords iMessageWithKeywords = null;


	@Override
	public void accept(ASTVisitor visitor) {
		if ( keywordParameterList != null ) {
			for ( MessageKeywordWithRealParameters s : this.keywordParameterList ) {
				s.accept(visitor);
			}
		}
		visitor.visit(this);
	}
	/**
	 * return true if this is a dynamic message send, those whose keywords are
	 * preceded by ? as in
	 *       person ?setAge: 10;
	 *       n = person ?age;
	 */
	public boolean isDynamicMessageSend() {
		if ( this.keywordParameterList != null &&
			 this.keywordParameterList.get(0).getkeyword().token == Token.IDENTCOLON )
			return false;
		else
			return true;
	}


	/*
	 * return true if there is a backquote, `, before one of the keywords. If there is
	 * ` in one keyword, all of them should be preceded by `
	 */
	public boolean getBackquote() {
		return keywordParameterList.get(0).getBackquote();
	}


	public List<MessageKeywordWithRealParameters> getkeywordParameterList() {
		return keywordParameterList;
	}

	public void setkeywordParameterList(List<MessageKeywordWithRealParameters> keywordParameterList) {
		this.keywordParameterList = keywordParameterList;
	}


	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( printInMoreThanOneLine ) {
			int i = 0;
			int size = keywordParameterList.size();
			pw.add();
			for ( MessageKeywordWithRealParameters p : keywordParameterList ) {
				++i;
				if ( i > 1  )
					pw.printIdent("");
				p.genCyan(pw, PWCounter.printInMoreThanOneLine(p), cyanEnv, genFunctions);
				if ( --size > 0 )
					pw.println();
			}
			pw.sub();
		}
		else {
			int size = keywordParameterList.size();
			for ( MessageKeywordWithRealParameters p : keywordParameterList ) {
				p.genCyan(pw, false, cyanEnv, genFunctions);
				if ( --size > 0 )
					pw.print( " " );
			}
		}
	}

	/**
	 * 	 * In the general case:
	 *         keywordName + ("_p" + typeName)* +
	 *         ("_s" + keywordName + ("_p" + typeName)* )+
	 *
	 *
	 */
	public String getJavaMethodName() {
		return NameServer.getJavaMethodNameOfMessageSend(this);
	}

	public String getJavaMethodNameReceiverJavaObject() {
		if ( keywordParameterList.size() != 1 ) {
			   // more than one keyword
			return null;
		}
		else {
			String methodName = keywordParameterList.get(0).getkeywordName();
			if ( methodName.endsWith(":") ) {
				return methodName.substring(0, methodName.length()-1);
			}
			else {
				return methodName;
			}
		}
	}

	public void calcInternalTypes(Env env) {
		for ( MessageKeywordWithRealParameters keywordWithRealParameter:  keywordParameterList ) {
			keywordWithRealParameter.calcInternalTypes(env);
		}
	}

	/**
	 * returns the name of the method that should be called by this message send.
	 * The name includes only the keywords, without the parameter types, but with the
	 * number of parameters. This method returns the names of all keywords plus its number of parameters concatenated.
	 * That is, the return for the call <br>
	 * <code>obj with: n, ch plus: f;</code><br>
	 * would be <code>with:2 plus:1</code	 *
	 * @return
	 */
	public String getMethodNameWithParamNumber() {
		String s = "";

		if ( keywordParameterList.get(0).getkeyword() instanceof SymbolOperator ) {
			  // operators with keywords are always binary so they have one parameter
			return keywordParameterList.get(0).getkeyword().getSymbolString() + "1";
		}
		else {
			int size = keywordParameterList.size();
			for ( MessageKeywordWithRealParameters keywordWithRealParameters: keywordParameterList ) {
				s = s + keywordWithRealParameters.getkeyword().getSymbolString() +
						keywordWithRealParameters.getExprList().size();
				if ( --size > 0 )
					s += " ";
			}
			return s;
		}
	}


	/**
	 * returns the name of the method that should be called by this message send.
	 * The name includes only the keywords, without the parameter types
	 * @return
	 */
	public String getMethodName() {
		String s = "";
		for ( MessageKeywordWithRealParameters keywordWithRealParameters: keywordParameterList )
			s = s + keywordWithRealParameters.getkeyword().getSymbolString();
		return s;
	}



	public Symbol getFirstSymbol() {
		return this.keywordParameterList.get(0).getkeyword();
	}


	/**
	 * return the token of the first keyword
	 */
	public Token getTokenFirstkeyword() {
		return this.keywordParameterList.get(0).getkeyword().token;
	}


	public String asString(CyanEnv cyanEnv) {
		PWCharArray pwChar = new PWCharArray();
		genCyan(pwChar, true, cyanEnv, true);
		return pwChar.getGeneratedString().toString();
	}

	public String asString() {
		return asString(NameServer.cyanEnv);

	}


	/**
	 *  represents the message name and arguments. In message send
	 *           anObject with: anotherObject do: aFunction1 aFunction2
	 *  there would be created two objects of MessageKeywordWithRealParameters
	 *  for list  keywordParameterList. One represents
	 *           with: anotherObject
	 *  and the other represents
	 *           do: aFunction1 aFunction2
	 */
	private List<MessageKeywordWithRealParameters> keywordParameterList;



}
