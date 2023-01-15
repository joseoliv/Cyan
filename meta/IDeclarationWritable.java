package meta;

/**
 * represent declarations whose documentation, examples, and features can be written.
 * It can be a declaration inside a compilation unit as prototypes, methods, fields,
 * local variables, and parameters. Or the program and packages during the parsing of
 * the project file.
   @author jose
 */
public interface IDeclarationWritable extends IDeclaration {



	/**
	 * add text for documenting this declaration. The text is in parameter
	 * {@code doc} and {@code docKind} is the kind of the {@code doc}, which
	 * may be {@code "text"}, {@code HTML}, {@code markdown}, etc. The
	 * allowed kinds are purposely left unspecified.
	   @param doc
	 */
	void addDocumentText(String doc, String docKind, WrEnv env);
	/**
	 * add an {@code example} for documenting this declaration.
	 * Its kind is {@coce exampleKind} which may be {@ Cyan},
	 * {@code methodOnly}, {@code prototype}, etc. The
	 * allowed kinds are purposely left unspecified.
	   @param doc
	   @param docType
	 */
	void addDocumentExample(String example, String exampleKind, WrEnv env);
	void addFeature(Tuple2<String, WrExprAnyLiteral> feature, WrEnv env);

    /**
     * information should not be got during parsing and afterSemAn
     */
	default void checkGetInfo(WrEnv env) {
		CompilationStep step = env.getCompilationStep();
    	if ( step == CompilationStep.step_1 ||
          		 step == CompilationStep.step_4 ||
          		 step == CompilationStep.step_7 ||
          		 step == CompilationStep.step_9
       		) {
    		throw new MetaSecurityException();
    	}
	}


}
