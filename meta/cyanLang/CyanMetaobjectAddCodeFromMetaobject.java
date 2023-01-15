package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFunction;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.IStayPrototypeInterface;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.Tuple3;
import meta.WrAnnotation;
import meta.WrAnnotationAt;
import meta.WrEnv;
import meta.WrGenericParameter;
import meta.WrPrototype;
import meta.WrType;

public class CyanMetaobjectAddCodeFromMetaobject extends CyanMetaobjectAtAnnot
  implements IAction_afterResTypes, IStayPrototypeInterface {

	public CyanMetaobjectAddCodeFromMetaobject() {
		super("addCodeFromMetaobject", AnnotationArgumentsKind.ZeroParameters, new AttachedDeclarationKind[] {
				AttachedDeclarationKind.PROTOTYPE_DEC }, Token.PRIVATE );
	}


	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList) {

		StringBuffer s = null;




		WrPrototype currentPrototype = (WrPrototype ) this.getAnnotation().getDeclaration();
		WrEnv env = compiler.getEnv();
		if ( currentPrototype.isGeneric(env) ) { return null; }
		List<List<WrGenericParameter>> gpListList = currentPrototype.getGenericParameterListList(env);
		if ( gpListList == null || gpListList.size() == 0 ) {
			this.addError("This annotation should only be attached to a generic prototype");
			return null;
		}
		WrGenericParameter gp = gpListList.get(0).get(0);
		WrType tgp = gp.getType();
		if ( !(tgp instanceof WrPrototype) || ((WrPrototype ) tgp).isInterface() ) {
			return null;
		}
		String outerGenericPrototypeName = currentPrototype.getFullName();
		outerGenericPrototypeName = outerGenericPrototypeName.substring(0, outerGenericPrototypeName.indexOf('<'));
		if ( outerGenericPrototypeName.startsWith(MetaHelper.cyanLanguagePackageNameDot) ) {
			outerGenericPrototypeName = outerGenericPrototypeName.substring(MetaHelper.cyanLanguagePackageNameDot.length());
		}
		WrPrototype proto = (WrPrototype ) tgp;
		String innerGenericPrototypeName = proto.getFullName();

		String tagInfo = null;
		List<List<WrGenericParameter>> gpListList2 = proto.getGenericParameterListList(env);
		if ( gpListList2 == null || gpListList2.size() == 0 ) {
			/*
			 * not something as Array<Array<String>>. Should be just Array<String>
			 */
			tagInfo = outerGenericPrototypeName;
		}
		else {
			/*
			 * something as Array<Array<String>>. gp2 would be String
			 */
			if ( gpListList2.get(0).size() != 1 ) {
				return null;
			}
			WrGenericParameter gp2 = gpListList2.get(0).get(0);
			WrType tgp2 = gp2.getType();
			if ( ! tgp2.isObjectDec() ) {
			//if ( !(tgp2 instanceof ObjectDec) ) {
				return null;
			}
			WrPrototype proto2 = (WrPrototype ) tgp2;
			List<List<WrGenericParameter>> gpListList3 = proto2.getGenericParameterListList(env);
			if ( gpListList3 != null && gpListList3.size() != 0 ) {
				/**
				 * Array<Array<Array<T>>>
				 */
				return null;
			}
			else {
				/*
				 * is proto2 a prototype created by the compiler through an instantiation of
				 * a generic prototype?
				 */
				//#$ {

//				boolean compilerCreated = false;
//				List<WrAnnotationAt> annotList =
//						proto.getAttachedAnnotationList(compiler.getEnv());

//				if ( annotList != null ) {
//					for ( WrAnnotationAt annot : annotList ) {
//						if ( annot.getCyanMetaobject() instanceof CyanMetaobjectGenericPrototypeInstantiationInfo ) {
//							compilerCreated = true;
//						}
//					}
//				}
//				if ( ! compilerCreated ) {
//					return null;
//				}
				//#$ }

				proto = proto2;


				innerGenericPrototypeName = innerGenericPrototypeName.substring(0, innerGenericPrototypeName.indexOf('<'));
				if ( innerGenericPrototypeName.startsWith(MetaHelper.cyanLanguagePackageNameDot) ) {
					innerGenericPrototypeName = innerGenericPrototypeName.substring(MetaHelper.cyanLanguagePackageNameDot.length());
				}


				tagInfo = outerGenericPrototypeName + " " + innerGenericPrototypeName;
				/*
				boolean found = false;
				for ( String arg : argList ) {
					if ( arg.equals(shouldBeTag) ) {
						tagInfo = arg;
						found = true;
						break;
					}
				}
				if ( ! found) { return null; }
				*/
			}
		}

		if ( ! tagInfo.startsWith(outerGenericPrototypeName) ) {
			/*
			 * the tag was not made for this prototype
			 */
			this.addError("The name of this generic prototype, '" + outerGenericPrototypeName + "', should be equal "
					+ "to the start string of the parameter to the annotation");
			return null;
		}

		String strSlotList = "";
		if ( proto.getAttachedAnnotationList(env) != null ) {
			for ( WrAnnotationAt annot : proto.getAttachedAnnotationList(env) ) {
				CyanMetaobjectAtAnnot metaobject = annot.getCyanMetaobject();
				/**
				 * test if it implements such and such interface. Asks a method to generate code.
				 * metaobject.afterResTypes_codeToAddPrototype(currentPrototype)
				 */
				if ( metaobject instanceof IActionFunction ) {

					IActionFunction checkProduce = (IActionFunction ) metaobject;
					Object obj = checkProduce.eval(this);
					if ( obj instanceof Tuple3 ) {

						@SuppressWarnings("unchecked")
						Tuple3<String, String, Object> t = (Tuple3<String, String, Object> ) obj;
						if ( t.f1.equalsIgnoreCase(tagInfo) ) {
							Object info = t.f3;
							if ( info instanceof StringBuffer ) {
								StringBuffer codeToAdd = (StringBuffer ) info;
								strSlotList += " " + t.f2;
								if ( s == null ) {
									s = new StringBuffer();
								}
								s.append("\n");
								s.append(codeToAdd);
								s.append("\n");
							}
						}
					}
				}

			}
		}
		if ( s != null ) {
			/*
			 * Tuple2<StringBuffer, String>
             */
			Tuple2<StringBuffer, String> t = new Tuple2<StringBuffer, String>(s, strSlotList);
			return t;
		}
		else {
			return null;
		}
	}

}
