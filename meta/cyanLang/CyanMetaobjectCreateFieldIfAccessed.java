package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFieldMissing_dsa;
import meta.IAction_afti;
import meta.IAction_dsa;
import meta.ICompiler_afti;
import meta.ICompiler_dsa;
import meta.ISlotInterface;
import meta.MetaHelper;
import meta.Tuple2;
import meta.Tuple3;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrExprSelfPeriodIdent;
import meta.WrFieldDec;
import meta.WrProgramUnit;

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
    implements IActionFieldMissing_dsa, IAction_afti, IAction_dsa {

	public CyanMetaobjectCreateFieldIfAccessed() {
		super("createFieldIfAccessed", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC } );
	}

	String []args;

	@Override
	public
	Tuple3<String, String, StringBuffer> dsa_replaceGetMissingField(
			WrExprSelfPeriodIdent fieldToGet, WrEnv env) {

//		String fieldSelfName = fieldToGet.asString().substring(5);
//		boolean found = false;
//		int size = this.getMetaobjectAnnotation().getJavaParameterList().size();
//		args = new String[size];
//		for (int i = 0; i < size; ++i) {
//			args[i] = this.getMetaobjectAnnotation().getJavaParameterList().get(i).toString();
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
				"   throw: ExceptionStr(\"Field " + fieldName + " was not found\")\r\n" +
				"   \r\n" +
				"} eval";
		return new Tuple3<String, String, StringBuffer>("cyan.lang", typeName,
				new StringBuffer(ret) );
	}



	@Override
	public
	StringBuffer dsa_replaceSetMissingField(
			WrExprSelfPeriodIdent fieldToSet, WrExpr rightHandSideAssignment, WrEnv env) {
		String fn = fieldToSet.asString();
		if ( fn.startsWith("self.") ) { fn = fn.substring(5); }
		//String fieldName = fieldToSet.getIdentSymbol().getSymbolString();
		return new StringBuffer( "missingFieldMap at: \"" + fn +
				"\" put: (" + rightHandSideAssignment.asString() +
						");");
	}


	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler) {

		WrProgramUnit proto = (WrProgramUnit ) this.getMetaobjectAnnotation().getDeclaration();
		for ( WrFieldDec iv : proto.getFieldList(compiler.getEnv()) ) {
			String protoName = iv.getType().getFullName();
			compiler.createNewGenericPrototype(this.getMetaobjectAnnotation().getFirstSymbol(),
					compiler.getEnv().getCurrentCompilationUnit(), compiler.getEnv().getCurrentProgramUnit(),
					MetaHelper.cyanLanguagePackageName + ".Function<" + protoName + ">",
					"Error caused by method dsa_codeToAdd of metaobject '" +
							this.getMetaobjectAnnotation().getCyanMetaobject().getName() + "'. "
					);

		}
		return null;
	}


	@Override
	public
	Tuple2<StringBuffer, String> afti_codeToAdd(
			ICompiler_afti compiler, List<Tuple2<WrAnnotation, List<ISlotInterface>>> infoList)  {
		if ( this.metaobjectAnnotation.getMetaobjectAnnotationNumberByKind() == 1 ) {
			return new Tuple2<StringBuffer, String>(new StringBuffer("    var IMap<String, Dyn> missingFieldMap = HashMap<String, Dyn>();\n"),
					"    var IMap<String, Dyn> missingFieldMap;");
		}
		return null;
	}


}
