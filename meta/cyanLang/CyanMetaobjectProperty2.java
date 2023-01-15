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

/**
 * generate methods getVarName and setVarName if field varName does not start with '_'. For
 * a field _varName methods varName and varName: are generated.
   @author jose
 */

public class CyanMetaobjectProperty2 extends CyanMetaobjectAtAnnot implements IAction_afterResTypes {

	public CyanMetaobjectProperty2() {
		super("property2", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.FIELD_DEC } );

	}


	@Override public
	Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {

		final StringBuffer s = new StringBuffer();
		final WrAnnotationAt atAnnot = this.getAnnotation();
		final WrFieldDec iv = (WrFieldDec ) atAnnot.getDeclaration();
		final String name = iv.getName();

		String methodGet;
		String methodSet;
		final String nameUpper = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
		String ivTypeName = iv.getType().getFullName();
		methodGet = "func get" + nameUpper + " -> " + ivTypeName;
		methodSet = "func set" + nameUpper + ": " + ivTypeName + " other";
		String code = methodGet + " = " + name + ";\n" +
		           methodSet + "\n    { " + "self." + name + " = other }\n";
		return new Tuple2<StringBuffer, String>(
				new StringBuffer(code),
				methodGet + ";\n" + methodSet + ";\n"
				);

	}

}
