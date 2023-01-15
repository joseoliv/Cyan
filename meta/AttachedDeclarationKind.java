package meta;

public enum AttachedDeclarationKind {
	PROGRAM_DEC("PROGRAM"),
	PACKAGE_DEC("PACKAGE"),
	PROTOTYPE_DEC("PROTOTYPE"),
	METHOD_DEC("METHOD"),
	METHOD_SIGNATURE_DEC("METHOD SIGNATURE"),
	FIELD_DEC("FIELD"),
	LOCAL_VAR_DEC("LOCAL VARIABLE"),
	EXPR("EXPR"),
	STATEMENT_DEC("STATEMENT"),
	NONE_DEC("none"),
	JAVA_TYPE("JAVA TYPE"),
	/*
	 * use this only for annotations attached to types used in declarations
	 * of local variables, parameters, fields, and return value. Example:<br>
	 * <code>    var Int@range(1, 12) month; </code>
	 */
	TYPE("TYPE");

	AttachedDeclarationKind(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	private String name;
}

