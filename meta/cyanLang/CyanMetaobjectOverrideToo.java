package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckOverride_afterSemAn;
import meta.ICompiler_semAn;
import meta.MetaHelper;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrPrototype;

public class CyanMetaobjectOverrideToo extends CyanMetaobjectAtAnnot
			implements // ICheckSubprototype_afterSemAn,
			ICheckOverride_afterSemAn {

	public CyanMetaobjectOverrideToo() {
		super("overrideToo", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.METHOD_DEC });
	}


	@Override
	public void check() {
		methodNameList = new ArrayList<>();
		int n = 1;
		for (Object obj : this.getAnnotation().getJavaParameterList() ) {
			if ( !(obj instanceof String) ) {
				this.addError("Parameter number '" + n + "' is not a String. It should be");
				return ;
			}
			String str = MetaHelper.removeQuotes( (String ) obj );
			methodNameList.add(str);
			++n;
		}
	}


	List<String> methodNameList;

	@Override
	public void afterSemAn_checkOverride(ICompiler_semAn compiler_semAn,
			WrMethodDec method) {

		WrMethodDec superMethod = (WrMethodDec ) this.getAttachedDeclaration();
		String superMethodName = superMethod.getName();
		WrPrototype superProto = superMethod.getDeclaringObject();
		WrPrototype subPrototype = method.getDeclaringObject();
		if ( subPrototype.getName().startsWith("Union<") ) { return ; }
		List<WrMethodSignature> msList =
				subPrototype.searchMethodPublicPackageSuperPublicPackage(
						superMethodName, compiler_semAn.getEnv());

		// method 'superMethodName' is overridden in the subprototype
		// check if all methods that are parameters to the annotation are overridden too
		for ( String methodName : this.methodNameList ) {
			msList = subPrototype.searchMethodPublicPackageSuperPublicPackage(methodName, compiler_semAn.getEnv());
			if ( msList == null || msList.size() == 0 ||
					msList.get(0).getMethod().getDeclaringObject() == superProto ) {
				this.addError(subPrototype.getFirstSymbol(compiler_semAn.getEnv()), "Annotation attached to method '"
						+ superMethodName + "' of prototype '" + superProto.getFullName()  + "' demands that"
								+ " every subprototype overrides method '" + methodName + "' when method '"
								+ superMethodName + "' is overridden");
			}
		}

		}

}
