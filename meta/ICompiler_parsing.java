package meta;

import ast.CompilationUnit;
import ast.Expr;
import ast.Prototype;
import ast.Type;
import saci.Compiler;
import saci.CompilerManager;

/**
 * Compiler interface for metaobjects during parsing. These metaobjects can retrieve
 * tokens using the Cyan compiler but only tokens that are inside the metaobject annotation.
 * Therefore the metaobject annotation should be delimited by a sequence of symbols or otherwise.
 * See {@link IParseWithCyanCompiler_parsing}.
 *
   @author José
 */
public interface ICompiler_parsing extends ICompilerAction_parsing {


	void next();
	WrSymbol getSymbol();
	boolean symbolCanStartExpr(WrSymbol symbol);
	/**
	 * all expressions and statements analyzed by methods {@link ICompiler_parsing#expr()}, {@link ICompiler_parsing#type()}, and
	 * {@link ICompiler_parsing#statement()} should be kept by the compiler in a list. In the semantic analysis,
	 * the compiler calculates the types of the expressions and statements of this list.
	 *
	 *  Method removeLastExprStat removes the last element of this list. This is necessary when
	 *  the last expression was mistakenly taken as an expression when it is in fact an element of
	 *  the DSL that follows the DSL. For exemple,<br>
	 *  <code><br>
	 *  {@literal @}shouldbe(R, S){*<br>
	 *      1 + 1 == 2,<br>
	 *      symbol R,<br>
	 *      localVariable S<br>
	 *  *}<br>
	 *  </code>
	 *  Here <code>"symbol"</code> and <code>"localVariable"</code> are initially taken
	 *  to be expressions but they are in fact keywords of the DSL whose code in between
	 *  <code>{*</code> and <code>*}</code>. In this case, the metaobject compile for
	 *  this DSL should use removeLastExprStat to remove <code>"symbol"</code> and <code>"localVariable"</code>
	 *  from the list.
	 */
	void removeLastExprStat();

	WrExpr type(boolean leftParAllowedAfterOr);
	WrExpr type();
	WrExpr expr();
	WrExpr exprBasicTypeLiteral();
	boolean startType(Token t);
	WrStatement statement();
	// List<WrStatement> statementList();
	WrExprIdentStar parseSingleIdent();



	boolean isOperator(Token token);


	void pushRightSymbolSeq(String rightSymbolSeq);
	WrExpr parseIdent();
	WrExprIdentStar ident();

	boolean isBasicType(Token t);
	WrMethodSignature methodSignature();
	WrMethodSignature methodSignature(boolean finalKeyword, boolean abstractKeyword);
	IActionFunction searchActionFunction(String name);

	static ICompiler_parsing getCompilerToInternalDSL(char []sourceCode, String sourceCodeFilename,
			String sourceCodeCanonicalPath, WrCyanPackage cyanPackage) {
		return CompilerManager.getCompilerToInternalDSL(sourceCode, sourceCodeFilename,
				sourceCodeCanonicalPath, cyanPackage);
	}

	WrSymbol next(int i);


	static public WrExpr parseSingleTypeFromString(String typeAsString,
			WrSymbol symUsedInError, String message, WrCompilationUnitSuper compUnit, WrPrototype currentPU
			//, Compiler compiler
			) {
		Expr e = Compiler.parseSingleTypeFromString(typeAsString, symUsedInError.hidden, message, compUnit.hidden,
				(Prototype ) currentPU.hidden);
		return e == null ? null : e.getI();
	}

	static public WrType singleTypeFromString(String typeAsString,
			WrSymbol symUsedInError, String message, WrCompilationUnit compUnit, WrPrototype currentPU, WrEnv env) {
		Type e = Compiler.singleTypeFromString(
				typeAsString, symUsedInError.hidden, message, (CompilationUnit ) compUnit.hidden,
				(Prototype ) currentPU.hidden, env.hidden);
		return e == null ? null : e.getI();
	}

	WrPrototype getCurrentPrototype();
	WrCompilationUnit getCurrentCompilationUnit();
	void addExprStat(IICalcInternalTypes exprStat);

	// boolean getProhibitTypeof();

	void setProhibitTypeof(boolean prohibitTypeof);

	boolean getParsingForInterpreter();
	void setParsingForInterpreter(boolean parsingForInterpreter);


}
