package models;

/**
 * Exception that prevents processing of the given operation. There is no
 * recovery to these failures.
 */
public class InternalServerErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	public InternalServerErrorException() {
		super();
	}

	public InternalServerErrorException(String message) {
		super(message);
	}

	public InternalServerErrorException(Throwable cause) {
		super(cause);
	}

	public InternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}

}
