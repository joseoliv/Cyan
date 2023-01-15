package meta;

public interface ICompilerPrototypeLater_parsing extends IAbstractCyanCompiler {

	WrPrototype getPrototype();
	void error(WrSymbol symbol, String message);
	void errorAtGenericPrototypeInstantiation(String errorMessage);
	ParsingPhase getParsingPhase();
	WrCompilationUnit getCompilationUnit();
	WrEnv getEnv();

	/*
	 * add to a list of metaobject annotations. Method {@link IListAfter_afterResTypes#after_afterResTypes_action(ICompiler_afterResTypes compiler)}
	 * of the metaobject will be called after the next AFTER_RES_TYPES phase
	 */
	void addToListAfter_afterResTypes(CyanMetaobject annotation);

}
