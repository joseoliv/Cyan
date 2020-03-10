/**
  
 */
package meta;

/**
 * When a generic prototype is being instantiated, it is necessary 
 * to replace the formal parameters to real ones. For example, in
 *       object Proto<T>
 *           @check(T, "T", #T) 
 *           func myMethod -> T {
 *                var x = [**  T: "one" **]; 
 *           }
 *           @javacode{*
 *               T []array;
 *           *}
 *       end 
 * parameter T will be replaced by "Int" in the instantiation 
 *     Proto<Int>
 *     
 * The return value of myMethod will surely be replaced by "Int". 
 * But it is unclear how to replace other occurrences of "T" that
 * appear in prototype Proto. There are other occurrences in uses of
 * metaobjects "check" and "javacode". And inside the literal 
 * object 
 *      [** T: "one"  **]
 *      
 * Enumeration ReplacementPolicyInGenericInstantiation is used by 
 * the compiler and the metaobject classes to discover when and how
 * to replace formal parameters such as T by real parameters such as 
 * Int. The metaobject classes define a method getReplacementPolicy 
 * that return the policy to be used. If it returns  NO_REPLACEMENT,
 * no replacement is made. if it returns REPLACE_BY_CYAN_VALUE,
 * the formal parameter will be replaced by the real parameter. For
 * example, suppose method getReplacementPolicy of metaobject "check" 
 * returns  REPLACE_BY_CYAN_VALUE. Then the compiler will create
 * the following prototype Proto<Int>
 * 
 *       object Proto<T>
 *           @check(Int, "Int", #Int) 
 *           func myMethod -> T { ... }
 *           ...
 *       end
 *       
 *  However, if method getReplacementPolicy of metaobject "check"
 *  return REPLACE_BY_JAVA_VALUE the compiler will create
 * 
 *       object Proto<T>
 *           @check(CyInt, "CyInt", #CyInt) 
 *           func myMethod -> T { ... }
 *           ...
 *       end
 *       
 * because "CyInt" is the name of the Java class that represents "Int" of Cyan.


 * This enumeration gives all options 
   @author José
   
 */
public enum ReplacementPolicyInGenericInstantiation {
	NO_REPLACEMENT, 
	REPLACE_BY_CYAN_VALUE, 
	REPLACE_BY_JAVA_VALUE
}
