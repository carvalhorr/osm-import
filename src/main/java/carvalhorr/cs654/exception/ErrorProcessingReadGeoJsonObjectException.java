package carvalhorr.cs654.exception;

public class ErrorProcessingReadGeoJsonObjectException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6385899423316331796L;

	public ErrorProcessingReadGeoJsonObjectException() {
		super();
	}

	public ErrorProcessingReadGeoJsonObjectException(String message) {
		super(message);
	}

	public ErrorProcessingReadGeoJsonObjectException(Throwable t) {
		super(t);
	}

	public ErrorProcessingReadGeoJsonObjectException(String message, Throwable t) {
		super(message, t);
	}
}
