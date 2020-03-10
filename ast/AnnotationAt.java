/**

 */
package ast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import cyan.lang.CyInt;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT__GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._IActionNewPrototypes__dsa;
import cyan.reflect._IActionVariableDeclaration__dsa;
import cyan.reflect._IAction__dsa;
import cyan.reflect._ICodeg;
import cyan.reflect._IStayPrototypeInterface;
import error.ErrorKind;
import lexer.CompilerPhase;
import lexer.Lexer;
import lexer.Symbol;
import lexer.SymbolCharSequence;
import lexer.SymbolCyanMetaobjectAnnotation;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionNewPrototypes_dsa;
import meta.IActionVariableDeclaration_dsa;
import meta.IAction_cge;
import meta.IAction_dsa;
import meta.ICodeg;
import meta.IDeclaration;
import meta.IParseWithCyanCompiler_dpa;
import meta.ISlotInterface;
import meta.IStayPrototypeInterface;
import meta.MetaHelper;
import meta.ReplacementPolicyInGenericInstantiation;
import meta.Token;
import meta.Tuple2;
import meta.Tuple4;
import meta.WrAnnotationAt;
import meta.WrExpr;
import meta.WrExprAnyLiteral;
import meta.WrStatementLocalVariableDec;
import meta.WrSymbol;
import meta.cyanLang.CyanMetaobjectCompilationContextPop;
import meta.cyanLang.CyanMetaobjectCompilationContextPush;
import meta.cyanLang.CyanMetaobjectCompilationMarkDeletedCode;
import meta.cyanLang.CyanMetaobjectJavaCode;
import metaRealClasses.Compiler_dsa;
import saci.CompilerManager;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** Represents a metaobject annotation such as </p>
 * <code>
 *       {@literal @}feature("author", "José") </p>
 *  </code>
 *  ou </p> <code>
 *       {@literal @}textAttachedDSL<<* <br>
 *            This is a textAttachedDSL which can <br>
 *            have anything but '* > >' <br>
 *            without spaces <br>
 *       *>> </p>
 *       </code>
 *
 *
   @author José

 */
public class AnnotationAt extends Annotation {

	public AnnotationAt( CompilationUnit compilationUnit,
			SymbolCyanMetaobjectAnnotation symbolCyanMetaobjectAnnotation,
			                   CyanMetaobjectAtAnnot cyanMetaobject, boolean inExpr ) {
		super(compilationUnit, inExpr);
		this.symbolCyanMetaobjectAnnotation = symbolCyanMetaobjectAnnotation;
		this.cyanMetaobject = cyanMetaobject;
		this.cyanMetaobject.setMetaobjectAnnotation(this.getI());
		this.declaration = null;
		this.leftParenthesis = null;
		this.rightParenthesis = null;
		this.textAttachedDSL = null;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		if ( cyanEnv.getGenInterfacesForCompiledCode() ) {
			/*
			 * when generating the file with interfaces (for separate compilation),
			 * generate only the annotations that will be useful when the package
			 * or prototype is imported. That is, those annotations that can act
			 * in the prototype that imports the current prototype. These are
			 * the metaobjects whose classes implement IStayPrototypeInterface or
			 * have non-private visibility
			 */
			if ( !
				 ((cyanMetaobject instanceof IStayPrototypeInterface
						 || cyanMetaobject instanceof _IStayPrototypeInterface) ||
				  (cyanMetaobject.getVisibility() != Token.PRIVATE)) ) {
				return ;
			}
		}

		String cyanMetaobjectName;
		cyanMetaobjectName = symbolCyanMetaobjectAnnotation.getSymbolString();

		if ( codeMetaobjectAnnotationParseWithCompiler != null )
			pw.print(codeMetaobjectAnnotationParseWithCompiler);
		else {
			//pw.print(" ");
			final String at = "@";
			pw.printIdent(at + cyanMetaobjectName);
			if ( symbolCyanMetaobjectAnnotation.getPostfix() != null ) {
				pw.print("#" + symbolCyanMetaobjectAnnotation.getPostfix().getName());
			}
			if ( leftParenthesis != null ) {
				pw.print(leftParenthesis.getSymbolString());
				int size = realParameterList.size();
				for ( final WrExpr e : this.realParameterList ) {

					if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
						if ( e instanceof WrExprAnyLiteral  ) {
							final ExprAnyLiteral ie = meta.GetHiddenItem.getHiddenExprAnyLiteral((WrExprAnyLiteral) e);
							ie.genCyanReplacingGenericParameters(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
						}
						else
							meta.GetHiddenItem.getHiddenExpr(e).genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
					}
					else
						meta.GetHiddenItem.getHiddenExpr(e).genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);

					if ( --size > 0 )
						pw.print(", ");
				}
				pw.print(rightParenthesis.getSymbolString());
			}
			if ( leftCharSeqSymbol != null ) {
				pw.print( leftCharSeqSymbol.getSymbolString() );


				if ( textAttachedDSL[textAttachedDSL.length - 1] == '\0' )
					textAttachedDSL[textAttachedDSL.length - 1] = ' ';
				if ( ! cyanEnv.getCreatingInstanceGenericPrototype() )
				    pw.println(textAttachedDSL);
				else {
					switch ( cyanMetaobject.getReplacementPolicy() ) {
					case NO_REPLACEMENT:
						pw.println(textAttachedDSL);
						break;
					case REPLACE_BY_CYAN_VALUE:
						replacePrint(textAttachedDSL, pw, cyanEnv.getFormalParamToRealParamTable(), "",
								ReplacementPolicyInGenericInstantiation.REPLACE_BY_CYAN_VALUE);
						break;
					case REPLACE_BY_JAVA_VALUE:
						//final String genProto = cyanEnv.getExprGenericPrototypeInstantiation().getName();
						replacePrint(textAttachedDSL, pw, cyanEnv.getFormalParamToRealParamTable(), cyanEnv.getExprGenericPrototypeInstantiation().getName(),
								ReplacementPolicyInGenericInstantiation.REPLACE_BY_JAVA_VALUE);
					}
				}

				/*
				StringBuffer strText = new StringBuffer(textAttachedDSL.toString());
				if ( strText != null && strText.length() > 0 ) {
					strText.trimToSize();
					int last = strText.length() - 1;
				    if ( strText.charAt(last) == '\0' )
					    strText.setCharAt(last, ' ');
					if ( cyanEnv == null )
					    pw.println(strText);
					else {
					    char []charText = new char[strText.length()];
						strText.getChars(0, strText.length(), charText, 0);
						replacePrint(charText, pw, cyanEnv.getFormalRealTable());
					}
				} */
				pw.printlnIdent(rightCharSeqSymbol.getSymbolString());
			}
			pw.print(" ");
		}
		if ( cyanEnv.getPrintNewLineAfterAnnotation() ) {
			pw.println("");
		}

	}


	@Override
	public void genJava(PWInterface pw, Env env) {
		if ( cyanMetaobject instanceof IAction_cge ) {
			// // cyanMetaobject.setMetaobjectAnnotation(this, 0);
			StringBuffer strText = null;


			try {
				strText = ( (IAction_cge ) cyanMetaobject).cge_codeToAdd();
			}
			catch ( final error.CompileErrorException e ) {
			}
			catch ( final NoClassDefFoundError e ) {
				env.error(this.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
			}
			catch ( final RuntimeException e ) {
				env.thrownException(this, this.getFirstSymbol(), e);
			}
			finally {
				env.errorInMetaobjectCatchExceptions(cyanMetaobject);
			}


			if (  strText != null ) {
				final int last = strText.length() - 1;
				if ( strText.charAt(last) == '\0' )
					strText.setCharAt(last, ' ');
				final int size = strText.length();
				String s = "";
				for ( int i = 0; i < size; ++i ) {
					final char ch = strText.charAt(i);
					if( Character.LINE_SEPARATOR == Character.getType(ch) ) {
						pw.printlnIdent(s);
						s = "";
					}
					s = s + ch;
				}
				pw.printlnIdent(s);
			}
		}

	}



	@Override
	public String genJavaExpr(PWInterface pw, Env env) {

		env.error(this.getFirstSymbol(),  "Internal error: a metaobject is being used as an expression and the compiler wants it to generate Java code."
				+ " The metaobject annotation should have been replaced by Cyan code in previous phases.");
		return "/* this should not be used as variable */";
	}


	@Override
	public boolean isParsedWithCompiler() {
		return this.cyanMetaobject instanceof IParseWithCyanCompiler_dpa;
	}


	@Override
	public void calcInternalTypes(Env env) {

		super.calcInternalTypes(env);

		if ( realParameterList != null ) {
			for ( final WrExpr e : this.realParameterList ) {
				final Expr ee = meta.GetHiddenItem.getHiddenExpr(e);

				if ( ee instanceof ExprLiteralArray || ee instanceof ExprLiteralTuple ) {
					/*
					if (  e instanceof ExprLiteralTuple ) {
						ExprLiteralTuple t = (ExprLiteralTuple ) e;
					}
					*/
					ee.calcInternalTypes(env);
				}
			}
		}


		if ( cyanMetaobject instanceof CyanMetaobjectCompilationContextPush ) {
			/**
			 * The metaobject annotation may have only two parameters. See {@link meta#CyanMetaobjectCompilationContextPush}. In this case the compiler
			 * has introduced <code>null</code> in the last three positions of
			 * the javaParameterList.
			 */
			final String id = (String ) javaParameterList.get(0);


			final String cyanMetaobjectName = (String ) javaParameterList.get(1);
			String packageName;
			String sourceFileName;
			int lineNumber = 0;
			if ( javaParameterList.size() <= 2 || javaParameterList.get(2) == null ) {
				if ( env.getCurrentProgramUnit() == null )
					packageName = "Unidentified";
				else
					packageName = env.getCurrentCompilationUnit().getPackageName();
			}
			else {
				packageName = (String ) javaParameterList.get(2);
			}

			if ( javaParameterList.size() <= 3 || javaParameterList.get(3) == null ) {
				if ( env.getCurrentProgramUnit() == null )
					sourceFileName = "Unidentified";
				else
					sourceFileName = env.getCurrentProgramUnit().getName();
			}
			else {
				sourceFileName = (String ) javaParameterList.get(3);
			}

			if ( javaParameterList.size() <= 4 || javaParameterList.get(4) == null ) {
                lineNumber = -1;
			}
			else {
                lineNumber = (Integer ) javaParameterList.get(4);
			}
			List<ISlotInterface> slotList = null;
			if ( javaParameterList.size() > 5 && javaParameterList.get(5) != null ) {
                String strSlotList = meta.MetaHelper.removeQuotes( (String ) javaParameterList.get(5));
                /*
                 * six parameters mean we are in a program unit
                 */
                ProgramUnit currentPU = env.getCurrentProgramUnit();
				slotList = currentPU.extractSlotListFrom(strSlotList, this, env, this.getFirstSymbol());
			}


			env.pushCompilationContext(id, cyanMetaobjectName, packageName, sourceFileName, lineNumber,
					this.symbolCyanMetaobjectAnnotation.getOffset(), slotList);
		}
		else if ( cyanMetaobject instanceof CyanMetaobjectCompilationContextPop ) {
			env.checkPushStackEmpty();

			final String id = (String ) javaParameterList.get(0);
			env.popCompilationContext(id, this.symbolCyanMetaobjectAnnotation);
		}
		else if ( cyanMetaobject instanceof CyanMetaobjectCompilationMarkDeletedCode ) {
			env.addToLineShift(-((CyanMetaobjectCompilationMarkDeletedCode) cyanMetaobject).getNumLinesDeleted());
		}
		else if ( cyanMetaobject instanceof CyanMetaobjectJavaCode ) {
			env.setHasJavaCode(true);
		}
		/* else
		if ( cyanMetaobject instanceof CyanMetaobjectCompilationMarkDeletedCode ) {
			env.markDeletedCodeCompilation( ((CyanMetaobjectCompilationMarkDeletedCode) cyanMetaobject).getNumLinesDeleted() );
		} */

		dsaActions(env);

		ProgramUnit pu;
		if ( cyanMetaobject.getPackageOfType() == null || cyanMetaobject.getPrototypeOfType() == null ) {
			pu = env.searchPackagePrototype(MetaHelper.cyanLanguagePackageName, "Nil");
		}
		else {
			pu = env.searchPackagePrototype(cyanMetaobject.getPackageOfType(), cyanMetaobject.getPrototypeOfType());
		}

		if ( pu == null ) {
			type = env.searchPackageJavaClass(cyanMetaobject.getPackageOfType(), cyanMetaobject.getPrototypeOfType());
			if ( type == null ) {
				env.error(true,
						this.getFirstSymbol(),
								"Metaobject has type '" + cyanMetaobject.getPackageOfType() + "." +
										cyanMetaobject.getPrototypeOfType() + "' which was not found", cyanMetaobject.getPrototypeOfType(), ErrorKind.prototype_was_not_found_inside_method);
			}
		}
		else
			type = pu;

		finalizeCalcInternalTypes(env);
	}

	/**
	   @param env
	 */
	public void dsaActions(Env env) {
		Compiler_dsa compiler_dsa = null;
		if ( env.getCompInstSet().contains(meta.CompilationInstruction.dsa_actions) ) {
			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			if ( cyanMetaobject instanceof IAction_dsa || (other != null && other instanceof _IAction__dsa) ) {

				/*
				if ( env.getDuring_dsa_actions() ) {
					env.error(this.getFirstSymbol(), "A dsa action cannot occur inside another dsa actions. For example, you cannot have a macro expansion inside another macro expansion or even a literal object as r\"[a-z]+\" inside a macro");
				}
				*/
				try {
					env.begin_dsa_actions();
					// // cyanMetaobject.setMetaobjectAnnotation(this, 0);
					compiler_dsa = new Compiler_dsa(env, this);
					StringBuffer cyanCode = null;

					try {
						if ( other == null ) {
							final IAction_dsa cyanMetaobjectCodeGen = (IAction_dsa ) cyanMetaobject;
							cyanCode = cyanMetaobjectCodeGen.dsa_codeToAdd(compiler_dsa);
						}
						else {
							cyanCode = new StringBuffer( ((_IAction__dsa ) other)._dsa__codeToAdd_1(compiler_dsa).s ) ;
						}

					}
					catch ( final error.CompileErrorException e ) {
					}
					catch ( final NoClassDefFoundError e ) {
						env.error(this.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( final RuntimeException e ) {
						//e.printStackTrace();
						env.thrownException(this, this.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobjectCatchExceptions(cyanMetaobject);
					}

					/*

						env.error(this.getFirstSymbol(), "This metaobject implements interface '" + IAction_dsa.class.getName() + "' "
								+ "but it is not generating code in phase DSA");

										 *
					 */


					if ( cyanCode == null || cyanCode.length() == 0 || cyanCode.charAt(0) == '\0' ) {


						if ( this.symbolCyanMetaobjectAnnotation.getPostfix() == null ) {
							// this metaobject did not generated code in previous compilation phases.
							// Then it should generated in this phase.
							if ( this.cyanMetaobject.isExpression() ) {
								if ( this.cyanMetaobject instanceof meta.ICodeg ) {
									env.error(this.getFirstSymbol(),
											"Codeg metaobject '" + this.cyanMetaobject.getName() +
											    "' is not generating code in phase dsa as expected. This probably is because " +
											"this Codeg was not initialized at editing time. Just put the mouse over it to edit it");
								}
								else {
									env.error(this.getFirstSymbol(),  "Metaobject '" + this.cyanMetaobject.getName() + "' is not generating code in phase dsa as expected");
								}
							}
						}
					}
					else {

						/*
						 * metaobjects attached to declarations cannot generate code
						 */
						if ( this.declaration != null ) {
							env.error(this.getFirstSymbol(), "Metaobject '" + this.cyanMetaobject.getName() +
									"' is attached to a declaration. But it is trying to generate code. This is illegal");
						}
						if (  ! this.insideMethod  ) {
							env.error(this.getFirstSymbol(), "Metaobject '" + this.cyanMetaobject.getName() +
									"' is outside a method and it is trying to generate code in phase dsa (semantic analysis). This is illegal");
						}
						/**
						 * annotations outside methods cannot generate code
						 */

						if ( cyanCode.charAt(cyanCode.length() - 1) == '\0' )
							cyanCode.deleteCharAt(cyanCode.length()-1);

						if ( this.isParsedWithCompiler() &&
								env.sizeStackMetaobjectAnnotationParseWithCompiler() > 1 ) {
							/*
							 * this metaobject annotation is a literal object that is inside other literal object
							 */
							this.setCodeMetaobjectAnnotationParseWithCompiler(cyanCode);
						}
						else {
							// env.removeCodeMetaobjectAnnotation(cyanMetaobject);
							this.codeThatReplacesThisStatement = new StringBuffer(
									this.asString() + " " + env.addCodeAtMetaobjectAnnotation(cyanMetaobject, cyanCode, -1));

						}

					}

				}
				finally {
					env.end_dsa_actions();
				}

			}
			if ( cyanMetaobject instanceof IActionNewPrototypes_dsa ||
					(other != null && other instanceof _IActionNewPrototypes__dsa)) {
				List<Tuple2<String, StringBuffer>> prototypeNameCodeList = null;
				try {
					if ( compiler_dsa == null ) {
						compiler_dsa = new Compiler_dsa(env, this);
					}
					if ( other == null ) {
						prototypeNameCodeList = ((IActionNewPrototypes_dsa ) cyanMetaobject)
								.dsa_NewPrototypeList(compiler_dsa);
					}
					else {
						_IActionNewPrototypes__dsa anp = (_IActionNewPrototypes__dsa ) other;
						_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT__GT array =
								anp._dsa__NewPrototypeList_1(compiler_dsa);
						int size = array._size().n;
						if ( size > 0 ) {
							prototypeNameCodeList = new ArrayList<>();
							for (int i = 0; i < size; ++i ) {
								_Tuple_LT_GP_CyString_GP_CyString_GT tss = array._at_1(new CyInt(i));
								String f1 = tss._f1().s;
								String f2 = tss._f2().s;
								if ( f1.length() > 0 ) {
									prototypeNameCodeList.add( new Tuple2<String, StringBuffer>(f1,
											new StringBuffer(f2)));
								}
							}
						}

					}


				}
				catch ( final error.CompileErrorException e ) {
				}
				catch ( final NoClassDefFoundError e ) {
					env.error(this.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
				}
				catch ( final RuntimeException e ) {
					env.thrownException(this, this.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(cyanMetaobject);
				}
				if ( prototypeNameCodeList != null ) {
					for ( final Tuple2<String, StringBuffer> prototypeNameCode : prototypeNameCodeList ) {
						final CompilationUnit cunit = (CompilationUnit ) this.compilationUnit;
						final Tuple2<CompilationUnit, String> t = env.getProject().getCompilerManager().createNewPrototype(prototypeNameCode.f1, prototypeNameCode.f2,
								cunit.getCompilerOptions(), cunit.getCyanPackage());
						if ( t != null && t.f2 != null ) {
							env.error(this.getFirstSymbol(), t.f2);
						}
					}
				}
			}

			if ( cyanMetaobject instanceof IActionVariableDeclaration_dsa ||
					(other != null && other instanceof _IActionVariableDeclaration__dsa)) {
				/*
				 * add code after the local variable declaration
				 */
				final IDeclaration dec = this.getDeclaration();
				if ( !(dec instanceof WrStatementLocalVariableDec) ) {
					env.error(this.getFirstSymbol(),  "Metaobject '" + cyanMetaobject.getName() + "' can only be attached to declaration of local variables");
				}
				final WrStatementLocalVariableDec stat = (WrStatementLocalVariableDec ) dec;
				final WrSymbol symbolAfter = stat.getSymbolAfter();
//				if ( symbolAfter == null ) {
//					System.out.println("symbolAfter is null");
//					stat.getSymbolAfter();
//					return ;
//				}
				int offset = symbolAfter.getOffset();
				if ( symbolAfter.token == Token.SEMICOLON ) {
					++offset;
				}
				StringBuffer code = null;


				try {
					if ( other == null ) {
						final IActionVariableDeclaration_dsa actionVar = (IActionVariableDeclaration_dsa ) cyanMetaobject;
						code = actionVar.dsa_codeToAddAfter(env.getI());
					}
					else {
						((_IActionVariableDeclaration__dsa ) other)._dsa__codeToAddAfter_1(env.getI());
					}
				}
				catch ( final error.CompileErrorException e ) {
				}
				catch ( final NoClassDefFoundError e ) {
					env.error(this.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
				}
				catch ( final RuntimeException e ) {
					env.thrownException(this, this.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(cyanMetaobject);
				}
				this.codeThatReplacesThisStatement = new StringBuffer(env.addCodeAtMetaobjectAnnotation(cyanMetaobject, code, offset));
			}
		}
	}




	public static void replacePrint(char []text,  PWInterface pw, Hashtable<String, String> formalRealTable, String currentPrototypeName,
			ReplacementPolicyInGenericInstantiation replacementPolicy) {
		pw.print(CompilerManager.replaceOnly(text, formalRealTable, currentPrototypeName, replacementPolicy));
	}

	public static void copyCharArray(char []target, char []source) {
		System.arraycopy(source, 0, target, 0, source.length);
		/*
		for (int i = 0; i < source.length; ++i)
			target[i] = source[i];
		*/
	}


	@Override
	public CyanMetaobjectAtAnnot getCyanMetaobject() {
		return cyanMetaobject;
	}


	@Override
	public  Symbol getFirstSymbol() {
		return this.symbolCyanMetaobjectAnnotation;
	}

	/*
	@Override
	public WrExpr getDeclaration() {
		return new WrExpr(this);
	}
	*/

	@Override
	public WrAnnotationAt getI() {
		if ( iCyanMetaobjectWithAtAnnotation == null ) {
			iCyanMetaobjectWithAtAnnotation = new WrAnnotationAt(this);
		}
		return this.iCyanMetaobjectWithAtAnnotation;
	}

	private WrAnnotationAt iCyanMetaobjectWithAtAnnotation = null;

	/**
	 * return the declaration or expression associated to this metaobject annotation or null if there is
	 * no one. A declaration can be a prototype, field, or method.
	 * Therefore 'declaration' can be an object of ProgramUnit, FieldDec,
	 * MethodDec, OR an expression.
	 */


	public IDeclaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(IDeclaration declaration) {
		this.declaration = declaration;
	}

	public List<WrExprAnyLiteral> getRealParameterList() {
		return realParameterList;
	}


	public void setRealParameterList(List<WrExprAnyLiteral> realParameterList) {
		this.realParameterList = realParameterList;
	}

	public List<Object> getJavaParameterList() {
		return javaParameterList;
	}

	/**
	 * Return the Java parameter of this metaobject annotation as a valid Java string.
	 * That is, if the parameter is an int, as 123, "123" is returned. If it
	 * is a string "ok",  "\"ok\"" is returned.
	   @param i
	   @return
	 */
	public String javaParameterAt(int i) {
		return convert(javaParameterList.get(i));
	}

	private static String convert(Object param) {
		if ( param instanceof String ) {
			String s = (String ) param;
			if ( s.length() > 0 && s.charAt(0) == '\"' && s.charAt(s.length()-1) == '\"' ) {
				return s;
			}
			else {
				return "\"" + (String ) param + "\"";
			}
		}
		else
			return param.toString();
	}

	public void setJavaParameterList(List<Object> javaParameterList) {
		this.javaParameterList = javaParameterList;
	}


	public SymbolCyanMetaobjectAnnotation getSymbolMetaobjectAnnotation() {
		return symbolCyanMetaobjectAnnotation;
	}

	public void setSymbolCyanMetaobjectAnnotation(SymbolCyanMetaobjectAnnotation symbolCyanMetaobjectAnnotation) {
		this.symbolCyanMetaobjectAnnotation = symbolCyanMetaobjectAnnotation;
	}

	public Symbol getLeftParenthesis() {
		return leftParenthesis;
	}

	public void setLeftParenthesis(Symbol leftDelimArgs) {
		this.leftParenthesis = leftDelimArgs;
	}

	public Symbol getRightParenthesis() {
		return rightParenthesis;
	}

	public void setRightParenthesis(Symbol rightDelimArgs) {
		this.rightParenthesis = rightDelimArgs;
	}

	public char[] getTextAttachedDSL() {
		return textAttachedDSL;
	}

	public void setTextAttachedDSL(char[] text) {
		this.textAttachedDSL = text;
	}

	public SymbolCharSequence getLeftCharSeqSymbol() {
		return leftCharSeqSymbol;
	}

	public void setLeftCharSeqSymbol(SymbolCharSequence leftCharSeqSymbol) {
		this.leftCharSeqSymbol = leftCharSeqSymbol;
	}

	public SymbolCharSequence getRightCharSeqSymbol() {
		return rightCharSeqSymbol;
	}

	public void setRightCharSeqSymbol(SymbolCharSequence rightCharSeqSymbol) {
		this.rightCharSeqSymbol = rightCharSeqSymbol;
	}


	@Override
	public CompilationUnitSuper getCompilationUnit() {
		return compilationUnit;
	}

	@Override
	public ProgramUnit getProgramUnit() {
		return programUnit;
	}

	@Override
	public CompilerPhase getPostfix() {
		return symbolCyanMetaobjectAnnotation.getPostfix();
	}




	/**
	 * return the information store at editing time by the visual interface of the Codeg
	   @return
	 */
	public byte []getCodegInfo() {
		return codegInfo;
	}

	public void setCodegInfo(byte []codegInfo) {
		this.codegInfo = codegInfo;
	}


	/**
	 * the complete name of the metaobject associated to this annotation. If the metaobject is a codeg,
	 * it is returned the metaobject name and the first parameter. Something like<br>
	 * <code>"color(red)"</code><br>
	 * If the metaobject is not a codeg, the metaobject name is returned.
	 */
	public String getCompleteName() {
		if ( this.cyanMetaobject instanceof ICodeg ) {
			final ExprAnyLiteral ea = meta.GetHiddenItem.getHiddenExprAnyLiteral(realParameterList.get(0));
			String id;
			if ( ea instanceof ExprAnyLiteralIdent ) {
				id = ((ExprAnyLiteralIdent ) ea).getIdentExpr().getName();
			}
			else {
				id = "unknown";
			}

			final String codegInfoFilename = this.cyanMetaobject.getName() +
					"(" + id + ")" ;
			return codegInfoFilename;
		}
		else {
			return this.cyanMetaobject.getName();
		}
	}

	/**
	 * return the complete path of the file that keeps information on this metaobject annotation.
	 * Usually this is only used for codegs. Returns null if this annotation is outside a compilation unit (this
	 * should never happens
	 */
	public String filenameMetaobjectAnnotationInfo(String firstParameter) {
		if ( compilationUnit == null )
			return null;
		String ext = "txt";
		String id = "";
		if ( firstParameter == null ) {
			_CyanMetaobject other = cyanMetaobject.getMetaobjectInCyan();
			if ( this.cyanMetaobject instanceof ICodeg || (other != null && other instanceof _ICodeg) ) {
				if ( other == null ) {
					ext = ((ICodeg ) this.cyanMetaobject).getFileInfoExtension();
				}
				else {
					ext = ((_ICodeg ) other)._getFileInfoExtension().s;
				}
				final ExprAnyLiteral ea = meta.GetHiddenItem.getHiddenExprAnyLiteral(this.getRealParameterList().get(0));
				if ( ea instanceof ExprAnyLiteralIdent ) {
					id = ((ExprAnyLiteralIdent ) ea).getIdentExpr().getName();
				}
			}
		}
		else {
			id = firstParameter;
		}
		final String codegPath = this.compilationUnit.getCanonicalPathUpDir() + NameServer.getCodegDirFor(this.getPrototypeOfAnnotation());


		final String codegInfoFilename = codegPath + NameServer.fileSeparatorAsString + this.cyanMetaobject.getName() +
				"(" + id + ")." + ext;
		return codegInfoFilename;
	}



	@Override
	public List<Tuple4<Integer, Integer, Integer, Integer>> getColorTokenList() {
		if ( colorTokenList == null ) {
			if ( this.cyanMetaobject != null ) {
				colorTokenList = this.cyanMetaobject.getColorList();
			}
		}
		return colorTokenList;
	}

	public void setColorTokenList(List<Tuple4<Integer, Integer, Integer, Integer>> colorTokenList) {
		this.colorTokenList = colorTokenList;
	}



	public Type getTypeAttached() {
		return typeAttached;
	}

	public void setTypeAttached(Type typeAttached) {
		this.typeAttached = typeAttached;
	}


	@Override
	public void replaceAnnotationBy( String newAnnotationText ) {
		final int offsetStart = this.symbolCyanMetaobjectAnnotation.getOffset();
		int offsetEnd;

		if ( this.rightCharSeqSymbol != null ) {
			offsetEnd = this.rightCharSeqSymbol.getOffset() + this.rightCharSeqSymbol.getSymbolString().length();
		}
		else {
			// no DSL between sequence delimiteres. Do the annotation have parameters?
			if ( this.rightParenthesis != null ) {
				// has a right ')'
				offsetEnd = this.rightParenthesis.getOffset() + 1;
			}
			else {
				// no parenthesis.   The + 1
				offsetEnd = offsetStart + this.symbolCyanMetaobjectAnnotation.getSymbolString().length() + 1;
			}
		}
		this.compilationUnit.setOriginalText(
				Lexer.replaceTextByNewText(offsetStart, offsetEnd,
						this.compilationUnit.getOriginalText(), newAnnotationText.toCharArray()) );
	}

	/**
	 * return a string with the annotation but with a new first parameter. That is, if the annotation is<br>
	 * <code> {@literal @}color(red)</code><br>
	 * and newFirstParameter is <code>"cyan"</code> then this method returns<br>
	 * <code> {@literal @}color(cyan)</code><br>
	 * Note that the annotation is NOT changed. If the annotation has not any parameter, a new parameter is inserted

	   @param newFirstParameter
	   @return
	 */
	public String newAnnotationText(String newFirstParameter) {
		String s = "@" + this.cyanMetaobject.getName() + "(" + newFirstParameter;
		if ( this.leftParenthesis == null ) {
			// no parameters, introduce this
			s += ")";
		}
		else {
			if ( realParameterList != null ) {
				final int numParam = realParameterList.size();
				for ( int i = 1; i < numParam; ++i) {
					final WrExprAnyLiteral e = this.realParameterList.get(i);
					s += e.asString();
					if ( i < numParam - 1 ) {
						s += ", ";
					}
				}
			}
		}
		s += ")";
		if ( this.rightCharSeqSymbol != null ) {
			// there is a DSL attached to the annotation
			s += this.leftCharSeqSymbol.getSymbolString() + new String(this.getTextAttachedDSL()) + this.rightCharSeqSymbol.getSymbolString();
		}
		return s;
	}


	/**
	 * return a string with the annotation but with a new DSL textAttachedDSL, the textAttachedDSL attached to
	 * the annotation. That is, if the annotation is<br>
	 * <code> {@literal @}concept{* T is Int *}</code><br>
	 * and newDSLText is <code>"cyan"</code> then this method returns<br>
	 * <code> {@literal @}color(cyan)</code><br>
	 * Note that the annotation is NOT changed. If the annotation has not any parameter, a new parameter is inserted

	   @param newDSLText
	   @return
	 */
	public String newAnnotationTextReplaceDSL(String newDSLText) {
		String s = "@" + this.cyanMetaobject.getName() + "(" + newDSLText;
		if ( this.leftParenthesis == null ) {
			// no parameters, introduce this
			s += ")";
		}
		else {
			if ( realParameterList != null ) {
				int numParam = realParameterList.size();
				for ( final WrExprAnyLiteral e : this.realParameterList ) {
					s += e.asString();
					if ( --numParam > 0 ) {
						s += ", ";
					}
				}
			}
		}
		s += ")";
		if ( this.rightCharSeqSymbol != null ) {
			// there is a DSL attached to the annotation
			s += this.leftCharSeqSymbol.getSymbolString() + newDSLText + this.rightCharSeqSymbol.getSymbolString();
		}
		return s;
	}


	public boolean getInsideMethod() {
		return insideMethod;
	}

	public void setInsideMethod(boolean insideMethod) {
		this.insideMethod = insideMethod;
	}

	public Symbol getLastSymbolAnnotation() {
		if ( this.rightCharSeqSymbol != null ) {
			return this.rightCharSeqSymbol;
		}
		else if ( rightParenthesis != null ) {
			return this.rightParenthesis;
		}
		else {
			return this.symbolCyanMetaobjectAnnotation;
		}
	}

	public AttachedDeclarationKind getOriginalDeclaration() {
		return originalDeclaration;
	}

	public void setOriginalDeclaration(AttachedDeclarationKind originalDeclaration) {
		this.originalDeclaration = originalDeclaration;
	}

	/**
	 * The Java class of this metaobject
	 */
	protected CyanMetaobjectAtAnnot	cyanMetaobject;
	/**
	 * the declaration or expression associated to this metaobject annotation or null if there is
	 * no one. A declaration can be a prototype, field, or method.
	 * Therefore 'declaration' can be an object of ProgramUnit, FieldDec,
	 * MethodDec, OR an expression.
	 */

	protected IDeclaration declaration;

	/**
	 * the symbol 'javacode' in @javacode
	 */
	private SymbolCyanMetaobjectAnnotation symbolCyanMetaobjectAnnotation;


	/**
	 * the arguments to this metaobject annotation. The elements of
	 * List may be objects of String, Integer, Float etc.
	 * In<br>
	 * <code>
	 *      {@literal @}feature( "author", "José")<br>
	 * </code>
	 * there would be a list of two literal strings	 *
	 */

	private List<Object> javaParameterList;



	/**
	 * the arguments to this metaobject annotation as AST objects.
	 * In
	 *      @feature<<* "author", "José" *>>
	 * there would be a list of two literal strings, objects
	 * of ExprLiteralString
	 */

	private List<WrExprAnyLiteral> realParameterList;

	/**
	 * if this metaobject annotation takes parameters, this symbol is the '(' '
	 * that comes before the parameters
	 *      @textAttachedDSL(trim_spaces)<**  ... **>
	 *
	 */
	private Symbol leftParenthesis;



	/**
	 * if this metaobject annotation takes parameters, this symbol is the ')'
	 * that comes after the parameters
	 *      @textAttachedDSL(trim_spaces)<**  ... **>
	 *
	 */
	private Symbol rightParenthesis;

	/**
	 * this is the symbol '<**' in
	 *      @textAttachedDSL(trim_spaces)<**  ... **>
	 */

	private SymbolCharSequence leftCharSeqSymbol;


	/**
	 * this is the symbol '<**' in
	 *      @textAttachedDSL(trim_spaces)<**  ... **>
	 */
	private SymbolCharSequence rightCharSeqSymbol;

	/**
	 * the textAttachedDSL of the metaobject annotation. It is the textAttachedDSL between <** and **>
	 * in the annotation below.
	 *
	 *      @javacode<**
	 *           return _add_dot(_a);
	 *      **>
	 */
	private char []textAttachedDSL;

	/**
	 * if the metaobject is a Codeg, variable {@link #codegInfo} is not null and receives the data of
	 * the file associated to this metaobject annotation. This data is produced at editing time and
	 * stored in a file. During compiling time this data is retrieved from the file and stored in
	 * this variable. The compiler discovers the name of this file using the current prototype name, its directory,
	 * the metaobject name, and the first parameter of the metaobject.
	 *
	 */
	private byte []codegInfo;



	/**
	 * the type to which the annotation is attached to
	 */
	private Type typeAttached;

	/**
	 * true if this annotation is inside a method. If it is, the associated metaobject
	 * is allowed to generated code in phase dsa
	 */
	private boolean insideMethod;

	/**
	 * if the value of this variable is AttachedDeclarationKind.PROGRAM_DEC, the annotation
	 * is attached to a Program. If it is AttachedDeclarationKind.PACKAGE_DEC, it is
	 * attached to a package. Field <code>declaration</code> of this class is changed
	 * to refer to each of the program units during the compilation. This field
	 * never changes.
	 */
	private AttachedDeclarationKind originalDeclaration = null;

}

