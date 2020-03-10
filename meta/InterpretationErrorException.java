package meta;

public class InterpretationErrorException extends RuntimeException {

	public InterpretationErrorException(String message, WrSymbol symbol) {
		this.message = message;
		this.symbol = symbol;
	}
	private static final long serialVersionUID = 3224603273416068476L;

	@Override
	public String getMessage() { return message; }
	public WrSymbol getSymbol() { return symbol; }

	private String message;
	private WrSymbol symbol;
}
