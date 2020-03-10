package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpp;
import meta.ICompilerAction_dpa;
import meta.ICompilerInfo_dpa;
import meta.ICompiler_dpp;
import meta.IDeclaration;
import meta.IDeclarationWritable;
import meta.Token;
import meta.Tuple2;
import meta.WrAnnotationAt;
import meta.WrCyanPackage_dpp;
import meta.WrExprAnyLiteral;
import meta.WrProgram_dpp;

/**
 * This class represents metaobject 'annot'.<br>
 * It can be attached to fields, methods, method signatures, packages, and
 *  programs.
 *  <br>
 *  <code>annot(value)</code> is the same as <br>
 *  <code>feature(annot, value)</code>
 *
   @author José
 */
public class CyanMetaobjectAnnot extends CyanMetaobjectAtAnnot  implements
				ICompilerInfo_dpa, IAction_dpp { //ICompilerInfo_dpa {

	public CyanMetaobjectAnnot() {
		super("annot", AnnotationArgumentsKind.OneParameter,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.FIELD_DEC,
						AttachedDeclarationKind.METHOD_DEC, AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.PACKAGE_DEC, AttachedDeclarationKind.PROGRAM_DEC },
				Token.PUBLIC);
	}




	@Override
	public 	void check() {
		final List<WrExprAnyLiteral> exprList = ((WrAnnotationAt ) metaobjectAnnotation).getRealParameterList();
		featureValue = exprList.get(0);
		if ( featureValue.isExprLiteralNil()) {
			addError("'Nil' is not allowed as a feature value");
			return ;
		}

		if ( ! featureValue.isValidMetaobjectFeatureParameter() ) {
			addError("The value of this annotation is not a valid expression. Probably it "
					+ "has an array with elements of more than one type or a map with keys or "
					+ "values of more than one type. For example,\n    @annot([ \"0.0\", \"0\", \"abc\"])");
		}

	}

//	@Override
//	public List<Tuple2<String, WrExprAnyLiteral>> infoListToDeclaration() {
//		// List<Expr> exprList = ((AnnotationAt ) metaobjectAnnotation).getRealParameterList();
//		// // Tuple2<String, ExprAnyLiteral> t = (Tuple2<String, ExprAnyLiteral> ) getMetaobjectAnnotation().getInfo_dpa();
//		final List<Tuple2<String, WrExprAnyLiteral>> array = new ArrayList<>();
//		array.add(new Tuple2<String, WrExprAnyLiteral>("annot", this.featureValue));
//		return array;
//	}

	@Override
//	public Tuple2<StringBuffer, String> afti_codeToAdd(
//			ICompiler_afti compiler, List<Tuple2<Annotation, List<ISlotInterface>>> infoList)
	public
	void action_dpa(ICompilerAction_dpa compiler){
		IDeclaration idec = this.getAttachedDeclaration();

		if ( idec instanceof IDeclarationWritable ) {
			IDeclarationWritable idw = (IDeclarationWritable ) idec;
			idw.addFeature(new Tuple2<String, WrExprAnyLiteral>("annot", this.featureValue), compiler.getEnv());
		}
		// else should be an internal compiler error
	}


	@Override
	public void dpp_action(ICompiler_dpp project) {
		IDeclaration idec = this.getAttachedDeclaration();
		if (idec instanceof WrProgram_dpp ) {
			final WrProgram_dpp program = (WrProgram_dpp ) this.getAttachedDeclaration();
			program.addFeature(new Tuple2<String, WrExprAnyLiteral>("annot", this.featureValue));
		}
		else if ( idec instanceof WrCyanPackage_dpp ){
			// a package
			final WrCyanPackage_dpp cp = (WrCyanPackage_dpp ) this.getAttachedDeclaration();
			cp.addFeature(new Tuple2<String, WrExprAnyLiteral>("annot", this.featureValue));
		}
		else {
			this.addError("In metaobject 'feature', there is probably an internal error");
		}

	}

	private WrExprAnyLiteral featureValue;

}

