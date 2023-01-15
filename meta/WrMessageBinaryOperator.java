package meta;

import java.util.List;
import ast.MessageBinaryOperator;

public class WrMessageBinaryOperator extends WrMessageWithKeywords {

	public WrMessageBinaryOperator(MessageBinaryOperator hidden) {
		super(hidden);
	}

	@Override
	MessageBinaryOperator getHidden() {
		return (MessageBinaryOperator ) hidden;
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		List<WrMessageKeywordWithRealParameters> keywordParameterList = getkeywordParameterList();
		if ( keywordParameterList != null ) {
			keywordParameterList.get(0).accept(visitor, env);
		}
		visitor.visit(this, env);
	}
}
