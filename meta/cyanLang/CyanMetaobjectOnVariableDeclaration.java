package meta.cyanLang;

import cyan.lang.CyString;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionVariableDeclaration_dsa;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.WrEnv;

public class CyanMetaobjectOnVariableDeclaration extends CyanMetaobjectAtAnnot
			implements IActionVariableDeclaration_dsa, IInterpreterMethods_afti
				{

	public CyanMetaobjectOnVariableDeclaration() {
		super("onVariableDeclaration", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.LOCAL_VAR_DEC
		});
	}



	@Override
	public StringBuffer dsa_codeToAddAfter(WrEnv env) {
 		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
 				null,
				env,
				interpreterPrototype,
				this,
				"dsa_codeToAddAfter",
				   // "env" is added by the called method
				new String [] { }, new Object [] { },
				CyString.class, "String");

		if ( r == null ) {
			return null;
		}
		else {
			return new StringBuffer( ((CyString ) r).s);
		}
	}



	@Override
	public boolean shouldTakeText() { return true; }

	InterpreterPrototype interpreterPrototype = null;

	@Override
	public InterpreterPrototype getInterpreterPrototype() {
		return interpreterPrototype;
	}

	@Override
	public void setInterpreterPrototype(InterpreterPrototype interpreterPrototype) {
		this.interpreterPrototype = interpreterPrototype;
	}


	@Override
	public CyanMetaobjectAtAnnot getCyanMetaobject() {
		return this;
	}


	@Override
	public String[] methodToInterpertList() {
		return new String[] { "afti_codeToAdd", "dsa_NewPrototypeList",
				"afti_NewPrototypeList", "runUntilFixedPoint", "afti_beforeMethodCodeList",
				"afti_renameMethod", "afti_dsa_afsa_shareInfoPrototype", "afti_dsa_afsa_receiveInfoPrototype",
				"dsa_codeToAddAfter" };
	}

}
