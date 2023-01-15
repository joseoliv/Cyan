/**

 */
package ast;

import java.util.List;
import meta.MetaHelper;
import meta.WrType;
import saci.Env;
import saci.NameServer;

/**
 * 	This is the type 'Dyn'. And also this is the type of an message send with hasBackquote like
	 *      person `str
	 * This type is compatible with any other type. The compiler should
	 * never issue an error when comparing this type with any other
   @author José

 */
public class TypeDynamic extends Type {

	public TypeDynamic() {
	}

	@Override
	public java.lang.String getName() {
		return MetaHelper.dynName;
	}

	@Override
	public String getPackageName() {
		return NameServer.cyanLanguagePackageName;
	}

	@Override
	public java.lang.String getJavaName() {
		return NameServer.javaDynName;
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
	public boolean isSupertypeOf(Type other, Env env) {
		return true;
	}

	@Override
	public java.lang.String getFullName() {
		return MetaHelper.dynName;
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
	public String getFullName(Env env) {
		return getFullName();
	}


	@Override
	public WrType getI() {
		return WrType.Dyn;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}


}
