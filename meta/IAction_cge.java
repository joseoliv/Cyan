
package meta;

/**
 * This interface should be implemented by all metaobjects that should add Java code to a program unit 
   @author jose
 */
public interface IAction_cge {

	/**
	 * If the Cyan prototype in which this metaobject annotation is P,
	 * the compiler will generate a Java class _P for P. 
	 * The compiler will add to the body of  _P the string
	 * returned by this method, if it returns a value that
	 * is not null.
	 */
	default StringBuffer cge_javaCodeClassBody() {
		return null; 
	}

	/**
	 * If the Cyan prototype in which this metaobject annotation is P,
	 * the compiler will generate a Java class _P for P. 
	 * The compiler will add to the source code in which _P is, 
	 * in the static section, the code returned by this method, if it 
	 * returns a value that is not null. The static section is something like<br>
	 * <code>
	 * static { ... } <br>
	 * </code> 
	 */

	default StringBuffer cge_javaCodeStaticSection() {
		return null;
	}
	
	
	/**
	 * If the Cyan prototype in which this metaobject annotation is P,
	 * the compiler will generate a Java class _P for P. 
	 * The compiler will add to the source code in which _P is, 
	 * before _P, the code returned by this method, if it 
	 * returns a value that is not null. 
	 */

	default StringBuffer cge_javaCodeBeforeClass() {
		return null;
	}

	/**
	 * the compiler will replace the metaobject annotation by the string
	 * returned by this method, if it returns a non-null value.
	 * It is expected that at most one of javaCode() or cyanCode()
	 * return a value that is not null.
	 */
	default StringBuffer cge_codeToAdd() {
		return null;
	}

}