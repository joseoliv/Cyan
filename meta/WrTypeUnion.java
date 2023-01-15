package meta;

import ast.Prototype;
import ast.Type;
import ast.TypeUnion;

public class WrTypeUnion extends WrType {

	public WrTypeUnion(TypeUnion hidden) {
        super(hidden);
        hidden.setiTypeUnion(this);
	}

    @Override
    TypeUnion getHidden() { return (TypeUnion ) hidden; }


	@Override
	public java.lang.String getFullName() {
		return ((TypeUnion ) hidden).getFullName();
	}

	@Override
	public java.lang.String getName() {
		return ((TypeUnion ) hidden).getName();
	}

	@Override
	public boolean isSupertypeOf(WrType other, WrEnv env) {

    	addThisAsDependenteToCurrentPrototype(env);
		return ((TypeUnion ) hidden).isSupertypeOf(other.hidden, env.hidden);


	}

	public void addThisAsDependenteToCurrentPrototype(WrEnv env) {
		if ( env.getCurrentPrototype() != null ) {
			Type cpu = env.getCurrentPrototype().hidden;
			if ( cpu instanceof Prototype ) {
				Prototype pu = (Prototype ) cpu;
				for ( Type elemUnion : this.getHidden().getTypeList() ) {
					if ( pu != this.hidden ) {
						if ( elemUnion instanceof Prototype ) {
							pu.addDependentPrototype( (Prototype ) elemUnion );
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
