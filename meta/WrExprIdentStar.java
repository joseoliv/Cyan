package meta;

import java.util.ArrayList;
import java.util.List;
import ast.ASTNode;
import ast.ExprIdentStar;
import ast.VariableDecInterface;
import lexer.Symbol;

public class WrExprIdentStar extends WrExpr {
	public WrExprIdentStar(ExprIdentStar hidden) {
		super(hidden);
	}

	public String getName() {
		return ((ExprIdentStar) hidden).getName();
	}

	List<WrSymbol> isymbolList = null;
	boolean thisMethod_wasNeverCalled = true;

	public List<WrSymbol> getIdentSymbolArray() {
		if ( thisMethod_wasNeverCalled ) {

			List<Symbol> fromList = ((ExprIdentStar) hidden).getIdentSymbolArray();
			if ( fromList == null ) {
					// unnecessary, just to document
				isymbolList = null;
			}
			else {
				isymbolList = new ArrayList<>();
				for ( Symbol from : fromList ) {
					isymbolList.add( from.getI() );
				}
			}
			thisMethod_wasNeverCalled = false;

		}
		return isymbolList;
	}


	@Override
	ExprIdentStar getHidden() {
		return (ExprIdentStar ) hidden;
	}

	public IdentStarKind getIdentStarKind() {
		return ((ExprIdentStar ) hidden).getIdentStarKind();
	}

	public IVariableDecInterface getVarDeclaration() {
		VariableDecInterface vd = ((ExprIdentStar ) hidden).getVarDeclaration();
		if ( vd == null ) { return null; }
		return (IVariableDecInterface ) (
				(ASTNode )
				   vd).getI();
	}


	@Override
	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}



	public WrMethodSignature getMethodSignatureForMessageSend() {
		return ((ExprIdentStar ) hidden).getMethodSignatureForMessageSend().getI();
	}

}
