
package saci;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ast.ASTNode;
import ast.Annotation;
import ast.AnnotationAt;
import ast.CompilationUnit;
import ast.CompilationUnitSuper;
import ast.CyanPackage;
import ast.EvalEnv;
import ast.Program;
import ast.Prototype;
import ast.Statement;
import ast.Type;
import ast.TypeDynamic;
import chooseFile.ChooseFoldersCyanInstallation;
import cyan.lang.CyByte;
import cyan.lang.CyInt;
import cyan.lang.CyString;
import cyan.lang._Array_LT_GP_CyByte_GT;
import cyan.lang._Set_LT_GP__Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._ICodeg;
import cyan.reflect._ICommunicateInPrototype__ded;
import cyanruntime.ExceptionContainer__;
import error.CompileErrorException;
import error.ProjectError;
import error.UnitError;
import lexer.Symbol;
import meta.CompilationInstruction;
import meta.CompilationPhase;
import meta.CompilationStep;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectLiteralObjectSeq;
import meta.CyanMetaobjectLiteralString;
import meta.CyanMetaobjectMacro;
import meta.CyanMetaobjectNumber;
import meta.Feature;
import meta.Function0;
import meta.ICodeg;
import meta.ICommunicateInPrototype_ded;
import meta.MetaHelper;
import meta.SourceCodeChangeByAnnotation;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.WrAnnotation;
import meta.WrAnnotationAt;
import meta.WrPrototype;
import meta.WrType;
import meta.WrTypeDynamic;
import metaRealClasses.Compiler_ded;

/**
 * This is the Cyan compiler. See more about it in the Cyan manual.
 *
 *
 * @author jose
 */

public class Saci {

	/*
	 * (non-Javadoc)
	 *
	 * @see saci.ISaci#searchCodegAnnotation(int, int) public AnnotationAt
	 * searchCodegAnnotation(int line, int column) {
	 *
	 * if ( codegList != null ) { for ( AnnotationAt annotation : this.codegList
	 * ) { int lineAnnotation = annotation.getFirstSymbol().getLineNumber(); int
	 * columnAnnotationStart = annotation.getFirstSymbol().getColumnNumber();
	 * int lineAnnotationEnds = annotation.getLastSymbol().getLineNumber(); int
	 * columnAnnotationEnds = annotation.getLastSymbol().getColumnNumber(); if (
	 * line >= lineAnnotation && line <= lineAnnotationEnds && column >=
	 * columnAnnotationStart && column <= columnAnnotationEnds ) { //* found a
	 * codeg annotation that is in (line, column) return annotation; } } }
	 * return null; }
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see saci.ISaci#searchCodegAnnotation(int)
	 */
	public CyanMetaobject searchCodegAnnotation(int offset) {

		if ( codegList != null ) {
			for (final AnnotationAt annotation : this.codegList) {
				final int start = annotation.getFirstSymbol().getOffset();
				final int end = annotation.getLastSymbol().getOffset();
				if ( offset >= start && offset <= end ) {
					return annotation.getCyanMetaobject();
				}

			}
		}
		if ( this.otherCodegList != null && this.otherCodegList.size() > 0 ) {
			for (final ICodeg aCodeg : this.otherCodegList) {

				int start;
				int end;
				_CyanMetaobject other = ((CyanMetaobject) aCodeg)
						.getMetaobjectInCyan();
				if ( other == null ) {
					start = aCodeg.getStartOffset();
					end = aCodeg.getEndOffset();
				}
				else {
					start = ((_ICodeg) other)._getStartOffset().n;
					end = ((_ICodeg) other)._getEndOffset().n;
				}
				if ( (offset >= start && offset <= end)
						&& (aCodeg instanceof CyanMetaobject) ) {
					return (CyanMetaobject) aCodeg;
				}
			}
		}
		return null;
	}

	/*
	 * to be called by the IDE plugin. codegAnnotation is a Codeg that is under
	 * the mouse pointer. This method calls method getUserInput of the Codeg
	 * metaobject passing as parameter the previous data. After this method
	 * returns, the new data is written in a file. <br> The return value is the
	 * new annotation text that should replace the current annotation. For
	 * example, if this method is called because the mouse pointer is over color
	 * in <br> <code> c = @color(red);</code><br> then this metaobject may
	 * choose to replace "@color(red)" by "@color(green)". This last string
	 * would be returned by method newCodegAnnotation of the metaobject
	 * (declared in interface ICodeg) and also returned by this method. If the
	 * metaobject does not want to replace the metaobject annotation, it should
	 * return null, which is the default. Then this method will return null too.
	 */

	public char[] eventCodegMenu(CyanMetaobject cyanMetaobject) {
		if ( !(cyanMetaobject instanceof ICodeg) ) {
			// probably an internal error
			return null;
		}
		final WrAnnotation annot = cyanMetaobject.getAnnotation();
		if ( annot instanceof WrAnnotationAt ) {
			return eventCodegMenu((WrAnnotationAt) annot);
		}
		else {

			final Compiler_ded compiler_ded = new Compiler_ded(this.project,
					meta.GetHiddenItem.getHiddenCompilationUnitSuper(
							annot.getCompilationUnit()),
					annot.getLocalVariableNameList());

			char[] ret;
			String newCodegText;
			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			byte[] previousCodegFileText = null;
			// recuperar o texto do metaobjeto
			if ( cyanMetaobject instanceof CyanMetaobjectLiteralObject ) {
				CyanMetaobjectLiteralObject litObj = (CyanMetaobjectLiteralObject) cyanMetaobject;
				String usefulString = litObj.getUsefulString();
				if ( usefulString != null ) {
					previousCodegFileText = usefulString.getBytes();
				}
			}

			if ( other == null ) {
				final ICodeg icodeg = (ICodeg) cyanMetaobject;

				final Runtime rt = Runtime.getRuntime();

				// rt.exec
				icodeg.getUserInput(compiler_ded, previousCodegFileText);
				newCodegText = icodeg.newCodegAnnotation();
			}
			else {
				final _ICodeg icodeg = (_ICodeg) cyanMetaobject;
				_Array_LT_GP_CyByte_GT cyanByteArray = null;
				if ( previousCodegFileText == null ) {
					cyanByteArray = new _Array_LT_GP_CyByte_GT();
				}
				else {
					CyByte[] cyByteArray = new CyByte[previousCodegFileText.length];
					for (int i = 0; i < previousCodegFileText.length; ++i) {
						cyByteArray[i] = new CyByte(previousCodegFileText[i]);
					}
					cyanByteArray = new _Array_LT_GP_CyByte_GT(cyByteArray);
				}

				icodeg._getUserInput_2(compiler_ded, cyanByteArray);
				newCodegText = icodeg._newCodegAnnotation().s;
			}
			if ( newCodegText != null ) {
				annot.replaceAnnotationBy(newCodegText);
				ret = annot.getCompilationUnit().getOriginalText();
			}
			else {
				ret = null;
			}
			return ret;

		}
	}

	public char[] eventCodegMenu(WrAnnotationAt codegAnnotation) {

		final CyanMetaobjectAtAnnot codeg = codegAnnotation.getCyanMetaobject();
		if ( !(codeg instanceof ICodeg) ) {
			return null;
		}
		byte[] codegFileData = null;

		Tuple2<String, byte[]> foundTuple = null;
		/*
		 * completeName may be "color(red)"
		 */
		final String completeName = codegAnnotation.getCompleteName();
		int i = 0;
		if ( codegNameWithCodegFile == null ) {
			/*
			 * parseSingleSource has not been called or parseSingleSource has
			 * not been called after the whole program has been compiled. {@link
			 * Saci#eventCodegMenu} should return false to give a change to the
			 * IDE call parseSingleSource.
			 */
			return null;
		}
		else {
			for (final Tuple2<String, byte[]> t : this.codegNameWithCodegFile) {
				if ( completeName.equals(t.f1) ) {
					codegFileData = t.f2;
					foundTuple = t;
					break;
				}
				++i;
			}
		}

		/*
		 * CompilationUnit compUnit = this.getLastCompilationUnitParsed();
		 * Prototype pu = null; ObjectDec prototype = null; if ( compUnit !=
		 * null ) { pu = compUnit.getPublicPrototype(); if ( pu instanceof
		 * ObjectDec ) prototype = (ObjectDec ) pu; }
		 */

		final Compiler_ded compiler_ded = new Compiler_ded(this.project,
				meta.GetHiddenItem.getHiddenCompilationUnitSuper(
						codegAnnotation.getCompilationUnit()),
				codegAnnotation.getLocalVariableNameList());

		_CyanMetaobject other = codeg.getMetaobjectInCyan();

		if ( other == null ) {
			final ICodeg icodeg = (ICodeg) codeg;
			codegFileData = icodeg.getUserInput(compiler_ded,
					foundTuple == null ? null : foundTuple.f2);
		}
		else {
			_Array_LT_GP_CyByte_GT byteArray = new _Array_LT_GP_CyByte_GT();
			if ( foundTuple != null && foundTuple.f2 != null ) {
				for (byte element : foundTuple.f2) {
					byteArray._add_1(new CyByte(element));
				}
			}

			final _ICodeg icodeg = (_ICodeg) other;
			codegFileData = CyanMetaobjectAtAnnot
					.fromCyanArrayByteToJavaArrayByte(
							icodeg._getUserInput_2(compiler_ded, byteArray));
		}

		if ( codegFileData == null ) {
			codegFileData = new byte[1];
		}

		if ( foundTuple == null ) {
			// this is the first time eventCodegMenu has been called with this
			// Codeg.
			// Insert it into codegNameWithCodegFile
			codegNameWithCodegFile
					.add(new Tuple2<>(completeName, codegFileData));
		}
		else {
			// The Codeg data was already in codegNameWithCodegFile. Update it
			/*
			 * update list codegNameWithCodegFile so it is not necessary to read
			 * the file again when calling {@link Saci#parseSingleSource}.
			 * During parsing, the compiler retrieves the information from
			 * codegNameWithCodegFile, which is passed as parameter.
			 */
			codegNameWithCodegFile.set(i,
					new Tuple2<>(completeName, codegFileData));
		}
		/**
		 * user really give information to the codeg which created a text to be
		 * stored in a file. This text was returned by
		 * {@link ICodeg#getUserInput}.
		 */
		char[] ret;

		String newCodegText;
		if ( other == null ) {
			newCodegText = ((ICodeg) codeg).newCodegAnnotation();
		}
		else {
			newCodegText = ((_ICodeg) other)._newCodegAnnotation().s;
		}

		if ( newCodegText != null ) {
			codegAnnotation.replaceAnnotationBy(newCodegText);
			ret = codegAnnotation.getCompilationUnit().getOriginalText();
		}
		else {
			ret = null;
		}

		codegAnnotation.setCodegInfo(codegFileData);

		String newFirstParameter;
		if ( other == null ) {
			newFirstParameter = ((ICodeg) codeg).getNewFirstParameter();
		}
		else {
			newFirstParameter = ((_ICodeg) other)._getNewFirstParameter().s;
		}
		final String filename = codegAnnotation
				.filenameAnnotationInfo(newFirstParameter);

		if ( filename == null ) {
			this.error(
					"Internal error: cannot find filename associated to codeg annotation of line "
							+ codegAnnotation.getFirstSymbol().getLineNumber()
							+ " and column " + codegAnnotation.getFirstSymbol()
									.getColumnNumber());
			return null;
		}
		else {

			final Path path = Paths.get(filename);

			try {

				final Path parentDir = path.getParent();
				if ( !Files.exists(parentDir) ) {
					Files.createDirectories(parentDir);
				}
				Files.write(path, codegFileData);
			}
			catch (final IOException e) {
				this.error("Error writing information on codeg '"
						+ codegAnnotation.getCompleteName() + "' to file "
						+ filename);
			}

			return ret;
			/*
			 * MyFile f = new MyFile(filename);
			 * f.writeFile(codegFileText.toString().toCharArray()); if (
			 * f.getError() == MyFile.do_not_exist_e ) {
			 * this.error("Error creating file '" + filename + "'"); } else { if
			 * ( f.getError() != MyFile.ok_e )
			 * this.error("Error writing information on codeg '" +
			 * codegCall.getCompleteName() + "' to file " + filename ); }
			 */

		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see saci.ISaci#eventChangeSourceCodeBeingEdited(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, char[],
	 * java.lang.String, char[], boolean)
	 */
	// @Override
	// @SuppressWarnings("hiding")
	// public void eventChangeSourceCodeBeingEdited( String cyanLangDir, String
	// javaLibDir,
	// String packageName, String prototypeName, char []sourceCodeToParse,
	// String projectFileName,
	// char []sourceCodeProject,
	// boolean loadProjectFromFile) {
	// if ( javaLibDir == null ) {
	// javaLibDir = cyanLangDir + "\\javalib";
	// }
	// codegNameWithCodegFile = null;
	// lastCompilationUnitParsed = null;
	// this.parseSingleSource(cyanLangDir, javaLibDir, packageName,
	// prototypeName, sourceCodeToParse, projectFileName,
	// sourceCodeProject, loadProjectFromFile);
	// }

	/**
	 * pairs of codeg name and the data stored in the file associated to the
	 * codeg. In the annotation <br>
	 * <code> var c = ({@literal @}color(red)); </code><br>
	 * the tuple would be, using Cyan syntax:
	 * <code> [. "color(red)", [ 3B, 4B, 3B ] .] </code>
	 */
	private List<Tuple2<String, byte[]>> codegNameWithCodegFile = null;

	public static void main(String[] args) {

		// new CyanTypeCompiler(null).test();
		// System.exit(0);

		final Saci aSaci = new Saci();
		aSaci.compilerCalledFromCommandLine = true;

		if ( args.length < 1 ) {
			System.out.println(
					"Usage:\n   CC.Saci projectDirectoryOrName compilerOptions ");
			System.out.println(
					"projectDirectoryOrName is the file name of the project or the directory name in which the project is");
			System.out.println("compilerOptions may be:");
			System.out.println(
					"    '-noexec' for not executing the compiled Java code");
			System.out.println(
					"    '-nojavac' for not calling the Java compiler");
			System.out.println(
					"    '-args argList' for arguments to the Cyan program. The arguments "
							+ "that follow '-args', argList, will be passed to the Cyan program if it is to be executed");
			System.out.println(
					"    '-cp aPath' for supplying 'aPath' for the Java compiler. This option"
							+ " can appear any number of times and multiple paths, separated by ';', can be given using a single '-cp'");
			System.out.println(
					"    '-es file' for interpreting the next argument as Cyan code");
			System.out.println(
					"    '-ef file' for interpreting 'file', like '-ef \"C:\tests\first.syan\" (the recommended extension is 'syan')");
			System.out.println(
					"    '-p' for profing the compiler (measure times in each step)");
			System.out.println(
					"    '-totaltime' for measuring the total execution time of the compiler");
			System.out.println(
					"    '-addTypeInfo outputDir' for adding code to be used by the tool 'addType'");
			System.out.println(
					"    '-noTimeLimForMO' don't limit time for metaobjects to execute");

			System.exit(1);
		}

		// main2(args); System.exit(0); System.out.println("End
		// parseSingleSource");
		aSaci.parseCmdLineArgs(args);
		TM.startTime("start saci");
		if ( aSaci.totalTime ) {
			TM.firstStartTime();
		}

		try {
			long realTotalCPUTime = 0;
			for (int numSaciCalls = 0; numSaciCalls < numCompilerCalls; ++numSaciCalls) {

				ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
				long cpuStart = threadMXBean.getCurrentThreadCpuTime(); // Nanoseconds
																		// CPU
																		// time

				aSaci.run();

				long cpuEnd = threadMXBean.getCurrentThreadCpuTime();
				double cpuTimeSeconds = (cpuEnd - cpuStart) / 1_000_000_000.0;
				realTotalCPUTime += cpuTimeSeconds;
			}
			TM.endTime("after run");
			if ( aSaci.profile ) {
				TM.printMessage();
			}
			if ( aSaci.totalTime ) {
				System.err.println("Avg time = " + (double) realTotalCPUTime
						/ (double) numCompilerCalls);
				TM.printEndTime();
			}

		}
		catch (Throwable e) {
			System.out.println("Exception '" + e.getClass().getName()
					+ "' was thrown during compilation." + " Its message is '"
					+ e.getMessage() + "'");
		}

		/*
		 * PrintWriter printWriter = new PrintWriter(System.out, true); if (
		 * aSaci.getProjectErrorList() != null ) { for ( ProjectError p :
		 * aSaci.getProjectErrorList() ) p.print(printWriter); } if (
		 * aSaci.getCyanErrorList() != null ) { for ( UnitError ue :
		 * aSaci.getCyanErrorList() ) ue.print(printWriter); }
		 */
		System.out.println("Saci is over");
		System.out.flush();
		System.exit(0);

	}

	public static void main2(String[] args) {

		final Saci aSaci = new Saci();
		aSaci.compilerCalledFromCommandLine = false;

		aSaci.parseCmdLineArgs(args);

		String dir = "C:\\Dropbox\\Cyan\\cyanTests\\codegTest\\";
		final MyFile fp = new MyFile(dir + "project.pyan");
		final char[] sourceProject = fp.readFile();
		final MyFile fs = new MyFile(dir + "main\\Program.cyan");
		final char[] sourceProgram = fs.readFile();

		final PrintWriter printWriter = new PrintWriter(System.out, true);

		/*
		 * test only
		 */

		for (int kk = 0; kk < 1; ++kk) {
			// System.exit( aSaci.run(args) );
			aSaci.parseSingleSource(aSaci.cyanLangDir, aSaci.javaLibDir, "main",
					"Program", sourceProgram,
					// "package main object Program func run { let G<Int> g; g m
					// println; }
					// end\0".toCharArray(),
					dir + "project.pyan", sourceProject, false);
			for (ICodeg codeg : aSaci.getOtherCodegList()) {
				System.out.println(((CyanMetaobject) codeg).getName());
			}
			if ( aSaci.getProjectErrorList() != null
					&& aSaci.getProjectErrorList().size() > 0 ) {
				for (final ProjectError p : aSaci.getProjectErrorList()) {
					p.print(printWriter);
				}
				break;
			}
			if ( aSaci.getCyanErrorList() != null
					&& aSaci.getCyanErrorList().size() > 0 ) {
				for (final UnitError ue : aSaci.getCyanErrorList()) {
					ue.print(printWriter);
				}
				break;
			}

		}

		/*
		 * if ( aSaci.getCodegList() != null && aSaci.getCodegList().size() > 0
		 * ) { Symbol sym = aSaci.getCodegList().get(0).getFirstSymbol();
		 * //System.out.println(sym.getLineNumber() + " " +
		 * sym.getColumnNumber() + " " + sym.getSymbolString()); sym =
		 * aSaci.getCodegList().get(0).getLastSymbol();
		 * //System.out.println(sym.getLineNumber() + " " +
		 * sym.getColumnNumber() + " " + sym.getSymbolString()); }
		 */

		System.exit(0);

		/*
		 * end test
		 */

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see saci.ISaci#parseSingleSource(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, char[], java.lang.String, char[],
	 * boolean)
	 */
	@SuppressWarnings("hiding")
	public boolean parseSingleSource(String cyanLangDir, String javaLibDir,
			String packageName, String prototypeName, char[] sourceCodeToParse,
			String projectDirectoryOrName, char[] sourceCodeProject,
			boolean loadProjectFromFile) {

		final long before = System.nanoTime();

		/*
		 * a static error list keeps all errors during compilation. It is
		 * necessary because there are compilation errors that simply disappear
		 * after they are signalled. That is, the compiler does not insert them
		 * in the list of errors --- not easy to fix because some errors should
		 * not be inserted in the list of errors although most should
		 */
		UnitError.initStaticErrorList();

		this.cyanLangDir = cyanLangDir;
		if ( javaLibDir == null ) {
			javaLibDir = cyanLangDir + NameServer.fileSeparator + "javalib";
		}

		this.javaLibDir = javaLibDir;
		this.packageName = packageName;
		this.prototypeName = prototypeName;
		this.sourceCodeToParse = sourceCodeToParse;
		this.projectDirectoryOrName = projectDirectoryOrName;
		this.sourceCodeProject = sourceCodeProject;

		if ( codegNameWithCodegFile == null ) {
			/*
			 * codegNameWithCodegFile is null when parseSingleSource has never
			 * been called
			 */
			codegNameWithCodegFile = new ArrayList<>();
		}

		addCyanLangLibToClassPath();

		if ( packageName == null || prototypeName == null
				|| sourceCodeToParse == null ) {
			return false;
		}
		if ( sourceCodeToParse[sourceCodeToParse.length - 1] != '\0' ) {
			this.error("The source code should end with character '\\0'");
		}

		if ( sourceCodeProject != null
				&& sourceCodeProject[sourceCodeProject.length - 1] != '\0' ) {
			this.error(
					"The source code of the project should end with character '\\0'");
		}

		compilerCalledFromCommandLine = false;

		final PrintWriter printWriter = new PrintWriter(System.out, true);

		// MyFile projectFile = new MyFile(args[0]);

		setErrorList = false;
		setProjectErrorList = false;

		HashSet<CompilationInstruction> compInstSet;
		if ( project == null || loadProjectFromFile
				|| sourceCodeProject == null ) {
			errorList = new ArrayList<>();
			projectErrorList = new ArrayList<>();
			program = new Program(addTypeInfo, typeInfoPath);

			project = parseProject();
			if ( project.hasErrors() ) {
				return false;
			}
			project.setCallJavac(callJavac);
			project.setExec(exec);
			project.setCmdLineArgs(cmdLineArgs);
			program.setProject(project);
			project.setProgram(program);
			program.setJavaLibDir(javaLibDir);

		}
		else {
			program.setProject(project);
			project.setProgram(program);
			if ( projectErrorList == null ) {
				projectErrorList = new ArrayList<>();
			}
			else {
				projectErrorList.clear();
			}
			if ( errorList == null ) {
				errorList = new ArrayList<>();
			}
			else {
				errorList.clear();
			}
		}

		loadJavaRuntimePackages(printWriter);

		final HashMap<String, String> compilerOptions = new HashMap<>();

		// createJavaClasses(cyanLangDir);

		final CompilerManager compilerManager = new CompilerManager(project,
				program, printWriter, compilerOptions);
		project.setCompilerManager(compilerManager);

		program.init();
		// System.out.println("the code 'program.init();' was removed from
		// parseSingleSource. Check if there is no problem with that");

		/*
		 * step 1 only
		 */
		compilerManager.setCompilationStep(CompilationStep.step_1);
		compInstSet = new HashSet<>();
		compInstSet.add(CompilationInstruction.parsing_actions);
		compInstSet.add(CompilationInstruction.pp_addCode);
		compInstSet.add(CompilationInstruction.parsing_originalSourceCode);

		/*
		 * parse only packageName.prototypeName
		 */

		final ast.CyanPackage cyanPackage = project
				.searchPackage(this.packageName);
		if ( cyanPackage == null ) {
			this.error("Package " + this.packageName + " was not found");
			return false;
		}
		lastCompilationUnitParsed = cyanPackage
				.searchCompilationUnit(this.prototypeName);
		lastCompilationUnitParsed.setText(this.sourceCodeToParse);
		lastCompilationUnitParsed.setOriginalText(this.sourceCodeToParse);

		lastCompilationUnitParsed.clearErrorList();

		final Compiler compiler = new Compiler(lastCompilationUnitParsed,
				compInstSet, CompilationStep.step_1, project,
				codegNameWithCodegFile);

		// long before2 = System.nanoTime();
		// long beforeMili = System.currentTimeMillis();

		compiler.parse();

		this.symbolList = compiler.getSymbolList();
		this.sizeSymbolList = compiler.getSizeSymbolList();
		this.codegList = compiler.getCodegList();
		this.otherCodegList = compiler.getOtherCodegList();

		// make Codegs of codegList communicate
		makeCodegsCommunicateInPrototype();

		/*
		 * double diffNano = (System.nanoTime() - before2)/1000000.0; long
		 * diffMili = System.currentTimeMillis() - beforeMili;
		 * //System.out.println("parse only: " + diffNano);
		 * //System.out.println("parse only: " + diffMili );
		 */

		// System.out.println("parse project : " + (System.nanoTime() -
		// before)/1000000.0 );
		return true;
	}

	/**
	 * the last compilation unit compiled by
	 * {@link Saci#parseSingleSource(String, String, String, String, char[], String, char[], boolean)}.
	 */
	private CompilationUnit	lastCompilationUnitParsed;
	boolean					exec					= true;
	boolean					callJavac				= true;
	List<String>			classPathList			= null;
	List<String>			sourcePathList			= null;

	/*
	 * number of time the compiler should be called. Used to evaluate the
	 * performance of the compiler
	 *
	 */
	static public int		numCompilerCalls		= 1;
	/*
	 * If true, there is a time limit for metaobject methods to execute give by
	 * variable MetaHelper.timeoutMillisecondsMetaobjectsDefaultValue
	 */
	static public boolean	timeLimitForMetaobjects	= true;
	/**
	 * true if the project may import Java packages. If classPathList is not
	 * empty, this variable is set to true
	 */
	boolean					mayImportJavaPackages;

	String					packageName				= null;
	String					prototypeName			= null;
	char[]					sourceCodeToParse		= null;
	char[]					sourceCodeProject		= null;
	String					projectDirectoryOrName	= null;
	String					cyanLangDir				= null;
	String					javaLibDir				= null;

	// String projectCanonicalPath = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see saci.ISaci#compileProject(java.lang.String, java.lang.String,
	 * java.lang.String, boolean, boolean, java.util.List)
	 */
	@SuppressWarnings("hiding")
	public void compileProject(String projectDirectoryOrName,
			String cyanLangDir, String javaLibDir, boolean exec,
			boolean callJavac, List<String> classPathList) {
		this.projectDirectoryOrName = projectDirectoryOrName;
		this.cyanLangDir = cyanLangDir;
		this.javaLibDir = javaLibDir;
		this.exec = exec;
		this.callJavac = callJavac;
		this.cmdLineArgs = null;
		this.classPathList = classPathList;

		compilerCalledFromCommandLine = false;
		run();

	}

	private static Set<String> optionsSet;
	static {
		String[] arrayOpSet = new String[] { "-javalib", "-noexec", "-nojavac",
				"-args", "-cp", "-java", "-sourcepath", "-es", "-ef",
				"-addTypeInfo" };
		Saci.optionsSet = new HashSet<>();
		for (String s : Saci.optionsSet) {
			Saci.optionsSet.add(s.toUpperCase());
		}
	}

	private int parseCmdLineArgs(String[] args) {

		final int ret = 1;

		cmdLineArgs = null;
		exec = true;
		callJavac = true;
		int i = 0;
		classPathList = new ArrayList<>();
		sourcePathList = new ArrayList<>();
		mayImportJavaPackages = false;
		this.profile = false;
		this.totalTime = false;
		while (i < args.length) {
			if ( args[i].equalsIgnoreCase("-noexec") ) {
				exec = false;
			}
			else if ( args[i].equalsIgnoreCase("-nojavac") ) {
				callJavac = false;
			}
			else if ( args[i].equalsIgnoreCase("-cp") ) {
				if ( i >= args.length - 1 ) {
					error("Missing Java class path after '-cp'");
				}
				classPathList.add(args[i + 1]);
				++i;
				mayImportJavaPackages = true;
			}
			else if ( args[i].equalsIgnoreCase("-r") ) {
				if ( i >= args.length - 1 ) {
					error("Missing number of repetition after '-r'");
				}
				try {
					numCompilerCalls = Integer.parseInt(args[i + 1]);
					if ( numCompilerCalls > 1000 || numCompilerCalls < 1 ) {
						System.err.println(
								"Number after -r should be >= 1 and <= 1000");
						numCompilerCalls = 100;
					}
				}
				catch (NumberFormatException e) {
					System.err.println("Invalid number format: " + args[i + 1]);
				}
				++i;
			}

			else if ( args[i].equalsIgnoreCase("-java") ) {
				mayImportJavaPackages = true;
			}
			else if ( args[i].equalsIgnoreCase("-sourcepath") ) {
				if ( i >= args.length - 1 ) {
					error("Missing Java source path after '-sourcepath'");
				}
				sourcePathList.add(args[i + 1]);
			}

			else if ( args[i].equalsIgnoreCase("-args") ) {
				cmdLineArgs = "";
				for (int j = i + 1; j < args.length; ++j) {
					if ( Saci.optionsSet.contains(args[j].toUpperCase()) ) {
						error("The compiler option '" + args[j]
								+ "' cannot appear after option '-args'");
					}
					cmdLineArgs += args[j] + " ";
				}
				break;
			}
			else if ( args[i].equalsIgnoreCase("-checkAST") ) {
				Saci.checkAST();
			}
			else if ( args[i].equalsIgnoreCase("-noTimeLimForMO") ) {
				timeLimitForMetaobjects = false;
			}
			else if ( args[i]
					.equalsIgnoreCase("-generateCyanPrototypesForMOP") ) {
				this.generateCyanPrototypesForMOP();
				System.exit(0);
			}
			else if ( args[i].charAt(0) != '-' ) {
				if ( projectDirectoryOrName == null ) {
					projectDirectoryOrName = args[i];
				}
				else {
					error("Found two project names: '" + projectDirectoryOrName
							+ "' and '" + args[i] + "'");
					return ret;
				}
			}
			else if ( args[i].equalsIgnoreCase("-es") ) {
				if ( i >= args.length - 1 ) {
					error("Missing Cyan source code after '-es'");
				}
				StringBuilder s = new StringBuilder();
				++i;
				while (i < args.length) {
					s.append(args[i]).append(" ");
					++i;
				}

				this.checkSetCyanLangDir();

				// Object result = new CyanCodeSnippet().evalCyanCode(s, new
				// Object() { },
				// cyanLangDir, javaLibDir);
				try {
					// System.out.println("The string is, between single quotes:
					// '" + s.toString() + "'");
					final Object result = new CyanCodeSnippet()
							.evalCyanCode(s.toString(), new Object() {
							});

					if ( EvalEnv.any.isAssignableFrom(result.getClass()) ) {
						// if ( result instanceof EvalEnv.any ) {

						/*
						 * Object sendMessage(Object receiver, String
						 * cyanMethodNameInJava, String javaMethodName, Object
						 * []argList, Function0 inErrorCall)
						 */
						System.out.println((String) Statement.getField(Statement
								.sendMessage(result, "_asString", "asString",
										null, "asString", null, null),
								"s"));
						// System.out.println("Answer: " + ((_Any )
						// result)._asString().s );
					}
					else {
						System.out.println("Answer: " + result);
					}
				}
				catch (RuntimeException e) {
					System.out.println(
							"There was an error interpreting the code OR an uncaught exception was thrown");
				}
				System.exit(0);
			}
			else if ( args[i].equalsIgnoreCase("-ef") ) {
				if ( i >= args.length - 1 ) {
					error("Missing file name with Cyan source code after '-ef'");
				}
				String filename = args[i + 1];

				String s = MyFile.readFile(filename, StandardCharsets.UTF_8);
				if ( s == null ) {
					System.out.println("Error reading file '" + filename
							+ "' that appeared after option '-ef'");
					System.exit(1);
				}

				this.checkSetCyanLangDir();

				try {
					final Object result = new CyanCodeSnippet().evalCyanCode(s,
							new Object() {
							});

					if ( result != null ) {
						if ( EvalEnv.any.isAssignableFrom(result.getClass()) ) {
							System.out.println((String) Statement
									.getField(Statement.sendMessage(result,
											"_asString", "asString", null,
											"asString", null, null), "s"));
						}
						else {
							System.out.println("Result: " + result);
						}
					}
				}
				catch (ExceptionContainer__ e) {
					System.out.println("\n"
							+ ((cyan.lang._ExceptionStr) e.elem)._message().s);
				}
				catch (RuntimeException e) {
					e.printStackTrace();
					System.out.println(
							"There was an error interpreting the code OR an uncaught exception was thrown");
				}
				System.exit(0);
			}
			else if ( args[i].equalsIgnoreCase("-p") ) {
				this.profile = true;
			}
			else if ( args[i].equalsIgnoreCase("-totaltime") ) {
				this.totalTime = true;
			}
			else if ( args[i].equalsIgnoreCase("-addTypeInfo") ) {
				this.addTypeInfo = true;
				if ( i + 1 < args.length && args[i + 1].charAt(0) != '-' ) {
					// a path should follows
					typeInfoPath = meta.MetaHelper.removeQuotes(args[i + 1]);
					File ft = new File(typeInfoPath);
					if ( !ft.exists() || !ft.isDirectory() ) {
						error("Path '" + typeInfoPath
								+ "' given after option -addTypeInfo either does not exist or it is not a directory");
					}
					++i;
				}

			}
			else {
				error("unknown option: '" + args[i] + "'");
				return ret;
			}
			++i;
		}
		if ( !checkSetCyanLangDir() ) {
			return ret;
		}
		if ( projectDirectoryOrName == null ) {
			error("Missing project name");
			return ret;
		}

		return ret;
	}

	/**
	 * @param ret
	 */
	private boolean checkSetCyanLangDir() {
		if ( cyanLangDir == null ) {
			cyanLangDir = System.getenv("CYAN_HOME");
			if ( cyanLangDir == null ) {
				ChooseFoldersCyanInstallation.setEnvironmentVariables(true,
						true);
				cyanLangDir = System.getenv("CYAN_HOME");
				if ( cyanLangDir == null ) {
					error("Missing system environment variable 'CYAN_HOME'. Reinstall the Cyan compiler");
					return false;
				}
			}
			cyanLangDir = meta.MetaHelper.removeQuotes(cyanLangDir);
			File f = new File(cyanLangDir);
			if ( !f.exists() || !f.isDirectory() ) {
				ChooseFoldersCyanInstallation.setEnvironmentVariables(true,
						true);
				cyanLangDir = System.getenv("CYAN_HOME");
				f = new File(cyanLangDir);
				if ( !f.exists() || !f.isDirectory() ) {
					error("Directory '" + cyanLangDir + "' does not exist. '"
							+ cyanLangDir
							+ "' is the value of the System variable CYAN_HOME. Reinstall the Cyan compiler");
					return false;
				}
			}
		}
		if ( javaLibDir == null ) {
			javaLibDir = cyanLangDir;
		}
		return true;
	}

	private static void checkAST() {
		Saci.checkAST_ASTNode();
		Saci.checkMeta();
	}

	private static void checkAST_ASTNode() {

		try {
			Class<?>[] astClassList = Saci.getClasses("ast");
			for (Class<?> aClass : astClassList) {
				try {
					String className = aClass.getName();
					if ( !ASTNode.class.isAssignableFrom(aClass) ) {
						System.out.println("Class '" + className
								+ "' does not implement interface ASTNode");
					}
				}
				catch (Throwable e) {
					// e.printStackTrace();
					// Method ml[] = aClass.getDeclaredMethods();
					System.out.println(
							"Problems with class '" + aClass.getName() + "'");
				}
			}
		}
		catch (ClassNotFoundException | IOException e) {
			System.out.println("Error when retrieving 'ast' classes");
		}
	}

	private static void checkMeta() {

		try {
			Class<?>[] astClassList = Saci.getClasses("meta");
			for (Class<?> aClass : astClassList) {
				try {
					String className = aClass.getName();
					/*
					 * if ( className.contains("WrProject") ) { for ( Method m :
					 * aClass.getDeclaredMethods()) {
					 * System.out.println(className + "::" + m); } }
					 */
					if ( className.contains("GetHiddenItem")
							|| aClass.getPackage().getName()
									.contains("metaRealClasses") ) {
						continue;
					}

					for (Method m : aClass.getDeclaredMethods()) {
						int mod = m.getModifiers();
						boolean isPublic = java.lang.reflect.Modifier
								.isPublic(mod);
						boolean isPrivate = java.lang.reflect.Modifier
								.isPrivate(mod);
						boolean isProtected = java.lang.reflect.Modifier
								.isProtected(mod);
						String mName = m.getName();

						if ( mName.equals("getHidden") ) {
							continue;
						}
						/*
						 * if ( !isPublic && !isProtected ) { continue; }
						 */

						if ( mName.equals("visit") || mName.equals("preVisit")
								|| mName.equals("accept")
								|| mName.startsWith("cge_") ) {
							continue;
						}
						String packageName;
						if ( m.getReturnType() != null
								&& m.getReturnType().getPackage() != null ) {
							packageName = m.getReturnType().getPackage()
									.getName();

							if ( !packageName.startsWith("java.")
									&& !packageName.startsWith("meta")
									&& !packageName.startsWith("meta.cyanLang")
									&& !m.getReturnType().getCanonicalName()
											.startsWith("saci.Tuple")
									&& !m.getReturnType().getCanonicalName()
											.startsWith("List")
							// && m.getDeclaringClass() == aClass
							) {

								System.out.println("Class '" + aClass.getName()
										+ "' method: '" + m.getName() + "'"
										+ " return type: '"
										+ m.getReturnType().getCanonicalName()
										+ "' of package '" + packageName + "'");
							}
						}

						for (java.lang.reflect.Parameter p : m
								.getParameters()) {
							Class<?> paramType = p.getType();
							if ( paramType.getPackage() == null ) {
								continue;
							}
							packageName = paramType.getPackage().getName();
							Saci.checkPackageJavaOrMeta(aClass, m, p,
									packageName);
						}
					}

				}
				catch (Throwable e) {
					// e.printStackTrace();
					// Method ml[] = aClass.getDeclaredMethods();
					System.out.println("Methods of class '" + aClass.getName()
							+ "' were not found");
				}
			}
		}
		catch (ClassNotFoundException | IOException e) {
			System.out.println("Error when retrieving 'ast' classes");
		}
		System.exit(0);

	}

	/**
	 * @param aClass
	 * @param m
	 * @param p
	 * @param packageName
	 */
	private static void checkPackageJavaOrMeta(Class<?> aClass, Method m,
			java.lang.reflect.Parameter p, String packageName) {
		if ( !packageName.startsWith("java.") && !packageName.startsWith("meta")
				&& !packageName.startsWith("meta.cyanLang")
				&& !p.getType().getCanonicalName().startsWith("saci.Tuple")
		// && m.getDeclaringClass() == aClass
		) {
			System.out.println("Class '" + aClass.getName() + "' method: '"
					+ m.getName() + "'" + " parameter type: '"
					+ p.getType().getCanonicalName() + "' of package '"
					+ packageName + "'");
		}
	}

	/*
	 * taken from https://dzone.com/articles/get-all-classes-within-package
	 */
	private static Class<?>[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		List<Class<?>> classes = new ArrayList<>();
		for (File directory : dirs) {
			classes.addAll(Saci.findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	private static List<Class<?>> findClasses(File directory,
			String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<>();
		if ( !directory.exists() ) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if ( file.isDirectory() ) {
				assert !file.getName().contains(".");
				classes.addAll(Saci.findClasses(file,
						packageName + "." + file.getName()));
			}
			else if ( file.getName().endsWith(".class") ) {
				classes.add(Class.forName(packageName + '.' + file.getName()
						.substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public int run() {

		/*
		 * a static error list keeps all errors during compilation. It is
		 * necessary because there are compilation errors that simply disappear
		 * after they are signalled. That is, the compiler does not insert them
		 * in the list of errors --- not easy to fix because some errors should
		 * not be inserted in the list of errors although most should
		 */
		UnitError.initStaticErrorList();
		lastCompilationUnitParsed = null;
		codegNameWithCodegFile = null;

		int ret = 1;

		final PrintWriter printWriter = new PrintWriter(System.out, true);

		// MyFile projectFile = new MyFile(args[0]);
		program = new Program(addTypeInfo, typeInfoPath);
		addCyanLangLibToClassPath();

		errorList = new ArrayList<>();
		setErrorList = false;
		setProjectErrorList = false;

		TM.endTime("before parseProject");
		project = parseProject();
		TM.endTime("end parseProject");

		if ( project.hasErrors() ) {
			project.printErrorList(printWriter);
			return ret;
		}

		project.setCallJavac(callJavac);
		project.setExec(exec);
		project.setCmdLineArgs(cmdLineArgs);
		program.setProject(project);
		// project.setProgram(program);
		if ( javaLibDir == null ) {
			// javaLibDir = cyanLangDir + NameServer.fileSeparatorAsString +
			// "javalib";
			javaLibDir = cyanLangDir;
		}
		program.setJavaLibDir(cyanLangDir);
		program.setCyanLangDir(cyanLangDir);
		program.setClassPathList(classPathList);
		program.setSourcePathList(sourcePathList);

		TM.endTime("before loadJavaRuntimePackages");
		loadJavaRuntimePackages(printWriter);

		final HashMap<String, String> compilerOptions = new HashMap<>();

		// createJavaClasses(cyanLangDir);

		final CompilerManager compilerManager = new CompilerManager(project,
				program, printWriter, compilerOptions);
		project.setCompilerManager(compilerManager);

		TM.endTime("before loading the cyan.lang libraries");

		program.loadCompiledPackages();
		program.init();

		errorsHaveBeenPrinted = false;
		/*
		 * step 1
		 */
		compilerManager.setCompilationStep(CompilationStep.step_1);
		final HashSet<meta.CompilationInstruction> compInstSet = new HashSet<>();
		compInstSet.add(CompilationInstruction.parsing_actions);
		compInstSet.add(CompilationInstruction.pp_addCode);
		compInstSet.add(CompilationInstruction.parsing_originalSourceCode);

		Env env = new Env(project);

		/*
		 * read all sources from disk, create prototypes for files in --dsl
		 * directories and the compile
		 */

		TM.endTime("before first parsing");
		if ( !compilerManager.readSourceFiles()
				|| !compilerManager.compile(compInstSet) ) {
			printErrorList(printWriter, env);
		}
		else {
			/*
			 * step 2
			 */
			TM.endTime("after first parsing");
			compilerManager.setCompilationStep(CompilationStep.step_2);

			env = new Env(project);
			// initializes variables of {@link ast.Type}
			Saci.findBasicTypes(program.getProject(), env);
			if ( env.isThereWasError() ) {
				printErrorList(printWriter, env);
			}

			program.calcInterfaceTypes(env);

			TM.endTime("after resTypes");
			if ( env.isThereWasError() ) {
				printErrorList(printWriter, env);
			}
			else {

				/*
				 * step 3
				 */
				compilerManager.setCompilationStep(CompilationStep.step_3);

				compInstSet.clear();
				compInstSet.add(CompilationInstruction.afterResTypes_actions);
				env.setCompInstSet(compInstSet);
				if ( !program.afterResTypes_actions(env) ) {
					printErrorList(printWriter, env);
				}
				else {
					/*
					 * step 4
					 */
					TM.endTime("after AFTER_RES_TYPES actions");
					compilerManager.setCompilationStep(CompilationStep.step_4);

					compInstSet.clear();
					compInstSet.add(CompilationInstruction.parsing_actions);
					compInstSet.add(CompilationInstruction.new_addCode);
					/*
					 * reset the state of all compilation units so to clear any
					 * traces they have already been compiled
					 */
					program.reset();

					if ( !compilerManager.compile(compInstSet) ) {
						printErrorList(printWriter, env);
					}
					else {
						/*
						 * step 5
						 */

						TM.endTime("after second parsing");

						compilerManager
								.setCompilationStep(CompilationStep.step_5);

						compInstSet.clear();

						env = new Env(project);
						env.setCompInstSet(compInstSet);

						if ( !project.getCyanLangPackage()
								.getUseCompiledPrototypes() ) {
							Saci.findBasicTypes(program.getProject(), env);
						}

						program.calcInterfaceTypes(env);
						TM.endTime("after second resTypes");
						if ( env.isThereWasError() ) {
							printErrorList(printWriter, env);
						}
						else {
							/*
							 * step 6
							 */

							compilerManager
									.setCompilationStep(CompilationStep.step_6);
							compInstSet.clear();
							compInstSet
									.add(CompilationInstruction.semAn_actions);
							env.setCompInstSet(compInstSet);
							program.calcInternalTypes(env);
							TM.endTime("after SEM_AN");

							if ( env.isThereWasError() ) {
								printErrorList(printWriter, env);
							}
							else {

								// execute the SEM_AN actions

								env.semAn_actions();
								TM.endTime("after semAn_actions");

								/*
								 * step 7
								 */
								compilerManager.setCompilationStep(
										CompilationStep.step_7);

								compInstSet.clear();
								compInstSet.add(
										CompilationInstruction.inner_addCode);
								compInstSet.add(
										CompilationInstruction.createPrototypesForInterfaces);
								compInstSet.add(
										CompilationInstruction.pp_new_inner_addCode);

								program.resetNonGeneric();

								if ( !compilerManager.compile(compInstSet) ) {
									printErrorList(printWriter, env);
								}
								else {
									TM.endTime("after third parsing");

									/*
									 * step 8
									 */
									compilerManager.setCompilationStep(
											CompilationStep.step_8);

									compInstSet.clear();
									compInstSet.add(
											CompilationInstruction.ati3_check);

									env = new Env(project);
									env.setCompInstSet(compInstSet);

									if ( !project.getCyanLangPackage()
											.getUseCompiledPrototypes() ) {
										Saci.findBasicTypes(
												program.getProject(), env);
									}
									program.calcInterfaceTypes(env);

									TM.endTime("after second resTypes");

									if ( env.isThereWasError() ) {
										printErrorList(printWriter, env);
									}
									else {
										/*
										 * step 9
										 */
										compilerManager.setCompilationStep(
												CompilationStep.step_9);

										compInstSet.clear();
										compInstSet.add(
												CompilationInstruction.matchExpectedCompilationErrors);
										compInstSet.add(
												CompilationInstruction.semAn_check);
										env.setCompInstSet(compInstSet);

										program.calcInternalTypes(env);
										TM.endTime("after third resTypes");

										if ( env.isThereWasError() ) {
											printErrorList(printWriter, env);
										}
										else {
											program.afterSemAn_check(env);
											TM.endTime(
													"after afterSemAn_check");

											/**
											 * check if any metaobject that
											 * implements interface
											 * 'IInformCompilationError' pointed
											 * some error that was not signaled
											 * in this compilation
											 */
											program.checkErrorMessagesAllCompilationUnits(
													env);

											/*
											 * step 10
											 */
											compilerManager.setCompilationStep(
													CompilationStep.step_10);
											// program.genJava(env);

											compInstSet.clear();
											env.setCompInstSet(compInstSet);
											program.genJava(env);

											TM.endTime("after genJava");

											if ( !env.isThereWasError() ) {
												/*
												 * save to each package
												 * directory information saying
												 * the package was compiled
												 * Successfully by this compiler
												 * version
												 */
												compilerManager
														.saveCompilationInfoPackages(
																env);
											}
											if ( env.isThereWasError() ) {
												printErrorList(printWriter,
														env);
											}
											else {
												/*
												 * if there was errors but they
												 * were not in the 'env' list,
												 * print them here. This is only
												 * necessary because there are
												 * errors in the compiler
												 */
												if ( UnitError
														.isThereWasError() ) {
													UnitError
															.printStaticErrorList(
																	printWriter);
												}

												program.compileGeneratedJavaCode(
														env);
												if ( env.isThereWasError() ) {
													printErrorList(printWriter,
															env);
												}
												else {
													ret = 0;
												}
											}
											// MyFile.write(env.searchPackagePrototype("main",
											// "Program").getCompilationUnit());

										}
									}
								}
							}
						}
					}
				}
			}

		}
		program.writePrototypesToFile(env);

		if ( env.isThereWasError() && !this.errorsHaveBeenPrinted ) {
			printErrorList(printWriter, env);
		}
		else {
			if ( !this.errorsHaveBeenPrinted ) {
				if ( UnitError.isThereWasError()
						&& !UnitError.wasStaticErrorListPrinted() ) {
					UnitError.printStaticErrorList(printWriter);
				}
			}
		}
		setErrorList();
		printWriter.flush();
		printWriter.close();
		return ret;
	}

	private boolean alreadyLoadedJavaRuntimePackages = false;

	/**
	 * load Java runtime Packages
	 *
	 * @param printWriter
	 */
	private void loadJavaRuntimePackages(final PrintWriter printWriter) {
		/*
		 * load Java classes
		 */
		Saci.javaHome = System.getenv(NameServer.JAVA_HOME_FOR_CYAN);
		if ( Saci.javaHome == null ) {
			ChooseFoldersCyanInstallation.setEnvironmentVariables(true, true);
			Saci.javaHome = System.getenv(NameServer.JAVA_HOME_FOR_CYAN);
			if ( Saci.javaHome == null ) {

				File jdk = new File("C:\\Program Files\\Java");
				if ( jdk.exists() && jdk.isDirectory() ) {
					String jdk18 = "jdk1.8.0_";
					File updated_jdk = null;
					String updated_jdk_str = null;
					int updated_version = -1;
					for (File f : jdk.listFiles()) {
						String dirName = f.getName();
						if ( dirName.startsWith(jdk18) ) {
							if ( updated_jdk == null ) {
								updated_jdk = f;
								updated_jdk_str = dirName.substring(
										jdk18.length(), dirName.length());
								try {
									updated_version = Integer
											.parseInt(updated_jdk_str);
								}
								catch (NumberFormatException e) {
									updated_version = -1;
									updated_jdk = null;
									updated_jdk_str = null;
								}
							}
							else {
								String last_jdk_str = dirName.substring(
										jdk18.length(), dirName.length());
								int last_version = -1;
								try {
									last_version = Integer
											.parseInt(last_jdk_str);
								}
								catch (NumberFormatException e) {
								}
								if ( last_version > updated_version ) {
									updated_jdk = f;
									updated_jdk_str = last_jdk_str;
								}
							}
						}
					}
					if ( updated_jdk != null ) {
						try {
							Saci.javaHome = updated_jdk.getCanonicalPath();
						}
						catch (IOException e) {
							Saci.javaHome = null;
						}
					}

				}
				if ( Saci.javaHome == null ) {
					System.out.println("Set environment variable "
							+ NameServer.JAVA_HOME_FOR_CYAN
							+ ". For this time only, the compiler will use variable java.home instead");
					Saci.javaHome = System.getenv("java.home");
					if ( Saci.javaHome == null ) {
						error("The environment variable 'java.home' was not found");
						printErrorList(printWriter, new Env(project));
						return;
					}
				}
			}
		}
		Saci.javaHome = meta.MetaHelper.removeQuotes(Saci.javaHome);
		if ( !alreadyLoadedJavaRuntimePackages && mayImportJavaPackages ) {

			// String javaHome2 = System.getProperty("sun.boot.class.path");
			// System.out.println("javaHome2 = " + javaHome2);
			String rtPath = Saci.javaHome + NameServer.fileSeparatorAsString
					+ "jre" + NameServer.fileSeparatorAsString + "lib"
					+ NameServer.fileSeparatorAsString + "rt.jar";
			classPathList.add(rtPath);

			// FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
			// Path objClassFilePath = fs.getPath("modules", "java.base",
			// "java/lang/Object.class");

			final List<String> errorMessageList = program
					.loadJavaPackages(this.classPathList);
			if ( errorMessageList != null && errorMessageList.size() > 0 ) {
				for (final String errorMessage : errorMessageList) {
					error(errorMessage);
				}
				printErrorList(printWriter, new Env(project));
			}
			else {
				alreadyLoadedJavaRuntimePackages = true;
			}
		}
	}

	/**
	 * add directory cyanLangDir and cyanLangdir\javalib to class path of Java
	 */
	public void addCyanLangLibToClassPath() {
		LoadUtil.addJarDirToClassPath(
				new String[] { cyanLangDir,
						cyanLangDir + File.separator + "javalib" },
				null, new String[] {
						this.cyanLangDir + File.separator + "saci.jar" });

	}

	public static boolean addPath(String s, Function0 inError) {
		File f = new File(s);
		URI u = f.toURI();
		try (URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader()) {
			Class<URLClassLoader> urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL",
					new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[] { u.toURL() });
		}
		catch (Exception e) {
			inError.eval();
			return false;
		}
		return true;

	}

	public List<ProjectError> getProjectErrorList() {
		if ( !setProjectErrorList ) {
			if ( project.hasErrors() ) {
				projectErrorList.addAll(project.getProjectErrorList());
			}
			setProjectErrorList = true;
		}
		return this.projectErrorList;
	}

	public List<UnitError> getCyanErrorList() {
		if ( !this.setErrorList ) {
			setErrorList();
		}
		return errorList;
	}

	/**
	
	 */
	private void setErrorList() {
		for (final CompilationUnit compUnit : program
				.getCompilationUnitList()) {
			if ( compUnit.getErrorList() != null ) {
				errorList.addAll(compUnit.getErrorList());
			}
		}
		if ( errorList.size() == 0 && UnitError.isThereWasError() ) {
			errorList.addAll(UnitError.getStaticErrorList());
		}
		setErrorList = true;
	}

	private void printErrorList(PrintWriter printWriter, Env env) {

		if ( !this.compilerCalledFromCommandLine ) {
			return;
		}

		errorsHaveBeenPrinted = true;
		int i = 0;
		for (final CompilationUnit compUnit : program
				.getCompilationUnitList()) {
			if ( compUnit.hasCompilationError() ) {
				compUnit.printErrorList(printWriter);
			}
			++i;
			if ( env != null ) {
				int numFalse = 0;
				for (final Tuple3<Integer, String, Boolean> t : compUnit
						.getLineMessageList()) {
					if ( !t.f3 ) {
						++numFalse;
					}
				}

				if ( numFalse == compUnit.getLineMessageList().size() ) {
					for (final Tuple3<Integer, String, Boolean> t : compUnit
							.getLineMessageList()) {
						if ( !t.f3 ) {
							try {
								env.error(null,
										"A metaobject implementing interface 'IInformCompilationError' points an error at line "
												+ t.f1 + " of file '"
												+ compUnit.getFullFileNamePath()
												+ "' with message '" + t.f2
												+ "' although this error is not signaled by the compiler."
												+ " If the error really is in the source code, what happens was similar to the following: there is a parser error and "
												+ "a semantic error in this source code, each pointed by a metaobject. This error was caused by the semantic error. This error"
												+ " would be pointed by the compiler in a later compiler phase. However, it will not because the compilation will stop because "
												+ "of the parsing error",
										false, true);
							}
							catch (final CompileErrorException e) {
							}
						}
					}

				}

			}
			if ( compUnit.getActionList().size() > 0 ) {
				compUnit.doActionList(printWriter);
			}
		}
		if ( project.getProjectErrorList() != null ) {
			for (final ProjectError pe : project.getProjectErrorList()) {
				pe.print(printWriter);
			}
		}
		if ( project.getProjectCompilationUnit().getErrorList() != null ) {
			project.getProjectCompilationUnit().printErrorList(printWriter);
		}
	}

	/**
	 * Initializes public static variables of class {@link ast.Type} with the
	 * correct prototypes of the program
	 *
	 * @param project
	 * @param env
	 */
	private static void findBasicTypes(Project project, Env env) {
		boolean found = false;
		for (final CyanPackage aPackage : project.getPackageList()) {
			if ( aPackage.getPackageName().compareTo("cyan.lang") == 0 ) {
				found = true;
				for (final CompilationUnit compilationUnit : aPackage
						.getCompilationUnitList()) {
					for (final Prototype prototype : compilationUnit
							.getPrototypeList()) {
						switch (prototype.getName()) {
						case "Byte":
							Type.Byte = prototype;
							WrType.Byte = WrPrototype.factory(prototype);
							break;
						case "Short":
							Type.Short = prototype;
							WrType.Short = WrPrototype.factory(prototype);
							break;
						case "Int":
							Type.Int = prototype;
							WrType.Int = WrPrototype.factory(prototype);
							break;
						case "Long":
							Type.Long = prototype;
							WrType.Long = WrPrototype.factory(prototype);
							break;
						case "Float":
							Type.Float = prototype;
							WrType.Float = WrPrototype.factory(prototype);
							break;
						case "Double":
							Type.Double = prototype;
							WrType.Double = WrPrototype.factory(prototype);
							break;
						case "Char":
							Type.Char = prototype;
							WrType.Char = WrPrototype.factory(prototype);
							break;
						case "Boolean":
							Type.Boolean = prototype;
							WrType.Boolean = WrPrototype.factory(prototype);
							break;
						// case "CySymbol" : Type.CySymbol = prototype; break;
						case "String":
							Type.String = prototype;
							WrType.String = WrPrototype.factory(prototype);
							break;
						case "Any":
							Type.Any = prototype;
							WrType.Any = WrPrototype.factory(prototype);
							break;
						case "Nil":
							Type.Nil = prototype;
							WrType.Nil = WrPrototype.factory(prototype);
							break;

						}
						// if ( prototype instanceof ObjectDec )
						// ((ObjectDec ) prototype).addSpecificMethods(env);
					}
				}
			}
		}
		WrType.Dyn = new WrTypeDynamic((TypeDynamic) Type.Dyn);

		if ( !found ) {
			env.error(null, "Package 'cyan.lang' was not found", true, true);
		}
		if ( Type.Byte == null || Type.Short == null || Type.Int == null
				|| Type.Long == null || Type.Float == null
				|| Type.Double == null || Type.Char == null
				|| Type.Boolean == null ||
				// Type.CySymbol == null ||
				Type.String == null || Type.Nil == null ) {
			env.error(null,
					"One of the basic prototypes (Byte, Int, String etc) was not found",
					true, true);
		}

	}

	void error(String str) {
		if ( this.compilerCalledFromCommandLine ) {
			System.out.println(str);
			System.exit(1);
		}
		else {
			program.getProject().error(str);
		}
	}

	/**
	 * recursively delete all directories with name "--tmp"
	 * (NameServer.temporaryDirName) from directory directoryPath
	 *
	 * @param directoryPath
	 * @return
	 */
	public static boolean deleteAllTmpDirectories(String directoryPath) {

		try {
			final File currentDir = new File(directoryPath);
			if ( currentDir.isDirectory() ) {
				if ( directoryPath
						.equalsIgnoreCase(NameServer.temporaryDirName) ) {
					currentDir.delete();
				}
				else {
					for (final File f : currentDir.listFiles()) {
						if ( f.isDirectory() ) {
							if ( f.getName().equalsIgnoreCase(
									NameServer.temporaryDirName) ) {
								if ( !MyFile.deleteFileDirectory(f) ) {
									System.out.println("Cannot delele "
											+ f.getCanonicalPath()
											+ " or some of its files");
								}
							}
							else {
								Saci.deleteAllTmpDirectories(
										f.getCanonicalPath());
							}
						}
					}
				}
			}

		}
		catch (final IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * return the new project and a string with an error message
	 */

	private Project parseProject() {

		/*
		 * delete all files of directory "cyanLangDir\tmp"
		 */
		// deleteAllTmpDirectories(cyanLangDir);

		if ( projectDirectoryOrName.endsWith(File.separator) ) {
			projectDirectoryOrName = projectDirectoryOrName.substring(0,
					projectDirectoryOrName.length() - 1);
		}
		String projectCanonicalPath = projectDirectoryOrName
				+ NameServer.fileSeparatorAsString;

		File projectFile = new File(projectDirectoryOrName);

		if ( !projectFile.exists() ) {
			if ( projectDirectoryOrName.endsWith("\"") ) {
				final Project newProject = new Project(program);
				error("The project filename or .pyan filename '"
						+ projectDirectoryOrName
						+ "' ends with \". Remove the \\ at the end of the directory name");
				return newProject;
			}
			if ( MyFile.isRelativePath(projectDirectoryOrName) ) {
				String curDir; // = System.getProperty("user.home");
				// curDir = new File("").getAbsolutePath();
				curDir = Paths.get(".").toAbsolutePath().normalize().toString();
				if ( !curDir.endsWith(File.separator) ) {
					curDir += File.separator;
				}
				projectDirectoryOrName = curDir + projectDirectoryOrName;
				projectFile = new File(projectDirectoryOrName);
				if ( !projectFile.exists() ) {
					final Project newProject = new Project(program);
					error("Name '" + projectDirectoryOrName
							+ "' should be either a directory of a program or a file ending with '.pyan'. But it does not exist");
					return newProject;
				}
			}
			else {
				final Project newProject = new Project(program);
				error("Name '" + projectDirectoryOrName
						+ "' should be either a directory of a program or a file ending with '.pyan'. But it does not exist");
				return newProject;
			}
		}

		String pyanFilename = projectDirectoryOrName;
		String fullPyanFilename;
		if ( !projectFile.isDirectory() ) {
			/*
			 * projectDirectoryOrName is really the name of a project ending in
			 * ".pyan"
			 */
			fullPyanFilename = pyanFilename;
			final File f = new File(projectDirectoryOrName);
			pyanFilename = f.getName();
			if ( !pyanFilename.endsWith(".pyan") ) {
				final Project newProject = new Project(program);
				error("'projectDirectoryOrName' should be the project file of a program. It should end with '.pyan'");
				return newProject;
			}
			projectCanonicalPath = "";
			try {
				projectCanonicalPath = f.getParentFile().getCanonicalPath()
						+ NameServer.fileSeparatorAsString;
			}
			catch (final IOException e) {
				final Project newProject = new Project(program);
				error("Error handling " + fullPyanFilename);
				return newProject;
			}
		}
		else {
			/*
			 * projectDirectoryOrName is the name of a directory. Create the
			 * '.pyan' file
			 */
			// delete all "--tmp" directories recursivelly
			Saci.deleteAllTmpDirectories(projectDirectoryOrName);
			// dreate the projet file.
			if ( !createProjectFile(projectDirectoryOrName, "", "", "", "") ) {
				final Project newProject = new Project(program);
				error("Error when creating project of directory or with file name '"
						+ projectDirectoryOrName + "'");
				return newProject;
			}
			pyanFilename = "project." + MetaHelper.pyanSourceFileExtension;
			fullPyanFilename = projectCanonicalPath + pyanFilename;
		}

		final MyFile myCyanProjectFile = new MyFile(fullPyanFilename);
		final char[] projectText = myCyanProjectFile.readFile();
		if ( projectText == null
				|| myCyanProjectFile.getError() != MyFile.ok_e ) {

			final Project newProject = new Project(program);
			error("Error opening/reading file " + fullPyanFilename);
			return newProject;
		}

		final CompilationUnitSuper projectCompilationUnit = new CompilationUnit(
				pyanFilename, projectCanonicalPath, null, null);
		projectCompilationUnit.readSourceFile();
		final HashSet<CompilationInstruction> compInstSet = new HashSet<>();
		compInstSet.add(CompilationInstruction.pyanSourceCode);
		final Compiler pc = new Compiler(projectCompilationUnit, compInstSet,
				CompilationStep.step_1, null, null);

		// name of the executable file, a file that calls the Java interpreter
		// after a
		// successful compilation
		String execFileName;

		String tmpPath = projectCanonicalPath;
		if ( tmpPath.endsWith(NameServer.fileSeparatorAsString) ) {
			tmpPath = tmpPath.substring(0, tmpPath.length() - 1);
		}

		final int indexLastSlash = tmpPath
				.lastIndexOf(NameServer.fileSeparator);
		if ( indexLastSlash > 0 ) {
			execFileName = tmpPath.substring(indexLastSlash + 1);
		}
		else {
			execFileName = tmpPath;
		}
		if ( NameServer.fileSeparator == '\\' ) {
			// should be in Windows
			execFileName += ".cmd";
		}

		final Project newProject = new Project(program, projectCanonicalPath,
				execFileName);
		newProject.setProjectCompilationUnit(projectCompilationUnit);
		try {
			pc.parseProject(newProject, program, projectCompilationUnit,
					pyanFilename, projectCanonicalPath, projectText,
					cyanLangDir);
		}
		catch (final Exception e) {

			final Project newProject2 = new Project(program);
			String msg = "Internal error when reading a project file or creating one. File '"
					+ projectDirectoryOrName + "'. "
					+ "The exception thrown was "
					+ e.getClass().getSimpleName();
			if ( e instanceof NullPointerException ) {
				NullPointerException npe = (NullPointerException) e;
				msg += " The stack trace is:\r\n";
				for (StackTraceElement st : npe.getStackTrace()) {
					msg += "   " + st.getMethodName();
				}
			}
			error(msg);
			return newProject2;

		}

		newProject.setText(projectText);
		final int indexDot = pyanFilename.indexOf('.');
		if ( indexDot >= 0 ) {
			newProject.setProjectName(pyanFilename.substring(0, indexDot));
		}
		else {
			final Project newProject2 = new Project(program);
			error("Internal error: project name does not have a '.'. File '"
					+ projectDirectoryOrName + "'");
			return newProject2;

		}

		if ( projectCompilationUnit.getErrorList() != null
				&& projectCompilationUnit.getErrorList().size() > 0 ) {
			for (final UnitError anError : projectCompilationUnit
					.getErrorList()) {

				final StringBuffer s = new StringBuffer();
				if ( anError.getFilename() != null ) {
					s.append("In project file '" + anError.getFilename()
							+ "' (line " + anError.getLineNumber() + " column "
							+ anError.getColumnNumber() + ") \n");
				}
				s.append(anError.getMessage() + "\n");
				if ( anError.getLine() != null ) {
					s.append(anError.getLine() + "\n");
				}

				newProject.error(s.toString());
			}
		}

		return newProject;
	}

	/**
	 * create a project file with name "project.pyan" in directory projDirName.
	 */
	public boolean createProjectFile(String dirName, String author,
			String options, String mainPackage, String mainObject) {

		// fileSeparator = System.getProperty("file.separator");
		final File projDir = new File(dirName);

		String canPathOfTheProject = null;
		PrintWriter outp = null;
		try {
			canPathOfTheProject = projDir.getCanonicalPath();
			final String cyanpFilePath = canPathOfTheProject
					+ NameServer.fileSeparator + "project.pyan";

			outp = new PrintWriter(cyanpFilePath);

			if ( author != null && author.length() != 0 ) {
				outp.print("@author(\"" + author + "\") ");
			}
			if ( options != null && options.length() != 0 ) {
				outp.print("@options(\"" + options + "\") ");
			}

			outp.print("program");
			if ( dirName != null && dirName.length() != 0 ) {
				outp.println(" at \"" + dirName + "\"");
			}
			outp.println("");

			if ( mainObject != null && mainPackage != null
					&& mainObject.length() != 0 && mainPackage.length() != 0 ) {
				outp.println("    " + mainPackage + "." + mainObject);
			}

			final List<String> projPath = new ArrayList<>();
			projPath.add(canPathOfTheProject);
			final List<String> projCyanName = new ArrayList<>();
			projCyanName.add("");
			String strError = Saci.getAllProjects(projPath, projCyanName, 0);
			if ( strError != null ) {
				this.error(strError);
				return false;
			}
			strError = Saci.generatePackageSourceList(projPath, projCyanName,
					outp);
			if ( strError != null ) {
				this.error(strError);
				return false;
			}

			outp.close();
		}
		catch (final IOException e) {
			error("Can´t write to file " + canPathOfTheProject);
			return false;
		}
		finally {
			if ( outp != null ) {
				outp.close();
			}
		}
		return true;
	}

	/**
	 * add to projPath all sub-directories of every sub-directory of the paths
	 * of projPath. Add to projCyanName the name of the Cyan name of the
	 * corresponding package.
	 *
	 * @param projPath,
	 *            a list of directories
	 * @param projCyanName,
	 *            a list of project names, ending with '.pyan', of the
	 *            directories of projPath. Or "" if none.
	 * @param start,
	 *            index of projPath to start processing
	 * @return
	 */
	public static String getAllProjects(List<String> projPath,
			List<String> projCyanName, int start) {

		final int size = projPath.size();
		int i;
		for (i = start; i < size; i++) {
			final String s = projPath.get(i);
			final String projectName = projCyanName.get(i);
			final File f = new File(s);
			if ( f.isDirectory() ) {
				final String[] subDirList = f.list();
				for (final String p : subDirList) {
					if ( !p.startsWith(MetaHelper.prefixNonPackageDir) ) {
						final File g = new File(
								s + NameServer.fileSeparator + p);
						if ( g.isDirectory() ) {
							try {
								projPath.add(g.getCanonicalPath());
							}
							catch (final IOException e) {
								return "error in handling file " + p;
							}
							if ( projectName.length() == 0 ) {
								projCyanName.add(p);
							}
							else {
								projCyanName.add(projectName + "." + p);
							}
						}
					}
				}
			}
		}
		if ( projPath.size() > size ) {
			return Saci.getAllProjects(projPath, projCyanName, size);
		}
		return null;
	}

	/**
	 *
	 *
	 * @param projPath
	 * @param projCyanName
	 * @param outp
	 */
	public static String generatePackageSourceList(List<String> projPath,
			List<String> projCyanName, PrintWriter outp) {

		for (int i = 1; i < projPath.size(); i++) {
			final File projFile = new File(projPath.get(i));
			String canProjFileName = null;
			try {
				canProjFileName = projFile.getCanonicalPath();
			}
			catch (final IOException e) {
				return "error handling file " + projFile.getName();
			}
			int numCyanSourceFilesFoundInPackage = 0;
			if ( !projFile.exists() ) {
				return "File " + projPath.get(i) + " does not exist";
			}
			final String cyanSource[] = projFile.list();
			for (final String source : cyanSource) {
				final String canCyanSource = canProjFileName
						+ NameServer.fileSeparator + source;
				final File f = new File(canCyanSource);
				if ( source.endsWith(".cyan") && !f.isDirectory() ) {
					++numCyanSourceFilesFoundInPackage;
					if ( numCyanSourceFilesFoundInPackage == 1 ) {
						outp.println("    package " + projCyanName.get(i)
								+ " at \"" + canProjFileName + "\"");
					}
				}
			}
		}
		return null;
	}

	static void printText(String message, char[] text, int size) {
		System.out.println("*** " + message + "****");
		for (int i = 0; i < size; ++i) {
			if ( text[i] == '\n' ) {
				System.out.print("\\n");
			}
			else if ( text[i] == '\r' ) {
				System.out.print("\\r");
			}
			else if ( text[i] == '\0' ) {
				System.out.println(
						"**********************  Found \\0 at index " + i);
			}
			else {
				System.out.print(text[i]);
			}
		}
		System.out.println("*** end ****");
	}

	/**
	 * for each compilation unit there is a list of changes to be made which may
	 * be:
	 * <ul>
	 * <li>change the metaobject annotation from something like
	 * "<code>{@literal @}annotation(1)</code>" to
	 * <code>"{@literal @}annotation#afterResTypes(1)"</code>. That is, add a
	 * suffix or change a suffix;
	 * <li>add text to the compilation unit. It may be fields, methods, or code
	 * after a metaobject annotation;
	 * <li>delete text. Macro calls and literal objects such as
	 * <code>101bin</code> and <code>{@literal @}graph{% 1:2 %}</code> are
	 * always removed from the source code in phase SEM_AN.
	 * </ul>
	 * <p>
	 * These changes may affect each other because each one should be made at a
	 * pre-defined position in the text of the compilation unit and this
	 * position changes after each insertion or deletion of code. For example,
	 * suppose metaobject annotation "<code>{@literal @}annotation(1)</code>" is
	 * attached to a prototype <code>Test</code> of file "Test.cyan". This
	 * metaobject annotation adds a fields "iv0001" and "c0001" to this
	 * prototype at phase AFTER_RES_TYPES. Before doing any changes in
	 * "Test.cyan", method {@link #makeChange} calculates the positions in the
	 * file where the changes will be made. Assume
	 * "<code>{@literal @}annotation(1)</code>" is at position 45, the field
	 * "iv0001" should be inserted at position 150 and the field "c0001" should
	 * be added at position 150 too. <br>
	 * Suppose now that {@link #makeChange} first changes
	 * "<code>{@literal @}annotation(1)</code>" to
	 * <code>"{@literal @}annotation#afterResTypes(1)"</code>. Four characters
	 * were inserted and the positions where the fields should be inserted
	 * should be changed to 154. If the code added to the field "iv0001" is
	 * <code>"Int iv0001\n"</code>, now the other field should be added at
	 * position 161. The size of <code>"Int iv0001\n"</code> is 11 characters.
	 *
	 * The code below sorts the changes of each compilation unit by offset and
	 * then apply the changes.
	 */
	public static void makeChanges(
			HashMap<CompilationUnitSuper, List<SourceCodeChangeByAnnotation>> setOfChanges,
			Env env, CompilationPhase compilationPhase) {

		for (final Map.Entry<CompilationUnitSuper, List<SourceCodeChangeByAnnotation>> entry : setOfChanges
				.entrySet()) {

			final List<SourceCodeChangeByAnnotation> changeList = entry
					.getValue();
			Collections.sort(changeList);
			final CompilationUnitSuper eachCompUnit = entry.getKey();

			if ( compilationPhase == CompilationPhase.afterResTypes ) {
				eachCompUnit.setSourceCodeChanged_inPhaseafterResTypes(true);
			}
			else {
				eachCompUnit.setSourceCodeChanged_inPhasesemAn(true);
			}
			// eachCompUnit.setSourceCodeChanged(true);

			final char[] text = eachCompUnit.getText();

			// printText("original", text, text.length);
			int shiftSizeText = 0;
			for (final SourceCodeChangeByAnnotation change : changeList) {
				shiftSizeText += change.getSizeToAdd();
				shiftSizeText -= change.getSizeToDelete();

				/*
				 * Annotation annot = change.getCyanAnnotation(); if ( annot !=
				 * null ) { CompilationUnitSuper cuUnitSuper =
				 * annot.getCompilationUnit(); if ( cuUnitSuper instanceof
				 * CompilationUnit && eachCompUnit instanceof CompilationUnit )
				 * { if ( !
				 * cuUnitSuper.getFullFileNamePath().equals(eachCompUnit.
				 * getFullFileNamePath()) ) {
				 * env.error(change.getCyanAnnotation().getFirstSymbol(),
				 * "This annotation is in file '" +
				 * cuUnitSuper.getFullFileNamePath() + "' but it is trying " +
				 * "to change file '" + eachCompUnit.getFullFileNamePath() +
				 * "'. This is illegal. " +
				 * "The metaobject of an annotation can only change the prototype it is in"
				 * ); } } }
				 */
			}

			final int newTextSize = text.length + shiftSizeText;
			final char[] newText = new char[newTextSize];
			int nextStop;
			int i = 0;
			int p = 0;
			int j = 0;

			if ( changeList.size() > 0 ) {
				// int shiftOffsetChange = 0;
				while (true) {
					if ( i < changeList.size() ) {
						nextStop = changeList.get(i).offset;
					}
					else {
						/*
						 * copy the characters after the last change
						 */
						while (p < text.length) {
							newText[j] = text[p];
							++j;
							++p;
						}
						break;
					}
					/*
					 * copy the characters till the next change
					 */
					while (p < nextStop) {
						newText[j] = text[p];
						++j;
						++p;
					}
					// printText("after copy", newText, j);

					final int useInError = i;
					/*
					 * The changes of one specific offset (nextStop) should
					 * consist of at most one delete and any number of text
					 * additions
					 */
					int numberOfDeletionsOperations = 0;
					while (i < changeList.size()
							&& changeList.get(i).offset == nextStop) {
						final int sizeToDelete = changeList.get(i)
								.getSizeToDelete();
						if ( sizeToDelete > 0 ) {
							++numberOfDeletionsOperations;
							if ( numberOfDeletionsOperations > 1 ) {
								String all = "[";
								while (useInError < changeList.size()
										&& changeList.get(
												useInError).offset == nextStop) {
									if ( changeList.get(useInError)
											.getCyanAnnotation() != null ) {
										final Annotation annotation = changeList
												.get(useInError)
												.getCyanAnnotation();
										all = "(name: "
												+ annotation.getCyanMetaobject()
														.getName()
												+ " package of annotation: "
												+ annotation
														.getPackageOfAnnotation()
												+ " prototype of annotation: "
												+ annotation
														.getPrototypeOfAnnotation()
												+ " line number of annotation: "
												+ annotation.getFirstSymbol()
														.getLineNumber()
												+ ") ";

									}
								}
								all = "]\n";
								env.error(null,
										"Two or more metaobjects of the following list are trying to delete code in exactly the same offset in file "
												+ "'"
												+ eachCompUnit
														.getFullFileNamePath()
												+ "'. " + all);
							}
						}
						p += sizeToDelete;
						final StringBuffer textToAdd = changeList.get(i)
								.getTextToAdd();
						if ( textToAdd != null ) {
							// copy text to add to newText
							for (int k = 0; k < textToAdd.length(); ++k) {
								newText[j] = textToAdd.charAt(k);
								++j;
							}
						}
						++i;
						// printText("after adding/removing", newText, j);
					}

				}
				eachCompUnit.setText(newText);
				/*
				 * if ( eachCompUnit.getFullFileNamePath().contains("Program") )
				 * { printText("Program", newText, newText.length); }
				 */

			}

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see saci.ISaci#getProject()
	 */
	public Project getProject() {
		return project;
	}

	public Symbol[] getSymbolList() {
		return symbolList;
	}

	public int getSizeSymbolList() {
		return sizeSymbolList;
	}

	public List<AnnotationAt> getCodegList() {
		return this.codegList;
	}

	public CompilationUnit getLastCompilationUnitParsed() {
		return lastCompilationUnitParsed;
	}

	public List<ICodeg> getOtherCodegList() {
		return otherCodegList;
	}

	public void setOtherCodegList(List<ICodeg> otherCodegList) {
		this.otherCodegList = otherCodegList;
	}

	/**
	 * make the metaobject annotations of this program unit communicate with
	 * each other any prototype whose name has a '<' in it, as
	 * <code>Set{@literal <}Int></code>, cannot communicate with other
	 * prototypes
	 */
	protected void makeCodegsCommunicateInPrototype() {

		// In generic prototypes, Codegs do not communicate with each other
		if ( prototypeName.contains("(") ) {
			return;
		}
		/*
		 * every metaobject can supply information to other metaobjects. Every
		 * tuple in this set correspond to a metaobject annotation. Every tuple
		 * is composed of a metaobject name, the number of this metaobject
		 * considering all metaobjects in the prototype, the number of this
		 * metaobject considering only the metaobjects with the same name, and
		 * the information this metaobject annotation wants to share with other
		 * metaobject annotations.
		 */
		final HashSet<Tuple4<String, Integer, Integer, Object>> moInfoSet = new HashSet<>();
		for (final AnnotationAt annotation : codegList) {
			final CyanMetaobjectAtAnnot cyanMetaobject = annotation
					.getCyanMetaobject();
			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			if ( cyanMetaobject instanceof ICommunicateInPrototype_ded
					|| (other != null
							&& other instanceof _ICommunicateInPrototype__ded) ) {
				Object sharedInfo;
				if ( other == null ) {
					sharedInfo = ((ICommunicateInPrototype_ded) cyanMetaobject)
							.ded_shareInfoPrototype();
				}
				else {
					sharedInfo = ((_ICommunicateInPrototype__ded) other)
							._ded__shareInfoPrototype();
				}
				if ( sharedInfo != null ) {

					final Tuple4<String, Integer, Integer, Object> t = new Tuple4<>(
							cyanMetaobject.getName(),
							annotation.getAnnotationNumber(),
							annotation.getAnnotationNumberByKind(), sharedInfo);
					moInfoSet.add(t);
				}

			}
		}
		/*
		 * send information to all annotations of this program unit. Let them
		 * communicate with each other
		 */
		if ( moInfoSet.size() > 0 ) {
			for (final AnnotationAt annotation : this.codegList) {
				final CyanMetaobjectAtAnnot cyanMetaobject = annotation
						.getCyanMetaobject();
				_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
				if ( cyanMetaobject instanceof ICommunicateInPrototype_ded
						|| (other != null
								&& other instanceof _ICommunicateInPrototype__ded) ) {

					if ( other == null ) {
						((ICommunicateInPrototype_ded) cyanMetaobject)
								.ded_receiveInfoPrototype(moInfoSet);
					}
					else {
						_Set_LT_GP__Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT_GT tupleSet = new _Set_LT_GP__Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT_GT();

						for (Tuple4<String, Integer, Integer, Object> elem : moInfoSet) {
							tupleSet._add_1(
									new _Tuple_LT_GP_CyString_GP_CyInt_GP_CyInt_GP_Object_GT(
											new CyString(elem.f1),
											new CyInt(elem.f2),
											new CyInt(elem.f3), elem.f4));
						}
						((_ICommunicateInPrototype__ded) other)
								._ded__receiveInfoPrototype_1(tupleSet);
					}

				}
			}
		}
	}

	public void generateCyanPrototypesForMOP() {
		checkSetCyanLangDir();
		for (Class<?> aClass : new Class<?>[] { CyanMetaobject.class,
				CyanMetaobjectAtAnnot.class, CyanMetaobjectLiteralObject.class,
				CyanMetaobjectMacro.class, CyanMetaobjectLiteralObjectSeq.class,
				CyanMetaobjectLiteralString.class,
				CyanMetaobjectNumber.class }) {
			this.generateEquivalentCyanPrototype(aClass);
		}
	}

	private static String getBasicTypeJavaName(String typename) {
		Class<?> aClass = NameServer.javaPrimitiveTypeToWrapperClass(typename);
		if ( aClass == null ) {
			if ( typename.equals("StringBuffer")
					|| typename.equals("java.lang.StringBuffer") ) {
				return "String";
			}
			else if ( typename.equals("java.lang.String") ) {
				return "String";
			}
			else {
				return typename;
			}
		}
		else if ( typename.equals("boolean") || typename.equals("Boolean")
				|| typename.equals("java.lang.Boolean") ) {
			return "Boolean";
		}
		else {
			return aClass.getCanonicalName();
		}
	}

	@SuppressWarnings("unchecked")
	public void generateEquivalentCyanPrototype(Class<?> aClass) {

		String className = aClass.getSimpleName();

		String pathCyanLangReflect = this.cyanLangDir + File.separator + "cyan"
				+ File.separator + "reflect" + File.separator + className
				+ ".cyan";
		Path path = Paths.get(pathCyanLangReflect);

		StringBuffer s = new StringBuffer();

		int modif = aClass.getModifiers();
		if ( Modifier.isAbstract(modif) ) {
		}

		String superclassName = aClass != CyanMetaobject.class
				? aClass.getSuperclass().getSimpleName()
				: null;
		int sizeInter = aClass.getInterfaces().length;
		String strInter = "";
		if ( sizeInter > 0 ) {
			for (Class<?> iInter : aClass.getInterfaces()) {
				String iname = iInter.getSimpleName();
				if ( iInter != Cloneable.class ) {
					if ( strInter.length() == 0 ) {
						strInter = "          implements ";
					}
					strInter += iname;
					if ( --sizeInter > 0 ) {
						strInter += ", ";
					}
				}
				else {
					--sizeInter;
				}
			}
		}
		s.append("package cyan.reflect\r\n" + "\r\n" + "import meta\r\n"
				+ "import java.lang\r\n" + "\r\n" + "\r\n" +
				// (isAbstract ? "abstract \r\n" : "open \r\n" ) +
				"open \r\n" + "object " + className
				+ (superclassName != null ? " extends " + superclassName + " "
						: "")
				+ "\r\n" + strInter + "\r\n" + "\r\n"
				+ (superclassName == null
						? "    @javaPublic var meta." + className
								+ " hidden;\r\n\r\n"
						: ""));
		if ( aClass == CyanMetaobjectAtAnnot.class ) {
			s.append("    func init: String name, String parameterKind,\r\n"
					+ "			Array<String> decKindList, String visibility { \r\n"
					+ "        @javacode{*\r\n"
					+ "            super( new meta.CyanMetaobjectAtAnnot(_name.s, \r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromStringAnnotationArgumentsKind(_parameterKind.s),\r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromStringAttachedDeclarationKind(_decKindList), \r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromStringVisibility(_visibility.s) ) );\r\n"
					+ "        *}\r\n" + "    } \r\n" + "    \r\n"
					+ "    func init: String name, String parameterKind,\r\n"
					+ "			Array<String> decKindList { \r\n"
					+ "        @javacode{*\r\n"
					+ "            super( new meta.CyanMetaobjectAtAnnot(_name.s, \r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromStringAnnotationArgumentsKind(_parameterKind.s),\r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromStringAttachedDeclarationKind(_decKindList), \r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromStringVisibility(\"PRIVATE\") ) );\r\n"
					+
					// " System.out.println(\"deckindList size = \" +
					// _decKindList._size().n);\r\n" +
					// " System.out.println(\"deckindList elem = \" +
					// _decKindList._at_1(new CyInt(0)).s); \r\n" +
					"        *}\r\n" + "    } \r\n" + "      \r\n"
					+ "    func init: String name, String parameterKind { \r\n"
					+ "        @javacode{*\r\n"
					+ "            super( new meta.CyanMetaobjectAtAnnot(_name.s, \r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromStringAnnotationArgumentsKind(_parameterKind.s),\r\n"
					+ "               null, \r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromStringVisibility(\"PRIVATE\") ) );\r\n"
					+ "        *}\r\n" + "    } \r\n" + "      \r\n"
					+ "    func init: String name { \r\n"
					+ "        @javacode{*\r\n"
					+ "            super( new meta.CyanMetaobjectAtAnnot(_name.s, \r\n"
					+ "               null,\r\n" + "               null, \r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromStringVisibility(\"PRIVATE\") ) );\r\n"
					+ "        *}\r\n" + "    } \r\n" + "");

		}
		else if ( aClass == CyanMetaobjectMacro.class ) {
			s.append(
					"    func init: Array<String> startKeywords, Array<String> macroKeywords {\r\n"
							+ "        @javacode{*\r\n"
							+ "            super( new meta.CyanMetaobjectMacro(\r\n"
							+ "               meta.CyanMetaobjectAtAnnot.fromCyanArrayStringToJavaArrayString(_startKeywords),\r\n"
							+ "               meta.CyanMetaobjectAtAnnot.fromCyanArrayStringToJavaArrayString(_macroKeywords)\r\n"
							+ "             ));\r\n" + "        *}\r\n"
							+ "    }\r\n" + "");
		}
		else if ( aClass == CyanMetaobjectNumber.class ) {
			s.append("    func init: Array<String> suffixNameList {\r\n"
					+ "        @javacode{*\r\n"
					+ "            super( new meta.CyanMetaobjectNumber( \r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromCyanArrayStringToJavaArrayString(_suffixNameList)\r\n"
					+ "               ));\r\n" + "        *}\r\n" + "    }\r\n"
					+ "");
		}
		else if ( aClass == CyanMetaobjectLiteralString.class ) {
			s.append("    func init: Array<String> prefixNameList {\r\n"
					+ "        @javacode{*\r\n"
					+ "            super( new meta.CyanMetaobjectLiteralString( \r\n"
					+ "               meta.CyanMetaobjectAtAnnot.fromCyanArrayStringToJavaArrayString(_prefixNameList)\r\n"
					+ "               ));\r\n" + "        *}\r\n" + "    }\r\n"
					+ "");
		}
		else if ( aClass == CyanMetaobjectLiteralObjectSeq.class ) {
			s.append("    func init: String leftCharSequence {\r\n"
					+ "        @javacode{*\r\n"
					+ "            super( new meta.CyanMetaobjectLiteralObjectSeq(_leftCharSequence.s) );\r\n"
					+ "        *}\r\n" + "    }\r\n" + "");
		}
		else if ( aClass == CyanMetaobjectLiteralObject.class ) {
			s.append(
					"    func init: meta.CyanMetaobjectLiteralObject hidden { \r\n"
							+ "        @javacode{*\r\n"
							+ "            super( _hidden );\r\n"
							+ "        *}\r\n" + "    } \r\n" + "");
		}
		else if ( aClass == CyanMetaobject.class ) {
			s.append("    private \r\n"
					+ "    func init { @javacode{* _hidden = null; *} }\r\n"
					+ "\r\n" + "" + "    func init: meta." + className
					+ " hidden {\r\n"
					+ (superclassName != null
							? "        super init: hidden;\r\n"
							: "        self.hidden = hidden;\r\n"
									+ "        @javacode{*\r\n"
									+ "            _hidden.setMetaobjectInCyan(this);\r\n"
									+ "        *}\r\n")
					+

					"\r\n    }\r\n");
		}
		else {
			System.out.println(
					"Unknown metaobject class for generating equivalent Cyan prototype: "
							+ aClass.getName());
		}

		s.append("" + "");

		if ( CyanMetaobject.class != aClass ) {
			s.append("    override");
			s.append("\r\n    func getHidden -> meta." + className
					+ " { @javacode{* return (meta." + className
					+ ") super._getHidden(); *} } \r\n\r\n");
		}
		else {
			s.append("\r\n    func getHidden -> meta." + className
					+ " { return hidden } \r\n\r\n");
		}

		// if ( aClass == CyanMetaobjectAtAnnot.class ) {
		// for ( java.lang.reflect.Method m : aClass.getDeclaredMethods() ) {
		// System.out.println(m.getName());
		// }
		// }
		Set<Tuple2<String, java.lang.reflect.Method>> superMethodList = new HashSet<>();
		Set<String> superMethodNameList = new HashSet<>();
		// System.out.println("\r\n\r\n" + className);
		for (java.lang.reflect.Method m : aClass.getSuperclass()
				.getDeclaredMethods()) {
			superMethodList.add(new Tuple2<>(m.toString(), m));
			superMethodNameList.add(m.getName());
			// System.out.println(m.getName());
		}
		for (java.lang.reflect.Method m : aClass.getDeclaredMethods()) {

			String methodName = m.getName();
			modif = m.getModifiers();
			boolean isStatic = Modifier.isStatic(modif);
			// if ( isStatic ) { continue; }

			boolean foundNocopy = false;
			java.lang.annotation.Annotation[] annotList = m.getAnnotations();
			for (java.lang.annotation.Annotation annot : annotList) {
				if ( annot instanceof Feature ) {
					Feature feature = (Feature) annot;
					if ( feature.value().equals("nocopy") ) {
						foundNocopy = true;
						break;
					}
				}
			}
			if ( foundNocopy ) {
				continue;
			}

			/*
			 * necessary because getDeclaredMethods may return a superclass
			 * method (!!!) Example: superclass: Object get(); subclass: String
			 * get();
			 *
			 * Both methods are returned when aClass is the subclass
			 */

			boolean useOverrideKeyword = false;
			boolean found = false;
			String methodNameToString = m.toString();
			for (Object obj : superMethodList.toArray()) {
				Tuple2<String, java.lang.reflect.Method> t = (Tuple2<String, java.lang.reflect.Method>) obj;
				// public meta.WrAnnotation
				// meta.CyanMetaobjectAtAnnot.getAnnotation()
				String superMethodToString = t.f1;
				if ( !superMethodToString.contains(methodName) ) {
					continue;
				}
				int indexLastDot = superMethodToString.lastIndexOf('.');
				if ( indexLastDot > 0 ) {
					String ss = superMethodToString.substring(0, indexLastDot);
					String remain = superMethodToString.substring(indexLastDot);
					indexLastDot = ss.lastIndexOf('.');
					if ( indexLastDot > 0 ) {
						ss = ss.substring(0, indexLastDot + 1);
						ss += className + remain;
						if ( ss.equals(methodNameToString) ) {
							found = true;
							break;
						}
					}
				}

				// if ( t.f1.equals(methodName) ) {
				// if ( m.equals(t.f2) ) continue;
				// }
			}

			for (String superMethodName : superMethodNameList) {
				if ( superMethodName.equals(methodName) ) {
					useOverrideKeyword = true;
					break;
				}
			}
			if ( !useOverrideKeyword ) {
				sizeInter = aClass.getInterfaces().length;
				if ( sizeInter > 0 ) {
					for (Class<?> iInter : aClass.getInterfaces()) {
						if ( iInter != Cloneable.class ) {
							for (java.lang.reflect.Method interMethod : iInter
									.getDeclaredMethods()) {
								if ( interMethod.getName()
										.equals(methodName) ) {
									useOverrideKeyword = true;
									break;
								}
							}
						}
					}
				}

			}
			if ( found ) {
				continue;
			}

			if ( !Modifier.isPublic(modif) ) {
				continue;
			}

			int size = m.getParameterCount();
			if ( methodName.equals("clone")
					&& (m.getReturnType() == Object.class) ) {
				continue;
			}
			size = Saci.generateCyanMethod(className, s, superclassName, m,
					methodName, isStatic, useOverrideKeyword, size);

		}

		s.append("end\r\n");

		try {
			Files.write(path, (new String(s).getBytes()));
		}
		catch (IOException e) {
			System.out.println("I am unable to create file for class "
					+ className + " in '" + path.toString() + "'");
		}

	}

	/**
	 * @param className
	 * @param s
	 * @param superclassName
	 * @param m
	 * @param methodName
	 * @param isStatic
	 * @param useOverrideKeyword
	 * @param size
	 * @return
	 */
	private static int generateCyanMethod(String className, StringBuffer s,
			String superclassName, java.lang.reflect.Method m,
			String methodName, boolean isStatic, boolean useOverrideKeyword,
			int size) {

		if ( isStatic ) {
			s.append("\r\n    shared");
		}
		if ( useOverrideKeyword ) {
			s.append("\r\n    override");
		}
		s.append("\r\n    func " + methodName);
		if ( size > 0 ) {
			s.append(":");
		}
		s.append(" ");
		for (java.lang.reflect.Parameter p : m.getParameters()) {
			s.append(Saci.getBasicTypeJavaName(p.getType().getCanonicalName())
					+ " " + p.getName());
			if ( --size > 0 ) {

				s.append(", ");
			}
		}
		boolean hasReturnType = m.getReturnType() != Void.class
				&& m.getReturnType() != void.class;
		if ( hasReturnType ) {
			s.append(" -> " + Saci.getBasicTypeJavaName(
					m.getReturnType().getCanonicalName()));
		}

		s.append(" {\r\n        ");
		if ( methodName.equals("semAn_codeToAdd") ) {
			s.append(
					"let StringBuffer s = self getHidden semAn_codeToAdd: arg0;\r\n"
							+ "        @javacode{*\r\n"
							+ "        return new CyString(_s.toString());\r\n"
							+ "        *}\r\n" + "");
		}
		else {
			if ( hasReturnType ) {
				s.append("return ");
			}

			if ( isStatic ) {
				s.append("meta." + className + " " + m.getName());
			}
			else if ( superclassName == null ) {
				s.append("hidden " + m.getName());
			}
			else {
				s.append("self getHidden " + m.getName());
			}
			size = m.getParameterCount();
			if ( size != 0 ) {
				s.append(": ");
				for (java.lang.reflect.Parameter p : m.getParameters()) {
					s.append(p.getName());
					if ( --size > 0 ) {

						s.append(", ");
					}
				}
			}
		}

		s.append("\r\n    }\r\n");

		return size;
	}

	/**
	 * true if the compiler was called from the command line. That is, it was
	 * called from method 'main'.
	 */
	boolean						compilerCalledFromCommandLine	= false;

	private Program				program;
	private Project				project;
	/**
	 * true if variable errorList has been initialized.
	 */
	private boolean				setErrorList;

	/**
	 * list of errors in the Cyan source code and in the Cyan project
	 */
	private List<UnitError>		errorList;

	/**
	 * true if variable projectErrorList has been initialized.
	 */

	private boolean				setProjectErrorList;

	private List<ProjectError>	projectErrorList;

	/**
	 * list of all symbols found in the compilation done with parseSingleSource
	 */
	private Symbol[]			symbolList						= null;

	/**
	 * size of symbolList
	 */
	private int					sizeSymbolList					= 0;

	/**
	 * list of codegs of the last compilation done with parseSingleSource
	 */
	private List<AnnotationAt>	codegList;

	/**
	 * list of all Codegs in the code that are not at-annotations. A Codeg can
	 * be a number like 0101bin. In a near future, it can be a message send. See
	 * otherCodegList;
	 */
	private List<ICodeg>		otherCodegList;

	/**
	 * the arguments that should be passed to the Cyan program
	 */
	private String				cmdLineArgs						= null;

	boolean						errorsHaveBeenPrinted			= false;

	/**
	 * true if the time the compiler spent in each step should be printed after
	 * compilation
	 */
	boolean						profile;
	/**
	 * true if the total compilation and execution time should be printed after
	 * the compilation
	 */
	boolean						totalTime;
	/**
	 * true if this program should be compiled to be later used by program
	 * addType that adds types to otherwise Dyn parameters and local variables
	 */
	boolean						addTypeInfo;
	/**
	 * if addTypeInfo is true, the type info should be put in the path
	 * typeInfoPath unless it is null
	 */
	String						typeInfoPath;

	public static String		javaHome;

	// number of threads created for calling metaobject methods
	public static int			numThreadsMO;
}
