
package meta;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import ast.CyanPackage;
import ast.Program;
import error.CompileErrorException;
import lexer.Symbol;
import saci.Env;
import saci.Project;
import saci.Saci;

public class Timeout<T> {

	public T addURL_ToClassPathRun(URL[] urlArray, Callable<T> toCall) {
		ClassLoader prevCl = null;
		URLClassLoader urlCl = null;
		try {
			prevCl = Thread.currentThread().getContextClassLoader();
			urlCl = URLClassLoader.newInstance(urlArray, prevCl);
			Thread.currentThread().setContextClassLoader(urlCl);
			// if ( urlArray[0].getPath().contains("xom") ) {
			// Class<?> builder = urlCl.loadClass("nu.xom.Builder");
			// //nu.xom.Builder
			// builder.newInstance();
			// }
			return toCall.call();
		}
		catch (CompileErrorException e) {
			errorMessage = e.getMessage();
		}
		catch (Exception e) {
			errorMessage = "Exception " + e.getCause().getClass().getName()
					+ " thrown ";
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
					errorMessage = "Exception "
							+ e.getCause().getClass().getName() + " thrown ";
				}
			}
		}
		return null;
	}

	@SuppressWarnings("resource")
	public T addPackageMetaToClassPath_and_Run(CyanPackage cyanPackage,
			Callable<T> toCall) {

		if ( cyanPackage == null ) {
			/*
			 * this only happens when no package was created
			 */
			try {
				return toCall.call();
			}
			catch (CompileErrorException e) {
				errorMessage = e.getMessage();
				return null;
			}
			catch (Exception e) {
				errorMessage = "Exception " + e.getCause().getClass().getName()
						+ " thrown ";
				return null;
			}
		}
		URL urlArray[] = cyanPackage.getUrlArray();
		if ( urlArray == null || urlArray.length == 0 ) {
			cyanPackage.calculateURLs();
		}
		if ( urlArray != null && urlArray.length > 0 ) {
			return addURL_ToClassPathRun(urlArray, toCall);
		}
		else {
			try {
				return toCall.call();
			}
			catch (CompileErrorException e) {
				errorMessage = e.getMessage();
				return null;
			}
			catch (MetaSecurityException e) {
				errorMessage = "There was a security exception. Some metaobject tried to access private information which it does not have access to";
				throw e;
			}
			catch (Exception e) {
				Throwable cause = e.getCause();
				errorMessage = "Exception " + cause.getClass().getName()
						+ " thrown ";
				return null;
			}
		}
	}

	/**
	 * either the project or the environment must be non-null
	 *
	 * @param toCall
	 * @param timeoutMilliSec
	 * @param metaobjectMethodName
	 * @param metaobject
	 * @return
	 */
	public T run(Callable<T> toCall, int timeoutMilliSec,
			String metaobjectMethodName, CyanMetaobject metaobject,
			Project project) {
		CyanPackage cyanPackageMO = null;
		if ( metaobject.getCyanPackage() == null ) {
			String cyanPackageNameMO = metaobject.getPackageName();
			cyanPackageMO = project.searchPackage(cyanPackageNameMO);
			metaobject.setCyanPackage(cyanPackageMO);
		}
		return runAux(toCall, timeoutMilliSec, metaobjectMethodName,
				metaobject);
	}

	public T run(Callable<T> toCall, int timeoutMilliSec,
			String metaobjectMethodName, CyanMetaobject metaobject, Env env) {
		if ( metaobject.getCyanPackage() == null ) {
			String cyanPackageNameMO = metaobject.getPackageName();
			CyanPackage cyanPackageMO;
			cyanPackageMO = env.searchPackage(cyanPackageNameMO);
			metaobject.setCyanPackage(cyanPackageMO);
		}
		return runAux(toCall, timeoutMilliSec, metaobjectMethodName,
				metaobject);
	}

	public T runAux(Callable<T> toCall, int timeoutMilliSec,
			String metaobjectMethodName, CyanMetaobject metaobject) {

		Saci.numThreadsMO++;
		try {
			if ( timeoutMilliSec > 0 ) {
				TimeBomb timeBomb = new TimeBomb(timeoutMilliSec,
						metaobjectMethodName, metaobject);
				final T t;
				t = addPackageMetaToClassPath_and_Run(
						metaobject.getCyanPackage(), () -> {
							return toCall.call();
						});
				timeBomb.stop();
				return t;
			}
			else {
				return addPackageMetaToClassPath_and_Run(
						metaobject.getCyanPackage(), () -> {
							return toCall.call();
						});
			}
		}
		catch (CompileErrorException e) {
			errorMessage = e.getMessage();
		}
		catch (MetaSecurityException e) {
			errorMessage = "There was a security exception. Some metaobject tried to access private information which it does not have access to";
			throw e;
		}

		catch (Exception e) {
			errorMessage = "Exception " + e.getCause().getClass().getName()
					+ " thrown ";
		}
		return null;
	}

	/**
	 * Run toCall. If the execution does not end within timeoutMilliSec
	 * milliseconds, an error message, using parameter metaobjectMethodName, is
	 * put in a field that can be retrieved with method getErrorMessage. This
	 * method returns the value returned by toCall.
	 *
	 */
	public T runNotUsed(Callable<T> toCall, int timeoutMilliSec,
			String metaobjectMethodName, CyanMetaobject metaobject) {
		errorMessage = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);
		try {
			Future<T> future = executor.submit(toCall);
			return future.get(timeoutMilliSec, TimeUnit.MILLISECONDS);
		}
		catch (ExecutionException e) {
			errorMessage = "Exception " + e.getCause().getClass().getName()
					+ " thrown ";
		}
		catch (InterruptedException e) {
			errorMessage = "Metaobject method '" + metaobjectMethodName
					+ "' of metaobject " + metaobject.getName() + " of line "
					+ metaobject
							.getAnnotation().getFirstSymbol().getLineNumber()
					+ " of file "
					+ metaobject.getAnnotation().getFirstSymbol()
							.getCompilationUnit().getFullFileNamePath()
					+ " took too long to finish. You can change the timeout "
					+ "settings by changing the value of '"
					+ MetaHelper.timeoutMillisecondsMetaobjectsStr
					+ "' using @options which should be attached either to the package or the program";
		}
		catch (TimeoutException e) {
			errorMessage = "Metaobject method '" + metaobjectMethodName
					+ "' of metaobject " + metaobject.getName() + " of line "
					+ metaobject
							.getAnnotation().getFirstSymbol().getLineNumber()
					+ " of file "
					+ metaobject.getAnnotation().getFirstSymbol()
							.getCompilationUnit().getFullFileNamePath()
					+ " took too long to finish. You can change the timeout "
					+ "settings by changing the value of '"
					+ MetaHelper.timeoutMillisecondsMetaobjectsStr
					+ "' using @options which should be attached either to the package or the program";
		}
		catch (CompileErrorException e) {
			errorMessage = e.getMessage();
			return null;
		}
		catch (Exception e) {
			errorMessage = e.getCause().getClass().getName() + " thrown ";
		}

		return null;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * return the timeout, in milliseconds, for metaobject methods.
	 * 'annotSymbol' is the symbol used for error messages
	 *
	 * @param env
	 * @param program
	 * @param cpackage
	 * @return
	 */
	static public int getTimeoutMilliseconds(Env env, WrProgram program,
			WrCyanPackage cpackage, Symbol annotSymbol) {
		if ( MetaHelper.onDebug ) {
			return Integer.MAX_VALUE;
		}
		int timeoutMilliseconds = MetaHelper.timeoutMillisecondsMetaobjectsDefaultValue;
		Object timeoutMetaobjectsInteger = null;
		if ( cpackage != null ) {
			timeoutMetaobjectsInteger = cpackage.getPackageValueFromKey(
					MetaHelper.timeoutMillisecondsMetaobjectsStr);
		}
		if ( timeoutMetaobjectsInteger != null ) {
			if ( !(timeoutMetaobjectsInteger instanceof Integer) ) {
				env.error(annotSymbol, "The package key value "
						+ MetaHelper.timeoutMillisecondsMetaobjectsStr
						+ ", set with @options, has a type that is not 'Integer'");
				return MetaHelper.timeoutMillisecondsMetaobjectsDefaultValue;
			}
			timeoutMilliseconds = (Integer) timeoutMetaobjectsInteger;
		}
		else {
			if ( program != null ) {
				timeoutMetaobjectsInteger = program.getProgramValueFromKey(
						MetaHelper.timeoutMillisecondsMetaobjectsStr);
				if ( timeoutMetaobjectsInteger != null ) {
					if ( !(timeoutMetaobjectsInteger instanceof Integer) ) {
						env.error(annotSymbol, "The program key value "
								+ MetaHelper.timeoutMillisecondsMetaobjectsStr
								+ ", set with @options, has a type that is not 'Integer'");
						return MetaHelper.timeoutMillisecondsMetaobjectsDefaultValue;
					}
					timeoutMilliseconds = (Integer) timeoutMetaobjectsInteger;
				}
			}
		}
		return timeoutMilliseconds;
	}

	/*
	 * return the timeout, in milliseconds, for metaobject methods.
	 * 'annotSymbol' is the symbol used for error messages
	 */
	static public int getTimeoutMilliseconds(saci.Compiler compiler,
			Program program, CyanPackage cpackage, Symbol annotSymbol) {
		int timeoutMilliseconds = MetaHelper.timeoutMillisecondsMetaobjectsDefaultValue;
		Object timeoutMetaobjectsInteger = null;
		if ( cpackage != null ) {
			timeoutMetaobjectsInteger = cpackage.getPackageValueFromKey(
					MetaHelper.timeoutMillisecondsMetaobjectsStr);
		}
		if ( timeoutMetaobjectsInteger != null ) {
			if ( !(timeoutMetaobjectsInteger instanceof Integer) ) {

				compiler.error2(annotSymbol, "The package key value "
						+ MetaHelper.timeoutMillisecondsMetaobjectsStr
						+ ", set with @options, has a type that is not 'Integer'");
				return MetaHelper.timeoutMillisecondsMetaobjectsDefaultValue;
			}
			timeoutMilliseconds = (Integer) timeoutMetaobjectsInteger;
		}
		else {
			if ( program != null ) {
				timeoutMetaobjectsInteger = program.getProgramValueFromKey(
						MetaHelper.timeoutMillisecondsMetaobjectsStr);
				if ( timeoutMetaobjectsInteger != null ) {
					if ( !(timeoutMetaobjectsInteger instanceof Integer) ) {
						compiler.error2(annotSymbol, "The program key value "
								+ MetaHelper.timeoutMillisecondsMetaobjectsStr
								+ ", set with @options, has a type that is not 'Integer'");
						return MetaHelper.timeoutMillisecondsMetaobjectsDefaultValue;
					}
					timeoutMilliseconds = (Integer) timeoutMetaobjectsInteger;
				}
			}
		}
		return timeoutMilliseconds;
	}

	private String errorMessage;

}

// test only
class MyRun implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {
		try {
			String s = null;
			if ( 1 < 2 ) {
				s = "";
			}
			if ( 1 < 2 ) {
				System.out.println(s.charAt(0));
			}

			TimeUnit.SECONDS.sleep(2);
			return 123;
		}
		catch (InterruptedException e) {
			throw new IllegalStateException("task	interrupted", e);
		}
	}
}
