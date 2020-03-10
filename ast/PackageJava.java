/**
 *
 */
package ast;

import java.util.List;

/**
 * This class represents a Java package, a store for classes and interfaces
 * @author José
 *
 */
public class PackageJava {

	private List<TypeJava> typeJavaList;

	public void setTypeJavaList(List<TypeJava> typeJavaList) {
		this.typeJavaList = typeJavaList;
	}

	public List<TypeJava> getTypeJavaList() {
		return typeJavaList;
	}

}
