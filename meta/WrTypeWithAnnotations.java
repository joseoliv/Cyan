package meta;

import java.util.List;
import ast.AnnotationAt;
import ast.TypeWithAnnotations;

public class WrTypeWithAnnotations extends WrType {

	public WrTypeWithAnnotations(TypeWithAnnotations hidden) {
        super(hidden);
    }

	@Override
	public String getFullName() {
		return hidden.getFullName();
	}

	@Override
	public boolean isSupertypeOf(WrType other, WrEnv env) {
		return hidden.isSupertypeOf(other.hidden, env.hidden);
	}

	@Override
	public WrType getInsideType() {
		return hidden.getInsideType().getI();
	}

	@Override
	public String getName() {
		return hidden.getName();
	}


	public List<AnnotationAt> getAnnotationToTypeList() {
		return ((TypeWithAnnotations ) hidden).getAnnotationToTypeList();
	}

}
