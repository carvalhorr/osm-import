package carvalhorr.cs654.exception;

public class CouldNotCreateSchemaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2057478688357747140L;

	public CouldNotCreateSchemaException() {
		super();
	}

	public CouldNotCreateSchemaException(String message) {
		super(message);
	}

	public CouldNotCreateSchemaException(Throwable t) {
		super(t);
	}

	public CouldNotCreateSchemaException(String message, Throwable t) {
		super(message, t);
	}
}
