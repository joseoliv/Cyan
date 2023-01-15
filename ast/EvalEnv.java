package ast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import chooseFile.ChooseFoldersCyanInstallation;
import cyan.lang._CyException;
import lexer.Symbol;
import meta.InterpretationErrorException;
import meta.MetaHelper;
import meta.Tuple2;
import meta.WrSymbol;
import saci.Function1;
import saci.NameServer;

/**
 * evaluation environment for 'eval' methods of classes
 * of the AST. An object of this class plays the role
 * of the memory/stack of a Cyan program that is interpreted
 * by means of calls to 'eval' methods of the AST.
 *
 * See methods eval of ExprMessageSendUnaryChainToExpr and ExprLiteralBoolean, which are the only
 * ones implemented.
   @author jose
 */
public class EvalEnv {




	/**
	 * symbolForErrorMessage is a symbol used for error messages if any other is not
	 * given. It may be null.
	   @param cyanLangPath
	   @param selfObject
	   @param symbolForErrorMessage
	 */
	public EvalEnv( String cyanLangPath, Object selfObject, WrSymbol symbolForErrorMessage) {
		properlyInitialized = false;
		this.cyanLangPath = cyanLangPath;
				//(WrSymbol sym, String msg) -> { env.error(meta.GetHiddenItem.getHiddenSymbol(sym), msg); };
		this.selfObject = selfObject;
		if ( selfObject == null ) {
			selfObject = new Object() { } ;
		}
		this.symbolForErrorMessage = symbolForErrorMessage;

		EvalEnv.loadBasicCyanPrototypes((String msg) -> { this.error( (WrSymbol ) null,  msg); } );

		this.jvmPackageMap = new HashMap<>();
		this.jvmPackageList = new ArrayList<>();
		importedClassMap = new HashMap<>();
		importedClassMapPackageLoader = new HashMap<>();

		//this.addPackage(env.getCyanLangJarFile(), env.getCyanRuntimeJarFile());
		// this.addPackage(env.getCyanLangJarFile());

		this.properlyInitialized = addJarsCyanLangDir();
		localVarDecStack = new Stack<>();
		numVarLevelStack = new Stack<>();
		memory = new HashMap<>();
		fieldMemory = null;
		this.importedPackageNameList = new ArrayList<>();
		importedPackageNameList.add("cyan.lang");
		}


	enum JarPathError { doesNotExistNotDir, other, ok };

	private boolean addJarsCyanLangDir() {


		File jlDir = new File(cyanLangPath);
		if ( !jlDir.exists() || !jlDir.isDirectory() ) {
			error(this.symbolForErrorMessage, "The path '" + cyanLangPath +
					"' was given as the directory of Cyan, probably in environment variable CYAN_HOME. "
					+ "However, this path does not exist or it is not a directory. "
					+ "Reinstall the Cyan compiler");
			return false;
		}
		boolean foundCyanLangJar = false;
		boolean foundCyanRuntime = false;
		List<URL> urlList = new ArrayList<>();
		List<String> pathList = new ArrayList<>();
		for ( File f : jlDir.listFiles() ) {
			String pathName = f.getPath();
			String simpleName = f.getName();
			if ( ! f.isDirectory() && simpleName.endsWith(".jar") ) {
				try {
					if ( simpleName.compareToIgnoreCase(meta.MetaHelper.cyanLangJarFileName) == 0)  {
						foundCyanLangJar = true;
						urlList.add( f.toURI().toURL());
						pathList.add(pathName);
					}
					if ( simpleName.compareToIgnoreCase(meta.MetaHelper.cyanLangRuntimeFileName) == 0)  {
						foundCyanRuntime = true;
						urlList.add( f.toURI().toURL());
						pathList.add(pathName);
					}
				}
				catch (MalformedURLException e) {
					error(this.symbolForErrorMessage, "The path '" + cyanLangPath +
							"' was given as the directory of Cyan, probably in environment variable CYAN_HOME. "
							+ "There was an error when reading the jar files of this directory"
							+ "Reinstall the Cyan compiler");
						return false;
				}
			}
		}
		if ( !foundCyanLangJar ) {
			ChooseFoldersCyanInstallation.askIfSet(false, true);
			error(this.symbolForErrorMessage, "The path '" + cyanLangPath +
					"' was given as the directory of Cyan, probably in environment variable CYAN_HOME. "
					+ "Inside this directory, file '" + meta.MetaHelper.cyanLangJarFileName + "' was not found"
					+ "Reinstall the Cyan compiler");
			return false;
		}
		if ( !foundCyanRuntime ) {
			error(this.symbolForErrorMessage, "The path '" + cyanLangPath +
					"' was given as the directory of Cyan, probably in environment variable CYAN_HOME. "
					+ "Inside this directory, file '" + meta.MetaHelper.cyanLangRuntimeFileName + "' was not found"
					+ "Reinstall the Cyan compiler");
			return false;
		}

		this.addToLists_LoadClassesFrom(pathList, urlList);
		return true;
	}

	public JarPathError addJarsPath(String path) {
		File jlDir = new File(path);
		if ( !jlDir.exists() || !jlDir.isDirectory() ) {
			return JarPathError.doesNotExistNotDir;
		}
		List<URL> urlList = new ArrayList<>();
		List<String> pathList = new ArrayList<>();
		for ( File f : jlDir.listFiles() ) {
			String name = f.getPath();
			if ( ! f.isDirectory() && name.endsWith(".jar") ) {
				try {
					urlList.add( f.toURI().toURL());
				}
				catch (MalformedURLException e) {
					return JarPathError.other;
				}
				pathList.add(name);
			}
		}
		this.addToLists_LoadClassesFrom(pathList, urlList);
		return JarPathError.ok;
	}

	// to be called at editing time
//	public void loadClassesFromPath(List<String> pathList, List<String> jarFileList) {
//		loadCyanLangPackage();
//	}
//

//	public void loadClassesFromPath(String []pathArrayList, String []jarFileArrayList) {
//		final List<String> pathList = new ArrayList<>();
//		for ( final String s : pathArrayList ) {
//			pathList.add(s);
//		}
//		final List<String> jarFileList = new ArrayList<>();
//		for ( final String s : jarFileArrayList ) {
//			jarFileList.add(s);
//		}
//		loadClassesFromPath(pathList, jarFileList);
//	}

	public void importPackage(String packageName) {
//		if ( importedPackageNameList == null ) {
//			importedPackageNameList = new ArrayList<>();
//		}
		importedPackageNameList.add(packageName);
	}

	private List<String> importedPackageNameList = null;


	public void error(Symbol firstSymbol, String msg) {
		throw new InterpretationErrorException(msg, firstSymbol.getI());
	}

	public void error(WrSymbol firstSymbol, String msg) {
		// env.error(firstSymbol, string);
		throw new InterpretationErrorException(msg, firstSymbol);
	}

	static public Method getJavaMethodByName(Class<?> aClass, String methodName, int numParam) {
		final java.lang.reflect.Method am[] = aClass.getMethods();
		for ( final java.lang.reflect.Method aMethod : am ) {
			if ( aMethod.getName().equals(methodName) && aMethod.getParameterCount() == numParam )
				return aMethod;
		}
		return null;
	}


	public Map<String, JVMPackage> getJvmPackageMap() { return this.jvmPackageMap; }


	public JVMPackage getCyanLangPackage() {
		if ( cyanLangPackage == null ) {
			cyanLangPackage = this.getJvmPackageMap().get(MetaHelper.cyanLanguagePackageName);
		}
		return cyanLangPackage;
	}

	private JVMPackage cyanLangPackage;

	Object selfObject;

	public  Class<?> getCyBoolean() {
//		if ( cyBoolean == null ) {
//			final TypeJavaRef classRef = cyanLangPackage.getJvmTypeClassMap().get( MetaHelper.getJavaName("Boolean") );
//			cyBoolean = classRef.getaClass(env, symbol);
//		}
		return cyBoolean;
	}
	public  Class<?> getCyInt() {
		return cyInt;
	}

	public  Class<?> getCyChar() {
		return cyChar;
	}


	public  Class<?> getCyByte() {
		return cyByte;
	}


	public  Class<?> getCyLong() {
		return cyLong;
	}


	public  Class<?> getCyFloat() {
		return cyFloat;
	}


	public  Class<?> getCyDouble() {
		return cyDouble;
	}


	public  Class<?> getCyString() {
		return cyString;
	}


	/**
	 * jarPathList is a list of paths of Jar files. This method creates an object of {@link ast#JVMPackage} for
	 * each package of this jar file. It is added to field jvmPackageList. The
	 * classes of the package are not loaded.
	   @param jarPath
	   @return
	   @throws IOException
	   @throws ClassNotFoundException
	 */
	public String loadPackagesInJarFile(String ...jarPathList) {
		// JarFile jarFile = null;
		try {
			final URL[] urls = new URL[jarPathList.length];
			int i = 0;
			for ( final String aJarPath : jarPathList ) {
				// urls[i] = new URL("jar:file:" + aJarPath + "!/");
				File jf = new File(aJarPath);
				urls[i] = jf.toURI().toURL();
				++i;
			}
			try ( URLClassLoader cl = URLClassLoader.newInstance(urls) ) {
				for ( final String jarPath : jarPathList ) {
					try ( JarFile jarFile = new JarFile(jarPath) ) {

						//System.out.println("jar size = " + jarFile.size());
						final Enumeration<JarEntry> e = jarFile.entries();


						while ( e.hasMoreElements() ) {
							final JarEntry je = e.nextElement();
							final String entryName = je.getName();
							if ( ! je.isDirectory() ) {
								final int lastSlash = entryName.lastIndexOf('/');
								if ( lastSlash > 0 ) {
									final String packageNameSlash = entryName.substring(0, lastSlash).replace('/', '.');
									if ( ! packageNameSlash.startsWith("META-INF") &&
											!packageNameSlash.contains(".internal.") && entryName.endsWith(".class") ) {
										JVMPackage jvmPackage = jvmPackageMap.get(packageNameSlash);
										if ( jvmPackage == null  ) {
											jvmPackage = new JVMPackage(jarPath, packageNameSlash);
											this.jvmPackageList.add(jvmPackage);
											this.jvmPackageMap.put(packageNameSlash, jvmPackage);
											jvmPackage.setUrls(urls);
										}
										final String className = entryName.substring(lastSlash + 1, entryName.length() - 6);
										final TypeJavaClass javaClass = new TypeJavaClass(className, jvmPackage);
										jvmPackage.put(className, javaClass);
									}

								}
							}
						}
					}
					catch ( final IOException e ) {
//						env.error(meta.GetHiddenItem.getHiddenSymbol(symbolForErrorMessage),
//								"Error when loading jar files '" + jarPath + "'");
						this.error(symbolForErrorMessage,
								"Error when loading jar files '" + jarPath + "'");
					}

				}
			}
			catch ( final IOException  e ) {
				int size = jarPathList.length;
				String s = "";
				for ( final String aJarPath : jarPathList ) {
					s = aJarPath;
					if ( --size > 0 ) { s += ", "; }
				}

				// env.error(meta.GetHiddenItem.getHiddenSymbol(symbolForErrorMessage),
				//		"Error when loading jar files '" + s + "'");
				this.error(symbolForErrorMessage,
						"Error when loading jar files '" + s + "'");
			}

		}
		catch ( final IOException  e ) {
			int size = jarPathList.length;
			String s = "";
			for ( final String aJarPath : jarPathList ) {
				s = aJarPath;
				if ( --size > 0 ) { s += ", "; }
			}

			//env.error(meta.GetHiddenItem.getHiddenSymbol(symbolForErrorMessage),
			//		"Error when loading jar files '" + s + "'");
			this.error(symbolForErrorMessage,
					"Error when loading jar files '" + s + "'");

		}
		return null;
	}


	/**
	 * add all the classes of jarPathList to fields jvmPackageList and jvmPackageMap
	 * of this object. In addition to that, the classes are loaded and initialized
	 * (static fields). urlList keeps a URL for each path of jarPathList. It may
	 * be null. In this case, the URLs for each path of jarPathList are created.
	   @param jarPathList
	   @return
	 */
	public String addToLists_LoadClassesFrom(List<String> jarPathList, List<URL> urlList)  {
		// JarFile jarFile = null;
		try {
			URL []urls;
			if ( urlList == null ) {
				urls = new URL[jarPathList.size()];
				int i = 0;
				for ( final String aJarPath : jarPathList ) {
					//urls[i] = new URL("jar:file:" + aJarPath + "!/");
					File jf = new File(aJarPath);
					urls[i] = jf.toURI().toURL();
					++i;
				}
			}
			else {
				urls = new URL[urlList.size()];
				int i = 0;
				for ( URL url : urlList ) {
					urls[i] = url;
					++i;
				}
			}
			try ( URLClassLoader cl = URLClassLoader.newInstance(urls) ) {
				for ( final String jarPath : jarPathList ) {
					try ( JarFile jarFile = new JarFile(jarPath) ) {

						//System.out.println("jar size = " + jarFile.size());
						final Enumeration<JarEntry> e = jarFile.entries();


						while ( e.hasMoreElements() ) {
							final JarEntry je = e.nextElement();
							final String entryName = je.getName();
							if ( ! je.isDirectory() ) {
								final int lastSlash = entryName.lastIndexOf('/');
								//int lastSlash = entryName.lastIndexOf( NameServer.fileSeparator );
								if ( lastSlash > 0 ) {
									final String packageNameSlash = entryName.substring(0, lastSlash).replace('/', '.');
									if ( ! packageNameSlash.startsWith("META-INF") &&
											!packageNameSlash.contains(".internal.") && entryName.endsWith(".class") ) {
										JVMPackage jvmPackage = jvmPackageMap.get(packageNameSlash);
										if ( jvmPackage == null  ) {
											jvmPackage = new JVMPackage(jarPath, packageNameSlash);
											this.jvmPackageList.add(jvmPackage);
											this.jvmPackageMap.put(packageNameSlash, jvmPackage);
											jvmPackage.setUrls(urls);
										}
										final String className = entryName.substring(lastSlash + 1, entryName.length() - 6);
										final TypeJavaClass javaClass = new TypeJavaClass(className, jvmPackage);
//										Class<?> aClass;
//										try {
//											final String fullClassName = packageNameSlash + "." + className;
//											aClass = Class.forName(fullClassName, true, cl);
//											if ( aClass == null ) {
//												error(meta.GetHiddenItem.getHiddenSymbol(symbolForErrorMessage),
//														"Class '" + fullClassName +
//														"' was not found in the Cyan compiler");
//											}
//											javaClass.setaClass(aClass);
											jvmPackage.put(className, javaClass);
											importedClassMapPackageLoader.put(className,
													new Tuple2<JVMPackage, ClassLoader>(jvmPackage, cl));
//											this.importedClassMap.put(className, null);
//										}
//										catch (final ClassNotFoundException e1) {
//											error(meta.GetHiddenItem.getHiddenSymbol(symbolForErrorMessage),
//													"Error when loading class '" + entryName + "'");
//										}
									}

								}
							}
						}
					}
					catch ( final IOException e ) {
						error(meta.GetHiddenItem.getHiddenSymbol(symbolForErrorMessage),
								"Error when loading jar files '" + jarPath + "'");
					}

				}
			}
			catch ( final IOException  e ) {
				int size = jarPathList.size();
				String s = "";
				for ( final String aJarPath : jarPathList ) {
					s = aJarPath;
					if ( --size > 0 ) { s += ", "; }
				}

				error(meta.GetHiddenItem.getHiddenSymbol(symbolForErrorMessage),
						"Error when loading jar files '" + s + "'");
			}

		}
		catch ( final IOException  e ) {
			int size = jarPathList.size();
			String s = "";
			for ( final String aJarPath : jarPathList ) {
				s = aJarPath;
				if ( --size > 0 ) { s += ", "; }
			}

			error(meta.GetHiddenItem.getHiddenSymbol(symbolForErrorMessage),
					"Error when loading jar files '" + s + "'");
		}
		return null;
	}



	/**
	 * search a Java class in packages java.lang and meta unless
	 * it is preceded by a package name. If it does, the search is
	 * made with that package. Then it is possible to look for a
	 * class java.lang.String, for example.
	   @param name
	   @return
	 */
	public Class<?> searchJavaClass_MetaJavaLang(String name) {
		Class<?> ret = null;
		for ( String pn : new String[] { "java.lang.", "meta." } ) {
			try {
				String fullName;
				if ( name.indexOf('.') < 0 ) {
					// if it does not have already a package in the name
					fullName = pn + name;
				}
				else {
					fullName = name;
				}
				ret = Class.forName(fullName);
				if ( ret != null ) {
					return ret;
				}
			}
			catch (ClassNotFoundException e) {
			}
		}
		return ret;
	}

	public void addLocalVar(VariableDecInterface localVar ) {
		VariableDecInterface other = searchLocalVar(localVar.getName());
		if ( other != null ) {
			this.error(localVar.getFirstSymbol(), "Variable '" + localVar.getName() + "' is being redeclared. The other variable is in line " +
		       other.getFirstSymbol().getLineNumber() );
		}
		localVarDecStack.push(localVar);
	}

	public void pushLexicalLevel() {
		numVarLevelStack.push(localVarDecStack.size());
	}

	public void popLexicalLevel() {
		final int n = numVarLevelStack.pop();
		while ( n < localVarDecStack.size() ) {
			localVarDecStack.pop();
		}
	}
	private final Stack<Integer> numVarLevelStack;

	public VariableDecInterface searchLocalVar(String name) {
		int size = localVarDecStack.size();
		for (int i = size - 1; i >= 0; i--) {
			VariableDecInterface varDec = localVarDecStack.get(i);
			if ( varDec.getName().equals(name) ) {
				return varDec;
			}
		}
		return null;
	}

	public Object searchPrototypeAsExpression(String name) {


		Class<?> aClass = this.searchPrototypeAsType(name);
		if ( aClass == null || (!EvalEnv.any.isAssignableFrom(aClass) && EvalEnv.nil != aClass) ) {
//			this.error(
//					this.symbolForErrorMessage,
//					"An unidentified variable or a non-prototype, like a Java class, used inside an expression. Its name is '" + name + "'");
			return null;

		}
		else {
			Object ret = null;
			try {
				Field f = aClass.getDeclaredField(NameServer.prototypeFieldInJava);
				f.setAccessible(true);
				if( f.isAccessible() ) {
					ret = f.get(null);
				}
				else {
					this.error(
							this.symbolForErrorMessage,
							"Error when accessing field '" +
									NameServer.prototypeFieldInJava + "' of  '" + name + "'");
					return null;
				}
				//ret = aClass.getField(NameServer.prototypeFieldInJava).get(null);
			}
			catch (IllegalArgumentException | IllegalAccessException
					| NoSuchFieldException | SecurityException e) {
				this.error(
						this.symbolForErrorMessage,
						"Error when accessing field '" +
								NameServer.prototypeFieldInJava + "' of  '" + name + "'");
				return null;
				/*env.error(meta.GetHiddenItem.getHiddenSymbol(symbolForErrorMessage),
						"Error when accessing field '" +
				          NameServer.prototypeFieldInJava + "' of  '" + name + "'"); */
			}
			catch (Throwable e) {
				this.error(
						this.symbolForErrorMessage,
						"Error when accessing field '" +
								NameServer.prototypeFieldInJava + "' of  '" + name + "'");
				return null;
			}
			if ( ret != null && ret instanceof Throwable ) {
				this.error(
						this.symbolForErrorMessage,
						"Internal error of method 'Class.getField'. The value returned is exception '" +
						          ret.getClass().getName() + "'");
				return null;
			}
			return ret;
		}
	}

	/**
	 * search in the imported packages, by the Cyan code being interpreted,
	 * by a class whose name is 'name'.
	   @param name
	   @param classJavaName
	   @return
	 */

	private Class<?> searchClassImportedPackages(String name) {
		int dotIndex = name.indexOf('.');
		int lessThanIndex = name.indexOf('<');
		boolean hasPackageName;
		if ( lessThanIndex >= 0 ) {
			if ( dotIndex < 0 ) {
				// Tuple<Int, Int>
				hasPackageName = false;
			}
			else if ( dotIndex < lessThanIndex ) {
				// util.MySet<Int>
				hasPackageName = true;
			}
			else {
				// util.MySet<main.Person>
				hasPackageName = false;
			}
		}
		else {
			// Int   or  cyan.lang.Int
			hasPackageName = dotIndex >= 0;
		}
		List<String> packageNameListToSearch;
		if ( hasPackageName ) {
			packageNameListToSearch = new ArrayList<>();
			packageNameListToSearch.add("");
		}
		else {
			// no package, search in all imported
			packageNameListToSearch = this.importedPackageNameList;
		}
		Class<?> aClass = null;
		ClassLoader cl = currentThread.getContextClassLoader();

		for ( String packageName : packageNameListToSearch ) {
			try {
				String fullName;
				if ( packageName.length() > 0 ) {
					fullName = packageName + "." + name;
				}
				else {
					fullName = name;
				}
				aClass = cl.loadClass(fullName);
				if ( aClass != null ) {
					return aClass;
				}
			}
			catch (ClassNotFoundException  e) {
			}
		}
		return null;
	}

	/**
	 * search 'name' in several places, in this order:
	 *
	 * (a) the imported class map, which currently keeps only
	 * classes from cyan.lang and from the Cyan runtime library. First
	 * looks for Cyan prototypes whose name is 'name' (the search is
	 * make with the corresponding Java name, then 'Int' is transformed into 'CyInt')
	 * then this method looks
	 * for Java classes whose name is 'name' (the search is made with 'name'
	 * without modifications)
	 * (b) the imported packages, both considering 'name' as a Cyan name
	 * and as a Java name;
	 * (c) if the class is not found and it is a generic prototype instantiation,
	 * like 'Tuple2<Integer, Char>', the name is stripped of its parameters.
	 * Then 'Tuple2<Integer, Char>' becomes 'Tuple2'. This new name is looked for
	 * in java.lang and in package 'meta' of the compiler. If a class is not
	 * found, a recursive search is made in this method using the stripped name.
	 * (d) if a class is not found and it is NOT a generic prototype instantiation,
	 * a search is made in java.lang and in package 'meta' of the compiler.
	 *
	   @param name
	   @return
	 */
	public Class<?> searchPrototypeAsType(String name) {
//		return this.importedClassMap.get(name);

		// name of the class assuming 'name' is a Cyan prototype
		String classJavaName = MetaHelper.getJavaName(name);
		// first search assuming 'name' is a Cyan prototype
		Class<?> aClass = null;
		Tuple2<JVMPackage, ClassLoader> t = importedClassMapPackageLoader.get(classJavaName);
		if ( t != null ) {
			aClass = loadJavaClass(classJavaName, t);
		}


		if ( aClass == null ) {
			// now search assuming 'name' is the real Java name
			aClass = this.importedClassMap.get(name);
			t = importedClassMapPackageLoader.get(name);
			if ( t != null ) {
				aClass = loadJavaClass(name, t);
			}

			if ( aClass == null ) {
				// search in the imported packages
				aClass = searchClassImportedPackages(classJavaName);
				if ( aClass == null ) {
					aClass = searchClassImportedPackages(name);
				}
			}
		}

		if ( aClass == null ) {
			/*
			 * search as if it were a Java class
			 */
			int indexLessThan = name.indexOf('<');

			/*
			 * possibilities:
			 * 		String
			 *      java.lang.String
			 *      java.util.Set<String>
			 *      Set<String>
			 *      Set<java.lang.String>
			 *      java.util.Set<java.lang.String>
			 */
			if ( indexLessThan >= 0 ) {
				String rnameStripped = name.substring(0, indexLessThan);
				aClass = searchJavaClass_MetaJavaLang(rnameStripped);
				if ( aClass == null ) {
					aClass = searchPrototypeAsType(rnameStripped);
				}
			}
			else {
				aClass = searchJavaClass_MetaJavaLang(name);
			}


		}



		return aClass;
	}


	/**
	   @param classJavaName
	   @param aClass
	   @param t
	   @return
	 */
	private Class<?> loadJavaClass(String classJavaName,
			            Tuple2<JVMPackage, ClassLoader> t) {
		/*
		 * the Java class of a Cyan prototype has been imported. That is,
		 * it has been put in a package that is inserted into map jvmPackageMap.
		 * Retrieve it and load it.
		 */

		JVMPackage jvmPackage = t.f1;
		String packageName = jvmPackage.getPackageName();
		ClassLoader cl = t.f2;
		TypeJavaRef javaClass = jvmPackage.searchJVMClass(classJavaName);
		// javaClass should not be null
		if ( javaClass == null ) {
			this.error(this.symbolForErrorMessage, "Internal error in searchPrototypeAsType");
			return null;
		}
		if ( javaClass.getTheClass() == null ) {
			try {

				final String fullClassName = packageName + "." + classJavaName;
				Class<?> aClass = Class.forName(fullClassName, true, cl);
				if ( aClass != null ) {
					javaClass.setaClass(aClass);
					importedClassMap.put(classJavaName, aClass);
					return aClass;
				}
			}
			catch (final ClassNotFoundException e1) {
			}
		}
		return null;
	}

	public JVMPackage searchPackage(String name) {
		return this.jvmPackageMap.get(name);
	}

	/**
	 * symbol used in error messages
	 */
	WrSymbol symbolForErrorMessage;

	/*
	 * 		Class<?> cyanClass = classRef.getaClass(ee.env, exprLiteral.getFirstSymbol());

	 */
    public static Class<?> cyBoolean = null;
    public static Class<?> cyChar = null;
    public static Class<?> cyByte = null;
    public static Class<?> cyShort = null;
    public static Class<?> cyInt = null;
    public static Class<?> cyLong = null;
    public static Class<?> cyFloat = null;
    public static Class<?> cyDouble = null;
    public static Class<?> cyString= null;
    public static Class<?> any = null;
    public static Class<?> nil = null;

    public static Class<?> exceptionContainer__ = null;
    public static Class<?> exceptionStr = null;

    public static Constructor<?> cyBooleanConstructor  = null;
    public static Constructor<?> cyCharConstructor  = null;
    public static Constructor<?> cyByteConstructor  = null;
    public static Constructor<?> cyIntConstructor  = null;
    public static Constructor<?> cyShortConstructor  = null;
    public static Constructor<?> cyLongConstructor  = null;
    public static Constructor<?> cyFloatConstructor  = null;
    public static Constructor<?> cyDoubleConstructor  = null;
    public static Constructor<?> cyStringConstructor  = null;


    public static Constructor<?> exceptionContainer__Constructor  = null;
    public static Constructor<?> exceptionStrConstructor  = null;

    public static Object nilValue = null;

	public static Thread currentThread = null;
	private static boolean alreadyLoadedBasicCyanPrototypes = false;

	public static void loadBasicCyanPrototypes(Function1<String> inError) {
		if ( alreadyLoadedBasicCyanPrototypes ) { return ; }
		if ( currentThread == null ) {
			currentThread = Thread.currentThread();
		}
		try {
			cyBoolean = cyan.lang.CyBoolean.class;
			cyChar = cyan.lang.CyChar.class;
			cyByte = cyan.lang.CyByte.class;
			cyInt = cyan.lang.CyInt.class;
			cyShort = cyan.lang.CyShort.class;
			cyLong = cyan.lang.CyLong.class;
			cyFloat = cyan.lang.CyFloat.class;
			cyDouble = cyan.lang.CyDouble.class;
			cyString = cyan.lang.CyString.class;
			any = cyan.lang._Any.class;
			nil = cyan.lang._Nil.class;
			exceptionContainer__ = cyanruntime.ExceptionContainer__.class;
			exceptionStr = cyan.lang._ExceptionStr.class;
			Class<?> cyException = cyan.lang._CyException.class;
			nilValue = nil.getField("prototype").get(null);

			cyBooleanConstructor = cyBoolean.getConstructor(boolean.class);
			cyBooleanConstructor.setAccessible(true);

			cyCharConstructor = cyChar.getConstructor(char.class);
			cyCharConstructor.setAccessible(true);

			cyByteConstructor = cyByte.getConstructor(int.class);
			cyByteConstructor.setAccessible(true);

			cyIntConstructor = cyInt.getConstructor(int.class);
			cyIntConstructor.setAccessible(true);

			cyShortConstructor = cyShort.getConstructor(int.class);
			cyShortConstructor.setAccessible(true);

			cyLongConstructor = cyLong.getConstructor(long.class);
			cyLongConstructor.setAccessible(true);

			cyFloatConstructor = cyFloat.getConstructor(float.class);
			cyFloatConstructor.setAccessible(true);

			cyDoubleConstructor = cyDouble.getConstructor(double.class);
			cyDoubleConstructor.setAccessible(true);

			cyStringConstructor = cyString.getConstructor(String.class);
			cyStringConstructor.setAccessible(true);


			exceptionContainer__Constructor = exceptionContainer__.getConstructor(cyException);
			exceptionContainer__Constructor.setAccessible(true);

			exceptionStrConstructor = exceptionStr.getConstructor(cyString);
			exceptionStrConstructor.setAccessible(true);

			alreadyLoadedBasicCyanPrototypes = true;
		}
		catch (NoSuchMethodException | SecurityException
				| IllegalArgumentException | IllegalAccessException | NoSuchFieldException | NoClassDefFoundError e) {
			inError.eval("Error when loading basic prototypes of Cyan from the "
					+ "directory given by environment variable CYAN_HOME. "
					+ "Reinstall the Cyan compiler");
		}

	}

	public Object newCyBoolean(boolean value) {

		return new cyan.lang.CyBoolean(value);
	}


	public Object newCyChar(char value) {
		return new cyan.lang.CyChar(value);
	}

	public Object newCyByte(byte value) {
		return new cyan.lang.CyByte(value);
	}

	public Object newCyInt(int value) {
		return new cyan.lang.CyInt(value);
	}

	public Object newCyShort(short value) {
		return new cyan.lang.CyShort(value);
	}

	public Object newCyLong(long value) {
		return new cyan.lang.CyLong(value);
	}

	public Object newCyFloat(float value) {
		return new cyan.lang.CyFloat(value);
	}

	public Object newCyDouble(double value) {
		return new cyan.lang.CyDouble(value);
	}

	public Object newCyString(String value) {
		return new cyan.lang.CyString(value);
	}


	public Object newExceptionContainer__(Object value) {

		if ( value instanceof _CyException ) {
			return new cyanruntime.ExceptionContainer__( (_CyException ) value );
		}
		else {
			this.error(this.symbolForErrorMessage,
					"When creating an object of ExceptionContainer__, parameter has class " + value.getClass().getCanonicalName() +
					" but it should have class '" + _CyException.class.getCanonicalName() + "' or prototype CyException");
			return null;
		}

	}

	public Object newExceptionStr(String value) {
		return new cyan.lang._ExceptionStr(new cyan.lang.CyString(value));
	}


	public static void loadClassFromUrlList(List<String> urlList, String binaryClassName) {
		// https://stackoverflow.com/questions/7884393/can-a-directory-be-added-to-the-class-path-at-runtime
		final ClassLoader prevCl = Thread.currentThread().getContextClassLoader();

		for ( final String url : urlList ) {
			URLClassLoader urlCl = null;
			try {

				// Create class loader using given codebase
				// Use prevCl as parent to maintain current visibility
				urlCl = URLClassLoader.newInstance(new URL[]{ new URL(url) }, prevCl);
				// Save class loader so that we can restore later
				Thread.currentThread().setContextClassLoader(urlCl);

				// Expect that environment properties are in
				// application resource file found at "url"
				final Context ctx = new InitialContext();

				urlCl.loadClass(binaryClassName);


				// Close context when no longer needed
				ctx.close();
			}
			catch (final NamingException e) {
			}
			catch (final MalformedURLException e) {
			}
			catch (final ClassNotFoundException e) {
			} finally {
				if ( urlCl != null ) {
					try {
						urlCl.close();
					}
					catch (final IOException e) {
					}
				}
				Thread.currentThread().setContextClassLoader(prevCl);
			}

		}
	}


	public void addVariable(String varName, Object value) {
		memory.put(varName, value);
	}

	public boolean setValueInInterpreter(String varName, Object value) {
		if ( memory.get(varName) != null ) {
			memory.put(varName, value);
		}
		else if ( fieldMemory != null && fieldMemory.get(varName) != null ) {
			this.fieldMemory.put(varName, value);
		}
		else {
			return false;
		}
		return true;
	}


	public Object getVariableValue(String varName) {

		Object r = memory.get(varName);
		if ( r == null && fieldMemory != null ) {
			r = this.fieldMemory.get(varName);
		}
		return r;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public void incLoop() {
		++countLoop;
	}
	public void decLoop() {
		--countLoop;
	}

	public int getCountLoop() {
		return countLoop;
	}



	public boolean getProperlyInitialized() {
		return properlyInitialized;
	}



	public void setFieldMemory(Map<String, Object> fieldMemory) {
		this.fieldMemory = fieldMemory;
	}


	public Object getSelfObject() {
		return selfObject;
	}


	public void setSelfObject(Object selfObject) {
		this.selfObject = selfObject;
	}

	public void addCreatedJavaObject(Object newObject) {
		if ( createdJavaObjectList == null ) {
			createdJavaObjectList = new ArrayList<>();
		}
		createdJavaObjectList.add(newObject);
	}

	public List<Object> getCreatedJavaObjectList() {
		return createdJavaObjectList;
	}

	/*
	 * memory with local variables and other accessible objects. The String is the variable name
	 * and Object is the value stored in the variable. It has type Type.
	 */
	Map<String, Object> memory;

	/*
	 * memory for fields of the metaobject
	 */
	Map<String, Object> fieldMemory;

	/**
	 * a list of JVM package of the program
	 */
	private final List<JVMPackage> jvmPackageList;

	public Map<String, JVMPackage> jvmPackageMap;

	private final Stack<VariableDecInterface> localVarDecStack;

	private final Map<String, Class<?>> importedClassMap;

	/*
	 * name and packages of imported classes/prototypes
	 */
	private final Map<String, Tuple2<JVMPackage, ClassLoader>> importedClassMapPackageLoader;

	/**
	 * return value of statements
	 */
	private Object returnValue;

	/**
	 * count the loop nesting:
	 * while expr {
	 *     // countLoop == 1
	 *     for elem in array {
	 *     	  // countLoop == 2
	 *     }
	 * }
	 */
	private int countLoop = 0;
	/**
	 * path of directory 'lib' that keeps the jar files of the Cyan library
	 */
	private String cyanLangPath;

	/**
	 * true if the object was properly initialized
	 */
	private boolean properlyInitialized;

	/**
	 * list of Java objects created by the interpreter
	 */
	private List<Object> createdJavaObjectList;

}

