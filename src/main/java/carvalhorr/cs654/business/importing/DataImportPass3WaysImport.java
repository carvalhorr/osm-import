package carvalhorr.cs654.business.importing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import carvalhorr.cs654.exception.ErrorInsertingDataToDatabase;
import carvalhorr.cs654.model.OsmObjectsReadFromFileCallback;
import carvalhorr.cs654.model.WayOsmObject;
import carvalhorr.cs654.osh.OshProcessor;
import exception.UnexpectedTokenException;

public class DataImportPass3WaysImport extends DataImportPass {

	public DataImportPass3WaysImport(String fileName, OsmObjectsReadFromFileCallback objectReadCallback) {
		super(fileName, objectReadCallback);
	}

	public void importFile() throws IOException, UnexpectedTokenException, ErrorInsertingDataToDatabase {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();

		do {
			lineCount++;

			String lineStartsWith = line.trim().split(" ")[0];

			switch (lineStartsWith) {
			case "<osm": {
				openOsm();
				break;
			}
			case "</osm>": {
				closeOsm();
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
			default:
			}

			line = reader.readLine();
		} while (line != null);

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
		OshProcessor.addTagFromString(objectBeingImported, tag);
		objectReadCallback.wayObjectReadFromFile((WayOsmObject) objectBeingImported);
		objectBeingImported = null;
	}

	private void addNodeToWay(String tag) throws UnexpectedTokenException {
		if (!(objectBeingImported instanceof WayOsmObject)) {
			throw new UnexpectedTokenException(lineCount + ": <nd> tag found outside <way> tag");
		}
		OshProcessor.addNodeToWayFromString((WayOsmObject) objectBeingImported, tag);
	}

	private void addTag(String tag) throws UnexpectedTokenException {
		if (objectBeingImported instanceof WayOsmObject)
			OshProcessor.addTagFromString(objectBeingImported, tag);
	}

}
