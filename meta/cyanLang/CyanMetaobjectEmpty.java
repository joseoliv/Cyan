
package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.IAction_semAn;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_semAn;
import meta.ISlotSignature;
import meta.Tuple2;
import meta.WrAnnotation;

public class CyanMetaobjectEmpty extends CyanMetaobjectAtAnnot
		implements IAction_afterResTypes, IAction_semAn {

	public CyanMetaobjectEmpty() {
		super("empty", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.METHOD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC,
						AttachedDeclarationKind.NONE_DEC });
	}

	@Override
	public boolean shouldTakeText() {
		return false;
	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler,
			List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {
		return null;
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		return null;
	}

}
