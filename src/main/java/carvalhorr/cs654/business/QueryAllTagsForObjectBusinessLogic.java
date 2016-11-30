package carvalhorr.cs654.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.ObjectTagsJsonWriter;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.3
 * 
 * Given an object type and ID returns its first and last versions in GeoJSON
 * format.
 * 
 * @author carvalhorr
 *
 */
@Deprecated
public class QueryAllTagsForObjectBusinessLogic {

	// TODO: Confirm ... Seems I understood this wrong. A JSON format was added
	// to ExportFormatType and also a new class OsmObjectJsonWriter.

	private OshQueryPersistence persistence = null;
	
	private String defaultWorkingDirectory = "";

	public QueryAllTagsForObjectBusinessLogic(OshQueryPersistence persistence, String defaultWorkingDirectory) {
		this.persistence = persistence;
		this.defaultWorkingDirectory = defaultWorkingDirectory;
	}

	/**
	 * FR 9.3
	 * 
	 * Given an object type and it's ID returns a list of tags for all object
	 * versions.
	 * 
	 * @param type
	 *            the type of the object to query.
	 * @param id
	 *            the id of the object to query.
	 * @return List of tags
	 * @throws FailedToCompleteQueryException
	 */
	public void queryAllTagsForAllVersionsOfObject(OsmObjectType type, long id, String fileName)
			throws FailedToCompleteQueryException {
		try {
			final ObjectTagsJsonWriter objectWriter = new ObjectTagsJsonWriter(fileName);

			List<String> tags = persistence.queryTagsForObject(type, id);

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("id", id);
			properties.put("type", (type.equals(OsmObjectType.NODE) ? "Node" : "Way"));
			properties.put("tags", tags.toString().replace("[", "\"").replace("]", "\"").replace(", ", "\", \""));

			objectWriter.startWritinFile();
			
			objectWriter.writeObject(properties, false);

			objectWriter.finishWritingFile();
		} catch (ErrorProcessingReadObjectException e) {
			throw new FailedToCompleteQueryException(e);
		} catch (ErrorWritingToFileException e) {
			throw new FailedToCompleteQueryException(e);
		} catch (NotConnectedToDatabase e) {
			throw new FailedToCompleteQueryException(e);
		} catch (ErrorReadingDataFromDatabase e) {
			throw new FailedToCompleteQueryException(e);
		}
	}

	public void queryAllTagsForAllVersionsOfObject(OsmObjectType type, long id) throws FailedToCompleteQueryException {
		String fileName = "";
		switch (type) {
		case NODE: {
			fileName = defaultWorkingDirectory + "node-" + id + "-tags.json";
			break;
		}
		case WAY: {
			fileName = defaultWorkingDirectory + "way-" + id + "-tags.json";
			break;
		}
		default:
			break;
		}
		queryAllTagsForAllVersionsOfObject(type, id, fileName);
	}

}
