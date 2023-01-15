package meta;

import java.util.ArrayList;
import java.util.List;
import ast.AnnotationAt;
import ast.InterfaceDec;
import ast.MethodSignature;

public abstract class WrMethodSignature extends WrASTNode
       implements IDeclarationWritable, ISlotSignature {




	abstract public List<WrParameterDec> getParameterList();

	abstract public WrType getReturnType(WrEnv env);


	abstract public String getFullName(WrEnv env);
	abstract public String getFullNameWithReturnType(WrEnv env);


	abstract public String getFullName();
	abstract public String getFullNameWithReturnType();


	abstract public void calcInterfaceTypes(WrEnv env);

	abstract public String asString();

	abstract public WrExpr getReturnTypeExpr();

	abstract public void setReturnTypeExpr(WrExpr returnType);


	abstract public WrSymbol getFirstSymbol();

	abstract public WrMethodDec getMethod();

	public String getFunctionNameWithSelf(String fullName) {
		return ((MethodSignature ) this.getHidden()).getFunctionNameWithSelf(fullName);
	}

	public String getFunctionName() {
		return ((MethodSignature ) this.getHidden()).getFunctionName();
	}

	public WrPrototype getDeclaringInterface() {
		InterfaceDec id = ((MethodSignature ) this.getHidden()).getDeclaringInterface();
		return id == null ? null : id.getI();
	}

	abstract public void accept(WrASTVisitor visitor, WrEnv env);


	public List<WrAnnotationAt> getAttachedAnnotationList() {
		List<AnnotationAt> annotList =
				((MethodSignature ) this.getHidden()).getAttachedAnnotationList();
		List<WrAnnotationAt> wrAnnotList = new ArrayList<>();
		if ( annotList != null ) {
				for ( final AnnotationAt m : annotList ) {
					if ( m.getCyanMetaobject().getVisibility() == Token.PUBLIC ) {
						wrAnnotList.add(m.getI());
					}
				}
		}
		return wrAnnotList;

	}

	public boolean createdByMetaobjects() {
		return ((MethodSignature ) this.getHidden()).createdByMetaobjects();
	}

}
