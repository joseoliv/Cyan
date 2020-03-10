package saci;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;

public class LoadUtil {

//	@SuppressWarnings("resource")
//	private static void kk() {
//		String cyanHome = System.getenv("CYAN_HOME");
//		if ( cyanHome == null ) {
//			System.out.println("cyan home is null");
//			return ;
//		}
//		File f = new File(cyanHome);
//		if ( ! f.exists() || !f.isDirectory() ) {
//			System.out.println("directory '" + cyanHome +
//					"' does not exist. It is as the value of the System variable CYAN_HOME");
//			return ;
//		}
//		URL []urlArray = new URL[1];
//		try {
//			urlArray[0] = f.toURI().toURL();
//			ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
//			ClassLoader urlCl = URLClassLoader.newInstance(urlArray, prevCl);
//			Thread.currentThread().setContextClassLoader(urlCl);
//			Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("cyan.lang._ExceptionReadFormat");
//			System.out.println(aClass.getName());
//		}
//		catch (MalformedURLException | ClassNotFoundException e) {
//			System.out.println("Error in transforming the directory "
//					+ "given by environment variable CYAN_HOME into a URL");
//			System.exit(1);
//		}
//
//	}

//	public static void main(String []args) {
//
//		kk();
//		String cyanHome = System.getenv("CYAN_HOME");
//		if ( cyanHome == null ) {
//			System.out.println("cyan home is null");
//			return ;
//		}
//		File f = new File(cyanHome);
//		if ( ! f.exists() || !f.isDirectory() ) {
//			System.out.println("directory '" + cyanHome +
//					"' does not exist. It is as the value of the System variable CYAN_HOME");
//			return ;
//		}
//
//
//		//Set<String> packageNameSet = new HashSet<>();
//		//LoadUtil.classesFromPackage("cyan.lang");
//		//printPackageSet(packageNameSet);
//
//		addJarDirToClassPath(new String[] { cyanHome, cyanHome + File.separator + "javalib" },
//				new String[] { "C:\\Dropbox\\Cyan\\cyanTests\\java-for-master"} );
//
//		// packageNameSet = new HashSet<>();
//		//Set<Class<? extends Object>> classSet = LoadUtil.classesFromPackage("cyan.lang");
//
//		// printPackageSet(packageNameSet);
//
//		Class<?> anyClass;
//		try {
//			ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
//			anyClass = currentClassLoader.loadClass("cyan.lang._Any");
//			// Class.forName("cyan.lang._Any");
//			Object any = anyClass.newInstance();
//			System.out.println(any.getClass().getName());
//
//			Class<?> cyString = currentClassLoader.loadClass("cyan.lang.CyString");
//			Constructor<?> consCyString = cyString.getConstructor(String.class);
//			Object objCyString = consCyString.newInstance("Carol");
//
//			Class<?> cyInt = currentClassLoader.loadClass("cyan.lang.CyInt");
//			Constructor<?> consCyInt = cyInt.getConstructor(int.class);
//			Object objCyInt = consCyInt.newInstance(8);
//
//			anyClass = currentClassLoader.loadClass("people._Person");
//			Constructor<?> cons = anyClass.getConstructor(cyString, cyInt);
//			any = cons.newInstance(objCyString, objCyInt);
//
//			java.lang.reflect.Method m = anyClass.getMethod("_name");
//
//			System.out.println(m.invoke(any).toString());
//		}
//		catch (   ClassNotFoundException | InstantiationException
//				| IllegalAccessException | NoSuchMethodException
//				| SecurityException | IllegalArgumentException
//				| InvocationTargetException e) {
//		}
//
//	}
//
//	@SuppressWarnings("resource")
//	public static void addJarsToClassPath(String[] args) {
//
//		ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
//		try {
//			URL []urlList = new URL[ args.length ];
//			int i = 0;
//			for (String s : args) {
//				urlList[i] = new URL("jar:file:///" + s.replace('\\', '/') + "!/");
//				++i;
//			}
//			ClassLoader urlCl = URLClassLoader.newInstance(urlList, prevCl);
//			Thread.currentThread().setContextClassLoader(urlCl);
//		}
//		catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//	}


	/**
	 * collect all '.jar' files from path 'path' to 'urlList'. The return is null
	 * if no error or an error message.
	 *
	   @param urlList
	   @param path
	   @return
	 */
	public static CollectError collectJarsFromPath(List<URL> urlList, String path, String []exceptThese) {
		File f = new File(path);
		try {
			if ( f.isDirectory() ) {
				for ( File inside : f.listFiles() ) {
					String canPath = inside.getCanonicalPath();
//					String name = inside.getName();
//					String absName = inside.getAbsolutePath();
					if ( canPath.endsWith(".jar") ) {
						if ( exceptThese != null ) {
							boolean found = false;
							for ( String exceptThis : exceptThese ) {
								if ( exceptThis.equalsIgnoreCase(canPath) ) {
									found = true;
									break;
								}
							}
							if ( found ) { continue; }
						}
						urlList.add( inside.toURI().toURL() );
						// urlList.add( new URL("jar:file:///" + canPath.replace('\\', '/') + "!/") ) ;

					}
				}
			}
		}
		catch (IOException e) {
			return CollectError.Exception_Thrown;
		}
		return null;

	}


	/**
	 * add to the current class loader the following:<br>
	 * all jar files of directories of pathWithJarsArray and all directories of pathToAdd.
	 * Return either a value of CollectError with an error or the previous ClassLoader object.
	   @param pathWithJarsArray
	   @param pathToAdd
	   @param toRun
	   @return
	 */

	@SuppressWarnings("resource")
	public static Object addJarDirToClassPath(String[] pathWithJarsArray, String []pathToAdd,
			String[] exceptThese) {
		List<URL> urlList = new ArrayList<>();
		if ( pathWithJarsArray != null ) {
			for ( String s : pathWithJarsArray ) {
				CollectError ce = collectJarsFromPath(urlList, s, exceptThese);
				if ( ce != null ) {
					return ce;
				}
			}
		}
		if ( pathToAdd != null ) {
			for ( String s : pathToAdd ) {
				try {
					URL url = (new File(s)).toURI().toURL();
					urlList.add( url );
				}
				catch (MalformedURLException e) {
					return CollectError.Exception_Thrown;
				}
			}
		}
		URL []urlArray = new URL[urlList.size()];
		int k = 0;
		for ( URL url : urlList ) {
			urlArray[k++] = url;
		}

		ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
		ClassLoader urlCl = URLClassLoader.newInstance(urlArray, prevCl);
		Thread.currentThread().setContextClassLoader(urlCl);
		return prevCl;
	}

	/**
	 * add to the current class loader the following:<br>
	 * all jar files of directories of pathWithJarsArray and all directories of pathToAdd.
	 * Then call method 'run' of toRun and restore the previous class Loader
	   @param pathWithJarsArray
	   @param pathToAdd
	   @param toRun
	   @return
	 */
	public static CollectError addToClassPathRun(String []pathWithJarsArray, String []pathToAdd, Runnable toRun) {
		Object r;
		ClassLoader prevCl = null;
		try {
			r = addJarDirToClassPath(pathWithJarsArray, pathToAdd, new String[] { "saci.jar" });
			if ( r instanceof ClassLoader ) {
				prevCl = (ClassLoader ) r;
			}
			toRun.run();
		}
		finally {
			if ( prevCl != null ) {
				Thread.currentThread().setContextClassLoader(prevCl);
			}
		}
		if ( r instanceof CollectError ) {
			return (CollectError ) r;
		}
		else {
			return null;
		}

	}


	 /** add to the current class loader the following:<br>
	 * all URL
	 * Then call method 'run' of toRun and restore the previous class Loader
	   @param pathWithJarsArray
	   @param pathToAdd
	   @param toRun
	   @return
	 */
	public static void addURL_ToClassPathRun(URL []urlArray, Runnable toRun) {
		ClassLoader prevCl = null;
		URLClassLoader urlCl = null;
		try {
			prevCl = Thread.currentThread().getContextClassLoader();
			urlCl = URLClassLoader.newInstance(urlArray, prevCl);
			Thread.currentThread().setContextClassLoader(urlCl);
			toRun.run();
		}
		finally {
			if ( prevCl != null ) {
				Thread.currentThread().setContextClassLoader(prevCl);
			}
			if ( urlCl != null ) {
				try {
					urlCl.close();
				}
				catch (IOException e) {
				}
			}
		}
	}



	public static CollectError addSaciOutputDirToClassPath(String projectDir, Runnable toRun) {


		final char separator = NameServer.fileSeparatorAsString.charAt(0);
		// String projectDir = env.getProject().getProjectDir();

		String partialProjectDir;
		if ( projectDir.charAt(projectDir.length()-1) == separator )
			partialProjectDir = projectDir.substring(0, projectDir.length()-1);
		else
			partialProjectDir = projectDir;
		int lastSlash = partialProjectDir.lastIndexOf(separator);
		if ( lastSlash < 0 ) {
			return CollectError.Path_Error;
		}
		// partialProjectDir = partialProjectDir.substring(0, partialProjectDir.length() - 1);
		lastSlash = partialProjectDir.lastIndexOf(separator);
		if ( lastSlash < 0 ) {
			return CollectError.Path_Error;
		}
		// here partialProjectDir is the directory in which the project dir is.
		final String projectDirName = partialProjectDir.substring(lastSlash+1);
		partialProjectDir = partialProjectDir.substring(0, lastSlash);
		final String javaForProjectPath = partialProjectDir + separator +  NameServer.startDirNameOutputJavaCode + projectDirName + separator;


		return LoadUtil.addToClassPathRun(null, new String [] { javaForProjectPath }, toRun);
	}


	public static Set<Class<? extends Object>> classesFromPackage(String packageName) {
		 Reflections reflections = new Reflections(packageName);

		 Set<Class<? extends Object>> allClasses =
		     reflections.getSubTypesOf(Object.class);
		 return allClasses;
	}



	/*
	 * taken from https://stackoverflow.com/questions/30106279/dynamically-load-a-class-and-its-object-in-java
	 */
	public static Serializable readObject(String path, final ClassLoader classLoader)
			throws FileNotFoundException, IOException, ClassNotFoundException {
	    Serializable obj;
	    try (FileInputStream fis = new FileInputStream(path);
	    		ObjectInputStream ois = new ObjectInputStream(fis) {

	        @Override
	        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
	            if (classLoader == null) {
	                return super.resolveClass(desc);
	            }
	            return Class.forName(desc.getName(), false, classLoader);
	        }

	        })
	    {
	        obj = (Serializable) ois.readObject();
	    }
	    return obj;
	}



	public static byte [] readObjectToByteArray(String path, final ClassLoader classLoader)
			throws FileNotFoundException, IOException, ClassNotFoundException {
	    byte [] byteArray = null;
	    try (FileInputStream fis = new FileInputStream(path);
	    		ObjectInputStream ois = new ObjectInputStream(fis) {

	        @Override
	        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
	            if (classLoader == null) {
	                return super.resolveClass(desc);
	            }
	            return Class.forName(desc.getName(), false, classLoader);
	        }

	        })
	    {
	    	byteArray = new byte[ois.available()];
    		ois.readFully(byteArray);
	    }
	    return byteArray;
	}


	public static byte []writeObjectToByteArray(Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(obj);
	    oos.flush();
	    return baos.toByteArray();
	}


	public static Serializable readObjectFromByteArray(byte []byteArray) throws ClassNotFoundException, IOException {

		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		Object obj = null;
	    try ( ObjectInputStream ois = new ObjectInputStream(bais) ) {
	        obj = ois.readObject();
	    }
	    return (Serializable) obj;
	}

//	/**
//	   @param packageNameSet
//	 */
//	public static void classesFromPackage3(Set<String> packageNameSet, String packageName) {
//		final List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
//		classLoadersList.add(ClasspathHelper.contextClassLoader());
//		classLoadersList.add(ClasspathHelper.staticClassLoader());
//
//		ClassLoader []classLoaderArray = classLoadersList.toArray(new ClassLoader[0]);
//		// URLClassLoader urlcl = new URLClassLoader( new URL[] { } );
//		final Reflections reflections = new Reflections(new ConfigurationBuilder()
//				.setScanners(new SubTypesScanner(false), new ResourcesScanner())
//				.setUrls(ClasspathHelper.forClassLoader(classLoaderArray)
//				).filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))
//						))
//				;
//		/*
//		 *  URLClassLoader urlcl = new URLClassLoader(urls);
//			Reflections reflections = new Reflections(
//  				new ConfigurationBuilder().setUrls(
//    		            ClasspathHelper.forClassLoader(urlcl)
//  			        ).addClassLoader(urlcl)
//);
//		 */
//
//		try {
//			final Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
//			for ( Class<?> aClass : classes ) {
//				final String packageName2 = aClass.getPackage().getName();
//				packageNameSet.add(packageName2);
//			}
//
//		}
//		catch ( org.reflections.ReflectionsException e ) {
//			System.out.println("Exception org.reflections.ReflectionsException thrown");
//		}
//	}

//	/**
//	   @param packageNameSet
//	 */
//	public static void printPackageSet(Set<String> packageNameSet) {
//		System.out.println("packages");
//		String []packageNameArray = new String[packageNameSet.size()];
//		int kk = 0;
//		for ( String pn : packageNameSet ) {
//			packageNameArray[kk] = pn;
//			++kk;
//		}
//		Arrays.sort(packageNameArray);
//		for (String packageName1 : packageNameArray) {
//			System.out.println(packageName1);
//		}
//	}


}
