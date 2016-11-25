package carvalhorr.cs654.exception;

public class ErrorReadingDataFromDatabase2 extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3621013095262468298L;

	public ErrorReadingDataFromDatabase2() {
		super();
	}

	public ErrorReadingDataFromDatabase2(String message) {
		super(message);
	}

	public ErrorReadingDataFromDatabase2(Throwable t) {
		super(t);
	}

	public ErrorReadingDataFromDatabase2(String message, Throwable t) {
		super(message, t);
	}
}
