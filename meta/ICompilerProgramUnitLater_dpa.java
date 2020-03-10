package meta;

public interface ICompilerProgramUnitLater_dpa extends IAbstractCyanCompiler {

	WrProgramUnit getProgramUnit();
	void error(WrSymbol symbol, String message);
	void errorAtGenericPrototypeInstantiation(String errorMessage);
	ParsingPhase getParsingPhase();
	WrCompilationUnit getCompilationUnit();
	WrEnv getEnv();

	/*
	 * add to a list of metaobject annotations. Method {@link IListAfter_afti#after_afti_action(ICompiler_afti compiler)}
	 * of the metaobject will be called after the next afti phase
	 */
	void addToListAfter_afti(CyanMetaobject annotation);

}
