package carvalhorr.cs654.exception;

public class ErrorProcessingXml extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6176144172373424997L;

	public ErrorProcessingXml() {
		super();
	}

	public ErrorProcessingXml(String message) {
		super(message);
	}

	public ErrorProcessingXml(Throwable t) {
		super(t);
	}

	public ErrorProcessingXml(String message, Throwable t) {
		super(message, t);
	}

}
