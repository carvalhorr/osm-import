package carvalhorr.cs654.command;

import carvalhorr.cs654.business.ProgressIndicator;

public abstract class BaseCommand implements ProgressIndicator {
	
	public void printMessage(String message) {
		System.out.println(message);
	}
	public void printFatalError(String errorMessage) {
		System.out.println("FATAL ERROR: " + errorMessage);
	}
	
	public abstract void printHeader();
}
