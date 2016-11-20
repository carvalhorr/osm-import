package carvalhorr.cs654.exception;

public class SchemaDoesNotExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -812276833536624893L;

	public SchemaDoesNotExistException() {
		super();
	}

	public SchemaDoesNotExistException(String message) {
		super(message);
	}

	public SchemaDoesNotExistException(Throwable t) {
		super(t);
	}

	public SchemaDoesNotExistException(String message, Throwable t) {
		super(message, t);
	}
}
