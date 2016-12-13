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
import carvalhorr.cs654.model.OsmObjectsReadFromFileCallback;
import carvalhorr.cs654.model.WayOsmObject;
import carvalhorr.cs654.osh.OshProcessor;
import carvalhorr.cs654.osh.PropertiesExtractor;
import exception.UnexpectedTokenException;

public class DataImportPass2NodesAndUsersImport extends DataImportPass {

	public DataImportPass2NodesAndUsersImport(String fileName, OsmObjectsReadFromFileCallback objectReadCallback) {
		super(fileName, objectReadCallback);
	
	}

	public void importFile() throws IOException, UnexpectedTokenException, ErrorInsertingDataToDatabase {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();
		List<String> types = new ArrayList<String>();
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
				processBounds(line);
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
				// addNodeToWay(line);
				break;
			} /*
				 * case "<relation": { //startRelationObject(line); break; }
				 * case "</relation>": { //finishRelationObject(line); break; }
				 * case "<member": { //addMember(line); break; }
				 */
			default:
				//throw new UnexpectedTokenException(
				//		lineCount + ": unexpected token " + lineStartsWith.toUpperCase() + "found");
			}
			if (!types.contains(lineStartsWith)) {
				types.add(lineStartsWith);
			}

			line = reader.readLine();
		} while (line != null);
		//System.out.println("read " + lineCount + " lines in " + (endTime - startTime) + " milliseconds");
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
		OshProcessor.processOpenTag(objectBeingImported, tag);
		if (lineContainsCloseTag(tag)) {
			finishWayObject(tag);
		}
	}

	private void finishWayObject(String tag) throws UnexpectedTokenException, ErrorInsertingDataToDatabase {
		if (objectBeingImported == null || !(objectBeingImported instanceof WayOsmObject)) {
			throw new UnexpectedTokenException(
					lineCount + ": closing <way> tag found without corresponding opening <way> tag");
		}
		// objectBeingImported.processCloseTag(tag);
		//extractUser();
		objectReadCallback.userObjectReadFromFile(objectBeingImported.getUser());
		objectBeingImported = null;
	}
	
	private void processBounds(String bounds)  throws UnexpectedTokenException, ErrorInsertingDataToDatabase {
		Map<String, String> properties = PropertiesExtractor.extractPropertiesFromLine(bounds);
		double minLat, minLon, maxLat, maxLon;
		minLat = Double.parseDouble(properties.get("minlat"));
		minLon = Double.parseDouble(properties.get("minlon"));
		maxLat = Double.parseDouble(properties.get("maxlat"));
		maxLon = Double.parseDouble(properties.get("maxlon"));
		OsmBounds osmBounds = new OsmBounds(minLat, minLon, maxLat, maxLon);
		objectReadCallback.boundsObjectReadfFromFile(osmBounds);
	}

	private void startNodeObject(String tag) throws UnexpectedTokenException, ErrorInsertingDataToDatabase {
		if (!osmStarted) {
			throw new UnexpectedTokenException(lineCount + ": opening <node> tag found outside <osm> tag");
		}
		if (objectBeingImported != null) {
			throw new UnexpectedTokenException(lineCount + ": opening <way> tag found inside other object tag");
		}
		objectBeingImported = new NodeOsmObject();
		OshProcessor.processOpenTag(objectBeingImported, tag);
		if (lineContainsCloseTag(tag)) {
			finishNodeObject(tag);
		}
	}

	private void finishNodeObject(String tag) throws UnexpectedTokenException, ErrorInsertingDataToDatabase {
		if (objectBeingImported == null || !(objectBeingImported instanceof NodeOsmObject)) {
			throw new UnexpectedTokenException(
					lineCount + ": closing <node> tag found without corresponding opening <node> tag");
		}
		OshProcessor.addTagFromString(objectBeingImported, tag);
		objectBeingImported.computeCoordinates();
		//extractUser();
		objectReadCallback.userObjectReadFromFile(objectBeingImported.getUser());
		objectReadCallback.nodeObjectReadFromFile((NodeOsmObject) objectBeingImported);
		objectBeingImported = null;
	}

	private void addTag(String tag) throws UnexpectedTokenException {
		//if (objectBeingImported == null) {
		//	throw new UnexpectedTokenException(lineCount + ": tag <tag> found outside any tagged object");
		//}
		if (objectBeingImported instanceof NodeOsmObject) {
			OshProcessor.addTagFromString(objectBeingImported, tag);
			//objectBeingImported.addTagFromString(tag);
		}
	}

}
