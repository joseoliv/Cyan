package metaRealClasses;

import java.util.List;
import java.util.Set;
import ast.CyanPackage;
import ast.Expr;
import ast.ExprIdentStar;
import ast.MethodSignature;
import ast.Statement;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.ICompiler_dsl;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCyanPackage;
import meta.WrExpr;
import meta.WrExprIdentStar;
import meta.WrMethodSignature;
import meta.WrStatement;
import meta.WrSymbol;
import saci.Compiler;
import saci.CompilerManager;

public class Compiler_dsl implements ICompiler_dsl {


	public Compiler_dsl(CompilerManager compilerManager, Compiler compiler) {
		this.compilerManager = compilerManager;
		this.compiler = compiler;
	}


	@Override
	public Object getProgramValueFromKey(String variableName) {
		return compilerManager.getProgram().getProject().getProgramValueFromKey(variableName);
	}

	@Override
	public Set<String> getProgramKeyValueSet(String variableName) {
		return compilerManager.getProgram().getProject().getProgramKeyValueSet(variableName);
	}


	@Override
	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(String fileName, String extension, String packageName, DirectoryKindPPP hiddenDirectory, int numParameters,
			List<String> realParamList) {
				return compilerManager.
						readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);

			}

	@Override
	public Tuple4<FileError, char[], String, String> readTextFileFromProgram(String fileName, String extension, DirectoryKindPPP hiddenDirectory, int numParameters, List<String> realParamList) {
		return compilerManager.readTextFileFromProject(fileName, extension, hiddenDirectory, numParameters, realParamList);
	}

	@Override
	public Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(String fileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return compilerManager.
				getAbsolutePathHiddenDirectoryFile(fileName, packageName, hiddenDirectory);
	}

	@Override
	public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(String fileName, String packageName) {
		return compilerManager.
				readBinaryDataFileFromPackage(fileName, packageName);

	}


	@Override
	public boolean deleteDirOfTestDir(String dirName) {
		String testPackageName = this.getPackageNameTest();
		return compilerManager.deleteDirOfTestDir(dirName, testPackageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		String testPackageName = this.getPackageNameTest();
		return compilerManager.
				writeTestFileTo(data, fileName, dirName, testPackageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
		return compilerManager.
				writeTestFileTo(data, fileName, dirName, packageName);
	}


	@Override
	public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return compilerManager.writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	@Override
	public FileError writeTextFile(String str, String fileName, String prototypeFileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return compilerManager.writeTextFile(str, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	@Override
	public String getPathFileHiddenDirectory(String prototypeFileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return compilerManager.getPathFileHiddenDirectory(prototypeFileName, packageName, hiddenDirectory);

	}


	public CyanPackage getCyanPackage() {
		return cyanPackage;
	}


	public void setCyanPackage(CyanPackage cyanPackage) {
		this.cyanPackage = cyanPackage;
	}


	@Override
	public String getPackageNameTest() {
		return cyanPackage.getPackageName();
	}


	@Override
	public void next() {
		compiler.next();
	}

	@Override
	public WrSymbol getSymbol() {
		return compiler.getSymbol().getI();
	}


	@Override
	public WrExpr expr() {
		Expr e = compiler.expr();
		return e == null ? null : e.getI();
	}

	@Override
	public WrExpr type() {
		Expr e = compiler.type();
		return e == null ? null : e.getI();
	}

	@Override
	public boolean startType(Token t) {
		return Compiler.startType(t);
	}


	@Override
	public WrStatement statement() {
		Statement s = compiler.statement();
		return s == null ? null : s.getI();
	}

	@Override
	public WrExprIdentStar parseSingleIdent() {
		Expr e = compiler.parseIdent();
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
	public boolean symbolCanStartExpr(WrSymbol symbol) {
		return Compiler.startExpr(meta.GetHiddenItem.getHiddenSymbol(symbol));
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


	private CyanPackage cyanPackage;

	private CompilerManager compilerManager;

	private Compiler compiler;
}
