package meta.cyanLang;

import java.util.List;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckSubprototype_afsa;
import meta.ICompiler_dpa;
import meta.ICompiler_dsa;
import meta.IParseWithCyanCompiler_dpa;
import meta.InterpretationErrorException;
import meta.MetaHelper;
import meta.WrProgramUnit;
import meta.WrStatement;

/**
 * The attached DSL code is run when the prototype attached to the annotation is inherited,
 * even indirectly. See details on the attached DSL code on metaobject {@link meta.CyanMetaobjectOnOverride CyanMetaobjectOnOverride}.
   @author jose
 */
public class CyanMetaobjectOnSubprototype extends CyanMetaobjectAtAnnot
implements ICheckSubprototype_afsa, IParseWithCyanCompiler_dpa {

	public CyanMetaobjectOnSubprototype() {
		super("onSubprototype", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC
						//, AttachedDeclarationKind.PACKAGE_DEC, AttachedDeclarationKind.PROGRAM_DEC
						});
	}


	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public void afsa_checkSubprototype(ICompiler_dsa compiler,
			WrProgramUnit subPrototype) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				statList,
				compiler,
				this,
				"afsa_checkSubprototype",
				new String [] { "compiler", "subPrototype" },
				new Object [] { compiler, subPrototype } ,
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
			// addError(e.getMessage());
		}

	}


	List<WrStatement> statList;


}
