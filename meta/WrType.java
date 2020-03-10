package meta;

import java.util.List;
import ast.InterfaceDec;
import ast.ObjectDec;
import ast.Type;

abstract public class WrType extends WrASTNode {

	public WrType(Type hidden) {
		this.hidden = hidden;
	}


	public static WrType Byte = null;
	public static WrType Short = null;
	public static WrType Int = null;
	public static WrType Long = null;
	public static WrType Float = null;
	public static WrType Double = null;
	public static WrType Char = null;
	public static WrType Boolean = null;
	public static WrType String = null;
	public static WrType Nil = null;
	public static WrType Any = null;
	public static WrType Dyn = null;

	abstract public String getFullName();
	abstract public String getName();
	final public String getPackageName() {
		return hidden.getPackageName();
	}

	abstract public boolean isSupertypeOf(WrType other, WrEnv env);
	abstract public WrType getInsideType();

	@SuppressWarnings("unused")
	public List<WrMethodSignature> searchMethodPublicPackageSuperPublicPackage(
			String methodName, WrEnv env) {
		return null;
	}

	@Override
	Type getHidden() {
		return hidden;
	}

	public boolean isInterface() { return hidden instanceof InterfaceDec; }
	public boolean isObjectDec() { return hidden instanceof ObjectDec; }

	Type hidden;

	public String getNameWithAttachedTypes() {
		return hidden.getNameWithAttachedTypes();
	}
	public String getJavaName() {
		return hidden.getJavaName();
	}


}
