package meta;

public interface ICompiler_dsl extends IAbstractCyanCompiler {


	void next();
	WrSymbol getSymbol();
	boolean symbolCanStartExpr(WrSymbol symbol);

	WrExpr type();
	WrExpr expr();
	WrStatement statement();
	WrExprIdentStar parseSingleIdent();
	boolean startType(Token t);
	boolean isOperator(Token token);
	void pushRightSymbolSeq(String rightSymbolSeq);
	WrExpr parseIdent();
	boolean isBasicType(Token t);
	WrMethodSignature methodSignature();
	WrMethodSignature methodSignature(boolean finalKeyword,
			boolean abstractKeyword);
}
