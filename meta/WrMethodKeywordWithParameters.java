package meta;

import java.util.ArrayList;
import java.util.List;
import ast.MethodKeywordWithParameters;
import ast.ParameterDec;

public class WrMethodKeywordWithParameters extends WrASTNode  { // implements GetHidden<MethodKeywordWithParameters> {

	MethodKeywordWithParameters hidden;

	public WrMethodKeywordWithParameters(MethodKeywordWithParameters hidden) {
		this.hidden = hidden;
	}

	public WrMethodKeywordWithParameters(WrSymbol symbol) {
		this.hidden = new MethodKeywordWithParameters(symbol.hidden);
	}

	@Override
	MethodKeywordWithParameters getHidden() {
		if ( this.iParameterList != null && this.iParameterList.size() != hidden.getParameterList().size() ) {
			List<ParameterDec> paramDecList = new ArrayList<>();
			for ( WrParameterDec from : this.getParameterList() ) {
				paramDecList.add(from.getHidden());
			}
			hidden.setParameterList(paramDecList);
		}
		return hidden;
	}


	List<WrParameterDec> iParameterList = null;
	boolean thisMethod_wasNeverCalled = true;

	public List<WrParameterDec> getParameterList() {
		if ( thisMethod_wasNeverCalled ) {
			thisMethod_wasNeverCalled = false;


			List<ParameterDec> fromList = hidden.getParameterList();
			if ( fromList == null ) {
					// unnecessary, just to document
				iParameterList = null;
			}
			else {
				iParameterList = new ArrayList<>();
				for ( ParameterDec from : fromList ) {
					iParameterList.add( from.getI() );
				}
			}

		}

		return this.iParameterList;
	}


	public AttachedDeclarationKind getKind() {
		return AttachedDeclarationKind.NONE_DEC;
	}


	public String getkeywordNameWithoutSpecialChars() {
		return hidden.getkeywordNameWithoutSpecialChars();
	}


	public String getName() {
		return hidden.getName();
	}

	public WrSymbol getkeyword() {
		return hidden.getkeyword().getI();
	}

	public void accept(WrASTVisitor visitor, WrEnv env) {
		List<WrParameterDec> ipList = getParameterList();

		if ( ipList != null ) {
			for (final WrParameterDec p : ipList) {
				p.accept(visitor, env);
			}
		}
		visitor.visit(this, env);
	}


}

