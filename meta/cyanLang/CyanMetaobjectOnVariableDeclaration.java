package meta.cyanLang;

import cyan.lang.CyString;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionVariableDeclaration_semAn;
import meta.InterpreterPrototype;
import meta.MetaHelper;
import meta.WrEnv;

public class CyanMetaobjectOnVariableDeclaration extends CyanMetaobjectAtAnnot
			implements IActionVariableDeclaration_semAn, IInterpreterMethods_afterResTypes
				{

	public CyanMetaobjectOnVariableDeclaration() {
		super("onVariableDeclaration", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.LOCAL_VAR_DEC
		});
	}



	@Override
	public StringBuffer semAn_codeToAddAfter(WrEnv env) {
 		Object r = MetaHelper.interpreterFor_MOPInterfaceMethod(
 				null,
				env,
				interpreterPrototype,
				this,
				"semAn_codeToAddAfter",
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
		return new String[] { "afterResTypes_codeToAdd", "semAn_NewPrototypeList",
				"afterResTypes_NewPrototypeList", "runUntilFixedPoint", "afterResTypes_beforeMethodCodeList",
				"afterResTypes_renameMethod", "afterResTypes_semAn_afterSemAn_shareInfoPrototype", "afterResTypes_semAn_afterSemAn_receiveInfoPrototype",
				"semAn_codeToAddAfter" };
	}

}
