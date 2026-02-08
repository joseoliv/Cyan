
package saci;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ast.Annotation;
import ast.AnnotationAt;
import ast.CompilationUnit;
import ast.CompilationUnitDSL;
import ast.CyanPackage;
import ast.Expr;
import ast.ExprGenericPrototypeInstantiation;
import ast.ExprIdentStar;
import ast.ExprTypeUnion;
import ast.InterfaceDec;
import ast.PW;
import ast.Program;
import ast.Prototype;
import ast.Type;
import ast.TypeJavaRef;
import cyan.lang.CyInt;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._IActionFunction;
import cyan.reflect._IActionNewPrototypes__afterResTypes;
import error.CompileErrorException;
import error.ErrorKind;
import error.UnitError;
import lexer.Symbol;
import meta.AttachedDeclarationKind;
import meta.CompilationInstruction;
import meta.CompilationStep;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.CyanMetaobjectFromDSL_toPrototype;
import meta.CyanMetaobjectLiteralObjectSeq;
import meta.CyanMetaobject_wrapperActionFunction_inCyan;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.IActionAttachedType_semAn;
import meta.IActionFieldAccess_semAn;
import meta.IActionFieldMissing_semAn;
import meta.IActionMethodMissing_semAn;
import meta.IActionNewPrototypes_afterResTypes;
import meta.IAction_parsing;
import meta.IAction_semAn;
import meta.ICheckOverride_afterSemAn;
import meta.ICheckSubprototype_afterSemAn;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_parsing;
import meta.IParse_parsing;
import meta.InitMetaobjectErrorException;
import meta.MetaHelper;
import meta.ReplacementPolicyInGenericInstantiation;
import meta.Timeout;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCyanPackage;
import metaRealClasses.Compiler_afterResTypes;
import metaRealClasses.Compiler_dsl;
import metaRealClasses.Compiler_parsing;

/**
 * Compiles a Cyan program described by a project.
 *
 * @author José
 *
 */
public class CompilerManager {

	public CompilerManager(Project project, Program program,
			PrintWriter printWriter, HashMap<String, String> compilerOptions) {
		this.project = project;
		this.program = program;
		this.printWriter = printWriter;
		nameSet = new HashSet<>();
	}

	/**
	 * return a compiler for compiling <code>sourceCode</code> of file
	 * 'sourceCodeCanonicalPath/sourceCodeFilename' of package 'cyanPackage'
	 */
	public static ICompiler_parsing getCompilerToInternalDSL(char[] sourceCode,
			String sourceCodeFilename, String sourceCodeCanonicalPath,
			WrCyanPackage cyanPackage) {

		final CompilationUnitDSL dslCompilationUnit = new CompilationUnitDSL(
				sourceCodeFilename, sourceCodeCanonicalPath,
				meta.GetHiddenItem.getHiddenCyanPackage(cyanPackage));
		dslCompilationUnit.setText(sourceCode);
		final HashSet<CompilationInstruction> compInstSet = new HashSet<>();
		try {
			final Compiler comp = new Compiler(dslCompilationUnit, compInstSet,
					CompilationStep.step_1, null, null);
			final Compiler_parsing compiler_parsing = new Compiler_parsing(comp,
					null, null);
			return compiler_parsing;
		}
		catch (RuntimeException e) {
			throw new CompileErrorException(
					"Unknown internal error in getCompilerToInternalDSL");
		}
		finally {
			List<UnitError> errorList = dslCompilationUnit.getErrorList();
			if ( errorList != null && errorList.size() > 0 ) {
				UnitError ue = errorList.get(0);
				throw new CompileErrorException("Error in line "
						+ ue.getLineNumber() + "(" + ue.getColumnNumber()
						+ "): " + ue.getMessage());
			}

		}
	}

	/**
	 * return a compiler for compiling <code>sourceCode</code> of file
	 * 'sourceCodeCanonicalPath/sourceCodeFilename' of package 'cyanPackage'
	 */
	public Compiler_dsl getCompilerToDSL_sourceFile(char[] sourceCode,
			String sourceCodeFilename, String sourceCodeCanonicalPath,
			CyanPackage cyanPackage) {

		final CompilationUnitDSL dslCompilationUnit = new CompilationUnitDSL(
				sourceCodeFilename, sourceCodeCanonicalPath, cyanPackage);
		dslCompilationUnit.setText(sourceCode);
		final HashSet<CompilationInstruction> compInstSet = new HashSet<>();
		final Compiler comp = new Compiler(dslCompilationUnit, compInstSet,
				CompilationStep.step_1, null, null);
		final Compiler_dsl compiler_dsl = new Compiler_dsl(this, comp);
		return compiler_dsl;
	}

	public Compiler getCompiler_sourceFile(char[] sourceCode,
			CompilationUnitDSL dslCompilationUnit) {

		dslCompilationUnit.setText(sourceCode);

		final HashSet<CompilationInstruction> compInstSet = new HashSet<>();
		final Compiler comp = new Compiler(dslCompilationUnit, compInstSet,
				CompilationStep.step_1, null, null);
		return comp;
	}

	/**
	 * read all source files to their compilation units. Return true if no
	 * error.
	 *
	 */
	public boolean readSourceFiles() {

		createPrototypesFromDSL_directories();
		if ( this.project.getProjectErrorList() != null
				&& this.project.getProjectErrorList().size() > 0 ) {
			return false;
		}
		for (final CompilationUnit compilationUnit : program
				.getCompilationUnitList()) {
			try {
				if ( !compilationUnit.getAlreadPreviouslyCompiled() ) {
					compilationUnit.readSourceFile();
				}
			}
			catch (final Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 */
	public void createPrototypesFromDSL_directories() {
		final String strDSL = DirectoryKindPPP.DSL.toString();

		for (final CyanPackage cp : this.program.getPackageList()) {
			String packageDSLdirPath = cp.getPackageCanonicalPath();
			if ( !packageDSLdirPath
					.endsWith(NameServer.fileSeparatorAsString) ) {
				packageDSLdirPath += NameServer.fileSeparatorAsString;
			}
			packageDSLdirPath += strDSL;
			final File packageDSLdirFile = new File(packageDSLdirPath);
			if ( packageDSLdirFile.exists() ) {
				if ( !packageDSLdirFile.isDirectory() ) {
					project.error("File '" + packageDSLdirPath
							+ "' should be a directory with DSL code.");
				}
				else {
					scanSubDirectories(packageDSLdirFile, packageDSLdirPath,
							cp);
				}
			}
		}

	}

	private void scanSubDirectories(File root, String partialPath,
			CyanPackage cp) {
		for (final File f : root.listFiles()) {
			if ( f.isDirectory() ) {
				scanSubDirectories(f, partialPath
						+ NameServer.fileSeparatorAsString + f.getName(), cp);
			}
			else {
				final String name = f.getName();
				final int lastDot = name.lastIndexOf('.');
				if ( lastDot > 0 ) {
					// should be a DSL file
					final String extension = name.substring(lastDot + 1);
					/*
					 * look for a metaobject whose name is 'extension'
					 */
					CyanMetaobject cyanMetaobject = null;
					if ( cp.getPyanBeforePackageMetaobjectMap() != null ) {
						cyanMetaobject = cp.getPyanBeforePackageMetaobjectMap()
								.get(extension);
					}
					if ( cyanMetaobject == null ) {
						/*
						 * some day we will use a hash table for that.
						 */
						for (final CyanMetaobject cyanMO : project
								.getCyanLangPackage().getMetaobjectList()) {
							if ( cyanMO.getName().equals(extension) ) {
								cyanMetaobject = cyanMO;
								break;
							}
						}
					}
					if ( cyanMetaobject != null
							&& cyanMetaobject instanceof CyanMetaobjectFromDSL_toPrototype ) {
						final String prototypeName = name.substring(0, lastDot);

						final CyanMetaobjectFromDSL_toPrototype moDSL = (CyanMetaobjectFromDSL_toPrototype) cyanMetaobject;
						moDSL.setPackageNameDSL(cp.getPackageName());
						moDSL.setPrototypeName(prototypeName);
						final Tuple2<Integer, char[]> t = MyFile.readFile(f, 0);
						if ( t == null || t.f2 == null ) {
							project.error("Error opening or reading file '"
									+ partialPath
									+ NameServer.fileSeparatorAsString + name
									+ "'");
						}
						else {
							moDSL.setText(t.f2);
						}
						try {
							moDSL.setFileNameDSLSourceCode(
									f.getCanonicalPath());
						}
						catch (final IOException e) {
							project.error("Error handling file '" + partialPath
									+ NameServer.fileSeparatorAsString + name
									+ "'");
						}
						/*
						 * ICompiler_dsl getCompilerToDSL_sourceFile(char
						 * []sourceCode, String sourceCodeFilename, String
						 * sourceCodeCanonicalPath, CyanPackage cyanPackage)
						 */
						final Compiler_dsl compiler_dsl = getCompilerToDSL_sourceFile(
								moDSL.getText(), name,
								moDSL.getFileNameDSLSourceCode(), cp);
						compiler_dsl.setCyanPackage(cp);
						final List<Tuple3<String, String, char[]>> protoNameFileNameCodeList = moDSL
								.parsing_NewPrototype(compiler_dsl);
						/**
						 * important: if the file name starts with an upper-case
						 * letter, protoNameFileNameCodeList should be just one
						 * element whose prototype name is exactly the file name
						 * of the DSL file without extension and equal to the
						 * generated Cyan source code.
						 */
						if ( protoNameFileNameCodeList == null
								|| protoNameFileNameCodeList.size() == 0 ) {
							project.error("Metaobject '" + moDSL.getName()
									+ "' did not produce code for file '"
									+ partialPath
									+ NameServer.fileSeparatorAsString
									+ f.getName() + "'");
							return;
						}
						if ( protoNameFileNameCodeList.size() == 1 ) {
							if ( !Character
									.isUpperCase(prototypeName.charAt(0)) ) {
								project.error("File '" + partialPath
										+ NameServer.fileSeparatorAsString
										+ name
										+ "' should start with an upper-case letter because metaobject '"
										+ moDSL.getName()
										+ "' produced just one prototype for it");
								return;
							}
							if ( !prototypeName.equals(
									protoNameFileNameCodeList.get(0).f1) ) {
								project.error("Metaobject '" + moDSL.getName()
										+ "' did produced a prototype whose name is '"
										+ protoNameFileNameCodeList.get(0).f1
										+ "'. However, the prototype name should be "
										+ "equal to the file name, '"
										+ partialPath
										+ NameServer.fileSeparatorAsString
										+ f.getName() + "'");
								return;
							}
						}
						else {
							if ( Character
									.isUpperCase(prototypeName.charAt(0)) ) {
								project.error("File '" + partialPath
										+ NameServer.fileSeparatorAsString
										+ name
										+ "' should start with a lower-case letter because metaobject '"
										+ moDSL.getName()
										+ "' produced more than one prototype for it");
							}

						}

						for (final Tuple3<String, String, char[]> protoNameFileNameCode : protoNameFileNameCodeList) {
							final char[] protoCode = protoNameFileNameCode.f3;

							String dirName = cp.getPackageCanonicalPath();
							if ( !dirName.endsWith(File.separator) ) {
								dirName += File.separator;
							}
							dirName += NameServer.temporaryDirName;
							final File dir = new File(dirName);
							if ( !dir.exists() ) {
								dir.mkdirs();
							}

							/*
							 * create a new compilation unit with the name of
							 * the file
							 */
							final CompilationUnit newCompilationUnit = new CompilationUnit(
									protoNameFileNameCode.f2,
									dirName + NameServer.fileSeparatorAsString,
									null, cp);
							newCompilationUnit.setText(protoCode);
							writeTextToFile(dirName, protoNameFileNameCode.f2,
									protoNameFileNameCode.f3);
							cp.addCompilationUnit(newCompilationUnit);
							newCompilationUnit.setCyanPackage(cp);
							/*
							 * nonGenericCompilationUnitList.add(
							 * newCompilationUnit); String newcuFullFileNamePath
							 * = newCompilationUnit.getFullFileNamePath(); if (
							 * nameSet.contains(newcuFullFileNamePath) ) {
							 * project.
							 * error("Internal error in DSL prototype: prototype is already in the list nameSet"
							 * ); } else nameSet.add(newcuFullFileNamePath);
							 */
							program.addCompilationUnit(newCompilationUnit);

						}

					}
					else {
						project.error("File '" + partialPath
								+ NameServer.fileSeparatorAsString + f.getName()
								+ "' is in a directory '--dsl' that keeps DSL files. There should be a metaobject whose name is '"
								+ extension
								+ "' but this metaobject was not found. Probably you forgot to use 'import' before"
								+ " the package declaration in the .pyan file. The metaobject class must inherit from '"
								+ CyanMetaobjectFromDSL_toPrototype.class
										.getName()
								+ "'");
					}
				}
			}
		}

	}

	void writeTextToFile(String dirName, String fileName, char[] text) {
		String fullFileName;
		if ( dirName.endsWith(NameServer.fileSeparatorAsString) ) {
			fullFileName = dirName + fileName;
		}
		else {
			fullFileName = dirName + NameServer.fileSeparatorAsString
					+ fileName;
		}
		try (FileOutputStream fos = new FileOutputStream(fullFileName);
				PrintWriter pw = new PrintWriter(fos, true);) {
			pw.write(text, 0, text.length);
		}
		catch (final FileNotFoundException e1) {
			project.error("Cannot create file " + fullFileName);
		}
		catch (final NullPointerException e3) {
			// e3.printStackTrace();
			project.error(
					"Internal error in CompilationUnit when writing to file "
							+ fullFileName);
		}
		catch (final IOException e) {
			project.error("Cannot write to file " + fullFileName);
		}

	}

	/**
	 * compile the project passed as parameter to the constructor. The basic
	 * packages of Cyan, stored in project <code>cyanLangPackageProject</code>,
	 * are added to the project. Currently there is only one basic package of
	 * the language: "cyan.lang". This should be included in every compilation.
	 *
	 * @return <code>false</code> in error
	 */
	public boolean compile(HashSet<meta.CompilationInstruction> compInstSet) {

		Compiler compiler;

		try {

			// list of non-generic prototypes of the programc
			nonGenericCompilationUnitList = new ArrayList<CompilationUnit>();
			// list of generic prototypes of the program
			final List<CompilationUnit> genericCompilationUnitList = new ArrayList<CompilationUnit>();

			/*
			 * separates the compilation units (source files) that have generic
			 * prototypes from those that don´t. A prototype whose file has a
			 * digit after '(' is a generic prototype. For example,
			 * Proto(1)(1).cyan has a generic prototype Proto<T><R>. And file
			 * MyData<main.Person><Int> is put in file
			 * MyData(main.Person)(Int).cyan.
			 */

			for (final CompilationUnit compilationUnit : program
					.getCompilationUnitList()) {

				/*
				 * // if the file name has a '(' character followed by a digit,
				 * then it is a // generic prototype. Note that
				 * "Stack(Int).cyan" contains prototype "Stack<Int>" // which is
				 * not considered generic
				 *
				 */
				final String filename = compilationUnit.getFilename();
				boolean foundDigit = false;
				int ifn = 0;
				final int sizeFilename = filename.length();
				while (ifn < sizeFilename) {
					if ( filename.charAt(ifn) == '('
							&& Character.isDigit(filename.charAt(ifn + 1)) ) {
						foundDigit = true;
						break;
					}
					++ifn;
				}
				if ( foundDigit ) {
					compilationUnit.setHasGenericPrototype(true);
					genericCompilationUnitList.add(compilationUnit);
				}
				else
					nonGenericCompilationUnitList.add(compilationUnit);
				/*
				 * if ( indexOfLeftPar > 0 &&
				 * Character.isDigit(compilationUnit.getFilename().charAt(
				 * indexOfLeftPar + 1)) )
				 * genericCompilationUnitList.add(compilationUnit); else
				 * nonGenericCompilationUnitList.add(compilationUnit);
				 */
			}

			final String dotExtension = "."
					+ MetaHelper.cyanSourceFileExtension;
			boolean thereWasErrorsGenericCompilationUnitList = false;

			/**
			 * first of all, parse all generic prototypes. This is not allowed
			 * in step 7 of the compilation because all generic prototype
			 * instantiation should have been created before that.
			 */
			if ( this.compilationStep.compareTo(CompilationStep.step_7) < 0 ) {
				for (final CompilationUnit compilationUnit : genericCompilationUnitList) {
					if ( compilationUnit.getGenericAndParsed() ) {
						continue;
					}
					compiler = new Compiler(compilationUnit, compInstSet,
							compilationStep, project, null);
					try {
						// compilationUnit.reset();
						compiler.parse();
						compilationUnit.setGenericAndParsed(true);
					}
					catch (final RuntimeException e) {

						compilationUnit.error(1, 1,
								"Internal error: exception '"
										+ e.getClass().getName()
										+ "' was thrown");
					}
					/**
					 * print the errors found in the generic prototypes and
					 * apply all actions to them. An action is a small
					 * refactoring like insert a ";"
					 */
					if ( compilationUnit.hasCompilationError() ) {
						thereWasErrorsGenericCompilationUnitList = true;
					}

					if ( compilationUnit.getActionList().size() > 0 )
						compilationUnit.doActionList(printWriter);
				}
			}

			/*
			 * in the first step of this while statement, all non-generic
			 * prototypes are compiled.
			 *
			 * In the second step of the while statement, the real prototypes
			 * created in the previous step are compiled. They may instantiate
			 * new generic prototypes. For example, Stack<Int> may declare a
			 * variable of type "Array<Int>". This new Cyan prototype should be
			 * created and compiled. The process continues till no new
			 * prototypes should be created.
			 */
			CompilationUnit compilationUnit;
			int numCompilationUnitsAlreadyCompiled = 0;
			int sizeNonGenericCompilationUnitList;
			boolean inCompilationStep4 = this.compilationStep == CompilationStep.step_4;
			boolean inCompilationStep7 = this.compilationStep == CompilationStep.step_7;

			while (numCompilationUnitsAlreadyCompiled < nonGenericCompilationUnitList
					.size()) {

				sizeNonGenericCompilationUnitList = nonGenericCompilationUnitList
						.size();
				boolean thereWasErrors = thereWasErrorsGenericCompilationUnitList;

				// CompilationUnit clientProto = null;

				// parse of all source files that were not yet parsed. That may
				// include some
				// generic prototypes that were instantiated in the previous
				// round of the above
				// while statement.
				for (int i = numCompilationUnitsAlreadyCompiled; i < sizeNonGenericCompilationUnitList; i++) {
					compilationUnit = nonGenericCompilationUnitList.get(i);
					// if ( compilationUnit.getFilename().equals("Program.cyan")
					// ) {
					// System.out.println("$$$$$ ");
					// }
					if ( compilationUnit.getAlreadPreviouslyCompiled() ) {
						continue;
					}

					// if ( inCompilationStep4 ) {
					// if ( !
					// compilationUnit.getSourceCodeChanged_inPhaseafterResTypes()
					// ) {
					// continue;
					// }
					// }
					// else if ( inCompilationStep7 ) {
					// if ( !
					// compilationUnit.getSourceCodeChanged_inPhasesemAn() ) {
					// continue;
					// }
					// }

					// if ( inCompilationStep4 || inCompilationStep7 ) {
					// compilationUnit.reset(); }

					compiler = new Compiler(compilationUnit, compInstSet,
							compilationStep, project, null);
					compiler.parse();
					compilationUnit.setParsed(true);
					if ( compilationUnit.hasCompilationError() ) {
						thereWasErrors = true;
					}
					else if ( compInstSet.contains(
							CompilationInstruction.createPrototypesForInterfaces)
							&&

							compilationUnit.getPrototypeIsNotGeneric()
							&& compilationUnit
									.getPublicPrototype() instanceof InterfaceDec ) {
						// if public program unit is an interface, create
						// ProtoInterface
						final CompilationUnit newCompilationUnit = compilationUnit
								.createProtoInterface();
						if ( newCompilationUnit == null ) {
							if ( compilationUnit.hasCompilationError() ) {
								thereWasErrors = true;
							}
						}
						else {
							final CyanPackage thisCyanPackage = compilationUnit
									.getCyanPackage();

							thisCyanPackage
									.addCompilationUnit(newCompilationUnit);
							newCompilationUnit.setCyanPackage(thisCyanPackage);
							nonGenericCompilationUnitList
									.add(newCompilationUnit);

							String name = newCompilationUnit.getFilename();
							final int indexDotCyan = name.indexOf(dotExtension);
							if ( indexDotCyan > 0 )
								name = name.substring(0, indexDotCyan);

							program.addCompilationUnit(newCompilationUnit);

							nameSet.add(newCompilationUnit.getFilename());
						}
					}

				}

				if ( thereWasErrors ) {
					return false;
				}
				numCompilationUnitsAlreadyCompiled = sizeNonGenericCompilationUnitList;
				// if ( clientProto != null ) {
				// System.out.println("%%$$ num methods Client = " +
				// ((ObjectDec )
				// clientProto.getPublicPrototype()).getMethodDecList().size())
				// ;
				// }

			}

		}
		catch (final Exception e) {
			e.printStackTrace();
			project.error("Internal error at CompilerManager::compile(). e = "
					+ e.getClass().getName());
			return false;
		}
		// project.printErrorList(printWriter);
		return true;

	}

	/**
	 * create a new prototype whose name is prototypeName and whose source code
	 * is <code>code</code>. The compiler options of this compilation unit
	 * should be prototypeCompilerOptions. Its package is cyanPackage.
	 *
	 * This method returns the compilation unit and an error message.
	 *
	 */
	public Tuple2<CompilationUnit, String> createNewPrototype(
			String prototypeName, StringBuffer code,
			CompilerOptions prototypeCompilerOptions, CyanPackage cyanPackage) {

		if ( prototypeName.contains("<") || prototypeName.contains(">") ) {
			return new Tuple2<CompilationUnit, String>(null,
					"Cannot create generic prototype '" + prototypeName
							+ "' using metaobjects");
		}
		String fileName, packageCanonicalPath1 = "";
		String newFileName;
		FileOutputStream fos = null;
		newFileName = fileName = cyanPackage.getPackageCanonicalPath();
		final char[] newText = new char[code.length() + 1];
		code.getChars(0, code.length(), newText, 0);
		if ( code.charAt(code.length() - 1) != '\0' ) {
			newText[newText.length - 1] = '\0';
		}

		try {

			final int indexOfStartFileName = fileName
					.lastIndexOf(File.separator);
			if ( indexOfStartFileName > 0 ) {
				newFileName = fileName.substring(0, indexOfStartFileName);
				final String dirName = newFileName + File.separator
						+ NameServer.temporaryDirName;
				final File dir = new File(dirName);
				if ( !dir.exists() ) {
					dir.mkdirs();
				}
				packageCanonicalPath1 = dirName + File.separator;
				newFileName = packageCanonicalPath1 + prototypeName
						+ MetaHelper.dotCyanSourceFileExtension;
			}
			fos = new FileOutputStream(newFileName);
			final PrintWriter pWriter = new PrintWriter(fos, true);
			final PW pw = new PW();
			pw.set(pWriter);
			if ( code.charAt(code.length() - 1) == '\0' ) {
				code.deleteCharAt(code.length() - 1);
			}
			pw.println(code);

			pWriter.close();
		}
		catch (final FileNotFoundException e1) {
			return new Tuple2<CompilationUnit, String>(null,
					"Cannot create file " + newFileName);
		}
		catch (final NullPointerException e3) {
			try {
				if ( fos != null ) fos.close();
			}
			catch (final IOException e) {
			}
			return new Tuple2<CompilationUnit, String>(null,
					"Internal error in CompilationUnit when writing to file "
							+ newFileName);
		}
		catch (final Exception e2) {
			try {
				if ( fos != null ) fos.close();
			}
			catch (final IOException e) {
			}
			return new Tuple2<CompilationUnit, String>(null,
					"error in writing to file " + newFileName);
		}

		final CompilationUnit compilationUnit = new CompilationUnit(
				prototypeName + MetaHelper.dotCyanSourceFileExtension,
				packageCanonicalPath1, prototypeCompilerOptions, cyanPackage);

		cyanPackage.addCompilationUnit(compilationUnit);
		compilationUnit.setCyanPackage(cyanPackage);
		nonGenericCompilationUnitList.add(compilationUnit);

		program.addCompilationUnit(compilationUnit);

		compilationUnit.setText(newText);
		// newCompilationUnit.readSourceFile();

		/*
		 * HashSet<saci.CompilationInstruction> compInstSet = new HashSet<>();
		 * compInstSet.add(CompilationInstruction.parsing_actions);
		 * compInstSet.add(CompilationInstruction.pp_addCode); if (
		 * compilationStep.compareTo(CompilationStep.step_5) >= 0 )
		 * compInstSet.add(CompilationInstruction.new_addCode); Compiler
		 * compiler = new Compiler(compilationUnit, compInstSet,
		 * compilationStep, project, null); compiler.parse();
		 * 
		 * if ( compilationUnit.hasCompilationError() ) { throw new
		 * CompileErrorException(); }
		 */
		return new Tuple2<CompilationUnit, String>(compilationUnit, null);
	}

	/**
	 * Create a generic prototype from parameter gpi. The new prototype is
	 * compiled. If it is an interface, the prototype Proto_IntefaceName is
	 * created too. Methods calcInterfaceTypes and calcInternalTypes are called
	 * for both the prototype and its Proto_InterfaceName, if any.
	 * 
	 * @param gpi
	 * @param env
	 * @return
	 */
	public static Type createGenericPrototype(
			ExprGenericPrototypeInstantiation gpi, Env env) {

		final saci.TupleTwo<String, Type> t = gpi
				.ifPrototypeReturnsNameWithPackageAndType(env);

		// t.f1 is the name of the prototype
		if ( t != null && t.f2 != null ) {
			/*
			 * prototype has already been created before or it is a Java class
			 */
			if ( t.f2 instanceof TypeJavaRef ) {
				return t.f2;
			}
			if ( t.f2 instanceof Prototype ) {
				return t.f2;
			}
			else {
				env.error(gpi.getFirstSymbol(),
						"Internal error: a type that is not a program unit is used to instantiate a generic prototype");
				return null;
			}

		}

		/*
		 * prototype has not been created. Create it. But this is only allowed
		 * in compilation steps < 7
		 */

		// if ( env.getCurrentMethod() != null &&
		// env.getCurrentMethod().getName().contains("run2") )
		// gpi.ifPrototypeReturnsItsNameWithPackage(env);

		/*
		 * first, create all prototypes that are real parameters to this generic
		 * prototype instantiation
		 */
		for (final List<Expr> realTypeList : gpi.getRealTypeListList()) {
			for (final Expr realType : realTypeList) {
				instantiateGenPrototypesForArgs(env, realType);
			}
		}

		/*
		 * number of compilation units of the program. If the calculus of
		 * interfaces of compUnit add code to the generic prototype, it has to
		 * be compiled again. All of the generic prototype instantiations
		 * created in the compilation must be destroyed. This variable is used
		 * for this.
		 */
		final int numCompilationUnitsProgram = env.getProject().getProgram()
				.getCompilationUnitList().size();

		CompilationUnit compUnit = env.getProject().getCompilerManager()
				.createGenericPrototypeInstantiation(gpi, env);

		Env newEnv = new Env(env.getProject());
		/*
		 * If a generic prototype is created when method calcInterfaceTypes of
		 * Program is being executed
		 * (env.getProject().getProgram().getInCalcInterfaceTypes() returns
		 * true), it is not necessary to call its method calcInterfaceTypes. It
		 * will be called in the loop of method Program::calcInterfaceTypes.
		 *
		 * If a generic prototype is created when method calcInternalTypes of
		 * Program is being executed, method calcInterfaceTypes of this generic
		 * prototype should be called because the interface of this prototype is
		 * necessary in methods calcInternalTypes. Method calcInternalTypes of
		 * this newly created generic prototype will be called in the loop of
		 * method Program::calcInternalTypes.
		 *
		 * calcInternalTypes cannot be called when a generic prototype is
		 * created when the compiler is calling calcInterfaceTypes. If this is
		 * allowed, some calcInternalType method could try to use the interface
		 * of some prototype P whose method calcInterfaceTypes have not been
		 * called.
		 */
		// if ( ! env.getProject().getProgram().getInCalcInterfaceTypes() ) {
		try {
			final CompilationUnit compUnitFinal = compUnit;
			final Env newEnvFinal = newEnv;
			compUnit.getCyanPackage().addPackageMetaToClassPath_and_Run(() -> {
				compUnitFinal.calcInterfaceTypes(newEnvFinal);
			});

		}
		finally {
			if ( newEnv.isThereWasError() ) {
				env.setThereWasError(true);
				// copyErrorsFromTo(newEnv, env);
			}
		}
		// compUnit.calcInternalTypes(newEnv);
		// }
		CompilationUnit interfaceCompilationUnit = compUnit
				.getInterfaceCompilationUnit();
		if ( interfaceCompilationUnit != null ) {
			newEnv = new Env(env.getProject());
			if ( !env.getProject().getProgram().getInCalcInterfaceTypes() ) {

				try {
					CompilationUnit interfaceCompilationUnitFinal = interfaceCompilationUnit;
					interfaceCompilationUnit.getCyanPackage()
							.addPackageMetaToClassPath_and_Run(() -> {
								interfaceCompilationUnitFinal
										.calcInterfaceTypes(env);
							});

				}
				catch (final CompileErrorException e) {
					if ( newEnv.isThereWasError() ) {
						copyErrorsFromTo(newEnv, env);
					}
					throw e;
				}
				if ( newEnv.isThereWasError() ) {
					copyErrorsFromTo(newEnv, env);
				}
			}
		}
		/*
		 * if the generic prototype was created from phase (4), included,
		 * onwards, then execute the AFTER_RES_TYPES actions. A generic
		 * prototype instantiation can only change itself and no other prototype
		 * can change it.
		 */
		if ( env.getProject().getCompilerManager().getCompilationStep()
				.ordinal() > CompilationStep.step_3.ordinal() ) {

			boolean changesWereMade = false;
			try {
				changesWereMade = apply_afterResTypes_ActionsToGenericPrototype(
						newEnv, compUnit);
			}
			catch (error.ErrorInMetaobjectException e) {
				env.getProject().getProgram().addCompilationUnit(compUnit);
				env.setThereWasError(true);
				throw e;
			}

			if ( interfaceCompilationUnit != null ) {
				try {
					changesWereMade = changesWereMade
							|| apply_afterResTypes_ActionsToGenericPrototype(
									env, interfaceCompilationUnit);
				}
				catch (error.ErrorInMetaobjectException e) {
					env.getProject().getProgram().addCompilationUnit(compUnit);
					env.setThereWasError(true);
					throw e;
				}
			}

			// ## changesWereMade &&
			if ( env.getProject().getCompilerManager().getCompilationStep()
					.ordinal() == CompilationStep.step_6.ordinal() ) {
				// parse the generic prototype to
				final boolean isInterface = compUnit
						.getPublicPrototype() instanceof InterfaceDec;

				/*
				 * the generic prototype has to be compiled again. Then the old
				 * one and all generic prototypes it had created must be
				 * destroyed.
				 */
				if ( !env.getProject().getProgram().trimCompilationUnitList(
						numCompilationUnitsProgram,
						env.getProject().getCompilerManager().nameSet) ) {
					/*
					 * numCompilationUnitsProgram is greater than or equal the
					 * number of compilation units of the program or the
					 * compilation units were not found in the packages they
					 * belong
					 */
					env.error(gpi.getFirstSymbol(),
							"Internal error near call to trimCompilationUnitList");
				}

				final HashSet<meta.CompilationInstruction> compInstSet = new HashSet<>();
				compInstSet.add(CompilationInstruction.parsing_actions);

				compUnit.reset();

				compUnit.getCyanPackage().addCompilationUnit(compUnit);
				env.getProject()
						.getCompilerManager().nonGenericCompilationUnitList
								.add(compUnit);

				if ( env.getProject().getCompilerManager().nameSet
						.contains(compUnit.getFullFileNamePath()) ) {
					env.error(gpi.getFirstSymbol(),
							"Internal error in CompilerManager");
					// gt.ifPrototypeReturnsNameWithPackageAndType(env);
				}
				else
					env.getProject().getCompilerManager().nameSet
							.add(compUnit.getFullFileNamePath());

				env.getProject().getProgram().addCompilationUnit(compUnit);

				/*
				 *
				 */
				compInstSet.add(CompilationInstruction.new_addCode);
				compInstSet.add(
						CompilationInstruction.createPrototypesForInterfaces);
				CompilationUnit interfaceCompUnit = null;
				if ( isInterface ) {
					interfaceCompUnit = compUnit;
				}
				compUnit = env.getProject().getCompilerManager()
						.parseNewGenericPrototypeInstantiation(newEnv,
								isInterface, compUnit,
								compUnit.getCyanPackage(), compInstSet);
				/*
				 * the code below is a repetition of a code above. I could not
				 * extract it to a method.
				 */
				if ( !env.getProject().getProgram()
						.getInCalcInterfaceTypes() ) {
					try {

						CompilationUnit compUnitFinal = compUnit;
						Env newEnvFinal = newEnv;
						compUnit.getCyanPackage()
								.addPackageMetaToClassPath_and_Run(() -> {
									compUnitFinal
											.calcInterfaceTypes(newEnvFinal);
								});

					}
					finally {
						if ( newEnv.isThereWasError() ) {
							env.setThereWasError(true);
							// copyErrorsFromTo(newEnv, env);
						}
					}
					// compUnit.calcInternalTypes(newEnv);
				}
				interfaceCompilationUnit = compUnit
						.getInterfaceCompilationUnit();
				if ( interfaceCompUnit != null ) {
					newEnv = new Env(env.getProject());
					if ( !env.getProject().getProgram()
							.getInCalcInterfaceTypes() ) {

						interfaceCompUnit.reset();
						try {

							CompilationUnit interfaceCompilationUnitFinal = interfaceCompUnit;
							Env newEnvFinal = newEnv;
							compUnit.getCyanPackage()
									.addPackageMetaToClassPath_and_Run(() -> {
										interfaceCompilationUnitFinal
												.calcInterfaceTypes(
														newEnvFinal);
									});

						}
						catch (final CompileErrorException e) {
							if ( newEnv.isThereWasError() ) {
								copyErrorsFromTo(newEnv, env);
							}
							throw e;
						}
						if ( newEnv.isThereWasError() ) {
							copyErrorsFromTo(newEnv, env);
						}
					}
				}

				if ( isInterface && interfaceCompUnit != null ) {
					compUnit = interfaceCompUnit;
				}
			}

		}
		if ( newEnv.isThereWasError() ) {
			env.setThereWasError(true);
		}

		return compUnit.getPublicPrototype();

	}

	/**
	 * create, recursively, the prototype for realType, if realType is a generic
	 * prototype instantiation or if it is an union type with generic prototype
	 * instantiations. This method also checks if there are annotations to the
	 * types. They are not allowed
	 * 
	 * @param env
	 * @param realType
	 */
	private static void instantiateGenPrototypesForArgs(Env env,
			final Expr realType) {
		if ( realType instanceof ExprGenericPrototypeInstantiation ) {
			final ExprGenericPrototypeInstantiation genRealType = (ExprGenericPrototypeInstantiation) realType;
			genRealType.setType(createGenericPrototype(genRealType, env));
			// javaName = type.getJavaName();
			genRealType.setJavaName(genRealType.getType().getJavaName());
			if ( genRealType.getAnnotationToTypeList() != null
					&& genRealType.getAnnotationToTypeList().size() > 0 ) {
				env.error(realType.getFirstSymbol(),
						"Type with attached metaobjects cannot be parameters to generic prototypes");
			}
		}
		else if ( realType instanceof ExprIdentStar ) {
			final ExprIdentStar paramType = (ExprIdentStar) realType;
			if ( paramType.getAnnotationToTypeList() != null
					&& paramType.getAnnotationToTypeList().size() > 0 ) {
				env.error(realType.getFirstSymbol(),
						"Type with attached metaobjects cannot be parameters to generic prototypes");
			}
		}
		else if ( realType instanceof ExprTypeUnion ) {
			ExprTypeUnion etu = (ExprTypeUnion) realType;
			for (Expr ue : etu.getTypeList()) {
				instantiateGenPrototypesForArgs(env, ue);
			}

		}
	}

	public static void copyErrorsFromTo(Env from, Env to) {
		final Map<CompilationUnit, List<UnitError>> mapCompUnitErrorList = from
				.getMapCompUnitErrorList();
		if ( mapCompUnitErrorList != null ) {
			for (final CompilationUnit cunit : mapCompUnitErrorList.keySet()) {
				final List<UnitError> errorList = mapCompUnitErrorList
						.get(cunit);
				if ( errorList != null ) {
					for (final UnitError error : errorList) {
						to.addError(error);
					}
				}
			}
		}
	}

	/**
	 * Apply AFTER_RES_TYPES action to a generic prototype. Return true if any
	 * changes were made.
	 *
	 * @param env
	 * @param compUnit
	 * @return
	 */
	private static boolean apply_afterResTypes_ActionsToGenericPrototype(
			Env env, CompilationUnit compUnit) {

		final ICompiler_afterResTypes compiler_afterResTypes = new Compiler_afterResTypes(
				env);
		final CompilerManager_afterResTypes compilerManager_afterResTypes = new CompilerManager_afterResTypes(
				env);

		compUnit.getCyanPackage().addPackageMetaToClassPath_and_Run(() -> {
			compUnit.afterResTypes_actions(compiler_afterResTypes,
					compilerManager_afterResTypes);
		});

		/*
		 * all changes demanded by metaobject annotations collected above are
		 * made in the call to CompilerManager_afterResTypes#changeCheckProgram.
		 */
		return compilerManager_afterResTypes.changeCheckProgram();

	}

	/**
	 * Create an instantiation of a generic prototype given by parameter gt
	 *
	 * @param gt
	 * @param env
	 * @return
	 */
	private CompilationUnit createGenericPrototypeInstantiation(
			ExprGenericPrototypeInstantiation gt, Env env) {

		CompilationUnit genericProto = null;

		String genSourceFileName = gt.getGenericSourceFileName();

		String genSourceFileNameVaryingNumberOfParameters = gt
				.getGenericSourceFileNameWithVaryingNumberOfParameters();
		boolean isInterface = false;
		if ( NameServer.isPrototypeFromInterface(genSourceFileName) ) {
			isInterface = true;
			genSourceFileName = NameServer
					.prototypeFileNameFromInterfaceFileName(genSourceFileName);
			genSourceFileNameVaryingNumberOfParameters = NameServer
					.prototypeFileNameFromInterfaceFileName(
							genSourceFileNameVaryingNumberOfParameters);

		}
		// something like util.Stack if gt is "util.Stack<Int>" or
		// Stack if gt is Stack<Int>
		final ExprIdentStar typeIdent = gt.getTypeIdent();
		if ( typeIdent.getIdentSymbolArray().size() == 1 ) {
			// no package preceding the generic prototype name as in
			// "Stack<Int>"
			Prototype pu = env.searchPrototypeBySourceFileName(
					genSourceFileName, gt.getFirstSymbol(), false);
			if ( pu != null ) {
				genericProto = pu.getCompilationUnit();
			}

			// if ( pu != null ) {
			// genericProto = pu.getCompilationUnit();
			// final Prototype pu2 =
			// env.searchPrototypeBySourceFileName(genSourceFileNameVaryingNumberOfParameters,
			// gt.getFirstSymbol(), false);
			// if ( pu2 != null )
			// /* found both generic prototype and generic prototype with
			// varying number of parameters
			// * Example: found both Tuple<T> and Tuple<T+>
			// */
			// env.error(gt.getFirstSymbol(), "Ambiguity in creating a real
			// prototype from a generic prototype. There is both "
			// + pu.getCompilationUnit().getPackageName() + "." +
			// genSourceFileName + " and " +
			// pu2.getCompilationUnit().getPackageName() + "." +
			// genSourceFileNameVaryingNumberOfParameters, true, true
			// );
			//
			// }
			if ( genericProto == null ) {
				pu = env.searchPrototypeBySourceFileName(
						genSourceFileNameVaryingNumberOfParameters,
						gt.getFirstSymbol(), false);
				if ( pu != null ) genericProto = pu.getCompilationUnit();
			}
		}
		else {
			// package preceding the generic prototype name as in
			// "util.Stack<Int>"
			int i = 0;
			final List<Symbol> symbolList = typeIdent.getIdentSymbolArray();
			final int sizeLessOne = symbolList.size() - 1;
			String packageName = "";
			while (i < sizeLessOne) {
				packageName = packageName + symbolList.get(i).getSymbolString();
				++i;
				if ( i < sizeLessOne ) packageName += ".";
			}
			final CyanPackage cyanPackage = env.getProject()
					.searchPackage(packageName);
			if ( cyanPackage == null ) {
				env.error(typeIdent.getFirstSymbol(),
						"Package '" + packageName + "' was not found", true,
						true);
				return null;
			}
			// first searches for something like "Stack(1)" in package 'util'
			for (final CompilationUnit cunit : cyanPackage
					.getCompilationUnitList()) {
				if ( genSourceFileName
						.equals(cunit.getFileNameWithoutExtension()) ) {
					genericProto = cunit;
					break;
				}

			}

			// searches for a generic prototype with varying number of
			// parameters
			// something like "Stack(1+)"

			// if ( genericProto != null && genericProto2 != null ) {
			// env.error(gt.getFirstSymbol(), "Ambiguity in creating a real
			// prototype from a generic prototype. There is both "
			// + genericProto.getPackageName() + "." + genSourceFileName + " and
			// " +
			// genericProto2.getPackageName() + "." +
			// genSourceFileNameVaryingNumberOfParameters, true, true
			// );
			// }
			if ( genericProto == null ) {
				for (final CompilationUnit cunit : cyanPackage
						.getCompilationUnitList()) {
					if ( genSourceFileNameVaryingNumberOfParameters
							.equals(cunit.getFileNameWithoutExtension()) ) {
						genericProto = cunit;
						break;
					}

				}

			}
		}

		// genericProto = nameGenProtoUnitTable.get(genSourceFileName);

		if ( genericProto == null ) {
			if ( env.getProject().getCompilerManager()
					.getCompilationStep() == CompilationStep.step_9 ) {
				env.error(true, gt.getFirstSymbol(), "Prototype '"
						+ gt.getName()
						+ "' was not found. This prototype probably was "
						+ "instantiated by code generated during semantic analysis. You cannot instantiate new generic prototypes in this phase",
						gt.getName(),
						ErrorKind.prototype_was_not_found_inside_method);
			}
			else {
				env.error(true, gt.getFirstSymbol(),
						"Prototype '" + gt.getName() + "' was not found",
						gt.getName(),
						ErrorKind.prototype_was_not_found_inside_method);
			}
			return null;
		}
		else {
			/**
			 * if there was no compilation error in "Stack(1).cyan", then create
			 * an instance of the generic prototype
			 */

			if ( env.getProject().getCompilerManager().getCompilationStep()
					.ordinal() >= CompilationStep.step_7.ordinal() ) {
				/*
				 * it is allowed to create instantiations of generic prototypes
				 * only for super-prototypes of inner prototypes. There is an
				 * inner prototype for each anonymous function of the prototype.
				 */
				if ( !env.getAllowCreationOfPrototypesInLastCompilerPhases()
						&& !genericProto.getPackageName()
								.equals(MetaHelper.cyanLanguagePackageName) ) {
					env.error(gt.getFirstSymbol(),
							"Attempt to create a generic prototype, "
									+ gt.asString()
									+ " after step 6 of the compilation."
									+ "This is caused by code introduced in step 6, semantic analysis, that introduced a new generic prototype instantiation. "
									+ "This could be a new type of variable or a new literal object such as an anonymous function or tuple. To solve that, "
									+ "create this generic prototype before this step. If necessary, use method 'createNewGenericPrototype' of interface ICompiler_semAn");
					return null;

				}
			}

			if ( isInterface ) {
				gt.removeProtoPrefix();
			}

			final CompilationUnit newCompilationUnit = genericProto
					.createInstanceGenericPrototype(gt, env);
			/**
			 * if the package for this generic prototype instantiation was not
			 * created before, create it now.
			 */

			final CyanPackage cyanPackage = genericProto.getCyanPackage();

			cyanPackage.addCompilationUnit(newCompilationUnit);
			newCompilationUnit.setCyanPackage(cyanPackage);
			nonGenericCompilationUnitList.add(newCompilationUnit);

			final String newcuFullFileNamePath = newCompilationUnit
					.getFullFileNamePath();
			if ( nameSet.contains(newcuFullFileNamePath) ) {
				gt.ifPrototypeReturnsNameWithPackageAndType(env);

				env.error(gt.getFirstSymbol(),
						"Internal error in CompilerManager");
				// gt.ifPrototypeReturnsNameWithPackageAndType(env);
			}
			else
				nameSet.add(newcuFullFileNamePath);

			program.addCompilationUnit(newCompilationUnit);

			newCompilationUnit.readSourceFile();

			final HashSet<meta.CompilationInstruction> compInstSet = new HashSet<>();
			compInstSet.add(CompilationInstruction.parsing_actions);
			compInstSet.add(CompilationInstruction.pp_addCode);
			// if ( compilationStep.compareTo(CompilationStep.step_5) >= 0 ) //
			// ##
			// compInstSet.add(CompilationInstruction.new_addCode);

			return parseNewGenericPrototypeInstantiation(env, isInterface,
					newCompilationUnit, cyanPackage, compInstSet);
		}

	}

	/**
	 * @param env
	 * @param isInterface
	 * @param newCompilationUnit
	 * @param cyanPackage
	 * @return
	 */
	private CompilationUnit parseNewGenericPrototypeInstantiation(Env env,
			boolean isInterface, CompilationUnit newCompilationUnit,
			CyanPackage cyanPackage,
			HashSet<meta.CompilationInstruction> compInstSet) {
		Compiler compiler = new Compiler(newCompilationUnit, compInstSet,
				compilationStep, project, null);
		final boolean allowCreationOfPrototypesInLastCompilerPhases = env
				.getAllowCreationOfPrototypesInLastCompilerPhases();
		try {
			if ( allowCreationOfPrototypesInLastCompilerPhases ) {
				compiler.setAllowCreationOfPrototypesInLastCompilerPhases(true);
			}
			compiler.parse();
			newCompilationUnit.setParsed(true);
		}
		finally {
			compiler.setAllowCreationOfPrototypesInLastCompilerPhases(
					allowCreationOfPrototypesInLastCompilerPhases);
		}

		CompilationUnit interCompilationUnit = null;

		if ( newCompilationUnit.hasCompilationError() ) {
			// newCompilationUnit.printErrorList(printWriter);
			env.setThereWasError(true);
			if ( newCompilationUnit.getErrorList() != null
					&& newCompilationUnit.getErrorList().size() > 0 ) {
				UnitError ue = newCompilationUnit.getErrorList().get(0);
				throw new CompileErrorException("Error in line " + ue.getLine()
						+ "(" + ue.getColumnNumber() + "): " + ue.getMessage());
			}
			else {
				throw new CompileErrorException(
						"Problably an internal error in CompilerManager::parseNewGenericPrototypeInstantiation");
			}
		}
		else if ( compInstSet
				.contains(CompilationInstruction.createPrototypesForInterfaces)
				&& newCompilationUnit.getPrototypeIsNotGeneric()
				&& newCompilationUnit
						.getPublicPrototype() instanceof InterfaceDec ) {
			// if public program unit is an interface, create ProtoInterface

			interCompilationUnit = newCompilationUnit.createProtoInterface();
			if ( interCompilationUnit != null ) {

				interCompilationUnit.setCyanPackage(cyanPackage);
				cyanPackage.addCompilationUnit(interCompilationUnit);
				newCompilationUnit.setCyanPackage(cyanPackage);
				nonGenericCompilationUnitList.add(interCompilationUnit);

				String nameInter = interCompilationUnit.getFilename();
				if ( nameInter.endsWith(MetaHelper.dotCyanSourceFileExtension) )
					nameInter = nameInter.substring(0, nameInter.length()
							- MetaHelper.sizeCyanSourceFileExtensionPlusOne);

				// nameRealGenProtoUnitTable.put(nameInter,
				// interCompilationUnit);
				program.addCompilationUnit(interCompilationUnit);

				interCompilationUnit.readSourceFile();
				compiler = new Compiler(interCompilationUnit, compInstSet,
						compilationStep, project, null);
				compiler.parse();
				if ( interCompilationUnit.getActionList().size() > 0 )
					interCompilationUnit.doActionList(printWriter);
				interCompilationUnit.clearErrorsActions();

			}
		}
		// newCompilationUnit.clearErrorsActions();
		if ( isInterface )
			return interCompilationUnit;
		else
			return newCompilationUnit;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public static void collectSubDirectories(String path, String parent,
			List<String> dirPathList) {

		final File root = new File(path);
		final File[] list = root.listFiles();

		if ( list == null ) return;

		for (final File f : list) {
			if ( f.isDirectory() ) {
				dirPathList.add(parent + f.getName()
						+ NameServer.fileSeparatorAsString);
				collectSubDirectories(f.getAbsolutePath(),
						parent + f.getName() + NameServer.fileSeparatorAsString,
						dirPathList);
			}
		}
	}

	public static void loadMetaobjectsFromPackage(String packageCanonicalPath,
			String packageName, List<CyanMetaobject> metaobjectList,
			Compiler compiler) {
		final String slash = File.separator;
		String path;
		String fullFilename = "";
		String packageCanonicalPathEndsSlash = packageCanonicalPath
				+ (packageCanonicalPath.endsWith(slash) ? "" : slash);

		List<CyanMetaobject> previousCyanMetaobjectList = loadedPackagePathSet
				.get(packageCanonicalPathEndsSlash);
		// System.out.println("try loading '" + packageCanonicalPathEndsSlash +
		// "'");
		if ( previousCyanMetaobjectList != null ) {
			metaobjectList.addAll(previousCyanMetaobjectList);
			return;
		}
		// System.out.println("metaobjects of '" + packageCanonicalPathEndsSlash
		// + "' will be loaded");

		path = packageCanonicalPathEndsSlash + NameServer.metaobjectPackageName;
		// path = packageCanonicalPath + (packageCanonicalPath.endsWith(slash) ?
		// "" : slash ) + "meta";
		if ( !path.endsWith(slash) ) path = path + slash;

		final int dotClassLength = ".class".length();
		String fileNameMetaobjectFile;
		String packageNameMO;
		URLClassLoader urlcl = null;
		try {
			final File dir = new File(path);
			if ( dir.exists() ) {
				/*
				 * urlcl = URLClassLoader.newInstance(new URL[] { new
				 * URL("file:///" + path ) });
				 */

				final List<String> dirPathList = new ArrayList<>();
				dirPathList.add("");
				collectSubDirectories(path, "", dirPathList);

				final URL singleURL = new File(path).toURI().toURL();
				/*
				 * urlcl = new URLClassLoader( new URL[] { new URL("file:" +
				 * slash + slash + path ) },
				 * Thread.currentThread().getContextClassLoader() );
				 *
				 */
				// String strsss = singleURL.toString();
				ClassLoader cl2 = Thread.currentThread()
						.getContextClassLoader();
				// com.google.common.reflect.ClassPath classPath2 =
				// ClassPath.from(cl2);
				// for ( ClassPath.ClassInfo ci :
				// classPath2.getTopLevelClasses() ) {
				// if ( ci.getName().startsWith("cyan.reflect") ||
				// ci.getName().endsWith("CyanMetaobject")
				// ) {
				// System.out.println(ci.getName());
				// }
				// }
				//
				// for ( ResourceInfo ri : classPath2.getResources() ) {
				// if ( ri.getResourceName().startsWith("cyan.reflect") ||
				// ri.getResourceName().endsWith("CyanMetaobject")
				// ) {
				// System.out.println(ri.getResourceName());
				// }
				// }
				Class<?> cyanMetaobjectPrototype;
				cyanMetaobjectPrototype = cl2
						.loadClass("cyan.reflect._CyanMetaobject");

				urlcl = new URLClassLoader(new URL[] { singleURL },
						Thread.currentThread().getContextClassLoader());

				final Class<?> cyanMetaobjectClass = CyanMetaobject.class;

				// cyanMetaobjectPrototype = cyan.reflect._CyanMetaobject.class;
				// System.out.println(cyanMetaobjectPrototype.getClassLoader().getName());

				// Class<?> cyanMetaobjectCallable = IActionFunction.class;

				for (final String classPath : dirPathList) {
					final File dirPathFile = new File(path + classPath);
					packageNameMO = classPath.replace(File.separatorChar, '.');
					final File[] fileList = dirPathFile.listFiles();
					if ( fileList == null ) continue;
					for (final File file : fileList) {

						if ( !file.isDirectory() ) {
							fullFilename = file.getName();
							if ( fullFilename.endsWith(".class") ) {
								fileNameMetaobjectFile = fullFilename.substring(
										0,
										fullFilename.length() - dotClassLength);
								String fullFileNameMetaobjectFile = packageNameMO
										+ fileNameMetaobjectFile;
								final Class<?> clazz = urlcl.loadClass(
										packageNameMO + fileNameMetaobjectFile);
								// boolean callableSubtype = false;
								if ( !fileNameMetaobjectFile.equals(
										NameServer.cyanMetaobjectClassName) ) {
									// we are only interested in subclasses of
									// CyanMetaobject
									// callableSubtype =
									// cyanMetaobjectCallable.isAssignableFrom(clazz);
									if ( !cyanMetaobjectClass
											.isAssignableFrom(clazz)
											&& !cyanMetaobjectPrototype
													.isAssignableFrom(clazz)

									) // && ! callableSubtype)
										continue;

								}
								try {
									final int modifier = clazz.getModifiers();
									if ( !Modifier.isAbstract(modifier)
											&& !Modifier.isInterface(modifier)
											&& !Modifier.isPrivate(modifier)
											&& !clazz.isEnum() ) {

										boolean foundConstructorWithoutParameters = false;
										final Constructor<?>[] constructorList = clazz
												.getConstructors();
										for (final Constructor<?> constructor : constructorList) {
											if ( constructor
													.getExceptionTypes().length == 0
													&& constructor
															.getGenericExceptionTypes().length == 0
													&& constructor
															.getParameterTypes().length == 0
													&& constructor
															.getGenericParameterTypes().length == 0 ) {
												foundConstructorWithoutParameters = true;
												break;
											}
										}
										if ( foundConstructorWithoutParameters ) {
											final Object newObj = clazz
													.getDeclaredConstructor()
													.newInstance();
											if ( !(newObj instanceof CyanMetaobject)
													&& !(newObj instanceof _CyanMetaobject) ) {
												compiler.error2(null,
														"Class of file '" + path
																+ classPath
																+ fullFilename
																+ "' should inherit from '"
																+ CyanMetaobject.class
																		.getName()
																+ "' or '"
																+ _CyanMetaobject.class
																		.getName()
																+ "'");
											}
											else {
												if ( newObj instanceof CyanMetaobject ) {
													final CyanMetaobject cyanMetaobject = (CyanMetaobject) newObj;
													checkMetaobject(
															cyanMetaobject,
															compiler,
															packageName);
													cyanMetaobject
															.setCanonicalPath(
																	file.getCanonicalPath());
													cyanMetaobject.setFileName(
															fullFilename);
													cyanMetaobject
															.setPackageName(
																	packageName);
													metaobjectList.add(
															cyanMetaobject);
												}
												else {
													final _CyanMetaobject _cyanMetaobject = (_CyanMetaobject) newObj;
													CyanMetaobject cyanMetaobject;
													if ( newObj instanceof _IActionFunction ) {
														cyanMetaobject = new CyanMetaobject_wrapperActionFunction_inCyan(
																(_IActionFunction) newObj);
														// checkMetaobject(cyanMetaobject,
														// compiler,
														// packageName);
														cyanMetaobject
																.setCanonicalPath(
																		file.getCanonicalPath());
														cyanMetaobject
																.setFileName(
																		fullFilename);
														cyanMetaobject
																.setPackageName(
																		packageName);
														metaobjectList.add(
																cyanMetaobject);
													}
													else {
														cyanMetaobject = _cyanMetaobject
																._getHidden();
														if ( cyanMetaobject != null ) {
															cyanMetaobject
																	.setCanonicalPath(
																			file.getCanonicalPath());
															cyanMetaobject
																	.setFileName(
																			fullFilename);
															cyanMetaobject
																	.setPackageName(
																			packageName);
															metaobjectList.add(
																	cyanMetaobject);
														}
													}

												}
											}
										}
										else
											compiler.error2(null, "File '"
													+ path + classPath
													+ fullFilename
													+ "' should contain a class with a constructor without parameters and that does not throw any exception");
									}
								}
								catch (InitMetaobjectErrorException e) {
									compiler.error2(null,
											"When creating a metaobject '"
													+ clazz.getClass().getName()
													+ "' of file '"
													+ fullFilename
													+ "', an exception was thrown. Its message is '"
													+ e.getErrorMessage()
													+ "'");
								}
								catch (IllegalAccessException
										| InstantiationException e) {
									compiler.error2(null,
											"Illegal metaobject in '" + path
													+ classPath + fullFilename
													+ "'");
								}
								catch (RuntimeException e) {
									compiler.error2(null,
											"When creating a metaobject '"
													+ clazz.getClass().getName()
													+ "' of file '"
													+ fullFilename
													+ "', an exception was thrown. Its message is '"
													+ e.getMessage() + "'");
								}
							}
						}

					}
				}

				urlcl.close();

			}
		}
		catch (ClassNotFoundException | NoClassDefFoundError | IOException e) {
			compiler.error2(null,
					"Error when reading metaobjects of path " + path
							+ (fullFilename.length() != 0
									? ", probably in file " + fullFilename
									: "")
							+ " The message from the exception "
							+ e.getClass().getName() + "' is '" + e.getMessage()
							+ "'");
			return;
		}
		catch (final Throwable e) {
			compiler.error2(null,
					"Internal error when reading metaobjects of path " + path
							+ (fullFilename.length() != 0
									? ", probably in file " + fullFilename
									: "")
							+ " The exception message is:\n" + e.getMessage());
			return;
		}
		finally {
			if ( urlcl != null ) {
				try {
					urlcl.close();
				}
				catch (final IOException e1) {
				}
			}

		}
		loadedPackagePathSet.put(packageCanonicalPathEndsSlash, metaobjectList);
	}

	/**
	 * check inheritance and interfaces implemented by that metaobject
	 * <code>cyanMetaobject</code>. Some combinations are illegal such as a
	 * macro metaobject implement
	 * {@link meta#IParseWithoutCyanCompiler_parsing}. However, most illegal
	 * combinations are not checked. This will be a future work.
	 * 
	 * @param cyanMetaobject
	 */
	private static void checkMetaobject(CyanMetaobject cyanMetaobject,
			Compiler compiler, String packageName) {

		_CyanMetaobject moCyan = cyanMetaobject.getMetaobjectInCyan();
		if ( moCyan != null ) {
			cyanMetaobject = moCyan._getHidden();
		}

		if ( cyanMetaobject instanceof CyanMetaobjectAtAnnot ) {
			final CyanMetaobjectAtAnnot withAt = (CyanMetaobjectAtAnnot) cyanMetaobject;
			if ( withAt.shouldTakeText()
					&& withAt instanceof IAction_parsing ) {
				compiler.error2(false, compiler.symbol, "Metaobject '"
						+ cyanMetaobject.getName() + "' imported from package '"
						+ packageName
						+ "' should take a text as in @concept{* ... *} and it implements interface '"
						+ meta.IAction_parsing.class.getName()
						+ "'. This is illegal. Metaobjects that take text should implement one "
						+ "of the '" + IParse_parsing.class.getName()
						+ "' sub-interfaces. To produce code, they should implement "
						+ "interface '" + IAction_parsing.class.getName()
						+ "' to produce code in phase 'SEM_AN'", true);
			}
			if ( cyanMetaobject instanceof ICheckSubprototype_afterSemAn ) {
				AttachedDeclarationKind[] decKindList = withAt
						.getAttachedDecKindList();
				boolean ok_cs = true;
				if ( decKindList == null ) {
					ok_cs = false;
				}
				else {
					for (AttachedDeclarationKind decKind : decKindList) {
						if ( decKind != AttachedDeclarationKind.PROTOTYPE_DEC ) {
							ok_cs = false;
						}
					}
				}
				if ( !ok_cs ) {
					compiler.error2(false, compiler.symbol, "Metaobject '"
							+ withAt.getName() + "' implements interface '"
							+ ICheckSubprototype_afterSemAn.class.getName()
							+ "'. Therefore, its annotations can only be attached to prototypes. But"
							+ " method 'getAttachedDecKindList' of the metaobject either returns null"
							+ " or something different from 'PROTOTYPE_DEC'");
				}
			}

		}

		/*
		 * if ( cyanMetaobject instanceof IActionPackage_afterResTypes ||
		 * cyanMetaobject instanceof IActionProgram_afterResTypes ) { if (
		 * cyanMetaobject instanceof IAction_parsing || cyanMetaobject
		 * instanceof IParse_parsing || cyanMetaobject instanceof IAction_semAn
		 * || cyanMetaobject instanceof IActionVariableDeclaration_semAn ||
		 * cyanMetaobject instanceof IActionMessageSend_semAn || cyanMetaobject
		 * instanceof IActionMethodMissing_semAn || cyanMetaobject instanceof
		 * IAction_cge || cyanMetaobject instanceof IActionAssignment_cge ||
		 * cyanMetaobject instanceof ICheck_afterResTypes_afterSemAn ) {
		 * incompatibleInterfaceNameList = new ArrayList<>();
		 * compiler.error2(false, compiler.symbol, "Metaobject '" +
		 * cyanMetaobject.getName() + "' imported from package '" + packageName
		 * + "' implements interface '" +
		 * IActionPackage_afterResTypes.class.getName() + "' or '" +
		 * IActionProgram_afterResTypes.class.getName() +
		 * "' and at least one other interface that should be implemented only "
		 * +
		 * "by metaobjects that should not be attached to a package or the program such as '"
		 * + IAction_parsing.class.getName() + "'", true);
		 * 
		 * } }
		 */
		// if ( cyanMetaobject instanceof IParse_parsing && cyanMetaobject
		// instanceof IAction_parsing ) {
		// compiler.error2( false, compiler.symbol, "Metaobject '" +
		// cyanMetaobject.getName() + "' imported from package '"
		// + packageName + "' implements a subinterface of '" +
		// IParse_parsing.class.getName() + "' and interface '" +
		// IAction_parsing.class.getName() + "'. They are incompatible, only one
		// of them should be implemented", true);
		//
		// }
		if ( cyanMetaobject instanceof IParse_parsing
				&& cyanMetaobject instanceof CyanMetaobjectAtAnnot ) {
			if ( !((CyanMetaobjectAtAnnot) cyanMetaobject).shouldTakeText() ) {
				compiler.error2(false, compiler.symbol, "Metaobject '"
						+ cyanMetaobject.getName() + "' imported from package '"
						+ packageName + "' implements a subinterface of '"
						+ IParse_parsing.class.getName()
						+ "'. Therefore a text (DSL) should be "
						+ "attached to the metaobject annotation. However, this is not possible because method 'shouldTakeText' returns false",
						true);
			}

		}

		if ( cyanMetaobject instanceof CyanMetaobjectLiteralObjectSeq ) {
			final String lcs = ((CyanMetaobjectLiteralObjectSeq) cyanMetaobject)
					.leftCharSequence();
			if ( illegalLeftCharSeq.contains(lcs) && !packageName
					.equals(MetaHelper.cyanLanguagePackageName) ) {
				compiler.error2(false, compiler.symbol, "Metaobject '"
						+ cyanMetaobject.getName() + "' imported from package '"
						+ packageName + "' extends '"
						+ CyanMetaobjectLiteralObjectSeq.class.getName()
						+ "' and it defines " + "'" + lcs + "' as "
						+ "left char sequence. This sequence is illegal because it is reserved for the package "
						+ "cyan.lang or it is '{*', the standard for delimiting DSL code that follows a metaobject annotation",
						true);

			}
		}

		if ( cyanMetaobject instanceof ICheckOverride_afterSemAn ) {
			if ( !(cyanMetaobject instanceof CyanMetaobjectAtAnnot) ) {
				compiler.error2(false, compiler.symbol,
						"Metaobject '" + cyanMetaobject.getName()
								+ "' imported from package '" + packageName
								+ "' implements interface '"
								+ ICheckOverride_afterSemAn.class.getName()
								+ "'. Therefore it should inherit from '"
								+ CyanMetaobjectAtAnnot.class.getName()
								+ "'. It does not",
						true);
			}
			else {
				final CyanMetaobjectAtAnnot withAt = (CyanMetaobjectAtAnnot) cyanMetaobject;
				final AttachedDeclarationKind[] decList = withAt
						.getAttachedDecKindList();
				if ( decList.length != 1 || decList.length == 1
						&& decList[0] != AttachedDeclarationKind.METHOD_DEC
						&& decList[0] != AttachedDeclarationKind.METHOD_SIGNATURE_DEC ) {
					compiler.error2(false, compiler.symbol, "Metaobject '"
							+ cyanMetaobject.getName()
							+ "' imported from package '" + packageName
							+ "' implements interface '"
							+ ICheckOverride_afterSemAn.class.getName()
							+ "'. Therefore its annotations should only be attached to methods or method interfaces",
							true);

				}
			}
		}
		if ( cyanMetaobject instanceof IActionFieldAccess_semAn ) {

			if ( !(cyanMetaobject instanceof CyanMetaobjectAtAnnot) ) {
				compiler.error2(false, compiler.symbol,
						"Metaobject '" + cyanMetaobject.getName()
								+ "' imported from package '" + packageName
								+ "' implements interface '"
								+ IActionFieldAccess_semAn.class.getName()
								+ "'. Therefore it should inherit from '"
								+ CyanMetaobjectAtAnnot.class.getName()
								+ "'. It does not",
						true);
			}
			else {
				final CyanMetaobjectAtAnnot withAt = (CyanMetaobjectAtAnnot) cyanMetaobject;
				final AttachedDeclarationKind[] decList = withAt
						.getAttachedDecKindList();
				if ( decList.length != 1 || decList.length == 1
						&& decList[0] != AttachedDeclarationKind.FIELD_DEC ) {
					compiler.error2(false, compiler.symbol, "Metaobject '"
							+ cyanMetaobject.getName()
							+ "' imported from package '" + packageName
							+ "' implements interface '"
							+ IActionFieldAccess_semAn.class.getName()
							+ "'. Therefore its annotations should only be attached to fields",
							true);

				}
			}

		}
		if ( cyanMetaobject instanceof IActionFieldMissing_semAn ) {

			if ( !(cyanMetaobject instanceof CyanMetaobjectAtAnnot) ) {
				compiler.error2(false, compiler.symbol,
						"Metaobject '" + cyanMetaobject.getName()
								+ "' imported from package '" + packageName
								+ "' implements interface '"
								+ IActionFieldMissing_semAn.class.getName()
								+ "'. Therefore it should inherit from '"
								+ CyanMetaobjectAtAnnot.class.getName()
								+ "'. It does not",
						true);
			}
			else {
				final CyanMetaobjectAtAnnot withAt = (CyanMetaobjectAtAnnot) cyanMetaobject;
				final AttachedDeclarationKind[] decList = withAt
						.getAttachedDecKindList();
				if ( decList.length != 1 || decList.length == 1
						&& decList[0] != AttachedDeclarationKind.PROTOTYPE_DEC ) {
					compiler.error2(false, compiler.symbol, "Metaobject '"
							+ cyanMetaobject.getName()
							+ "' imported from package '" + packageName
							+ "' implements interface '"
							+ IActionFieldMissing_semAn.class.getName()
							+ "'. Therefore its annotations should only be attached to prototypes",
							true);

				}
			}

		}
		if ( cyanMetaobject instanceof IActionAttachedType_semAn ) {
			if ( cyanMetaobject instanceof IAction_parsing
					|| cyanMetaobject instanceof IAction_semAn ) {
				compiler.error2(false, compiler.symbol, "Metaobject '"
						+ cyanMetaobject.getName() + "' imported from package '"
						+ packageName + "' implements interface '"
						+ IActionAttachedType_semAn.class.getName()
						+ "'. Therefore it cannot also implement interfaces '"
						+ IAction_parsing.class.getName() + "' or '"
						+ IAction_semAn.class.getName() + "'", true);

			}
		}

		if ( cyanMetaobject instanceof IActionMethodMissing_semAn ) {
			if ( !(cyanMetaobject instanceof CyanMetaobjectAtAnnot) ) {
				compiler.error2(false, compiler.symbol, "Metaobject '"
						+ cyanMetaobject.getName() + "' imported from package '"
						+ packageName + "' implements interface '"
						+ IActionMethodMissing_semAn.class.getName()
						+ "'. Therefore its class or prototype should inherits from '"
						+ CyanMetaobjectAtAnnot.class.getName() + "'", true);

			}
			else {
				CyanMetaobjectAtAnnot mo = (CyanMetaobjectAtAnnot) cyanMetaobject;
				AttachedDeclarationKind[] decKindList = mo
						.getAttachedDecKindList();
				boolean onlyMethodPrototype = false;
				if ( decKindList != null ) {
					for (AttachedDeclarationKind decKind : decKindList) {
						if ( decKind == AttachedDeclarationKind.METHOD_DEC
								|| decKind == AttachedDeclarationKind.PROTOTYPE_DEC
								|| decKind == AttachedDeclarationKind.METHOD_SIGNATURE_DEC ) {
							onlyMethodPrototype = true;
						}
						else {
							onlyMethodPrototype = false;
							break;
						}
					}
				}
				if ( !onlyMethodPrototype ) {
					compiler.error2(false, compiler.symbol, "Metaobject '"
							+ cyanMetaobject.getName()
							+ "' imported from package '" + packageName
							+ "' implements interface '"
							+ IActionMethodMissing_semAn.class.getName()
							+ "'. Therefore, its list of allowed attached declarations should include "
							+ "only methods, method signatures, and prototypes",
							true);
				}
				if ( mo.getVisibility() != Token.PUBLIC ) {
					compiler.error2(false, compiler.symbol, "Metaobject '"
							+ cyanMetaobject.getName()
							+ "' imported from package '" + packageName
							+ "' implements interface '"
							+ IActionMethodMissing_semAn.class.getName()
							+ "'. Therefore it should have 'PUBLIC' visibility. It does not",
							true);

				}
			}
		}
		if ( cyanMetaobject instanceof IActionAttachedType_semAn ) {
			if ( !(cyanMetaobject instanceof CyanMetaobjectAtAnnot) ) {
				compiler.error2(false, compiler.symbol, "Metaobject '"
						+ cyanMetaobject.getName() + "' imported from package '"
						+ packageName + "' implements interface '"
						+ IActionAttachedType_semAn.class.getName()
						+ "'. Therefore its class or prototype should inherit from CyanMetaobjectAtAnnot. It does not",
						true);

			}
			else {
				CyanMetaobjectAtAnnot mo = (CyanMetaobjectAtAnnot) cyanMetaobject;
				AttachedDeclarationKind[] kindList = mo
						.getAttachedDecKindList();
				boolean atError = false;
				if ( kindList != null && kindList.length == 1 ) {
					if ( kindList[0] != AttachedDeclarationKind.TYPE ) {
						atError = true;
					}
				}
				else {
					atError = true;
				}
				if ( atError ) {
					compiler.error2(false, compiler.symbol, "Metaobject '"
							+ cyanMetaobject.getName()
							+ "' imported from package '" + packageName
							+ "' implements interface '"
							+ IActionAttachedType_semAn.class.getName()
							+ "'. Therefore its method getAttachedDecKindList() should return an array with just one element: "
							+ AttachedDeclarationKind.TYPE.name(), true);
				}
			}
		}

		// List<String> incompatibleInterfaceNameList = new ArrayList<>();
		// if ( cyanMetaobject instanceof meta.IParse_parsing && cyanMetaobject
		// instanceof meta.IAction_parsing ) {
		// incompatibleInterfaceNameList = new ArrayList<>();
		// incompatibleInterfaceNameList.add(meta.IParse_parsing.class.getName());
		// incompatibleInterfaceNameList.add(meta.IAction_parsing.class.getName());
		// }
		// if ( incompatibleInterfaceNameList != null ) {
		// String all = "";
		// for ( final String s : incompatibleInterfaceNameList ) {
		// all += s + " ";
		// }
		// compiler.error2( false, compiler.symbol, "Metaobject '" +
		// cyanMetaobject.getName() + "' imported from package '"
		// + packageName + "' implements two incompatible interfaces: " + all,
		// true);
		// }

	}

	/**
	 * @param compiler_afterResTypes
	 * @param env
	 * @param annotation
	 * @param cyanMetaobject
	 */
	public static void createNewPrototypes(
			ICompiler_afterResTypes compiler_afterResTypes, Env env,
			Annotation annotation, CyanMetaobject cyanMetaobject,
			CompilationUnit compilationUnit) {

		/*
		 * create new prototypes
		 */
		try {
			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			List<Tuple2<String, StringBuffer>> prototypeNameCodeList = null;

			try { // afterResTypes_NewPrototypeList

				int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(env,
						compiler_afterResTypes.getEnv().getProject()
								.getProgram(),
						compiler_afterResTypes.getCompilationUnit()
								.getCyanPackage(),
						annotation.getFirstSymbol());

				if ( other == null ) {
					Timeout<List<Tuple2<String, StringBuffer>>> to = new Timeout<>();
					final IActionNewPrototypes_afterResTypes metaobject = (IActionNewPrototypes_afterResTypes) cyanMetaobject;

					if ( Saci.timeLimitForMetaobjects ) {
						prototypeNameCodeList = to.run(() -> {
							return metaobject.afterResTypes_NewPrototypeList(
									compiler_afterResTypes);
						}, timeoutMilliseconds,
								"afterResTypes_NewPrototypeList",
								cyanMetaobject, env);

					}
					else {
						prototypeNameCodeList = metaobject
								.afterResTypes_NewPrototypeList(
										compiler_afterResTypes);
					}

					// prototypeNameCodeList =
					// metaobject.afterResTypes_NewPrototypeList(compiler_afterResTypes);
				}
				else {
					Timeout<_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT> to = new Timeout<>();
					_IActionNewPrototypes__afterResTypes anp = (_IActionNewPrototypes__afterResTypes) other;
					_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array = to
							.run(() -> {
								return anp._afterResTypes__NewPrototypeList_1(
										compiler_afterResTypes);
							}, timeoutMilliseconds,
									"afterResTypes_NewPrototypeList",
									cyanMetaobject, env);
					// _Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT
					// array =
					// anp._afterResTypes__NewPrototypeList_1(compiler_afterResTypes);
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
			catch (final RuntimeException e) {
				env.thrownException(annotation, annotation.getFirstSymbol(), e);
			}
			catch (final NoClassDefFoundError e) {
				env.error(annotation.getFirstSymbol(), e.getMessage() + " "
						+ NameServer.messageClassNotFoundException);
			}
			finally {
				env.errorInMetaobjectCatchExceptions(cyanMetaobject);
			}
			if ( prototypeNameCodeList != null ) {
				CyanPackage cyanPackage = compilationUnit.getCyanPackage();
				for (final Tuple2<String, StringBuffer> prototypeNameCode : prototypeNameCodeList) {
					String prototypeName = prototypeNameCode.f1;
					final Tuple2<CompilationUnit, String> t = env.getProject()
							.getCompilerManager().createNewPrototype(
									prototypeName, prototypeNameCode.f2,
									compilationUnit.getCompilerOptions(),
									cyanPackage);
					if ( t != null && t.f2 != null ) {
						env.error(meta.GetHiddenItem
								.getHiddenSymbol(cyanMetaobject.getAnnotation()
										.getFirstSymbol()),
								t.f2);
					}
					cyanPackage.addPrototypeNameAnnotationInfo(prototypeName,
							annotation);
				}
			}
		}
		catch (final CompileErrorException e) {

		}
	}

	private static Set<String> illegalLeftCharSeq;

	static {
		illegalLeftCharSeq = new HashSet<String>();
		for (final String s : new String[] { "[!", "[@", "[&", "[=", "[:", "[|",
				"[*", "[+", "[?", "{*" }) {
			illegalLeftCharSeq.add(s);
		}
	}
	private final Project			project;
	/**
	 * stream to where the errors should be sent
	 */
	private final PrintWriter		printWriter;
	/**
	 * table with all compilation units whose public prototype is an
	 * instantiated generic prototype like "Stack(Int)". That is, the public
	 * prototype is like object Stack<Int> ... end
	 */
	// private Hashtable<String, CompilationUnit> nameRealGenProtoUnitTable;

	/*
	 * list of non-generic prototypes of the program
	 */
	private List<CompilationUnit>	nonGenericCompilationUnitList;
	/**
	 * the program
	 */
	private Program					program;

	HashSet<String>					nameSet;

	/**
	 * the compilation step according to the Figure of Chapter Metaobjects of
	 * the Cyan manual
	 */
	private CompilationStep			compilationStep;

	public CompilationStep getCompilationStep() {
		return compilationStep;
	}

	public void setCompilationStep(CompilationStep compilationStep) {
		this.compilationStep = compilationStep;
	}

	/**
	 * save to each package directory information saying the package was
	 * compiled Successfully by this compiler version
	 *
	 */
	public void saveCompilationInfoPackages(Env env) {
		final List<CyanPackage> packageList = program.getPackageList();
		for (final CyanPackage p : packageList) {
			String path = p.getPackageCanonicalPath();
			if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
				path = path + NameServer.fileSeparatorAsString;
			}
			path = path + NameServer.directoryNameLinkPastFuture;

			appendToFile(path, NameServer.fileNameAfterSuccessfulCompilation,
					"compiler saci version "
							+ CompilerManager.CompilerVersonNumber + "\n",
					env, true);
		}
	}

	/**
	 * load information regarding when the package was last successfully
	 * compiled
	 *
	 */
	public void loadCompilationInfoPackages(Env env) {
		final List<CyanPackage> packageList = program.getPackageList();
		for (final CyanPackage p : packageList) {

			String path = p.getPackageCanonicalPath();
			if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
				path = path + NameServer.fileSeparatorAsString;
			}
			path = path + NameServer.directoryNameLinkPastFuture;
			final List<String> lineList = this.readTextLinesFromFile(path,
					NameServer.fileNameAfterSuccessfulCompilation, env,
					"This file has information regarding the last successful compilation");
			int i = lineList.size() - 1;
			while (i >= 0) {
				if ( lineList.get(i).trim().length() == 0 ) --i;
			}
			if ( i >= 0 ) {
				// found a line with compilation info
				final String line = lineList.get(i);
				final String wordList[] = line.split(" ");
				if ( wordList.length > 0 ) {
					final String last = wordList[wordList.length - 1];
					int n = -1;
					try {
						n = Integer.parseInt(last);
					}
					catch (final NumberFormatException e) {
						env.error(null, "File '" + path
								+ "' is damaged. The last field is not a compiler version number");
					}
					p.setCompilerVersionLastSuccessfulCompilation(n);
				}
			}

		}
	}

	/**
	 * append <code>strToAppend</code> to the end of file
	 * <code>pathDir/fileName</code>. Any errors are signaled using env. If the
	 * directory or the file do not exist, there are created. It is assumed that
	 * the file is a text file. <br>
	 * Be sure to add a {@literal \n} at the end of <code>strToAppend</code> if
	 * you want a new line after each string written.
	 *
	 * If <code>appendOnlyIfDifferent</code> is true, the string
	 * <code>strToAppend</code> will only be appended if it is different from
	 * the last line.
	 * 
	 * @param pathDir
	 * @param fileName
	 * @param strToAppend
	 * @param env
	 */
	public void appendToFile(String pathDir, String fileName,
			String strToAppend, Env env, boolean appendOnlyIfDifferent) {

		String path = pathDir;
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path = path + NameServer.fileSeparatorAsString;
		}
		File f = new File(path);
		if ( !f.exists() ) {
			if ( !f.mkdirs() ) {
				env.error(null, "Cannot create directory " + path);
				return;
			}
		}
		path = path + NameServer.fileSeparatorAsString + fileName;
		f = new File(path);
		if ( !f.exists() ) {
			try {
				f.createNewFile();
			}
			catch (final IOException e) {
				env.error(null, "Cannot create file " + path);
				return;
			}
		}
		if ( appendOnlyIfDifferent ) {
			try (BufferedReader br = new BufferedReader(new FileReader(path))) {
				String line, lastLine = null;
				while ((line = br.readLine()) != null) {
					lastLine = line;
				}
				if ( lastLine != null ) {
					if ( lastLine.endsWith("\n") )
						lastLine = lastLine.substring(0, lastLine.length() - 1);
					String s = strToAppend;
					if ( s.endsWith("\n") ) s = s.substring(0, s.length() - 1);
					if ( s.equals(lastLine) ) return;
				}
			}
			catch (final FileNotFoundException e1) {
				env.error(null, "File '" + path
						+ "' was supposed to exist. But it does not");
				return;
			}
			catch (final IOException e1) {
				env.error(null, "Error reading file '" + path + "'");
				return;
			}
		}
		try (FileWriter fw = new FileWriter(path, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print(strToAppend);
		}
		catch (final IOException e) {
			env.error(null, "Cannot write to file " + path);
			return;
		}

	}

	/**
	 * load text from file pathDir/fileName and appends the contents in
	 * <code>text</code>. Returns false in error. If there is any error, an
	 * error message is issued through <code>env</code> with message
	 * <code>whyMessage</code> appended.
	 */
	public boolean readTextFromFile(String pathDir, String fileName,
			StringBuffer text, Env env, String whyMessage) {

		String path = pathDir;
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path = path + NameServer.fileSeparatorAsString;
		}
		path = path + NameServer.fileSeparatorAsString + fileName;
		final MyFile myFile = new MyFile(path);
		final char[] charArray = myFile.readFile();
		if ( charArray != null ) {
			text.append(charArray);
			return true;
		}
		else {
			env.error(null, "Cannot load file '" + path + "'. " + whyMessage);
			return false;
		}
	}

	/**
	 * load text from file pathDir/fileName and return the contents as an array
	 * of strings, one for each line. If there is any error, null is returned
	 * and an error message is issued through <code>env</code> with message
	 * <code>whyMessage</code> appended.
	 */
	public List<String> readTextLinesFromFile(String pathDir, String fileName,
			Env env, String whyMessage) {

		String path = pathDir;
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path = path + NameServer.fileSeparatorAsString;
		}
		path = path + NameServer.fileSeparatorAsString + fileName;
		final MyFile myFile = new MyFile(path);
		final List<String> lineList = myFile.readLinesFile();
		if ( lineList == null ) {
			env.error(null, "Cannot load file '" + path + "'. " + whyMessage);
		}
		return lineList;
	}

	/**
	 * return a tuple consisting of: a) a file name b) a directory and c) a
	 * package.
	 *
	 *
	 * Parameter fileName contains a file name possibly with a directory such as
	 * <code>python/script0.py</code>. There should be a file
	 * <code>python/script0.py</code> in the hidden directory hiddenDirectory of
	 * package packageName.
	 *
	 * The file name returned does not contain the directory name. The directory
	 * returned is the full name of the directory.
	 *
	 * Return null if the file or directory does not exist
	 * 
	 * @param fileName
	 * @param packageName
	 * @param hiddenDirectory
	 * @return
	 */

	public Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(
			String fileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {

		final CyanPackage cyanPackage = this.project.searchPackage(packageName);
		if ( cyanPackage == null ) {
			return null;
		}
		String path = cyanPackage.getPackageCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + hiddenDirectory.toString()
				+ NameServer.fileSeparatorAsString;

		final int indexdot = fileName
				.lastIndexOf(NameServer.fileSeparatorAsString);
		if ( indexdot < 0 ) {
			// no '.' in the file name, use package of the metaobject
			return new Tuple3<String, String, WrCyanPackage>(fileName, path,
					cyanPackage.getI());
		}
		else {
			final String fn = fileName.substring(indexdot + 1);
			if ( indexdot + 1 > fileName.length() ) {
				return null;
			}
			String dir = fn.substring(0, indexdot);
			if ( !dir.endsWith(NameServer.fileSeparatorAsString) ) {
				dir += NameServer.fileSeparatorAsString;
			}
			return new Tuple3<String, String, WrCyanPackage>(fn, path + dir,
					cyanPackage.getI());
		}

	}

	/**
	 * @param fileName
	 * @param prototypeFileName
	 * @param packageName
	 * @param hiddenDirectory
	 * @return //
	 *         C:\Dropbox\Cyan\cyanTests\simple\\main\Program\--tmpkeepValue_n\keepValue_n
	 */
	public String getPathFileHiddenDirectory(String prototypeFileName,
			String packageName, DirectoryKindPPP hiddenDirectory) {
		String path = this.project.getProjectDir();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		switch (hiddenDirectory.getWhere()) {
		case PROJECT:
			path = path + hiddenDirectory.toString()
					+ NameServer.fileSeparatorAsString
					+ packageName.replace('.', NameServer.fileSeparator)
					+ NameServer.fileSeparatorAsString + prototypeFileName
					+ NameServer.fileSeparatorAsString;
			break;
		case PACKAGE:
			path = path + packageName.replace('.', NameServer.fileSeparator)
					+ NameServer.fileSeparatorAsString
					+ hiddenDirectory.toString()
					+ NameServer.fileSeparatorAsString;
			break;
		case PROTOTYPE:
			path = path + packageName.replace('.', NameServer.fileSeparator)
					+ NameServer.fileSeparatorAsString + prototypeFileName
					+ NameServer.fileSeparatorAsString
					+ hiddenDirectory.toString()
					+ NameServer.fileSeparatorAsString;
			break;
		default:
			break;

		}
		return path;
	}

	private static Tuple2<List<String>, String> getFormalParamList(
			String filename, int start) {

		final List<String> formalParamList = new ArrayList<>();
		String paramListStr = filename.substring(start);
		final int indexRightPar = paramListStr.indexOf(')');
		if ( indexRightPar < 0 ) {
			return new Tuple2<List<String>, String>(null,
					"parameter list of file name is not well formed");
		}
		paramListStr = paramListStr.substring(0, indexRightPar);
		if ( paramListStr.indexOf(' ') >= 0 ) {
			return new Tuple2<List<String>, String>(null,
					"Parameter list cannot contains space");
		}
		// now paramListStr is a list like "T,R,S"
		int i = 0;
		while (i < paramListStr.length()) {
			String s = "";
			while (i < paramListStr.length()
					&& Character.isAlphabetic(paramListStr.charAt(i))) {
				s += paramListStr.charAt(i);
				++i;
			}
			if ( s.length() == 0 ) {
				return new Tuple2<List<String>, String>(null,
						"parameter list of file name is not well formed");
			}
			formalParamList.add(s);
			if ( i >= paramListStr.length() )
				return new Tuple2<List<String>, String>(formalParamList, null);
			if ( paramListStr.charAt(i) != ',' )
				return new Tuple2<List<String>, String>(null,
						"',' expected in parameter list of file name");
			++i;
			if ( i + 1 >= paramListStr.length()
					|| !Character.isAlphabetic(paramListStr.charAt(i)) ) {
				return new Tuple2<List<String>, String>(null,
						"parameter list of file name is not well formed");
			}
		}

		return new Tuple2<List<String>, String>(null,
				"parameter list of file name is not well formed");
	}

	/**
	 * load a text file from the hidden directory given by the enumerate value
	 * <code>hiddenDirectory</code> of package <code>packageName</code>. If
	 * numParameters is 0, the file read is fileName.extension. If numParameters
	 * is, for example, 2, the file read may be something as
	 * <code>fileName(A,B).extension</code>. realParamList is a list of strings
	 * that should replace, in the file read, its parameters. That is, if
	 * realParamList is, in Cyan syntax, <br>
	 * <code>
	 *     [ "Company", "Client" ]<br>
	 * </code><br>
	 * and the complete file name is <code>Relation(A,B).rel</code>, this method
	 * searches for words <code>A</code> and <code>B</code> in the text read
	 * from <code>Relation(A,B).rel</code> and replaces them for
	 * <code>Company</code> and <code>Client</code>.
	 *
	 * This method returns a 5-tuple. The first element is an error messages.
	 * null if none. The second is the text read (with the replacements if
	 * realParamList is not null). The third tuple element is the complete file
	 * name, with the parameters. The fourth is the path of the directory and
	 * the fifth is the Cyan Package in which the file is.
	 *
	 */

	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName, String extension, String packageName,
			DirectoryKindPPP hiddenDirectory, int numParameters,
			List<String> realParamList) {

		final CyanPackage cyanPackage = this.project.searchPackage(packageName);
		if ( cyanPackage == null ) {
			return new Tuple5<FileError, char[], String, String, WrCyanPackage>(
					FileError.package_not_found, null, null, null, null);
		}
		String path = cyanPackage.getPackageCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + hiddenDirectory.toString()
				+ NameServer.fileSeparatorAsString;
		String filenameWithParameters;
		/**
		 * look for a file name with the appropriate number of parameters
		 */
		final File dslDir = new File(path);
		if ( dslDir.list() == null ) {
			/*
			 * "File '" + fileName + (numParameters == 0 ? "" : "(...)") +
			 * fileNameExtension + " of package " + packageName +
			 * "' was not found"
			 */
			return new Tuple5<FileError, char[], String, String, WrCyanPackage>(
					FileError.file_not_found, null, null, null, null);
		}
		String[] formalParamList;
		final Tuple2<String, String[]> fnWithParam = searchFileWithParameters(
				fileName, extension, dslDir, numParameters);
		if ( fnWithParam == null ) {
			/*
			 * "File '" + fileName + (numParameters == 0 ? "" : "(...)") +
			 * fileNameExtension + " of package " + packageName +
			 * "' was not found"
			 */
			return new Tuple5<FileError, char[], String, String, WrCyanPackage>(
					FileError.file_not_found, null, null, null, null);
		}
		filenameWithParameters = fnWithParam.f1;
		formalParamList = fnWithParam.f2;

		final String packagePath = path;
		path = path + filenameWithParameters;
		final MyFile f = new MyFile(path);
		char[] charArray = f.readFile(false, false);
		if ( f.getError() != MyFile.ok_e ) {
			/*
			 * "Error in reading file '" + fileName + (numParameters == 0 ? "" :
			 * "(...)") + fileNameExtension + " of package " + packageName + "'"
			 */
			return new Tuple5<FileError, char[], String, String, WrCyanPackage>(
					FileError.read_error_e, null, null, null, null);
		}
		else {
			// int indexLeftPar = filenameWithParameters.indexOf('(');
			if ( realParamList != null && realParamList.size() > 0 ) {
				if ( numParameters == 0 ) {
					/*
					 * "Internal error in metaobject: file '" + fileName +
					 * (numParameters == 0 ? "" : "(...)") + fileNameExtension +
					 * " of package " + packageName +
					 * "' take parameters. It was expected that it does not take any"
					 */
					return new Tuple5<FileError, char[], String, String, WrCyanPackage>(
							FileError.file_should_not_take_parameters, null,
							null, null, null);
				}
			}
			else if ( numParameters > 0 ) {
				/*
				 * "Internal error in metaobject: file '" + fileName +
				 * (numParameters == 0 ? "" : "(...)") + fileNameExtension +
				 * " of package " + packageName +
				 * "' take parameters. It was expected that it does not take any"
				 * ,
				 */
				return new Tuple5<FileError, char[], String, String, WrCyanPackage>(
						FileError.file_should_take_parameters, null, null, null,
						null);
			}

			if ( numParameters > 0 && realParamList != null ) {
				/*
				 * replace the formal parameters by the real parameters in the
				 * text file / Tuple2<List<String>, String>
				 * messageFormalParamList =
				 * getFormalParamList(filenameWithParameters, indexLeftPar + 1);
				 * if ( messageFormalParamList.f2 != null ) { /* "File '" +
				 * filenameWithParameters + "' of package " + packageName +
				 * "' do not have the correct file name format. Use something like 'comparison(T).concept' or 'comparable(R,S).concept'"
				 * , / return new Tuple5<FileError, char[], String, String,
				 * CyanPackage>(
				 * FileError.file_name_does_not_have_the_correct_name_format,
				 * null, null, null, null); } formalParamList =
				 * messageFormalParamList.f1;
				 */
				final Hashtable<String, String> formalRealTable = new Hashtable<>();
				int k = 0;
				for (final String formalParam : formalParamList) {
					if ( formalRealTable.put(formalParam,
							realParamList.get(k)) != null ) {
						/*
						 * "Error in file '" + fileName +
						 * NameServer.fileSeparatorAsString + packageName +
						 * "'. Its parameters list has two parameters with the same name"
						 */
						return new Tuple5<FileError, char[], String, String, WrCyanPackage>(
								FileError.two_parameters_are_the_same, null,
								null, null, null);
					}
					++k;
				}
				charArray = CompilerManager.replaceOnly(charArray,
						formalRealTable, "",
						ReplacementPolicyInGenericInstantiation.REPLACE_BY_CYAN_VALUE);
			}

			return new Tuple5<FileError, char[], String, String, WrCyanPackage>(
					FileError.ok_e, charArray, filenameWithParameters,
					packagePath, cyanPackage.getI());
		}
	}

	/**
	 * load a text file from the hidden directory given by the enumerate value
	 * <code>hiddenDirectory</code> of the project. If numParameters is 0, the
	 * file read is fileName.extension. If numParameters is, for example, 2, the
	 * file read may be something as <code>fileName(A,B).extension</code>.
	 * realParamList is a list of strings that should replace, in the file read,
	 * its parameters. That is, if realParamList is, in Cyan syntax, <br>
	 * <code>
	 *     [ "Company", "Client" ]<br>
	 * </code><br>
	 * and the complete file name is <code>Relation(A,B).rel</code>, this method
	 * searches for words <code>A</code> and <code>B</code> in the text read
	 * from <code>Relation(A,B).rel</code> and replaces them for
	 * <code>Company</code> and <code>Client</code>.
	 *
	 * This method returns a 4-tuple. The first element is an error messages.
	 * null if none. The second is the text read (with the replacements if
	 * realParamList is not null). The third tuple element is the complete file
	 * name, with the parameters. The fourth is the path of the directory in
	 * which the file is.
	 *
	 */

	public Tuple4<FileError, char[], String, String> readTextFileFromProject(
			String fileName, String extension, DirectoryKindPPP hiddenDirectory,
			int numParameters, List<String> realParamList) {

		/***
		 * WARNING: HIGHLY duplicated code
		 */

		String path = this.project.getProjectCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + hiddenDirectory.toString()
				+ NameServer.fileSeparatorAsString;
		String filenameWithParameters;
		/**
		 * look for a file name with the appropriate number of parameters
		 */
		final File dslDir = new File(path);
		if ( dslDir.list() == null ) {
			/*
			 * "File '" + fileName + (numParameters == 0 ? "" : "(...)") +
			 * fileNameExtension + " of package " + packageName +
			 * "' was not found"
			 */
			return new Tuple4<FileError, char[], String, String>(
					FileError.file_not_found, null, null, null);
		}
		String[] formalParamList;
		final Tuple2<String, String[]> fnWithParam = searchFileWithParameters(
				fileName, extension, dslDir, numParameters);
		if ( fnWithParam == null ) {
			/*
			 * "File '" + fileName + (numParameters == 0 ? "" : "(...)") +
			 * fileNameExtension + " of package " + packageName +
			 * "' was not found"
			 */
			return new Tuple4<FileError, char[], String, String>(
					FileError.file_not_found, null, null, null);
		}
		filenameWithParameters = fnWithParam.f1;
		formalParamList = fnWithParam.f2;

		final String packagePath = path;
		path = path + filenameWithParameters;
		final MyFile f = new MyFile(path);
		char[] charArray = f.readFile(false, false);
		if ( f.getError() != MyFile.ok_e ) {
			/*
			 * "Error in reading file '" + fileName + (numParameters == 0 ? "" :
			 * "(...)") + fileNameExtension + " of package " + packageName + "'"
			 */
			return new Tuple4<FileError, char[], String, String>(
					FileError.read_error_e, null, null, null);
		}
		else {
			// int indexLeftPar = filenameWithParameters.indexOf('(');
			if ( realParamList != null && realParamList.size() > 0 ) {
				if ( numParameters == 0 ) {
					/*
					 * "Internal error in metaobject: file '" + fileName +
					 * (numParameters == 0 ? "" : "(...)") + fileNameExtension +
					 * " of package " + packageName +
					 * "' take parameters. It was expected that it does not take any"
					 */
					return new Tuple4<FileError, char[], String, String>(
							FileError.file_should_not_take_parameters, null,
							null, null);
				}
			}
			else if ( numParameters > 0 ) {
				return new Tuple4<FileError, char[], String, String>(
						FileError.file_should_take_parameters, null, null,
						null);
			}

			if ( numParameters > 0 && realParamList != null ) {
				/*
				 * replace the formal parameters by the real parameters in the
				 * text file /
				 */
				final Hashtable<String, String> formalRealTable = new Hashtable<>();
				int k = 0;
				for (final String formalParam : formalParamList) {
					if ( formalRealTable.put(formalParam,
							realParamList.get(k)) != null ) {
						return new Tuple4<FileError, char[], String, String>(
								FileError.two_parameters_are_the_same, null,
								null, null);
					}
					++k;
				}
				charArray = CompilerManager.replaceOnly(charArray,
						formalRealTable, "",
						ReplacementPolicyInGenericInstantiation.REPLACE_BY_CYAN_VALUE);
			}

			return new Tuple4<FileError, char[], String, String>(FileError.ok_e,
					charArray, filenameWithParameters, packagePath);
		}
	}

	/**
	 * read a binary file from the hidden directory given by the enumerate value
	 * <code>hiddenDirectory</code> of package <code>packageName</code>. The
	 * file read is fileName.extension.
	 *
	 * This method returns a 5-tuple. The first element is an error messages.
	 * null if none. The second is the data read. The third tuple element is the
	 * complete file name. The fourth is the path of the directory and the fifth
	 * is the Cyan Package in which the file is.
	 *
	 */

	public Tuple5<FileError, byte[], String, String, CyanPackage> readBinaryFileFromPackage(
			String fileName, String extension, String packageName,
			DirectoryKindPPP hiddenDirectory) {

		final CyanPackage cyanPackage = this.project.searchPackage(packageName);
		if ( cyanPackage == null ) {
			return new Tuple5<FileError, byte[], String, String, CyanPackage>(
					FileError.package_not_found, null, null, null, null);
		}
		String path = cyanPackage.getPackageCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + hiddenDirectory.toString()
				+ NameServer.fileSeparatorAsString;
		final String filenameExtension = fileName + "." + extension;

		final String packagePath = path;
		path = path + filenameExtension;

		final Path aPath = Paths.get(path);
		byte[] byteArray = null;
		try {
			byteArray = Files.readAllBytes(aPath);
		}
		catch (final IOException e) {
			return new Tuple5<FileError, byte[], String, String, CyanPackage>(
					FileError.cannot_be_read_e, null, null, null, null);
		}

		return new Tuple5<FileError, byte[], String, String, CyanPackage>(
				FileError.ok_e, byteArray, filenameExtension, packagePath,
				cyanPackage);
	}

	/**
	 * read a binary file from the hidden directory given by the enumerate value
	 * <code>hiddenDirectory</code> of the project. The file read is
	 * fileName.extension.
	 *
	 * This method returns a 4-tuple. The first element is an error messages.
	 * null if none. The second is the data read. The third tuple element is the
	 * complete file name. The fourth is the path of the directory in which the
	 * file is.
	 *
	 */

	public Tuple4<FileError, byte[], String, String> readBinaryFileFromProject(
			String fileName, String extension,
			DirectoryKindPPP hiddenDirectory) {

		/***
		 * WARNING: HIGHLY duplicated code
		 */

		String path = this.project.getProjectCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + hiddenDirectory.toString()
				+ NameServer.fileSeparatorAsString;

		final String filenameExtension = fileName + "." + extension;

		final String packagePath = path;
		path = path + filenameExtension;

		final Path aPath = Paths.get(path);
		byte[] byteArray = null;
		try {
			byteArray = Files.readAllBytes(aPath);
		}
		catch (final IOException e) {
			return new Tuple4<FileError, byte[], String, String>(
					FileError.cannot_be_read_e, null, null, null);
		}

		return new Tuple4<FileError, byte[], String, String>(FileError.ok_e,
				byteArray, filenameExtension, packagePath);
	}

	/**
	 * @param numParameters
	 * @param filenameWithParameters
	 * @param dslDir
	 * @return
	 */
	public Tuple2<String, String[]> searchFileWithParameters(String fileName,
			String extension, File dslDir, int numParameters) {
		if ( numParameters == 0 ) {
			fileName += "." + extension;
			for (final String fname : dslDir.list()) {
				if ( fname.equals(fileName) )
					return new Tuple2<String, String[]>(fname, null);
			}
			return null;
		}
		else {
			for (final String fname : dslDir.list()) {

				final int indexLeftPar = fname.indexOf('(');
				String s;
				if ( indexLeftPar > 0 ) {
					final String name = fname.substring(0, indexLeftPar);
					if ( name.equals(fileName) ) {
						s = fname.substring(indexLeftPar + 1);
						final int indexRightPar = s.indexOf(')');
						if ( indexRightPar > 0 ) {
							String ext = "";
							if ( indexRightPar < s.length() - 1 )
								ext = s.substring(indexRightPar + 2);
							s = s.substring(0, indexRightPar);
							if ( s.length() > 0 ) {
								final String paramList[] = s.split(",");
								if ( paramList.length == numParameters
										&& ext.equals(extension) ) {
									return new Tuple2<String, String[]>(fname,
											paramList);
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * load text file <code>fileName</code> from the data directory of package
	 * <code>packageName</code>. Return a tuple with an error code and the read
	 * char array.
	 *
	 * @param fileName
	 * @param packageName
	 * @return
	 */

	public Tuple2<FileError, char[]> readTextDataFileFromPackage(
			String fileName, String packageName) {

		final CyanPackage cyanPackage = this.project.searchPackage(packageName);
		if ( cyanPackage == null ) {
			return new Tuple2<FileError, char[]>(FileError.package_not_found,
					null);
		}
		String path = cyanPackage.getPackageCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + NameServer.directoryNamePackageData
				+ NameServer.fileSeparatorAsString + fileName;
		final MyFile f = new MyFile(path);
		final char[] charArray = f.readFile(false, false);
		if ( f.getError() != MyFile.ok_e ) {
			return new Tuple2<FileError, char[]>(FileError.cannot_be_read_e,
					null);
		}
		else
			return new Tuple2<FileError, char[]>(FileError.ok_e, charArray);
	}

	/**
	 * load text file <code>fileName</code> from the data directory of package
	 * <code>packageName</code>. Issue <code>errorMessage</code> in error with
	 * symbol <code>sym</code>
	 * 
	 * @param fileName
	 * @param packageName
	 * @return
	 */

	public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(
			String fileName, String packageName) {
		final CyanPackage cyanPackage = this.project.searchPackage(packageName);
		if ( cyanPackage == null ) {
			return new Tuple2<FileError, byte[]>(FileError.package_not_found,
					null);
		}
		String path = cyanPackage.getPackageCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + NameServer.directoryNamePackageData
				+ NameServer.fileSeparatorAsString + fileName;

		final Path aPath = Paths.get(path);
		byte[] byteArray = null;
		try {
			byteArray = Files.readAllBytes(aPath);
		}
		catch (final IOException e) {
			return new Tuple2<FileError, byte[]>(FileError.cannot_be_read_e,
					null);
		}
		return new Tuple2<FileError, byte[]>(FileError.ok_e, byteArray);
	}

	public FileError writeBinaryDataFileToPackage(byte[] data, String fileName,
			String packageName) {
		return this.saveBinaryDataFileToPackage(data, fileName, packageName);
	}

	public FileError saveBinaryDataFileToPackage(byte[] data, String fileName,
			String packageName) {

		final CyanPackage cyanPackage = this.project.searchPackage(packageName);
		if ( cyanPackage == null ) {
			return FileError.package_not_found;
		}
		String path = cyanPackage.getPackageCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + NameServer.directoryNamePackageData
				+ NameServer.fileSeparatorAsString;
		final File pathFile = new File(path);
		if ( !pathFile.exists() ) {
			pathFile.mkdirs();
		}
		path = path + fileName;

		final Path aPath = Paths.get(path);
		try {
			Files.write(aPath, data);
		}
		catch (final IOException e) {
			return FileError.write_error_e;
		}
		return FileError.ok_e;
	}

	public FileError writeBinaryDataFileToProject(byte[] data, String fileName,
			String packageName) {

		final CyanPackage cyanPackage = this.project.searchPackage(packageName);
		if ( cyanPackage == null ) {
			return FileError.package_not_found;
		}
		String path = this.project.getProjectCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + NameServer.directoryNamePackageData
				+ NameServer.fileSeparatorAsString;
		final File pathFile = new File(path);
		if ( !pathFile.exists() ) {
			pathFile.mkdirs();
		}
		path = path + fileName;

		final Path aPath = Paths.get(path);
		try {
			Files.write(aPath, data);
		}
		catch (final IOException e) {
			return FileError.write_error_e;
		}
		return FileError.ok_e;
	}

	/**
	 * delete all files of the directory dirName of the test directory of
	 * package packageName
	 */
	public boolean deleteDirOfTestDir(String dirName, String packageName) {

		String path = this.project.getProjectDir();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + NameServer.directoryNamePackageTests
				+ NameServer.fileSeparatorAsString
				+ packageName.replace(NameServer.fileSeparator, '.')
				+ NameServer.fileSeparatorAsString + dirName;
		final File fpath = new File(path);
		return MyFile.deleteFileDirectory(fpath);
	}

	/**
	 * write 'data' to file 'fileName' that is created in the test directory of
	 * package packageName. Return an object of FileError indicating any errors.
	 */
	public FileError writeTestFileTo(StringBuffer data, String fileName,
			String dirName, String packageName) {

		String path = this.project.getProjectDir();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + // NameServer.directoryNamePackageTests +
						// NameServer.fileSeparatorAsString +
				packageName.replace(".", NameServer.fileSeparatorAsString)
				+ NameServer.fileSeparatorAsString
				+ NameServer.directoryNamePackageTests
				+ NameServer.fileSeparatorAsString;
		if ( dirName != null && dirName.length() > 0 ) {
			path = path + dirName + NameServer.fileSeparatorAsString;
		}
		final File fpath = new File(path);
		fpath.mkdirs();

		path += fileName;

		try {
			final Path aPath = Paths.get(path);
			Files.write(aPath, data.toString().getBytes());
		}
		catch (final IOException e) {
			return FileError.write_error_e;
		}
		return FileError.ok_e;

	}

	/**
	 * write 'str' to file 'fileName' of the directory 'hiddenDirectory' of the
	 * prototype of package 'packageName' that is in file 'prototypeFileName'
	 * 
	 * @param charArray
	 * @param fileName
	 * @param prototypeFileName
	 * @param packageName
	 * @param hiddenDirectory
	 * @return
	 */
	public FileError writeTextFile(String str, String fileName,
			String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {

		if ( hiddenDirectory.isReadOnly() ) {
			return FileError.write_error_e;
		}

		String path = getPathFileHiddenDirectory(prototypeFileName, packageName,
				hiddenDirectory);
		final File fpath = new File(path);
		fpath.mkdirs();

		if ( path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += fileName;
		}
		else {
			path += NameServer.fileSeparatorAsString + fileName;
		}

		try (FileWriter f = new FileWriter(path)) {
			f.write(str);
		}
		catch (final IOException e) {
			return FileError.write_error_e;
		}
		return FileError.ok_e;
	}

	/**
	 * write 'charArray' to file 'fileName' of the directory 'hiddenDirectory'
	 * of the prototype of package 'packageName' that is in file
	 * 'prototypeFileName'
	 * 
	 * @param charArray
	 * @param fileName
	 * @param prototypeFileName
	 * @param packageName
	 * @param hiddenDirectory
	 * @return
	 */
	public FileError writeTextFile(char[] charArray, String fileName,
			String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {

		if ( hiddenDirectory.isReadOnly() ) {
			return FileError.write_error_e;
		}

		String path = getPathFileHiddenDirectory(prototypeFileName, packageName,
				hiddenDirectory);
		final File fpath = new File(path);
		fpath.mkdirs();

		if ( path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += fileName;
		}
		else {
			path += NameServer.fileSeparatorAsString + fileName;
		}

		try (FileWriter f = new FileWriter(path)) {
			f.write(charArray);
		}
		catch (final IOException e) {
			return FileError.write_error_e;
		}
		return FileError.ok_e;
	}

	/**
	 * @param fileName
	 * @param prototypeFileName
	 * @param packageName
	 * @param hiddenDirectory
	 * @return
	 */
	public String getPathFileHiddenDirectory(String prototypeFileName,
			WrCyanPackage aPackage, DirectoryKindPPP hiddenDirectory) {
		String path = this.project.getProjectDir();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		String packagePath = meta.GetHiddenItem.getHiddenCyanPackage(aPackage)
				.getPackageCanonicalPath();
		if ( !packagePath.endsWith(NameServer.fileSeparatorAsString) ) {
			packagePath += NameServer.fileSeparatorAsString;
		}
		switch (hiddenDirectory.getWhere()) {
		case PROJECT:
			path = path + hiddenDirectory.toString()
					+ NameServer.fileSeparatorAsString
					+ aPackage.getName().replace('.', NameServer.fileSeparator)
					+ prototypeFileName + NameServer.fileSeparatorAsString;
			break;
		case PACKAGE:
			path = packagePath + hiddenDirectory.toString()
					+ NameServer.fileSeparatorAsString;
			break;
		case PROTOTYPE:
			path = packagePath + prototypeFileName
					+ NameServer.fileSeparatorAsString
					+ hiddenDirectory.toString()
					+ NameServer.fileSeparatorAsString;
			break;
		default:
			break;

		}
		return path;
	}

	/**
	 * write 'charArray' to file 'fileName' of the directory 'hiddenDirectory'
	 * of the prototype of package 'packageName' that is in file
	 * 'prototypeFileName'
	 * 
	 * @param charArray
	 * @param fileName
	 * @param prototypeFileName
	 * @param packageName
	 * @param hiddenDirectory
	 * @return
	 */
	public FileError writeTextFile(char[] charArray, String fileName,
			String prototypeFileName, WrCyanPackage aPackage,
			DirectoryKindPPP hiddenDirectory) {

		if ( hiddenDirectory.isReadOnly() ) {
			return FileError.write_error_e;
		}

		String path = getPathFileHiddenDirectory(prototypeFileName, aPackage,
				hiddenDirectory);
		final File fpath = new File(path);
		fpath.mkdirs();

		if ( path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += fileName;
		}
		else {
			path += NameServer.fileSeparatorAsString + fileName;
		}
		try (FileWriter f = new FileWriter(path)) {
			f.write(charArray);
		}
		catch (final IOException e) {
			return FileError.write_error_e;
		}
		return FileError.ok_e;
	}

	/**
	 * return the absolute path in which the file <code>fileName</code> would be
	 * stored in the data directory of package <code>packageName</code>. If the
	 * package does not exist or the fileName is invalid, returns null
	 * 
	 * @param fileName
	 * @param packageName
	 * @return
	 */
	public String pathDataFilePackage(String fileName, String packageName) {
		final CyanPackage cyanPackage = this.project.searchPackage(packageName);
		if ( cyanPackage == null ) {
			return null;
		}
		String path = cyanPackage.getPackageCanonicalPath();
		if ( !path.endsWith(NameServer.fileSeparatorAsString) ) {
			path += NameServer.fileSeparatorAsString;
		}
		path = path + NameServer.directoryNamePackageData
				+ NameServer.fileSeparatorAsString + fileName;
		return path;
	}

	/**
	 * replace all occurrences of keys of formalRealTable by values of this
	 * table. Return the modified char array
	 */

	public static char[] replaceOnly(char[] text,
			Hashtable<String, String> formalRealTable,
			String currentPrototypeName,
			ReplacementPolicyInGenericInstantiation replacementPolicy) {
		final int StepSize = 200;
		final int sizeText = text.length;
		int sizeNewText = sizeText;
		char[] newText = new char[sizeNewText + 1];
		int indexText = 0;
		int indexNewText = 0;
		while (indexText < sizeText) {
			// look for an identifier
			if ( text[indexText] == '_'
					|| Character.isLetter(text[indexText]) ) {
				String id = "";
				while (indexText < sizeText && text[indexText] == '_'
						|| Character.isLetterOrDigit(text[indexText])) {
					id = id + text[indexText];
					if ( indexNewText >= sizeNewText - 1 ) {
						final char[] newNewText = new char[sizeNewText
								+ StepSize];
						AnnotationAt.copyCharArray(newNewText, newText);
						sizeNewText = sizeNewText + StepSize;
						newText = newNewText;
					}
					newText[indexNewText++] = text[indexText];
					++indexText;
				}
				/* found an identifier (a sequence of letters, digits and _ ) */
				String value = formalRealTable.get(id);
				if ( value == null
						&& id.equals("Java_Current__Prototype___Name") ) {
					value = currentPrototypeName;
				}

				if ( value != null ) {

					switch (replacementPolicy) {
					case NO_REPLACEMENT:
						// this should not occur because this method should not
						// have been
						// called with replacementPolicy equal to NO_REPLACEMENT
						value = id;
						break;
					case REPLACE_BY_CYAN_VALUE:
						// value is already the Cyan value
						break;
					case REPLACE_BY_JAVA_VALUE:
						value = MetaHelper.getJavaName(value);
					}

					indexNewText = indexNewText - id.length();
					for (int i = 0; i < value.length(); ++i) {
						if ( indexNewText >= sizeNewText - 1 ) {
							final char[] newNewText = new char[sizeNewText
									+ StepSize];
							AnnotationAt.copyCharArray(newNewText, newText);
							sizeNewText = sizeNewText + StepSize;
							newText = newNewText;
						}
						newText[indexNewText++] = value.charAt(i);
					}
				}
			}
			else {
				if ( indexNewText >= sizeNewText - 1 ) {
					final char[] newNewText = new char[sizeNewText + StepSize];
					AnnotationAt.copyCharArray(newNewText, newText);
					sizeNewText = sizeNewText + StepSize;
					newText = newNewText;
				}
				newText[indexNewText++] = text[indexText];
				++indexText;
			}

		}
		newText[indexNewText] = '\0';
		return newText;
	}

	public static Tuple2<String, String> separatePackagePrototype(
			String fullName) {
		String packageName = "";
		String prototypeName = "";
		String s = fullName;
		final int indexLessThan = s.indexOf('<');
		if ( indexLessThan >= 0 ) {
			// eliminates the parameters to the generic prototype
			// "Tuple<main.Person, Int>" becomes "Tuple" and
			// "cyan.lang.tmp.Tuple<main.Person, Int>" becomes
			// "cyan.lang.tmp.Tuple"
			prototypeName = s.substring(indexLessThan);
			s = s.substring(0, indexLessThan);
		}
		final int i = s.lastIndexOf('.');
		if ( i >= 0 ) {
			// there is a package
			packageName = s.substring(0, i);
			prototypeName = s.substring(i + 1) + prototypeName;
		}
		else {
			prototypeName = s + prototypeName;
		}

		return new Tuple2<String, String>(packageName, prototypeName);
	}

	public class VersionFeatures {
		public VersionFeatures(int version, String[] featureList) {
			this.version = version;
			this.featureList = featureList;
		}

		public int		version;
		public String[]	featureList;
	}

	/*
	 * the two methods below were taken from
	 * https://stackoverflow.com/questions/134492/how-to-serialize-an-object-
	 * into-a-string
	 */

	/** Read the object from Base64 string. */
	static Object fromString(String s)
			throws IOException, ClassNotFoundException {
		final byte[] data = Base64.getDecoder().decode(s);
		final ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(data));
		final Object obj = ois.readObject();
		ois.close();
		return obj;
	}

	/** Write the object to a Base64 string. */
	static String toString(Serializable obj) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	public final static String								CompilerVersionName		= "photon 0.0001";
	// it is in fact CompilerVersonNumber/10.000
	public final static int									CompilerVersonNumber	= 1;
	/**
	 * for each version number, there is a list of features that it has or that
	 * were added since the last version
	 */
	public VersionFeatures[]								versionFeaturesList		= {
			new VersionFeatures(1, new String[] { "fun", "multimethod" }) };

	/**
	 * list of all paths of package directories whose metaobjects have already
	 * been loaded
	 */
	public static final Map<String, List<CyanMetaobject>>	loadedPackagePathSet	= new HashMap<>();
}
