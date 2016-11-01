package carvalhorr.cs654.exception;

public class ErrorInsertingDataToDatabase extends Exception {

	public ErrorInsertingDataToDatabase() {
		super();
	}

	public ErrorInsertingDataToDatabase(String message) {
		super(message);
	}

	public ErrorInsertingDataToDatabase(Throwable t) {
		super(t);
	}

	public ErrorInsertingDataToDatabase(String message, Throwable t) {
		super(message, t);
	}
}
