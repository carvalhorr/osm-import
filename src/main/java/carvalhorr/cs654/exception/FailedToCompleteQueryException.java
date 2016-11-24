package carvalhorr.cs654.exception;

public class FailedToCompleteQueryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1467978703160365079L;

	public FailedToCompleteQueryException() {
		super();
	}

	public FailedToCompleteQueryException(String message) {
		super(message);
	}

	public FailedToCompleteQueryException(Throwable t) {
		super(t);
	}

	public FailedToCompleteQueryException(String message, Throwable t) {
		super(message, t);
	}
}
