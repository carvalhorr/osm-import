package exception;

public class UnexpectedTokenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1999940525089178789L;

	public UnexpectedTokenException() {
		super();
	}
	
	public UnexpectedTokenException(String message) {
		super(message);
	}
	
	public UnexpectedTokenException(Throwable cause) {
		super(cause);
	}
	
	public UnexpectedTokenException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
