package meta.cyanLang;

import java.util.List;
import error.CompileErrorException;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckSubprototype_afterSemAn;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IParseWithCyanCompiler_parsing;
import meta.InterpretationErrorException;
import meta.MetaHelper;
import meta.WrPrototype;
import meta.WrStatement;

/**
 * The attached DSL code is run when the prototype attached to the annotation is inherited,
 * even indirectly. See details on the attached DSL code on metaobject {@link meta.CyanMetaobjectOnOverride CyanMetaobjectOnOverride}.
   @author jose
 */
public class CyanMetaobjectOnSubprototype extends CyanMetaobjectAtAnnot
implements ICheckSubprototype_afterSemAn, IParseWithCyanCompiler_parsing {

	public CyanMetaobjectOnSubprototype() {
		super("onSubprototype", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC
						//, AttachedDeclarationKind.PACKAGE_DEC, AttachedDeclarationKind.PROGRAM_DEC
						});
	}


	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public void afterSemAn_checkSubprototype(ICompiler_semAn compiler,
			WrPrototype subPrototype) {

		MetaHelper.interpreterFor_MOPInterfaceMethod(
				statList,
				compiler,
				this,
				"afterSemAn_checkSubprototype",
				new String [] { "compiler", "subPrototype" },
				new Object [] { compiler, subPrototype } ,
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
			// addError(e.getMessage());
		}

	}


	List<WrStatement> statList;


}
