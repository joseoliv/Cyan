package ast;

import error.ErrorKind;
import lexer.Symbol;
import meta.CompilationStep;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionAssignment_cge;
import meta.LeftHandSideKind;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrStatementReturn;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/**
 * represents a return statement, like
 * 	   fun get -> int {
 *         return n;
 *     }
 * @author José
 *
 */
public class StatementReturn extends Statement {


	public StatementReturn(Symbol returnSymbol, Expr expr, MethodDec currentMethod) {
		super(currentMethod, false);
		this.expr = expr;
		this.returnSymbol = returnSymbol;
	}

	@Override
	public WrStatementReturn getI() {
		if ( iStatementReturn == null ) {
			iStatementReturn = new WrStatementReturn(this);
		}
		return iStatementReturn;
	}

	private WrStatementReturn iStatementReturn = null;

	@Override
	public Object eval(EvalEnv ee) {
		ee.setReturnValue(expr.eval(ee));
		throw new ReturnValueEvalEnvException();
	}


	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		if ( expr != null ) {
			expr.accept(visitor);
		}
	}

	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {

		String s = "return ";
		if ( cyanEnv.getCreatingInnerPrototypeFromFunction() ) {
			//String methodName = currentMethod.getName();
			//if ( methodName.equals("eval") || methodName.startsWith("eval:") )
			s = "return_method__ ";
			//System.out.println(s);
		}
		pw.print(s);
		if ( expr != null ) {
			expr.genCyan(pw, false, cyanEnv, genFunctions);
		}
	}

	@Override
	public void genJava(PWInterface pw, Env env) {

		Type returnType = env.getCurrentMethod().getMethodSignature().getReturnType(env);
		if ( returnType == Type.Nil || returnType == null ) {
			pw.printlnIdent("return ;");
			return ;
		}


		String tmpVar = expr.genJavaExpr(pw, env);
		pw.println();
		/*
		 * four cases do consider:
		 *     fun m -> Int { return Dyn }   // convert Dyn to Int if possible. Otherwise throw exception
		 *     fun m -> Int { return 0 }     // no conversion
		 *     fun m -> Dyn { return 0 }     // no conversion
		 *     fun m -> Dyn { return Dyn }   // no conversion
		 */
		final Type methodReturnType = currentMethod.getMethodSignature().getReturnType(env);
		final Type exprType = expr.getType();


		/*
		 * A metaobject attached to the type of the formal parameter may demand that the real argument be
		 * changed. The new argument is the return of method  changeRightHandSideTo
		 */


		final Tuple2<IActionAssignment_cge, ObjectDec> cyanMetaobjectPrototype = MetaInfoServer.getChangeAssignmentCyanMetaobject(env, methodReturnType);
		IActionAssignment_cge changeCyanMetaobject = null;
        ObjectDec prototypeFoundMetaobject = null;
        if ( cyanMetaobjectPrototype != null ) {
        	changeCyanMetaobject = cyanMetaobjectPrototype.f1;
        	prototypeFoundMetaobject = cyanMetaobjectPrototype.f2;

				if ( changeCyanMetaobject != null ) {

					try {
						tmpVar = changeCyanMetaobject.cge_changeRightHandSideTo( prototypeFoundMetaobject,
		   	           			tmpVar, exprType);
					}
					catch ( final error.CompileErrorException e ) {
					}
					catch ( final NoClassDefFoundError e ) {
						final Annotation annotation = meta.GetHiddenItem.getHiddenCyanAnnotation(((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation());
						env.error(annotation.getFirstSymbol(), e.getMessage() + " " + NameServer.messageClassNotFoundException);
					}
					catch ( final RuntimeException e ) {
						final Annotation annotation = meta.GetHiddenItem.getHiddenCyanAnnotation(((CyanMetaobjectAtAnnot) changeCyanMetaobject).getAnnotation());
						env.thrownException(annotation, annotation.getFirstSymbol(), e);
					}
					finally {
	   					env.errorInMetaobject( (meta.CyanMetaobject ) changeCyanMetaobject, this.getFirstSymbol());
					}



   				}


        }





		if ( exprType == Type.Dyn && methodReturnType != Type.Dyn ) {
			// first case
			/*
			 *
			 */
			pw.printlnIdent("if ( " + tmpVar + " instanceof " + methodReturnType.getJavaName() + " ) {");
			pw.add();
			pw.printlnIdent("return (" + methodReturnType.getJavaName() + " ) " + tmpVar + ";");
			pw.sub();
			pw.printlnIdent("}");
			pw.printlnIdent("else {");
			pw.add();

			pw.printlnIdent("throw new ExceptionContainer__("
					+ env.javaCodeForCastException(expr, methodReturnType) + " );");

			pw.sub();
			pw.printlnIdent("}");
		}
		else {
			if ( methodReturnType == Type.Any && exprType instanceof InterfaceDec ) {
				tmpVar = " (" + MetaHelper.AnyInJava + " ) " + tmpVar;
			}

			/*
			if ( exprType instanceof TypeJavaRef ) {
				if ( methodReturnType.getInsideType() instanceof Prototype ) {
					// cast Java to Cyan
					String javaClass = exprType.getName();
					tmpVar = "new " + NameServer.cyanNameFromJavaBasicType(javaClass) + "(" + tmpVar + ")";
				}
			}
			else if ( methodReturnType instanceof TypeJavaRef ) {
				if ( exprType.getInsideType() instanceof Prototype ) {
					// cast Cyan to Java
					tmpVar = tmpVar + "." + NameServer.getFieldBasicType( exprType.getName() );
				}
			}
			*/
			tmpVar = Type.genJavaExpr_CastJavaCyan(env, tmpVar, exprType, methodReturnType, expr.getFirstSymbol());

			pw.printlnIdent("return " + tmpVar + ";");
		}
	}

	@Override
	public Symbol getFirstSymbol() {
		return returnSymbol;
	}

	@Override
	public boolean alwaysReturn(Env env) {
		return true;
	}

	@Override
	public boolean alwaysBreak(Env env) {
		return false;
	}

	public boolean alwaysReturnFromFunction() {
		return true;
	}


	@Override
	public void calcInternalTypes(Env env) {
		try {
			env.pushCheckUsePossiblyNonInitializedPrototype(true);
			if ( env.hasTryCatchWithFinallyInControlFlowStack() ) {
				env.error(returnSymbol, "'return' cannot be used if the immediate outer statement is "
						+ "'try-catch' with a 'finally' clause. That is confusing because "
						+ "the developer may not be sure if the 'finally' clause will be executed or not");
			}
//			if ( env.isTopTryCatchStatement()  ) {
//				StatementTry outerStat = (StatementTry ) env.peekControlFlowStack();
//				StatementList finallyStatList = outerStat.getFinallyStatementList();
//				if ( finallyStatList != null  ) {
//				}
//			}
			expr.calcInternalTypes(env);
		}
		finally {
			env.popCheckUsePossiblyNonInitializedPrototype();
		}

//		if ( expr.getType(env) == null ) {
//			expr.calcInternalTypes(env);
//		}
		if ( ! currentMethod.getMethodSignature().getReturnType(env).isSupertypeOf(expr.getType(env), env) )  {
			currentMethod.getMethodSignature().getReturnType(env).isSupertypeOf(expr.getType(env), env);
			env.error(true, expr.getFirstSymbol(),
					"The type of the returned value, " +   expr.getType(env).getFullName() +
			        ", is not a subtype of the method return type, " + currentMethod.getMethodSignature().getReturnType(env).getFullName(),
			        null, ErrorKind.type_error_return_value_type_is_not_a_subtype_of_the_method_return_type);
		}
		super.calcInternalTypes(env);
        if ( env.getProject().getCompilerManager().getCompilationStep() == CompilationStep.step_6 ) {
            MetaInfoServer.checkAssignmentPluggableTypeSystem(env,
            		env.getCurrentMethod().getMethodSignature().getReturnType(env), this, LeftHandSideKind.MethodSignatureReturn_LHS,
            		expr.getType(), expr);
        }

	}


	@Override
	public boolean demandSemicolon() { return false; }

	public Expr getExpr() {
		return expr;
	}

	private final Symbol returnSymbol;
	private final Expr expr;

}
