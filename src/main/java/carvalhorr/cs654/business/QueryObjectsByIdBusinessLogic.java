package carvalhorr.cs654.business;

import java.sql.SQLException;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.OsmObjectCsvWriter;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectGeoJsonWriter;
import carvalhorr.cs654.files.OsmObjectJsonWriter;
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
		OsmObjectFileWriter writer = getFileWriterForExportType(format, fileName, id);
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
			fileName = defaultWorkingDirectory + "nodes-" + id + format.toString();
			break;
		}
		case WAY: {
			fileName = defaultWorkingDirectory + "ways-" + id + format.toString();
			break;
		}
		default:
			break;
		}
		OsmObjectFileWriter writer = getFileWriterForExportType(format, fileName, id);
		queryObjectsById(type, id, writer);
	}
	
	private OsmObjectFileWriter getFileWriterForExportType(ExportFormatType format, String fileName, long objectId)
			throws FailedToCompleteQueryException {

		OsmObjectFileWriter fileWriter = null;
		try {
			switch (format) {
			case CSV: {
				fileWriter = new OsmObjectCsvWriter(fileName);
				break;
			}
			case GEOJSON: {
				fileWriter = new OsmObjectGeoJsonWriter(fileName);
				break;
			}
			case JSON: {
				fileWriter = new OsmObjectJsonWriter(fileName);
				break;
			}
			default:
				break;
			}
		} catch (ErrorWritingToFileException e) {
			throw new FailedToCompleteQueryException(e);
		}
		return fileWriter;
	}

}
