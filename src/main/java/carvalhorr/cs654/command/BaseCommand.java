package carvalhorr.cs654.command;

public abstract class BaseCommand {
	protected void printMessage(String message) {
		System.out.println(message);
	}
	protected void printFatalError(String errorMessage) {
		System.out.println("FATAL ERROR: " + errorMessage);
	}
}
