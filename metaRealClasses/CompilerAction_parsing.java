package metaRealClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import ast.CompilationUnit;
import ast.CyanPackage;
import ast.GenericParameter;
import ast.MethodDec;
import ast.Prototype;
import meta.CompilationStep;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.ICompilerAction_parsing;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCompilationUnit;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrExprAnyLiteral;
import meta.WrMethodDec;
import meta.WrPrototype;
import meta.WrSymbol;
import saci.Compiler;
import saci.Env;

public class CompilerAction_parsing implements ICompilerAction_parsing {

	public CompilerAction_parsing(Compiler compiler) {
		this.compiler = compiler.clone();
	}

	@Override
	public List<List<String>> getGenericPrototypeArgListList() {
		final Prototype pu = compiler.getCurrentPrototype();
		if ( pu == null || pu.getGenericParameterListList() == null || pu.getGenericParameterListList().size() == 0 )
			return null;
		else {
			// current prototype is generic
			final List<List<String>> strListList = new ArrayList<>();
			for ( final List<GenericParameter> gpList: pu.getGenericParameterListList() ) {
				final List<String> strList = new ArrayList<>();
				for ( final GenericParameter gp: gpList ) {
					strList.add(gp.getParameter().asString());
				}
				strListList.add(strList);
			}
			return strListList;
		}
	}

	@Override
	public String getCurrentPrototypeName() {
		if ( compiler.getCurrentPrototype() != null ) {
			return compiler.getCurrentPrototype().getName();
		}
		else
			return null;
	}

	@Override
	public String getCurrentPrototypeId() {
		if ( compiler.getCurrentPrototype() != null ) {
			return compiler.getCurrentPrototype().getSimpleName();
		}
		else
			return null;
	}

	@Override
	public WrMethodDec getCurrentMethod() {
		MethodDec im = compiler.getCurrentMethod();
		if ( im != null ) {
			return im.getI();
		}
		return null;
	}


	@Override
	public Object getProgramValueFromKey(String variableName) {
		return compiler.getProject().getProgramValueFromKey(variableName);
	}

	@Override
	public Set<String> getProgramKeyValueSet(String variableName) {
		return compiler.getProject().getProgramKeyValueSet(variableName);
	}

	//#$ {
//
//	/**
//	 * if the current program unit was created from a generic prototype instantiation,
//	 * the instantiation is in package packageNameInstantiation, prototype prototypeNameInstantiation,
//	 * line number lineNumberInstantiation, and column number columnNumberInstantiation.
//	 * The methods below are the getters and setters for these variables. In regular
//	 * prototypes packageNameInstantiation and prototypeNameInstantiation are null.
//	 */
//	@Override
//	public String getPackageNameInstantiation() {
//		final CompilationUnit cunit = this.compiler.getCompilationUnit();
//		if ( cunit == null ) {
//			return null;
//		}
//		return cunit.getPackageNameInstantiation();
//	}
//
//	@Override
//	public void setPackageNameInstantiation(String packageNameInstantiation) {
//		final CompilationUnit cunit = this.compiler.getCompilationUnit();
//		if ( cunit == null ) {
//			compiler.error2(null, "Attempt to set package name of a prototype instantiation outside a compilation unit");
//			return ;
//		}
//		cunit.setPackageNameInstantiation(packageNameInstantiation);
//	}
//
//	@Override
//	public String getPrototypeNameInstantiation() {
//		final CompilationUnit cunit = this.compiler.getCompilationUnit();
//		if ( cunit == null ) {
//			return null;
//		}
//		return cunit.getPrototypeNameInstantiation();
//	}
//
//	@Override
//	public void setPrototypeNameInstantiation(String prototypeNameInstantiation) {
//		//CompilationUnit cunit = this.compiler.
//		final CompilationUnit cunit = this.compiler.getCompilationUnit();
//		if ( cunit == null ) {
//			compiler.error2(null, "Attempt to set prototype name of a prototype instantiation outside a compilation unit");
//			return ;
//		}
//		cunit.setPrototypeNameInstantiation(prototypeNameInstantiation);
//	}
//
//	@Override
//	public int getLineNumberInstantiation() {
//		final CompilationUnit cunit = this.compiler.getCompilationUnit();
//		if ( cunit == null ) {
//			return -1;
//		}
//		return cunit.getLineNumberInstantiation();
//	}
//
//	@Override
//	public void setLineNumberInstantiation(int lineNumberInstantiation) {
//		final CompilationUnit cunit = this.compiler.getCompilationUnit();
//		if ( cunit == null ) {
//			compiler.error2(null, "Attempt to set the line number of a prototype instantiation outside a compilation unit");
//			return ;
//		}
//		cunit.setLineNumberInstantiation(lineNumberInstantiation);
//	}
//
//	@Override
//	public int getColumnNumberInstantiation() {
//		final CompilationUnit cunit = this.compiler.getCompilationUnit();
//		if ( cunit == null ) {
//			return -1;
//		}
//		return cunit.getColumnNumberInstantiation();
//	}
//
//	@Override
//	public void setColumnNumberInstantiation(int columnNumberInstantiation) {
//		final CompilationUnit cunit = this.compiler.getCompilationUnit();
//		if ( cunit == null ) {
//			compiler.error2(null, "Attempt to set the column number of a prototype instantiation outside a compilation unit");
//			return ;
//		}
//		cunit.setColumnNumberInstantiation(columnNumberInstantiation);
//	}
	//#$ }

	/**
	 * return the feature list of the current prototype, if there is one. Otherwise return null
	 */
	@Override
	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		final Prototype pu = compiler.getCurrentPrototype();
		if ( pu != null ) {
			return pu.getFeatureList();
		}
		else
			return null;
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


		String s = "";
		CompilationUnit previousCompUnit = cunit; // prototypeInstantiation.getCompilationUnit();
		if ( previousCompUnit.getPackageNameInstantiation() != null &&
				previousCompUnit.getPrototypeNameInstantiation() != null ) {
			s = "\n" + "Stack of generic prototype instantiations: \n" ;
			while ( previousCompUnit != null &&
					previousCompUnit.getPackageNameInstantiation() != null &&
					previousCompUnit.getPrototypeNameInstantiation() != null ) {
				s += "    " + previousCompUnit.getPackageNameInstantiation() + "." + previousCompUnit.getPrototypeNameInstantiation() + " line " +
						previousCompUnit.getLineNumberInstantiation() + " column " + previousCompUnit.getColumnNumberInstantiation() + "\n";
				final Prototype pppu = compiler.searchPackagePrototype(previousCompUnit.getPackageNameInstantiation(), previousCompUnit.getPrototypeNameInstantiation());
				if ( pppu != null ) {
					previousCompUnit = pppu.getCompilationUnit();
				}
				else {
					break;
				}
			}
		}


		final int lineNumberInstantiation = cunit.getLineNumberInstantiation();


		final int columnNumberInstantiation = cunit.getColumnNumberInstantiation();
		prototypeInstantiation.getCompilationUnit().error(lineNumberInstantiation, columnNumberInstantiation, errorMessage + s);
	}


	@Override
	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(String fileName, String extension, String packageName, DirectoryKindPPP hiddenDirectory, int numParameters,
			List<String> realParamList) {
				return compiler.getProject().getCompilerManager().
						readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);

			}

	@Override
	public Tuple4<FileError, char[], String, String> readTextFileFromProgram(String fileName, String extension,
			DirectoryKindPPP hiddenDirectory, int numParameters, List<String> realParamList) {
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
	public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		final String testPackageName = this.getPackageNameTest();
		return compiler.getProject().getCompilerManager().
				writeTestFileTo(data, fileName, dirName, testPackageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
		return compiler.getProject().getCompilerManager().
				writeTestFileTo(data, fileName, dirName, packageName);
	}


	@Override
	public String getPackageNameTest() {
		return compiler.getCompilationUnit().getPackageName() + MetaHelper.suffixTestPackageName;
	}

	@Override
	public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return compiler.getProject().getCompilerManager().writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	@Override
	public FileError writeTextFile(String str, String fileName, String prototypeFileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return compiler.getProject().getCompilerManager().writeTextFile(str, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	@Override
	public String getPathFileHiddenDirectory(String prototypeFileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return compiler.getProject().getCompilerManager().getPathFileHiddenDirectory(prototypeFileName, packageName, hiddenDirectory);

	}


	@Override
	public char[] getText(int offsetLeftCharSeq, int offsetRightCharSeq) {
		return compiler.getText(offsetLeftCharSeq, offsetRightCharSeq);
	}

	@Override
	public WrCompilationUnit getCompilationUnit() {
		CompilationUnit cunit = compiler.getCompilationUnit();
		return cunit == null ? null: cunit.getI();
	}

	@Override
	public WrPrototype searchPackagePrototype(String packageNameInstantiation,
			String prototypeNameInstantiation) {
		Prototype pu = compiler.searchPackagePrototype(packageNameInstantiation, prototypeNameInstantiation);
		return pu == null ? null : pu.getI();
	}


	@Override
	public void error(int lineNumber, String message) {
		compiler.error2(lineNumber, message, false);
	}

	@Override
	public void error(int lineNumber, int columnNumber, String message) {
		compiler.error2(false, lineNumber, columnNumber, message);
	}


	@Override
	public void error(WrSymbol sym, String message) {
		compiler.error2(meta.GetHiddenItem.getHiddenSymbol(sym), message);
	}


	@Override
	public WrEnv getEnv() {
		Env env = new Env(compiler.getProject());
		env.setCurrentCompilationUnit(compiler.getCompilationUnit());
		env.setCurrentPrototype(compiler.getCurrentPrototype());
		env.setCurrentMethod(compiler.getCurrentMethod());

		WrEnv wrenv = new WrEnv(env, CompilationStep.step_1);
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


	public Compiler compiler;

}

