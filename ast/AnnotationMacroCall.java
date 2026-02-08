/**

 */

package ast;

import java.util.ArrayList;
import java.util.List;
import cyan.lang.CyInt;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobjectMacro;
import cyan.reflect._IActionNewPrototypes__semAn;
import error.ErrorKind;
import lexer.CompilerPhase;
import lexer.Symbol;
import meta.CyanMetaobject;
import meta.CyanMetaobjectMacro;
import meta.IActionNewPrototypes_semAn;
import meta.Timeout;
import meta.Tuple2;
import meta.Tuple4;
import meta.WrAnnotationMacroCall;
import metaRealClasses.Compiler_semAn;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
import saci.Saci;

/**
 * @author José
 *
 */
public class AnnotationMacroCall extends Annotation {

	public AnnotationMacroCall(CyanMetaobjectMacro cyanMacro,
			CompilationUnit compilationUnit, Prototype prototype,
			Symbol firstSymbol, boolean inExpr, MethodDec method) {
		super(compilationUnit, inExpr, method);
		this.setCurrentPrototype(prototype);
		this.setAnnotationNumber(prototype.getIncAnnotationNumber());
		this.cyanMacro = cyanMacro;
		this.cyanMacro.setAnnotation(this.getI());
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
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {

		/*
		 * if ( this.replaceCodeByThis != null ) {
		 * pw.print(this.replaceCodeByThis); } else {}
		 */

		if ( codeAnnotationParseWithCompiler != null )
			pw.print(codeAnnotationParseWithCompiler);
		else
			pw.print(this.originalText);

	}

	/**
	 * should have been removed before code generation. Therefore this method
	 * should never be called.
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
		 * if ( env.getDuring_semAn_actions() ) {
		 * env.error(this.getFirstSymbol(),
		 * "A SEM_AN action cannot occur inside another SEM_AN actions. For example, you cannot have a macro expansion inside another macro expansion or even a literal object as r\"[a-z]+\" inside a macro"
		 * ); }
		 */
		try {
			env.begin_semAn_actions();
			super.calcInternalTypes(env);
			if ( env.getCompInstSet()
					.contains(meta.CompilationInstruction.semAn_actions) ) {
				final Compiler_semAn compiler_semAn = new Compiler_semAn(env,
						this);
				// // cyanMacro.setAnnotation(this, 0);
				StringBuffer cyanCode = null;

				_CyanMetaobjectMacro other = (_CyanMetaobjectMacro) cyanMacro
						.getMetaobjectInCyan();
				int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(env,
						env.getProject().getProgram().getI(),
						env.getCurrentCompilationUnit().getCyanPackage().getI(),
						this.getFirstSymbol());

				try {
					Timeout<StringBuffer> to = new Timeout<>();
					if ( other == null ) {

						if ( Saci.timeLimitForMetaobjects ) {

							cyanCode = to.run(() -> {
								return cyanMacro
										.semAn_codeToAdd(compiler_semAn);
							}, timeoutMilliseconds, "semAn_codeToAdd",
									this.cyanMacro, env);

						}
						else {

							cyanCode = cyanMacro
									.semAn_codeToAdd(compiler_semAn);
						}

						// cyanCode = cyanMacro.semAn_codeToAdd(compiler_semAn);
					}
					else {
						cyanCode = to.run(() -> {
							return new StringBuffer(other
									._semAn__codeToAdd_1(compiler_semAn).s);
						}, timeoutMilliseconds, "semAn_codeToAdd",
								this.cyanMacro, env);

						// cyanCode = new
						// StringBuffer(other._semAn__codeToAdd_1(compiler_semAn).s);
					}
				}
				catch (final error.CompileErrorException e) {
				}
				catch (final NoClassDefFoundError e) {
					env.error(this.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (final RuntimeException e) {
					env.thrownException(this, this.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobjectCatchExceptions(cyanMacro);
				}

				if ( cyanCode != null ) {

					if ( env.sizeStackAnnotationParseWithCompiler() > 1 ) {
						/*
						 * this metaobject annotation is a literal object that
						 * is inside other literal object
						 */
						this.setCodeAnnotationParseWithCompiler(cyanCode);
					}
					else {

						/*
						 * macros are always removed from the source code
						 */
						env.removeCodeAnnotation(cyanMacro);
						final Symbol lastSymbol = this.lastSymbolMacroCall;

						this.codeThatReplacesThisStatement = new StringBuffer(
								env.addCodeAtAnnotation(cyanMacro, cyanCode,
										lastSymbol.getOffset() + lastSymbol
												.getSymbolString().length()));

					}
				}
				final Prototype pu = env.searchPackagePrototype(
						cyanMacro.getPackageOfType(),
						cyanMacro.getPrototypeOfType());
				if ( pu == null ) {
					type = env.searchPackageJavaClass(
							cyanMacro.getPackageOfType(),
							cyanMacro.getPrototypeOfType());
					if ( type == null ) {
						env.error(true, this.getFirstSymbol(),
								"Macro has type '"
										+ cyanMacro.getPackageOfType() + "."
										+ cyanMacro.getPrototypeOfType()
										+ "' which was not found",
								cyanMacro.getPrototypeOfType(),
								ErrorKind.prototype_was_not_found_inside_method);
					}
				}
				else
					type = pu;

				if ( cyanMacro instanceof IActionNewPrototypes_semAn
						|| (other != null
								&& other instanceof _IActionNewPrototypes__semAn) ) {
					List<Tuple2<String, StringBuffer>> prototypeNameCodeList = null;
					try {

						if ( other == null ) {
							Timeout<List<Tuple2<String, StringBuffer>>> to = new Timeout<>();

							if ( Saci.timeLimitForMetaobjects ) {
								prototypeNameCodeList = to.run(() -> {
									return ((IActionNewPrototypes_semAn) cyanMacro)
											.semAn_NewPrototypeList(
													compiler_semAn);
								}, timeoutMilliseconds,
										"semAn_NewPrototypeList",
										this.cyanMacro, env);

							}
							else {
								prototypeNameCodeList = ((IActionNewPrototypes_semAn) cyanMacro)
										.semAn_NewPrototypeList(compiler_semAn);
							}

							// prototypeNameCodeList =
							// ((IActionNewPrototypes_semAn )
							// cyanMacro).semAn_NewPrototypeList(compiler_semAn);
						}
						else {
							_IActionNewPrototypes__semAn anp = (_IActionNewPrototypes__semAn) other;
							Timeout<_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT> to = new Timeout<>();
							_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array = to
									.run(() -> {
										return anp._semAn__NewPrototypeList_1(
												compiler_semAn);
									}, timeoutMilliseconds,
											"semAn_NewPrototypeList",
											this.cyanMacro, env);

							// _Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT
							// array =
							// anp._semAn__NewPrototypeList_1(compiler_semAn);
							int size = array._size().n;
							if ( size > 0 ) {
								prototypeNameCodeList = new ArrayList<>();
								for (int i = 0; i < size; ++i) {
									_Tuple_LT_GP_CyString_GP_CyString_GT tss = array
											._at_1(new CyInt(i));
									String f1 = tss._f1().s;
									String f2 = tss._f2().s;
									if ( f1.length() > 0 ) {
										prototypeNameCodeList.add(
												new Tuple2<String, StringBuffer>(
														f1,
														new StringBuffer(f2)));
									}
								}
							}
						}

					}
					catch (final error.CompileErrorException e) {
					}
					catch (final NoClassDefFoundError e) {
						env.error(this.getFirstSymbol(), e.getMessage() + " "
								+ NameServer.messageClassNotFoundException);
					}
					catch (final RuntimeException e) {
						env.thrownException(this, this.getFirstSymbol(), e);
					}
					finally {
						env.errorInMetaobjectCatchExceptions(this.cyanMacro);
					}
					if ( prototypeNameCodeList != null ) {
						CyanPackage currentPackage = env
								.getCurrentCompilationUnit().getCyanPackage();
						for (final Tuple2<String, StringBuffer> prototypeNameCode : prototypeNameCodeList) {
							String prototypeName = prototypeNameCode.f1;
							final CompilationUnit cunit = (CompilationUnit) this.compilationUnit;
							final Tuple2<CompilationUnit, String> t = env
									.getProject().getCompilerManager()
									.createNewPrototype(prototypeNameCode.f1,
											prototypeNameCode.f2,
											cunit.getCompilerOptions(),
											cunit.getCyanPackage());
							if ( t != null && t.f2 != null ) {
								env.error(firstSymbol, t.f2);
							}
							currentPackage.addPrototypeNameAnnotationInfo(
									prototypeName, this);
						}
					}
				}

			}
			finalizeCalcInternalTypes(env);

		}
		finally {
			env.end_semAn_actions();
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

	private final Symbol				firstSymbol;

	private final CyanMetaobjectMacro	cyanMacro;

	/**
	 * last symbol of the macro call
	 */
	private Symbol						lastSymbolMacroCall;

}
