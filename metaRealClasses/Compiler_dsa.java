package metaRealClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ast.CompilationUnit;
import ast.Annotation;
import ast.CyanPackage;
import ast.FieldDec;
import ast.GenericParameter;
import ast.MethodDec;
import ast.MethodSignature;
import ast.MethodSignatureUnary;
import ast.ObjectDec;
import ast.ParameterDec;
import ast.ProgramUnit;
import ast.VariableDecInterface;
import error.CompileErrorException;
import meta.CompilationStep;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.ICompiler_dsa;
import meta.IVariableDecInterface;
import meta.MetaHelper;
import meta.MetaSecurityException;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrCompilationUnitSuper;
import meta.WrAnnotationAt;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprAnyLiteral;
import meta.WrFieldDec;
import meta.WrMethodDec;
import meta.WrProgramUnit;
import meta.WrStatement;
import meta.WrSymbol;
import meta.WrType;
import saci.Env;

public class Compiler_dsa implements ICompiler_dsa {


	public Compiler_dsa(Env env) {
		originalEnv = env;
		this.env = env.clone();
	}
	public Compiler_dsa(Env env, Annotation cyanMetaobjectAnnotation) {
		originalEnv = env;
		this.env = env.clone();
		this.cyanMetaobjectAnnotation = cyanMetaobjectAnnotation;
	}

	@Override
	public int getColumnNumberCyanMetaobjectAnnotation() {
		return this.cyanMetaobjectAnnotation.getFirstSymbol().getColumnNumber();
	}
	@Override
	public int getLineNumberCyanMetaobjectAnnotation() {
		return this.cyanMetaobjectAnnotation.getFirstSymbol().getLineNumber();
	}

	@Override
	public List<WrFieldDec> getFieldList() {
		final ObjectDec proto = env.getCurrentObjectDec();
		if ( proto != null ) {

			if ( proto.getFieldList() == null ) {
				return null;
			}
			else {
				final List<WrFieldDec> ret = new ArrayList<>();
				for ( final FieldDec iv : proto.getFieldList() ) {
					ret.add(iv.getI());
				}
				return ret;
			}
				}
		else {
			return null;
		}
	}



	List<WrMethodDec> iMethodDecList = null;
	boolean thisMethod_wasNeverCalled = true;

	@Override
	public List<WrMethodDec> getMethodDecList() {
//		if ( thisMethod_wasNeverCalled ) {}

		final ObjectDec proto = env.getCurrentObjectDec();
		if ( proto == null ) {
			return null;
		}
		List<MethodDec> fromList = proto.getMethodDecList();
		if ( fromList == null ) {
				// unnecessary, just to document
			iMethodDecList = null;
		}
		else {
			iMethodDecList = new ArrayList<>();
			for ( MethodDec from : fromList ) {
				iMethodDecList.add( from.getI() );
			}
		}
//		thisMethod_wasNeverCalled = false;


		return iMethodDecList;
	}



	@Override
	public WrFieldDec searchField(String strParam) {
		FieldDec f = env.searchField(strParam);
		return f == null ? null : f.getI();
	}

	@Override
	public IVariableDecInterface searchLocalVariableParameter(String varName) {
		VariableDecInterface vdi = env.searchLocalVariableParameter(varName);
		return (IVariableDecInterface ) (vdi == null ? null : vdi.getI());
	}

	@Override
	public IVariableDecInterface searchLocalVariable(String varName) {
		final VariableDecInterface v = env.searchLocalVariableParameter(varName);
		if ( v instanceof ParameterDec )
			return null;
		else if ( v != null ) {
			return (IVariableDecInterface ) v.getI();
		}
		else {
			return null;
		}
	}

	@Override
	public IVariableDecInterface searchParameter(String varName) {
		final VariableDecInterface v = env.searchLocalVariableParameter(varName);
		if ( v instanceof ParameterDec )
			return (IVariableDecInterface ) v.getI();
		else
			return null;
	}


	@Override
	public void error(WrSymbol symbol, String message) {
		env.error(meta.GetHiddenItem.getHiddenSymbol(symbol), message);
	}

	@Override
	public void error(int lineNumber, int columnNumber, String message) {
		env.error(lineNumber,  columnNumber, message);
	}


	@Override
	public WrEnv getEnv() {
		return env.getI();
	}


	@Override
	public Object getProgramValueFromKey(String variableName) {
		return this.env.getProject().getProgramValueFromKey(variableName);
	}

	@Override
	public Set<String> getProgramKeyValueSet(String variableName) {
		return env.getProject().getProgramKeyValueSet(variableName);
	}

	@Override
	public List<String> getUnaryMethodNameList() {
		final ObjectDec currentProto = env.getCurrentObjectDec();
		if ( currentProto == null )
			return null;
		else {
			final List<String> ret = new ArrayList<String>();
			final List<MethodDec> methodList = currentProto.getMethodDecList();
			for ( final MethodDec methodDec : methodList ) {
				final MethodSignature ms = methodDec.getMethodSignature();
				if ( ms instanceof MethodSignatureUnary ) {
					ret.add(  ((MethodSignatureUnary ) ms).getName() );
				}
			}
			return ret;
		}
	}

	@Override
	public boolean isInPackageCyanLang(String name) {
		return env.isInPackageCyanLang(name);
	}

	@Override
	public void errorAtGenericPrototypeInstantiation(String errorMessage) {
		final CompilationUnit cunit = this.env.getCurrentCompilationUnit();
		if ( cunit == null ) {
			env.error(null,  "Internal error: current compilation unit does not exist at Compiler_dsa");
			return ;
		}
		final String packageNameInstantiation = cunit.getPackageNameInstantiation();
		if ( packageNameInstantiation == null ) {
			/*
			 * an error was signalled and the program unit is not generic. This may happen when a metaobject
			 * such as 'concept' is used in non-generic prototypes. This is legal.
			 */
			cunit.error(cunit.getPublicPrototype().getFirstSymbol().getLineNumber(), cunit.getPublicPrototype().getFirstSymbol().getColumnNumber(), errorMessage);
			env.setThereWasError(true);
			return ;
		}
		final String prototypeNameInstantiation = cunit.getPrototypeNameInstantiation();
		final ProgramUnit programUnitInstantiation = env.searchPackagePrototype(packageNameInstantiation, prototypeNameInstantiation);
		if ( programUnitInstantiation == null ) {
			env.error(null,  "Internal error: prototype '" + prototypeNameInstantiation +
					"' of package '" + packageNameInstantiation + "' was not found");
			return ;
		}

		String s = "";
		CompilationUnit previousCompUnit = cunit; // programUnitInstantiation.getCompilationUnit();
		if ( previousCompUnit.getPackageNameInstantiation() != null &&
				previousCompUnit.getPrototypeNameInstantiation() != null ) {
			s = "\n" + "Stack of generic prototype instantiations: \n" ;
			while ( previousCompUnit != null &&
					previousCompUnit.getPackageNameInstantiation() != null &&
					previousCompUnit.getPrototypeNameInstantiation() != null ) {
				s += "    " + previousCompUnit.getPackageNameInstantiation() + "." + previousCompUnit.getPrototypeNameInstantiation() + " line " +
						previousCompUnit.getLineNumberInstantiation() + " column " + previousCompUnit.getColumnNumberInstantiation() + "\n";
				final ProgramUnit pppu = env.searchPackagePrototype(previousCompUnit.getPackageNameInstantiation(), previousCompUnit.getPrototypeNameInstantiation());
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
		programUnitInstantiation.getCompilationUnit().error(lineNumberInstantiation, columnNumberInstantiation, errorMessage + s);
		// env.getCurrentCompilationUnit().error(lineNumberInstantiation, columnNumberInstantiation, errorMessage + s);
		env.setThereWasError(true);
	}


	/**
	 * return the feature list of the current prototype, if there is one. Otherwise return null
	 */
	@Override
	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		final ProgramUnit pu = env.getCurrentProgramUnit();
		if ( pu != null ) {
			return pu.getFeatureList();
		}
		else
			return null;
	}



	@Override
	public Tuple5<FileError, char[], String, String, WrCyanPackage> readTextFileFromPackage(
			String fileName,
			String extension,
			String packageName,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return env.getProject().getCompilerManager().
				readTextFileFromPackage(fileName, extension, packageName, hiddenDirectory, numParameters, realParamList);

	}


	@Override
	public Tuple4<FileError, char[], String, String> readTextFileFromProgram(
			String fileName,
			String extension,
			DirectoryKindPPP hiddenDirectory,
			int numParameters,
			List<String> realParamList) {
		return env.getProject().getCompilerManager().readTextFileFromProject(fileName, extension, hiddenDirectory, numParameters, realParamList);
	}


	@Override
	public Tuple2<FileError, byte[]> readBinaryDataFileFromPackage(String fileName, String packageName) {
		return env.getProject().getCompilerManager().
				readBinaryDataFileFromPackage(fileName, packageName);

	}



	@Override
	public boolean deleteDirOfTestDir(String dirName) {
		final String testPackageName = this.getPackageNameTest();
		return env.getProject().getCompilerManager().deleteDirOfTestDir(dirName, testPackageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in the test directory of the package in which the annotation is.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName) {
		final String testPackageName = this.getPackageNameTest();
		return env.getProject().getCompilerManager().
				writeTestFileTo(data, fileName, dirName, testPackageName);
	}

	/**
	 * write 'data' to file 'fileName' that is created in directory 'dirName' of the test directory of package 'packageName'.
	 * Return an object of FileError indicating any errors.
	 */
	@Override
	public
	FileError  writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName) {
		return env.getProject().getCompilerManager().
				writeTestFileTo(data, fileName, dirName, packageName);
	}



	@Override
	public String getPackageNameTest() {
		return env.getCurrentCompilationUnit().getPackageName() + MetaHelper.suffixTestPackageName;
	}

	@Override
	public Tuple3<String, String, WrCyanPackage> getAbsolutePathHiddenDirectoryFile(String fileName, String packageName, DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().
				getAbsolutePathHiddenDirectoryFile(fileName, packageName, hiddenDirectory);
	}

	/**
	 * remove the code of 'expr' and replace it by codeToAdd.
	 * This is being asked by metaobject annotation 'annotation'.  The type of the expression codeToAdd is
	 * codeType.
	 */

	@Override
	public
	boolean replaceStatementByCode(WrStatement stat,
				WrAnnotationAt annotation, StringBuffer code, WrType codeType) {
		if ( !stat.getFirstSymbol().getCompilationUnit().getFullFileNamePath().equals(
				annotation.getCompilationUnit().getFullFileNamePath())  ) {
			/*
			 * annotation in one prototype is trying to replace a statement in another prototype. This is illegal.
			 */
			throw new MetaSecurityException();
		}
		if ( env.getCompilationStep().ordinal() > CompilationStep.step_6.ordinal() ) {
			this.error(annotation.getFirstSymbol(), "The metaobject associated to "
					+ "this annotation is trying to replace code after step 6 of the compilation. This is illegal");
		}
		return env.replaceStatementByCode(
				meta.GetHiddenItem.getHiddenStatement(stat),
				meta.GetHiddenItem.getHiddenCyanMetaobjectWithAtAnnotation(annotation),
				code, codeType != null ? meta.GetHiddenItem.getHiddenType(codeType) : null);
	}


	/**
	 * return a map with a key for each prototype or interface. The value for the key is a
	 * set with all direct subtypes of the prototype or interface.
	 * This map is only created on demand. The key has the format: the package name, a single space, prototype name.
	 * It can be, for example,<br>
	 * <code>
	 * "br.main Program"
	 * </code><br>
	 * The package name is "br.main" and the prototype name is "Program".
	 *
	 * This method is NOT in interface ICompiler_dsa. It should not be used for now.
	 *
	 */

	public Map<String, Set<WrProgramUnit>> getMapPrototypeSubtypeList(WrAnnotationAt annot) {
		if ( env.getCompilationStep().ordinal() < CompilationStep.step_7.ordinal() ) {
			this.error(annot.getFirstSymbol(), "The metaobject associated to "
					+ "this annotation is trying to get the complete list of "
					+ "subtypes of the program in a compilation phase below 7. This is illegal");
			return null;
		}

		return env.getMapPrototypeSubtypeList();
	}


	@Override
	public WrType createNewGenericPrototype(WrSymbol symUsedInError, WrCompilationUnitSuper compUnit, WrProgramUnit currentPU,
			String fullPrototypeName, String errorMessage) {
		try {
			this.originalEnv.setPrefixErrorMessage(errorMessage);
			final WrExpr newProgramUnit = saci.Compiler.parseSingleTypeFromString(fullPrototypeName,
					meta.GetHiddenItem.getHiddenSymbol(symUsedInError), errorMessage,
					meta.GetHiddenItem.getHiddenCompilationUnitSuper(compUnit),
					meta.GetHiddenItem.getHiddenProgramUnit(currentPU)).getI();
			newProgramUnit.calcInternalTypes(originalEnv.getI());
			return newProgramUnit.getType();

		}
		catch ( final CompileErrorException cee ) {
			this.originalEnv.setThereWasError(true);
		}
		finally {
			this.originalEnv.setPrefixErrorMessage(null);
		}
		return null;
	}


	@Override
	public FileError writeTextFile(char[] charArray, String fileName, String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().writeTextFile(charArray, fileName, prototypeFileName, packageName, hiddenDirectory);
	}

	@Override
	public FileError writeTextFile(
			String str,
			String fileName,
			String prototypeFileName,
			String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().writeTextFile(str, fileName, prototypeFileName, packageName, hiddenDirectory);
	}


	@Override
	public String getPathFileHiddenDirectory(String prototypeFileName, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().getPathFileHiddenDirectory(prototypeFileName, packageName, hiddenDirectory);
	}


	@Override
	public List<List<String>> getGenericPrototypeArgListList() {
		final ProgramUnit pu = env.getCurrentProgramUnit();
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
	public void addNewTypeDef(String typename, WrType newtype ) {
		env.addNewTypeDef(typename, newtype);
	}

	@Override
	public Tuple5<FileError, byte[], String, String, CyanPackage> readBinaryFileFromPackage(
			String fileName, String extension, String packageName,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().readBinaryFileFromPackage(fileName, extension, packageName, hiddenDirectory);
	}



	@Override
	public Tuple4<FileError, byte[], String, String> readBinaryFileFromProject(
			String fileName, String extension,
			DirectoryKindPPP hiddenDirectory) {
		return env.getProject().getCompilerManager().readBinaryFileFromProject(fileName, extension, hiddenDirectory);
	}


	private Annotation	cyanMetaobjectAnnotation;

	private final Env env, originalEnv;


}
