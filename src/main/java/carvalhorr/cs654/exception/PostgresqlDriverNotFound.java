package carvalhorr.cs654.exception;

public class PostgresqlDriverNotFound extends Exception {

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
