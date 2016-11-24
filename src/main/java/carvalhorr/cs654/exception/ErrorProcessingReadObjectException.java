package carvalhorr.cs654.exception;

public class ErrorProcessingReadObjectException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6385899423316331796L;

	public ErrorProcessingReadObjectException() {
		super();
	}

	public ErrorProcessingReadObjectException(String message) {
		super(message);
	}

	public ErrorProcessingReadObjectException(Throwable t) {
		super(t);
	}

	public ErrorProcessingReadObjectException(String message, Throwable t) {
		super(message, t);
	}
}
