package meta;

import ast.Prototype;
import ast.Type;
import ast.TypeIntersection;

public class WrTypeIntersection extends WrType {

	public WrTypeIntersection(TypeIntersection hidden) {
        super(hidden);
        hidden.setiTypeIntersection(this);
	}

    @Override
    TypeIntersection getHidden() { return (TypeIntersection ) hidden; }


	@Override
	public java.lang.String getFullName() {
		return ((TypeIntersection ) hidden).getFullName();
	}

	@Override
	public java.lang.String getName() {
		return ((TypeIntersection ) hidden).getName();
	}

	@Override
	public boolean isSupertypeOf(WrType other, WrEnv env) {

    	addThisAsDependenteToCurrentPrototype(env);
		return ((TypeIntersection ) hidden).isSupertypeOf(other.hidden, env.hidden);


	}

	public void addThisAsDependenteToCurrentPrototype(WrEnv env) {
		if ( env.getCurrentPrototype() != null ) {
			Type cpu = env.getCurrentPrototype().hidden;
			if ( cpu instanceof Prototype ) {
				Prototype pu = (Prototype ) cpu;
				for ( Type elemIntersection : this.getHidden().getTypeList() ) {
					if ( pu != this.hidden ) {
						if ( elemIntersection instanceof Prototype ) {
							pu.addDependentPrototype( (Prototype ) elemIntersection );
						}
					}
				}
			}
		}
	}



	@Override
	public WrType getInsideType() {
		return this;
	}

}
