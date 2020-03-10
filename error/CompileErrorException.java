package error;

public class CompileErrorException extends RuntimeException {

	public CompileErrorException() { }
	public CompileErrorException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String message;

}
