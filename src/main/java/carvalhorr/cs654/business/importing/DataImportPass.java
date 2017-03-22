package carvalhorr.cs654.business.importing;

import carvalhorr.cs654.model.OsmObject;

public class DataImportPass {

	protected int lineCount = 0;
	protected OsmObject objectBeingImported;
	protected boolean osmStarted = false;

	protected OsmObjectsReadFromFileCallback objectReadCallback;

	protected String fileName;

	public DataImportPass(String fileName, OsmObjectsReadFromFileCallback objectReadCallback) {
		this.objectReadCallback = objectReadCallback;
		this.fileName = fileName;
	}

	protected boolean lineContainsCloseTag(String line) {
		return line.endsWith("/>");
	}

}
