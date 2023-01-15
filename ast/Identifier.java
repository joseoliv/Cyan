/**
 * 
 */
package ast;

/** represents an identifier: field, local variable, prototype name (even generic ones),
 * interfaces. Examples:
 *     size     Stack<Int>     Person     i
 * 
 * @author José
 *
 */
public interface Identifier {
	   // if the identifier is a generic type as Stack<Int>, its name is "Stack<Int>"
	   // if it is size, its name is "size"
	public String getName();
}
