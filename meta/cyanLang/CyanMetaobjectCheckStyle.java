/**

 */
package meta.cyanLang;

import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.WrASTVisitor;
import meta.WrEnv;
import meta.WrFieldDec;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrMethodSignatureUnary;
import meta.WrMethodSignatureWithKeywords;
import meta.WrPrototype;
import meta.WrSymbol;

/**
 * This metaobject checks the style of its attached declaration. If the declaration is a program, all
 * the packages are checked. If it is a package, all of its prototypes are checked. If it is
 * a prototype, all of its methods are checked. If it is a method or field,
 * the name is checked. The checking is rather arbitrary, just for explaining the idea. Change
 * it at your will.<br>
   @author José

 */
public class CyanMetaobjectCheckStyle extends CyanMetaobjectAtAnnot implements ICheckDeclaration_afterSemAn {

	public CyanMetaobjectCheckStyle() {
		super("checkStyle", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[]{ AttachedDeclarationKind.METHOD_DEC, AttachedDeclarationKind.PROTOTYPE_DEC,
						AttachedDeclarationKind.FIELD_DEC,
						AttachedDeclarationKind.METHOD_SIGNATURE_DEC
						});
	}

	/*
	@Override
	public
	StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {
		System.out.println("semAn_codeToAdd in checkStyle");
		return null;
	}
	*/

	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler_semAn) {
		final IDeclaration declaration = this.getAttachedDeclaration();
		final String name = declaration.getName();
		WrEnv env = compiler_semAn.getEnv();
		switch ( declaration.getKind(null) ) {
//		case PROGRAM_DEC:
//			final WrProgram p = (WrProgram ) declaration;
//			p.accept( new ASTVisitor() {
//				@Override
//				public void visit(CyanPackage node) {
//					checkPackage(node.getI());
//				}
//			});
//			break;
//		case PACKAGE_DEC:
//			checkPackage( (WrCyanPackage ) declaration );
//			break;
		case METHOD_DEC:
			final WrMethodDec method = (WrMethodDec ) declaration;
			final String pp2 = method.getDeclaringObject().getCompilationUnit(env).getCyanPackage().getName() + "." + method.getDeclaringObject().getName();
			checkMethodName(pp2, name, method.getFirstSymbol(compiler_semAn.getEnv()));
			break;
		case METHOD_SIGNATURE_DEC:
			final WrMethodSignature ms = (WrMethodSignature ) declaration;
			final String pp3 = ms.getDeclaringInterface().getCompilationUnit(env).getCyanPackage().getName() + "." + ms.getDeclaringInterface().getName();
			checkMethodName(pp3, name, ms.getFirstSymbol());
			break;
		case PROTOTYPE_DEC:
			final WrPrototype pu = (WrPrototype) declaration;
			checkPrototype(pu, compiler_semAn.getEnv());
			break;
		case FIELD_DEC:
			for (int i = 0; i < name.length(); ++i) {
				final char ch = name.charAt(i);
				if ( ch == '_' ) {
					this.addError(
							((WrFieldDec ) declaration).getFirstSymbol(compiler_semAn.getEnv()),
							    "A variable name should not have underscores");
				}
			}
			break;
		default:
			break;
		}
	}


	/**
	   @param declaration
	   @param name
	 */
//	private void checkPackage(WrCyanPackage aPackage) {
//		checkPackageName(aPackage.getName());
//		aPackage.accept( new ASTVisitor() {
//			@Override
//			public void visit(Prototype node) {
//				checkPrototype(node.getI());
//			}
//		});
//	}


	/**
	   @param declaration
	   @param name
	   @param pu
	 */
	private void checkPrototype(WrPrototype pu, WrEnv wrEnv) {
		final String name = pu.getName();
		pu.accept( new WrASTVisitor() {
			@Override
			public void visit(WrMethodSignatureWithKeywords node, WrEnv env) {
				if ( node.getDeclaringInterface() != null ) {
					final String pp = node.getDeclaringInterface().getCompilationUnit(env).getCyanPackage().getName() + "." +
							node.getDeclaringInterface().getName();
					checkMethodName(pp, node.getName(), node.getFirstSymbol());
				}
				else {
					final String pp = node.getMethod().getDeclaringObject().getCompilationUnit(env).getCyanPackage().getName() + "." +
					          node.getMethod().getDeclaringObject().getName();
					checkMethodName(pp, node.getName(), node.getFirstSymbol());
				}
			}
			@Override
			public void visit(WrMethodSignatureUnary node, WrEnv env) {
				if ( node.getDeclaringInterface() != null ) {
					final String pp = node.getDeclaringInterface().getCompilationUnit(env).getCyanPackage().getName() + "." +
							node.getDeclaringInterface().getName();
					checkMethodName(pp, node.getName(), node.getFirstSymbol());
				}
				else {
					final String pp = node.getMethod().getDeclaringObject().getCompilationUnit(env).getCyanPackage().getName() + "." +
					          node.getMethod().getDeclaringObject().getName();
					checkMethodName(pp, node.getName(), node.getFirstSymbol());
				}
			}
			@Override
			public void visit(WrMethodDec node, WrEnv env) {
				final String pp = node.getDeclaringObject().getCompilationUnit(env).getCyanPackage().getName() + "." +
				          node.getDeclaringObject().getName();
				checkMethodName(pp, node.getName(), node.getFirstSymbol(env));
			}

		}, wrEnv);
		checkPrototypeName( pu.getCompilationUnit(wrEnv).getCyanPackage().getPackageName(), name,
				pu.getFirstSymbol(wrEnv));
	}


	/**
	   @param name
	 */
	private void checkMethodName(String pp, String name, WrSymbol errSymbol) {
		if ( name.contains("__") ) {
			int i = name.indexOf("__");
			while ( i > 0 && i < name.length() ) {
				int j;
				for ( j = i+2; j < name.length(); ++j) {
					if ( name.charAt(j) == ':' ) { break; }
					if ( name.charAt(j) != '_' ) {
						this.addError(errSymbol, "A method name cannot have '__' (two underscovers) unless they are in the end of the method name. See '" + pp + "::" + name + "'");
						return ;
					}
				}
				if ( j >= name.length() - 1 ) { return ; }
				i = j + 1 + name.substring(j+1).indexOf("__");
			}
			return ;

		}
//		for (int i = 0; i < name.length(); ++i) {
//			final char ch = name.charAt(i);
//			if ( ch == '_' ) {
//				/*
//				 * it is legal unless there is something that is not _ till the end of the name
//				 */
//				for (int j = i+1; j < name.length(); ++j) {
//					if ( name.charAt(j) != '_' ) {
//						this.addError(errSymbol, "A method keyword should not use underscore. See '" + pp + "::" + name + "'");
//						return ;
//					}
//				}
//			}
//		}
	}


	/**
	   @param name
	 */
	private void checkPrototypeName(String packageName1, String name, WrSymbol errSymbol) {
		boolean everyCharIsUpperCase = true;
		boolean previousChIsUpperCase = false;
		boolean twoUpperCaseLettersTogether = false;
		for (int i = 0; i < name.length(); ++i) {
			final char ch = name.charAt(i);
			if ( !Character.isUpperCase(ch) ) {
				everyCharIsUpperCase = false;
				twoUpperCaseLettersTogether = false;
			}
			else if ( previousChIsUpperCase ) {
				twoUpperCaseLettersTogether = true;
			}
			else {
				previousChIsUpperCase = true;
			}
		}
		if ( name.length() > 3 ) {
			if ( everyCharIsUpperCase ) {
				this.addError(
						errSymbol, "A prototype name should not be composed of only uppercase letters. See '" + packageName1 + "." + name + "'");
			}
			else if ( twoUpperCaseLettersTogether ) {
				this.addError(errSymbol, "A prototype name should not have two uppercase letters side by side. See '" + packageName1 + "." + name + "'");
			}
		}
	}


	/**
	   @param name
	 */
//	private void checkPackageName(String name) {
//		for (int i = 0; i < name.length(); ++i) {
//			final char ch = name.charAt(i);
//			if ( ch == '_' ) {
//				this.addError("A package name should not have any underscores. '" + name + "' does.");
//			}
//		}
//	}

}
