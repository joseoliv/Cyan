package meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import ast.AnnotationMacroCall;
import ast.CyanPackage;
import ast.Expr;
import ast.ICalcInternalTypes;
import ast.Statement;
import lexer.Symbol;
import metaRealClasses.Compiler_parsing;
import saci.Compiler;

public class CompilerMacro_parsing implements ICompilerMacro_parsing {

	public CompilerMacro_parsing(Compiler compiler) {
		this.compiler = compiler.clone();
		this.wasErrors = false;
		exprStatList = new ArrayList<>();
		lastSymbol = null;
	}


	public AnnotationMacroCall getMacroCall() {
		return macroCall;
	}

	public void setCyanMetaobjectMacro(AnnotationMacroCall macroCall) {
		this.macroCall = macroCall;
	}

	@Override
	public void next() {
		lastSymbol = compiler.getSymbol();
		compiler.next();
	}

	@Override
	public WrSymbol getSymbol() {
		return compiler.getSymbol().getI();
	}

	@Override
	public boolean symbolCanStartExpr(WrSymbol symbol) {
		return Compiler.canStartMessagekeyword(symbol.token);
	}

	@Override
	public WrExpr expr() {
		Expr e = compiler.expr();
		exprStatList.add(e);
		return e.getI();
	}

	@Override
	public WrExpr exprBasicTypeLiteral() {
		Expr e = compiler.exprLiteral();
		exprStatList.add(e);
		return e.getI();
	}


	@Override
	public WrStatement statement() {
		Statement s = compiler.statement();
		exprStatList.add(s);
		return s.getI();
	}

	@Override
	public WrExpr functionDec() {
		Expr e = compiler.functionDec();
		exprStatList.add(e);
		return e.getI();
	}

	@Override
	public void error(WrSymbol sym, String specificMessage) {
		wasErrors = true;
		// 	public void error2(boolean throwException, Symbol sym, String msg, boolean checkMessage) {
		compiler.error2(true, sym == null ? null : sym.hidden, specificMessage, true);
	}

	@Override
	public boolean getThereWasErrors() {
		return wasErrors;
	}


	@Override
	public void setThereWasErrors(boolean wasError) {
		this.wasErrors = wasError;
	}


	public List<ICalcInternalTypes> getExprStatList() {
		return exprStatList;
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
	public WrSymbol getLastSymbol() {
		return lastSymbol.getI();
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
		String testPackageName = this.getPackageNameTest();
		return compiler.getProject().getCompilerManager().deleteDirOfTestDir(dirName, testPackageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		String testPackageName = this.getPackageNameTest();
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


	public Compiler compiler;
	private AnnotationMacroCall macroCall;

	private boolean wasErrors;

	/**
	 * list of expressions and statements returned by calls to {@link Compiler_parsing#expr()} and, in the future, to method statement()
	 */
	private List<ICalcInternalTypes> exprStatList;
	/**
	 * last symbol of the macro
	 */
	private Symbol lastSymbol;

}
