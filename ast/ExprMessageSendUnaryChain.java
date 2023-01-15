/**
 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import error.ErrorKind;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.Token;
import saci.Env;
import saci.NameServer;

/**
 * @author José
 *
 */
abstract public class ExprMessageSendUnaryChain extends ExprMessageSend {

	public ExprMessageSendUnaryChain(Symbol nextSymbol, MethodDec method) {
		super(nextSymbol, method);
		hasBackquote = false;
	}

	public ExprMessageSendUnaryChain(MethodDec method) {
		super(method);
		hasBackquote = false;
	}




	/**
	   @param env
	   @param tokenFirstkeyword
	 */
	public void calcInternaltTypesWithBackquote(Env env,  Symbol ... arrayUnarySymbols ) {
		type = Type.Dyn;

		if ( arrayUnarySymbols[0].token != Token.IDENT ) {
			env.error(getFirstSymbol(), "The hasBackquote ` should not be followed by '?' or '?.'", true, true);
		}

		quotedVariableList = new ArrayList<>();
		// something like   f1 `first `second  in which first and second should be variables
		// of type String
		for ( Symbol sym : arrayUnarySymbols ) {
			String name = sym.getSymbolString();
			VariableDecInterface varDec = env.searchVariable(name);
			if ( varDec == null ) {
				if ( env.getEnclosingObjectDec() == null ) {
					/*
					 * inside a regular prototype that is NOT inside another prototype
					 */
					varDec = env.searchVariable(name);
				}
				else {
					/*
					 * inside an inner prototype
					 */
					MethodDec currentMethod = env.getCurrentMethod();
					if ( currentMethod != null &&
							NameServer.isMethodNameEval(currentMethod.getNameWithoutParamNumber()) ) {
						/*
						 * inside an 'eval' or 'eval:eval: ...' method of an inner prototype
						 */

						varDec = env.searchVariableInEvalOfInnerPrototypes(name);
					}
					else {
						/*
						 * inside a method of an inner prototype that is not 'eval', 'eval:eval: ...'
						 */


						varDec = env.searchVariableIn_NOT_EvalOfInnerPrototypes(name);
					}
				}
			}
			if ( varDec == null ) {
				varDec = env.searchField(name);
				env.error(true, sym,
						"Variable " + sym.getSymbolString() + " was not declared",
						sym.getSymbolString(), ErrorKind.variable_was_not_declared);
			}
			else {
				if ( ! Type.String.isSupertypeOf(varDec.getType(), env)  && varDec.getType() != Type.Dyn )
					env.error(true, sym,
							"Variable " + sym.getSymbolString() + " should be of type String or Dyn",
							sym.getSymbolString(), ErrorKind.backquote_not_followed_by_a_string_variable);

			}
			quotedVariableList.add(varDec);
		}
		return;
	}



	public boolean getHasBackQuote() {
		return hasBackquote;
	}

	public void setHasBackQuote(boolean hasBackQuote) {
		this.hasBackquote = hasBackQuote;
	}

	/**
	 * true if this unary message send is preceded by hasBackquote, `. This means
	 * the method to be called is contained in the variable unarySymbol[0].
	 * There should be just one element in unarySymbol.
	 */
	protected boolean hasBackquote;


	/**
	 * if hasBackquote is true, this is the list of variables in the message send. That is,
	 * if the message is
	 *           f1 `first `second;
	 * then quotedVariableList contains references to variables first and second.
	 */
	protected List<VariableDecInterface> quotedVariableList;

	/**
	 * add an unary message keyword at the end of the chain. That is,
	 * if this object represents
	 *        super getClub
	 * and we want to add unary message "size" (because the code is
	 * "super getClub size"), then we call
	 *       addUnarySymbol(symbol for "size");
	 *
	 * @param unarySymbol1
	 */
	public void setUnarySymbol(SymbolIdent unarySymbol1) {
		this.unarySymbol = unarySymbol1;
	}

	public SymbolIdent getUnarySymbol() {
		return unarySymbol;
	}

	public String getMessageName() {
		return this.unarySymbol.getSymbolString();
	}

	public Symbol getBackquoteSymbol() {
		return backquoteSymbol;
	}

	public void setBackquoteSymbol(Symbol backquote) {
		this.backquoteSymbol = backquote;
	}
	/**
	 * the hasBackquote symbol, if present, like in
	 *     self `s;
	 */
	Symbol backquoteSymbol = null;

	/**
	 * the method signature of the method found in a search for an adequate method for this message.
	 */

	public MethodSignature getMethodSignatureForMessageSend() {
		return methodSignatureForMessageSend;
	}

	/**
	 * the method signature of the method found in a search for an adequate method for this message.
	 */

	protected MethodSignature methodSignatureForMessageSend;

	/**

	 * the unary message symbols (each one will result in an unary method
	 * at runtime).
	 */
	protected SymbolIdent unarySymbol;


}
