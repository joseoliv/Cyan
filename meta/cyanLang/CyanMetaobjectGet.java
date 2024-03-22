
package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrFieldDec;

public class CyanMetaobjectGet extends CyanMetaobjectAtAnnot
		implements IAction_afterResTypes {

	public CyanMetaobjectGet() {
		super("get", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.FIELD_DEC });
	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler,
			List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {

		final WrFieldDec field = (WrFieldDec) this.getAnnotation()
				.getDeclaration();

		final String name = field.getName();

		String methodSig = "func get_" + name + " -> "
				+ field.getType().getFullName();
		String method = methodSig + " = " + name + ";";
		return new Tuple2<StringBuffer, String>(new StringBuffer(method),
				methodSig);

	}

}
