
package saci;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Executors;
import ast.Annotation;
import ast.AnnotationAt;
import ast.AnnotationLiteralObject;
import ast.AnnotationMacroCall;
import ast.CaseRecord;
import ast.CastRecord;
import ast.CompilationUnit;
import ast.CompilationUnitDSL;
import ast.CompilationUnitSuper;
import ast.ContextParameter;
import ast.CyanPackage;
import ast.Expr;
import ast.ExprAnyLiteral;
import ast.ExprAnyLiteralIdent;
import ast.ExprBooleanAnd;
import ast.ExprBooleanOr;
import ast.ExprFunction;
import ast.ExprFunctionRegular;
import ast.ExprFunctionWithKeywords;
import ast.ExprGenericPrototypeInstantiation;
import ast.ExprIdentStar;
import ast.ExprIndexed;
import ast.ExprJavaArrayType;
import ast.ExprLiteralArray;
import ast.ExprLiteralBooleanFalse;
import ast.ExprLiteralBooleanTrue;
import ast.ExprLiteralByte;
import ast.ExprLiteralChar;
import ast.ExprLiteralDouble;
import ast.ExprLiteralFloat;
import ast.ExprLiteralInt;
import ast.ExprLiteralLong;
import ast.ExprLiteralMap;
import ast.ExprLiteralNil;
import ast.ExprLiteralShort;
import ast.ExprLiteralString;
import ast.ExprLiteralTuple;
import ast.ExprMessageSendUnaryChain;
import ast.ExprMessageSendUnaryChainToExpr;
import ast.ExprMessageSendUnaryChainToSuper;
import ast.ExprMessageSendWithKeywordsToExpr;
import ast.ExprMessageSendWithKeywordsToSuper;
import ast.ExprNonExpression;
import ast.ExprObjectCreation;
import ast.ExprSelf;
import ast.ExprSelfPeriodIdent;
import ast.ExprSelf__;
import ast.ExprSelf__PeriodIdent;
import ast.ExprSurroundedByContext;
import ast.ExprTypeIntersection;
import ast.ExprTypeUnion;
import ast.ExprTypeof;
import ast.ExprUnary;
import ast.ExprWithParenthesis;
import ast.FieldDec;
import ast.GenericParameter;
import ast.GenericParameter.GenericParameterKind;
import ast.InterfaceDec;
import ast.JVMPackage;
import ast.MessageBinaryOperator;
import ast.MessageKeywordWithRealParameters;
import ast.MessageSendToAnnotation;
import ast.MessageWithKeywords;
import ast.MetaInfoServer;
import ast.MethodDec;
import ast.MethodKeywordWithParameters;
import ast.MethodSignature;
import ast.MethodSignatureOperator;
import ast.MethodSignatureUnary;
import ast.MethodSignatureWithKeywords;
import ast.ObjectDec;
import ast.ParameterDec;
import ast.Program;
import ast.Prototype;
import ast.SlotDec;
import ast.Statement;
import ast.StatementAnnotation;
import ast.StatementAssignmentList;
import ast.StatementBreak;
import ast.StatementCast;
import ast.StatementFor;
import ast.StatementIf;
import ast.StatementList;
import ast.StatementLocalVariableDec;
import ast.StatementLocalVariableDecList;
import ast.StatementMinusMinusIdent;
import ast.StatementNull;
import ast.StatementPlusPlusIdent;
import ast.StatementRepeat;
import ast.StatementReturn;
import ast.StatementReturnFunction;
import ast.StatementThrow;
import ast.StatementTry;
import ast.StatementType;
import ast.StatementWhile;
import ast.Type;
import ast.TypeJavaRef;
import ast.TypeUnion;
import ast.VariableDecInterface;
import cyan.lang.CyInt;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._CyanMetaobjectAtAnnot;
import cyan.reflect._CyanMetaobjectMacro;
import cyan.reflect._IActionNewPrototypes__parsing;
import cyan.reflect._IActionPrototypeLater__parsing;
import cyan.reflect._IAction__dpp;
import cyan.reflect._ICodeg;
import cyan.reflect._ICompilerInfo__parsing;
import cyan.reflect._IInformCompilationError;
import cyan.reflect._IParseWithCyanCompiler__parsing;
import error.CompileErrorException;
import error.ErrorKind;
import error.UnitError;
import lexer.CompilerPhase;
import lexer.Lexer;
import lexer.Symbol;
import lexer.SymbolCharSequence;
import lexer.SymbolCyanAnnotation;
import lexer.SymbolIdent;
import lexer.SymbolKeyword;
import lexer.SymbolLiteralObject;
import lexer.SymbolLiteralObjectParsedWithCompiler;
import lexer.SymbolMacroKeyword;
import lexer.SymbolOperator;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CompilationInstruction;
import meta.CompilationStep;
import meta.CompilerMacro_parsing;
import meta.Compiler_dpp;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.CyanMetaobjectError;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectLiteralString;
import meta.CyanMetaobjectMacro;
import meta.IActionAssignment_cge;
import meta.IActionAttachedType_semAn;
import meta.IActionFunction;
import meta.IActionNewPrototypes_parsing;
import meta.IActionPrototypeLater_parsing;
import meta.IAction_cge;
import meta.IAction_dpp;
import meta.IAction_parsing;
import meta.ICodeg;
import meta.ICompilerAction_parsing;
import meta.ICompilerInfo_parsing;
import meta.IInformCompilationError;
import meta.IParseWithCyanCompiler_parsing;
import meta.MetaHelper;
import meta.Timeout;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple5;
import meta.VariableKind;
import meta.WrExprAnyLiteral;
import meta.cyanLang.CyanMetaobjectCompilationContextPop;
import meta.cyanLang.CyanMetaobjectCompilationContextPush;
import meta.cyanLang.CyanMetaobjectCompilationMarkDeletedCode;
import metaRealClasses.CompilerAction_parsing;
import metaRealClasses.CompilerGenericPrototype_parsing;
import metaRealClasses.Compiler_parsing;
import refactoring.ActionDelete;
import refactoring.ActionInsert;

/**
 * This class compiles a single compile unit which can be an object or an
 * interface.
 *
 * @author José
 *
 */
public final class Compiler implements Cloneable {

	static private Hashtable<String, String>	falseKeywordsTable;

	static private Set<String>					basicTypesSet;

	/**
	 *
	 * @param compilationUnit,
	 *            the compilation unit (source code) to be compiled
	 * @param compInstSet,
	 *            the instructions to the compilation
	 */
	public Compiler(CompilationUnitSuper compilationUnit,
			HashSet<meta.CompilationInstruction> compInstSet,
			CompilationStep compilationStep, Project project,
			List<Tuple2<String, byte[]>> codegNameWithCodegFile
	// , Env notNullIfCreatingGenericPrototypeInstantiation
	) {

		this.compilationUnitSuper = compilationUnit;

		if ( compilationUnit instanceof CompilationUnit ) {
			this.compilationUnit = (CompilationUnit) compilationUnit;
		}

		this.compInstSet = compInstSet;
		this.compilationStep = compilationStep;
		this.project = project;
		if ( project != null ) {
			this.program = this.project.getProgram();
		}
		this.codegNameWithCodegFile = codegNameWithCodegFile;

		final String fileName = compilationUnit.getFilename();
		if ( fileName.endsWith(NameServer.ScriptCyanExtension) ) {
			this.scriptCyan = true;
			/*
			 * initially consider that the source code has only statements. If
			 * keyword <code>func</code> is found, this variable is set to
			 * <code>false</code>.
			 */
			scriptCyanStatementsOnly = true;
		}
		else
			this.scriptCyan = false;
		// redo
		/*
		 * symbolArray = compilationUnit.getSymbolArray(); isa = 0;
		 * sizeSymbolArray = compilationUnit.getSizeSymbolArray();
		 */

		nextSymbolList = new Symbol[3];
		nextSymbolList[0] = nextSymbolList[1] = nextSymbolList[2] = null;

		compilationUnit.prepareLexicalAnalysis();

		final char[] text = compilationUnit.getText();

		sizeSymbolList = 0;
		/*
		 * we suppose that the number of symbols is 1/3 of the number of
		 * characters in the text. The "+ 2" is just to prevent any errors
		 */
		symbolListAllocatedSize = text.length / 3 + 100;
		symbolList = new Symbol[symbolListAllocatedSize + 2];
		// if ( symbolList == null || symbolListAllocatedSize < text.length ) {
		// /*
		// * if symbolList has not been allocated before, allocate it now. If
		// * the new text is larger than before, allocate again
		// */
		// symbolList = new Symbol[text.length + 1];
		// symbolListAllocatedSize = text.length;
		// }

		metaObjectMacroTable = compilationUnit.getMetaObjectMacroTable();

		parameterDecStack = new Stack<ParameterDec>();
		localVariableDecStack = new Stack<StatementLocalVariableDec>();
		functionCounter = 0;
		functionStack = new Stack<ExprFunction>();
		objectDecStack = new Stack<ObjectDec>();
		cyanMetaobjectContextStack = new Stack<>();
		lineShift = 0;
		codegList = new ArrayList<>();
		otherCodegList = new ArrayList<>();
		insideCyanMetaobjectCompilationContextPushAnnotation = false;
		mayBeWrongVarDeclaration = false;
		prohibitTypeof = false;
		allowCreationOfPrototypesInLastCompilerPhases = false;
		this.lineNumberStartCompilationContextPush = -1;

		// this.notNullIfCreatingGenericPrototypeInstantiation =
		// notNullIfCreatingGenericPrototypeInstantiation;
		/*
		 * change method clone too
		 */
		lexer = new Lexer(text, compilationUnit, compInstSet, this);
		symbol = lexer.symbol;

		// symbolList[sizeSymbolList] = symbol;
		// ++sizeSymbolList;

	}

	/**

	 */
	public void pushStartMacroKeyworsemAnllMacros() {
		for (final String keyword : this.metaObjectMacroTable.keySet()) {
			final CyanMetaobjectMacro cyanMacro = this.metaObjectMacroTable
					.get(keyword);
			lexer.pushStartMacroKeywords(cyanMacro);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Compiler clone() {

		try {
			final Compiler clone = (Compiler) super.clone();

			// do not clone compilationUnit
			/*
			 * if ( this.compilationUnit != null ) { clone.compilationUnit =
			 * this.compilationUnit.clone(); }
			 */
			if ( compilationUnit != null ) {
				clone.compilationUnitSuper = compilationUnit;
			}

			clone.parameterDecStack = (Stack<ParameterDec>) clone.parameterDecStack
					.clone();
			clone.localVariableDecStack = (Stack<StatementLocalVariableDec>) localVariableDecStack
					.clone();
			clone.functionStack = (Stack<ExprFunction>) functionStack.clone();
			clone.objectDecStack = (Stack<ObjectDec>) objectDecStack.clone();
			clone.cyanMetaobjectContextStack = (Stack<Tuple5<String, String, String, String, Integer>>) cyanMetaobjectContextStack
					.clone();
			// clone.codegList = (List<AnnotationAt>) codegList
			// .clone();
			// clone.otherCodegList = (List<ICodeg>) otherCodegList.clone();
			return clone;
		}
		catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	static {
		falseKeywordsTable = new Hashtable<String, String>();
		falseKeywordsTable.put("byte", "Byte");
		falseKeywordsTable.put("short", "Short");
		falseKeywordsTable.put("int", "Int");
		falseKeywordsTable.put("long", "Long");
		falseKeywordsTable.put("float", "Float");
		falseKeywordsTable.put("double", "Double");
		falseKeywordsTable.put("char", "Char");
		falseKeywordsTable.put("boolean", "Boolean");

		basicTypesSet = new HashSet<String>();
		basicTypesSet.add("Byte");
		basicTypesSet.add("Short");
		basicTypesSet.add("Int");
		basicTypesSet.add("Long");
		basicTypesSet.add("Float");
		basicTypesSet.add("Double");
		basicTypesSet.add("Char");
		basicTypesSet.add("Boolean");
		basicTypesSet.add("String");

	}

	public void setInitialPositionLexer(int offset) {
		lexer.setInitialPositionLexer(offset);
	}

	private int lineNumberStartCompilationContextPush;

	/**
	 * return the line shift, the number of lines that the compiler added before
	 * the current symbol. However, if the current symbol is inside code
	 * introduced by a metaobject annotation then the value returned is -1.
	 *
	 * @return
	 */
	public int getLineShift() {
		if ( cyanMetaobjectContextStack.isEmpty()
				&& !insideCyanMetaobjectCompilationContextPushAnnotation )
			return lineShift;
		else
			return -1;
	}

	/**
	 * copy all variables related to lexical analysis from 'from' to this
	 *
	 * @param from
	 */
	public void copyLexerData(Compiler from) {
		this.previousSymbol = from.previousSymbol;
		this.symbol = from.symbol;
		this.nextSymbolList = from.nextSymbolList;
	}

	public void next() {
		// redo
		previousSymbol = symbol;
		if ( nextSymbolList[0] == null ) {
			lexer.next();
			symbol = lexer.symbol;
			if ( cyanMetaobjectContextStack.isEmpty()
					&& !insideCyanMetaobjectCompilationContextPushAnnotation ) {
				symbol.setLineNumber(symbol.getLineNumber() - lineShift);
			}
		}
		else {
			symbol = nextSymbolList[0];
			nextSymbolList[0] = nextSymbolList[1];
			nextSymbolList[1] = nextSymbolList[2];
			nextSymbolList[2] = null;
		}

		/*
		 * symbol = symbolArray[isa]; if ( isa < sizeSymbolArray ) isa++;
		 */
	}

	/**
	 * return the n-th symbol from the current symbol. If n is 0, the next
	 * symbol is returned. n should be smaller than 3
	 *
	 * @param n
	 * @return
	 */
	public Symbol next(int n) {

		switch (n) {
		case 0:
			if ( nextSymbolList[0] == null ) {
				lexer.next();
				nextSymbolList[0] = lexer.symbol;
			}
			return nextSymbolList[0];
		case 1:
			if ( nextSymbolList[0] == null ) {
				lexer.next();
				nextSymbolList[0] = lexer.symbol;
			}
			if ( nextSymbolList[1] == null ) {
				lexer.next();
				nextSymbolList[1] = lexer.symbol;
			}
			return nextSymbolList[1];
		case 2:
			if ( nextSymbolList[0] == null ) {
				lexer.next();
				nextSymbolList[0] = lexer.symbol;
			}
			if ( nextSymbolList[1] == null ) {
				lexer.next();
				nextSymbolList[1] = lexer.symbol;
			}
			if ( nextSymbolList[2] == null ) {
				lexer.next();
				nextSymbolList[2] = lexer.symbol;
			}
			return nextSymbolList[2];
		default:
			error(true, symbol,
					"Internal error at Compiler::next(int): n is " + n, null,
					ErrorKind.internal_error);
			return null;
		}
		// redo

		/*
		 * if ( isa + n < sizeSymbolArray ) return symbolArray[isa + n]; else
		 * return null;
		 */
	}

	private void getLeftCharSequence() {
		try {
			lexer.getLeftCharSequence();
		}
		catch (final CompileErrorException e) {
			lexer.next();
			throw e;
		}
		finally {
			symbol = lexer.symbol;
		}
	}

	// redo
	/**
	 * returns the symbol that precedes the current one
	 *
	 * @return
	 *
	 *         private Symbol previousSymbol() { if ( isa > 1 ) return
	 *         symbolArray[isa - 2]; else return symbolArray[isa]; }
	 *
	 */

	/**
	 * insert a symbol at the current position of the array of symbols produced
	 * by the lexer
	 */
	private void insertSymbol(Symbol s) {
		nextSymbolList[2] = nextSymbolList[1];
		nextSymbolList[1] = nextSymbolList[0];
		nextSymbolList[0] = s;
	}

	public void parseProject(Project newProject, Program program,
			CompilationUnitSuper projectCompilationUnit,
			String projectFilename2, String canonicalPath, char[] text,
			String cyanLangDir) {
		/*
		 * public CompilationUnit(String filename, String packageCanonicalPath,
		 * CompilerOptions compilerOptions, CyanPackage cyanPackage) {
		 *
		 */
		// CompilationUnit fakeCompilationUnit = new
		// CompilationUnit(projectFilename2, canonicalPath, compilerOptions,
		// null);

		lineShift = 0;
		this.projectFilename = projectFilename2;
		lexer = new Lexer(text, projectCompilationUnit, compInstSet, this);
		try {
			parseProject(newProject, program, cyanLangDir);
			newProject.setProjectDir(canonicalPath);
		}
		catch (final CompileErrorException e) {
		}
	}

	private void dpp_action(AnnotationAt annotation, Compiler_dpp compiler_dpp,
			Compiler compiler, CyanPackage cyanPackage) {
		final CyanMetaobjectAtAnnot cyanMetaobject = annotation
				.getCyanMetaobject();

		_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();

		if ( cyanMetaobject instanceof IAction_dpp || (other != null
				&& other instanceof cyan.reflect._IAction__dpp) ) {
			if ( !this.compInstSet
					.contains(CompilationInstruction.pyanSourceCode) ) {
				this.error2(annotation.getFirstSymbol(),
						"This metaobject can only be used inside a project file, one with '.pyan' extension");
			}
			final IAction_dpp icp = (IAction_dpp) cyanMetaobject;

			try {
				int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(
						compiler, compiler.getProject().getProgram(),
						cyanPackage, annotation.getFirstSymbol());

				Timeout<Object> to = new Timeout<>();

				if ( other == null ) {

					to.run( // Executors.callable(
							() -> {
								icp.dpp_action(compiler_dpp);
								return null;
							}, timeoutMilliseconds, "dpp_action",
							annotation.getCyanMetaobject(), project);
					// icp.dpp_action(compiler_dpp);
				}
				else {
					to.run(Executors.callable(() -> {
						((_IAction__dpp) other)._dpp__action_1(compiler_dpp);
					}), timeoutMilliseconds, "dpp_action",
							annotation.getCyanMetaobject(), project);
					// ((_IAction__dpp ) other)._dpp__action_1(compiler_dpp);
				}
			}
			catch (final error.CompileErrorException e) {
			}
			catch (final NoClassDefFoundError e) {
				error2(annotation.getFirstSymbol(), e.getMessage() + " "
						+ NameServer.messageClassNotFoundException);
			}
			catch (final RuntimeException e) {
				thrownException(annotation, annotation.getFirstSymbol(), e);
			}
			catch (final Throwable et) {
				thrownException(annotation, annotation.getFirstSymbol(), et);

			}
			finally {
				this.metaobjectError(cyanMetaobject, annotation);
			}

		}
		else {
			// it may be an annotation of a metaobject that can be attached to a
			// prototype
			if ( !cyanMetaobject
					.mayBeAttachedTo(AttachedDeclarationKind.PROTOTYPE_DEC) ) {
				this.error2(annotation.getFirstSymbol(),
						"The metaobject class of annotation '"
								+ cyanMetaobject.getName()
								+ "' should either implement interface '"
								+ IAction_dpp.class.getName()
								+ "' or it should be legal to attach the annotation"
								+ " to a prototype");
			}
		}

	}

	/**
	 * Program ::= { ImportList } [ CTmetaobjectAnnotationList ] “program” [
	 * AtFolder ] [ “main” QualifId ] { CTmetaobjectAnnotationList Package }
	 * ImportList ::= “import” QualifId AtFolder Package ::= “package” QualifId
	 * [ AtFolder ] AtFolder ::= “at” FileName CTmetaobjectAnnotationList ::= {
	 * annotation } annotation ::= “@” Id [ “(” ExprLiteral [ “,” ExprLiteral ]
	 * “)” ] [ LeftCharString TEXT RightCharString ] QualifId ::= { Id “.” } Id
	 *
	 *
	 */
	public void parseProject(Project newProject, Program newProgram,
			String cyanLangDir) {

		final String fileSeparator = System.getProperty("file.separator");
		String projectPath = newProject.getProjectCanonicalPath();
		if ( !projectPath.endsWith(fileSeparator) ) {
			projectPath = projectPath + fileSeparator;
		}

		compInstSet = new HashSet<>();
		compInstSet.add(CompilationInstruction.parsing_actions);
		compInstSet.add(CompilationInstruction.pyanSourceCode);

		final List<String> importList = new ArrayList<>();

		project = newProject;

		/**
		 * load metaobjects from package cyan.lang
		 */
		final List<CyanMetaobject> cyanLangMetaobjectList = new ArrayList<>();
		String fullCyanLangDir = cyanLangDir;
		if ( cyanLangDir.endsWith(fileSeparator) )
			fullCyanLangDir += MetaHelper.cyanLanguagePackageDirectory;
		else
			fullCyanLangDir += fileSeparator
					+ MetaHelper.cyanLanguagePackageDirectory;

		// # add metaobjects of cyan.lang
		project.getProgram().setProgramKeyValue("cyanLangDir", cyanLangDir);
		project.getProgram().setProgramKeyValue("fullCyanLangDir",
				fullCyanLangDir);

		try {
			CompilerManager.loadMetaobjectsFromPackage(fullCyanLangDir,
					MetaHelper.cyanLanguagePackageName, cyanLangMetaobjectList,
					this);
		}
		catch (final RuntimeException e) {
			// e.printStackTrace();
			for (final UnitError error : this.compilationUnit.getErrorList()) {
				project.error(error.getMessage());
			}
			return;
		}

		// read the metaobjects of cyan.lang just one time.
		// get rid of Program.setCyanMetaobjectTable

		compilationUnit.loadCyanMetaobjects(cyanLangMetaobjectList, symbol,
				this);
		lexer.addMetaObjectLiteralObjectSeqTable(
				compilationUnit.getMetaObjectLiteralObjectSeqTable());
		lexer.addMetaobjectLiteralNumberTable(
				compilationUnit.getMetaObjectLiteralNumber());
		lexer.addMetaobjectLiteralStringTable(
				compilationUnit.getMetaobjectLiteralObjectString());

		if ( symbol.token == Token.IMPORT ) {
			// importPackagePyan(fileSeparator);
			this.importPackagePyan(fileSeparator,
					(String packageName2, String atPackage2) -> {
						project.addNamePackageImportList(packageName2);
						project.addPathPackageImportList(atPackage2);
					}, (List<CyanMetaobject> metaobjectList2,
							Symbol importSymbol2) -> {
						compilationUnit.loadCyanMetaobjects(metaobjectList2,
								importSymbol2, this);
					}

			);

		}

		/*
		 * the metaobject annotations may or may not be attached to the program
		 */

		List<AnnotationAt> attachedAnnotationList = new ArrayList<>();
		List<AnnotationAt> nonAttachedAnnotationList = null;

		Tuple2<List<AnnotationAt>, List<AnnotationAt>> tc = parseAnnotations_NonAttached_Attached();
		if ( tc != null ) {
			nonAttachedAnnotationList = tc.f1;
			attachedAnnotationList = tc.f2;
			if ( nonAttachedAnnotationList != null
					&& nonAttachedAnnotationList.size() > 0 ) {
				/*
				 * there could be no annotations before 'program' that are not
				 * linked to it
				 */
				project.error("The metaobject of the annotation '"
						+ nonAttachedAnnotationList.get(0).getCompleteName()
						+ "' should be attached to 'program'");

			}
		}

		String atProgram = null;
		if ( symbol.token != Token.IDENT
				&& !symbol.getSymbolString().equals("program") )
			error(true, symbol, "'program' expected. Found '"
					+ symbol.getSymbolString() + "'", "",
					ErrorKind.keyword_program_expected);
		else
			next();

		if ( attachedAnnotationList != null ) {
			for (final AnnotationAt annotation : attachedAnnotationList) {
				final CyanMetaobjectAtAnnot cyanMetaobject = annotation
						.getCyanMetaobject();
				if ( !cyanMetaobject
						.mayBeAttachedTo(AttachedDeclarationKind.PROGRAM_DEC)
						&& !cyanMetaobject.mayBeAttachedTo(
								AttachedDeclarationKind.PROTOTYPE_DEC) ) {
					this.error(true, annotation.getSymbolAnnotation(),
							"This metaobject annotation cannot be attached to a program. It can be attached to "
									+ " one entity of the following list: [ "
									+ cyanMetaobject.attachedListAsString()
									+ " ]",
							null, ErrorKind.metaobject_error);

				}
				annotation.setDeclaration(newProgram.getI_dpp());
				annotation.setOriginalDeclaration(
						AttachedDeclarationKind.PROGRAM_DEC);
				// if ( cyanMetaobject instanceof ICompilerInfo_parsing ) {
				// final ICompilerInfo_parsing moInfo = (ICompilerInfo_parsing)
				// cyanMetaobject;
				// final List<Tuple2<String, WrExprAnyLiteral>> t = moInfo
				// .featureListToDeclaration();
				// if ( t != null ) program.addFeatureList(t);
				// moInfo.action_parsing(getCompilerAction_parsing());
				// }
			}
		}
		newProgram.setAttachedAnnotationList(attachedAnnotationList);

		Symbol atSymbol = null;
		if ( symbol.token == Token.IDENT
				&& symbol.getSymbolString().equals("at") ) {
			atSymbol = symbol;
			next();
			if ( symbol.token != Token.LITERALSTRING ) error(true, symbol,
					"A literal string with a directory (folder) was expected."
							+ foundSuch(),
					"", ErrorKind.literal_string_expected);
			atProgram = symbol.getSymbolString();
			next();
		}
		String projectDir;
		if ( atProgram != null ) {
			final File f = new File(atProgram);
			if ( !f.exists() ) {
				error(true, atSymbol, "File '" + atProgram + "' does not exist",
						"", ErrorKind.file_does_not_exist,
						"filename = " + atProgram);
			}
			if ( f.isDirectory() ) {
				projectDir = atProgram;
				if ( !projectDir.endsWith(fileSeparator) )
					projectDir += fileSeparator;
			}
			else {

				project.error("In the project, file '" + atProgram
						+ "' should be a directory");
				return;
			}
		}
		else {
			projectDir = this.compilationUnitSuper.getCanonicalPathUpDir();
		}

		String mainObject;
		String mainPackage;

		if ( symbol.token == Token.IDENT
				&& symbol.getSymbolString().equals("main") ) {
			next();
			final Symbol mainSymbol = symbol;
			final ExprIdentStar mainPrototype = ident();
			final String s = mainPrototype.asString();
			final int indexLastDot = s.lastIndexOf('.');
			if ( indexLastDot < 1 || indexLastDot == s.length() - 1 ) {
				this.error2(mainSymbol,
						"After 'main' it was expected the complete name of the main prototype, with the package, as in 'br.main.Program'");
			}
			mainPackage = s.substring(0, indexLastDot);
			mainObject = s.substring(indexLastDot + 1);
		}
		else {
			mainObject = "Program";
			mainPackage = "main";
		}

		if ( attachedAnnotationList != null
				&& attachedAnnotationList.size() > 0 ) {
			Compiler_dpp compiler_dpp = new Compiler_dpp(project);

			for (final AnnotationAt annotation : attachedAnnotationList) {
				dpp_action(annotation, compiler_dpp, this, null);
			}
		}

		// Program fakeProgram = new Program();
		/*
		 * Project project = new Project( program, mainPackage, mainObject,
		 * null, null, null, nonAttachedAnnotationList, attachedAnnotationList,
		 * importList);
		 *
		 */
		project.setMainPackage(mainPackage);
		project.setMainObject(mainObject);
		project.setImportList(importList);

		project.setCompilerOptions(null);

		while (symbol.token == Token.PACKAGE
				|| symbol.token == Token.METAOBJECT_ANNOTATION
				|| symbol.token == Token.IMPORT) {

			final List<String> packageNamePackageImportList = new ArrayList<>();
			final List<String> packagePathPackageImportList = new ArrayList<>();
			final List<CyanMetaobject> pyanBeforePackageMetaobjectList = new ArrayList<>();
			// List<CyanMetaobject> pyanBeforePackageMetaobjectList
			/*
			 * the tables are restored to their original values after this
			 * package
			 */
			final HashMap<String, CyanMetaobjectMacro> metaObjectMacroTable1 = this.compilationUnit
					.getMetaObjectMacroTable();
			final HashMap<String, CyanMetaobjectAtAnnot> metaObjectTable = this.compilationUnit
					.getMetaObjectTable();

			if ( symbol.token == Token.IMPORT ) {

				this.importPackagePyan(fileSeparator,
						(String packageName2, String atPackage2) -> {
							packageNamePackageImportList.add(packageName2);
							packagePathPackageImportList.add(atPackage2);
						}, (List<CyanMetaobject> metaobjectList2,
								Symbol importSymbol2) -> {
							pyanBeforePackageMetaobjectList
									.addAll(metaobjectList2);

							compilationUnit.loadCyanMetaobjects(metaobjectList2,
									importSymbol2, this);

						}

				);

			}

			/*
			 * the metaobject annotations may or may not be attached to the
			 * package
			 */

			attachedAnnotationList = null;
			nonAttachedAnnotationList = null;

			tc = parseAnnotations_NonAttached_Attached();
			if ( tc != null ) {
				nonAttachedAnnotationList = tc.f1;
				attachedAnnotationList = tc.f2;
				if ( nonAttachedAnnotationList != null
						&& nonAttachedAnnotationList.size() > 0 ) {
					/*
					 * there could be no annotations before 'package' that are
					 * not linked to it
					 */
					project.error("The metaobject of the annotation '"
							+ nonAttachedAnnotationList.get(0).getCompleteName()
							+ "' should be attached to a package");

				}
			}

			if ( symbol.token != Token.PACKAGE )
				error(true, symbol, "'package' expected." + foundSuch(), "",
						ErrorKind.keyword_package_expected);
			else
				next();

			final ExprIdentStar packageId = ident();
			final String packageName = packageId.asString();
			String s2 = "";
			final int size = packageName.length();
			char ch;
			for (int i = 0; i < size; ++i)
				s2 = s2 + ((ch = packageName.charAt(i)) == '.' ? fileSeparator
						: "" + ch);
			String atPackage;
			if ( packageName.startsWith("cyan.") ) {
				atPackage = cyanLangDir + fileSeparator + s2;
			}
			else {
				atPackage = projectDir + s2;

			}
			if ( symbol.token == Token.IDENT
					&& symbol.getSymbolString().equals("at") ) {
				next();
				if ( symbol.token != Token.LITERALSTRING ) error(true, symbol,
						"A literal string with a directory (folder) was expected."
								+ foundSuch(),
						"", ErrorKind.literal_string_expected);
				atPackage = symbol.getSymbolString();
				if ( MyFile.isRelativePath(atPackage) ) {
					atPackage = projectPath + atPackage;
				}
				next();
			}
			final String packageCanonicalPath = atPackage + fileSeparator; // C:\Dropbox\Cyan\lib\
																			// packageName:
																			// "cyan.util"
			/*
			 * if packageName is "cyan.util", npn is "cyan\\util"
			 */

			if ( packageName
					.equalsIgnoreCase(MetaHelper.cyanLanguagePackageName) ) {
				error2(packageId.getFirstSymbol(),
						"Package cyan.lang cannot be specified in the project file");
			}

			if ( packageCanonicalPath.contains(NameServer.temporaryDirName) ) {
				error(true, symbol, "package '" + packageName
						+ "' is in directory '" + packageCanonicalPath
						+ "' which is illegal because it has the string '"
						+ NameServer.temporaryDirName + "' in it. "
						+ "This may occur because the compiler was not able to delete the temporary directory of the previous compilation. Delete"
						+ " it yourself", "", ErrorKind.file_error);
			}

			// **************************

			final List<CyanMetaobject> metaobjectList = new ArrayList<>();
			if ( !loadMetaobjectsFromPackage(packageName, packageCanonicalPath,
					metaobjectList) ) {
				return;
			}

			// try {
			// CompilerManager.loadMetaobjectsFromPackage(packageCanonicalPath,
			// packageName, metaobjectList, this);
			// }
			// catch (final RuntimeException e) {
			// // e.printStackTrace();
			// for (final UnitError error : this.compilationUnit
			// .getErrorList()) {
			// project.error(error.getMessage());
			// }
			// return;
			// }
			// **************************

			final CyanPackage cyanPackage = createCyanPackage(newProgram,
					project, attachedAnnotationList, packageName,
					packageCanonicalPath, metaobjectList,
					pyanBeforePackageMetaobjectList);

			if ( project.searchPackage(packageName) != null )
				project.error("Package '" + packageName
						+ "' is duplicated in this project");

			project.addCyanPackage(cyanPackage);

			if ( attachedAnnotationList != null
					&& attachedAnnotationList.size() > 0 ) {
				Compiler_dpp compiler_dpp = new Compiler_dpp(project);
				for (final AnnotationAt annotation : attachedAnnotationList) {
					final CyanMetaobjectAtAnnot cyanMetaobject = annotation
							.getCyanMetaobject();
					if ( !cyanMetaobject.mayBeAttachedTo(
							AttachedDeclarationKind.PACKAGE_DEC)
							&& !cyanMetaobject.mayBeAttachedTo(
									AttachedDeclarationKind.PROTOTYPE_DEC) ) {
						this.error(true, annotation.getSymbolAnnotation(),
								"This metaobject annotation cannot be attached to a package. It can be attached to "
										+ " one entity of the following list: [ "
										+ cyanMetaobject.attachedListAsString()
										+ " ]",
								null, ErrorKind.metaobject_error);

					}
					else {
						annotation.setDeclaration(cyanPackage.get_dpp());
						annotation.setOriginalDeclaration(
								AttachedDeclarationKind.PACKAGE_DEC);
						dpp_action(annotation, compiler_dpp, this, cyanPackage);
					}
				}
			}

			/**
			 * restore the metaobject tables to the values of the Program,
			 * eliminating any metaobjects specific to this package
			 */
			compilationUnit.setMetaObjectMacroTable(metaObjectMacroTable1);
			compilationUnit.setMetaObjectTable(metaObjectTable);

			/*
			 * if ( cyanPackage.getCompilationUnitList().size() > 0 ) { //
			 * directories without .cyan files are not considered packages }
			 */
		}

		/*
		 * add packages of projectDir to the project. But only those that have
		 * not been added explicitly in the .pyan file
		 */
		final List<String> projPath = new ArrayList<String>();
		String projectDirWithoutSlash = projectDir;
		if ( projectDir.endsWith(fileSeparator) )
			projectDirWithoutSlash = projectDir.substring(0,
					projectDir.length() - 1);
		projPath.add(projectDirWithoutSlash);

		final List<String> projCyanName = new ArrayList<String>();
		projCyanName.add("");
		final String strError = Saci.getAllProjects(projPath, projCyanName, 0);
		if ( strError != null ) {
			project.error(strError);
		}
		projPath.remove(0); //
		projCyanName.remove(0);

		for (final String packageName : projCyanName) {

			if ( !packageName.contains(MetaHelper.prefixNonPackageDir)
					&& !packageName.endsWith(NameServer.temporaryDirName)
					&& project.searchPackage(packageName) == null ) {
				String packageCanonicalPath = projectDir; // C:\Dropbox\Cyan\lib\
															// packageName:
															// "cyan.util"

				final String npn = packageName.replace(".", fileSeparator)
						+ fileSeparator;
				if ( !packageCanonicalPath.endsWith(npn) )
					packageCanonicalPath += npn;

				if ( packageName.equalsIgnoreCase(
						MetaHelper.cyanLanguagePackageName) ) {
					project.error(
							"Package cyan.lang cannot be specified in the project file. It is in '"
									+ packageCanonicalPath + "'");
				}

				// **************************

				final List<CyanMetaobject> metaobjectList = new ArrayList<>();

				// **************************

				final CyanPackage cyanPackage = createCyanPackage(newProgram,
						project, null, packageName, packageCanonicalPath, null, // metaobjectList,
						null);

				try {
					String packageCanonicalPathFinal = packageCanonicalPath;
					cyanPackage.addPackageMetaToClassPath_and_Run(() -> {
						CompilerManager.loadMetaobjectsFromPackage(
								packageCanonicalPathFinal, packageName,
								metaobjectList, this);
					});
					cyanPackage.calculateURLs();
				}
				catch (final RuntimeException e) {
					// e.printStackTrace();
					for (final UnitError error : this.compilationUnit
							.getErrorList()) {
						project.error(error.getMessage());
					}
					return;
				}
				cyanPackage.setMetaobjectList(metaobjectList);

				// if ( cyanPackage.getCompilationUnitList().size() > 0 ) {
				// directories without .cyan files are not considered packages
				project.addCyanPackage(cyanPackage);
				// }

				// project.addCyanPackage(cyanPackage);

			}

		}

		if ( symbol.token != Token.EOF ) {
			this.error2(symbol,
					"Unidentified symbol: '" + symbol.getSymbolString() + "'");
		}
		// String fullCyanLangDir = cyanLangDir + fileSeparator +
		// NameServer.cyanLanguagePackageDirectory;

		final CyanPackage cyanLangPackage = createCyanPackage(newProgram,
				project, new ArrayList<AnnotationAt>(),
				MetaHelper.cyanLanguagePackageName, fullCyanLangDir,
				cyanLangMetaobjectList, null);

		project.addCyanPackage(cyanLangPackage);
		project.setCyanLangPackage(cyanLangPackage);
		newProgram.setCyanLangPackage(cyanLangPackage);

	}

	public boolean loadMetaobjectsFromPackage(String packageName,
			String packageCanonicalPath,
			final List<CyanMetaobject> metaobjectList) {
		// final List<CyanMetaobject> metaobjectList = new ArrayList<>();

		try {
			CompilerManager.loadMetaobjectsFromPackage(packageCanonicalPath,
					packageName, metaobjectList, this);
		}
		catch (final RuntimeException e) {
			for (final UnitError error : this.compilationUnit.getErrorList()) {
				project.error(error.getMessage());
			}
			return false;
		}
		return true;
	}

	private void importPackagePyan(String fileSeparator,
			Function2<String, String> nameAt,
			Function2<List<CyanMetaobject>, Symbol> moListSymbol) {
		List<CyanMetaobject> metaobjectList;
		while (symbol.token == Token.IMPORT) {
			next();
			final Symbol importSymbol = symbol;
			final ExprIdentStar packageId = ident();
			final String packageName = packageId.asString();
			String s2 = "";
			final int size = packageName.length();
			char ch;
			for (int i = 0; i < size; ++i)
				s2 = s2 + ((ch = packageName.charAt(i)) == '.' ? fileSeparator
						: "" + ch);
			String atPackage = null;
			if ( symbol.token == Token.IDENT
					&& symbol.getSymbolString().equals("at") ) {
				next();
				if ( symbol.token != Token.LITERALSTRING ) error(true, symbol,
						"A literal string with a directory (folder) was expected."
								+ foundSuch(),
						"", ErrorKind.literal_string_expected);
				atPackage = symbol.getSymbolString();
				if ( atPackage.contains(NameServer.metaobjectPackageName) ) {
					this.error2(symbol,
							"The directory of a package should not include '"
									+ NameServer.metaobjectPackageName + "'");
				}
				next();
				nameAt.eval(packageName, atPackage);

				/*
				 * project.addNamePackageImportList(packageName);
				 * project.addPathPackageImportList(atPackage);
				 */
				/*
				 * load metaobjects from the HDD
				 */
				metaobjectList = new ArrayList<>();
				try {
					CompilerManager.loadMetaobjectsFromPackage(atPackage,
							packageName, metaobjectList, this);
				}
				catch (final RuntimeException e) {
					// e.printStackTrace();
					throw e;
				}

				/*
				 * load metaobjects to this compilation unit
				 */
				moListSymbol.eval(metaobjectList, importSymbol);

				/*
				 * compilationUnit.loadCyanMetaobjects(metaobjectList,
				 * importSymbol, this);
				 * lexer.addMetaObjectLiteralObjectSeqTable(compilationUnit.
				 * getMetaObjectLiteralObjectSeqTable());
				 * lexer.addMetaobjectLiteralNumberTable(compilationUnit.
				 * getMetaObjectLiteralNumber());
				 * lexer.addMetaobjectLiteralStringTable(compilationUnit.
				 * getMetaobjectLiteralObjectString());
				 */

			}
			else {
				this.error2(symbol, "'at' expected");
			}
		}
	}

	/**
	 * Create a Cyan package of program <code>program</code> and project
	 * <code>project1</code>. The annotations attached to this package in the
	 * project file are <code>attachedAnnotationList</code>. The package name is
	 * <code>packageName</code> of directory <code>packageCanonicalPath</code>.
	 * The metaobjects of this package, avaliable in Cyan source code when the
	 * package is imported, are in the list <code>metaobjectList</code>.
	 * <code>pyanBeforePackageMetaobjectList</code> is a list of metaobjects
	 * given in import statements just before the package declaration in the
	 * project file:<br>
	 * <code>
	 *     import lang.python<br>
	 *     package main <br>
	 * </code>
	 *
	 * All the source files ending with <code>.cyan</code> of the directory are
	 * included in the package.
	 *
	 * @param program1
	 * @param fileSeparator
	 * @param project1
	 * @param packageName
	 * @param packageCanonicalPath
	 * @return
	 */
	private CyanPackage createCyanPackage(Program program1, Project project1,
			List<AnnotationAt> attachedAnnotationList, String packageName,
			String packageCanonicalPath, List<CyanMetaobject> metaobjectList,
			List<CyanMetaobject> pyanBeforePackageMetaobjectList) {

		final String fileSeparator = NameServer.fileSeparatorAsString;

		/*
		 * if ( packageCanonicalPath.contains(NameServer.prefixNonPackageDir) )
		 * { error2(symbol, "A package directory cannot contain '" +
		 * NameServer.prefixNonPackageDir + "'"); }
		 */

		final CyanPackage cyanPackage = new CyanPackage(program1, packageName,
				project1, packageCanonicalPath, attachedAnnotationList,
				metaobjectList, pyanBeforePackageMetaobjectList);
		CollectError ce = cyanPackage.calculateURLs();

		if ( ce != null ) {
			project1.error("Error when discovering the jar files of directory '"
					+ packageCanonicalPath + File.separator
					+ NameServer.metaobjectPackageName + "' of package '"
					+ packageName + "'");
		}

		final File possiblePackageDir = new File(
				(packageCanonicalPath.endsWith(fileSeparator)
						? packageCanonicalPath
						: packageCanonicalPath + fileSeparator)
						+ packageName.replace('.', fileSeparator.charAt(0)));
		if ( possiblePackageDir.exists() && possiblePackageDir.isDirectory() ) {
			try {
				this.error2(symbol, "To package '" + packageName
						+ "' was associated in the .pyan file a wrong directory. "
						+ "It should be '"
						+ possiblePackageDir.getCanonicalPath().toString()
						+ "'");
			}
			catch (final IOException e) {
			}
		}

		/*
		 * List<CyanMetaobject> metaobjectList = new ArrayList<>();
		 *
		 * CompilerManager.loadMetaobjectsFromPackage(packageCanonicalPath,
		 * packageName, metaobjectList, compiler);
		 * cyanPackage.setMetaobjectList(metaobjectList);
		 */

		final File packageDir = new File(packageCanonicalPath);
		if ( !packageDir.exists() ) {
			error(true, symbol,
					"Directory '" + packageCanonicalPath
							+ "' cited in project '" + this.projectFilename
							+ "' does not exist",
					"", ErrorKind.file_does_not_exist,
					"filename = " + packageCanonicalPath);
		}
		if ( !packageDir.isDirectory() )
			error(true, symbol, "File '" + packageCanonicalPath
					+ "' cited in project '" + this.projectFilename
					+ "' is not a directory. It should be a package directory",
					"", ErrorKind.file_should_be_directory,
					"filename = " + packageCanonicalPath);

		List<String> filesPackage = new ArrayList<String>();

		/**
		 * first load all files from directory --dsl with the DSL code
		 */
		String packageCanonicalPathWithSlash = packageCanonicalPath;
		if ( !packageCanonicalPathWithSlash.endsWith(fileSeparator) ) {
			packageCanonicalPathWithSlash += fileSeparator;
		}
		final File dslPackageDir = new File(
				packageCanonicalPath + NameServer.directoryNamePackageDSL);
		if ( dslPackageDir.exists() ) {
			if ( !dslPackageDir.isDirectory() ) {
				error2(symbol, "File '" + dslPackageDir
						+ "' should be a directory. But it is not");
			}
			for (final String dslFilename : dslPackageDir.list()) {

				// it is considered a DSL if the extension do not start with ~
				// or has 'bak' in the name
				boolean isBackup = false;
				for (final String extension : NameServer.backupExtensionList) {
					if ( dslFilename.endsWith(extension) ) {
						isBackup = true;
						break;
					}
				}
				if ( !isBackup ) {

					final String pathSourceFile = packageDir + fileSeparator
							+ dslFilename;
					final File f = new File(pathSourceFile);

					if ( !f.isDirectory() ) {

						final int indexOfPoint = dslFilename.lastIndexOf(".");
						if ( indexOfPoint > 0 ) {

							final CompilationUnitDSL compilationUnitDSL = new CompilationUnitDSL(
									dslFilename, packageCanonicalPath,
									cyanPackage);
							cyanPackage
									.addCompilationUnitDSL(compilationUnitDSL);
						}

					}

				}

			}
		}

		filesPackage = new ArrayList<String>();

		final String sourceFileList[] = packageDir.list();

		for (final String sourceFilename : sourceFileList) {
			String pathSourceFile = packageDir + fileSeparator + sourceFilename;
			final File f = new File(pathSourceFile);

			if ( sourceFilename.endsWith(MetaHelper.dotCyanSourceFileExtension)
					&& !f.isDirectory() ) {
				filesPackage.add(sourceFilename);

				int indexOfPoint = pathSourceFile.lastIndexOf(".");
				if ( indexOfPoint < 0 ) {
					pathSourceFile = pathSourceFile + ".cyan";
					indexOfPoint = pathSourceFile.lastIndexOf(".");
				}

				final CompilationUnit compilationUnit3 = new CompilationUnit(
						sourceFilename, packageCanonicalPath, null,
						cyanPackage);
				cyanPackage.addCompilationUnit(compilationUnit3);

				int lastIndexBar = pathSourceFile.lastIndexOf(fileSeparator);
				if ( lastIndexBar < 0 ) lastIndexBar = -1;
				String objectOrInterfaceName = "";
				if ( indexOfPoint > 0 )
					objectOrInterfaceName = pathSourceFile
							.substring(lastIndexBar + 1, indexOfPoint);
				else
					error(true, symbol,
							"Filename " + pathSourceFile
									+ " does not have '.cyan' extension",
							"", ErrorKind.file_does_not_have_cyan_extension,
							"filename = " + pathSourceFile);
				compilationUnit3
						.setObjectInterfaceName(cyanPackage.getPackageName()
								+ "." + objectOrInterfaceName);

			}

		}

		return cyanPackage;
	}

	private boolean checkIf(Token t, String name) {

		if ( symbol.token == t )
			return true;
		else {
			if ( symbol.getSymbolString().length() > 1
					&& symbol instanceof SymbolKeyword ) {
				if ( computeLevenshteinDistance(name,
						symbol.getSymbolString()) >= 0.5 ) {

					if ( ask(symbol, "'" + symbol.getSymbolString()
							+ "' seems to be mistyped. Can I change it to '"
							+ name + "' ? (y, n)") ) {
						compilationUnit
								.addAction(new ActionDelete(compilationUnit,
										symbol.startOffsetLine
												+ symbol.getColumnNumber() - 1,
										symbol.getSymbolString().length(),
										symbol.getLineNumber(),
										symbol.getColumnNumber()));

						symbol = ((SymbolKeyword) symbol).newObject(t, name);

						compilationUnit.addAction(
								new ActionInsert(name, compilationUnit,
										symbol.startOffsetLine
												+ symbol.getColumnNumber() - 1,
										symbol.getLineNumber(),
										symbol.getColumnNumber()));
						return true;
					}
				}

			}
			return false;
		}
	}

	/**
	 * Parse the compilation unit
	 *
	 * CompilationUnit ::= PackageDec ImportDec { CTmetaobjectAnnotationList
	 * Prototype }
	 *
	 */

	public void parse() {

		lineShift = 0;

		currentPrototype = null;

		final CyanPackage importedPackage = this.project.getCyanLangPackage();

		compilationUnit.setHasGenericPrototype(false);
		compilationUnit.loadCyanMetaobjects(importedPackage.getMetaobjectList(),
				symbol, this);

		lexer.addMetaObjectLiteralObjectSeqTable(
				compilationUnit.getMetaObjectLiteralObjectSeqTable());
		lexer.addMetaobjectLiteralNumberTable(
				compilationUnit.getMetaObjectLiteralNumber());
		// lexer.setMetaObjectLiteralObjectIdentSeqTable(compilationUnit.getMetaObjectLiteralObjectIdentSeqTable());
		lexer.addMetaobjectLiteralStringTable(
				compilationUnit.getMetaobjectLiteralObjectString());

		try {
			ExprIdentStar packageIdent = null;
			if ( symbol.token == Token.METAOBJECT_ANNOTATION ) {
				final Tuple2<List<AnnotationAt>, List<AnnotationAt>> tctmo = this
						.parseAnnotations_NonAttached_Attached();
				if ( tctmo != null ) {
					final List<AnnotationAt> nonAttachedAnnotationList = tctmo.f1;
					final List<AnnotationAt> attachedAnnotationList = tctmo.f2;
					if ( attachedAnnotationList != null
							&& attachedAnnotationList.size() > 0 ) {
						for (final AnnotationAt annotation : attachedAnnotationList) {
							this.error2(annotation.getFirstSymbol(),
									"Metaobject '"
											+ annotation.getCyanMetaobject()
													.getName()
											+ "' should be attached to a declaration. It cannot appear before 'package'");
						}
					}
					this.compilationUnit
							.setNonAttachedAnnotationListBeforePackage(
									nonAttachedAnnotationList);
				}

			}

			if ( symbol.token != Token.PACKAGE ) {
				error(true, symbol, "keyword 'package' expected." + foundSuch(),
						symbol.getSymbolString(),
						ErrorKind.keyword_package_expected);
			}
			next();
			if ( symbol.token != Token.IDENT ) error(true, symbol,
					"package name expected." + foundSuch(),
					symbol.getSymbolString(), ErrorKind.package_name_expected);

			final Symbol packageSymbol = symbol;

			final int ch = packageSymbol.getSymbolString().charAt(0);
			// if ( !
			// Character.isLowerCase(packageSymbol.getSymbolString().charAt(0))
			// )
			if ( ch < 'a' || ch > 'z' ) error(true, symbol,
					"The package name should start with a lower case letter ",
					symbol.getSymbolString(),
					ErrorKind.package_name_not_start_with_lower_case_letter);

			packageIdent = ident();

			if ( symbol.token == Token.COMMA ) {
				if ( ask(symbol, "'" + symbol.getSymbolString()
						+ "' is illegal here. Can I remove it? (y, n)") ) {
					final int sizeIdentSymbol = 1;
					compilationUnit.addAction(new ActionDelete(compilationUnit,
							symbol.startOffsetLine + symbol.getColumnNumber()
									- 1,
							sizeIdentSymbol, symbol.getLineNumber(),
							symbol.getColumnNumber()));
				}
			}
			if ( symbol.token == Token.SEMICOLON ) next();

			/**
			 * load metaobjects of this package
			 */
			final String packageName = compilationUnit.getCyanPackage()
					.getPackageName();
			if ( !packageName.equals(MetaHelper.cyanLanguagePackageName) ) {
				compilationUnit.loadCyanMetaobjects(
						compilationUnit.getCyanPackage().getMetaobjectList(),
						packageSymbol, this);

				lexer.addMetaObjectLiteralObjectSeqTable(
						compilationUnit.getMetaObjectLiteralObjectSeqTable());
				lexer.addMetaobjectLiteralNumberTable(
						compilationUnit.getMetaObjectLiteralNumber());
				// lexer.setMetaObjectLiteralObjectIdentSeqTable(compilationUnit.getMetaObjectLiteralObjectIdentSeqTable());
				lexer.addMetaobjectLiteralStringTable(
						compilationUnit.getMetaobjectLiteralObjectString());
			}

			final List<ExprIdentStar> importPackageList = importDecList();
			compilationUnit.setPackageIdent(packageIdent);
			compilationUnit.setImportPackageList(importPackageList);

			if ( !compilationUnit.getCyanPackage().getPackageName()
					.equals(packageIdent.getName()) ) {
				this.error(true, packageIdent.getFirstSymbol(),
						"Package name should be '" + packageName
								+ "'. Maybe you gave the wrong directory to compile. Maybe it should be a"
								+ " father or a child of '"
								+ this.getProject().getProjectCanonicalPath()
								+ "'",
						packageName, ErrorKind.package_has_a_wrong_name);
			}

			this.numPublicPackagePrototypes = 0;
			while (true) {

				if ( symbol.token != Token.EOF ) {

					try {
						currentPrototype = prototype();
						// prototypeArray.add(currentPrototype);
						// System.out.println("currentPrototype***" +
						// currentPrototype.getFullName());

					}
					catch (final CompileErrorException e) {
						/*
						 * skip to the end of prototype of to the end of the
						 * file
						 *
						 * while ( symbol.token != Token.EOF && symbol.token !=
						 * Token.END ) next(); if ( symbol.token == Token.END )
						 * next();
						 */
						break;
					}

				}
				else
					break;
			}

			if ( !this.parsingPackageInterfaces ) {
				/*
				 * the number of public prototypes, if the compiler really
				 * pointed out errors, etc should not be made if the compiler is
				 * parsing a ".iyan" file
				 */
				if ( !compilationUnit.hasCompilationError() ) {

					if ( this.numPublicPackagePrototypes == 0 ) error(true,
							currentPrototype != null
									? currentPrototype.getSymbol()
									: null,
							"This source file should declare at least a public prototype named "
									+ NameServer.fileNameToPrototypeName(
											compilationUnit.getFilename()),
							currentPrototype != null
									? currentPrototype.getName()
									: null,
							ErrorKind.no_public_protected_prototype_found_in_source_file,
							"identifier = \"" + (currentPrototype != null
									? currentPrototype.getName()
									: "") + "\"");

					// there should be exactly one public or protected prototype
					// in
					// the source file
					if ( this.numPublicPackagePrototypes != 1 ) {
						int n = 0, i = 0;
						while (i < compilationUnit.getPrototypeList().size()) {
							final Prototype pu = compilationUnit
									.getPrototypeList().get(i);
							if ( pu.getVisibility() == Token.PUBLIC
									|| pu.getVisibility() == Token.PACKAGE ) {
								++n;
								if ( n == 1 ) break;
							}
							++i;
						}
						error(true,
								compilationUnit.getPrototypeList().get(1)
										.getSymbol(),
								"There should be exactly one 'public' or 'package' prototype in every source file. This is the second one",
								compilationUnit.getPrototypeList().get(1)
										.getName(),
								ErrorKind.two_or_more_public_protected_prototype_found_in_source_file);
					}

					// If this compilation unit has a generic prototype, it
					// should
					// be the only one in the file.
					final boolean hasGenericPrototype = compilationUnit
							.getHasGenericPrototype();
					// if ( hasGenericPrototype ) {}

					for (final Prototype pu : compilationUnit
							.getPrototypeList()) {

						for (final Annotation annotation : pu
								.getCompleteAnnotationList()) {
							final CyanMetaobject cyanMetaobject = annotation
									.getCyanMetaobject();
							_CyanMetaobject other = cyanMetaobject
									.getMetaobjectInCyan();
							if ( cyanMetaobject instanceof IActionPrototypeLater_parsing
									|| (other != null
											&& other instanceof _IActionPrototypeLater__parsing) ) {

								// //
								// cyanMetaobject.setAnnotation(annotation,
								// 0);
								final CompilerGenericPrototype_parsing compilerGenericPrototype_parsing = new CompilerGenericPrototype_parsing(
										this, pu);

								try {
									if ( other == null ) {
										final IActionPrototypeLater_parsing iaction = (IActionPrototypeLater_parsing) cyanMetaobject;
										iaction.parsing_actionPrototypeLater(
												compilerGenericPrototype_parsing);
									}
									else {
										((_IActionPrototypeLater__parsing) other)
												._parsing__actionPrototypeLater_1(
														compilerGenericPrototype_parsing);
									}
								}
								catch (final error.CompileErrorException e) {
								}
								catch (final NoClassDefFoundError e) {
									error2(annotation.getFirstSymbol(), e
											.getMessage() + " "
											+ NameServer.messageClassNotFoundException);
								}
								catch (final RuntimeException e) {
									thrownException(annotation,
											annotation.getFirstSymbol(), e);
								}
								finally {
									this.metaobjectError(cyanMetaobject,
											annotation);
								}
							}
						}

					}

					if ( this.otherCodegList.size() > 0 ) {
						this.compilationUnitSuper
								.setOtherCodegList(otherCodegList);
					}
					/**
					 * check the codegs All codegs should have an identifier as
					 * the first parameter unless method demandsLabel() returns
					 * false
					 */
					checkCodegList();

				}
				int numFalse = 0;
				for (final Tuple3<Integer, String, Boolean> t : compilationUnit
						.getLineMessageList()) {
					if ( !t.f3 ) ++numFalse;
				}

				if ( numFalse != compilationUnit.getLineMessageList().size() ) {
					for (final Tuple3<Integer, String, Boolean> t : compilationUnit
							.getLineMessageList()) {
						if ( !t.f3 ) {
							try {
								error2(null,
										"A metaobject implementing interface 'IInformCompilationError' points an error at line "
												+ t.f1 + " with message '"
												+ t.f2
												+ "' although this error is not signaled by the compiler."
												+ " If the error really is in the source code, what happens was similar to the following: there is a parser error and "
												+ "a semantic error in this source code, each pointed by a metaobject. This error was caused by the semantic error. This error"
												+ " wound be pointed by the compiler in a later compiler phase. However, it will not because the compilation will stop because "
												+ "of the parsing error",
										false);
							}
							catch (final CompileErrorException e) {
							}
						}
					}

				}

			}
			this.compilationUnitSuper.setSymbolList(symbolList, sizeSymbolList);
		}
		catch (final CompileErrorException e) {
			// return prototypeArray;
		}

		// return prototypeArray;
	}

	/**

	 */
	private void checkCodegList() {
		if ( codegList.size() > 0 ) {
			for (final AnnotationAt annotation : codegList) {
				final List<WrExprAnyLiteral> paramList = annotation
						.getRealParameterList();
				/*
				 * A code should have exactly one parameter and this should be
				 * an identifier
				 */
				if ( paramList == null || paramList.size() < 1 ) {
					CyanMetaobject metaobject = annotation.getCyanMetaobject();
					_CyanMetaobject other = metaobject.getMetaobjectInCyan();
					boolean demandsLabel = false;
					if ( other == null ) {
						demandsLabel = ((ICodeg) metaobject).demandsLabel();
					}
					else {
						demandsLabel = ((_ICodeg) other)._demandsLabel().b;
					}
					if ( demandsLabel ) {
						error2(annotation.getFirstSymbol(),
								"This Codeg annotation should take at "
										+ "least one parameter that is an identifier. Method 'demandsLabel()' of "
										+ "the metaobject '"
										+ annotation.getCyanMetaobject()
												.getClass().getName()
										+ "' returned true");
						break;
					}
				}
			}
			/*
			 * A codeg stores information in a file during editing time. This
			 * information is retrieved now from the file which is stored in a
			 * directory that is in the same directory as the source file.
			 */
			if ( compInstSet
					.contains(CompilationInstruction.parsing_actions) ) {
				for (final AnnotationAt annotation : codegList) {
					/*
					 * read codeg information from files or from
					 * codegNameWithCodegFile
					 */
					if ( annotation.getFirstSymbol()
							.getLineNumber() != annotation.getLastSymbol()
									.getLineNumber() ) {
						this.error2(annotation.getFirstSymbol(),
								"The last symbol of a codeg annotation, "
										+ "usually ')', should be in the same line as the first symbol of the annotation, the codeg name");
					}
					final String codegPath = this.compilationUnit
							.getCanonicalPathUpDir()
							+ NameServer.getCodegDirFor(NameServer
									.prototypeNameToFileName(annotation
											.getPrototypeOfAnnotation()));

					final ExprAnyLiteral ea = meta.GetHiddenItem
							.getHiddenExprAnyLiteral(
									annotation.getRealParameterList().get(0));
					String id;
					if ( ea instanceof ExprAnyLiteralIdent ) {
						id = ((ExprAnyLiteralIdent) ea).getIdentExpr()
								.getName();
					}
					else {
						id = "unknown";
					}

					// String id = ((ExprAnyLiteralIdent )
					// annotation.getRealParameterList().get(0)).getIdentExpr().getName();
					String fileInfoExtension;
					CyanMetaobject metaobject = annotation.getCyanMetaobject();
					_CyanMetaobject other = metaobject.getMetaobjectInCyan();
					if ( other == null ) {
						fileInfoExtension = ((ICodeg) metaobject)
								.getFileInfoExtension();
					}
					else {
						fileInfoExtension = ((_ICodeg) other)
								._getFileInfoExtension().s;
					}
					final String codegInfoFilename = codegPath
							+ NameServer.fileSeparatorAsString
							+ annotation.getCyanMetaobject().getName() + "("
							+ id + ")." + fileInfoExtension;

					byte[] codegFileData = null;
					Tuple2<String, byte[]> foundTuple = null;
					final String codegCompleteName = annotation
							.getCompleteName();
					if ( codegNameWithCodegFile != null ) {
						for (final Tuple2<String, byte[]> t : this.codegNameWithCodegFile) {
							if ( codegCompleteName.equals(t.f1) ) {
								codegFileData = t.f2;
								foundTuple = t;
								/*
								 * this call is redundant (at least I think)
								 * because if the codeg annotation is found in
								 * codegNameWithCodegFile then the annotation
								 * has been previously set with codegFileText
								 */
								annotation.setCodegInfo(
										codegFileData == null ? "".getBytes()
												: codegFileData);
								break;
							}
						}
						/*
						 * next steps: if foundTuple is null, no codeg with name
						 * codegCompleteName was found in
						 * codegNameWithCodegFile. Then this is the first time
						 * {@link Saci#parseSingleSource} is called. Two things
						 * may have happened: (a) codegCompleteName may have
						 * just been added by the user during edition and the
						 * IDE has not called {@link Saci#eventCodegMenu}. The
						 * codeg menu has not been called. In this case the file
						 * should not be read from the HD. But we don´t know how
						 * to differentiate this from case (b); (b) this source
						 * file has been compiled before but the user changed
						 * the edition in the IDE to another source code and
						 * then returned to this. Then all codeg info has to be
						 * read from files again.
						 *
						 *
						 * if foundTuple is not null, {@link
						 * Saci#parseSingleSource} has been called before and:
						 * (a) this same piece of code has initialized
						 * codegNameWithCodegFile with a codeg named
						 * codegCompleteName (by reading info from a file ---
						 * see code below); (b) {@link Saci#eventCodegMenu} has
						 * been called (mouse over the codeg text) and it has
						 * initialized foundTuple.
						 *
						 * codegFileText may be null or "". Maybe the file has
						 * "" as content. Maybe the mouse has been over the
						 * codeg but the user gave no input. If foundTuple is
						 * not null, nothing should be done. The user, using the
						 * IDE, will update the codeg info and the file.
						 */

					}
					if ( foundTuple == null
							|| codegNameWithCodegFile == null ) {
						/*
						 * either codegNameWithCodegFile is null (the compiler
						 * is not being called by {@link
						 * Saci#parseSingleSource}) or codegNameWithCodegFile is
						 * not null and does not contains a codeg with name
						 * codegCompleteName or or codegNameWithCodegFile is not
						 * null and the codegFileText is null or contains "".
						 * This middle case happens when {@link
						 * Saci#parseSingleSource} is called for the first time
						 * and the program has been previously compiled. Then
						 * the codeg information is in the file. The last case
						 * happens when {@link Saci#parseSingleSource} has been
						 * called but there is no information on the codeg.
						 * Maybe the information is in the file. Maybe it is
						 * not.
						 */

						final Path aPath = Paths.get(codegInfoFilename);
						codegFileData = null;
						try {
							codegFileData = Files.readAllBytes(aPath);
						}
						catch (final IOException e) {
							try {
								this.error(true, annotation.getFirstSymbol(),
										"Error reading information on codeg '"
												+ annotation.getCyanMetaobject()
														.getName()
												+ "("
												+ (String) annotation
														.getJavaParameterList()
														.get(0)
												+ (annotation
														.getJavaParameterList()
														.size() > 1 ? ", ..."
																: "")
												+ ")'. File "
												+ codegInfoFilename
												+ " was not found",
										annotation.getFirstSymbol()
												.getSymbolString(),
										ErrorKind.metaobject_error_reading_codeg_info_file);

							}
							catch (final error.CompileErrorException e1) {
							}
						}

						/*
						 * MyFile f = new MyFile(codegInfoFilename); char
						 * []charArray = f.readFile();
						 */
						if ( codegFileData != null )
							annotation.setCodegInfo(codegFileData);
						else
							annotation.setCodegInfo("".getBytes());
						if ( foundTuple == null
								&& codegNameWithCodegFile != null ) {
							/*
							 * no codeg was found. Add it to
							 * codegNameWithCodegFile In a regular compilation
							 * (not in parseSingleSource), foundTuple == null &&
							 * codegNameWithCodegFile == null
							 *
							 * Only in parseSingleSource the code below is
							 * executed.
							 */
							this.codegNameWithCodegFile
									.add(new Tuple2<String, byte[]>(
											codegCompleteName,
											annotation.getCodegInfo()));
						}

					}

				}
			}
			this.compilationUnit.setCodegList(codegList);
		}
	}

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	/**
	 * taken from
	 * http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
	 *
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int computeLevenshteinDistance(String str1, String str2) {
		final int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0
										: 1));

		return distance[str1.length()][str2.length()];
	}

	/**
	 * \p{ImportDec} ::= \{ ``import"\/ IdList \}
	 *
	 * @return
	 */

	private List<ExprIdentStar> importDecList() {
		final List<ExprIdentStar> importList = new ArrayList<ExprIdentStar>();

		final Set<CyanPackage> importPackageSet = new HashSet<CyanPackage>();
		final Map<String, JVMPackage> importJVMPackageSet = new HashMap<>();
		final Map<String, TypeJavaRef> importJVMJavaRefSet = new HashMap<>();

		while (symbol.token == Token.IMPORT) {
			final Symbol importSymbol = symbol;
			next();
			if ( symbol.token != Token.IDENT ) {
				error(true, symbol,
						"package name expected in import declaration."
								+ foundSuch(),
						null, ErrorKind.package_name_expected);
			}
			else {

				ExprIdentStar importPackage = ident();
				if ( importPackage.getName()
						.startsWith(MetaHelper.cyanLanguagePackageName) ) {
					if ( importPackage.getName()
							.equals(MetaHelper.cyanLanguagePackageName) ) {
						error2(importPackage.getFirstSymbol(),
								"Package 'cyan.lang' is automatically imported. It cannot be imported by the user");
					}
					else {
						error2(importPackage.getFirstSymbol(),
								"It is not legal to have a package that starts with 'cyan.lang'");
					}
				}

				/*
				 * load metaobjects of the imported package
				 */
				String importedPackageName = importPackage.getName();
				CyanPackage importedPackage = compilationUnit.getCyanPackage()
						.getProject().searchPackage(importedPackageName);
				// if ( importedPackage == null &&
				// importedPackageName.startsWith("cyan.") ) {
				// importedPackage = loadMissingPackageFromCyanLibrary(
				// importedPackageName);
				// }
				if ( importedPackage == null ) {
					importJava(importJVMPackageSet, importJVMJavaRefSet,
							importPackage, importedPackageName);
				}
				else {
					importList.add(importPackage);
					compilationUnit.loadCyanMetaobjects(
							importedPackage.getMetaobjectList(), importSymbol,
							this);
					lexer.addMetaObjectLiteralObjectSeqTable(compilationUnit
							.getMetaObjectLiteralObjectSeqTable());
					lexer.addMetaobjectLiteralNumberTable(
							compilationUnit.getMetaObjectLiteralNumber());
					// lexer.setMetaObjectLiteralObjectIdentSeqTable(compilationUnit.getMetaObjectLiteralObjectIdentSeqTable());
					lexer.addMetaobjectLiteralStringTable(
							compilationUnit.getMetaobjectLiteralObjectString());

					importPackageSet.add(importedPackage);
				}

				while (symbol.token == Token.COMMA
						|| symbol.token == Token.IDENT) {
					if ( symbol.token == Token.IDENT ) {
						if ( !symbol.getSymbolString().equals("open") ) {
							error2(symbol,
									"',' expected between imported packages."
											+ foundSuch());
						}
						else {
							break;
						}
					}
					else {
						lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
						next();
					}
					if ( symbol.token != Token.IDENT ) error(true, symbol,
							"Package name expected." + foundSuch(), null,
							ErrorKind.package_name_expected);
					final Symbol packageSymbol = symbol;
					importPackage = ident();
					/*
					 * load metaobjects of the imported package
					 */
					importedPackageName = importPackage.getName();
					importedPackage = compilationUnit.getCyanPackage()
							.getProject().searchPackage(importedPackageName);
					if ( importedPackage == null ) {
						importJava(importJVMPackageSet, importJVMJavaRefSet,
								importPackage, importedPackageName);
					}
					else {
						importList.add(importPackage);
						compilationUnit.loadCyanMetaobjects(
								importedPackage.getMetaobjectList(),
								packageSymbol, this);

						lexer.addMetaObjectLiteralObjectSeqTable(compilationUnit
								.getMetaObjectLiteralObjectSeqTable());
						lexer.addMetaobjectLiteralNumberTable(
								compilationUnit.getMetaObjectLiteralNumber());
						// lexer.setMetaObjectLiteralObjectIdentSeqTable(compilationUnit.getMetaObjectLiteralObjectIdentSeqTable());
						lexer.addMetaobjectLiteralStringTable(compilationUnit
								.getMetaobjectLiteralObjectString());
						importPackageSet.add(importedPackage);
					}
				}
			}
			if ( symbol.token == Token.SEMICOLON ) next();
		}

		compilationUnit.setImportPackageSet(importPackageSet);
		compilationUnit.setImportJVMPackageSet(importJVMPackageSet);
		compilationUnit.setImportJVMJavaRefSet(importJVMJavaRefSet);
		return importList;
	}

	/**
	 * @param importedPackageName
	 * @return
	 */
	private CyanPackage loadMissingPackageFromCyanLibrary(
			String importedPackageName) {
		CyanPackage importedPackage;
		/*
		 * The package importedPackageName of the standard Cyan library that has
		 * not been loaded, load it. Packages of the standard Cyan library
		 * starts with "cyan." and are in directory CYAN_HOME\cyan\packageName
		 */

		final List<CyanMetaobject> metaobjectList = new ArrayList<>();
		String packageCanonicalPath = this.project.getProgram()
				.getCyanLangDir();
		if ( packageCanonicalPath.endsWith(NameServer.fileSeparatorAsString) ) {
			packageCanonicalPath += importedPackageName.replace('.',
					NameServer.fileSeparator);
		}
		else {
			packageCanonicalPath += NameServer.fileSeparator
					+ importedPackageName.replace('.',
							NameServer.fileSeparator);
		}
		try {
			CompilerManager.loadMetaobjectsFromPackage(packageCanonicalPath,
					importedPackageName, metaobjectList, this);
		}
		catch (final RuntimeException e) {
			this.error2(symbol, "Internal error in Compiler: exception '"
					+ e.getClass().getName() + "' was thrown and not caught");
		}
		importedPackage = createCyanPackage(program, project, null,
				importedPackageName, packageCanonicalPath, metaobjectList,
				null);

		project.addCyanPackage(importedPackage);
		return importedPackage;
	}

	/**
	 * @param importJVMPackageSet
	 * @param importJVMClassSet
	 * @param importedPackage
	 * @param importedPackageName
	 */
	private void importJava(Map<String, JVMPackage> importJVMPackageSet,
			Map<String, TypeJavaRef> importJVMClassSet,
			ExprIdentStar importPackage, String importedPackageName) {
		final String si = importPackage.getIdentSymbolArray().get(
				importPackage.getIdentSymbolArray().size() - 1).symbolString;
		int i = 0;
		while (i < si.length() && si.charAt(i) == '_') {
			++i;
		}

		if ( i < si.length() && Character.isUpperCase(si.charAt(i)) ) {
			// assume it is a JVM class
			final int j = importedPackageName.lastIndexOf('.');
			if ( j <= 0 ) {
				error(true, importPackage.getFirstSymbol(), "Package '"
						+ importedPackageName
						+ "' was not found. Usually this is caused by an "
						+ "incorret '.pyan' project file. As an example, suppose prototype 'Math' was"
						+ "not found. Create a 'p.pyan' file with the following contents:"
						+ "program \r\n" + "    package cyan.math\r\n"
						+ "Call the compiler passing this file as argument. Problem solved",
						importPackage.getName(),
						ErrorKind.package_was_not_found_outside_prototype);

			}
			else {
				final String jvmClassName = importedPackageName
						.substring(j + 1);
				importedPackageName = importedPackageName.substring(0, j);
				final JVMPackage jvmPackage = program
						.searchJVMPackage(importedPackageName);
				if ( jvmPackage == null ) {
					error(true, importPackage.getFirstSymbol(),
							"Package '" + importedPackageName
									+ "' was not found",
							importPackage.getName(),
							ErrorKind.package_was_not_found_outside_prototype);
				}
				else {
					final TypeJavaRef javaRef = jvmPackage
							.searchJVMClass(jvmClassName);
					if ( javaRef == null ) {
						error(true, importPackage.getFirstSymbol(),
								"Java class '" + importPackage.getName()
										+ "' was not found",
								importPackage.getName(),
								ErrorKind.package_was_not_found_outside_prototype);
					}
					else {
						/*
						 * try { program.loadJavaClass(javaRef); } catch
						 * (ClassNotFoundException | IOException e) {
						 * this.error2(importedPackage.getFirstSymbol(),
						 * "Error loading Java class '" +
						 * javaRef.getPackageName() + "." + javaRef.getName() +
						 * "' of file '" + jvmPackage.getPathToJar() + "'"); }
						 */
						importJVMClassSet.put(jvmClassName, javaRef);
					}
				}

			}
		}
		else {
			// assume it is a package
			final JVMPackage jvmPackage = program
					.searchJVMPackage(importedPackageName);
			if ( jvmPackage == null ) {
				error(true, importPackage.getFirstSymbol(),
						"Package '" + importPackage.getName()
								+ "' was not found",
						importedPackageName,
						ErrorKind.package_was_not_found_outside_prototype);
			}
			else {
				/*
				 * load the classes of the package if it has not been loaded
				 * before
				 *
				 * if ( !jvmPackage.getClassesWereLoaded() ) { try {
				 * program.loadClassesFromJavaPackage(jvmPackage);
				 * jvmPackage.setClassesWereLoaded(true); } catch
				 * (ClassNotFoundException | IOException e) {
				 * this.error2(importedPackage.getFirstSymbol(),
				 * "Error loading classes from Java package '" +
				 * jvmPackage.getPackageName() + "' of file '" +
				 * jvmPackage.getPathToJar() + "'"); } }
				 */
				importJVMPackageSet.put(jvmPackage.getPackageName(),
						jvmPackage);
				/*
				 * for (Map.Entry<String, TypeJavaRef> entry :
				 * jvmPackage.getJvmTypeClassMap().entrySet() ) {
				 * importJVMClassSet.put(entry.getKey(), entry.getValue()); }
				 */
			}

		}
	}

	/**
	 * return an object that groups several identifiers separated by . such as
	 * cyan.util.Stack It is assumed that the current symbol, given by variable
	 * symbol, is the first identifier in the list ("cyan" in
	 * "cyan.util.Stack").
	 *
	 * @param identOne
	 * @return
	 */

	public ExprIdentStar ident() {

		final List<Symbol> identSymbolArray = new ArrayList<Symbol>();
		identSymbolArray.add(symbol);
		next();
		while (symbol.token == Token.PERIOD) {
			next();
			if ( symbol.token != Token.IDENT ) {
				error(true, symbol,
						"Package, object name or slot (variable or method) expected."
								+ foundSuch(),
						null, ErrorKind.identifier_expected_inside_method);
			}
			identSymbolArray.add(symbol);
			next();
		}
		return new ExprIdentStar(identSymbolArray, symbol, currentMethod);
	}

	private ExprIdentStar identColon() {

		final List<Symbol> identSymbolArray = new ArrayList<Symbol>();
		if ( symbol.token == Token.IDENTCOLON ) {
			identSymbolArray.add(symbol);
			next();
			return new ExprIdentStar(identSymbolArray, symbol, currentMethod);
		}
		return null;
	}

	private AnnotationAt annotation(boolean inExpr) {
		final String metaobjectName = symbol.getSymbolString();
		final CyanMetaobjectAtAnnot cyanMetaobject = this.compilationUnit
				.getMetaObjectTable().get(metaobjectName);
		return annotation(cyanMetaobject, metaobjectName, inExpr);
	}

	/**
	 * analyzes a metaobject annotation. The current token should be
	 * AnnotationAt
	 *
	 * @return
	 */
	private AnnotationAt annotation(CyanMetaobjectAtAnnot cyanMetaobject,
			String metaobjectName, boolean inExpr) {

		final SymbolCyanAnnotation metaobjectSymbol = (SymbolCyanAnnotation) symbol;
		// String metaobjectName = symbol.getSymbolString();

		if ( cyanMetaobject == null ) {

			/*
			 * error(true, symbol, "Metaobject " + metaobjectName +
			 * " was not found", metaobjectName,
			 * ErrorKind.metaobject_was_not_found);
			 */
			this.error2(true, symbol,
					"Metaobject " + metaobjectName + " was not found");
			return null;
		}

		if ( cyanMetaobject instanceof IAction_cge
				&& !cyanMetaobject.getPackageName()
						.equals(MetaHelper.cyanLanguagePackageName)
				&& this.compilationStep == CompilationStep.step_1
				&& this.cyanMetaobjectContextStack.empty() ) {
			/*
			 * Metaobject that implement interface IAction_cge generate Java
			 * code. These metaobjects can only be used inside package
			 * cyan.lang. Unless they are introduced by the compiler itself in
			 * phases >= 2 (CompilationStep.step_2 and beyond)
			 */
			this.error(true, metaobjectSymbol, "Metaobject '"
					+ cyanMetaobject.getName()
					+ "' can only be declared inside package cyan.lang because it implements interface 'IAction_cge'",
					metaobjectName, ErrorKind.metaobject_error);

		}

		final Symbol metaobjectAnnotationSymbol = symbol;
		cyanMetaobject = cyanMetaobject.myClone();
		final AnnotationAt annotation = new AnnotationAt(compilationUnit,
				(SymbolCyanAnnotation) symbol, cyanMetaobject, inExpr,
				currentMethod);
		_CyanMetaobject other_mo = cyanMetaobject.getMetaobjectInCyan();
		if ( other_mo != null ) {
			other_mo._getHidden().setAnnotation(cyanMetaobject.getAnnotation());
		}
		// cyanMetaobject.setAnnotation(annotation);

		annotation.setInsideProjectFile(this.compInstSet
				.contains(CompilationInstruction.pyanSourceCode));

		annotation.setInsideMethod(this.currentMethod != null);

		metaobjectSymbol.setAnnotation(annotation);

		if ( cyanMetaobject instanceof ICodeg ) {
			this.codegList.add(annotation);
		}

		if ( currentPrototype != null ) {
			currentPrototype.addAnnotation(annotation);
			annotation.setCurrentPrototype(currentPrototype);
			annotation.setAnnotationNumber(
					currentPrototype.getIncAnnotationNumber());
		}
		annotation.setCompilationUnit(compilationUnit);

		/*
		 * there are many possibilities of passing parameters to a metaobject
		 * annotation: a) the annotation is in an expression; that is,
		 * <code>inExpr</code> is true. <br> correct: <br> <code> var n
		 * = @annotation(hi) 2; </code> <br> there is no white space before '('
		 * in the annotation and the metaobject takes arguments. <br> <code> var
		 * n = @annotation (hi); </code> <br> no white space before and the
		 * metaobject does not take arguments.<br> wrong: <br> <code> var hi =
		 * 0; <br> var n = @annotation (hi); </code> <br> white space before:
		 * ambiguous to the user. If the annotation may take arguments, the
		 * compiler should sign an error. <br> <code> var hi = 0; <br> var n
		 * = @annotation (hi); </code> <br>
		 *
		 * b) the annotation is not in an expression; that is,
		 * <code>inExpr</code> is false. <br> correct:<br> <code> @style(plain)
		 * func get -> Int { ... }</code><br> No white space before '('<br>
		 * wrong:<br> <code> @style (plain) func get -> Int { ... }</code><br>
		 * The compiler should sign an error but continue if the annotation may
		 * take arguments.
		 *
		 * <br> Currently we show only the most basic message: if the next
		 * symbol is '(' and the metaobject may take any arguments the compiler
		 * issue an error message. Using the comments above one could show more
		 * information to the user.
		 *
		 */
		boolean action_parsing = false;
		if ( // ! cyanMetaobject.shouldTakeText() &&
		cyanMetaobject instanceof IAction_parsing
				&& (annotation.getPostfix() == null || annotation.getPostfix()
						.lessThan(CompilerPhase.PARSING))
				&& (this.currentPrototype == null
						|| !this.currentPrototype.isGeneric()) ) {

			// symbol is a metaobject annotation
			action_parsing = true;
			// always use compilation context
			insertPhaseChange(CompilerPhase.PARSING, annotation);

			/*
			 * if ( cyanMetaobject.useCompilationContext_keepTheAnnotation( ) )
			 * { insertPhaseChange(CompilerPhase.PARSING, annotation); } else {
			 * offsetStartDelete = symbol.getOffset(); }
			 */
		}

		final List<WrExprAnyLiteral> exprList = new ArrayList<>();
		List<Object> javaObjectList = null;

		if ( !metaobjectSymbol.getLeftParAfterAnnotation() ) {
			// no '(' immediatelly after the metaobject name. Then there should
			// be no parameters
			if ( cyanMetaobject.shouldTakeText() ) {

				try {
					getLeftCharSequence();
				}
				catch (final CompileErrorException e) {
					return annotation;
				}

			}
			else {
				next();
				if ( symbol instanceof SymbolCharSequence ) {
					this.error2(symbol,
							"This metaobject should not take a DSL after its name and parameters. If it should, its method 'shouldTakeText' should return 'true'");
				}
			}
		}
		else {
			/*
			 * the metaobject has a '(' after its name, without any spaces
			 * before the '('. Therefore the metaobject annotation should have
			 * parameters
			 */
			next();
			if ( symbol.token != Token.LEFTPAR ) {
				this.error(true, metaobjectAnnotationSymbol,
						"'(' used just after a metaobject annotation. It was expected just the character '('",
						"", ErrorKind.metaobject_error);
			}
			annotation.setLeftParenthesis(symbol);
			next();
			lexer.setOneCharSymbols(true);
			if ( startExpr(symbol) || symbol.token == Token.IDENTCOLON
					|| Character.isAlphabetic(symbol.toString().charAt(0)) ) {
				exprList.add(exprBasicTypeLiteral().getI());
				while (symbol.token == Token.COMMA) {
					lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");

					next();
					exprList.add(exprBasicTypeLiteral().getI());
				}
			}
			lexer.setOneCharSymbols(false);

			if ( symbol.token != Token.RIGHTPAR )
				error(true, symbol,
						"')' expected after the parameters of this metaobject annotation. Or maybe a ',' is missing",
						"",
						ErrorKind.metaobject_annotation_error_missing_args_symbols);
			else {

				if ( Lexer.hasIdentNumberAfter(symbol, compilationUnitSuper) ) {
					error2(symbol, "letter, number, or '_' after ')'");
				}

				annotation.setRightParenthesis(symbol);
				if ( cyanMetaobject.shouldTakeText() )
					getLeftCharSequence();
				else
					next();
			}

		}
		annotation.setRealParameterList(exprList);
		if ( cyanMetaobject
				.getParameterKinds() == AnnotationArgumentsKind.OneParameter
				&& exprList.size() != 1 ) {
			this.error(true, metaobjectAnnotationSymbol,
					"Metaobject " + cyanMetaobject.getName()
							+ " accepts exactly one parameter",
					"", ErrorKind.metaobject_wrong_number_of_parameters);
		}
		else if ( cyanMetaobject
				.getParameterKinds() == AnnotationArgumentsKind.TwoParameters
				&& exprList.size() != 2 ) {
			this.error(true, metaobjectAnnotationSymbol,
					"Metaobject " + cyanMetaobject.getName()
							+ " accepts two parameters",
					"", ErrorKind.metaobject_wrong_number_of_parameters);
		}
		else if ( cyanMetaobject
				.getParameterKinds() == AnnotationArgumentsKind.OneOrMoreParameters
				&& exprList.size() == 0 ) {
			this.error(true, metaobjectAnnotationSymbol,
					"Metaobject " + cyanMetaobject.getName()
							+ " accepts at least one parameter",
					"", ErrorKind.metaobject_wrong_number_of_parameters);
		}
		else if ( cyanMetaobject
				.getParameterKinds() == AnnotationArgumentsKind.ZeroParameters
				&& exprList.size() != 0 ) {
			this.error(true, metaobjectAnnotationSymbol,
					"Metaobject " + cyanMetaobject.getName()
							+ " does not accept parameters",
					"", ErrorKind.metaobject_wrong_number_of_parameters);
		}

		javaObjectList = new ArrayList<Object>();
		for (final WrExprAnyLiteral e : exprList) {
			javaObjectList.add(e.getJavaValue());
		}
		annotation.setJavaParameterList(javaObjectList);

		boolean nonGenericPrototype = !compilationUnit.hasGenericPrototype();

		if ( cyanMetaobject instanceof IInformCompilationError ) {
			final IInformCompilationError cyanMetaobjectCompilationError = (IInformCompilationError) cyanMetaobject;

			if ( cyanMetaobjectCompilationError.activeInGenericPrototype() ) {
				nonGenericPrototype = true;
			}
		}

		if ( nonGenericPrototype ) {
			cyanMetaobject.check();
			final List<CyanMetaobjectError> errorList = cyanMetaobject
					.getErrorMessageList_cleanAll();
			if ( errorList != null ) {
				for (final CyanMetaobjectError cyanMetaobjectError : errorList) {
					this.error(true, metaobjectAnnotationSymbol,
							cyanMetaobjectError.getMessage(),
							metaobjectAnnotationSymbol.getSymbolString(),
							ErrorKind.metaobject_error);
				}
			}
		}

		ICompilerAction_parsing compilerAction_dpaLocal = null;
		/*
		 * true if a text between a sequence of characters was found after the
		 * metaobject name/parameters
		 */
		boolean foundtextBetweenSeq = false;

		if ( action_parsing ) {
			final IAction_parsing codeGen = (IAction_parsing) cyanMetaobject;
			final Compiler_parsing compiler_parsing = new Compiler_parsing(this,
					lexer, null);
			compilerAction_dpaLocal = compiler_parsing;
			StringBuffer sb = null;

			try {
				int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(this,
						program, this.compilationUnit.getCyanPackage(),
						annotation.getFirstSymbol());

				Timeout<StringBuffer> to = new Timeout<>();
				sb = to.run(() -> {
					return codeGen.parsing_codeToAdd(compiler_parsing);
				}, timeoutMilliseconds, "parsing_codeToAdd", cyanMetaobject,
						project);

				// sb = codeGen.parsing_codeToAdd(compiler_parsing);
			}
			catch (final CompileErrorException e) {
			}
			catch (final NoClassDefFoundError e) {
				error2(annotation.getFirstSymbol(), e.getMessage() + " "
						+ NameServer.messageClassNotFoundException);
			}
			catch (final RuntimeException e) {
				thrownException(annotation, annotation.getFirstSymbol(), e);
			}
			finally {
				final List<CyanMetaobjectError> errorList = cyanMetaobject
						.getErrorMessageList_cleanAll();
				if ( errorList != null ) {
					for (final CyanMetaobjectError moError : errorList) {
						error2(annotation.getFirstSymbol(),
								moError.getMessage());
					}
				}
				this.copyLexerData(compiler_parsing.compiler);
			}

			if ( sb != null ) {

				if ( cyanMetaobject.shouldTakeText() ) {
					error2(annotation.getFirstSymbol(),
							"The metaobject class associated to this annotation implements interface '"
									+ IAction_parsing.class.getName()
									+ "' and at the same time its method 'shouldTakeText' returns true."
									+ " That is, there should be a DSL following the annotation like in '@rpn{* 1 2 + *}'. "
									+ "This is only valid if sole method of '"
									+ IAction_parsing.class.getName()
									+ "' returns null");
				}

				if ( cyanMetaobject instanceof IActionAttachedType_semAn ) {
					error2(annotation.getFirstSymbol(),
							"The metaobject class associated to this annotation implements '"
									+ IActionAttachedType_semAn.class.getName()
									+ "'. Therefore the metaobject cannot add code"
									+ " after the annotation. However, Method parsing_codeToAdd of the "
									+ "metaobject returned a non-null string");
				}
				if ( this.compilationStep.ordinal() >= CompilationStep.step_7
						.ordinal() ) {
					if ( !allowCreationOfPrototypesInLastCompilerPhases ) {
						this.error2(metaobjectAnnotationSymbol,
								"This metaobject annotation is trying to generate code in phase 7 or greater of the compilation. This is illegal"
										+ " Probably this metaobject annotation was inserted in this source code in phases 5, 6, or 7 of the compilation by another metaobject annotation");
					}
				}

				/*
				 * if ( sb.indexOf("\n") >= 0 ) {
				 * this.error2(metaobjectAnnotationSymbol,
				 * "During parsing, this metaobject is trying to add code that contains \\n. "
				 * +
				 * "That is illegal because it prevents the compiler of tracking the correct line number of the tokens"
				 * ); }
				 */
				final StringBuffer s = new StringBuffer();
				// always use compilation context
				if ( cyanMetaobject.isExpression() ) {
					s.append(" @" + MetaHelper.pushCompilationContextName
							+ "(parsing" + Compiler.contextNumber + ", \""
							+ metaobjectName + "\", \""
							+ this.compilationUnit.getPackageName() + "\", \""
							+ compilationUnit.getFullFileNamePath() + "\", "
							+ metaobjectSymbol.getLineNumber());
					s.append(") ");
					s.append(sb);
					s.append(" @" + MetaHelper.popCompilationContextName
							+ "(parsing" + Compiler.contextNumber + ", \""
							+ cyanMetaobject.getPackageOfType() + "\", \""
							+ cyanMetaobject.getPrototypeOfType() + "\") ");
				}
				else {
					s.append(" @"
							+ MetaHelper.pushCompilationContextStatementName
							+ "(parsing" + Compiler.contextNumber + ", \""
							+ metaobjectName + "\", \""
							+ this.compilationUnit.getPackageName() + "\", \""
							+ compilationUnit.getFullFileNamePath() + "\", "
							+ metaobjectSymbol.getLineNumber());
					s.append(") ");
					s.append(sb);
					s.append(" @" + MetaHelper.popCompilationContextName
							+ "(parsing" + Compiler.contextNumber + ") ");
				}

				/*
				 * pushCompilationContext(id, moName, packageName,
				 * prototypeName, sourceFileName, lineNumber
				 */

				++Compiler.contextNumber;
				int offset = symbol.getOffset();
				final char[] text = this.compilationUnit.getText();
				offset = Lexer.findIndexInsertText(text, offset);
				insertTextInput(s, offset);
				insideCyanMetaobjectCompilationContextPushAnnotation = true;
				next();

				hasMade_dpa_actions = true;

			}

		}

		_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();

		if ( !cyanMetaobject.shouldTakeText() ) {
			/*
			 * there should not be a text between a sequence of characters like
			 * in <code> <br> var g = @graph#parsing{* 1:2 2:3 *} <br> </code>
			 * <br> That is, the metaobject annotation is as in the following
			 * examples: <code> <br> var list = @compilationInfo("fieldlist");
			 *
			 * @genSomething(10); </code>
			 *
			 */

			annotation.setNextSymbol(symbol);
		}
		else {
			/*
			 * metaobject should take text between sequences such as {* and *}.
			 */
			if ( symbol.token != Token.LEFTCHAR_SEQUENCE ) this.error(true,
					metaobjectAnnotationSymbol,
					"After this metaobject annotation there should appear a text between two sequences of symbols",
					"", ErrorKind.metaobject_annotation_error_missing_text);

			final SymbolCharSequence leftCharSeqSymbol = (SymbolCharSequence) symbol;

			/*
			 * if ( !
			 * compInstSet.contains(saci.CompilationInstruction.parsing_actions)
			 * && !(cyanMetaobject instanceof IAction_cge) ) { /* test if the
			 * metaobject annotation ends with #parsing such as in <code> <br>
			 * var g = @graph#parsing{* 1:2 2:3 *} <br> </code> / if (
			 * annotation.getPostfix() != CompilerPhase.PARSING ) { /* found a
			 * literal object in a compiler step that does not allow literal
			 * objects. See the Figure in Chapter "Metaobjects" of the Cyan
			 * manual / this.error(true, symbol,
			 * "Literal object in a compiler step that does not allow literal objects"
			 * , symbol.getSymbolString(), ErrorKind.
			 * parsing_compilation_phase_literal_objects_and_macros_are_not_allowed)
			 * ;
			 *
			 * } } else
			 */
			if ( (cyanMetaobject instanceof IParseWithCyanCompiler_parsing)
					|| (other != null
							&& other instanceof _IParseWithCyanCompiler__parsing) ) {
				/*
				 * represents DSL such as in <br> <code>
				 *
				 * @graph{* 1:2, 2:3, 3:1 *} <br>
				 *
				 * @hashTable[* "one":1, "two":2 *] <br> </code><br> that is
				 * parsed with the help of the Cyan compiler.
				 */

				// // cyanMetaobject.setAnnotation(annotation, 0);
				final Compiler_parsing compiler_parsing = new Compiler_parsing(
						this, lexer, leftCharSeqSymbol.getSymbolString());
				final int offsetLeftCharSeq = this.symbol.getOffset()
						+ symbol.getSymbolString().length();

				compilerAction_dpaLocal = compiler_parsing;

				/*
				 * the errors found in the compilation are introduced in object
				 * 'this'. They will be processed as regular compiler errors.
				 */

				try {
					int timeoutMilliseconds = getTimeoutMilliseconds(
							annotation);
					Timeout<Object> to = new Timeout<>();
					final CyanMetaobjectAtAnnot cyanMetaobjectFinal = cyanMetaobject;
					if ( other == null ) {
						to.run(Executors.callable(() -> {
							((IParseWithCyanCompiler_parsing) cyanMetaobjectFinal)
									.parsing_parse(compiler_parsing);
						}), timeoutMilliseconds, "parsing_parse",
								cyanMetaobject, project);
					}
					else {
						to.run(Executors.callable(() -> {
							((_IParseWithCyanCompiler__parsing) other)
									._parsing__parse_1(compiler_parsing);

						}), timeoutMilliseconds, "parsing_parse",
								cyanMetaobject, project);
					}
				}
				catch (final error.CompileErrorException e) {
				}
				catch (final NoClassDefFoundError e) {
					error2(annotation.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (final RuntimeException e) {
					e.printStackTrace();
					thrownException(annotation, annotation.getFirstSymbol(), e);
				}
				finally {
					this.metaobjectError(cyanMetaobject, annotation);
					this.copyLexerData(compiler_parsing.compiler);
				}

				annotation.setExprStatList(compiler_parsing.getExprStatList());
				if ( symbol.token != Token.RIGHTCHAR_SEQUENCE ) {
					while (symbol.token != Token.EOF) {
						next();
						/*
						 * if ( symbol.getColumnNumber() <= columnAnnotation2 )
						 * { this.error2(symbol,
						 * "The text attached to this annotation, between the delimiters, should "
						 * + "be at least in column " + columnAnnotation2); }
						 */
						if ( symbol.token == Token.RIGHTCHAR_SEQUENCE ) {
							if ( symbol.getSymbolString().equals(
									compiler_parsing.getRightSeqSymbols()) ) {
								break;
							}
						}
					}
				}

				if ( symbol.token == Token.RIGHTCHAR_SEQUENCE ) {
					final int offsetRightCharSeq = this.symbol.getOffset();
					/*
					 * store the text between the left and right char sequence
					 * in the metaobject annotation
					 */
					final char[] text = lexer.getText(offsetLeftCharSeq,
							offsetRightCharSeq);
					annotation.setTextAttachedDSL(text);
					annotation.setLeftCharSeqSymbol(leftCharSeqSymbol);
					// // cyanMetaobject.setAnnotation(annotation, 0);
					annotation
							.setRightCharSeqSymbol((SymbolCharSequence) symbol);

					next();
					annotation.setNextSymbol(symbol);

				}
				else {
					error(true, symbol,
							"The right symbol sequence '"
									+ lexer.expectedRightSymbolSequence()
									+ "' was expected." + foundSuch(),
							null, ErrorKind.right_symbol_sequence_expected);
				}
				annotation.setNextSymbol(symbol);
				lexer.setNewLineAsToken(false);
				foundtextBetweenSeq = true;

			}
			else { // if ( cyanMetaobject instanceof IAction_semAn ) {
				/*
				 * found a literal object that is parsed with a user-defined
				 * compiler. That is, the text inside the literal object is not
				 * parsed with the help of the Cyan compiler.
				 */

				if ( cyanMetaobject instanceof IActionNewPrototypes_parsing ) {
					compilerAction_dpaLocal = new CompilerAction_parsing(this);
				}

				final char[] rightCharSeq = lexer.getRightSymbolSeq(
						leftCharSeqSymbol.getCharSequence(),
						leftCharSeqSymbol.getSizeCharSequence());
				final char[] text = lexer.getTextTill(rightCharSeq,
						leftCharSeqSymbol.getOffset()
								+ leftCharSeqSymbol.getSizeCharSequence());
				final SymbolCharSequence rightCharSeqSymbol = lexer
						.getSymbolRightCharSeq();

				annotation.setLeftCharSeqSymbol(leftCharSeqSymbol);
				annotation.setTextAttachedDSL(text);
				cyanMetaobject.setAnnotation(annotation.getI());
				annotation.setRightCharSeqSymbol(rightCharSeqSymbol);
				next();
				this.previousSymbol = rightCharSeqSymbol;
				annotation.setNextSymbol(symbol);

				foundtextBetweenSeq = true;

			}
			// else {
			// this.error(true, symbol, "metaobject annotation '"
			// + metaobjectName + "' has a Java class "
			// + " that does not implement any of the following interfaces:
			// 'IParseWithoutCyanCompiler_parsing',
			// 'IParseWithCyanCompiler_parsing', 'IAction_cge', IAction_semAn",
			// null, ErrorKind.metaobject_error);
			//
			// }
		}

		if ( (cyanMetaobject instanceof IActionNewPrototypes_parsing
				|| (other != null
						&& other instanceof _IActionNewPrototypes__parsing))
				&& this.compilationStep.ordinal() < CompilationStep.step_7
						.ordinal() ) {
			if ( compilerAction_dpaLocal == null ) {
				compilerAction_dpaLocal = new Compiler_parsing(this, lexer,
						null);
			}

			actionNewPrototypes_parsing(cyanMetaobject,
					metaobjectAnnotationSymbol, annotation,
					compilerAction_dpaLocal);

		}

		/*
		 * this is necessary because getLeftCharSequence, called in some
		 * previous 'if', does not follow the regular rules of lexical analysis.
		 */
		// if ( ! cyanMetaobject.mayTakeArguments() && !
		// cyanMetaobject.mayTakeText() )
		// next();

		cyanMetaobject.setAnnotation(annotation.getI());

		// # checkError = ... was here

		/**
		 * the following steps only make sense in real prototypes. The generic
		 * ones should not be checked, no action should be taken on them.
		 *
		 * It is used method {@link CompilationUnit#hasGenericPrototype} because
		 * it checks the file name of the source code. Metaobjects may be used
		 * before the compiler knows, by the source file, that there is a
		 * generic prototype.
		 */
		if ( !compilationUnit.hasGenericPrototype() ) {

			if ( cyanMetaobject.shouldTakeText() && !foundtextBetweenSeq ) {
				this.error(true, metaobjectAnnotationSymbol, "Metaobject '"
						+ cyanMetaobject.getName()
						+ "' should take a text between two sequences of "
						+ "characters after the metaobject name (maybe followed by parameters). Example: @graph{* 1:2 2:3 *}",
						metaobjectAnnotationSymbol.getSymbolString(),
						ErrorKind.metaobject_error);
			}

			if ( cyanMetaobject instanceof CyanMetaobjectCompilationContextPush ) {
				if ( compInstSet.contains(
						CompilationInstruction.parsing_originalSourceCode)
						&& !this.hasAdded_pp_new_Methods
						&& !this.hasAddedInnerPrototypes
						&& !hasMade_dpa_actions )
					this.error(true, metaobjectSymbol,
							"'@" + MetaHelper.pushCompilationContextName
									+ "' can only be annotated by the compiler",
							null, ErrorKind.metaobject_error);

				if ( javaObjectList.size() == 2 ) {
					cyanMetaobjectContextStack.push(new Tuple5<>(
							(String) javaObjectList.get(0),
							(String) javaObjectList.get(1), null, null, null));

				}
				else if ( javaObjectList.size() == 5
						|| javaObjectList.size() == 6 ) {
					cyanMetaobjectContextStack
							.push(new Tuple5<>((String) javaObjectList.get(0),
									MetaHelper.removeQuotes(
											(String) javaObjectList.get(1)),
									(String) javaObjectList.get(2),
									(String) javaObjectList.get(3),
									(Integer) javaObjectList.get(4)));
				}

				insideCyanMetaobjectCompilationContextPushAnnotation = false;
				if ( lineNumberStartCompilationContextPush < 0 ) {
					/*
					 * The '< 0' avoids that the line below is executed inside
					 * nested metaobject annotations of CompilationContextPush
					 */
					this.lineNumberStartCompilationContextPush = metaobjectSymbol
							.getLineNumber();
				}

			}
			else if ( cyanMetaobject instanceof CyanMetaobjectCompilationContextPop ) {
				if ( compInstSet.contains(
						CompilationInstruction.parsing_originalSourceCode)
						&& !this.hasAdded_pp_new_Methods
						&& !this.hasAddedInnerPrototypes
						&& !hasMade_dpa_actions )
					this.error(true, metaobjectSymbol,
							"'@popCompilationContext' can only be annotated by the compiler",
							null, ErrorKind.metaobject_error);
				if ( cyanMetaobjectContextStack.empty() )
					this.error(true, metaobjectSymbol,
							"Attempt to pop a context through '@popCompilationContext' in an empty stack",
							null, ErrorKind.internal_error);
				else {

					final String id = (String) javaObjectList.get(0);
					if ( !id.equals(cyanMetaobjectContextStack.peek().f1) ) {
						this.error(true, metaobjectSymbol,
								"Attempt to pop a context through '@popCompilationContext' with a wrong id. It should be '"
										+ cyanMetaobjectContextStack.peek().f1
										+ "'",
								null, ErrorKind.internal_error);
					}
					else {
						cyanMetaobjectContextStack.pop();
						if ( cyanMetaobjectContextStack.isEmpty()
								&& !insideCyanMetaobjectCompilationContextPushAnnotation ) {
							boolean replace = true;
							final String symName = symbol.symbolString;
							if ( symbol instanceof lexer.SymbolCyanAnnotation
									&& (symName.equals(
											MetaHelper.pushCompilationContextName)
											|| symName.equals(
													MetaHelper.pushCompilationContextStatementName)) ) {
								replace = false;
							}
							if ( replace ) {
								symbol.setLineNumber(
										symbol.getLineNumber() - lineShift);
							}
						}
						// lineShift +=
						// cyanMetaobject.getAnnotation().getSymbolCTMOCall().getLineNumber()
						// - lineStartCompilationContextPush + 1;
						/*
						 * if ( cyanMetaobjectContextStack.empty() ) { lineShift
						 * -= metaobjectSymbol.getLineNumber() -
						 * lineStartCompilationContextPush + 1; }
						 */
					}
				}
				// lineShift += metaobjectSymbol.getLineNumber() -
				// this.lineNumberStartCompilationContextPush + 1;
				this.lineNumberStartCompilationContextPush = -1;
			}
			else if ( cyanMetaobject instanceof CyanMetaobjectCompilationMarkDeletedCode ) {
				if ( compInstSet.contains(
						CompilationInstruction.parsing_originalSourceCode)
						&& !this.hasAdded_pp_new_Methods
						&& !this.hasAddedInnerPrototypes
						&& !hasMade_dpa_actions )
					this.error(true, metaobjectSymbol,
							"'@" + MetaHelper.markDeletedCodeName
									+ "' can only be annotated by the compiler",
							null, ErrorKind.metaobject_error);

				lineShift -= ((CyanMetaobjectCompilationMarkDeletedCode) cyanMetaobject)
						.getNumLinesDeleted();

			}

		}

		if ( cyanMetaobject instanceof IInformCompilationError || (other != null
				&& other instanceof _IInformCompilationError) ) {

			if ( other == null ) {
				final IInformCompilationError cyanMetaobjectCompilationError = (IInformCompilationError) cyanMetaobject;

				if ( !compilationUnit.hasGenericPrototype()
						|| cyanMetaobjectCompilationError
								.activeInGenericPrototype() ) {

					this.compilationUnit.addLineMessageList(
							new Tuple3<Integer, String, Boolean>(
									cyanMetaobjectCompilationError
											.getLineNumber(),
									cyanMetaobjectCompilationError
											.getErrorMessage(),
									false));
				}

			}
			else {
				final _IInformCompilationError cyanMetaobjectCompilationError = (_IInformCompilationError) other;

				if ( !compilationUnit.hasGenericPrototype()
						|| cyanMetaobjectCompilationError
								._activeInGenericPrototype().b ) {

					this.compilationUnit.addLineMessageList(
							new Tuple3<Integer, String, Boolean>(
									cyanMetaobjectCompilationError
											._getLineNumber().n,
									cyanMetaobjectCompilationError
											._getErrorMessage().s,
									false));
				}

			}
		}
		/*
		 * if ( metaobjectName.equals("createTuple") && !
		 * this.currentPrototype.isGeneric() &&
		 * this.currentPrototype.getName().equals(
		 * "Tuple<key,String,value,Any>") ) {
		 * MyFile.write(this.compilationUnit); }
		 */

		if ( localVariableDecStack != null
				&& localVariableDecStack.size() > 0 ) {
			final List<Tuple2<String, String>> localVariableNameList = getLocalVariableList();
			annotation.setLocalVariableNameList(localVariableNameList);
		}

		if ( !(cyanMetaobject instanceof meta.cyanLang.CyanMetaobjectCompilationContextPop
				|| cyanMetaobject instanceof meta.cyanLang.CyanMetaobjectCompilationContextPush
				|| cyanMetaobject instanceof meta.cyanLang.CyanMetaobjectCompilationMarkDeletedCode) ) {

			final String packageOfType = cyanMetaobject.getPackageOfType();
			final String prototypeOfType = cyanMetaobject.getPrototypeOfType();

			if ( cyanMetaobject.isExpression() ) {
				if ( this.currentPrototype == null ) {
					// metaobject produce code that can be used inside an
					// expression but it is being used outside a program unit
					this.error2(annotation.getFirstSymbol(),
							"Method 'isExpression' of this metaobject returns true meaning this object is an expression. "
									+ "Therefore it can only be used inside a prototype");
					return null;
				}
				if ( packageOfType == null || prototypeOfType == null ) {
					this.error2(annotation.getFirstSymbol(),
							"Method 'isExpression' of this metaobject returns true meaning this object is an expression. "
									+ "However, either method 'getPackageOfType' or 'getPrototypeOfType' return null");
					return null;

				}

				if ( packageOfType.indexOf(' ') >= 0 ) {
					this.error2(annotation.getFirstSymbol(),
							"Method 'getPackageOfType' of this metaobject returns a package name that has spaces in it");
					return null;
				}
				if ( prototypeOfType.indexOf(' ') >= 0 ) {
					this.error2(annotation.getFirstSymbol(),
							"Method 'getPrototypeOfType' of this metaobject returns a name that has spaces in it. This is illegal."
									+ " Instead of 'Tuple<Int, String>', use 'Tuple<Int,String>'");
					return null;
				}

				/*
				 * the expression below is associated to this compilation unit.
				 * In the phase AFTER_RES_TYPES, the compiler will assure that
				 * the type is created if necessary.
				 */
				final Expr prototypeAsExpr = Compiler.parseSingleTypeFromString(
						packageOfType + "." + prototypeOfType,
						annotation.getFirstSymbol(),
						"The type of this metaobject annotation is '"
								+ packageOfType + "." + prototypeOfType
								+ "'. This type is not syntactically correct",
						this.compilationUnit,
						this.currentPrototype /* , this */);
				if ( prototypeAsExpr instanceof ExprGenericPrototypeInstantiation ) {
					this.currentPrototype.addGpiList(
							(ExprGenericPrototypeInstantiation) prototypeAsExpr);
				}
			}
			else {
				if ( packageOfType != null || prototypeOfType != null ) {
					this.error2(annotation.getFirstSymbol(),
							"Method 'isExpression' of this metaobject returns false meaning this object is NOT an expression. "
									+ "However, either method 'getPackageOfType' or 'getPrototypeOfType' return a non-null string");
					return null;
				}
				if ( inExpr ) {
					this.error2(annotation.getFirstSymbol(),
							"Method 'isExpression' of this metaobject returns false meaning this object is NOT an expression. "
									+ "However, this metaobject is being used inside an expression");
				}
			}

		}

		annotation.setLastSymbol(this.previousSymbol);

		return annotation;
	}

	/**
	 * @param annotation
	 * @return
	 */
	private int getTimeoutMilliseconds(final Annotation annotation) {
		int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(this, program,
				this.compilationUnit.getCyanPackage(),
				annotation.getFirstSymbol());
		return timeoutMilliseconds;
	}

	/**
	 * @param cyanMetaobject
	 * @param errSymbol
	 * @param annotation
	 * @param compilerAction_dpaLocal
	 */
	private void actionNewPrototypes_parsing(CyanMetaobject cyanMetaobject,
			final Symbol errSymbol, final Annotation annotation,
			ICompilerAction_parsing compilerAction_dpaLocal) {

		// Compiler_parsing compiler_parsing = new Compiler_parsing(this, lexer,
		// null,
		// metaobjectAnnotationSymbol.getColumnNumber());
		_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();

		List<Tuple2<String, StringBuffer>> prototypeNameCodeList = null;
		try {
			int timeoutMilliseconds = getTimeoutMilliseconds(annotation);

			if ( other == null ) {
				Timeout<List<Tuple2<String, StringBuffer>>> to = new Timeout<>();
				final IActionNewPrototypes_parsing actionNewPrototype = (IActionNewPrototypes_parsing) cyanMetaobject;
				prototypeNameCodeList = to.run(
						() -> actionNewPrototype.parsing_NewPrototypeList(
								compilerAction_dpaLocal),
						timeoutMilliseconds, "parsing_NewPrototypeList",
						cyanMetaobject, project);
				// prototypeNameCodeList = actionNewPrototype
				// .parsing_NewPrototypeList(compilerAction_dpaLocal);
			}
			else {
				Timeout<_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT> to = new Timeout<>();
				_IActionNewPrototypes__parsing anp = (_IActionNewPrototypes__parsing) other;
				_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array = to
						.run(() -> anp._parsing__NewPrototypeList_1(
								compilerAction_dpaLocal), timeoutMilliseconds,
								"parsing_NewPrototypeList", cyanMetaobject,
								project);
				// _Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array =
				// anp._parsing__NewPrototypeList_1(compilerAction_dpaLocal);
				int size = array._size().n;
				if ( size > 0 ) {
					prototypeNameCodeList = new ArrayList<>();
					for (int i = 0; i < size; ++i) {
						_Tuple_LT_GP_CyString_GP_CyString_GT tss = array
								._at_1(new CyInt(i));
						String f1 = tss._f1().s;
						String f2 = tss._f2().s;
						if ( f1.length() > 0 ) {
							prototypeNameCodeList
									.add(new Tuple2<String, StringBuffer>(f1,
											new StringBuffer(f2)));
						}
					}
				}

			}

		}
		catch (final error.CompileErrorException e) {
		}
		catch (final NoClassDefFoundError e) {
			error2(annotation.getFirstSymbol(), e.getMessage() + " "
					+ NameServer.messageClassNotFoundException);
		}
		catch (final RuntimeException e) {
			thrownException(annotation, annotation.getFirstSymbol(), e);
		}
		finally {
			metaobjectError(cyanMetaobject, annotation);
		}
		if ( prototypeNameCodeList != null ) {
			CyanPackage currentCyanPackage = this.compilationUnit
					.getCyanPackage();

			for (final Tuple2<String, StringBuffer> prototypeNameCode : prototypeNameCodeList) {
				String prototypeName = prototypeNameCode.f1;

				final Tuple2<CompilationUnit, String> t = this.project
						.getCompilerManager().createNewPrototype(prototypeName,
								prototypeNameCode.f2,
								this.compilationUnit.getCompilerOptions(),
								currentCyanPackage);
				if ( t != null && t.f2 != null ) {
					this.error2(errSymbol, t.f2);
				}
				currentCyanPackage.addPrototypeNameAnnotationInfo(prototypeName,
						annotation);
			}
		}

	}

	/**
	 * @return
	 */
	private List<Tuple2<String, String>> getLocalVariableList() {
		final List<Tuple2<String, String>> localVariableNameList = new ArrayList<>();
		for (final StatementLocalVariableDec aVar : this.localVariableDecStack) {
			String strType = null;
			if ( aVar.getTypeInDec() != null ) {
				strType = aVar.asString();
			}
			localVariableNameList
					.add(new Tuple2<String, String>(aVar.getName(), strType));
		}
		return localVariableNameList;
	}

	/**
	 * @param cyanMetaobject
	 * @param annotation
	 */
	public void metaobjectError(CyanMetaobject cyanMetaobject,
			Annotation annotation) {

		final List<CyanMetaobjectError> errorList = cyanMetaobject
				.getErrorMessageList_cleanAll();
		if ( errorList != null ) {
			for (final CyanMetaobjectError moError : errorList) {
				try {
					error2(moError.getSymbol() != null
							? meta.GetHiddenItem
									.getHiddenSymbol(moError.getSymbol())
							: annotation.getFirstSymbol(),
							moError.getMessage());
				}
				catch (final CompileErrorException e) {
				}

			}
		}
	}

	public IActionFunction searchActionFunction(String metaobjectName) {
		if ( compilationUnit.getActionFunctionTable() != null ) {
			return this.compilationUnit.getActionFunctionTable()
					.get(metaobjectName);
		}
		return null;
	}

	private Prototype prototype() {

		List<AnnotationAt> attachedAnnotationList = null;
		List<AnnotationAt> nonAttachedAnnotationList = null;

		final Tuple2<List<AnnotationAt>, List<AnnotationAt>> tc = parseAnnotations_NonAttached_Attached();
		if ( tc != null ) {
			nonAttachedAnnotationList = tc.f1;
			attachedAnnotationList = tc.f2;
		}

		if ( attachedAnnotationList != null ) {
			for (final AnnotationAt annotation : attachedAnnotationList) {
				if ( annotation.getCyanMetaobject()
						.shouldBeAttachedToSomething() ) {
					if ( !annotation.getCyanMetaobject().mayBeAttachedTo(
							AttachedDeclarationKind.PROTOTYPE_DEC) )
						this.error2(annotation.getFirstSymbol(),
								"This metaobject annotation cannot be attached to a prototype. It can be attached to "
										+ " one entity of the following list: [ "
										+ annotation.getCyanMetaobject()
												.attachedListAsString()
										+ " ]");

				}
			}

		}
		Token visibility = Token.PUBLIC;

		if ( symbol.token == Token.PUBLIC || symbol.token == Token.PACKAGE ) {
			visibility = symbol.token;
			if ( symbol.token == Token.PACKAGE ) {
				this.warning(symbol,
						"'package' visibility for prototypes is not yet supported. Changed to 'public'");
				visibility = Token.PUBLIC;
			}
			next();
		}
		if ( visibility == Token.PUBLIC || visibility == Token.PACKAGE ) {
			++this.numPublicPackagePrototypes;
		}
		boolean immutable = false;
		if ( symbol.token == Token.IMMUTABLE ) {
			immutable = true;
		}
		if ( symbol.token == Token.INTERFACE )
			interfaceDec(visibility, nonAttachedAnnotationList,
					attachedAnnotationList, null);
		else
			objectDec(visibility, nonAttachedAnnotationList,
					attachedAnnotationList, null);

		if ( attachedAnnotationList != null
				&& attachedAnnotationList.size() > 0 ) {
			for (final AnnotationAt annotation : attachedAnnotationList) {
				if ( annotation.getCyanMetaobject().mayBeAttachedTo(
						AttachedDeclarationKind.PROTOTYPE_DEC) ) {
					annotation.setDeclaration(currentPrototype == null ? null
							: currentPrototype.getI());
				}
			}
		}

		if ( !cyanMetaobjectContextStack.empty()
				&& !this.compilationUnitSuper.hasCompilationError() )
			this.error(true, null, "'@" + MetaHelper.pushCompilationContextName
					+ "(" + cyanMetaobjectContextStack.peek().f1
					+ ", ...)' was used inside the source code without the '@"
					+ MetaHelper.popCompilationContextName + "("
					+ cyanMetaobjectContextStack.peek().f1 + ")'", null,
					ErrorKind.metaobject_error);
		/*
		 * set the number of the metaobject annotations considering only the
		 * metaobjects with the same name. That is, if there are annotations to
		 * metaobjects "moA", "moB", "moA", "moA", and "moB", in this textual
		 * order, the code below associate to these annotations the number 1, 1,
		 * 2, 3, 2.
		 */
		final HashMap<String, Integer> mapMetaobjectNameToNumber = new HashMap<>();
		for (final Annotation annotation : currentPrototype
				.getCompleteAnnotationList()) {
			final String name = annotation.getCyanMetaobject().getName();
			final Integer metaobjectNumber = mapMetaobjectNameToNumber
					.get(name);
			int numberByKind;
			numberByKind = metaobjectNumber != null ? metaobjectNumber + 1
					: Annotation.firstAnnotationNumber;
			mapMetaobjectNameToNumber.put(name, numberByKind);
			annotation.setAnnotationNumberByKind(numberByKind);

		}

		if ( this.compilationStep.ordinal() < CompilationStep.step_7
				.ordinal() ) {
			ICompilerAction_parsing compilerAction_dpaLocal = null;
			List<Annotation> annotList = currentPrototype
					.getPrototypePackageProgramAnnotationList();
			currentPrototype.setDeclarationImportedFromPackageProgram();

			// for ( Annotation annot : annotList) {
			// CyanMetaobject cyanMetaobject = annot.getCyanMetaobject();
			// _CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			// if ( cyanMetaobject instanceof IActionNewPrototypes_parsing ||
			// (other != null &&
			// other instanceof _IActionNewPrototypes__parsing) ) {
			// if ( compilerAction_dpaLocal == null ) {
			// compilerAction_dpaLocal = new Compiler_parsing(this, lexer,
			// null);
			// }
			//
			// actionNewPrototypes_parsing(cyanMetaobject,
			// annot.getFirstSymbol(),
			// annot, compilerAction_dpaLocal);
			// }
			// }

		}
		return currentPrototype;
	}

	/**
	 *
	 * @param visibility
	 * @param metaobjectAnnotationList
	 * @return
	 */
	private Prototype interfaceDec(Token visibility,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList, ObjectDec outerObject) {

		// List<AnnotationAt>
		// nonAttachedAnnotationList,
		// List<AnnotationAt>
		// attachedAnnotationList

		InterfaceDec interfaceDec = null;
		final Symbol interfaceSymbol = symbol;

		next();
		if ( symbol.token != Token.IDENT ) {
			error(true, symbol, "interface name expected." + foundSuch(), "",
					ErrorKind.interface_name_expected);
		}
		else {
			methodNumber = 0;

			if ( outerObject == null && this.compilationStep
					.ordinal() > CompilationStep.step_3.ordinal()
					&& this.compilationUnit.getParsed() ) {
				InterfaceDec newObj = new InterfaceDec();
				InterfaceDec previousObj = (InterfaceDec) this.compilationUnit
						.getPublicPrototype();
				this.cloneFromTo(newObj, previousObj);
				InterfaceDec.initInterfaceDec(previousObj, outerObject,
						interfaceSymbol, (SymbolIdent) symbol, visibility,
						nonAttachedAnnotationList, attachedAnnotationList,
						lexer);
				currentPrototype = interfaceDec = previousObj;
				this.compilationUnit.removePrototype(previousObj);
			}
			else {
				currentPrototype = interfaceDec = new InterfaceDec(outerObject,
						interfaceSymbol, (SymbolIdent) symbol, visibility,
						nonAttachedAnnotationList, attachedAnnotationList,
						lexer);
			}

			currentPrototype.setCompilationUnit(compilationUnit);

			compilationUnit.addPrototype(currentPrototype);

			currentPrototype.setFirstSymbol(interfaceSymbol);
			currentPrototype.setSymbolObjectInterface(interfaceSymbol);

			if ( attachedAnnotationList != null ) {
				/*
				 * the number of a metaobject annotation that is textually
				 * before a prototype is not set in method {@link
				 * Compiler#annotation()} before when this method is called
				 * {@link currentPrototype} is null.
				 */
				for (final AnnotationAt annotation : attachedAnnotationList) {
					currentPrototype.addAnnotation(annotation);
					annotation.setCompilationUnit(compilationUnit);
					annotation.setCurrentPrototype(currentPrototype);
					annotation.setAnnotationNumber(
							currentPrototype.getIncAnnotationNumber());
					annotation.setDeclaration(currentPrototype == null ? null
							: currentPrototype.getI());
					if ( annotation
							.getCyanMetaobject() instanceof IActionAssignment_cge ) {
						this.error2(annotation.getFirstSymbol(), "Metaobject "
								+ annotation.getCyanMetaobject().getName()
								+ " implements "
								+ "interface IActionAssignment_cge and can only be attached to prototypes that are not interfaces");
					}
				}
			}

			next();

			if ( symbol.token != Token.LT_NOT_PREC_SPACE ) {
				// check if the file name of the source file is correct
				if ( currentPrototype.getVisibility() == Token.PUBLIC ) {
					final String fileNameCurrentPrototype = currentPrototype
							.getName() + "."
							+ MetaHelper.cyanSourceFileExtension;
					if ( fileNameCurrentPrototype
							.compareTo(compilationUnit.getFilename()) != 0
							&& !parsingPackageInterfaces )
						error(true, currentPrototype.getSymbol(),
								"The file name of this compilation unit has an incorret name. It should be "
										+ fileNameCurrentPrototype,
								"",
								ErrorKind.file_name_incorrect_in_compilation_unit);
				}
			}
			else {
				if ( currentPrototype.getVisibility() != Token.PUBLIC ) {
					error(true, currentPrototype.getSymbol(),
							"Generic prototypes should be declared 'public'",
							currentPrototype.getName(),
							ErrorKind.non_public_generic_prototype);
				}
				if ( compilationUnit.getHasGenericPrototype() ) {
					error(true, currentPrototype.getSymbol(),
							"Two generic prototypes cannot be declared in the same source file",
							currentPrototype.getName(),
							ErrorKind.two_or_more_generic_prototype_in_the_same_source_file);
				}
				while (symbol.token == Token.LT_NOT_PREC_SPACE)
					currentPrototype.addGenericParameterList(templateDec());

				/*
				 * check if real and formal parameters are mixed in the
				 * declaration of the generic prototype. It is illegal to
				 * declare object Function<T, Int> ... end object
				 * Struct<Boolean, Int, U>
				 *
				 *
				 * boolean hasRealParameter = false; boolean hasTypeParameter =
				 * false; GenericParameter firstTypeGenericParameter = null;
				 * GenericParameter firstRealPrototypeGenericParameter = null;
				 * for ( List<GenericParameter> genericParameterList :
				 * currentPrototype.getGenericParameterListList() ) { for (
				 * GenericParameter gp : genericParameterList ) { if (
				 * gp.isRealPrototype() ) { hasRealParameter = true;
				 * firstRealPrototypeGenericParameter = gp; if (
				 * hasTypeParameter ) error(currentPrototype.getSymbol(),
				 * "Generic parameters and real parameters cannot be mixed in generic prototype declaration. That is, 'Function<T, Int>' is illegal in which T is a formal parameter"
				 * , "", ErrorKind.mixins_of_generic_and_non_generic_parameters,
				 * "parameter0 = " +
				 * firstRealPrototypeGenericParameter.getName(), "parameter1 = "
				 * + firstTypeGenericParameter.getName()); } else {
				 * firstTypeGenericParameter = gp; hasTypeParameter = true; if (
				 * hasRealParameter ) error(currentPrototype.getSymbol(),
				 * "Generic parameters and real parameters cannot be mixed in generic prototype declaration. That is, 'Function<T, Int>' is illegal in which T is a formal parameter"
				 * , "", ErrorKind.mixins_of_generic_and_non_generic_parameters,
				 * "parameter0 = " +
				 * firstRealPrototypeGenericParameter.getName(), "parameter1 = "
				 * + firstTypeGenericParameter.getName()); } } }
				 */

				final boolean hasTypeParameter = checkFormalParameterListGenericPrototype(
						currentPrototype);

				currentPrototype.setGenericPrototype(hasTypeParameter);
				compilationUnit.setHasGenericPrototype(hasTypeParameter);
				compilationUnit.setPrototypeIsNotGeneric(!hasTypeParameter);
				currentPrototype.setPrototypeIsNotGeneric(!hasTypeParameter);
				/**
				 * check if the source file has the correct name. It should be
				 * "Stack(Int).cyan" if the generic prototype is "Stack<Int>"
				 * and "Function(2)(1).cyan" if the generic prototype is
				 * "Function<T1, T2><R>".
				 *
				 */

				filename = currentPrototype.getNameSourceFile() + "."
						+ MetaHelper.cyanSourceFileExtension;
				if ( filename.compareTo(compilationUnit.getFilename()) != 0
						&& !parsingPackageInterfaces ) {
					currentPrototype.getNameSourceFile();
					error2(currentPrototype.getSymbol(),
							"The file name of this compilation unit has an incorret name. It should be "
									+ filename);
				}

			}

			List<AnnotationAt> beforeExtensemAnttachedAnnotationList = null;
			List<AnnotationAt> beforeExtendsNonAttachedAnnotationList = null;

			if ( symbol.token == Token.METAOBJECT_ANNOTATION ) {
				final Tuple2<List<AnnotationAt>, List<AnnotationAt>> tc = parseAnnotations_NonAttached_Attached();
				if ( tc != null ) {
					beforeExtendsNonAttachedAnnotationList = tc.f1;
					beforeExtensemAnttachedAnnotationList = tc.f2;
				}
				if ( symbol.token == Token.EXTENDS ) {
					if ( beforeExtensemAnttachedAnnotationList != null ) {
						final CyanMetaobject cyanMetaobject = beforeExtensemAnttachedAnnotationList
								.get(0).getCyanMetaobject();
						this.error(true,
								beforeExtensemAnttachedAnnotationList.get(0)
										.getFirstSymbol(),
								"There is a metaobject annotation to metaobject '"
										+ cyanMetaobject.getName()
										+ "' that should be attached to a declaration "
										+ "(prototype, method, field, etc). However, no declaration follows the metaobject annotation",
								null, ErrorKind.metaobject_error);
					}
					this.currentPrototype
							.setCyanMetaobjectListBeforeExtendsMixinImplements(
									beforeExtendsNonAttachedAnnotationList);
					beforeExtendsNonAttachedAnnotationList = null;
				}
			}

			if ( symbol.token == Token.IMPLEMENTS ) {
				this.error2(symbol, "Use 'extends' instead of 'implements'");
			}
			if ( symbol.token == Token.EXTENDS ) {
				next();
				final List<Expr> superInterfaceList = typeList();
				interfaceDec.setSuperInterfaceExprList(superInterfaceList);

			}
			else {
				if ( beforeExtensemAnttachedAnnotationList != null
						|| beforeExtendsNonAttachedAnnotationList != null ) {
					nonAttachedAnnotationList = beforeExtendsNonAttachedAnnotationList;
					attachedAnnotationList = beforeExtensemAnttachedAnnotationList;
				}
			}

			Tuple2<List<AnnotationAt>, List<AnnotationAt>> tc;
			if ( beforeExtensemAnttachedAnnotationList == null
					&& beforeExtendsNonAttachedAnnotationList == null ) {
				tc = parseAnnotations_NonAttached_Attached();
				if ( tc != null ) {
					nonAttachedAnnotationList = tc.f1;
					attachedAnnotationList = tc.f2;
				}
			}

			while (symbol.token == Token.FUNC || symbol.token == Token.PUBLIC
					|| symbol.token == Token.PRIVATE
					|| symbol.token == Token.PROTECTED
					|| symbol.token == Token.OVERRIDE
					|| symbol.token == Token.ABSTRACT) {

				if ( symbol.token == Token.OVERRIDE ) {
					if ( ask(symbol,
							"Keyword 'override' cannot appear in a method signature of an interface. Can I remove it? (y, n)") ) {
						compilationUnit
								.addAction(new ActionDelete(compilationUnit,
										symbol.startOffsetLine
												+ symbol.getColumnNumber() - 1,
										symbol.getSymbolString().length(),
										symbol.getLineNumber(),
										symbol.getColumnNumber()));
					}
					else {
						this.error(true, symbol,
								"Keyword 'override' cannot appear in a method signature of an interface",
								symbol.getSymbolString(),
								ErrorKind.qualifier_cannot_preced_method_signature_in_interfaces,
								"qualifier = override");
					}

				}
				else if ( symbol.token == Token.ABSTRACT ) {
					if ( ask(symbol,
							"Keyword 'abstract' cannot appear in a method signature of an interface. Can I remove it? (y, n)") ) {
						compilationUnit
								.addAction(new ActionDelete(compilationUnit,
										symbol.startOffsetLine
												+ symbol.getColumnNumber() - 1,
										symbol.getSymbolString().length(),
										symbol.getLineNumber(),
										symbol.getColumnNumber()));
					}
					else {
						this.error(true, symbol,
								"Keyword 'abstract' cannot appear in a method signature of an interface",
								symbol.getSymbolString(),
								ErrorKind.abstract_cannot_preced_method_signature_in_interfaces);
					}

				}
				else if ( symbol.token == Token.PUBLIC
						|| symbol.token == Token.PRIVATE
						|| symbol.token == Token.PROTECTED ) {
					final String qualifier = symbol.getSymbolString();
					next();
					if ( ask(symbol, "Qualifier '" + qualifier
							+ "' is illegal here. Can I remove it? (y, n)") ) {
						compilationUnit
								.addAction(new ActionDelete(compilationUnit,
										symbol.startOffsetLine
												+ symbol.getColumnNumber() - 1,
										symbol.getSymbolString().length(),
										symbol.getLineNumber(),
										symbol.getColumnNumber()));
					}
					else {
						this.error(true, symbol,
								"'public',  'protected',  or 'private' cannot appear before a method signature in an interface",
								symbol.getSymbolString(),
								ErrorKind.qualifier_cannot_preced_method_signature_in_interfaces,
								"qualifier = " + qualifier);
					}

				}
				else {
					next();

					MethodSignature ms = null;
					try {
						this.prohibitTypeof = true;

						/*
						 * currently, the currentObject is null
						 */
						currentMethod = new MethodDec(null, visibility, false,
								false, nonAttachedAnnotationList,
								attachedAnnotationList, methodNumber++,
								// compiler does not create method for
								// interfaces
								false, cyanMetaobjectContextStack);

						ms = methodSignature();
						checkInitNewMethods(ms);
					}
					finally {
						this.prohibitTypeof = false;
					}

					interfaceDec.addMethodSignature(ms);
					ms.setDeclaringInterface(interfaceDec);

					if ( ms.getNameWithoutParamNumber()
							.equals(MetaHelper.initShared) )
						error2(ms.getFirstSymbol(),
								"'initShared' methods cannot be declared in interfaces");

					ms.setAnnotationNonAttachedAttached(
							nonAttachedAnnotationList, attachedAnnotationList);

					if ( attachedAnnotationList != null ) {

						for (final AnnotationAt annotation : attachedAnnotationList) {

							final CyanMetaobject cyanMetaobject = annotation
									.getCyanMetaobject();
							actionCompilerInfo_parsing(cyanMetaobject);

							if ( annotation.getCyanMetaobject().mayBeAttachedTo(
									AttachedDeclarationKind.METHOD_DEC) ) {
								annotation.setDeclaration(ms.getI());
							}
							else {
								/*
								 * the metaobject cannot be attached to a method
								 */
								this.error(true, annotation.getFirstSymbol(),
										"This metaobject annotation cannot be attached to a method. It can be attached to "
												+ " one entity of the following list: [ "
												+ annotation.getCyanMetaobject()
														.attachedListAsString()
												+ " ]",
										null, ErrorKind.metaobject_error);

							}

						}
					}

					nonAttachedAnnotationList = null;
					attachedAnnotationList = null;

					tc = parseAnnotations_NonAttached_Attached();
					if ( tc != null ) {
						nonAttachedAnnotationList = tc.f1;
						attachedAnnotationList = tc.f2;
					}
				}

			}
			if ( attachedAnnotationList != null ) {
				final CyanMetaobject cyanMetaobject = attachedAnnotationList
						.get(0).getCyanMetaobject();
				this.error(true, attachedAnnotationList.get(0).getFirstSymbol(),
						"There is a metaobject annotation to metaobject '"
								+ cyanMetaobject.getName()
								+ "' that should be attached to a declaration "
								+ "(prototype, method, field, etc). However, no declaration follows the metaobject annotation",
						null, ErrorKind.metaobject_error);

			}
			this.currentPrototype.setBeforeEndNonAttachedAnnotationList(
					nonAttachedAnnotationList);

			if ( symbol.token != Token.END )
				error(true, symbol, "keyword 'end'." + foundSuch(), "",
						ErrorKind.keyword_end_expected);
			this.currentPrototype.setEndSymbol(symbol);
			/*
			 * if ( symbol.getColumnNumber() !=
			 * this.currentPrototype.getFirstSymbol().getColumnNumber() ) {
			 * error2(symbol, "'end' should be in the same column as 'object'");
			 * }
			 */

			next();

		}
		lexer.setPrototype(null);

		return interfaceDec;

	}

	private boolean cloneFromTo(Object from, Object to) {
		return cloneFromTo(from, from.getClass(), to);
	}

	private boolean cloneFromTo(Object from, Class<?> classFrom, Object to) {
		try {
			if ( classFrom != Object.class ) {
				cloneFromTo(from, classFrom.getSuperclass(), to);
				for (Field field : classFrom.getDeclaredFields()) {
					field.setAccessible(true);
					field.set(to, field.get(from));
				}
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param visibility
	 * @param metaobjectAnnotationList
	 * @param outerObject
	 *            If this object is declared inside another object,
	 *            'outerObject' is this external object
	 * @return
	 */

	private ObjectDec objectDec(Token visibility,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList, ObjectDec outerObject) {

		methodNumber = 0;

		ObjectDec currentObject;
		if ( outerObject == null && this.compilationStep
				.ordinal() > CompilationStep.step_3.ordinal()
				&& this.compilationUnit.getParsed() ) {
			ObjectDec newObj = new ObjectDec();
			ObjectDec previousObj = (ObjectDec) this.compilationUnit
					.getPublicPrototype();
			this.cloneFromTo(newObj, previousObj);
			ObjectDec.initObjectDec(previousObj, outerObject, visibility,
					nonAttachedAnnotationList, attachedAnnotationList, lexer);
			currentObject = previousObj;
			this.compilationUnit.removePrototype(previousObj);
		}
		else {
			currentObject = new ObjectDec(outerObject, visibility,
					nonAttachedAnnotationList, attachedAnnotationList, lexer);
		}

		if ( outerObject == null ) {
			compilationUnit.addPrototype(currentObject);
		}
		currentObject.setCompilationUnit(compilationUnit);
		objectDecStack.push(currentObject);

		currentObject.setCompilationUnit(this.compilationUnit);

		currentPrototype = currentObject;

		if ( attachedAnnotationList != null ) {
			/*
			 * the number of a metaobject annotation that is textually before a
			 * prototype is not set in method {@link Compiler#annotation()}
			 * before when this method is called {@link currentPrototype} is
			 * null.
			 */
			for (final AnnotationAt annotation : attachedAnnotationList) {
				currentPrototype.addAnnotation(annotation);
				annotation.setCompilationUnit(compilationUnit);
				annotation.setCurrentPrototype(currentPrototype);
				annotation.setAnnotationNumber(
						currentPrototype.getIncAnnotationNumber());
				annotation.setDeclaration(currentPrototype == null ? null
						: currentPrototype.getI());
			}

		}

		if ( symbol.token == Token.ABSTRACT ) {
			next();
			currentObject.setIsAbstract(true);
			currentObject.setIsFinal(false);
		}
		if ( symbol.token == Token.FINAL ) {
			warning(symbol,
					"prototypes are 'final' by default. This keyword cannot be used here");
			next();
		}
		if ( symbol.token == Token.IDENT
				&& symbol.getSymbolString().equals("open") ) {
			next();
			currentObject.setIsFinal(false);
		}
		if ( symbol.token == Token.LEFTPAR ) {
			next();
			if ( symbol.token != Token.PACKAGE ) {
				error2(symbol, "keyword 'package' was expected");
			}
			if ( symbol.token != Token.RIGHTPAR ) {
				error2(symbol, "a ')' was expected");
			}
			next();
			currentObject.setOpenPackage(true);
		}

		if ( !checkIf(Token.OBJECT, "object") ) {
			error(true, symbol,
					"A prototype declaration was expected." + foundSuch(),
					symbol.getSymbolString(),
					ErrorKind.keyword_object_expected);
			this.skipTo(Token.OBJECT, Token.INTERFACE);
		}

		if ( symbol.token != Token.OBJECT ) error2(symbol,
				"keyword 'object' or 'interface' expected." + foundSuch());

		currentObject.setSymbolObjectInterface(symbol);
		currentObject.setFirstSymbol(symbol);
		next();
		if ( symbol.token != Token.IDENT && !isBasicType(symbol.token)
				&& symbol.token != Token.NIL && symbol.token != Token.STRING )
			error2(symbol, "Object name expected." + foundSuch());
		currentObject.setSymbol(symbol);

		next();
		if ( symbol.token != Token.LT_NOT_PREC_SPACE ) {
			// check if the file name of the source file is correct
			if ( currentObject.getVisibility() == Token.PUBLIC
					&& !this.parsingPackageInterfaces ) {
				final String fileNameCurrentObjectDec = currentObject.getName()
						+ "." + MetaHelper.cyanSourceFileExtension;
				// if outerObject != null then this is an inner object. It name
				// can be different from
				// the source file.
				if ( outerObject == null
						&& fileNameCurrentObjectDec
								.compareTo(compilationUnit.getFilename()) != 0
						&& !parsingPackageInterfaces ) {
					error2(currentObject.getSymbol(),
							"The file name of this compilation unit has an incorret name. It should be "
									+ fileNameCurrentObjectDec);
				}
			}
		}
		else {

			// a generic prototype. It should be public and it should be the
			// only one in the file
			if ( currentObject.getVisibility() != Token.PUBLIC ) {
				error2(currentObject.getSymbol(),
						"Generic prototypes should be declared 'public'");
			}
			if ( compilationUnit.getHasGenericPrototype() ) {
				error2(currentObject.getSymbol(),
						"Two generic prototypes cannot be declared in the same source file");
			}
			while (symbol.token == Token.LT_NOT_PREC_SPACE)
				currentObject.addGenericParameterList(templateDec());

			/*
			 * check if real and formal parameters are mixed in the declaration
			 * of the generic prototype. It is illegal to declare any of the
			 * objects below object Function<T, Int> ... end object
			 * Struct<Boolean, Int, U>
			 *
			 *
			 * boolean hasRealParameter = false; boolean hasTypeParameter =
			 * false; for ( List<GenericParameter> genericParameterList :
			 * currentObject.getGenericParameterListList() ) { for (
			 * GenericParameter gp : genericParameterList ) { if (
			 * gp.isRealPrototype() ) { hasRealParameter = true; if (
			 * hasTypeParameter ) error2(currentObject.getSymbol(),
			 * "generic parameters and real parameters cannot be mixed in generic prototype declaration. That is, 'Function<T, Int>' is illegal if T is a formal parameter"
			 * ); } else { hasTypeParameter = true; if ( hasRealParameter )
			 * error2(currentObject.getSymbol(),
			 * "generic parameters and real parameters cannot be mixed in generic prototype declaration. That is, 'Function<T, Int>' is illegal if T is a formal parameter"
			 * ); } } }
			 */
			final boolean hasTypeParameter = checkFormalParameterListGenericPrototype(
					currentObject);

			currentPrototype.setGenericPrototype(hasTypeParameter);
			compilationUnit.setHasGenericPrototype(hasTypeParameter);
			compilationUnit.setPrototypeIsNotGeneric(!hasTypeParameter);
			currentPrototype.setPrototypeIsNotGeneric(!hasTypeParameter);
			/**
			 * check if the source file has the correct name. It should be
			 * "Stack(Int).cyan" if the generic prototype is "Stack<Int>" and
			 * "Function(2)(1).cyan" if the generic prototype is "Function<T1,
			 * T2><R>".
			 *
			 */

			filename = currentObject.getNameSourceFile() + "."
					+ MetaHelper.cyanSourceFileExtension;
			if ( filename.compareTo(compilationUnit.getFilename()) != 0
					&& !parsingPackageInterfaces ) {
				currentObject.getNameSourceFile();
				error2(currentObject.getSymbol(),
						"The file name of this compilation unit has an incorret name. It should be "
								+ filename);
			}

		}
		if ( symbol.token == Token.LEFTPAR )
			currentObject.setContextParameterArray(contextDec());

		Tuple2<List<AnnotationAt>, List<AnnotationAt>> tc = parseAnnotations_NonAttached_Attached();
		if ( tc != null ) {
			nonAttachedAnnotationList = tc.f1;
			attachedAnnotationList = tc.f2;
		}

		boolean hadExtendsMixinImplements = false;

		if ( symbol.token == Token.EXTENDS ) {
			if ( attachedAnnotationList != null ) {
				final CyanMetaobject cyanMetaobject = attachedAnnotationList
						.get(0).getCyanMetaobject();
				this.error(true, attachedAnnotationList.get(0).getFirstSymbol(),
						"There is a metaobject annotation to metaobject '"
								+ cyanMetaobject.getName()
								+ "' that should be attached to a declaration "
								+ "(prototype, method, field, etc). However, no declaration follows the metaobject annotation",
						null, ErrorKind.metaobject_error);

			}
			next();
			Expr superPrototype;

			try {
				this.prohibitTypeof = true;
				superPrototype = type();
			}
			finally {
				this.prohibitTypeof = false;
			}

			currentObject.setSuperobjectExpr(superPrototype);
			this.currentPrototype
					.setCyanMetaobjectListBeforeExtendsMixinImplements(
							nonAttachedAnnotationList);
			hadExtendsMixinImplements = true;

			// TODO: this assignment is unnecessary, replace currentObj by
			// currentObject
			final ObjectDec currentObj = (ObjectDec) this.currentPrototype;
			if ( currentObj.getContextParameterArray() != null
					&& currentObj.getContextParameterArray().size() > 0 ) {
				/*
				 * there are context parameters. Then there may be '(' in the
				 * super-prototype as in <br> {@code object Worker(String name,
				 * Company company) extends Person(name) <br> ...<br> end<br> }
				 */
				if ( symbol.token == Token.LEFTPAR ) {
					next();
					final List<ContextParameter> cpList = currentObj
							.getContextParameterArray();
					final List<ContextParameter> superContextParameterList = new ArrayList<>();
					if ( symbol.token != Token.IDENT ) {
						error2(symbol, "A context parameter was expected."
								+ foundSuch());
					}
					else {
						// List<ContextParameter> newContextParameterList =
						// new ArrayList<>();
						while (symbol.token == Token.IDENT) {
							final Symbol idSym = symbol;
							final String cpName = idSym.getSymbolString();
							next();
							boolean foundCP = false;

							for (final ContextParameter cp : cpList) {
								if ( cpName.equals(cp.getName()) ) {
									foundCP = true;
									superContextParameterList.add(cp);
									// cp.setDoNotCreateField(true);
									((ObjectDec) this.currentPrototype)
											.removeField(cp);
									((ObjectDec) this.currentPrototype)
											.removeSlot(cp);

								}
							}
							if ( !foundCP ) {
								error2(idSym, "Identifier '" + cpName
										+ "' should be one of the context parameter of this prototype");
							}
							if ( symbol.token == Token.COMMA ) {
								lexer.checkWhiteSpaceParenthesisAfter(symbol,
										",");
								next();
								if ( symbol.token != Token.IDENT ) {
									error2(symbol,
											"A context parameter was expected."
													+ foundSuch());
								}
							}
							else {
								break;
							}
						}
						currentObj.setSuperContextParameterList(
								superContextParameterList);
						// currentObject.setContextParameterArray(newContextParameterList);
					}
					if ( symbol.token != Token.RIGHTPAR ) {
						this.error2(symbol, "')' expected." + foundSuch());
					}
					else {
						if ( Lexer.hasIdentNumberAfter(symbol,
								compilationUnitSuper) ) {
							error2(symbol, "letter, number, or '_' after ')'");
						}
						next();
					}
				}
			}

		}
		if ( symbol.token == Token.IMPLEMENTS ) {
			next();
			currentObject.setInterfaceList(typeList());
			hadExtendsMixinImplements = true;
			this.currentPrototype
					.setCyanMetaobjectListBeforeExtendsMixinImplements(
							nonAttachedAnnotationList);

		}

		if ( hadExtendsMixinImplements ) {
			attachedAnnotationList = null;
			nonAttachedAnnotationList = null;

			tc = parseAnnotations_NonAttached_Attached();
			if ( tc != null ) {
				nonAttachedAnnotationList = tc.f1;
				attachedAnnotationList = tc.f2;
			}
		}

		hasMade_dpa_actions = false;
		hasAdded_pp_new_Methods = false;
		/**
		 * true if the method being compiled was created by the compiler
		 */
		boolean compilerCreatedMethod = false;

		/*
		 * number of objects created from functions that have already been
		 * parsed
		 */
		int numObjForFunctionParsed = 0;

		hasAddedInnerPrototypes = false;

		/*
		 * true if hasSetBeforeInnerObjectNonAttachedAnnotationList of
		 * currentObjectDec has been set already
		 *
		 */

		boolean hasSetBeforeInnerObjectNonAttachedAnnotationList = false;

		while (symbol.token == Token.PRIVATE || symbol.token == Token.PUBLIC
				|| symbol.token == Token.PROTECTED
				|| symbol.token == Token.PACKAGE
				|| symbol.token == Token.METAOBJECT_ANNOTATION
				|| symbol.token == Token.FINAL || symbol.token == Token.SHARED
				|| symbol.token == Token.VAR || symbol.token == Token.FUNC
				|| symbol.token == Token.OVERLOAD
				|| symbol.token == Token.OVERRIDE
				|| symbol.token == Token.ABSTRACT || symbol.token == Token.LET
				|| symbol.token == Token.END || symbol.token == Token.OBJECT
				|| startType(symbol.token)) {

			if ( symbol.token == Token.OBJECT ) {
				if ( !hasAddedInnerPrototypes )
					error(true, symbol,
							"Keyword 'object' cannot be used here. Attempt to declare an inner prototype",
							symbol.getSymbolString(),
							ErrorKind.syntax_error_object);
				else {
					// found the declaration of an object inside the
					// user-declared object. These
					// objects were introduced by the compiler.
					/**
					 * it is assumed that the first object created by this
					 * compiler correspond to a function (if any). It was
					 * created by method addObjectDecForFunctions().
					 */

					final ObjectDec innerObjectDec = objectDec(Token.PUBLIC,
							nonAttachedAnnotationList, attachedAnnotationList,
							currentObject);
					if ( numObjForFunctionParsed < currentObject
							.getNextFunctionNumber() ) {
						currentObject.getFunctionList()
								.get(numObjForFunctionParsed)
								.setInnerObjectForThisFunction(innerObjectDec);
						++numObjForFunctionParsed;
					}

					this.currentPrototype = currentObject = this.objectDecStack
							.peek();

					nonAttachedAnnotationList = null;
					attachedAnnotationList = null;

					tc = parseAnnotations_NonAttached_Attached();
					if ( tc != null ) {
						nonAttachedAnnotationList = tc.f1;
						attachedAnnotationList = tc.f2;
					}
				}
			}
			else {
				final Symbol firstSlotSymbol = symbol;

				if ( symbol.token != Token.END ) {
					try {
						slotDec(currentObject, nonAttachedAnnotationList,
								attachedAnnotationList, firstSlotSymbol,
								compilerCreatedMethod);
					}
					catch (final CompileErrorException e) {
						this.skipTo(Token.FUNC, Token.OVERLOAD, Token.PUBLIC,
								Token.END, Token.PRIVATE, Token.PROTECTED);
					}
					nonAttachedAnnotationList = null;
					attachedAnnotationList = null;

					tc = parseAnnotations_NonAttached_Attached();
					if ( tc != null ) {
						nonAttachedAnnotationList = tc.f1;
						attachedAnnotationList = tc.f2;
					}
				}

				if ( symbol.token == Token.END ) {

					if ( hasAdded_pp_new_Methods ) {

						if ( hasAddedInnerPrototypes )
							break;
						else {
							if ( currentPrototype instanceof ObjectDec
									&& !currentPrototype.isGeneric()
							// && ! currentPrototype.getName().equals("Nil")
							) {

								/*
								 * for each interface the compiler create a new
								 * prototype to represent the interface when it
								 * is used in expressions (the interface as an
								 * object). This new prototype is only created
								 * after the semantic analysis with SEM_AN
								 * actions, which is phase 7 of the Figure of
								 * chapter metaobjects of the Cyan manual.
								 * Therefore code for methods prototype,
								 * defaultValue, inner prototypes, etc were NOT
								 * added to these new prototypes. They are added
								 * in just one phase of the compilation, which
								 * is phase 7, the first phase in which they
								 * appear. Then
								 * <code>compilationUnit.getIsInterfaceAsObject(
								 * )</code> is only true in phase 7.
								 */

								if ( compInstSet.contains(
										CompilationInstruction.inner_addCode)
										|| compilationUnit
												.getIsInterfaceAsObject() ) {

									if ( currentPrototype
											.getAttachedAnnotationList() != null ) {
										for (final Annotation annotation : currentPrototype
												.getAttachedAnnotationList()) {
											final CyanMetaobject cyanMetaobject = annotation
													.getCyanMetaobject();
											actionCompilerInfo_parsing(
													cyanMetaobject);
										}
									}

									/*
									 * if outerObject is null, then this is the
									 * user-created object and a prototype
									 * should be created for each method. This
									 * happen even for 'Function' prototypes and
									 * its sub-prototypes. When generating code
									 * these inner prototypes will be discarded.
									 * At this point we cannot know whether a
									 * prototype inherits from Function or not.
									 * Then we generate prototypes for every
									 * method.
									 *
									 * if outerObject is not null then this
									 * method call (of objectDec) was made to
									 * compile an inner prototype which was
									 * created by the compiler by
									 * addInnerObjectDec and addProtoForMethods.
									 * Since these methods create sub-prototypes
									 * of Function, their methods are not
									 * objecs. Therefore addInnerObjectDec and
									 * addProtoForMethods should not be called.
									 */
									if ( outerObject == null ) {
										/**
										 * add prototypes corresponding to
										 * anonymous functions
										 */

										if ( !hasSetBeforeInnerObjectNonAttachedAnnotationList ) {

											if ( currentPrototype instanceof ObjectDec ) {
												List<SlotDec> slotList = currentObject
														.getSlotList();
												if ( slotList != null
														&& slotList
																.size() > 0 ) {
													slotList.get(
															slotList.size() - 1)
															.setLastSlot(true);
												}

											}
											currentPrototype
													.setBeforeInnerObjectNonAttachedAnnotationList(
															nonAttachedAnnotationList);
											currentPrototype
													.setBeforeInnerObjectAttachedAnnotationList(
															attachedAnnotationList);

											hasSetBeforeInnerObjectNonAttachedAnnotationList = true;

											nonAttachedAnnotationList = null;
											attachedAnnotationList = null;

											tc = parseAnnotations_NonAttached_Attached();
											if ( tc != null ) {
												nonAttachedAnnotationList = tc.f1;
												attachedAnnotationList = tc.f2;
											}
										}

										final StringBuffer s = new StringBuffer();

										s.append(" @"
												+ MetaHelper.pushCompilationContextName
												+ "(inner"
												+ Compiler.contextNumber
												+ ", inner) ");

										/*
										 * add a method that return the features
										 * of this program unit and of the
										 * methods of it
										 */
										this.addMethodsForFeatures(s);

										/**
										 * add a inner prototype for each method
										 */
										// addObjectDecForMethods(s);
										s.append(" @"
												+ MetaHelper.popCompilationContextName
												+ "(inner"
												+ Compiler.contextNumber
												+ ") \n");
										++Compiler.contextNumber;

										s.append(" @"
												+ MetaHelper.pushCompilationContextName
												+ "(inner"
												+ Compiler.contextNumber
												+ ", inner) ");
										if ( ((ObjectDec) currentPrototype)
												.getFunctionList().size() > 0 )
											addObjectDecForFunctions(s);

										/**
										 * add a inner prototype for each method
										 */
										// addObjectDecForMethods(s);
										s.append(" @"
												+ MetaHelper.popCompilationContextName
												+ "(inner"
												+ Compiler.contextNumber
												+ ") \n");
										++Compiler.contextNumber;

										insertTextInput(s, symbol.getOffset());
										insideCyanMetaobjectCompilationContextPushAnnotation = true;
										next();
									}
								}
							}
							hasAddedInnerPrototypes = true;
						}
						// hasAddedInnerPrototypes = true;
					}
					else {
						if ( currentPrototype instanceof ObjectDec
								&& !currentPrototype.isGeneric()
								&& !currentPrototype.getName().equals("Nil") ) {

							/**
							 * methods 'new', 'new:', 'prototype',
							 * 'prototypeName', etc should be added to inner
							 * prototypes. addCodeForInnerPrototypes is true if
							 * these methods should be added to the current
							 * prototype, which should start with
							 * NameServer.functionProtoName or
							 * NameServer.methodProtoName.
							 */
							final boolean addCodeForInnerPrototypes = compInstSet
									.contains(
											CompilationInstruction.pp_new_inner_addCode)
									&& NameServer.isNameInnerPrototype(
											currentPrototype.getName());

							// see comment above on inner_addCode
							if ( compInstSet
									.contains(CompilationInstruction.pp_addCode)
									|| compilationUnit.getIsInterfaceAsObject()
									|| addCodeForInnerPrototypes ) {

								final StringBuffer s = new StringBuffer();
								s.append(" @"
										+ MetaHelper.pushCompilationContextStatementName
										+ "(pp" + Compiler.contextNumber
										+ ", pp) \n");
								this.addMethodsEveryPrototypeHas(s);
								s.append(" @"
										+ MetaHelper.popCompilationContextName
										+ "(pp" + Compiler.contextNumber
										+ ") \n");
								++Compiler.contextNumber;
								insertTextInput(s, symbol.getOffset());
								insideCyanMetaobjectCompilationContextPushAnnotation = true;
								next();

								// all methods compiled from this point onward
								// were created by the compiler
								compilerCreatedMethod = true;
							}

							// see comment above on inner_addCode
							if ( compInstSet.contains(
									CompilationInstruction.new_addCode)
									|| compilationUnit.getIsInterfaceAsObject()
									|| addCodeForInnerPrototypes ) {

								final StringBuffer s = new StringBuffer();
								s.append(" @"
										+ MetaHelper.pushCompilationContextStatementName
										+ "(new" + Compiler.contextNumber
										+ ", new) ");
								addMethodsToPrototypeIfNotDefined(s);
								s.append(" @"
										+ MetaHelper.popCompilationContextName
										+ "(new" + Compiler.contextNumber
										+ ") \n");
								++Compiler.contextNumber;
								insertTextInput(s, symbol.getOffset());
								insideCyanMetaobjectCompilationContextPushAnnotation = true;
								next();

								// all methods compiled from this point onward
								// were created by the compiler
								compilerCreatedMethod = true;
							}

						}
						hasAdded_pp_new_Methods = true;
					}

					tc = parseAnnotations_NonAttached_Attached();
					if ( tc != null ) {
						if ( tc.f1 != null ) {
							if ( nonAttachedAnnotationList == null )
								nonAttachedAnnotationList = tc.f1;
							else
								nonAttachedAnnotationList.addAll(tc.f1);
						}
						if ( tc.f2 != null ) {
							if ( attachedAnnotationList == null )
								attachedAnnotationList = tc.f2;
							else
								attachedAnnotationList.addAll(tc.f2);
						}
					}

				}
			}

		}
		if ( symbol.token != Token.END ) {
			if ( currentPrototype instanceof ObjectDec ) {
				final ObjectDec objDec = (ObjectDec) this.currentPrototype;
				final List<FieldDec> ivList = objDec.getFieldList();
				if ( ivList.size() > 0 ) {
					final Expr expr = ivList.get(ivList.size() - 1).getExpr();
					if ( expr instanceof ExprMessageSendUnaryChainToExpr ) {

						final ExprMessageSendUnaryChainToExpr unary = (ExprMessageSendUnaryChainToExpr) expr;
						if ( unary.getReceiver() instanceof ExprIdentStar
								&& ((ExprIdentStar) unary.getReceiver())
										.getName().equals("new") ) {
							if ( Character.isUpperCase(unary.getUnarySymbol()
									.getSymbolString().charAt(0)) ) {
								this.error2(unary.getFirstSymbol(),
										"It seems you are trying to create an object of "
												+ unary.getUnarySymbol()
														.getSymbolString()
												+ " . Since 'new' is a method name, put it after '"
												+ unary.getUnarySymbol()
														.getSymbolString()
												+ "', not before",
										true);

							}
							else {
								if ( symbol.token == Token.PERIOD
										|| symbol.token == Token.LEFTPAR ) {
									this.error2(unary.getFirstSymbol(),
											"It seems you are trying to create an object using 'new'."
													+ " However, 'new' is a method and therefore it should appear after the object name",
											true);

								}
							}
						}
						/*
						 * if ( unary.getr) receiverOrExpr new unarySymbol
						 * something that starts with upper case
						 */
					}
				}
			}
			error2(symbol, "keyword 'end' expected." + foundSuch());
		}
		this.currentPrototype.setEndSymbol(symbol);

		/*
		 * if ( symbol.getColumnNumber() !=
		 * this.currentPrototype.getFirstSymbol().getColumnNumber() ) {
		 * error2(symbol, "'end' should be in the same column as 'object'"); }
		 */

		if ( attachedAnnotationList != null ) {
			final CyanMetaobject cyanMetaobject = attachedAnnotationList.get(0)
					.getCyanMetaobject();
			this.error(true, attachedAnnotationList.get(0).getFirstSymbol(),
					"There is a metaobject annotation to metaobject '"
							+ cyanMetaobject.getName()
							+ "' that should be attached to a declaration "
							+ "(prototype, method, field, etc). However, no declaration follows the metaobject annotation",
					null, ErrorKind.metaobject_error);

		}
		if ( nonAttachedAnnotationList != null ) {
			this.currentPrototype.setBeforeEndNonAttachedAnnotationList(
					nonAttachedAnnotationList);
		}

		next();

		if ( outerObject != null ) {
			outerObject.addInnerPrototype(currentObject);
		}

		objectDecStack.pop();
		return currentObject;
	}

	/**
	 * add methods to the end of the text of an object. These methods return the
	 * list of features of the prototype and the list of features of each method
	 * and field.
	 *
	 * @param s
	 */
	private void addMethodsForFeatures(StringBuffer s) {
		s.append("\n");
		final String puName = this.currentPrototype.getName();
		if ( puName.equals("Nil") ) return;
		final boolean isAny = puName.equals("Any");
		if ( !isAny ) {
			s.append("    override\n");
		}
		List<String> annotList = null;
		s.append(
				"    func getFeatureListNameDoesNotCollide__ -> Array<Tuple<key, String, value, Any>> {\n");
		if ( currentPrototype.getFeatureList() != null
				&& currentPrototype.getFeatureList().size() > 0 ) {
			// s.append("featureList_name_does_not_collide__;\n");
			s.append(
					"    let featureList_name_does_not_collide__ = Array<Tuple<key, String, value, Any>> new;\n");
			// s.append(" [ ");
			// int size = currentPrototype.getFeatureList().size();
			for (final Tuple2<String, WrExprAnyLiteral> t : this.currentPrototype
					.getFeatureList()) {
				s.append("        featureList_name_does_not_collide__ add: ");

				String strValue;
				strValue = t.f2.metaobjectParameterAsString(() -> {
					this.error2(
							meta.GetHiddenItem
									.getHiddenSymbol(t.f2.getFirstSymbol()),
							"This expression cannot be used in a parameter in a metaobject annotation");
				}); // Lexer.valueToFeatureString(t);

				final String key = MetaHelper.removeQuotes(t.f1);
				s.append("[. key = \"" + key + "\", value = Any toAny: "
						+ strValue + " .];\n");
				if ( key.equals("annot") ) {
					if ( annotList == null ) {
						annotList = new ArrayList<>();
					}
					annotList.add(strValue);
				}
				/*
				 * if ( --size > 0 ) s.append(", "); s.append("\n");
				 */
			}
			// s.append(" ];\n");
			s.append("        return featureList_name_does_not_collide__;\n");
		}
		else {
			s.append(
					"        return Array<Tuple<key, String, value, Any>> new;\n");
		}
		s.append("    }\n");

		if ( !isAny ) {
			s.append("    override\n");
		}
		s.append("    func getAnnotListNameDoesNotCollide__ -> Array<Any> {\n");

		if ( annotList != null ) {
			s.append(
					"    let annotList_name_does_not_collide__ = Array<Any> new;\n");
			for (final String value : annotList) {
				s.append("        annotList_name_does_not_collide__ add: "
						+ value + ";\n");
			}
			s.append("        return annotList_name_does_not_collide__;\n");
		}
		else {
			s.append("        return Array<Any> new;\n");
		}
		s.append("    }\n");

		s.append("\n");
		if ( !isAny ) {
			s.append("    override\n");
		}
		s.append(
				"    func getSlotFeatureListNameDoesNotCollide__ -> Array<Tuple<slotName, String, key, String, value, Any>> {\n");
		// s.append("slotFeatureList_name_does_not_collide__;\n");

		s.append(
				"        let slotFeatureList_name_does_not_collide__ = Array<Tuple<slotName, String, key, String, value, Any>> new;\n");

		/*
		 * int sizeS = s.length(); s.append("        [ \n");
		 */
		// s.append(" ");

		// s.append("[. slotName = \"\", key = \"\", value = Any cast: \"\"
		// .]");
		if ( this.currentPrototype instanceof ObjectDec ) {
			final ObjectDec currentObj = (ObjectDec) this.currentPrototype;
			for (final FieldDec iv : currentObj.getFieldList()) {
				final String ivName = iv.getName();
				if ( iv.getFeatureList() != null ) {
					for (final Tuple2<String, WrExprAnyLiteral> t : iv
							.getFeatureList()) {
						// s.append(",\n ");
						s.append(
								"        slotFeatureList_name_does_not_collide__ add: ");
						String strValue;
						// strValue = Lexer.valueToFeatureString(t);
						strValue = t.f2.metaobjectParameterAsString(() -> {
							this.error2(
									meta.GetHiddenItem.getHiddenSymbol(
											t.f2.getFirstSymbol()),
									"This expression cannot be used in a parameter in a metaobject annotation");
						});
						s.append("[. slotName = \"" + ivName + "\", key = \""
								+ MetaHelper.removeQuotes(t.f1)
								+ "\", value = Any toAny: " + strValue
								+ " .];\n");
					}
				}
			}
			for (final MethodDec method : currentObj.getMethodDecList()) {
				final String methodName = method
						.getSignatureWithoutReturnType();
				if ( method.getFeatureList() != null ) {
					for (final Tuple2<String, WrExprAnyLiteral> t : method
							.getFeatureList()) {
						// s.append(",\n ");
						s.append(
								"        slotFeatureList_name_does_not_collide__ add: ");
						String strValue;
						// strValue = Lexer.valueToFeatureString(t);
						strValue = t.f2.metaobjectParameterAsString(() -> {
							this.error2(
									meta.GetHiddenItem.getHiddenSymbol(
											t.f2.getFirstSymbol()),
									"This expression cannot be used in a parameter in a metaobject annotation");
						});

						s.append("[. slotName = \"" + methodName
								+ "\", key = \"" + MetaHelper.removeQuotes(t.f1)
								+ "\", value = Any toAny: " + strValue
								+ " .];\n");
					}
				}
			}
		}
		else {
			// interface
			final InterfaceDec interDec = (InterfaceDec) this.currentPrototype;
			for (final MethodSignature ms : interDec.getMethodSignatureList()) {
				final String signature = ms.getName();
				if ( ms.getFeatureList() != null ) {
					for (final Tuple2<String, WrExprAnyLiteral> t : ms
							.getFeatureList()) {
						// s.append(",\n ");
						s.append(
								"        slotFeatureList_name_does_not_collide__ add: ");
						String strValue;
						// strValue = Lexer.valueToFeatureString(t);
						strValue = t.f2.metaobjectParameterAsString(() -> {
							this.error2(
									meta.GetHiddenItem.getHiddenSymbol(
											t.f2.getFirstSymbol()),
									"This expression cannot be used in a parameter in a metaobject annotation");
						});

						s.append("[. slotName = \"" + signature + "\", key = \""
								+ MetaHelper.removeQuotes(t.f1)
								+ "\", value = Any toAny: " + strValue
								+ " .];\n");
					}
				}
			}
		}
		s.append("        return slotFeatureList_name_does_not_collide__;\n");
		s.append("    }\n");

		/*
		 * if ( numFeatures > 0 ) { s.delete(s.length()-2, s.length()-1);
		 * s.append("        ];\n"); } else { s.delete(sizeS, s.length()); s.
		 * append(" Array<Tuple<slotName, String, key, String, value, Any>>();\n"
		 * ); }
		 */

	}

	/*
	 * check if the real parameters and formal parameters of a program unit are
	 * correct. Formal and real parameters cannot be mixed as in object
	 * Bad<Int><T> ... end
	 *
	 * Return true if the parameter is a generic prototype (with formal
	 * parameters), false otherwise.
	 */
	private boolean checkFormalParameterListGenericPrototype(
			Prototype currentPrototype2) {
		/*
		 * use real parameters such as in object Stack<main.Person> ... end
		 * object Inter<add> ... end object Stack<Int> ... end
		 */
		int useRealParameters = 0;
		/*
		 * use formal parameter such as in object Stack<T> ... end
		 */
		int useFormalParameterWITHOUT_Plus = 0;
		/*
		 * use formal parameter followed by + such as in object Union<T+> ...
		 * end
		 */
		int useFormalParameterWithPlus = 0;
		for (final List<GenericParameter> genericParameterList : currentPrototype2
				.getGenericParameterListList()) {
			for (final GenericParameter gp : genericParameterList) {
				if ( gp.getKind() == GenericParameterKind.FormalParameter ) {
					if ( gp.getPlus() ) {
						useFormalParameterWithPlus = 1;
						if ( genericParameterList.size() != 1 ) {
							error(true, gp.getParameter().getFirstSymbol(),
									"A formal parameter with '+' should appear alone between '<' and '>' in a generic prototype. There is another parameter with "
											+ gp.getName()
											+ "+ between '<' and '>'",
									gp.getParameter().asString(),
									ErrorKind.more_than_one_formal_plus_parameter_in_generic_prototype);
						}
					}
					else
						useFormalParameterWITHOUT_Plus = 1;
				}
				else if ( gp.isRealPrototype() )
					useRealParameters = 1;
				else
					error2(gp.getParameter().getFirstSymbol(),
							"Internal error in Compiler::objectDec");
			}

		}

		if ( useRealParameters + useFormalParameterWITHOUT_Plus
				+ useFormalParameterWithPlus > 1 )
			error(true, currentPrototype2.getSymbol(),
					"Different kinds of parameters (real parameters, formal parameters, formal parameters with +) cannot apper together in the declaration of generic prototype",
					currentPrototype2.getSymbol().getSymbolString(),
					ErrorKind.mixing_of_different_parameter_kinds_in_generic_prototype);

		if ( useFormalParameterWithPlus == 1 ) {
			if ( currentPrototype2.getGenericParameterListList().size() != 1
					|| currentPrototype2.getGenericParameterListList().get(0)
							.size() != 1 )
				error(true, currentPrototype2.getSymbol(),
						"A generic prototype with varying number of parameters should have just one set of pairs '<' and '>' and just one parameter",
						currentPrototype2.getSymbol().getSymbolString(),
						ErrorKind.mixing_of_different_parameter_kinds_in_generic_prototype);
		}

		return useFormalParameterWITHOUT_Plus + useFormalParameterWithPlus > 0;
	}

	/**
	 * analyzes a list of metaobject annotations. It returns tuple (L1, L2) in
	 * which L1 is a list of metaobject annotations that should not be attached
	 * to any declaration and L2 is a list of metaobject annotations that should
	 * be attached to something. If the order is not Non-Attached followed by
	 * Attached, an error is issued.
	 *
	 * We discover if a metaobject should be attached to a declaration or
	 * statement by calling method attachedToSomething
	 *
	 * @return
	 */
	private Tuple2<List<AnnotationAt>, List<AnnotationAt>> parseAnnotations_NonAttached_Attached() {

		List<AnnotationAt> attachedAnnotationList = null;
		List<AnnotationAt> nonAttachedAnnotationList = null;

		AnnotationAt annotation = null;
		int step = 0; // getting non-attached first
		while (symbol.token == Token.METAOBJECT_ANNOTATION) {

			final Symbol ctmoSymbol = symbol;
			try {
				annotation = annotation(false);
			}
			catch (final error.CompileErrorException e) {
				throw e;
			}
			catch (final RuntimeException e) {
				e.printStackTrace();
				this.error2(ctmoSymbol,
						"Runtime exception in metaobject annotation");
			}

			if ( annotation == null ) {
				return new Tuple2<List<AnnotationAt>, List<AnnotationAt>>(
						nonAttachedAnnotationList, attachedAnnotationList);
			}

			CyanMetaobjectAtAnnot withAt = annotation.getCyanMetaobject();
			boolean mayBeAttachedToSomething;
			_CyanMetaobjectAtAnnot other_mo = (_CyanMetaobjectAtAnnot) withAt
					.getMetaobjectInCyan();
			if ( other_mo == null ) {
				// metaobject coded in Java
				mayBeAttachedToSomething = withAt.mayBeAttachedToSomething();
			}
			else {
				// ## {
				// mayBeAttachedToSomething =
				// other_mo._mayBeAttachedToSomething().b;
				// ## }
				mayBeAttachedToSomething = true;
			}
			switch (step) {
			case 0:
				if ( mayBeAttachedToSomething ) {
					step = 1;
					if ( attachedAnnotationList == null )
						attachedAnnotationList = new ArrayList<>();
					attachedAnnotationList.add(annotation);
				}
				else {
					if ( nonAttachedAnnotationList == null )
						nonAttachedAnnotationList = new ArrayList<>();
					nonAttachedAnnotationList.add(annotation);
				}
				break;
			case 1:
				if ( mayBeAttachedToSomething ) {
					if ( attachedAnnotationList == null )
						attachedAnnotationList = new ArrayList<>();
					attachedAnnotationList.add(annotation);
				}
				else {
					/*
					 * found something like
					 *
					 * @feature("author", "José")
					 *
					 * @prototypeCallOnly
					 *
					 * @javacode<<* static int num = 0 *>> func myMethod ->
					 * Boolean [ ... ] This is illegal because javacode is not
					 * linked to any declaration. However, the code
					 *
					 * @javacode<<* static int num = 0 *>>
					 *
					 * @feature("author", "José")
					 *
					 * @prototypeCallOnly func myMethod -> Boolean [ ... ]
					 *
					 * is legal and javacode would not be linked to myMethod
					 */
					final String name = annotation.getCyanMetaobject()
							.getName();
					this.error2(symbol, "metaobject annotation '" + name
							+ "' follows some other metaobject annotations that should be attached"
							+ " to a declaration (variable, method, prototype). However, annotation '"
							+ name
							+ "' should not be attached to a declaration");

				}
			}

		}

		return new Tuple2<List<AnnotationAt>, List<AnnotationAt>>(
				nonAttachedAnnotationList, attachedAnnotationList);
	}

	/**
	 * add one inner prototype for each method declared in this prototype
	 */
	private void addObjectDecForMethods(StringBuffer s) {
		final ObjectDec currentObject = (ObjectDec) currentPrototype;
		final CyanEnv cyanEnv = new CyanEnv(false, false);

		cyanEnv.atBeginningOfPrototype(currentPrototype);

		// String currentObjectName = currentObject.getName();

		// StringBuffer s = new StringBuffer();
		s.append("\n");
		for (final MethodDec method : currentObject.getMethodDecList()) {
			method.genProtoForMethod(s, cyanEnv);
		}
		s.append("\n\n");
		cyanEnv.atEndOfCurrentPrototype();
		/*
		 * the lines below add string 's' to the text of the current compilation
		 * unit. The string is added just before 'symbol'.
		 */

	}

	private void insertPhaseChange(CompilerPhase phase,
			AnnotationAt annotation) {

		final StringBuffer s = new StringBuffer("#" + phase.getName() + "\0");
		final char[] input = s.toString().toCharArray();
		final int inputSize = input.length;
		final Symbol ctmoSymbol = annotation.getFirstSymbol();
		// the "+1" accounts for the '@' before the metaobject name
		compilationUnit.addTextToCompilationUnit(input, inputSize,
				ctmoSymbol.getOffset() + ctmoSymbol.getSymbolString().length()
						+ 1);
		lexer.setInput(compilationUnit.getText());
		/*
		 * the index should be shift by #phaseName which is s.length()
		 */
		lexer.shiftInputIndex(s.length() - 1);
	}

	/**
	 * add text <code>s</code> in the text of this compilation unit at
	 * <code>offset</code>. If allowEndOfLine is true, the text to be inserted
	 * may have '\n'. If allowEndOfLine is false and {@code s} has a '\n' in it
	 * then this method return false.
	 *
	 * @param s
	 */

	private boolean insertTextInput(StringBuffer s, int offset) {
		s.append("\0");
		final char[] input = s.toString().toCharArray();
		final int inputSize = input.length;

		compilationUnit.addTextToCompilationUnit(input, inputSize, offset);
		lexer.setInput(compilationUnit.getText());
		/*
		 * the index of the next symbol to be scanned by lexer should be
		 * decreased by the size of the last symbol found
		 */
		// lexer.shiftInputIndex(-symbol.getSymbolString().length());
		lexer.setInputIndex(offset);

		final int numLinesS = Env.numLinesOf(s);
		// lexer.subFromLineNumber(numLinesS);

		this.lineShift += numLinesS;

		return true;
	}

	/**
	 * add inner prototypes to the current prototype. Each anonymous function
	 * that appears in any method of the current prototype gives origin to a
	 * inner prototype.
	 */
	private void addObjectDecForFunctions(StringBuffer s) {

		final ObjectDec currentObject = (ObjectDec) currentPrototype;

		final String currentObjectName = currentObject.getName();

		s.append("\n");
		s.append(
				"        // a prototype for each of the anonymous functions of prototype "
						+ currentObjectName + " \n\n");

		final CyanEnv cyanEnv = new CyanEnv(true, false);
		cyanEnv.atBeginningOfPrototype(currentPrototype);

		for (final ExprFunction function : currentObject.getFunctionList()) {
			s.append(function.genContextObjectForFunction(cyanEnv));
		}
		s.append("\n\n");
		cyanEnv.atEndOfCurrentPrototype();

	}

	/**
	 * add to parameter <code>s</code> methods that every prototype should have
	 * and that the user cannot define. For a prototype <code>Proto</code>,
	 * these methods are:
	 * </p>
	 * <code>
	 * override
	 * func prototype -> Proto</p>
	 * </code>
	 *
	 * @param s
	 */
	private void addMethodsEveryPrototypeHas(StringBuffer s) {

		final ObjectDec currentObject = (ObjectDec) currentPrototype;
		final String currentObjectName = currentObject.getName();
		String currentObjectTypeName;

		List<MethodSignature> methodSignatureList;

		/**
		 * For each interface "Inter" the compiler creates a prototype
		 * "Proto_Inter" that is used whenever the interface appears, in the
		 * Cyan code, as an expression. Compilation units that has these
		 * compiler-created prototypes return true in method
		 * getIsPrototypeInterface(). All methods added by the compiler that use
		 * the prototype name should use "Inter" in prototype "Proto_Inter".
		 * Then, for example, method override func prototype -> P is added to
		 * prototype P. But in the compiler-created prototype "Proto_Inter",
		 * this method is override func prototype -> Inter
		 *
		 *
		 * In regular prototypes, currentObjectTypeName is equal to the
		 * prototype name. In a compiler-created prototype "Proto_Inter" created
		 * because of an interface "Inter", currentObjectTypeName is equal to
		 * the interface name, "Inter".
		 *
		 */
		if ( NameServer.isPrototypeFromInterface(currentObject.getName()) ) {

			// if ( compilationUnit.getIsPrototypeInterface() ) {
			// was created from an interface by the compiler. Use the interface
			// name as parameter
			currentObjectTypeName = NameServer
					.interfaceNameFromPrototypeName(currentObjectName);
		}
		else
			currentObjectTypeName = currentObjectName;

		final StringBuffer nameWithSpaces = new StringBuffer();
		for (int ii = 0; ii < currentObjectTypeName.length(); ++ii) {
			final char ch = currentObjectTypeName.charAt(ii);
			nameWithSpaces.append(ch);
			if ( ch == ',' ) nameWithSpaces.append(' ');
		}
		currentObjectTypeName = nameWithSpaces.toString();
		// S ystem.out.p rintln("addMethods: " + currentObjectName);

		s.append("\n");
		s.append("    // Methods added by the compiler\n");

		// prototype cannot be user-defined
		/**
		 * override public func prototype -> Proto { return Proto; }
		 *
		 */

		methodSignatureList = currentObject
				.searchMethodPrivateProtectedPublic("prototype");
		if ( methodSignatureList.size() == 0 ) {
			s.append("    override");
			s.append("    func prototype -> "
					+ Lexer.addSpaceAfterComma(currentObjectTypeName) + " {\n");
			s.append("        @javacode{* return ");
			if ( this.currentPrototype.getOuterObject() == null )
				s.append("prototype");
			else
				s.append("prototype" + currentObjectName);
			s.append(";\n");
			s.append("        *}\n");
			s.append("    } \n");
		}
		else if ( currentObjectName.compareTo("Any") != 0 )
			error(true, currentObject.getSymbol(),
					"Method 'prototype' cannot be user-defined", "prototype",
					ErrorKind.method_cannot_be_user_defined);

	}

	/**
	 * add to parameter <code>s</code> methods that every prototype should have
	 * and that were not defined in the source code. For every <code>init</code>
	 * and <code>init:</code> method the compiler adds a <code>new</code> and
	 * <code>new:</code> method. If the source code does not define method
	 * <code>clone</code> {@link addMethodsToPrototype} adds this method to the
	 * current prototype.
	 *
	 * @param s
	 */
	private void addMethodsToPrototypeIfNotDefined(StringBuffer s) {

		final ObjectDec currentObject = (ObjectDec) currentPrototype;

		final String currentObjectName = currentObject.getName();
		String currentObjectTypeName;
		final String currentObjectJavaName = MetaHelper
				.getJavaName(currentObjectName);

		List<MethodSignature> methodSignatureList;

		/**
		 * For each interface "Inter" the compiler creates a prototype
		 * "Proto_Inter" that is used whenever the interface appears, in the
		 * Cyan code, as an expression. Compilation units that has these
		 * compiler-created prototypes return true in method
		 * getIsPrototypeInterface(). All methods added by the compiler that use
		 * the prototype name should use "Inter" in prototype "Proto_Inter".
		 * Then, for example, method override func prototype -> P is added to
		 * prototype P. But in the compiler-created prototype "Proto_Inter",
		 * this method is override func prototype -> Inter
		 *
		 *
		 * In regular prototypes, currentObjectTypeName is equal to the
		 * prototype name. In a compiler-created prototype "Proto_Inter" created
		 * because of an interface "Inter", currentObjectTypeName is equal to
		 * the interface name, "Inter".
		 *
		 */
		final boolean isPrototypeFromInterface = NameServer
				.isPrototypeFromInterface(currentObject.getName());
		if ( isPrototypeFromInterface ) {
			// if ( compilationUnit.getIsPrototypeInterface() ) {
			// was created from an interface by the compiler. Use the interface
			// name as parameter
			currentObjectTypeName = NameServer
					.interfaceNameFromPrototypeName(currentObjectName);
		}
		else
			currentObjectTypeName = currentObjectName;

		// S ystem.out.p rintln("addMethods: " + currentObjectName);

		s.append("\n");
		s.append("    // Methods added by the compiler\n");

		// add clone method
		methodSignatureList = currentObject
				.searchMethodPrivateProtectedPublic("clone");
		if ( methodSignatureList.size() == 0 ) {
			// add clone method
			/**
			 * func clone -> Proto { ... }
			 */
			s.append("\n    override");
			s.append("    func clone -> "
					+ Lexer.addSpaceAfterComma(currentObjectTypeName) + " {\n");

			s.append("        @javacode<<*\n");

			if ( isPrototypeFromInterface ) {
				s.append(
						"        throw new ExceptionContainer__(_ExceptionCannotCallInterfaceMethod.prototype);\n");

			}
			else {
				s.append("        try {\n");
				s.append(
						"            return (" + currentObjectJavaName + " ) ");
				/*
				 * s.append("prototype"); if (
				 * this.currentPrototype.getOuterObject() != null )
				 * s.append(this.currentPrototype.getName());
				 */
				s.append("this.clone(); \n");
				s.append(
						"        } catch (CloneNotSupportedException e) { }\n");
				s.append("        return null;\n");

			}
			s.append("        *>>");
			s.append("    } \n");
		}
		else if ( methodSignatureList.size() == 1 ) {
			// check whether the return type of close is equal to the current
			// prototype.
			// A clone should have the signature "close -> T" in which T is the
			// current prototype
			if ( methodSignatureList.get(0).getReturnTypeExpr()
					.ifPrototypeReturnsItsName()
					.compareTo(currentObjectName) != 0
					&& methodSignatureList.get(0).getReturnTypeExpr()
							.ifPrototypeReturnsItsName()
							.compareTo(this.compilationUnit.getPackageName()
									+ "." + currentObjectName) != 0 )
				this.error2(currentObject.getSymbol(),
						"Method 'clone' has a wrong signature. It should be 'clone -> "
								+ currentObjectName + "'");
		}
		else if ( methodSignatureList.size() > 1 )
			error2(currentObject.getSymbol(),
					"Two or more 'clone' methods. There should be only one");

		// add 'new' method
		methodSignatureList = currentObject.searchInitNewMethod("init");
		boolean hasAtLeastOneInitMethod = methodSignatureList.size() > 0;
		if ( methodSignatureList.size() == 1 ) {
			final MethodSignature initSignature = methodSignatureList.get(0);

			// MethodDec initMethod = initSignature.getMethod();

			/*
			 * Expr returnTypeExpr = initSignature.getReturnTypeExpr(); // the
			 * return value should be null or Void if ( returnTypeExpr != null
			 * && returnTypeExpr.ifPrototypeReturnsItsName().compareTo( "Nil")
			 * != 0 && returnTypeExpr.ifPrototypeReturnsItsName()
			 * .compareTo(this.compilationUnit.getPackageName() + "." +
			 * currentObjectName) != 0 ) { error(true,
			 * initSignature.getFirstSymbol(),
			 * "Methods 'init' and 'init:' should have no return value type",
			 * "init", ErrorKind.init_should_return_Nil); } methodSignatureList
			 * = currentObject .searchMethodPrivateProtectedPublic("new"); if (
			 * methodSignatureList.size() != 0 )
			 * error2(initSignature.getFirstSymbol(),
			 * "You cannot declare both methods 'init' and 'new' in the same prototype"
			 * );
			 */

			// add new method
			s.append("\n    ");
			if ( initSignature.getMethod().getVisibility() == Token.PRIVATE ) {
				s.append("private ");
			}
			else if ( initSignature.getMethod()
					.getVisibility() == Token.PROTECTED ) {
				s.append("protected ");
			}

			s.append("func new -> "
					+ Lexer.addSpaceAfterComma(currentObjectTypeName) + " {\n");

			s.append("         @javacode<**< \n");
			s.append("            return new ");
			s.append(currentObjectJavaName + "();");
			s.append("         >**>\n");

			s.append("    }\n");

		}
		else if ( methodSignatureList.size() > 1 )
			error(true, currentObject.getSymbol(),
					"There is a 'init' method declared in line "
							+ methodSignatureList.get(0).getFirstSymbol()
									.getLineNumber()
							+ " and at least another declared in line "
							+ methodSignatureList.get(1).getFirstSymbol()
									.getLineNumber(),
					"init", ErrorKind.two_or_more_init_methods);

		// add 'new:' methods
		// ##
		methodSignatureList = currentObject
				.searchInitNewMethodBySelectorList("init:");
		hasAtLeastOneInitMethod = hasAtLeastOneInitMethod
				|| methodSignatureList.size() > 0;
		int initMethodNumber = 0;
		if ( methodSignatureList.size() > 0 ) {
			initMethodNumber = methodSignatureList.get(0).getMethod()
					.getMethodNumber();

		}

		if ( currentObject.getContextParameterArray() != null ) {
			/*
			 * Is there any init method with the parameters types given in the
			 * prototype head? For example, in object Sum(Int s) ... end We
			 * check if there is an init method with an "Int" as parameter.
			 */
			/*
			 * List<ContextParameter> cpArray =
			 * currentObject.getContextParameterArray(); boolean foundInit =
			 * true; for (MethodSignature methodSignature : methodSignatureList)
			 * { int indexcp = 0; MethodSignatureWithKeywords ms =
			 * (MethodSignatureWithKeywords ) methodSignature; if (
			 * ms.getkeywordArray().get(0).getParameterList().size() !=
			 * cpArray.size() ) foundInit = false; else { for ( ParameterDec
			 * paramDec : ms.getkeywordArray().get(0).getParameterList() ) { if
			 * ( indexcp >= cpArray.size() ) break; ContextParameter cp =
			 * cpArray.get(indexcp); if ( !
			 * paramDec.getTypeInDec().ifPrototypeReturnsItsName().equals(
			 * cp.getTypeInDec().ifPrototypeReturnsItsName()) ) { foundInit =
			 * false; } } } if ( foundInit ) { error(true, null,
			 * "Prototype with an 'init:' method with the same parameter types as those in the prototype head"
			 * , "", ErrorKind.two_or_more_init_methods); } }
			 */

			// add 'init:' method
			s.append("\n");
			s.append("    func init: ");
			final StringBuffer initBody = new StringBuffer();
			initBody.append("        @javacode{*\n");

			int sizecp = currentObject.getContextParameterArray().size();
			for (final ContextParameter cp : currentObject
					.getContextParameterArray()) {
				if ( cp.getTypeInDec() != null ) {
					if ( cp.getTypeInDec() instanceof ExprIdentStar ) {
						s.append(((ExprIdentStar) cp.getTypeInDec())
								.getNameWithAttachedTypes() + " ");
					}
					else if ( cp
							.getTypeInDec() instanceof ExprGenericPrototypeInstantiation ) {
						s.append(((ExprGenericPrototypeInstantiation) cp
								.getTypeInDec()).getNameWithAttachedTypes()
								+ " ");
					}
					else {
						s.append(cp.getTypeInDec().ifPrototypeReturnsItsName()
								+ " ");
					}
				}
				else {
					s.append("Dyn ");
				}
				if ( cp.getVariableKind() != VariableKind.COPY_VAR )
					s.append(cp.getVariableKind().toString());
				String ivName;
				if ( cp.getVisibility() == Token.PUBLIC )
					ivName = "_" + cp.getName();
				else
					ivName = cp.getName();
				s.append(ivName);
				final String ivJavaName = MetaHelper.getJavaName(ivName);
				if ( currentObject.searchField(cp.getName()) != null ) {
					// context paramter is a field
					initBody.append("        this." + ivJavaName + " = "
							+ ivJavaName + ";\n");
				}

				if ( --sizecp > 0 ) s.append(", ");
			}
			s.append(" {\n");
			if ( currentObject.getSuperContextParameterList() != null
					&& currentObject.getSuperContextParameterList()
							.size() > 0 ) {
				/*
				 * super init: aa, bb;
				 */
				s.append("    super init: ");
				int sizescpl = currentObject.getSuperContextParameterList()
						.size();
				for (final ContextParameter cp : currentObject
						.getSuperContextParameterList()) {
					s.append(cp.getName());
					if ( --sizescpl > 0 ) s.append(", ");
				}
				s.append(";\n");
			}
			initBody.append("        *}\n");
			s.append(initBody);
			s.append("    }\n");

			// add 'new' method
			s.append("\n");
			s.append("    func new: ");

			sizecp = currentObject.getContextParameterArray().size();
			for (final ContextParameter cp : currentObject
					.getContextParameterArray()) {
				String strType = "";
				if ( cp.getTypeInDec() != null ) {
					if ( cp.getTypeInDec() instanceof ExprIdentStar ) {
						strType = ((ExprIdentStar) cp.getTypeInDec())
								.getNameWithAttachedTypes();
					}
					else if ( cp
							.getTypeInDec() instanceof ExprGenericPrototypeInstantiation ) {
						strType = ((ExprGenericPrototypeInstantiation) cp
								.getTypeInDec()).getNameWithAttachedTypes();
					}
					else {
						strType = cp.getTypeInDec().ifPrototypeReturnsItsName();
					}
				}
				else {
					strType = "Dyn";
				}
				s.append(Lexer.addSpaceAfterComma(strType) + " ");
				// s.append(
				// Lexer.addSpaceAfterComma(cp.getTypeInDec().ifPrototypeReturnsItsName())
				// + " ");
				if ( cp.getVariableKind() != VariableKind.COPY_VAR )
					s.append(cp.getVariableKind().toString());
				s.append(cp.getName());
				if ( --sizecp > 0 ) s.append(", ");
			}
			s.append(" -> " + Lexer.addSpaceAfterComma(currentObjectTypeName));
			s.append(" {\n");

			s.append("        @javacode<**< \n");
			s.append("            return new ");
			s.append(currentObjectJavaName + "(");

			int sizecpa = currentObject.getContextParameterArray().size();
			for (final ContextParameter cp : currentObject
					.getContextParameterArray()) {
				final String ivJavaName = MetaHelper.getJavaName(cp.getName());
				s.append(ivJavaName);
				if ( --sizecpa > 0 ) {
					s.append(", ");
				}
			}
			s.append(");\n");
			s.append("        >**>\n");
			s.append("    }\n");

		}

		if ( !currentObject.getIsAbstract() ) {
			for (final MethodSignature methodSignature : methodSignatureList) {
				/*
				 * for each 'init:' method, create a 'new:' method
				 */

				if ( initMethodNumber != methodSignature.getMethod()
						.getMethodNumber()
						&& !methodSignature.getMethod()
								.getCompilerCreatedMethod()
						&& methodSignature.getMethod().getAnnotContextStack()
								.size() == 0 ) {
					error(true, methodSignature.getFirstSymbol(),
							"Method of line "
									+ methodSignature.getFirstSymbol()
											.getLineNumber()
									+ " should be declared right after the previous method 'init:'",
							"init:",
							ErrorKind.method_should_be_declared_after_previous_method_with_the_same_keywords);
				}
				++initMethodNumber;

				final MethodSignatureWithKeywords initSignature = (MethodSignatureWithKeywords) methodSignature;

				if ( initSignature.getMethod().getHasOverride() ) error(true,
						initSignature.getFirstSymbol(),
						"Method 'init:' cannot be declared with 'override'",
						"init",
						ErrorKind.init_should_not_be_declared_with_override);

				final Expr returnTypeExpr = initSignature.getReturnTypeExpr();
				// the return value should be null or Void
				if ( returnTypeExpr != null
						&& returnTypeExpr.ifPrototypeReturnsItsName()
								.compareTo("Nil") != 0
						&& returnTypeExpr.ifPrototypeReturnsItsName()
								.compareTo("cyan.lang.Nil") != 0 )
					error(true, initSignature.getFirstSymbol(),
							"Methods 'init' and 'init:' should have 'Nil' as return value or no return value type",
							"init", ErrorKind.init_should_return_Nil);

				// add new method
				s.append("\n");
				s.append("    ");
				if ( initSignature.getMethod()
						.getVisibility() == Token.PRIVATE ) {
					s.append("private ");
				}
				else if ( initSignature.getMethod()
						.getVisibility() == Token.PROTECTED ) {
					s.append("protected ");
				}
				else if ( initSignature.getMethod()
						.getVisibility() == Token.PACKAGE ) {
					s.append("package ");
				}

				s.append("func new: ");

				final List<MethodKeywordWithParameters> keywordArray = initSignature
						.getKeywordArray();
				final MethodKeywordWithParameters initkeyword = keywordArray
						.get(0);
				final List<ParameterDec> parameterList = initkeyword
						.getParameterList();
				int size = parameterList.size();
				if ( size > 0 ) s.append("( ");
				int indexParam = 0;
				for (final ParameterDec p : parameterList) {

					String typeParam;
					if ( p.getTypeInDec() != null ) {
						if ( p.getTypeInDec() instanceof ExprIdentStar ) {
							typeParam = ((ExprIdentStar) p.getTypeInDec())
									.getNameWithAttachedTypes();
						}
						else if ( p
								.getTypeInDec() instanceof ExprGenericPrototypeInstantiation ) {
							typeParam = ((ExprGenericPrototypeInstantiation) p
									.getTypeInDec()).getNameWithAttachedTypes();
						}
						else {
							typeParam = p.getTypeInDec()
									.ifPrototypeReturnsItsName();
						}
					}
					else {
						typeParam = "Dyn";
					}

					typeParam = Lexer.addSpaceAfterComma(typeParam);

					/*
					 * if ( p.getTypeInDec() == null ) typeParam = "Dyn"; else
					 * typeParam = Lexer.addSpaceAfterComma(p.getTypeInDec().
					 * ifPrototypeReturnsItsName());
					 */

					s.append(typeParam + " p" + indexParam);
					if ( --size > 0 ) s.append(", ");
					++indexParam;
				}
				if ( parameterList.size() > 0 ) s.append(" )");

				s.append(" -> "
						+ Lexer.addSpaceAfterComma(currentObjectTypeName));
				s.append(" {\n");

				s.append("        @javacode<**< \n");
				s.append("            return new ");
				s.append(currentObjectJavaName + "(");
				size = parameterList.size();
				for (int ii = 0; ii < parameterList.size(); ++ii) {
					s.append("_p" + ii);
					if ( --size > 0 ) s.append(", ");
				}

				s.append(");\n");
				s.append("        >**>\n");
				s.append("    }\n");

			}

		}

		if ( !hasAtLeastOneInitMethod && (currentObject
				.getContextParameterArray() == null
				|| currentObject.getContextParameterArray().size() == 0) ) {
			if ( currentObject.getFieldList().size() > 0 ) {
				/*
				 * there is no init or init: method and the prototype has
				 * fields.
				 */
				final List<FieldDec> nonInitializedField = new ArrayList<>();
				for (final FieldDec iv : currentObject.getFieldList()) {
					if ( iv.getExpr() == null && !iv.isShared() ) {
						nonInitializedField.add(iv);
						break;
					}
				}
				if ( nonInitializedField.size() > 0 ) {
					int size = nonInitializedField.size();
					String strList = "";
					for (final FieldDec v : nonInitializedField) {
						strList += v.getName();
						if ( --size > 0 ) strList += ", ";
					}
					if ( nonInitializedField.size() == 1 )
						strList = "The field " + strList
								+ " is not initialized in its declaration ";
					else
						strList = "The fields " + strList
								+ " are not initialized in their declarations ";
					this.error2(this.currentPrototype.getSymbol(), strList
							+ "and the prototype does not declare any 'init' or 'init:' method. This is illegal");
				}
			}

			// create an empty init method
			s.append("    func init { } \n");
			currentObject.setCreatedInitMethod(true);

			// add new method
			if ( !currentObject.getIsAbstract() ) {
				s.append("\n    func new -> "
						+ Lexer.addSpaceAfterComma(currentObjectTypeName)
						+ " {\n");

				s.append("        @javacode<**< \n");
				s.append("            return new ");
				s.append(currentObjectJavaName + "();");
				s.append("        >**>\n");
				s.append("    }\n");
			}

		}

	}

	private List<GenericParameter> templateDec() {

		/**
		 * true if the file name of this compilation unit is something like
		 * Set(1).cyan That is, there is a number after '('. This means that the
		 * identifier that follows '<' in the declaration of Set is a *generic
		 * parameter* and it is not a real prototype. Note that if mixed generic
		 * prototypes were allowed, with file names Hashtable(1, String).cyan
		 * and declarations like object Hashtable<T, String> ... end then it
		 * will be necessary to use an array of boolean. Each position could be
		 * a generic parameter or a real prototype.
		 *
		 *
		 * int indexLeftPar = compilationUnit.getFilename().indexOf('('); if (
		 * indexLeftPar >= 0 ) { char ch =
		 * compilationUnit.getFilename().charAt(indexLeftPar + 1); if (
		 * Character.isDigit(ch) ) hasGenericParameter = true; }
		 */

		final List<GenericParameter> genericParameterList = new ArrayList<GenericParameter>();
		next();
		genericParameterList.add(templateVarDec());
		while (symbol.token == Token.COMMA) {
			lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
			next();
			genericParameterList.add(templateVarDec());
		}
		if ( symbol.token != Token.GT ) {
			error2(symbol, "> expected after the types of a generic object."
					+ foundSuch());
		}
		next();

		return genericParameterList;
	}

	private GenericParameter templateVarDec() {

		GenericParameter genericParameter;

		Expr type;

		try {
			this.prohibitTypeof = true;
			type = type();
		}
		finally {
			this.prohibitTypeof = false;
		}

		if ( symbol.token == Token.IDENT ) {
			/*
			 * something like Stack< Person T >
			 */
			error2(symbol, "A ',' or '>' expected instead of an identifier."
					+ foundSuch());
			genericParameter = null;
		}
		else {
			/*
			 * type may be: (a) a real parameter such as Int or main.Person:
			 * object Stack<Int> ... end object Stack<main.Person> ... end (b)
			 * or a formal parameter such as T in object Stack<T> ... end or
			 * object Stack<T+> ... end (c) a real parameter such as 'add' in
			 * object Inter<add> ... end
			 */

			/*
			 * if ( ! (type instanceof ExprIdentStar) )
			 * error(type.getFirstSymbol(), "Generic parameter expected",
			 * type.asString(), ErrorKind.generic_parameter_expected);
			 */

			if ( type instanceof ExprTypeUnion ) {
				return new GenericParameter(type,
						GenericParameterKind.PrototypeWithPackage);

			}

			List<Symbol> paramSymbolArray;
			if ( type instanceof ExprIdentStar )
				paramSymbolArray = ((ExprIdentStar) type).getIdentSymbolArray();
			else if ( type instanceof ast.ExprGenericPrototypeInstantiation )
				paramSymbolArray = ((ast.ExprGenericPrototypeInstantiation) type)
						.getTypeIdent().getIdentSymbolArray();
			else {
				error(true, type.getFirstSymbol(),
						"Generic parameter expected." + foundSuch(),
						type.asString(), ErrorKind.generic_parameter_expected);
				return null;
			}

			if ( paramSymbolArray.size() == 1 ) {
				final Symbol sym = paramSymbolArray.get(0);
				String s = sym.getSymbolString();
				final int indexLessThan = s.indexOf('<');
				if ( indexLessThan > 0 )
					// "Tuple<Char, Int>" becomes "Tuple"
					s = s.substring(0, indexLessThan);

				if ( this.compilationUnit.getProgram().isInPackageCyanLang(s)
						|| s.equals(MetaHelper.dynName) ) {
					/*
					 * (a) a real parameter such as Int or main.Person: object
					 * Stack<Int> ... end object Stack<Tuple<Char, Int>> ... end
					 */
					genericParameter = new GenericParameter(type,
							GenericParameterKind.PrototypeCyanLang);

				}
				else if ( Character
						.isLowerCase(sym.getSymbolString().charAt(0)) ) {
					/*
					 * (c) a real parameter such as 'add' in object Inter<add>
					 * ... end
					 */
					genericParameter = new GenericParameter(type,
							GenericParameterKind.LowerCaseSymbol);
				}
				else {
					/*
					 * (b) or a formal parameter such as T in object Stack<T>
					 * ... end or object Stack<T+> ... end
					 */
					genericParameter = new GenericParameter(type,
							GenericParameterKind.FormalParameter);
					if ( symbol.token == Token.PLUS ) {
						next();
						genericParameter.setPlus(true);
					}
				}
			}
			else {
				/*
				 * (a) a real parameter such as cyan.lang.Int or main.Person:
				 * object Stack<cyan.lang.Int> ... end object Stack<main.Person>
				 * ... end object Stack<cyan.lang.Tuple<Char, Int>> ... end
				 */
				String s = type.asString();
				final int indexCyanLang = s
						.indexOf(MetaHelper.cyanLanguagePackageName);
				if ( indexCyanLang == 0 ) {
					final int indexLessThan = s.indexOf('<');
					if ( indexLessThan > 0 )
						// "cyan.lang.Tuple<Char, Int>" becomes
						// "cyan.lang.Tuple"
						s = s.substring(0, indexLessThan);
					final String protoName = s.substring(
							MetaHelper.cyanLanguagePackageName.length() + 1,
							s.length());
					if ( this.compilationUnit.getProgram()
							.isInPackageCyanLang(protoName)
							|| protoName.equals(MetaHelper.dynName) ) {
						/*
						 * (a) a real parameter such as object
						 * Stack<cyan.lang.Int> ... end
						 */
						genericParameter = new GenericParameter(type,
								GenericParameterKind.PrototypeCyanLang);

					}
					else
						genericParameter = new GenericParameter(type,
								GenericParameterKind.PrototypeWithPackage);

				}
				else {
					genericParameter = new GenericParameter(type,
							GenericParameterKind.PrototypeWithPackage);
				}
			}
		}
		return genericParameter;
	}

	/*
	 * private GenericParameter templateVarDecDelete(boolean
	 * hasGenericParameter) {
	 *
	 *
	 * GenericParameter genericParameter;
	 *
	 *
	 * if ( hasGenericParameter ) { Expr t = type(); Symbol
	 * genericParameterSymbol; if ( symbol.token == Token.IDENT ) {
	 * genericParameter = new GenericParameter(symbol);
	 * genericParameter.setParameterType(t); next(); } else {
	 *
	 * //the type is in fact the parameter name
	 *
	 * if ( ! (t instanceof ExprIdentStar) || ((ExprIdentStar
	 * )t).getIdentSymbolArray().size() != 1 ) error2( t.getFirstSymbol(),
	 * "Generic parameter expected" ); genericParameterSymbol = ((ExprIdentStar
	 * ) t).getIdentSymbolArray().get(0); genericParameter = new
	 * GenericParameter(genericParameterSymbol); }
	 * currentPrototype.setGenericPrototype(true); } else {
	 *
	 * // a real prototype as formal parameter such as Boolean in // object
	 * MyGeneric<Int><Boolean> ... end
	 *
	 * if ( ! startType(symbol.token) ) error2(symbol,
	 * "type expected in generic object declaration"); Expr realParameter =
	 * exprPrimary(); genericParameter = new GenericParameter(realParameter); }
	 * return genericParameter; }
	 */

	/**
	 * ContextDec ::= "(" CtxtObjParamDec { "," CtxtObjParamDec } ")"
	 *
	 * @return
	 */
	private List<ContextParameter> contextDec() {
		final List<ContextParameter> contextParameterArray = new ArrayList<ContextParameter>();
		next();
		CtxtObjParamDec(contextParameterArray);
		while (symbol.token == Token.COMMA) {
			lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
			next();
			CtxtObjParamDec(contextParameterArray);
		}
		if ( symbol.token != Token.RIGHTPAR ) error2(symbol,
				"')' expected after the declaration of variables of a context object."
						+ foundSuch());
		if ( Lexer.hasIdentNumberAfter(symbol, compilationUnitSuper) ) {
			error2(symbol, "letter, number, or '_' after ')'");
		}

		next();
		return contextParameterArray;
	}

	/*
	 * CtxtObjParamDec ::= [ ``public"\/ \verb@|@ ``protected"\/ \verb@|@
	 * ``private"\/ ] Type \\ [ ``\%"\/ \verb"|" ``\&"\/ \verb"|" ``*"\/ ] Id
	 */
	private void CtxtObjParamDec(List<ContextParameter> contextParameterArray) {

		VariableKind parameterType;
		Token visibility = Token.PRIVATE;

		List<AnnotationAt> attachedAnnotationList = null;
		List<AnnotationAt> nonAttachedAnnotationList = null;

		final Tuple2<List<AnnotationAt>, List<AnnotationAt>> tc = parseAnnotations_NonAttached_Attached();
		if ( tc != null ) {
			nonAttachedAnnotationList = tc.f1;
			attachedAnnotationList = tc.f2;
		}
		if ( nonAttachedAnnotationList != null ) {
			this.error(true, nonAttachedAnnotationList.get(0).getFirstSymbol(),
					"This metaobject annotation of metaobject '"
							+ nonAttachedAnnotationList.get(0)
									.getCyanMetaobject().getName()
							+ "' cannot be attached to a context object or any other declaration."
							+ "just before the specification of a Program.",
					null, ErrorKind.metaobject_error);
		}
		if ( attachedAnnotationList != null ) {
			for (final AnnotationAt annotation : attachedAnnotationList) {
				final CyanMetaobjectAtAnnot cyanMetaobject = annotation
						.getCyanMetaobject();
				if ( !cyanMetaobject
						.mayBeAttachedTo(AttachedDeclarationKind.FIELD_DEC) ) {
					this.error(true,
							attachedAnnotationList.get(0).getFirstSymbol(),
							"This metaobject annotation cannot be attached to a field. It can be attached to "
									+ " one entity of the following list: [ "
									+ cyanMetaobject.attachedListAsString()
									+ " ]",
							null, ErrorKind.metaobject_error);

				}
			}
		}

		final Symbol firstSymbol = symbol;
		if ( symbol.token == Token.PUBLIC || symbol.token == Token.PROTECTED
				|| symbol.token == Token.PRIVATE ) {
			error2(symbol,
					"Context parameters cannot have qualifier. Currently they are always private. That will change someday");
			visibility = symbol.token;
			next();
		}
		Expr type;

		try {
			this.prohibitTypeof = true;
			type = type();
		}
		finally {
			this.prohibitTypeof = false;
		}

		if ( symbol.token == Token.BITAND ) {
			/*
			 * if ( ! this.currentPrototype.getIsFinal() ) { this.error2(symbol,
			 * "This prototype has a reference context parameter as 'sum' in " +
			 * "\n    object Sum(Int &sum) ... end\n" +
			 * "Therefore it should be a final prototype, it cannot be declared with 'open'"
			 * ); }
			 */
			parameterType = VariableKind.LOCAL_VARIABLE_REF;
			next();
		}
		else {
			parameterType = VariableKind.COPY_VAR;
		}
		if ( symbol.token != Token.IDENT ) {
			error2(symbol, "identifier expected." + foundSuch());
		}
		final ContextParameter contextParameter = new ContextParameter(
				(ObjectDec) this.currentPrototype, (SymbolIdent) symbol,
				parameterType, type, visibility, firstSymbol,
				nonAttachedAnnotationList, attachedAnnotationList,
				cyanMetaobjectContextStack);

		final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
				.get(symbol.getSymbolString());
		if ( cyanMacro != null ) {
			this.error2(symbol,
					"This field has the name of a macro keyword of a macro imported by this compilation unit");
		}

		if ( attachedAnnotationList != null ) {
			for (final AnnotationAt annotation : attachedAnnotationList) {
				if ( annotation.getCyanMetaobject()
						.mayBeAttachedTo(AttachedDeclarationKind.FIELD_DEC) ) {
					annotation.setDeclaration(contextParameter.getI());
				}
				else {
					/*
					 * the metaobject cannot be attached to a field
					 */
					this.error(true, annotation.getFirstSymbol(),
							"This metaobject annotation cannot be attached to a field. It can be attached to "
									+ " one entity of the following list: [ "
									+ annotation.getCyanMetaobject()
											.attachedListAsString()
									+ " ]",
							null, ErrorKind.metaobject_error);

				}
				final CyanMetaobject cyanMetaobject = annotation
						.getCyanMetaobject();
				actionCompilerInfo_parsing(cyanMetaobject);
			}
		}

		next();
		contextParameterArray.add(contextParameter);
		((ObjectDec) this.currentPrototype).addField(contextParameter);
		((ObjectDec) this.currentPrototype).addSlot(contextParameter);
		((ObjectDec) this.currentPrototype).setHasContextParameter(true);
	}

	/**
	 * @param cyanMetaobject
	 */
	private void actionCompilerInfo_parsing(
			final CyanMetaobject cyanMetaobject) {
		_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
		if ( cyanMetaobject instanceof ICompilerInfo_parsing || (other != null
				&& other instanceof _ICompilerInfo__parsing) ) {
			int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(this,
					program, this.compilationUnit.getCyanPackage(),
					meta.GetHiddenItem
							.getHiddenCyanAnnotation(
									cyanMetaobject.getAnnotation())
							.getFirstSymbol());

			Timeout<Object> to = new Timeout<>();

			if ( other == null ) {
				final ICompilerInfo_parsing moInfo = (ICompilerInfo_parsing) cyanMetaobject;

				to.run(Executors.callable(() -> {
					moInfo.action_parsing(getCompilerAction_parsing());
				}), timeoutMilliseconds, "action_parsing", cyanMetaobject,
						project);

				// moInfo.action_parsing(getCompilerAction_parsing());
			}
			else {
				final _ICompilerInfo__parsing moInfo = (_ICompilerInfo__parsing) cyanMetaobject;

				to.run(Executors.callable(() -> {
					moInfo._action__parsing_1(getCompilerAction_parsing());
				}), timeoutMilliseconds, "action_parsing", cyanMetaobject,
						project);

				//
			}
		}
	}

	private ICompilerAction_parsing compilerAction_parsing = null;

	private ICompilerAction_parsing getCompilerAction_parsing() {
		if ( compilerAction_parsing == null ) {
			compilerAction_parsing = new CompilerAction_parsing(this);
		}
		return compilerAction_parsing;
	}

	public Expr type() {
		return type(true);
	}

	/**
	 * parse a type that may have a "|" in it as Int | Char If
	 * leftParAllowedAfterOr is true, it is allowed to have types like var
	 * Int|(Char|String) x;
	 *
	 * If false, this is an error. The call 'type(false)' is only used by
	 * metaobject grammarMethod. This is necessary in cases like: (addElem: Int
	 * | addElem: String) or (addElem: Int | (addElem: String)* )
	 *
	 * @return
	 */
	public Expr type(boolean leftParAllowedAfterOr) {
		Expr t = typeAnd();
		/*
		 * if ( symbol.token != Token.BITOR || symbol.token == Token.BITOR &&
		 * (next(0).token == Token.IDENTCOLON || next(0).token == Token.LEFTPAR)
		 * ) { return t; }
		 */

		boolean typeAfterOr;
		if ( leftParAllowedAfterOr ) {
			typeAfterOr = true;
		}
		else {
			typeAfterOr = next(0).token != Token.IDENTCOLON
					&& next(0).token != Token.LEFTPAR;
		}
		if ( symbol.token != Token.BITOR ) {
			return t;
		}
		else if ( !typeAfterOr ) {
			return t;
		}
		else {
			final List<Expr> typeArray = new ArrayList<Expr>();
			if ( t instanceof ExprTypeof ) error(true, t.getFirstSymbol(),
					"'typeof' cannot be used in union types", "",
					ErrorKind.typeof_used_in_union);
			typeArray.add(t);
			while (symbol.token == Token.BITOR) {
				next();
				if ( symbol.token == Token.LEFTPAR ) {
					next();
					t = type();
					if ( t instanceof ExprTypeUnion ) {
						ExprTypeUnion etu = (ExprTypeUnion) t;
						for (Expr ty : etu.getTypeList()) {
							typeArray.add(ty);
						}
					}
					else {
						if ( t instanceof ExprTypeof ) {
							error2(t.getFirstSymbol(),
									"'typeof' cannot be used in union types");
						}
						typeArray.add(t);
					}
					if ( symbol.token != Token.RIGHTPAR ) {
						this.error2(symbol,
								"')' expected after this type because it started with '('");
					}
					next();
				}
				else {
					t = typeAnd();
					if ( t instanceof ExprTypeof )
						error(true, t.getFirstSymbol(),
								"'typeof' cannot be used in union types", "",
								ErrorKind.typeof_used_in_union);
					typeArray.add(t);
				}

			}
			ExprTypeUnion unionType = new ExprTypeUnion(this.currentMethod,
					typeArray);

			if ( symbol.token == Token.DOT_OCTOTHORPE ) {
				this.error2(symbol,
						".# cannot be used after an union type, which is a virtual type. There is no file or source code associated to an union type");
			}
			return unionType;

		}

	}

	/**
	 * parse a type that is not a union type. That is, there is no "|" in it as
	 * Int | Char
	 *
	 * @return
	 */

	private Expr typeAnd() {

		Expr t = typeSimple();
		if ( symbol.token != Token.BITAND ) {
			return t;
		}
		else {
			boolean andFollowedByType = true;
			Symbol nextS = next(0);
			if ( nextS.token == Token.IDENT ) {
				char ch = nextS.getSymbolString().charAt(0);
				if ( Character.isLowerCase(ch) || ch == '_' ) {
					// something as 'main'. It is a type only if followed by '.'
					andFollowedByType = next(1).token == Token.PERIOD;
				}
			}
			if ( !andFollowedByType ) {
				return t;
			}

			final List<Expr> typeArray = new ArrayList<Expr>();
			if ( t instanceof ExprTypeof ) error2(t.getFirstSymbol(),
					"'typeof' cannot be used in intersection types");
			typeArray.add(t);
			while (symbol.token == Token.BITAND) {
				next();
				if ( symbol.token == Token.LEFTPAR ) {
					next();
					t = type();
					if ( t instanceof ExprTypeIntersection ) {
						ExprTypeIntersection etu = (ExprTypeIntersection) t;
						for (Expr ty : etu.getTypeList()) {
							typeArray.add(ty);
						}
					}
					else {
						if ( t instanceof ExprTypeof ) {
							error2(t.getFirstSymbol(),
									"'typeof' cannot be used in intersection types");
						}
						typeArray.add(t);
					}
					if ( symbol.token != Token.RIGHTPAR ) {
						this.error2(symbol,
								"')' expected after this type because it started with '('");
					}
					next();
				}
				else {
					t = typeSimple();
					if ( t instanceof ExprTypeof ) {
						error2(t.getFirstSymbol(),
								"'typeof' cannot be used in intersection types");
					}
					typeArray.add(t);
				}

			}
			ExprTypeIntersection intersectionType = new ExprTypeIntersection(
					this.currentMethod, typeArray);

			if ( symbol.token == Token.DOT_OCTOTHORPE ) {
				this.error2(symbol,
						".# cannot be used after an intersection type, "
								+ "which is a virtual type. There is no file or "
								+ "source code associated to an intersection type");
			}
			return intersectionType;
		}
	}

	private Expr typeSimple() {
		Symbol identOne;

		if ( symbol.token == Token.LEFTPAR ) {
			next();
			Expr et = type();

			if ( symbol.token != Token.RIGHTPAR ) {
				this.error2(symbol,
						"')' expected after this type because it started with '('");
			}
			next();
			return et;
		}

		switch (symbol.token) {
		case IDENT:
			Expr identExpr;
			identOne = symbol;
			next();
			if ( symbol.token != Token.PERIOD ) {
				String newName;
				if ( (newName = falseKeywordsTable
						.get(identOne.getSymbolString())) != null ) {
					// found something like a "int", which a lower-case letter.
					if ( ask(identOne,
							"Should change " + identOne.getSymbolString()
									+ " to " + newName + " ? (y, n)") ) {
						final int sizeIdentSymbol = identOne.getSymbolString()
								.length();
						compilationUnit.addAction(new ActionDelete(
								compilationUnit,
								identOne.startOffsetLine
										+ identOne.getColumnNumber() - 1,
								sizeIdentSymbol, identOne.getLineNumber(),
								identOne.getColumnNumber()));
						compilationUnit.addAction(new ActionInsert(newName,
								compilationUnit,
								identOne.startOffsetLine
										+ identOne.getColumnNumber() - 1,
								identOne.getLineNumber(),
								identOne.getColumnNumber()));
					}
				}

				identExpr = new ExprIdentStar(currentMethod, identOne);
			}
			else {
				final List<Symbol> identSymbolArray = new ArrayList<Symbol>();
				identSymbolArray.add(identOne);
				while (symbol.token == Token.PERIOD) {
					next();
					if ( !startType(symbol.token) ) {
						error2(symbol,
								"package, object name or slot (variable or method) expected."
										+ foundSuch());
					}
					identSymbolArray.add(symbol);
					next();
				}
				identExpr = new ExprIdentStar(identSymbolArray, symbol,
						currentMethod);
			}
			if ( symbol.token == Token.LT_NOT_PREC_SPACE ) {

				final List<List<Expr>> arrayOfTypeList = new ArrayList<List<Expr>>();
				while (symbol.token == Token.LT_NOT_PREC_SPACE) {
					next();
					final List<Expr> aTypeList = typeList();
					if ( symbol.token != Token.GT ) {
						error2(symbol,
								"> expected after the types of a generic object."
										+ foundSuch());
					}
					next();
					arrayOfTypeList.add(aTypeList);
				}
				if ( arrayOfTypeList.size() == 0 ) error2(
						identExpr.getFirstSymbol(),
						"Missing parameter for generic prototype instantiation. Something like 'Stack<>'");
				MessageSendToAnnotation messageSendToAnnotation = null;
				if ( symbol.token == Token.DOT_OCTOTHORPE ) {
					// if ( symbol.token == Token.CYANSYMBOL ) {
					messageSendToAnnotation = this
							.parseMessageSendToAnnotation();
				}
				ExprGenericPrototypeInstantiation gpi;
				identExpr = gpi = new ExprGenericPrototypeInstantiation(
						(ExprIdentStar) identExpr, arrayOfTypeList,
						currentPrototype, messageSendToAnnotation,
						currentMethod);

				final Tuple2<Symbol, String> tss = checkExprGenericPrototypeInstantiation(
						gpi);
				if ( tss != null ) {
					this.error2(tss.f1, tss.f2);
				}

				if ( messageSendToAnnotation != null )
					if ( !messageSendToAnnotation.action(
							compilationUnit.getProgram(),
							(ExprGenericPrototypeInstantiation) identExpr) ) {
								error2(identExpr.getFirstSymbol(),
										"No action associated to message '"
												+ messageSendToAnnotation
														.getMessage()
												+ "'");
							}
			}
			else {
				if ( identExpr instanceof ExprIdentStar ) {
					MessageSendToAnnotation messageSendToAnnotation = null;
					if ( symbol.token == Token.DOT_OCTOTHORPE ) {
						// if ( symbol.token == Token.CYANSYMBOL ) {
						messageSendToAnnotation = this
								.parseMessageSendToAnnotation();
					}
					((ExprIdentStar) identExpr).setMessageSendToAnnotation(
							messageSendToAnnotation);
					if ( messageSendToAnnotation != null ) {
						if ( !messageSendToAnnotation.action(
								compilationUnit.getProgram(),
								(ExprIdentStar) identExpr) ) {
							error2(identExpr.getFirstSymbol(),
									"No action associated to message '"
											+ messageSendToAnnotation
													.getMessage()
											+ "'");
						}
					}
					else if ( symbol.token == Token.LEFTRIGHTSB ) {
						int numDimensions = 1;
						next();
						while (symbol.token == Token.LEFTRIGHTSB) {
							error2(symbol,
									"Currently the number of dimensions of a Java array should be 1");
							++numDimensions;
							next();
						}
						// a Java array
						if ( !(identExpr instanceof ExprIdentStar) ) {
							error2(identExpr.getFirstSymbol(),
									"'[]' means that '" + identExpr.asString()
											+ "' should be a Java class or "
											+ "interface. It cannot be a generic prototype");
						}
						identExpr = new ExprJavaArrayType(
								(ExprIdentStar) identExpr, numDimensions,
								currentMethod);
					}
				}

			}

			return checkAttachedAnnotations(identExpr);
		case TYPEOF:
			if ( prohibitTypeof ) {
				/*
				 * 'typeof' is not allowed inside a method signature or as a
				 * type of a field
				 */
				error2(symbol,
						"'typeof' can only be used inside methods or in the DSL attached to metaobjects that start with @");
			}
			final Symbol typeofSymbol = symbol;
			next();
			if ( symbol.token != Token.LEFTPAR ) error2(symbol,
					"'(' expected after keyword Expr." + foundSuch());
			next();
			final Expr exprType = expr();
			if ( symbol.token != Token.RIGHTPAR ) error2(symbol,
					"')' expected after function 'typeof'." + foundSuch());
			if ( Lexer.hasIdentNumberAfter(symbol, compilationUnitSuper) ) {
				error2(symbol, "letter, number, or '_' after ')'");
			}
			next();
			return new ExprTypeof(typeofSymbol, exprType, currentMethod);
		default:
			if ( isBasicType(symbol.token) || symbol.token == Token.STRING
					|| symbol.token == Token.DYN ) {
				final Symbol s = symbol;
				next();
				identExpr = new ExprIdentStar(currentMethod, s);

				return checkAttachedAnnotations(identExpr);
			}
			else {
				String otherMsg = "";
				if ( this.currentMethod == null ) {
					otherMsg = ". Note that in the declaration of a field the order of keywords is fixed: [ private | public ] [ shared ] [ var | let ] Type Id ';'";
				}
				// else {
				// otherMsg = ". If the intension was to use the comparison
				// operator";
				// }
				error2(symbol, "type expected." + foundSuch() + otherMsg);
				return null;
			}

		}
	}

	/**
	 * @param identExpr
	 */
	private Expr checkAttachedAnnotations(Expr identExpr) {
		if ( symbol.token == Token.METAOBJECT_ANNOTATION
				&& !lexer.isThereWhiteSpaceBefore(symbol) ) {
			/**
			 * annotation just after a type: there should be no space between @
			 * and the type.
			 */
			if ( !(identExpr instanceof ExprGenericPrototypeInstantiation)
					&& !(identExpr instanceof ExprIdentStar) ) {
				error2(symbol,
						"An annotation can be attached only to Cyan prototypes");
			}
			final List<AnnotationAt> annotationList = new ArrayList<>();
			while (symbol.token == Token.METAOBJECT_ANNOTATION) {
				AnnotationAt annotation = null;
				final Symbol ctmoSymbol = symbol;
				try {
					annotation = annotation(false);
					annotationList.add(annotation);
				}
				catch (final error.CompileErrorException e) {
					throw e;
				}
				catch (final RuntimeException e) {
					this.error2(ctmoSymbol,
							"Runtime exception in metaobject annotation");
				}

			}
			if ( identExpr instanceof ExprGenericPrototypeInstantiation ) {
				((ExprGenericPrototypeInstantiation) identExpr)
						.setAnnotationToTypeList(annotationList);
			}
			else if ( identExpr instanceof ExprIdentStar ) {
				((ExprIdentStar) identExpr)
						.setAnnotationToTypeList(annotationList);
			}
		}
		return identExpr;
	}

	/**
	 * the current symbol is Token.DOT_OCTOTHORPE
	 */
	private MessageSendToAnnotation parseMessageSendToAnnotation() {

		// # added
		next();
		if ( symbol.token != Token.IDENT ) {
			this.error2(symbol,
					"Identifier expected. The identifier should be the name of the message sent at compile-time to the prototype that precedes it."
							+ foundSuch());
		}
		// # end added
		MessageSendToAnnotation messageSendToAnnotation;
		final String messageName = symbol.getSymbolString();
		final Symbol firstSymbol = symbol;
		messageSendToAnnotation = new MessageSendToAnnotation(messageName);
		if ( !MetaInfoServer.metaobjectAnnotationMethodNameSet
				.contains(messageName) ) {
			error2(symbol,
					"Unknown message send to generic prototype instantiation: '"
							+ messageName + "'");
		}
		else {

			next();
			int numParam = 0;
			if ( symbol.token == Token.LEFTPAR
					&& !Lexer.hasSpaceBefore(symbol, compilationUnit) ) {
				next();
				while (symbol.token == Token.IDENT
						|| symbol.token == Token.LITERALSTRING) { // ||
																	// symbol.token
																	// ==
																	// Token.CYANSYMBOL
																	// ) {
					messageSendToAnnotation.addExpr(symbol.getSymbolString());
					++numParam;
					next();
					if ( symbol.token == Token.COMMA ) {
						lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
						next();
					}
					else
						break;
				}
				if ( symbol.token != Token.RIGHTPAR )
					error2(symbol, "')' expected after a list of parameters."
							+ foundSuch());
				if ( Lexer.hasIdentNumberAfter(symbol, compilationUnitSuper) ) {
					error2(symbol, "letter, number, or '_' after ')'");
				}
				next();
			}
			if ( messageName.equals("writeCode") ) {
				if ( numParam != 0 ) {
					this.error2(firstSymbol,
							"'writeCode' takes zero or one parameter");
				}
			}

			/*
			 * if ( Lexer.hasSpaceBefore(symbol, compilationUnit) ) {
			 * error2(symbol,
			 * "Space between the '>' of a generic prototype instantiation and the message send to the metaobject annotation. Something like Function<Int>   .#writeCode"
			 * ); } else {}
			 */
		}

		return messageSendToAnnotation;
	}

	/**
	 * error in the static methods below
	 */
	static public void staticError(Symbol errSymbol, String msg,
			CompilationUnitSuper compUnit, String errorMessage) {
		final Compiler compiler = null;
		compUnit.error(errSymbol, errSymbol.getLineNumber(), errorMessage + msg,
				compiler, null);

	}

	/**
	 * parse the package/prototype given by <code>typeAsString</code>. The type
	 * may be a simple name (Person, Int, main.Program) or a generic prototype
	 * instantiation. Any error messages will use the line and column number of
	 * <code>symUsedInError</code>. The error message will be prefixed by
	 * parameter <code>message</code>. The current compilation unit is
	 * <code>compUnit</code> and the current program unit is
	 * <code>currentPU</code>.
	 *
	 * @param typeAsString
	 * @return
	 */
	static public Expr parseSingleTypeFromString(String typeAsString,
			Symbol symUsedInError, String message,
			CompilationUnitSuper compUnit, Prototype currentPU) {

		// String typeAsString = packageName + "." + prototypeName + "\0";
		typeAsString += "\0";
		final Compiler comp = compilerFromString(typeAsString);
		lexerFromString = comp.lexer;
		Expr prototypeAsExpr = null;
		try {
			prototypeAsExpr = singleTypeFromStringRec(symUsedInError, compUnit,
					currentPU, message);
		}
		catch (final CompileErrorException cee) {
			/*
			 * an horrible thing to do. No better implementation in sight. /
			 * UnitError lastError =
			 * compUnit.getErrorList().get(compUnit.getErrorList().size()-1);
			 * errorMessage = lastError.getMessage();
			 * lastError.setMessage(message + " " + errorMessage);
			 */
			throw new CompileErrorException(cee.getMessage());
		}
		return prototypeAsExpr;
	}

	/**
	 * @param typeAsString
	 * @return
	 */
	private static Compiler compilerFromString(String typeAsString) {
		char[] sourceCode = typeAsString.toCharArray();

		final CompilationUnitDSL dslCompilationUnit = new CompilationUnitDSL(
				"not a source code, this is a type whose text is '"
						+ typeAsString + "'",
				"'there is no path, this is a type as string'", null);
		dslCompilationUnit.setText(sourceCode);
		final HashSet<CompilationInstruction> compInstSet = new HashSet<>();
		final Compiler comp = new Compiler(dslCompilationUnit, compInstSet,
				CompilationStep.step_1, null, null);
		return comp;
	}

	static public Type singleTypeFromString(String typeAsString,
			Symbol symUsedInError, String message, CompilationUnit compUnit,
			Prototype currentPU, Env env) {

		Type p = null;
		try {
			// p = Compiler.singleTypeFromStringThrow(typeAsString,
			// symUsedInError,
			// message, compUnit, currentPU, env);
			p = CyanTypeCompiler.singleTypeFromStringThrow(typeAsString,
					symUsedInError, message, compUnit, env);
		}
		catch (final CompileErrorException e) {
			env.setThereWasError(true);
			throw new CompileErrorException(message + e.getMessage());
		}
		return p;
	}

	/**
	 * return the program unit corresponding to the package/prototype given by
	 * <code>typeAsString</code>. The type may be a simple name (Person, Int,
	 * main.Program) or a generic prototype instantiation. Any error messages
	 * will use the line and column number of <code>symUsedInError</code>. The
	 * error message will be prefixed by parameter <code>message</code>. The
	 * current compilation unit is <code>compUnit</code> and the current program
	 * unit is <code>currentPU</code>.
	 *
	 * @param typeAsString
	 * @return
	 */
	static public Type singleTypeFromStringThrow(String typeAsString,
			Symbol symUsedInError, String message, CompilationUnit compUnit,
			Prototype currentPU, Env env) {

		// String typeAsString = packageName + "." + prototypeName + "\0";
		typeAsString += "\0";
		final Compiler comp = compilerFromString(typeAsString);
		lexerFromString = comp.lexer;

		Expr prototypeAsExpr = null;
		try {
			prototypeAsExpr = singleTypeFromStringRec(symUsedInError, compUnit,
					currentPU, message);

		}
		catch (final CompileErrorException cee) {
			/*
			 * an horrible thing to do. No better implementation in sight. /
			 * UnitError lastError =
			 * compUnit.getErrorList().get(compUnit.getErrorList().size()-1);
			 * String errorMessage = lastError.getMessage();
			 * lastError.setMessage(message + " " + errorMessage);
			 */
			throw new CompileErrorException(cee.getMessage());
		}
		return searchCreateTypeFromExpr(symUsedInError, compUnit, env,
				prototypeAsExpr);

	}

	/**
	 * @param symUsedInError
	 * @param compUnit
	 * @param env
	 * @param exprType
	 * @return
	 */
	static public Type searchCreateTypeFromExpr(Symbol symUsedInError,
			CompilationUnit compUnit, Env env, Expr exprType) {
		if ( exprType instanceof ExprGenericPrototypeInstantiation ) {
			return CompilerManager.createGenericPrototype(
					(ExprGenericPrototypeInstantiation) exprType, env);
		}
		else if ( exprType instanceof ExprIdentStar ) {
			return searchTypeFromExprIdentStar(symUsedInError, compUnit, env,
					exprType);
		}
		else if ( exprType instanceof ExprTypeUnion ) {
			ExprTypeUnion etu = (ExprTypeUnion) exprType;
			TypeUnion tu = new TypeUnion(etu);
			for (Expr et : etu.getTypeList()) {
				Type t = searchCreateTypeFromExpr(symUsedInError, compUnit, env,
						et);
				tu.addType(t);
			}
			return tu;
		}
		else {
			compUnit.error(symUsedInError, symUsedInError.getLineNumber(),
					"Internal error at Compiler::singleTypeFromString", null,
					env);
			return null;
		}
	}

	/**
	 * @param symUsedInError
	 * @param compUnit
	 * @param env
	 * @param prototypeAsExpr
	 * @return
	 */
	private static Type searchTypeFromExprIdentStar(Symbol symUsedInError,
			CompilationUnit compUnit, Env env, Expr prototypeAsExpr) {
		final ExprIdentStar idStar = (ExprIdentStar) prototypeAsExpr;
		final List<Symbol> symList = idStar.getIdentSymbolArray();
		final int size = symList.size();
		String packageName = "";
		String prototypeName;
		int k = size - 1;
		for (int i = 0; i < size - 1; ++i) {
			packageName = packageName + symList.get(i).getSymbolString();
			if ( --k > 0 ) packageName += ".";
		}
		prototypeName = symList.get(size - 1).getSymbolString();

		if ( prototypeName.equals("Dyn") ) {
			return Type.Dyn;
		}

		Prototype pu;
		if ( packageName.length() == 0 ) {
			pu = env.searchVisiblePrototype(prototypeName, symUsedInError,
					true);
		}
		else {
			pu = env.searchPackagePrototype(packageName, prototypeName);
		}
		if ( pu == null ) {

			final TypeJavaRef javaClass = env
					.searchPackageJavaClass(packageName, prototypeName);
			if ( javaClass != null ) {
				return javaClass;
			}
			else {
				if ( packageName.length() == 0 ) {
					compUnit.error(symUsedInError,
							symUsedInError.getLineNumber(),
							"Prototype '" + prototypeName + "' was not found",
							null, env);
				}
				else {
					compUnit.error(symUsedInError,
							symUsedInError.getLineNumber(),
							"Prototype '" + packageName + "." + prototypeName
									+ "' was not found",
							null, env);
				}
			}
			return null;
		}
		else
			return pu;
	}

	/**
	 * return an expression corresponding to the type typeAsString given as a
	 * string
	 *
	 * @param typeAsString
	 * @return
	 */
	static public Expr singleTypeFromStringRec(Symbol symUsedInError,
			CompilationUnitSuper compUnit, Prototype currentPU,
			String errorMessage) {
		Symbol identOne;

		// public Lexer(char[] in, CompilationUnitSuper compilationUnit,
		// HashSet<saci.CompilationInstruction> compInstSet) {

		Expr ret = null;
		switch (lexerFromString.symbol.token) {
		case TYPEOF:
			staticError(symUsedInError, "'typeof' is not supported", compUnit,
					errorMessage);
			return null;
		case IDENT:
			Expr identExpr;
			identOne = lexerFromString.symbol;
			lexerFromString.next();
			if ( lexerFromString.symbol.token != Token.PERIOD ) {
				identExpr = new ExprIdentStar(null, identOne);
			}
			else {
				final List<Symbol> identSymbolArray = new ArrayList<Symbol>();
				identSymbolArray.add(identOne);
				while (lexerFromString.symbol.token == Token.PERIOD) {
					lexerFromString.next();
					if ( !startType(lexerFromString.symbol.token) ) {
						staticError(symUsedInError,
								" package, object name or slot (variable or method) expected."
										+ foundSuch(lexerFromString.symbol),
								compUnit, errorMessage);
					}
					identSymbolArray.add(lexerFromString.symbol);
					lexerFromString.next();
				}
				identExpr = new ExprIdentStar(identSymbolArray, null, null);
			}
			if ( lexerFromString.symbol.token == Token.LT_NOT_PREC_SPACE ) {

				final List<List<Expr>> arrayOfTypeList = new ArrayList<List<Expr>>();
				while (lexerFromString.symbol.token == Token.LT_NOT_PREC_SPACE) {
					lexerFromString.next();
					final List<Expr> aTypeList = typeListFromString(
							symUsedInError, compUnit, currentPU, errorMessage);
					if ( lexerFromString.symbol.token != Token.GT ) {
						staticError(symUsedInError,
								" '>' expected after the types of a generic object."
										+ foundSuch(lexerFromString.symbol),
								compUnit, errorMessage);
					}
					lexerFromString.next();
					arrayOfTypeList.add(aTypeList);
				}

				ExprGenericPrototypeInstantiation gpi;
				identExpr = gpi = new ExprGenericPrototypeInstantiation(
						(ExprIdentStar) identExpr, arrayOfTypeList, currentPU,
						null, null);

				final Tuple2<Symbol, String> tss = checkExprGenericPrototypeInstantiation(
						gpi);
				if ( tss != null ) {
					staticError(tss.f1, tss.f2, compUnit, "");
				}
			}
			ret = identExpr;
			break;
		default:
			if ( isBasicType(lexerFromString.symbol.token)
					|| lexerFromString.symbol.token == Token.STRING
					|| lexerFromString.symbol.token == Token.DYN ) {
				final Symbol s = lexerFromString.symbol;
				lexerFromString.next();
				ret = new ExprIdentStar(null, s);
			}
			else {
				staticError(symUsedInError,
						" type expected." + foundSuch(lexerFromString.symbol),
						compUnit, errorMessage);
				return null;
			}
		}
		// ExprTypeUnion(MethodDec currentMethod,
		if ( lexerFromString.token == Token.BITOR ) {
			List<Expr> typeList = new ArrayList<>();
			typeList.add(ret);
			while (lexerFromString.token == Token.BITOR) {
				lexerFromString.next();
				ret = singleTypeFromStringRec(symUsedInError, compUnit,
						currentPU, errorMessage);
				typeList.add(ret);
			}
			ret = new ExprTypeUnion(null, typeList);
		}
		return ret;
	}

	private List<Expr> genericPrototypeArgList() {

		final List<Expr> aTypeList = new ArrayList<Expr>();

		aTypeList.add(type());
		while (symbol.token == Token.COMMA) {
			lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
			next();
			aTypeList.add(type());
		}
		return aTypeList;

	}

	private List<Expr> typeList() {

		final List<Expr> aTypeList = new ArrayList<Expr>();

		Expr t;

		try {
			this.prohibitTypeof = true;
			t = type();
		}
		finally {
			this.prohibitTypeof = false;
		}

		aTypeList.add(t);
		while (symbol.token == Token.COMMA) {
			if ( this.currentPrototype != null
					&& this.currentPrototype.getOuterObject() == null ) {
				lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
			}
			next();
			try {
				this.prohibitTypeof = true;
				t = type();
			}
			finally {
				this.prohibitTypeof = false;
			}
			aTypeList.add(t);
		}
		return aTypeList;

	}

	private static List<Expr> typeListFromString(Symbol symUsedInError,
			CompilationUnitSuper compUnit, Prototype currentPU,
			String errorMessage) {

		final List<Expr> aTypeList = new ArrayList<Expr>();

		aTypeList.add(singleTypeFromStringRec(symUsedInError, compUnit,
				currentPU, errorMessage));
		while (Compiler.lexerFromString.token == Token.COMMA) {
			/*
			 * String ermsg =
			 * lexerFromString.retMessageCheckWhiteSpaceParenthesisAfter(",");
			 * if ( ermsg != null ) { staticError(symUsedInError, ermsg,
			 * compUnit, ""); }
			 */
			Compiler.lexerFromString.next();
			aTypeList.add(singleTypeFromStringRec(symUsedInError, compUnit,
					currentPU, errorMessage));
		}
		return aTypeList;

	}

	public void slotDec(ObjectDec currentObject,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList, Symbol firstSlotSymbol,
			boolean compilerCreatedMethod) {

		boolean foundFinalKeyword = false;

		Token visibility = null;
		if ( symbol.token == Token.PUBLIC || symbol.token == Token.PROTECTED
				|| symbol.token == Token.PACKAGE
				|| symbol.token == Token.PRIVATE ) {
			visibility = symbol.token;
			next();
		}
		if ( symbol.token == Token.FINAL ) {
			foundFinalKeyword = true;
			next();
			if ( symbol.token != Token.IDENTCOLON && symbol.token != Token.FUNC
					&& symbol.token != Token.OVERLOAD
					&& symbol.token != Token.OVERRIDE ) {
				error2(symbol, "'final' applies only to methods");
			}
		}
		if ( isBasicType(symbol.token) || symbol.token == Token.STRING
				|| symbol.token == Token.DYN ) {
			if ( visibility == null ) visibility = Token.PRIVATE;

			objectVariableDec(currentObject, visibility,
					nonAttachedAnnotationList, attachedAnnotationList,
					firstSlotSymbol, false, true);

		}
		else {
			switch (symbol.token) {
			case SHARED:
				next();
				if ( isBasicType(symbol.token) || symbol.token == Token.STRING
						|| symbol.token == Token.DYN ) {
					if ( visibility == null ) visibility = Token.PRIVATE;
					objectVariableDec(currentObject, visibility,
							nonAttachedAnnotationList, attachedAnnotationList,
							firstSlotSymbol, true, true);
				}
				else if ( symbol.token == Token.FUNC ) {
					if ( visibility == null ) visibility = Token.PUBLIC;
					methodDec(currentObject, visibility, foundFinalKeyword,
							true, nonAttachedAnnotationList,
							attachedAnnotationList, compilerCreatedMethod);
				}
				else {

					if ( symbol.token != Token.VAR
							&& symbol.token != Token.IDENT
							&& symbol.token != Token.LET ) {
						if ( symbol.token == Token.OVERRIDE
								|| symbol.token == Token.OVERLOAD ) {
							this.error2(symbol,
									"This method is declared with keywords 'shared' and "
											+ "'override'. That does not make sense because shared methods cannot be"
											+ " overridden");
						}
						else {
							this.error2(symbol,
									"'var', 'let', or a type expected. OR wrong order of keywords."
											+ foundSuch()
											+ ". Note that in the declaration of a field or method,"
											+ " the order of keywords is fixed: \r\n"
											+ "[ private ] [ shared ] [ var | let ] Type Id ';'   or"
											+ "[ private | public | package | protected ] [ shared ] func ..."

							);
						}
					}
					else {
						if ( visibility == null ) visibility = Token.PRIVATE;
						if ( symbol.token == Token.VAR ) {
							next();
							objectVariableDec(currentObject, visibility,
									nonAttachedAnnotationList,
									attachedAnnotationList, firstSlotSymbol,
									true, false);
						}
						else {
							if ( symbol.token == Token.LET ) next();
							objectVariableDec(currentObject, visibility,
									nonAttachedAnnotationList,
									attachedAnnotationList, firstSlotSymbol,
									true, false);
						}

					}

				}
				break;
			case VAR:
				if ( visibility == null ) visibility = Token.PRIVATE;
				next();
				objectVariableDec(currentObject, visibility,
						nonAttachedAnnotationList, attachedAnnotationList,
						firstSlotSymbol, false, false);
				break;
			case IDENTCOLON:
				// user declared something like
				// public add: (Int item) [ ... ]
				if ( ask(symbol, "Should I insert 'func' before "
						+ symbol.getSymbolString() + " ? (y, n)") ) {
					compilationUnit.addAction(new ActionInsert("func ",
							compilationUnit,
							symbol.startOffsetLine + symbol.getColumnNumber()
									- 1,
							symbol.getLineNumber(), symbol.getColumnNumber()));
					// String symbolString, int startOffsetLine, int lineNumber,
					// int columnNumber)
					insertSymbol(new Symbol(Token.FUNC, "func",
							symbol.getStartLine(), symbol.getLineNumber(),
							symbol.getColumnNumber(), symbol.getOffset(),
							this.compilationUnit));
				}
				else {
					error2(symbol, "keyword 'func' expected." + foundSuch());
				}

				//$FALL-THROUGH$
			case FUNC:
			case OVERLOAD:
			case OVERRIDE:
			case ABSTRACT:
				if ( visibility == null ) visibility = Token.PUBLIC;
				methodDec(currentObject, visibility, foundFinalKeyword, false,
						nonAttachedAnnotationList, attachedAnnotationList,
						compilerCreatedMethod);
				break;
			case IDENT:
			case LET:
				if ( visibility == null ) visibility = Token.PRIVATE;
				if ( symbol.token == Token.LET ) next();
				objectVariableDec(currentObject, visibility,
						nonAttachedAnnotationList, attachedAnnotationList,
						firstSlotSymbol, false, true);

				break;
			default:
				error2(symbol,
						"variable declaration or method declaration declaration expected."
								+ foundSuch());
			}

		}
	}

	/**
	 * ObjectVariableDec} ::= [ ``shared"\/ ] [ ``var"\/ ] Type Id \{ ``,"\/ Id
	 * \} [ ``="\/ Expr ] [ ``;"\/ ]
	 *
	 * @param currentObject
	 * @param visibility
	 * @param ctmoCallArray
	 */

	private void objectVariableDec(ObjectDec currentObject, Token visibility,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList, Symbol firstSymbol,
			boolean shared, boolean isReadonly) {
		Expr typeInDec = null;
		Expr expr = null;
		FieldDec instVarDec = null;

		if ( visibility == Token.PROTECTED ) {
			this.error2(firstSymbol, "fields can only be private");
			return;
		}
		if ( visibility == Token.PUBLIC ) {
			this.error2(firstSymbol, "fields can only be private");
			return;
		}

		try {
			this.prohibitTypeof = true;
			typeInDec = type();
		}
		finally {
			this.prohibitTypeof = false;
		}

		int numVariableInThisDeclaration = 0;

		boolean hasExpr = false;
		if ( symbol.token != Token.IDENT ) {
			if ( symbol.token == Token.ASSIGN )
				error2(symbol, "A type should always be supplied for a field");
			else
				error2(symbol, "variable name expected." + foundSuch());
			return;

		}
		else {
			while (symbol.token == Token.IDENT) {
				final SymbolIdent variableSymbol = (SymbolIdent) symbol;
				next();
				++numVariableInThisDeclaration;
				if ( symbol.token == Token.ASSIGN ) {
					next();

					// Tuple2<Expr, Boolean> t = exprBasicTypeLiteral_Ident();
					expr = expr();
					hasExpr = true;
					/*
					 * if ( t.f2 ) { // initialized with an identifier or array
					 * of tuple containing an identifier
					 * error2(expr.getFirstSymbol(),
					 * "Expression with an identifier used to initialize a field or shared variable"
					 * ); }
					 */
				}
				else
					expr = null;

				if ( Character.isUpperCase(
						variableSymbol.getSymbolString().charAt(0)) )
					this.error2(variableSymbol,
							"Variables cannot start with an uppercase letter");

				instVarDec = new FieldDec(currentObject, variableSymbol,
						typeInDec, expr, visibility, shared,
						nonAttachedAnnotationList, attachedAnnotationList,
						firstSymbol, isReadonly, cyanMetaobjectContextStack);

				final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
						.get(variableSymbol.getSymbolString());
				if ( cyanMacro != null ) {
					this.error2(variableSymbol,
							"This field has the name of a macro keyword of a macro imported by this compilation unit");
				}

				if ( attachedAnnotationList != null ) {
					for (final AnnotationAt annotation : attachedAnnotationList) {
						if ( annotation.getCyanMetaobject().mayBeAttachedTo(
								AttachedDeclarationKind.FIELD_DEC) ) {
							annotation.setDeclaration(instVarDec.getI());
						}
						else {
							/*
							 * the metaobject cannot be attached to a field
							 */
							this.error(true, annotation.getFirstSymbol(),
									"This metaobject annotation cannot be attached to a field. It can be attached to "
											+ " one entity of the following list: [ "
											+ annotation.getCyanMetaobject()
													.attachedListAsString()
											+ " ]",
									null, ErrorKind.metaobject_error);

						}

						final CyanMetaobject cyanMetaobject = annotation
								.getCyanMetaobject();
						actionCompilerInfo_parsing(cyanMetaobject);

					}
				}
				currentObject.addField(instVarDec);
				currentObject.addSlot(instVarDec);
				if ( symbol.token == Token.COMMA ) {
					lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
					next();
				}
				else
					break;

			}
			if ( numVariableInThisDeclaration > 1 && hasExpr ) {
				this.error2(firstSymbol,
						"A declaration of several variables with a single type with one of the "
								+ "variables receiving a value. This is illegal. Put the variable or variables that receive a value in a separate declaration");
			}

		}
		if ( symbol.token == Token.SEMICOLON )
			next();
		else if ( hasExpr ) {
			// with expression but no ;
			this.error2(symbol,
					"; expected. A ';' should appear after a field receives a value in an expression");
		}

		if ( attachedAnnotationList != null ) {
			if ( numVariableInThisDeclaration > 1 ) {
				this.error2(firstSymbol,
						"A metaobject annotation cannot be attached to a list of fields. Use one annotation for each variable");
			}

			for (final AnnotationAt annotation : attachedAnnotationList) {
				if ( annotation.getCyanMetaobject()
						.shouldBeAttachedToSomething() ) {
					if ( !annotation.getCyanMetaobject().mayBeAttachedTo(
							AttachedDeclarationKind.FIELD_DEC) ) {
						this.error2(annotation.getFirstSymbol(),
								"This metaobject annotation cannot be attached to a field. It can be attached to "
										+ " one entity of the following list: [ "
										+ annotation.getCyanMetaobject()
												.attachedListAsString()
										+ " ]");
					}

				}
			}

		}
		if ( symbol.token == Token.END ) {
			// this is the last method of the object. Mark it as such
			if ( instVarDec != null ) {
				instVarDec.setLastSlot(true);
			}
		}

	}

	/**
	 *
	 * @param currentObject
	 * @param visibility
	 * @param finalKeyword
	 * @param ctmoCallArray
	 * @param firstSymbol
	 *            is the symbol that starts the method declaration. It is
	 */

	private void methodDec(ObjectDec currentObject, Token visibility,
			boolean finalKeyword, boolean shared,
			List<AnnotationAt> nonAttachedAnnotationList,
			List<AnnotationAt> attachedAnnotationList,
			boolean compilerCreatedMethod) {

		currentMethod = new MethodDec(currentObject, visibility,
				finalKeyword || this.currentPrototype.getIsFinal(), shared,
				nonAttachedAnnotationList, attachedAnnotationList,
				methodNumber++, compilerCreatedMethod,
				// || !this.cyanMetaobjectContextStack.isEmpty(),
				cyanMetaobjectContextStack);

		functionStack.clear();
		this.parameterDecStack.clear();

		if ( attachedAnnotationList != null ) {
			for (final AnnotationAt annotation : attachedAnnotationList) {
				if ( annotation.getCyanMetaobject()
						.shouldBeAttachedToSomething() ) {
					if ( !annotation.getCyanMetaobject().mayBeAttachedTo(
							AttachedDeclarationKind.METHOD_DEC) )
						this.error2(annotation.getFirstSymbol(),
								"This metaobject annotation cannot be attached to a method. It can be attached to "
										+ " one entity of the following list: [ "
										+ annotation.getCyanMetaobject()
												.attachedListAsString()
										+ " ]");

				}
			}

		}

		// List<AnnotationAt>
		// attachedAnnotationList = null;

		MethodSignature ms = null;
		if ( symbol.token == Token.OVERRIDE ) {
			currentMethod.setHasOverride(true);
			next();

		}
		boolean abstractKeyword = false;
		if ( symbol.token == Token.ABSTRACT ) {
			currentMethod.setAbstract(true);
			abstractKeyword = true;
			next();

		}
		if ( attachedAnnotationList != null ) {
			for (final AnnotationAt annotation : attachedAnnotationList) {
				if ( annotation.getCyanMetaobject()
						.mayBeAttachedTo(AttachedDeclarationKind.METHOD_DEC) ) {
					annotation.setDeclaration(currentMethod == null ? null
							: currentMethod.getI());
				}
				else {
					/*
					 * the metaobject cannot be attached to a method
					 */
					this.error(true, annotation.getFirstSymbol(),
							"This metaobject annotation cannot be attached to a method. It can be attached to "
									+ " one entity of the following list: [ "
									+ annotation.getCyanMetaobject()
											.attachedListAsString()
									+ " ]",
							null, ErrorKind.metaobject_error);

				}
			}
		}

		if ( symbol.token == Token.OVERLOAD ) {
			this.currentMethod.setOverload(true);
			this.currentMethod.setPrecededBy_overload(true);
			next();
		}

		if ( symbol.token == Token.FUNC ) {
			currentMethod.setFirstSymbol(symbol);
			next();
		}
		else {
			this.error2(false, symbol,
					"'func' expected before a method declaration (I assumed this is a method declaration). "
							+ "Maybe you forget that there is an order of keywords that can appear before a method declaration. This order is "
							+ "'public/private/protected/package', 'final', 'override', 'abstract', 'overload'");
			if ( ask(symbol,
					"Should I insert 'func' before " + symbol.getSymbolString()
							+ " ? (y, n)") )
				compilationUnit.addAction(new ActionInsert("func",
						compilationUnit,
						symbol.startOffsetLine + symbol.getColumnNumber() - 1,
						symbol.getLineNumber(), symbol.getColumnNumber()));
		}

		if ( finalKeyword && this.currentPrototype.getIsFinal() ) {
			error2(false, symbol,
					"This prototype is final (it was not declared with 'open'). "
							+ "Therefore it cannot have a 'final' method");
		}

		try {
			this.prohibitTypeof = true;

			if ( this.currentPrototype.getIsFinal()
					&& visibility == Token.PROTECTED ) {
				this.error2(symbol,
						"Invalid 'protected' visibility. Prototype '"
								+ this.currentPrototype.getName()
								+ "' is final, its declaration was not preceded by 'open'. "
								+ "Therefore it cannot be inherited and visibility 'protected' for methods does not make sense");
			}

			ms = methodSignature(finalKeyword);
			checkInitNewMethods(ms);

			if ( ms.isIndexingMethod() ) {
				if ( this.currentMethod.getVisibility() != Token.PUBLIC ) {
					this.error2(ms.getFirstSymbol(),
							"Indexing methods should be public");
				}
				if ( this.currentMethod.isAbstract() ) {
					this.error2(ms.getFirstSymbol(),
							"Indexing methods cannot be abstract");
				}
			}

		}
		catch (final CompileErrorException e) {
			if ( !(this.compilationUnitSuper instanceof CompilationUnit) ) {
				throw e;
			}

			skipTo(Token.FUNC, Token.OVERLOAD, Token.PUBLIC, Token.PRIVATE,
					Token.PROTECTED, Token.END);
			this.prohibitTypeof = false;
			return;
		}
		finally {
			// probably unnecessary but ...
			this.prohibitTypeof = false;
		}
		currentMethod.setMethodSignature(ms);

		/*
		 * String methodName = ms.getName(); if ( methodName.equals("eq:1") ||
		 * methodName.equals("neq:1") ) { String currentObjectName =
		 * currentObject.getName(); if (
		 * !NameServer.isBasicType(currentObjectName) &&
		 * !currentObjectName.equals("Nil") ) { error2(ms.getFirstSymbol(),
		 * "Methods 'eq:' and 'neq:' with a single parameter " +
		 * "can only be defined in prototypes Any and the basic types"); } }
		 */

		currentObject.addMethod(currentMethod);
		currentObject.addSlot(currentMethod);

		if ( symbol.token == Token.LEFTCB && currentMethod.isAbstract() ) {
			error2(symbol, "Abstract methods cannot have a body");
		}

		/*
		 * if ( ms.getNameWithoutParamNumber().equals("init:") && !
		 * this.cyanMetaobjectContextStack.isEmpty() ) {
		 * //System.out.println(ms.asString()); if (
		 * ms.asString().contains("ImPerson") ) {
		 * //System.out.println("Found ImPerson"); }
		 * //System.out.println("ccm = " +
		 * currentMethod.getCompilerCreatedMethod()); }
		 */

		boolean initShared = false;
		if ( currentMethod.getNameWithoutParamNumber()
				.equals(MetaHelper.initShared) ) {
			if ( currentMethod.getVisibility() != Token.PRIVATE ) {
				error2(ms.getFirstSymbol(),
						"'initShared' methods should be 'private'");
			}
			if ( currentMethod.getIsFinal()
					&& !this.currentPrototype.getIsFinal()
					|| currentMethod.isAbstract()
					|| currentMethod.isIndexingMethod() )
				error2(ms.getFirstSymbol(),
						"'initShared' methods cannot be 'final', 'abstract', or indexing method");
			if ( ms.getReturnTypeExpr() != null ) {
				error2(ms.getReturnTypeExpr().getFirstSymbol(),
						"'initShared' methods cannot declare return value type, even if it is 'Nil'");
			}
			initShared = true;
		}

		if ( !currentMethod.isAbstract() && !this.parsingPackageInterfaces ) {
			if ( symbol.token == Token.ASSIGN ) {
				next();
				currentMethod.setFirstSymbolExpr(symbol);
				currentMethod.setExpr(expr());
				currentMethod.setLastSymbolExpr(previousSymbol);
				if ( symbol.token == Token.SEMICOLON
						|| symbol.token == Token.FUNC
						|| symbol.token == Token.LET
						|| symbol.token == Token.VAR ) {
					if ( symbol.token == Token.SEMICOLON ) next();
				}
				else {
					error2(symbol, "';' expected." + foundSuch());
				}
			}
			else {
				// methodBody();
				if ( symbol.token != Token.LEFTCB ) {
					error2(symbol,
							"'{' expected in a method body." + foundSuch());
				}
				else {
					if ( !Lexer.hasSpaceBefore(symbol, compilationUnit) ) {
						if ( ask(symbol, "Should I insert a space before "
								+ symbol.getSymbolString() + " ? (y, n)") )
							compilationUnit.addAction(new ActionInsert(" ",
									compilationUnit,
									symbol.startOffsetLine
											+ symbol.getColumnNumber() - 1,
									symbol.getLineNumber(),
									symbol.getColumnNumber()));
					}
					currentMethod.setLeftCBsymbol(symbol);

					next();
					whileForCount = 0;
					tryCount = 0;

					/*
					 * initShared methods can have only assignment statements
					 */
					if ( initShared ) {
						currentMethod.setStatementList(initSharedBody());
					}
					else {
						currentMethod.setStatementList(statementList());
					}

					this.localVariableDecStack.clear();
					if ( symbol.token != Token.RIGHTCB ) {
						error2(symbol,
								"'}' expected at the end of a method body."
										+ foundSuch());
					}
					else {
						if ( !Lexer.hasSpaceAfter(symbol, compilationUnit) ) {
							// char ch =
							// compilationUnit.getText()[symbol.getOffset() +
							// symbol.getSymbolString().length()];
							if ( ask(symbol, "Should I insert a space after "
									+ symbol.getSymbolString() + " ? (y, n)") )
								compilationUnit.addAction(new ActionInsert(" ",
										compilationUnit,
										symbol.startOffsetLine
												+ symbol.getColumnNumber()
												+ symbol.getSymbolString()
														.length(),
										symbol.getLineNumber(),
										symbol.getColumnNumber()));
						}
						currentMethod.setRightCBsymbol(symbol);
						next();

					}
				}
			}
		}

		if ( currentMethod.getAttachedAnnotationList() != null ) {
			for (final Annotation annotation : this.currentMethod
					.getAttachedAnnotationList()) {

				final CyanMetaobject cyanMetaobject = annotation
						.getCyanMetaobject();
				actionCompilerInfo_parsing(cyanMetaobject);
			}
		}

		if ( symbol.token == Token.END ) {
			// this is the last method of the object. Mark it as such
			currentMethod.setLastSlot(true);
		}

		currentMethod = null;
	}

	private void checkInitNewMethods(MethodSignature ms) {
		String name = ms.getName();
		int indexDD = name.indexOf(':');
		if ( indexDD > 0 ) {
			name = name.substring(0, indexDD + 1);
		}
		boolean isInit = name.equals("init") || name.equals("init:");
		boolean isInitShared = name.equals("initShared");
		if ( isInit || isInitShared || name.equals("new")
				|| name.equals("new:") ) {
			if ( currentPrototype instanceof InterfaceDec ) {
				error(true, symbol,
						"Methods 'init', 'init:', and 'initShared' cannot be declared in interfaces",
						"init",
						ErrorKind.init_new_methods_cannot_be_declared_in_interfaces);
			}

			if ( this.currentMethod.getHasOverride() ) error(true, symbol,
					"Methods 'init', 'init:', and 'initShared' cannot be declared with 'override'",
					"init",
					ErrorKind.init_should_not_be_declared_with_override);
			if ( currentMethod.isAbstract() ) error(true, symbol,
					"Methods 'init', 'init:', and 'initShared' cannot be declared 'abstract'",
					"init", ErrorKind.init_should_not_be_abstract);
			/*
			 * if ( isInit && currentMethod.getVisibility() != Token.PUBLIC ) {
			 * error(true, symbol,
			 * "'init' and 'init:' methods should be public", "init",
			 * ErrorKind.init_new_should_be_public); }
			 */

			if ( !isInit && !isInitShared && !this.parsingPackageInterfaces ) {
				if ( this.cyanMetaobjectContextStack.isEmpty() ) {
					// user-made code
					this.error2(ms.getFirstSymbol(),
							"'new' and 'new:' cannot be user-defined");
				}
			}

		}

	}

	/**
	 * ParamList ::= ParamDec { "," ParamDec } | "(" ParamDec { "," ParamDec }
	 * ")"
	 *
	 * ParamDec} ::= Type Id
	 *
	 */

	private void parameterDecList(List<ParameterDec> parameterList) {

		if ( symbol.token == Token.LEFTPAR ) {
			next();
			if ( symbol.token == Token.LEFTPAR )
				warning(symbol, symbol.getLineNumber(),
						"two or more ( in a parameter declaration");
			parameterDecList(parameterList);
			if ( symbol.token != Token.RIGHTPAR )
				error2(symbol, "')' expected after parameter declaration."
						+ foundSuch());
			else {
				if ( Lexer.hasIdentNumberAfter(symbol, compilationUnitSuper) ) {
					error2(symbol, "letter, number, or '_' after ')'");
				}
				next();
			}
		}
		else {
			paramDec(parameterList);
			while (symbol.token == Token.COMMA) {
				lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
				next();
				paramDec(parameterList);
			}
		}
	}

	/**
	 * Parameter list of a function. It can have a first parameter which is
	 * 'self' ParamList ::= ParamDec { "," ParamDec } | "(" ParamDec { ","
	 * ParamDec } ")"
	 *
	 * ParamDec} ::= Type Id
	 *
	 */

	private void parameterDecListFunction(List<ParameterDec> parameterList) {

		if ( symbol.token == Token.LEFTPAR ) {
			next();
			if ( symbol.token == Token.LEFTPAR )
				warning(symbol, symbol.getLineNumber(),
						"two or more ( in a parameter declaration");
			parameterDecListFunction(parameterList);
			if ( symbol.token != Token.RIGHTPAR )
				error2(symbol, "')' expected after parameter declaration."
						+ foundSuch());
			else {
				if ( Lexer.hasIdentNumberAfter(symbol, compilationUnitSuper) ) {
					error2(symbol, "letter, number, or '_' after ')'");
				}
				next();
			}
		}
		else {
			/*
			 * it may not have a type. It may be { (: eval: eval: :) ^0 }
			 */
			if ( Compiler.startType(symbol.token) ) {
				ParameterDec p;
				paramDecFunction(parameterList);
				while (symbol.token == Token.COMMA) {
					lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
					next();
					p = paramDecFunction(parameterList);
					if ( p.getName().compareTo("self") == 0 ) error2(
							p.getFirstSymbol(),
							"'self' can only be used as the first parameter of a context function.");

				}

			}
		}
	}

	/**
	 * This method parses rule ParamDec ::= [ Type ] Id
	 */
	private void paramDec(List<ParameterDec> parameterList) {

		Symbol variableSymbol;
		Expr typeInDec;

		try {
			this.prohibitTypeof = true;
			typeInDec = type();
		}
		finally {
			this.prohibitTypeof = false;
		}

		VariableKind parameterType;
		if ( symbol.token == Token.BITAND ) {
			/**
			 * valid if: (a) stack is not empty (b) the top of the stack, f1,
			 * starts with 'new'
			 */
			if ( !this.cyanMetaobjectContextStack.isEmpty()
					&& this.cyanMetaobjectContextStack.peek().f1
							.startsWith("new") ) {
				parameterType = VariableKind.LOCAL_VARIABLE_REF;
				next();
			}
			else {
				this.error2(symbol,
						"A reference parameter is only allowed in context parameters:\n    object Sum(Int &sum) ... end");
				return;
			}
		}
		else {
			parameterType = VariableKind.COPY_VAR;
		}

		if ( symbol.token != Token.IDENT ) {

			if ( isType(typeInDec) ) {
				variableSymbol = null;
			}
			else {
				// type is in fact the variable, which was given without the
				// type as in
				// func at: x { }
				variableSymbol = typeInDec.getFirstSymbol();
				typeInDec = null;
			}

		}
		else {
			variableSymbol = symbol;
			if ( Character
					.isUpperCase(variableSymbol.getSymbolString().charAt(0)) )
				this.error2(variableSymbol,
						"Variables and parameters cannot start with an uppercase letter");

			if ( !Lexer.hasSpaceBefore(symbol, compilationUnitSuper) && !Lexer
					.isCharBeforeSymbolEqualTo(symbol, '&', compilationUnit) ) {
				this.error2(symbol,
						"there should be a space before the parameter name");
			}
			next();
		}

		if ( variableSymbol != null && equalToFormalGenericParameter(
				variableSymbol.getSymbolString()) ) {
			error2(symbol,
					"Parameter names cannot be equal to one of the formal parameters of the generic prototype");
		}
		final ParameterDec parameterDec = new ParameterDec(variableSymbol,
				typeInDec, currentMethod);
		parameterDec.setVariableKind(parameterType);
		if ( this.functionStack.size() > 0 )
			parameterDec.setDeclaringFunction(functionStack.peek());
		parameterList.add(parameterDec);

		parameterDecStack.push(parameterDec);
	}

	/**
	 * The parameter can have 'self' as name. ParamDec ::= [ Type ] Id
	 */
	private ParameterDec paramDecFunction(List<ParameterDec> parameterList) {

		Symbol variableSymbol;
		final Expr typeInDec = type();

		if ( symbol.token == Token.IDENTCOLON
				&& !symbol.getSymbolString().equals("eval:") ) {
			error2(symbol,
					"Maybe you forgot to put an space before ':)' as in '{ (: Int n:) ^n}'. The correct would be '{ (: Int n :) ^n}'");
		}
		if ( symbol.token != Token.IDENT && symbol.token != Token.SELF ) {
			variableSymbol = null;
		}
		else {
			if ( searchIdent(symbol.getSymbolString()) != null ) {
				error(false, true, symbol,
						"Parameter " + symbol.getSymbolString()
								+ " is being redeclared",
						symbol.getSymbolString(),
						ErrorKind.parameter_is_being_redeclared);
			}
			variableSymbol = symbol;
			next();
		}
		if ( variableSymbol != null && variableSymbol.token != Token.SELF
				&& equalToFormalGenericParameter(
						variableSymbol.getSymbolString()) ) {
			error2(false, symbol,
					"Parameter names cannot be equal to one of the formal parameters of the generic prototype");
		}

		if ( variableSymbol != null && Character
				.isUpperCase(variableSymbol.getSymbolString().charAt(0)) )
			this.error2(false, variableSymbol,
					"Variables and parameters cannot start with an uppercase letter");

		if ( variableSymbol == null && Character.isLowerCase(
				typeInDec.getFirstSymbol().getSymbolString().charAt(0)) ) {
			this.error2(false, typeInDec.getFirstSymbol(),
					"Type for variable '"
							+ typeInDec.getFirstSymbol().getSymbolString()
							+ "' is missing");

		}

		final ParameterDec parameterDec = new ParameterDec(variableSymbol,
				typeInDec, currentMethod);
		if ( this.functionStack.size() > 0 ) {
			final ExprFunction currentFunction = functionStack.peek();
			parameterDec.setDeclaringFunction(currentFunction);
			if ( variableSymbol != null && variableSymbol.token != Token.SELF )
				currentFunction.addLocalVariableDec(parameterDec);
		}
		parameterList.add(parameterDec);

		parameterDecStack.push(parameterDec);

		return parameterDec;
	}

	private StatementList initSharedBody() {
		final List<Statement> statList = new ArrayList<Statement>();

		while (symbol.token == Token.IDENT || symbol.token == Token.SELF) {
			Expr e = null;

			if ( symbol.token == Token.IDENT ) {
				final ExprIdentStar eis = ident();
				if ( eis.getIdentSymbolArray().size() != 1 ) {
					error2(eis.getFirstSymbol(),
							"An identifie without '.' expected");
				}
				e = eis;
			}
			else {
				final Symbol selfSymbol = symbol;
				next();
				if ( symbol.token == Token.PERIOD ) {
					// something like "self.x"
					next();
					if ( symbol.token != Token.IDENT ) {
						error2(symbol, "identifier expected after 'self.'"
								+ foundSuch());
					}
					else {
						e = new ExprSelfPeriodIdent(selfSymbol, symbol,
								currentMethod);
						next();
					}
				}
				else {
					error2(selfSymbol, "'self.id = expr' expected");
				}
			}
			if ( symbol.token != Token.ASSIGN ) error2(symbol,
					"'=' expected. 'initShared' methods can have only assignments"
							+ foundSuch());
			next();

			final StatementAssignmentList assignmentList = new StatementAssignmentList(
					currentMethod);
			assignmentList.add(e);
			// Tuple2<Expr, Boolean> t = exprBasicTypeLiteral_Ident();
			/*
			 * if ( t.f2 ) { error2(t.f1.getFirstSymbol(),
			 * "A literal expression containing only literal values was expected"
			 * ); }
			 */
			final Expr expr = expr();
			assignmentList.add(expr);
			statList.add(assignmentList);
			if ( symbol.token == Token.SEMICOLON )
				next();
			else if ( symbol.token != Token.RIGHTCB )
				error2(symbol, "';' expected");
		}
		if ( symbol.token != Token.RIGHTCB ) {
			error2(symbol, "initShared methods should have only assignments");
		}

		return new StatementList(statList);
	}

	public StatementList statementList() {
		return statementList(false);
	}

	/**
	 * Parse a list of statements. If calledByTryStatement is true, the parsing
	 * is done only until a 'catch' or 'finally' is found
	 *
	 * @param calledByTryStatement
	 * @return
	 */
	public StatementList statementList(boolean calledByTryStatement) {

		final List<Statement> statList = new ArrayList<Statement>();
		Symbol previousSym;
		Statement lastStatement = null;

		if ( symbol.token != Token.RIGHTCB && symbol.token != Token.PUBLIC
				&& symbol.token != Token.PRIVATE
				&& symbol.token != Token.PROTECTED
				&& (!calledByTryStatement || (symbol.token != Token.FINALLY
						&& symbol.token != Token.CATCH)) ) {
			try {
				statList.add(lastStatement = statement());
			}
			catch (final CompileErrorException e) {
				if ( !(this.compilationUnitSuper instanceof CompilationUnit) ) {
					throw e;
				}
				skipTo(Token.SEMICOLON, Token.END, Token.PUBLIC, Token.PRIVATE,
						Token.PROTECTED, Token.FUNC, Token.OVERLOAD);
				lastStatement = new StatementNull(symbol, currentMethod);
				if ( symbol.token == Token.SEMICOLON )
					next();
				else if ( !startExpr(symbol) ) {
					return new StatementList(statList);
				}
			}
			// finally {
			// lexer.inStatement = false;
			// if ( pushedFirstSymbolStatement ) {
			// --lexer.numberNestedStatements;
			// // lexer.firstStatementSymbolStack.pop();
			// }
			// }
			lastStatement.setNextSymbol(symbol);
			while (isEndOfStatement()
					&& (!calledByTryStatement || (symbol.token != Token.FINALLY
							&& symbol.token != Token.CATCH))) {

				if ( lastStatement.demandSemicolon() ) {
					if ( symbol.token == Token.SEMICOLON )
						next();
					else if ( !(previousSymbol instanceof SymbolCharSequence)
							&& previousSymbol.token != Token.SEMICOLON ) {
						previousSym = previousSymbol;
						if ( symbol.token == Token.ASSIGN ) {
							error2(symbol,
									"assignments are statements in Cyan. They cannot appear inside an expression");
						}
						this.error2(previousSym, "';' expected." + foundSuch());
						if ( ask(previousSym, "Should I insert ';' after "
								+ previousSym.getSymbolString() + " ? (y, n)") )
							compilationUnit.addAction(new ActionInsert(";",
									compilationUnit,
									previousSym.getOffset() + previousSym
											.getSymbolString().length(),
									previousSym.getLineNumber(),
									previousSym.getColumnNumber()));
					}
				}
				else {
					if ( symbol.token == Token.SEMICOLON ) next();
				}

				// boolean thereWasCompilationError = false;
				if ( symbol.token != Token.RIGHTCB
						&& symbol.token != Token.UNTIL
						&& symbol.token != Token.CATCH
						&& symbol.token != Token.FINALLY ) {
					try {
						Statement currentStatement;
						currentStatement = statement();
						if ( lastStatement instanceof StatementAnnotation ) {
							StatementAnnotation statAnnot = (StatementAnnotation) lastStatement;
							CyanMetaobjectAtAnnot cyanMetaobject = statAnnot
									.getAnnotation().getCyanMetaobject();
							if ( cyanMetaobject.mayBeAttachedTo(
									AttachedDeclarationKind.STATEMENT_DEC)
									&& cyanMetaobject instanceof meta.IActionStatement_semAn_afterSemAn ) {
								statAnnot.getAnnotation()
										.setStatement(currentStatement.getI());
							}
						}
						lastStatement = currentStatement;
						statList.add(lastStatement);
					}
					catch (final CompileErrorException e) {
						if ( !(this.compilationUnitSuper instanceof CompilationUnit) ) {
							throw e;
						}
						// thereWasCompilationError = true;
						skipTo(Token.SEMICOLON, Token.END, Token.PUBLIC,
								Token.PRIVATE, Token.PROTECTED, Token.FUNC,
								Token.OVERLOAD);
						lastStatement = new StatementNull(symbol,
								currentMethod);
						if ( symbol.token == Token.SEMICOLON )
							next();
						else if ( !startExpr(symbol) ) {
							return new StatementList(statList);
						}
					}
					lastStatement.setNextSymbol(symbol);
				}
			}
		}
		if ( this.compilationStep == CompilationStep.step_1
				&& statList.size() > 0
				&& this.cyanMetaobjectContextStack.empty() ) {
			int columnNumber = statList.get(0).getFirstSymbol()
					.getColumnNumber();
			int lineNumber = statList.get(0).getFirstSymbol().getLineNumber();
			for (final Statement s : statList) {
				if ( s instanceof ast.StatementAnnotation ) {
					/**
					 * metaobjects CyanMetaobjectCompilationContextPush are
					 * allowed to be non-indented
					 */
					final StatementAnnotation annotation = (StatementAnnotation) s;
					if ( annotation.getAnnotation()
							.getCyanMetaobject() instanceof meta.cyanLang.CyanMetaobjectCompilationContextPush )
						continue;
				}
				/*
				 * check whether the statements of forStatList are aligned (with
				 * correct indentation).
				 */
				Symbol firstSymbol = s.getFirstSymbolMayBeAnnotation();
				if ( firstSymbol.getColumnNumber() != columnNumber
						&& firstSymbol.getLineNumber() != lineNumber
						&& !s.getCreatedByMetaobjects() ) {
					columnNumber = statList.get(0).getFirstSymbol()
							.getColumnNumber();
					firstSymbol = s.getFirstSymbolMayBeAnnotation();
					this.error2(s.getFirstSymbol(),
							"This statement is not correctly indented. It should be in column '"
									+ columnNumber + "' but it is in column '"
									+ firstSymbol.getColumnNumber()
									+ "'. Check if the problem was caused by tab characters");
					return null;
				}
				lineNumber = firstSymbol.getLineNumber();
			}
		}
		int i = 0;
		for (final Statement s : statList) {
			if ( s instanceof StatementReturn || s instanceof StatementBreak ) {
				if ( i != statList.size() - 1 ) {
					boolean foundNonNullStatement = false;
					for (int j = i + 1; j < statList.size(); ++j) {
						final Statement other = statList.get(j);
						if ( !(other instanceof StatementNull)
								&& !(other instanceof StatementAnnotation
										&& ((StatementAnnotation) other)
												.getAnnotation()
												.getCyanMetaobject() instanceof CyanMetaobjectCompilationContextPop) ) {
							foundNonNullStatement = true;
							break;
						}
					}
					if ( foundNonNullStatement )
						this.error(true, statList.get(i + 1).getFirstSymbol(),
								"Unreachable code",
								statList.get(i + 1).getFirstSymbol()
										.getSymbolString(),
								ErrorKind.unreachable_code);
				}
			}
			++i;
		}
		return new StatementList(statList);
	}

	/**
	 * @return
	 */
	private boolean isEndOfStatement() {
		return symbol.token != Token.RIGHTCB && symbol.token != Token.END
				&& symbol.token != Token.UNTIL;
	}

	/**
	 * if the last statement is an annotation that can be attached to
	 * statements, this variable refer to it. It is reset if the current
	 * statement is not an annotation that can be attached to statements
	 */
	private AnnotationAt lastAnnotation;

	public Statement statement() {

		Statement ret = null;

		startSymbolCurrentStatement = symbol;

		switch (symbol.token) {
		case BREAK:
			if ( whileForCount <= 0 ) {
				try {
					this.error2(symbol,
							"'break' outside any 'while', 'repeat-until', or 'for' command");
				}
				catch (final error.CompileErrorException e) {
					if ( !(this.compilationUnitSuper instanceof CompilationUnit) ) {
						throw e;
					}
				}
			}
			final Symbol breakSymbol = symbol;
			next();
			ret = new StatementBreak(breakSymbol, currentMethod);
			break;
		case PLUSPLUS:
			final Symbol plusPlus = symbol;
			next();
			final Expr idExpr = expr();
			if ( idExpr instanceof ExprSelfPeriodIdent ) {
				this.error2(plusPlus,
						"'++' cannot currently be applied to a field access starting with 'self'. Instead of '++self.id', use just '++id'");
			}
			if ( !(idExpr instanceof ExprIdentStar) || ((ExprIdentStar) idExpr)
					.getIdentSymbolArray().size() != 1 ) {
				this.error2(symbol,
						"'++' is an operator that can only be applied to identifiers (fields, variables)");
			}
			final ExprIdentStar id = (ExprIdentStar) idExpr;
			ret = new StatementPlusPlusIdent(plusPlus, id, currentMethod);
			break;
		case MINUSMINUS:
			final Symbol minusMinus = symbol;
			next();
			final Expr idExpr2 = expr();
			if ( idExpr2 instanceof ExprSelfPeriodIdent ) {
				this.error2(minusMinus,
						"'--' cannot currently be applied to a field access starting with 'self'. Instead of '--self.id', use just '--id'");
			}

			if ( !(idExpr2 instanceof ExprIdentStar)
					|| ((ExprIdentStar) idExpr2).getIdentSymbolArray()
							.size() != 1 ) {
				this.error2(symbol,
						"'++' is an operator that can only be applied to identifiers (fields, variables)");
			}
			final ExprIdentStar id2 = (ExprIdentStar) idExpr2;
			ret = new StatementMinusMinusIdent(minusMinus, id2, currentMethod);
			break;
		case VAR:
			ret = localVariableDec(false);
			break;
		case LET:
			ret = localVariableDec(true);
			break;
		case RETURN:
			ret = returnStatement();
			break;
		case RETURN_FUNCTION:
			/*
			 * if ( functionStack.isEmpty() ) ret = returnStatement(); else ret
			 * = returnFunctionStatement();
			 */
			ret = returnFunctionStatement();
			break;
		case METAOBJECT_ANNOTATION:

			final String metaobjectName = symbol.getSymbolString();
			final CyanMetaobjectAtAnnot cyanMetaobject = this.compilationUnit
					.getMetaObjectTable().get(metaobjectName);

			try {
				if ( cyanMetaobject == null ) {
					error(true, symbol,
							"Metaobject " + metaobjectName + " was not found",
							metaobjectName, ErrorKind.metaobject_was_not_found);
					return null;
				}
			}
			catch (final RuntimeException e) {
				if ( cyanMetaobject != null ) {
					error2(symbol,
							"Metaobject '" + cyanMetaobject.getName() + "' "
									+ " has thrown exception '"
									+ e.getClass().getName() + "'");
				}
				throw e;
			}

			if ( cyanMetaobject.isExpression() || cyanMetaobject.getName()
					.equals(MetaHelper.pushCompilationContextName) ) {

				// metaobject is inside an expression. Maybe it is alone an
				// expression
				ret = expr();
			}
			else {

				// metaobject has no type, it is a statement

				final AnnotationAt regularAnnotation = annotation(
						cyanMetaobject, metaobjectName, false);

				if ( cyanMetaobject.mayBeAttachedTo(
						AttachedDeclarationKind.LOCAL_VAR_DEC) ) {
					StatementLocalVariableDecList localVarDecList;
					switch (symbol.token) {
					case VAR:
						ret = localVarDecList = localVariableDec(false);
						if ( localVarDecList.getLocalVariableDecList()
								.size() != 1 ) {
							this.error2(symbol,
									"Annotations can be attached to just one declaration");
						}
						localVarDecList.setBeforeAnnotation(regularAnnotation);
						StatementLocalVariableDec slv = localVarDecList
								.getLocalVariableDecList().get(0);
						regularAnnotation.setDeclaration(
								slv == null ? null : slv.getI());
						break;
					case LET:
						ret = localVarDecList = localVariableDec(true);
						if ( localVarDecList.getLocalVariableDecList()
								.size() != 1 ) {
							this.error2(symbol,
									"Annotations can be attached to just one declaration");
						}
						localVarDecList.setBeforeAnnotation(regularAnnotation);
						slv = localVarDecList.getLocalVariableDecList().get(0);
						regularAnnotation.setDeclaration(
								slv == null ? null : slv.getI());
						break;
					default:
						ret = new StatementAnnotation(regularAnnotation,
								currentMethod);
					}
				}
				else if ( cyanMetaobject.mayBeAttachedTo(
						AttachedDeclarationKind.STATEMENT_DEC) ) {
					if ( !(cyanMetaobject instanceof meta.IActionStatement_semAn_afterSemAn) ) {
						this.error2(symbol,
								"Metaobjects that can be attached to statements should implement interface '"
										+ meta.IActionStatement_semAn_afterSemAn.class
												.getCanonicalName()
										+ "'");
					}
					else if ( !isEndOfStatement() ) {
						this.error2(symbol,
								"Metaobjects that implement interface '"
										+ meta.IActionStatement_semAn_afterSemAn.class
												.getCanonicalName()
										+ "' should be followed by a statement");
					}
					else {
						// IActionStatement_semAn_afterSemAn iaction =
						// (IActionStatement_semAn_afterSemAn ) cyanMetaobject;
						lastAnnotation = regularAnnotation;
						ret = new StatementAnnotation(regularAnnotation,
								currentMethod);
					}

				}
				else {
					ret = new StatementAnnotation(regularAnnotation,
							currentMethod);
				}

			}
			break;
		case IF:
			ret = ifStatement();
			break;
		case WHILE:
			final StatementWhile sw = whileStatement();
			ret = sw;
			if ( !(sw.getFirstSymbol().getColumnNumber() == sw
					.getRightCBEndsIf().getColumnNumber())
					&& !(sw.getFirstSymbol().getLineNumber() == sw
							.getRightCBEndsIf().getLineNumber()) ) {
				if ( parsingForInterpreter || this.project.getCompilerManager()
						.getCompilationStep() == CompilationStep.step_1 )
					error2(sw.getRightCBEndsIf(),
							"The '}' that closes a 'while' statement should be either in the same line as the 'while' or in the same column");
			}

			break;
		case TRY:
			final StatementTry stry = tryStatement();
			ret = stry;

			break;
		case THROW:
			final StatementThrow sthrow = throwStatement();
			ret = sthrow;
			break;

		case REPEAT:
			final StatementRepeat sru = repeatStatement();
			ret = sru;
			if ( !(sru.getFirstSymbol().getColumnNumber() == sru
					.getUntilSymbol().getColumnNumber())
					&& !(sru.getFirstSymbol().getLineNumber() == sru
							.getUntilSymbol().getLineNumber()) ) {
				if ( parsingForInterpreter || this.project.getCompilerManager()
						.getCompilationStep() == CompilationStep.step_1 )
					error2(sru.getUntilSymbol(),
							"Keyword 'until' that closes a 'repeat' statement should be either in the same line as the 'repeat' or in the same column");
			}
			break;
		case FOR:
			final StatementFor sf = forStatement();
			ret = sf;
			if ( !(sf.getFirstSymbol().getColumnNumber() == sf
					.getRightCBEndsIf().getColumnNumber())
					&& !(sf.getFirstSymbol().getLineNumber() == sf
							.getRightCBEndsIf().getLineNumber()) ) {
				if ( parsingForInterpreter || this.project.getCompilerManager()
						.getCompilationStep() == CompilationStep.step_1 )
					error2(sf.getRightCBEndsIf(),
							"The '}' that closes a 'for' statement should be either in the same line as the 'for' or in the same column");
			}
			break;
		case TYPE:
			ret = typeStatement();
			break;
		case CAST:
			ret = castStatement();
			break;
		case SEMICOLON:
			final Symbol s = symbol;
			next();
			ret = new StatementNull(s, currentMethod);
			break;
		default:

			if ( symbol.token == Token.IDENT ) {
				final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
						.get(symbol.getSymbolString());
				if ( cyanMacro != null ) {
					final AnnotationMacroCall mCall = macroCall(cyanMacro,
							true);
					ret = mCall != null ? mCall
							: new ExprNonExpression(currentMethod);
					/*
					 * if ( ! compInstSet.contains(saci.CompilationInstruction.
					 * parsing_actions) ) { this.error(true, symbol,
					 * "macro call in a compiler step that does not allow macros"
					 * , symbol.getSymbolString(), ErrorKind.
					 * parsing_compilation_phase_literal_objects_and_macros_are_not_allowed
					 * ); ret = new ExprNonExpression(); } else { ret =
					 * macroCall(cyanMacro, false); }
					 */
				}
				else {
					// just to improve error messages
					final String strId = symbol.getSymbolString();
					if ( MetaHelper.isBasicType(strId)
							|| Character.isUpperCase(strId.charAt(0)) ) {
						this.mayBeWrongVarDeclaration = true;
					}
				}
			}
			else if ( symbol.token == Token.MACRO_KEYWORD ) {
				final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
						.get(symbol.getSymbolString());
				if ( cyanMacro == null ) {
					this.error2(symbol, "Internal error: symbol '"
							+ symbol.getSymbolString()
							+ "' is a macro keyword but the macro metaobject was not found");
				}
				else {
					final AnnotationMacroCall mCall = macroCall(cyanMacro,
							true);
					ret = mCall != null ? mCall
							: new ExprNonExpression(currentMethod);
				}

			}
			if ( ret == null ) {
				ret = exprAssign();
				this.mayBeWrongVarDeclaration = false;
			}
		}
		ret.setLastSymbol(previousSymbol);
		if ( ret instanceof StatementLocalVariableDecList ) {
			StatementLocalVariableDecList decList = (StatementLocalVariableDecList) ret;
			List<StatementLocalVariableDec> localDecList = decList
					.getLocalVariableDecList();
			for (StatementLocalVariableDec localDec : localDecList) {
				localDec.setLastSymbol(previousSymbol);
			}
		}
		startSymbolCurrentStatement = null;

		if ( symbol.token == Token.METAOBJECT_ANNOTATION ) {
			if ( symbol.getSymbolString()
					.equals(MetaHelper.popCompilationContextName) ) {
				if ( ret.getAfterAnnotation() == null ) {
					final AnnotationAt annotation = annotation(false);
					ret.setAfterAnnotation(annotation);
				}
			}
		}

		ret.setSymbolAfter(symbol);

		if ( ret instanceof StatementLocalVariableDecList ) {
			StatementLocalVariableDecList decList = (StatementLocalVariableDecList) ret;
			List<StatementLocalVariableDec> localDecList = decList
					.getLocalVariableDecList();
			for (StatementLocalVariableDec localDec : localDecList) {
				localDec.setSymbolAfter(symbol);
			}
		}

		// if ( !isStatementAnnotation && lastAnnotation != null ) {
		// lastAnnotation.setStatement(ret.getI());
		// lastAnnotation = null;
		// }

		ret.setCreatedByMetaobjects(!this.cyanMetaobjectContextStack.empty());
		return ret;
	}

	/**
	 *
	 * @param cyanMacro
	 * @param inExpr,
	 *            true if the macro is being called inside an expression
	 * @return
	 */
	private AnnotationMacroCall macroCall(CyanMetaobjectMacro cyanMacro,
			boolean inExpr) {
		AnnotationMacroCall ret = null;
		try {
			lexer.pushMacroKeywords(cyanMacro);
			ret = macroCallAux(cyanMacro, inExpr);
			cyanMacro.setAnnotation(ret.getI());
		}
		finally {
			lexer.popMacroKeywords(cyanMacro);

		}

		return ret;
	}

	private AnnotationMacroCall macroCallAux(CyanMetaobjectMacro cyanMacro,
			boolean inExpr) {

		if ( !compInstSet
				.contains(meta.CompilationInstruction.parsing_actions) ) {
			/*
			 * found a macro in a compiler step that does not allow macros. See
			 * the Figure in Chapter "Metaobjects" of the Cyan manual
			 */
			this.error(true, symbol,
					"macro call in a compiler step that does not allow macros",
					symbol.getSymbolString(),
					ErrorKind.parsing_compilation_phase_literal_objects_and_macros_are_not_allowed);
			return null;
		}

		final Symbol startSymbol = symbol;
		final CompilerMacro_parsing compilerMacro_parsing = new CompilerMacro_parsing(
				this);
		// CompilationUnit compilationUnit, Prototype prototype, Symbol
		// firstSymbol
		cyanMacro = cyanMacro.myClone();
		final AnnotationMacroCall cyanMetaobjectMacroCall = new AnnotationMacroCall(
				cyanMacro, this.compilationUnit, this.currentPrototype,
				startSymbol, inExpr, currentMethod);
		compilerMacro_parsing.setCyanMetaobjectMacro(cyanMetaobjectMacroCall);

		// // cyanMacro.setAnnotation(cyanMetaobjectMacroCall, 0);

		_CyanMetaobjectMacro other = (_CyanMetaobjectMacro) cyanMacro
				.getMetaobjectInCyan();
		try {
			if ( other == null ) {
				cyanMacro.parsing_parseMacro(compilerMacro_parsing);
			}
			else {
				other._parsing__parseMacro_1(compilerMacro_parsing);
			}
		}
		catch (final error.CompileErrorException e) {
			throw e;
		}
		catch (final NoClassDefFoundError e) {
			error2(meta.GetHiddenItem.getHiddenSymbol(
					cyanMacro.getAnnotation().getFirstSymbol()),
					e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
		}
		catch (final RuntimeException e) {
			thrownException(cyanMetaobjectMacroCall,
					cyanMetaobjectMacroCall.getFirstSymbol(), e);
		}
		finally {
			this.metaobjectError(cyanMacro, cyanMetaobjectMacroCall);
		}

		cyanMetaobjectMacroCall
				.setExprStatList(compilerMacro_parsing.getExprStatList());

		cyanMetaobjectMacroCall.setLastSymbolMacroCall(meta.GetHiddenItem
				.getHiddenSymbol(compilerMacro_parsing.getLastSymbol()));

		this.copyLexerData(compilerMacro_parsing.compiler);
		cyanMetaobjectMacroCall.setNextSymbol(symbol);

		if ( currentPrototype != null ) {
			currentPrototype.addAnnotation(cyanMetaobjectMacroCall);
			cyanMetaobjectMacroCall.setCurrentPrototype(currentPrototype);
			cyanMetaobjectMacroCall.setAnnotationNumber(
					currentPrototype.getIncAnnotationNumber());
		}
		cyanMetaobjectMacroCall.setCompilationUnit(compilationUnit);

		if ( (cyanMacro instanceof IActionNewPrototypes_parsing
				|| (other != null
						&& other instanceof _IActionNewPrototypes__parsing))
				&& (this.currentPrototype == null
						|| !this.currentPrototype.isGeneric())
				&& this.compilationStep == CompilationStep.step_1 ) {
			// this.compInstSet.contains(CompilationInstruction.parsing_actions)
			// ) {
			final ICompilerAction_parsing compilerAction_dpa1 = new Compiler_parsing(
					this, lexer, null);

			List<Tuple2<String, StringBuffer>> prototypeNameCodeList = null;
			try {
				int timeoutMilliseconds = getTimeoutMilliseconds(
						cyanMetaobjectMacroCall);

				if ( other == null ) {
					final IActionNewPrototypes_parsing actionNewPrototype = (IActionNewPrototypes_parsing) cyanMacro;
					Timeout<List<Tuple2<String, StringBuffer>>> to = new Timeout<>();
					prototypeNameCodeList = to.run(
							() -> actionNewPrototype.parsing_NewPrototypeList(
									compilerAction_dpa1),
							timeoutMilliseconds, "parsing_NewPrototypeList",
							cyanMacro, project);

					// prototypeNameCodeList = actionNewPrototype
					// .parsing_NewPrototypeList(compilerAction_dpa1);
				}
				else {
					_IActionNewPrototypes__parsing anp = (_IActionNewPrototypes__parsing) other;
					Timeout<_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT> to = new Timeout<>();

					_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array = to
							.run(() -> anp._parsing__NewPrototypeList_1(
									compilerAction_dpa1), timeoutMilliseconds,
									"parsing_NewPrototypeList", cyanMacro,
									project);

					// _Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT
					// array =
					// anp._parsing__NewPrototypeList_1(compilerAction_dpa1);
					int size = array._size().n;
					if ( size > 0 ) {
						prototypeNameCodeList = new ArrayList<>();
						for (int i = 0; i < size; ++i) {
							_Tuple_LT_GP_CyString_GP_CyString_GT tss = array
									._at_1(new CyInt(i));
							String f1 = tss._f1().s;
							String f2 = tss._f2().s;
							if ( f1.length() > 0 ) {
								prototypeNameCodeList.add(
										new Tuple2<String, StringBuffer>(f1,
												new StringBuffer(f2)));
							}
						}
					}

				}

			}
			catch (final error.CompileErrorException e) {
			}
			catch (final NoClassDefFoundError e) {
				error2(cyanMetaobjectMacroCall.getFirstSymbol(), e.getMessage()
						+ " " + NameServer.messageClassNotFoundException);
			}
			catch (final RuntimeException e) {
				thrownException(cyanMetaobjectMacroCall,
						cyanMetaobjectMacroCall.getFirstSymbol(), e);
			}
			finally {
				metaobjectError(cyanMacro, cyanMetaobjectMacroCall);
			}
			if ( prototypeNameCodeList != null ) {
				CyanPackage currentCyanPackage = this.compilationUnit
						.getCyanPackage();

				for (final Tuple2<String, StringBuffer> prototypeNameCode : prototypeNameCodeList) {
					String prototypeName = prototypeNameCode.f1;
					final Tuple2<CompilationUnit, String> t = this.project
							.getCompilerManager().createNewPrototype(
									prototypeName, prototypeNameCode.f2,
									this.compilationUnit.getCompilerOptions(),
									this.compilationUnit.getCyanPackage());
					if ( t != null && t.f2 != null ) {
						this.error2(cyanMetaobjectMacroCall.getFirstSymbol(),
								t.f2);
					}
					currentCyanPackage.addPrototypeNameAnnotationInfo(
							prototypeName, cyanMetaobjectMacroCall);
				}
			}

		}

		return cyanMetaobjectMacroCall;
	}

	private StatementCast castStatement() {

		final Symbol castSymbol = symbol;
		StatementList castStatementList = null;
		StatementList elseStatementList = null;
		Symbol rightCBEndsIf = null, lastElse = null;
		final List<CastRecord> castRecordList = new ArrayList<>();

		next();

		final int numberOfLocalVariables = this.localVariableDecStack.size();
		int sizeLocalVariableDecList = -1;

		SymbolIdent variableSymbol;
		Expr typeInDec = type();
		while (true) {

			if ( symbol.token == Token.IDENT ) {
				variableSymbol = (SymbolIdent) symbol;

				if ( Character.isUpperCase(
						variableSymbol.getSymbolString().charAt(0)) )
					this.error2(variableSymbol,
							"Variables cannot start with an uppercase letter");

				next();
			}
			else {
				/*
				 * no type at the declaration
				 */
				if ( !(typeInDec instanceof ExprIdentStar) ) {
					error2(symbol, "Variable name expected." + foundSuch());
				}
				if ( ((ExprIdentStar) typeInDec).getIdentSymbolArray()
						.size() > 1 )
					error2(typeInDec.getFirstSymbol(),
							"Identifier expected." + foundSuch());
				final Symbol ident = ((ExprIdentStar) typeInDec)
						.getIdentSymbolArray().get(0);
				if ( !(ident instanceof SymbolIdent) ) {
					if ( symbol instanceof lexer.SymbolKeyword )
						error2(ident, "Keyword '" + symbol.getSymbolString()
								+ "' used as an Identifier");
					else
						error2(ident, "Identifier expected." + foundSuch());
				}
				typeInDec = null;
				variableSymbol = (SymbolIdent) ident;
			}

			// ****************

			// ***********************************

			if ( symbol.token != Token.ASSIGN ) {
				error2(symbol, "'=' expected." + foundSuch());
			}
			next();
			final Expr e = expr();

			final StatementLocalVariableDec localVariableDec = new StatementLocalVariableDec(
					variableSymbol, typeInDec, e, currentMethod,
					functionStack.size() + 1, true, currentMethod);

			final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
					.get(variableSymbol.getSymbolString());
			if ( cyanMacro != null ) {
				this.error2(variableSymbol,
						"This variable has the name of a macro keyword of a macro imported by this compilation unit");
			}

			if ( functionStack.size() > 0 ) {
				localVariableDec.setDeclaringFunction(functionStack.peek());
			}
			this.localVariableDecStack.push(localVariableDec);

			if ( !functionStack.isEmpty() ) {
				final ExprFunction currentFunction = functionStack.peek();
				sizeLocalVariableDecList = currentFunction
						.getSizeLocalVariableDecList();
				currentFunction.addLocalVariableDec(localVariableDec);
			}

			/*
			 * CastRecord(Expr typeInDec, StatementLocalVariableDec localVar,
			 * Expr expr)
			 */
			final CastRecord nnr = new CastRecord(typeInDec, localVariableDec,
					e);
			castRecordList.add(nnr);

			if ( symbol.token != Token.COMMA )
				break;
			else {
				lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
				next();
				typeInDec = type();
			}
		}

		if ( symbol.token != Token.LEFTCB ) {
			error2(symbol, "'{' expected after if expression." + foundSuch());
		}
		else {
			next();

			castStatementList = statementList();

			while (localVariableDecStack.size() > numberOfLocalVariables)
				localVariableDecStack.pop();

			if ( symbol.token != Token.RIGHTCB ) {
				this.error2(symbol,
						"'}' expected in an if statement." + foundSuch());
			}
			else {
				rightCBEndsIf = symbol;
				next();
			}

			if ( !functionStack.isEmpty() && sizeLocalVariableDecList > 0 ) {
				final ExprFunction currentFunction = functionStack.peek();
				currentFunction.trimLocalVariableDecListToSize(
						sizeLocalVariableDecList);
			}

			if ( symbol.token == Token.ELSE ) {
				lastElse = symbol;

				next();

				if ( symbol.token != Token.LEFTCB ) {
					error2(symbol,
							"'{' expected after if expression." + foundSuch());
				}
				else
					next();

				elseStatementList = statementList();
				if ( symbol.token != Token.RIGHTCB ) {
					error2(symbol,
							"'}' expected after if statements." + foundSuch());
				}
				else {
					rightCBEndsIf = symbol;
					next();
				}
			}

		}

		return new StatementCast(castSymbol, castRecordList, castStatementList,
				elseStatementList, rightCBEndsIf, lastElse, currentMethod);
	}

	/**
	 * IfStat} ::= ``if"\/ ``("\/ Expr ``)"\/ \{ StatementList \} \\ \rr \{
	 * ``else"\/ ``if"\/ ``("\/ Expr ``)"\/ \{ StatementList \} \}\\ \rr [
	 * ``else"\/ \{ StatementList \} ]
	 *
	 *
	 *
	 * @return
	 */
	private StatementIf ifStatement() {
		StatementList thenStatementList, elseStatementList = null;

		final List<Expr> ifExprList = new ArrayList<Expr>();
		final List<StatementList> ifStatementList = new ArrayList<StatementList>();
		Symbol rightCBEndsIf = null;
		Symbol previousRightCBEndsIf = null;
		Symbol lastElse = null;
		int lineIf = -1;
		int columnIf = -1;
		int lineElse = -1;
		int columnElse = -1;

		final boolean firstCompilationStep = this.compilationStep == CompilationStep.step_1;

		final Symbol ifSymbol = symbol;
		while (symbol.token == Token.IF) {
			lineIf = symbol.getLineNumber();
			columnIf = symbol.getColumnNumber();
			next();

			final Expr booleanExpr = expr();
			ifExprList.add(booleanExpr);

			if ( symbol.token != Token.LEFTCB ) {
				error2(symbol,
						"'{' expected after if expression." + foundSuch());
			}
			else {
				if ( (firstCompilationStep || parsingForInterpreter)
						&& columnIf > symbol.getColumnNumber() ) {
					error2(symbol,
							"The column of '{' that follows an 'if' statement should be in a column greater than the 'if' keyword column");
				}
				next();

				if ( (firstCompilationStep || parsingForInterpreter)
						&& columnIf > booleanExpr.getFirstSymbol()
								.getColumnNumber() ) {
					error2(symbol,
							"The first symbol of the expression that follows the 'if' statement should be in a column greater that the 'if' keyword column");
				}

				int numberOfLocalVariables = this.localVariableDecStack.size();

				thenStatementList = statementList();

				while (localVariableDecStack.size() > numberOfLocalVariables)
					localVariableDecStack.pop();

				ifStatementList.add(thenStatementList);
				if ( symbol.token != Token.RIGHTCB ) {
					this.error2(symbol,
							"'}' expected in an if statement." + foundSuch());
					return null;
				}
				else {
					previousRightCBEndsIf = rightCBEndsIf;
					rightCBEndsIf = symbol;
					if ( firstCompilationStep || parsingForInterpreter ) {
						Symbol cmpCBEndsIf = previousRightCBEndsIf != null
								? previousRightCBEndsIf
								: ifSymbol;
						if ( cmpCBEndsIf != null ) {
							final int line = symbol.getLineNumber();
							final int column = symbol.getColumnNumber();
							if ( lineIf != line && columnIf != column
									&& lineElse != line
									&& columnElse != column ) {
								if ( cmpCBEndsIf.getColumnNumber() != column
										&& cmpCBEndsIf.getLineNumber() != line )
									error2(symbol,
											"The '}' that closes the 'if' statements should be in the same line or same column as the 'if' or 'else' keywords");
							}
						}

					}

					next();
				}
				if ( symbol.token != Token.ELSE )
					break;
				else {
					lastElse = symbol;

					if ( firstCompilationStep || parsingForInterpreter ) {
						final int line = symbol.getLineNumber();
						final int column = symbol.getColumnNumber();
						if ( lineIf != line && columnIf != column
								&& lineElse != line && columnElse != column ) {
							if ( columnIf != rightCBEndsIf.getColumnNumber()
									|| line != rightCBEndsIf.getLineNumber() ) {
								error2(symbol,
										"'else' must be in the same line or in the same column as the previous 'if' or 'else' keywords");
							}
						}
						lineElse = line;
						columnElse = column;
					}

					next();
					if ( symbol.token != Token.IF ) {
						if ( symbol.token != Token.LEFTCB ) {
							error2(symbol, "'{' expected after if expression."
									+ foundSuch());
						}
						else
							next();

						numberOfLocalVariables = this.localVariableDecStack
								.size();

						elseStatementList = statementList();

						while (localVariableDecStack
								.size() > numberOfLocalVariables)
							localVariableDecStack.pop();

						if ( symbol.token != Token.RIGHTCB ) {
							error2(symbol, "'}' expected after if statements."
									+ foundSuch());
						}
						else {
							previousRightCBEndsIf = rightCBEndsIf;
							rightCBEndsIf = symbol;
							if ( (firstCompilationStep || parsingForInterpreter)
									&& lineElse != symbol.getLineNumber()
									&& columnElse != symbol
											.getColumnNumber() ) {
								if ( previousRightCBEndsIf
										.getColumnNumber() != rightCBEndsIf
												.getColumnNumber()
										|| previousRightCBEndsIf
												.getLineNumber() != lineElse ) {
									error2(symbol,
											"The '}' that closes the 'else' statements should be in the same line or same column as the 'else' keyword");
								}
							}

							next();
						}

						break;
					}

				}

			}
		}
		return new StatementIf(ifSymbol, ifExprList, ifStatementList,
				elseStatementList, rightCBEndsIf, lastElse, currentMethod);

	}

	private StatementThrow throwStatement() {
		final Symbol throwSymbol = symbol;
		next();
		Expr expr = expr();
		return new StatementThrow(throwSymbol, expr, currentMethod);
	}

	private StatementTry tryStatement() {
		final Symbol trySymbol = symbol;
		next();

		final int numberOfLocalVariables = this.localVariableDecStack.size();

		tryCount++;
		StatementList statementList = this.statementList(true);
		tryCount--;

		while (localVariableDecStack.size() > numberOfLocalVariables)
			localVariableDecStack.pop();

		List<Symbol> catchSymbolList = new ArrayList<>();
		List<Expr> catchExprList = new ArrayList<>();
		StatementList finallyStatementList = null;
		if ( symbol.token == Token.CATCH ) {
			while (symbol.token == Token.CATCH) {

				if ( symbol.getLineNumber() != trySymbol.getLineNumber() ) {
					if ( symbol.getColumnNumber() != trySymbol
							.getColumnNumber() ) {
						this.error2(symbol,
								"'try' and 'catch' should be all in the same line or in the same column");
					}
				}

				catchSymbolList.add(symbol);
				next();
				Expr catchExpr = expr();
				catchExprList.add(catchExpr);
			}
		}
		Symbol finallySymbol = null;
		if ( symbol.token == Token.FINALLY ) {
			finallySymbol = symbol;

			if ( symbol.getLineNumber() != trySymbol.getLineNumber() ) {
				if ( symbol.getColumnNumber() != trySymbol.getColumnNumber() ) {
					this.error2(symbol,
							"'try', 'catch', and 'finally' should be all in the same line or in the same column");
				}
			}
			next();
			if ( symbol.token != Token.LEFTCB ) {
				error2(symbol,
						"'{' expected after a 'finally' clause" + foundSuch());
			}
			if ( symbol.getLineNumber() != finallySymbol.getLineNumber() ) {
				if ( symbol.getColumnNumber() != finallySymbol
						.getColumnNumber() ) {
					this.error2(symbol,
							"'{' and 'finally' should be all in the same line or in the same column");
				}
			}
			next();

			finallyStatementList = this.statementList();
			if ( symbol.token != Token.RIGHTCB ) {
				error2(symbol, "'}' expected at the end of a 'finally' clause"
						+ foundSuch());
			}
			else {
				if ( symbol.getLineNumber() != finallySymbol.getLineNumber() ) {
					if ( symbol.getColumnNumber() != finallySymbol
							.getColumnNumber() ) {
						this.error2(symbol,
								"The closing '}' of a 'finally' should be in the same line or in the same column as 'finally'");
					}
				}

				next();
			}

		}
		return new StatementTry(trySymbol, statementList, catchExprList,
				finallyStatementList, currentMethod);
	}

	private StatementWhile whileStatement() {
		StatementList statementList = null;
		Symbol rightCBEndsIf = null;

		final Symbol whileSymbol = symbol;
		next();
		final Expr booleanExpr = expr();

		if ( symbol.token != Token.LEFTCB ) {
			error2(symbol,
					"'{' expected after while expression." + foundSuch());
		}
		else {

			next();

			final int numberOfLocalVariables = this.localVariableDecStack
					.size();

			++whileForCount;
			statementList = statementList();
			--whileForCount;

			while (localVariableDecStack.size() > numberOfLocalVariables)
				localVariableDecStack.pop();

			if ( symbol.token != Token.RIGHTCB ) {
				error2(symbol, "'}' expected at the end of a while statement."
						+ foundSuch());
			}
			else {
				rightCBEndsIf = symbol;
				next();
			}

		}
		return new StatementWhile(whileSymbol, booleanExpr, statementList,
				rightCBEndsIf, currentMethod);
	}

	private StatementRepeat repeatStatement() {
		StatementList statementList = null;
		Symbol untilSymbol = null;

		final Symbol repeatSymbol = symbol;
		next();

		final int numberOfLocalVariables = this.localVariableDecStack.size();

		if ( symbol.token != Token.UNTIL ) {
			++whileForCount;
			statementList = statementList();
			--whileForCount;
		}

		while (localVariableDecStack.size() > numberOfLocalVariables)
			localVariableDecStack.pop();

		if ( symbol.token != Token.UNTIL ) {
			error2(symbol,
					"'until' expected at the end of a 'repeat-until' statement."
							+ foundSuch());
		}
		else {
			untilSymbol = symbol;
			next();
		}

		final Expr booleanExpr = expr();
		List<Statement> statList = statementList.getStatementList();
		if ( statList != null && statList.size() > 0 ) {
			Statement stat = statList.get(statList.size() - 1);
			if ( stat instanceof StatementBreak
					|| stat instanceof StatementReturn ) {
				this.error2(booleanExpr.getFirstSymbol(),
						"The 'until' expression is unreachable because the "
								+ "last statement of the repeat-until statement is a "
								+ ((stat instanceof StatementBreak) ? "break"
										: "return"));
			}
		}

		return new StatementRepeat(repeatSymbol, booleanExpr, statementList,
				untilSymbol, currentMethod);
	}

	private StatementFor forStatement() {
		StatementList statementList = null;
		final Symbol forSymbol = symbol;
		Symbol rightCBEndsIf = null;

		next();

		/*
		 * Expr typeInDec = type(); while ( true ) {
		 *
		 * if ( symbol.token != Token.COMMA ) break; else {
		 * lexer.checkWhiteSpaceParenthesisAfter(symbol, ","); next(); if (
		 * typeInDec != null ) lastTypeInDec = typeInDec; typeInDec = type(); }
		 * }
		 *
		 */
		Expr typeInDec = type();
		ExprIdentStar id;
		SymbolIdent variableSymbol;

		if ( symbol.token == Token.IDENT ) {
			variableSymbol = (SymbolIdent) symbol;
			id = this.ident();
			if ( id.getIdentSymbolArray().size() != 1 ) {
				this.error2(id.getFirstSymbol(), "An identifier was expected");
			}

			if ( Character
					.isUpperCase(variableSymbol.getSymbolString().charAt(0)) )
				this.error2(variableSymbol,
						"Variables cannot start with an uppercase letter");

		}
		else {
			if ( !(typeInDec instanceof ExprIdentStar) ) {
				error2(symbol, "Variable name expected." + foundSuch());
			}
			if ( ((ExprIdentStar) typeInDec).getIdentSymbolArray().size() > 1 )
				error2(typeInDec.getFirstSymbol(),
						"Identifier expected." + foundSuch());
			final Symbol ident = ((ExprIdentStar) typeInDec)
					.getIdentSymbolArray().get(0);
			if ( !(ident instanceof SymbolIdent) ) {
				if ( symbol instanceof lexer.SymbolKeyword )
					error2(ident, "Keyword '" + symbol.getSymbolString()
							+ "' used as an Identifier");
				else
					error2(ident, "Identifier expected." + foundSuch());
			}
			typeInDec = null;
			variableSymbol = (SymbolIdent) ident;
		}

		if ( symbol.token != Token.IN ) {
			error2(symbol, "'in' expected");
		}
		next();
		final Expr forExpr = expr();

		final StatementLocalVariableDec localVariableDec = new StatementLocalVariableDec(
				variableSymbol, null, null, currentMethod,
				functionStack.size() + 1, true, currentMethod);

		final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
				.get(variableSymbol.getSymbolString());
		if ( cyanMacro != null ) {
			this.error2(variableSymbol,
					"This variable has the name of a macro keyword of a macro imported by this compilation unit");
		}

		if ( functionStack.size() > 0 ) {
			localVariableDec.setDeclaringFunction(functionStack.peek());
		}
		this.localVariableDecStack.push(localVariableDec);

		int sizeLocalVariableDecList = -1;

		if ( !functionStack.isEmpty() ) {
			final ExprFunction currentFunction = functionStack.peek();
			sizeLocalVariableDecList = currentFunction
					.getSizeLocalVariableDecList();
			currentFunction.addLocalVariableDec(localVariableDec);
		}

		// #
		final int numberOfLocalVariables = this.localVariableDecStack.size();

		if ( symbol.token != Token.LEFTCB ) {
			error2(symbol,
					"'{' expected after 'for' expression." + foundSuch());
		}
		else {
			next();
			++whileForCount;

			statementList = statementList();
			--whileForCount;
			if ( symbol.token != Token.RIGHTCB ) {
				error2(symbol, "'}' expected at the end of a 'for' statement."
						+ foundSuch());
			}
			else {
				rightCBEndsIf = symbol;
				next();
			}
			// #
			while (localVariableDecStack.size() > numberOfLocalVariables)
				localVariableDecStack.pop();
			this.localVariableDecStack.pop();

			if ( !functionStack.isEmpty() && sizeLocalVariableDecList > 0 ) {
				final ExprFunction currentFunction = functionStack.peek();
				currentFunction.trimLocalVariableDecListToSize(
						sizeLocalVariableDecList);
			}

		}

		return new StatementFor(forSymbol, typeInDec, localVariableDec, forExpr,
				statementList, rightCBEndsIf, currentMethod);
	}

	/*
	 * type str case String str2 { str2[0] println } case Nil nil2 { }
	 *
	 */
	private StatementType typeStatement() {

		Symbol rightCBEndsIf = null;

		final Symbol typeSymbol = symbol;
		next();
		final Expr expr = expr();
		final StatementType statementType = new StatementType(typeSymbol, expr,
				currentMethod);
		if ( this.symbol.token != Token.CASE ) {
			this.error2(this.symbol,
					"'case' expected after the expression of 'type'");
		}
		while (symbol.token == Token.CASE) {
			final Symbol caseSymbol = symbol;
			next();

			StatementList statementList = null;

			final Expr caseExprType = type();

			Symbol idSymbol = null;
			StatementLocalVariableDec localVariableDec = null;
			if ( symbol.token == Token.IDENT ) {
				idSymbol = symbol;
				next();
				localVariableDec = new StatementLocalVariableDec(
						(SymbolIdent) idSymbol, null, null, currentMethod,
						functionStack.size() + 1, true, currentMethod);

				final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
						.get(idSymbol.getSymbolString());
				if ( cyanMacro != null ) {
					this.error2(idSymbol,
							"This variable has the name of a macro keyword of a macro imported by this compilation unit");
				}

				if ( functionStack.size() > 0 ) {
					localVariableDec.setDeclaringFunction(functionStack.peek());
				}
				this.localVariableDecStack.push(localVariableDec);

			}

			int sizeLocalVariableDecList = -1;

			if ( !functionStack.isEmpty() ) {
				final ExprFunction currentFunction = functionStack.peek();
				sizeLocalVariableDecList = currentFunction
						.getSizeLocalVariableDecList();
				if ( localVariableDec != null ) {
					currentFunction.addLocalVariableDec(localVariableDec);
				}
			}

			// #
			final int numberOfLocalVariables = this.localVariableDecStack
					.size();

			if ( symbol.token != Token.LEFTCB ) {
				error2(symbol, "'{' expected after 'case Type variable' ."
						+ foundSuch());
			}
			else {
				next();

				statementList = statementList();
				if ( symbol.token != Token.RIGHTCB ) {
					error2(symbol,
							"'}' expected at the end of a 'type-case' statement."
									+ foundSuch());
				}
				else {
					rightCBEndsIf = symbol;
					next();
				}
				// #
				while (localVariableDecStack.size() > numberOfLocalVariables)
					localVariableDecStack.pop();
				if ( idSymbol != null ) {
					this.localVariableDecStack.pop();
				}

				if ( !functionStack.isEmpty()
						&& sizeLocalVariableDecList > 0 ) {
					final ExprFunction currentFunction = functionStack.peek();
					currentFunction.trimLocalVariableDecListToSize(
							sizeLocalVariableDecList);
				}

				statementType.addCaseRecord(new CaseRecord(caseSymbol,
						caseExprType, localVariableDec, statementList,
						rightCBEndsIf));

			}

		}
		StatementList elseStatementList = null;
		if ( symbol.token == Token.ELSE ) {
			next();

			int sizeLocalVariableDecList = -1;
			if ( !functionStack.isEmpty() ) {
				final ExprFunction currentFunction = functionStack.peek();
				sizeLocalVariableDecList = currentFunction
						.getSizeLocalVariableDecList();
			}

			final int numberOfLocalVariables = this.localVariableDecStack
					.size();

			if ( symbol.token != Token.LEFTCB ) {
				error2(symbol, "'{' expected after 'else'." + foundSuch());
			}
			else {
				next();

				elseStatementList = statementList();
				if ( symbol.token != Token.RIGHTCB ) {
					error2(symbol,
							"'}' expected at the end of a 'type-case-else' statement."
									+ foundSuch());
				}
				else {
					rightCBEndsIf = symbol;
					next();
				}
				// #
				while (localVariableDecStack.size() > numberOfLocalVariables)
					localVariableDecStack.pop();

				if ( !functionStack.isEmpty()
						&& sizeLocalVariableDecList > 0 ) {
					final ExprFunction currentFunction = functionStack.peek();
					currentFunction.trimLocalVariableDecListToSize(
							sizeLocalVariableDecList);
				}

			}
			statementType.setElseStatementList(elseStatementList);
		}
		return statementType;
	}

	/**
	 * VariableDec ::= [ ``var"\/ ] [ Type ] Id [ ``="\/ Expr ] \{ ``,"\/ [ Type
	 * ] Id [ ``="\/ Expr ] \} [ ``;"\/ ]
	 *
	 * @return
	 */
	private StatementLocalVariableDecList localVariableDec(boolean isReadonly) {
		Expr typeInDec = null;
		final StatementLocalVariableDecList localVariableDecList = new StatementLocalVariableDecList(
				symbol, currentMethod);
		SymbolIdent variableSymbol;
		next();

		/*
		 * AnnotationAt annotation = null; if ( symbol.token == Token.annotation
		 * ) { annotation = annotation(false); if ( !
		 * annotation.getCyanMetaobject().mayBeAttached(DeclarationKind.
		 * LOCAL_VAR_DEC) ) { this.error2(annotation.getFirstSymbol(),
		 * "Metaobject '" + annotation.getCyanMetaobject().getName() +
		 * "' cannot be attached to declaration of variables"); } }
		 *
		 */

		Expr lastTypeInDec = null;
		typeInDec = type();
		while (true) {

			if ( symbol.token == Token.IDENT ) {
				variableSymbol = (SymbolIdent) symbol;

				if ( Character.isUpperCase(
						variableSymbol.getSymbolString().charAt(0)) )
					this.error2(variableSymbol,
							"Variables cannot start with an uppercase letter");

				next();
				localVariableDecList.add(singleLocalVariableDec(typeInDec,
						variableSymbol, isReadonly));
			}
			else {
				/*
				 * no type at the declaration
				 */
				if ( !(typeInDec instanceof ExprIdentStar) ) {
					error2(symbol, "Variable name expected." + foundSuch());
				}
				if ( ((ExprIdentStar) typeInDec).getIdentSymbolArray()
						.size() > 1 )
					error2(typeInDec.getFirstSymbol(),
							"Identifier expected." + foundSuch());
				final Symbol ident = ((ExprIdentStar) typeInDec)
						.getIdentSymbolArray().get(0);
				if ( !(ident instanceof SymbolIdent) ) {
					if ( symbol instanceof lexer.SymbolKeyword )
						error2(ident, "Keyword '" + symbol.getSymbolString()
								+ "' used as an Identifier");
					else
						error2(ident, "Identifier expected." + foundSuch());
				}
				typeInDec = null;
				variableSymbol = (SymbolIdent) ident;
				localVariableDecList.add(singleLocalVariableDec(lastTypeInDec,
						variableSymbol, isReadonly));
			}
			if ( symbol.token != Token.COMMA )
				break;
			else {
				lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
				next();
				if ( typeInDec != null ) lastTypeInDec = typeInDec;
				typeInDec = type();
			}
		}

		if ( !functionStack.isEmpty() ) {
			final ExprFunction currentFunction = functionStack.peek();
			for (final StatementLocalVariableDec localDec : localVariableDecList
					.getLocalVariableDecList()) {
				currentFunction.addLocalVariableDec(localDec);
			}
		}

		if ( localVariableDecList.getLocalVariableDecList().size() > 1 ) {
			for (final StatementLocalVariableDec varDec : localVariableDecList
					.getLocalVariableDecList()) {
				if ( varDec.getExpr() != null ) {
					this.error2(varDec.getFirstSymbol(),
							"A declaration of several variables with a single type with one of the "
									+ "variables receiving a value. This is illegal. Put the variable or variables that receive a value in a separate declaration");
				}
			}

		}
		/*
		 * if ( annotation != null ) {
		 * localVariableDecList.setBeforeAnnotation(annotation);
		 * annotation.setDeclaration(localVariableDecList); }
		 */
		return localVariableDecList;
	}

	/**
	 * [ ``="\/ Expr ]
	 *
	 * @param typeInDec
	 * @param variableSymbol
	 * @return
	 */
	private StatementLocalVariableDec singleLocalVariableDec(Expr typeInDec,
			SymbolIdent variableSymbol, boolean isReadonly) {
		Expr expr;

		if ( symbol.token == Token.ASSIGN ) {
			next();
			expr = expr();
		}
		else {
			if ( isReadonly ) {
				error2(variableSymbol,
						"A read-only variable should be followed by '= expr'");
			}
			expr = null;
		}

		if ( equalToFormalGenericParameter(variableSymbol.getSymbolString()) ) {
			error2(variableSymbol,
					"Local variable names cannot be equal to one of the formal parameters of the generic prototype");
		}

		final StatementLocalVariableDec localVariableDec = new StatementLocalVariableDec(
				variableSymbol, typeInDec, expr, currentMethod,
				functionStack.size() + 1, isReadonly, currentMethod);

		final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
				.get(variableSymbol.getSymbolString());
		if ( cyanMacro != null ) {
			this.error2(variableSymbol,
					"This variable has the name of a macro keyword of a macro imported by this compilation unit");
		}

		if ( functionStack.size() > 0 ) {
			localVariableDec.setDeclaringFunction(functionStack.peek());
		}
		this.localVariableDecStack.push(localVariableDec);
		return localVariableDec;
	}

	private StatementReturn returnStatement() {

		final Symbol returnSymbol = symbol;
		next();

		final Expr returnedExpr = expr();
		final StatementReturn statementReturn = new StatementReturn(
				returnSymbol, returnedExpr, currentMethod);
		if ( !functionStack.isEmpty() ) {
			this.error2(returnSymbol,
					"Currently return statements inside anonymous function are not allowed");
			/*
			 * functionStack.peek().addStatementReturn(statementReturn); for (
			 * ExprFunction ef : functionStack ) {
			 * ef.setHasMethodReturnStatement(true); }
			 */
		}

		return statementReturn;
	}

	private StatementReturnFunction returnFunctionStatement() {
		final Symbol returnSymbol = symbol;

		next();
		final Expr returnedExpr = expr();

		if ( functionStack.isEmpty() ) {
			this.error2(false, returnSymbol,
					"Return of a function with '^'. But this statement is not inside a function. ",
					true);
			return new StatementReturnFunction(returnSymbol, returnedExpr, null,
					currentMethod);
		}

		/*
		 * if ( functionStack.size() == 0 && nestedIfWhile > 0 )
		 * this.error(true, returnSymbol, "Return of a function with '^ " +
		 * returnedExpr.asString() +
		 * "'. But this statement is not inside a function. " +
		 * "This statement cannot be used as in 'if i < 10 { ^ 0 }'. The same applies other statements that use { and }"
		 * , null, ErrorKind.return_with_caret_outside_a_function);
		 */

		final StatementReturnFunction statementReturnFunction = new StatementReturnFunction(
				returnSymbol, returnedExpr, functionStack.peek(),
				currentMethod);
		functionStack.peek()
				.addStatementReturnFunction(statementReturnFunction);
		return statementReturnFunction;
	}

	/*
	 * ExprAssign ::= Expr [ Assign ] Expr ::= OrExpr [ MessageSendNonUnary ] |
	 * MessageSendNonUnary Assign ::= { "," OrExpr } "=" OrExpr
	 */
	/**
	 * analyzes an expression or an assignment. The return value is of type
	 * Statement but it may also be of type Expr, subtype of Statement
	 *
	 * @return
	 */
	private Statement exprAssign() {

		Expr e = expr();
		StatementAssignmentList assignmentList = null;
		Statement stat;

		if ( symbol.token != Token.COMMA && symbol.token != Token.ASSIGN )
			stat = e;
		else {
			checkIfAssignable(e);
			assignmentList = new StatementAssignmentList(currentMethod);
			assignmentList.add(e);
			if ( symbol.token == Token.COMMA ) {
				while (symbol.token == Token.COMMA) {
					lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
					next();
					e = expr();
					checkIfAssignable(e);
					assignmentList.add(e);
				}
			}
			if ( symbol.token != Token.ASSIGN ) error2(symbol,
					"'=' expected after a list of expressions separated by commas."
							+ foundSuch());
			for (final Expr leftExpr : assignmentList.getExprList()) {
				if ( leftExpr instanceof ExprIndexed ) {
					((ExprIndexed) leftExpr).setLeftHandSideAssignment(true);
				}
			}
			next();
			assignmentList.add(expr());
			stat = assignmentList;
		}
		if ( symbol.token == Token.METAOBJECT_ANNOTATION ) {
			if ( symbol.getSymbolString()
					.equals(MetaHelper.popCompilationContextName) ) {
				if ( stat.getAfterAnnotation() == null ) {
					final AnnotationAt annotation = annotation(false);
					stat.setAfterAnnotation(annotation);
				}
			}
		}

		return stat;
	}

	private void checkIfAssignable(Expr e) {
		if ( !(e instanceof ExprIdentStar) && !(e instanceof ExprIndexed)
				&& !(e instanceof ExprSelfPeriodIdent)
				&& !(e instanceof ExprSelf__PeriodIdent) ) {
			if ( symbol.token == Token.COMMA )
				error2(symbol,
						"This seems a variable declaration. But there is a 'var' or 'let' keyword missing");
			else {
				warning(symbol, symbol.getLineNumber(),
						"Expression on the left is not assignable. If this is a variable declaration, you are forgetting to put keyword 'var' before it.");
				final Symbol firstSymbol = e.getFirstSymbol();
				if ( ask(symbol,
						"Should I insert 'var' before "
								+ firstSymbol.getSymbolString() + " ? (y, n)") )
					compilationUnit
							.addAction(new ActionInsert("var ", compilationUnit,
									firstSymbol.startOffsetLine
											+ firstSymbol.getColumnNumber() - 1,
									firstSymbol.getLineNumber(),
									firstSymbol.getColumnNumber()));

			}
		}
	}

	public Expr expr() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprXor();

		while (symbol.token == Token.OR) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			final Expr rightExpr = exprXor();
			leftExpr = new ExprBooleanOr(leftExpr, symbolOperator, rightExpr,
					currentMethod);

		}
		leftExpr.setLastSymbol(previousSymbol);
		leftExpr.setNextSymbol(symbol);
		return leftExpr;
	}

	private Expr exprXor() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprAnd();
		while (symbol.token == Token.XOR) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			// leftExpr = new ExprXor(leftExpr, symbolOperator,
			// exprAnd(firstExpr));
			final Expr rightExpr = exprAnd();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	private Expr exprAnd() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprEqGt();
		while (symbol.token == Token.AND) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			final Expr rightExpr = exprEqGt();
			leftExpr = new ExprBooleanAnd(leftExpr, symbolOperator, rightExpr,
					currentMethod);

		}
		return leftExpr;
	}

	private Expr exprEqGt() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprBinExclamation();
		while (symbol.token == Token.EQGT || symbol.token == Token.EQEQGT) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			final Expr rightExpr = exprBinExclamation();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	private Expr exprBinExclamation() {

		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprRel();
		while (symbol.token == Token.NOT) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			final Expr rightExpr = exprRel();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;

	}

	private Expr exprRel() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprMSNonUnary();
		if ( symbol.token == Token.LT_NOT_PREC_SPACE ) {
			// if ( ask(symbol, "Should I insert space before '<' ? (y, n)") ) {
			/*
			 * compilationUnit.addAction( new ActionInsert(" ", compilationUnit,
			 * symbol.startLine + symbol.getColumnNumber() - 1,
			 * symbol.getLineNumber(), symbol.getColumnNumber()));
			 */
			this.error2(symbol,
					"There should be a space before the comparision operator '"
							+ symbol.getSymbolString() + "'");
			// symbol.token = Token.LT;
			// }

		}
		/*
		 * if ( symbol.token == Token.LT && ! Lexer.hasSpaceAfter(symbol,
		 * compilationUnit) ) compilationUnit.addAction( new ActionInsert(" ",
		 * compilationUnit, symbol.startLine + symbol.getColumnNumber() +
		 * symbol.getSymbolString().length() - 1, symbol.getLineNumber(),
		 * symbol.getColumnNumber()));
		 *
		 *
		 * if ( symbol.token == Token.GT && ! Lexer.hasSpaceAfter(symbol,
		 * compilationUnit) ) compilationUnit.addAction( new ActionInsert(" ",
		 * compilationUnit, symbol.startLine + symbol.getColumnNumber() +
		 * symbol.getSymbolString().length() - 1, symbol.getLineNumber(),
		 * symbol.getColumnNumber()));
		 *
		 *
		 * if ( symbol.token == Token.GT && ! Lexer.hasSpaceBefore(symbol,
		 * compilationUnit) ) compilationUnit.addAction( new ActionInsert(" ",
		 * compilationUnit, symbol.startLine + symbol.getColumnNumber(),
		 * symbol.getLineNumber(), symbol.getColumnNumber()));
		 *
		 */

		if ( isRelationalOperator(symbol.token) ) {
			if ( symbol.token == Token.GT ) {
				/*
				 * this is the only symbol that Lexer does not check if there
				 * spaces before and after
				 */
				this.lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol, "<");
			}
			symbolOperator = (SymbolOperator) symbol;
			next();
			// leftExpr = new ExprRel(leftExpr, symbolOperator,
			// exprInter(firstExpr));
			final Expr rightExpr = exprMSNonUnary();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	public Expr exprMSNonUnary() {

		MessageWithKeywords messageWithkeywords;

		if ( symbol.token == Token.SUPER
				&& canStartMessageSendNonUnary(next(0)) ) {
			final Symbol superSymbol = symbol;
			next();
			messageWithkeywords = messageSendNonUnary();
			final ExprMessageSendWithKeywordsToSuper msSuper = new ExprMessageSendWithKeywordsToSuper(
					superSymbol, messageWithkeywords, symbol, currentMethod);
			this.currentPrototype.addMessageSendWithkeywordsToSuper(msSuper);
			return msSuper;
		}
		else {
			if ( canStartMessageSendNonUnary(symbol)
					|| symbol.token == Token.BACKQUOTE
							&& canStartMessageSendNonUnary(next(0)) ) {
				// message send with keywords to "self"
				messageWithkeywords = messageSendNonUnary();
				return new ExprMessageSendWithKeywordsToExpr(null,
						messageWithkeywords, symbol, currentMethod);
			}
			else {
				final Expr e = exprOrGt();
				e.setNextSymbol(symbol);
				if ( canStartMessageSendNonUnary(symbol)
						|| symbol.token == Token.BACKQUOTE
								&& canStartMessageSendNonUnary(next(0)) ) {
					messageWithkeywords = messageSendNonUnary();
					return new ExprMessageSendWithKeywordsToExpr(e,
							messageWithkeywords, symbol, currentMethod);
				}
				return e;
			}
		}
	}

	private Expr exprOrGt() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprBinPlusPlus_MinusMinus();
		while (symbol.token == Token.ORGT) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			// leftExpr = new ExprAdd(leftExpr, symbolOperator,
			// exprMult(firstExpr));
			final Expr rightExpr = exprBinPlusPlus_MinusMinus();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	final static int sizePlusPlus = 2; // the size of "++" e "--"

	private Expr exprBinPlusPlus_MinusMinus() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprInter();
		while (symbol.token == Token.PLUSPLUS
				|| symbol.token == Token.MINUSMINUS) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			final Expr rightExpr = exprInter();
			if ( symbolOperator.getLineNumber() == rightExpr.getFirstSymbol()
					.getLineNumber() ) {
				if ( symbolOperator.getColumnNumber()
						+ sizePlusPlus == rightExpr.getFirstSymbol()
								.getColumnNumber() ) {
					String msg = "";
					if ( leftExpr.getFirstSymbol()
							.getLineNumber() < symbolOperator
									.getLineNumber() ) {
						msg = ". Beware that this may have been caused "
								+ "by a missing ';' in a previous line "
								+ "because the '++' or '--' is not in the "
								+ "same line as its left operand";
					}
					this.error2(symbolOperator,
							"'++' or '--' here is a binary operator. There should be at least one space after it"
									+ msg);
				}
			}
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	private Expr exprInter() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprAdd();
		if ( symbol.token == Token.TWOPERIOD
				|| symbol.token == Token.TWOPERIODLT ) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			// leftExpr = new ExprRel(leftExpr, symbolOperator,
			// exprAdd(firstExpr));
			final Expr rightExpr = exprAdd();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	private Expr exprAdd() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprMult();
		while (symbol.token == Token.PLUS || symbol.token == Token.MINUS) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			// leftExpr = new ExprAdd(leftExpr, symbolOperator,
			// exprMult(firstExpr));
			final Expr rightExpr = exprMult();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	private Expr exprMult() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprBit();
		while (symbol.token == Token.MULT || symbol.token == Token.DIV
				|| symbol.token == Token.REMAINDER) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			// leftExpr = new ExprMult(leftExpr, symbolOperator,
			// exprBit(firstExpr));
			final Expr rightExpr = exprBit();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	private Expr exprBit() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprShift();
		while (symbol.token == Token.BITAND || symbol.token == Token.BITOR
				|| symbol.token == Token.BITOR3
				|| symbol.token == Token.BITXOR) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			final Expr rightExpr = exprShift();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);
			/*
			 * switch ( symbolOperator.token ) { case BITAND: leftExpr = new
			 * ExprBitAnd(leftExpr, symbolOperator, exprShift(firstExpr));
			 * break; case BITOR: leftExpr = new ExprBitOr(leftExpr,
			 * symbolOperator, exprShift(firstExpr)); break; case BITXOR:
			 * leftExpr = new ExprBitXor(leftExpr, symbolOperator,
			 * exprShift(firstExpr)); break; default: error(symbol,
			 * "internal error at exprBit"); }
			 */
		}
		return leftExpr;
	}

	/**
	 * \p{ExprShift} ::= ExprDotOp [ ShiftOp ExprDotOp ]
	 *
	 * @return
	 */

	private Expr exprShift() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprColonColonOp();
		if ( symbol.token == Token.LEFTSHIFT || symbol.token == Token.RIGHTSHIFT
				|| symbol.token == Token.RIGHTSHIFTTHREE ) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			final Expr rightExpr = exprColonColonOp();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	/**
	 * \p{ExprColonColon} ::= ExprDotOp \{ ``::"\/ ExprDotOp \}
	 */
	private Expr exprColonColonOp() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprDotOp();
		if ( symbol.token == Token.COLONCOLON ) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			final Expr rightExpr = exprDotOp();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	/**
	 * \p{ExprDotOp} ::= ExprUnaryUnMS \{ DotOp ExprUnaryUnMS \}
	 *
	 * \p{DotOp} ::= ``\verb|.*|"\/ \verb"|" ``\verb|.+|"\/
	 */

	private Expr exprDotOp() {
		Expr leftExpr;
		SymbolOperator symbolOperator;

		leftExpr = exprUnaryUnMS2();
		if ( symbol.token == Token.DOT_STAR
				|| symbol.token == Token.DOT_PLUS ) {
			symbolOperator = (SymbolOperator) symbol;
			next();
			final Expr rightExpr = exprUnaryUnMS2();
			leftExpr = new ExprMessageSendWithKeywordsToExpr(leftExpr,
					new MessageBinaryOperator(symbolOperator, rightExpr),
					symbol, currentMethod);

		}
		return leftExpr;
	}

	/**
	 * @param firstExpr
	 * @param mayBeMessageSendWithkeywords
	 * @return
	 */
	private Expr exprUnaryUnMS2() {

		Expr e = exprUnary();
		e.setNextSymbol(symbol);

		if ( symbol.token == Token.IDENT || symbol.token == Token.INTER_ID
				|| symbol.token == Token.INTER_DOT_ID
				|| symbol.token == Token.BACKQUOTE
						&& next(0).token == Token.IDENT ) {

			int numUnarySends = 0;
			if ( e instanceof ExprMessageSendUnaryChainToSuper ) {
				++numUnarySends;
			}
			ExprMessageSendUnaryChainToExpr chain = new ExprMessageSendUnaryChainToExpr(
					e, currentMethod);

			int numBackquotes = 0;
			while (symbol.token == Token.IDENT || symbol.token == Token.INTER_ID
					|| symbol.token == Token.INTER_DOT_ID
					|| symbol.token == Token.BACKQUOTE
							&& next(0).token == Token.IDENT) {

				if ( symbol.token == Token.BACKQUOTE ) {
					chain.setBackquoteSymbol(symbol);
					if ( numUnarySends != 0 ) {
						if ( numBackquotes > 0 )
							// receiver `s `p
							error(true, symbol,
									"Two or more backquotes (`) in a chain of unary messages",
									symbol.getSymbolString(),
									ErrorKind.two_or_more_backquotes_in_unary_chain);
						else
							// receiver s `p
							error(true, symbol,
									"Illegal use of hasBackquote. Unary message chain should have only one unary message when character ` is used",
									symbol.getSymbolString(),
									ErrorKind.illegal_use_of_backquote);

					}
					else {
						chain.setHasBackQuote(true);
						++numBackquotes;
						next();

						/**
						 * each function should have a list of accessed local
						 * variables and parameters of outer scope.
						 */
						/**
						 * it is a parameter, local variable, field, unary
						 * method
						 */
						final String varName = symbol.getSymbolString();
						final Object localVarParameter = searchIdent(varName);
						if ( localVarParameter != null ) {

							int i = functionStack.size() - 1;
							// Iterator<ExprFunction> functionIter =
							// functionStack.iterator();
							while (i >= 0) {
								final ExprFunction function = functionStack
										.get(i);
								--i;
								if ( compilationStep == CompilationStep.step_7
										&& function.searchLocalVariableDec(
												varName) == null ) {

									if ( localVarParameter instanceof ParameterDec ) {
										function.addAccessedParameter(
												(ParameterDec) localVarParameter);
										((ParameterDec) localVarParameter)
												.addInnerObjectNumberList(
														function.getNumber());
									}
									else if ( localVarParameter instanceof StatementLocalVariableDec ) {
										function.addAccessedVariableDec(
												(StatementLocalVariableDec) localVarParameter);
										((StatementLocalVariableDec) localVarParameter)
												.addInnerObjectNumberList(
														function.getNumber());
									}
								}
								else
									/*
									 * if the variable was found, it is not
									 * necessary to consider it as an external
									 * variable for this and all upper level
									 * functions
									 */
									break;
							}

						}

					}
				}
				chain.setUnarySymbol((SymbolIdent) symbol);
				next();
				chain.setNextSymbol(symbol);
				if ( symbol.token == Token.IDENT
						|| symbol.token == Token.INTER_ID
						|| symbol.token == Token.INTER_DOT_ID
						|| symbol.token == Token.BACKQUOTE
								&& next(0).token == Token.IDENT ) {
					chain = new ExprMessageSendUnaryChainToExpr(chain,
							currentMethod);
				}

				++numUnarySends;
			}
			e = chain;
		}

		return e;
	}

	private Expr exprUnary() {
		SymbolOperator unarySymbolOperator = null;
		Expr e;

		if ( symbol.token == Token.PLUSPLUS
				|| symbol.token == Token.MINUSMINUS ) {
			error2(symbol,
					"++ and -- start a statement in Cyan. They do not return an expression and therefore cannot be used here");
		}
		if ( isUnaryOperator(symbol.token) ) {
			unarySymbolOperator = (SymbolOperator) symbol;
			next();
		}

		e = exprPrimary();
		while (symbol.token == Token.LEFTSB
				|| symbol.token == Token.INTER_LEFTSB) {
			final Symbol firstIndexOperator = symbol;
			next();
			final Expr indexOfExpr = expr();
			if ( firstIndexOperator.token == Token.LEFTSB ) {
				if ( symbol.token != Token.RIGHTSB ) {
					error2(symbol,
							"']' expected after the end of an array index. "
									+ foundSuch());
				}
			}
			else if ( firstIndexOperator.token == Token.INTER_LEFTSB ) {
				if ( symbol.token != Token.RIGHTSB_INTER ) {
					error2(symbol,
							"']?' expected after the end of an array index."
									+ foundSuch());
				}
			}
			next();
			e = new ExprIndexed(e, indexOfExpr, firstIndexOperator,
					currentMethod);
		}
		if ( unarySymbolOperator != null ) {
			e = new ExprUnary(unarySymbolOperator, e, currentMethod);
		}
		/*
		 * if ( mayBeMessageSendWithkeywords &&
		 * canStartMessageSendNonUnary(symbol) ) { // this is a non-unary
		 * message send MessageWithKeywords messageWithkeywords =
		 * messageSendNonUnary(); throw new FoundMessageSendExpression(new
		 * ExprMessageSendWithKeywordsToExpr(e, messageWithkeywords)); }
		 */
		return e;
	}

	private Expr exprPrimary() {

		AnnotationAt regularAnnotation;
		Symbol receiverSymbol, selfSymbol;
		switch (symbol.token) {
		case SELF:
			selfSymbol = symbol;
			next();
			Expr e = null;
			if ( symbol.token == Token.PERIOD ) {
				// something like "self.x"
				next();
				if ( symbol.token != Token.IDENT ) {
					error2(symbol,
							"identifier expected after 'self.'" + foundSuch());
				}
				else {
					// ExprIdentStar exprIdentStar = new ExprIdentStar(symbol);
					// Iterator<ExprFunction> functionIter =
					// functionStack.iterator();
					// while ( functionIter.hasNext() ) {
					// ExprFunction function = functionIter.next();
					// function.addPossiblyAcccessedIdentifier(exprIdentStar);
					// }
					e = new ExprSelfPeriodIdent(selfSymbol, symbol,
							currentMethod);
					next();
				}
			}
			else
				e = new ExprSelf(selfSymbol, currentPrototype, currentMethod);
			return e;

		case SUPER:
			receiverSymbol = symbol;
			next();

			if ( this.insideContextFunction() ) error2(receiverSymbol,
					"'super' cannot be used inside a context function. "
							+ "A context function is an anonymous function in which the name of the first parameter is 'self'");
			if ( symbol.token == Token.IDENT || symbol.token == Token.INTER_ID
					|| symbol.token == Token.INTER_DOT_ID
					|| symbol.token == Token.BACKQUOTE
							&& next(0).token == Token.IDENT ) {
				ExprMessageSendUnaryChain chain;
				chain = new ExprMessageSendUnaryChainToSuper(receiverSymbol,
						symbol, currentMethod);

				if ( symbol.token == Token.BACKQUOTE ) {
					chain.setHasBackQuote(true);
					next();

					/**
					 * each function should have a list of accessed local
					 * variables and parameters of outer scope.
					 */
					/**
					 * it is a parameter, local variable, field, unary method
					 */
					final String varName = symbol.getSymbolString();
					final Object localVarParameter = searchIdent(varName);

					if ( localVarParameter != null ) {
						int i = functionStack.size() - 1;
						// Iterator<ExprFunction> functionIter =
						// functionStack.iterator();
						while (i >= 0) {
							final ExprFunction function = functionStack.get(i);
							--i;
							if ( compilationStep == CompilationStep.step_7
									&& function.searchLocalVariableDec(
											varName) == null ) {

								if ( localVarParameter instanceof ParameterDec ) {
									function.addAccessedParameter(
											(ParameterDec) localVarParameter);
									((ParameterDec) localVarParameter)
											.addInnerObjectNumberList(
													function.getNumber());
								}
								else if ( localVarParameter instanceof StatementLocalVariableDec ) {
									function.addAccessedVariableDec(
											(StatementLocalVariableDec) localVarParameter);
									((StatementLocalVariableDec) localVarParameter)
											.addInnerObjectNumberList(
													function.getNumber());
								}
							}
							else
								/*
								 * if the variable was found, it is not
								 * necessary to consider it as an external
								 * variable for this and all upper level
								 * functions
								 */
								break;
						}
					}

				}
				chain.setUnarySymbol((SymbolIdent) symbol);
				next();

				/*
				 * while (symbol.token == Token.IDENT || symbol.token ==
				 * Token.INTER_ID || symbol.token == Token.INTER_DOT_ID ) {
				 * chain.addUnarySymbol(symbol); next(); }
				 */

				return chain;
			}
			else {
				error2(receiverSymbol, "message send expected after 'super'."
						+ foundSuch()
						+ ". Be aware of the precedence order. Use '(' and ')' around non-unary message sends. For example, "
						+ "'1 == super m: 0' should be written '1 == (super m: 0)'");
				return null;
			}

		case IDENT:
			if ( symbol.symbolString
					.equals(NameServer.selfNameInnerPrototypes) ) {
				selfSymbol = symbol;
				next();
				e = null;
				if ( symbol.token == Token.PERIOD ) {
					// something like "self__.x"
					next();
					if ( symbol.token != Token.IDENT ) {
						error2(symbol, "identifier expected after 'self__.'. "
								+ foundSuch());
					}
					else {
						e = new ExprSelf__PeriodIdent(selfSymbol, symbol,
								currentMethod);
						next();
					}
				}
				else
					e = new ExprSelf__(selfSymbol, currentMethod);
				return e;
			}
			else {
				return parseIdent();
			}
		case MACRO_KEYWORD:

			final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
					.get(symbol.getSymbolString());
			if ( cyanMacro == null ) {
				this.error2(symbol, "Internal error: symbol '"
						+ symbol.getSymbolString()
						+ "' is a macro keyword but the macro metaobject was not found");
				return null;
			}
			else {
				return macroCall(cyanMacro, true);
			}

		case TYPEOF:
			if ( prohibitTypeof ) {
				/*
				 * 'typeof' is not allowed inside a method signature or as a
				 * type of a field
				 */
				error2(symbol,
						"'typeof' can only be used inside methods or in the DSL attached to metaobjects that start with @");
			}
			final Symbol typeofSymbol = symbol;
			next();
			if ( symbol.token != Token.LEFTPAR ) error2(symbol,
					"'(' expected after keyword Expr." + foundSuch());
			next();
			final Expr exprType = expr();
			if ( symbol.token != Token.RIGHTPAR ) error2(symbol,
					"')' expected after function typeof." + foundSuch());
			if ( Lexer.hasIdentNumberAfter(symbol, compilationUnitSuper) ) {
				error2(symbol, "letter, number, or '_' after ')'");
			}
			next();
			return new ExprTypeof(typeofSymbol, exprType, currentMethod);

		case METAOBJECT_ANNOTATION:
			/*
			 * The grammar for this expression may be one of the following, in
			 * which annotation is a regular metaobject annotation, it is not an
			 * annotation of markDeletedCode or pushCompilationContext.
			 *
			 * annotation annotation markDeletedCode pushCompilationContext code
			 * popCompilationContext annotation pushCompilationContext code
			 * popCompilationContext markDeletedCode pushCompilationContext code
			 * popCompilationContext pushCompilationContext code
			 * popCompilationContext
			 */

			/*
			 * EXTREMELY redundant code. I know that. It will be correctly some
			 * day. Maybe never.
			 */
			regularAnnotation = annotation(true);

			AnnotationAt nextAnnotation = null, markDeletedCode = null,
					pushAnnotation = null, popAnnotation = null;

			final CyanMetaobjectAtAnnot cyanMetaobject = regularAnnotation
					.getCyanMetaobject();
			if ( cyanMetaobject instanceof CyanMetaobjectCompilationMarkDeletedCode
					|| cyanMetaobject instanceof CyanMetaobjectCompilationContextPush ) {
				/*
				 * markDeletedCode pushCompilationContext code
				 * popCompilationContext pushCompilationContext code
				 * popCompilationContext
				 */

				if ( cyanMetaobject instanceof CyanMetaobjectCompilationMarkDeletedCode ) {

					/*
					 * markDeletedCode pushCompilationContext code
					 * popCompilationContext
					 */

					markDeletedCode = regularAnnotation;
					regularAnnotation = null;

					if ( symbol.token != Token.METAOBJECT_ANNOTATION ) {
						this.error2(symbol, "An annotation of metaobject '"
								+ MetaHelper.pushCompilationContextName
								+ "' was expected. Probably this is error was caused by incorrect use of metaobject annotations such as "
								+ " using two of them in sequence inside an expression: 'k = @pi @other;'");
						return null;
					}
					pushAnnotation = annotation(true);
					if ( !(pushAnnotation
							.getCyanMetaobject() instanceof CyanMetaobjectCompilationContextPush) ) {
						this.error2(pushAnnotation.getFirstSymbol(),
								"A metaobject annotation '"
										+ MetaHelper.pushCompilationContextName
										+ "' was expected. Probably this is error was caused by incorrect use of metaobject annotations such as "
										+ " using two of them in sequence inside an expression: 'k = @pi @other;'");
						return null;
					}
					final Expr insideExpr = expr();

					if ( symbol.token != Token.METAOBJECT_ANNOTATION ) {
						this.error(true, symbol,
								"Expected an annotation of metaobject '"
										+ MetaHelper.popCompilationContextName
										+ "'." + foundSuch(),
								null, ErrorKind.metaobject_error);
						return null;
					}
					else {
						popAnnotation = annotation(true);
						if ( !(popAnnotation
								.getCyanMetaobject() instanceof CyanMetaobjectCompilationContextPop) ) {
							this.error(true, symbol,
									"Expected an annotation of metaobject '"
											+ MetaHelper.popCompilationContextName
											+ "'." + foundSuch(),
									null, ErrorKind.metaobject_error);
							return null;
						}
						return new ExprSurroundedByContext(null,
								markDeletedCode, pushAnnotation, insideExpr,
								popAnnotation, currentMethod);
					}

				}
				else {
					/*
					 * pushCompilationContext code popCompilationContext
					 */
					pushAnnotation = regularAnnotation;
					regularAnnotation = null;

					final Expr insideExpr = expr();

					if ( symbol.token != Token.METAOBJECT_ANNOTATION ) {
						this.error(true, symbol,
								"Expected an annotation of metaobject '"
										+ MetaHelper.popCompilationContextName
										+ "'." + foundSuch(),
								null, ErrorKind.metaobject_error);
						return null;
					}
					else {
						popAnnotation = annotation(true);
						if ( !(popAnnotation
								.getCyanMetaobject() instanceof CyanMetaobjectCompilationContextPop) ) {
							this.error(true, symbol,
									"Expected an annotation of metaobject '"
											+ MetaHelper.popCompilationContextName
											+ "'." + foundSuch(),
									null, ErrorKind.metaobject_error);
							return null;
						}
						return new ExprSurroundedByContext(null, null,
								pushAnnotation, insideExpr, popAnnotation,
								currentMethod);
					}

				}
			}
			else {
				/*
				 * annotation annotation markDeletedCode pushCompilationContext
				 * code popCompilationContext annotation pushCompilationContext
				 * code popCompilationContext
				 *
				 */
				if ( symbol.token != Token.METAOBJECT_ANNOTATION ) {
					/*
					 * annotation
					 */
					return regularAnnotation;
				}
				else {
					/*
					 * annotation markDeletedCode pushCompilationContext code
					 * popCompilationContext annotation pushCompilationContext
					 * code popCompilationContext
					 *
					 */
					nextAnnotation = annotation(true);
					if ( nextAnnotation
							.getCyanMetaobject() instanceof CyanMetaobjectCompilationMarkDeletedCode ) {
						/*
						 * annotation markDeletedCode pushCompilationContext
						 * code popCompilationContext
						 *
						 */

						markDeletedCode = nextAnnotation;
						if ( symbol.token != Token.METAOBJECT_ANNOTATION ) {
							this.error2(symbol, "An annotation of metaobject '"
									+ MetaHelper.pushCompilationContextName
									+ "' was expected. Probably this is error was caused by incorrect use of metaobject annotations such as "
									+ " using two of them in sequence inside an expression: 'k = @pi @other;'");
							return null;
						}
						pushAnnotation = annotation(true);
						if ( !(pushAnnotation
								.getCyanMetaobject() instanceof CyanMetaobjectCompilationContextPush) ) {
							this.error2(pushAnnotation.getFirstSymbol(),
									"An annotation of metaobject '"
											+ MetaHelper.pushCompilationContextName
											+ "' was expected. Probably this is error was caused by incorrect use of metaobject annotations such as "
											+ " using two of them in sequence inside an expression: 'k = @pi @other;'");
							return null;
						}
						final Expr insideExpr = expr();

						if ( symbol.token != Token.METAOBJECT_ANNOTATION ) {
							this.error(true, symbol,
									"Expected an annotation of metaobject '"
											+ MetaHelper.popCompilationContextName
											+ "'." + foundSuch(),
									null, ErrorKind.metaobject_error);
							return null;
						}
						else {
							popAnnotation = annotation(true);
							if ( !(popAnnotation
									.getCyanMetaobject() instanceof CyanMetaobjectCompilationContextPop) ) {
								this.error(true, symbol,
										"Expected an annotation of metaobject '"
												+ MetaHelper.popCompilationContextName
												+ "'." + foundSuch(),
										null, ErrorKind.metaobject_error);
								return null;
							}
							return new ExprSurroundedByContext(
									regularAnnotation, markDeletedCode,
									pushAnnotation, insideExpr, popAnnotation,
									currentMethod);
						}
					}
					else if ( nextAnnotation
							.getCyanMetaobject() instanceof CyanMetaobjectCompilationContextPush ) {
						/*
						 * annotation pushCompilationContext code
						 * popCompilationContext
						 *
						 */
						pushAnnotation = nextAnnotation;

						final Expr insideExpr = expr();

						if ( symbol.token != Token.METAOBJECT_ANNOTATION ) {
							this.error(true, symbol,
									"Expected an annotation of metaobject '"
											+ MetaHelper.popCompilationContextName
											+ "'." + foundSuch(),
									null, ErrorKind.metaobject_error);
							return null;
						}
						else {
							popAnnotation = annotation(true);
							if ( !(popAnnotation
									.getCyanMetaobject() instanceof CyanMetaobjectCompilationContextPop) ) {
								this.error(true, symbol,
										"Expected an annotation of metaobject '"
												+ MetaHelper.popCompilationContextName
												+ "'." + foundSuch(),
										null, ErrorKind.metaobject_error);
								return null;
							}
							return new ExprSurroundedByContext(
									regularAnnotation, null, pushAnnotation,
									insideExpr, popAnnotation, currentMethod);
						}

					}
					else {
						this.error2(nextAnnotation.getFirstSymbol(),
								"An annotation of metaobject '"
										+ MetaHelper.pushCompilationContextName
										+ "' was expected. Probably this is error was caused by incorrect use of metaobject annotations such as "
										+ " using two of them in sequence inside an expression: 'k = @pi @other;'");
						return null;
					}

				}
			}

		case LEFTPAR:
			Symbol leftParSymbol = symbol;
			Symbol rightParSymbol = null;
			Expr exprPar;
			next();

			exprPar = expr();
			if ( symbol.token != Token.RIGHTPAR ) {
				if ( symbol.token == Token.ASSIGN ) {
					error2(symbol,
							"assignments are statements in Cyan. They cannot appear inside an expression");
				}
				error2(symbol, "')' expected." + foundSuch());
			}
			else {
				if ( Lexer.hasIdentNumberAfter(symbol, compilationUnitSuper) ) {
					error2(symbol, "letter, number, or '_' after ')'");
				}
				rightParSymbol = symbol;
				next();
			}
			exprPar = new ExprWithParenthesis(leftParSymbol, exprPar,
					rightParSymbol, currentMethod);
			return exprPar;

		case NULL:

		default:
			if ( isBasicType(symbol.token) || symbol.token == Token.STRING ) {
				final Symbol s = symbol;
				next();
				Expr retExpr = new ExprIdentStar(currentMethod, s);

				if ( symbol.token == Token.LEFTPAR ) {
					/*
					 * object creation such as in Person("jose") Stack<Int>(10)
					 */
					leftParSymbol = symbol;
					next();
					final List<Expr> exprArray = realParameters();
					if ( symbol.token != Token.RIGHTPAR ) error2(symbol,
							"')' expected after passing parameters to a context object."
									+ foundSuch());
					rightParSymbol = symbol;
					if ( Lexer.hasIdentNumberAfter(symbol,
							compilationUnitSuper) ) {
						error2(symbol, "letter, number, or '_' after ')'");
					}

					next();
					retExpr = new ExprObjectCreation(retExpr, exprArray,
							rightParSymbol, currentMethod);
				}
				return retExpr;

			} // redo
			/*
			 * else if ( symbol.token == Token.IDENTCOLON || symbol.token ==
			 * Token.INTER_ID_COLON || symbol.token == Token.INTER_DOT_ID_COLON
			 * ) { /* message send to implicit self as in b = eq: other;
			 *
			 * MessageWithKeywords m = messageSendNonUnary(); return new
			 * ExprMessageSendWithKeywordsToExpr(null, m); }
			 */
			else
				return exprLiteral();
		}
	}

	private String foundSuch() {
		String s = " Found '" + symbol.getSymbolString() + "'";
		if ( symbol instanceof SymbolKeyword
				&& !(symbol instanceof SymbolMacroKeyword) ) {
			s = s + " which is a Cyan keyword";
		}
		return s;
	}

	private static String foundSuch(Symbol sym) {
		String s = " Found '" + sym.getSymbolString() + "'";
		if ( sym instanceof SymbolKeyword
				&& !(sym instanceof SymbolMacroKeyword) ) {
			s = s + " which is a Cyan keyword";
		}
		return s;
	}

	/**
	 * parse an identifier
	 *
	 * @return
	 */

	public Expr parseIdent() {
		Expr identExpr = null;
		final Symbol identOne = symbol;
		next();
		/*
		 * It can be a local variable, an field, a literal object, a macro call,
		 * or a unary method name. prototypes may be preceded by a package name
		 * such as in math.Sin And generic object should be followed by an Expr
		 * list between < and >
		 */

		if ( symbol.token != Token.PERIOD ) {

			final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
					.get(identOne.getSymbolString());
			if ( cyanMacro != null ) {
				final AnnotationMacroCall mCall = macroCall(cyanMacro, true);
				return mCall != null ? mCall
						: new ExprNonExpression(currentMethod);
				/*
				 * if ( ! compInstSet.contains(saci.CompilationInstruction.
				 * parsing_actions) ) { this.error(true, symbol,
				 * "macro call in a compiler step that does not allow macros",
				 * identOne.getSymbolString(), ErrorKind.
				 * parsing_compilation_phase_literal_objects_and_macros_are_not_allowed
				 * ); return new ExprNonExpression(); } else { return
				 * macroCall(cyanMacro, true); }
				 */
			}

			identExpr = new ExprIdentStar(currentMethod, identOne);
			/** it is a parameter, local variable, field, unary method */
			final VariableDecInterface localVarParameter = searchIdent(
					identOne.getSymbolString());

			if ( localVarParameter != null ) {

				addAccessedLocalVariableToFunctions(identOne,
						localVarParameter);
			}
		}
		else {

			final List<Symbol> identSymbolArray = new ArrayList<Symbol>();
			identSymbolArray.add(identOne);
			while (symbol.token == Token.PERIOD) {
				next();
				if ( symbol.token != Token.IDENT
						&& !isBasicType(symbol.token) ) {
					error2(symbol,
							"package, object name or slot (variable or method) expected."
									+ foundSuch());
				}
				identSymbolArray.add(symbol);
				next();
			}
			identExpr = new ExprIdentStar(identSymbolArray, symbol,
					currentMethod);
		}

		if ( symbol.token == Token.LT_NOT_PREC_SPACE ) {
			if ( this.previousSymbol.token == Token.IDENT ) {
				String symStr = this.previousSymbol.getSymbolString();
				if ( Character.isLowerCase(symStr.charAt(0)) ) {
					this.error2(symbol, "'<' is joined to " + symStr
							+ " without space. Therefore it is considered a type. But it cannot "
							+ "because it starts with a lower-case letter. If you want to compare"
							+ " two values with '<', put a space before and after this symbol");
				}
			}
			final List<List<Expr>> arrayOfTypeList = new ArrayList<List<Expr>>();
			while (symbol.token == Token.LT_NOT_PREC_SPACE) {
				next();
				final List<Expr> aTypeList = genericPrototypeArgList();
				if ( symbol.token != Token.GT ) {
					error2(symbol,
							"'>' expected after the types of a generic object."
									+ foundSuch());
				}
				next();
				arrayOfTypeList.add(aTypeList);
			}

			MessageSendToAnnotation messageSendToAnnotation = null;
			if ( symbol.token == Token.DOT_OCTOTHORPE ) {
				// if ( symbol.token == Token.CYANSYMBOL ) {
				messageSendToAnnotation = this.parseMessageSendToAnnotation();
			}

			ExprGenericPrototypeInstantiation gpi;
			identExpr = gpi = new ExprGenericPrototypeInstantiation(
					(ExprIdentStar) identExpr, arrayOfTypeList,
					currentPrototype, messageSendToAnnotation, currentMethod);

			final Tuple2<Symbol, String> tss = checkExprGenericPrototypeInstantiation(
					gpi);
			if ( tss != null ) {
				this.error2(tss.f1, tss.f2);
			}
			if ( messageSendToAnnotation != null ) if ( !messageSendToAnnotation
					.action(compilationUnit.getProgram(),
							(ExprGenericPrototypeInstantiation) identExpr) ) {
								error2(identExpr.getFirstSymbol(),
										"No action associated to message '"
												+ messageSendToAnnotation
														.getMessage()
												+ "'");
							}
		}
		else {
			if ( identExpr instanceof ExprIdentStar ) {
				MessageSendToAnnotation messageSendToAnnotation = null;
				if ( symbol.token == Token.DOT_OCTOTHORPE ) {
					// if ( symbol.token == Token.CYANSYMBOL ) {
					messageSendToAnnotation = this
							.parseMessageSendToAnnotation();
				}
				((ExprIdentStar) identExpr)
						.setMessageSendToAnnotation(messageSendToAnnotation);
				if ( messageSendToAnnotation != null ) {
					if ( !messageSendToAnnotation.action(
							compilationUnit.getProgram(),
							(ExprIdentStar) identExpr) ) {
						error2(identExpr.getFirstSymbol(),
								"No action associated to message '"
										+ messageSendToAnnotation.getMessage()
										+ "'");
					}
				}
			}
		}

		if ( symbol.token == Token.LEFTPAR ) {
			/*
			 * object creation such as in Person("jose") Stack<Int>(10)
			 */
			next();
			final List<Expr> exprArray = realParameters();
			if ( symbol.token != Token.RIGHTPAR ) error2(symbol,
					"')' expected after passing parameters to a context object."
							+ foundSuch());
			final Symbol rightParSymbol = symbol;
			if ( Lexer.hasIdentNumberAfter(symbol, compilationUnitSuper) ) {
				error2(symbol, "letter, number, or '_' after ')'");
			}

			next();
			identExpr = new ExprObjectCreation(identExpr, exprArray,
					rightParSymbol, currentMethod);
		}

		return identExpr;

	}

	private static Tuple2<Symbol, String> checkExprGenericPrototypeInstantiation(
			ExprGenericPrototypeInstantiation gpi) {

		for (final List<Expr> list : gpi.getRealTypeListList()) {
			for (final Expr expr : list) {
				if ( expr instanceof ExprGenericPrototypeInstantiation ) {
					final ExprGenericPrototypeInstantiation typeParam = (ExprGenericPrototypeInstantiation) expr;
					if ( typeParam.getAnnotationToTypeList() != null
							&& typeParam.getAnnotationToTypeList()
									.size() > 0 ) {
						return new Tuple2<Symbol, String>(expr.getFirstSymbol(),
								"Type with attached metaobjects cannot be parameters to generic prototypes");
					}
				}
				else if ( expr instanceof ExprIdentStar ) {
					final ExprIdentStar typeParam = (ExprIdentStar) expr;
					if ( typeParam.getAnnotationToTypeList() != null
							&& typeParam.getAnnotationToTypeList()
									.size() > 0 ) {
						return new Tuple2<Symbol, String>(expr.getFirstSymbol(),
								"Type with attached metaobjects cannot be parameters to generic prototypes");
					}
				}

			}
		}
		return null;
	}

	/**
	 * @param identOne
	 * @param localVarParameter
	 */
	private void addAccessedLocalVariableToFunctions(Symbol identOne,
			VariableDecInterface localVarParameter) {
		/**
		 * each function should have a list of accessed local variables and
		 * parameters of outer scope.
		 */

		final String varName = localVarParameter.getName();

		int i = functionStack.size() - 1;
		// Iterator<ExprFunction> functionIter = functionStack.iterator();
		while (i >= 0) {
			final ExprFunction function = functionStack.get(i);
			--i;

			if ( function.isContextFunction()
					&& localVarParameter instanceof StatementLocalVariableDec ) {
				if ( ((StatementLocalVariableDec) localVarParameter)
						.getLevel() < function.getFunctionLevel() ) {
					/*
					 * an external local variable is used inside a context
					 * function
					 */
					error2(identOne,
							"External local variable used inside a context function");
				}
			}
			/*
			 * if varName was not found in the list of parameters and local
			 * variables of 'function', insert it into the list of accessed
			 * parameters or local variables
			 */

			if ( function.searchLocalVariableDec(varName) == null
					&& function.searchParameter(varName) == null
					&& compilationStep == CompilationStep.step_7 ) {

				if ( localVarParameter instanceof ParameterDec ) {
					function.addAccessedParameter(
							(ParameterDec) localVarParameter);
					((ParameterDec) localVarParameter)
							.addInnerObjectNumberList(function.getNumber());
				}
				else if ( localVarParameter instanceof StatementLocalVariableDec ) {
					function.addAccessedVariableDec(
							(StatementLocalVariableDec) localVarParameter);
					((StatementLocalVariableDec) localVarParameter)
							.addInnerObjectNumberList(function.getNumber());
				}
			}
			else
				/*
				 * if the variable was found, it is not necessary to consider it
				 * as an external variable for this and all upper level
				 * functions
				 */
				break;
		}
	}

	public Expr exprLiteral() {
		Expr ret;

		switch (symbol.token) {
		case BYTELITERAL:
			ret = new ExprLiteralByte(symbol, currentMethod);
			next();
			break;
		case SHORTLITERAL:
			ret = new ExprLiteralShort(symbol, currentMethod);
			next();
			break;
		case INTLITERAL:
			ret = new ExprLiteralInt(symbol, currentMethod);
			next();
			break;
		case LONGLITERAL:
			ret = new ExprLiteralLong(symbol, currentMethod);
			next();
			break;
		case FLOATLITERAL:
			ret = new ExprLiteralFloat(symbol, currentMethod);
			next();
			break;
		case DOUBLELITERAL:
			ret = new ExprLiteralDouble(symbol, currentMethod);
			next();
			break;
		case CHARLITERAL:
			ret = new ExprLiteralChar(symbol, currentMethod);
			next();
			break;
		case FALSE:
			ret = new ExprLiteralBooleanFalse(symbol, currentMethod);
			next();
			break;
		case TRUE:
			ret = new ExprLiteralBooleanTrue(symbol, currentMethod);
			next();
			break;
		case LITERALSTRING:
			// this rule represents both strings of the kind "hello" or """
			// multiple lines """

			ExprLiteralString els;
			ret = els = new ExprLiteralString(symbol, currentMethod);
			if ( els.getVarNameList() != null ) {
				for (final String varName : els.getVarNameList()) {
					/**
					 * it is a parameter, local variable, field, unary method
					 */
					final VariableDecInterface localVarParameter = searchIdent(
							varName);

					if ( localVarParameter != null ) {

						addAccessedLocalVariableToFunctions(
								els.getFirstSymbol(), localVarParameter);
					}
				}
			}

			next();
			break;
		case NIL:
			ret = new ExprLiteralNil(symbol, currentMethod);
			next();
			break;
		case LEFTSB:

			lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol, "[");
			Symbol startSymbol, endSymbol;
			startSymbol = symbol;
			List<Expr> exprList = new ArrayList<Expr>();
			next();
			boolean foundLiteralArray = true;
			if ( startExpr(symbol) ) {
				exprList.add(expr());

				if ( symbol.token == Token.RETURN_ARROW ) {
					lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol,
							symbol.symbolString);
					// a literal map
					foundLiteralArray = false;
					next();
					exprList.add(expr());
					while (symbol.token == Token.COMMA) {
						lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
						next();
						exprList.add(expr());
						if ( symbol.token != Token.RETURN_ARROW ) {
							error2(symbol, "'->' was expected");
						}
						lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol,
								symbol.symbolString);

						next();
						exprList.add(expr());
					}
				}
				else {
					// a literal array
					while (symbol.token == Token.COMMA) {
						lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
						next();
						exprList.add(expr());
					}

				}
			}
			else {
				if ( symbol.token == Token.RIGHTSB )
					error(true, symbol, "Empty literal arrays are illegal",
							null, ErrorKind.empty_literal_array);
				else
					error(true, symbol, "Expression expected." + foundSuch(),
							null, ErrorKind.expression_expected_inside_method);
			}
			if ( symbol.token != Token.RIGHTSB ) {
				error2(symbol, "']' expected at the end of literal array or map"
						+ foundSuch());
			}
			lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol, "]");
			endSymbol = symbol;
			next();
			if ( foundLiteralArray ) {
				ret = new ExprLiteralArray(startSymbol, endSymbol, exprList,
						currentMethod);
			}
			else {
				ret = new ExprLiteralMap(startSymbol, endSymbol, exprList,
						currentMethod);
			}
			break;
		case LEFTSB_DOT:
			startSymbol = symbol;
			next();

			exprList = new ArrayList<Expr>();

			Expr firstExpr;
			boolean namedTuple = false;

			firstExpr = expr();
			if ( symbol.token == Token.ASSIGN ) {
				if ( !(firstExpr instanceof ExprIdentStar)
						|| ((ExprIdentStar) firstExpr).getIdentSymbolArray()
								.size() != 1 ) {
					error2(firstExpr.getFirstSymbol(),
							"An identifier was expected as the field name of a tuple."
									+ foundSuch());
				}
				namedTuple = true;
				next();
				exprList.add(firstExpr);
				Expr secondExpr = expr();
				exprList.add(secondExpr);
				while (symbol.token == Token.COMMA) {
					lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
					next();
					firstExpr = expr();
					exprList.add(firstExpr);
					if ( symbol.token != Token.ASSIGN ) {
						error2(symbol,
								"It was expected a field name, an identifier, followed by '=' and an expression."
										+ foundSuch());
					}
					next();
					if ( !(firstExpr instanceof ExprIdentStar)
							|| ((ExprIdentStar) firstExpr).getIdentSymbolArray()
									.size() != 1 ) {
						error2(firstExpr.getFirstSymbol(),
								"An identifier was expected as the field name of a tuple."
										+ foundSuch());
					}
					secondExpr = expr();
					exprList.add(secondExpr);
				}
			}
			else {
				namedTuple = false;
				exprList.add(firstExpr);
				while (symbol.token == Token.COMMA) {
					lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
					next();
					firstExpr = expr();
					exprList.add(firstExpr);
				}
			}

			if ( symbol.token != Token.RIGHTDOT_SB )
				error(true, symbol, "'.]' expected" + foundSuch(), null,
						ErrorKind.dot_square_backet_expected);
			endSymbol = symbol;
			next();
			ret = new ExprLiteralTuple(startSymbol, endSymbol, exprList,
					namedTuple, currentMethod);
			break;
		case LEFTCB:
			// found the declaration of a function
			ret = functionDec();
			break;
		case LITERALOBJECT:

			AnnotationLiteralObject annotation = null;
			if ( !compInstSet
					.contains(meta.CompilationInstruction.parsing_actions) ) {
				/*
				 * found a literal object in a compiler step that does not allow
				 * literal objects. See the Figure in Chapter "Metaobjects" of
				 * the Cyan manual
				 */
				this.error(true, symbol,
						"Literal object in a compiler step that does not allow literal objects",
						symbol.getSymbolString(),
						ErrorKind.parsing_compilation_phase_literal_objects_and_macros_are_not_allowed);
				ret = new ExprNonExpression(currentMethod);
			}
			else {
				CyanMetaobjectLiteralObject cyanMetaobject = null;
				if ( symbol instanceof SymbolLiteralObject ) {
					/*
					 * found a literal object that is parsed with a user-defined
					 * compiler. That is, the text inside the literal object is
					 * not parsed with the help of the Cyan compiler. For
					 * example, usually literal strings that start with a letter
					 * and user-defined literal numbers do not use the Cyan
					 * compiler. <code><br> var regexpr = r"0+[a-z]*"; <br> var
					 * five = 101bin;<br> </code>
					 */

					final SymbolLiteralObject symbolLiteralObject = (SymbolLiteralObject) symbol;
					annotation = symbolLiteralObject
							.getCyanMetaobjectLiteralObjectAnnotation();
					cyanMetaobject = annotation
							.getCyanMetaobjectLiteralObject();

					if ( cyanMetaobject instanceof ICodeg ) {
						this.otherCodegList.add((ICodeg) cyanMetaobject);
					}

					if ( localVariableDecStack != null
							&& localVariableDecStack.size() > 0 ) {
						final List<Tuple2<String, String>> localVariableNameList = getLocalVariableList();
						annotation.setLocalVariableNameList(
								localVariableNameList);
					}

					// //
					// cyanMetaobject.setAnnotation(annotation,
					// 0);
					annotation.setOriginalCode(
							symbolLiteralObject.getSymbolString());
					ret = annotation;

					// _CyanMetaobject other =
					// cyanMetaobject.getMetaobjectInCyan();
					// if ( !(cyanMetaobject instanceof
					// IParseWithoutCyanCompiler_parsing ||
					// (other != null && other instanceof
					// _IParseWithoutCyanCompiler__parsing) ) ) {
					// this.error(true, symbolLiteralObject,
					// "Internal error: the Cyan metaobject should implement
					// interface '" +
					// IParseWithoutCyanCompiler_parsing.class.getName() + "'",
					// symbolLiteralObject.getSymbolString(),
					// ErrorKind.internal_error);
					// ret = new ExprNonExpression();
					// }
					// else {
					//
					// final ICompilerAction_parsing compilerAction = new
					// CompilerAction_parsing(
					// this);
					//
					// try {
					// if ( other == null ) {
					// CyanPackage moPackage =
					// this.program.getProject().searchPackage(cyanMetaobject.getPackageName());
					// if ( moPackage != null ) {
					// CyanMetaobjectLiteralObject cyanMetaobjectFinal =
					// cyanMetaobject;
					// moPackage.addPackageMetaToClassPath_and_Run(
					// () -> {
					// ((IParseWithoutCyanCompiler_parsing) cyanMetaobjectFinal)
					// .parsing_parse(compilerAction,
					// symbolLiteralObject
					// .getUsefulString());
					//
					// }
					// );
					// }
					// else {
					// ((IParseWithoutCyanCompiler_parsing) cyanMetaobject)
					// .parsing_parse(compilerAction,
					// symbolLiteralObject
					// .getUsefulString());
					// }
					// }
					// else {
					// ((_IParseWithoutCyanCompiler__parsing) cyanMetaobject)
					// ._dpa__parse_2(compilerAction,
					// new CyString(symbolLiteralObject
					// .getUsefulString()));
					// }
					// }
					// catch (final error.CompileErrorException e) {
					// }
					// catch (final NoClassDefFoundError e) {
					// error2(annotation.getFirstSymbol(), e.getMessage()
					// + " "
					// + NameServer.messageClassNotFoundException);
					// }
					// catch (final RuntimeException e) {
					// thrownException(annotation,
					// annotation.getFirstSymbol(), e);
					// }
					// finally {
					// this.metaobjectError(cyanMetaobject, annotation);
					// }
					//
					// next();
					// }
					next();
				}
				else if ( symbol instanceof SymbolLiteralObjectParsedWithCompiler ) {
					/*
					 * represents a literal object such as <br> <code> {* 1:2,
					 * 2:3, 3:1 *} <br> [* "one":1, "two":2 *] <br> </code><br>
					 * that is parsed with the help of the Cyan compiler.
					 */
					final SymbolLiteralObjectParsedWithCompiler symbolLiteralObject = (SymbolLiteralObjectParsedWithCompiler) symbol;
					annotation = symbolLiteralObject
							.getCyanMetaobjectLiteralObjectAnnotation();
					cyanMetaobject = annotation
							.getCyanMetaobjectLiteralObject();
					// //
					// cyanMetaobject.setAnnotation(annotation,
					// 0);
					ret = annotation;

					if ( localVariableDecStack != null
							&& localVariableDecStack.size() > 0 ) {
						final List<Tuple2<String, String>> localVariableNameList = getLocalVariableList();
						annotation.setLocalVariableNameList(
								localVariableNameList);
					}

					_CyanMetaobject other = cyanMetaobject
							.getMetaobjectInCyan();
					if ( !(cyanMetaobject instanceof IParseWithCyanCompiler_parsing
							|| ((other != null
									&& other instanceof _IParseWithCyanCompiler__parsing))) ) {
						this.error(true, symbolLiteralObject,
								"Internal error: the Cyan metaobject should implement interface 'IParseWithCyanCompiler_parsing'",
								symbolLiteralObject.getSymbolString(),
								ErrorKind.internal_error);

					}
					else {
						next();
						final Compiler_parsing compiler_parsing = new Compiler_parsing(
								this, lexer,
								symbolLiteralObject.getSymbolString());
						/*
						 * the errors found in the compilation are introduced in
						 * object 'this'. They will be processed as regular
						 * compiler errors.
						 */

						try {

							int timeoutMilliseconds = this
									.getTimeoutMilliseconds(annotation);
							// Timeout.getTimeoutMilliseconds( this,
							// program,
							// this.compilationUnit.getCyanPackage(),
							// annotation.getFirstSymbol());
							Timeout<Object> to = new Timeout<>();
							final CyanMetaobjectLiteralObject cyanMetaobjectFinal = cyanMetaobject;

							if ( other == null ) {
								to.run(Executors.callable(() -> {
									((IParseWithCyanCompiler_parsing) cyanMetaobjectFinal)
											.parsing_parse(compiler_parsing);
								}), timeoutMilliseconds, "parsing_parse",
										cyanMetaobject, project);
							}
							else {
								to.run(Executors.callable(() -> {
									((_IParseWithCyanCompiler__parsing) other)
											._parsing__parse_1(
													compiler_parsing);
								}), timeoutMilliseconds, "parsing_parse",
										cyanMetaobject, project);

							}
						}
						catch (final error.CompileErrorException e) {
						}
						catch (final NoClassDefFoundError e) {
							error2(annotation.getFirstSymbol(), e.getMessage()
									+ " "
									+ NameServer.messageClassNotFoundException);
						}
						catch (final RuntimeException e) {
							thrownException(annotation,
									annotation.getFirstSymbol(), e);
						}
						finally {
							this.metaobjectError(cyanMetaobject, annotation);
							this.copyLexerData(compiler_parsing.compiler);
						}

						annotation.setExprStatList(
								compiler_parsing.getExprStatList());

						if ( symbol.token == Token.RIGHTCHAR_SEQUENCE ) {
							next();
						}
						else {
							if ( lexer.expectedRightSymbolSequence() == null ) {
								this.error(true, symbol,
										"Internal error in Compiler::annotation",
										null, ErrorKind.internal_error);
							}
							else
								error(true, symbol,
										"The right symbol sequence '" + lexer
												.expectedRightSymbolSequence()
												+ "' was expected."
												+ foundSuch(),
										null,
										ErrorKind.right_symbol_sequence_expected);
						}

						lexer.setNewLineAsToken(false);
					}
				}
				else {
					this.error(true, symbol, "Literal object '"
							+ symbol.getSymbolString() + "' has a Java class "
							+ " that does not implement either 'IParseWithoutCyanCompiler_parsing' or 'IParseWithCyanCompiler_parsing'",
							null, ErrorKind.metaobject_error);

					ret = new ExprNonExpression(currentMethod);
				}
				if ( annotation != null ) {
					annotation.setLastSymbol(previousSymbol);
					annotation.setNextSymbol(symbol);

					if ( currentPrototype != null ) {
						currentPrototype.addAnnotation(annotation);
						annotation.setCurrentPrototype(currentPrototype);
						annotation.setAnnotationNumber(
								currentPrototype.getIncAnnotationNumber());
					}
					annotation.setCompilationUnit(compilationUnit);

					_CyanMetaobject other = cyanMetaobject != null
							? cyanMetaobject.getMetaobjectInCyan()
							: null;

					if ( cyanMetaobject != null
							&& (cyanMetaobject instanceof IActionNewPrototypes_parsing
									|| (other != null
											&& other instanceof _IActionNewPrototypes__parsing))
							&& (this.currentPrototype == null
									|| !this.currentPrototype.isGeneric())
							&& this.compilationStep == CompilationStep.step_1 ) {
						// this.compInstSet.contains(CompilationInstruction.parsing_actions)
						// ) {
						final ICompilerAction_parsing compilerAction_dpa1 = new Compiler_parsing(
								this, lexer, null);

						List<Tuple2<String, StringBuffer>> prototypeNameCodeList = null;
						try {
							int timeoutMilliseconds = getTimeoutMilliseconds(
									annotation);

							if ( other == null ) {
								final IActionNewPrototypes_parsing actionNewPrototype = (IActionNewPrototypes_parsing) cyanMetaobject;

								Timeout<List<Tuple2<String, StringBuffer>>> to = new Timeout<>();
								prototypeNameCodeList = to.run(
										() -> actionNewPrototype
												.parsing_NewPrototypeList(
														compilerAction_dpa1),
										timeoutMilliseconds,
										"parsing_NewPrototypeList",
										cyanMetaobject, project);

								// prototypeNameCodeList = actionNewPrototype
								// .parsing_NewPrototypeList(compilerAction_dpa1);
							}
							else {
								_IActionNewPrototypes__parsing anp = (_IActionNewPrototypes__parsing) other;
								Timeout<_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT> to = new Timeout<>();
								_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array = to
										.run(() -> anp
												._parsing__NewPrototypeList_1(
														compilerAction_dpa1),
												timeoutMilliseconds,
												"parsing_NewPrototypeList",
												cyanMetaobject, project);

								// _Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT
								// array =
								// anp._parsing__NewPrototypeList_1(compilerAction_dpa1);
								int size = array._size().n;
								if ( size > 0 ) {
									prototypeNameCodeList = new ArrayList<>();
									for (int i = 0; i < size; ++i) {
										_Tuple_LT_GP_CyString_GP_CyString_GT tss = array
												._at_1(new CyInt(i));
										String f1 = tss._f1().s;
										String f2 = tss._f2().s;
										if ( f1.length() > 0 ) {
											prototypeNameCodeList.add(
													new Tuple2<String, StringBuffer>(
															f1,
															new StringBuffer(
																	f2)));
										}
									}
								}

							}
						}
						catch (final error.CompileErrorException e) {
						}
						catch (final NoClassDefFoundError e) {
							error2(annotation.getFirstSymbol(), e.getMessage()
									+ " "
									+ NameServer.messageClassNotFoundException);
						}
						catch (final RuntimeException e) {
							thrownException(annotation,
									annotation.getFirstSymbol(), e);
						}
						finally {
							metaobjectError(cyanMetaobject, annotation);
						}
						CyanPackage currentCyanPackage = this.compilationUnit
								.getCyanPackage();
						if ( prototypeNameCodeList != null ) {
							for (final Tuple2<String, StringBuffer> prototypeNameCode : prototypeNameCodeList) {
								String prototypeName = prototypeNameCode.f1;
								final Tuple2<CompilationUnit, String> t = this.project
										.getCompilerManager()
										.createNewPrototype(prototypeName,
												prototypeNameCode.f2,
												this.compilationUnit
														.getCompilerOptions(),
												currentCyanPackage);
								if ( t != null && t.f2 != null ) {
									this.error2(annotation.getFirstSymbol(),
											t.f2);
								}
								currentCyanPackage
										.addPrototypeNameAnnotationInfo(
												prototypeName, annotation);

							}
						}

					}
				}
			}
			break;
		default:
			if ( symbol.getSymbolString().equals("Dyn") ) {
				error2(symbol,
						"'Dyn' is a virtual type. It cannot be used in an expression. It can only be used as a type of variable/parameter/return value type of method");
			}
			else
				error2(symbol, "Expression expected." + foundSuch());
			ret = null;
		}
		return ret;
	}

	/**
	 * analyzes basic types literals that are arguments to metaobject
	 * annotations. That includes arrays of basic types and tuples.
	 *
	 * @return
	 */
	public ExprAnyLiteral exprBasicTypeLiteral() {
		final Tuple2<ExprAnyLiteral, Boolean> t = exprBasicTypeLiteral_Ident();
		return t.f1;
	}

	/**
	 * analyzes basic types literals that are arguments to metaobject
	 * annotations. That includes arrays of basic types and tuples. Return a
	 * tuple composed of the expression and a boolean value. If this boolean
	 * value is true then an identifier was used in the expression. Then
	 * expression "i" returns a tuple <code>[. e, true .]</code> using the Cyan
	 * syntax. Expression "0" returns <code>[. e, false .]</code> and
	 * <code>"[ 0, 1, i ]"</code> returns <code>[. e, true .]</code> <br>
	 * The type of an identifier is considered String. That includes the basic
	 * types such as Int. Then the type of 'Int' is String.
	 *
	 * @return
	 */

	private Tuple2<ExprAnyLiteral, Boolean> exprBasicTypeLiteral_Ident() {
		ExprAnyLiteral ret;
		boolean foundIdent = false;
		Tuple2<ExprAnyLiteral, Boolean> t;

		if ( isBasicType(symbol.token) ) {
			final Symbol s = symbol;
			next();
			ret = new ExprAnyLiteralIdent(new ExprIdentStar(currentMethod, s),
					currentMethod);
		}
		else {
			Symbol prefix = null;
			if ( symbol.token == Token.PLUS || symbol.token == Token.MINUS ) {
				prefix = symbol;
				next();
				if ( symbol.token != Token.BYTELITERAL
						&& symbol.token != Token.INTLITERAL
						&& symbol.token != Token.LONGLITERAL
						&& symbol.token != Token.FLOATLITERAL
						&& symbol.token != Token.DOUBLELITERAL ) {
					error2(symbol, "A number was expected after symbol '"
							+ prefix.getSymbolString() + "'");
				}
			}
			switch (symbol.token) {
			case IDENT:
				foundIdent = true;
				ret = new ExprAnyLiteralIdent(ident(), currentMethod);
				break;
			case IDENTCOLON:
				foundIdent = true;
				ret = new ExprAnyLiteralIdent(identColon(), currentMethod);
				break;
			case BYTELITERAL:
				ret = new ExprLiteralByte(symbol, prefix, currentMethod);
				next();
				break;
			case SHORTLITERAL:
				ret = new ExprLiteralShort(symbol, prefix, currentMethod);
				next();
				break;
			case INTLITERAL:
				ret = new ExprLiteralInt(symbol, prefix, currentMethod);
				next();
				break;
			case LONGLITERAL:
				ret = new ExprLiteralLong(symbol, prefix, currentMethod);
				next();
				break;
			case FLOATLITERAL:
				ret = new ExprLiteralFloat(symbol, prefix, currentMethod);
				next();
				break;
			case DOUBLELITERAL:
				ret = new ExprLiteralDouble(symbol, prefix, currentMethod);
				next();
				break;
			case CHARLITERAL:
				ret = new ExprLiteralChar(symbol, currentMethod);
				next();
				break;
			case FALSE:
				ret = new ExprLiteralBooleanFalse(symbol, currentMethod);
				next();
				break;
			case TRUE:
				ret = new ExprLiteralBooleanTrue(symbol, currentMethod);
				next();
				break;
			case LITERALSTRING:
				// this rule represents both strings of the kind "hello" or """
				// multiple lines """
				ExprLiteralString els;
				ret = els = new ExprLiteralString(symbol, currentMethod);
				if ( els.getVarNameList() != null ) {
					for (final String varName : els.getVarNameList()) {
						/**
						 * it is a parameter, local variable, field, unary
						 * method
						 */
						final VariableDecInterface localVarParameter = searchIdent(
								varName);

						if ( localVarParameter != null ) {

							addAccessedLocalVariableToFunctions(
									els.getFirstSymbol(), localVarParameter);
						}
					}
				}
				next();
				break;
			case NIL:
				ret = new ExprLiteralNil(symbol, currentMethod);
				next();
				break;

			case LEFTSB:

				lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol, "[");
				Symbol startSymbol, endSymbol;
				startSymbol = symbol;
				List<Expr> exprList = new ArrayList<Expr>();
				next();
				boolean foundLiteralArray = true;
				if ( startExpr(symbol) ) {

					t = exprBasicTypeLiteral_Ident();
					if ( t.f2 ) foundIdent = true;
					exprList.add(t.f1);

					if ( symbol.token == Token.RETURN_ARROW ) {
						lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol,
								symbol.symbolString);
						// a literal map
						foundLiteralArray = false;
						next();

						t = exprBasicTypeLiteral_Ident();
						if ( t.f2 ) foundIdent = true;
						exprList.add(t.f1);

						// exprList.add(expr());
						while (symbol.token == Token.COMMA) {
							lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
							next();

							t = exprBasicTypeLiteral_Ident();
							if ( t.f2 ) foundIdent = true;
							exprList.add(t.f1);

							// exprList.add(expr());
							if ( symbol.token != Token.RETURN_ARROW ) {
								error2(symbol, "'->' was expected");
							}
							lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol,
									symbol.symbolString);

							next();

							t = exprBasicTypeLiteral_Ident();
							if ( t.f2 ) foundIdent = true;
							exprList.add(t.f1);
							// exprList.add(expr());
						}
					}
					else {
						// a literal array
						while (symbol.token == Token.COMMA) {
							lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
							next();
							t = exprBasicTypeLiteral_Ident();
							if ( t.f2 ) foundIdent = true;
							exprList.add(t.f1);
							// exprList.add(expr());
						}

					}
				}
				else {
					if ( symbol.token == Token.RIGHTSB )
						error(true, symbol, "Empty literal arrays are illegal",
								null, ErrorKind.empty_literal_array);
					else
						error(true, symbol,
								"Expression expected." + foundSuch(), null,
								ErrorKind.expression_expected_inside_method);
				}
				if ( symbol.token != Token.RIGHTSB ) {
					error2(symbol,
							"']' expected at the end of literal array or map"
									+ foundSuch());
				}
				lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol, "]");
				endSymbol = symbol;
				next();
				if ( foundLiteralArray ) {
					ret = new ExprLiteralArray(startSymbol, endSymbol, exprList,
							currentMethod);
				}
				else {
					ret = new ExprLiteralMap(startSymbol, endSymbol, exprList,
							currentMethod);
				}

				/*
				 * lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol, "[");
				 * Symbol startSymbol, endSymbol; startSymbol = symbol;
				 * List<Expr> exprList = new ArrayList<Expr>(); next(); if (
				 * startExpr(symbol) ) { t = exprBasicTypeLiteral_Ident(); if (
				 * t.f2 ) foundIdent = true; exprList.add(t.f1); while (
				 * symbol.token == Token.COMMA ) {
				 * lexer.checkWhiteSpaceParenthesisAfter(symbol, ","); next(); t
				 * = exprBasicTypeLiteral_Ident(); if ( t.f2 ) foundIdent =
				 * true; exprList.add(t.f1); } } else { if ( symbol.token ==
				 * Token.RIGHTSB ) error(true, symbol,
				 * "Empty literal arrays are illegal", null,
				 * ErrorKind.empty_literal_array); else error(true, symbol,
				 * "Expression expected." + foundSuch(), null,
				 * ErrorKind.expression_expected_inside_method); } if (
				 * symbol.token != Token.RIGHTSB ) { error2(symbol,
				 * "']' expected at the end of literal array." + foundSuch()); }
				 * lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol, "]");
				 * endSymbol = symbol; next(); ret = new
				 * ExprLiteralArray(startSymbol, endSymbol, exprList);
				 */
				break;
			case LEFTSB_DOT:
				startSymbol = symbol;
				next();

				exprList = new ArrayList<Expr>();

				Expr firstExpr;
				// boolean namedTuple = false;

				t = exprBasicTypeLiteral_Ident();
				if ( t.f2 ) foundIdent = true;
				firstExpr = t.f1;

				exprList.add(firstExpr);
				while (symbol.token == Token.COMMA) {
					lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
					next();
					t = exprBasicTypeLiteral_Ident();
					if ( t.f2 ) foundIdent = true;
					firstExpr = t.f1;
					exprList.add(firstExpr);
				}

				if ( symbol.token != Token.RIGHTDOT_SB ) {
					if ( symbol.token == Token.ASSIGN ) {
						error(true, symbol,
								"named tuples, with '=', are not allowed here. Use a non-named tuple instead",
								null, ErrorKind.dot_square_backet_expected);
					}
					else {
						error(true, symbol, "'.]' expected." + foundSuch(),
								null, ErrorKind.dot_square_backet_expected);
					}
				}
				endSymbol = symbol;
				next();
				ret = new ExprLiteralTuple(startSymbol, endSymbol, exprList,
						false, currentMethod);
				break;

			default:
				if ( Character.isAlphabetic(symbol.toString().charAt(0)) ) {
					final Symbol s = symbol;
					next();
					ret = new ExprAnyLiteralIdent(
							new ExprIdentStar(currentMethod, s), currentMethod);
				}
				else {
					error2(symbol,
							"A literal expression of basic types was expected."
									+ foundSuch());
					ret = null;
				}
			}

		}

		return new Tuple2<ExprAnyLiteral, Boolean>(ret, foundIdent);
	}

	public Expr functionDec() {

		final Symbol startSymbol = symbol;
		final Expr selfType = null;
		next();

		ExprFunction aFunctionDec;
		final int numberOfParameters = parameterDecStack.size();

		if ( symbol.token == Token.LEFTPARCOLON ) {
			lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol,
					symbol.symbolString);
			next();
			aFunctionDec = functionSignature(startSymbol);
			if ( symbol.token != Token.COLONRIGHTPAR ) {
				error2(symbol, "':)' expected after function signature."
						+ foundSuch());
			}
			next();
		}
		else
			aFunctionDec = new ExprFunctionRegular(startSymbol, currentMethod);

		aFunctionDec.setFunctionLevel(functionStack.size());

		if ( aFunctionDec instanceof ExprFunctionRegular ) {
			final ExprFunctionRegular ef = (ExprFunctionRegular) aFunctionDec;
			final List<ParameterDec> parameterList = ef.getParameterList();
			if ( parameterList != null && parameterList.size() > 0
					&& (parameterList.get(0) == null
							|| parameterList.get(0).getName() == null) ) {
				this.error2(symbol, "Internal error in functionDec");
			}

		}

		aFunctionDec.setFunctionPrototypeName((aFunctionDec.isContextFunction()
				? NameServer.contextFunctionProtoName
				: NameServer.functionProtoName) + functionCounter++
				+ NameServer.endsInnerProtoName);

		if ( currentPrototype != null ) {
			aFunctionDec.setNumber(currentPrototype.getNextFunctionNumber());
			currentPrototype.addNextFunctionNumber();

			((ObjectDec) currentPrototype).addToBeCreatedFunction(aFunctionDec);
		}
		else {
			// parsing statements to be interpreted
			aFunctionDec.setNumber(this.getNextFunctionNumber());
		}

		functionStack.push(aFunctionDec);
		final int numberOfLocalVariables = this.localVariableDecStack.size();

		final StatementList statementList = statementList();
		if ( symbol.token != Token.RIGHTCB ) error2(symbol,
				"'}' expected at the end of a function." + foundSuch());
		aFunctionDec.setEndSymbol(symbol);

		/*
		 * if ( (symbol.getColumnNumber() ==
		 * this.startSymbolCurrentStatement.getColumnNumber() ) ) { /* insert a
		 * ';' / symbol = new SymbolOperator(Token.SEMICOLON, ";",
		 * symbol.getStartLine(), symbol.getLineNumber(),
		 * symbol.getColumnNumber() + 1, symbol.getOffset());
		 *
		 * } else { }
		 */
		next();

		functionStack.pop();

		while (localVariableDecStack.size() > numberOfLocalVariables)
			localVariableDecStack.pop();

		while (parameterDecStack.size() > numberOfParameters)
			parameterDecStack.pop();

		aFunctionDec.setStatementList(statementList);
		aFunctionDec.setSelfType(selfType);
		aFunctionDec.setCurrentMethod(currentMethod);
		aFunctionDec.setCurrentPrototype(currentPrototype);
		return aFunctionDec;
	}

	/**
	 * CloSignature ::= \{ Type Id \} \{ ``,"\/ Type Id \} [ ``\verb@->@"\/ Type
	 * ] \verb"|" \{ IdColon \{ Type Id \} \{ ``,"\/ Type Id \} \} \} [
	 * ``\verb@->@"\/ Type ]
	 *
	 * @param startSymbol
	 * @return
	 */

	private ExprFunction functionSignature(Symbol startSymbol) {

		ExprFunction exprFunction;
		final List<MethodKeywordWithParameters> keywordWithParametersList = new ArrayList<MethodKeywordWithParameters>();
		if ( symbol.token == Token.IDENTCOLON ) {
			while (symbol.token == Token.IDENTCOLON) {
				// an unusual function that has keywords such as
				// var b = { (: eval: (Int f1) eval: (Int y) -> Int :) ^f1*y };
				// k = b eval: 10 eval: 5;

				if ( symbol.getSymbolString().compareTo("eval:") != 0 )
					error2(symbol, "'eval:' expected." + foundSuch());
				final MethodKeywordWithParameters keywordWithParameters = new MethodKeywordWithParameters(
						symbol);
				next();
				parameterDecListFunction(
						keywordWithParameters.getParameterList());
				keywordWithParametersList.add(keywordWithParameters);
			}
			for (final MethodKeywordWithParameters evalkeyword : keywordWithParametersList) {
				if ( evalkeyword.getName().compareTo("eval:") != 0 ) error2(
						evalkeyword.getkeyword(),
						"Function with multiple keywords may only have 'eval:' keywords");
			}
			if ( keywordWithParametersList.size() == 1
					&& keywordWithParametersList.get(0).getParameterList()
							.size() == 0 ) {
				this.error2(keywordWithParametersList.get(0).getkeyword(),
						"Illegal anonymous function: a single keyword 'even:' without parameters");
			}
			exprFunction = new ExprFunctionWithKeywords(startSymbol,
					keywordWithParametersList, currentMethod);

		}
		else {
			final ExprFunctionRegular aFunctionDec = new ExprFunctionRegular(
					startSymbol, currentMethod);
			if ( symbol.token != Token.RETURN_ARROW ) {
				parameterDecListFunction(aFunctionDec.getParameterList());
			}
			lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol,
					symbol.symbolString);

			exprFunction = aFunctionDec;
			if ( exprFunction.isContextFunction() ) {
				error2(aFunctionDec.getParameterList().get(0).getFirstSymbol(),
						"Context function are not supported in this version of the compiler");
			}
		}
		if ( symbol.token == Token.RETURN_ARROW ) {
			lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol,
					symbol.symbolString);
			next();

			exprFunction.setReturnTypeExpr(type());
		}
		return exprFunction;
	}

	/**
	 * analyzes a non-unary message, therefore without the receiver. In circle
	 * f1: 10 y: 30 r: 5 this method would be called to analyze "f1: 10 y: 30 r:
	 * 5"
	 *
	 * @return
	 */
	private MessageWithKeywords messageSendNonUnary() {

		List<MessageKeywordWithRealParameters> keywordParameterList;
		Symbol keyword;
		MessageKeywordWithRealParameters keywordWithRealParameters;

		if ( symbol.token == Token.IDENTCOLON
				|| symbol.token == Token.INTER_ID_COLON
				|| symbol.token == Token.INTER_DOT_ID_COLON
				|| symbol.token == Token.BACKQUOTE ) {

			keywordParameterList = new ArrayList<MessageKeywordWithRealParameters>();
			Symbol firstkeyword = null;
			while (symbol.token == Token.IDENTCOLON
					|| symbol.token == Token.INTER_ID_COLON
					|| symbol.token == Token.INTER_DOT_ID_COLON
					|| symbol.token == Token.BACKQUOTE) {
				boolean backquote = false;
				Symbol backquoteSymbol = null;
				if ( symbol.token == Token.BACKQUOTE ) {
					backquoteSymbol = symbol;
					backquote = true;
					next();

					if ( symbol.token == Token.IDENTCOLON ) {

						/**
						 * each function should have a list of accessed local
						 * variables and parameters of outer scope.
						 */
						/**
						 * it is a parameter, local variable, field, unary
						 * method
						 */
						String varName = symbol.getSymbolString();
						varName = varName.substring(0, varName.length() - 1);
						final Object localVarParameter = searchIdent(varName);

						if ( localVarParameter != null ) {
							int i = functionStack.size() - 1;
							// Iterator<ExprFunction> functionIter =
							// functionStack.iterator();
							while (i >= 0) {
								final ExprFunction function = functionStack
										.get(i);
								--i;
								if ( compilationStep == CompilationStep.step_7
										&& function.searchLocalVariableDec(
												varName) == null ) {

									if ( localVarParameter instanceof ParameterDec ) {
										function.addAccessedParameter(
												(ParameterDec) localVarParameter);
										((ParameterDec) localVarParameter)
												.addInnerObjectNumberList(
														function.getNumber());
									}
									else if ( localVarParameter instanceof StatementLocalVariableDec ) {
										function.addAccessedVariableDec(
												(StatementLocalVariableDec) localVarParameter);
										((StatementLocalVariableDec) localVarParameter)
												.addInnerObjectNumberList(
														function.getNumber());
									}
								}
								else
									/*
									 * if the variable was found, it is not
									 * necessary to consider it as an external
									 * variable for this and all upper level
									 * functions
									 */
									break;
							}

						}
					}

					if ( symbol.token != Token.IDENTCOLON ) error2(symbol,
							"A message keyword like 'add:' was expected after '`' (hasBackquote)."
									+ foundSuch());
				}
				lastkeyword = keyword = symbol;
				if ( firstkeyword == null ) firstkeyword = symbol;
				if ( symbol.token != firstkeyword.token ) error2(symbol,
						"mixing of different types of keywords in the same message (regular, dynamic call, Nil-safe)");
				next();
				final List<Expr> exprList = realParameters();
				keywordWithRealParameters = new MessageKeywordWithRealParameters(
						keyword, backquoteSymbol, backquote, exprList);
				keywordParameterList.add(keywordWithRealParameters);
			}
			return new MessageWithKeywords(keywordParameterList);
		}
		else {
			error2(symbol, "message send expected." + foundSuch());
			return null;
		}

	}

	private Symbol lastkeyword;

	/**
	 * return true if the symbol s can start
	 *
	 * @return
	 */
	private static boolean canStartMessageSendNonUnary(Symbol s) {
		return s.token == Token.IDENTCOLON || s.token == Token.INTER_ID_COLON
				|| s.token == Token.INTER_DOT_ID_COLON;
		// it was || isBinaryOperator(s.token)
	}

	private List<Expr> realParameters() {

		final List<Expr> exprList = new ArrayList<Expr>();
		if ( startExpr(symbol) ) {
			Expr innerExpr = exprOrGt();
			exprList.add(innerExpr);
			innerExpr.setNextSymbol(symbol);

			while (symbol.token == Token.COMMA) {
				lexer.checkWhiteSpaceParenthesisAfter(symbol, ",");
				next();
				if ( symbol.token == Token.IDENTCOLON
						|| symbol.token == Token.INTER_DOT_ID_COLON
						|| symbol.token == Token.INTER_ID_COLON ) {
					error2(symbol,
							"This message send is being passed as parameter to keyword '"
									+ lastkeyword.getSymbolString()
									+ "'. It should be put between parentheses");
				}
				innerExpr = exprOrGt();
				exprList.add(innerExpr);
				innerExpr.setNextSymbol(symbol);
			}
		}
		return exprList;
	}

	public MethodSignature methodSignature() {
		return this.methodSignature(false);
	}

	public MethodSignature methodSignature(boolean finalKeyword) {
		return methodSignature(finalKeyword, false);
	}

	/**
	 * parser a method signature. 'finalKeyword' is true if 'final' starts the
	 * method signature. 'abstractKeyword' is true is 'abstract' stars the
	 * method signature
	 *
	 * @return
	 */
	public MethodSignature methodSignature(boolean finalKeyword,
			boolean abstractKeyword) {

		MethodSignature methodSignature = null;
		List<MethodKeywordWithParameters> keywordList;
		boolean indexingMethod = false;
		Expr returnType = null;

		/*
		 * if ( startType(symbol.token) ) { returnType = type(); }
		 */

		if ( symbol.token == Token.LEFTRIGHTSB ) {
			indexingMethod = true;
			next();
		}
		final Symbol identSymbol = symbol;

		/*
		 * if ( symbol.token != Token.IDENT && symbol.token != Token.IDENTCOLON
		 * && symbol.token != Token.LEFTPAR && ! isOperator(symbol.token) ) { if
		 * ( returnType != null ) { boolean anError = false; if ( ! (returnType
		 * instanceof ExprIdentStar) ) { anError = true; } else { ExprIdentStar
		 * idstar = (ExprIdentStar ) returnType; if (
		 * idstar.getIdentSymbolArray().size() != 1 ) anError = true; } if (
		 * anError ) error(identSymbol, "Identifier expected as method name",
		 * "", ErrorKind.identifier_expected_inside_method); } returnType =
		 * null; methodSignature = new MethodSignatureUnary(identSymbol,
		 * currentMethod); refactorChangeId_to_IdDot(identSymbol); } else { }
		 */
		boolean isInit = false;
		boolean isInitShared = false;
		String name = "";

		switch (symbol.token) {
		case IDENT:
			name = symbol.symbolString;

			final CyanMetaobjectMacro cyanMacro = metaObjectMacroTable
					.get(name);
			if ( cyanMacro != null ) {
				this.error2(symbol,
						"This unary method has the name of a macro keyword of a macro imported by this compilation unit");
			}

			methodSignature = new MethodSignatureUnary(symbol, currentMethod);

			if ( indexingMethod ) error2(symbol,
					"unary methods cannot be indexing methods (declared with '[]')");

			if ( name.equals("new") && !this.parsingPackageInterfaces ) {
				if ( cyanMetaobjectContextStack.empty()
						&& !this.currentPrototype.isInnerPrototype() ) {
					this.error2(symbol, "'new' cannot be user-declared");
				}
			}
			if ( name.equals("initShared") ) isInitShared = true;

			next();
			refactorChangeId_to_IdDot(identSymbol);

			break;

		case IDENTCOLON:
			keywordList = new ArrayList<MethodKeywordWithParameters>();
			while (symbol.token == Token.IDENTCOLON) {
				final MethodKeywordWithParameters keywordWithParameters = new MethodKeywordWithParameters(
						symbol);
				// if ( name.length() > 0 )
				name = name + symbol.symbolString;
				next();

				/*
				 * if ( indexingMethod && !
				 * keywordWithParameters.getName().equals("at:") )
				 * this.error(symbol,
				 * "'[]' can only be used with keyword 'at:'",
				 * keywordWithParameters.getName(),
				 * ErrorKind.method_cannot_be_indexing_method);
				 */
				// this if is necessary because it is legal something like
				// public func file: open: (String name) [ ... ]

				if ( symbol.token == Token.LEFTPAR
						|| startType(symbol.token) ) {
					parameterDecList(keywordWithParameters.getParameterList());
				}
				else if ( symbol.token == Token.BOOLEAN_LOWER
						|| symbol.token == Token.BYTE_LOWER
						|| symbol.token == Token.CHAR_LOWER
						|| symbol.token == Token.DOUBLE_LOWER
						|| symbol.token == Token.FLOAT_LOWER
						|| symbol.token == Token.INT_LOWER
						|| symbol.token == Token.SHORT_LOWER
						|| symbol.token == Token.SHORT_LOWER ) {
					error2(symbol,
							"Lowercase type names are not allowed. Use types whose names start with upper case");
				}
				keywordList.add(keywordWithParameters);

			}
			methodSignature = new MethodSignatureWithKeywords(keywordList,
					indexingMethod, currentMethod);
			if ( keywordList.size() == 1 ) {
				if ( keywordList.get(0).getName().equals("new:")
						&& !this.parsingPackageInterfaces ) {
					if ( cyanMetaobjectContextStack.empty()
							&& !this.currentPrototype.isInnerPrototype() ) {
						this.error2(symbol, "'new:' cannot be user-declared");
					}
				}
				if ( name.equals("init:") ) {
					if ( keywordList.get(0).getParameterList() == null
							|| keywordList.get(0).getParameterList()
									.size() == 0 ) {
						error2(keywordList.get(0).getkeyword(),
								"'init:' methods should take parameters");
					}
				}
			}
			else {
				/*
				 * check if any keyword is 'init:' or 'new:'
				 */
				for (final MethodKeywordWithParameters sel : keywordList) {
					if ( sel.getName().equals("init:")
							|| sel.getName().equals("new:") )
						error2(sel.getkeyword(),
								"It is illegal to have a keyword with name 'init:' or 'new:'");
				}
			}

			/*
			 * String methodName = methodSignature.getNameWithoutParamNumber();
			 * //.getMethodSignatureWithParametersAsString(); if (
			 * methodName.compareTo("init:") == 0 ) { if (
			 * currentMethod.getHasOverride() ) error(true, idSymbol,
			 * "'init:' methods cannot be preceeded by 'override'", "init:",
			 * ErrorKind.init_should_not_be_declared_with_override); if (
			 * currentMethod.isAbstract() ) error(true, idSymbol,
			 * "'init:' methods cannot be abstract", "init:",
			 * ErrorKind.init_should_not_be_abstract); if ( indexingMethod )
			 * error(true, idSymbol, "'init:' methods cannot preceded by '[]'",
			 * "init:",
			 * ErrorKind.init_new_cannot_be_preceded_by_indexing_operator); if (
			 * currentMethod.getVisibility() != Token.PUBLIC ) error(true,
			 * idSymbol, "'init:' methods should be public", "init:",
			 * ErrorKind.init_new_should_be_public); isInit = true; } else if (
			 * methodName.compareTo("new:") == 0 ) { if (
			 * currentMethod.getHasOverride() ) error(true, idSymbol,
			 * "'new:' methods cannot be preceeded by 'override'", "new:",
			 * ErrorKind.new_cannot_be_declared_with_override); if (
			 * currentMethod.isAbstract() ) error(true, idSymbol,
			 * "'new:' methods cannot be abstract", "new:",
			 * ErrorKind.new_cannot_be_abstract); if ( indexingMethod )
			 * error(true, idSymbol, "'new:' methods cannot preceded by '[]'",
			 * "new:",
			 * ErrorKind.init_new_cannot_be_preceded_by_indexing_operator); if (
			 * currentMethod.getVisibility() != Token.PUBLIC ) error(true,
			 * idSymbol, "'new:' methods should be public", "new:",
			 * ErrorKind.new_methods_should_be_public); }
			 */

			final MethodSignatureWithKeywords mss = (MethodSignatureWithKeywords) methodSignature;
			if ( mss.getKeywordArray().size() == 1 && mss.getKeywordArray()
					.get(0).getParameterList().size() == 0 ) {
				// one keyword, no parameters. Illegal
				this.error2(mss.getKeywordArray().get(0).getkeyword(),
						"Illegal method name: a single keyword without parameters");
			}

			break;
		default:
			// should be an operator
			if ( isOperator(symbol.token) ) {
				if ( symbol.token == Token.AND || symbol.token == Token.OR ) {
					this.error2(symbol,
							"'&&' and '||' cannot be method names. They can only be used with Boolean values");
					return null;
				}
				if ( this.currentPrototype != null
						&& isArithmeticalOperator(symbol.token) ) {
					this.currentPrototype.setHasOperatorMethod(true);
				}
				final Symbol operatorSymbol = symbol;
				final MethodSignatureOperator mso = new MethodSignatureOperator(
						symbol, currentMethod);
				methodSignature = mso;
				next();
				if ( startType(symbol.token) || symbol.token == Token.LEFTPAR
						|| symbol.token == Token.IDENT ) { // this last is not
															// really necessary
															// but ...

					boolean leftpar = false;
					if ( symbol.token == Token.LEFTPAR ) {
						leftpar = true;
						next();
					}
					SymbolIdent parameterSymbol;
					Expr typeInDec = type();

					if ( symbol.token != Token.IDENT ) {

						if ( typeInDec instanceof ExprIdentStar ) {
							final ExprIdentStar eisType = (ExprIdentStar) typeInDec;
							if ( eisType.getIdentSymbolArray().size() > 1 ) {
								// found a prototype preceded by a package
								parameterSymbol = null;
							}
							else if ( Character.isUpperCase(
									eisType.getIdentSymbolArray().get(0)
											.getSymbolString().charAt(0)) ) {
								/*
								 * typeInDec is a type really
								 */
								parameterSymbol = null;
							}
							else {
								/*
								 * no package, starts with a lower case letter.
								 * It must be a parameter
								 */
								parameterSymbol = (SymbolIdent) eisType
										.getIdentSymbolArray().get(0);
								typeInDec = null;
							}

						}
						else {
							// typeInDec may be a generic prototype
							// instantiation
							parameterSymbol = null;
						}
					}
					else {
						parameterSymbol = (SymbolIdent) symbol;

						if ( Character.isUpperCase(
								parameterSymbol.getSymbolString().charAt(0)) ) {
							this.error2(parameterSymbol,
									"Variables and parameters cannot start with an uppercase letter");
							return null;
						}

						next();
					}
					if ( leftpar ) {
						if ( symbol.token != Token.RIGHTPAR ) {
							error2(symbol, "')' expected." + foundSuch());
							return null;
						}
						else {
							if ( Lexer.hasIdentNumberAfter(symbol,
									compilationUnitSuper) ) {
								error2(symbol,
										"letter, number, or '_' after ')'");
								return null;
							}
							next();
						}
					}
					final ParameterDec parameterDec = new ParameterDec(
							parameterSymbol, typeInDec, currentMethod);
					if ( this.functionStack.size() > 0 )
						parameterDec.setDeclaringFunction(functionStack.peek());

					mso.setOptionalParameter(parameterDec);
					parameterDecStack.push(parameterDec);
					if ( operatorSymbol.token == Token.BITNOT ) {
						this.error2(operatorSymbol,
								"This operator cannot be used as a binary method. It should not take a parameter");
						return null;
					}
				}
				else {
					// without parameters: then it should be a unary operator
					if ( !Compiler.isUnaryOperator(operatorSymbol.token) ) {
						this.error2(operatorSymbol,
								"This operator cannot be used as a unary method. It should take a parameter");
						return null;
					}
				}
			}
			else {
				this.error2(symbol,
						"A method name was expected. " + this.foundSuch());
				return null;
			}
		}

		if ( symbol.token == Token.RETURN_ARROW ) {
			lexer.checkWhiteSpaceParenthesisBeforeAfter(symbol,
					symbol.symbolString);

			next();

			returnType = type();

			if ( (isInit || isInitShared) && returnType != null ) {
				error(true, returnType.getFirstSymbol(),
						"'init:', 'init', and 'initShared' methods cannot have a return value",
						identSymbol.getSymbolString(),
						ErrorKind.init_should_return_Nil);
			}
		}

		methodSignature.setReturnTypeExpr(returnType);
		methodSignature.setFinalKeyword(finalKeyword);
		methodSignature.setAbstractKeyword(abstractKeyword);
		return methodSignature;
	}

	private static boolean isArithmeticalOperator(Token t) {

		return t == Token.MINUS || t == Token.MULT || t == Token.DIV
				|| t == Token.LEFTSHIFT || t == Token.PLUS
				|| t == Token.REMAINDER || t == Token.RIGHTSHIFT
				|| t == Token.RIGHTSHIFTTHREE;
	}

	private void refactorChangeId_to_IdDot(Symbol identSymbol) {
		if ( symbol.token == Token.COLON ) {
			warning(identSymbol, identSymbol.getLineNumber(),
					"':' unexpected right after '"
							+ identSymbol.getSymbolString() + "'");
			if ( ask(symbol, "Should I glue ':' right after "
					+ identSymbol.getSymbolString() + " ? (y, n)") ) {
				final int sizeIdentSymbol = identSymbol.getSymbolString()
						.length();
				compilationUnit.addAction(new ActionDelete(compilationUnit,
						symbol.startOffsetLine + symbol.getColumnNumber() - 1,
						1, symbol.getLineNumber(),
						symbol.getColumnNumber() + 1));
				compilationUnit.addAction(new ActionInsert(":", compilationUnit,
						identSymbol.startOffsetLine
								+ identSymbol.getColumnNumber() - 1
								+ sizeIdentSymbol,
						identSymbol.getLineNumber(),
						identSymbol.getColumnNumber() + sizeIdentSymbol));

			}

		}
	}

	/**
	 * searchs the list of local variables and parameters for identString.
	 * Returns the local variable or parameter found
	 */
	private VariableDecInterface searchIdent(String identString) {
		for (final StatementLocalVariableDec var : this.localVariableDecStack) {
			if ( var.getName().compareTo(identString) == 0 ) return var;
		}
		for (final ParameterDec p : parameterDecStack) {
			// if ( p.getName() == null || identString == null )
			// System.exit(1);
			if ( p.getName() != null
					&& p.getName().compareTo(identString) == 0 )
				return p;
		}
		return null;
	}

	/**
	 * parameters, local variables, and fields cannot have name equal to one of
	 * the formal parameters of a generic prototype. Then it is illegal to
	 * write: object Wrong<:T> private let Int T private String T ... end
	 *
	 * If the current prototype is generic, this method takes an identifier and
	 * checks whether it is equal to one of the formal parameters. It returns
	 * true if it is and false otherwise
	 */

	public boolean equalToFormalGenericParameter(String ident) {
		if ( currentPrototype != null && this.currentPrototype.isGeneric() ) {
			for (final List<GenericParameter> genericParameterList : currentPrototype
					.getGenericParameterListList()) {
				for (final GenericParameter genericParameter : genericParameterList) {
					if ( ident.compareTo(genericParameter.getName()) == 0 )
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * returns true if token t can start a unary method or an operator method.
	 * Example of uses of unary methods: f1 get f1 # get
	 */
	public static boolean startUnaryOperatorMethodName(Token t) {
		return t == Token.IDENT;
	}

	/**
	 * return true if token t can start a method name
	 *
	 * @param t
	 * @return
	 */
	public static boolean startMethodName(Token t) {
		return t == Token.IDENT || t == Token.IDENTCOLON || isOperator(t);

	}

	/**
	 * return true if 'e' is a type, which should start with an upper-case
	 * letter without any dots as "Int", "Program" or it should be a package
	 * name followed by a prototype name as "main.Program", "cyan.lang.Int"
	 */
	static public boolean isType(Expr e) {

		if ( e instanceof ExprIdentStar ) {
			final ExprIdentStar eis = (ExprIdentStar) e;
			final String firstName = eis.getIdentSymbolArray().get(0)
					.getSymbolString();
			return eis.getIdentSymbolArray().size() > 1
					|| Character.isUpperCase(firstName.charAt(0));
		}
		else
			return true;
	}

	/**
	 * returns true if token t can be a keyword of a message send such as get
	 * #get put: print: #print: + <<
	 */
	public static boolean canStartMessagekeyword(Token t) {
		return t == Token.IDENT || t == Token.IDENTCOLON
				|| t == Token.INTER_DOT_ID || t == Token.INTER_DOT_ID_COLON;
	}

	public static boolean isOperator(Token t) {

		return t == Token.AND || t == Token.BITAND || t == Token.BITNOT
				|| t == Token.BITOR3 || t == Token.BITXOR || t == Token.DIV
				|| t == Token.DOT_PLUS || t == Token.DOT_STAR || t == Token.EQ
				|| t == Token.GE || t == Token.EQEQEQ || t == Token.GT
				|| t == Token.LE || t == Token.LEG || t == Token.LEFTSHIFT
				|| t == Token.LT || t == Token.MINUS || t == Token.MULT
				|| t == Token.NEQ || t == Token.NEQEQ || t == Token.NOT
				|| t == Token.OR || t == Token.PLUS || t == Token.REMAINDER
				|| t == Token.RIGHTSHIFT || t == Token.RIGHTSHIFTTHREE
				|| t == Token.TWOPERIOD || t == Token.TWOPERIODLT
				|| t == Token.XOR || t == Token.TILDE_EQUAL || t == Token.EQGT
				|| t == Token.EQEQGT || t == Token.PLUSPLUS
				|| t == Token.MINUSMINUS || t == Token.ORGT;

	}

	/*
	 * == <= < > >= != === <=> ~=
	 */
	public static boolean isRelationalOperator(Token t) {
		return t == Token.EQ || t == Token.GE || t == Token.GT || t == Token.LE
				|| t == Token.LT || t == Token.NEQ || t == Token.LEG
				|| t == Token.EQEQEQ || t == Token.TILDE_EQUAL;
	}

	public static boolean isUnaryOperator(Token t) {
		return t == Token.PLUS || t == Token.MINUS || t == Token.NOT
				|| t == Token.BITNOT;
	}

	public static boolean isBinaryOperator(Token t) {
		return t == Token.AND || t == Token.BITAND || t == Token.BITOR3
				|| t == Token.BITXOR || t == Token.DIV || t == Token.EQ
				|| t == Token.GE || t == Token.GT || t == Token.LE
				|| t == Token.LEFTSHIFT || t == Token.LT || t == Token.MINUS
				|| t == Token.MULT || t == Token.NEQ || t == Token.OR
				|| t == Token.PLUS || t == Token.REMAINDER
				|| t == Token.RIGHTSHIFT || t == Token.RIGHTSHIFTTHREE
				|| t == Token.TWOPERIOD || t == Token.XOR;

	}

	public static boolean startPrototype(Token t) {
		return t == Token.METAOBJECT_ANNOTATION || t == Token.PUBLIC
				|| t == Token.PRIVATE || t == Token.PROTECTED
				|| t == Token.MIXIN || t == Token.OBJECT
				|| t == Token.INTERFACE;
	}

	public static boolean startExpr(Symbol sym) {

		final Token t = sym.token;
		return t == Token.SUPER || t == Token.SELF || t == Token.IDENT
				|| t == Token.LITERALSTRING || t == Token.STRING
				|| t == Token.LEFTCB || t == Token.LEFTPAR
				|| t == Token.LEFTSB_DOT ||
				// t == Token.CYANSYMBOL ||
				t == Token.PLUS || t == Token.MINUS || t == Token.NOT
				|| t == Token.BITNOT || t == Token.LEFTSB || startType(t)
				|| isBasicTypeLiteral(t) || t == Token.LITERALOBJECT
				|| t == Token.METAOBJECT_ANNOTATION;
		// (t == Token.LITERALOBJECT &&
		// Lexer.checkLeftCharSeq(sym.getSymbolString()));
	}

	public static boolean isBasicTypeLiteral(Token t) {

		return t == Token.BYTELITERAL || t == Token.SHORTLITERAL
				|| t == Token.INTLITERAL || t == Token.LONGLITERAL
				|| t == Token.FLOATLITERAL || t == Token.DOUBLELITERAL
				|| t == Token.CHARLITERAL || t == Token.TRUE || t == Token.FALSE
				|| t == Token.NIL || t == Token.LEFTSB;
	}

	public static boolean startType(Token t) {
		return t == Token.IDENT || t == Token.TYPEOF || t == Token.STRING
				|| t == Token.DYN || isBasicType(t);

	}

	public static boolean isBasicType(Token t) {
		return t == Token.BYTE || t == Token.SHORT || t == Token.INT
				|| t == Token.LONG || t == Token.FLOAT || t == Token.DOUBLE
				|| t == Token.CHAR || t == Token.BOOLEAN || t == Token.NIL
				|| t == Token.STRING;
	}

	/**
	 * skip tokens till symbol.token is one of the tokens of array tokenArray
	 *
	 * @param tokenArray
	 * @return
	 */
	private boolean skipTo(Token... tokenArray) {
		int i;
		while (symbol.token != Token.EOF) {
			i = 0;
			while (i < tokenArray.length) {
				if ( symbol.token == tokenArray[i++] ) return true;
			}
			next();
		}
		return false;

	}

	@SuppressWarnings({ "static-method", "unused" })
	private boolean ask(Symbol sym, String message) {
		return false;
		/*
		 * boolean ret = false; String yesNo; try { Scanner sc = new
		 * Scanner(System.in); //System.out.println("File " +
		 * compilationUnit.getFilename() + " (" + sym.getLineNumber() + ", " +
		 * sym.getColumnNumber() + ")"); //System.out.println(message); while (
		 * true ) { yesNo = null; while ( yesNo == null ) { try { yesNo =
		 * sc.nextInt() == 'y' ? "y" : "n"; } catch ( NoSuchElementException e)
		 * { }
		 *
		 * } if ( yesNo.compareTo("y") == 0 || yesNo.compareTo("n") == 0 )
		 * break; else //System.out.println("type y or n"); } ret =
		 * yesNo.compareTo("y") == 0; sc.close(); return ret; } catch (Exception
		 * e) { return false; }
		 */
	}

	public void setCheckSpaceAfterComma(boolean checkSpaceAfterComma) {
		lexer.setCheckSpaceAfterComma(checkSpaceAfterComma);
	}

	/**
	 * the context of the code generated by a metaobject annotation. The
	 * elements of each tuple are: an identifier, the metaobject name, the
	 * package name of the metaobject annotation or the project name (.pyan
	 * file), the prototype name of the metaobject annotation, and the line
	 * number of the annotation. If this stack is not empty and there is a
	 * compilation error, then the code that caused the error was introduced by
	 * a metaobject annotation. This code was generated by a metaobject
	 * annotation in the prototype specified in the tuple. That is, the
	 * statement
	 * </p>
	 * <code>cyanMetaobjectContextStack.push(new Tuple5<...>(...))</code>
	 * </p>
	 * is called before the compilation of the code generated by the metaobject
	 * and
	 * </p>
	 * <code>cyanMetaobjectContextStack.pop()</code>
	 * </p>
	 * is called after the compilation of the code generated by the metaobject.
	 * The compiler itself, when generating code for a metaobject, inserts
	 * annotations <code>{@literal @}pushCompilationContext</code> and
	 * <code>{@literal @}popCompilationContext</code>.
	 *
	 */
	private Stack<Tuple5<String, String, String, String, Integer>> cyanMetaobjectContextStack;

	public void warning(Symbol sym, int lineNumber, String msg) {
		compilationUnit.warning(sym, lineNumber, msg, this, null);
	}

	public void warning(Symbol sym, String msg) {
		try {
			error2(sym, msg);
		}
		catch (final RuntimeException e) {

		}
	}

	public void error2(Symbol sym, String msg) {
		error2(true, sym, msg, true);
	}

	public void error2(boolean throwException, Symbol sym, String msg) {
		error2(throwException, sym, msg, true);
	}

	public void error2(Symbol sym, String msg, boolean checkMessage) {
		error2(true, sym, msg, checkMessage);
	}

	public void error2(boolean throwException, Symbol sym, String msg,
			boolean checkMessage) {
		error2(throwException, sym, -1, -1, msg, checkMessage);
	}

	public void error2(boolean throwException, Symbol sym, int lineNumber,
			int columnNumber, String msg, boolean checkMessage) {

		if ( mayBeWrongVarDeclaration ) {
			// this may be an error caused by a missing 'var' or 'let' before
			// a variable declaration
			msg = msg
					+ ". If your intension was to declare a variable, there is a missing 'var' or 'let' like in 'var Int n'";
			mayBeWrongVarDeclaration = false;
		}

		int lineNum = sym == null ? lineNumber : sym.getLineNumber();

		if ( !cyanMetaobjectContextStack.isEmpty()
				|| insideCyanMetaobjectCompilationContextPushAnnotation ) {
			if ( sym != null && lineNum > 0 ) {
				lineNum -= lineShift;
			}
		}
		else {
			/*
			 * error was in code produced by the compiler, maybe with the help
			 * of metaobjects. Give to the user the line number of the file with
			 * the expanded source code
			 */
			if ( this.compilationStep.ordinal() >= CompilationStep.step_4
					.ordinal() ) {
				msg = "Look for the error in the expanded source code, not in the original one. If the source code is 'Program.cyan' in package 'main', the "
						+ "expanded source code should be in 'full-main-Program.cyan' in the directory of the project. \n"
						+ msg;
			}

		}

		if ( checkMessage ) if ( checkErrorMessage(sym, lineNum, msg) ) return;

		msg = addContextMessage(cyanMetaobjectContextStack, msg);

		/*
		 * if ( ! cyanMetaobjectContextStack.isEmpty() ||
		 * this.compilationStep.ordinal() >= CompilationStep.step_7.ordinal() )
		 * { }
		 */

		compilationUnitSuper.error(throwException, sym, lineNum, columnNumber,
				msg, this, null);
	}

	public void error2(int lineNum, String msg, boolean checkMessage) {

		if ( checkMessage ) if ( checkErrorMessage(null, lineNum, msg) ) return;

		msg = addContextMessage(cyanMetaobjectContextStack, msg);
		/*
		 * if ( ! cyanMetaobjectContextStack.isEmpty() ||
		 * this.compilationStep.ordinal() >= CompilationStep.step_7.ordinal() )
		 * { }
		 */
		compilationUnitSuper.error(null, lineNum, msg, this, null);
	}

	public void error2(boolean throwException, int lineNumber, int columnNumber,
			String msg) {

		compilationUnitSuper.error(lineNumber, columnNumber, msg);
		if ( throwException ) {
			throw new CompileErrorException("Error in line " + lineNumber + "("
					+ columnNumber + "): " + msg);
			// throw new CompileErrorException();
		}

	}

	public void errorInsideAnnotation(Annotation metaobjectAnnotation,
			int lineNumber, int columnNumber, String message) {
		final CyanMetaobject metaobject = metaobjectAnnotation
				.getCyanMetaobject();
		if ( metaobject instanceof CyanMetaobjectLiteralString
				|| metaobject instanceof CyanMetaobjectAtAnnot ) {
			error2(true, null,
					metaobjectAnnotation.getFirstSymbol().getLineNumber()
							+ lineNumber - 1,
					columnNumber, message, true);
		}
	}

	/**
	 * If necessary, add a context message to the error message. This context
	 * informs that the error was caused by code introduced by such and such
	 * metaobject annotations OR by code introduced by the compiler.
	 *
	 * @param msg
	 * @return
	 */
	public static String addContextMessage(
			Stack<Tuple5<String, String, String, String, Integer>> contextStack,
			String msg) {

		if ( contextStack.isEmpty() ) {
			return msg;
		}
		/*
		 * there is a context. Then the code that caused this compilation error
		 * was introduced by some metaobject annotation or by the compiler
		 */
		Tuple5<String, String, String, String, Integer> t = contextStack.peek();

		if ( t.f3 == null && t.f4 == null && t.f5 == null ) {
			if ( !msg.endsWith(".") ) msg = msg + ".";
			msg = msg
					+ " This error was caused by code introduced by the compiler in step '"
					+ t.f2
					+ "'. Check the documentation of CyanMetaobjectCompilationContextPush";
		}
		else {
			String cyanMetaobjectName = t.f2;
			String packageName = t.f3;
			String sourceFileName = t.f4;
			int lineNumber = t.f5;
			if ( !msg.endsWith(".") ) msg = msg + ".";
			msg = msg
					+ " This error was caused by code introduced initially by metaobject annotation '"
					+ cyanMetaobjectName + "' at line " + lineNumber
					+ " of file " + sourceFileName + " of package "
					+ packageName;

			if ( contextStack.size() > 1 ) {
				String s = ". The complete stack of "
						+ "context (metaobject name, package.prototype, line number) is: ";
				for (int kk = 1; kk < contextStack.size(); ++kk) {
					t = contextStack.get(kk);
					cyanMetaobjectName = t.f2;
					packageName = t.f3;
					sourceFileName = t.f4;
					lineNumber = t.f5;
					s += "(" + cyanMetaobjectName + ", " + packageName + "."
							+ sourceFileName + ", " + lineNumber + ") ";
				}
				msg = msg + s;
			}
		}

		return msg;
	}

	/**
	 * A metaobject may have implemented interface
	 * {@link meta#IInformCompilationError} and informed the compiler that an
	 * error should be signaled. If any did, this method checks whether the
	 * error message passed as parameter was foreseen. If it wasn´t, a warning
	 * is signaled.
	 *
	 * @param sym
	 * @param lineNumber
	 * @param specificMessage
	 * @return true if there was a previous call to compilationError
	 */
	private boolean checkErrorMessage(Symbol sym, int lineNumber,
			String specificMessage) {

		if ( compilationUnit == null ) return false;

		if ( compilationUnit.getLineMessageList().size() == 0 )
			return false;
		else {
			/*
			 * A metaobject have implemented interface {@link
			 * meta#IInformCompilationError} and informed the compiler that an
			 * error should be signaled
			 */
			int i = 0;
			boolean found = false;
			for (final Tuple3<Integer, String, Boolean> t : compilationUnit
					.getLineMessageList()) {
				if ( lineNumber < 0 && t.f1 < 0 || t.f1 == lineNumber ) {
					// found the correct line number
					int correctLineNumber;
					if ( lineNumber < 0 )
						correctLineNumber = -1;
					else
						correctLineNumber = lineNumber;

					this.warning(sym, correctLineNumber,
							"The expected error message was '" + t.f2
									+ "'. The message given by the compiler was '"
									+ specificMessage + "'");
					compilationUnit.getLineMessageList().get(i).f3 = true;
					found = true;
					// throw new CompileErrorException(specificMessage);
					throw new CompileErrorException("Error in line "
							+ lineNumber + ": " + specificMessage);

				}
				++i;
			}
			if ( !found ) { // # how lineshift is 42 in file p1.E.cyan ?
				this.warning(sym, lineNumber,
						"The compiler issued the error message '"
								+ specificMessage
								+ "'. However, no metaobject implementing 'IInformCompilationError' has foreseen this error");
			}
			return true;
		}
	}

	public void error(boolean checkMessage, Symbol sym, String specificMessage,
			String identifier, ErrorKind errorKind, String... furtherArgs) {
		error(true, checkMessage, sym, specificMessage, identifier, errorKind,
				furtherArgs);
	}

	public void error(boolean throwException, boolean checkMessage, Symbol sym,
			String specificMessage, String identifier, ErrorKind errorKind,
			String... furtherArgs) {
		error2(throwException, sym, specificMessage, checkMessage);
	}

	public void error(boolean useEMS, boolean throwException,
			boolean checkMessage, Symbol sym, String specificMessage,
			String identifier, ErrorKind errorKind, String... furtherArgs) {

		/*
		 * if ( checkErrorMessage(sym, sym == null ? -1 : sym.getLineNumber(),
		 * specificMessage) ) return;
		 */

		error2(throwException, sym, specificMessage, checkMessage);

		if ( !useEMS ) return;

		/*
		 * if not call error2 above, call addContextMessage with specificMessage
		 */
		final List<String> sielCode = new ArrayList<String>();
		sielCode.add("error = \"" + specificMessage + "\"");
		for (final String field : errorKind.getFieldList()) {
			switch (field) {
			case "implementedInterfaces":
				if ( currentPrototype != null
						&& currentPrototype instanceof ObjectDec
						&& ((ObjectDec) currentPrototype)
								.getSuperobjectExpr() != null ) {
					final ObjectDec currentObject = (ObjectDec) currentPrototype;
					final List<Expr> exprList = currentObject
							.getInterfaceList();
					int sizeExprList = exprList.size();
					String s = "";
					for (final Expr ee : exprList) {
						s = s + ee.asString();
						if ( --sizeExprList > 0 ) s = s + ", ";
					}
					sielCode.add("implementedInterfaces = \"" + s + "\"");
					error2(null, "Internal error in Compiler::error: field '"
							+ field + "' of Siel cannot be used here");
				}
				break;

			case "supertype":
				if ( currentPrototype != null
						&& currentPrototype instanceof ObjectDec
						&& ((ObjectDec) currentPrototype)
								.getSuperobjectExpr() != null ) {
					sielCode.add(
							"supertype = \""
									+ ((ObjectDec) currentPrototype)
											.getSuperobjectExpr().asString()
									+ "\"");
					error2(null, "Internal error in Compiler::error: field '"
							+ field + "' of Siel cannot be used here");
				}
				break;
			case "identifier":
				sielCode.add("identifier = \"" + identifier + "\"");
				break;
			case "statementText":
				if ( !(currentPrototype instanceof InterfaceDec) ) {
					sielCode.add("statementText = \""
							+ lexer.stringStatementFromTo(
									this.startSymbolCurrentStatement, sym)
							+ "\"");
				}
				break;
			case "methodSignature":
				if ( currentMethod != null ) {
					sielCode.add("methodSignature = \""
							+ currentMethod.stringSignature() + "\"");
				}
				break;
			case "prototypeName":
				sielCode.add("prototypeName = \"" + currentPrototype.getName()
						+ "\"");
				break;
			case "interfaceName":
				sielCode.add("interfaceName = \"" + identifier + "\"");
				break;
			case "packageName":
				sielCode.add(
						"packageName = \"" + compilationUnit.getPackageName());
				break;
			case "importList":
				String strImportList = "";
				for (final ExprIdentStar e : compilationUnit
						.getImportPackageList())
					strImportList = strImportList + " " + e.getName();
				sielCode.add("importList = \"" + strImportList + "\"");
				break;
			case "metaobject":
				sielCode.add("metaobject = \"" + identifier + "\"");
				break;
			case "returnType":
				if ( currentMethod != null ) {
					sielCode.add(
							"returnType = \""
									+ currentMethod.getMethodSignature()
											.getReturnTypeExpr().asString()
									+ "\"");
				}
				break;
			default:
				String keyValue = null;
				String fieldName;
				for (final String other : furtherArgs) {
					int i = other.indexOf("=");
					if ( i > 0 ) {
						while (i > 0 && other.charAt(i - 1) == ' ')
							--i;
						if ( i > 0 ) {
							fieldName = other.substring(0, i);
							if ( fieldName.equals(field) ) {
								keyValue = other;
								break;
							}
						}

					}
					else
						error2(null,
								"Internal error in Env::error: error called without a key/value pair");

				}
				if ( keyValue != null )
					sielCode.add(keyValue);
				else
					error2(null, "Internal error in Env::error: field '" + field
							+ "' of Siel was not recognized");
				return;
			}
		}

	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public void setPrototype(Prototype prototype) {
		this.currentPrototype = prototype;
	}

	public Prototype getCurrentPrototype() {
		return currentPrototype;
	}

	public MethodDec getCurrentMethod() {
		return this.currentMethod;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public Project getProject() {
		return project;
	}

	/**
	 * if the parameter is true the lexer should consider the character new line
	 * as a token. See {@link Lexer#setNewLineAsToken(boolean)}.
	 *
	 * @param newLineAsToken
	 */
	public void setNewLineAsToken(boolean newLineAsToken) {
		lexer.setNewLineAsToken(newLineAsToken);
	}

	/**
	 * return true if the current token is inside a context function
	 *
	 * @return
	 */
	private boolean insideContextFunction() {
		for (final ExprFunction f : functionStack) {
			if ( f.isContextFunction() ) return true;
		}
		return false;
	}

	public Stack<Tuple5<String, String, String, String, Integer>> getCyanMetaobjectContextStack() {
		return cyanMetaobjectContextStack;
	}

	public Symbol[] getSymbolList() {
		cleanSymbolList = new Symbol[symbolList.length];
		boolean insidePushCompilationContext = false;
		sizeCleanSymbolList = 0;
		for (int i = 0; i < sizeSymbolList; ++i) {
			final Symbol sym = symbolList[i];
			if ( sym instanceof lexer.SymbolCyanAnnotation ) {
				final SymbolCyanAnnotation symAnnotation = (SymbolCyanAnnotation) sym;
				if ( symAnnotation.getCyanAnnotation()
						.getCyanMetaobject() instanceof meta.cyanLang.CyanMetaobjectCompilationContextPush ) {
					insidePushCompilationContext = true;
				}
			}
			if ( !insidePushCompilationContext ) {
				cleanSymbolList[sizeCleanSymbolList] = sym;
				++sizeCleanSymbolList;
			}
			if ( sym instanceof lexer.SymbolCyanAnnotation ) {
				final SymbolCyanAnnotation symAnnotation = (SymbolCyanAnnotation) sym;
				if ( symAnnotation.getCyanAnnotation()
						.getCyanMetaobject() instanceof meta.cyanLang.CyanMetaobjectCompilationContextPop ) {
					insidePushCompilationContext = false;
				}
			}

		}
		return cleanSymbolList;
	}

	public int getSizeSymbolList() {
		return sizeSymbolList;
	}

	public List<AnnotationAt> getCodegList() {
		return codegList;
	}

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void addToListAfter_afterResTypes(CyanMetaobject annotation) {
		this.project.getProgram().addToListAfter_afterResTypes(annotation);
	}

	private void thrownException(Annotation annotation, Symbol firstSymbol,
			Throwable e) {
		final String prototypeName = annotation.getPrototypeOfAnnotation();
		final String packageName = annotation.getPackageOfAnnotation();
		final int lineNumber = annotation.getFirstSymbol().getLineNumber();
		error2(firstSymbol,
				"Metaobject '" + annotation.getCyanMetaobject().getName() + "' "
						+ "of annotation of line number " + lineNumber + " in "
						+ packageName + "." + prototypeName
						+ " has thrown exception '" + e.getClass().getName()
						+ "'");

	}

	public Prototype searchPackagePrototype(String packageNameInstantiation,
			String prototypeNameInstantiation) {
		final CyanPackage cyanPackage = this.getProject()
				.searchPackage(packageNameInstantiation);
		if ( cyanPackage == null ) return null;
		final Prototype pu = cyanPackage
				.searchPublicNonGenericPrototype(prototypeNameInstantiation);

		return pu;
	}

	public CompilationStep getCompilationStep() {
		return compilationStep;
	}

	public char[] getText(int offsetLeftCharSeq, int offsetRightCharSeq) {
		return lexer.getText(offsetLeftCharSeq, offsetRightCharSeq);
	}

	public void pushRightSymbolSeq(String rightSymbolSeq) {
		lexer.pushRightSymbolSeq(rightSymbolSeq);
	}

	public void setAllowCreationOfPrototypesInLastCompilerPhases(
			boolean allowCreationOfPrototypesInLastCompilerPhases) {
		this.allowCreationOfPrototypesInLastCompilerPhases = allowCreationOfPrototypesInLastCompilerPhases;
	}

	public List<ICodeg> getOtherCodegList() {
		return otherCodegList;
	}

	public boolean getProhibitTypeof() {
		return prohibitTypeof;
	}

	public void setProhibitTypeof(boolean prohibitTypeof) {
		this.prohibitTypeof = prohibitTypeof;
	}

	// to be used only when parsing statements to be interpreted
	public int getNextFunctionNumber() {
		return nextFunctionNumber++;
	}

	public boolean getParsingPackageInterfaces() {
		return parsingPackageInterfaces;
	}

	public void setParsingPackageInterfaces(boolean parsingPackageInterfaces) {
		this.parsingPackageInterfaces = parsingPackageInterfaces;
	}

	// to be used only when parsing statements to be interpreted
	private int											nextFunctionNumber		= 0;

	/** next symbol of the input */

	public Symbol										symbol;

	/**
	 * This class is used for compiling Cyan code and Project code (method
	 * parseProject). In this last case, field projectFilename contains the
	 * project file name.
	 */
	private String										projectFilename;

	/**
	 * name of the filename in which objectName is declared
	 */
	private String										filename;
	/**
	 * The compilation unit that is currently being compiled. It is composed by
	 * all program units declared in the same source file.
	 */
	private CompilationUnit								compilationUnit;

	/**
	 * The compilation unit that is currently being compiled. It may be either a
	 * Cyan source file or a project file. In the first case,
	 * {@link #compilationUnit} refer to the same object as
	 * {@link #compilationUnitSuper}.
	 */
	private CompilationUnitSuper						compilationUnitSuper;

	/**
	 * the current program unit (object or interface) being compiled
	 */
	private Prototype									currentPrototype;

	/**
	 * a stack of prototypes. When the compiler starts to analyze a prototype it
	 * pushes it into the stack. There is at most one level of inner prototypes
	 * (prototype declared inside prototype) so this stack has at most two
	 * elements.
	 */
	private Stack<ObjectDec>							objectDecStack;
	/**
	 * the current method being compiled. Or the last compiled if the Compiler
	 * is not compiling a method when this variable is accessed.
	 */
	private MethodDec									currentMethod;

	/**
	 * number of public or protected prototypes (objects or interfaces) in the
	 * current source file
	 */
	private int											numPublicPackagePrototypes;

	/**
	 * a stack of functions. In point 1 below there will be two functions in the
	 * stack func test { var b = { var c = { // 1 } }; ]
	 */
	private Stack<ExprFunction>							functionStack;

	/**
	 * stack of visible parameters
	 */
	private Stack<ParameterDec>							parameterDecStack;
	/**
	 * stack of visible local variables
	 */
	private Stack<StatementLocalVariableDec>			localVariableDecStack;

	/*
	 * # stack used for compiling expressions. If the top of isMessageSendStack
	 * is true then the current expression (call to method expr() ) may be a
	 * message send.
	 *
	 * private Stack<Boolean> isMessageSendStack; #
	 */

	/**
	 * a counter of the number of functions inside a compilation unit
	 */
	private int											functionCounter;

	/**
	 * the lexer
	 */
	private Lexer										lexer;

	/**
	 * previous symbol
	 */
	private Symbol										previousSymbol;
	/**
	 * array with the next symbols. Each entry is null if the next symbols were
	 * not found yet
	 */
	private Symbol										nextSymbolList[];

	/**
	 * number of nested while´s and for´s. If this number is greater than 0,
	 * then a command 'break' is legal.
	 */
	private int											whileForCount;

	/**
	 * number of nested try statements. If this number is greater than 0, then a
	 * command 'return' is illegal.
	 */
	private int											tryCount;

	/**
	 * symbol that starts the current statement
	 *
	 */
	public Symbol										startSymbolCurrentStatement;
	/**
	 * the number of the current method. The first method declared has number 0.
	 */
	private int											methodNumber;

	/**
	 * a table with all macros imported by the current compilation units. The
	 * 'key' of the table is the start macro keyword such as "assert" in macro
	 * assert: assert n >= 0;
	 */
	private final HashMap<String, CyanMetaobjectMacro>	metaObjectMacroTable;

	/**
	 * the instruction set to the compilation
	 */
	private HashSet<meta.CompilationInstruction>		compInstSet;

	/**
	 * context number used to generated unique identifiers to
	 * {@literal @}pushCompilationContext and {@literal @}popCompilationContext
	 */
	private static int									contextNumber			= 0;
	/**
	 * Code may be inserted into a compilation unit by the compiler itself (see
	 * Figure of chapter Metaobjects of the Cyan manual) or by metaobject
	 * annotations. Code may be deleted from a compilation unit by metaobject
	 * annotations. Therefore when the compiler finds an error it may point to
	 * the wrong line. If code was inserted before the error the message would
	 * point to a line number greater than it is. The opposite happens when code
	 * was deleted. Variable lineShift keeps how many lines were inserted in the
	 * code. If negative, -lineShift is how many lines were deleted.
	 */
	private int											lineShift;

	/**
	 * To each anonymous function inside the prototype and for each method the
	 * compiler adds an inner prototype. This variable is true if this method,
	 * {@link Compiler#objectDec}, has already added these inner prototypes to
	 * the current prototype
	 */
	private boolean										hasAddedInnerPrototypes;
	/**
	 * true if methods such as prototype, clone, cast:, etc have already been
	 * added to this prototype by this method
	 */
	private boolean										hasAdded_pp_new_Methods;
	/**
	 * true if the compiler has added code in parsing actions
	 */
	private boolean										hasMade_dpa_actions;
	/**
	 * true if the file being compiled is a ScriptCyan file
	 */
	private boolean										scriptCyan;
	/**
	 * true if {@link #scriptCyan} is true and the source file being compiled
	 * has statements only. That is, it does not have methods, shared variables,
	 * and fields.
	 */
	private boolean										scriptCyanStatementsOnly;

	/**
	 * keeps all metaobject at-annotations of the current compilation unit whose
	 * metaobject classes are codegs. See otherCodegList;
	 */
	private List<AnnotationAt>							codegList;

	/**
	 * list of all Codegs in the code that are not at-annotations. A Codeg can
	 * be a number like 0101bin. In a near future, it can be a message send. See
	 * otherCodegList;
	 */
	private List<ICodeg>								otherCodegList;

	/**
	 * the compilation step, of course
	 */
	private final CompilationStep						compilationStep;
	/**
	 * lexer used only to do parsing of types given as strings
	 */
	private static Lexer								lexerFromString;
	/**
	 * the project. During the compilation of the '.pyan' source code this
	 * object is created and initialized. During the compilation of Cyan source
	 * code the value of this variable is received when the object Compiler is
	 * created.
	 */
	private Project										project;

	/**
	 * true if the lexer is scanning a metaobject annotation to
	 * {literal @}compilationContextPush. If this is true, the tokens found
	 * should not have their line numbers subtracted from lineShift. If this
	 * variable is true, stack cyanMetaobjectContextStack should still be empty.
	 */
	boolean												insideCyanMetaobjectCompilationContextPushAnnotation;

	/**
	 * true if a statement starts with an identifier that may be a type. This
	 * usually implies that this is a wrong variable declaration. The user
	 * forgot to put 'var' or 'let' before the declaration like in<br>
	 * {@code Int n;<br>
	 * Person p;<br>
	 * }<br>
	 */
	private boolean										mayBeWrongVarDeclaration;

	/**
	 * list of all symbols found in this compilation
	 */
	public Symbol[]										symbolList				= null;

	/**
	 * real size of symbolList
	 */
	public int											symbolListAllocatedSize	= 0;

	/**
	 * size of symbolList
	 */
	public int											sizeSymbolList			= 0;

	/**
	 * list of all symbols found in the parsing with symbols introduced by
	 * metaobjects and the compiler removed.
	 */
	public static Symbol[]								cleanSymbolList;

	public static int									sizeCleanSymbolList;

	/**
	 * pairs of codeg name and the text stored in the file associated to the
	 * codeg. In the annotation <br>
	 * <code> var c = {@literal @}color(red) </code><br>
	 * the tuple would be, using Cyan syntax:
	 * <code> [. "color(red)", "343" .] </code>
	 */
	private List<Tuple2<String, byte[]>>				codegNameWithCodegFile	= null;

	public CompilationUnitSuper getCompilationUnitSuper() {
		return compilationUnitSuper;
	}

	public boolean getParsingForInterpreter() {
		return parsingForInterpreter;
	}

	public void setParsingForInterpreter(boolean parsingForInterpreter) {
		this.parsingForInterpreter = parsingForInterpreter;
	}

	/**
	 * if this variable is true, it is a compiler error if function 'typeof' is
	 * used.
	 */
	private boolean	prohibitTypeof;

	/**
	 * true if the compiler is allowed to create instantiations of generic
	 * prototypes in compilation phases >= 7.
	 */
	private boolean	allowCreationOfPrototypesInLastCompilerPhases;

	private Program	program;

	// private Env notNullIfCreatingGenericPrototypeInstantiation;

	/**
	 * true if parsing is for the Cyan interpreter. Then some checks should not
	 * be made by the compiler
	 */
	private boolean	parsingForInterpreter		= false;

	/**
	 * true if parsing is for a source file with interfaces of a package. This
	 * is used for separate compilation. The package is not compiled again but
	 * the source file, "allInterfaces.iyan", with the prototype interfaces, is.
	 * Then if this field is true, the compiled file is "allInterfaces.iyan".
	 */
	private boolean	parsingPackageInterfaces	= false;

}
