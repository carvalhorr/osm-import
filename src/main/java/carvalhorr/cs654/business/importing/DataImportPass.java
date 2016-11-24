package carvalhorr.cs654.business.importing;

import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectsReadFromFileCallback;
import carvalhorr.cs654.model.OsmUser;

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

	protected void extractUser() throws ErrorInsertingDataToDatabase {
		Integer uidStr = objectBeingImported.getUser().getUid();
		if (uidStr == null) {
			System.out.println(lineCount);
		} else {
			Integer uid = uidStr;
			String userName = objectBeingImported.getUser().getUserName();
			OsmUser user = new OsmUser(uid, userName);
			objectReadCallback.userObjectReadFromFile(user);
		}
	}

	protected boolean lineContainsCloseTag(String line) {
		return line.endsWith("/>");
	}

}
