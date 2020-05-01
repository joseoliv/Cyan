package meta;

/**
 * compiler interface for macros
   @author José
 */
public interface ICompilerMacro_parsing extends IAbstractCyanCompiler {

	void next();
	WrSymbol getSymbol();
	WrSymbol getLastSymbol();
	boolean symbolCanStartExpr(WrSymbol symbol);
	WrExpr expr();
	WrExpr exprBasicTypeLiteral();
	WrStatement statement();
	WrExpr functionDec();
	void error(WrSymbol sym, String specificMessage);
	void setThereWasErrors(boolean wasError);
	boolean getThereWasErrors();

}
