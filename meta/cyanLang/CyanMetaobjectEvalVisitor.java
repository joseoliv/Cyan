package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFunction;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.WrASTVisitor;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrMethodDec;
import meta.WrMethodSignatureOperator;
import meta.WrMethodSignatureUnary;
import meta.WrMethodSignatureWithKeywords;
import meta.WrPrototype;

/**
 * The annotation of this metaobject takes a list of function metaobject names as parameters. It
 * then calls every of these functions passing as parameter the attached declaration. Example:
 * <code> <br>
 *     {@literal @}evalVisitor(checkSomething, checkOtherthing)<br>
 *     object Test ... end<br>
 * </code><br>
 * Prototype Test will be passed as parameter to function metaobjects checkSomething and checkOtherthing.
   @author jose
 */
public class CyanMetaobjectEvalVisitor  extends CyanMetaobjectAtAnnot implements ICheckDeclaration_afterSemAn {

	public CyanMetaobjectEvalVisitor() {
		super("evalVisitor", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[]{
//						AttachedDeclarationKind.PACKAGE_DEC,
//						AttachedDeclarationKind.PROGRAM_DEC,
						AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.METHOD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC,
						AttachedDeclarationKind.FIELD_DEC
						});
	}

	@Override
	public void check() {
		final WrAnnotationAt annot = this.getAnnotation();
		final List<Object> paramList = annot.getJavaParameterList();
		strList = new ArrayList<>();
		for ( final Object param : paramList ) {
			if (!(param instanceof String) ) {
				this.addError("A string or identifer parameter was expected");
				return ;
			}
			strList.add((String ) param);

		}
	}
	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler_semAn) {
		final List<IActionFunction> functionList = new ArrayList<>();

		for ( final String strParam : strList ) {
			functionList.add(compiler_semAn.getEnv().searchActionFunction(strParam));
		}
		final IDeclaration dec = this.getAttachedDeclaration();
		WrEnv env = compiler_semAn.getEnv();
		if ( dec instanceof WrPrototype ) {
			((WrPrototype ) dec).accept( new WrASTVisitor() {
				@Override
				public void visit(WrPrototype node, WrEnv env) {
					for ( final IActionFunction function : functionList ) {
						function.eval(node);
					}
				}
			}, env
			);
		}
		else if ( dec instanceof WrMethodDec ) {
			((WrMethodDec ) dec).accept( new WrASTVisitor() {
				@Override
				public void visit(WrMethodDec node, WrEnv env) {
					for ( final IActionFunction function : functionList ) {
						function.eval(node);
					}
				}
			}, env
			);
		}
		else if ( dec instanceof WrMethodSignatureWithKeywords ) {
			((WrMethodSignatureWithKeywords ) dec).accept( new WrASTVisitor() {
				@Override
				public void visit(WrMethodSignatureWithKeywords node, WrEnv env) {
					for ( final IActionFunction function : functionList ) {
						function.eval(node);
					}
				}
			}, env
			);
		}
		else if ( dec instanceof WrMethodSignatureUnary ) {
			((WrMethodSignatureUnary ) dec).accept( new WrASTVisitor() {
				@Override
				public void visit(WrMethodSignatureUnary node, WrEnv env) {
					for ( final IActionFunction function : functionList ) {
						function.eval(node);
					}
				}
			}, env
			);
		}
		else if ( dec instanceof WrMethodSignatureOperator ) {
			((WrMethodSignatureOperator ) dec).accept( new WrASTVisitor() {
				@Override
				public void visit(WrMethodSignatureOperator node, WrEnv env) {
					for ( final IActionFunction function : functionList ) {
						function.eval(node);
					}
				}
			}, env
			);
		}

		/*
		dec.accept( new ASTVisitor() {
			@Override
			public void visit(FieldDec node) {
				String fieldTypeName = node.getType().getFullName();
				String []basicTypes = new String[] {
						"Char", "Boolean", "Byte", "Short", "Int",
						"Long", "Float", "Double", "String" };
				for ( String typeName : basicTypes ) {
					if ( typeName.equals(fieldTypeName) ) {
						return ;
					}
				}
				addError("All the fields of this prototype "
						+ "should have basic types as types. " +
						"However, field '" + node.getName() + "' has " +
						"type '" + fieldTypeName + "'");
			}

		});
		*/


	}

	private List<String> strList;
}
