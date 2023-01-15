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
import meta.WrMethodSignature;

/**
 * generate methods getVarName and setVarName if field varName does not start with '_'. For
 * a field _varName methods varName and varName: are generated.
   @author jose
 */

public class CyanMetaobjectProperty extends CyanMetaobjectAtAnnot implements IAction_afterResTypes {

	public CyanMetaobjectProperty() {
		super("property", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.FIELD_DEC } );

	}

	@Override public
	Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {

		StringBuffer strSlotList = new StringBuffer();
		final StringBuffer s = new StringBuffer();
		final WrAnnotationAt annot = this.getAnnotation();
		final WrFieldDec iv = (WrFieldDec ) annot.getDeclaration();
		final String name = iv.getName();

		String methodNameGet;
		String methodNameSet;

		if ( name.charAt(0) == '_' ) {
			methodNameGet = name.substring(1, name.length());
			methodNameSet = methodNameGet;
		}
		else {
			final String nameUpper = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
			methodNameGet = "get" + nameUpper;
			methodNameSet = "set" + nameUpper;
		}
		List<WrMethodSignature> mList;
		mList = compiler.getPrototype()
				.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodNameGet, compiler.getEnv());
		if ( mList != null && mList.size() > 0 ) {
			this.addError("Metaobject '" + this.getName() + "' called at line " + annot.getSymbolAnnotation().getLineNumber()
					+ " needs to create a method called '" + methodNameGet + "'. However this method already exists");
		}
		if ( iv.isShared() ) {
			s.append("    @prototypeCallOnly\n");
			if ( ! iv.getDeclaringObject().getIsFinal(compiler.getEnv()) ) {
				s.append("    final");
			}
		}
		String ivTypeName = iv.getType().getFullName();
		final StringBuffer ivname = new StringBuffer();
		final int sizeName = ivTypeName.length();
		for (int i = 0; i < sizeName; ++i) {
			final char ch = ivTypeName.charAt(i);
			if ( ch == ',' && i < sizeName - 1 && !Character.isWhitespace(ivTypeName.charAt(i+1)) ) {
				ivname.append(", ");
			}
			else {
				ivname.append(ch);
			}
		}

		ivTypeName = ivname.toString();

		strSlotList.append("    func " + methodNameGet + " -> " + ivTypeName + ";\n");
		s.append("    func " + methodNameGet + " -> " + ivTypeName + " = " + name + ";\n");
		if ( ! iv.isReadonly() ) {

			mList = compiler.getPrototype()
					.searchMethodPrivateProtectedPublicPackageSuperProtectedPublicPackage(methodNameSet + ":1",
							compiler.getEnv());
			if ( mList != null && mList.size() > 0 ) {
				this.addError("Metaobject annotation '" + this.getName() + "' at line " + annot.getSymbolAnnotation().getLineNumber()
						+ " needs to create a method called '" + methodNameSet + ":'. However this method already exists");
			}

			if ( iv.isShared() ) {
				s.append("    @prototypeCallOnly\n");
				if ( ! iv.getDeclaringObject().getIsFinal(compiler.getEnv()) ) {
					s.append("    final");
				}
			}
			strSlotList.append("    func " + methodNameSet + ": " + ivTypeName + " other;");
			s.append("    func " + methodNameSet + ": " + ivTypeName + " other { self." + name + " = other; }\n");
		}

		return new Tuple2<StringBuffer, String>(s, strSlotList.toString());

	}


}
