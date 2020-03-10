/**
 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrMethodKeywordWithParameters;
import meta.cyanLang.MessageKeywordLexer;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** This class represents a keyword declared with ":" at the end
 *   and its parameters in a method declaration.
 *
 * For example, if the method is
 *     public fun to: (Int max)  do: Function<Nil> aFunc { /* method body * / }
 * Then there should be two objects of MethodKeywordWithParameters:
 *   1. one for "to:" with one parameter
 *   2. one for "do:" with one parameter
 *
 *  A keyword may not have parameters. For example:
 *      public fun read: { ... }
 *      public fun amount: gas: Float
 *  In the last line, "amount:" does not have any parameters.
 *
 *
 * @author José
 *
 */
public class MethodKeywordWithParameters implements ASTNode, GenCyan {

	public MethodKeywordWithParameters(Symbol keyword) {
		this.keyword = keyword;
		parameterList = new ArrayList<ParameterDec>();
		keywordNameWithoutSpecialChars = null;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		for (final ParameterDec p : this.parameterList) {
			p.accept(visitor);
		}
		visitor.visit(this);
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


	/**
	 * the ast for this grammar method has its root in type astRootType, a type given by the user.
	 */
	protected Type astRootType;

	public void addParamDec( ParameterDec paramDec ) {
		parameterList.add(paramDec);
	}

	public void setParameterList(List<ParameterDec> parameterList) {
		this.parameterList = parameterList;
	}
	public List<ParameterDec> getParameterList() {
		return parameterList;
	}

	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			pw.print(cyanEnv.formalGenericParamToRealParam(keyword.getSymbolString()));
		}
		else {
			String keywordSymbol = keyword.getSymbolString();
//			if ( cyanEnv.getGenInterfacesForCompiledCode() &&
//					keywordSymbol.equals("init:") ) {
//				pw.print("new:");
//			}
//			else {
				pw.print(keywordSymbol);
//			}
		}
		int size = parameterList.size();
		boolean onlyOneTypeWithoutParameterName = false;
		if ( size == 1 && parameterList.get(0).getVariableSymbol() == null ) {
				    // there is one parameter and the first one does not have a type
				onlyOneTypeWithoutParameterName = true;
		}
		else if ( size == 0 )
			onlyOneTypeWithoutParameterName = true;

		pw.print(" ");
		if ( ! onlyOneTypeWithoutParameterName )
			pw.print("(");
		for ( final ParameterDec p : parameterList ) {
			p.genCyan(pw, false, cyanEnv, genFunctions);
			--size;
			if ( size > 0 )
				pw.print(", ");
		}
		if ( ! onlyOneTypeWithoutParameterName )
    		pw.print(")");
		pw.print(" ");
	}


	/**
	 * considering the method has just one parameter, which is its type?
	 * If the method is
	 *      public fun add: Int, String with: Float { }
	 * its type is Tuple<int, String, float>
	 * An object of this class represents "add: int, String" or "with: float".
	 * It would return "int, String" and "float" in these cases.
	 */
	public String getStringType() {
		int size = parameterList.size();
		if ( size == 1 )
			return PWCounter.toStringBuffer(parameterList.get(0).getTypeInDec()).toString();
		else {
			String s = "Tuple<";
			for ( final ParameterDec p : parameterList ) {
				s = s + PWCounter.toStringBuffer(p.getTypeInDec()).toString();
				if ( --size > 0 )
					s = s + ", ";
			}
			return s + ">";
		}
	}

	/**
	 * the Java name of the method with this keyword. For example, a keyword
	 *      public fun run: [ ]
	 * the Java name is "run". A keyword
	 * 	    format: String print: int
	 * has Java name
	 *      format_s_print
	 * Underscores are duplicated to avoid confusion with underscores added
	 * by the Compiler. Therefore keyword
	 * 	    person_name:
	 * has Java name
	 *     person__name
	 */

	public String getJavaName() {
		return MetaHelper.getJavaNameOfkeyword(keyword.getSymbolString());
	}


	public String getFullName(Env env) {
		if ( parameterList == null || parameterList.size() == 0 )
			return keyword.getSymbolString();
		else {
			String s = keyword.getSymbolString();
			int size = parameterList.size();
			if ( size > 0 )
				s = s + " ";
			for ( final ParameterDec p : parameterList ) {
				if ( p.getType(env) == null ) {
					s = s + MetaHelper.dynName;
				}
				else {
					s = s + p.getType(env).getFullName(env);
				}
				if ( --size > 0 )
					s = s + ", ";

			}
			return s;
		}
	}


	public String getFullName() {
		if ( parameterList == null || parameterList.size() == 0 )
			return keyword.getSymbolString();
		else {
			String s = keyword.getSymbolString();
			int size = parameterList.size();
			if ( size > 0 )
				s = s + " ";
			for ( final ParameterDec p : parameterList ) {
				if ( p.getType() == null ) {
					s = s + MetaHelper.dynName;
				}
				else {
					s = s + p.getType().getFullName();
				}
				if ( --size > 0 )
					s = s + ", ";

			}
			return s;
		}
	}



	public String getName() {
		return keyword.getSymbolString();
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

	public Symbol getkeyword() {
		return keyword;
	}

	public void calcInternalTypes(Env env) {


		for ( final ParameterDec parameter: parameterList ) {
			final String parameterName = parameter.getName();
			if ( parameterName != null ) {
				if ( env.searchLocalVariableParameter(parameterName) != null ) {
					env.searchLocalVariableParameter(parameterName);
					env.error(parameter.getFirstSymbol(), "Parameter '" + parameterName + "' is being redeclared", true, true);
				}
			}
			parameter.calcInternalTypes(env);
		}
	}

	public void calcInterfaceTypes(Env env) {
		for ( final ParameterDec parameterDec : parameterList )
			parameterDec.calcInternalTypes(env);
	}

	/**
	 * generate the parameter declarations of this method.
	 * @param env
	 */
	public void genJava(PWInterface pw, Env env) {
		int size = parameterList.size();
		for ( final ParameterDec paramDec : parameterList ) {
			paramDec.genJava(pw, env);
			if ( --size > 0 )
				pw.print(", ");
		}
	}

	public Tuple2<String, String> parse(MessageKeywordLexer lexer, Env env) {
		return null;
	}

	public boolean matchesEmptyInput() {
		return false;
	}


	@Override
	public WrMethodKeywordWithParameters getI() {
		if ( iMessageKeywordWithParameters == null ) {
			iMessageKeywordWithParameters = new WrMethodKeywordWithParameters(this);
		}
		return iMessageKeywordWithParameters;
	}

	private WrMethodKeywordWithParameters iMessageKeywordWithParameters;

	/**
	 * the keyword. It is "to:" in
	 *     to: Int max
	 * and "amount:" in
	 *     amount:  // no parameters
	 */
	private final Symbol keyword;
	/**
	 * list of the parameters associated with keywordName. It may be empty
	 * for a keyword may not have parameters.
	 */
	private List<ParameterDec> parameterList;
	/**
	 * the name of the keyword without ':', '?.' and the like
	 */
	private String keywordNameWithoutSpecialChars;

}
