package meta;

import java.util.ArrayList;
import java.util.List;
import ast.MessageKeywordWithRealParameters;
import ast.MessageWithKeywords;

public class WrMessageWithKeywords extends WrASTNode {

	public WrMessageWithKeywords(MessageWithKeywords hidden) {
		this.hidden = hidden;
	}
	List<WrMessageKeywordWithRealParameters> iList = null;

	public List<WrMessageKeywordWithRealParameters> getkeywordParameterList() {
		if ( iList == null ) {
			iList = new ArrayList<>();
			if ( hidden.getkeywordParameterList() != null ) {
				for ( final MessageKeywordWithRealParameters mk : hidden.getkeywordParameterList() ) {
					iList.add(mk.getI());
				}
			}
		}
		return iList;
	}

	public String asString() {
		return hidden.asString();
	}

	public WrSymbol getFirstSymbol() {
		return hidden.getFirstSymbol().getI();
	}

	public String getMethodName() {
		return hidden.getMethodName();
	}

	public void accept(WrASTVisitor visitor, WrEnv env) {
		List<WrMessageKeywordWithRealParameters> keywordParameterList = getkeywordParameterList();
		if ( keywordParameterList != null ) {
			for ( WrMessageKeywordWithRealParameters s : keywordParameterList ) {
				s.accept(visitor, env);
			}
		}
		visitor.visit(this, env);
	}


	MessageWithKeywords hidden;

	@Override
	MessageWithKeywords  getHidden() {
		return hidden;
	}

}
