package cyanruntime;

import java.lang.reflect.Method;
import java.util.Hashtable;
import cyan.lang._ExceptionReadFormat;

public class CyanRuntime {

	static private Hashtable<String, String> symbolToAlpha;
	static private Hashtable<String, String> cyanJavaBasicTypeTable;
	
	static {
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

		
		cyanJavaBasicTypeTable = new Hashtable<String, String>();
		cyanJavaBasicTypeTable.put("Byte", "CyByte");
		cyanJavaBasicTypeTable.put("Short", "CyShort");
		cyanJavaBasicTypeTable.put("Int", "CyInt");
		cyanJavaBasicTypeTable.put("Long", "CyLong");
		cyanJavaBasicTypeTable.put("Float", "CyFloat");
		cyanJavaBasicTypeTable.put("Double", "CyDouble");
		cyanJavaBasicTypeTable.put("Char", "CyChar");
		cyanJavaBasicTypeTable.put("Boolean", "CyBoolean");
		cyanJavaBasicTypeTable.put("String", "CyString");
		

		
		
		
		
	}
	
	/**
	 * return the Java name of the method whose selectors are given in the list selectorList and whose
	 * number of parameters of each selector is given by numParamList
	 */
	static public String getJavaNameOfMethod(String []selectorList, int []numParamList) {
		String javaName = "";
		
		if ( numParamList.length == 1 ) {
			char first = selectorList[0].charAt(0);
			if ( first != '_' && ! Character.isAlphabetic(first) ) {
				// should be an operator
				return alphaName(selectorList[0]);
				
			}
		}
		
		int i = 0;
		for ( String selector : selectorList ) {
			String javaNameSel = getJavaNameOfSelector(selector);
			if ( ! selector.endsWith(":") )
				javaNameSel += "_";
			javaName = javaName + javaNameSel  + numParamList[i];
			++i;
		}
		return javaName;
	}
	

	static public String getJavaNameOfUnaryMethod(String name) {
		return "_" + name;
	}

	/**
	 * get the Java name corresponding to this selector. It is
	 * equal to "_symbolString" except when there is a underscore.
	 * All underscore characters are duplicated. So,
	 *       Is_A_Number
	 * results in
	 *       _Is__A__Number
	 * The ending character ':' is changed to "_dot". So
	 *    "eval:" produces  "_eval_dot"
	 * @param lineNumber
	 */
	static public String getJavaNameOfSelector(String symbolString) {
		

		if ( CyanRuntime.alphaName("" + symbolString.charAt(0)) != null ) {
			int size = symbolString.length();
			String alpha = "";
			for (int i = 0; i < size; i++) {
				String s = symbolToAlpha.get("" + symbolString.charAt(i));
				if ( s == null ) 
					return null;
			   alpha = alpha + "_" + s;
			}
			return alpha;
		}
		else {
			StringBuffer s = new StringBuffer("_");
			for ( int i = 0; i < symbolString.length(); i++ ) {
				char ch = symbolString.charAt(i);
				if ( ch != ':' )
					s.append(ch);
				else
					s.append("_");
				if ( ch == '_' )
					s.append("__");
			}
			return s.toString();
			
		}
	}
	
	/**
	 * return the alphanumeric name of a method composed by symbols. That is, if the 
	 * method is '+', this method returns "_plus". If the method were '<*' (if possible) this
	 * method would return "_lessThan_star"
	 */

	static public String alphaName(String symbolName) {
		int size = symbolName.length();
		String alpha = "";
		for (int i = 0; i < size; i++) {
			String s = symbolToAlpha.get("" + symbolName.charAt(i));
			if ( s == null ) 
				return null;
		   alpha = alpha + "_" + s;
		}
		return alpha;
	}

	static public Method getJavaMethodByName(Class<?> aClass, String methodName, int numParam) {
		java.lang.reflect.Method am[] = aClass.getMethods();
		for ( java.lang.reflect.Method aMethod : am ) {
			if ( aMethod.getName().equals(methodName) && aMethod.getParameterCount() == numParam ) 
				return aMethod;
		}
		return null;
	}
	
	public static void runtimeError(String message) {
		System.out.println(message);
	}
	
	public static String readLine() {
		String str;
		System.out.flush();
		//java.io.Console console = null; // System.console();
		try {
			/*
			if ( console != null ) {
				str = console.readLine();
			}
			else {}
			*/

			java.io.InputStreamReader read = new java.io.InputStreamReader(System.in);
			java.io.BufferedReader in = new java.io.BufferedReader(read);
			str = in.readLine();
					
			return str;
		}
		catch (java.io.IOException e) {
			throw new ExceptionContainer__( new _ExceptionReadFormat());
		}
		catch (NumberFormatException e) {
			throw new ExceptionContainer__( new _ExceptionReadFormat());
		}
	}	

}
