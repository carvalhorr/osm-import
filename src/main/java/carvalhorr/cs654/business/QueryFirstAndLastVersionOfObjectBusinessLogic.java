package carvalhorr.cs654.business;

import java.sql.SQLException;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;
import carvalhorr.cs654.persistence.OsmObjectsReadFromDatabaseCallback;

/**
 * FR 9.2
 * 
 * Given an object type and ID returns its first and last versions in GeoJSON
 * format.
 * 
 * @author carvalhorr
 *
 */
public class QueryFirstAndLastVersionOfObjectBusinessLogic extends BaseBusinessLogic {

	// TODO Add logic to check if the directory exist and create if it doesn't

	private OsmObject firstVersion = null;
	private OsmObject lastVersion = null;

	private OshQueryPersistence persistence = null;

	public QueryFirstAndLastVersionOfObjectBusinessLogic(OshQueryPersistence persistence,
			ProgressIndicator progressIndicator) {
		super(progressIndicator);
		this.persistence = persistence;
	}

	/**
	 * 
	 * Calls the persistence to retrieve the objects from the database and use
	 * the GeoJsonWriter to write to the file name specified.
	 * 
	 * @param type
	 *            the type of the object to query.
	 * @param id
	 *            the id of the object to query.
	 * @return The GeoJson data for all versions of the specified object.
	 * @throws FailedToCompleteQueryException
	 * @throws NotConnectedToDatabase
	 * @throws SQLException
	 * 
	 */
	public void queryFirstAndLastVersionsOfObject(ExportFormatType format, OsmObjectType type, long id,
			final String fileName) throws FailedToCompleteQueryException {
		OsmObjectFileWriter fileWriter = OsmObjectWriterFactory.getOsmObjectWriter(format, fileName);
		queryFirstAndLastVersionsOfObject(type, id, fileWriter);
	}

	private void queryFirstAndLastVersionsOfObject(OsmObjectType type, long id, final OsmObjectFileWriter fileWriter)
			throws FailedToCompleteQueryException {

		try {
			fileWriter.startWritingFile();
			persistence.queryObjectsById(type, id, new OsmObjectsReadFromDatabaseCallback() {

				@Override
				public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst) {

					if (isFirst) {
						firstVersion = object;
						lastVersion = object;
					} else {
						if (object.getVersion() > lastVersion.getVersion()) {
							lastVersion = object;
						} else if (object.getVersion() < firstVersion.getVersion()) {
							firstVersion = object;
						}
					}
				}
			});
			if (firstVersion != null) {
				fileWriter.writeObject(firstVersion, true);
			}
			if (lastVersion != null) {
				fileWriter.writeObject(lastVersion, false);
			}

			fileWriter.finishWritingFile();
			sendMessage("Query finished.");
			sendMessage("File saved in:" + fileWriter.getFullFileName());

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

	/**
	 * 
	 * Generates a file name based on the id and type and calls the method to
	 * retrieve the data and write to the file.
	 * 
	 * @param type
	 * @param id
	 * @throws FailedToCompleteQueryException
	 * 
	 * @throws SQLException
	 * @throws NotConnectedToDatabase
	 * @throws ErrorWritingToFileException
	 */
	public void queryFirstAndLastVersionsOfObject(ExportFormatType format, OsmObjectType type, long id)
			throws FailedToCompleteQueryException {
		String fileName = "";
		switch (type) {
		case NODE: {
			fileName = "node-" + id + "-first-and-last";
			break;
		}
		case WAY: {
			fileName = "way-" + id + "-first-and-last";
			break;
		}
		default:
			break;
		}
		queryFirstAndLastVersionsOfObject(format, type, id, fileName);
	}

}
