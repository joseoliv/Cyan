/**
 *
 */

package ast;

import java.util.List;
import cyan.lang.CyString;
import cyan.lang._Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT;
import cyan.reflect._CyanMetaobjectAtAnnot;
import cyan.reflect._IActionFieldAccess__semAn;
import cyan.reflect._IActionFieldMissing__semAn;
import lexer.Symbol;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFieldAccess_semAn;
import meta.IActionFieldMissing_semAn;
import meta.MetaHelper;
import meta.Timeout;
import meta.Tuple3;
import meta.WrExprSelfPeriodIdent;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 * represents the access to a field of self.
 *
 * @author José
 *
 */
public class ExprSelfPeriodIdent extends Expr
		implements LeftHandSideAssignment {

	/**
	 *
	 */
	public ExprSelfPeriodIdent(Symbol selfSymbol, Symbol identSymbol,
			MethodDec method) {
		super(method);
		this.selfSymbol = selfSymbol;
		this.identSymbol = identSymbol;
	}

	@Override
	public WrExprSelfPeriodIdent getI() {
		return new WrExprSelfPeriodIdent(this);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean mayBeStatement() {
		return false;
	}

	public void setSelfSymbol(Symbol selfSymbol) {
		this.selfSymbol = selfSymbol;
	}

	public Symbol getSelfSymbol() {
		return selfSymbol;
	}

	public void setIdentSymbol(Symbol identSymbol) {
		this.identSymbol = identSymbol;
	}

	public Symbol getIdentSymbol() {
		return identSymbol;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {

		if ( cyanEnv.getCreatingInstanceGenericPrototype() ) {
			pw.print("self." + cyanEnv.formalGenericParamToRealParam(
					identSymbol.getSymbolString()));
		}
		else {
			String name = identSymbol.getSymbolString();
			String iv = "." + name; // NameServer.getJavaNameIdentifier(name);
			if ( cyanEnv.getCreatingInnerPrototypesInsideEval() ) {
				/*
				 * in this case, 'self' is being used inside a prototype created
				 * from a function or a outer prototype method.
				 */
				pw.print(NameServer.selfNameInnerPrototypes + iv);
			}
			else if ( cyanEnv.getCreatingContextObject() ) {
				/*
				 * in this case, 'self' is being used inside a prototype created
				 * from a context function
				 */
				// pw.print(NameServer.selfNameContextObject + " ");
				pw.print(NameServer.selfNameContextObject + iv);
			}
			else
				pw.print("self." + name);
		}
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String tmpVar;
		tmpVar = MetaHelper.getJavaName(identSymbol.getSymbolString())
				+ (fieldDec.getRefType() ? ".elem" : "");
		return tmpVar;
	}

	@Override
	public void genJavaCodeVariable(PWInterface pw, Env env) {
		if ( fieldDec.isShared() ) {
			pw.print(MetaHelper.getJavaName(
					fieldDec.getDeclaringObject().getName()) + ".");
		}
		else {
			pw.print("this.");
		}

		pw.print(MetaHelper.getJavaName(identSymbol.getSymbolString()));
		if ( fieldDec.getRefType() ) pw.print(".elem");
	}

	@Override
	public Symbol getFirstSymbol() {
		return selfSymbol;
	}

	public FieldDec getFieldDec() {
		return fieldDec;
	}

	@Override
	public void calcInternalTypes(Env env, boolean leftHandSideAssignment) {
		fieldDec = env.getCurrentObjectDec()
				.searchField(identSymbol.getSymbolString());
		if ( fieldDec == null ) {
			type = Type.Dyn;
			if ( !leftHandSideAssignment ) {
				if ( replaceGetFieldMissing_semAn(env) ) {
					// 'self.iv' was replaced, no need for issuing an error
					return;
				}
			}

			env.error(this.selfSymbol, "field '" + identSymbol.getSymbolString()
					+ "' was not found", true, true);
		}

		type = fieldDec.getType();

		if ( !leftHandSideAssignment ) {
			ExprSelfPeriodIdent.actionFieldAccess_semAn(env, this, fieldDec);
		}

		// TODO replace currentMethod by this.currentMethod and remove local
		// variable declaration
		MethodDec currentMethod2 = env.getCurrentMethod();
		if ( currentMethod2 != null ) {
			currentMethod2.addToAccessedFieldSet(fieldDec);
			if ( currentMethod2.getShared() ) {
				if ( !fieldDec.isShared() ) {
					env.error(this.selfSymbol,
							"Shared methods cannot access non-shared fields. Method '"
									+ currentMethod2.getName()
									+ " is accessing field '"
									+ fieldDec.getName() + "'");
				}
			}
		}

		if ( !fieldDec.isShared()
				&& !env.getCurrentMethod().getAllowAccessToFields() ) {
			/*
			 * access to fields is not allowed
			 */
			env.error(this.getFirstSymbol(),
					"fields are not allowed in this method. Probable cause: "
							+ "metaobject 'prototypeCallOnly' is attached to it");
		}
		if ( !leftHandSideAssignment && !fieldDec.isShared() ) {
			String currentMethodName = env.getCurrentMethod()
					.getNameWithoutParamNumber();
			if ( currentMethodName.equals("init")
					|| currentMethodName.equals("init:") ) {

				if ( !this.fieldDec.getWasInitialized() ) {
					env.error(this.getFirstSymbol(),
							"Access to a non-initialized field");
				}
				/*
				 * env.error(this.getFirstSymbol(),
				 * "Access to a field in an expression inside an 'init' or 'init:' method. This is illegal because the "
				 * +
				 * "Cyan compiler is not able yet to discover if the field have been initialized or not"
				 * );
				 */
			}
		}
	}

	@Override
	public void calcInternalTypes(Env env) {

		this.calcInternalTypes(env, false);
		super.calcInternalTypes(env);
	}

	/**
	 * return true if a metaobject replaced the assignment, false otherwise
	 */

	private boolean replaceGetFieldMissing_semAn(Env env) {

		// metaobjectAnnotationParseWithCompilerStack

		// List<AnnotationAt> annotList =
		// env.getCurrentPrototype().getAttachedAnnotationList();
		List<Annotation> anyAnnotList = env.getCurrentPrototype()
				.getPrototypePackageProgramAnnotationList();

		if ( env.getProject().getCompilerManager()
				.getCompilationStep() != CompilationStep.step_6
				|| anyAnnotList == null
				|| env.sizeStackAnnotationParseWithCompiler() > 0 ) {
			return false;
		}

		for (Annotation annot : anyAnnotList) {
			if ( !(annot instanceof AnnotationAt) ) {
				continue;
			}

			CyanMetaobjectAtAnnot cyanMetaobject = (CyanMetaobjectAtAnnot) annot
					.getCyanMetaobject();
			_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot) cyanMetaobject
					.getMetaobjectInCyan();

			if ( cyanMetaobject instanceof IActionFieldMissing_semAn
					|| (other != null
							&& other instanceof _IActionFieldMissing__semAn) ) {

				StringBuffer sb = null;
				Tuple3<String, String, StringBuffer> t = null;
				try {
					int timeoutMilliseconds = Timeout
							.getTimeoutMilliseconds(env,
									env.getProject().getProgram().getI(),
									env.getCurrentCompilationUnit()
											.getCyanPackage().getI(),
									this.getFirstSymbol());

					if ( other == null ) {
						IActionFieldMissing_semAn access = (IActionFieldMissing_semAn) cyanMetaobject;
						Timeout<Tuple3<String, String, StringBuffer>> to = new Timeout<>();
						t = to.run(() -> {
							return access.semAn_replaceGetMissingField(
									this.getI(), env.getI());

						}, timeoutMilliseconds, "semAn_replaceGetMissingField",
								cyanMetaobject, env);

						// t = access.semAn_replaceGetMissingField(this.getI(),
						// env.getI());
					}
					else {
						Timeout<_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT> to = new Timeout<>();
						_Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT cyTuple = to
								.run(() -> {
									return ((_IActionFieldMissing__semAn) other)
											._semAn__replaceGetMissingField_2(
													this.getI(), env.getI());

								}, timeoutMilliseconds,
										"semAn_replaceGetMissingField",
										cyanMetaobject, env);

						// _Tuple_LT_GP_CyString_GP_CyString_GP_CyString_GT
						// cyTuple = ((_IActionFieldMissing__semAn) other )
						// ._semAn__replaceGetMissingField_2(this.getI(),
						// env.getI());

						CyString f1 = cyTuple._f1();
						CyString f2 = cyTuple._f2();
						CyString f3 = cyTuple._f3();
						String f1s = f1.s;
						if ( f1s.length() != 0 ) {
							t = new Tuple3<String, String, StringBuffer>(f1s,
									f2.s, new StringBuffer(f3.s));
						}
					}
				}
				catch (error.CompileErrorException e) {
				}
				catch (NoClassDefFoundError e) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (RuntimeException e) {
					e.printStackTrace();
					env.thrownException(annot, this.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobject(cyanMetaobject,
							this.getFirstSymbol());
				}

				if ( t != null && t.f3.length() != 0 ) {
					Symbol symForError = this.getFirstSymbol();

					if ( this.getCodeThatReplacesThisExpr() != null ) {
						/*
						 * this field access has already been replaced by
						 * another expression
						 */
						if ( this
								.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
							env.warning(symForError, "Metaobject annotation '"
									+ cyanMetaobject.getName() + "' at line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ annot.getPackageOfAnnotation() + "."
									+ annot.getPackageOfAnnotation()
									+ " is trying to replace the field access '"
									+ this.asString()
									+ "' by an expression. But this has already been asked by metaobject annotation '"
									+ this.getCyanAnnotationThatReplacedMSbyExpr()
											.getCyanMetaobject().getName()
									+ "'" + " at line "
									+ this.getCyanAnnotationThatReplacedMSbyExpr()
											.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ this.getCyanAnnotationThatReplacedMSbyExpr()
											.getPackageOfAnnotation()
									+ "."
									+ this.getCyanAnnotationThatReplacedMSbyExpr()
											.getPackageOfAnnotation());
						}
						else {
							env.warning(symForError, "Metaobject annotation '"
									+ cyanMetaobject.getName() + "' at line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ annot.getPackageOfAnnotation() + "."
									+ annot.getPackageOfAnnotation()
									+ " is trying to replace the field access '"
									+ this.asString()
									+ "' by an expression. But this has already been asked by someone else");
						}
					}

					// if there is any errors, signals them
					env.errorInMetaobject(cyanMetaobject, symForError);

					type = env.searchPackagePrototype(t.f1, t.f2);
					if ( type == null ) {
						env.error(this.selfSymbol, "field '"
								+ identSymbol.getSymbolString() + "' has type '"
								+ t.f1 + "." + t.f2
								+ "' according to metaobject '"
								+ cyanMetaobject.getName()
								+ "' attached to this prototype. But this type does not exist",
								true, true);
					}

					sb = t.f3;
					env.removeAddCodeFromToOffset(this,
							this.selfSymbol.getOffset()
									+ this.selfSymbol.getSymbolString().length()
									+ 1
									+ this.identSymbol.getSymbolString()
											.length(),
							(AnnotationAt) annot, sb, type);

					this.setCyanAnnotationThatReplacedMSbyExpr(annot);
					return true;
				}
			}

		}
		return false;
	}

	void setFieldType(Type type) {
		this.type = type;
	}

	static public void actionFieldAccess_semAn(Env env, Expr fieldExpr,
			FieldDec fieldDec) {

		// metaobjectAnnotationParseWithCompilerStack

		List<AnnotationAt> annotList = fieldDec.getAttachedAnnotationList();
		if ( env.getProject().getCompilerManager()
				.getCompilationStep() != CompilationStep.step_6
				|| annotList == null
				|| env.sizeStackAnnotationParseWithCompiler() > 0 ) {
			return;
		}
		int timeoutMilliseconds = Timeout.getTimeoutMilliseconds(env,
				env.getProject().getProgram().getI(),
				env.getCurrentCompilationUnit().getCyanPackage().getI(),
				fieldExpr.getFirstSymbol());

		for (AnnotationAt annot : annotList) {

			CyanMetaobjectAtAnnot cyanMetaobject = annot.getCyanMetaobject();
			_CyanMetaobjectAtAnnot other = (_CyanMetaobjectAtAnnot) cyanMetaobject
					.getMetaobjectInCyan();

			if ( cyanMetaobject instanceof IActionFieldAccess_semAn
					|| (other != null
							&& other instanceof _IActionFieldAccess__semAn) ) {

				StringBuffer sb = null;
				try {
					Timeout<StringBuffer> to = new Timeout<>();
					if ( other == null ) {
						IActionFieldAccess_semAn access = (IActionFieldAccess_semAn) cyanMetaobject;

						sb = to.run(() -> {
							return access.semAn_replaceGetField(
									fieldExpr.getI(), env.getI());
						}, timeoutMilliseconds, "semAn_replaceGetField",
								cyanMetaobject, env);
						// sb = access.semAn_replaceGetField(fieldExpr.getI(),
						// env.getI());
					}
					else {
						sb = to.run(() -> {
							return new StringBuffer(
									((_IActionFieldAccess__semAn) other)
											._semAn__replaceGetField_2(
													fieldExpr.getI(),
													env.getI()).s);
						}, timeoutMilliseconds, "semAn_replaceGetField",
								cyanMetaobject, env);
						// sb = new StringBuffer(
						// ((_IActionFieldAccess__semAn )
						// other)._semAn__replaceGetField_2(fieldExpr.getI(),
						// env.getI()).s);
					}
				}
				catch (error.CompileErrorException e) {
				}
				catch (NoClassDefFoundError e) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (RuntimeException e) {
					env.thrownException(annot, fieldExpr.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobject(cyanMetaobject,
							fieldExpr.getFirstSymbol());
				}

				if ( sb != null && sb.length() != 0 ) {
					Symbol symForError = fieldExpr.getFirstSymbol();

					if ( fieldExpr.getCodeThatReplacesThisExpr() != null ) {
						/*
						 * this field access has already been replaced by
						 * another expression
						 */
						if ( fieldExpr
								.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
							env.warning(symForError, "Metaobject annotation '"
									+ cyanMetaobject.getName() + "' at line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ annot.getPackageOfAnnotation() + "."
									+ annot.getPackageOfAnnotation()
									+ " is trying to replace the field access '"
									+ fieldExpr.asString()
									+ "' by an expression. But this has already been asked by metaobject annotation '"
									+ fieldExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getCyanMetaobject().getName()
									+ "'" + " at line "
									+ fieldExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ fieldExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getPackageOfAnnotation()
									+ "."
									+ fieldExpr
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getPackageOfAnnotation());
						}
						else {
							env.warning(symForError, "Metaobject annotation '"
									+ cyanMetaobject.getName() + "' at line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ annot.getPackageOfAnnotation() + "."
									+ annot.getPackageOfAnnotation()
									+ " is trying to replace the field access '"
									+ fieldExpr.asString()
									+ "' by an expression. But this has already been asked by someone else");
						}
					}

					// if there is any errors, signals them
					env.errorInMetaobject(cyanMetaobject, symForError);

					Type typeOfCode = fieldExpr.type;

					// env.removeAddCodeStatement(this, annot, sb, typeOfCode);
					/*
					 * removeAddCodeFromToOffset(Statement statement, int
					 * offsetNext, AnnotationAt annotation, StringBuffer
					 * codeToAdd)
					 */
					int offsetNext = fieldExpr.getFirstSymbol().getOffset();
					if ( fieldExpr instanceof ExprSelfPeriodIdent ) {
						offsetNext += fieldExpr.asString().length();
					}
					else {
						offsetNext += fieldExpr.getFirstSymbol()
								.getSymbolString().length();
					}
					env.removeAddCodeFromToOffset(fieldExpr, offsetNext, annot,
							sb, typeOfCode);

					fieldExpr.setCyanAnnotationThatReplacedMSbyExpr(annot);

				}
			}

		}
	}

	private FieldDec	fieldDec;
	private Symbol		selfSymbol;
	private Symbol		identSymbol;
}
