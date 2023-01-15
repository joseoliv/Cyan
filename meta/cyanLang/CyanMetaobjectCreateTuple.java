package meta.cyanLang;

import java.util.ArrayList;
import java.util.List;
import lexer.Lexer;
import meta.AnnotationArgumentsKind;
import meta.AttachedDeclarationKind;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_afterResTypes;
import meta.ICompiler_afterResTypes;
import meta.ISlotSignature;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.WrAnnotation;
import meta.WrEnv;
import meta.WrPrototype;
import meta.WrSymbol;
import meta.WrType;
import meta.WrTypeJavaClass;

/**
 * Create code for prototypes for tuples, <code>Tuple{@literal<}T1, T2, ... Tn></code>.
   @author jose
 */
public class CyanMetaobjectCreateTuple extends CyanMetaobjectAtAnnot
      implements
          //IAction_parsing,
          IAction_afterResTypes {

	public CyanMetaobjectCreateTuple() {
		super("createTuple", AnnotationArgumentsKind.ZeroParameters,
				new AttachedDeclarationKind[] { AttachedDeclarationKind.PROTOTYPE_DEC },
				Token.PRIVATE );
	}



	@Override
	public Tuple2<StringBuffer, String> afterResTypes_codeToAdd(
			ICompiler_afterResTypes compiler, List<Tuple2<WrAnnotation,
			List<ISlotSignature>>> infoList) {

		List<List<String>> strListList = compiler.getGenericPrototypeArgListList();
		String fullPrototypeName = compiler.getCurrentPrototypeName();
		int indexOfLessThan = fullPrototypeName.indexOf('<');

		if ( ! compiler.getCurrentPrototypeName().startsWith("Tuple<") ||
				strListList == null || indexOfLessThan < 0 ) {
			compiler.error(this.getAnnotation().getFirstSymbol(),
					"This metaobject should only be used in generic prototype Tuple");
			return null;
		}
		if ( strListList.size() != 1 ) {
			compiler.error(this.getAnnotation().getFirstSymbol(),
					"This generic prototype should be instantiated with just one pair of '<' and '>' as in 'Tuple<Int, String>'");
			return null;
		}
		List<String> strList = strListList.get(0);
		boolean isNTuple = false;
		for ( String str : strList ) {
			int indexOfDot = str.indexOf('.');
			if ( Character.isLowerCase(str.charAt(0)) && indexOfDot < 0 ) {
				   // found a symbol such as 'key' and 'value' in Tuple<key, Int, value, String>
				   // if indexOfDot >= 0 then 'str' would be a prototype preceded by a package
				isNTuple = true;
			}
		}
		List<String> typeList;
		String []fieldList;
		int size;
		int i;

		if ( isNTuple ) {
			// NTuple
			if ( strList.size() %2 != 0 ) {
				compiler.error(this.getAnnotation().getFirstSymbol(), "The number of generic parameters to this generic prototype should be even");
				return null;
			}
			typeList = new ArrayList<>();
			size = strList.size()/2;
			fieldList = new String[size];
			int k = 0;
			for ( String str : strList ) {
				if ( k%2 == 0 ) {
					if ( ! Character.isLowerCase(str.charAt(0)) || str.indexOf('.') >= 0 ) {
						compiler.error(this.getAnnotation().getFirstSymbol(),
								"The " + k + "-th parameter to this generic prototype should start with a lower case letter");
						return null;
					}
					else
						fieldList[k/2] = str;
				}
				else {
					if ( ! Character.isUpperCase(str.charAt(0)) && str.indexOf('.') < 0 )  {
						compiler.error(this.getAnnotation().getFirstSymbol(),
								"The " + k + "-th parameter to this generic prototype should start with an upper case letter");
						return null;
					}
					else
						typeList.add(str);
				}
				++k;
			}
		}
		else {
			// Tuple
			typeList = strListList.get(0);
			size = typeList.size();
			fieldList = new String[size];
			for (i = 0; i < size; ++i) {
				fieldList[i] = "f" + (i+1);
			}
		}
		boolean hasJavaTypeParam = false;
		WrEnv env = compiler.getEnv();
		WrSymbol first = this.annotation.getFirstSymbol();
		for ( String aType : typeList ) {
			WrPrototype pu = compiler.getCompilationUnit().getPublicPrototype();

			WrType paramType = env.searchPackagePrototype(aType, first);
			if ( paramType instanceof WrTypeJavaClass || paramType instanceof meta.WrTypeJavaRefArray ) {
				hasJavaTypeParam = true;
			}
		}


		StringBuffer s = new StringBuffer();
		StringBuffer ai = new StringBuffer();
		StringBuffer tmp = new StringBuffer();
		tmp.append("    func init: ");
		for (i = 0; i < size; ++i) {
			String tn = typeList.get(i);
			tmp.append(tn + " g" + (i+1));
			if ( i < size - 1 )
				tmp.append(", ");
		}
		ai.append(tmp);
		s.append(tmp + " { \n");
		tmp.setLength(0);
		for (i = 0; i < size; ++i) {
			s.append("        _" + fieldList[i] + " = g" + (i+1) + ";\n");
		}
		s.append("    }\n");

		if ( size > 1 ) {
			tmp.append("    func ");
			for (i = 0; i < size; ++i) {
				String tn = typeList.get(i);
				tmp.append(fieldList[i] + ": " + tn + " g" + (i+1) + " ");
			}
			tmp.append("-> Tuple<");
			String s2 = "";
			for(i = 0; i < size; ++i) {
				if ( isNTuple ) {
					s2 += fieldList[i] + ", ";
				}
			    s2 += typeList.get(i);
				if ( i < size - 1)
					s2 += ", ";
			}
			tmp.append(s2 + "> ");
			s.append(tmp + "{ \n" );
			ai.append(tmp);
			tmp.setLength(0);

			s.append("        return Tuple<" + s2 + "> new: ");
			for (i = 0; i < size; ++i) {
				s.append(" g" + (i+1));
				if ( i < size - 1 )
					s.append(", ");
			}
			s.append(";\n");


			s.append("    }\n");

		}
		for (i = 0; i < size; ++i) {
			String tn = typeList.get(i);

			tmp.setLength(0);
			tmp.append(" var " + tn + " _" + fieldList[i] + " ");
			s.append("    @annot( #" + fieldList[i] + " )" +
			    tmp + "\n");
			ai.append(tmp);
			tmp.setLength(0);
			tmp.append("    func " + fieldList[i] + " -> " + tn);
			s.append(tmp + " = _" + fieldList[i] + ";\n");
			ai.append(tmp);
			tmp.setLength(0);
			tmp.append("    func " + fieldList[i] + ": " + tn + " other ");
			s.append(tmp +
			   " { _" + fieldList[i] + " = other }\n");
			ai.append(tmp);
			tmp.setLength(0);
		}
		fullPrototypeName = Lexer.addSpaceAfterComma(fullPrototypeName);

		if ( ! hasJavaTypeParam ) {
			s.append("    override\n");
			tmp.append("    func == (Dyn other) -> Boolean ");
			ai.append(tmp);
			s.append(tmp + "{\n");
			s.append("        if other isA: " + fullPrototypeName + " {\n");
			String javaFPN = MetaHelper.getJavaName(fullPrototypeName);
			s.append("            var " + fullPrototypeName + " another;\n");
			s.append("            @javacode{*");
			s.append("             _another = (" + javaFPN + " ) _other;\n");

			s.append("            *}\n");
			for (int k = 0; k < fieldList.length; ++k) {
				s.append("            if " + fieldList[k] + " != (another " + fieldList[k] + ") {  return false }" + "\n");
			}


			s.append("            return true\n");
			s.append("        }\n");
			s.append("        else {\n");
			s.append("            return false\n");
			s.append("        }\n");
			s.append("     }\n");

			tmp.setLength(0);
			tmp.append("    func hashCode -> Int ");
			ai.append(tmp);
			String hashCodeStr = "    override\n" + tmp  + "= ";
			tmp.setLength(0);

			//s.append("            let " + fullPrototypeName + " another = " + fullPrototypeName + " cast: other;\n");
			for (int k = 0; k < fieldList.length; ++k) {
				hashCodeStr += fieldList[k] + " hashCode";
				if ( k < fieldList.length - 1 ) { hashCodeStr += " + "; }
			}

			s.append(hashCodeStr + ";\n\n");
			/*
	         return "[. key = " + key + ", value = " + value+ " .] "
	        */
			s.append("    override");
			tmp.setLength(0);
			tmp.append("    func asString -> String ");
			ai.append(tmp);
			s.append(tmp + "{\n");
			s.append("         return \"[. ");
			for (i = 0; i < size; ++i) {
				String f = fieldList[i];
				if ( typeList.get(i).equals("Nil") || typeList.get(i).equals("cyan.lang.Nil") ) {
					s.append(f + " = Nil\"" );
				}
				/*
				else if ( typeList.get(i).equals("String") || typeList.get(i).equals("cyan.lang.String") ) {
					  //     return "[. key = \"" ++ key\" ++ ", value = " ++ value ++ " .]"
					  //     return "[. key = \"" ++ key ++ "\" ++ ", value = " ++ value ++ " .]"
					s.append(f + " = \\\"\" ++ " + f + " ++ \"\\\"\"");
				}
				*/
				else {
					s.append(f + " = \" ++ " + f + " asStringQuoteIfString");
				}
				if ( i < size - 1 )
					s.append(" ++ \", ");
			}
			s.append(" ++ \" .]\"\n");
			s.append("     }\n");
		}


		return new Tuple2<StringBuffer, String>(s, ai.toString());
	}
}



