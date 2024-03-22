
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
import meta.WrAnnotationAt;
import meta.WrFieldDec;

public class CyanMetaobjectMyProperty2 extends CyanMetaobjectAtAnnot
		implements IAction_afterResTypes {

	public CyanMetaobjectMyProperty2() {
		super("myproperty2", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.FIELD_DEC });

	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler,
			List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {

		StringBuffer methodsSignature = new StringBuffer();
		final StringBuffer methodsCode = new StringBuffer();
		final WrAnnotationAt annotation1 = this.getAnnotation();
		final WrFieldDec iv = (WrFieldDec) annotation1.getDeclaration();
		final String name = iv.getName();

		final String nameUpper = Character.toUpperCase(name.charAt(0))
				+ name.substring(1, name.length());
		String methodNameGet = "get" + nameUpper;
		String methodNameSet = "set" + nameUpper;
		String ivTypeName = iv.getType().getFullName();

		String methodGet = "    func " + methodNameGet + " -> " + ivTypeName;
		String methodSet = "    func " + methodNameSet + ": " + ivTypeName
				+ " other";

		methodsSignature.append(methodGet + ";\n");
		methodsSignature.append(methodSet + " ;");

		methodsCode.append(methodGet + " = " + name + ";\n");
		methodsCode.append(methodSet + " { self." + name + " = other; }\n");

		return new Tuple2<StringBuffer, String>(methodsCode,
				methodsSignature.toString());

	}

}
