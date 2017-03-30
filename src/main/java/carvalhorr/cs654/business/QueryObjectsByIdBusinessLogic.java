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
 * FR 9.1
 * 
 * Given an object type and it's ID returns all the versions of this object in
 * GeoJSON format.
 * 
 * @author carvalhorr
 *
 */
public class QueryObjectsByIdBusinessLogic extends BaseBusinessLogic {

	private OshQueryPersistence persistence = null;

	public QueryObjectsByIdBusinessLogic(OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
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
	public void queryObjectsById(ExportFormatType format, OsmObjectType type, long id, final String fileName)
			throws FailedToCompleteQueryException {
		OsmObjectFileWriter writer = OsmObjectWriterFactory.getOsmObjectWriter(format, fileName);
		queryObjectsById(type, id, writer);
	}

	private void queryObjectsById(OsmObjectType type, long id, final OsmObjectFileWriter writer)
			throws FailedToCompleteQueryException {

		try {
			writer.startWritingFile();
			persistence.queryObjectsById(type, id, new OsmObjectsReadFromDatabaseCallback() {

				@Override
				public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
						throws ErrorProcessingReadObjectException {
					writer.writeObject(object, isFirst);
				}

			});

			writer.finishWritingFile();
			sendMessage("Query finished.");
			sendMessage("File saved in:" + writer.getFullFileName());

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
	public void queryObjectsById(ExportFormatType format, OsmObjectType type, long id)
			throws FailedToCompleteQueryException {
		String fileName = "";
		switch (type) {
		case NODE: {
			fileName = "node-" + id + "-all-versions";
			break;
		}
		case WAY: {
			fileName = "way-" + id + "-all-versions";
			break;
		}
		default:
			break;
		}
		queryObjectsById(format, type, id, fileName);
	}

}
