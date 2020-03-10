package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afti;
import meta.ICompiler_afti;
import meta.ISlotInterface;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrFieldDec;
import meta.WrMethodDec;
import meta.WrMethodSignature;
import meta.WrProgramUnit;

/**
 * A demonstration metaobject. It takes two parameters. The first is the name of a field
 * that will be created. If the second parameter is 'counter', the field, an Int, will be
 * initialized with the number of prototype fields. If the second parameter is 'names',
 * the field, an Array<String>, will be initialized with an array with the names of the fields.
 *
   @author jose
 */
public class CyanMetaobjectAddFieldInfo extends CyanMetaobjectAtAnnot
implements IAction_afti {

	public CyanMetaobjectAddFieldInfo() {
		super("addFieldInfo", AnnotationArgumentsKind.OneOrMoreParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC } );
	}


	@Override
	public void check() {
		List<Object> paramList = this.getMetaobjectAnnotation().getJavaParameterList();
		if ( paramList == null || paramList.size() != 2 ||
				!(paramList.get(0) instanceof String) || !(paramList.get(1) instanceof String)) {
			this.addError("Annotation '" + this.getName() + "' should take two String parameters");
			return ;
		}
		first = CyanMetaobject.removeQuotes( (String ) paramList.get(0));
		second = CyanMetaobject.removeQuotes( (String ) paramList.get(1));
		if ( !second.equals("counter") && !second.equals("names") ) {
			this.addError("The second parameter to annotation '" + this.getName() + "' should be either 'counter' or 'names'");
		}

	}


	@Override
	public Tuple2<StringBuffer, String> afti_codeToAdd(
			ICompiler_afti compiler, List<Tuple2<WrAnnotation, List<ISlotInterface>>> infoList) {

		String slotStr = "";
		StringBuffer s = new StringBuffer();
		WrProgramUnit pu = (WrProgramUnit ) this.getAttachedDeclaration();
		WrEnv wrenv = compiler.getEnv();
		List<WrFieldDec> fieldList = pu.getFieldList(wrenv);
		int newFields = 0;
		List<String> newFieldsStr = new ArrayList<>();
		int countNewMethod = 0;
		if ( infoList != null ) {
			for ( Tuple2<WrAnnotation, List<ISlotInterface>> t : infoList ) {
				for ( ISlotInterface newSlot : t.f2 ) {
					if ( newSlot instanceof WrFieldDec ) {
						newFieldsStr.add( ((WrFieldDec ) newSlot).getName() );
						++newFields;
					}
					else if ( newSlot instanceof WrMethodSignature ) {
						++countNewMethod;
					}
				}
			}
		}
		if ( second.equals("counter") ) {
			slotStr = "    let Int " + first + ";\n";
			s.append("    let Int " + first + " = " + (fieldList.size() + newFieldsStr.size()) + ";\n");

			String func = "    func counter_" + first + " -> Int";
			slotStr += func + ";\n";
			List<WrMethodDec> msList = pu.getMethodDecList(wrenv);
			s.append( func + " = " + (msList.size() + countNewMethod) + ";\n");
		}
		else {
			slotStr = "    let Array<String> " + first + ";\n";
			int n = newFields + fieldList.size();
			s.append( "    let Array<String> " + first + " = [ " );
			for ( WrFieldDec f : fieldList ) {
				s.append("\"" + f.getName() + "\"");
				if ( --n > 0 ) {
					s.append(", ");
				}
			}
			for ( String strField : newFieldsStr ) {
				s.append("\"" + strField + "\"");
				if ( --n > 0 ) {
					s.append(", ");
				}
			}
			s.append(" ];\n");
		}
		return new Tuple2<StringBuffer, String>(s, slotStr);

	}

	@Override
	public
	boolean runUntilFixedPoint() { return true; }


	private String first;
	private String second;

}
