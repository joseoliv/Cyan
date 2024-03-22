
package chooseFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class ChooseFoldersCyanInstallation {

	static private String addDuringTests = "";

	static private void main(String[] args) {

		// String oldPathList[] = { "aa;c:\\drop box\\cyan; d:\\list aa\\bb;",
		// "c:\\aa\\bb\\cc;d:\\aa bb\\cc dd ee;e:\\ff\\gg\\hh"
		// };
		// String cyanHomePathList[] = { "c:\\dropbox\\cyan\\lib", "lib",
		// "c:\\drop box\\lib"};
		// for ( String oldPath : oldPathList ) {
		// for ( String cyanHomePath : cyanHomePathList ) {
		// System.out.println("Path: " + oldPath);
		// System.out.println("cyan Home: " + cyanHomePath);
		// System.out.println("new Path: " + addCyanHomeToPathEV(oldPath,
		// cyanHomePath));
		// }
		// }
		// askIfSet(true, false);
		addDuringTests = "2";
		setEnvironmentVariables(true, true);
	}

	/**
	 * return true if the variables were effectively set
	 *
	 * @param getJavaHome
	 * @param getCyanHome
	 * @return
	 */
	static public boolean askIfSet(boolean getJavaHome, boolean getCyanHome) {

		String msg = "";
		if ( getJavaHome ) {
			msg = "Probably variable JAVA_HOME_FOR_CYAN was not set correctly. Do you want to choose this path now?";
		}
		else if ( getCyanHome ) {
			msg = "Probably variable CYAN_HOME was not set correctly. Do you want to choose this path now?";
		}
		int reply = JOptionPane.showConfirmDialog(null, msg, "Choose the path?",
				JOptionPane.YES_NO_OPTION);
		if ( reply == JOptionPane.YES_OPTION ) {
			setEnvironmentVariables(getJavaHome, getCyanHome);
		}
		return false;
	}

	static public void setEnvironmentVariables(boolean getJavaHome,
			boolean getCyanHome) {

		// String cyan_home_ev = System.getenv("CYAN_HOME2");
		// String java_home_for_cyan_ev = System.getenv("JAVA_HOME_FOR_CYAN2");
		// if ( cyan_home_ev != null && java_home_for_cyan_ev != null ) {
		// System.out.println("Variables have already been set");
		// return true;
		// }

		UIManager.put("FileChooser.fontSize", 20);
		UIManager.put("OptionPane.fontSize", 20);
		boolean isWindows = System.getProperty("os.name").toLowerCase()
				.startsWith("windows");

		if ( !isWindows ) {
			int reply = JOptionPane.showConfirmDialog(null,
					"The Cyan compiler only works in the Windows Operating System.\r\nAre you sure you want to install it in another OS?",
					"Install anyway?", JOptionPane.YES_NO_OPTION);
			if ( reply != JOptionPane.YES_OPTION ) {
				System.exit(0);
			}
		}

		String javaSDKPath = null;
		if ( getJavaHome ) {
			javaSDKPath = getJAVA_HOME();
		}
		String cyanHomePath = null;
		if ( getCyanHome ) {
			cyanHomePath = getCYAN_HOME();
		}

		setEnvironmentVariables(isWindows, cyanHomePath, javaSDKPath);
		System.out.println("Close this command line section and open a "
				+ "new one with 'cmd' in Windows. The new section will "
				+ "recognize the new environment variables. This section "
				+ "will continue to use the old set of variables, "
				+ "CYAN_HOME and JAVA_HOME_FOR_CYAN are not set here");
		System.exit(1);

		// System.out.println("You selected the directory: " + cyanHomePath);
		// System.out.println("Choose end");
	}

	/**
	 * @return
	 */
	static private String getCYAN_HOME() {
		String cyanHomePath = System.getProperty("user.dir");

		boolean askUserWhichDirForCyanHome = true;
		while (askUserWhichDirForCyanHome) {

			JFileChooser jfc = new JFileChooser(cyanHomePath);
			jfc.setDialogTitle(
					"Choose the folder created by the execution of 'install-cyan.exe'. "
							+ "This is the folder that has the name 'lib' and contains 'saci.exe'");
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnValue = jfc.showOpenDialog(null);
			if ( returnValue == JFileChooser.APPROVE_OPTION ) {
				if ( jfc.getSelectedFile().isDirectory() ) {
					try {
						cyanHomePath = jfc.getSelectedFile().getCanonicalPath();
					}
					catch (IOException e) {
						cyanHomePath = jfc.getSelectedFile().getAbsolutePath();
					}
				}
				askUserWhichDirForCyanHome = !checkCyanHomeDir(cyanHomePath);
			}
			else {
				areYouSureToQuitInstallation();
			}
		}

		return cyanHomePath;
	}

	/**
	 * @return
	 */
	static private void areYouSureToQuitInstallation() {
		int answer = JOptionPane.showConfirmDialog(null,
				"Are you sure you want to quit the\ninstalation of the Cyan compiler?",
				"Quit installation?", JOptionPane.YES_NO_OPTION);
		if ( answer == JOptionPane.YES_OPTION
				|| answer == JOptionPane.CANCEL_OPTION
				|| answer == JOptionPane.CLOSED_OPTION ) {
			System.exit(0);
		}

	}

	static private int wantChangePathEnvironmentVariable() {
		return JOptionPane.showConfirmDialog(null,
				"Do you want to change the Path environment variable?\n"
						+ "The folder of the Cyan compiler will be added to it.\n"
						+ "The contents of the old Path will be put in the\nPathBeforeCyan variable.",
				"Change Path?", JOptionPane.YES_NO_OPTION);
	}

	/**
	 * check if directory cyanHomePath is a valid 'lib' directory for Cyan
	 *
	 * @param cyanHomePath
	 * @return true if the directory is valid
	 */
	static private boolean checkCyanHomeDir(String cyanHomePath) {
		File cyanHomeDir = new File(cyanHomePath);
		String expectedFiles[] = new String[] { "saci.exe", "sacilib.jar",
				"cyan.lang.jar", "cyan.io.jar", "cyan.math.jar",
				"cyan.reflect.jar", "cyan.util.jar", "cyanruntime.jar" };
		List<String> foundFileList = new ArrayList<>();
		for (File f : cyanHomeDir.listFiles()) {
			foundFileList.add(f.getName());
		}
		for (String fn : expectedFiles) {
			boolean found = false;
			for (String ff : foundFileList) {
				if ( fn.equals(ff) ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				JOptionPane.showMessageDialog(null,
						"The chosen directory was invalid because some\nexpected files such as saci.exe, sacilib.zip, and\ncyan.lang.jar are missing.\nIn particular, '"
								+ fn + "' is missing");
				return false;
			}
		}
		return true;
	}

	// static private int isThisTheCorrect_cyan_home(String cyan_home_path) {
	// return JOptionPane.showConfirmDialog(null,
	// "Is '" + cyan_home_path + "' the path created by 'install-saci.exe'?",
	// "Correct path", JOptionPane.YES_NO_OPTION);
	// }

	@SuppressWarnings({ "resource", "static-method" })
	static private boolean setEnvironmentVariables(boolean isWindows,
			String cyanHomePath, String javaSDKPath) {

		if ( cyanHomePath == null && javaSDKPath == null ) {
			return true;
		}
		String s = "";
		if ( cyanHomePath != null ) {
			s = "setx CYAN_HOME" + addDuringTests + " \"" + cyanHomePath
					+ "\"\r\n";
			int answer = wantChangePathEnvironmentVariable();
			if ( answer == JOptionPane.YES_OPTION ) {
				s = addCyanHomeToPathEV(System.getenv("PATH"), cyanHomePath);
			}
		}
		if ( javaSDKPath != null ) {
			s = s + "setx JAVA_HOME_FOR_CYAN" + addDuringTests + " \""
					+ javaSDKPath + "\"\r\n";
		}
		new MyFile("setCyan.bat").writeFile(s.toCharArray());
		try {
			if ( isWindows ) {
				Runtime.getRuntime().exec("cmd /c start setCyan.bat");
			}
			else {
				Runtime.getRuntime().exec("sh -c start setCyan.bat");
			}
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(null,
					"Could not set variable CYAN_HOME or \nJAVA_HOME_FOR_CYAN. \nTry to set it by hand");
			return false;
		}
		return true;
	}

	@SuppressWarnings("static-method")
	static private String getJAVA_HOME() {

		String javaJDKPath = System.getProperty("java.home");
		String sepJRE = File.separator + "jre";
		if ( javaJDKPath == null || javaJDKPath.length() == 0 ) {
			javaJDKPath = System.getProperty("user.dir");
			if ( javaJDKPath == null || javaJDKPath.length() == 0 ) {
				javaJDKPath = "";
			}
		}

		if ( javaJDKPath.endsWith(sepJRE) ) {
			javaJDKPath = javaJDKPath.substring(0,
					javaJDKPath.length() - sepJRE.length());
		}
		int lastS = javaJDKPath.lastIndexOf(File.separator);
		if ( lastS > 0 ) {
			String s = javaJDKPath.substring(0, lastS);
			File f = new File(s);
			for (String g : f.list()) {
				if ( g.startsWith("jdk1.8") ) {
					try {
						javaJDKPath = f.getCanonicalPath() + File.separator + g;
					}
					catch (IOException e) {
						javaJDKPath = f.getAbsolutePath() + File.separator + g;
					}
					break;
				}
			}
		}
		boolean askUserWhichDirForJavaJSK = true;

		while (askUserWhichDirForJavaJSK) {

			JFileChooser jfc = new JFileChooser(javaJDKPath);
			// setFileChooserFont(jfc.getComponents());
			jfc.setDialogTitle(
					"Choose the folder in which JDK 1.8 is installed");
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnValue = jfc.showOpenDialog(null);
			if ( returnValue == JFileChooser.APPROVE_OPTION ) {
				if ( jfc.getSelectedFile().isDirectory() ) {
					try {
						javaJDKPath = jfc.getSelectedFile().getCanonicalPath();
					}
					catch (IOException e) {
						javaJDKPath = jfc.getSelectedFile().getAbsolutePath();
					}
				}
				if ( javaJDKPath != null ) {
					int lastSlash = javaJDKPath.lastIndexOf(File.separator);
					if ( lastSlash > 0 ) {
						String s = javaJDKPath.substring(lastSlash + 1);
						if ( s.startsWith("jdk1.8") ) {
							askUserWhichDirForJavaJSK = false;
						}
						else if ( s.startsWith("jdk-") ) {
							int reply = JOptionPane.showConfirmDialog(null,
									"This folder is not from JDK version 8.\nAre you sure you want to choose it?\n"
											+ "Some Cyan features related to compile-time metaprogramming\nwill not work",
									"Quit installation?",
									JOptionPane.YES_NO_OPTION);
							if ( reply == JOptionPane.YES_OPTION ) {
								askUserWhichDirForJavaJSK = false;
							}
						}
						else {
							JOptionPane.showMessageDialog(null,
									"The chosen directory was invalid.\nChoose something as \n'C:\\ChooseFoldersCyanInstallation Files\\Java\\jdk1.8.0_291'");
						}
					}
				}
			}
			else if ( returnValue == JFileChooser.CANCEL_OPTION ) {
				areYouSureToQuitInstallation();
			}
		}

		return javaJDKPath;
	}

	static private String addCyanHomeToPathEV(String oldPath,
			String cyanHomePath) {

		// String oldPath = System.getenv("PATH");
		String newEnvPath;
		if ( oldPath == null || oldPath.length() == 0 ) {
			newEnvPath = "";
		}
		else {
			String pathList[] = oldPath.split(";");
			boolean foundCyanHome = false;
			for (String aPath : pathList) {
				if ( aPath.equals(cyanHomePath) ) {
					foundCyanHome = true;
					break;
				}
			}
			if ( foundCyanHome ) {
				newEnvPath = oldPath;
			}
			else {
				if ( oldPath.charAt(oldPath.length() - 1) == ';' ) {
					newEnvPath = oldPath + cyanHomePath;
				}
				else {
					newEnvPath = oldPath + ";" + cyanHomePath;
				}
			}

		}
		return "setx PathBeforeCyan \"" + oldPath + "\"\r\n" + "setx Path"
				+ addDuringTests + " \"" + newEnvPath + "\"\r\n";
	}
}

/**
 * from https://www.baeldung.com/run-shell-command-in-java
 *
 * @author jose
 */
class StreamGobbler implements Runnable {
	private InputStream			inputStream;
	private Consumer<String>	consumer;

	public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
		this.inputStream = inputStream;
		this.consumer = consumer;
	}

	@Override
	public void run() {
		new BufferedReader(new InputStreamReader(inputStream)).lines()
				.forEach(consumer);
	}
}