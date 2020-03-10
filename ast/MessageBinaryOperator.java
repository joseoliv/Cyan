/**
 *
 */
package ast;

import java.util.ArrayList;
import java.util.List;
import lexer.Symbol;
import meta.MetaHelper;
import meta.WrMessageBinaryOperator;
import meta.WrMessageWithKeywords;
import saci.CyanEnv;

/** Represents a message with a binary operator such as '+' in prototype Int
 * @author jose
 *
 */
public class MessageBinaryOperator extends MessageWithKeywords {


	public MessageBinaryOperator(Symbol binaryOperator, Expr expr) {
		List<MessageKeywordWithRealParameters> keywordParameterList = new ArrayList<MessageKeywordWithRealParameters>();
		List<Expr> exprList = new ArrayList<Expr>();
		exprList.add(expr);
		keywordParameterList.add(new MessageKeywordWithRealParameters(binaryOperator, false, exprList));
		this.setkeywordParameterList(keywordParameterList);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		super.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public WrMessageWithKeywords getI() {
		if ( iMessageBinaryOperator == null ) {
			iMessageBinaryOperator = new WrMessageBinaryOperator(this);
		}
		return iMessageBinaryOperator;
	}

	private WrMessageWithKeywords iMessageBinaryOperator = null;


	@Override
	public boolean isDynamicMessageSend() { return false; }

	@Override
	public void genCyan(PWInterface pw, boolean printInMoreThanOneLine, CyanEnv cyanEnv, boolean genFunctions) {
		pw.print( this.getkeywordParameterList().get(0).getkeyword().getSymbolString() + " ");
		this.getkeywordParameterList().get(0).getExprList().get(0).genCyan(pw, printInMoreThanOneLine, cyanEnv, genFunctions);
	}

	@Override
	public String getJavaMethodName() {
		return MetaHelper.getJavaNameOfkeyword(this.getkeywordParameterList().get(0).getkeywordNameWithoutSpecialChars());
	}


}
