package ast;

import meta.WrASTNode;

public interface ASTNode {
	void accept(ASTVisitor visitor);
	WrASTNode getI();

}
