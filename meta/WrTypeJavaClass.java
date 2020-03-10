package meta;

import ast.TypeJavaClass;

public class WrTypeJavaClass extends WrType {


	public WrTypeJavaClass(TypeJavaClass hidden) {
		super(hidden);
	}

	@Override
	public String getName() {
		return ((TypeJavaClass ) hidden).getName();
	}


	@Override
	public String getFullName() {
		return ((TypeJavaClass ) hidden).getFullName();
	}

	@Override
	public boolean isSupertypeOf(WrType other, WrEnv env) {
		return ((TypeJavaClass ) hidden).isSupertypeOf(other.hidden, env.hidden);
	}

	@Override
	public WrType getInsideType() {
		return ((TypeJavaClass ) hidden).getInsideType().getI();
	}


}
