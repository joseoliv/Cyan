/**

 */

package ast;

import java.lang.reflect.Field;
import java.util.List;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._IActionFieldAccess__semAn;
import lexer.Symbol;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFieldAccess_semAn;
import meta.IdentStarKind;
import meta.Timeout;
import meta.Token;
import meta.WrStatementPlusPlusIdent;
import saci.CyanEnv;
import saci.Env;
import saci.Function2R;
import saci.NameServer;
import saci.Saci;

/**
 * Represents an statement like<br>
 * {@code ++i;}<br>
 * 
 * @author jose
 * 
 */
public class StatementPlusPlusIdent extends Statement {

	public StatementPlusPlusIdent(Symbol plusPlus, ExprIdentStar varId,
			MethodDec method) {
		super(method);
		this.plusPlus = plusPlus;
		this.varId = varId;
	}

	@Override
	public WrStatementPlusPlusIdent getI() {
		if ( iStatementPlusPlusIdent == null ) {
			iStatementPlusPlusIdent = new WrStatementPlusPlusIdent(this);
		}
		return iStatementPlusPlusIdent;
	}

	private WrStatementPlusPlusIdent iStatementPlusPlusIdent = null;

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		if ( varId != null ) {
			varId.accept(visitor);
		}
	}

	@Override
	public Object eval(EvalEnv ee) {
		String id = varId.getName();
		VariableDecInterface v = ee.searchLocalVar(id);
		if ( v == null ) {
			ee.error(this.getFirstSymbol(),
					"Variable '" + id + "' was not found");
		}
		else {
			Object value = v.getValueInInterpreter();

			Class<?> valueClass = value.getClass();
			Object exprValue;
			if ( EvalEnv.any.isAssignableFrom(valueClass) ) {
				Field f;
				try {
					String fieldName;
					if ( valueClass.getName().equals("Character") ) {
						fieldName = "c";
					}
					else {
						fieldName = "n";
					}
					f = valueClass.getField(fieldName);
					exprValue = f.get(value);
				}
				catch (NoSuchFieldException | SecurityException
						| IllegalArgumentException | IllegalAccessException e) {
					ee.error(getFirstSymbol(),
							"Internal error in ++ statement");
					return null;
				}

				String className = exprValue.getClass().getName();
				int lastDot = className.lastIndexOf('.');
				if ( lastDot > 0 ) {
					className = className.substring(lastDot + 1);
				}
				switch (className) {
				case "Byte":
					v.setValueInInterpreter(
							ee.newCyByte((byte) ((Byte) exprValue + 1)));
					break;
				case "Character":
					v.setValueInInterpreter(
							ee.newCyChar((char) ((Character) exprValue + 1)));
					break;
				case "Integer":
					v.setValueInInterpreter(
							ee.newCyInt(((Integer) exprValue + 1)));
					break;
				case "Long":
					v.setValueInInterpreter(
							ee.newCyLong(((Long) exprValue + 1)));
					break;
				case "Short":
					v.setValueInInterpreter(
							ee.newCyShort((short) ((Short) exprValue + 1)));
					break;
				default:
					ee.error(this.getFirstSymbol(),
							"'++' can only be used with 'Byte', 'Char', 'Int', 'Long', and 'Short' types");
				}

			}
			else {
				String className = value.getClass().getName();
				int lastDot = className.lastIndexOf('.');
				if ( lastDot > 0 ) {
					className = className.substring(lastDot + 1);
				}
				switch (className) {
				case "Byte":
					v.setValueInInterpreter(((Byte) value) + 1);
					break;
				case "Character":
					v.setValueInInterpreter(((Character) value) + 1);
					break;
				case "Integer":
					v.setValueInInterpreter(((Integer) value) + 1);
					break;
				case "Long":
					v.setValueInInterpreter(((Long) value) + 1);
					break;
				case "Short":
					v.setValueInInterpreter(((Short) value) + 1);
					break;
				default:
					ee.error(this.getFirstSymbol(),
							"'++' can only be used with 'Byte', 'Char', 'Int', 'Long', and 'Short' types");
				}

			}

		}
		return null;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine,
			CyanEnv cyanEnv, boolean genFunctions) {
		pw.print("++");
		varId.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}

	@Override
	public void calcInternalTypes(Env env) {
		varId.calcInternalTypes(env, true);
		VariableDecInterface aVar = varId.getVarDeclaration();
		if ( aVar == null || aVar.isReadonly() ) {
			env.error(varId.getFirstSymbol(),
					"Operator ++ can only be applied to variables that are not read only");
		}
		Type outType = varId.getType(env);
		Type t = outType.getInsideType();
		if ( t != outType ) {
			env.error(varId.getFirstSymbol(), "Variable '" + varId.getName()
					+ "' has a type with attached metaobjects. Operator ++ cannot be used with it.");
		}
		if ( t != Type.Byte && t != Type.Char && t != Type.Int && t != Type.Long
				&& t != Type.Short && t != Type.Dyn ) {
			env.error(varId.getFirstSymbol(),
					"Operator ++ can only be applied to variables of types Byte, Char, Int, Long, Short, and Dyn");
		}

		if ( varId.getIdentStarKind() == IdentStarKind.instance_variable_t ) {
			StatementPlusPlusIdent.replaceOpOpFieldAccessIfAsked(env, this,
					(IActionFieldAccess_semAn access, Statement stat) -> {
						_CyanMetaobject other = ((CyanMetaobject) access)
								.getMetaobjectInCyan();

						int timeoutMilliseconds = Timeout
								.getTimeoutMilliseconds(env,
										env.getProject().getProgram().getI(),
										env.getCurrentCompilationUnit()
												.getCyanPackage().getI(),
										this.getFirstSymbol());

						Timeout<StringBuffer> to = new Timeout<>();
						if ( other == null ) {

							if ( Saci.timeLimitForMetaobjects ) {
								return to.run(() -> {
									return access.semAn_replacePlusPlusField(
											stat.getI(), env.getI());
								}, timeoutMilliseconds,
										"semAn_replacePlusPlusField",
										(CyanMetaobject) access, env);

							}
							else {
								return access.semAn_replacePlusPlusField(
										stat.getI(), env.getI());

							}

							// return
							// access.semAn_replacePlusPlusField(stat.getI(),
							// env.getI());
						}
						else if ( other instanceof _IActionFieldAccess__semAn ) {
							return to.run(() -> {
								return new StringBuffer(
										((_IActionFieldAccess__semAn) other)
												._semAn__replacePlusPlusField_2(
														stat.getI(),
														env.getI()).s);
							}, timeoutMilliseconds,
									"semAn_replacePlusPlusField",
									(CyanMetaobject) access, env);

							// return new StringBuffer(
							// ((_IActionFieldAccess__semAn )
							// other)._semAn__replacePlusPlusField_2(
							// stat.getI(), env.getI()).s);
						}
						else {
							return null;
						}
					});
		}

		super.calcInternalTypes(env);

		/*
		 * if ( env.getProject().getCompilerManager().getCompilationStep() ==
		 * CompilationStep.step_6 ) {
		 * MetaInfoServer.checkAssignmentPluggableTypeSystem(env, outType, this,
		 * LeftHandSideKind.LeftSideAssignment_LHS, t, varId); }
		 */

	}

	@Override
	public Symbol getFirstSymbol() {
		return plusPlus;
	}

	public ExprIdentStar getVarId() {
		return varId;
	}

	@Override
	public void genJava(PWInterface pw, Env env) {
		Type t = varId.getType();
		String javaNameType = t.getJavaName();
		String javaNameId = varId.getJavaName();
		if ( varId.getVarDeclaration().getRefType() )
			javaNameId = javaNameId + ".elem";

		if ( t == Type.Dyn ) {
			pw.printlnIdent("if ( " + javaNameId + " instanceof CyChar ) ");
			pw.printlnIdent("    " + javaNameId + " = ((CyChar ) " + javaNameId
					+ ")._succ();");
			pw.printlnIdent(
					"else if ( " + javaNameId + " instanceof CyByte ) ");
			pw.printlnIdent("    " + javaNameId + " = ((CyByte ) " + javaNameId
					+ ")._succ();");
			pw.printlnIdent("else if ( " + javaNameId + " instanceof CyInt ) ");
			pw.printlnIdent("    " + javaNameId + " = ((CyInt ) " + javaNameId
					+ ")._succ();");

			pw.printlnIdent(
					"else if ( " + javaNameId + " instanceof CyLong ) ");
			pw.printlnIdent("    " + javaNameId + " = ((CyLong ) " + javaNameId
					+ ")._succ();");

			pw.printlnIdent(
					"else if ( " + javaNameId + " instanceof CyShort ) ");
			pw.printlnIdent("    " + javaNameId + " = ((CyShort ) " + javaNameId
					+ ")._succ();");

			pw.printlnIdent("else");

			pw.printlnIdent("    throw new ExceptionContainer__("
					+ env.javaCodeForCastException(varId, Type.Int) + " );");

		}
		else if ( t == Type.Char ) {
			// ++ch results in
			// ch = ch succ that results in _ch = _ch._succ()
			pw.printlnIdent(javaNameId + " = " + javaNameId + "._succ();");
		}
		else {
			// ++i; results in i = i + 1 which results in the Java code _i =
			// _i._plus( new CyInt(1) )
			pw.printlnIdent(javaNameId + " = " + javaNameId + "._succ();");
		}
	}

	public static void replaceOpOpFieldAccessIfAsked(Env env,
			Statement opOpField,
			Function2R<IActionFieldAccess_semAn, Statement, StringBuffer> f) {

		List<AnnotationAt> annotList = env.getCurrentPrototype()
				.getAttachedAnnotationList();

		if ( annotList == null ) {
			return;
		}
		for (AnnotationAt annot : annotList) {

			CyanMetaobjectAtAnnot cyanMetaobject = annot.getCyanMetaobject();

			if ( cyanMetaobject instanceof IActionFieldAccess_semAn ) {
				IActionFieldAccess_semAn access = (IActionFieldAccess_semAn) cyanMetaobject;
				StringBuffer sb = null;
				try {
					// StringBuffer semAn_replacePlusPlusField(Statement
					// plusPlusField, Expr rightHandSideAssignment)
					sb = f.eval(access, opOpField);
					// sb = access.semAn_replacePlusPlusField(plusPlusField);
				}
				catch (error.CompileErrorException e) {
				}
				catch (NoClassDefFoundError e) {
					env.error(annot.getFirstSymbol(), e.getMessage() + " "
							+ NameServer.messageClassNotFoundException);
				}
				catch (RuntimeException e) {
					env.thrownException(annot, opOpField.getFirstSymbol(), e);
				}
				finally {
					env.errorInMetaobject(cyanMetaobject,
							opOpField.getFirstSymbol());
				}

				if ( sb != null ) {
					Symbol symForError = opOpField.getFirstSymbol();

					if ( opOpField.getCodeThatReplacesThisExpr() != null ) {
						/*
						 * this field access has already been replaced by
						 * another expression
						 */
						if ( opOpField
								.getCyanAnnotationThatReplacedMSbyExpr() != null ) {
							env.warning(symForError, "Metaobject annotation '"
									+ cyanMetaobject.getName() + "' at line "
									+ annot.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ annot.getPackageOfAnnotation() + "."
									+ annot.getPackageOfAnnotation()
									+ " is trying to replace the field access '"
									+ opOpField.asString()
									+ "' by an expression. But this has already been asked by metaobject annotation '"
									+ opOpField
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getCyanMetaobject().getName()
									+ "'" + " at line "
									+ opOpField
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getFirstSymbol().getLineNumber()
									+ " of prototype "
									+ opOpField
											.getCyanAnnotationThatReplacedMSbyExpr()
											.getPackageOfAnnotation()
									+ "."
									+ opOpField
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
									+ opOpField.asString()
									+ "' by an expression. But this has already been asked by someone else");
						}
					}

					// if there is any errors, signals them
					env.errorInMetaobject(cyanMetaobject, symForError);

					// Type typeOfCode = leftExpr.type;

					int offsetNext = opOpField.nextSymbol.getOffset();
					if ( opOpField.getNextSymbol() != null && opOpField
							.getNextSymbol().token == Token.SEMICOLON ) {
						offsetNext = opOpField.getNextSymbol().getOffset() + 1;
					}
					env.removeAddCodeFromToOffset(opOpField, offsetNext, annot,
							sb, null);
					// env.removeAddCodeStatement(this, annot, sb, null);
					/*
					 *
					 */
					// removeCodeFromToOffset(int offsetStart, int offsetNext,
					// CompilationUnitSuper compilationUnit,
					// AnnotationAt annotation)

					opOpField.setCyanAnnotationThatReplacedMSbyExpr(annot);

					return;
				}
			}
		}
	}

	private ExprIdentStar	varId;

	private Symbol			plusPlus;

}
