package meta.designPattern;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrFieldDec;

public class CyanMetaobjectObservable extends CyanMetaobjectAtAnnot implements IAction_afterResTypes  {

	public CyanMetaobjectObservable() {
		super("observable", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC } );

	}

	@Override
	public void check() {
		if ( this.getAnnotation().getExprStatList() != null && this.getAnnotation().getExprStatList().size() == 0 ) {
			addError("This metaobject takes zero parameters");
		}
	}

	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation, List<ISlotSignature>>> infoList)  {

		final List<StringBuffer> tupleList = new ArrayList<>();

		String code = "    Function<String, Nil> notify;\n";

		code     += "    func init: Function<String, Nil> f { \n";
		for ( final WrFieldDec iv : compiler.getFieldList() )
			code += "        self." + iv.getName() + " = \"nil\";\n";
		code     += "        self.notify = f;\n";
		code     += "    }\n";

		String strSlotList = "    Function<String, Nil> notify;\n    func init: Function<String, Nil> f; \n";
		for ( final WrFieldDec iv : compiler.getFieldList() ) {

			String name = iv.getName().toLowerCase();
			name = Character.toString(name.charAt(0)).toUpperCase()+name.substring(1);

			strSlotList += "    func set" + name  + ": String s;\n";
			code += "    func set" + name  + ": String s {\n";
			code += "        self." + iv.getName() + " = s;\n";
			code += "        notify eval: s;\n";
			code += "    }\n";
		}
		tupleList.add(new StringBuffer(code));
		return new Tuple2<StringBuffer, String>(new StringBuffer(code),
				strSlotList);
	}



}
