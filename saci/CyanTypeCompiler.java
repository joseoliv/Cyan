package saci;

import java.util.ArrayList;
import java.util.List;
import ast.CompilationUnit;
import ast.Expr;
import ast.ExprGenericPrototypeInstantiation;
import ast.ExprIdentStar;
import ast.ExprTypeUnion;
import ast.Type;
import error.CompileErrorException;
import lexer.Symbol;
import lexer.SymbolIdent;
import lexer.SymbolOperator;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;

/**
 * this is a compiler for a Cyan type
   @author jose
 */
public class CyanTypeCompiler {

	public CyanTypeCompiler(Env env) {
		this.env = env;
	}

	public void test() {
		try {
			this.testThrows();
//			compareTest();
		}
		catch ( CompileErrorException e ) {
			e.printStackTrace();
		}
	}

	public void compareTest(String typeName, String[]typeNameList) {

		String firstJavaName = fromCyanNameToJavaName(typeNameList[0]);
		int sizeFirst = firstJavaName.length();
		System.out.println(typeName + " has the following Java names:");
		List<String> messageList = new ArrayList<>();
		int i1 = 0;
		for ( String cyanTypeName : typeNameList ) {

			String otherJavaName = fromCyanNameToJavaName(cyanTypeName);
			System.out.println("    " + otherJavaName);
			if ( ! otherJavaName.equals(firstJavaName) ) {
				int sizeOther = otherJavaName.length();
				int posDiff = -1;
				char chDiff = ' ';
				for ( int j = 0; j < sizeFirst && j < sizeOther; ++j ) {
					if ( firstJavaName.charAt(j) != otherJavaName.charAt(j) ) {
						posDiff = j + 1;
					}
				}
				messageList.add("first and " + i1 +
						"th Java names are different at position " + posDiff + " ch = " + chDiff);

			}
			++i1;
		}
	}

	public void compareTest() {
		compareTest("Array<Array<Int>>", new String[] {
				"Array<Array<Int>>",
				"cyan.lang.Array<Array<Int>>",
				"Array<Array<cyan.lang.Int>>",
				"Array<cyan.lang.Array<Int>>",
				"Array<cyan.lang.Array<cyan.lang.Int>>",
				"cyan.lang.Array<cyan.lang.Array<cyan.lang.Int>>"
				});
	}


	public void testThrows() {
		for ( String cyanTypeName : new String[] {
				"Array<Array<Int>>",
				"cyan.lang.Array<Array<Int>>",
				"Array<Array<cyan.lang.Int>>",
				"Array<cyan.lang.Array<Int>>",
				"Array<cyan.lang.Array<cyan.lang.Int>>",
				"cyan.lang.Array<cyan.lang.Array<cyan.lang.Int>>",
//				_Array_LT_GP_Array_LT_GP_CyInt_GT_GT
//				_Array_LT_GP__Array_LT_GP_Object_GT_GT.class
//
//
//				_Function_LT_GP__Array_LT_GP_CyInt_GT_GP__Nil_GT
//				_Function_LT_GP__Array_LT_GP_CyInt_GT__GP__Nil_GT.java



//				"Array<Tuple<key, String, value, Any>>",
//				"Tuple<key, String, value, cyan.lang.Int>",
//				"Dyn", "Dyn|AAA", "Tuple<cyan.lang.Int, Dyn>", "Array<Int|Dyn>",
//				"Array<Any|Dyn|cyan.lang.Int>",
//				"Tuple<Int, Char>", "Int", "String", "cyan.lang.Int", "Person",
//				"main.Person", "main.util.Person",
//				"Tuple<cyan.lang.Int, Char>",
//				"Tuple<Int, cyan.lang.Char>", "Tuple<cyan.lang.Int, cyan.lang.Char>",
//				"Tuple<Person, aaa.bbb.ddd.CCC>",
//				"Tuple<Int,Char|String|Nil>", "Tuple<Int,Char|String|cyan.lang.Nil>",
//				"Tuple<cyan.lang.Int, cyan.lang.Char|cyan.lang.String|cyan.lang.Nil>",
//				"Tuple<cyan.lang.Int, Char|cyan.lang.String|Nil>",
//				"Tuple<cyan.lang.Int, Char|cyan.lang.String|Nil>|Char",
//				"Tuple<Tuple<Int, Char|Int>, Char|String, cyan.lang.String>",
//				"Function<Int|Char, Int>",
//				"Array<Tuple< Tuple<Int|Char, Char|Int>, Int, Nil >>",
//				"Function<Function<A|B|C, B, C>, Function<Function<Int, AAA, BB|C>, String>>"
		} ) {
			System.out.println("cyan type: " + cyanTypeName + "   '" +
					fromCyanNameToJavaName(cyanTypeName) + "'");
		}
	}
	/*
	 * parse type cyanTypeName or a Cyan identifier or an operator method.
	 *
	 * if cyanTypeName is a Type, it is parsed according to the grammar below.
	 * The method returns the Java name of the type, identifier, or operator.
	 *
	 * It is assumed that:
	 *    a) there is no package before the cyanTypeName
	 *    b) 'typeof' is not used
	 *
        Type       ::= SingleType { '|' SingleType }
        SingleType ::= QualifId { '<' TypeList '>' } | BasicType
        BasicType  ::= 'Byte' | 'Short' | 'Int' | 'Long' | 'Float' |
                      'Double' | 'Char' | 'Boolean'
        QualifId   ::= Id { '.' Id }
        TypeList   ::= Type { ',' Type }
	 *
	 */
	public static String cyanType_noPackage_ToJavaName(String cyanTypeName) {
		CyanTypeCompiler c = new CyanTypeCompiler(null);
		return c.fromCyanNameToJavaName(cyanTypeName);
	}

	public static Expr cyanType_noPackage_ToExpr(String cyanTypeName, Env env) {
		CyanTypeCompiler c = new CyanTypeCompiler(env);
		return c.fromCyanNameToExpr(cyanTypeName);
	}

	static public Type singleTypeFromStringThrow(String typeAsString,
			Symbol symUsedInError, String message, CompilationUnit compUnit,
			Env env) {

		Expr prototypeAsExpr = null;
		try {
			prototypeAsExpr = cyanType_noPackage_ToExpr(typeAsString, env);
		}
		catch (final CompileErrorException cee) {
			/*
			 * an horrible thing to do. No better implementation in sight. /
			 * UnitError lastError =
			 * compUnit.getErrorList().get(compUnit.getErrorList().size()-1);
			 * String errorMessage = lastError.getMessage();
			 * lastError.setMessage(message + " " + errorMessage);
			 */
			throw new CompileErrorException(message + " " + cee.getMessage());
		}
		return Compiler.searchCreateTypeFromExpr(symUsedInError, compUnit, env,
				prototypeAsExpr);

	}

	/*
	 * parse type cyanTypeName or a Cyan identifier or an operator method.
	 *
	 *
	 * The method returns the expression of the type or identifier. It
	 * return null if cyanTypeName is an operator like '-' or '++'
	*/
	public Expr fromCyanNameToExpr(String cyanTypeName) {
		Tuple2<Expr, String> t = parseType(cyanTypeName);
		if ( t != null ) {
			return t.f1;
		}
		else {
			return null;
		}
	}

	public String fromCyanNameToJavaName(String cyanTypeName) {
		Tuple2<Expr, String> t = parseType(cyanTypeName);
		if ( t != null ) {
			return t.f2;
		}
		else {
			return null;
		}
	}

	public Tuple2<Expr, String> parseType(String cyanTypeName) {
		typeName = cyanTypeName;
		text = cyanTypeName.toCharArray();
		size = cyanTypeName.length();
		i = 0;
		id = "";
		symbol = null;


		String s = MetaHelper.alphaName(cyanTypeName);
		if ( s != null ) {
			return new Tuple2<Expr, String>(null, s);
		}

		next();
		Tuple2<Expr, String> ret = type();
		if ( token != Token.EOF ) {
			error("unexpected end of type");
		}
		return ret;
	}


	/**
	   @return
	 */
	private Tuple2<Expr, String> type() {
		Tuple2<Expr, String> ret = null;
		final List<Tuple2<Expr, String>> typeJavaNameArray = new ArrayList<Tuple2<Expr, String>>();
		Tuple2<Expr, String> singleType = singleType();
		typeJavaNameArray.add(singleType);
		while ( token == Token.BITOR ) {
			next();
			singleType = singleType();
			typeJavaNameArray.add(singleType);
		}
		if ( typeJavaNameArray.size() == 1 ) {
			ret = singleType;
		}
		else {
			List<Expr> typeArray = new ArrayList<>();
			for ( Tuple2<Expr, String> t : typeJavaNameArray ) {
				typeArray.add(t.f1);
			}
			ExprTypeUnion unionType = new ExprTypeUnion(
					env != null ? env.getCurrentMethod() : null,
					typeArray);
			ret = new Tuple2<Expr, String>(unionType, "Object");
		}
		return ret;
	}

	/**
	 *
	 *         SingleType ::= QualifId { '<' TypeList '>' } | BasicType
	 *         TypeList   ::= Type { ',' Type }
	   @return
	 */
	private Tuple2<Expr, String> singleType() {
		Tuple2<Expr, String> t = this.qualifId();
		String javaName = t.f2;

		if ( token == Token.LT ) {
			List<List<Expr>> realTypeListList = new ArrayList<>();
			while ( token == Token.LT ) {
				next();
				javaName += "_LT_GP_";
				List<Tuple2<Expr, String>> typeList = new ArrayList<>();
				Tuple2<Expr, String> typeArg = type();
				List<Expr> exprList = new ArrayList<>();
				typeList.add(typeArg);
				exprList.add(typeArg.f1);
				javaName += typeArg.f2;
				while ( token == Token.COMMA ) {
					next();
					typeArg = type();
					typeList.add(typeArg);
					exprList.add(typeArg.f1);
					javaName += "_GP_" + typeArg.f2;
				}
				if ( token != Token.GT ) {
					error("'>' expected");
				}
				next();
				javaName += "_GT";
				realTypeListList.add(exprList);
			}
			return new Tuple2<Expr, String>(
					new ExprGenericPrototypeInstantiation( (ExprIdentStar ) t.f1,
					    realTypeListList,
	                    env != null ? env.getCurrentPrototype() : null,
	                    null,
	                    env != null ? env.getCurrentMethod() : null), javaName);
		}
		else {
			return t;
		}
	}


	/*
	 *         QualifId   ::= Id { '.' Id }

	 */
	private Tuple2<Expr, String> qualifId() {
		if ( token != Token.IDENT ) {
			error("identifier expected. Found " + symbol.getSymbolString());
		}
		List<Symbol> idList = new ArrayList<>();
		String s = id;
		idList.add(symbol);
		next();
		int numDots = 0;
		while ( token == Token.PERIOD ) {
			next();
			if ( token != Token.IDENT ) {
				error("identifier expected ");
			}
			s += "." + id;
			idList.add(symbol);
			next();
			++numDots;
		}
		if ( s.startsWith(NameServer.cyanLanguagePackageNameDot) ) {
			s = s.substring(NameServer.cyanLanguagePackageNameDot.length());
			idList.remove(0);
			idList.remove(0);
			numDots = 0;
		}
		String javaBasicTypeName = MetaHelper.cyanJavaBasicTypeTable.get(s);
		if ( javaBasicTypeName != null ) {
			s = javaBasicTypeName;
		}
		else {
			s = getJavaNameFromQualifId(s, numDots);
		}
		return new Tuple2<Expr, String>(
				new ExprIdentStar(idList, symbol, env != null ? env.getCurrentMethod() : null),
				    s);
	}

	private void initTokenSymbol(Token t, String strToken ) {
		++i;
	    token = t;
		symbol = new SymbolOperator(token, strToken, 0,
			1, i, i, env != null ? env.getCurrentCompilationUnit() : null );

	}
	private void next() {
		while ( i < size ) {
			char ch = text[i];
			if      ( ch == '<' ) { initTokenSymbol(Token.LT, "<");  return; }
			else if ( ch == '>' ) { initTokenSymbol(Token.GT, "<");  return; }
			else if ( ch == '|' ) { initTokenSymbol(Token.BITOR, "|");  return; }
			else if ( ch == '.' ) { initTokenSymbol(Token.PERIOD, ".");  return; }
			else if ( ch == ',' ) { initTokenSymbol(Token.COMMA, ",");  return; }
			else if ( Character.isAlphabetic(ch) || ch == '_' ) {
				id = "";
				while ( i < size && isIdentifierChar(text[i]) ) {
					id += text[i];
					++i;
				}
				token = Token.IDENT;
				symbol = new SymbolIdent(token, id, 0,
						1, i, i, env != null ? env.getCurrentCompilationUnit() : null );

				return;
			}
			else if ( Character.isWhitespace(ch) ) {
				++i;
			}
			else {
				error("Unknown character '" + ch + "'");
			}
		}
		token = Token.EOF;
	}

	private void error(String msg) {
		throw new error.CompileErrorException("At column " + i + " of type '" +
				typeName + "': "+ msg);

	}
	private static boolean isIdentifierChar(char ch) {
		return Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_';
	}

	/**
	 * return the Java name of a qualified identifier, something like<br>
	 * <code>    name <br>
	 *       name.id <br>
	 *       </code>
	   @param cyanName
	   @return
	 */
	public String getJavaNameFromQualifId(String cyanName, int numDots) {
		char ch;
		String javaName = "";
		cyanName = cyanName.replace(NameServer.cyanLanguagePackageNameDot, "");
//		int numPeriods = numDots;
		for (int n = 0; n < cyanName.length(); ++n) {
			ch = cyanName.charAt(n);
			if ( Character.isWhitespace(ch) )
				continue;
			if ( Character.isAlphabetic(ch) || Character.isDigit(ch) ) {
				javaName += ch;
			}
			else if ( ch == '_' ) {
				javaName += "__";
			}
			else if ( ch == '.' ){
				javaName += "_p_";
//				if ( --numPeriods == 0 ) {
//					javaName += "._";
//				}
//				else {
//					javaName += ".";
//				}
			}
			else {
				error("unknown character '" + ch + "' in identifier '" + cyanName + "'");
				return null;
			}
		}
		if ( numDots == 0 ) {
			javaName = MetaHelper.removeCyanLangChange(javaName);
		}
		return javaName;
	}



	int i = 0;
	char []text;
	int size = 0;
	String typeName;
	Token token;
	Symbol symbol;
	String id;
	private Env env;

}
