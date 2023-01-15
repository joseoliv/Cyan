package metaRealClasses;

import java.util.List;
import java.util.Set;
import ast.CompilationUnit;
import ast.CyanPackage;
import ast.Prototype;
import meta.CompilationStep;
import meta.CyanMetaobject;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.ICompilerPrototypeLater_parsing;
import meta.MetaHelper;
import meta.ParsingPhase;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCompilationUnit;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrPrototype;
import meta.WrSymbol;
import saci.Compiler;
import saci.Env;

public class CompilerGenericPrototype_parsing implements ICompilerPrototypeLater_parsing {


	public CompilerGenericPrototype_parsing(Compiler compiler, Prototype prototype) {
		this.compiler = compiler.clone();
		this.prototype = prototype;
	}


	@Override
	public WrPrototype getPrototype() {
		return prototype.getI();
	}

	@Override
	public void error(WrSymbol symbol, String message) {
		compiler.error2(meta.GetHiddenItem.getHiddenSymbol(symbol), message);
	}



	@Override
	public void errorAtGenericPrototypeInstantiation(String errorMessage) {
		final CompilationUnit cunit = compiler.getCurrentPrototype().getCompilationUnit();
		if ( cunit == null ) {
			compiler.error2(null,  "Internal error: current compilation unit does not exist at Compiler_semAn");
			return ;
		}
		final String packageNameInstantiation = cunit.getPackageNameInstantiation();
		if ( packageNameInstantiation == null ) {
			compiler.error2(null,  "Attempt to sign an error in a generic prototype instantiation outside a generic prototype instantiation");
		}
		final String prototypeNameInstantiation = cunit.getPrototypeNameInstantiation();
		final Prototype prototypeInstantiation = compiler.searchPackagePrototype(packageNameInstantiation, prototypeNameInstantiation);
		if ( prototypeInstantiation == null ) {
			compiler.error2(null,  "Internal error: prototype '" + prototypeNameInstantiation +
					"' of package '" + packageNameInstantiation + "' was not found");
			return ;
		}

		final int lineNumberInstantiation = cunit.getLineNumberInstantiation();


		final int columnNumberInstantiation = cunit.getColumnNumberInstantiation();
		prototypeInstantiation.getCompilationUnit().error(lineNumberInstantiation, columnNumberInstantiation, errorMessage);
	}

	@Override
	public Object getProgramValueFromKey(String variableName) {
		return compiler.getProject().getProgramValueFromKey(variableName);
	}

	@Override
	public Set<String> getProgramKeyValueSet(String variableName) {
		return compiler.getProject().getProgramKeyValueSet(variableName);
	}



	@Override
	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName,
			String extension,
			String packageName,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return compiler.getProject().getCompilerManager().
				readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);

	}


	@Override
	public Tuple4<FileError, char[], String, String> readTextFileFromProgram(
			String fileName,
			String extension,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return compiler.getProject().getCompilerManager().readTextFileFromProject(fileName, extension, hiddenDirectory, numParameters, realParamList);
	}


	@Override
	public Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(String fileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return compiler.getProject().getCompilerManager().
				getAbsolutePathHiddenDirectoryFile(fileName, packageName, hiddenDirectory);
	}


	@Override
	public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(String fileName, String packageName) {
		return compiler.getProject().getCompilerManager().
				readBinaryDataFileFromPackage(fileName, packageName);

	}


	@Override
	public boolean deleteDirOfTestDir(String dirName) {
		final String testPackageName = this.getPackageNameTest();
		return compiler.getProject().getCompilerManager().deleteDirOfTestDir(dirName, testPackageName);
	}


	/**
	 * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		final String testPackageName = this.getPackageNameTest();
		return compiler.getProject().getCompilerManager().
				writeTestFileTo(data, fileName, dirName, testPackageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
		return compiler.getProject().getCompilerManager().
				writeTestFileTo(data, fileName, dirName, packageName);
	}


	@Override
	public String getPackageNameTest() {
		return compiler.getCompilationUnit().getPackageName() + MetaHelper.suffixTestPackageName;
	}

	@Override
	public ParsingPhase getParsingPhase() {
		if ( compiler.getCompilationStep() == CompilationStep.step_4 ) {
			return ParsingPhase.dpaGeneric;
		}
		else if ( compiler.getCompilationStep() == CompilationStep.step_7 ) {
			return ParsingPhase.dpaNonGeneric;
		}
		else {
			return null;
		}
	}

	@Override
	public void addToListAfter_afterResTypes(CyanMetaobject annotation) {
		compiler.addToListAfter_afterResTypes(annotation);
	}

	@Override
	public WrCompilationUnit getCompilationUnit() {
		CompilationUnit cunit = compiler.getCompilationUnit();
		return cunit == null ? null : cunit.getI();
	}



	@Override
	public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return compiler.getProject().getCompilerManager().writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);
	}




	@Override
	public FileError writeTextFile(String str, String fileName, String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return compiler.getProject().getCompilerManager().writeTextFile(str, fileName, prototypeFileName, packageName, hiddenDirectory);
	}


	@Override
	public
	String getPathFileHiddenDirectory(String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return compiler.getProject().getCompilerManager().getPathFileHiddenDirectory(prototypeFileName, packageName, hiddenDirectory);

	}

	@Override
	public WrEnv getEnv() {
		Env env = new Env(compiler.getProject());
		env.setCurrentCompilationUnit(compiler.getCompilationUnit());
		env.setCurrentPrototype(compiler.getCurrentPrototype());
		env.setCurrentMethod(compiler.getCurrentMethod());
		WrEnv wrenv = new WrEnv(env, env.getCompilationStep());
		return wrenv;
	}


	@Override
	public Tuple5<FileError, byte[], String, String, CyanPackage> readBinaryFileFromPackage(
			String fileName, String extension, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return compiler.getProject().getCompilerManager().readBinaryFileFromPackage(fileName, extension, packageName, hiddenDirectory);
	}



	@Override
	public Tuple4<FileError, byte[], String, String> readBinaryFileFromProject(
			String fileName, String extension,
			DirectoryKindPPP hiddenDirectory) {
		return compiler.getProject().getCompilerManager().readBinaryFileFromProject(fileName, extension, hiddenDirectory);
	}


	private final Compiler compiler;
	private final Prototype prototype;

}
