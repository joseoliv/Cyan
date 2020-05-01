package meta.cyanLang;

import java.util.Stack;
import meta.AnnotationArgumentsKind;
import meta.CyanMetaobjectLiteralObject;
import meta.CyanMetaobjectAtAnnot;
import meta.IAction_semAn;
import meta.ICompiler_parsing;
import meta.ICompiler_semAn;
import meta.IParseWithCyanCompiler_parsing;
import meta.Token;
import meta.WrSymbol;
import meta.WrSymbolIntLiteral;

/**
 * an Reverse Polish Notation calculator with integer numbers. Example:<br>
 * <code>
 * let value = {@literal @}rpn{* 4 3 2 * + *};<br>
 * assert value == 10<br>
 * </code>
   @author jose
 */
public class CyanMetaobjectRPN extends CyanMetaobjectAtAnnot implements IParseWithCyanCompiler_parsing, IAction_semAn {

	public CyanMetaobjectRPN() {
		super("rpn", AnnotationArgumentsKind.ZeroParameters );
	}

	@Override
	public void parsing_parse(ICompiler_parsing compiler_parsing) {
		compiler_parsing.next();
		parseExpr(compiler_parsing);
		if ( compiler_parsing.getSymbol().token != Token.EOLO ) {
			compiler_parsing.error(compiler_parsing.getSymbol(), "Unexpected symbol: '" + compiler_parsing.getSymbol().getSymbolString() + "'");
		}
	}


	@Override
	public StringBuffer semAn_codeToAdd(ICompiler_semAn compiler) {

		return new StringBuffer(value);
	}

	private void parseExpr(ICompiler_parsing compiler_parsing) {

		WrSymbol sym = compiler_parsing.getSymbol();
		final Stack<Integer> valueStack = new Stack<>();
		while ( compiler_parsing.getSymbol().token != Token.EOLO ) {
			if ( sym.token == Token.INTLITERAL ) {
				valueStack.push( ((WrSymbolIntLiteral ) sym).getIntValue() );
			}
			else if ( compiler_parsing.isOperator(sym.token) ) {
				if ( valueStack.size() < 2 ) {
					this.addError(sym, "An operator with insuffient arguments: '" + sym.getSymbolString() + "'");
					return ;
				}
				final int right = valueStack.pop();
				final int left = valueStack.pop();
				int result = 0;
				switch ( sym.token ) {
				case DIV:
					if ( right == 0 ) {
						this.addError(sym, "Division by zero");
					}
					result = left/right;
					break;
				case MINUS:
					result = left - right;
					break;
				case MULT:
					result = left * right;
					break;
				case PLUS:
					result = left + right;
					break;
				default:
					this.addError(sym, "Operator '" + sym.getSymbolString() + "' is not supported");
					return ;
				}
				valueStack.push(result);
			}
			else {
				this.addError(sym, "An operator or number was expected. Found '" + sym.getSymbolString() + "'");
				return ;
			}
			compiler_parsing.next();
			sym = compiler_parsing.getSymbol();

		}
		if ( valueStack.size() != 1 ) {
			this.addError(compiler_parsing.getSymbol(), "Insufficient number of operators");
			return ;
		}
		value = "" + valueStack.pop();
	}

	@Override
	public boolean shouldTakeText() { return true; }

	@Override
	public boolean isExpression() {
		return true;
	}



	@Override
	public String getPackageOfType() {
		return "cyan.lang";
	}
	/**
	 * If the metaobject annotation has type <code>packageName.prototypeName</code>, this method returns
	 * <code>prototypeName</code>.  See {@link CyanMetaobjectLiteralObject#getPackageOfType()}
	   @return
	 */

	@Override
	public String getPrototypeOfType() { return "Int"; }


	String value;

}
