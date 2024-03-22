package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFieldMissing_semAn;
import meta.IAction_afterResTypes;
import meta.IAction_semAn;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_semAn;
import meta.ISlotSignature;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple3;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprSelfPeriodIdent;
import meta.WrFieldDec;
import meta.WrPrototype;

/**
 *  Annotation 'createFieldIfAccessed' should be attached to a prototype. The associated metaobject
 * will simulates the existence of fields using a hash table.
 *
 * If the prototype does not declare field 'iv' and 'self.iv' is used in the prototype,
 * it is replaced by a get or set in the hash table. The type of 'iv' will be considered 'Int' if
 * its name starts with 'i', 'String' if it starts with 's', or 'Dyn' otherwise.
   @author jose
 */
public class CyanMetaobjectCreateFieldIfAccessed extends CyanMetaobjectAtAnnot
    implements IActionFieldMissing_semAn, IAction_afterResTypes, IAction_semAn {

	public CyanMetaobjectCreateFieldIfAccessed() {
		super("createFieldIfAccessed", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC } );
	}

	String []args;

	@Override
	public
	Tuple3<String, String, StringBuffer> semAn_replaceGetMissingField(
			WrExprSelfPeriodIdent fieldToGet, WrEnv env) {

//		String fieldSelfName = fieldToGet.asString().substring(5);
//		boolean found = false;
//		int size = this.getAnnotation().getJavaParameterList().size();
//		args = new String[size];
//		for (int i = 0; i < size; ++i) {
//			args[i] = this.getAnnotation().getJavaParameterList().get(i).toString();
//			if ( args[i].equals(fieldSelfName) ) {
//				found = true;
//			}
//		}
//		if ( !found ) {
//			return null;
//		}

		//String fieldName = fieldToGet.getIdentSymbol().getSymbolString();
		String fieldName = fieldToGet.asString();
		if ( fieldName.startsWith("self.") ) { fieldName = fieldName.substring(5); }
		String typeName = "Dyn";
		if ( fieldName.charAt(0) == 'i' ) {
			typeName = "Int";
		}
		else if ( fieldName.charAt(0) == 's' ) {
			typeName = "String";
		}  // (Cast<Int> asReceiver: (missingFieldMap get: "iv") )

		String ret; //  = "(Cast<" + typeName + "> asReceiver: (missingFieldMap get: \"" + fieldName + "\") )" ;
		String tmpName = env.getNewUniqueVariableName();
		ret = "{   \r\n" +
				"   cast " + typeName + " " + tmpName + " = missingFieldMap get: \"" + fieldName + "\" {\r\n" +
				"       ^ " + tmpName + "\r\n" +
				"   }\r\n" +
				"   throw ExceptionStr(\"Field " + fieldName + " was not found\")\r\n" +
				"   \r\n" +
				"} eval";
		return new Tuple3<String, String, StringBuffer>("cyan.lang", typeName,
				new StringBuffer(ret) );
	}



	@Override
	public
	StringBuffer semAn_replaceSetMissingField(
			WrExprSelfPeriodIdent fieldToSet, WrExpr rightHandSideAssignment, WrEnv env) {
		String fn = fieldToSet.asString();
		if ( fn.startsWith("self.") ) { fn = fn.substring(5); }
		//String fieldName = fieldToSet.getIdentSymbol().getSymbolString();
		return new StringBuffer( "missingFieldMap at: \"" + fn +
				"\" put: (" + rightHandSideAssignment.asString() +
						");");
	}


	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler) {

		WrPrototype proto = (WrPrototype ) this.getAnnotation().getDeclaration();
		for ( WrFieldDec iv : proto.getFieldList(compiler.getEnv()) ) {
			String protoName = iv.getType().getFullName();
			compiler.createNewGenericPrototype(this.getAnnotation().getFirstSymbol(),
					compiler.getEnv().getCurrentCompilationUnit(), compiler.getEnv().getCurrentPrototype(),
					MetaHelper.cyanLanguagePackageName + ".Function<" + protoName + ">",
					"Error caused by method semAn_codeToAdd of metaobject '" +
							this.getAnnotation().getCyanMetaobject().getName() + "'. "
					);

		}
		return null;
	}


	@Override
	public
	Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList)  {
		if ( this.annotation.getAnnotationNumberByKind() == 1 ) {
			return new Tuple2<StringBuffer, String>(new StringBuffer("    var IMap<String, Dyn> missingFieldMap = HashMap<String, Dyn>();\n"),
					"    var IMap<String, Dyn> missingFieldMap;");
		}
		return null;
	}


}
