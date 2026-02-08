
package ast;

import java.lang.reflect.Field;
import cyan.reflect._CyanMetaobject;
import cyan.reflect._IActionFieldAccess__semAn;
import lexer.Symbol;
import meta.CyanMetaobject;
import meta.IActionFieldAccess_semAn;
import meta.IdentStarKind;
import meta.Timeout;
import meta.WrStatementMinusMinusIdent;
import saci.CyanEnv;
import saci.Env;
import saci.Saci;

/**
 * Represents an statement like<br>
 * {@code ++i;}<br>
 * 
 * @author jose
 * 
 */
public class StatementMinusMinusIdent extends Statement {

	public StatementMinusMinusIdent(Symbol minusMinus, ExprIdentStar varId,
			MethodDec method) {
		super(method);
		this.minusMinus = minusMinus;
		this.varId = varId;
	}

	@Override
	public WrStatementMinusMinusIdent getI() {
		if ( iStatementMinusMinusIdent == null ) {
			iStatementMinusMinusIdent = new WrStatementMinusMinusIdent(this);
		}
		return iStatementMinusMinusIdent;
	}

	private WrStatementMinusMinusIdent iStatementMinusMinusIdent = null;

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
					className = className.substring(lastDot - 1);
				}
				switch (className) {
				case "Byte":
					v.setValueInInterpreter(
							ee.newCyByte((byte) ((Byte) exprValue - 1)));
					break;
				case "Character":
					v.setValueInInterpreter(
							ee.newCyChar((char) ((Character) exprValue - 1)));
					break;
				case "Integer":
					v.setValueInInterpreter(
							ee.newCyInt(((Integer) exprValue - 1)));
					break;
				case "Long":
					v.setValueInInterpreter(
							ee.newCyLong(((Long) exprValue - 1)));
					break;
				case "Short":
					v.setValueInInterpreter(
							ee.newCyShort((short) ((Short) exprValue - 1)));
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
					className = className.substring(lastDot - 1);
				}
				switch (className) {
				case "Byte":
					v.setValueInInterpreter(((Byte) value) - 1);
					break;
				case "Character":
					v.setValueInInterpreter(((Character) value) - 1);
					break;
				case "Integer":
					v.setValueInInterpreter(((Integer) value) - 1);
					break;
				case "Long":
					v.setValueInInterpreter(((Long) value) - 1);
					break;
				case "Short":
					v.setValueInInterpreter(((Short) value) - 1);
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
		pw.print("--");
		varId.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}

	@Override
	public void calcInternalTypes(Env env) {

		varId.calcInternalTypes(env, true);
		VariableDecInterface aVar = varId.getVarDeclaration();
		if ( aVar == null || aVar.isReadonly() ) {
			env.error(varId.getFirstSymbol(),
					"Operator -- can only be applied to variables that are not read only");
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
					"Operator -- can only be applied to variables of types Byte, Char, Int, Long, Short, and Dyn");
		}

		if ( varId.getIdentStarKind() == IdentStarKind.instance_variable_t ) {
			StatementPlusPlusIdent.replaceOpOpFieldAccessIfAsked(env, this,
					(IActionFieldAccess_semAn access, Statement stat) -> {

						int timeoutMilliseconds = Timeout
								.getTimeoutMilliseconds(env,
										env.getProject().getProgram().getI(),
										env.getCurrentCompilationUnit()
												.getCyanPackage().getI(),
										this.getFirstSymbol());

						Timeout<StringBuffer> to = new Timeout<>();

						_CyanMetaobject other = ((CyanMetaobject) access)
								.getMetaobjectInCyan();
						if ( other == null ) {
							if ( Saci.timeLimitForMetaobjects ) {
								return to.run(() -> {
									return access.semAn_replaceMinusMinusField(
											stat.getI(), env.getI());
								}, timeoutMilliseconds,
										"semAn_replaceMinusMinusField",
										(CyanMetaobject) access, env);

							}
							else {
								return access.semAn_replaceMinusMinusField(
										stat.getI(), env.getI());
							}

							// return
							// access.semAn_replaceMinusMinusField(stat.getI(),
							// env.getI());
						}
						else if ( other instanceof _IActionFieldAccess__semAn ) {
							return to.run(() -> {
								return new StringBuffer(
										((_IActionFieldAccess__semAn) other)
												._semAn__replaceMinusMinusField_2(
														stat.getI(),
														env.getI()).s);
							}, timeoutMilliseconds,
									"semAn_replaceMinusMinusField",
									(CyanMetaobject) access, env);

							// return new StringBuffer(
							// ((_IActionFieldAccess__semAn )
							// other)._semAn__replaceMinusMinusField_2(
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
		 * MetaInfoServer.checkAssignmentPluggableTypeSystem(env, outType,
		 * varId, LeftHandSideKind.LeftSideAssignment_LHS, t, varId); }
		 */

	}

	@Override
	public Symbol getFirstSymbol() {
		return minusMinus;
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
					+ ")._pred();");
			pw.printlnIdent(
					"else if ( " + javaNameId + " instanceof CyByte ) ");
			pw.printlnIdent("    " + javaNameId + " = ((CyByte ) " + javaNameId
					+ ")._pred();");
			pw.printlnIdent("else if ( " + javaNameId + " instanceof CyInt ) ");
			pw.printlnIdent("    " + javaNameId + " = ((CyInt ) " + javaNameId
					+ ")._pred();");

			pw.printlnIdent(
					"else if ( " + javaNameId + " instanceof CyLong ) ");
			pw.printlnIdent("    " + javaNameId + " = ((CyLong ) " + javaNameId
					+ ")._pred();");

			pw.printlnIdent(
					"else if ( " + javaNameId + " instanceof CyShort ) ");
			pw.printlnIdent("    " + javaNameId + " = ((CyShort ) " + javaNameId
					+ ")._pred();");

			pw.printlnIdent("else");

			pw.printlnIdent("    throw new ExceptionContainer__("
					+ env.javaCodeForCastException(varId, Type.Int) + " );");

		}
		else if ( t == Type.Char ) {
			// --ch results in
			// ch = ch pred that results in _ch = _ch._pred()
			pw.printlnIdent(javaNameId + " = " + javaNameId + "._pred();");
		}
		else {
			// --i; results in i = i - 1 which results in the Java code _i =
			// _i._minus( new CyInt(1) )
			pw.printlnIdent(javaNameId + " = " + javaNameId + "._minus( new "
					+ javaNameType + "(1) );");
		}
	}

	private ExprIdentStar	varId;

	private Symbol			minusMinus;

}
