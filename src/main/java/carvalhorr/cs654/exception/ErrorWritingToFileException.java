package carvalhorr.cs654.exception;

public class ErrorWritingToFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6385899423316331796L;

	public ErrorWritingToFileException() {
		super();
	}

	public ErrorWritingToFileException(String message) {
		super(message);
	}

	public ErrorWritingToFileException(Throwable t) {
		super(t);
	}

	public ErrorWritingToFileException(String message, Throwable t) {
		super(message, t);
	}
}
