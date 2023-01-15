/**
 *
 */
package ast;

import java.util.List;
import lexer.Symbol;
import meta.WrType;
import saci.Env;
import saci.NameServer;

/** Represents a type of a variable, parameter, return type, or expression.
 *  It may be a Cyan prototype or a Java class.
 *  The subclasses of Type represent basic types and objects. An object
 *  also represents a type since one can be used as the type of a
 *  variable, parameter, etc:
 *        var Person p; // Person is an object
 *  An instantiation of a generic object is also a type:
 *        var Stack<Person> s;
 *
 * @author José
 *
 */
abstract public class Type implements ASTNode {

	public static Type Byte = null;
	public static Type Short = null;
	public static Type Int = null;
	public static Type Long = null;
	public static Type Float = null;
	public static Type Double = null;
	public static Type Char = null;
	public static Type Boolean = null;
	public static Type String = null;
	public static Type Nil = null;
	public static Type Any = null;
	public static Prototype IMapName;
	public static Prototype ISetName;
	/**
	 * this is the type of an message send with hasBackquote like
	 *      person `str
	 * This type is compatible with any other type. The compiler should
	 * never issue an error when comparing this type with any other
	 */
	final public static Type Dyn = new TypeDynamic();

	/**
	 * returns the name of this type
	 */
	abstract public String getName();

	abstract public String getPackageName();


	public String getNameWithAttachedTypes() { return getName(); }

	/**
	 * returns the full name of this type, including its package
	 */
	abstract public String getFullName();

	/**
	 * returns the full name of this type, including its package. If it is
	 * a generic prototype, it includes the packages of each of the
	 * parameters.
	 */
	abstract public String getFullName(Env env);
	/**
	 * Returns the unique Java name associated to this type. An object
	 * Person should have Java name "_Person". But object Stack<Int>
	 * should have a Java  _Stack_LT_GP_CyInt_GT. See
	 * this method in subclasses of Type. If the type is already a Java class,
	 * this method returns the name of the class (of course).
	 * @return
	 */
	abstract public String getJavaName();

	/**
	 * returns the method signatures of this prototype with name methodName.
	 * The signatures of all methods found are returned.
	 * The searches includes private, public, and protected methods of
	 * this prototype and public and protected methods of super-prototypes.
	 * @param methodName
	 * @return
	 */
	abstract public List<MethodSignature> searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(String methodName, Env env);

	/**
	 * returns the method signatures of this prototype with name methodName.
	 * The signatures of all methods found are returned.
	 * The searches includes protected, public, and package methods of
	 * this prototype
	 * @param methodName
	 * @return
	 */
	abstract public List<MethodSignature> searchMethodProtectedPublicPackage(String methodName, Env env);


	/**
	 * returns the method signatures of this prototype with name methodName.
	 * The signatures of all protected methods found are returned.
	 * @param methodName
	 * @return
	 */
	abstract public List<MethodSignature> searchMethodProtected(String methodName, Env env);

	/**
	 * returns the method signatures of this prototype with name methodName.
	 * The signatures of all methods found are returned.
	 * The searches includes private, public, and protected methods of
	 * this prototype.
	 * @param methodName
	 * @return
	 */
	abstract public List<MethodSignature> searchMethodPrivateProtectedPublicPackage(String methodName, Env env);


	/**
	 * returns the method signatures of this type with name methodName.
	 * The searches includes only public and package methods of
	 * this type and public and package methods of super-types, if the
	 * method is not found in this type.
	 * @param methodName
	 * @return
	 */
	abstract public List<MethodSignature> searchMethodPublicPackageSuperPublicPackage(String methodName, Env env);


	/**
	 * returns the method signatures of this type with name methodName.
	 * The searches includes only public methods of
	 * this type.
	 * @param methodName
	 * @return
	 */
	abstract public List<MethodSignature> searchMethodPublicPackage(String methodName, Env env);


	/**
	 * searches for a method called methodName in this type and all its super-types.
	 * Public and protected methods are considered. The signatures of all methods with name "methodName"
	 * are returned.
	 *
	 * @param methodName
	 * @param env
	 * @return
	 */
	abstract public List<MethodSignature> searchMethodProtectedPublicPackageSuperProtectedPublicPackage(String methodName, Env env);

	abstract public boolean getIsFinal();

	/**
	 * return true if this type is supertype of 'other'
	 */
	abstract public boolean isSupertypeOf(Type other, Env env);

	/**
	 * return the type as an expr which may be ExprIdentStar or ExprGenericPrototypeInstantiation.
	 *  <code>seed</code> is used to create new Symbols with the same line number as <code>seed</code>
	 */
	public Expr asExpr(Symbol sym) {
		return NameServer.stringToExprIdentStar(this.getFullName(), sym);
	}

	/*
	public boolean isSupertypeOf(WrType other, Env env) {
		if ( !(other instanceof Type) ) {
			env.error(null, "Internal error: found an WrType that is not instance of Type", true, true);
			return false;
		}
		else {
			Type otherType = (Type ) other;
			return this.isSupertypeOf(otherType, env);
		}
	}
	*/

	/**
	 * return true if the receiver is an inner prototype of Cyan. Such prototypes are created for anonymous functions of the outer prototype
	 */
	public boolean isInnerPrototype() { return false; }

	/**
	 *  rightTmpVar is the name of a variable in the generated Java code that will keep, at runtime, the right-hand
	 *  side value of an assignment. Its type is rightType. The value of rightTmpVar at runtime should be cast
	 *  to the type of leftType. It is known that leftType is supertype of rightType considering the equivalence of
	 *  types in Cyan and Java. That is, leftType may be Any or Int (Cyan) and rightType may be int or Integer (Java).
	 *  Or leftType may be Object or Integer (Java) and rightType may be Any or Int (Cyan). Or leftType may be
	 *  int[] or Integer[] (Java) and rightType may be Array<Int> (Cyan). It may be vice-versa too.
	 *
	 *  This method is responsible for issuing errors if the conversion is not possible.
	 *
	 *  This method generates code to convert the value of rightTmpVar at runtime to the correct type.
	   @param env
	   @param rightTmpVar
	   @param rightType
	   @param leftType
	   @return
	 */
	public static String genJavaExpr_CastJavaCyan(Env env, String rightTmpVar, Type rightType, Type leftType, Symbol symForErrorMessage) {

		Type leftInsideType = leftType.getInsideType();
		Type rightInsideType = rightType.getInsideType();
		if ( leftInsideType instanceof TypeJavaRef && rightInsideType instanceof TypeJavaRef ||
				leftInsideType instanceof Prototype && rightInsideType instanceof Prototype  ||
				leftInsideType instanceof TypeUnion && rightInsideType instanceof Prototype  ||
				leftInsideType instanceof TypeUnion && rightInsideType instanceof TypeUnion  ||
				leftInsideType instanceof TypeIntersection && rightInsideType instanceof Prototype  ||
				leftInsideType instanceof TypeIntersection && rightInsideType instanceof TypeIntersection) {
			return rightTmpVar;
		}
		else if ( leftInsideType instanceof TypeJavaRef && rightInsideType instanceof Prototype ) {
			/*
			 * assignment of the type
			 *     Java = Cyan
			 */
			String cyanTypeName = rightType.getName();
			String javaTypeName = leftType.getName();
			String cyanNameFromWrapper = null;
   			if ( javaTypeName.equals("Object") ) {
				// any convertion is valid
				return rightTmpVar;
			}
			else if ( javaTypeName.equals(Character.toLowerCase(cyanTypeName.charAt(0)) + cyanTypeName.substring(1)) ||
					javaTypeName.equals("String") && cyanTypeName.equals("String") ||
					(cyanNameFromWrapper = NameServer.javaWrapperClassToCyanName(javaTypeName)) != null &&
					cyanNameFromWrapper.equals(cyanTypeName) ) {

				  // something like  "int = Int" or "Integer = Int"

				rightTmpVar = rightTmpVar + "." + NameServer.getFieldBasicType(cyanTypeName);
			}
			else {
				env.error(symForErrorMessage, "Cannot cast this Cyan value of type '" + rightType.getFullName() + "' to "
						+ "Java, type '" + leftType.getFullName() + "'");
			}
        }
        else if ( rightInsideType instanceof TypeJavaRef && leftInsideType instanceof Prototype ) {
			/*
			 * assignment of the type
			 *     Cyan = Java
			 */

			String cyanTypeName = leftType.getName();
			String javaTypeName = rightType.getName();
			String cyanNameFromWrapper = null;

			if ( javaTypeName.equals(Character.toLowerCase(cyanTypeName.charAt(0)) + cyanTypeName.substring(1)) ||
					javaTypeName.equals("String") && cyanTypeName.equals("String") ||
					// NameServer.javaWrapperClassToCyanName(javaTypeName).equals(cyanTypeName)
					(cyanNameFromWrapper = NameServer.javaWrapperClassToCyanName(javaTypeName)) != null &&
					cyanNameFromWrapper.equals(cyanTypeName)
					) {

				  // something like  "int = Int" or "Integer = Int"

				rightTmpVar = "(new Cy" + cyanTypeName + "(" + rightTmpVar + "))";
			}
			else if ( cyanTypeName.equals("Any") && (cyanNameFromWrapper = NameServer.javaWrapperClassToCyanName(javaTypeName)) != null ) {
				rightTmpVar = "(new Cy" + cyanNameFromWrapper + "(" + rightTmpVar + "))";
			}
			else {
       			env.error(symForErrorMessage, "Cannot cast a Java value of type '" + rightType.getFullName() +
       					"' to the Cyan type '" + leftType.getFullName() + "'");
			}

        }
        else if ( rightType instanceof TypeJavaRef && leftType == Type.Dyn ) {
			String javaTypeName = rightType.getName();
			String cyanTypeName = NameServer.cyanNameFromJavaBasicType(javaTypeName);
			if (  cyanTypeName != null ) {
				rightTmpVar = "(new " + cyanTypeName + "(" + rightTmpVar + "))";
			}
			else if ( javaTypeName.equals("Object") ) {
				return rightTmpVar;
			}
			else {
       			env.error(symForErrorMessage, "Cannot cast a Java value of type '" + rightType.getFullName() +
       					"' to the Cyan type '" + leftType.getFullName() + "'");
			}
        }
        else if ( leftInsideType instanceof Prototype && rightInsideType == Type.Dyn ) {
        	return rightTmpVar;
        }
        else if ( rightType instanceof TypeUnion && ! (leftType instanceof TypeUnion) ) {
        	rightTmpVar = "(" + leftType.getJavaName() + " ) " + rightTmpVar;
        }
        else if ( rightType instanceof TypeIntersection && ! (leftType instanceof TypeIntersection) ) {
        	rightTmpVar = "(" + leftType.getJavaName() + " ) " + rightTmpVar;
        }
        else if ( leftType != Type.Dyn && rightType != Type.Dyn ) {
        	env.error(symForErrorMessage, "Error: unknown types '" + leftType.getFullName() + "' and '" + rightType.getFullName() + "'");
        }


		return rightTmpVar;
	}


	public Type getInsideType() {
		return this;
	}


	@Override
	abstract public WrType getI();


}
