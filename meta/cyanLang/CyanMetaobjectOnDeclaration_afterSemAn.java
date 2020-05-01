package meta.cyanLang;

import java.util.List;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IParseWithCyanCompiler_parsing;
import meta.InterpretationErrorException;
import meta.MetaHelper;
import meta.WrStatement;

public class CyanMetaobjectOnDeclaration_afterSemAn extends CyanMetaobjectAtAnnot
implements ICheckDeclaration_afterSemAn, IParseWithCyanCompiler_parsing {

	public CyanMetaobjectOnDeclaration_afterSemAn() {
		super("onDeclaration_afterSemAn", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.METHOD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC,
						AttachedDeclarationKind.FIELD_DEC,
						AttachedDeclarationKind.LOCAL_VAR_DEC
		});
	}


	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				statList,
				compiler,
				this,
				"afterSemAn_checkDeclaration",
				new String [] { "compiler" },
				new Object [] { compiler } ,
				null);
	}



	@Override
	public void parsing_parse(ICompiler_parsing cp) {
		try {
			cp.next();
			statList = MetaHelper.parseCyanStatementList(cp);
		}
		catch (InterpretationErrorException e) {
			addError(e.getMessage());
		}
		catch (CompileErrorException e) {
			//addError(e.getMessage());
		}


	}


	List<WrStatement> statList;

}
