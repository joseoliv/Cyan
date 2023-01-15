package saci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ast.CompilationUnit;
import ast.CompilationUnitSuper;
import ast.Annotation;
import ast.AnnotationAt;
import ast.MethodDec;
import ast.MethodKeywordWithParameters;
import ast.MethodSignature;
import ast.MethodSignatureOperator;
import ast.MethodSignatureUnary;
import ast.MethodSignatureWithKeywords;
import ast.ObjectDec;
import ast.Prototype;
import error.ErrorKind;
//import meta.*;
import lexer.CompilerPhase;
import lexer.Symbol;
import meta.CompilationPhase;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.MetaHelper;
import meta.SourceCodeChangeAddText;
import meta.SourceCodeChangeByAnnotation;
import meta.SourceCodeChangeDeleteText;
import meta.SourceCodeChangeShiftPhase;
import meta.Tuple2;
import meta.Tuple4;
import meta.Tuple5;
import meta.Tuple6;
import meta.WrAnnotation;
import meta.lexer.MetaLexer;

public class CompilerManager_afterResTypes  {

	public CompilerManager_afterResTypes(Env env) {
		this.env = env;
		packagePrototypeMethodMap = new HashMap<String, CyanMetaobjectAtAnnot>();
		methodToAddList = new ArrayList<Tuple5<CyanMetaobjectAtAnnot, String, String, String, StringBuffer>>();
		codeToAddList = new ArrayList<>();
		codeToAddList2 = new ArrayList<>();
		codeToAddAtAnnotationList = new ArrayList<Tuple4<CyanMetaobjectAtAnnot, String, String, StringBuffer>>();
		packagePrototypeFieldMap = new HashMap<String, CyanMetaobjectAtAnnot>();
		fieldToAddList = new ArrayList<Tuple6<CyanMetaobjectAtAnnot, String, String, String, String, String>>();
		renameMethodList = new ArrayList<>();
		packagePrototypeMethodRenameMap = new HashMap<>();
		beforeMethodToAddList = new ArrayList<>();
	}

	/*
	public List<IPackage_afterResTypes> getPackageList() {
		final List<IPackage_afterResTypes> ipackage_afterResTypesList = new ArrayList<IPackage_afterResTypes>();
		for ( final CyanPackage cyanPackage : env.getProject().getPackageList() ) {
			final IPackage_afterResTypes ipackage = new WrPackage_afterResTypes(cyanPackage);
			ipackage_afterResTypesList.add(ipackage);
		}
		return ipackage_afterResTypesList;
	}
	*/


	/**
	 * add codeToAdd to prototype prototypeName of package packageName. Metaobject cyanMetaobject asked for that.
	   @param cyanMetaobject
	   @param packageName
	   @param prototypeName
	   @param codeToAdd  code to be added
	   @return <code>true</code> if the method can be added, <code>false</code> otherwise.
	 */
//	public boolean addCode( CyanMetaobjectAtAnnot cyanMetaobject, String packageName, String prototypeName,
//			StringBuffer codeToAdd ) {
//
//		packageName = MetaHelper.removeQuotes(packageName);
//		prototypeName = MetaHelper.removeQuotes(prototypeName);
//
//
//		codeToAddList.add( new Tuple4<CyanMetaobjectAtAnnot, String, String, StringBuffer>(
//				cyanMetaobject, packageName, prototypeName, codeToAdd) );
//		return true;
//
//	}


	public boolean addCode( CyanMetaobject cyanMetaobject, String packageName, String prototypeName,
			StringBuffer codeToAdd, String strSlotList ) {

		packageName = MetaHelper.removeQuotes(packageName);
		prototypeName = MetaHelper.removeQuotes(prototypeName);


		codeToAddList2.add( new Tuple5<CyanMetaobject, String, String, StringBuffer, String>(
				cyanMetaobject, packageName, prototypeName, codeToAdd, strSlotList) );
		return true;

	}


	/**
	 * add method methodName to prototype prototypeName of package packageName. Metaobject cyanMetaobject asked for that.
	   @param cyanMetaobject
	   @param packageName
	   @param prototypeName
	   @param methodName the name of the method
	   @param methodCode  code of the method to be added
	   @return <code>true</code> if the method can be added, <code>false</code> otherwise.
	 */
	public boolean addMethod( CyanMetaobjectAtAnnot cyanMetaobject, String packageName, String prototypeName,
			String methodName, StringBuffer methodCode ) {

		packageName = MetaHelper.removeQuotes(packageName);
		prototypeName = MetaHelper.removeQuotes(prototypeName);
		methodName = MetaHelper.removeQuotes(methodName);

		// checkAnnotation(cyanMetaobject, packageName, prototypeName);

		final CyanMetaobject previousCyanMetaobject =
				packagePrototypeMethodMap.put(packageName + " " + prototypeName + " " + methodName, cyanMetaobject);
		if ( previousCyanMetaobject == null ) {
			methodToAddList.add( new Tuple5<CyanMetaobjectAtAnnot, String, String, String, StringBuffer>(
					cyanMetaobject, packageName, prototypeName, methodName, methodCode) );
			return true;
		}
		else {
			/*
			 * previousCyanMetaobject already asked to add a method methodName to packageName.prototypeName.
			 */
			final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
			final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();

			final String packageName2 = previousCyanMetaobject.getAnnotation().getPackageOfAnnotation();
			final String prototypeName2 = previousCyanMetaobject.getAnnotation().getPrototypeOfAnnotation();

			error(meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
					"metaobject annotation of line " +
					cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
					prototypeName1 + " of package " + packageName1 + " is trying to add method " + methodName + " to prototype " +
					prototypeName + " of package " + packageName +
					". However, another metaobject annotation has added the same method to the same prototype. This annotation is in" +
					" line " + previousCyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() +
					" of prototype " +
					prototypeName2 + " of package " + packageName2, methodName,
					ErrorKind.metaobject_attempt_to_add_two_methods_with_the_same_name_to_a_prototype);
			return false;
		}
	}


	/**
	 * add the statements statementCode before the first statement of method methodName of
	 * prototype prototypeName of package packageName. statementCode is added to all methods with name
	 * methodName of the given prototype.  Metaobject cyanMetaobject asked for that.
	   @param cyanMetaobject
	   @param packageName
	   @param prototypeName
	   @param methodName
	   @param statementCode code to be added before the first statement of the method
	   @return <code>true</code> if statementCode can be added, <code>false</code> otherwise.
	*/

	public boolean addBeforeMethod(CyanMetaobject cyanMetaobject, String packageName, String prototypeName,
			String methodSignature, StringBuffer statementCode) {

		packageName = MetaHelper.removeQuotes(packageName);
		prototypeName = MetaHelper.removeQuotes(prototypeName);
		methodSignature = MetaHelper.removeQuotes(methodSignature);
		statementCode = new StringBuffer(MetaLexer.unescapeJavaString(MetaHelper.removeQuotes(statementCode.toString())));


		// checkAnnotation(cyanMetaobject, packageName, prototypeName);
		beforeMethodToAddList.add(new Tuple5<>(cyanMetaobject, packageName, prototypeName, methodSignature, statementCode));
		return true;
	}


	/*
	@Override
	public boolean addBeforeMethod(CyanMetaobjectAtAnnot cyanMetaobject, String packageName, String prototypeName,
			String methodName, StringBuffer statementCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addEndMethod(CyanMetaobjectAtAnnot cyanMetaobject, String packageName, String prototypeName,
			String methodName, StringBuffer statementCode) {
		// TODO Auto-generated method stub
		return false;
	}
	*/

	public boolean renameMethods(CyanMetaobject cyanMetaobject, String packageName,
			String prototypeName, String methodName, String[] newMethodkeywords) {

		packageName = MetaHelper.removeQuotes(packageName);
		prototypeName = MetaHelper.removeQuotes(prototypeName);
		methodName = MetaHelper.removeQuotes(methodName);
		for ( int i = 0; i  < newMethodkeywords.length; ++i ) {
			newMethodkeywords[i] = MetaHelper.removeQuotes(newMethodkeywords[i]);
		}


		final CyanMetaobject previousCyanMetaobject = this.packagePrototypeMethodRenameMap.put(
				packageName + " " + prototypeName + " " + methodName, cyanMetaobject);
		if ( previousCyanMetaobject == null ) {
			renameMethodList.add( new Tuple5<CyanMetaobject, String, String, String, String[]>(
					cyanMetaobject, packageName, prototypeName, methodName, newMethodkeywords));
			return true;
		}
		else {
			/*
			 * previousCyanMetaobject already asked to rename the same method
			 */

			final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
			final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();

			final String packageName2 = previousCyanMetaobject.getAnnotation().getPackageOfAnnotation();
			final String prototypeName2 = previousCyanMetaobject.getAnnotation().getPrototypeOfAnnotation();

			error(meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()), "metaobject annotation of line " +
					cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
					prototypeName1 + " of package " + packageName1 + " is trying to rename method " + methodName
					+ " of prototype " +
					prototypeName + " of package " + packageName +
					". However, another metaobject annotation has tried to rename the same method of the same prototype. This annotation is in" +
					" line " + previousCyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() +
					" of prototype " +
					prototypeName2 + " of package " + packageName2, methodName,
					ErrorKind.metaobject_error);
			return false;
		}


	}

	/** add field variableName of type variableType to prototype prototypeName of package packageName.
	 *
	   @param cyanMetaobject
	   @param packageName
	   @param prototypeName
	   @param variableType
	   @param variableName
	   @return <code>true</code> if the field can be added, <code>false</code> otherwise.
	 */
	public boolean addField(CyanMetaobjectAtAnnot cyanMetaobject, String packageName,
			String prototypeName, boolean isPublic, boolean isShared, boolean isReadonly, String variableType, String variableName) {

		packageName = MetaHelper.removeQuotes(packageName);
		prototypeName = MetaHelper.removeQuotes(prototypeName);
		variableType = MetaHelper.removeQuotes(variableType);
		variableName = MetaHelper.removeQuotes(variableName);
		String qualifiers = "";
		if ( isPublic ) { qualifiers = "public "; }
		if ( isShared ) { qualifiers = qualifiers + "shared "; }
		if ( ! isReadonly ) { qualifiers = qualifiers + "var "; }



		// checkAnnotation(cyanMetaobject, packageName, prototypeName);

		final CyanMetaobject previousCyanMetaobject = packagePrototypeFieldMap.put(packageName + " " + prototypeName + " " + variableName, cyanMetaobject);
		if ( previousCyanMetaobject == null ) {
			fieldToAddList.add( new Tuple6<CyanMetaobjectAtAnnot, String, String, String, String, String>(
					cyanMetaobject, packageName, prototypeName, qualifiers, variableType, variableName));
			return true;
		}
		else {
			/*
			 * previousCyanMetaobject already asked to add field variableName to packageName.prototypeName.
			 */

			final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
			final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();

			final String packageName2 = previousCyanMetaobject.getAnnotation().getPackageOfAnnotation();
			final String prototypeName2 = previousCyanMetaobject.getAnnotation().getPrototypeOfAnnotation();

			error(meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()), "metaobject annotation of line " +
					cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
					prototypeName1 + " of package " + packageName1 + " is trying to add the field " + variableName
					+ " to prototype " +
					prototypeName + " of package " + packageName +
					". However, another metaobject annotation has added the same field to the same prototype. This annotation is in" +
					" line " + previousCyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() +
					" of prototype " +
					prototypeName2 + " of package " + packageName2, variableName,
					ErrorKind.metaobject_attempt_to_add_two_instance_variables_with_the_same_name_to_a_prototype);
			return false;
		}
	}



	/**
	 * Code <code>codeToAdd</code> should be added after the metaobject annotation <code>cyanMetaobject.getAnnotation()</code>
	   @param cyanMetaobject
	   @param codeToAdd
	   @return <code>false</code> if there was an error
	 */
	public boolean addCodeAtAnnotation(CyanMetaobjectAtAnnot cyanMetaobject, StringBuffer codeToAdd) {
		/*
		 * 			String cyanMetaobjectName = t.f2;
			String packageName = t.f3;
			String prototypeName = t.f4;
			int lineNumber = t.f5;
		 */
		final Annotation cyanAnnotation =
				meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
		codeToAddAtAnnotationList.add(new Tuple4<CyanMetaobjectAtAnnot, String, String, StringBuffer>(
				cyanMetaobject, cyanAnnotation.getPackageOfAnnotation(),
				cyanAnnotation.getPrototypeOfAnnotation(), codeToAdd));
		return true;
	}






	/**
	 * add to the prototypes of the program all changes demanded by calls to methods #addMethod, #addField, etc.
	 * And do all checks demanded by the metaobjects of the program.
	 *
	   @return <code>false</code> if there was any changes in the code, <code>true</code> otherwise
	 */
	public boolean changeCheckProgram() {

		/**
		 * for each compilation unit that should be changed by metaobjects or by adding "#AFTER_RES_TYPES" to metaobject annotations,
		 * there is associated list of code changes
		 */
		final HashMap<CompilationUnitSuper, List<SourceCodeChangeByAnnotation>> setOfChanges = new HashMap<>();
		/**
		 * set of prefixes of metaobject annotations to change. This variable is necessary because a
		 * metaobject annotation may insert, for example, a method and a field. So the change of the prefix
		 * could be inserted twice in the set <code>setOfChanges</code>.
		 */
		//final HashMap<CompilationUnitSuper, List<Integer>> setOfSuffixToChange = new HashMap<>();
		final HashMap<CompilationUnitSuper, List<Tuple2<Integer, Annotation>>> setOfSuffixToChange2 = new HashMap<>();
		  /*
		   * set with elements "packageName prototypeName" such that the prototype of the package
		   * has been changed during the compiler phase "AFTER_RES_TYPES". Then these prototypes should be
		   * compiled again.
		   */
		// compilationUnitChangedSet = new HashSet<CompilationUnit>();
		Prototype pu;
		CompilationUnit compUnit;
		String packageName, prototypeName;



		for ( final Tuple4<CyanMetaobjectAtAnnot, String, String, StringBuffer> t : codeToAddList ) {
			final CyanMetaobjectAtAnnot cyanMetaobject = t.f1;
			packageName = t.f2;
			prototypeName = t.f3;
			final StringBuffer codeToAdd = t.f4;
			pu = env.searchPackagePrototype(packageName, prototypeName);
			if ( pu == null ) {
				final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
				final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
				error(meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
						"metaobject annotation of line " +
						cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
						prototypeName1 + " of package " + packageName1 + " is trying to add code to prototype " +
						prototypeName + " of package " + packageName +
						". However, there is no package " + packageName + " or no prototype " +
						prototypeName + " in this package",  null,
						ErrorKind.metaobject_error);
			}
			else {
				compUnit = pu.getCompilationUnit();
				/**
				 * it is necessary to find where the add the code. If there is no method with the same name,
				 * the method can be added at the 'end' symbol of the prototype. Otherwise the method
				 * should be added before the first method with the same name.
				 */

				final ObjectDec objDec = (ObjectDec ) pu;
				final int smallerOffset = objDec.getEndSymbol().getOffset();
				/*
				 * add change to the list of changes
				 */
				final String codeToAddWithContext = Env.getCodeToAddWithContext(cyanMetaobject,
						"\n" + codeToAdd + "\n", null);
				final AnnotationAt annotation = meta.GetHiddenItem.getHiddenCyanMetaobjectWithAtAnnotation(
						cyanMetaobject.getAnnotation());
				addChange(setOfChanges, compUnit,
						new SourceCodeChangeAddText(smallerOffset, new StringBuffer(codeToAddWithContext),
						annotation
						 ));

				if ( ! annotation.getInsideProjectFile() ) {
					/*
					 * metaobject annotation is inside a regular .cyan file
					 */
					final Prototype puAnnotation = env.searchPackagePrototype(annotation.getPackageOfAnnotation(),
							cyanMetaobject.getAnnotation().getPrototypeOfAnnotation());
					if ( puAnnotation == null )
						error(null, "Internal error: I cannot find a metaobject annotation of package " +
					            cyanMetaobject.getAnnotation().getPackageOfAnnotation() + " and prototype" +
								cyanMetaobject.getAnnotation().getPrototypeOfAnnotation(),
								cyanMetaobject.getAnnotation().toString(), ErrorKind.metaobject_error);
					else {
						/*
						 * the metaobject annotation will be changed to use the suffix "AFTER_RES_TYPES". Then all
						 * metaobject annotations should be collected
						 */
						addSuffixToChange2(setOfSuffixToChange2, puAnnotation.getCompilationUnit(), cyanMetaobject);
					}

				}

			}
		}


		for ( final Tuple5<CyanMetaobject, String, String, StringBuffer, String> t : codeToAddList2 ) {
			final CyanMetaobject cyanMetaobject = t.f1;
			packageName = t.f2;
			prototypeName = t.f3;
			final StringBuffer codeToAdd = t.f4;
			pu = env.searchPackagePrototype(packageName, prototypeName);
			if ( pu == null ) {
				final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
				final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
				error(meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
						"metaobject annotation of line " +
						cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
						prototypeName1 + " of package " + packageName1 + " is trying to add code to prototype " +
						prototypeName + " of package " + packageName +
						". However, there is no package " + packageName + " or no prototype " +
						prototypeName + " in this package",  null,
						ErrorKind.metaobject_error);
			}
			else {
				compUnit = pu.getCompilationUnit();
				/**
				 * it is necessary to find where the add the code. If there is no method with the same name,
				 * the method can be added at the 'end' symbol of the prototype. Otherwise the method
				 * should be added before the first method with the same name.
				 */

				final int smallerOffset = pu.getEndSymbol().getOffset();

				/*
				 * add change to the list of changes
				 */
				final String codeToAddWithContext = Env.getCodeToAddWithContext(cyanMetaobject,
						"\n" + codeToAdd + "\n", null, t.f5);
				final Annotation annotation = meta.GetHiddenItem.getHiddenCyanAnnotation(
						cyanMetaobject.getAnnotation());
				addChange(setOfChanges, compUnit,
						new SourceCodeChangeAddText(smallerOffset, new StringBuffer(codeToAddWithContext),
						annotation
						 ));

				if ( ! annotation.getInsideProjectFile() && (cyanMetaobject instanceof CyanMetaobjectAtAnnot)) {
					/*
					 * metaobject annotation is inside a regular .cyan file
					 */
					final Prototype puAnnotation = env.searchPackagePrototype(annotation.getPackageOfAnnotation(),
							cyanMetaobject.getAnnotation().getPrototypeOfAnnotation());
					if ( puAnnotation == null )
						error(null, "Internal error: I cannot find a metaobject annotation of package " +
					            cyanMetaobject.getAnnotation().getPackageOfAnnotation() + " and prototype" +
								cyanMetaobject.getAnnotation().getPrototypeOfAnnotation(),
								cyanMetaobject.getAnnotation().toString(), ErrorKind.metaobject_error);
					else {
						/*
						 * the metaobject annotation will be changed to use the suffix "AFTER_RES_TYPES". Then all
						 * metaobject annotations should be collected
						 */
						addSuffixToChange2(setOfSuffixToChange2, puAnnotation.getCompilationUnit(), cyanMetaobject);
					}

				}

			}
		}




		for ( final Tuple6<CyanMetaobjectAtAnnot, String, String, String, String, String> t : fieldToAddList ) {
			final CyanMetaobjectAtAnnot cyanMetaobject = t.f1;
			packageName = t.f2;
			prototypeName = t.f3;
			// String qualifiers = t.f4;
			final String fieldType = t.f5;
			final String fieldName = t.f6;
			pu = env.searchPackagePrototype(packageName, prototypeName);

			/*
			 * the field is to be added to <code>pu</code> before the 'end' symbol.
			 * The metaobject annotation that asked for this inclusion is in package
			 *     			cyanMetaobject.getAnnotation().getPackageOfAnnotation()
			 *  and prototype
			 *              cyanMetaobject.getAnnotation().getPrototypeOfAnnotation()

			 */
			final Annotation cyanAnnotation =
					meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
			if ( pu == null ) {
				if ( cyanAnnotation.getInsideProjectFile() ) {
					error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
							cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
							env.getProject().getProjectName() + " is trying to add field '"
							+ fieldName + "' to prototype " +
							prototypeName + " of package " + packageName +
							". However, there is no package " + packageName + " or no prototype " +
							prototypeName + " in this package",  fieldName,
							ErrorKind.metaobject_error);

				}
				else {
					final String packageName1 = cyanAnnotation.getPackageOfAnnotation();
					final String prototypeName1 = cyanAnnotation.getPrototypeOfAnnotation();
					error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
							cyanAnnotation.getFirstSymbol().getLineNumber() + " of prototype " +
							prototypeName1 + " of package " + packageName1 + " is trying to add field '"
							+ fieldName + "' to prototype " +
							prototypeName + " of package " + packageName +
							". However, there is no package " + packageName + " or no prototype " +
							prototypeName + " in this package",  fieldName,
							ErrorKind.metaobject_error);

				}
			}
			else {
				compUnit = pu.getCompilationUnit();
				/*
				 * add change to the list of changes
				 */
				final String codeToAddWithContext = Env.getCodeToAddWithContext(cyanMetaobject,
						"\n    " + fieldType + " " + fieldName + "\n", null);
				addChange(setOfChanges, compUnit, new SourceCodeChangeAddText(pu.getEndSymbol().getOffset(),
						new StringBuffer(codeToAddWithContext), cyanAnnotation)
				);

				if ( ! cyanAnnotation.getInsideProjectFile() )  {
					final Prototype puAnnotation = env.searchPackagePrototype(cyanMetaobject.getAnnotation().getPackageOfAnnotation(),
							cyanMetaobject.getAnnotation().getPrototypeOfAnnotation());
					if ( puAnnotation == null )
						error(null, "Internal error: I cannot find a metaobject annotation of package " +
					            cyanMetaobject.getAnnotation().getPackageOfAnnotation() + " and prototype" +
								cyanMetaobject.getAnnotation().getPrototypeOfAnnotation(),
								cyanMetaobject.getAnnotation().toString(), ErrorKind.metaobject_error);
					else {
						/*
						 * the metaobject annotation will be changed to use the suffix "AFTER_RES_TYPES". Then all
						 * metaobject annotations should be collected
						 */
						addSuffixToChange2(setOfSuffixToChange2, puAnnotation.getCompilationUnit(), cyanMetaobject);
					}
				}
				// compilationUnitChangedSet.add(compUnit);

			}
		}
		for ( final Tuple5<CyanMetaobjectAtAnnot, String, String, String, StringBuffer> t : methodToAddList ) {
			final CyanMetaobjectAtAnnot cyanMetaobject = t.f1;
			final Annotation cyanAnnotation =
					meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
			packageName = t.f2;
			prototypeName = t.f3;
			final String methodName = t.f4;
			final StringBuffer methodCode = t.f5;
			pu = env.searchPackagePrototype(packageName, prototypeName);
			if ( pu == null ) {

				if ( cyanAnnotation.getInsideProjectFile() ) {
					error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
							cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
							env.getProject().getProjectName() + " is trying to add method " + methodName
							+ " to prototype " +
							prototypeName + " of package " + packageName +
							". However, there is no package " + packageName + " or no prototype " +
							prototypeName + " in this package",  methodName,
							ErrorKind.metaobject_error);
				}
				else {
					final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
					final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
					error(meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
							"metaobject annotation of line " +
							cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
							prototypeName1 + " of package " + packageName1 + " is trying to add method " + methodName
							+ " to prototype " +
							prototypeName + " of package " + packageName +
							". However, there is no package " + packageName + " or no prototype " +
							prototypeName + " in this package",  methodName,
							ErrorKind.metaobject_error);

				}
			}
			else {
				compUnit = pu.getCompilationUnit();
				/**
				 * it is necessary to find where the add the method. If there is no method with the same name,
				 * the method can be added at the 'end' symbol of the prototype. Otherwise the method
				 * should be added before the first method with the same name.
				 */

				final ObjectDec objDec = (ObjectDec ) pu;
				final List<MethodSignature> sameNameMethodList = objDec.searchMethodPrivateProtectedPublic(methodName);
				int smallerOffset;
				if ( sameNameMethodList == null || sameNameMethodList.size() == 0 ) {
					smallerOffset = objDec.getEndSymbol().getOffset();
				}
				else {
					// method 'methodName' should be added before the first method of this list
					smallerOffset = sameNameMethodList.get(0).getMethod().getFirstSymbol().getOffset();
					for ( int i = 1; i < sameNameMethodList.size(); ++i ) {
						if ( sameNameMethodList.get(i).getMethod().getFirstSymbol().getOffset() < smallerOffset )
							smallerOffset = sameNameMethodList.get(i).getFirstSymbol().getOffset();
					}
				}



				/*
				 * add change to the list of changes
				 */
				// Annotation cyanAnnotation = cyanMetaobject.getAnnotation();
				final String codeToAddWithContext = Env.getCodeToAddWithContext(cyanMetaobject,
						"\n" + methodCode + "\n", null);
				addChange(setOfChanges, compUnit, new SourceCodeChangeAddText(
						smallerOffset, new StringBuffer(codeToAddWithContext),
						meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation())
						));


				if ( ! cyanAnnotation.getInsideProjectFile() ) {
					final Prototype puAnnotation = env.searchPackagePrototype(cyanMetaobject.getAnnotation().getPackageOfAnnotation(),
							cyanMetaobject.getAnnotation().getPrototypeOfAnnotation());
					if ( puAnnotation == null )
						error(null, "Internal error: I cannot find a metaobject annotation of package " +
					            cyanMetaobject.getAnnotation().getPackageOfAnnotation() + " and prototype" +
								cyanMetaobject.getAnnotation().getPrototypeOfAnnotation(),
								cyanMetaobject.getAnnotation().toString(), ErrorKind.metaobject_error);
					else {
						/*
						 * the metaobject annotation will be changed to use the suffix "AFTER_RES_TYPES". Then all
						 * metaobject annotations should be collected
						 */
						addSuffixToChange2(setOfSuffixToChange2, puAnnotation.getCompilationUnit(), cyanMetaobject);
					}

				}


				// compilationUnitChangedSet.add(pu.getCompilationUnit());
			}
		}

		for ( final Tuple5<CyanMetaobject, String, String, String, StringBuffer> t : beforeMethodToAddList) {
			final CyanMetaobject cyanMetaobject = t.f1;
			final Annotation cyanAnnotation =
					meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
			packageName = t.f2;
			prototypeName = t.f3;
			final String methodName = t.f4;
			final StringBuffer statementsCode = t.f5;
			pu = env.searchPackagePrototype(packageName, prototypeName);
			if ( pu == null ) {

				if ( cyanAnnotation.getInsideProjectFile() ) {
					error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
							cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
							env.getProject().getProjectName() + " is trying to add statements to method " + methodName
							+ " of prototype " +
							prototypeName + " of package " + packageName +
							". However, there is no package " + packageName + " or no prototype " +
							prototypeName + " in this package",  methodName,
							ErrorKind.metaobject_error);

				}
				else {


					final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
					final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
					error( meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()), "metaobject annotation of line " +
							cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
							prototypeName1 + " of package " + packageName1 + " is trying to add statements to method " + methodName
							+ " of prototype " +
							prototypeName + " of package " + packageName +
							". However, there is no package " + packageName + " or no prototype " +
							prototypeName + " in this package",  methodName,
							ErrorKind.metaobject_error);
				}
			}
			else {
				compUnit = pu.getCompilationUnit();

				final ObjectDec objDec = (ObjectDec ) pu;
				final List<MethodSignature> sameNameMethodList = objDec.searchMethodPrivateProtectedPublic(methodName);
				if ( sameNameMethodList == null || sameNameMethodList.size() == 0 ) {

					if ( cyanAnnotation.getInsideProjectFile() ) {
						error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
								cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
								env.getProject().getProjectName() + " is trying to add statements to method " + methodName
								+ " of prototype " +
								prototypeName + " of package " + packageName +
								". However, there is no such method", methodName,
								ErrorKind.metaobject_error);
					}
					else {
						final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
						final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
						error( meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
								"metaobject annotation of line " +
								cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
								prototypeName1 + " of package " + packageName1 + " is trying to add statements to method " + methodName
								+ " of prototype " +
								prototypeName + " of package " + packageName +
								". However, there is no such method", methodName,
								ErrorKind.metaobject_error);

					}
				}
				else {
					/*
					 * add the statements to all methods with name 'methodName'
					 */
					for ( final MethodSignature ms : sameNameMethodList ) {
						final MethodDec aMethod = ms.getMethod();
						if ( aMethod.getLeftCBsymbol() != null ) {
							/*
							 * method has a '{' after its signature, possibly followed by statements till '}'.
							 * Some methods are different, they have a '=' followed by an expression.
							 * For example, <br>
							 * <code>
							 * func zero -> Int = 0 <br>
							 * </code>
							 * These methods are not changed
							 */
							final int offsetFirstStat = aMethod.getLeftCBsymbol().getOffset() + 1;
							/*
							 * add change to the list of changes
							 */
							final String codeToAddWithContext = Env.getCodeToAddWithContext(cyanMetaobject,
									"\n" + statementsCode + "\n", null);
							addChange(setOfChanges, compUnit, new SourceCodeChangeAddText(offsetFirstStat, new StringBuffer(codeToAddWithContext),
									meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation())
									));

							if ( ! cyanAnnotation.getInsideProjectFile() ) {

								final Prototype puAnnotation = env.searchPackagePrototype(cyanMetaobject.getAnnotation().getPackageOfAnnotation(),
										cyanMetaobject.getAnnotation().getPrototypeOfAnnotation());
								if ( puAnnotation == null )
									error(null, "Internal error: I cannot find a metaobject annotation of package " +
								            cyanMetaobject.getAnnotation().getPackageOfAnnotation() + " and prototype" +
											cyanMetaobject.getAnnotation().getPrototypeOfAnnotation(),
											cyanMetaobject.getAnnotation().toString(), ErrorKind.metaobject_error);
								else {
									/*
									 * the metaobject annotation will be changed to use the suffix "AFTER_RES_TYPES". Then all
									 * metaobject annotations should be collected
									 */
									addSuffixToChange2(setOfSuffixToChange2, puAnnotation.getCompilationUnit(), cyanMetaobject);
								}

							}
						}
					}

					// compilationUnitChangedSet.add(pu.getCompilationUnit());
				}
			}
		}

		for ( final Tuple4<CyanMetaobjectAtAnnot, String, String, StringBuffer> t : codeToAddAtAnnotationList ) {
			final CyanMetaobjectAtAnnot cyanMetaobject = t.f1;
			final Annotation cyanAnnotation =
					meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());

			packageName = t.f2;
			prototypeName = t.f3;
			final StringBuffer codeToAdd = t.f4;

			pu = env.searchPackagePrototype(packageName, prototypeName);

			if ( pu == null ) {
				if ( cyanAnnotation.getInsideProjectFile() ) {
					error(  cyanAnnotation.getFirstSymbol(), "Internal error: metaobject annotation of line " +
							cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
							env.getProject().getProjectName() +  " does not exist anymore", null,
							ErrorKind.metaobject_error );
				}
				else {
					final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
					final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
					error( cyanAnnotation.getFirstSymbol(), "Internal error: metaobject annotation of line " +
							cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
							prototypeName1 + " of package " + packageName1 + " does not exist anymore", null,
							ErrorKind.metaobject_error );
				}
			}
			else {
				compUnit = pu.getCompilationUnit();
				/*
				 * add change to the list of changes
				 */
				final String codeToAddWithContext = Env.getCodeToAddWithContext(cyanMetaobject,
						codeToAdd + " ", null);
				addChange(setOfChanges, compUnit, new SourceCodeChangeAddText(
						cyanAnnotation.getNextSymbol().getOffset(),
						new StringBuffer(codeToAddWithContext),
						   meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation())
						));

				if ( ! cyanAnnotation.getInsideProjectFile() ) {
					/*
					 * the metaobject annotation will be changed to use the suffix "AFTER_RES_TYPES". Then all
					 * metaobject annotations should be collected
					 */
					addSuffixToChange2(setOfSuffixToChange2, pu.getCompilationUnit(), cyanMetaobject);

				}
				// compilationUnitChangedSet.add(pu.getCompilationUnit());
			}


		}

		for ( final Tuple5<CyanMetaobject, String, String, String, String[]> t : renameMethodList ) {



			final CyanMetaobject cyanMetaobject = t.f1;
			final Annotation cyanAnnotation =
					meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation());
			packageName = t.f2;
			prototypeName = t.f3;
			final String oldMethodName = t.f4;
			final String[] keywordNames = t.f5;
			pu = env.searchPackagePrototype(packageName, prototypeName);
			if ( pu == null ) {

				if ( cyanAnnotation.getInsideProjectFile() ) {
					error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
							cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
							env.getProject().getProjectName() + " is trying to rename " + oldMethodName
							+ " of prototype " +
							prototypeName + " of package " + packageName +
							". However, there is no package " + packageName + " or no prototype " +
							prototypeName + " in this package",  oldMethodName,
							ErrorKind.metaobject_error);

				}
				else {


					final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
					final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
					error( meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
							"metaobject annotation of line " +
							cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
							prototypeName1 + " of package " + packageName1 + " is trying rename method " + oldMethodName
							+ " of prototype " +
							prototypeName + " of package " + packageName +
							". However, there is no package " + packageName + " or no prototype " +
							prototypeName + " in this package",  oldMethodName,
							ErrorKind.metaobject_error);
				}
			}
			else {
				compUnit = pu.getCompilationUnit();

				final ObjectDec objDec = (ObjectDec ) pu;
				final List<MethodSignature> sameNameMethodList = objDec.searchMethodPrivateProtectedPublic(oldMethodName);
				if ( sameNameMethodList == null || sameNameMethodList.size() == 0 ) {

					if ( cyanAnnotation.getInsideProjectFile() ) {
						error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
								cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
								env.getProject().getProjectName() + " is trying rename method " + oldMethodName
								+ " of prototype " +
								prototypeName + " of package " + packageName +
								". However, there is no such method", oldMethodName,
								ErrorKind.metaobject_error);
					}
					else {
						final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
						final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
						error( meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
								"metaobject annotation of line " +
								cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
								prototypeName1 + " of package " + packageName1 + " is trying to rename method " + oldMethodName
								+ " of prototype " +
								prototypeName + " of package " + packageName +
								". However, there is no such method", oldMethodName,
								ErrorKind.metaobject_error);

					}
				}
				else {
					if ( sameNameMethodList.size() > 1 ) {
						// an overloaded method. Issue an error
						final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
						final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
						env.error(meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
								"metaobject annotation of line " +
								cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
								prototypeName1 + " of package " + packageName1 + " is trying to rename method " + oldMethodName
								+ " of prototype " +
								prototypeName + " of package " + packageName +
								". However, there is more than one method with this name. This is illegal.");
					}
					/*
					 * rename all methods with name oldMethodName
					 */
					for ( final MethodSignature ms : sameNameMethodList ) {
						if ( ms instanceof MethodSignatureWithKeywords ) {
							final MethodSignatureWithKeywords realMS = (MethodSignatureWithKeywords ) ms;
							final List<MethodKeywordWithParameters> selecWithParametersList = realMS.getKeywordArray();
							if ( keywordNames.length != selecWithParametersList.size() ) {
								String newMethodName = "";
								for ( final String s : keywordNames ) {
									newMethodName += s;
								}
								if ( cyanAnnotation.getInsideProjectFile() ) {
									error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
											cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
											env.getProject().getProjectName() + " is trying to rename " + oldMethodName
											+ " of prototype " +
											prototypeName + " of package " + packageName +
											". However, the number of keywords of the old and new methods are different: " +
											oldMethodName + " (old, " + selecWithParametersList.size() + " keywords) " +
											newMethodName + " (new, " + keywordNames.length + " keywords)",  oldMethodName,
											ErrorKind.metaobject_error);

								}
								else {
									final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
									final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
									error(  meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
											"metaobject annotation of line " +
											cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
											prototypeName1 + " of package " + packageName1 + " is trying rename method " + oldMethodName
											+ " of prototype " +
											prototypeName + " of package " + packageName +
											". However, the number of keywords of the old and new methods are different: " +
											oldMethodName + " (old, " + selecWithParametersList.size() + " keywords) " +
											newMethodName + " (new, " + keywordNames.length + " keywords)",  oldMethodName,
											ErrorKind.metaobject_error);
								}

							}
							int i = 0;
							for ( final MethodKeywordWithParameters selec : selecWithParametersList ) {
								final int offsetFirstStat = selec.getkeyword().getOffset();
								addChange(setOfChanges, compUnit, new SourceCodeChangeDeleteText(
										offsetFirstStat, selec.getkeyword().getSymbolString().length(),
										meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation())
										));
								String keyword = keywordNames[i];
								if ( ! keyword.endsWith(":") ) {
									keyword += ":";
								}
								addChange(setOfChanges, compUnit, new SourceCodeChangeAddText(offsetFirstStat,
										new StringBuffer(keyword),
										meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation())
										));
								++i;
							}
						}
						else if ( ms instanceof MethodSignatureOperator ) {
							final MethodSignatureOperator realMS = (MethodSignatureOperator ) ms;
							boolean signError = keywordNames.length != 1;
							signError = signError || keywordNames.length > 1;
							/*
							 * many more checkings should be made.
							 */

							if ( signError ) {
								String newMethodName = "";
								for ( final String s : keywordNames ) {
									newMethodName += s;
								}
								if ( cyanAnnotation.getInsideProjectFile() ) {
									error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
											cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
											env.getProject().getProjectName() + " is trying to rename " + oldMethodName
											+ " of prototype " +
											prototypeName + " of package " + packageName +
											". However, the old method name and the new name are incompatible: " +
											oldMethodName + " (old) " +
											newMethodName + " (new)",  oldMethodName,
											ErrorKind.metaobject_error);

								}
								else {
									final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
									final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
									error(  meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
											"metaobject annotation of line " +
											cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
											prototypeName1 + " of package " + packageName1 + " is trying rename method " + oldMethodName
											+ " of prototype " +
											prototypeName + " of package " + packageName +
											". However, the old method name and the new name are incompatible: " +
											oldMethodName + " (old) " +
											newMethodName + " (new)",  oldMethodName,
											ErrorKind.metaobject_error);
								}

							}
							final Symbol unarySymbol = realMS.getFirstSymbol();
							final int offsetFirstStat = unarySymbol.getOffset();
							addChange(setOfChanges, compUnit, new SourceCodeChangeDeleteText(
									offsetFirstStat, unarySymbol.getSymbolString().length(),
									meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation())
									));
							addChange(setOfChanges, compUnit, new SourceCodeChangeAddText(offsetFirstStat,
									new StringBuffer(keywordNames[0]),
									meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation())
									));




						}
						else if ( ms instanceof MethodSignatureUnary ) {
							final MethodSignatureUnary realMS = (MethodSignatureUnary )  ms;
							if ( keywordNames.length != 1 || keywordNames.length > 0 && keywordNames[0].endsWith(":") ) {
								String newMethodName = "";
								for ( final String s : keywordNames ) {
									newMethodName += s;
								}
								if ( cyanAnnotation.getInsideProjectFile() ) {
									error(  cyanAnnotation.getFirstSymbol(), "metaobject annotation of line " +
											cyanAnnotation.getFirstSymbol().getLineNumber() + " of the project file " +
											env.getProject().getProjectName() + " is trying to rename " + oldMethodName
											+ " of prototype " +
											prototypeName + " of package " + packageName +
											". However, the old method is unary and the new name is from a non-unary method: " +
											oldMethodName + " (old) " +
											newMethodName + " (new)",  oldMethodName,
											ErrorKind.metaobject_error);

								}
								else {
									final String packageName1 = cyanMetaobject.getAnnotation().getPackageOfAnnotation();
									final String prototypeName1 = cyanMetaobject.getAnnotation().getPrototypeOfAnnotation();
									error(  meta.GetHiddenItem.getHiddenSymbol(cyanMetaobject.getAnnotation().getFirstSymbol()),
											"metaobject annotation of line " +
											cyanMetaobject.getAnnotation().getFirstSymbol().getLineNumber() + " of prototype " +
											prototypeName1 + " of package " + packageName1 + " is trying rename method " + oldMethodName
											+ " of prototype " +
											prototypeName + " of package " + packageName +
											". However, the old method is unary and the new name is from a non-unary method: " +
											oldMethodName + " (old) " +
											newMethodName + " (new)",  oldMethodName,
											ErrorKind.metaobject_error);
								}

							}
							final Symbol unarySymbol = realMS.getFirstSymbol();
							final int offsetFirstStat = unarySymbol.getOffset();
							addChange(setOfChanges, compUnit, new SourceCodeChangeDeleteText(
									offsetFirstStat, unarySymbol.getSymbolString().length(),
									meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation())
									));
							addChange(setOfChanges, compUnit, new SourceCodeChangeAddText(offsetFirstStat,
									new StringBuffer(keywordNames[0]),
									meta.GetHiddenItem.getHiddenCyanAnnotation(cyanMetaobject.getAnnotation())
									));


						}
					}

					// compilationUnitChangedSet.add(pu.getCompilationUnit());
				}
			}



		}
		/**
		 * changeList has already all changes demanded by the metaobjects such as add methods and
		 * fields to prototypes.
		 * The code below adds the phase changes to the metaobject annotations. That is, a metaobject annotation
		 *           @init(name)
		 *  should be changed to
		 *           @init#afterResTypes(name)
		 *  The phase changes are in map setOfPrefixToChange.
		 *  The code below just add these changes to the list setOfChanges.
		 */
		for ( final Map.Entry<CompilationUnitSuper, List<Tuple2<Integer, Annotation>>>
		         entry: setOfSuffixToChange2.entrySet() )  {
			  // the compilation unit in which the metaobject annotation is
			final CompilationUnitSuper compUnitEntry = entry.getKey();
			  // the list of offsets of the metaobject annotations inside the compilation unit
			final List<Tuple2<Integer, Annotation>> offsetList = entry.getValue();
			   // the changes already collected for this compilation unit
			List<SourceCodeChangeByAnnotation> changeList = setOfChanges.get(compUnitEntry);
			if ( changeList == null ) {
				  // there were no changes for this compilation unit. Add one list of changes, empty
				changeList = new ArrayList<SourceCodeChangeByAnnotation>();
				setOfChanges.put(compUnitEntry, changeList);
			}
			   // for each metaobject annotation of this compilation unit, add a change to changeList
			for ( final Tuple2<Integer, Annotation> t : offsetList ) {
				changeList.add(new SourceCodeChangeShiftPhase(
						compUnitEntry.getText(), env, t.f1, CompilerPhase.AFTER_RES_TYPES, compUnitEntry,
						t.f2));
			}
		}

//		for ( final Map.Entry<CompilationUnitSuper, List<Integer>> entry: setOfSuffixToChange.entrySet() )  {
//			  // the compilation unit in which the metaobject annotation is
//			final CompilationUnitSuper compUnitEntry = entry.getKey();
//			  // the list of offsets of the metaobject annotations inside the compilation unit
//			final List<Integer> offsetList = entry.getValue();
//			   // the changes already collected for this compilation unit
//			List<SourceCodeChangeByAnnotation> changeList = setOfChanges.get(compUnitEntry);
//			if ( changeList == null ) {
//				  // there were no changes for this compilation unit. Add one list of changes, empty
//				changeList = new ArrayList<SourceCodeChangeByAnnotation>();
//				setOfChanges.put(compUnitEntry, changeList);
//			}
//			   // for each metaobject annotation of this compilation unit, add a change to changeList
//			for ( final Integer offset : offsetList ) {
//				changeList.add(new SourceCodeChangeShiftPhase(compUnitEntry.getText(), env, offset, CompilerPhase.AFTER_RES_TYPES, compUnitEntry,
//						null));
//			}
//		}


		final boolean changeSomething = !setOfChanges.isEmpty();
		if ( changeSomething ) {
			Saci.makeChanges(setOfChanges, env, CompilationPhase.afterResTypes);
		}

		return changeSomething;
	}


	static private void addChange(HashMap<CompilationUnitSuper, List<SourceCodeChangeByAnnotation>> setOfChanges,
			CompilationUnit compUnit, SourceCodeChangeByAnnotation change) {

		List<SourceCodeChangeByAnnotation> changeList = setOfChanges.get(compUnit);
		if ( changeList == null ) {
			changeList = new ArrayList<SourceCodeChangeByAnnotation>();
			setOfChanges.put(compUnit, changeList);
		}
		changeList.add(change);
	}

	private static void addSuffixToChange(
			HashMap<CompilationUnitSuper, List<Integer>> setOfPrefixToChange,
			CompilationUnit compUnit,
			CyanMetaobject cyanMetaobject) {

		final int newOffset = cyanMetaobject.getAnnotation().getFirstSymbol().getOffset();
		List<Integer> offsetList = setOfPrefixToChange.get(compUnit);
		if ( offsetList == null ) {
			offsetList = new ArrayList<>();
			offsetList.add(newOffset);
			setOfPrefixToChange.put(compUnit, offsetList);
		}
		else {
			/**
			 * two Integers with the same value are
			 * considered different. Then we have to search in offsetList for
			 * an integer with value equal to newOffset
			 */
			boolean found = false;
			for (final Integer offset : offsetList ) {
				if ( offset == newOffset ) {
					found = true;
					break;
				}
			}
			if ( ! found )
				offsetList.add(newOffset);
		}
	}


	private static void addSuffixToChange2(
            HashMap<CompilationUnitSuper, List<Tuple2<Integer, Annotation>>> setOfPrefixToChange,
            CompilationUnit compUnit,
            CyanMetaobject cyanMetaobject) {

        WrAnnotation wrannot = cyanMetaobject.getAnnotation();
        Annotation annot = meta.GetHiddenItem.getHiddenCyanAnnotation(wrannot);
        final int newOffset = cyanMetaobject.getAnnotation().getFirstSymbol().getOffset();
        List<Tuple2<Integer, Annotation>> offsetTupleList  = setOfPrefixToChange.get(compUnit);
        if ( offsetTupleList == null ) {
        	offsetTupleList = new ArrayList<>();
        	offsetTupleList.add( new Tuple2<Integer, Annotation>(newOffset, annot));
            setOfPrefixToChange.put(compUnit, offsetTupleList);
        }
        else {
            /**
             * two Integers with the same value are
             * considered different. Then we have to search in offsetList for
             * an integer with value equal to newOffset
             */
            boolean found = false;
            for (final Tuple2<Integer, Annotation> t : offsetTupleList ) {
            	int offset = t.f1;
                if ( offset == newOffset ) {
                    found = true;
                    break;
                }
            }
            if ( ! found )
            	offsetTupleList.add( new Tuple2<Integer, Annotation>(newOffset, annot));
        }
    }

	/**
	 * add the statements statementCode after the last statement of method methodName of
	 * prototype prototypeName of package packageName. statementCode is added to all methods with name
	 * methodName of the given prototype.  Metaobject cyanMetaobject asked for that.
	   @param cyanMetaobject
	   @param packageName
	   @param prototypeName
	   @param methodName
	   @param statementCode code to be added after the last statement of the method
	   @return <code>true</code> if statementCode can be added, <code>false</code> otherwise.
	 *
	boolean addEndMethod(       CyanMetaobjectAtAnnot cyanMetaobject, String packageName, String prototypeName, String methodName, StringBuffer statementCode);
	*/


	/*
	public HashSet<CompilationUnit> getCompilationUnitChangedSet() {
		return compilationUnitChangedSet;
	} */

	public void error(Symbol sym, String specificMessage, String identifier,
			ErrorKind errorKind, String... furtherArgs) {
		env.error(true, sym, specificMessage, identifier, errorKind, furtherArgs);
	}


	/**
	 * map containing pairs <code>(s, m)</code> in which <code>s</code> is a string of the form
	 * <code>"packageName prototypeName methodName"</code> and <code>m</code> is a metaobject.
	 * Metaobject <code>m</code> asked to add method <code>methodName</code> to prototype
	 * <code>prototypeName</code> of package <code>packageName</code>. This map is used to check
	 * whether two different metaobjects try to add the same method to a prototype. This is illegal.
	 * A method name is just a concatenation of the method keywords such as "at:put:".
	 */
	private final HashMap<String, CyanMetaobjectAtAnnot> packagePrototypeMethodMap;


	/**
	 * list of tuples containing a metaobject, package name, a prototype name, and a source code
	 * that should be inserted in the prototype
	*/
	private final List<Tuple4<CyanMetaobjectAtAnnot, String, String, StringBuffer>> codeToAddList;



	/**
	 * list of tuples containing a metaobject, package name, a prototype name, a source code
	 * that should be inserted in the prototype, and a string with the list of fields and
	 * methods (only signatures) that should be inserted
	*/
	private final List<Tuple5<CyanMetaobject, String, String, StringBuffer, String>> codeToAddList2;

	/**
	 * list of tuples containing a metaobject, package name, a prototype name, method name, and the source code of a method
	 * that should be inserted in the prototype
	*/
	private final List<Tuple5<CyanMetaobjectAtAnnot, String, String, String, StringBuffer>> methodToAddList;

	/**
	 * list of tuples containing a metaobject, package name, a prototype name, a method signature (with
	 * full name of the parameters), and the source code of a method
	 * that should be inserted before the first statement of the method.
	*/
	private final List<Tuple5<CyanMetaobject, String, String, String, StringBuffer>> beforeMethodToAddList;

	/**
	 * list of tuples containing a metaobject, a package name, a prototype name, and a source code to be
	 * added after the metaobject annotation.
	 */
	private final List<Tuple4<CyanMetaobjectAtAnnot, String, String, StringBuffer>> codeToAddAtAnnotationList;

	/**
	 * map containing pairs <code>(s, m)</code> in which <code>s</code> is a string of the form
	 * <code>packageName prototypeName fieldName</code> and <code>m</code> is a metaobject.
	 * Metaobject <code>m</code> asked to add field <code>fieldName</code> to prototype
	 * <code>prototypeName</code> of package <code>packageName</code>. This map is used to check
	 * whether two different metaobjects try to add the same field to a prototype. This is illegal.
	 */
	private final HashMap<String, CyanMetaobjectAtAnnot> packagePrototypeFieldMap;

	/**
	 * map containing pairs <code>(s, m)</code> in which <code>s</code> is a string of the form
	 * <code>packageName prototypeName methodName</code> and <code>m</code> is a metaobject.
	 * Metaobject <code>m</code> asked to rename method methodname of prototype
	 * <code>prototypeName</code> of package <code>packageName</code>. This map is used to check
	 * whether two different metaobjects try to rename the same method of a prototype. This is illegal.
	 */
	private final HashMap<String, CyanMetaobject> packagePrototypeMethodRenameMap;
	/**
	 * list of tuples containing a metaobject, package name, a prototype name, qualifiers (public, private, shared, let, var), a type name, and a field name
	 * that should be inserted in this prototype
	*/
	private final List<Tuple6<CyanMetaobjectAtAnnot, String, String, String, String, String>> fieldToAddList;

	/**
	 * list of tuples containing a metaobject, package name, a prototype name,
	 * old method name (with the parameters like 'at:1 with:2'), and an array with the new keyword names
	 *
	*/

	private final List<Tuple5<CyanMetaobject, String, String, String, String[]>> renameMethodList;
	  /*
	   * set with the compilation units that have been changed during the compiler phase "AFTER_RES_TYPES".
	   * These compilation units should be compiled again.
	   * /
	private HashSet<CompilationUnit> compilationUnitChangedSet; */



	private final Env env;

}
