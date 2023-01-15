package meta;

public enum VariableKind { 
	COPY_VAR("%"),                       // default or with  % as in   object DoNotSum(:s %Int) ... end 
	LOCAL_VARIABLE_REF("&");             // as if by reference as in   object Sum(:sum &Int) ... end
	
	private VariableKind(String sym) {
		this.sym = sym;
	}
	private String sym;
	
	@Override public String toString() {
		return sym;
	}

	
}