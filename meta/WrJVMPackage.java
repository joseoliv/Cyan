package meta;

import ast.JVMPackage;

public class WrJVMPackage extends WrASTNode {

	private JVMPackage hidden;

	public WrJVMPackage(JVMPackage hidden) {
		this.hidden = hidden;
	}

	@Override
	JVMPackage getHidden() {
		return hidden;
	}

	public void accept(WrASTVisitor visitor, WrEnv env) {
		visitor.visit(this, env);
	}

	public String getPackageName() {
		return hidden.getPackageName();
	}

}
