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
import carvalhorr.cs654.model.OsmObjectsReadFromDatabaseCallback;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.1
 * 
 * Given an object type and it's ID returns all the versions of this object in
 * GeoJSON format.
 * 
 * @author carvalhorr
 *
 */
public class QueryObjectsByIdBusinessLogic {

	private OshQueryPersistence persistence = null;
	
	private String defaultWorkingDirectory = "";

	public QueryObjectsByIdBusinessLogic(OshQueryPersistence persistence, String defaultWorkingDirectory) {
		this.persistence = persistence;
		this.defaultWorkingDirectory = defaultWorkingDirectory;
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

			persistence.queryObjectsById(type, id, new OsmObjectsReadFromDatabaseCallback() {

				@Override
				public void osmObjectRead(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException {
					writer.writeObject(object, isFirst);
				}

				@Override
				public void osmObjectReadWithAdditionalInfo(OsmObject object, Map<String, Object> additionalInfo,
						boolean isFirst) throws ErrorProcessingReadObjectException {
					
				}

			});

			writer.finishWritingFile();
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
	public void queryObjectsById(ExportFormatType format, OsmObjectType type, long id) throws FailedToCompleteQueryException {
		String fileName = "";
		switch (type) {
		case NODE: {
			fileName = defaultWorkingDirectory + "nodes-" + id ;
			break;
		}
		case WAY: {
			fileName = defaultWorkingDirectory + "ways-" + id ;
			break;
		}
		default:
			break;
		}
		queryObjectsById(format, type, id, fileName);
	}

}
