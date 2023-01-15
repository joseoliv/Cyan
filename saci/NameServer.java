/**
 *
 */
package saci;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ast.ExprIdentStar;
import ast.MessageKeywordWithRealParameters;
import ast.MessageWithKeywords;
import ast.MethodKeywordWithParameters;
import ast.Type;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.MetaHelper;
import meta.Token;
import meta.Tuple2;
import meta.cyanLang.MessageKeywordGrammar;

/** This is the Compiler name server. It has methods to give the
 * names of temporary variables, method, objets, etc.
 *
 * @author José
 *
 */

public class NameServer {

	public static Hashtable<String, String> symbolToAlpha;
	static private Hashtable<String, Class<?>> cyanToJavaClassTable;
	static private Hashtable<String, Class<?>> primitiveJavaToWrapperClassTable;

	static private Hashtable<String, String> fieldNameCyanBasicTypeTable;

	static private Hashtable<String, String> javaBasicTypeToCyan;

	static private Hashtable<String, String> javaWrapperClassToCyanName;

	static private Map<String, Class<?>> javaWrapperClassNameToClass;
	static private Set<String> javaBasicTypes;

	static {
		javaWrapperClassNameToClass = new HashMap<>();
		javaWrapperClassNameToClass.put("Byte", byte.class);
        javaWrapperClassNameToClass.put("Short", short.class);
        javaWrapperClassNameToClass.put("Integer", int.class);
        javaWrapperClassNameToClass.put("Long", long.class);
        javaWrapperClassNameToClass.put("Float", float.class);
        javaWrapperClassNameToClass.put("Double", double.class);
        javaWrapperClassNameToClass.put("Character", char.class);
        javaWrapperClassNameToClass.put("Boolean", boolean.class);

		symbolToAlpha = new Hashtable<String, String>();


		symbolToAlpha.put("!", "exclamation");
		symbolToAlpha.put("?", "interrogation");
		symbolToAlpha.put("@", "at");
		symbolToAlpha.put("#", "numberSign");
		symbolToAlpha.put("$", "dollar");
		symbolToAlpha.put("=", "equal");
		symbolToAlpha.put("%", "percent");
		symbolToAlpha.put("&", "ampersand");
		symbolToAlpha.put("*", "mult");
		symbolToAlpha.put("+", "plus");
		symbolToAlpha.put("/", "slash");

		symbolToAlpha.put("<", "lessThan");
		symbolToAlpha.put("-", "minus");
		symbolToAlpha.put("^", "caret");
		symbolToAlpha.put("~", "tilde");
		symbolToAlpha.put(".", "dot");
		symbolToAlpha.put(":", "colon");
		symbolToAlpha.put(">", "greaterThan");
		symbolToAlpha.put("|", "verticalBar");
		symbolToAlpha.put("\\", "backslash");
		symbolToAlpha.put("(", "leftPar");
		symbolToAlpha.put(")", "rightPar");
		symbolToAlpha.put("[", "leftSquareBracket");
		symbolToAlpha.put("]", "rightSquareBracket");
		symbolToAlpha.put("{", "leftCurlyBracket");
		symbolToAlpha.put("}", "rightCurlyBracket");
		symbolToAlpha.put(",", "comma");



		cyanToJavaClassTable = new Hashtable<>();
		cyanToJavaClassTable.put("Byte", byte.class);
		cyanToJavaClassTable.put("Short", short.class);
		cyanToJavaClassTable.put("Int", int.class);
		cyanToJavaClassTable.put("Long", long.class);
		cyanToJavaClassTable.put("Float", float.class);
		cyanToJavaClassTable.put("Double", double.class);
		cyanToJavaClassTable.put("Char", char.class);
		cyanToJavaClassTable.put("Boolean", boolean.class);
		// cyanToJavaClassTable.put("CySymbol", String.class);
		cyanToJavaClassTable.put("String", String.class);
		cyanToJavaClassTable.put("Any", Object.class);
		cyanToJavaClassTable.put("Nil", Object.class);



		primitiveJavaToWrapperClassTable = new Hashtable<>();
		primitiveJavaToWrapperClassTable.put("byte", Byte.class);
		primitiveJavaToWrapperClassTable.put("short", Short.class);
		primitiveJavaToWrapperClassTable.put("int", Integer.class);
		primitiveJavaToWrapperClassTable.put("long", Long.class);
		primitiveJavaToWrapperClassTable.put("float", Float.class);
		primitiveJavaToWrapperClassTable.put("double", Double.class);
		primitiveJavaToWrapperClassTable.put("char", Character.class);
		primitiveJavaToWrapperClassTable.put("boolean", Boolean.class);


		fieldNameCyanBasicTypeTable = new Hashtable<>();
        fieldNameCyanBasicTypeTable.put("Byte", "b");
        fieldNameCyanBasicTypeTable.put("Short", "n");
        fieldNameCyanBasicTypeTable.put("Int", "n");
        fieldNameCyanBasicTypeTable.put("Long", "n");
        fieldNameCyanBasicTypeTable.put("Float", "n");
        fieldNameCyanBasicTypeTable.put("Double", "n");
        fieldNameCyanBasicTypeTable.put("Char", "c");
        fieldNameCyanBasicTypeTable.put("Boolean", "b");
        // fieldNameCyanBasicTypeTable.put("CySymbol", "s");
        fieldNameCyanBasicTypeTable.put("String", "s");


        javaBasicTypes = new HashSet<>();
        javaBasicTypes.add("byte");
        javaBasicTypes.add("short");
        javaBasicTypes.add("int");
        javaBasicTypes.add("long");
        javaBasicTypes.add("float");
        javaBasicTypes.add("double");
        javaBasicTypes.add("char");
        javaBasicTypes.add("boolean");
        javaBasicTypes.add("String");
        javaBasicTypes.add("void");


        javaBasicTypeToCyan = new Hashtable<>();
        javaBasicTypeToCyan.put("byte", "CyByte");
        javaBasicTypeToCyan.put("short", "CyShort");
        javaBasicTypeToCyan.put("int", "CyInt");
        javaBasicTypeToCyan.put("long", "CyLong");
        javaBasicTypeToCyan.put("float", "CyFloat");
        javaBasicTypeToCyan.put("double", "CyDouble");
        javaBasicTypeToCyan.put("char", "CyChar");
        javaBasicTypeToCyan.put("boolean", "CyBoolean");
        javaBasicTypeToCyan.put("String", "CyString");

        javaBasicTypeToCyan.put("Byte", "CyByte");
        javaBasicTypeToCyan.put("Short", "CyShort");
        javaBasicTypeToCyan.put("Integer", "CyInt");
        javaBasicTypeToCyan.put("Long", "CyLong");
        javaBasicTypeToCyan.put("Float", "CyFloat");
        javaBasicTypeToCyan.put("Double", "CyDouble");
        javaBasicTypeToCyan.put("Character", "CyChar");
        javaBasicTypeToCyan.put("Boolean", "CyBoolean");


        javaWrapperClassToCyanName = new Hashtable<>();
        javaWrapperClassToCyanName.put("Byte", "Byte");
        javaWrapperClassToCyanName.put("Short", "Short");
        javaWrapperClassToCyanName.put("Integer", "Int");
        javaWrapperClassToCyanName.put("Long", "Long");
        javaWrapperClassToCyanName.put("Float", "Float");
        javaWrapperClassToCyanName.put("Double", "Double");
        javaWrapperClassToCyanName.put("Character", "Char");
        javaWrapperClassToCyanName.put("Boolean", "Boolean");
        javaWrapperClassToCyanName.put("String", "String");


	}

	static final public String startDirNameOutputJavaCode = "java-for-";
	static final public String messageClassNotFoundException = " was not found. Jar files used by "
			+ "a metaobject should be put in directory '--meta' of the metaobject package";
	static final public String javaNameAddMethod = MetaHelper.getJavaNameOfMethodWith("add:", 1);
	static final public String javaNameAddMethodTwoParameters = MetaHelper.getJavaNameOfMethodWith("add:", 2);

	static final public String javaNameAtPutMethod = MetaHelper.getJavaNameOfMethodWith("at:", 1, "put:", 1);
	static final public String javaNameAtMethod = MetaHelper.getJavaNameOfMethodWith("at:", 1);
	static final public String javaNameDoesNotUnderstand = MetaHelper.getJavaNameOfMethodWith("doesNotUnderstand:", 2);
	static final public String javaName_asStringThisOnly = MetaHelper.getJavaNameOfMethodWith("asStringThisOnly:", 1);
	static final public String featureTypeJavaName = MetaHelper.getJavaName("Tuple<key, String, value, Any>");
	static final public String featureListTypeJavaName = MetaHelper.getJavaName("Array<Tuple<key, String, value, Any>>");
	static final public String slotFeatureTypeJavaName = MetaHelper
			.getJavaName("Tuple<slotName, String, key, String, value, Any>");
	static final public String slotFeatureListTypeJavaName = MetaHelper
			.getJavaName("Array<Tuple<slotName, String, key, String, value, Any>>");

	static final public String annotListTypeJavaName = MetaHelper.getJavaName("Array<Any>");
	static final public String slotAnnotTypeJavaName = MetaHelper
			.getJavaName("Tuple<slotName, String, value, Any>");
	static final public String slotAnnotListTypeJavaName = MetaHelper
			.getJavaName("Array<Tuple<slotName, String, value, Any>>");



	    // generic prototype instantiations of a prototype "Stack" that is
	   // in directory "util" are created in "util\tmp".
	public static final String temporaryDirName = "--tmp";
	   // generic prototype instantiations of a prototype "Stack" that is
	   // in directory "util" are created in "util\--tmp".
	public static final String dotTemporaryDirName = ".tmp";

	// name of the Cyan language package
	public static final String cyanLanguagePackageName = "cyan.lang";
	public static final String cyanLanguagePackageNameDot = "cyan.lang.";
	public static final String cyanLanguagePackageName_p_Dot = "cyan_p_lang_p_";
	public static final String evalInJava = MetaHelper.getJavaName("eval");
	public static final String evalDotInJava = MetaHelper.getJavaNameOfkeyword("eval:");

	public static final String metaobjectPackageName = "--meta";
	public static final String metaobjectPackageNameCyanCompilerDot = "meta.";
	public static final int    dotClassLength = ".class".length();
	public static final String cyanMetaobjectClassName = meta.CyanMetaobject.class.getName();
	public static final String directoryNameLinkPastFuture = "--lpf";
	public static final String fileNameAfterSuccessfulCompilation = "afterSuccComp.txt";
	public static final String directoryNamePackageData = "--data";
	public static final String directoryNamePackageTests = "--test";
	public static final String directoryNamePackageDSL = "--dsl";
	public static final String directoryNamePackagePrototypeTmp = "--tmp";
	public static final String directoryNameProgramPackagePrototypeDoc = "--doc";
	// is given in the project file, package cyan.lang should be compiled
	// so in another compilation it does not need to be compiled again
	public static final String compilePackageCyanLang = "compilePackageCyanLang";
	public static final String fileName_of_interfacesCompiledPrototypes = "allInterfaces.iyan";



	public static final String fileSeparatorAsString = java.io.File.separator; // System.getProperty("file.separator");
	public static final char fileSeparator = java.io.File.separatorChar;
	public static final String pathSeparatorAsString = java.io.File.pathSeparator;
	public static final char pathSeparator = java.io.File.pathSeparatorChar;

			//System.getProperty("file.separator").charAt(0);

	public static final String selfNameInnerPrototypes = "self__";
	public static final String javaSelfNameInnerPrototypes = MetaHelper.getJavaName(selfNameInnerPrototypes);
	public static final String systemJavaName = MetaHelper.getJavaName("System");

	public static final String selfNameContextObject = "newSelf__";
	public static final String javaSelfNameContextObject = MetaHelper.getJavaName(selfNameContextObject);
	public static final String ArrayArrayDynInJava = MetaHelper.getJavaName("Array<Array<Dyn>>");
	public static final String ArrayDynInJava = MetaHelper.getJavaName("Array<Dyn>");


	/**
	 * name of type DYN in Java
	 */
	public static final String javaDynName = "Object";


	/**
	 * the Java name of the prototype Any
	 */
	static public String javaNameObjectAny = MetaHelper.getJavaName("Any");


	static public Class<?> wrapperToBasicClass(String wrapperName) {
		return javaWrapperClassNameToClass.get(wrapperName);
	}

	/*
	 * converts something like  "Proto<Stack<Int>, String><Void>"  into "Proto(Stack(Int),String)(Void)"
	 */
	static public String prototypeNameToFileName(String prototypeName) {
		String r = "";
		for (int i = 0; i < prototypeName.length(); ++i ) {
			char ch = prototypeName.charAt(i);
			if ( ch != ' ' ) {
				switch ( ch ) {
				case '<' : ch = '(';
				   break;
				case '>' : ch = ')';
				}
				r = r + ch;
			}
		}
		return r;
	}


	public static String fileNameToPrototypeName(String filename) {
		String r = "";
		for (int i = 0; i < filename.length(); ++i ) {
			char ch = filename.charAt(i);
			if ( ch != ' ' ) {
				switch ( ch ) {
				case '(' : ch = '<';
				   break;
				case ')' : ch = '>';
					break;
				case '-' : ch = '|';
				}
				r = r + ch;
			}
		}
		return r;
	}



	static public String getVisibilityString(Token visibility) {
		switch ( visibility ) {
		case PUBLIC:    return "public";
		case PRIVATE:   return "private";
		case PROTECTED: return "protected";
		case PACKAGE:   return "";
		default:        return null;
		}
	}

	static public String nextJavaLocalVariableName() {
		return "tmp" + numberLocalVariable++;
	}

	static public String nextPrototypeOfFunctionName() {
		return "Function" + numberPrototypeOfFunction++;
	}

	/**
	 * return the name of the codeg directory for prototype prototypeName
	 * The codeg information of a prototype "Proto" is stored in directory "--" + codegPrefix + "-" + Proto
	   @param prototypeName
	   @return
	 */
	static public String getCodegDirFor(String prototypeName) {
		return  "--" + prototypeName + NameServer.fileSeparatorAsString + codegPrefix ;
	}

	public static boolean isNameInnerPrototype(String name) {
		return (name.startsWith(functionProtoName) && name.endsWith(endsInnerProtoName)) ||
			   (name.startsWith(methodProtoName) && name.endsWith(endsInnerProtoName));
	}

	public static boolean isNameInnerProtoForMethod(String name) {
		return (name.startsWith(methodProtoName) && name.endsWith(endsInnerProtoName));
	}


	public static boolean isNameInnerProtoForFunction(String name) {
		return name.startsWith(functionProtoName) && name.endsWith(endsInnerProtoName);
	}


	public static final String contextFunctionPrototypeName = "ContextFunction";
	public static final int MAX_CHAR_JAVA_NAME = 100;
	private static final int NUM_CHARS_TO_KEEP = 40;

	public static boolean isNameInnerProtoForContextFunction(String name) {
		return name.startsWith(NameServer.contextFunctionProtoName) && name.endsWith(endsInnerProtoName);
	}

	/**
	 * return true if the method name is 'eval', 'eval:', 'eval:eval:' , etc.
	 * The parameter should be of the form "eval:eval:eval:" or "eval: eval: ", without the number of parameters
	 */
	public static boolean isMethodNameEval(String name) {
		int indexOfColon = name.indexOf(':');
		if ( indexOfColon < 0 )
			return name.equals("eval");
		else {
			String s = "";
			for (int i = 0; i < name.length(); ++i) {
				if ( name.charAt(i) == ':' ) {
					if ( !s.equals("eval") )
						return false;
					while ( name.charAt(i) == ' ' || Character.isDigit(name.charAt(i)) ) {
						++i;
					}
					s = "";
				}
				else
					s += name.charAt(i);
			}
			return s.length() == 0;
		}
	}



	/**
	 * return the name of an identifier which may have "." in it, as "java.lang.Int".
	 */
	static public String getJavaNameQualifiedIdentifier(List<Symbol> identSymbolArray) {

		String packageName = "";
		int max = identSymbolArray.size() - 1;
		for (int j = 0; j < max; ++j) {
			packageName = packageName + identSymbolArray.get(j).getSymbolString();
			if ( j < max - 1 )
				packageName = packageName + ".";
		}
		String fullName;
		if ( packageName.equals(MetaHelper.cyanLanguagePackageName) )
			fullName = "";
		else
			fullName = packageName;
		if ( fullName.length() > 0 )
			fullName = fullName + ".";
		fullName = fullName + MetaHelper.getJavaName( identSymbolArray.get(identSymbolArray.size()-1).getSymbolString()  );
		return fullName.toString();
	}

	/**
	 * taken from https://www.javamex.com/tutorials/collections/strong_hash_code_implementation_2.shtml
	   @return
	 */
	private static final long []createLookupTable() {
		  long []byteTable1 = new long[256];
		  long h = 0x544B2FBACAAF1684L;
		  for (int i = 0; i < 256; i++) {
		    for (int j = 0; j < 31; j++) {
		      h = (h >>> 7) ^ h;
		      h = (h << 11) ^ h;
		      h = (h >>> 10) ^ h;
		    }
		    byteTable1[i] = h;
		  }
		  return byteTable1;
	}

	 private static final long[] byteTable = createLookupTable();
	  private static final long HSTART = 0xBB40E64DA205B064L;
	  private static final long HMULT = 7664345821815920749L;

	  public static long hash(CharSequence cs) {
		  long h = HSTART;
		  final long hmult = HMULT;
		  final long[] ht = byteTable;
		  final int len = cs.length();
		  for (int i = 0; i < len; i++) {
		    char ch = cs.charAt(i);
		    h = (h * hmult) ^ ht[ch & 0xff];
		    h = (h * hmult) ^ ht[(ch >>> 8) & 0xff];
		  }
		  return h > 0 ? h : -h;
		}

	static int numSuffixJavaName = 0;
	public static String stubName(String javaName) {
		StringBuffer s = new StringBuffer(javaName.substring(0,  NameServer.NUM_CHARS_TO_KEEP));
		s.append("_" + NameServer.hash(javaName) + "_" + javaName.length());
		return s.toString();
	}

	/**
	 * return true if 'name' is the name of a prototype created from an interface.
	 * That is, 'name' is something like 'Proto_myInter__'.
	 */
	public static boolean isPrototypeFromInterface(String name) {
		int indexOfLessThan = name.indexOf('<');
		int i = name.indexOf('(');
		if ( i < 0 )
			i = indexOfLessThan;
		else if ( indexOfLessThan >= 0 && i >= indexOfLessThan )
			i = indexOfLessThan;

		String firstPart = name;
		if ( i >= 0 )
			firstPart = name.substring(0, i);
		int indexOfDot = firstPart.lastIndexOf('.');
		if ( indexOfDot >= 0 )
			firstPart = firstPart.substring(indexOfDot+1);
		return firstPart.startsWith(prefixProtoInterface) && firstPart.endsWith(endsProtoForInterfaceName);
	}

	/**
	 * To each interface Inter the compiler creates a regular object
	 * named Proto_Inter__. This method return the name of the prototype
	 * created for the interface with name 'name'. Here 'name' can be a file name or a prototype name
	 * possibly with '<' and '>'.
	   @param name
	   @return
	 */

	public static String prototypeFileNameFromInterfaceFileName(String name) {
		int indexOfLessThan = name.indexOf('<');
		int indexOfLeftPar = name.indexOf('(');
		if ( indexOfLeftPar > 0 )
			if ( indexOfLessThan < 0 || indexOfLessThan > indexOfLeftPar )
				indexOfLessThan = indexOfLeftPar;
		String firstPart = name;
		String secondPart = "";
		String lastPart = "";
		String ret;
		if ( indexOfLessThan < 0 ) {
			int indexOfDot = firstPart.lastIndexOf('.');
			if ( indexOfDot < 0 ) {
				// no package or '<'
				ret = prefixProtoInterface + name + endsProtoForInterfaceName;
			} else {
				// package but no '<'
				secondPart = name.substring(indexOfDot+1);
				firstPart = name.substring(0, indexOfDot+1);
				ret = firstPart + prefixProtoInterface + secondPart + endsProtoForInterfaceName;
			}
		}
		else {
			// there is a '<'. Therefore name is from a generic prototype
			lastPart = name.substring(indexOfLessThan);
			firstPart = name.substring(0, indexOfLessThan);
			int indexOfDot = firstPart.lastIndexOf('.');
			if ( indexOfDot < 0 ) {
				// no package
				ret = prefixProtoInterface + firstPart + endsProtoForInterfaceName + lastPart;
			} else {
				// package and '<'
				secondPart = firstPart.substring(indexOfDot+1);
				firstPart = firstPart.substring(0, indexOfDot+1);
				ret = firstPart + prefixProtoInterface + secondPart + endsProtoForInterfaceName + lastPart;
			}
		}
		return ret;
	}

	public static String javaPrototypeFileNameFromInterfaceFileName(String name) {
		return "_" + prefixProtoInterface + name + endsProtoForInterfaceName;
	}



	/**
	 * given the prototype name created from an interface, return
	 * the original name of the interface. Or null in error.
	 * If the parameter is "Proto_Inter", the returned string is "Inter"
	 */
	public static String interfaceNameFromPrototypeName(String name) {
		String suffix = "";
		int iLessThan = name.indexOf('<');
		if ( iLessThan >= 0 ) {
			suffix = name.substring(iLessThan);
			name = name.substring(0, iLessThan);
		}
		int i = name.indexOf(prefixProtoInterface);
		if ( i != 0 || ! name.endsWith(endsProtoForInterfaceName) )
			return name;
		else {
			name = name.substring(prefixProtoInterface.length(), name.length() - endsProtoForInterfaceName.length());
			return name + suffix;
		}
	}


	public static String getJavaNameOfMethod(MessageKeywordGrammar keywordGrammar) {
		return null;
	}
	/**
	 * return the Java name corresponding to the regular method with keywords given as parameters.
	 *
	 */
	static public String getJavaNameOfMethod(List<MethodKeywordWithParameters>  keywordArray) {
		// int size = 0;
		String javaName = "";
		for ( MethodKeywordWithParameters keyword : keywordArray ) {
			/*if ( size > 0 )
				javaName = javaName + "_s_";  */
			javaName = javaName + MetaHelper.getJavaNameOfkeyword(keyword.getName()) + keyword.getParameterList().size();
			// size++;
		}
		return javaName;
	}


	/**
	 * return the Java name of the method whose keywords are given in the list keywordArray
	 */
	static public String getJavaNameOfUnaryMethod(String keyword) {
		return MetaHelper.getJavaNameOfkeyword(keyword);
	}


	/**
	 * return the name of the Java method that should be called when the call is made with the
	 * keywords given as parameters.
	 *
	 * @param messageWithkeywords
	 * @return
	 */
	static public String getJavaMethodNameOfMessageSend(MessageWithKeywords messageWithkeywords) {
		String methodName = "";
		// int size = messageWithkeywords.getkeywordParameterList().size();
		for ( MessageKeywordWithRealParameters p : messageWithkeywords.getkeywordParameterList() ) {
			methodName = methodName +
			   MetaHelper.getJavaNameOfkeyword(p.getkeywordName()) + p.getExprList().size();
			/* if ( --size > 0 )
				methodName = methodName + "_s_";  */
		}
		return methodName;
	}


	static public Method getJavaMethodByName(Class<?> aClass, String methodName) {
		java.lang.reflect.Method am[] = aClass.getMethods();
		for ( java.lang.reflect.Method aMethod : am ) {
			if ( aMethod.getName().equals(methodName) )
				return aMethod;
		}
		return null;
	}
	/**
	 * return the name of the Java method that should be called when the call is made with the
	 * keywords given as parameters.
	 *
	 * @param messageWithkeywords
	 * @return
	 */
	static public String getJavaMethodNameOfMessageSend(String unaryOrSinglekeywordMessage) {
		return MetaHelper.getJavaNameOfkeyword(unaryOrSinglekeywordMessage);
	}



	final public static CyanEnv cyanEnv = new CyanEnv(false, false);

	/**
	 * from a prototype name preceded by a package name create an expression.
	   @param fullName
	   @return
	 */
	public static ExprIdentStar stringToExprIdentStar(String fullName, Symbol source) {
		int indexOfDot = fullName.indexOf('.');
		List<Symbol> identSymbolArray = new ArrayList<>();
		String name = fullName;
		while ( indexOfDot >= 0 ) {
			identSymbolArray.add( new SymbolIdent(Token.IDENT, name.substring(0, indexOfDot),
					source.getStartLine(), source.getLineNumber(), source.getColumnNumber(), source.getOffset(), source.getCompilationUnit()) );
			name = name.substring(indexOfDot + 1);
			indexOfDot = name.indexOf('.');
		}
		identSymbolArray.add( new SymbolIdent(Token.IDENT, name,
					source.getStartLine(), source.getLineNumber(), source.getColumnNumber(), source.getOffset(), source.getCompilationUnit()) );

		return new ExprIdentStar(identSymbolArray, null, null);
	}


	public static Tuple2<String, String> splitPackagePrototype(String packageProtoName) {
		String packageName;
		String prototypeName;

		int indexOfLess = packageProtoName.indexOf('<');
		if ( indexOfLess >= 0 ) {

			String beforeLess = packageProtoName.substring(0, indexOfLess);
			int lastIndexOfDot = beforeLess.lastIndexOf('.');
			if ( lastIndexOfDot >= 0 ) {
				/*
				 * main.P<Int>  packageProtoName
				 *     ^ ^      lastIndexOfDot   indexOfLess
				 * main.P       beforeLess
				 */
				packageName = beforeLess.substring(0, lastIndexOfDot);
				prototypeName = packageProtoName.substring(lastIndexOfDot + 1);
			}
			else {
				/*
				 * P<Int>       packageProtoName
				 *  ^      		indexOfLess
				 * P            beforeLess
				 */
				packageName = "";
				prototypeName = packageProtoName;

			}
		}
		else {
			/* no generic prototype instantiation. Something like
			       meta.tg.Proto
			   OR
			        Proto
			*/


			int lastIndexOfDot = packageProtoName.lastIndexOf('.');
			if ( lastIndexOfDot >= 0 ) {
				/*
				 * meta.tg.Proto    packageProtoName
				 *        ^         lastIndexOfDot
				 */
				packageName = packageProtoName.substring(0, lastIndexOfDot);
				prototypeName = packageProtoName.substring(lastIndexOfDot + 1);
			}
			else {
				/*
				 * P<Int>       packageProtoName
				 *  ^      		indexOfLess
				 * P            beforeLess
				 */
				packageName = "";
				prototypeName = packageProtoName;

			}

		}

		return new Tuple2<String, String>(packageName, prototypeName);
	}

	public static boolean isBasicType(Type t) {
		return MetaHelper.cyanJavaBasicTypeTable.get(t.getName()) != null;
	}

	/**
	 * return the name of a private method corresponding to a superclass method.
	 */
	public static String getNamePrivateMethodForSuperclassMethod(String name) {
		return name + "_super__" ;
	}
	static private int numberPrototypeOfFunction = 0;

	static public void setNumberLocalVariable() {
		numberLocalVariable = 0;
	}
	public static int numberLocalVariable = 0;
	public static String cyanLangJar = "cyanLang.jar";
	public static String cyanRuntime = "cyanruntime.jar";
	public static String prototypeFieldInJava = "prototype";
	/**
	 * prefix used to compose the name of a directory that stores the codeg information of a prototype.
	 * The codeg information of a prototype "Proto" is stored in directory "--" + codegPrefix + "-" + Proto
	 */
	public static final String	codegPrefix = "codeg";

	/**
	 * extension of source files of language Script Cyan
	 */
	public static final String	ScriptCyanExtension	= ".syan";

	/**
	 *  prefix added to prototypes that represent interfaces
	 */
	static final public String prefixProtoInterface = "Proto_";

	/**
	 * name, with number of parameters, of the main method of a context object
	 */
	public static final Object bindToFunctionWithParamNumber = "bindToFunction:1";
	public static final String []backupExtensionList = new String[] { "bak", "~" };


	/**
	 * string that starts the name of every inner prototype created for a function
	 */
	public static final String functionProtoName = "Fun_";
	/**
	 * string that starts the name of every inner prototype created for a context function
	 */
	public static final String contextFunctionProtoName = "CFun_";
	/**
	 * string that starts the name of every inner prototype created for a method
	 */
	public static final String methodProtoName = "M_";
	/**
	 * string that ends the name of every inner prototype, be it created for a function or a method
	 */
	public static final String endsInnerProtoName = "__";

	/**
	 * string that ends the name of every prototype created for an interface. So
	 * interface "MyInter" will cause the creation of the non-abstract prototype
	 * Proto_MyInter__
	 *
	 */
	public static final String endsProtoForInterfaceName = "__";

	public static final java.lang.String IAny = MetaHelper.getJavaName("IAny");

	public static final java.util.Random random = new java.util.Random();
	public static final String IMapName = "IMap";
	public static final String ISetName = "ISet";
	public static final String COMMUNICATE_IN_PACKAGE = "communicateInPackage";
	public static final String ON = "on";
	public static final String OFF = "off";

	public static final String JAVA_HOME_FOR_CYAN = "JAVA_HOME_FOR_CYAN";
	public static final Object THISPROTOTYPE999 = "THISPROTOTYPE999";


	public static long nextLong() {
		return random.nextLong();
	}

	/**
	 * taken from stack overflow
	   @param i
	   @return
	 */
	public static String ordinal(int i) {
	    String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	    switch (i % 100) {
	    case 11:
	    case 12:
	    case 13:
	        return i + "th";
	    default:
	        return i + sufixes[i % 10];

	    }
	}


	public static Class<?> getJavaClassFromCyanName(String name) {
		if ( MetaHelper.isBasicType(name) ) {
			return cyanToJavaClassTable.get(name);
		}
		else {
			return Object.class;
		}
	}

	public static boolean isBasicPrototype_andString(String name) {
		return name.equals("String") || name.equals("Byte") || name.equals("Short") ||
				name.equals("Int") || name.equals("Long") ||
				name.equals("Float") || name.equals("Double") ||
				name.equals("Char");
	}

	/*
	public static Class<?> getJavaWrapperClassFromCyanName(String name) {
		if ( NameServer.isBasicType(name) ) {
			return primitiveJavaToWrapperClassTable.get(name);
		}
		else {
			return Object.class;
		}
	}
	*/


	public static String cyanNameFromJavaBasicType(String name) {
		return javaBasicTypeToCyan.get(name);
	}

	public static String getFieldBasicType(String name) {
		return fieldNameCyanBasicTypeTable.get(name);
	}

	public static boolean isJavaBasicType(String elem) {
		return javaBasicTypes.contains(elem);
	}



	public static String javaWrapperClassToCyanName(String javaName) {
		return javaWrapperClassToCyanName.get(javaName);
	}

	public static String javaPrimitiveTypeToWrapperClassName(String javaName) {
		if ( javaName.equals("int") ) {
			return "Integer";
		}
		else if ( javaName.equals("char") ) {
			return "Character";
		}
		else {
			return Character.toUpperCase(javaName.charAt(0)) + javaName.substring(1);
		}
	}


	public static Class<?> javaPrimitiveTypeToWrapperClass(String javaName) {
		return primitiveJavaToWrapperClassTable.get(javaName);
	}

	public static void println(Object s) {
		System.out.println(s);
	}
	public static void print(Object s) {
		System.out.print(s);
	}


    public static String generateFileNameToAddTypeInfo(String mainPackageName, String mainPrototypeName) {
        String s;
        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        s = d.format(now);
        s += " " +
                mainPackageName +
                "." +
                mainPrototypeName +
                ".json";
        return s;
    }




}
