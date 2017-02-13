package carvalhorr.cs654.business;

public abstract class BaseBusinessLogic {

	protected ProgressIndicator mProgressIndicator;
	
	public BaseBusinessLogic(ProgressIndicator progressIndicator) {
		this.mProgressIndicator = progressIndicator;
	}
	
	protected void sendMessage(String message) {
		if (mProgressIndicator != null) {
			mProgressIndicator.printMessage(message);
		}
	}
}
