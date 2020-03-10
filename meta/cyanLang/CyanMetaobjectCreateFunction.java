package meta.cyanLang;

import java.util.List;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectAtAnnot;
import meta.ICompilerAction_dpa;
import meta.MetaHelper;


/**
 * This metaobject creates methods for prototypes of anonymous functions: <code>Function{@literal<}T1, T2, ... Tn></code>.
   @author jose
 */
public class CyanMetaobjectCreateFunction extends CyanMetaobjectAtAnnot
       implements meta.IAction_dpa {



	public CyanMetaobjectCreateFunction() {
		super("createFunction", AnnotationArgumentsKind.ZeroParameters);
	}

	@Override
	public StringBuffer dpa_codeToAdd(ICompilerAction_dpa compiler) {
		StringBuffer s = new StringBuffer();

		/*
		List<Object> javaParamList = this.getMetaobjectAnnotation().getJavaParameterList();
		Object first = javaParamList.get(0);
		if ( !(first instanceof String) ) {
			compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), "Parameter 'u' or 'r' expected to this metaobject annotation",
					this.getMetaobjectAnnotation().getFirstSymbol().getSymbolString(), ErrorKind.metaobject_error);
			return s;
		}
		char param = ((String ) first).charAt(0);
		if ( param != 'u' && param != 'r' ) {
			compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), "Parameter 'u' or 'r' expected to this metaobject annotation",
					this.getMetaobjectAnnotation().getFirstSymbol().getSymbolString(), ErrorKind.metaobject_error);
			return s;
		}
		*/

		List<List<String>> strListList = compiler.getGenericPrototypeArgListList();
		if ( strListList == null ) {
			compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), "Metaobject '" + getName() + "' should only be used in a generic prototype");
			return null;
		}

		/*
		if ( compiler.getCurrentPrototypeName().startsWith("UFunction") ) {
			s.append("    extends Function");
			for ( List<String> sList : strListList ) {
				s.append("<");
				int sizeS = sList.size();
				for ( String ss : sList ) {
					s.append(ss);
					if ( --sizeS > 0 )
						s.append(", ");
				}
				s.append(">");
			}
			s.append("\n");
		}
		*/


		int size = 0;
		int sizeListList = strListList.size();


		if ( strListList.size() < 1 ) {
			compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(),
					"This metaobject should only be used in generic prototype cyan.lang.Function or cyan.lang.UFunction");
			return s;
		}
		else if ( strListList.size() == 1 && strListList.get(0).size() == 1 ) {
			s.append("    abstract func eval -> " + strListList.get(0).get(0) + "\n");
		}
		else {


			int count = 0;
			s.append("    abstract func ");

			for ( List<String> strList  : strListList ) {
				size = strList.size();
				if ( sizeListList > 1 && count == sizeListList - 1 && size == 1 ) {
					if ( sizeListList > 1 ) {
						compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(), "The last pair of '<' and '>' should have at least two arguments");
						return new StringBuffer();
					}

				}
				s.append("eval: ");

				if ( count == sizeListList - 1 ) {
					/*
					 * last pair of < and >. The return type is the last parameter. It should not be added to 's'
					 */
					if ( size > 2 )
						s.append("(");
					for (int i = 0; i < size - 1; ++i) {
						String typeName = strList.get(i);
						if ( typeName.equals(MetaHelper.noneArgumentNameForFunctions) ) {
							if ( size != 2 ) {
								compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(),
										"The '" + MetaHelper.noneArgumentNameForFunctions + "' argument for a function type should be alone between "
										+ " '<' and '>' as in 'Function<none><Int><none>', except in the last pair of '<' and '>'. This type represents a function with method 'eval: eval: Int eval:'. "
											+ "It would be illegal to write 'Function<none, Int, String>'");
								return new StringBuffer();
							}
							// appends nothing
						}
						else {
							s.append( typeName );
						}
						if ( i < size - 2 )
							s.append(", ");
					}
					if ( size > 2 )
						s.append(")");
				}
				else {
					if ( size > 1 )
						s.append("(");
					for (int i = 0; i < size; ++i) {

						String typeName = strList.get(i);
						if ( typeName.equals(MetaHelper.noneArgumentNameForFunctions) ) {
							if ( size != 1) {
								compiler.error(this.getMetaobjectAnnotation().getFirstSymbol(),
										"The '" + MetaHelper.noneArgumentNameForFunctions + "' argument for a function type should be alone between "
										+ " '<' and '>' as in 'Function<none><Int><none>', except in the last pair of '<' and '>'. This type represents a function with method 'eval: eval: Int eval:'. "
											+ "It would be illegal to write 'Function<none, Int, String>'");
								return new StringBuffer();
							}
							// appends nothing
						}
						else {
							s.append( typeName );
						}
						if ( i < size - 1 )
							s.append(", ");

					}
					if ( size > 1 )
						s.append(")");
				}

				s.append(" ");


				++count;
			}
			s.append("-> " +
			  strListList.get(sizeListList-1).get(
					  strListList.get(sizeListList-1).size()-1)
					   + "\n");

			List<String> strList;
			if ( sizeListList == 1 ) {
				strList = strListList.get(0);
				size = strList.size();

				if ( size != 2 || ! strList.get(0).equals(MetaHelper.noneArgumentNameForFunctions) ) {
					/*
					 * if size == 2 and the first parameter is 'none', there should be no
					 * curry: method. The eval: method does not take parameters.
					 */
					for (int j = 0; j < size - 1; ++j) {
						s.append("    func curry: ");
						for (int i = 0; i <= j; ++i) {
							s.append(strList.get(i) + " t" + (i+1));
							if ( i < j )
								s.append(", ");
						}
						s.append(" -> Function<");
						for (int k = j+1; k < size - 1; ++k ) {
							s.append(strList.get(k) + ", ");
						}
						s.append(strList.get(size-1) + "> {\n");
						s.append("        return { (: ");
						for ( int k = j+1; k < size - 1; ++k ) {
							s.append(strList.get(k) + " t" + (k+1));
							if ( k < size - 2 )
								s.append(", ");
						}

						s.append(" -> " + strList.get(size-1) + " :) ");

						s.append("^ self eval: ");
						for (int k = 0; k < size - 1; ++k) {
							s.append("t" + (k+1));
							if ( k < size - 2)
								s.append(", ");
						}
						s.append(" }\n");
						s.append("    }\n");
					}
				}
			}

		}
		if ( sizeListList == 1 ) {
			List<String> strList = strListList.get(0);
			String T1 = strList.get(0);
			if ( strList.size() == 2 && strList.get(0).equals(strList.get(1)) )  {
				s.append("    func |> Function<" + T1 + ", " + T1 + "> f -> Function<" + T1 + ", " + T1 + "> { \n" +
			             "        return { (: " + T1 + " elem :) ^ f eval: (self eval: elem) }\n" +
                         "    }\n");
				s.append("    func nilReturn -> Function<" + T1 + ", Nil> = { (: "  + T1 + " elem :) self eval: elem; };");
			}
			else if ( strList.size() == 1 ) {
				s.append("    func |> Function<" + T1 + ", " + T1 + "> f -> Function<" + T1 + "> { \n" +
			             "        return { ^ f eval: (self eval) }\n" +
                        "    }\n");
			}

		}

		return s;
	}

}
