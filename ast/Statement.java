/**
 *
 */
package ast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import cyan.lang._Nil;
import lexer.Symbol;
import meta.InterpretationErrorException;
import meta.WrEnv;
import meta.WrStatement;
import meta.WrSymbol;
import saci.CyanEnv;
import saci.Env;
import saci.NameServer;

/** This class is the superclass of all classes that represent statements
 *
 * @author José
 *
 */
public abstract class Statement implements GenCyan, CodeWithError, ICalcInternalTypes, ASTNode, INextSymbol {

	public Statement(MethodDec currentMethod) {
		this.currentMethod = currentMethod;
		this.shouldBeFollowedBySemicolon = true;
		setCreatedByMetaobjects(false);
		cyanAnnotationThatReplacedStatByAnotherOne = null;
	}

	public Statement(MethodDec currentMethod, boolean shouldBeFollowedBySemicolon) {
		this.currentMethod = currentMethod;
		this.shouldBeFollowedBySemicolon = shouldBeFollowedBySemicolon;
		cyanAnnotationThatReplacedStatByAnotherOne = null;
	}

	@Override
	public
	abstract WrStatement getI();


	/**
	 * return true if this object may be a statement. For example, a literal integer is an expression but it cannot be a statement.
	 * A identifier may be a statement if it is a unary method call. Or it may be not if it is an identifier.
	   @return
	 */
	public boolean mayBeStatement() {
		return true;
	}

	/**
	 * return true if a ';' should be added at the end of code generation to Java
	 */
	public boolean addSemicolonJavaCode() {
		return false;
	}

	// abstract public void genCyan(PWInterface pw, CyanEnv cyanEnv);

	@Override
	final public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		if ( this.codeThatReplacesThisStatement != null ) {
			pw.print(this.codeThatReplacesThisStatement);
		}
		else {
			this.genCyanReal(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
		}
	}

	abstract public void genCyanReal(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions);
	/**
	 * return the first symbol of this statement. For example, if the statement is
	 *     a[i] = 0;
	 * the symbol returned is  that corresponding to variable "a"
	 *
	 * @return
	 */
	@Override
	abstract public Symbol getFirstSymbol();

	/**
	 * return the first symbol of a statement. If it is an annotation, return the first annotation symbol
	   @return
	 */
	public Symbol getFirstSymbolMayBeAnnotation() {
		return this.getFirstSymbol();
	}

	@Override
	public Symbol getLastSymbol() {
		return lastSymbol;
	}


	abstract public void genJava(PWInterface pw, Env env);



	public void setLastSymbol(Symbol lastSymbol) {
		this.lastSymbol = lastSymbol;
	}

	public void calcInternalTypes(Env env) {
		if ( afterAnnotation != null ) {
			afterAnnotation.calcInternalTypes(env);
		}
	}

	@Override
	public void calcInternalTypes(WrEnv env) {
		calcInternalTypes(meta.GetHiddenItem.getHiddenEnv(env));
	}


	/**
	 * return true if this statement demand a <code>'{@literal ;}'</code> after it
	 */
	public boolean demandSemicolon() { return shouldBeFollowedBySemicolon; }


	public String asString(CyanEnv cyanEnv) {
		final PWCharArray pwChar = new PWCharArray();
		genCyan(pwChar, true, cyanEnv, true);
		return pwChar.getGeneratedString().toString();
	}

	@Override
	public String asString() {
		return asString(NameServer.cyanEnv);

	}

	public String genJavaAsString(Env env) {
		final PWCharArray pwChar = new PWCharArray();
		genJava(pwChar, env);
		return pwChar.getGeneratedString().toString();
	}

	/**
	 * return true if this statement always execute a 'return' statement
	   @return
	 */
	public boolean alwaysReturn(Env env) {
		return false;
	}

	/**
	 * return true if this statement always execute a 'break' statement
	   @param env
	   @return
	 */
	public boolean alwaysBreak(Env env) {
		return false;
	}



	public void prepareLiveAnalysis() {
		inLiveAnalysis = new HashSet<>();
		outLiveAnalysis = new HashSet<>();
		useLiveAnalysis = new HashSet<>();
		defLiveAnalysis = new HashSet<>();
	}

	public Set<Statement> successors() {
		return null;
	}

	public boolean getShouldBeFollowedBySemicolon() {
		return shouldBeFollowedBySemicolon;
	}

	public void setShouldBeFollowedBySemicolon(boolean shouldBeFollowedBySemicolon) {
		this.shouldBeFollowedBySemicolon = shouldBeFollowedBySemicolon;
	}

	/**
	 * return true if this statement do return; that is, the execution
	 * continue past it.
	 * For example, a message send with keyword 'throw' do
	 * not return.
	   @return
	 */
	public boolean statementDoReturn() {
		return false;
	}




	/**
	 * generate code for an unary message send to a variable whose name is nameVar of type Dyn (Object in Java).
	 * Return the name of the local variable in Java that keeps the return value of the method called.
	 *
	 */
	static public String genJavaDynamicUnaryMessageSend(PWInterface pw, String nameVar,
			String unaryMessage, Env env, int lineNumber, String unaryMessageJava) {


		final String aMethodTmp = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("java.lang.reflect.Method " + aMethodTmp + " = CyanRuntime.getJavaMethodByName(" + nameVar + ".getClass(), \"" +
				unaryMessage + "\", 0);");
		if ( unaryMessageJava != null ) {
			pw.printlnIdent("if (" + aMethodTmp + " == null ) { " + aMethodTmp + " = CyanRuntime.getJavaMethodByName(" + nameVar + ".getClass(), \"" +
					unaryMessageJava + "\", 0); } ");
		}
		pw.printlnIdent("if (" + aMethodTmp +
				" == null) throw new ExceptionContainer__( new _ExceptionMethodNotFound( new CyString(\"Method called at line \" + " + lineNumber +
				"+ \" of prototype '" + env.getCurrentPrototype().getFullName() + "' was not found\") ) );");
		final String tmp = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("Object " + tmp + " = null;");
		pw.printlnIdent("try {");
		pw.add();
		pw.printlnIdent(aMethodTmp + ".setAccessible(true);");

		pw.printlnIdent(tmp + " = " + aMethodTmp + ".invoke(" + nameVar + ");");
		pw.sub();
		pw.printlnIdent("}");

		String ep = NameServer.nextJavaLocalVariableName();

		pw.printlnIdent("catch ( java.lang.reflect.InvocationTargetException " + ep +" ) {");
        pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
        pw.printlnIdent("	if ( t__ instanceof ExceptionContainer__ ) {");
        pw.printlnIdent("    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
        pw.printlnIdent("	}");
        pw.printlnIdent("	else");
        pw.printlnIdent("		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
        pw.printlnIdent("}");
		pw.printlnIdent("catch (IllegalAccessException | IllegalArgumentException " + ep + ") {");
		pw.add();

		final String dnuTmpVar = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("//	func doesNotUnderstand: (String methodName, Array<Array<Dyn>> args)");
		pw.printlnIdent("java.lang.reflect.Method " + dnuTmpVar + " = CyanRuntime.getJavaMethodByName(" + nameVar + ".getClass(), \"" +
		         NameServer.javaNameDoesNotUnderstand + "\", 2);");
		pw.printlnIdent(tmp + " = null;");
		pw.printlnIdent("try {");
		pw.add();
		pw.printlnIdent(aMethodTmp + ".setAccessible(true);");
		pw.printlnIdent(tmp + " = " + aMethodTmp + ".invoke(" + nameVar + ");");
		pw.sub();

		pw.printlnIdent("}");
		ep = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("catch ( java.lang.reflect.InvocationTargetException " + ep + " ) {");
        pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
        pw.printlnIdent("	if ( t__ instanceof ExceptionContainer__ ) {");
        pw.printlnIdent("    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
        pw.printlnIdent("	}");
        pw.printlnIdent("	else");
        pw.printlnIdent("		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
        pw.printlnIdent("}");
		pw.printlnIdent("catch (IllegalAccessException | IllegalArgumentException " + ep + ") {");
		pw.printlnIdent("        throw new ExceptionContainer__( new _ExceptionMethodNotFound( new CyString(\"Method called at line \" + " + lineNumber +
				"+ \" of prototype '" + env.getCurrentPrototype().getFullName() + "' was not found\") ) );");


		pw.printlnIdent("}");
		pw.sub();
		pw.printlnIdent("}");

		return tmp;
	}

	/**
	 * generate code for a message send to a variable whose name is nameVar of type Dyn (Object in Java).
	 * Return the name of the local variable in Java that keeps the return value of the method called.
	 *
	 */
	static public String genJavaDynamicKeywordMessageSend(PWInterface pw, String nameVar, String methodJavaName,
			String commaParameterList, int numParam, Env env, int lineNumber) {

			//  indexedExpr.getType() == Type.Dyn

		final String aMethodTmp = NameServer.nextJavaLocalVariableName();

		pw.printlnIdent("java.lang.reflect.Method " + aMethodTmp + " = CyanRuntime.getJavaMethodByName(" + nameVar +
				".getClass(), \"" +
				methodJavaName  + "\", " + numParam + ");");
		pw.printlnIdent("if ( " + aMethodTmp + " == null ) throw new ExceptionContainer__( new _ExceptionMethodNotFound( new CyString(\"Method called at line \" + " + lineNumber +
				"+ \" of prototype '" + env.getCurrentPrototype().getFullName() + "' was not found\") ) );");




		final String resultTmpVar = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("Object " + resultTmpVar + " = null;");
		pw.printlnIdent("try {");
		pw.add();


		pw.printlnIdent(aMethodTmp + ".setAccessible(true);");
		pw.printlnIdent(resultTmpVar + " = " + aMethodTmp + ".invoke(" + nameVar  + ", " + commaParameterList +
				 ");");
		pw.sub();
		pw.printlnIdent("}");

		String ep = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("catch ( java.lang.reflect.InvocationTargetException " + ep + " ) {");
        pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
        pw.printlnIdent("	if ( t__ instanceof ExceptionContainer__ ) {");
        pw.printlnIdent("    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
        pw.printlnIdent("	}");
        pw.printlnIdent("	else");
        pw.printlnIdent("		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
        pw.printlnIdent("}");

		ep = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("catch (IllegalAccessException | IllegalArgumentException " + ep + ") {");
		pw.add();

		final String dnuTmpVar = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("//	func doesNotUnderstand: (String methodName, Array<Array<Dyn>> args)");
		pw.printlnIdent("java.lang.reflect.Method " + dnuTmpVar + " = CyanRuntime.getJavaMethodByName(" +
				nameVar + ".getClass(), \"" +
		       NameServer.javaNameDoesNotUnderstand  			+ "\", 2);");
		pw.printlnIdent("try {");
		pw.add();
		pw.printlnIdent(dnuTmpVar + ".setAccessible(true);");

		pw.printlnIdent(resultTmpVar + " = " + dnuTmpVar + ".invoke(" + nameVar + ", \"" + methodJavaName + "\", " + commaParameterList +
				 ");");
		pw.sub();

		pw.printlnIdent("}");
		ep = NameServer.nextJavaLocalVariableName();
		pw.printlnIdent("catch ( java.lang.reflect.InvocationTargetException " + ep + " ) {");
        pw.printlnIdent("	Throwable t__ = " + ep + ".getCause();");
        pw.printlnIdent("	if ( t__ instanceof ExceptionContainer__ ) {");
        pw.printlnIdent("    	throw new ExceptionContainer__( ((ExceptionContainer__) t__).elem );");
        pw.printlnIdent("	}");
        pw.printlnIdent("	else");
        pw.printlnIdent("		throw new ExceptionContainer__( new _ExceptionJavaException(t__));");
        pw.printlnIdent("}");
		pw.printlnIdent("catch (IllegalAccessException | IllegalArgumentException " + ep + ") {");
		pw.printlnIdent("        throw new ExceptionContainer__( new _ExceptionMethodNotFound( new CyString(\"Method called at line \" + " + lineNumber +
				"+ \" of prototype '" + env.getCurrentPrototype().getFullName() + "' was not found\") ) );");
		pw.printlnIdent("}");
		pw.sub();
		pw.printlnIdent("}");
		return resultTmpVar;

	}



	public Annotation getCyanAnnotationThatReplacedMSbyExpr() {
		return cyanAnnotationThatReplacedStatByAnotherOne;
	}

	public void setCyanAnnotationThatReplacedMSbyExpr(Annotation cyanAnnotationThatReplacedMSbyExpr) {
		this.cyanAnnotationThatReplacedStatByAnotherOne = cyanAnnotationThatReplacedMSbyExpr;
	}


	public AnnotationAt getAfterAnnotation() {
		return afterAnnotation;
	}

	public void setAfterAnnotation(AnnotationAt afterAnnotation) {
		this.afterAnnotation = afterAnnotation;
	}

	public Symbol getSymbolAfter() {
		return symbolAfter;
	}

	public void setSymbolAfter(Symbol symbolAfter) {
		this.symbolAfter = symbolAfter;
	}

	public boolean getCreatedByMetaobjects() {
		return createdByMetaobjects;
	}

	public void setCreatedByMetaobjects(boolean createdByMetaobjects) {
		this.createdByMetaobjects = createdByMetaobjects;
	}

	public StringBuffer getCodeThatReplacesThisExpr() {
		return codeThatReplacesThisStatement;
	}

	public void setCodeThatReplacesThisExpr(StringBuffer codeThatReplacesThisExpr) {
		this.codeThatReplacesThisStatement = codeThatReplacesThisExpr;
	}


	@Override
	public Symbol getNextSymbol() { return nextSymbol; }

	@Override
	public void setNextSymbol(Symbol nextSymbol) {
		this.nextSymbol = nextSymbol;
	}

	@SuppressWarnings("unused")
	public Object eval(EvalEnv ee) { return null; }


	static public Object sendMessage(Object receiver,
			String cyanMethodNameInJava,
			final String javaMethodName,
			Object []argList,
			String selector, WrSymbol symForError, EvalEnv ee) {
        Object ret = null;
        try {
			Object []parameterTypes = null;
			final Class<?> receiverClass;
			receiverClass = receiver.getClass();
			java.lang.reflect.Method javaMethod;
			if ( EvalEnv.any.isAssignableFrom(receiverClass) ) {
				// A Cyan object
				if ( argList != null ) {
					parameterTypes = new Object[argList.length];
					int i = 0;
					for ( final Object p : argList ) {
						parameterTypes[i] = p.getClass();
						++i;
					}
				}
				// a Cyan method
				javaMethod = Statement.getMethod(receiverClass, cyanMethodNameInJava, argList != null ? argList.length : 0);
			}
			else {
				// a Java object
				if ( argList != null ) {
					parameterTypes = new Object[argList.length];
					for ( int i = 0; i < argList.length; ++i ) {
						final Object p = argList[i];
						final Class<?> paramClass = p.getClass();
						parameterTypes[i] = paramClass;
						final String fullName = paramClass.getName();
						final int lastDot = fullName.lastIndexOf('.');
						String className;
						if ( lastDot > 0 ) {
							className = fullName.substring(lastDot+1);
						}
						else {
							className = fullName;
						}
						switch ( className ) {
						case "CyBoolean":
							argList[i] = getField(p, "b" );
							parameterTypes[i] = Boolean.class;
							break;
						case "CyChar":
							argList[i] = getField(p, "c");
							parameterTypes[i] = Character.class;
							break;
						case "CyByte":
							argList[i] = getField(p, "n");
							parameterTypes[i] = Byte.class;
							break;
						case "CyInt":
							argList[i] = getField(p, "n");
							parameterTypes[i] = Integer.class;
							break;
						case "CyShort":
							argList[i] = getField(p, "n");
							parameterTypes[i] = Short.class;
							break;
						case "CyLong":
							argList[i] = getField(p, "n");
							parameterTypes[i] = Long.class;
							break;
						case "CyFloat":
							argList[i] = getField(p, "n");
							parameterTypes[i] = Float.class;
							break;
						case "CyDouble":
							argList[i] = getField(p, "n");
							parameterTypes[i] = Double.class;
							break;
						case "CyString":
							argList[i] = getField(p, "s");
							parameterTypes[i] = String.class;
							break;
						}
					}
				}
				if ( javaMethodName == null ) { throw new NoSuchMethodException(); }
				// the line below was inside the next 'if'
				javaMethod = Statement.getMethod(receiverClass, javaMethodName, argList != null ? argList.length : 0);
//				if ( receiver instanceof Class<?> ) {
//					// a call to a Java static method
//					javaMethod = Statement.getMethod( (Class<?> ) receiver, javaMethodName, argList != null ? argList.length : 0);
//				}
//				else {
//					javaMethod = Statement.getMethod(receiverClass, javaMethodName, argList != null ? argList.length : 0);
//				}
			}
			if ( javaMethod == null ) { throw new NoSuchMethodException(); }
			javaMethod.setAccessible(true);
			if ( javaMethodName.equals("++") ) {
				if ( argList != null && !EvalEnv.any.isAssignableFrom(argList[0].getClass()) ) {
					// a Java argument
					argList[0] = new cyan.lang.CyString(argList[0].toString());
		            ret = javaMethod.invoke(receiver, argList);
				}
				else {
		            ret = javaMethod.invoke(receiver, argList);
				}
			}
			else {
				try {
					/**
					 * test whether the method invoked belongs to
					 *  Iterator
					 */
					if ( receiver instanceof  Iterator<?> ) {
						Iterator<?> it = (Iterator<?> ) receiver;
						switch ( javaMethodName ) {
						case "hasNext" :
							ret = it.hasNext();
							break;
						case "next" :
							ret = it.next();
							break;
						default:
				            ret = javaMethod.invoke(receiver, argList);
				            if ( javaMethod.getReturnType() == void.class ) {
				            	ret = _Nil.prototype;
				            }
						}
					}
					else {
						if ( receiver instanceof ArrayList<?> &&
								javaMethodName.equals("iterator") ) {
							ret = ((ArrayList<?> ) receiver).iterator();
						}
						else {
				            ret = javaMethod.invoke(receiver, argList);
				            if ( javaMethod.getReturnType() == void.class ) {
				            	ret = _Nil.prototype;
				            }

						}
					}
				}
				catch (IllegalAccessException
						| IllegalArgumentException
						e1) {
					if ( ee == null ) { throw e1; }
					try {

						Object []newObjArgList = ExprMessageSendWithKeywordsToExpr.cast(ee, argList, javaMethod.getParameterTypes());
						ret = javaMethod.invoke(receiver, newObjArgList);
			            if ( javaMethod.getReturnType() == void.class ) {
			            	ret = _Nil.prototype;
			            }
						return ret;
					}
					catch (IllegalAccessException
							| IllegalArgumentException
							e2) {
					}
				}


			}
		}
		catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			String s = receiver.getClass().getName();
			if ( s.charAt(0) == '_' ) { s = s.substring(1); }
			String typeList = "";
			if ( argList != null ) {
				int size = argList.length;
				for (Object obj : argList ) {
					typeList += obj.getClass().getCanonicalName();
					if ( --size > 0 ) {
						typeList += ", ";
					}
				}
			}
			if ( e instanceof InvocationTargetException ) {
				InvocationTargetException ite = (InvocationTargetException ) e;
	        	if ( ite.getTargetException() instanceof cyanruntime.ExceptionContainer__ ) {
	        		cyanruntime.ExceptionContainer__ ec = (cyanruntime.ExceptionContainer__ ) ite.getTargetException();
	        		if ( ec.elem instanceof cyan.lang._ExceptionStr ) {
	        			cyan.lang._ExceptionStr estr = (cyan.lang._ExceptionStr ) ec.elem;
	        			throw new meta.InterpretationErrorException("Method '" + selector +
	        					"' " + (typeList.length() != 0 ? "with parameters of types "+ typeList + " " : "") +
	        					" has thrown an exception whose message is '" + estr._message().s + "'",
	        					symForError);

	        		}
	        	}

			}
			else {
				throw new meta.InterpretationErrorException("Method '" + selector +
						"' " + (typeList.length() != 0 ? "with parameters of types "+ typeList + " " : "") +
						"was not found in the Cyan prototype or Java class '" + s +
						"'" +
						((e instanceof SecurityException) ?
						". Or there was a SecurityException exception, maybe because a statement tried to read from the standard input" : ""),
						symForError);
			}


		}
		return ret;
	}

	static public Object getField(Object t, String name) {

		try {
			final Field f = t.getClass().getField(name);
			return f.get(t);

		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new InterpretationErrorException("Probably an internal error in Statement::getField. "
					+ "Field '" + name + "' of object of class '" + t.getClass().getCanonicalName() +
					"' was not found", null);
		}
	}


	static public Method getMethod(Class<?> receiverClass, String name, int numParameters) throws NoSuchMethodException, SecurityException {
		for ( final Method m : receiverClass.getMethods() ) {
			String mName = m.getName();
			if ( mName.equals(name) && m.getParameterCount() == numParameters ) {
				return m;
			}
		}
		return null;
	}

	public static Object castJavaBasicToCyan(Object obj) {
		Class<?> objClass = obj.getClass();
		if ( !EvalEnv.any.isAssignableFrom(objClass) ) {
			String className = objClass.getName();
			switch ( className ) {
			case "java.lang.Byte" :
				return new cyan.lang.CyByte( ((Byte) obj).intValue() );
			case "java.lang.Short" :
				return new cyan.lang.CyShort( ((Short) obj).intValue() );
			case "java.lang.Integer" :
				return new cyan.lang.CyInt( ((Integer) obj).intValue() );
			case "java.lang.Long" :
				return new cyan.lang.CyLong( ((Long ) obj).longValue() );
			case "java.lang.Float" :
				return new cyan.lang.CyFloat( ((Float ) obj).floatValue());
			case "java.lang.Double" :
				return new cyan.lang.CyDouble( ((Double) obj).doubleValue() );
			case "java.lang.Character" :
				return new cyan.lang.CyChar( ((Character ) obj).charValue() );
			case "java.lang.Boolean" :
				return new cyan.lang.CyBoolean( ((Boolean ) obj).booleanValue() );
			case "java.lang.String" :
				return new cyan.lang.CyString( (String) obj );
			}
		}
		return obj;
	}

	/** if rightValue is a Java object of a basic type (wrapper class included) and leftSideClass is in Cyan,
	 *  return a Cyan object with the value corresponding to the Java object. If rightValue is a basic
	 *  Cyan value and leftSideClass is a Java class, converts the Cyan value to Java.
	   @param ee
	   @param leftSide
	   @param rightValue
	   @return
	 */
	public static Object castCyanJava(EvalEnv ee, Class<?> leftSideClass, Object rightValue) {
		if ( ! EvalEnv.any.isAssignableFrom(leftSideClass) ) {
			// left side type is a Java type
			if ( EvalEnv.any.isAssignableFrom(rightValue.getClass()) ) {
				// right side is a Cyan value

				//  JavaVariable = CyanValue
				final String fullName = rightValue.getClass().getName();
				final int lastDot = fullName.lastIndexOf('.');
				String className;
				if ( lastDot > 0 ) {
					className = fullName.substring(lastDot+1);
				}
				else {
					className = fullName;
				}
				switch ( className ) {
				case "CyBoolean":
					rightValue = getField(rightValue, "b");
					break;
				case "CyChar":
					rightValue = getField(rightValue, "c");
					break;
				case "CyByte":
					rightValue = getField(rightValue, "n");
					break;
				case "CyInt":
					rightValue = getField(rightValue, "n");
					break;
				case "CyShort":
					rightValue = getField(rightValue, "n");
					break;
				case "CyLong":
					rightValue = getField(rightValue, "n");
					break;
				case "CyFloat":
					rightValue = getField(rightValue, "n");
					break;
				case "CyDouble":
					rightValue = getField(rightValue, "n");
					break;
				case "CyString":
					rightValue = getField(rightValue, "s");
					break;
				}

			}
		}
		else
			// left side type is a Cyan type
			if ( !EvalEnv.any.isAssignableFrom(rightValue.getClass()) ) { // cyan.lang._Any
				// right side is a Java value

				//  CyanVariable = JavaValue
				final String fullName = rightValue.getClass().getName();
				final int lastDot = fullName.lastIndexOf('.');
				String className;
				if ( lastDot > 0 ) {
					className = fullName.substring(lastDot+1);
				}
				else {
					className = fullName;
				}
				switch ( className ) {
				case "Boolean": case "boolean":
					//rightValue = new cyan.lang.CyBoolean( (Boolean ) rightValue);
					rightValue = ee.newCyBoolean( (boolean ) rightValue );

					break;
				case "Character":
					//rightValue = new cyan.lang.CyChar( (Character ) rightValue);
					rightValue = ee.newCyChar( (char ) rightValue );
					break;
				case "Byte":
					// rightValue = new cyan.lang.CyByte( (Byte ) rightValue);
					rightValue = ee.newCyByte( (byte) rightValue );
					break;
				case "Integer":
					// rightValue = new cyan.lang.CyInt( (Integer ) rightValue);
					rightValue = ee.newCyInt( (int ) rightValue );
					break;
				case "Short":
					// rightValue = new cyan.lang.CyShort( (Short ) rightValue);
					rightValue = ee.newCyShort( (short ) rightValue );
					break;
				case "Long":
					// rightValue = new cyan.lang.CyLong( (Long ) rightValue);
					rightValue = ee.newCyLong( (long ) rightValue );
					break;
				case "Float":
					// rightValue = new cyan.lang.CyFloat( (Float ) rightValue);
					rightValue = ee.newCyFloat( (float ) rightValue );
					break;
				case "Double":
					// rightValue = new cyan.lang.CyDouble( (Double ) rightValue);
					rightValue = ee.newCyDouble( (double ) rightValue );
					break;
				case "String":
					// rightValue = new cyan.lang.CyString( (String ) rightValue);
					rightValue = ee.newCyString( (String ) rightValue );
					break;
				}

			}
		return rightValue;
	}

	/**
	 * if this expression is used as a statement, the user should be warned that
	 * this may be an error.
	   @return
	 */
	public boolean warnIfStatement() {
		return false;
	}




	public MethodDec getCurrentMethod() {
		return currentMethod;
	}




	/**
	 * the symbol that follows the expression
	 */
	protected Symbol nextSymbol;


	/**
	 * last symbol of the statement
	 */
	private Symbol lastSymbol;

	/**
	 * symbol that follows the statement. It may be ';'.
	 */
	private Symbol symbolAfter;

	/**
	 * used of live variable analysis. See https://www.cs.cornell.edu/courses/cs4120/2011fa/lectures/lec21-fa11.pdf
	 */
	public Set<String> inLiveAnalysis, outLiveAnalysis, useLiveAnalysis, defLiveAnalysis;

	/**
	 * true if this statement should have a semicolon after it
	 */
	protected boolean shouldBeFollowedBySemicolon;

	/**
	 * metaobject annotation that follows this statement
	 */
	private AnnotationAt afterAnnotation;

	/**
	 * true if this statement was produced by a metaobject annotation.
	 * That is, it is inside a contextPush and contextPop
	 */
	private boolean createdByMetaobjects;

	/**
	 * the code that should replace this expression. Some metaobject or the compiler itself changed the
	 * expression by this code
	 */
	StringBuffer codeThatReplacesThisStatement;


	/**
	 * this statement may have been replaced by another one in phase SEM_AN. This
	 * variable keeps the metaobject annotation that asked for this replacement, if non-null.
	 * Then if this variable is not null, {@link wasReplacedByExpr} is true. But
	 * {@link wasReplacedByExpr} may be true and {@link cyanAnnotationThatReplacedStatByAnotherOne}
	 * null because the compiler itself may have replaced the statement by another statement.
	 * This does not happens currently.
	 */
	protected Annotation cyanAnnotationThatReplacedStatByAnotherOne;

	/**
	 * method in which the statement is
	 */
	protected MethodDec currentMethod;

}
