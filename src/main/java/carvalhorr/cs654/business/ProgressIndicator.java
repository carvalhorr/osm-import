package carvalhorr.cs654.business;

public interface ProgressIndicator {
	public void updateProgress(String type, float progress);

	public void printMessage(String message);

	public void finished();
}
