package meta;

public enum AttachedDeclarationKind {
	PROGRAM_DEC("program"),
	PACKAGE_DEC("package"),
	PROTOTYPE_DEC("prototype"),
	METHOD_DEC("method"),
	METHOD_SIGNATURE_DEC("method signature"),
	FIELD_DEC("field"),
	LOCAL_VAR_DEC("local variable"),
	EXPR("expression"),
	STATEMENT_DEC("statement"),
	NONE_DEC("none"),
	JAVA_TYPE("java type") ;

	AttachedDeclarationKind(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	private String name;
}

