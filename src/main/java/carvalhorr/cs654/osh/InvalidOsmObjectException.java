package carvalhorr.cs654.osh;

public class InvalidOsmObjectException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidOsmObjectException(String message) {
		super(message);
	}
	
	public InvalidOsmObjectException(Throwable throwable) {
		super(throwable);
	}
	
	public InvalidOsmObjectException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
