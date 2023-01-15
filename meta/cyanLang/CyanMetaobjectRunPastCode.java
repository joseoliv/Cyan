package meta.cyanLang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import cyan.lang.CyString;
import cyanruntime.ExceptionContainer__;
import cyanruntime.Ref;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.DirectoryKindPPP;
import meta.IAbstractCyanCompiler;
import meta.IAction_semAn;
import meta.ICompiler_ded;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.InterpretationErrorException;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrAnnotationAt;
import meta.WrStatement;
import saci.CyanCodeSnippet;
import saci.LoadUtil;

/**
 *
   @author jose
 */
public class CyanMetaobjectRunPastCode extends CyanMetaobjectAtAnnot
implements IAction_semAn {

	public CyanMetaobjectRunPastCode() {
		super("runPastCode", AnnotationArgumentsKind.OneOrMoreParameters);
	}


	@Override
	public boolean shouldTakeText() { return true; }


	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {

		checkIfPackageWasImported(compiler_semAn, "cyan.io");



		Object param = this.getAnnotation().getJavaParameterList().get(0);
		if ( !(param instanceof Boolean) ) {
			this.addError("The parameter to this annotation should be 'true' or 'false'");
			return null;
		}
		cyanCode = new String( ((WrAnnotationAt) annotation).getTextAttachedDSL() );
		shouldRun = (Boolean ) param;



		if ( shouldRun ) {
			final Ref<Object> ret = new Ref<>();
			ret.elem = null;
			ICompiler_parsing compiler = MetaHelper.createNewCompiler_parsing(cyanCode);
			try {
				statList = MetaHelper.parseCyanStatementList(compiler);
			}
			catch (InterpretationErrorException e) {
				addError(e.getMessage());
				return null;
			}
			catch (CompileErrorException e) {
				// addError(e.getMessage());
				return null;
			}


			List<Object> javaParamList = this.getAnnotation().getJavaParameterList();

			String [] varNameList = new String [javaParamList.size()];
			varNameList[0] = "compiler";
			Object [] objValueList = new Object [javaParamList.size()];
			objValueList[0] = compiler_semAn;


			// if ( ! fillVarNameList(javaParamList) ) { return null; }


			LoadUtil.addSaciOutputDirToClassPath(compiler_semAn.getEnv().getProject().getProjectDir(), () -> {

				for(int i = 1; i < javaParamList.size(); ++i) {
					Object obj = javaParamList.get(i);
					String varName = CyanMetaobject.removeQuotes((String ) obj);
					varNameList[i] = varName;

					WrAnnotationAt annotation = this.getAnnotation();
					final String filename = getFilenameVar(compiler_semAn, varName, annotation);
//					SerializeContainer sc = new SerializeContainer(null);
//					sc.load(filename);
//					objValueList[i] = sc.value;


					try (   java.io.FileInputStream file = new java.io.FileInputStream(filename);
							java.io.ObjectInputStream in = new java.io.ObjectInputStream(file) ) {
						//objValueList[i] = in.readObject();
						objValueList[i] = LoadUtil.readObject(filename, Thread.currentThread().getContextClassLoader());

					}
					catch (ClassNotFoundException e) {
						this.addError("Error when retrieving value of variable '" + varName + "' from file '" +
					       filename + "'. Class '" + e.getMessage() + "' was not found.");
					}
					catch (IOException e) {
						this.addError("Error when retrieving value of variable '" + varName + "' from file '" + filename + "'");
					}



				}

				ret.elem = MetaHelper.interpreterFor_MOPInterfaceMethod(
						statList,
						compiler_semAn,
						this,
						"semAn_codeToAdd",
						varNameList,
						objValueList,
						CyString.class);

			} );
			if ( ret.elem == null ) {
				return null;
			}
			else if ( !(ret.elem instanceof CyString) ) {
				this.addError("The code of this annotation should return an object of cyan.lang.String. "
						+ "It returned an object of class/prototype '" + ret.elem.getClass().getCanonicalName() + "'");
				return null;
			}
			else {
				return new StringBuffer( ((CyString ) ret.elem).s );
			}
		}
		else {
			List<Object> javaParamList = this.getAnnotation().getJavaParameterList();
			if ( javaParamList.size() > 1 ) {
				StringBuffer sb = new StringBuffer();
				//if ( ! fillVarNameList(javaParamList) ) { return null; }
				for(int i = 1; i < javaParamList.size(); ++i) {
					Object obj = javaParamList.get(i);
					String varName = CyanMetaobject.removeQuotes((String ) obj);
					if ( compiler_semAn.searchLocalVariable(varName) == null ) {
						this.addError("Variable '" + varName + "' was not found");
						return null;
					}

					WrAnnotationAt annotation = this.getAnnotation();
					final String filename = CyanMetaobject.escapeString(getFilenameVar(compiler_semAn, varName, annotation));

					sb.append("    ContainerForSerialize saveVariable: " + varName + ", \"" + filename + "\";\n");
				}
				return sb;
			}
			return null;
		}
	}



	/**
	   @param compiler
	   @param varName
	   @param annotation
	   @return
	 */
	private static String getFilenameVar(IAbstractCyanCompiler compiler, String varName,
			WrAnnotationAt annotation) {
		return compiler.getPathFileHiddenDirectory(
				annotation.getPrototypeOfAnnotation(),
				annotation.getPackageOfAnnotation(),
				DirectoryKindPPP.TMP) +  "keepValue_" + varName + ".txt";
	}

//
//	/**
//	   @param javaParamList
//	 */
//	private boolean fillVarNameList(List<Object> javaParamList) {
//		varNameList = new ArrayList<>();
//		for(int i = 1; i < javaParamList.size(); ++i) {
//			Object obj = javaParamList.get(i);
//			if ( !(obj instanceof String) ) {
//				this.addError("All parameters of this annotation, after the first one, should be literal strings");
//				return false;
//			}
//			String varName = CyanMetaobject.removeQuotes((String ) obj);
//			varNameList.add(varName);
//		}
//		return true;
//	}

//	public void parsing_parse(String code) {
//
//		Object param = this.getAnnotation().getJavaParameterList().get(0);
//		if ( !(param instanceof Boolean) ) {
//			this.addError("The parameter to this annotation should be 'true' or 'false'");
//			return ;
//		}
//		cyanCode = code;
//		shouldRun = (Boolean ) param;
//	}

	/**
	 * semAn_codeToAdd  for Codeg 'cyan' that uses the syntax:
	 *      {@literal @}cyan(codegId, v1, v2, ..., vn)
	 *
	 * This method should be called whenever
	 *    - botton 'run' of the GUI of the Codeg is pressed
	 *    - after 0.5 second of the last keypressed (if in mode 'live')
	 *
	 * This method run the code and returns a tuple composed of:
	 *    - an error string, null if no error;
	 *    - the result of the computation (the value of the expression
	 *       following 'return')
	 *
	   @param compiler_semAn
	   @return
	 */

	public Tuple2<String, Object> shouldRun(ICompiler_ded compiler_semAn) {

		final Ref<Object> ret = new Ref<>();
		ret.elem = null;
		ICompiler_parsing compiler = MetaHelper.createNewCompiler_parsing(cyanCode);
		try {
			statList = MetaHelper.parseCyanStatementList(compiler);
		}
		catch (InterpretationErrorException e) {
			return new Tuple2<String, Object>(e.getMessage(), null);
		}
		catch (CompileErrorException e) {
			// addError(e.getMessage());
			return new Tuple2<String, Object>(e.getMessage(), null);
		}


		List<Object> javaParamList = this.getAnnotation().getJavaParameterList();

		varNameList = new String [javaParamList.size()-1];
		objValueList = new Object [javaParamList.size()-1];


		// if ( ! fillVarNameList(javaParamList) ) { return null; }


		LoadUtil.addSaciOutputDirToClassPath(compiler_semAn.getProjectDir(), () -> {

//			if ( varNameList.length > 0 && objValueList.length > 0 ) {
//
//			}
			for(int i = 1; i < javaParamList.size(); ++i) {
				Object obj = javaParamList.get(i);
				String varName = CyanMetaobject.removeQuotes((String ) obj);
				varNameList[i-1] = varName;


				WrAnnotationAt annotation = this.getAnnotation();

				final String filename = getFilenameVar(compiler_semAn, varName, annotation);
//				SerializeContainer sc = new SerializeContainer(null);
//				sc.load(filename);
//				objValueList[i] = sc.value;


				try (   java.io.FileInputStream file = new java.io.FileInputStream(filename);
						java.io.ObjectInputStream in = new java.io.ObjectInputStream(file) ) {
					//objValueList[i] = in.readObject();
					objValueList[i-1] = LoadUtil.readObject(filename, Thread.currentThread().getContextClassLoader());

				}
				catch (ClassNotFoundException e) {
					objValueList[i-1] = null;
					/* this.addError("Error when retrieving value of variable '" + varName + "' from file '" +
				       filename + "'. Class '" + e.getMessage() + "' was not found."); */
				}
				catch (IOException e) {
					objValueList[i-1] = null;
					//this.addError("Error when retrieving value of variable '" + varName + "' from file '" + filename + "'");
				}



			}

			ret.elem = btRunActionPerformed( varNameList, objValueList );

		} );
		if ( this.getErrorList().size() > 0 ) {
			return new Tuple2<String, Object>(this.getErrorList().get(0).getMessage(), null);
		}
		else {
			return new Tuple2<String, Object>(null, ret.elem);
		}
	}

	String [] varNameList;
	Object [] objValueList;

	private javax.swing.JTextArea txtCommand;
	private javax.swing.JTextArea txtOutput;


	private Object btRunActionPerformed( String [] varNameList, Object [] objValueList ) {

		PrintStream pout = System.out;
		PrintStream perr = System.err;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		System.setErr(ps);

		String cyanCode = txtCommand.getText();
		Object selfObject = new Object();
		String cyanLangDir = null;
		Object ret = null;
		try {
			CyanCodeSnippet cs = new CyanCodeSnippet();
			ret = cs.evalCyanCode(cyanCode, selfObject, cyanLangDir,
                varNameList, objValueList );
		}
		catch (CompileErrorException e) {
			System.out.println(e.getMessage());
		}
		catch (meta.InterpretationErrorException e ) {
			System.out.println(e.getMessage());
		}
		catch ( ExceptionContainer__ e ) {
			System.out.println("Exception " + e.elem._prototypeName().s + " was thrown");
		}
		catch ( Throwable e ) {
			System.out
			.println("Exception " + e.getClass().getCanonicalName() + " was thrown. Its message is '"
			        + e.getMessage() + "1" );
		}

		String output = baos.toString();
		txtOutput.setText(output); // + "\n\n TryCyan is over");

		System.setOut(pout);
		System.setErr(perr);


		formClicked();
		return ret;

	}

	void formClicked() {

	}


	public StringBuffer semAn_codeToAdd_ForCodegCyan(ICompiler_semAn compiler_semAn) {

		checkIfPackageWasImported(compiler_semAn, "cyan.io");


		List<Object> javaParamList = this.getAnnotation().getJavaParameterList();
		if ( javaParamList.size() > 1 ) {
			StringBuffer sb = new StringBuffer();
			//if ( ! fillVarNameList(javaParamList) ) { return null; }
			for(int i = 1; i < javaParamList.size(); ++i) {
				Object obj = javaParamList.get(i);
				String varName = CyanMetaobject.removeQuotes((String ) obj);
				if ( compiler_semAn.searchLocalVariable(varName) == null ) {
					this.addError("Variable '" + varName + "' was not found");
					return null;
				}

				WrAnnotationAt annotation = this.getAnnotation();
				final String filename = CyanMetaobject.escapeString(getFilenameVar(compiler_semAn, varName, annotation));

				sb.append("    ContainerForSerialize saveVariable: " + varName + ", \"" + filename + "\";\n");
				//sb.append("(SerializeContainer new: " + varName + ") save: " + filename + ";\n");

				// SerializeContainer new:
				/*
				 *

				 */
			}
			return sb;
		}
		return null;
		}




	String cyanCode = null;
	List<WrStatement> statList;
	boolean shouldRun = false;
//	List<String> varNameList;

}
