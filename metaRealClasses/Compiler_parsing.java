/**

 */
package metaRealClasses;

import java.util.ArrayList;
import java.util.List;
import ast.CompilationUnit;
import ast.Expr;
import ast.ExprIdentStar;
import ast.ICalcInternalTypes;
import ast.MethodSignature;
import ast.Prototype;
import ast.Statement;
import ast.StatementImport;
import lexer.Lexer;
import lexer.Symbol;
import meta.IActionFunction;
import meta.ICompiler_parsing;
import meta.IICalcInternalTypes;
import meta.MetaHelper;
import meta.Token;
import meta.WrCompilationUnit;
import meta.WrExpr;
import meta.WrExprIdentStar;
import meta.WrMethodSignature;
import meta.WrPrototype;
import meta.WrStatement;
import meta.WrSymbol;
import meta.lexer.MetaLexer;
import saci.Compiler;

/**
 * An abstraction of the Cyan compiler used by regular users to parse literal objects and "at" metaobjects with a DSL
   @author José

 */
public class Compiler_parsing extends CompilerAction_parsing implements ICompiler_parsing {


	/**
	 * constructor
	   @param compiler
	   @param cyanAnnotation
	   @param lexer
	   @param leftSeqSymbols, the sequence of symbols that start literal object or the DSL of the "at" metaobject. It may be null
	 */
	public Compiler_parsing(saci.Compiler compiler, Lexer lexer, String leftSeqSymbols) {
		//this.compiler = compiler.clone();
		super(compiler);
		//seqSymbolStack = new Stack<>();
		if ( leftSeqSymbols != null ) {
			rightSeqSymbols = MetaLexer.rightSymbolSeqFromLeftSymbolSeq(leftSeqSymbols);
			lexer.pushRightSymbolSeq(rightSeqSymbols);
			//seqSymbolStack.push(rightSeqSymbols);
		}
		else
			rightSeqSymbols = null;
		foundEOLO = false;
		endOfLiteralObject = null;
		exprStatList = new ArrayList<>();

	}

	/**
	 * Consider character <code>\n</code> as a token if the parameter is true. See {@link Lexer#setNewLineAsToken(boolean)}.
	 */
	public void setNewLineAsToken(boolean newLineAsToken) {
		compiler.setNewLineAsToken(newLineAsToken);
	}

	@Override
	public void next() {
		if ( ! foundEOLO ) {
			compiler.next();

			final Symbol sym = compiler.getSymbol();
			final String symStr = sym.getSymbolString();


			if ( symStr.equals(this.rightSeqSymbols) || sym.token == Token.EOF ) {  // sym.token == Token.RIGHTCHAR_SEQUENCE ) { //
				foundEOLO = true;
				this.endOfLiteralObject =  new Symbol(Token.EOLO, "", sym.getStartLine(), sym.getLineNumber(),
						sym.getColumnNumber(), sym.getOffset(), sym.getCompilationUnit());
			}

		}
	}

	@Override
	public WrSymbol getSymbol() {
		if ( foundEOLO )
			return this.endOfLiteralObject.getI();
		final Symbol sym = compiler.getSymbol();
		final String symStr = sym.getSymbolString();


		if ( symStr.equals(this.rightSeqSymbols) || sym.token == Token.EOF ) {  // sym.token == Token.RIGHTCHAR_SEQUENCE ) { //
			foundEOLO = true;
			this.endOfLiteralObject =  new Symbol(Token.EOLO, "", sym.getStartLine(), sym.getLineNumber(),
					sym.getColumnNumber(), sym.getOffset(), sym.getCompilationUnit());
			return endOfLiteralObject.getI();
		}
		else {
			/*
			if ( sym.getColumnNumber() <= columnAnnotation2 ) {
				this.error(sym, "The text attached to this annotation, between the delimiters, should "
										+ "be at least in column " + columnAnnotation2);
			}
			*/
			return sym.getI();
		}
	}

	@Override
	public void removeLastExprStat() {
		final int last = exprStatList.size();
		if ( last > 0 )
			exprStatList.remove(last-1);
	}

	@Override
	public WrExpr expr() {
		final Expr e = compiler.expr();
		exprStatList.add(e);
		return e.getI();
	}


	@Override
	public WrExpr exprBasicTypeLiteral() {
		final Expr e = compiler.exprBasicTypeLiteral();
		this.exprStatList.add(e);
		return e.getI();
	}

	@Override
	public WrExpr type() {
		final Expr t = compiler.type();
		exprStatList.add(t);
		return t.getI();
	}

	@Override
	public WrExpr type(boolean leftParAllowedAfterOr) {
		final Expr t = compiler.type(leftParAllowedAfterOr);
		exprStatList.add(t);
		return t.getI();
	}

	@Override
	public boolean startType(Token t) {
		return Compiler.startType(t);
	}


	@Override
	public WrStatement statement() {
		Statement s;
		if ( compiler.getSymbol().token == Token.IMPORT ) {
			s = meta.GetHiddenItem.getHiddenStatement(
					this.importPackages());
		}
		else {
			s = compiler.statement();
		}
		exprStatList.add(s);
		return s.getI();
	}


	private WrStatement importPackages() {

		WrStatement stat = null;
		if ( compiler.getSymbol().token == Token.IMPORT ) {
			compiler.next();
			if ( compiler.getSymbol().token != Token.IDENT ) {
				error( getSymbol(),
						  "package name expected in import declaration");
			}
			else {
				final ExprIdentStar importPackage = compiler.ident();
				if ( importPackage.getName().startsWith(MetaHelper.cyanLanguagePackageName) ) {
					if ( importPackage.getName().equals(MetaHelper.cyanLanguagePackageName) ) {
						error(importPackage.getFirstSymbol().getI(),
								"Package 'cyan.lang' is automatically imported. It cannot be imported by the user");
					}
					else {
						error(importPackage.getFirstSymbol().getI(),
								"It is not legal to have a package that starts with 'cyan.lang'");
					}
				}
				stat = (new StatementImport(importPackage, null)).getI();
			}
			if ( compiler.getSymbol().token == Token.SEMICOLON )
				compiler.next();
		}
		return stat;
	}
	/*
	@Override
	public List<WrStatement> statementList() {
		StatementList s = compiler.statementList();
		List<WrStatement> isList = new ArrayList<>();
		for ( Statement stat : s.getStatementList() ) {
			exprStatList.add(stat);
			isList.add(stat);
		}
		return isList;
	}
	*/

	@Override
	public WrExprIdentStar parseSingleIdent() {
		final Expr e = compiler.parseIdent();
		if ( e instanceof ExprIdentStar ) {
			if ( ((ExprIdentStar ) e).getIdentSymbolArray().size() > 1 )
				return null;
			else
				return ((ExprIdentStar ) e).getI();
		}
		else {
			return null;
		}
	}

	@Override
	public WrExpr parseIdent() {
		Expr e = compiler.parseIdent();
		return e == null ? null : e.getI();
	}

	@Override
	public WrExprIdentStar ident() {
		ExprIdentStar e = compiler.ident();
		return e == null ? null : e.getI();
	}
	@Override
	public boolean symbolCanStartExpr(WrSymbol symbol) {
		return Compiler.startExpr(meta.GetHiddenItem.getHiddenSymbol(symbol));
	}


	@Override
	public void addExprStat(IICalcInternalTypes exprStat) {
		exprStatList.add( meta.GetHiddenItem.getHiddenStatement( (WrStatement ) exprStat) );
	}

	public void addExprStat(ICalcInternalTypes exprStat) {
		exprStatList.add(exprStat);
	}

	public List<ICalcInternalTypes> getExprStatList() {
		return exprStatList;
	}

	@Override
	public boolean isOperator(Token token) {
		return Compiler.isOperator(token);
	}


	@Override
	public void pushRightSymbolSeq(String rightSymbolSeq) {
		compiler.pushRightSymbolSeq(rightSymbolSeq);
	}

	@Override
	public boolean isBasicType(Token t) {
		return Compiler.isBasicType(t);
	}

	@Override
	public WrMethodSignature methodSignature() {
		MethodSignature ms = compiler.methodSignature();
		return ms == null ? null : ms.getI();
	}

	@Override
	public WrMethodSignature methodSignature(boolean finalKeyword, boolean abstractKeyword) {
		MethodSignature ms = compiler.methodSignature(finalKeyword, abstractKeyword);
		return ms == null ? null : ms.getI();
	}

	private String rightSeqSymbols;
	//private Stack<String> seqSymbolStack;
	/**
	 * true if the right char sequence was found. Subsequent calls to 'next' will return EOLO
	 */
	private boolean foundEOLO;
	/**
	 * the end-of-literal-object symbol
	 */
	private Symbol endOfLiteralObject;
	/**
	 * list of expressions and statements returned by calls to {@link Compiler_parsing#expr()} and, in the future, to method statement()
	 */
	private final List<ICalcInternalTypes> exprStatList;
	public String getRightSeqSymbols() {
		return rightSeqSymbols;
	}


	@Override
	public 	IActionFunction searchActionFunction(String name) {
		return compiler.searchActionFunction(name);
	}


	@Override
	public WrSymbol next(int i) {
		return compiler.next(i).getI();
	}

	@Override
	public WrPrototype getCurrentPrototype() {
		Prototype pu = compiler.getCurrentPrototype();
		return pu == null ? null : pu.getI();
	}

	@Override
	public WrCompilationUnit getCurrentCompilationUnit() {
		CompilationUnit cunit = compiler.getCurrentPrototype().getCompilationUnit();
		return cunit == null ? null : cunit.getI();
	}


	@Override
	public void setProhibitTypeof(boolean value) {
		compiler.setProhibitTypeof(value);
	}


	@Override
	public boolean getParsingForInterpreter() {
		return compiler.getParsingForInterpreter();
	}

	@Override
	public void setParsingForInterpreter(boolean parsingForInterpreter) {
		compiler.setParsingForInterpreter(parsingForInterpreter);
	}

}
