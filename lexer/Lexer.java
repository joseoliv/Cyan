package lexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import ast.AnnotationLiteralObject;
import ast.CompilationUnit;
import ast.CompilationUnitSuper;
import ast.ExprAnyLiteral;
import ast.ExprAnyLiteralIdent;
import ast.ExprLiteralNil;
import ast.Prototype;
import error.UnitError;
import meta.CompilationInstruction;
import meta.CompilationStep;
import meta.CyanMetaobjectLiteralObjectSeq;
import meta.CyanMetaobjectLiteralString;
import meta.CyanMetaobjectMacro;
import meta.CyanMetaobjectNumber;
import meta.IParseWithCyanCompiler_parsing;
import meta.Token;
import meta.Tuple2;
import meta.WrExprAnyLiteral;
import meta.lexer.MetaLexer;
import saci.Compiler;

public class Lexer implements Cloneable {






	public Lexer( char[] in, CompilationUnitSuper compilationUnit,
			      HashSet<meta.CompilationInstruction> compInstSet, Compiler compiler) {

		this.in = in;
		this.compilationUnit = compilationUnit;
		this.compInstSet = compInstSet;
		this.compiler = compiler;
		prototype = null;
		lineNumber = 1;
		k = 0;
		commentLevel = 0;
		startLine = 0;
		beforeLastTokenPos = 0;
		lastTokenPos = 0;
		metaObjectLiteralObjectSeqTable = new HashMap<String, CyanMetaobjectLiteralObjectSeq>();
		metaobjectLiteralNumberTable =  new HashMap<String, CyanMetaobjectNumber>();
		// metaObjectLiteralObjectIdentSeqTable = new HashMap<>();
		metaobjectLiteralStringTable = new HashMap<>();
		metaobjectMacroKeywordMap = new HashMap<>();
		rightSymbolSeqStack = new Stack<>();
		newLineAsToken = false;
		numTabsInCurrentLine = 0;
		next();
	}

	// private HashMap<String, CyanMetaobjectLiteralObjectIdentSeq> metaObjectLiteralObjectIdentSeqTable;

	/**
	 * This table keeps the symbols that start a literal object delimited by a
	 * sequence of symbols such as
	 *        [*  "one":1, "two":2 *]
	 */
	private final HashMap<String, CyanMetaobjectLiteralObjectSeq> metaObjectLiteralObjectSeqTable;

	private final HashMap<String, CyanMetaobjectNumber> metaobjectLiteralNumberTable;

	private final HashMap<String, CyanMetaobjectLiteralString> metaobjectLiteralStringTable;

	/**
	 * each map element is composed of <name, n> in which name is the name of the
	 * macro keyword and n is the number of macros that are being parsed
	 * currently that have 'name' as keyword. For example, a macro may have
	 * keywords 'aaa', 'bbb' e 'ccc' and other may have keywords 'bbb' and 'ddd'.
	 * If we have a statement
	 *      aaa   ( bbb i < 10 ddd ) bbb 100 ccc
	 * This map will have the following values before the left parenthesis:
	 *       aaa 1
	 *       bbb 1
	 *       ccc 1
	 * After 'bbb' and before the right parenthesis the map value will be
	 *       aaa 1
	 *       bbb 2
	 *       ccc 1
	 *       ddd 1
	 */
	public Map<String, Integer> metaobjectMacroKeywordMap;


	/**
	 * the compilation unit that is being compiled
	 */
	private final CompilationUnitSuper compilationUnit;
	/**
	 * the program unit that is being compiled.
	 */
	private Prototype prototype;


	public Token token;
	public String ident;
	public String tokenString;

	public byte byteValue;
	public short shortValue;
	public int intValue;
	public long longValue;
	public float floatValue;
	public double doubleValue;
	public char charValue;
	public boolean booleanValue;
	static private Hashtable<String, Token> keywordsTable;
	static public Hashtable<String, Token> specialCharTable;
	static private Set<String> mayBeAroundNonSpace;
	/*
	 * Each symbol in this set of symbols should either be around spaces or symbols '(' and ')' appear before and/or after it.
	 */
	static private Set<String> shouldBeAroundSpace;

	private char[] in;
	private int commentLevel;
	// index for the input. in[k] is the next character to be analyzed
	private int k;
	public int startToken;
	private int lineNumber;
	private int beforeLastTokenPos, lastTokenPos;

	public Symbol symbol;


	private Token tokenRightCharSeq;
	private SymbolCharSequence symbolRightCharSeq;


	private int startLineComment;
	/**
	 * in[startOffsetLine] is the first character of the line in which the symbol is
	 */
	private int startLine;


	private static final long MaxInteger = 2147483647;
	private static final int MaxByte = 255;
	private static final int MaxShort = 32767;

	/**
	 * Maximum number of characters of a sequence of symbols that ends a
	 * metaobject. Example:
	 *
	 * @javacode[*= .... =*] Here =*] has three characters. It could have at
	 *            most MaxChEndSymbolString.
	 */
	private static final int MaxChEndSymbolString = 30;

	static {
		initSymbols();
	}


	/**
	 * valid chars for the symbol sequence that delimits the attached text of a metaobject.
	 * This text is that shown below. The left sequence in this case is {@literal [}{@literal $}{@literal<}<br>
	 * <code>
	 * {@literal @}concept[$< <br>
	 *     T extends U<br>
	 * >$]<br>
	 * </code>
	 */
	private static final String ValidCharsForAnnotationSequence = "=!?$%&*-+^~/:.\\|([{<>}])";


	/**
	 * valid chars for a sequence that delimits a literal object such as<br>
	 * <code>
	 *     [* "one" -> 1, "two" -> 2 *]
	 * </code>
	 */
	public static final String validSymbolsForLiteralSeqObjects = "\\/|!@$%&*-+=~:?(){}[]<>";
	/**
	 * position in the text starting the left char sequence in a metaobject annotation.
	 * It is the position of '<' in
	 *      @javacode<** ...   **>
	 */
	private int positionStartingLeftCharSeq;



	/**
	 * in a metaobject annotation like
	 *      @feature<<* "author", "José" *>>
	 * leftCharSequence keeps the first char sequence, "<<*".
	 */
	private char[] leftCharSequence;



	public void setPrototype(Prototype prototype) {
		this.prototype = prototype;
	}


	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}



	/**
	 * set the initial position of the lexical analysis
	 *
	   @param offset
	 */
	public void setInitialPositionLexer(int offset) {
		this.k = offset;
	}


	/*public void setMetaObjectLiteralObjectIdentSeqTable(
			HashMap<String, CyanMetaobjectLiteralObjectIdentSeq> metaObjectLiteralObjectIdentSeqTable) {
		this.metaObjectLiteralObjectIdentSeqTable = metaObjectLiteralObjectIdentSeqTable;
	} */


	public void addMetaobjectLiteralNumberTable(
			HashMap<String, CyanMetaobjectNumber> metaobjectLiteralNumberTablePackage) {
		//this.metaobjectLiteralNumberTable = metaobjectLiteralNumberTable;

		metaobjectLiteralNumberTablePackage.forEach( (key, value) -> {
			metaobjectLiteralNumberTable.put(key, value);
		} );

	}

	public void addMetaobjectLiteralStringTable(
			HashMap<String, CyanMetaobjectLiteralString> metaobjectLiteralStringTablePackage) {
		//this.metaobjectLiteralStringTable = metaobjectLiteralStringTable;


		metaobjectLiteralStringTablePackage.forEach( (key, value) -> {
			metaobjectLiteralStringTable.put(key, value);
		} );

	}


	public void setInput( char []in ) {
		this.in = in;
	}


	public int getCurrentChar() {
		// return the position of the current character in the input "in"
		return k;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getCurrentLine() {
		// return the current line

		//int i = startOffsetLine;
		int i = k;
		while ( in[i] != '\n' && in[i] != '\r' && i > 0 )
			--i;
		++i;
		startLine = i;
		final StringBuffer line = new StringBuffer();
		while (in[i] != '\0' && in[i] != '\n' && in[i] != '\r')
			line.append(in[i++]);
		return line.toString();
	}

	public int getColumn() {

		return startToken - startLine + 1 - this.numTabsInCurrentLine + this.numTabsInCurrentLine*Lexer.numWhiteSpacesForTab;
		/*
		 * // return the column of start of the last token int i = startToken;
		 * while ( i >= 0 && in[i] != '\n' ) i--; return startToken - i; int j =
		 * k - i; if ( token != Symbol.IDENT ) j = j - stringValue.length();
		 * else if ( ident != null ) j = j - ident.length(); return j;
		 */
	}

	/**
	 * Initialize the private variables of class lexer: - fill the keywords
	 * table with the keywords; - scan all the file and puts all symbols in
	 * array tokenArray
	 *
	 * all future calls to method next will only retrieve the symbols from array
	 * tokenArray
	 */
	static private void initSymbols() {


		keywordsTable = new Hashtable<String, Token>();
		for (final Token s : Token.values())
			if (Character.isLetter(s.toString().charAt(0)))
				keywordsTable.put(s.toString(), s);
		specialCharTable = new Hashtable<String, Token>();

		specialCharTable.put("=", Token.ASSIGN);

		specialCharTable.put("&", Token.BITAND);
		specialCharTable.put("~", Token.BITNOT);
		specialCharTable.put("|", Token.BITOR);
		specialCharTable.put("|||", Token.BITOR3);
		specialCharTable.put("~|", Token.BITXOR);

		specialCharTable.put("&&", Token.AND);
		specialCharTable.put("!", Token.NOT);
		specialCharTable.put("||", Token.OR);
		specialCharTable.put("~||", Token.XOR);

		specialCharTable.put(":", Token.COLON);
		specialCharTable.put("::", Token.COLONCOLON);
		specialCharTable.put(",", Token.COMMA);
		specialCharTable.put("/", Token.DIV);
		specialCharTable.put("$", Token.DOLLAR);
		specialCharTable.put("[$", Token.LEFTSB_DOLLAR);
		specialCharTable.put("$]", Token.DOLLAR_RIGHTSB);
		specialCharTable.put(".#", Token.DOT_OCTOTHORPE);
		specialCharTable.put(".+", Token.DOT_PERCENT);
		specialCharTable.put(".*", Token.DOT_STAR);
		specialCharTable.put(".+", Token.DOT_PLUS);
    	specialCharTable.put("==", Token.EQ);
    	specialCharTable.put("===", Token.EQEQEQ);
    	specialCharTable.put("!==", Token.NEQEQ);

    	specialCharTable.put("==>", Token.EQEQGT);
    	specialCharTable.put("=>", Token.EQGT);
		specialCharTable.put(">=", Token.GE);
		specialCharTable.put(">", Token.GT);
		specialCharTable.put("?[",  Token.INTER_LEFTSB);
		specialCharTable.put("<=", Token.LE);
		specialCharTable.put("<=>", Token.LEG);
		//specialCharTable.put("{#", Token.LEFTCB_HASH);
		specialCharTable.put("(", Token.LEFTPAR);
		specialCharTable.put("(:", Token.LEFTPARCOLON);
		specialCharTable.put("[", Token.LEFTSB);
		specialCharTable.put("[.", Token.LEFTSB_DOT);
		specialCharTable.put("{", Token.LEFTCB);
		specialCharTable.put("[]", Token.LEFTRIGHTSB);
		specialCharTable.put("<.<", Token.LEFTSHIFT);
		specialCharTable.put("<", Token.LT);
		specialCharTable.put("-", Token.MINUS);
		specialCharTable.put("--", Token.MINUSMINUS);
		specialCharTable.put("*", Token.MULT);
		specialCharTable.put("!=", Token.NEQ);
		specialCharTable.put(".", Token.PERIOD);
		specialCharTable.put("+", Token.PLUS);
		specialCharTable.put("++", Token.PLUSPLUS);
		specialCharTable.put("?", Token.QUESTION_MARK);
		specialCharTable.put("%", Token.REMAINDER);
		specialCharTable.put("->", Token.RETURN_ARROW);
		specialCharTable.put("^", Token.RETURN_FUNCTION);
		specialCharTable.put(")", Token.RIGHTPAR);
		specialCharTable.put(":)", Token.COLONRIGHTPAR);
		specialCharTable.put("}", Token.RIGHTCB);
		specialCharTable.put("]", Token.RIGHTSB);
		specialCharTable.put(".]", Token.RIGHTDOT_SB);
		specialCharTable.put("]?", Token.RIGHTSB_INTER);
		specialCharTable.put(">.>", Token.RIGHTSHIFT);
		specialCharTable.put(">.>>", Token.RIGHTSHIFTTHREE);
		specialCharTable.put(";", Token.SEMICOLON);
		specialCharTable.put("~=", Token.TILDE_EQUAL);
		specialCharTable.put("..", Token.TWOPERIOD);
		specialCharTable.put("..<", Token.TWOPERIODLT);
		specialCharTable.put("`", Token.BACKQUOTE);
		specialCharTable.put("|>", Token.ORGT);

		mayBeAroundNonSpace = new HashSet<String>();
		mayBeAroundNonSpace.add("Boolean");
		mayBeAroundNonSpace.add("Char");
		mayBeAroundNonSpace.add("Byte");
		mayBeAroundNonSpace.add("Int");
		mayBeAroundNonSpace.add("Short");
		mayBeAroundNonSpace.add("Long");
		mayBeAroundNonSpace.add("Float");
		mayBeAroundNonSpace.add("Double");
		mayBeAroundNonSpace.add("String");
		mayBeAroundNonSpace.add("Dyn");
		mayBeAroundNonSpace.add("self");
		mayBeAroundNonSpace.add("Nil");
		mayBeAroundNonSpace.add("true");
		mayBeAroundNonSpace.add("false");
		mayBeAroundNonSpace.add("typeof");

		shouldBeAroundSpace = new HashSet<String>();

		shouldBeAroundSpace.add("||");
		shouldBeAroundSpace.add("~||");
		shouldBeAroundSpace.add("&&");
		shouldBeAroundSpace.add("==");
		shouldBeAroundSpace.add("<=");
		shouldBeAroundSpace.add("<");
		// shouldBeAroundSpace.add(">");
		shouldBeAroundSpace.add(">=");
		shouldBeAroundSpace.add("!=");
		shouldBeAroundSpace.add("~|");
		shouldBeAroundSpace.add("~=");
		shouldBeAroundSpace.add("<.<");
		shouldBeAroundSpace.add(">.>");
		shouldBeAroundSpace.add(">.>>");
		shouldBeAroundSpace.add(".*");
		shouldBeAroundSpace.add(".+");
		shouldBeAroundSpace.add("===");
		shouldBeAroundSpace.add("<=>");

		shouldBeAroundSpace.add("{");
		shouldBeAroundSpace.add("}");
		shouldBeAroundSpace.add("=");

	}

	/**
	 * return true if the character after <code>sym</code> is a white space. This symbol is in <code>compilationUnit</code>
	 */

	static public boolean hasSpaceAfter(Symbol symbol, CompilationUnitSuper compilationUnit) {
		final int i = symbol.offset + symbol.getSymbolString().length();
		final char compUnitText[] = compilationUnit.getText();
		return Character.isWhitespace(compUnitText[i]) ||
				compUnitText[i] == '\0';

	}

	/**
	 * return true if the character after <code>sym</code> is a letter, underscore, or number.
	 * This symbol is in <code>compilationUnit</code>
	 */
	static public boolean hasIdentNumberAfter(Symbol symbol, CompilationUnitSuper compilationUnit) {
		final int i = symbol.offset + symbol.getSymbolString().length();
		final char compUnitText[] = compilationUnit.getText();
		final char ch = compUnitText[i];
		return Character.isLetterOrDigit(ch) || ch == '_';

	}

	/**
	 * return true if the character before the first character of <code>sym</code> is a white space. This symbol is in <code>compilationUnit</code>
	 */


	static public boolean hasSpaceBefore(Symbol sym, CompilationUnitSuper compilationUnit) {
		return Character.isWhitespace(
				compilationUnit.getText()[sym.offset - 1]);

	}

	static public boolean isCharBeforeSymbolEqualTo(Symbol sym, char ch, CompilationUnitSuper compilationUnit) {
		return compilationUnit.getText()[sym.offset - 1] == ch;

	}


	// public boolean hasCachedPreviousSymbol = false;
	// private Symbol cachedPreviousSymbol = null;

	public void next() {

		/*if ( hasCachedPreviousSymbol ) {
			symbol = this.cachedPreviousSymbol;
			hasCachedPreviousSymbol = false;
			return ;
		} */

		StringBuffer s;
		String ext = "";
		// position in which starts a token --- used only in some parts of this method
		int positionStartingToken = 0;

		while (   Character.isWhitespace(in[k])  ) {
			if ( in[k] == '\n' ) {
				if ( this.newLineAsToken ) {
					symbol = new Symbol(Token.NEWLINE, "\n", startLine, lineNumber, getColumn(), k, compilationUnit);
					k++;
					lineNumber++;
					if ( in[k+1] != '\0' )
						startLine = k + 1;
					return;
				}
				lineNumber++;
				if ( in[k+1] != '\0' )
					startLine = k + 1;
				numTabsInCurrentLine = 0;
			}
			else if ( in[k] == '\t' )
				++numTabsInCurrentLine;
			k++;
		}
		if (in[k] == '\0') {
			token = Token.EOF;
			symbol = new Symbol(token, "", startLine, lineNumber, k - startLine + 1, k, compilationUnit);
			return;
		}

		if (in[k] == '/' && in[k + 1] == '/') {
			// comment till the end of the line
			s = new StringBuffer();
			s.append("//");
			final int startOffsetLine = startLine;
			final int lineStartComment = lineNumber;
			final int positionStartingComment = k;
			k += 2;
			while (in[k] != '\n' && in[k] != '\0') {
				s.append(in[k]);
				k++;
			}
			if (in[k] == '\n') {
				k++;
				lineNumber++;
				startLine = k;
				numTabsInCurrentLine = 0;
			}
			if ( compiler.sizeSymbolList >= compiler.symbolListAllocatedSize ) {
				expandSymbolArray();
			}
			compiler.symbolList[compiler.sizeSymbolList] = new SymbolComment(Token.COMMENT,
					s.toString(), startOffsetLine, lineStartComment, getColumn(),
					positionStartingComment, compilationUnit);
			++compiler.sizeSymbolList;

			next();
		} else if (in[k] == '/' && in[k + 1] == '*') {
			// start comment
			s = new StringBuffer();
			s.append("/*");

			commentLevel = 1;
			final int startOffsetLine = startLine;
			final int lineStartComment = lineNumber;
			final int positionStartingComment = k;
			k += 2;
			while ( commentLevel > 0 ) {
				if (in[k] == '*' && in[k + 1] == '/') {
					s.append("*/");
					commentLevel--;
					k += 2;
				} else if (in[k] == '/' && in[k + 1] == '*') {
					s.append("/*");
					commentLevel++;
					k += 2;
				} else if (in[k] == '\0') {
					token = Token.EOF;
					symbol = new Symbol(token, "", startLine, lineNumber,
							k - this.startLine + 1, positionStartingComment, compilationUnit);
					compiler.error2(symbol, "Comment started at line " + lineStartComment
							+ " does not terminate");

					return;
				} else {
					if (in[k] == '\n') {
						lineNumber++;
						startLine = k + 1;
					}
					s.append(in[k]);
					k++;
				}
			}
			if ( compiler.sizeSymbolList >= compiler.symbolListAllocatedSize ) {
				expandSymbolArray();
			}

			compiler.symbolList[compiler.sizeSymbolList] = new SymbolComment(Token.COMMENT,
					s.toString(), startOffsetLine, lineStartComment, getColumn(),
					positionStartingComment, compilationUnit);
			++compiler.sizeSymbolList;

			next();
		} else {
			startToken = k;
			// not a comment
			if ( Character.isLetter(in[k]) || in[k] == '_' ) {
				s = new StringBuffer();
				boolean foundLetterOrDigit = false;
				while (Character.isLetterOrDigit(in[k]) || in[k] == '_') {
					if ( in[k] != '_' )
						foundLetterOrDigit = true;
					s.append(in[k]);
					k++;
				}
				ident = s.toString();
				if ( compInstSet.contains(CompilationInstruction.parsing_originalSourceCode) &&
					 ident.endsWith("__") )
					error("An identifier cannot end with two underscores, \"__\"");

				if ( ident.compareTo("_") == 0 || !foundLetterOrDigit ) {
					error("An identifier cannot be composed only by the character '_'");
					ident = ident + "randomVariableNameXYZTU";
				}
				if (in[k] == ':') {
					ident = ident + ":";
					k++;
					token = Token.IDENTCOLON;
					symbol = new SymbolIdent(token, ident, startLine,
							lineNumber, getColumn(), k-ident.length(), compilationUnit);
					if ( ! Character.isWhitespace(in[k]) && in[k] != ')' && in[k] != ';') {
						compiler.error2(false, symbol, "No whitespace after keyword '" + ident + "'. Use something like 'list add: 5' instead of 'list add:5'");
					}
				} else {
					final Token aSymbol = keywordsTable.get(ident);
					if ( aSymbol == null ) {

						if ( in[k] == '"' ) {
							CyanMetaobjectLiteralString cyanMetaobjectString = this.metaobjectLiteralStringTable.get(ident);
							if ( cyanMetaobjectString == null ) {
								final int lineShift = compiler.getLineShift();
								if ( lineShift > 0 ) {
									this.error("A letter followed by \" mean a literal object string, which is a metaobject annotation. However, there is no metaobject associated to character '"
								           + ident + "' ", this.lineNumber - lineShift);
								}
								else {
									this.error("A letter followed by \" mean a literal object string, which is a metaobject annotation. However, there is no metaobject associated to character '"
								      + ident + "' ", lineNumber);
								}
							}
							else {
								cyanMetaobjectString = cyanMetaobjectString.myClone();

								final int positionStartingLiteralObjectString = k - ident.length();
								++k;

								String literalObjectString;

								boolean foundTriple = false;
								if ( in[k] == '"' && in[k+1] == '"' ) {
									// found """
									foundTriple = true;
									k += 2;
								}
								final int columnStartingString = getColumn();
								String usefulString;
								final int lineNumberStartingString = lineNumber;
								if ( foundTriple ) {
									usefulString = this.getTripleLiteralString().toString();
									literalObjectString = ident + "\"\"\"" + usefulString + "\"\"\"";

								}
								else {
									usefulString = this.getLiteralString();
									literalObjectString = ident + "\"" + usefulString + "\"";
								}

								final AnnotationLiteralObject metaobjectAnnotation =
										new AnnotationLiteralObject( compilationUnit, prototype,
												cyanMetaobjectString, this.compiler.getCurrentMethod());
								metaobjectAnnotation.setUsefulString(usefulString);
								symbol =
								    new SymbolLiteralObject(Token.LITERALOBJECT, metaobjectAnnotation,  null,
										literalObjectString,
										usefulString,
										        stringToCharArray(usefulString),
										        startLine,  lineNumberStartingString, columnStartingString, positionStartingLiteralObjectString, compilationUnit);
								metaobjectAnnotation.setSymbolLiteralObject( symbol);
							}

						}
						else {
							/*
							 *  This was used for metaobjects of the kind
							 *           Graph{* 1:2 *}
							 *   there were not preceded by '@'
							 *   They are not longer valid.
							 *
							CyanMetaobjectLiteralObjectIdentSeq lop = this.metaObjectLiteralObjectIdentSeqTable.get(ident);
					    	if ( lop != null ) {
								int startLineMOCall = startOffsetLine;
								int lineNumberMOCall = lineNumber;
								int columnNumberMOCall = getColumn();
								int positionStartingTokenMOCall = k - ident.length();
								getLeftCharSequence();
					    		String leftSymbolSeq = symbol.getSymbolString();
					    		String rightSymbolSeq = rightSymbolSeqFromLeftSymbolSeq(leftSymbolSeq);
					    		char []text = getTextTill(stringToCharArray(rightSymbolSeq), symbol.getOffset());
					    		String textStr = trimToString(text);
					    		String symbolStr = "";
					    		int max = symbolRightCharSeq.getOffset() +
					    				symbolRightCharSeq.getSizeCharSequence();
					    		for (int jj = positionStartingTokenMOCall; jj < max; ++jj)
					    			symbolStr = symbolStr + in[jj];

					    		k = positionStartingTokenMOCall;
					    		AnnotationLiteralObject annotation = new AnnotationLiteralObject(
					    				compilationUnit, prototype, lop);
					    		symbol = new SymbolLiteralObject( Token.LITERALOBJECT,
					    				annotation,
					    				symbolStr,
					    				textStr,
					    				text,
					    				startLineMOCall,  lineNumberMOCall, columnNumberMOCall,
					    				positionStartingTokenMOCall);
					    		annotation.setSymbolLiteralObject( (SymbolLiteralObject) symbol );
					    	}
					    	else { */
								// identifier

								/*
								 * SymbolMacroKeyword(String symbolString, int startLine, int lineNumber, int columnNumber, int offset,
			CompilationUnitSuper compilationUnit)
								 */
								if ( this.metaobjectMacroKeywordMap.get(ident) != null ) {
									token = Token.MACRO_KEYWORD;
									symbol = new SymbolMacroKeyword(ident, startLine,
											lineNumber, getColumn(), k - ident.length(), compilationUnit);
								}
								else {
									token = Token.IDENT;
									symbol = new SymbolIdent(token, ident, startLine,
											lineNumber, getColumn(), k - ident.length(), compilationUnit);
								}
					    	//}
						}
					}
					else {
						// keyword


						token = aSymbol;
						symbol = new SymbolKeyword(token, ident, startLine,
								       lineNumber, getColumn(),  k - ident.length(), compilationUnit);
						if ( ! mayBeAroundNonSpace.contains(ident) ) {
							checkWhiteSpaceParenthesisBeforeAfter(symbol, ident);
							/*
							int previousIndex = k - ident.length() - 1;
							if ( k - ident.length() - 1 >= 0 && getColumn() > 0 &&
									! Character.isWhitespace(in[previousIndex]) &&
									in[previousIndex] != '(' ) {
								if ( ask(symbol, "Should I insert space before '" + ident + "'" +
										" ? (y, n)") )
											compilationUnit.addAction(
													new ActionInsert(" ", compilationUnit,
														symbol.startLine + symbol.getColumnNumber() - ident.length() - 1,
														symbol.getLineNumber(), symbol.getColumnNumber()));
							}
							if ( ! Character.isWhitespace(in[k]) &&
									in[k] != ')' ) {
								if ( ask(symbol, "Should I insert space after '" + ident + "'" +
										" ? (y, n)") )
											compilationUnit.addAction(
													new ActionInsert(" ", compilationUnit, k,
															symbol.getLineNumber(), symbol.getColumnNumber()));
							}*/
						}
					}
				}
			} else if ( in[k] >= '0' && in[k] <= '9' ) {
				final int positionStartingNumber = k;
				StringBuffer strnum = new StringBuffer();
				try {
					int state = 1;
					ext = "";
					StringBuffer extension;
					while (state != 100)
						switch (state) {
						case 1:
							while (in[k] >= '0' && in[k] <= '9'
									|| in[k] == '_') {

								if (in[k] != '_')
									strnum.append(in[k]);
								k++;
							}
							// check if the number is of the kind "123e3" or "123e3Float"
							if ( (in[k] == 'E' || in[k] == 'e') && Character.isLetter(in[k]) ) {
								strnum.append("e");
								++k;
								state = 9;
							}
							else {
								String usefulString = "";
								for (int iii = positionStartingNumber; iii < k; ++iii) {
									usefulString += in[iii];
								}

								extension = new StringBuffer();
								while ( Character.isLetter(in[k]) || Character.isDigit(in[k]) || in[k] == '_' )
									extension.append(in[k++]);
								ext = extension.toString();

								if ( ext.equals("B") || ext.equals("Byte") )
									state = 3;
								else if (ext.equals("L") || ext.equals("Long") )
									state = 4;
								else if (ext.equals("F") || ext.equals("Float") )
									state = 5;
								else if (ext.equals("D") || ext.equals("Double") )
									state = 6;
								else if ( ext.compareTo("S") == 0 || ext.equals("Short") )
									state = 11;
								else if ( ext.equals("Int") || ext.equals("I") )
									state = 2;
								else if (ext.compareTo("E") == 0
										|| ext.compareTo("e") == 0) {
									strnum.append(ext);
									state = 9;
								}
								else if ( ext.length() > 0 ) {
									final int sizeExt = ext.length();
									int ii = sizeExt - 1;
									while ( ii > 0 && (Character.isDigit(ext.charAt(ii)) || ext.charAt(ii) == '_') )
										--ii;
									int jj = ii;
									while ( jj >= 0 && Character.isLetter(ext.charAt(jj)) )
										--jj;
									final String metaobjectName = ext.substring(jj+1, ii+1);

									CyanMetaobjectNumber cyanMetaobjectNumber = metaobjectLiteralNumberTable.get(metaobjectName);
									if ( cyanMetaobjectNumber == null ) {
										this.error("Unknow number extension: '" + ext + "'");
									}
									else {
										cyanMetaobjectNumber = cyanMetaobjectNumber.myClone();

										// k = positionStartingNumber;
										final String strnumStr = (strnum + ext).toString();
										usefulString += ext;
										final AnnotationLiteralObject metaobjectAnnotation  =
												new AnnotationLiteralObject(compilationUnit,
														prototype, cyanMetaobjectNumber,
														this.compiler.getCurrentMethod());
										symbol = new SymbolLiteralObject(Token.LITERALOBJECT,
												        metaobjectAnnotation, null,
												        strnumStr,
												        usefulString,
												        stringToCharArray(strnumStr),
												        startLine,  lineNumber, getColumn(), positionStartingNumber, compilationUnit);
										metaobjectAnnotation.setSymbolLiteralObject( symbol );
										metaobjectAnnotation.setUsefulString(usefulString);
										state = 100;

									}
								}
								else if (in[k] == '.') {
									if ( in[k+1] == '.' ) {
										  // found '..' for intervals
										state = 2;
									}
									else {
										strnum.append('.');
										state = 7;
									}
								}
								else if ( ext.compareTo("E") == 0
										|| ext.compareTo("e") == 0) {
									strnum.append(ext);
									state = 9;
								}
								else {
									if ( ext.equals("I") || ext.equals("Int") )
										k++;
									state = 2;
								}
							}
							break;
						case 2:
							token = Token.INTLITERAL;
							tokenString = strnum + ext;
							intValue = Integer.valueOf(strnum.toString()).intValue();
							symbol = new SymbolIntLiteral(token, tokenString,
									intValue, startLine, lineNumber,
									getColumn(), positionStartingNumber, compilationUnit);
							final long n = Long.valueOf(strnum.toString()).longValue();
							if (n > MaxInteger)
								throw new NumberFormatException();
							state = 100;
							break;
						case 3:
							token = Token.BYTELITERAL;
							tokenString = strnum + ext;
							byteValue = (byte) (intValue = Integer.valueOf(
									strnum.toString()).intValue());
							symbol = new SymbolByteLiteral(token, tokenString,
									byteValue, startLine, lineNumber,
									getColumn(), positionStartingNumber, compilationUnit);
							if (intValue > MaxByte)
								throw new NumberFormatException();
							state = 100;
							break;
						case 4:
							token = Token.LONGLITERAL;
							tokenString = strnum + ext;
							longValue = Long.valueOf(strnum.toString()).longValue();
							symbol = new SymbolLongLiteral(token, tokenString,
									longValue, startLine, lineNumber,
									getColumn(), positionStartingNumber, compilationUnit);
							state = 100;
							break;
						case 5:
							token = Token.FLOATLITERAL;
							tokenString = strnum + ext;
							floatValue = Float.valueOf(strnum.toString()).floatValue();
							symbol = new SymbolFloatLiteral(token, tokenString, strnum.toString(),
									floatValue, startLine, lineNumber,
									getColumn(), positionStartingNumber, compilationUnit);
							state = 100;
							break;
						case 6:
							token = Token.DOUBLELITERAL;
							tokenString = strnum + ext;
							doubleValue = Double.valueOf(strnum.toString()).doubleValue();
							symbol = new SymbolDoubleLiteral(token,
									tokenString, strnum.toString(), doubleValue, startLine,
									lineNumber, getColumn(), positionStartingNumber, compilationUnit);
							state = 100;
							break;
						case 7:
							k++;
							if (in[k] < '0' || in[k] > '9') {
								if ( !Character.isLetter(in[k]) && in[k] != '_' )
									error("Illegal literal number: . should be followed by a number");
								strnum = new StringBuffer(strnum.toString()
										.substring(0, strnum.length() - 1));
								token = Token.INTLITERAL;
								tokenString = strnum + ext;
								intValue = Integer.valueOf(strnum.toString()).intValue();
								symbol = new SymbolIntLiteral(token,
										tokenString, intValue, startLine,
										lineNumber, getColumn(), positionStartingNumber, compilationUnit);
								k--;
								state = 100;
							} else
								state = 8;
							break;
						case 8:
							while (in[k] >= '0' && in[k] <= '9') {
								strnum.append(in[k]);
								k++;
							}

							if ( (in[k] == 'E' || in[k] == 'e') && Character.isLetter(in[k]) ) {
								strnum.append("e");
								++k;
								state = 9;
							}
							else {


								extension = new StringBuffer();
								while ( Character.isLetter(in[k]) || Character.isDigit(in[k]) || in[k] == '_' )
									extension.append(in[k++]);
								ext = extension.toString();

								if ( ext.equalsIgnoreCase("E") ) {
									strnum.append(in[k]);
									k++;
									state = 9;
								}
								else if ( ext.equals("F") || ext.equals("Float")  )
									state = 5;
								else if ( ext.equals("D") || ext.equals("Double") || ext.length() == 0 )
									state = 6;
								else {
									this.error("Unknow number extension: '" + ext + "'");
								}

							}

							break;
						case 9:
							if (in[k] == '+' || in[k] == '-') {
								strnum.append(in[k]);
								k++;
							}
							else if (in[k] < '0' || in[k] > '9') {
								error("Illegal literal number: E or e should be followed by a number");
								token = Token.FLOATLITERAL;
								tokenString = strnum + ext;
								floatValue = 1.0F;
								symbol = new SymbolFloatLiteral(token,
										tokenString, strnum.toString(), floatValue, startLine,
										lineNumber, getColumn(), positionStartingNumber, compilationUnit);
							}
							else
								state = 10;
							break;
						case 10:
							while (in[k] >= '0' && in[k] <= '9') {
								strnum.append(in[k]);
								k++;
							}

							extension = new StringBuffer();
							while ( Character.isLetter(in[k]) || Character.isDigit(in[k]) || in[k] == '_' )
								extension.append(in[k++]);
							ext = extension.toString();

							if (ext.equals("F") || ext.equals("Float") )
								state = 5;
							else if (ext.equals("D") || ext.equals("Double") )
								state = 6;
							else if ( ext.length() > 0 ){
								this.error("Unknow number extension: '" + ext + "'");
							}
							else
								state = 6;

							/*
							if (in[k] == 'd') {
								k++;
								state = 6;
							} else {
								if (in[k] == 'r')
									k++;
								state = 5;
							}
							*/
							break;
						case 11:
							token = Token.SHORTLITERAL;
							tokenString = strnum + ext;
							shortValue = (short ) (intValue = Short.valueOf(
									strnum.toString()).intValue());
							symbol = new SymbolShortLiteral(token, tokenString,
									shortValue, startLine, lineNumber,
									getColumn(), positionStartingNumber, compilationUnit);
							if (intValue > MaxShort)
								throw new NumberFormatException();
							state = 100;
							break;
						}
				} catch (final NumberFormatException e) {
					error("error in converting number " + strnum + " ext " + ext);
					token = Token.INTLITERAL;
					symbol = new SymbolIntLiteral(token, "0", 0, startLine,
							lineNumber, getColumn(), positionStartingNumber, compilationUnit);
				}
			} else {
				boolean foundToken = false;
				positionStartingToken = k;
				if ( in[k] == '?' ) {
					int lookAt = k + 1;
					if (in[lookAt] == '.') {
						lookAt = lookAt + 1;
						error("Unknown sequence of characters: '?.'");
					}
					if ( Character.isLetter(in[lookAt]) || in[lookAt] == '_') {
						foundToken = true;
						k = lookAt;
						s = new StringBuffer();
						while ( Character.isLetterOrDigit(in[k]) || in[k] == '_' ) {
							s.append(in[k]);
							k++;
						}
						ident = s.toString();
						if (in[k] == ':') {
							ident = ident + ":";
							k++;
							token = Token.INTER_ID_COLON;

							/* // ?.set:  or ?set:
							 * token = interWithDot ? Token.INTER_DOT_ID_COLON
									: Token.INTER_ID_COLON;  */
						}
						else {
							token = Token.INTER_ID;
							/*
							// ?.set  or ?set
							token = interWithDot ? Token.INTER_DOT_ID
									: Token.INTER_ID;  */

						}
						symbol = new SymbolIdent(token, ident, startLine,
								lineNumber, getColumn(), positionStartingToken, compilationUnit);
						if ( ! Character.isWhitespace(in[k]) && in[k] != ')' && in[k] != ';') {
							compiler.error2(false, symbol, "No whitespace after keyword '" + ident + "'. Use something like 'list add: 5' instead of 'list add:5'");
						}
					}
				}
				/*
				else if ( in[k] == '@' && in[k+1] == '"' ) {
					foundToken = true;
					k += 2;
					tokenString = getRawLiteralString();
					token = Token.LITERALSTRING;
					symbol = new SymbolStringLiteral(token, tokenString,
							startOffsetLine, lineNumber, getColumn(), positionStartingToken);

				}
				*/

				else if ( in[k] == '@' ) {
					final int startOffsetAnnotation = k;
					final int lookAt = k+1;
					if ( Character.isLetter(in[lookAt]) || in[lookAt] == '_' ) {
						k = lookAt;
						setAnnotation(startOffsetAnnotation );
						foundToken = true;
					}
				}
				else if ( in[k] == '.' && in[k+1] == '.' ) {
				    foundToken = true;
				    if ( in[k+2] == '<' ) {
				    	symbol = new SymbolOperator(token = Token.TWOPERIODLT, "..<", startLine,
								lineNumber, getColumn(), positionStartingToken, compilationUnit);
				    	k = k + 3;
				    }
				    else {
				    	symbol = new SymbolOperator(token = Token.TWOPERIOD, "..", startLine,
								lineNumber, getColumn(), positionStartingToken, compilationUnit);
				    	k = k + 2;
				    }
			    }
				else if ( in[k] == '`' ) {
				    foundToken = true;
			    	symbol = new SymbolOperator(token = Token.BACKQUOTE, "`", startLine,
							lineNumber, getColumn(), positionStartingToken, compilationUnit);
			    	++k;
				}



				if ( ! foundToken )
					switch (in[k]) {
					case '"':
						k++;
						boolean foundTriple = false;
						if ( in[k] == '"' && in[k+1] == '"' ) {
							// found """
							foundTriple = true;
							k += 2;
						}
						if ( foundTriple ) {
							final int lineNumberStartingString = lineNumber;
							tokenString = getTripleLiteralString().toString();
							token = Token.LITERALSTRING;
							symbol = new SymbolStringLiteral(token, tokenString,
									startLine, lineNumberStartingString, getColumn(), positionStartingToken,
									"\"" + tokenString + "\"", compilationUnit, true
									);

						}
						else {
							tokenString = getLiteralString();
							token = Token.LITERALSTRING;
							symbol = new SymbolStringLiteral(token, tokenString,
									startLine, lineNumber, getColumn(), positionStartingToken,
									"\"" + tokenString + "\"", compilationUnit, false
									);
						}
						if ( Character.isLetter(in[k]) ) {
							error("A literal string cannot be followed by a letter. Use a space between the literal string and the letter");
						}
						break;
					case '\'':
						s = new StringBuffer();
						k++;
						if (in[k] == '\\') {
							/*
							 * escape characters:  b n t r f \ "
							 * plus numbers
							 */
							++k;
							final char escapeChar = in[k];
							if ( escapeChar != 'b' && escapeChar != 'n' && escapeChar != 't' && escapeChar != 'r' &&
								 escapeChar != 'f' && escapeChar != '\\' && escapeChar != '\"' && escapeChar != '\'' &&
								 !Character.isDigit(escapeChar) ) {
								error("I found a literal character. After \\ was expected one of the following escape characters: b n t r f \\ \"");
							}
							s.append("\\");
							if ( Character.isDigit(escapeChar) ) {
								while ( Character.isDigit(in[k]) ) {
									s.append(in[k]);
									++k;
								}
							}
							else {
								s.append(escapeChar);
								++k;
							}
							/*
							while (in[k] != '\'' && in[k] != '\n' && in[k] != '\0') {
								s.append(in[k]);
								k++;
							}
							*/
						}
						else {
							s.append(in[k]);
							k++;
						}
						if (in[k] != '\'') {
							if (in[k] == '\n' || in[k] == '\0')
								error("Non-terminated literal character");
							else
								error("\' expected");
							s.setLength(0);
							s.append("a");
						} else
							k++;
						tokenString = s.toString();
						charValue = tokenString.charAt(0);

						token = Token.CHARLITERAL;
						symbol = new SymbolCharLiteral(token, tokenString,
								charValue, startLine, lineNumber, getColumn(), positionStartingToken, compilationUnit);
						break;
					case ',':
						token = Token.COMMA;
						k++;
						symbol = new SymbolOperator(token, ",", startLine,
								lineNumber, getColumn(), positionStartingToken, compilationUnit);
						/*if ( ! Character.isWhitespace(in[k]) && compiler.getCyanMetaobjectContextStack().size() == 0 ) {
							compiler.error2(false, symbol, "No whitespace after ','");
						} */
						break;
					case ';':
						token = Token.SEMICOLON;
						k++;
						symbol = new SymbolOperator(token, ";", startLine,
								lineNumber, getColumn(), positionStartingToken, compilationUnit);
						break;
					case '(' :
						k++;
						if ( in[k] == ':' ) {
							token = Token.LEFTPARCOLON;
							++k;
							symbol = new SymbolOperator(token, "(:", startLine,
									lineNumber, getColumn(), positionStartingToken, compilationUnit);
						}
						else {
							token = Token.LEFTPAR;
							symbol = new SymbolOperator(token, "(", startLine,
									lineNumber, getColumn(), positionStartingToken, compilationUnit);
						}
						break;
					case ')':
						token = Token.RIGHTPAR;
						k++;
						symbol = new SymbolOperator(token, ")", startLine,
								lineNumber, getColumn(), positionStartingToken, compilationUnit);
						break;
					case '#':
						++k;

				    	String cyanSymbol = "";
				    	while (Character.isLetter(in[k]) || in[k] == '_' || in[k] == '.'
				    			|| Character.isDigit(in[k]) || in[k] == ':') {
				    		cyanSymbol = cyanSymbol + in[k];
				    		k++;
				    	}
			    		if ( cyanSymbol.length() != 0 )  {
					    	// found a Cyan symbol
			    			/*
							tokenString = getLiteralString();
							token = Token.LITERALSTRING;
							symbol = new SymbolStringLiteral(token, tokenString,
									startLine, lineNumber, getColumn(), positionStartingToken,
									"\"" + tokenString + "\"", compilationUnit, false
									);
			    			 *
			    			 */
							token = Token.LITERALSTRING;
			    			tokenString = cyanSymbol;
			    			SymbolStringLiteral symbolLS;
							symbol = symbolLS = new SymbolStringLiteral(token, cyanSymbol,
									startLine, lineNumber, getColumn(), positionStartingToken,
									"\"" + tokenString + "\"", compilationUnit, false
									);
							symbolLS.setOctothorpe(true);
			    		}
			    		else {
			    			//System.out.println( (int ) in[k]);

				    		symbol = new SymbolOperator(Token.LITERALSTRING, "Lexical error", startLine,
				    				lineNumber, getColumn(), positionStartingToken, compilationUnit);
				    		compiler.error2(symbol, "A letter, digit, '_', or ':' was expected after '#'. This symbol starts a Cyan symbol");
			    		}

			    		break;
					case '^':
						token = Token.RETURN_FUNCTION;
						k++;
						symbol = new SymbolOperator(token, "^", startLine,
								lineNumber, getColumn(), positionStartingToken, compilationUnit);
						break;

						/*
						 * \ ! @ $ % & * - + = ~ : ? / ( ) { } [ ] < >
						 */
					case '!':  case '?':  case '@':  case '$':
					case '=':  case '%':  case '&':  case '*':  case '+':
					case '/':  case '<':  case '-':  case '~':
					case '.':  case ':':  case '>':  case '|':  case '\\':
					case '[':  case ']':  case '{':  case '}':

						if ( in[k] == '|' && in[k+1] == '\"' ) {
							if ( in[k+2] == '\0' || in[k+3] == '\0' ) {
								token = Token.EOF;
								symbol = new Symbol(token, "", startLine, lineNumber, getColumn(), k, compilationUnit);
								error("Unexpected end of file in a string that starts "
										+ "with '|'. The correct syntax is |\"\"\" string here \"\"\"");
								return ;
							}
							if ( in[k+2] != '\"' || in[k+3] != '\"' ) {
								error("A literal string that starts with |\"\"\" was expected");
								return ;
							}
							k += 4;
							int column = this.getColumn();
							int lineNumberStartTripleString = lineNumber;
							tokenString = removeStartSpaces(getTripleLiteralString(), lineNumberStartTripleString, column);

							token = Token.LITERALSTRING;
							symbol = new SymbolStringLiteral(token, tokenString,
									startLine, lineNumberStartTripleString, getColumn(), positionStartingToken,
									"\"" + tokenString + "\"", compilationUnit, true
									);

							break;
						}
						s = new StringBuffer();

						while ( in[k] == '!' || in[k] == '?' || in[k] == '@'
    							|| in[k] == '$' || in[k] == '#'

								|| in[k] == '=' || in[k] == '%'	|| in[k] == '&'
								|| in[k] == '*' || in[k] == '+'

								|| in[k] == '/' || in[k] == '<' || in[k] == '-'
								|| in[k] == '~'

								|| in[k] == '.' || in[k] == ':' || in[k] == '>'
								|| in[k] == '|' || in[k] == '\\'

								|| in[k] == '(' || in[k] == ')'
								|| in[k] == '[' || in[k] == ']' || in[k] == '{'
								|| in[k] == '}' )
							s.append(in[k++]);
						String ss = s.toString();

						if ( oneCharSymbols && ss.charAt(0) == ')' && ss.length() > 1 ) {
							ss = "" + s.charAt(0);
							k = k - s.length() - 1;
						}

					    String newSS = ss;
					    int newK = k;
					    token = specialCharTable.get(ss);

					    if ( token != null ) {
					    	//   if '<' is not preceded by space, it is token  GT_NOT_PREC_SPACE
					    	if ( token == Token.LT && k > 1 && in[k-2] != ' ' ) {
					    		token = Token.LT_NOT_PREC_SPACE;
						    	symbol = new SymbolOperator(token, ss, startLine,
						    			lineNumber, getColumn(), positionStartingToken, compilationUnit);
					    	}
					    	else if ( shouldBeAroundSpace.contains(ss) ) {
						    	symbol = new SymbolOperator(token, ss, startLine,
						    			lineNumber, getColumn(), positionStartingToken, compilationUnit);
					    		checkWhiteSpaceParenthesisBeforeAfter(symbol, ss);
					    	}
					    	else
					    		symbol = new SymbolOperator(token, ss, startLine,
						    			lineNumber, getColumn(), positionStartingToken, compilationUnit);

					    }
					    else {
					    	if ( Lexer.checkLeftCharSeq(ss) ) {
					    		//#

					    		/**
					    		 * the symbol may be a sequence of characters that start a metaobject annotation that
					    		 * is a literal object such as <br>
					    		 * <code>
					    		 *     {* 1:2, 2:3, 3:1 *} <br>
					    		 *     [* "one":1, "two":2 *] <br>
					    		 * </code><br>
					    		 * This literal object may be parsed by with the help of the Cyan compiler or
					    		 * parsed by a user-defined compiler. In the first case the metaobject inherits
					    		 * from {@link meta#IParseWithCyanCompiler_parsing}.
					    		 *
					    		 * <br>
					    		 * When metaobjects are loaded (imported) the compiler already checks whether
					    		 * the sequence that delimits the literal object is valid. So
					    		 * the search with 'get' below will only succeeds if ss is a valid left sequence.
					    		 */
						    	CyanMetaobjectLiteralObjectSeq lop = this.metaObjectLiteralObjectSeqTable.get(ss);
						    	if ( lop != null ) {

						    		lop = lop.myClone();
						    		final String rightSymbolSeq = MetaLexer.rightSymbolSeqFromLeftSymbolSeq(ss);
						    		final AnnotationLiteralObject metaobjectAnnotation = new AnnotationLiteralObject(
						    				compilationUnit, prototype, lop, this.compiler.getCurrentMethod());

						    		if ( lop instanceof IParseWithCyanCompiler_parsing ) {
						    			// the compiler will parse the source inside the literal object. Then
						    			// we should push the right symbol sequence in stack {@link #rightSymbolSeqStack}
						    			rightSymbolSeqStack.push(rightSymbolSeq);
						    			token = Token.LITERALOBJECT;
						    			symbol = new SymbolLiteralObjectParsedWithCompiler(Token.LITERALOBJECT,
						    				metaobjectAnnotation, null, ss, startLine,  lineNumber, getColumn(), positionStartingToken, compilationUnit);
						    		}
						    		else {
						    			/*
						    			 * the metaobject will parse the source inside the literal object without
						    			 * using the Cyan compiler.
						    			 */
							    		final char []text = getTextTill(stringToCharArray(rightSymbolSeq), positionStartingToken);
							    		final String textStr = text.toString();

						    			token = Token.LITERALOBJECT;
							    		symbol = new SymbolLiteralObject( Token.LITERALOBJECT,
							    				metaobjectAnnotation, null,
							    				ss + " " + textStr + " " + rightSymbolSeq,
							    				textStr,
							    				text,
							    				startLine,  lineNumber, getColumn(), positionStartingToken, compilationUnit);
										metaobjectAnnotation.setUsefulString(textStr);


						    		}
						    		metaobjectAnnotation.setSymbolLiteralObject( symbol );
						    	}
						    	else {
						    		// found an unexpected left char sequence
						    		k = k - ss.length();
						    		getLeftCharSequence();
						    	}
					    	}
					    	else if ( Lexer.checkRightCharSeq(ss) ) {
					    		if ( ! rightSymbolSeqStack.empty() &&
						    			rightSymbolSeqStack.peek().equals(ss) ) {
					    			token = Token.RIGHTCHAR_SEQUENCE;
					    			/*
				    				symbol = new SymbolRightSequence( Token.RIGHTCHAR_SEQUENCE, ss,
						    				startLine,  lineNumber, getColumn(), positionStartingToken, compilationUnit);

	public SymbolCharSequence(Token token, String symbolString, int startLine,
			int lineNumber, int columnNumber, int offset, char[] charSequence,
			int sizeCharSequence, CompilationUnitSuper compilationUnit) {

						    		*/
					    			symbol = new SymbolCharSequence(token, ss,
						    				startLine,  lineNumber, getColumn(), positionStartingToken,
						    				ss.toCharArray(), ss.length(), compilationUnit);
				    				rightSymbolSeqStack.pop();

					    		}
					    		else {
						    		token = Token.LITERALSTRING;
						    		symbol = new SymbolOperator(Token.LITERALSTRING, "Lexical error", startLine,
						    				lineNumber, getColumn(), positionStartingToken, compilationUnit);
						    		if ( ss.equals("*/") ) {
										compiler.error2(symbol, "'*/' without a previous '/*'. That is, a comment was closed without have been opened" );
						    		}
						    		else {
										compiler.error2(symbol, "The sequence of symbols: '" + ss + "' seems to be a right sequence of symbols delimiting" +
								    		      " a literal object. However, there is no left sequence of symbols for this right sequence of symbols." );
						    		}

					    		}
					    	}
					    	else {
					    		/*
					    		 * token ==  null, try to slice it in order to find a substring that is a token
					    		 */
							    while ( token == null && newSS.length() > 0 ) {
							    	newSS = newSS.substring(0, newSS.length()-1);
							    	--newK;
								    token = specialCharTable.get(newSS);
							    }
							    if ( token != null ) {
							    	ss = newSS;
							    	k = newK;
							    	//   if '<' is not preceded by space, it is token  GT_NOT_PREC_SPACE
							    	if ( token == Token.LT && k > 1 && in[k-2] != ' ' ) {
							    		token = Token.LT_NOT_PREC_SPACE;
								    	symbol = new SymbolOperator(token, ss, startLine,
								    			lineNumber, getColumn(), positionStartingToken, compilationUnit);
							    	}
							    	else if ( shouldBeAroundSpace.contains(ss) ) {
								    	symbol = new SymbolOperator(token, ss, startLine,
								    			lineNumber, getColumn(), positionStartingToken, compilationUnit);
							    		checkWhiteSpaceParenthesisBeforeAfter(symbol, ss);
							    	}
							    	else {
								    	symbol = new SymbolOperator(token, ss, startLine,
								    			lineNumber, getColumn(), positionStartingToken, compilationUnit);
							    	}
							    }
							    else {
						    		token = Token.LITERALSTRING;
						    		symbol = new SymbolOperator(Token.LITERALSTRING, "Lexical error", startLine,
						    				lineNumber, getColumn(), positionStartingToken, compilationUnit);
						    		compiler.error2(symbol, "Unidentified string of symbols: '" + ss + "' OR this is a right sequence of symbols delimiting a literal object that is in the wrong place (unbalanced left and right sequence of symbols)");
							    }

					    	}
					    }

					    /*
					     * token == null if there was an error in the above code
					     */

					    break;
					default:
						k++;
						symbol = new SymbolOperator(Token.LITERALSTRING, "Lexical error", startLine,
								lineNumber, getColumn(), positionStartingToken, compilationUnit);
						compiler.error2(symbol, "Unidentified character '" + in[k - 1] + "'"
								+ " ASCII " + (int) in[k - 1]);
					}
			}
		}
		beforeLastTokenPos = lastTokenPos;
		lastTokenPos = k;

//		if ( this.numberNestedStatements > 0 ) {
//			if ( symbol.token == Token.RIGHTCB ) {
//				firstStatementSymbolStack.pop();
//				if ( Compiler.sizeSymbolList > 0) {
//					if ( Compiler.symbolList[Compiler.sizeSymbolList - 1].token != Token.SEMICOLON ) {
//						firstStatementSymbolStack.pop();
//					}
//				}
//
//			}
//			else if ( symbol.token == Token.SEMICOLON ) {
//					firstStatementSymbolStack.pop();
//			}
//			else {
//
//
//	        	if ( this.firstStatementSymbolStack.size() == 0 ) {
//	        		System.out.println(this.compilationUnit.getEntityName());
//	        	}
//	        	firstStatementSymbol = firstStatementSymbolStack.peek();
//	            columnNumberFirstSymbol = firstStatementSymbol.columnNumber;
//	            if ( symbol.lineNumber != firstStatementSymbol.lineNumber ) {
//	                if ( symbol.columnNumber < columnNumberFirstSymbol ) {
//	                    /* something like
//	                            Out println: 0
//	                          'a' println;
//	                    */
//	                    error("if symbol '" +
//	                    		symbol.symbolString
//	                    		+ "' is part of the previous non-blank line, it should be at least in column " +
//	                        columnNumberFirstSymbol + Lexer.MIN_INDENTATION +
//	                        "\nIf this symbol is not part of previous non-blank line, "
//	                        + "then a ';' is missing");
//
//	                }
//	                else if ( symbol.columnNumber == columnNumberFirstSymbol ) {
//	                    error("because of the language rules, symbol '" +
//	                          symbol.symbolString + "' starts a new statement" +
//	                    " because it is in the same column as symbol '" +
//	                    		firstStatementSymbol.symbolString +
//	                    		"'. Therefore there should be a ';' after the previous symbol");
//	                }
//	                else {
//	                    // symbol.columnNumber > columnNumberFirstSymbol
//	                    if ( symbol.columnNumber  < columnNumberFirstSymbol +
//	                         Lexer.MIN_INDENTATION ) {
//	                    	error("Symbols of the same statement but in another line should be indented by at least "
//	                    			+ Lexer.MIN_INDENTATION + " espaces");
//	                    }
//	                }
//	            }
//
//			}
//
//		}
//	    if ( this.numberNestedStatements > 0 && symbol.token != Token.RIGHTCB ) {}

		if ( compiler.sizeSymbolList >= compiler.symbolListAllocatedSize ) {
			expandSymbolArray();
		}

		compiler.symbolList[compiler.sizeSymbolList] = symbol;
		++compiler.sizeSymbolList;

	}


	/**

	 */
	private void expandSymbolArray() {
		compiler.symbolListAllocatedSize *= 1.5 + 100;
		Symbol []newArray = new Symbol[compiler.symbolListAllocatedSize + 2];
		System.arraycopy(compiler.symbolList, 0, newArray, 0, compiler.sizeSymbolList);
		compiler.symbolList = newArray;
	}

	/**
	 * Possible strings that start with |""" are:
	 *
	 *          |"""   |first
	 *                 |second
	 *           """
	 *
	 *          |"""   |first |second |third """   // each is put in one line   ERROR !!!
	 *
	 *          |"""
	 *              |first
	 *              |second
	 *          """
	 *
	 *          |"""
	 *      |first
	 *              |second
	 *          """
	 *
	 *          |"""
	 *      |first
	 *
	 *              |second
	 *          """
	 *
	 *   algorithm: remove all spaces before the first '|'. From the second '|' and beyond,
	 *      remove all spaces between the last \n and the '|'
	   @param tripleLiteralString
	   @param columnStartsOne
	   @return
	 */
	private String removeStartSpaces(StringBuffer tripleLiteralString, int lineNumberStartTripleString, int columnStartsOne) {
		   // -1 because columns start in 1 for the compiler and +4 because of the four
           // characters of |"""
		int column = columnStartsOne + 3;
		int i = 0;
		int size = tripleLiteralString.length();
//		for ( int k1 = 0; k1 < size; ++k1 ) {
//			System.out.print( (int) tripleLiteralString.charAt(k1) + ", ");
//			if ( tripleLiteralString.charAt(k1) == '\n' ) System.out.println("");
//		}
//		System.out.println("\n\n\n");
		StringBuffer news = new StringBuffer();
		boolean firstFound = false;
		while ( i < size ) {
			char ch = tripleLiteralString.charAt(i);
			if ( ch == '|' && (i == 0 || tripleLiteralString.charAt(i-1) != '\\') ) {
				++i;
				firstFound = true;
				break;
			}
			if ( ch == '\n' ) {
				column = 0;
			}
			else {
				++column;
			}
			if ( ! Character.isWhitespace(ch) ) {
				error("Inside the literal string starting at line " + lineNumberStartTripleString +
						" with |\"\"\", only white spaces can appear before '|'." +
						" We have found '" + ch + "', ASCII " + (int) ch);
				return "";
			}

			++i;
		}
		if ( !firstFound ) {
			error("Inside the literal string starting at line " + lineNumberStartTripleString + " with |\"\"\" there should be at least one '|'");
			return "";
		}
		String cr_nl = "";
		while ( i < size ) {
			char ch = tripleLiteralString.charAt(i);
			while ( ch != '\n' && ch != '\r' && i < size ) {
				if ( ch == '\\' && i < size - 1 && tripleLiteralString.charAt(i+1) == '|' ) {
					// found \|, copy only |
					++i;
					ch = '|';
				}
				else if ( ch == '|' && tripleLiteralString.charAt(i-1) != '\\' ) {
					error("Inside the literal string starting at line " + lineNumberStartTripleString + " with |\"\"\", "
							+ "'|' cannot appear in the same line as other '|'. "
							+ "If you want the character '|', use \\|");
				}
				news.append(ch);
				++i;
				if ( i < size ) {
					ch = tripleLiteralString.charAt(i);
				}
			}
			// skip everything till the next |
			int start = i;
			while ( ch != '|' && i < size ) {

				if ( ch == '\r' || ch == '\n' ) {
					if ( ch == '\r' ) {
						++i;
						if ( i < size ) {
							ch = tripleLiteralString.charAt(i);
						}
						else {
							error("Inside the literal string starting at line " + lineNumberStartTripleString +
									" with |\"\"\", '\\n' expected after '\\r' ");
						}
						cr_nl = ch == '\n' ? "\r\n" : "\r";
					}
					else {
						cr_nl = "\n";
					}
					start = i + 1;
				}

				if ( ! Character.isWhitespace(ch) ) {
					error("Inside the literal string starting at line " + lineNumberStartTripleString +
							" with |\"\"\", only white spaces can appear before '|'");
					return "";
				}
				++i;
				if ( i < size ) {
					ch = tripleLiteralString.charAt(i);
				}
			}
			if ( ch == '|' ) {
				news.append(cr_nl);
				if ( i - start != column ) {
					error("Inside the literal string starting at line " + lineNumberStartTripleString + " with |\"\"\", all '|' "
							+ "should be in the same column. However, "
							+ "one '|' is in column " + (column + 1) + " and another in column "
							+ (i - start + 1) + ". Maybe this error was caused by tabs");
				}
				++i;
			}
		}
//		for ( int k1 = 0; k1 < news.length(); ++k1 ) {
//			System.out.print( (int) news.charAt(k1) + ", ");
//			if ( news.charAt(k1) == '\n' ) System.out.println("");
//		}
//		System.out.println("\n\n\n");

		return news.toString();
	}


	/**
	 * convert charArray to String removing trailing '\0'
	   @param
	   @return
	 */
	public  static String trimToString(char[] charArray) {
		String s = "";
		int i = 0;
		final int len = charArray.length;
		while ( i < len ) {
			if ( charArray[i] == '\0' )
				break;
			s = s + charArray[i];
			++i;
		}
		return s;
	}

	/**
	 * returns a literal string.
	 * Assumes that in[k] is in the first character after " in a string or Cyan
	 * Symbol. That is, in "olá" in[k] must be 'o' and in
	 * #"this is a cyan symbol" in[k] must be 't'
	 *
	 * @return
	 */
	private String getLiteralString() {
		String ret = null;
		final StringBuffer s = new StringBuffer();
		final int oldk = k;
		while (in[k] != '\0' && in[k] != '\n')
			if (in[k] == '"')
				break;
			else if (in[k] == '\\') {
				if (in[k + 1] == '\\') {
					s.append("\\\\");
					k += 2;
				} else if (in[k + 1] != '\n' && in[k + 1] != '\0') {
					s.append(in[k]);
					k++;
					s.append(in[k]);
					k++;
				}
			} else {
				s.append(in[k]);
				k++;
			}

		if (in[k] == '\0' || in[k] == '\n') {
			k = oldk;
			error("Nonterminated string");
			ret = "";
		} else {
			k++;
			ret = s.toString();
		}
		return ret;
	}


	/**
	 * returns a literal string that started with """.
	 * Assumes that in[k] is in the first character after """".
	 *
	 * @return
	 */
	private StringBuffer getTripleLiteralString() {
		String ret = null;
		final StringBuffer s = new StringBuffer();
		while (in[k] != '\0' )
			if (in[k] == '"' && in[k+1] == '"' && in[k+2] == '"' )
				break;
			else if (in[k] == '\\') {
				if (in[k + 1] == '\\') {
					s.append("\\\\");
					k += 2;
				} else if ( in[k + 1] != '\0') {
					s.append(in[k]);
					k++;
					s.append(in[k]);
					k++;
				}
			} else {

				if ( in[k] == '\n' ) {
					lineNumber++;
					//## if ( in[k+1] != '\0' )
					//##	startLine = k + 1;
					numTabsInCurrentLine = 0;
				}
				else if ( in[k] == '\t' )
					++numTabsInCurrentLine;

				s.append(in[k]);
				k++;
			}

		if ( in[k] == '\0' ) {
			error("Nonterminated string");
			return new StringBuffer("");
		} else {
			k += 3;
			return s;
		}
	}


	public void checkWhiteSpaceParenthesisBeforeAfter(Symbol sym, String strSymbol) {
		if ( compiler.getCompilationStep() != CompilationStep.step_1 ) {
			return ;
		}
		final int previousIndex = k - strSymbol.length() - 1;
		if ( previousIndex >= 0 && getColumn() > 0 &&
				! Character.isWhitespace(in[previousIndex]) &&
				in[previousIndex] != '(' ) {
			if ( sym.token != Token.LEFTCB || in[previousIndex] != '^' ) {
				compiler.error2(false, sym, "'" + strSymbol + "' should be preceded and followed by whitespace or the beginning of a line or the end of a line. "
					+ "This may be a symbol used by Cyan or a reserved word of the language");
				return ;
			}


			/* if ( ask(symbol, "Should I insert space before '" + strSymbol + "'" +
					" ? (y, n)") )
				compilationUnit.addAction(
						new ActionInsert(" ", compilationUnit,
								symbol.startLine + symbol.getColumnNumber() - strSymbol.length() - 1,
								symbol.getLineNumber(), symbol.getColumnNumber())); */
		}
		if ( ! Character.isWhitespace(in[k]) &&
				in[k] != ')' && in[k] != ';' && in[k] != ',' && in[k] != '\0' ) {
			compiler.error2(false, sym, "'" + strSymbol + "' should be preceded and followed by whitespace or the beginning of a line or the end of a line. "
					+ "This may be a symbol used by Cyan or a reserved word of the language");
			/* if ( ask(symbol, "Should I insert space after '" + strSymbol + "'" +
					" ? (y, n)") )
				compilationUnit.addAction(
						new ActionInsert(" ", compilationUnit, k,
								symbol.getLineNumber(), symbol.getColumnNumber())); */
		}
	}


	public void checkWhiteSpaceParenthesisAfter(Symbol sym, String strSymbol) {

		if ( compiler.getCompilationStep() == CompilationStep.step_1 && checkSpaceAfterComma ) {
			if ( ! Character.isWhitespace(in[k]) &&
					in[k] != ')' && in[k] != ';' && in[k] != ',' && in[k] != '\0' ) {
				compiler.error2(false, sym, "'" + strSymbol + "' should be followed by whitespace or the beginning of a line or the end of a line. "
						+ "This may be a symbol used by Cyan or a reserved word of the language");
				/* if ( ask(symbol, "Should I insert space after '" + strSymbol + "'" +
						" ? (y, n)") )
					compilationUnit.addAction(
							new ActionInsert(" ", compilationUnit, k,
									symbol.getLineNumber(), symbol.getColumnNumber())); */
			}
		}
	}

	public boolean isThereWhiteSpaceBefore(Symbol sym) {

		return sym.offset > 0 && Character.isWhitespace(in[sym.offset-1]);
	}




	public String retMessageCheckWhiteSpaceParenthesisAfter(String strSymbol) {
		if ( ! Character.isWhitespace(in[k]) &&
				in[k] != ')' && in[k] != ';' && in[k] != ',' && in[k] != '\0' ) {
			return "'" + strSymbol + "' should be followed by whitespace or the beginning of a line or the end of a line. "
					+ "This may be a symbol used by Cyan or a reserved word of the language";
			/* if ( ask(symbol, "Should I insert space after '" + strSymbol + "'" +
					" ? (y, n)") )
				compilationUnit.addAction(
						new ActionInsert(" ", compilationUnit, k,
								symbol.getLineNumber(), symbol.getColumnNumber())); */
		}
		else
			return null;
	}




	/**
	 * Assumes that in[k] is in the first character after " in a string.
	 * That is, in "olá" in[k] must be 'o'. This method does not consider
	 * escape characters --- it should be called when the string starts
	 * with @" as in
	 *       @"c:\t\f"
	 *  This string has six characters, including two '\\'.
	 *
	 * @return

	private String getRawLiteralString() {
		String ret = null;
		StringBuffer s = new StringBuffer();
		while (in[k] != '\0' && in[k] != '\n')
			if (in[k] == '"')
				break;
            else {
				s.append(in[k]);
				k++;
			}

		if (in[k] == '\0' || in[k] == '\n') {
			error("Nonterminated string");
			ret = "";
		} else {
			k++;
			ret = StringEscapeUtils.escapeJava( s.toString() );
		}
		return ret;
	}
	*/

	public int getLineNumberBeforeLastToken() {
		return getLineNumber(beforeLastTokenPos);
	}

	private int getLineNumber(int index) {
		// return the line number in which the character input[index] is
		int i, n, size;
		n = 1;
		i = 0;
		size = in.length;
		while (i < size && i < index) {
			if (in[i] == '\n')
				n++;
			i++;
		}
		return n;
	}

	public String getLineBeforeLastToken() {
		return getLine(beforeLastTokenPos);
	}

	private String getLine(int index) {
		// get the line that contains input[index]. Assume input[index] is at a
		// token, not
		// a white space or newline

		int i = index;
		if (i == 0)
			i = 1;
		else if (i >= in.length)
			i = in.length;

		final StringBuffer line = new StringBuffer();
		// go to the beginning of the line
		while (i >= 1 && in[i] != '\n')
			i--;
		if (in[i] == '\n')
			i++;
		// go to the end of the line putting it in variable line
		while (in[i] != '\0' && in[i] != '\n' && in[i] != '\r') {
			line.append(in[i]);
			i++;
		}
		return line.toString();
	}

	/**
	 * returns true if the parameter can be part of a sequence that delimits
	 * a literal object or a metaobject.
	 * @param ch
	 * @return
	 */
	private static boolean isSequenceChar(char ch) {



		return  ValidCharsForAnnotationSequence.indexOf(ch) >= 0;

		/*
		return ch == '=' ||  ch == '!' ||  ch == '?' ||
		       ch == '$' ||  ch == '%' ||  ch == '&' ||
		       ch == '*' ||  ch == '+' ||  ch == '/' ||  ch == '<' ||
		       ch == '-' ||  ch == '^' ||  ch == '~' ||  ch == '.' ||
		       ch == ':' ||  ch == '>' ||  ch == '|' ||  ch == '\\' ||
		       ch == '(' ||  ch == ')' ||  ch == '[' ||  ch == ']' ||
		       ch == '{' ||  ch == '}';  */

	}

	public void setStartLineComment(int startLineComment) {
		this.startLineComment = startLineComment;
	}

	public int getStartLineComment() {
		return startLineComment;
	}

	public void error(String message) {

		if ( compiler == null ) {
			compilationUnit.addError(new UnitError(null, compilationUnit
					.getEntityName(), compilationUnit.getFilename(),
					getCurrentLine(), message, lineNumber, //getLineNumber(lastTokenPos),
					getColumn(), compilationUnit));
		}
		else
			compiler.error2(lineNumber, message, true);


	}

	public void error(String message, int errorLineNumber) {

		if ( compiler == null ) {
			compilationUnit.addError(new UnitError(null, compilationUnit
					.getEntityName(), compilationUnit.getFilename(),
					getCurrentLine(), message, errorLineNumber,
					getColumn(), compilationUnit));
		}
		else
			compiler.error2(errorLineNumber, message, true);

	}

	/*
	private boolean ask(Symbol sym, String message) {
		boolean ret = false;
		try {
			  Scanner sc = new Scanner(System.in);
			  //System.out.println("File " + compilationUnit.getFilename() +
					   " (" + sym.getLineNumber() + ", " + sym.getColumnNumber() + ")");
			  //System.out.println(message);
			  String yesNo;
			  while ( true ) {
				  yesNo = sc.nextLine();
			  if ( yesNo.compareTo("y") == 0 || yesNo.compareTo("n") == 0 )
				  break;
			  else
				  //System.out.println("type y or n");
		      }
			  ret = yesNo.compareTo("y") == 0;
			  sc.close();
			  return ret;
		} catch (Exception e) {
			return false;
		}

	}
	*/

	/**
	 * append at the last position of string 's' all characters between
	 * symbols first and last, including these two symbols. The text
	 * in which these symbols are is 'text
	 *
	   @param s
	   @param first
	   @param last
	 */
	static public void append(StringBuffer s, Symbol first, Symbol last, char []text) {
		final int lastOffset = last.offset + last.getSymbolString().length();
		for ( int i = first.offset; i < lastOffset; ++i )
			s.append(text[i]);

	}



	/**
	 * converts a string into a '\0' terminated char array
	 */
	private static char []stringToCharArray(String s) {
		final int size = s.length();
		final char []charArray = new char[size + 1];
		for (int i = 0; i < size; i++)
			charArray[i] = s.charAt(i);
		charArray[size] = '\0';
		return charArray;
	}

	/**
	 * treats a metaobject annotation such as
	 *
	 * @javacode{ System.out.println(j); }
	 * @color(blue)
	 *
	 * Only '@javacode' and '@color' is scanned.
	 *
	 */
	private void setAnnotation( int startOffsetAnnotation) {

		final StringBuffer s = new StringBuffer();
		String postfixName = "";
		boolean inPostfix = false;
		while ( Character.isLetter(in[k]) || in[k] == '_' || in[k] == '#' || Character.isDigit(in[k])) {
			if ( in[k] == '#' ) {
				if ( inPostfix )
					this.error("two characters '#' in a metaobject annotation");
				inPostfix = true;
			}
			else {
				if ( inPostfix )
					postfixName += in[k];
				else
					s.append(in[k]);
			}
			k++;
		}
		CompilerPhase postfix = null;
		final String ctmoName = s.toString();
		if ( postfixName.length() > 0 ) {
			postfix = CompilerPhase.search(postfixName);
			if ( postfix == null )
				error("Wrong postfix to a metaobject annotation. It may be one of " + CompilerPhase.phaseNames);
		}

		symbol = new SymbolCyanAnnotation(token = Token.METAOBJECT_ANNOTATION,
				ctmoName, postfix, startLine, lineNumber,
				getColumn(), startOffsetAnnotation, in[k] == '(', compilationUnit);
	}


	public char nextChar() {
		return in[k];
	}

	public boolean isNextCharSpace() {
		return Character.isSpaceChar(in[k]);
	}

	public static boolean isLeftParAfter(Symbol sym, CompilationUnit compUnit) {
		final int i = sym.offset + sym.symbolString.length();
		final char []t = compUnit.getText();
		return t[i] == '(' && ! isSequenceChar(t[i+1]);
	}

	/**
	 * set token and symbol for the left char sequence that starts a metaobject annotation. In
	 *      @text <<*
	 *          ...
	 *      *>>
	 *
	 *  this left char sequence is "<<*". Return the size of the sequence found or
	 *  -1 if no sequence was found.
	   @return
	 */
	public void getLeftCharSequence() {
		while (   Character.isWhitespace(in[k])  ) {
			if ( in[k] == '\n' ) {
				lineNumber++;
				if ( in[k+1] != '\0' )
					startLine = k + 1;
			}
			k++;
		}
		if (in[k] == '\0') {
			token = Token.EOF;
			symbol = new Symbol(token, "", startLine, lineNumber, getColumn(), k, compilationUnit);
			error("Unexpected end of file while looking for a left char sequence of a metaobject annotation");
		}
		else if ( ! isSequenceChar(in[k]) ) {
			token = Token.EOF;
			symbol = new Symbol(token, "", startLine, lineNumber, getColumn(), k, compilationUnit);
			error("A left char sequence expected in a metaobject annotation. "
					+ "That is, the metaobject associated to this annotation expects a text or DSL after its parameters");
		}
		else {
			leftCharSequence = new char[MaxChEndSymbolString + 1];
			  // size of leftCharSequence and rightCharSequence
			int size = 0;
			positionStartingLeftCharSeq = k;
			String leftCharSequenceString = "";
			while ( isSequenceChar(in[k]) ) {
				if (size >= MaxChEndSymbolString) {
					error("Sequence of symbols in a metaobject or literal object that has more than "
							+ MaxChEndSymbolString + " symbols");
					token = Token.EOF;
					symbol = new Symbol(token, "", startLine, lineNumber,
							getColumn(), positionStartingLeftCharSeq, compilationUnit);
					return;
				}
				final char ch = in[k];
				if ( ch == ')' || ch == ']' || ch == '}' || ch == '>' ) {
					error("')', ']', '}', or > cannot be used in starting a metaobject annotation");
					token = Token.EOF;
					symbol = new Symbol(token, "", startLine, lineNumber,
							getColumn(), positionStartingLeftCharSeq, compilationUnit);
					return;
				}
				leftCharSequenceString = leftCharSequenceString + in[k];
				leftCharSequence[size++] = in[k++];
			}
			leftCharSequence[size] = '\0';
			token = Token.LEFTCHAR_SEQUENCE;
			symbol = new SymbolCharSequence(token, leftCharSequenceString, startLine,
					         lineNumber, getColumn(), positionStartingLeftCharSeq, leftCharSequence,
					         size, compilationUnit);
		}
	}

	/**
	 * gets a left symbol sequence of a literal object or metaobject as input and returns
	 * the right symbol sequence:
	 *            (#  returns  #)
	 *            <(*   returns *)>
	 *
	 * @param leftCharSeq should be a sequence of char´s terminated with '\0'
	 * @param size
	 * @return
	 */

	public char []getRightSymbolSeq(char []leftCharSequence2, int size) {
		return getRightSymbolSeq(leftCharSequence2, size, positionStartingLeftCharSeq);
	}
	/**
	 * gets a left symbol sequence of a literal object or metaobject as input and returns
	 * the right symbol sequence:
	 *            (#  returns  #)
	 *            <(*   returns *)>
	 *
	 * @param leftCharSeq should be a sequence of char´s terminated with '\0'
	 * @param size
	 * @return
	 */
	private char []getRightSymbolSeq(char []leftCharSeq, int size, int position) {

		/*
		 * realSize makes this method works whether leftCharSequence terminates with '\0' or not.
		 */
		int realSize = size;
		if ( leftCharSeq[size-1] == '\0' ) --realSize;
		final char[] rightCharArray2 = new char[realSize + 1];
		int j;
		char ch;
		for (j = 0; j < realSize; j++) {
			ch = leftCharSeq[realSize - j - 1];
			if (ch == ')' || ch == ']' || ch == '}' || ch == '>') {
				error("')', ']', '}', or '>' cannot be used in starting a metaobject annotation");
				token = Token.EOF;
				symbol = new Symbol(token, "", startLine, lineNumber,
						getColumn(), position, compilationUnit);
				return null;
			} else if (ch == '(')
				ch = ')';
			else if (ch == '[')
				ch = ']';
			else if (ch == '{')
				ch = '}';
			else if (ch == '<')
				ch = '>';
			rightCharArray2[j] = ch;
		}
		rightCharArray2[realSize] = '\0';
		return rightCharArray2;
	}

	/**
	 * return the text between offsetLeftCharSeq and offsetRightCharSeq - 1. The last character
	 * is followed by '\0'
	 */
	public char[] getText(int offsetLeftCharSeq, int offsetRightCharSeq) {
		final char []text = new char[offsetRightCharSeq - offsetLeftCharSeq];
		int j = 0;
		for ( int i = offsetLeftCharSeq; i < offsetRightCharSeq; ++i) {
			text[j] = this.in[i];
			++j;
		}
		/* if ( text[text.length-1] == '\0' )
			//System.out.println("found '\0' in getText of Lexer"); */
		return text;
	}




	/**
	 * the current character, in[k], is the first one of a metaobject annotation
	 * or literal object. in[k] would be '1' in the examples below
	 *         @version<<+1+>>
	 *         ($$1  > 0  $$)
	 * This method returns the text between the current character (including it)
	 * and the sequence of symbols rightCharSequence.
	 *
	 * rightCharSequence should be terminated with '\0'
	 */
	public char []getTextTill(char[] rightCharArray2, int positionStartingToken) {
		/**
		 * an inefficient way of looking for rightCharSequence, I know that
		 */
		  /*
		   * size of rightCharSequence. The last character, '\0', does not count
		   */
		int size = rightCharArray2.length - 1;
		  // this method should work even if rightCharSequence does not end with '\0'
		if ( rightCharArray2[size] != '\0' ) ++size;

		char ch = rightCharArray2[0];
		final int startText = k;
		final int startLineAnnotation = lineNumber;
		int offsetStartCurrentLine = k;
		while ( true ) {
			if (in[k] == '\0') {
				final StringBuilder rightSeq = new StringBuilder();
				int jj = 0;
				while ( rightCharArray2[jj] != '\0' && jj < rightCharArray2.length ) {
					rightSeq.append(rightCharArray2[jj]);
					++jj;
				}
				error("Compile-Time metaobject annotation or literal object that started in line "
						+ startLineAnnotation + " was not ended with "
						+ rightSeq + " as expected", startLineAnnotation);
				token = Token.EOF;
				symbol = new Symbol(token, "", offsetStartCurrentLine, lineNumber,
						getColumn(), positionStartingToken, compilationUnit);
				return null;
			} else {
				if (in[k] == ch) {
					final int auxK = k;
					int j = 0;
					while (in[k] != '\0' && rightCharArray2[j] != '\0'
							&& in[k] == rightCharArray2[j]) {
						k++;
						j++;
					}
					// found rightCharSequence in array "in" ?
					if (rightCharArray2[j] == '\0') {
						// reached end of array rightString
						final int sizeTextMetaobject = k - startText - size;
						//char[] text = new char[sizeTextMetaobject+1];
						final char[] text = new char[sizeTextMetaobject];
						/*
						 * int m = 0; for (j = startText; j <= k - 2; j++) {
						 * text[m++] = in[j]; }
						 */
						j = startText;
						for (int m = 0; m < sizeTextMetaobject; m++) {
							text[m] = in[j];
							j++;
						}
						// text[sizeTextMetaobject] = '\0';

						String rightCharSeqString = "";
						ch = rightCharArray2[0];
						int i = 0;
						while ( ch != '\0' ) {
							rightCharSeqString = rightCharSeqString + ch;
							ch = rightCharArray2[++i];
						}
						tokenRightCharSeq = Token.RIGHTCHAR_SEQUENCE;
						symbolRightCharSeq = new SymbolCharSequence(token, rightCharSeqString, offsetStartCurrentLine,
								         lineNumber, getColumn(), k - size, rightCharArray2,
								         size, compilationUnit);


						return text;
					}
					else
						k = auxK + 1;

				} else {
					if (in[k] == '\n') {
						if (in[k] == '\r')
							k++;
						lineNumber++;
						offsetStartCurrentLine = k + 1;
					}
					k++;
				}
			}
		}
	}


	/**
	 * the current character, in[k], is the first one of a metaobject annotation
	 * or literal object. in[k] would be '1' in the examples below
	 *         @version<<+1+>>
	 *         ($$1  > 0  $$)
	 * This method returns the position of rightCharArray2 in the text,
	 * which is "+>>" and "$$)" in the examples.
	 * rightCharArray2 should be terminated with '\0'
	 */
	private int getOffsetRightCharArray(char[] rightCharArray2, int positionStartingToken) {
		/**
		 * an inefficient way of looking for rightCharSequence, I know that
		 */
		  /*
		   * size of rightCharSequence. The last character, '\0', does not count
		   */
		int size = rightCharArray2.length - 1;
		  // this method should work even if rightCharSequence does not end with '\0'
		if ( rightCharArray2[size] != '\0' ) ++size;

		int index = k;

		final char ch = rightCharArray2[0];
		int startLine2 = lineNumber;
		while (true) {
			if (in[index] == '\0') {
				error("Compile-Time metaobject annotation or literal object that started in line"
						+ startLine2 + " was not ended with "
						+ rightCharArray2.toString() + " as expected");
				token = Token.EOF;
				symbol = new Symbol(token, "", startLine2, lineNumber,
						getColumn(), positionStartingToken, compilationUnit);
				return -1;
			} else {
				if (in[index] == ch) {
					final int auxK = index;
					int j = 0;
					while (in[index] != '\0' && rightCharArray2[j] != '\0'
							&& in[index] == rightCharArray2[j]) {
						index++;
						j++;
					}
					// found rightCharSequence in array "in" ?
					if (rightCharArray2[j] == '\0') {
						return index - size;
					}
					else
						index = auxK + 1;

				} else {
					if (in[index] == '\n') {
						if (in[index] == '\r')
							index++;
						lineNumber++;
						startLine2 = index + 1;
					}
					index++;
				}
			}
		}
	}

	public void setOneCharSymbols(boolean oneCharSymbols) {
		this.oneCharSymbols = oneCharSymbols;
	}
	public Token getTokenRightCharSeq() {
		return tokenRightCharSeq;
	}

	public SymbolCharSequence getSymbolRightCharSeq() {
		return symbolRightCharSeq;
	}

	/**
	 * return the string of the statement that starts at 'first' and whose last
	 * symbol is 'last'
	   @param sym
	   @return
	 */
	public String stringStatementFromTo(Symbol first, Symbol last) {
		final char []currentCompilationUnitText = compilationUnit.getText();
		final int start = first.getOffset();
		final int theEnd = last.getOffset() + last.symbolString.length();
		final int size = theEnd - start;
		final char []statementText = new char[size];
		/*
		for (int i = 0; i < size; ++i) {
			statementText[i] = currentCompilationUnitText[start + i];
		}
		*/
		System.arraycopy(currentCompilationUnitText, start, statementText, 0, size);

		return new String(statementText);
	}

	public HashMap<String, CyanMetaobjectLiteralObjectSeq> getMetaObjectLiteralObjectSeqTable() {
		return metaObjectLiteralObjectSeqTable;
	}

	public void addMetaObjectLiteralObjectSeqTable(
			HashMap<String, CyanMetaobjectLiteralObjectSeq> metaObjectLiteralObjectSeqTablePackage) {

				metaObjectLiteralObjectSeqTablePackage.forEach( (key, value) -> {
					this.metaObjectLiteralObjectSeqTable.put(key, value);
				} );

		//this.metaObjectLiteralObjectSeqTable = metaObjectLiteralObjectSeqTable;
	}

	/**
	 * shift the index in the input by <code>amount</code>. If the index is 'k', this
	 * method makes k = k + amount
	   @param length
	 */
	public void shiftInputIndex(int amount) {
		k = k + amount;
	}

	/**
	 * set the current index for lexical analysis. Very dangerous.
	   @param newIndex
	 */
	public void setInputIndex(int newIndex) {
		// beforeLastTokenPos = lastTokenPos = newIndex;
    	k = newIndex;
	}

	/**
	 * subtract count from the current line number
	 */
	public void subFromLineNumber(int count) {
		lineNumber = lineNumber - count;
	}
	/**
	 * set the current index for lexical analysis. Very dangerous.
	   @param newIndex
	 */
	public void setInputIndex2(int newIndex) {

		commentLevel = 0;
		startLine = 0;


		beforeLastTokenPos = lastTokenPos = newIndex;
		lineNumber = 1;
		for (int i = 0; i < newIndex; ++i) {
			if ( in[i] == '\n' ) {
				++lineNumber;
				if ( in[i+1] != '\0' )
					startLine = i + 1;

			}
		}
    	k = newIndex;
		if ( in[k+1] != '\0' )
			startLine = k + 1;
	}

	public String expectedRightSymbolSequence() {
		if ( rightSymbolSeqStack.empty() )
			return null;
		else
			return rightSymbolSeqStack.peek();
	}


	public boolean getNewLineAsToken() {
		return newLineAsToken;
	}


	public void setNewLineAsToken(boolean newLineAsToken) {
		this.newLineAsToken = newLineAsToken;
	}

	/**
	 * return true if the string <code>charSeq</code> is valid right char sequence
	   @param charSeq
	   @return
	 */

	public static boolean checkRightCharSeq(String charSeq) {
		final int size = charSeq.length();
		if ( ">}]/\\".indexOf(charSeq.charAt(size-1)) < 0 )
			return false;
		for ( int i = 0; i < size; ++i) {
			final char ch = charSeq.charAt(i);
			if ( validSymbolsForLiteralSeqObjects.indexOf(ch) < 0 || "([{<".indexOf(ch) >= 0 )
				return false;
			if ( (ch == '?' || ch == ':') && charSeq.indexOf(')') >= 0 ) {
				return false;
			}
			if ( ch == '+' && charSeq.indexOf(">") >= 0 )
				return false;
		}
		if ( charSeq.startsWith(":)") )
			return false;
		if ( charSeq.startsWith("]]") )
			return false;

		/*
		 * right char sequences cannot start or end with '('
		 */
		return charSeq.charAt(size-1) != ')' && !charSeq.contains(">>");
	}


	/**
	 * return true if the string <code>leftCharSeq</code> is valid left char sequence
	   @param charSeq
	   @return
	 */

	public static boolean checkLeftCharSeq(String charSeq) {
		final int size = charSeq.length();
		if ( "[{</\\".indexOf(charSeq.charAt(0)) < 0 )
			return false;

		for ( int i = 0; i < size; ++i) {
			final char ch = charSeq.charAt(i);
			if ( validSymbolsForLiteralSeqObjects.indexOf(ch) < 0 || ")]}>".indexOf(ch) >= 0 )
				return false;
			if ( (ch == '?' || ch == ':') && charSeq.indexOf('(') >= 0 ) {
				return false;
			}
			if ( ch == '+' && charSeq.indexOf("<") >= 0 )
				return false;
}
		/*
		 * left char sequences cannot start or end with '(' or ')'
		 */
		return charSeq.charAt(size-1) != '(' && !charSeq.contains("<<");
	}

	public void pushRightSymbolSeq(String rightSymbolSeq) {
		rightSymbolSeqStack.push(rightSymbolSeq);
	}

	public boolean checkRightSymbolSeq(String possibleRightSymbolSeq) {
		if ( Lexer.checkRightCharSeq(possibleRightSymbolSeq) ) {
    		if ( ! rightSymbolSeqStack.empty() &&
	    			rightSymbolSeqStack.peek().equals(possibleRightSymbolSeq) ) {
    			return true;
    		}
		}
		return false;
	}

	/**
	   @param compiler TODO
	 * @param cyanMacro
	 */
	public void pushMacroKeywords(CyanMetaobjectMacro cyanMacro) {
		for ( final String keyword : cyanMacro.getMacroKeywords() ) {
			final Integer number = metaobjectMacroKeywordMap.get(keyword);
			if ( number == null ) {
				metaobjectMacroKeywordMap.put(keyword, 1);
			}
			else {
				metaobjectMacroKeywordMap.put(keyword, number + 1);
			}
		}
	}

	public void pushStartMacroKeywords(CyanMetaobjectMacro cyanMacro) {
		for ( final String keyword : cyanMacro.getStartKeywords() ) {
			final Integer number = metaobjectMacroKeywordMap.get(keyword);
			if ( number == null ) {
				metaobjectMacroKeywordMap.put(keyword, 1);
			}
			else {
				   // for now, this will never happens
				metaobjectMacroKeywordMap.put(keyword, number + 1);
			}
		}
	}


	/**
	   @param compiler TODO
	 * @param cyanMacro
	 */
	public void popMacroKeywords(CyanMetaobjectMacro cyanMacro) {
		for ( final String keyword : cyanMacro.getMacroKeywords() ) {
			final Integer number = metaobjectMacroKeywordMap.get(keyword);
			if ( number == null ) {
				compiler.error2(compiler.symbol, "Internal error: macro keywords of '" + cyanMacro.getName() +
						"' are not in the Lexer table anymore");
			}
			else {
				if ( number == 1 ) {
					metaobjectMacroKeywordMap.remove(keyword);
				}
				else {
					metaobjectMacroKeywordMap.put(keyword, number - 1);
				}
			}
		}
	}


	/**
	   @param t
	   @return
	 */
	public static String valueToFeatureString(Tuple2<String, WrExprAnyLiteral> t) {
		String strValue;

		final ExprAnyLiteral value = meta.GetHiddenItem.getHiddenExprAnyLiteral(t.f2);
		if ( value instanceof ExprAnyLiteralIdent || value instanceof ExprLiteralNil ) {
			strValue = "\"" + value.asString() + "\"";
		}
		else {
			strValue = value.asString();
		}

		// strValue = NameServer.removeQuotes(t.f2.asString());

		if ( strValue.equals("Nil") )
			strValue = "\"" + strValue + "\"";
		return strValue;
	}


	/**
	   @param typeName
	   @param isSymbol
	   @return
	 */
	public static boolean isSymbol(String typeName) {
		if ( typeName == null || typeName.length() == 0 ) {
			/*
			 * according to method ifPrototypeReturnsItsName, typeOrSymbol is not a type.
			 * It should be a symbol
			 */
			return true;
		}
		else {
			if ( typeName.indexOf('.') < 0 && Character.isLowerCase(typeName.charAt(0)) ) {
				/*
				 * the type name does not contains '.' and it starts with a lower case letter.
				 * Then typeOrSymbol should be a symbol, not a type. Note that a type may start with
				 * a lower case letter if it is preceded by a package:<br>
				 * <code>cyan.lang.Int</code>
				 */
				return true;
			}
		}
		return false;
	}


	/**
	 * find a index to insert a metaobject annotation. It should be at text[offset] or before.
	 * It is assumed that text[offset] is not ' ', '\t', '\r', '\n', or '\0'
	   @param text
	   @param offset
	   @return
	 */
	static public int findIndexInsertText(char []text, int offset) {
		int n = offset;
		if ( n >= 0 && (text[n-1] == ' ' || text[n-1] == '\t') ) {
			--n;
			while ( n >= 0 && (text[n] == ' ' || text[n] == '\t') ) {
				--n;
			}
			/*if ( text[n] == '\r' || text[n] == '\n' )
				return n;
			else  */
			return n + 1;
		}
		return n;
	}


	static public boolean iskeyword(String s) {

		if ( s.length() == 0 )
			return false;
		char ch = s.charAt(0);
		if ( Character.isLetter(ch) || ch == '_' ) {
			for (int i = 0; i < s.length() - 1; ++i) {
				ch = s.charAt(i);
				if ( ! Character.isLetterOrDigit(ch) && ch != '_' )
					return false;
			}
			return s.charAt(s.length()-1) == ':';
		}
		else {
			return false;
		}
	}

	static public String addSpaceAfterComma(String s) {
		String ret = "";
		for (int i = 0; i < s.length() - 1; ++i) {
			final char ch = s.charAt(i);
			final char nextCh = s.charAt(i+1);
			if ( ch == ',' && nextCh != ' ' ) {
				ret += ", ";
			}
			else {
				ret += ch;
			}
		}
		return ret + s.charAt(s.length()-1);
	}


	public static boolean isCyanKeyword(String ident) {
		return keywordsTable.get(ident) != null;
	}

	/**
	 * replace in text the characters between offsetStart (including) and offsetEnd (excluding, till offsetEnd - 1)
	 * by the characters of textToReplace. The new char array is returned.
	   @param offsetStart
	   @param offsetEnd
	   @param text
	   @param textToReplace
	 */
	public static char []replaceTextByNewText(int offsetStart, int offsetEnd, char []text, char []textToReplace) {
		final int size = text.length;
		final int sizeTextReplace = textToReplace.length;
		final int newSize = size - (offsetEnd - offsetStart) + sizeTextReplace;
		final char []newText = new char[newSize];
		int i = 0;
		for (; i < offsetStart; ++i) {
			newText[i] = text[i];
		}

		for ( int j = 0; j < sizeTextReplace; ++j) {
			newText[i] = textToReplace[j];
			++i;
		}

		for ( int k = offsetEnd; k < size; ++k) {
			newText[i] = text[k];
			++i;
		}
		return newText;
	}

	public void setCheckSpaceAfterComma(boolean checkSpaceAfterComma) {
		this.checkSpaceAfterComma = checkSpaceAfterComma;
	}


	/**
	 * true if the lexer should consider sequences of symbols one char at a time.
	 * That is, if the lexer finds ")[", it will consider just the ")". This is used
	 * when a metaobject annotation has parameters and text:
	 *      @text(trim_spaces)[[
	 *          some text
	 *      ]]
	 */
	private boolean oneCharSymbols;

	/**
	 * the instruction set to the compilation
	 */
	private final HashSet<CompilationInstruction>	compInstSet;

	/**
	 * a stack of right symbol sequences. Each time the lexer found a left symbol sequence
	 * that starts a literal metaobject such as <br>
	 * <code>
	 *     {@literal @}graph{* 1:2, 2:3, 3:1 *} <br>
	 *     [* "one":1, "two":2 *]<br>
	 * </code><br>
	 * it pushes the right sequence into this stack. When it found a right sequence
	 * it pops it from the stack after checking it is the expected sequence. This is important
	 * in nested literal objects such as<br>
	 * <code> [*  [* 1, 2 *], [* 3 *] *]<br>
	 *        [* [+  "one":1, "two":2 +], [+ "three":3 +] *] <br>
	 * </code>
	 */
	private final Stack<String> rightSymbolSeqStack;

	/**
	 * true if the character <code>\n</code> should be considered as a symbol. This
	 * may be necessary when the lexer is used with literal metaobjects that implement
	 * interface {@link meta#IParseWithCyanCompiler_parsing}. Some DSL´s may consider the
	 * end of line as a token.
	 */
	private boolean newLineAsToken;

	private final Compiler compiler;

	/**
	 * number of tabs since the beginning of line. This is important for the error messages give the
	 * correct column number of the error
	 */
	private int numTabsInCurrentLine;
	/**
	 * number of white spaces for each tab character
	 */
	private static final int numWhiteSpacesForTab = 4;

	/**
	 * minimun number of spaces of indentation. This value is used
	 * both for internal statements, like the statements of if´s and while´s
	 * and also for considering the next line as part of the same statement
	 * or expression as the previous line.
	 * Since MIN_INDENTATION is 2, this code is illegal:
	 *           if true {
	 *            a = 0;
	 *           }
	 * because the column of 'if' is just 1 less than the column of 'a'.
	 * This is also illegal:
	 * 			Out println: 1
	 * 			 + 2;
	 * The column of '+' is just 1 more than the column of 'Out'.
	 */
	public static final int MIN_INDENTATION = 2;
//	/**
//	 * true if the current symbol is inside a statement
//	 * that is user code. That is, the statement was not produced by
//	 * metaobjects or the compiler itself
//	 */
//	public boolean inStatement = false;
//	public int numberNestedStatements = 0;
//	/**
//	 * the first symbol of a statement. It only has a valid value
//	 * if inStatement is true
//	 */
//	public Symbol firstStatementSymbol;
//	public Stack<Symbol> firstStatementSymbolStack = new Stack<>();
//
//	/**
//	 * column number of the first symbol of a statement. It only has a
//	 * valid value if inStatement is true
//	 */
//	private int columnNumberFirstSymbol;

	/**
	 * true if the lexer should check if there is a space after ','
	 */
	private boolean checkSpaceAfterComma = true;
}
