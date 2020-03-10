package error;

public class ErrorInMetaobjectException extends RuntimeException {

	public ErrorInMetaobjectException(String message) {
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
