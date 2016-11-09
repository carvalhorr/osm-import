package carvalhorr.cs654.business.importing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmBounds;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.PropertiesExtractor;
import carvalhorr.cs654.model.WayOsmObject;
import exception.UnexpectedTokenException;

public class DataImportPass {

	protected int lineCount = 0;
	protected OsmObject objectBeingImported;
	protected boolean osmStarted = false;

	protected OsmObjectsReadCallback objectReadCallback;

	protected String fileName;

	public DataImportPass(String fileName, OsmObjectsReadCallback objectReadCallback) {
		this.objectReadCallback = objectReadCallback;
		this.fileName = fileName;
	}

	protected void extractUser() throws ErrorInsertingDataToDatabase {
		String uidStr = objectBeingImported.getUid();
		if (uidStr == null) {
			System.out.println(lineCount);
		} else {
			Integer uid = Integer.parseInt(uidStr);
			String userName = objectBeingImported.getUserName();
			OsmUser user = new OsmUser(uid, userName);
			objectReadCallback.userObjectReadFromFile(user);
		}
	}

	protected boolean lineContainsCloseTag(String line) {
		return line.endsWith("/>");
	}

}
