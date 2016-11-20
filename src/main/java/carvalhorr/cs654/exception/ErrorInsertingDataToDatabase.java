package carvalhorr.cs654.exception;

public class ErrorInsertingDataToDatabase extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3621013095262468298L;

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
