/**
 *
 */
package meta.cyanLang;

import java.util.List;
import meta.WrEnv;
import meta.WrPrototype;
import meta.WrSymbol;
import meta.Token;


/** Represents a keyword of a grammar method that is, in most cases,
 * ended by +, *, or ? such as
 *    (add: Int)+
 *    (case: Char do: Function<Nil>)+
 *    (gas: Float | alcohol: Float)
 *    ( (case: Char do: Function<Nil>)+ (else: Function<Nil>)? )
 *
 * In the last case, the keyword is not ended by +, *, or ? Nonetheless,
 * it is represented a MessageKeywordGrammar object.
 *
 * In any case, the keyword starts with "(".
 *
 * This abstract class has two subclasses. One, MessageKeywordGrammarList represents
 * composite keywords such as
 *    (case: char do: Function<Nil>)+
 * and MessageKeywordGrammarOrList represents keywords separated by | such as
 *    (gas: float | alcohol: float)
 *
 * @author José
 *
 */
abstract public class MessageKeywordGrammar extends IMessageKeyword {


	public MessageKeywordGrammar(List<IMessageKeyword> keywordArray, WrSymbol firstSymbol) {
		super();
		this.firstSymbol = firstSymbol;
		this.keywordArray = keywordArray;
		this.regularOperator = null;
	}




	public void setRegularOperator(WrSymbol regularOperator) {
		this.regularOperator = regularOperator;
	}
	public WrSymbol getRegularOperator() {
		return regularOperator;
	}

	public void setkeywordArray(List<IMessageKeyword> keywordArray) {
		this.keywordArray = keywordArray;
	}
	public List<IMessageKeyword> getkeywordArray() {
		return keywordArray;
	}


	public WrSymbol getFirstSymbol() {
		return firstSymbol;
	}

	@Override
	public void calcInterfaceTypes(WrEnv env) {
		for ( final IMessageKeyword keyword : keywordArray )
			keyword.calcInterfaceTypes(env);
	}

	@Override
	public String getFullName(WrEnv env)  {
		final StringBuffer sb = new StringBuffer();
		int size = keywordArray.size();
		for ( final IMessageKeyword s : keywordArray ) {
			sb.append(s.getFullName(env));
			if ( --size > 0 )
				sb.append(" ");
		}
		if ( regularOperator != null )
			sb.append(regularOperator.getSymbolString());
		return sb.toString();
	}


	@Override
	public boolean matchesEmptyInput() {
		if ( regularOperator != null && (regularOperator.token == Token.MULT || regularOperator.token == Token.QUESTION_MARK) )
			return true;
		for ( final IMessageKeyword sel : this.keywordArray ) {
			if ( !sel.matchesEmptyInput() )
				return false;
		}
		return true;
	}


	/**
	 * return the parameter type of the grammar method based on the method declaration.
	 * This is necessary if the user does not supply himself the type as in
	 *      fun (add: (Int)*) :t [ ... ]
	   @param env
	 */
	abstract public WrPrototype getParameterType(WrEnv env);
	/**
	 * the regular language operator that appear after the list of
	 * keywords. In
	 *    (case: char do: Function<Nil>)+
	 * regularOperator  is the symbol corresponding to Token.PLUS.
	 */
	protected WrSymbol regularOperator;
	/**
	 * the list of keywords. In
	 *    (case: char do: Function<Nil>)+
	 * keywordArray contains two keyword objets, one for "case: char" and one
	 * for "do: Function<Nil>".
	 */
	protected List<IMessageKeyword>  keywordArray;

	/**
	 * first symbol of this keyword
	 */
	private final WrSymbol firstSymbol;

}
