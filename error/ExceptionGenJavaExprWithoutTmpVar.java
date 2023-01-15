/**
  
 */
package error;

/** This exception is thrown when a method genJavaExprWithoutTmpVar is called
 * in an illegal context. That is, method genJavaExpr should have been called
 * instead of  genJavaExprWithoutTmpVar.
   @author José
   
 */
public class ExceptionGenJavaExprWithoutTmpVar extends RuntimeException {

	private static final long	serialVersionUID	= 1L;

}
