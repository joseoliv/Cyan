package ast;

import java.lang.reflect.Field;
import java.util.List;
import lexer.Symbol;
import meta.BreakException;
import meta.WrStatementFor;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

public class StatementFor extends Statement {


	public StatementFor(Symbol forSymbol, Expr typeInDec, StatementLocalVariableDec localVariableDec, Expr forExpression,
			StatementList statementList, Symbol rightCBEndsIf,
			MethodDec method) {
		super(method);
		this.forSymbol = forSymbol;
		this.typeInDec = typeInDec;
		this.localVariableDec = localVariableDec;
		this.forExpression = forExpression;
		this.statementList = statementList;
		this.rightCBEndsIf = rightCBEndsIf;
		this.shouldBeFollowedBySemicolon = false;
	}

	@Override
	public WrStatementFor getI() {
		if ( iStatementFor == null ) {
			iStatementFor = new WrStatementFor(this);
		}
		return iStatementFor;
	}

	private WrStatementFor iStatementFor = null;

	@Override
	public void accept(ASTVisitor visitor) {
		this.forExpression.accept(visitor);
		this.localVariableDec.accept(visitor);
		this.statementList.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public boolean alwaysReturn(Env env) {
		return this.statementList.alwaysReturn(env);
	}

	@Override
	public boolean alwaysBreak(Env env) {
		return false;
	}



	@Override
	public Object eval(EvalEnv ee) {

		ee.incLoop();
		Object forExpr = forExpression.eval(ee);
		if ( forExpr == null ) { return null; }
		Object iterator = Statement.sendMessage(forExpr, "_iterator", "iterator", null, "iterator", this.getFirstSymbol().getI(), ee  );
		if ( iterator == null ) {
			ee.error(this.getFirstSymbol(), "The 'for' expression does not have a 'iterator' method");
		}

		try {
			ee.pushLexicalLevel();
			ee.addLocalVar(localVariableDec);
			while ( true ) {
				Object hasNext = Statement.sendMessage(iterator, "_hasNext", "hasNext", null, "hasNext",
						this.getFirstSymbol().getI(), ee );
				if ( hasNext == null ) {
					ee.error(this.getFirstSymbol(), "Method 'hasNext' of the 'for' iterator returned null");
					return null;
				}
				try {

					if ( hasNext instanceof Boolean ) {
						if (! ((Boolean ) hasNext) ) { break; }
					}
					else if ( hasNext.getClass() != ee.getCyBoolean() ) {
						ee.error(this.getFirstSymbol(), "A Boolean expression was expected");
					}
					else {
						Field f;
						f = hasNext.getClass().getField("b");
						if ( ! (Boolean ) f.get(hasNext) ) 	{
							break;
						}
					}

					Object next = Statement.sendMessage(iterator, "_next", "next", null, "next",
							this.getFirstSymbol().getI(), ee);
					if ( next == null ) {
						ee.error(this.getFirstSymbol(), "Method 'next' of the 'for' iterator returned null");
						return null;
					}

					localVariableDec.setValueInInterpreter(next);
					ee.pushLexicalLevel();
					statementList.eval(ee);
					ee.popLexicalLevel();
				}
				catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					ee.error(this.getFirstSymbol(), "Internal error in Boolean expression");
				}
			}

		}
		catch ( BreakException e ) {
			// a break exception was throw, return from loop
		}
		finally {
			ee.popLexicalLevel();
			ee.decLoop();
		}
		return null;
	}



	@Override
	public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.print("for " + localVariableDec.getName());
		pw.print(" in ");
		forExpression.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.println(" {");
		pw.add();
		statementList.genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		pw.sub();
		pw.printlnIdent("}");
	}

	@Override
	public Symbol getFirstSymbol() {
		return forSymbol;
	}

	@Override
	public void genJava(PWInterface pw, Env env) {

		pw.add();
		pw.printlnIdent("{");
		Type localVariableDecType = localVariableDec.getType();
		if ( localVariableDecType == Type.Dyn || localVariableDecType instanceof TypeUnion ) {
			/*
			 *  // var Dyn s = expression;
			 * for s in container {
			 * }
			 * code for calling _iterator. The result is in variable nameVarReturnedValue
	        CyByte tmp141 = _container._iterator();
	        while ( tmp141._hasNext().b ) {
	         	_s = _container._next();
	         	// statement code
	        }
			 *
			 */
			localVariableDec.genJava(pw, env);
			String tmpForExpression = forExpression.genJavaExpr(pw, env);

			String nameVarIterator = Statement.genJavaDynamicUnaryMessageSend(pw, tmpForExpression, "_iterator", env,
					forExpression.getFirstSymbol().getLineNumber(), "iterator");
			pw.printlnIdent("while ( true ) { ");
			pw.add();
			String nameVarHasNextResult = Statement.genJavaDynamicUnaryMessageSend(pw, nameVarIterator, "_hasNext",
					env, localVariableDec.getFirstSymbol().getLineNumber(), "hasNext");
			// add convertion from Dyn
			pw.printlnIdent("if ( !(" + nameVarHasNextResult + " instanceof CyBoolean) ) {");
			pw.add();

			pw.printlnIdent("throw new ExceptionContainer__("
					+ env.javaCodeForCastException(localVariableDec, Type.Boolean) + " );");

			pw.sub();
			pw.printlnIdent("}");
			pw.printlnIdent("if ( ! ((CyBoolean ) " + nameVarHasNextResult + ").b ) break;");

			String nameVarNextResult = Statement.genJavaDynamicUnaryMessageSend(pw, nameVarIterator, "_next",
					env, localVariableDec.getFirstSymbol().getLineNumber(), "next");

			pw.printIdent(localVariableDec.getJavaName());
			if ( localVariableDec.getRefType() ) {
				pw.print(".elem");
			}
			pw.println(" = " + nameVarNextResult + ";");

	 		if ( statementList != null )
			    statementList.genJava(pw, env);
			pw.sub();
			pw.printlnIdent("}");

		}
		else {
			/*
			 * for s in container {
			 * }
	        CyByte tmp141 = _container._iterator();
	        while ( tmp141._hasNext().b ) {
	         	_s = _container._next();
	         	// statement code
	        }
			 *
			 */

			localVariableDec.genJava(pw, env);
			String tmpForExpression = forExpression.genJavaExpr(pw, env);
			String tmpIterator = NameServer.nextJavaLocalVariableName();
			// forExpression.getType()
			pw.printlnIdent( this.returnTypeInteratorMethod.getJavaName() + " " +  tmpIterator + " = " +
			    tmpForExpression + "._iterator();");
			pw.printlnIdent("while ( " + tmpIterator + "._hasNext().b ) { ");
			pw.add();
			pw.printIdent(localVariableDec.getJavaName());
			if ( localVariableDec.getRefType() ) {
				pw.print(".elem");
			}
			pw.println(" = " + tmpIterator + "._next();");
	 		if ( statementList != null )
			    statementList.genJava(pw, env);
			pw.sub();
			pw.printlnIdent("}");
		}
		pw.sub();
		pw.printlnIdent("}");
	}




	@Override
	public void calcInternalTypes(Env env) {
		try {
			env.pushRepetitionStatStack('f');
			env.pushControlFlowStack(this);


			if ( typeInDec != null ) {

 				try {
 					env.pushCheckUsePossiblyNonInitializedPrototype(false);
 					typeInDec.calcInternalTypes(env);
 				}
 				finally {
 					env.popCheckUsePossiblyNonInitializedPrototype();
 				}

			}

			env.addLexicalLevel();

			int numLocalVariables = env.numberOfLocalVariables();


			String name = localVariableDec.getName();
			if ( env.searchLocalVariableParameter(name) != null ) {
				env.error(localVariableDec.getFirstSymbol(), "Variable is being redeclared. A 'for' variable cannot have been previously declared in the same method");
			}
			try {
				env.pushCheckUsePossiblyNonInitializedPrototype(true);
				forExpression.calcInternalTypes(env);
			}
			finally {
				env.popCheckUsePossiblyNonInitializedPrototype();
			}

			Type t = forExpression.getType();
			Type elemType = Type.Dyn;

			/*
			 * do not demand method 'iterator' if the type is Dyn
			 */
			if ( t != Type.Dyn && !(t instanceof TypeUnion) ) {
				List<MethodSignature> msList = t.searchMethodProtectedPublicPackageSuperProtectedPublicPackage("iterator", env);
				if ( msList == null || msList.size() == 0 ) {
					env.error(forExpression.getFirstSymbol(), "This expression should have a method 'iterator -> Iterator<T>'");
					return ;
				}
				MethodSignature ms = msList.get(0);
				Type returnType = ms.getReturnType(env);
				if ( returnType instanceof InterfaceDec ) {
					this.returnTypeInteratorMethod = (InterfaceDec ) returnType;
					List<List<GenericParameter>> gpListList = returnTypeInteratorMethod.getGenericParameterListList();
					if ( gpListList != null && gpListList.size() == 1 ) {
						List<GenericParameter> gpList = gpListList.get(0);
						if ( gpList.size() == 1 ) {
							elemType = gpList.get(0).getParameter().getType();
						}
					}
				}
				if ( elemType == null ) {
					env.error(forExpression.getFirstSymbol(), "This expression should have a method 'iterator -> Iterator<T>'");
					return ;
				}
			}
			localVariableDec.setType(elemType);

			if ( typeInDec != null ) {
				Type declaredVarType = typeInDec.ifRepresentsTypeReturnsType(env);
				if ( declaredVarType instanceof TypeWithAnnotations ) {
					env.error(localVariableDec.getFirstSymbol(), "'for' variable cannot be declared with "
							+ "a type with an annotation as 'Int@range(1, 10)'");
				}
				if ( elemType !=  declaredVarType ) {
					env.error(localVariableDec.getFirstSymbol(), "According to the 'for' expression the "
							+ "type of this variable should be '" + localVariableDec.getType().getFullName() + "' "
									+ "but it is declared with type '" + declaredVarType.getFullName() + "'");
				}
			}

			env.pushVariableDec(localVariableDec);

			env.pushVariableAndLevel(localVariableDec, name);
	        /* unnecessary because the for variable is read-only and it is initialized by the
	         * for statement
	         *
	         *
	         */
			env.addLocalVariableInfoToCurrentLexicalLevel(localVariableDec, this.forExpression);

			statementList.calcInternalTypes(env);

			int numLocalVariablesToPop = env.numberOfLocalVariables() - numLocalVariables;

			env.popNumLocalVariableDec(numLocalVariablesToPop);

			env.removeVariablesLastLevel();
			env.removeLocalVarInfoLastLevel();
			env.subLexicalLevel();

			super.calcInternalTypes(env);


		}
		finally {
			env.popRepetitionStatStack();
			env.popControlFlowStack();
		}
	}

	public StatementList getStatementList() {
		return statementList;
	}

	public Symbol getRightCBEndsIf() {
		return rightCBEndsIf;
	}

	public Expr getTypeInDec() {
		return typeInDec;
	}

	public StatementLocalVariableDec getLocalVariableDec() {
		return localVariableDec;
	}

	public Expr getForExpression() {
		return forExpression;
	}

	private Symbol forSymbol;
	private Expr forExpression;
	private Expr typeInDec;
	private StatementList statementList;
	private StatementLocalVariableDec localVariableDec;
	private InterfaceDec returnTypeInteratorMethod;
	/**
	 * the '}' symbol that ends a for
	 */
	private Symbol rightCBEndsIf;


}
