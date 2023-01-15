package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_dpp;
import meta.ICompilerAction_parsing;
import meta.ICompilerInfo_parsing;
import meta.ICompiler_dpp;
import meta.IDeclaration;
import meta.IDeclarationWritable;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.WrCyanPackage_dpp;
import meta.WrExprAnyLiteral;
import meta.WrExprAnyLiteralIdent;
import meta.WrExprLiteralNil;
import meta.WrExprLiteralString;
import meta.WrProgram_dpp;

/**
 * This class represents metaobject 'feature' that associates a value to an identifier.
 *  It can be attached to fields, methods, method signatures, packages, and
 *  programs.
 *
 *  Usage:<br>
 *  <code>
 *  @feature(author, "Jose")
 *  package main
 *  </code>
 *
   @author José
 */
public class CyanMetaobjectFeature extends CyanMetaobjectAtAnnot
    implements ICompilerInfo_parsing, IAction_dpp {

	public CyanMetaobjectFeature() {
		super("feature", AnnotationArgumentsKind.TwoParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.FIELD_DEC,
						AttachedDeclarationKind.METHOD_DEC, AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC, AttachedDeclarationKind.LOCAL_VAR_DEC,
						AttachedDeclarationKind.PACKAGE_DEC, AttachedDeclarationKind.PROGRAM_DEC },
				Token.PUBLIC);
	}

	@Override
	public void check() {
		if ( getAnnotation().getRealParameterList() == null ||
				getAnnotation().getRealParameterList().size() != 2 )  {
			addError("This metaobject annotation should have exactly two parameters");
			return ;
		}
		final String featureName = getAnnotation().javaParameterAt(0);
		final Object featureValue = getAnnotation().getRealParameterList().get(1);
		WrExprAnyLiteral firstParameter = getAnnotation().getRealParameterList().get(0);
		if ( !(firstParameter instanceof WrExprLiteralString) && !(firstParameter instanceof WrExprAnyLiteralIdent)) {
			addError("This metaobject annotation should have the first parameter of type 'String'");
			return ;
		}
		if ( featureValue instanceof WrExprLiteralNil ) {
			addError("'Nil' is not allowed as a feature value");
			return ;
		}
		if ( ! (featureValue instanceof WrExprAnyLiteral) ) {
			addError("Internal error: the parameter to metaobject '" + getName() + "' should be subtype of ExprAnyLiteral");
			return ;
		}
		final WrExprAnyLiteral valueExpr = (WrExprAnyLiteral ) featureValue;
		if ( ! valueExpr.isValidMetaobjectFeatureParameter() ) {
			addError("The value of this feature is not a valid expression. Probably it "
					+ "has an array with elements of more than one type or a map with keys or "
					+ "values of more than one type. For example,\n    @feature(list, [ 0.0, 0, \"abc\"])");
			return ;
		}

		String featureNameWithoutQuotes = MetaHelper.removeQuotes( featureName );
		info = new Tuple2<String, WrExprAnyLiteral>( featureNameWithoutQuotes,
		        				((WrExprAnyLiteral ) featureValue));
	}



	@Override
	public void dpp_action(ICompiler_dpp compiler) {
		IDeclaration idec = this.getAttachedDeclaration();
		if (idec instanceof WrProgram_dpp ) {
			final WrProgram_dpp program = (WrProgram_dpp ) this.getAttachedDeclaration();
			program.addFeature(info);
		}
		else if ( idec instanceof WrCyanPackage_dpp ){
			// a package
			final WrCyanPackage_dpp cp = (WrCyanPackage_dpp ) this.getAttachedDeclaration();
			cp.addFeature(info);
		}
		else {
			this.addError("In metaobject 'feature', there is probably an internal error");
		}

	}

	@Override
	public
	void action_parsing(ICompilerAction_parsing compiler)	{
		IDeclaration idec = this.getAttachedDeclaration();

		if ( idec instanceof IDeclarationWritable ) {
			IDeclarationWritable idw = (IDeclarationWritable ) idec;
			idw.addFeature(info, compiler.getEnv());
		}
		else {
			this.addError("In metaobject 'feature', there is probably an internal error");
		}
	}

	private Tuple2<String, WrExprAnyLiteral> info;

}
