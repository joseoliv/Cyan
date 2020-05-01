package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICheckDeclaration_afterSemAn;
import meta.ICheckSubprototype_afterSemAn;
import meta.ICompiler_semAn;
import meta.IDeclaration;
import meta.MetaHelper;
import meta.Token;
import meta.WrAnnotationAt;
import meta.WrCyanPackage;
import meta.WrEnv;
import meta.WrFieldDec;
import meta.WrProgram;
import meta.WrPrototype;
import meta.WrType;

public class CyanMetaobjectImmutable extends CyanMetaobjectAtAnnot
          implements ICheckDeclaration_afterSemAn, ICheckSubprototype_afterSemAn   {

	public CyanMetaobjectImmutable() {
		super("immutable", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] {
						AttachedDeclarationKind.PROTOTYPE_DEC,
						}, Token.PUBLIC );
	}

	/**
	 * subprototypes of an immutable prototype should be immutable too
	 */

	@Override
	public void afterSemAn_checkSubprototype(ICompiler_semAn compiler_semAn, WrPrototype subPrototype) {

		final IDeclaration dec = this.getAttachedDeclaration();
		if ( dec instanceof WrPrototype ) {

			final boolean isSubProtoImmutable = isAnnotatedWithImmutable(
					subPrototype, compiler_semAn.getEnv());
			if ( ! isSubProtoImmutable ) {
				this.addError(subPrototype.getFirstSymbol(compiler_semAn.getEnv()), "Prototype '"
						+ subPrototype.getFullName() + "' inherits from "
						+ "a immutable prototype, '" + ((WrPrototype ) dec).getFullName() +
						". It should also be annotated with @immutable");
			}
		}
	}


	/**
	   @param subProto
	   @return
	 */
	private static boolean isAnnotatedWithImmutable(WrPrototype subProto, WrEnv env) {
		boolean isSubProtoImmutable = false;
		final List<WrAnnotationAt> annotList =
				subProto.getAttachedAnnotationList(env);
		if ( annotList != null ) {
			for ( final WrAnnotationAt mo : annotList ) {
				if ( mo.getCyanMetaobject() instanceof CyanMetaobjectImmutable ) {
					isSubProtoImmutable = true;
					break;
				}
			}
		}
		return isSubProtoImmutable;
	}

	@Override
	public void afterSemAn_checkDeclaration(ICompiler_semAn compiler_semAn) {

		final IDeclaration dec = this.getAttachedDeclaration();
		if ( dec instanceof WrPrototype ) {
			checkImmutabilityObjectDec( (WrPrototype ) dec, WhoAsked.prototypeAsked, compiler_semAn.getEnv());
		}
		else if ( dec instanceof WrCyanPackage ) {
			checkImmutabilityCyanPackage( (WrCyanPackage ) dec, WhoAsked.packageAsked );
		}
		else if ( dec instanceof WrProgram ) {
			checkImmutabilityProgram( (WrProgram ) dec);
		}
	}
//	@Override
	//public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler_semAn) {}

	private void checkImmutabilityProgram(WrProgram program) {
//		program.accept( new ASTVisitor() {
//			@Override
//			public void visit(CyanPackage node)  {
//				checkImmutabilityCyanPackage(node.getI(), WhoAsked.packageAsked);
//			}
//		});
	}


	private void checkImmutabilityCyanPackage(WrCyanPackage cyanPackage, WhoAsked whoAsked) {

//		if ( !cyanPackage.getName().equals("cyan.lang") ) {
//			cyanPackage.accept( new ASTVisitor() {
//				@Override
//				public void visit(ObjectDec node)  {
//					if ( ! isAnnotatedWithImmutable(node.getI()) ) {
//						addError(node.getFirstSymbol().getI(), "Prototype '" + node.getFullName() +
//								"' belongs to a package marked as immutable by an annotation. " +
//								"It should also be annotated with @immutable too");
//
//					}
//					checkImmutabilityObjectDec(node.getI(), whoAsked);
//				}
//			});
//		}
	}

	enum WhoAsked { programAsked, packageAsked, prototypeAsked }
	/**
	   @param dec
	 */
	private void checkImmutabilityObjectDec(WrPrototype proto, WhoAsked whoAsked, WrEnv env) {

		//proto.setImmutable(true, env);

		final WrPrototype superProto = proto.getSuperobject(env);
		if (  superProto != null && !superProto.getName().equals("Any") ) {
			if ( !isAnnotatedWithImmutable(superProto, env) ) {
				this.addError("This prototype is immutable. It should either be 'Any' or its superprototype should be immutable too");
			}
		}
		for ( final WrFieldDec iv : proto.getFieldList(env) ) {
			if ( !iv.isReadonly() ) {
				switch ( whoAsked ) {
				case programAsked:
					this.addError(iv.getFirstSymbol(env), "The program of prototype '" + proto.getFullName() + "' is declared with annotation '"
							+ this.getName() + "'. Therefore all of its fields should be read only (declared with 'let')");
					break;
				case packageAsked:
					this.addError(iv.getFirstSymbol(env), "The package of prototype '" + proto.getFullName() + "' is declared with annotation '"
							+ this.getName() + "'. Therefore all of its fields should be read only (declared with 'let'). See the project file of the program.");
					break;
				case prototypeAsked:
					this.addError(iv.getFirstSymbol(env), "Prototype '" + proto.getFullName() + "' is declared with annotation '"
							+ this.getName() + "'. Therefore all fields should be read only (declared with 'let')");
				}
			}
			else {
				final String ivTypeName = iv.getType().getName();
				if ( ! MetaHelper.isBasicType(ivTypeName) && ! ivTypeName.equals("Nil") ) {
					final WrType ivType = iv.getType();
					if ( ivType.isInterface() || ! ivType.isObjectDec() ) {
					// if (  (ivType instanceof InterfaceDec) || !(ivType instanceof ObjectDec) ) {
						switch ( whoAsked ) {
						case programAsked:
							this.addError(iv.getFirstSymbol(env), "The program of prototype '" + proto.getFullName() + "' is declared with annotation '"
									+ this.getName() + "'. Therefore all fields should have types that were basic types or immutable types");
							break;
						case packageAsked:
							this.addError(iv.getFirstSymbol(env), "The package of prototype '" + proto.getFullName() + "' is declared with annotation '"
									+ this.getName() + "'. Therefore all fields should have types that were basic types or immutable types");
							break;
						case prototypeAsked:
							this.addError(iv.getFirstSymbol(env), "This prototype is declared with annotation '"
									+ this.getName() + "'. Therefore all fields should have types that were basic types or immutable types");
						}
					}
					else {
						final WrPrototype ivProto = (WrPrototype ) ivType;
						boolean error = true;
						if ( ivProto.getAttachedAnnotationList(env) == null ) {
							error = true;
						}
						else {
							for ( final WrAnnotationAt annot : ivProto.getAttachedAnnotationList(env) ) {
								if ( annot.getCyanMetaobject().getName().equals(this.getName()) ) {
									error = false;
									break;
								}
							}
						}
						if ( error ) {
							switch ( whoAsked ) {
							case programAsked:
								this.addError(iv.getFirstSymbol(env), "The program of prototype '" + proto.getFullName() + "' is declared with annotation '"
										+ this.getName() +
										"'. Therefore all fields should have types that were basic types or immutable types."
										+ " field '" + iv.getName() + "' has a type that was not "
												+ "declared with annotation '" + this.getName() + "'");
								break;
							case packageAsked:
								this.addError(iv.getFirstSymbol(env), "The package of prototype '" + proto.getFullName() + "' is declared with annotation '"
										+ this.getName() +
										"'. Therefore all fields should have types that were basic types or immutable types."
										+ " field '" + iv.getName() + "' has a type that was not "
												+ "declared with annotation '" + this.getName() + "'");
								break;
							case prototypeAsked:
								this.addError(iv.getFirstSymbol(env), "This prototype is declared with annotation '"
										+ this.getName() +
										"'. Therefore all fields should have types that were basic types or immutable types."
										+ " field '" + iv.getName() + "' has a type that was not "
												+ "declared with annotation '" + this.getName() + "'");
							}

						}
					}
				}
			}
		}
	}
}

