package metaRealClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ast.Annotation;
import ast.CompilationUnit;
import ast.CyanPackage;
import ast.FieldDec;
import ast.GenericParameter;
import ast.MethodDec;
import ast.MethodSignature;
import ast.MethodSignatureUnary;
import ast.ObjectDec;
import ast.ParameterDec;
import ast.Prototype;
import ast.VariableDecInterface;
import error.CompileErrorException;
import meta.CompilationStep;
import meta.DirectoryKindPPP;
import meta.FileError;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.IVariableDecInterface;
import meta.MetaHelper;
import meta.MetaSecurityException;
import meta.Tuple2;
import meta.Tuple3;
import meta.Tuple4;
import meta.Tuple5;
import meta.WrAnnotationAt;
import meta.WrCompilationUnitSuper;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprAnyLiteral;
import meta.WrFieldDec;
import meta.WrMethodDec;
import meta.WrPrototype;
import meta.WrStatement;
import meta.WrSymbol;
import meta.WrType;
import saci.Env;

public class Compiler_semAn implements ICompiler_semAn {


	public Compiler_semAn(Env env) {
		originalEnv = env;
		this.env = env.clone();
	}
	public Compiler_semAn(Env env, Annotation cyanAnnotation) {
		originalEnv = env;
		this.env = env.clone();
		this.cyanAnnotation = cyanAnnotation;
	}

	@Override
	public int getColumnNumberCyanAnnotation() {
		return this.cyanAnnotation.getFirstSymbol().getColumnNumber();
	}
	@Override
	public int getLineNumberCyanAnnotation() {
		return this.cyanAnnotation.getFirstSymbol().getLineNumber();
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
			env.error(null,  "Internal error: current compilation unit does not exist at Compiler_semAn");
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
		final Prototype prototypeInstantiation = env.searchPackagePrototype(packageNameInstantiation, prototypeNameInstantiation);
		if ( prototypeInstantiation == null ) {
			env.error(null,  "Internal error: prototype '" + prototypeNameInstantiation +
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
				final Prototype pppu = env.searchPackagePrototype(previousCompUnit.getPackageNameInstantiation(), previousCompUnit.getPrototypeNameInstantiation());
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
		// env.getCurrentCompilationUnit().error(lineNumberInstantiation, columnNumberInstantiation, errorMessage + s);
		env.setThereWasError(true);
	}


	/**
	 * return the feature list of the current prototype, if there is one. Otherwise return null
	 */
	@Override
	public List<Tuple2<String, WrExprAnyLiteral>> getFeatureList() {
		final Prototype pu = env.getCurrentPrototype();
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
	 * remove the code of 'stat' and replace it by 'code'.
	 * This is being asked by metaobject annotation 'annot'.
	 * The type of the expression 'code' is 'codeType'
	 *
	 * Use the method with the same name of CyanMetaobjectAtAnnot
	 */

	@Deprecated
	@Override
	public
	boolean replaceStatementByCode(WrStatement stat,
				WrAnnotationAt annot, StringBuffer code, WrType codeType) {
		/*
		 * cases to consider:
		 * 		a) annot is attached to a prototype. Check if
		 *            stat is inside the prototype or not
		 *      b) annot is attached to a method. Check if
		 *            stat is inside the method
		 *      c) annot is inside a method. Check if stat is inside the method
		 */

		WrMethodDec statMethod = stat.getCurrentMethod();
		IDeclaration annotDec = annot.getDeclaration();
		if ( annotDec instanceof WrPrototype ) {
			// annotation is attached to a program unit

			// is statement inside the prototype of the annotation?
			if ( statMethod.getDeclaringObject() != (WrPrototype ) annotDec ) {
				throw new MetaSecurityException(
						"Annotation " + annot.getCyanMetaobject().getName()
						+ " is trying to replace a statement in " +
				       "another prototype. This is illegal");
			}
		}
		else if ( annotDec instanceof WrMethodDec ) {
			WrMethodDec annotMethod = (WrMethodDec ) annotDec;
			// annotation is attached to 'annotMethod'
			if ( statMethod != annotMethod ) {
				throw new MetaSecurityException(
						"Annotation " + annot.getCyanMetaobject().getName()
						+ " of method " + annotMethod.getName() +
								" is trying to replace a statement in " +
				       " method " + statMethod.getName() + ". This is illegal. " +
						"A metaobject can only replace statements in its own annotation method"		);
			}
		}
		else {
			// annotation is attached to something else or not attached to anything
			// annotMethod is the method of the current annotation
			WrMethodDec annotMethod = annot.getCurrentMethod();
			if ( statMethod != annotMethod ) {
				throw new MetaSecurityException(
						"Annotation " + annot.getCyanMetaobject().getName()
						+ " of method " + annotMethod.getName() +
								" is trying to replace a statement in " +
				       " method " + statMethod.getName() + ". This is illegal. " +
						"A metaobject can only replace statements in its own annotation method"		);
			}

		}
//
//		WrCompilationUnitSuper cunitSuper = stat.getFirstSymbol().getCompilationUnit();
//		WrMethodDec annotMethod = annot.getCurrentMethod();
//		if ( cunitSuper instanceof WrCompilationUnit ) {
//			WrPrototype annotProgUnit = annotMethod.getDeclaringObject();
//			WrPrototype statProgUnit = statMethod.getDeclaringObject();
//			if ( annotProgUnit != statProgUnit ) {
//				/*
//				 * An annotation in one prototype is trying to replace a statement in
//				 * another prototype. This is illegal.
//				 */
//
//				throw new MetaSecurityException(
//						"Annotation " + annot.getCyanMetaobject().getName()
//						+ " is trying to replace a statement in " +
//				       "another prototype. This is illegal");
//			}
//			else {
//				// same prototype
//				if ( statMethod != annotMethod ) {
//					throw new MetaSecurityException(
//							"Annotation " + annot.getCyanMetaobject().getName()
//							+ " of method " + annotMethod.getName() +
//									" is trying to replace a statement in " +
//					       " method " + statMethod.getName() + ". This is illegal. " +
//							"A metaobject can only replace statements in its own annotation method"		);
//				}
//			}
//		}
//		else {
//			throw new MetaSecurityException(
//					"An annotation is trying to replace a statement in " +
//			       "a code that is not considered a regular Cyan compilation unit. "
//			       + "Maybe an attached DSL code. This may be an internal error");
//		}
		if ( env.getCompilationStep().ordinal() > CompilationStep.step_6.ordinal() ) {
			this.error(annot.getFirstSymbol(), "The metaobject associated to "
					+ "this annotation is trying to replace code after step 6 of the compilation. This is illegal");
		}
		return env.replaceStatementByCode(
				meta.GetHiddenItem.getHiddenStatement(stat),
				meta.GetHiddenItem.getHiddenCyanMetaobjectWithAtAnnotation(annot),
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
	 * This method is NOT in interface ICompiler_semAn. It should not be used for now.
	 *
	 */

	public Map<String, Set<WrPrototype>> getMapPrototypeSubtypeList(WrAnnotationAt annot) {
		if ( env.getCompilationStep().ordinal() < CompilationStep.step_7.ordinal() ) {
			this.error(annot.getFirstSymbol(), "The metaobject associated to "
					+ "this annotation is trying to get the complete list of "
					+ "subtypes of the program in a compilation phase below 7. This is illegal");
			return null;
		}

		return env.getMapPrototypeSubtypeList();
	}


	@Override
	public WrType createNewGenericPrototype(WrSymbol symUsedInError, WrCompilationUnitSuper compUnit, WrPrototype currentPU,
			String fullPrototypeName, String errorMessage) {
		try {
			this.originalEnv.setPrefixErrorMessage(errorMessage);
			final WrExpr newPrototype = saci.Compiler.parseSingleTypeFromString(fullPrototypeName,
					meta.GetHiddenItem.getHiddenSymbol(symUsedInError), errorMessage,
					meta.GetHiddenItem.getHiddenCompilationUnitSuper(compUnit),
					meta.GetHiddenItem.getHiddenPrototype(currentPU)).getI();
			newPrototype.calcInternalTypes(originalEnv.getI());
			return newPrototype.getType();

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
		final Prototype pu = env.getCurrentPrototype();
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


	private Annotation	cyanAnnotation;

	private final Env env, originalEnv;


}
