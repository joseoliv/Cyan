package meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ast.CompilationUnit;
import ast.CyanPackage;
import ast.MethodDec;
import ast.MethodSignature;
import ast.Prototype;
import ast.StatementLocalVariableDec;
import ast.Type;
import saci.Env;
import saci.Project;

public class WrEnv {

	Env hidden;
	CompilationStep compilationStep = null;
	CompilationPhase compilationPhase = null;

	private WrEnv(Env hidden) {
		this.hidden = hidden;
	}

	/**
	 * only for special users!
	 */
	public WrEnv(Env hidden, CompilationStep compilationStep) {
		this(hidden);
		this.compilationStep = compilationStep;
		if ( this.compilationStep == null ) {
			this.error(null, "Internal error in constructor of WrEnv: compilationStep is null");
		}
	}


	public WrType createNewGenericPrototype(WrSymbol firstSymbol,
			WrCompilationUnit currentCompilationUnit, WrPrototype currentPrototype,
			String fullPrototypeName, String errorMessage) {
		return hidden.createNewGenericPrototype(firstSymbol.hidden,
				currentCompilationUnit.hidden,
				(Prototype ) currentPrototype.hidden, fullPrototypeName,
				errorMessage);
	}

	/**
	 * return true if the project file is being parsed.
	   @return
	 */
	public boolean parsingProject() {

		return hidden.getCompInstSet().contains(CompilationInstruction.pyanSourceCode);
	}


	public Set<CompilationInstruction> getCompInstSet() {

		return Collections.unmodifiableSet(hidden.getCompInstSet());
	}

	CompilationStep getCompilationStep() {
		if ( this.compilationStep != null ) {
			return this.compilationStep;
		}
		else {
			return hidden.getCompilationStep();
		}
	}

	public CompilationPhase getCompilationPhase() {
		if ( this.compilationPhase != null ) {
			return compilationPhase;
		}
		else {
			switch ( this.compilationStep ) {
			case step_1:
			case step_4:
			case step_7:
				this.compilationPhase = CompilationPhase.parsing;
				break;
			case step_3:
				this.compilationPhase = CompilationPhase.afterResTypes;
				break;
			case step_6:
				this.compilationPhase = CompilationPhase.semAn;
				break;
			case step_9:
				this.compilationPhase = CompilationPhase.afterSemAn;
				break;
			default:
				error(null, "Internal error in WrEnv::getCompilationPhase. compilationStep has an illegal value");
				return null;
			}
			return this.compilationPhase;
		}
	}


	public WrType getCyException() {
		Prototype pu = hidden.getCyException();
		return pu == null ? null : pu.getI();
	}

	public String getNewUniqueVariableName() {
		return hidden.getNewUniqueVariableName();
	}

	public String getStringVisibleLocalVariableList() {
		return hidden.getStringVisibleLocalVariableList();
	}

	public String getStringFieldList() {
		return hidden.getStringFieldList();
	}

	public String getStringSignatureAllMethods() {
		return hidden.getStringSignatureAllMethods();
	}

	public WrMethodDec getCurrentMethod() {
		MethodDec m = hidden.getCurrentMethod();
		return m == null ? null : m.getI();
	}


	public void removeAllLocalVariableDec() {
		hidden.removeAllLocalVariableDec();
	}

	public void addDependentToCurrentPrototype( WrPrototype dependentPrototype ) {

		if ( this.getCurrentPrototype() != null ) {
			Type cpu = getCurrentPrototype().hidden;
			if ( cpu instanceof Prototype ) {
				Prototype pu = (Prototype ) cpu;
				pu.addDependentPrototype(dependentPrototype.getHidden());
			}
		}
	}


	public WrMethodSignature searchMethodSignature(WrMethodSignature ms,
			List<WrMethodSignature> msList2) {

		// objDec.checkDependentePrototype(this);

		if ( msList2 != null ) {
			final List<MethodSignature> methodSignatureList = new ArrayList<>();
			for ( final WrMethodSignature ims : msList2 ) {
				methodSignatureList.add(meta.GetHiddenItem.getHiddenMethodSignature(ims));
			}
			MethodSignature msHidden;
			if ( ms instanceof WrMethodSignatureOperator ) {
				msHidden = ((WrMethodSignatureOperator ) ms).hidden;
			}
			else if ( ms instanceof WrMethodSignatureUnary ) {
				msHidden = ((WrMethodSignatureUnary ) ms).hidden;
			}
			else if ( ms instanceof WrMethodSignatureWithKeywords ) {
				msHidden = ((WrMethodSignatureWithKeywords ) ms).hidden;
			}
			else {
				return null;
			}

			MethodSignature msig = hidden.searchMethodSignature(msHidden, methodSignatureList);
			if ( msig == null ) {
				return null;
			}
			else {
				return msig.getI();
			}
		}
		else {
			return null;
		}
	}


	public WrType searchPackagePrototype( WrPrototype whoIsAsking,
			String packageName, String prototypeName) {
		whoIsAsking.addThisAsDependenteToCurrentPrototype(this);

		Type t = hidden.searchPackagePrototype(packageName, prototypeName);
		return t == null ? null : t.getI();
	}

	public WrType searchPackagePrototype(String packagePrototypeName,
			WrSymbol symUsedInError) {
		// objDec.addThisAsDependenteToCurrentPrototype(this);
		Type t = hidden.searchPackagePrototype(packagePrototypeName,
				symUsedInError.hidden);
		return t == null ? null : t.getI();
	}

	public List<WrMethodSignature> searchMethodProtectedPublicSuperProtectedPublic(
			WrPrototype objDec, String methodName) {

		objDec.addThisAsDependenteToCurrentPrototype(this);

		if ( objDec.isInterface() ) {
			return null;
		}


		final List<MethodSignature> msList =
				objDec.hidden.searchMethodProtectedPublicPackageSuperProtectedPublicPackage(methodName, hidden);

		if ( msList != null ) {
			final List<WrMethodSignature> imsList = new ArrayList<>();
			for ( final MethodSignature ms : msList ) {
				imsList.add(ms.getI());
			}
			return imsList;
		}
		else {
			return null;
		}
	}

	public List<WrMethodSignature> searchMethodPublicSuperPublic(WrPrototype objDec, String methodName) {
		// List<MethodSignature> msList = pu.searchMethodPublicPackageSuperPublicPackage(methodName, hidden);

		objDec.addThisAsDependenteToCurrentPrototype(this);

		final List<MethodSignature> msList =
				objDec.hidden.searchMethodPublicPackageSuperPublicPackage(methodName, hidden);

		if ( msList != null ) {
			final List<WrMethodSignature> imsList = new ArrayList<>();
			for ( final MethodSignature ms : msList ) {
				imsList.add(ms.getI());
			}
			return imsList;
		}
		else {
			return null;
		}
	}

	public WrCompilationUnit getCurrentCompilationUnit() {
		CompilationUnit cunit = hidden.getCurrentCompilationUnit();
		return cunit == null ? null : cunit.getI();
	}

	public WrPrototype getCurrentPrototype() {
		Prototype pu = hidden.getCurrentPrototype();
		return pu == null ? null : pu.getI();
	}

	public Set<WrCyanPackage> getImportedPackageSet() {
		final Set<CyanPackage> sp = hidden.getImportedPackageSet();

		if ( sp == null ) {
			return null;
		}
		else {
			final Set<WrCyanPackage> isp = new HashSet<>();
			for ( final CyanPackage cp : sp ) {
				isp.add(cp.getI());
			}
			return isp;
		}

	}

	public String getCyanLangJarFile() { return hidden.getCyanLangJarFile(); }
	public String getCyanRuntimeJarFile() { return hidden.getCyanRuntimeJarFile(); }

	public boolean wasJavaClassImported(String packageName, String className) {
		return hidden.wasJavaClassImported(packageName, className);
	}

	public boolean wasJavaPackageImported(String packageName)  {
		return hidden.wasJavaPackageImported(packageName);
	}

	public boolean wasJavaClassImported(String className) {
		return hidden.wasJavaClassImported(className);
	}

	public boolean wasCyanPrototypedImported(String prototypeName) {
		return hidden.wasCyanPrototypedImported(prototypeName);
	}

	public boolean wasCyanPackageImported(String packageName) {
		return hidden.wasCyanPackageImported(packageName);
	}

	public void error(WrSymbol symbol, String message) {
		hidden.error(symbol.hidden, message);
	}



	public void atEndMethodDec() {
		hidden.atEndMethodDec();
	}



	public void setPackageNameInstantiation(String packageNameInstantiation) {
		hidden.setPackageNameInstantiation(packageNameInstantiation);
	}



	public void setPrototypeNameInstantiation(
			String prototypeNameInstantiation) {
		hidden.setPrototypeNameInstantiation(prototypeNameInstantiation);
	}



	public void setLineNumberInstantiation(int lineNumber) {
		hidden.setLineNumberInstantiation(lineNumber);
	}



	public void setColumnNumberInstantiation(int columnNumber) {
		hidden.setColumnNumberInstantiation(columnNumber);
	}



	public boolean isThereWasError() {
		return hidden.isThereWasError();
	}



	public WrProject getProject() {
		Project p = hidden.getProject();
		return p == null ? null : p.getI();
	}



	public WrLocalVarInfo getLocalVariableInfo(
			WrStatementLocalVariableDec varDec) {
		LocalVarInfo lvi = hidden.getLocalVariableInfo( (StatementLocalVariableDec ) varDec.hidden);
		return lvi.getI();
	}

	public IActionFunction searchActionFunction(String strParam) {
		return hidden.searchActionFunction(strParam);
	}


	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName,
			String packageName,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return hidden.readTextFileFromPackage(fileName, packageName, hiddenDirectory, numParameters, realParamList);
	}

	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(String fileName, String extension, String packageName, DirectoryKindPPP hiddenDirectory, int numParameters,
			List<String> realParamList) {
				return hidden.
						readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);

			}

	public Tuple4<FileError, char[], String, String> readTextFileFromProgram(String fileName, String extension, DirectoryKindPPP hiddenDirectory, int numParameters, List<String> realParamList) {
		return hidden.readTextFileFromProgram(fileName, extension, hiddenDirectory, numParameters, realParamList);
	}

	public Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(String fileName,
			String packageName, DirectoryKindPPP hiddenDirectory) {
		return hidden.
				getAbsolutePathHiddenDirectoryFile(fileName, packageName, hiddenDirectory);
	}

	public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(String fileName, String packageName) {
		return hidden.
				readBinaryDataFileFromPackage(fileName, packageName);

	}



	public boolean deleteDirOfTestDir(String dirName) {
		return hidden.deleteDirOfTestDir(dirName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
	 * Return an object of FileError indicating any errors.
	 */
	public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		return hidden.
				writeTestFileTo(data, fileName, dirName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
	 * Return an object of FileError indicating any errors.
	 */
	public FileError writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
		return hidden.
				writeTestFileTo(data, fileName, dirName, packageName);
	}


	public String getPackageNameTest() {
		return hidden.getPackageNameTest();
	}

	public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName,
			String packageName, DirectoryKindPPP hiddenDirectory) {
		return hidden.writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	public FileError writeTextFile(String str, String fileName, String prototypeFileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return hidden.writeTextFile(str, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	public String getPathFileHiddenDirectory(String prototypeFileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return hidden.getPathFileHiddenDirectory(prototypeFileName, packageName, hiddenDirectory);

	}



}
