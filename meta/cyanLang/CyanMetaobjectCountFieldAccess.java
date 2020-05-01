package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IActionFieldAccess_semAn;
import meta.IAction_afterResTypes;
import meta.IAction_semAn;
import meta.ICompiler_afterResTypes;
import meta.ICompiler_semAn;
import meta.ISlotSignature;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrExpr;
import meta.WrFieldDec;
import meta.WrPrototype;

public class CyanMetaobjectCountFieldAccess extends CyanMetaobjectAtAnnot
     implements IActionFieldAccess_semAn, IAction_afterResTypes, IAction_semAn {

	public CyanMetaobjectCountFieldAccess() {
		super("countFieldAccess", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.FIELD_DEC } );
	}

	@Override
	public
	StringBuffer semAn_replaceGetField(WrExpr fieldToGet, WrEnv env) {
		String id = fieldToGet.asString();
		if ( id.startsWith("self.") ) {
			id = id.substring(5);
		}
		return new StringBuffer(" { ++countFieldAccess_"+ id + "; ^" + id + " } eval ") ;
	}

	@Override
	public
	StringBuffer semAn_replaceSetField(WrExpr fieldToSet, WrExpr rightHandSideAssignment, WrEnv env) {
		String id = fieldToSet.asString();
		String id2 = id;
		if ( id.startsWith("self.") ) {
			id2 = id.substring(5);
		}
		return new StringBuffer(" ++countFieldAccess_"+ id2 + "; \n " +
		          id + " = " + rightHandSideAssignment.asString() + ";") ;
	}

	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler) {


		WrPrototype proto = compiler.getEnv().getCurrentPrototype();
		for ( WrFieldDec iv : proto.getFieldList(compiler.getEnv()) ) {
			String protoName = iv.getType().getFullName();
			compiler.createNewGenericPrototype(this.getAnnotation().getFirstSymbol(),
					compiler.getEnv().getCurrentCompilationUnit(), proto,
					MetaHelper.cyanLanguagePackageName + ".Function<" + protoName + ">",
			            "Error caused by method semAn_codeToAdd of metaobject '" +
			            		this.getAnnotation().getCyanMetaobject().getName() + "'. "
			            );

		}
		return null;
	}


	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList)  {
		String countField = "ountFieldAccess_" + this.getAttachedDeclaration().getName();
		return new Tuple2<StringBuffer, String>(new
				StringBuffer("    var Int c" + countField + " = 0;\n    func getC" + countField +
						" -> Int = c" + countField + ";\n"),
				"   var Int c" + countField + ";\n    func getC" + countField + " -> Int");
		}

}

