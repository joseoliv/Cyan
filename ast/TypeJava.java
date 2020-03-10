/**
 *
 */
package ast;

import saci.NameServer;

/**
 * This class represents a Java class which may be:
 * 1. a class, represented by objects of TypeJavaClass
 * 2. an interface, represented by objects of TypeJavaInterface
 * 3. a basic type such as int, float, etc. These are
 *    represented by objects of class TypeJavaBasic
 *
 * @author José
 *
 */
abstract public class TypeJava extends Type {
	@Override
	abstract public String getName();
	abstract public String getJavaPackage();
	/**
	 * the unique name of the type which is equal to the
	 * Java type
	 * @return
	 */
	@Override
	public String getJavaName() {
		String fn = this.getFullName();
		if ( fn.endsWith("[]") ) {
			String less = fn.substring(0, fn.length()-2);
			int index = less.lastIndexOf('.');
			if ( index > 0 ) {
				less = less.substring(index + 1);
			}
			if ( NameServer.isJavaBasicType(less) ) {
				return this.getName();
			}
		}
		String theName = this.getName();
		if ( NameServer.isJavaBasicType(theName) && !theName.equals("String") ) {
			return theName;
		}

		return fn;
//		if ( getName().indexOf('.') > 0 ) {
//			return this.getFullName();
//		}
//		else {
//			return this.getName();
//		}
	}
}
