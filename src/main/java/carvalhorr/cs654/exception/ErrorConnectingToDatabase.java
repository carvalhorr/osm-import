package carvalhorr.cs654.exception;

public class ErrorConnectingToDatabase extends Exception {

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
