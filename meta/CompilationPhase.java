package meta;

/**
 * compiler phases. Phases ti  and code generation are not represented because
 * user metaobjects cannot act in them. Phases afti2 and bsa will be deprecated
 * and are not represented either.
   @author jose
 */
public enum CompilationPhase {
	dpa, afti, dsa, afsa
}
