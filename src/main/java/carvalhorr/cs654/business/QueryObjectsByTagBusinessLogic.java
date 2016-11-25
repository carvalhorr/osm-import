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
 * FR 9.7
 *  
 * @author carvalhorr
 *
 */
public class QueryObjectsByTagBusinessLogic {

	private OshQueryPersistence persistence = null;
	
	private String defaultWorkingDirectory = "";

	public QueryObjectsByTagBusinessLogic(OshQueryPersistence persistence, String defaultWorkingDirectory) {
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
	public void queryObjectsByTag(ExportFormatType format, String tagName, String tagValue, final String fileName)
			throws FailedToCompleteQueryException {
		OsmObjectFileWriter writer = getFileWriterForExportType(format, fileName);
		queryObjectsByTag(tagName, tagValue, writer);
	}
	
	private void queryObjectsByTag(String tagName, String tagValue, final OsmObjectFileWriter writer)
			throws FailedToCompleteQueryException {

		try {

			persistence.queryObjectsByTagValue(tagName, tagValue, new OsmObjectsReadFromDatabaseCallback() {

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
	public void queryObjectsByTag(ExportFormatType format, String tagName, String tagValue) throws FailedToCompleteQueryException {
		String fileName = defaultWorkingDirectory + "tag-" + tagName + "-" + tagValue + "." + format.toString();
		OsmObjectFileWriter writer = getFileWriterForExportType(format, fileName);
		queryObjectsByTag(tagName, tagValue, writer);
	}
	
	private OsmObjectFileWriter getFileWriterForExportType(ExportFormatType format, String fileName)
			throws FailedToCompleteQueryException {

		OsmObjectFileWriter fileWriter = null;
		try {
			switch (format) {
			case CSV: {
				fileWriter = new OsmObjectCsvWriter(fileName);
				break;
			}
			case GEOJSON: {
				// FR 9.1
				fileWriter = new OsmObjectGeoJsonWriter(fileName);
				break;
			}
			case JSON: {
				// FR 9.3
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
