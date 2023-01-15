package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionStatement_semAn_afterSemAn;
import meta.WrEnv;
import meta.WrExpr;

public class CyanMetaobjectWhileFalse extends CyanMetaobjectAtAnnot implements IActionStatement_semAn_afterSemAn {

	public CyanMetaobjectWhileFalse() {
		super("whileFalse", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind [] { AttachedDeclarationKind.STATEMENT_DEC });
	}


	@Override
	public StringBuffer semAn_replaceExpr(WrEnv env, WrExpr e) {
		return new StringBuffer("false");
	}

	@Override
	public StringBuffer semAn_addStat(WrEnv env) {
		return null;
	}

	@Override
	public void afterSemAn_check(WrEnv env) {
	}

}
