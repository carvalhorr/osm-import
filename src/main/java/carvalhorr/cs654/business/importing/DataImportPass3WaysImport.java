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
import carvalhorr.cs654.model.RelationOsmObject;
import carvalhorr.cs654.model.WayOsmObject;
import carvalhorr.cs654.persistence.OsmDataPersistence;
import exception.UnexpectedTokenException;

public class DataImportPass3WaysImport {

	private int lineCount = 0;
	private OsmObject objectBeingImported;
	private boolean osmStarted = false;
	
	private OsmObjectsReadCallback objectReadCallback;
	
	public DataImportPass3WaysImport(OsmObjectsReadCallback objectReadCallback) {
		this.objectReadCallback = objectReadCallback;
	}

	public void importFile(String fileName) throws IOException, UnexpectedTokenException, ErrorInsertingDataToDatabase {
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
				addNodeToWay(line);
				break;
			}
			case "<relation": {
				startRelationObject(line);
				break;
			}
			case "</relation>": {
				finishRelationObject(line);
				break;
			}
			case "<member": {
				addMember(line);
				break;
			}
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
		objectBeingImported.processCloseTag(tag);
		objectReadCallback.wayObjectReadFromFile((WayOsmObject) objectBeingImported);
		objectBeingImported = null;
	}

	private void addNodeToWay(String tag) throws UnexpectedTokenException {
		/*if (!(objectBeingImported instanceof WayOsmObject)) {
			throw new UnexpectedTokenException(lineCount + ": <nd> tag found outside <way> tag");
		}
		((WayOsmObject) objectBeingImported).addNodeFromString(tag);*/
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
		objectReadCallback.nodeObjectReadFromFile((NodeOsmObject) objectBeingImported);
		objectBeingImported = null;
	}

	private void addTag(String tag) throws UnexpectedTokenException {
		if (objectBeingImported == null) {
			throw new UnexpectedTokenException(lineCount + ": tag <tag> found outside any tagged object");
		}
		if (objectBeingImported instanceof NodeOsmObject)
		objectBeingImported.addTagFromString(tag);
	}

	private void addMember(String tag) throws UnexpectedTokenException {
		if (!(objectBeingImported instanceof RelationOsmObject)) {
			throw new UnexpectedTokenException(lineCount + ": <member> tag found outside <relation> tag");
		}
		((RelationOsmObject) objectBeingImported).addMember(tag);
	}

	private void startRelationObject(String tag) throws UnexpectedTokenException {
		if (!osmStarted) {
			throw new UnexpectedTokenException(lineCount + ": opening <relation> tag found outside <osm> tag");
		}
		if (objectBeingImported != null) {
			throw new UnexpectedTokenException(lineCount + ": opening <way> tag found inside other object tag");
		}
		objectBeingImported = new RelationOsmObject();
		objectBeingImported.processOpenTag(tag);
		if (lineContainsCloseTag(tag)) {
			finishRelationObject(tag);
		}
	}

	private void finishRelationObject(String tag) throws UnexpectedTokenException {
		if (objectBeingImported == null || !(objectBeingImported instanceof RelationOsmObject)) {
			throw new UnexpectedTokenException(
					lineCount + ": closing <node> tag found without corresponding opening <node> tag");
		}
		objectBeingImported.processCloseTag(tag);
		objectBeingImported = null;

	}

	private boolean lineContainsCloseTag(String line) {
		return line.endsWith("/>");
	}

}
