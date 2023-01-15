package meta;

/**
 * where each directory used by the compiler should be. For example,
 * the test directory, "--test" should be in the directory of the project.
 * The "--DSL" directory should be in directory of a package.
   @author jose
 */
public enum DirectoryWhere {
	PROJECT, PACKAGE, PROTOTYPE, ALL

}
