package meta;

import java.io.PrintWriter;
import error.UnitError;

public class WrUnitError implements Comparable<WrUnitError> {

	public WrUnitError(UnitError hidden) {
		this.hidden = hidden;
	}

	UnitError hidden;


	@Override
	public int compareTo(WrUnitError other) {
		return hidden.compareTo(other.hidden);
	}

	public void print( PrintWriter printWriter ) {
		hidden.print(printWriter);
	}

	public String asString() {
		return hidden.asString();
	}

	public String getobjectInterfaceName() {
		return hidden.getobjectInterfaceName();
	}

	public String getLine() {
		return hidden.getLine();
	}

	public String getMessage() {
		return hidden.getMessage();
	}

	public int getLineNumber() {
		return hidden.getLineNumber();
	}
	public int getColumnNumber() {
		return hidden.getColumnNumber();
	}

	public WrSymbol getSymbol() {
		return hidden.getSymbol().getI();
	}

}
