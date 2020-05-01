package meta;

import ast.Statement;
import lexer.Symbol;

abstract public class WrStatement extends WrASTNode implements IICalcInternalTypes {

	public WrStatement(Statement hidden) {
		this.hidden = hidden;
	}

	Statement hidden = null;

	public String asString() {
		return hidden.asString();
	}

	public WrSymbol getFirstSymbol() {
		return hidden.getFirstSymbol().getI();
	}


	public Object eval(WrEvalEnv ee) {
		return hidden.eval(ee.hidden);
	}

	@Override
	public void calcInternalTypes(WrEnv env) {
		hidden.calcInternalTypes(env.hidden);
	}

	@Override
	abstract Statement getHidden();

	public WrSymbol getSymbolAfter() {
		Symbol sym = hidden.getSymbolAfter();
		return sym == null ? null : sym.getI();
	}


	abstract public void accept(WrASTVisitor visitor, WrEnv env);

	public boolean demandSemicolon() {
		return hidden.demandSemicolon();
	}

	public WrMethodDec getCurrentMethod() {
		if ( hidden.getCurrentMethod() == null ) {
			return null;
		}
		else {
			return hidden.getCurrentMethod().getI();
		}
	}

	public boolean getCreatedByMetaobjects() {
		return hidden.getCreatedByMetaobjects();
	}


}
