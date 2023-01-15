/**
 *
 */
package ast;

import java.util.List;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.Token;
import meta.WrMessageKeywordWithRealParameters;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** Represents a keyword and its real parameters of a message send. For example, the message send
 *       anObject with: anotherObject do: aFunction1, aFunction2
 * needs two objects of this class. The first one represents
 *     with: anotherObject
 * and the other represents
 *     do: aFunction1, aFunction2
 *
 * This class also represents keywords that represent dynamic calls such as
 *     ?put: "Peter"
 *  or
 *      ?do:  function1, function2
 *
 * @author José
 *
 */
public class MessageKeywordWithRealParameters implements GenCyan, ASTNode {

	public MessageKeywordWithRealParameters(Symbol keyword, Symbol backquoteSymbol, boolean backquote,
			List<Expr> exprList) {
		this.keyword = keyword;
		this.backquoteSymbol = backquoteSymbol;
		this.backquote = backquote;
		this.exprList = exprList;
		keywordNameWithoutSpecialChars = null;
	}

	@Override
	public WrMessageKeywordWithRealParameters getI() {
		if ( iMessageKeywordWithRealParameters == null ) {
			iMessageKeywordWithRealParameters = new WrMessageKeywordWithRealParameters(this);
		}
		return iMessageKeywordWithRealParameters;
	}


	WrMessageKeywordWithRealParameters iMessageKeywordWithRealParameters = null;

	@Override
	public void accept(ASTVisitor visitor) {
		if ( exprList != null ) {
			for ( Expr e : this.exprList )
				e.accept(visitor);
		}
		visitor.visit(this);
	}

	public Symbol getFirstSymbol() {
		if ( this.backquoteSymbol == null ) {
			return keyword;
		}
		else {
			return this.backquoteSymbol;
		}
	}
	public void setExprList(List<Expr> exprList) {
		this.exprList = exprList;
	}

	public List<Expr> getExprList() {
		return exprList;
	}
	public void setkeyword(Symbol keywordNameWithoutSpecialChars) {
		this.keyword = keywordNameWithoutSpecialChars;
	}
	public Symbol getkeyword() {
		return keyword;
	}


	/**
	 * return the keyword name without any of the characters: ':', '?', '.'
	   @return
	 */
	public String getkeywordNameWithoutSpecialChars() {
		if ( keywordNameWithoutSpecialChars == null ) {
			String s = this.keyword.getSymbolString();
			if ( s.startsWith("?.") )
				s = s.substring(2);
			else if ( s.charAt(0) == '?' )
				s = s.substring(1);
			if ( s.endsWith(":") )
				s = s.substring(0, s.length() - 1);
			/*
			int size = s.length();
			keywordNameWithoutSpecialChars = "";
			for (int i = 0; i < size; ++i) {
				char ch = s.charAt(i);
				if ( ch != ':' && ch != '?' && ch != '.' )
					keywordNameWithoutSpecialChars += ch;
			}
			*/
			keywordNameWithoutSpecialChars = s;
		}
		return keywordNameWithoutSpecialChars;
	}

	/**
	 * return the keyword name without the characters  '?',  '.'
	   @return
	 */
	public String getkeywordName() {
		if ( keywordName == null ) {
			String s = this.keyword.getSymbolString();
			if ( s.startsWith("?.") )
				s = s.substring(2);
			else if ( s.charAt(0) == '?' )
				s = s.substring(1);
			keywordName = s;
		}
		return keywordName;
	}



	/**
	 * there are several kinds of keywords:
	 *      box ?set: 0    // dynamic call
	 *      box ?get
	 *      box ?.set: 0   // nil-safe message send
	 *      box ?.get
	 */

	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		String name;

		String str = keyword.getSymbolString();
		String prefix = "";
		Token token = keyword.token;
		if  ( token == Token.INTER_ID || token == Token.INTER_ID_COLON )
			prefix = "?";
		else if ( token == Token.INTER_DOT_ID )
			prefix = "?.";




		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			name = prefix + cyanEnv.formalGenericParamToRealParam(str);
		}
		else {
			name = prefix + str;
		}

		// Token.INTER_DOT_ID, Token.INTER_DOT_ID_COLON, Token.INTER_ID_COLON, Token.INTER_ID
		if ( printInMoreThanOneLine ) {
			if ( backquote )
				pw.print("`");
			pw.print(name + " ");
			if ( exprList != null ) {
				int n = exprList.size();
				for (Expr e : exprList) {
					e.genCyan(pw, PWCounter.printInMoreThanOneLine(e), cyanEnv, genFunctions);
					--n;
					if (n > 0)
						pw.print(", ");
				}
			}

		}
		else {
			if ( backquote )
				pw.print("`");
			pw.print(name + " ");
			if ( exprList != null ) {
				int n = exprList.size();
				for (Expr e : exprList) {
					e.genCyan(pw, false, cyanEnv, genFunctions);
					--n;
					if (n > 0)
						pw.print(", ");
				}
			}
		}
	}


	public void calcInternalTypes(Env env) {
		try {
			env.pushCheckUsePossiblyNonInitializedPrototype(true);
			for ( Expr expr : exprList ) {
				expr.calcInternalTypes(env);
			}
		}
		finally {
			env.popCheckUsePossiblyNonInitializedPrototype();
		}
	}




	public boolean getBackquote() {
		return backquote;
	}


	public String asString(CyanEnv cyanEnv) {
		PWCharArray pwChar = new PWCharArray();
		genCyan(pwChar, true, cyanEnv, true);
		return pwChar.getGeneratedString().toString();
	}

	@Override
	public String asString() {
		return asString(NameServer.cyanEnv);
	}


	public Symbol getBackquoteSymbol() {
		return backquoteSymbol;
	}

	public void setBackquoteSymbol(Symbol backquote) {
		this.backquoteSymbol = backquote;
	}
	/**
	 * the hasBackquote symbol, if present, like in
	 *     self `s;
	 */
	Symbol backquoteSymbol = null;
	/**
	 * the keyword name (or method name). In the example above, it is "with:" for
	 * the first case
	 */
	private Symbol keyword;

	/**
	 * represents the real arguments
	 */
	private List<Expr> exprList;

	/**
	 * true if this keyword was preceded by `, hasBackquote, like in
	 *     elem `message;
	 * in which message is a String variable.
	 */
	private boolean backquote;

	/**
	 * the keyword name without any of the characters: ':', '?', '.'
	 */
	private String keywordNameWithoutSpecialChars;
	/**
	 * the keyword name
	 */
	private String keywordName;

	/**

	 * the unary message symbols (each one will result in an unary method
	 * at runtime).
	 */
	protected SymbolIdent unarySymbol;


}
