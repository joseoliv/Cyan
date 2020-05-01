package ast;

import java.util.List;
import org.apache.commons.lang3.ClassUtils;
import meta.WrType;
import saci.Env;
import saci.NameServer;

public class TypeJavaNull extends TypeJavaRef {

	public TypeJavaNull(String className, JVMPackage jvmPackage) {
		super(className, jvmPackage);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}


	@Override
	public boolean checkMethod(String methodName,  Expr ... realParameterList) {
		return false;
	}

	/**
	 * Return true if this is a supertype of otherClass
	 * Comment: only classes can be supertypes of classes
	 *
	 * @param other
	 * @return
	 */
	@Override
	public boolean isSupertypeOf(Type other, Env env) {
		if ( aClass == null ) {
			getClassLoad(env, null);
		}

		if ( other instanceof TypeJavaClass ) {
			((TypeJavaClass) other).getClassLoad(env, null);
//			assert ClassUtils.isAssignable(Integer.class, int.class);
//			assert ClassUtils.isAssignable(int.class, Integer.class);
//			assert ClassUtils.isAssignable(Integer.class, float.class);
//			assert ClassUtils.isAssignable(float.class, Integer.class);
//			assert ClassUtils.isAssignable(Object.class, String.getClass());
//			assert ClassUtils.isAssignable(String.class, Object.class);
			return ClassUtils.isAssignable(((TypeJavaClass ) other).aClass, aClass);
//			if ( aClass.isAssignableFrom(((TypeJavaClass ) other).aClass) ) {
//				return true;
//			}
//			else {
//
//				Class<?> basicClass = NameServer.wrapperToBasicClass(this.getName());
//				if (  basicClass != null && basicClass == ((TypeJavaClass) other).getJavaClass() ) {
//					return true;
//				}
//				else {
//					Class<?> wrapperClass = NameServer.javaPrimitiveTypeToWrapperClass(this.getName());
//					return wrapperClass != null && wrapperClass == ((TypeJavaClass) other).getJavaClass();
//				}
//			}
			/*
			Class<?> c = ((TypeJavaClass ) other).aClass;
			while ( c != null ) {
				if ( c.equals(aClass) )
					return true;
				c = c.getSuperclass();
			}
			*/
		}
		else if ( this.getFullName().equals("java.lang.Object") ) {
			return true;
		}
		else if ( other.getInsideType() instanceof Prototype ) {
			/*
			 * assignment of the type
			 *     Java = Cyan
			 */
			Prototype cyanType = (Prototype) other;
			String cyanTypeName = cyanType.getName();
			String javaTypeName = getName();
			String cyanNameFromWrapper = null;

   			return javaTypeName.equals(Character.toLowerCase(cyanTypeName.charAt(0)) + cyanTypeName.substring(1)) ||
					javaTypeName.equals("String") && javaTypeName.equals("String") ||
					//NameServer.javaWrapperClassToCyanName(javaTypeName).equals(cyanTypeName) );
					(cyanNameFromWrapper = NameServer.javaWrapperClassToCyanName(javaTypeName)) != null &&
					cyanNameFromWrapper.equals(cyanTypeName);

		}
		else if ( other == Type.Dyn ) {
			return true;
		}

		return false;

	}


	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		return null;
	}


	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackage(String methodName, Env env) {
		return null;
	}

	@Override
	public List<MethodSignature> searchMethodPrivateProtectedPublicPackage(String methodName, Env env) {
		return null;
	}


	@Override
	public List<MethodSignature> searchMethodPublicPackageSuperPublicPackage(
			java.lang.String methodName, Env env) {
		return null;
	}

	@Override
	public List<MethodSignature> searchMethodPublicPackage(String methodName, Env env) {
		return null;
	}


	@Override
	public List<MethodSignature> searchMethodProtectedPublicPackageSuperProtectedPublicPackage(
			java.lang.String methodName, Env env) {
		return null;
	}


	@Override
	public boolean getIsFinal() {
		return false;
	}


	@Override
	public java.lang.String getFullName(Env env) {
		return this.getFullName();
	}

	@Override
	public String getPackageName() {
		return "java.lang";
	}


	@Override
	public WrType getI() {
		return null;
	}
}
