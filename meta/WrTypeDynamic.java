package meta;

import ast.TypeDynamic;

public class WrTypeDynamic extends WrType {

	public WrTypeDynamic(TypeDynamic hidden) {
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
		return ((TypeDynamic ) hidden).isSupertypeOf(other.hidden, env.hidden);
	}

	@Override
	public WrType getInsideType() {
		return hidden.getInsideType().getI();
	}



}
