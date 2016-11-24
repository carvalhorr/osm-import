package carvalhorr.cs654.exception;

public class ErrorReadingDataFromDatabase extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3621013095262468298L;

	public ErrorReadingDataFromDatabase() {
		super();
	}

	public ErrorReadingDataFromDatabase(String message) {
		super(message);
	}

	public ErrorReadingDataFromDatabase(Throwable t) {
		super(t);
	}

	public ErrorReadingDataFromDatabase(String message, Throwable t) {
		super(message, t);
	}
}
