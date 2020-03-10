package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFieldAccess_dsa;
import meta.IAction_afti;
import meta.IAction_dsa;
import meta.ICompiler_afti;
import meta.ICompiler_dsa;
import meta.ISlotInterface;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrFieldDec;
import meta.WrProgramUnit;

public class CyanMetaobjectCountFieldAccess extends CyanMetaobjectAtAnnot
     implements IActionFieldAccess_dsa, IAction_afti, IAction_dsa {

	public CyanMetaobjectCountFieldAccess() {
		super("countFieldAccess", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.FIELD_DEC } );
	}

	@Override
	public
	StringBuffer dsa_replaceGetField(WrExpr fieldToGet, WrEnv env) {
		String id = fieldToGet.asString();
		if ( id.startsWith("self.") ) {
			id = id.substring(5);
		}
		return new StringBuffer(" { ++countFieldAccess_"+ id + "; ^" + id + " } eval ") ;
	}

	@Override
	public
	StringBuffer dsa_replaceSetField(WrExpr fieldToSet, WrExpr rightHandSideAssignment, WrEnv env) {
		String id = fieldToSet.asString();
		String id2 = id;
		if ( id.startsWith("self.") ) {
			id2 = id.substring(5);
		}
		return new StringBuffer(" ++countFieldAccess_"+ id2 + "; \n " +
		          id + " = " + rightHandSideAssignment.asString() + ";") ;
	}

	@Override
	public StringBuffer dsa_codeToAdd(ICompiler_dsa compiler) {


		WrProgramUnit proto = compiler.getEnv().getCurrentProgramUnit();
		for ( WrFieldDec iv : proto.getFieldList(compiler.getEnv()) ) {
			String protoName = iv.getType().getFullName();
			compiler.createNewGenericPrototype(this.getMetaobjectAnnotation().getFirstSymbol(),
					compiler.getEnv().getCurrentCompilationUnit(), proto,
					MetaHelper.cyanLanguagePackageName + ".Function<" + protoName + ">",
			            "Error caused by method dsa_codeToAdd of metaobject '" +
			            		this.getMetaobjectAnnotation().getCyanMetaobject().getName() + "'. "
			            );

		}
		return null;
	}


	@Override
	public Tuple2<StringBuffer, String> afti_codeToAdd(
			ICompiler_afti compiler, List<Tuple2<WrAnnotation, List<ISlotInterface>>> infoList)  {
		String countField = "ountFieldAccess_" + this.getAttachedDeclaration().getName();
		return new Tuple2<StringBuffer, String>(new
				StringBuffer("    var Int c" + countField + " = 0;\n    func getC" + countField +
						" -> Int = c" + countField + ";\n"),
				"   var Int c" + countField + ";\n    func getC" + countField + " -> Int");
		}

}

