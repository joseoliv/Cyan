package ast;

import java.util.ArrayList;
import java.util.List;

/**
 * this class keeps information on message sends to metaobject annotations. A message send to
 * a metaobject annotation has the syntax<br>
 * <code>
 * var g = @graph{@literal .#}writeCode{* 1:2, 2:1 *};   <br>
 * var x = @text(trim){@literal .#}sendTo("myFile"){* 1:2, 2:1 *};
 * </code> <br>
 * A generic prototype instantiation  and a qualified expression are also considered metaobject annotations:<br>
 * <code>
 * var Set{@literal <}Int>{@literal .#}writeCode s = Set{@literal <}Int> new;
 * </code> <br>
 *
 *
   @author José
 */
public class MessageSendToAnnotation {

	public MessageSendToAnnotation(String message) {
		this.message = message;
		paramList = null;
	}


	public String getMessage() {
		return message;
	}

	public void addExpr(String expr) {
		if ( paramList == null )
			paramList = new ArrayList<>();
		paramList.add(expr);
	}

	public List<String> getParamList() {
		return paramList;
	}

	public void genCyan(PWInterface pw) {
		pw.print(".# " + message);
		if ( paramList != null ) {
			pw.print("(");
			int size = paramList.size();
			for ( String s : paramList ) {
				pw.print(s);
				if ( --size > 0 )
					pw.print(", ");
			}
			pw.print(")");
		}
	}

	/*
	public boolean action(ChooseFoldersCyanInstallation p, ExprGenericPrototypeInstantiation e) {
		switch (message) {
		case "writeCode" :
			p.addCompilationUnitToWrite( e );
			return true;
		default:
			return false;
		}

	}
	*/

	public boolean action(Program p, IReceiverCompileTimeMessageSend e) {
		switch (message) {
		case "writeCode" :
			p.addCompilationUnitToWrite( e );
			return true;
		default:
			return false;
		}

	}




	private String message;
	private List<String> paramList;
}
