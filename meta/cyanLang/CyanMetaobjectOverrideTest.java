package meta.cyanLang;

import java.util.Set;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckOverride_afterSemAn;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.MetaHelper;
import meta.WrAnnotationAt;
import meta.WrCompilationUnit;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import saci.NameServer;

public class CyanMetaobjectOverrideTest extends CyanMetaobjectAtAnnot
    implements ICheckOverride_afterSemAn {

	public CyanMetaobjectOverrideTest() {
		super("overrideTest", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC });
	}


	@Override
	public boolean shouldTakeText() { return true; }




	@Override
	public void afterSemAn_checkOverride(ICompiler_semAn compiler_semAn,
			WrMethodDec method) {
		/*
		 * FileError meta.IAbstractCyanCompiler.writeTestFileTo(StringBuffer data, String fileName, String dirName, String packageName)
		 */
		final WrEnv env = compiler_semAn.getEnv();
		final String subPrototypeName = method.getDeclaringObject().getName();
		final String subPrototypeFullName = method.getDeclaringObject().getFullName();
		final String subPrototypePackageName = method.getDeclaringObject().getPackageName();
		final WrCyanPackage prototypePackage = method.getDeclaringObject().getCompilationUnit(env).getCyanPackage();
		boolean generateTestCase = false;
		Set<String> set = prototypePackage.getPackageKeyValueSet(overrideTest);
		Set<String> prset = compiler_semAn.getProgramKeyValueSet(overrideTest);
		if ( set != null && (set.contains(subPrototypeFullName) ||
				 set.contains(subPrototypeName)) ) {
			generateTestCase = true;
		}
		else if ( prset != null && (prset.contains(subPrototypePackageName) ||
				 prset.contains(subPrototypeFullName)) ) {
			generateTestCase = true;
		}
		if ( generateTestCase ) {
			final StringBuffer code = new StringBuffer();
			final String packageName1 = overrideTest;
			WrAnnotationAt annot = this.getAnnotation();
			code.append("/*\n  This test was created by the metaobject associated to the annotation \r\n" +
					"  " + this.getName()  +  "  in line " + annot.getFirstSymbol().getLineNumber() +
					" of prototype " + ((WrCompilationUnit ) annot.getCompilationUnit()).getPublicPrototype().getFullName() + " of file \r\n  "
					+ annot.getCompilationUnit().getFullFileNamePath() + "\r\n" );
			code.append("*/\r\n");
			code.append("package " + subPrototypePackageName + "\n\n");
			IDeclaration dec = this.getAttachedDeclaration();
			String importThis = "";
			if ( dec instanceof WrMethodDec ) {
				importThis = ((WrMethodDec ) dec).getDeclaringObject().getPackageName();
			}
			else if ( dec instanceof WrMethodSignature ) {
				importThis = ((WrMethodSignature ) dec).getDeclaringInterface().getPackageName();
			}
			else {
				this.addError("This annotation should be attached to a method or method signature. Probably an internal error occurred");
			}
			code.append("import " + importThis + "\n\n");

			String methodName = method.getName();
			methodName = methodName.replace(":", "_");
			methodName = methodName.replace(' ', '_');
			char ch = methodName.charAt(0);
			if ( !Character.isAlphabetic(ch) && ch != '_' ) {
				methodName = MetaHelper.alphaName(methodName);
			}
			final String protoName = "OverrideTest_" + subPrototypeFullName.replace('.', '_') + "_" +
			    methodName;
			code.append("object " + protoName + "\n");
			// code.append("    func test { \n");
			final WrAnnotationAt annotation = this.getAnnotation();
			String text = new String(annotation.getTextAttachedDSL());
			text = text.replace("(%SUBPROTOTYPE%)", subPrototypeName);
			code.append(text);
			// code.append("        \n");
			code.append("\nend\n");
			compiler_semAn.writeTestFileTo(code, protoName + ".cyan", "",
					method.getDeclaringObject().getPackageName().replace('.', NameServer.fileSeparator) );
		}

	}

	static final private String overrideTest = "overrideTest";

}

