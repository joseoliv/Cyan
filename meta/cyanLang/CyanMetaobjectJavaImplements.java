package meta.cyanLang;

import ast.ObjectDec;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_semAn;
import meta.MetaHelper;
import meta.WrAnnotationAt;
import meta.WrPrototype;

/**
 * The annotation of this metaobject should be attached to a prototype and should have a sole parameter.
 * The metaobject makes the Java code for the prototype implements the Java interface given as parameter.
 * Example:<br>
 * <code>
 * {@literal @}javaImplements(Action)<br>
 * object Test ... end<br>
   @author jose
 */
public class CyanMetaobjectJavaImplements extends CyanMetaobjectAtAnnot implements ICheckDeclaration_afterSemAn {

	public CyanMetaobjectJavaImplements() {
		super("javaImplements", AnnotationArgumentsKind.OneParameter,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC });
	}


	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler) {
		final WrAnnotationAt annotation = (WrAnnotationAt  ) this.annotation;
		final WrPrototype pu = (WrPrototype  ) annotation.getDeclaration();
		if ( pu == null ) {
			addError("This metaobject should be used inside a prototype");
			return ;
		}
		if ( pu.isInterface() ) {
			addError("This metaobject cannot be used with interfaces");
			return ;
		}
		final Object objParam = annotation.getJavaParameterList().get(0);
		if ( !(objParam instanceof String) ) {
			addError("This metaobject should take a literal string or identifier as parameter");
			return ;
		}
		String param = (String ) objParam;
		param = MetaHelper.removeQuotes(param);

		ObjectDec hiddenPU = (ObjectDec ) meta.GetHiddenItem.getHiddenPrototype(pu);
		// this.addError("Metaobject javaImplements should not be used. If necessary, uncomment the lines below");
		if ( ! hiddenPU.addJavaInterface(param) ) {
			compiler.error(annotation.getFirstSymbol(), "The interface '" + param + "' is already in the list of Java interfaces "
					+ "implemented by this prototype");
		}
	}


}
