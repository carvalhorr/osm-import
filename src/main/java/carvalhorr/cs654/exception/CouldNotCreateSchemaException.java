package carvalhorr.cs654.exception;

public class CouldNotCreateSchemaException extends Exception {

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
