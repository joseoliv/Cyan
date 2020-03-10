package meta.cyanLang;

import java.util.List;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afsa;
import meta.ICompiler_dpa;
import meta.ICompiler_dsa;
import meta.IParseWithCyanCompiler_dpa;
import meta.InterpretationErrorException;
import meta.MetaHelper;
import meta.WrStatement;

public class CyanMetaobjectOnDeclaration_afsa extends CyanMetaobjectAtAnnot
implements ICheckDeclaration_afsa, IParseWithCyanCompiler_dpa {

	public CyanMetaobjectOnDeclaration_afsa() {
		super("onDeclaration_afsa", AnnotationArgumentsKind.ZeroParameters,
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
	public void afsa_checkDeclaration(ICompiler_dsa compiler) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				statList,
				compiler,
				this,
				"afsa_checkDeclaration",
				new String [] { "compiler" },
				new Object [] { compiler } ,
				null);
	}



	@Override
	public void dpa_parse(ICompiler_dpa cp) {
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
