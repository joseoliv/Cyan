package meta;


/** Methods of this interface are called at compile-time when the compiler
 * DOES NOT finds a method corresponding to a message passing.
 * The annotation should be attached to a method or a prototype.
 * The annotation should be public.
   @author jose
 */

public interface IActionMethodMissing_semAn extends IStayPrototypeInterface {

	/**
	 * analyze the message send with keywords and return a tuple composed of:
	 *    a) the source code that should replace the message send and
	 *    b) the package of the type of the value returned
	 *    c) the prototype name of the type of the value returned
	 *
	 * if any of b) or c) is null or the empty string, Dyn is considered
	 * the type of the expression that replaced the message send; that is a)
	 */
	@SuppressWarnings("unused")
	default Tuple3<StringBuffer, String, String> semAn_missingKeywordMethod(
			    WrExpr receiver, WrMessageWithKeywords message, WrEnv env) {
		return null;
	}


	/**
	 * analyze the unary message send  and return a tuple composed of:
	 *    a) the source code that should replace the message send and
	 *    b) the package of the type of the value returned
	 *    c) the prototype name of the type of the value returned
	 * if any of b) or c) is null or the empty string, Dyn is considered
	 * the type of the expression that replaced the message send; that is a)
	 */
	@SuppressWarnings("unused")
	default Tuple3<StringBuffer, String, String> semAn_missingUnaryMethod(
				WrExpr receiver, WrSymbol unarySymbol, WrEnv env) {
		return null;
	}

}
//  semAn_analyzeReplaceMessageWithkeywords    semAn_missingKeywordMethod
//  semAn_analyzeReplaceUnaryMessage           semAn_missingUnaryMethod
