package carvalhorr.cs654.exception;

public class PostgresqlDriverNotFound extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8616487280135933815L;

	public PostgresqlDriverNotFound() {
		super();
	}

	public PostgresqlDriverNotFound(String message) {
		super(message);
	}

	public PostgresqlDriverNotFound(Throwable t) {
		super(t);
	}

	public PostgresqlDriverNotFound(String message, Throwable t) {
		super(message, t);
	}
}
