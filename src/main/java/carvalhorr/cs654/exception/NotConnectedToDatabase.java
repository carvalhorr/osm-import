package carvalhorr.cs654.exception;

public class NotConnectedToDatabase extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2815524255639126828L;

	public NotConnectedToDatabase() {
		super();
	}

	public NotConnectedToDatabase(String message) {
		super(message);
	}

	public NotConnectedToDatabase(Throwable t) {
		super(t);
	}

	public NotConnectedToDatabase(String message, Throwable t) {
		super(message, t);
	}
}
