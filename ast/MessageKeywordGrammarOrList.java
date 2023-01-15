/**
 *
 */
package ast;

import java.util.List;
import meta.Token;
import meta.Tuple2;
import meta.WrEnv;
import meta.WrPrototype;
import meta.WrSymbol;
import meta.cyanLang.IMessageKeyword;
import meta.cyanLang.MessageKeywordGrammar;
import meta.cyanLang.MessageKeywordLexer;

/**
 * This class represents keywords separated by | such as
 *    (gas: Float | alcohol: Float)
 * or
 *    (gas: Float | alcohol: Float)+
 *
 * @author José
 *
 */
public class MessageKeywordGrammarOrList extends MessageKeywordGrammar {

	public MessageKeywordGrammarOrList(List<IMessageKeyword> keywordArray, WrSymbol firstSymbol) {
		super(keywordArray, firstSymbol);
	}

//	@Override
//	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
//
//		int size = this.getkeywordArray().size();
//		pw.print("(");
//		for ( MessageKeyword s : this.getkeywordArray() ) {
//			s.genCyan(pw, cyanEnv);
//			if ( --size > 0 )
//			    pw.print(" | ");
//		}
//		pw.print(")");
//		if ( this.getRegularOperator() != null )
//			pw.print(this.getRegularOperator().getSymbolString());
//
//	}

//	/**
//	 * return the Java name of a method that has this keyword. If the
//	 * keyword is
//	 *           (case: char | do: Proto)+
//	 *  the generated name will be
//	 *      _left_s_case_p_char_s_do_p_Proto_right_or_PLUS
//	 *  if it is
//	 *  	     (case: char | do: Proto)
//	 *  the generated name will be
//	 *      left_s_case_p_char_s_do_p_Proto_right_or
//	 */
//	@Override
//	public String getJavaName() {
//		String s = "left";
//		for ( MessageKeyword keyword : keywordArray ) {
//			s = s + "_s_" + keyword.getJavaName();
//		}
//		s = s + "_right_or";
//		if ( regularOperator != null )
//			s = s + "_" + regularOperator.getSymbolString();
//		return s;
//	}


	@Override
	public String getStringType() {
		String s;
		s = getStringUnionType();
		if ( regularOperator != null ) {
			switch ( regularOperator.token ) {
			case QUESTION_MARK:
				   // example: (add: Int)?,    UUnion<Int>
				return "Union<some, " + s + ", none, Any>";
			case PLUS:
			case MULT:
				   // example: (add: Int)*
				return "Array<" + s + ">";
			default:
				return "compile time error";
			}
		}
		else {
			// no operator. Example:  (add: Int) | str: String
			return s;
		}

	}

	public String getStringUnionType() {
		String s;
		s = "";
		int i = 1;
	    int sizekeywordArray = keywordArray.size();
		for ( final IMessageKeyword keyword : keywordArray ) {
			s = s + "f" + i + ", ";
			s = s + keyword.getStringType();
			if ( --sizekeywordArray > 0 )
				s += ", ";
			++i;
		}

		s = "Union<" + s + ">";
		return s;
	}

	@Override
	public WrPrototype getParameterType(WrEnv env) {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}


	@Override
	public Tuple2<String, String> parse(MessageKeywordLexer lexer, WrEnv env) {
		/*

            ( ( add: Int with: Char ) | ( aaa: Int bbb: Char ) ) +

    	    add: 0 with: 'a' add: 1 with: 'b' aaa: 2 bbb: 'c'

            Array<Union<f1, Tuple<Int, Char>, f2, Tuple<Int, Char>>>

            [   Union<f1, Tuple<Int, Char>, f2, Tuple<Int, Char>> f1: [. 0, 'a' .],
                Union<f1, Tuple<Int, Char>, f2, Tuple<Int, Char>> f1: [. 1, 'b' .],
                Union<f1, Tuple<Int, Char>, f2, Tuple<Int, Char>> f2: [. 2, 'c' .]
            ]

            if it was    ( ( add: Int with: Char ) | ( aaa: Int bbb: Char ) ) ?
                  add: 0 with: 'a'
            Union<some, Union<f1, Tuple<Int, Char>, f2, Tuple<Int, Char>>, none, Any>

            Union<some, Union<f1, Tuple<Int, Char>, f2, Tuple<Int, Char>>, none, Any>
                  some: ( Union<f1, Tuple<Int, Char>, f2, Tuple<Int, Char>> f1: [. 0, 'a' .] ) ;


		 */
		int fn;
		Tuple2<String, String> t;
		String s;
		final String unionType = this.getStringUnionType();

		if ( this.regularOperator == null ) {
			s = " ( " + unionType + "() ";
		}
		else if ( this.regularOperator.token == Token.QUESTION_MARK ) {
			s = " ( " + this.getStringType() + "() ";
		}
		else {
			s = " [ ";
		}
		int n = 0;
		if ( lexer.current() == null ) {
			return null;
		}
		while ( lexer.current() != null ) {
			t = null;
			fn = 1;
			for ( final IMessageKeyword formalkeyword : this.keywordArray ) {
				final int next = lexer.getIndex();
				t = formalkeyword.parse(lexer, env);
				if ( t != null ) {
					break;
				}
				else {
					lexer.setIndex(next);
				}
				++fn;
			}
			if ( t == null ) {
				if ( n == 0 ) {
					if ( this.regularOperator == null )
						return null;
					else if ( this.regularOperator.token == Token.QUESTION_MARK )  {
						s += " none: Any ) ";
						return new Tuple2<String, String>(s, this.getStringType());
					}
					else if ( this.regularOperator.token == Token.MULT ) {
						s += this.getStringType() + "() ] ";
						return new Tuple2<String, String>(s, this.getStringType());
					}
					else {
						return null;
					}
				}
				else {
					s += " ] ";
					return new Tuple2<String, String>(s, this.getStringType());
				}
			}
			else {
				if ( this.regularOperator != null && this.regularOperator.token == Token.QUESTION_MARK ) {
					s += "some: (" + unionType + "() f" + fn + ": " + t.f1 + ") ) ";
					return new Tuple2<String, String>(s, this.getStringType());
				}
				// regularOperator.token != Token.QUESTION_MARK
				if ( n > 0 )
					s += ", ";

				/*
				 * { var uu = Union<f1, Tuple<Int, Char>, f2, Tuple<Int, Char>> new;
                     uu f1: [. 0, 'a' .] } eval,
				 */
				if ( this.regularOperator == null ) {
					s += "f" + fn + ": " + t.f1 + " ) ";
					return new Tuple2<String, String>(s, this.getStringType());
				}
				else {
					s += " ( " + unionType + "() f" + fn + ": " + t.f1 + " ) ";
				}
			}
			++n;
		}

		return new Tuple2<String, String>(s + " ] ", this.getStringType());

	}

//	@Override
//	void setAstRootType(ObjectDec astRootType, Env env, Symbol first) {
//		// TODO Auto-generated method stub
//
//	}

}
