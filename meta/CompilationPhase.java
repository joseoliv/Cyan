package meta;

/**
 * compiler phases. Phases resTypes  and code generation are not represented because
 * user metaobjects cannot act in them. Phases afterResTypes2 and bsa will be deprecated
 * and are not represented either.
   @author jose
 */
public enum CompilationPhase {
	parsing, afterResTypes, semAn, afterSemAn
}
