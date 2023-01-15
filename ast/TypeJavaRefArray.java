package ast;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import meta.WrType;
import meta.WrTypeJavaRefArray;
import saci.Env;

public class TypeJavaRefArray extends TypeJavaRef {

	public TypeJavaRefArray(TypeJavaRef exprJavaArrayType, int numDimensions ) {
		super(exprJavaArrayType.getName() + "[]",
				exprJavaArrayType.getJvmPackage());
		this.elementType = exprJavaArrayType;
		this.numDimensions = numDimensions;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}


	@Override
	public boolean checkMethod(String methodName,  Expr ... realParameterList) {
		return false;
	}

	@Override
	public boolean isSupertypeOf(Type otherClass, Env env) {
		if ( !(otherClass instanceof TypeJavaRefArray) ) {
			return false;
		}
		TypeJavaRefArray other = (TypeJavaRefArray ) otherClass;
		return this.elementType == other.elementType &&
				this.getNumDimensions() == other.getNumDimensions();
	}

	@Override
	public java.lang.String getFullName(Env env) {
		return elementType.getJavaName() + "[]";
	}

	@Override
	public java.lang.String getFullName() {
		return elementType.getJavaName() + "[]";
	}

	@Override
	public String getPackageName() {
		return "java.lang";
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
	public List<MethodSignature> searchMethodProtected(String methodName, Env env) {
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
		return true;
	}

	@Override
	public String loadJavaClass() throws IOException, ClassNotFoundException  {

		URL[] urls = jvmPackage.getUrls();
		URLClassLoader cl = URLClassLoader.newInstance(urls);
		String msg = null;
		if ( elementType.getTheClass() == null ) {
			msg = this.elementType.loadJavaClass();
		}
		else {
			aClass = TypeJavaRef.getArrayClass(this.elementType.getTheClass());
		}
		cl.close();
		return msg;
	}

	@Override
	public WrType getI() {
		if ( iTypeJavaRefArray == null ) {
			iTypeJavaRefArray = new WrTypeJavaRefArray(this);
		}
		return iTypeJavaRefArray;
	}

	private WrTypeJavaRefArray iTypeJavaRefArray = null;

	private TypeJavaRef elementType;

	public Type getElementType() {
		return elementType;
	}
	public int getNumDimensions() {
		return numDimensions;
	}

	private int numDimensions;


}
