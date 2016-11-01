package carvalhorr.cs654.business.importing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.RelationOsmObject;
import carvalhorr.cs654.model.WayOsmObject;
import carvalhorr.cs654.persistence.OsmDataPersistence;
import exception.UnexpectedTokenException;

public class DataImportPass2NodesAndUsersImport {

	private int lineCount = 0;
	private OsmObject objectBeingImported;
	private boolean osmStarted = false;
	
	private OsmObjectsReadCallback objectReadCallback;
	
	private String fileName;
	
	public DataImportPass2NodesAndUsersImport(String fileName, OsmObjectsReadCallback objectReadCallback) {
		this.objectReadCallback = objectReadCallback;
		this.fileName = fileName;
	}

	public void importFile() throws IOException, UnexpectedTokenException, ErrorInsertingDataToDatabase {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();
		List<String> types = new ArrayList<String>();

		long startTime = System.currentTimeMillis();
		do {
			lineCount++;

			String lineStartsWith = line.trim().split(" ")[0];

			switch (lineStartsWith) {
			case "<?xml": {
				break;
			}
			case "<osm": {
				openOsm();
				break;
			}
			case "</osm>": {
				closeOsm();
				break;
			}
			case "<bounds": {
				System.out.println(line);
				break;
			}
			case "<node": {
				startNodeObject(line);
				break;
			}
			case "</node>": {
				finishNodeObject(line);
				break;
			}
			case "<tag": {
				addTag(line);
				break;
			}
			case "<way": {
				startWayObject(line);
				break;
			}
			case "</way>": {
				finishWayObject(line);
				break;
			}
			case "<nd": {
				//addNodeToWay(line);
				break;
			}/*
			case "<relation": {
				//startRelationObject(line);
				break;
			}
			case "</relation>": {
				//finishRelationObject(line);
				break;
			}
			case "<member": {
				//addMember(line);
				break;
			}*/
			default:
				throw new UnexpectedTokenException(
						lineCount + ": unexpected token " + lineStartsWith.toUpperCase() + "found");
			}
			if (!types.contains(lineStartsWith)) {
				types.add(lineStartsWith);
			}

			line = reader.readLine();
		} while (line != null);
		long endTime = System.currentTimeMillis();
		System.out.println("read " + lineCount + " lines in " + (endTime - startTime) + " milliseconds");
	}

	private void openOsm() {
		osmStarted = true;
	}

	private void closeOsm() {
		osmStarted = false;
	}

	private void startWayObject(String tag) throws UnexpectedTokenException, ErrorInsertingDataToDatabase {
		if (!osmStarted) {
			throw new UnexpectedTokenException(lineCount + ": opening <way> tag found outside <osm> tag");
		}
		if (objectBeingImported != null) {
			throw new UnexpectedTokenException(lineCount + ": opening <way> tag found inside other object tag");
		}
		objectBeingImported = new WayOsmObject();
		objectBeingImported.processCloseTag(tag);
		if (lineContainsCloseTag(tag)) {
			finishWayObject(tag);
		}
	}

	private void finishWayObject(String tag) throws UnexpectedTokenException, ErrorInsertingDataToDatabase {
		if (objectBeingImported == null || !(objectBeingImported instanceof WayOsmObject)) {
			throw new UnexpectedTokenException(
					lineCount + ": closing <way> tag found without corresponding opening <way> tag");
		}
		//objectBeingImported.processCloseTag(tag);
		extractUser();
		objectBeingImported = null;
	}

	private void startNodeObject(String tag) throws UnexpectedTokenException, ErrorInsertingDataToDatabase {
		if (!osmStarted) {
			throw new UnexpectedTokenException(lineCount + ": opening <node> tag found outside <osm> tag");
		}
		if (objectBeingImported != null) {
			throw new UnexpectedTokenException(lineCount + ": opening <way> tag found inside other object tag");
		}
		objectBeingImported = new NodeOsmObject();
		objectBeingImported.processOpenTag(tag);
		if (lineContainsCloseTag(tag)) {
			finishNodeObject(tag);
		}
	}

	private void finishNodeObject(String tag) throws UnexpectedTokenException, ErrorInsertingDataToDatabase {
		if (objectBeingImported == null || !(objectBeingImported instanceof NodeOsmObject)) {
			throw new UnexpectedTokenException(
					lineCount + ": closing <node> tag found without corresponding opening <node> tag");
		}
		objectBeingImported.processCloseTag(tag);
		extractUser();
		objectReadCallback.nodeObjectReadFromFile((NodeOsmObject) objectBeingImported);
		objectBeingImported = null;
	}

	private void addTag(String tag) throws UnexpectedTokenException {
		if (objectBeingImported == null) {
			throw new UnexpectedTokenException(lineCount + ": tag <tag> found outside any tagged object");
		}
		if (objectBeingImported instanceof NodeOsmObject) {
			objectBeingImported.addTagFromString(tag);
		}
	}
	
	private void extractUser() throws ErrorInsertingDataToDatabase {
		Integer uid = Integer.parseInt(objectBeingImported.getPropertyByKey("uid"));
		String userName = objectBeingImported.getPropertyByKey("user");
		OsmUser user = new OsmUser(uid, userName);
		objectReadCallback.userObjectReadFromFile(user);

	}

	private boolean lineContainsCloseTag(String line) {
		return line.endsWith("/>");
	}

}
