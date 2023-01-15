/**
 *
 */
package ast;

import java.util.List;
import lexer.Symbol;
import lexer.SymbolOperator;
import meta.MetaHelper;
import meta.Token;
import meta.WrExprUnary;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** Represents an expression preceded by an unary operator
 * @author José
 *
 */
public class ExprUnary extends Expr {

	public ExprUnary(SymbolOperator symbolOperator, Expr expr, MethodDec method) {
		super(method);
		this.symbolOperator = symbolOperator;
		this.expr = expr;
	}

	@Override
	public WrExprUnary getI() {
		return new WrExprUnary(this);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		expr.accept(visitor);
		visitor.visit(this);
	}


	@Override
	public Object eval(EvalEnv ee) {
		Object receiverValue = expr.eval(ee);
		String messageName = this.symbolOperator.getSymbolString();
		Class<?> receiverClass;
		receiverClass = receiverValue.getClass();

		String cyanMethodNameInJava = MetaHelper.getJavaName(messageName);
		String methodNameInJava = cyanMethodNameInJava;
		if ( cyanMethodNameInJava.charAt(0) == '_' ) {
			if ( cyanMethodNameInJava.endsWith(":") ) {
				methodNameInJava = cyanMethodNameInJava.substring(1, cyanMethodNameInJava.length()-1);
			}
			else {
				methodNameInJava = cyanMethodNameInJava.substring(1);
			}
		}
        Object ret = null;
		ret = Statement.sendMessage(receiverValue, cyanMethodNameInJava,  methodNameInJava,
				null, this.symbolOperator.getSymbolString(), this.getFirstSymbol().getI(), ee );
        if ( ret != null ) {
	        return ret;
        }


		return null;
	}



	@Override
	public boolean mayBeStatement() {
		return false;
	}


	@Override
	public boolean isNRE(Env env) {
		return expr instanceof ExprLiteral;
	}

	@Override
	public boolean isNREForInitShared(Env env) {
		return expr instanceof ExprLiteral;
		//return expr.isNREForInitShared(env);
	}


	public void setSymbolOperator(SymbolOperator symbolOperator) {
		this.symbolOperator = symbolOperator;
	}
	public SymbolOperator getSymbolOperator() {
		return symbolOperator;
	}

	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	public Expr getExpr() {
		return expr;
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.print(symbolOperator.getSymbolString());
		expr.genCyan(pw, false, cyanEnv, genFunctions);
	}

	@Override
	public String genJavaExpr(PWInterface pw, Env env) {
		String tmpVarName = NameServer.nextJavaLocalVariableName();
		String exprVarName = expr.genJavaExpr(pw, env);

		if ( expr.getType(env) != Type.Dyn ) {
			pw.printlnIdent(type.getJavaName() + " " + tmpVarName + " = " + exprVarName + "." +
					MetaHelper.getJavaNameOfkeyword(this.symbolOperator.getSymbolString()) + "();");
		}
		else {
			String unaryName = this.symbolOperator.getSymbolString();
			tmpVarName = Statement.genJavaDynamicUnaryMessageSend(pw, exprVarName,
					MetaHelper.getJavaNameOfkeyword(unaryName),
					env, expr.getFirstSymbol().getLineNumber(), null);
		}

		return tmpVarName;
	}


	@Override
	public Symbol getFirstSymbol() {
		return symbolOperator;
	}


	@Override
	public void calcInternalTypes(Env env) {

		try {
			env.pushCheckUsePossiblyNonInitializedPrototype(true);
			expr.calcInternalTypes(env);
		}
		finally {
			env.popCheckUsePossiblyNonInitializedPrototype();
		}

		Type receiverType = expr.getType(env);
		String methodName = symbolOperator.getSymbolString();

		if ( receiverType == Type.Dyn ) {
			type = Type.Dyn;
		}
		else {
			List<MethodSignature> methodSignatureList;
			if ( expr instanceof ExprSelf ) {
				methodSignatureList = receiverType.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodName, env);
			}
			else {
				methodSignatureList = receiverType.searchMethodPublicPackageSuperPublicPackage(methodName, env);
			}
			if ( methodSignatureList == null || methodSignatureList.size() == 0 ) {
				env.error(getFirstSymbol(), "Method " + methodName + " was not found in " + receiverType.getName(), true, true);
				type = Type.Dyn;
				return ;
			}
			else
				type = methodSignatureList.get(0).getReturnType(env);

			MethodSignature ms = methodSignatureList.get(0);
			MethodDec aMethod = ms.getMethod();
			if ( aMethod != null ) {
				if ( aMethod.getVisibility() == Token.PRIVATE  ) {
					if ( aMethod.getDeclaringObject() != env.getCurrentOuterObjectDec() ) {
						env.error(this.getFirstSymbol(), "Method '" + ms.getFullName(env) +
								"' is private. It can only be called "
								+ "inside prototype '" + aMethod.getDeclaringObject().getFullName() + "'");
					}

				}
				else if ( aMethod.getVisibility() == Token.PACKAGE ) {
					if ( aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage() !=
							env.getCurrentObjectDec().getCompilationUnit().getCyanPackage() ) {
						env.error(this.getFirstSymbol(), "Method '" + ms.getFullName(env) +
								"'  has 'package' visibility. It can only be called "
								+ "inside package '" + aMethod.getDeclaringObject().getCompilationUnit().getCyanPackage().getName() + "'");
					}
				}
				else if ( aMethod.getVisibility() == Token.PROTECTED ) {
					if ( !aMethod.getDeclaringObject().isSupertypeOf(env.getCurrentObjectDec(), env)  ) {
						env.error(this.getFirstSymbol(), "Method '" + ms.getFullName(env) +
								"'  has 'package' visibility. It can only be called "
								+ "in subprototypes of '" +
								aMethod.getDeclaringObject().getName() + "'", true, true);
					}
				}
			}


		}
		super.calcInternalTypes(env);

	}

	public String javaNameAsType(Env env) {
		return "Error in ExprUnary::javaNameAsType";
	}


	protected SymbolOperator symbolOperator;
	protected Expr expr;
}
