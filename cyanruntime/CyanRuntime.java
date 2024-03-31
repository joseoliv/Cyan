
package cyanruntime;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Hashtable;
import cyan.lang._CyException;
import cyan.lang._ExceptionReadFormat;

public class CyanRuntime {

	static private Hashtable<String, String>	symbolToAlpha;
	static private Hashtable<String, String>	cyanJavaBasicTypeTable;

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
	 * return the Java name of the method whose selectors are given in the list
	 * selectorList and whose number of parameters of each selector is given by
	 * numParamList
	 */
	static public String getJavaNameOfMethod(String[] selectorList,
			int[] numParamList) {
		String javaName = "";

		if ( numParamList.length == 1 ) {
			char first = selectorList[0].charAt(0);
			if ( first != '_' && !Character.isAlphabetic(first) ) {
				// should be an operator
				return alphaName(selectorList[0]);

			}
		}

		int i = 0;
		for (String selector : selectorList) {
			String javaNameSel = getJavaNameOfSelector(selector);
			if ( !selector.endsWith(":") ) javaNameSel += "_";
			javaName = javaName + javaNameSel + numParamList[i];
			++i;
		}
		return javaName;
	}

	static public String getJavaNameOfUnaryMethod(String name) {
		return "_" + name;
	}

	/**
	 * get the Java name corresponding to this selector. It is equal to
	 * "_symbolString" except when there is a underscore. All underscore
	 * characters are duplicated. So, Is_A_Number results in _Is__A__Number The
	 * ending character ':' is changed to "_dot". So "eval:" produces
	 * "_eval_dot"
	 *
	 * @param lineNumber
	 */
	static public String getJavaNameOfSelector(String symbolString) {

		if ( CyanRuntime.alphaName("" + symbolString.charAt(0)) != null ) {
			int size = symbolString.length();
			String alpha = "";
			for (int i = 0; i < size; i++) {
				String s = symbolToAlpha.get("" + symbolString.charAt(i));
				if ( s == null ) return null;
				alpha = alpha + "_" + s;
			}
			return alpha;
		}
		else {
			StringBuffer s = new StringBuffer("_");
			for (int i = 0; i < symbolString.length(); i++) {
				char ch = symbolString.charAt(i);
				if ( ch != ':' )
					s.append(ch);
				else
					s.append("_");
				if ( ch == '_' ) s.append("__");
			}
			return s.toString();

		}
	}

	/**
	 * return the alphanumeric name of a method composed by symbols. That is, if
	 * the method is '+', this method returns "_plus". If the method were '<*'
	 * (if possible) this method would return "_lessThan_star"
	 */

	static public String alphaName(String symbolName) {
		int size = symbolName.length();
		String alpha = "";
		for (int i = 0; i < size; i++) {
			String s = symbolToAlpha.get("" + symbolName.charAt(i));
			if ( s == null ) return null;
			alpha = alpha + "_" + s;
		}
		return alpha;
	}

	static public Method getJavaMethodByName(Class<?> aClass, String methodName,
			int numParam) {
		java.lang.reflect.Method am[] = aClass.getMethods();
		for (java.lang.reflect.Method aMethod : am) {
			if ( aMethod.getName().equals(methodName)
					&& aMethod.getParameterCount() == numParam )
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
		// java.io.Console console = null; // System.console();
		try {
			/*
			 * if ( console != null ) { str = console.readLine(); } else {}
			 */

			java.io.InputStreamReader read = new java.io.InputStreamReader(
					System.in);
			java.io.BufferedReader in = new java.io.BufferedReader(read);
			str = in.readLine();

			return str;
		}
		catch (java.io.IOException e) {
			throw new ExceptionContainer__(new _ExceptionReadFormat());
		}
		catch (NumberFormatException e) {
			throw new ExceptionContainer__(new _ExceptionReadFormat());
		}
	}

	static public void writeToFileToAddTypeInfo(String parameterName,
			boolean typeSupplied, int fileOffset, String runtimeType,
			boolean addCommaAtEnd, String fileNameToAddTypeInfo) {

		String wasTypeSupplied = typeSupplied ? "true" : "false";

		fileToAddTypeInfo.println("        {\r\n"
				+ "          \"parameter name\": \"" + parameterName + "\",\r\n"
				+ "          \"type supplied\": " + wasTypeSupplied + ",\r\n"
				+ "          \"file offset\": " + fileOffset + ",\r\n"
				+ "          \"runtime Type\": \"" + runtimeType + "\"\r\n"
				+ "        }" + (addCommaAtEnd ? "," : ""));
		if ( fileToAddTypeInfo.checkError() ) {
			System.out.println("Error in writing to file '"
					+ fileNameToAddTypeInfo + "' that keeps information on "
					+ "the types of Dyn variables and parameters. "
					+ "If the error persists, try to compile without "
					+ "option -addTypeInfo " + runtimeType);
		}

	}

	static public void writeToFileToAddTypeInfo(String variableFieldName,
			int fileOffset, String runtimeType, boolean typeSupplied,
			String fileNameToAddTypeInfo) {

		String wasTypeSupplied = typeSupplied ? "true" : "false";

		fileToAddTypeInfo.println("      \"variable name\": \""
				+ variableFieldName + "\",\r\n" + "      \"type supplied\": "
				+ wasTypeSupplied + ",\r\n" + "      \"file offset\": "
				+ fileOffset + ",\r\n" + "      \"runtime Type\": \""
				+ runtimeType + "\"\r\n");
		if ( fileToAddTypeInfo.checkError() ) {
			System.out.println("Error in writing to file '"
					+ fileNameToAddTypeInfo + "' that keeps information on "
					+ "the types of Dyn variables and parameters. "
					+ "If the error persists, try to compile without "
					+ "option -addTypeInfo " + runtimeType);
		}

	}

	static public void write_ln_only_ToFileToAddTypeInfo() {
		fileToAddTypeInfo.println();
	}

	static public void writeToFileToAddTypeInfo(String s,
			String fileNameToAddTypeInfo) {

		fileToAddTypeInfo.println(s);
		if ( fileToAddTypeInfo.checkError() ) {
			System.out.println("Error in writing to file '"
					+ fileNameToAddTypeInfo + "' that keeps information on "
					+ "the types of Dyn variables and parameters. "
					+ "If the error persists, try to compile without "
					+ "option -addTypeInfo '" + s);
			System.exit(0);
		}

	}

	/**
	 * number of method calls whose dynamic argument types were saved in file
	 * fileNameToAddTypeInfo
	 */
	static public long numberEntriesTypeInfo = 0;

	/*
	 * String name, int line, int column, int fileOffset, boolean
	 * wasTypeSupplied) {
	 */
	public static void addDynParameterInfo(String prototypeFullName,
			String sha256, String methodName, String parameterName, int line,
			int column, int fileOffset, boolean wasTypeSupplied,
			String typeToAddToParameter) {
		DynamicTypeInfoPrototype ip = dynamicTypeInfoProgram.prototypeSet
				.get(prototypeFullName);
		if ( ip == null ) {
			ip = new DynamicTypeInfoPrototype(prototypeFullName, sha256);
			dynamicTypeInfoProgram.prototypeSet.put(prototypeFullName, ip);
		}
		DynamicTypeInfoMethod im = ip.methodSet.get(methodName);
		if ( im == null ) {
			im = new DynamicTypeInfoMethod(methodName);
			ip.methodSet.put(methodName, im);
		}
		DynamicTypeInfoVarParamField par = im.parameterSet.get(parameterName);
		if ( par == null ) {
			par = new DynamicTypeInfoVarParamField(parameterName, line, column,
					fileOffset, wasTypeSupplied);
			im.parameterSet.put(parameterName, par);
		}
		par.runtimeType.add(typeToAddToParameter);

	}

	public static void addDynLocalVariableInfo(String prototypeFullName,
			String sha256, String methodName, String localVariableName,
			int line, int column, int fileOffset, boolean wasTypeSupplied,
			String typeToAddToParameter) {
		DynamicTypeInfoPrototype ip = dynamicTypeInfoProgram.prototypeSet
				.get(prototypeFullName);
		if ( ip == null ) {
			ip = new DynamicTypeInfoPrototype(prototypeFullName, sha256);
			dynamicTypeInfoProgram.prototypeSet.put(prototypeFullName, ip);
		}
		DynamicTypeInfoMethod im = ip.methodSet.get(methodName);
		if ( im == null ) {
			im = new DynamicTypeInfoMethod(methodName);
			ip.methodSet.put(methodName, im);
		}

		DynamicTypeInfoVarParamField localVariable = im.localVariableSet
				.get(localVariableName);
		if ( localVariable == null ) {
			localVariable = new DynamicTypeInfoVarParamField(localVariableName,
					line, column, fileOffset, wasTypeSupplied);
			im.localVariableSet.put(localVariableName, localVariable);
		}
		localVariable.runtimeType.add(typeToAddToParameter);

	}

	public static void addDynFieldInfo(String prototypeFullName, String sha256,
			String fieldName, int line, int column, int fileOffset,
			boolean wasTypeSupplied, String typeToAddToParameter) {
		DynamicTypeInfoPrototype ip = dynamicTypeInfoProgram.prototypeSet
				.get(prototypeFullName);
		if ( ip == null ) {
			ip = new DynamicTypeInfoPrototype(prototypeFullName, sha256);
			dynamicTypeInfoProgram.prototypeSet.put(prototypeFullName, ip);
		}
		DynamicTypeInfoVarParamField prototypeField = ip.fieldSet
				.get(fieldName);
		if ( prototypeField == null ) {
			prototypeField = new DynamicTypeInfoVarParamField(fieldName, line,
					column, fileOffset, wasTypeSupplied);
			ip.fieldSet.put(fieldName, prototypeField);
		}
		prototypeField.runtimeType.add(typeToAddToParameter);

	}

	// static public void initAddTypeInfo() {
	// dynamicTypeInfoProgram = new DynamicTypeInfoProgram();
	// }
	static public void saveAddTypeInfo() {
		/*
		 * try (FileWriter fosFileToAddTypeInfo = new FileWriter(
		 * fileNameToAddTypeInfo); PrintWriter fileToAddTypeInfo = new
		 * PrintWriter(fosFileToAddTypeInfo)) { this.fileNameToAddTypeInfo =
		 * fileNameToAddTypeInfo;
		 *
		 * CyanRuntime.fileToAddTypeInfo = fileToAddTypeInfo; String projectDir
		 * = this.project.getProjectDir(); if (
		 * projectDir.endsWith(File.separator) ) { projectDir =
		 * projectDir.substring(0, projectDir.length()-1); } projectDir =
		 * projectDir.replace('\\', '/');
		 * pw.printlnIdent("cyan.lang._System.writeToFileToAddTypeInfo(" +
		 * "\"{\\r\\n\" + \r\n" + "  \"  \\\"project Directory\\\": \\\"" +
		 * projectDir + "\\\",\\r\\n\" +\r\n" + "  \"  \\\"data\\\": [ \",\r\n"
		 * + "\"" + fileNameToAddTypeInfo + "\");");
		 *
		 *
		 */
	}

	static public void genJSON_DynamicTypeInfo() {
		int size = dynamicTypeInfoProgram.prototypeSet.size();
		if ( size > 0 ) {
			try (FileWriter fosFileToAddTypeInfo = new FileWriter(
					dynamicTypeInfoProgram.fileNameToAddTypeInfo);
					PrintWriter pw = new PrintWriter(fosFileToAddTypeInfo)) {

				dynamicTypeInfoProgram.genJSON(pw, size > 0);
			}
			catch (IOException e) {
				System.out.println("Error when creating or writing to the JSON "
						+ "file that keeps information about runtime types of "
						+ "the program. This program is named '"
						+ dynamicTypeInfoProgram.fileNameToAddTypeInfo
						+ "' and it should be created because the program was compiled with option -addTypeInfo");
				System.exit(1);
			}
		}

	}

	public static String sha256(char[] base) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		int size = base.length;
		while (i < size) {
			if ( base[i] == '\0' ) break;
			sb.append(base[i]);
			++i;
		}
		return sha256(new String(sb));
	}

	/**
	 * taken from
	 * https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
	 */
	public static String sha256(final String base) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] hash = digest.digest(base.getBytes("UTF-8"));
			final StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < hash.length; i++) {
				final String hex = Integer.toHexString(0xff & hash[i]);
				if ( hex.length() == 1 ) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void catchException(Object[] catchList,
			ExceptionContainer__ t) {

		_CyException anException = t.elem;
		boolean called = false;
		for (Object f : catchList) {
			Class<?> cClass = f.getClass();
			while (cClass != null) {
				Object result = null;
				java.lang.reflect.Method[] methodList = cClass
						.getDeclaredMethods();
				// System.out.println("class = " + cClass.getName());
				for (java.lang.reflect.Method m : methodList) {
					if ( m.getName().equals("_eval_1")
							&& java.lang.reflect.Modifier
									.isPublic(m.getModifiers()) ) {
						// System.out.println(" Method: " + m.getName());
						Class<?> paramTypeList[] = m.getParameterTypes();
						if ( paramTypeList[0]
								.isAssignableFrom(anException.getClass()) ) {
							// System.out.println(
							// " is assignable from the exception class "
							// + anException.getClass().getName());
							try {
								m.setAccessible(true);
								// System.out.println(" invoking the method");
								result = m.invoke(f, anException);
								// System.out.println(
								// " after invoking the method");

							}
							catch (IllegalAccessException
									| IllegalArgumentException
									| java.lang.reflect.InvocationTargetException e) {
								if ( e instanceof java.lang.reflect.InvocationTargetException ) {
									java.lang.reflect.InvocationTargetException it = (java.lang.reflect.InvocationTargetException) e;
									Throwable anotherException = it
											.getTargetException();
									if ( anotherException instanceof cyanruntime.ExceptionContainer__ ) {
										throw (cyanruntime.ExceptionContainer__) anotherException;
									}
								}
								System.out.println("Error in calling method '"
										+ cClass.getName() + "::" + m.getName()
										+ "' with parameter of types " + " '"
										+ f.getClass().getName() + "' and '"
										+ t.getClass().getName()
										+ "' in an exception. "
										+ "This error was caused by the use of type 'Dyn' or it is an internal compiler error (improbable)");
							}
							if ( !called ) {
								return;
							}
							// if ( result != null || m.getReturnType() ==
							// void.class ) {
							// called = true;
							// break exit;
							// }
						}
					}
				}
				cClass = cClass.getSuperclass();
			}
		}
		if ( !called ) throw t;
	}

	static public DynamicTypeInfoProgram	dynamicTypeInfoProgram;
	static public PrintWriter				fileToAddTypeInfo;
	static public String					fileNameToAddTypeInfo;
	static public String					projectDirectory;
	/**
	 * ( prototype (field info)* ( method name (parameter)* (local variable)* )*
	 * )*
	 */
}
