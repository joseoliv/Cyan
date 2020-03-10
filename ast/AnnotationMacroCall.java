/**

 */
package ast;


import java.util.ArrayList;
import java.util.List;
import cyan.lang.CyInt;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT__GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobjectMacro;
import cyan.reflect._IActionNewPrototypes__dsa;
import error.ErrorKind;
import lexer.CompilerPhase;
import lexer.Symbol;
import meta.CyanMetaobject;
import meta.CyanMetaobjectMacro;
import meta.IActionNewPrototypes_dsa;
import meta.Tuple2;
import meta.Tuple4;
import meta.WrAnnotationMacroCall;
import metaRealClasses.Compiler_dsa;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
   @author José

 */
public class AnnotationMacroCall extends Annotation {


	public AnnotationMacroCall(CyanMetaobjectMacro cyanMacro, CompilationUnit compilationUnit,
			 ProgramUnit programUnit, Symbol firstSymbol, boolean inExpr) {
		super(compilationUnit, inExpr);
		this.setProgramUnit(programUnit);
		this.setMetaobjectAnnotationNumber(programUnit.getIncMetaobjectAnnotationNumber());
		this.cyanMacro = cyanMacro;
		this.cyanMacro.setMetaobjectAnnotation(this.getI());
		this.firstSymbol = firstSymbol;
		lastSymbolMacroCall = null;
	}

	@Override
	public WrAnnotationMacroCall getI() {
		if ( iCyanMetaobjectMacroCall == null ) {
			iCyanMetaobjectMacroCall = new WrAnnotationMacroCall(this);
		}
		return iCyanMetaobjectMacroCall;
	}

	private WrAnnotationMacroCall iCyanMetaobjectMacroCall = null;

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		/*
		if ( this.replaceCodeByThis != null ) {
			pw.print(this.replaceCodeByThis);
		}
		else {}
		*/

		if ( codeMetaobjectAnnotationParseWithCompiler != null )
			pw.print(codeMetaobjectAnnotationParseWithCompiler);
		else
			pw.print(this.originalText);


	}

	/**
	 * should have been removed before code generation. Therefore this method should never be called.
	 */
	@Override
	public void genJava(PWInterface pw, Env env) {
	}

	@Override
	public Symbol getFirstSymbol() {
		return firstSymbol;
	}


	@Override
	public boolean isParsedWithCompiler() {
		return true;
	}


	@Override
	public void calcInternalTypes(Env env) {
		/*
		if ( env.getDuring_dsa_actions() ) {
			env.error(this.getFirstSymbol(), "A dsa action cannot occur inside another dsa actions. For example, you cannot have a macro expansion inside another macro expansion or even a literal object as r\"[a-z]+\" inside a macro");
		}
		*/
		try {
			env.begin_dsa_actions();
			super.calcInternalTypes(env);
			if ( env.getCompInstSet().contains(meta.CompilationInstruction.dsa_actions) ) {
				final Compiler_dsa compiler_dsa = new Compiler_dsa(env, this);
				// // cyanMacro.setMetaobjectAnnotation(this, 0);
				StringBuffer cyanCode = null;

				_CyanMetaobjectMacro other = (_CyanMetaobjectMacro ) cyanMacro.getMetaobjectInCyan();

				try {
					if ( other == null ) {
						cyanCode = cyanMacro.dsa_codeToAdd(compiler_dsa);
					}
					else {
						cyanCode = new StringBuffer(other._dsa__codeToAdd_1(compiler_dsa).s);
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
					env.errorInMetaobjectCatchExceptions(cyanMacro);
				}


				if ( cyanCode != null ) {

					if ( env.sizeStackMetaobjectAnnotationParseWithCompiler() > 1 ) {
						/*
						 * this metaobject annotation is a literal object that is inside other literal object
						 */
						this.setCodeMetaobjectAnnotationParseWithCompiler(cyanCode);
					}
					else {

						  /*
						   * macros are always removed from the source code
						   */
						env.removeCodeMetaobjectAnnotation(cyanMacro);
						final Symbol lastSymbol = this.lastSymbolMacroCall;

						this.codeThatReplacesThisStatement = new StringBuffer(
								env.addCodeAtMetaobjectAnnotation(cyanMacro, cyanCode, lastSymbol.getOffset() + lastSymbol.getSymbolString().length()) );


					}
				}
				final ProgramUnit pu = env.searchPackagePrototype(cyanMacro.getPackageOfType(), cyanMacro.getPrototypeOfType());
				if ( pu == null ) {
					type = env.searchPackageJavaClass(cyanMacro.getPackageOfType(), cyanMacro.getPrototypeOfType());
					if ( type == null ) {
						env.error(true,
								this.getFirstSymbol(),
										"Macro has type '" + cyanMacro.getPackageOfType() + "." +
												cyanMacro.getPrototypeOfType() + "' which was not found", cyanMacro.getPrototypeOfType(), ErrorKind.prototype_was_not_found_inside_method);
					}
				}
				else
					type = pu;


				if ( cyanMacro instanceof IActionNewPrototypes_dsa ||
						(other != null && other instanceof _IActionNewPrototypes__dsa)
						) {
					List<Tuple2<String, StringBuffer>> prototypeNameCodeList = null;
					try {

						if ( other == null ) {
							prototypeNameCodeList = ((IActionNewPrototypes_dsa ) cyanMacro).dsa_NewPrototypeList(compiler_dsa);
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
						env.errorInMetaobjectCatchExceptions(this.cyanMacro);
					}
					if ( prototypeNameCodeList != null ) {
						for ( final Tuple2<String, StringBuffer> prototypeNameCode : prototypeNameCodeList ) {
							final CompilationUnit cunit = (CompilationUnit ) this.compilationUnit;
							final Tuple2<CompilationUnit, String> t = env.getProject().getCompilerManager().createNewPrototype(prototypeNameCode.f1, prototypeNameCode.f2,
									cunit.getCompilerOptions(), cunit.getCyanPackage());
							if ( t != null && t.f2 != null ) {
								env.error(firstSymbol, t.f2);
							}
						}
					}
				}

			}
			finalizeCalcInternalTypes(env);


		}
		finally {
			env.end_dsa_actions();
		}
	}




	@Override
	public CompilerPhase getPostfix() {
		return null;
	}

	@Override
	public CyanMetaobject getCyanMetaobject() {
		return cyanMacro;
	}


	public void setLastSymbolMacroCall(Symbol lastSymbolMacroCall) {
		this.lastSymbolMacroCall = lastSymbolMacroCall;
	}

	public Symbol getLastSymbolMacroCall() {
		return lastSymbolMacroCall;
	}

	@Override
	public List<Tuple4<Integer, Integer, Integer, Integer>> getColorTokenList() {
		if ( colorTokenList == null ) {
			if ( this.cyanMacro != null ) {
				colorTokenList = this.cyanMacro.getColorTokenList(this.getI());
			}
		}
		return colorTokenList;
	}

	private final Symbol firstSymbol;

	private final CyanMetaobjectMacro cyanMacro;


	/**
	 * last symbol of the macro call
	 */
	private Symbol lastSymbolMacroCall;

}
