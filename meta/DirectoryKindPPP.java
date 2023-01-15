package meta;

import saci.NameServer;

/**
 * kinds of directories associated to programs, packages, and prototypes (hence PPP)
   @author jose
 */
public enum DirectoryKindPPP {

	/*
	 * data directory that keeps information that is read-only by metaobjects and by the compiler
	 * However, the files can be changed at editing time by Codegs
	 */
	DATA(NameServer.directoryNamePackageData, true, DirectoryWhere.PACKAGE),
	/*
	 * test directory in which metaobjects and the compiler can add prototypes for testing.
	 * It can be written by metaobjects and by the compiler
	 */
	TEST(NameServer.directoryNamePackageTests, false, DirectoryWhere.PACKAGE),
	/*
	 * Domain Specific Language (DSL) directory containing source code of DSLs that
	 * can be compiled by metaobjects. It is read-only by metaobjects and by the compiler.
	 */
	DSL(NameServer.directoryNamePackageDSL, true, DirectoryWhere.PACKAGE),
	/*
	 * directory in which the files for link past-future can be kept
	 */
	LPF(NameServer.directoryNameLinkPastFuture, false, DirectoryWhere.PACKAGE),
	/*
	 * directory in which temporary files can be written and read by metaobjects.
	 */
	TMP(NameServer.directoryNamePackagePrototypeTmp, false, DirectoryWhere.PROTOTYPE),
	/*
	 * directory in which documentation is kept
	 */
	DOC(NameServer.directoryNameProgramPackagePrototypeDoc, false, DirectoryWhere.ALL);

	@Override public String toString() {
		return dirName;
	}

	DirectoryKindPPP(String dirName, boolean readOnly, DirectoryWhere where) {
		this.dirName = dirName;
		this.readOnly = readOnly;
		this.where = where;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public DirectoryWhere getWhere() {
		return where;
	}


	private String dirName;
	private boolean readOnly;
	private DirectoryWhere where;

}
