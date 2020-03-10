package meta;
/**
 * represents the kind of a qualified identifier such as "count",  "main.Program", "Program", "factorial"
   @author jose
 */
public enum IdentStarKind {
		variable_t,
		instance_variable_t,
		unaryMethod_t,
		prototype_t,
		jvmClass_t,
		  /*
		   * inside the Cyan interpreter, eval(EvalEnv) methods, this
		   * represents injected variables in the Cyan code being interpreted
		   */
		injectedVariable_t
}
