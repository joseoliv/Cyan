package ast;

public class CompilationUnitDSL extends CompilationUnitSuper {

	public CompilationUnitDSL(String filename, String packageCanonicalPath,
			CyanPackage cyanPackage) {

		super(filename, packageCanonicalPath);

		this.filename = filename;
		this.packageCanonicalPath = packageCanonicalPath;
		this.cyanPackage = cyanPackage;

	}

	/**
	 * the package to which this compilation unit belong to
	 */
	private CyanPackage cyanPackage;

	/**
	 * Canonical path of the source file. This path is something like
	 *      D:\My Dropbox\art\programming languages\Cyan\
	 * the last character is always \ in Windows
	 */
	private String packageCanonicalPath;

	public CyanPackage getCyanPackage() {
		return cyanPackage;
	}

	public void setCyanPackage(CyanPackage cyanPackage) {
		this.cyanPackage = cyanPackage;
	}

	public String getPackageCanonicalPath() {
		return packageCanonicalPath;
	}

	public void setPackageCanonicalPath(String packageCanonicalPath) {
		this.packageCanonicalPath = packageCanonicalPath;
	}
}
