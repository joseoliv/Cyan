package ast;

import java.util.ArrayList;
import java.util.List;
import cyan.lang.CyInt;
import cyan.lang._Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobjectLiteralObject;
import cyan.reflect._IActionNewPrototypes__semAn;
import error.ErrorKind;
import lexer.CompilerPhase;
import lexer.IWithCompilerPhase;
import lexer.Lexer;
import lexer.Symbol;
import lexer.SymbolLiteralObject;
import meta.CyanMetaobject;
import meta.CyanMetaobjectLiteralObject;
import meta.IActionNewPrototypes_semAn;
import meta.IParseWithCyanCompiler_parsing;
import meta.Timeout;
import meta.Tuple2;
import meta.Tuple4;
import meta.WrAnnotationLiteralObject;
import metaRealClasses.Compiler_semAn;
import saci.Compiler;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;
/**
 * Represents a literal object such as
 *
 *     [* 1:2, 3:1, 1:3 *]
 *  or
 *     n"c:\table\cup\file.txt"
 *  or
 *     r"[A-Z][0-9]+"
 *
   @author José
 */
public class AnnotationLiteralObject extends Annotation {

	public AnnotationLiteralObject(CompilationUnitSuper compilationUnit, Prototype prototype,
			CyanMetaobjectLiteralObject cyanMetaobjectLiteralObject, MethodDec method) {
		super(compilationUnit, true, method);
		this.setCurrentPrototype(prototype);
		this.setAnnotationNumber(prototype.getIncAnnotationNumber());
		this.cyanMetaobjectLiteralObject = cyanMetaobjectLiteralObject;
		this.cyanMetaobjectLiteralObject.setAnnotation(this.getI());
	}


	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		/*
		if ( replaceCodeByThis != null ) {
			pw.print(this.replaceCodeByThis);
		}
		else {}
		*/

		if ( codeAnnotationParseWithCompiler != null )
			pw.print(codeAnnotationParseWithCompiler);
		else
			pw.print(this.originalCode);

	}

	/**
	 * should have been removed before code generation. Therefore this method should never be called.
	 */
	@Override
	public void genJava(PWInterface pw, Env env) {
		pw.println(" ");
	}


	public CyanMetaobjectLiteralObject getCyanMetaobjectLiteralObject() {
		return cyanMetaobjectLiteralObject;
	}

	public Symbol getSymbolLiteralObject() {
		return symbol;
	}

	public void setSymbolLiteralObject(Symbol symbol) {
		this.symbol = symbol;
	}


	@Override
	public Symbol getFirstSymbol() {
		return symbol;
	}


	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		return " ";
	}

	/**
	 * The metaobject annotation has type <code>packageName.prototypeName</code>. This method returns
	 * <code>packageName</code> which may be a list of identifiers separated by dots.
	   @return
	 */
	public String getPackageOfType() {
		return cyanMetaobjectLiteralObject.getPackageOfType();
	}

	/**
	 * The metaobject annotation has type <code>packageName.prototypeName</code>. This method returns
	 * <code>prototypeName</code>.
	   @return
	 */
	public String getPrototypeOfType() {
		return cyanMetaobjectLiteralObject.getPrototypeOfType();
	}

	@Override
	public boolean isParsedWithCompiler() {
		return this.cyanMetaobjectLiteralObject instanceof IParseWithCyanCompiler_parsing;
	}

	@Override
	public void calcInternalTypes(Env env) {

		super.calcInternalTypes(env);
		if ( ! env.getCompInstSet().contains(meta.CompilationInstruction.semAn_actions) )
			/*
			 * literal objects should only exist till phase 6 of the compiler. After that
			 * they should be removed from the code.
			 */
			env.error(true, this.getFirstSymbol(), "Internal error", null, ErrorKind.internal_error);
		else {


			/*
			if ( env.getDuring_semAn_actions() ) {
				env.error(this.getFirstSymbol(), "A SEM_AN action cannot occur inside another SEM_AN actions. For example, you cannot have a macro expansion inside another macro expansion or even a literal object as r\"[a-z]+\" inside a macro");
				//System.out.print("");
			}
			*/


			try {
				env.begin_semAn_actions();
				final Compiler_semAn compiler_semAn = new Compiler_semAn(env, this);
				// // cyanMetaobjectLiteralObject.setAnnotation(this, 0);


				int timeoutMilliseconds = Timeout.getTimeoutMilliseconds( env,
						env.getProject().getProgram().getI(),
						env.getCurrentCompilationUnit().getCyanPackage().getI(),
						this.getFirstSymbol());

				StringBuffer cyanCode = null;

				try {
					Timeout<StringBuffer> to = new Timeout<>();

					_CyanMetaobjectLiteralObject other = (_CyanMetaobjectLiteralObject ) cyanMetaobjectLiteralObject.getMetaobjectInCyan();
					if ( other == null ) {

						cyanCode = to.run(
								() -> { return cyanMetaobjectLiteralObject.semAn_codeToAdd(compiler_semAn); },
								timeoutMilliseconds, "semAn_codeToAdd",
								this.cyanMetaobjectLiteralObject, env);

						// cyanCode = cyanMetaobjectLiteralObject.semAn_codeToAdd(compiler_semAn);
					}
					else {
						cyanCode = to.run(
								() -> { return new StringBuffer(other._semAn__codeToAdd_1(compiler_semAn).s); },
								timeoutMilliseconds, "semAn_codeToAdd",
								this.cyanMetaobjectLiteralObject, env);
						// cyanCode = new StringBuffer(other._semAn__codeToAdd_1(compiler_semAn).s);

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
					env.errorInMetaobjectCatchExceptions(cyanMetaobjectLiteralObject);
				}


				if ( cyanCode != null ) {
					type = Compiler.singleTypeFromString(cyanMetaobjectLiteralObject.getPackageOfType() + "." +
							cyanMetaobjectLiteralObject.getPrototypeOfType(),
							this.getFirstSymbol(), "Error in literal object: ", env.getCurrentCompilationUnit(),
							env.getCurrentPrototype(), env);
					if ( this.isParsedWithCompiler() &&
							env.sizeStackAnnotationParseWithCompiler() > 1 ) {
						/*
						 * this metaobject annotation is a literal object that is inside other literal object
						 */
						this.setCodeAnnotationParseWithCompiler(cyanCode);
					}
					else {
						  /*
						   * literal objects are always removed from the source code
						   */

						env.removeCodeAnnotation(cyanMetaobjectLiteralObject);

						this.codeThatReplacesThisStatement = new StringBuffer(env.addCodeAtAnnotation(cyanMetaobjectLiteralObject, cyanCode, -1));
					}
				}

				_CyanMetaobjectLiteralObject other = (_CyanMetaobjectLiteralObject ) cyanMetaobjectLiteralObject.getMetaobjectInCyan();

				if ( this.cyanMetaobjectLiteralObject instanceof IActionNewPrototypes_semAn
						||
						(other != null && other instanceof _IActionNewPrototypes__semAn)
						) {
					List<Tuple2<String, StringBuffer>> prototypeNameCodeList = null;
					try {

						if ( other == null ) {
							Timeout<List<Tuple2<String, StringBuffer>>> to = new Timeout<>();

							prototypeNameCodeList = to.run(
									() -> {
										return ((IActionNewPrototypes_semAn ) cyanMetaobjectLiteralObject)
												.semAn_NewPrototypeList(compiler_semAn);
									},
									timeoutMilliseconds, "semAn_NewPrototypeList",
									cyanMetaobjectLiteralObject, env);

//							prototypeNameCodeList = ((IActionNewPrototypes_semAn ) cyanMetaobjectLiteralObject)
//									.semAn_NewPrototypeList(compiler_semAn);
						}
						else {
							_IActionNewPrototypes__semAn anp = (_IActionNewPrototypes__semAn ) other;
							Timeout<_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT> to = new Timeout<>();

							_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array =
									to.run(
											() -> {
												return anp._semAn__NewPrototypeList_1(compiler_semAn);
											},
											timeoutMilliseconds, "semAn_NewPrototypeList",
											cyanMetaobjectLiteralObject, env);


//							_Array_LT_GP__Tuple_LT_GP_CyString_GP_CyString_GT_GT array =
//									anp._semAn__NewPrototypeList_1(compiler_semAn);
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
						env.errorInMetaobjectCatchExceptions(this.cyanMetaobjectLiteralObject);
					}
					if ( prototypeNameCodeList != null ) {
						CyanPackage currentPackage = env.getCurrentCompilationUnit().getCyanPackage();
						for ( final Tuple2<String, StringBuffer> prototypeNameCode : prototypeNameCodeList ) {
							String prototypeName = prototypeNameCode.f1;
							final CompilationUnit cunit = (CompilationUnit ) this.compilationUnit;
							final Tuple2<CompilationUnit, String> t = env.getProject().getCompilerManager().createNewPrototype(
									prototypeNameCode.f1, prototypeNameCode.f2,
									cunit.getCompilerOptions(), cunit.getCyanPackage());
							if ( t != null && t.f2 != null ) {
								env.error(symbol, t.f2);
							}
							currentPackage.addPrototypeNameAnnotationInfo(prototypeName, this);
						}
					}
				}
			}
			finally {
				env.end_semAn_actions();
				finalizeCalcInternalTypes(env);
			}

		}
	}

	@Override
	public CompilerPhase getPostfix() {
		if ( symbol instanceof IWithCompilerPhase )
			return ((IWithCompilerPhase ) symbol).getPostfix();
		else
			return null;
	}

	@Override

	public CyanMetaobject getCyanMetaobject() {
		return cyanMetaobjectLiteralObject;
	}


	public String getOriginalCode() {
		return originalCode;
	}


	public void setOriginalCode(String originalCode) {
		this.originalCode = originalCode;
	}

	@Override
	public WrAnnotationLiteralObject getI() {
		if ( iCyanMetaobjectLiteralObjectAnnotation == null ) {
			iCyanMetaobjectLiteralObjectAnnotation = new WrAnnotationLiteralObject(this);
		}
		return iCyanMetaobjectLiteralObjectAnnotation;
	}

	private WrAnnotationLiteralObject iCyanMetaobjectLiteralObjectAnnotation = null;


	@Override
	public List<Tuple4<Integer, Integer, Integer, Integer>> getColorTokenList() {
		if ( colorTokenList == null ) {
			if ( this.cyanMetaobjectLiteralObject != null ) {
				colorTokenList = this.cyanMetaobjectLiteralObject.getColorTokenList(this.getI());
			}
		}
		return colorTokenList;
	}

	public String getUsefulString() {
		return usefulString;
	}


	public void setUsefulString(String usefulString) {
		this.usefulString = usefulString;
	}

	@Override
	public void replaceAnnotationBy( String newAnnotationText ) {
		final int offsetStart = this.symbol.getOffset();
		int offsetEnd;
		SymbolLiteralObject slo = (SymbolLiteralObject ) symbol;
		offsetEnd = offsetStart + slo.getSymbolString().length();
		this.compilationUnit.setOriginalText(
				Lexer.replaceTextByNewText(offsetStart, offsetEnd,
						this.compilationUnit.getOriginalText(),
						newAnnotationText.toCharArray()) );
	}


	/**
	 * if this literal object is a literal string, this keeps the string value
	 */
	private String usefulString;

	private Symbol symbol;

	private final CyanMetaobjectLiteralObject cyanMetaobjectLiteralObject;

	/**
	 * the original text of the metaobject annotation. In "101bin" it is "101bin". In "[* (1, 2), (2, 3), (3, 1) *]"
	 * it should be "(1, 2), (2, 3), (3, 1)" or "[* (1, 2), (2, 3), (3, 1) *]" (not sure which!!!).
	 */
	protected String originalCode;

}
