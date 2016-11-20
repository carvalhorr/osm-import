package carvalhorr.cs654.exception;

public class ErrorConnectingToDatabase extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1467978703160365079L;

	public ErrorConnectingToDatabase() {
		super();
	}

	public ErrorConnectingToDatabase(String message) {
		super(message);
	}

	public ErrorConnectingToDatabase(Throwable t) {
		super(t);
	}

	public ErrorConnectingToDatabase(String message, Throwable t) {
		super(message, t);
	}
}
