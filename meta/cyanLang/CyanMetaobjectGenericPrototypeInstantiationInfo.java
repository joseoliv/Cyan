package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_cge;
import meta.IAction_dpa;
import meta.ICheckDeclaration_afsa;
import meta.ICompilerAction_dpa;
import meta.ICompiler_dsa;
import meta.IStayPrototypeInterface;
import meta.MetaHelper;
import meta.Token;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrSymbol;
import metaRealClasses.Compiler_dpa;

/**
 * This metaobject cannot be used by programmers. It is used by the compiler to help trace
 * the stack of generic prototype instantiations when there is an error.
   @author jose
 */
public class CyanMetaobjectGenericPrototypeInstantiationInfo extends CyanMetaobjectAtAnnot
          implements IAction_dpa, ICheckDeclaration_afsa, IAction_cge,
          IStayPrototypeInterface {

	public CyanMetaobjectGenericPrototypeInstantiationInfo() {
		super("genericPrototypeInstantiationInfo",
				AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.PROTOTYPE_DEC },
				Token.PUBLIC
				);
	}

	@Override
	public void check() {
		final WrAnnotationAt annotation = (WrAnnotationAt ) this.metaobjectAnnotation;
		if ( annotation.getJavaParameterList().size() != 4 ) {
			addError("four parameters was expected for metaobject '" + this.getName() + "'");
		}
	}

	@Override
	public StringBuffer cge_codeToAdd() {
		final WrAnnotationAt annotation = (WrAnnotationAt ) this.metaobjectAnnotation;
		final List<Object> javaParamList = annotation.getJavaParameterList();
		final String packageNameInstantiation = MetaHelper.removeQuotes((String ) javaParamList.get(0));
		final String prototypeNameInstantiation = MetaHelper.removeQuotes( (String ) javaParamList.get(1));
		final int lineNumber = (Integer ) javaParamList.get(2);
		final int columnNumber = (Integer ) javaParamList.get(3);

		return new StringBuffer("/* this generic prototype was created because of a type that is in \n    "
				+ packageNameInstantiation + "." + prototypeNameInstantiation + " at line " + lineNumber + " column "
				+ columnNumber + " */");
	}



	@Override
	public StringBuffer dpa_codeToAdd(ICompilerAction_dpa compiler_dpa) {

		final WrAnnotationAt annotation = (WrAnnotationAt ) this.metaobjectAnnotation;
		final WrSymbol sym = annotation.getFirstSymbol();
		final int errorLine = sym.getLineNumber();
		final List<Object> javaParamList = annotation.getJavaParameterList();
		if ( ! (javaParamList.get(0) instanceof String) ) {
			compiler_dpa.error(errorLine, "A package name, as String, was expected as first parameter to this metaobject annotation");
		}
		if ( ! (javaParamList.get(1) instanceof String) ) {
			compiler_dpa.error(errorLine, "A prototype name, as String, was expected as second parameter to this metaobject annotation");
		}
		if ( ! (javaParamList.get(2) instanceof Integer) ) {
			compiler_dpa.error(errorLine, "A line number was expected as third parameter to this metaobject annotation");
		}
		if ( ! (javaParamList.get(3) instanceof Integer) ) {
			compiler_dpa.error(errorLine, "A column number was expected as fourth parameter to this metaobject annotation");
		}
		final String packageNameInstantiation = MetaHelper.removeQuotes((String ) javaParamList.get(0));
		final String prototypeNameInstantiation = MetaHelper.removeQuotes( (String ) javaParamList.get(1));
		final int lineNumber = (Integer ) javaParamList.get(2);
		final int columnNumber = (Integer ) javaParamList.get(3);
		/*
		 * this cast is to hide these methods from the regular programmer. This cast
		 * can be made illegal at any time by the compiler designer.
		 */
		final Compiler_dpa compiler = (Compiler_dpa ) compiler_dpa;
		compiler.setPackageNameInstantiation(packageNameInstantiation);
		compiler.setPrototypeNameInstantiation(prototypeNameInstantiation);
		compiler.setLineNumberInstantiation(lineNumber);
		compiler.setColumnNumberInstantiation(columnNumber);
		return null;
	}

	@Override
	public void afsa_checkDeclaration(ICompiler_dsa compiler_dsa) {

		final WrAnnotationAt annotation = (WrAnnotationAt ) this.metaobjectAnnotation;
		final List<Object> javaParamList = annotation.getJavaParameterList();
		final String packageNameInstantiation = MetaHelper.removeQuotes((String ) javaParamList.get(0));
		final String prototypeNameInstantiation = MetaHelper.removeQuotes( (String ) javaParamList.get(1));
		final int lineNumber = (Integer ) javaParamList.get(2);
		final int columnNumber = (Integer ) javaParamList.get(3);

		final WrEnv env = compiler_dsa.getEnv();

		/*
		if ( env.getCurrentProgramUnit() != null  ) {
			packageNameInstantiation += " / " + env.getCurrentProgramUnit().getCompilationUnit().getPackageName();
			prototypeNameInstantiation += " / " + env.getCurrentProgramUnit().getName();
		}
		*/
		env.setPackageNameInstantiation(packageNameInstantiation  );
		env.setPrototypeNameInstantiation(prototypeNameInstantiation );
		env.setLineNumberInstantiation(lineNumber);
		env.setColumnNumberInstantiation(columnNumber);

	}

}
