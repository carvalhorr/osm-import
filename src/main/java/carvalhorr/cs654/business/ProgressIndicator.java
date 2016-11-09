package carvalhorr.cs654.business;

public interface ProgressIndicator {
	
	public void updateProgress(String type, int progress);
	public void finished();
	
}
