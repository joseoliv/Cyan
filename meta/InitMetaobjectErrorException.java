package meta;

public class InitMetaobjectErrorException extends RuntimeException {

	private String errorMessage;

	public InitMetaobjectErrorException(String errorMessage) {
		this.setErrorMessage(errorMessage);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	private static final long serialVersionUID = 7376130810396756129L;

}
