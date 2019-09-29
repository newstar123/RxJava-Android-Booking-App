package app.qamode.mvp.validator;

public class ValidateException extends Exception {
	private String errorMessage;

	public ValidateException(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
