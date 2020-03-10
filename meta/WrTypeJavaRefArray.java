package meta;

import ast.TypeJavaRefArray;

public class WrTypeJavaRefArray extends WrType {




	public WrTypeJavaRefArray(TypeJavaRefArray hidden) {
		super(hidden);
	}

	@Override
	public String getName() {
		return hidden.getName();
	}

	@Override
	public String getFullName() {
		return hidden.getFullName();
	}

	@Override
	public boolean isSupertypeOf(WrType other, WrEnv env) {
		return ((TypeJavaRefArray ) hidden).isSupertypeOf(other.hidden, env.hidden);
	}

	@Override
	public WrType getInsideType() {
		return hidden.getInsideType().getI();
	}


}
