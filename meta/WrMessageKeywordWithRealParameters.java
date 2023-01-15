package meta;

import java.util.ArrayList;
import java.util.List;
import ast.Expr;
import ast.MessageKeywordWithRealParameters;

public class WrMessageKeywordWithRealParameters  extends WrASTNode {

	public WrMessageKeywordWithRealParameters(MessageKeywordWithRealParameters hidden) {
		this.hidden = hidden;
	}
	MessageKeywordWithRealParameters hidden;

	List<WrExpr> eList = null;
	public List<WrExpr> getExprList() {
		if ( eList == null ) {
			eList = new ArrayList<>();
			if ( hidden.getExprList() != null ) {
				for ( final Expr e : hidden.getExprList() ) {
					eList.add(e.getI());
				}
			}
		}
		return eList;
	}
	public Object getkeywordName() {
		return hidden.getkeywordName();
	}
	public Object getkeywordNameWithoutSpecialChars() {
		return hidden.getkeywordNameWithoutSpecialChars();
	}
	public WrSymbol getkeyword() {
		return hidden.getkeyword().getI();
	}

	@Override
	MessageKeywordWithRealParameters getHidden() {
		return hidden;
	}

	public void accept(WrASTVisitor visitor, WrEnv env) {
		List<Expr> exprList = hidden.getExprList();
		if ( exprList != null ) {
			for ( Expr e : exprList )
				e.getI().accept(visitor, env);
		}
		visitor.visit(this, env);
	}}
