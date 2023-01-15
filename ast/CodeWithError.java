/**
  
 */
package ast;

import lexer.Symbol;

/**
 *  this interface is inherited by classes that represent pieces of code that may contain certain errors
 *  such as "Identifier not found". It is necessary because method signalCompilerError needs the
 *  text of the statement or declaration that has the error. This text is got from the first and
 *  last symbols of the code.  
   @author José
   
 */
public interface CodeWithError {
	Symbol getFirstSymbol();
	Symbol getLastSymbol();
}
