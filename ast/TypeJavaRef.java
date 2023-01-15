package ast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLClassLoader;
import lexer.Symbol;
import saci.Env;
import saci.NameServer;

/**
 * This class represents a Java class or interface which may be:
 * 1. a class, represented by objects of TypeJavaClass
 * 2. an interface, represented by objects of TypeJavaInterface
 *
 * Of course, TypeJavaRef is a superclass of both TypeJavaClass
 * and TypeJavaInterface
 *  @author José
 *
 */
public abstract class TypeJavaRef extends TypeJava {

	public TypeJavaRef(String name, JVMPackage jvmPackage) {
		this.name = name;
		this.jvmPackage = jvmPackage;
	}



	@Override
	public String getFullName() {
		return getPackageName() + "." + getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPackageName() {
		return this.jvmPackage.getPackageName();
	}


	/**
	 * The source code being compiled has a message send
	 *        obj methodName: e1, e2, ... en
	 * If the declared type of obj is a Java class represented by
	 * aJavaClass, the call
	 *      aJavaClass.checkMethod(methodName, e1, e2, ... en)
	 * returns true if the Java class represented by aJavaClass
	 * declares a method called methodName that accepts as parameters
	 * objects e1, e2, ... en. Of course, the declared type of ei
	 * should either be a Java class or should be a basic type such
	 * as Int, Char, Float, etc. Method checkMethod assumes that
	 * a basic Java type such as int is compatible with the Cyan type
	 * Int. A future improvement in Cyan will be to allow the passing
	 * of Cyan objects to Java code. That will not take long to happen.
	 *
	 *
	 * @param methodName, the name of the method
	 * @param realParameterList, the parameter types of the
	 * @return
	 */
	abstract public boolean checkMethod(String methodName, Expr ... realParameterList);



	@Override
	public String getJavaPackage() {
		return getJavaClass().getPackage().getName();
	}

	/**
	 * return true if this is a supertype of otherClass.
	 */

	@Override
	abstract public boolean isSupertypeOf(Type otherClass, Env env);


	/**
	 * two objects of TypeJavaRef are equal, that is, two classes or interfaces
	 * are considered equal if the have the same name and belong to
	 * the same package.
	 */
	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof TypeJavaRef) )
			return false;
		else {
			TypeJavaRef other = (TypeJavaRef ) obj;
			return aClass.getName().compareTo(other.getName()) == 0 &&
			       aClass.getPackage().getName().compareTo(
			    		   other.getClass().getPackage().getName()) == 0;
		}
	}

	public Class<?> getJavaClass() {
		if ( aClass == null ) {
			try {
				this.loadJavaClass();
			}
			catch (ClassNotFoundException | IOException e) {
				return null;
			}
		}
		return aClass;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public Class<?> getClassLoad(Env env, Symbol symForError) {
		if ( aClass == null ) {
			try {
				String msg = loadJavaClass();
				if ( msg != null ) {
					env.error(symForError, msg);
				}
			}
			catch (ClassNotFoundException | IOException | NoClassDefFoundError e) {
				env.error(symForError, "Error when reading Java class '" + this.getFullName() + "' from "
						+ this.jvmPackage.getUrls()[0]);
			}
		}
		return aClass;
	}

	public Class<?> getTheClass() {
		return aClass;
	}

	public void setaClass(Class<?> aClass) {
		this.aClass = aClass;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JVMPackage getJvmPackage() {
		return jvmPackage;
	}

	public String loadJavaClass() throws IOException, ClassNotFoundException  {

		URL[] urls = jvmPackage.getUrls();
		//URLClassLoader cl = URLClassLoader.newInstance(urls);
		URLClassLoader cl = new URLClassLoader(urls);

		String packagePath = jvmPackage.getPackageName() + '.';
		String aClassName = packagePath + name;
		try {
			aClass = cl.loadClass(aClassName);
		}
		catch (ClassNotFoundException | NoClassDefFoundError e) {
			return "Error when reading Java class '" + this.getFullName() + "' from "
					+ this.jvmPackage.getUrls()[0];
		}
		finally {
			cl.close();
		}
		return null;
	}





	public static boolean javaTypeIsSupertypeofCyanTypeByName(String javaName,
			String cyanName) {
		return javaName.equals(Character.toLowerCase(cyanName.charAt(0)) + cyanName.substring(1)) ||
				javaName.equals("Object")   ||
				javaName.equals("String") && cyanName.equals("String");
	}

	/*
	 * taken from https://stackoverflow.com/questions/13392160/about-java-get-string-class-from-string-class-what-if-string-class-is
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T[]> getArrayClass(Class<T> clazz) {
	    return (Class<? extends T[]>) Array.newInstance(clazz, 0).getClass();
	}

	public static Type classJavaToTypeJavaRef(Class<?> classJavaType, Env env, Symbol symInError) {

		String classJavaTypeName = classJavaType.getSimpleName();
		if ( NameServer.isJavaBasicType(classJavaTypeName )  ) {
			return env.getProject().getProgram().searchJavaBasicType(classJavaTypeName);
		}
		else {
			String rtPackageName = null;
			String rtClassName = null;
			//System.out.println(methodReturnType.getName());
			String retTypeCanonicalName = classJavaType.getCanonicalName();
			boolean isArray = false;
			if ( retTypeCanonicalName.endsWith("[]") ) {
				int lastIndexOfDot = retTypeCanonicalName.lastIndexOf('.');
				if ( lastIndexOfDot > 0 ) {
					rtPackageName = retTypeCanonicalName.substring(0, lastIndexOfDot);
				}
				else {
					   // should be int, char, long, etc.
					rtPackageName = "java.lang";
					/*env.error(this.getFirstSymbol(), "The Java class of the return type of this method call is '"
							+ methodReturnType.getName() + "'. Its package is '" + rtPackageName + "' which was not found"); */
				}
				rtClassName = retTypeCanonicalName.substring(lastIndexOfDot + 1, retTypeCanonicalName.length()-2);
				isArray = true;
			}
			else {
				rtPackageName = classJavaType.getPackage().getName();
				rtClassName = classJavaType.getSimpleName();
			}

			JVMPackage jvmPackage = env.getProject().getProgram().searchJVMPackage(rtPackageName);
			if ( jvmPackage == null ) {
				env.error(symInError, "The Java class of the return type of this method call is '"
						+ classJavaType.getName() + "'. Its package is '" + rtPackageName + "' which was not found");
				return null;
			}
			else {
				Type t = jvmPackage.getJvmTypeClassMap().get(rtClassName);
				if ( t == null ) {
					if ( isArray ) {
						if ( NameServer.isJavaBasicType(rtClassName) ) {
							TypeJavaRef elementType = env.getProject().getProgram().searchJavaBasicType(rtClassName);
							return new TypeJavaRefArray( elementType, 1);
						}
					}
					env.error(symInError, "The Java class of the return type of this method call is '"
							+ classJavaType.getName() + "'. It was not found in its package. Maybe the package was not imported");
					return null;
				}
				if ( isArray ) {
					return new TypeJavaRefArray( (TypeJavaRef ) t, 1);
				}
				return t;
			}
		}
	}


	public void setGPI(
			ExprGenericPrototypeInstantiation exprGenericPrototypeInstantiation) {
		this.exprGenericPrototypeInstantiation = exprGenericPrototypeInstantiation;
	}

	public ExprGenericPrototypeInstantiation getGPI() {
		return exprGenericPrototypeInstantiation;
	}

	/**
	 * the information on the class.
	 */
	protected Class<?> aClass;
	protected String name;
	protected JVMPackage jvmPackage;

	/**
	 * if this type is a generic prototype instantiation, this variable keeps its
	 * parameters.
	 */
	protected ExprGenericPrototypeInstantiation exprGenericPrototypeInstantiation;

}