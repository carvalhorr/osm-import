package carvalhorr.cs654.business;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.OsmObjectGeoJsonWriter;
import carvalhorr.cs654.files.OsmObjectJsonWriter;
import carvalhorr.cs654.files.OsmObjectCsvWriter;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectsReadFromDatabaseCallback;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.4
 * 
 * 
 * @author carvalhorr
 *
 */
public class QueryAllEditsPerformedByUserBusinessLogic {

	private OshQueryPersistence persistence = null;
	
	private String defaultWorkingDirectory = "";

	public QueryAllEditsPerformedByUserBusinessLogic(OshQueryPersistence persistence, String defaultWorkingDirectory) {
		this.persistence = persistence;
		this.defaultWorkingDirectory = defaultWorkingDirectory;
	}

	/**
	 * 
	 * @param type
	 *            the type of the object to query.
	 * @param id
	 *            the id of the object to query.
	 * @return List of tags
	 * @throws FailedToCompleteQueryException
	 */
	public void exportAllEditsPerformedByUSer(ExportFormatType format, long userId, String fileName)
			throws FailedToCompleteQueryException {
		OsmObjectFileWriter fileWriter = getFileWriterForExportType(format, fileName);
		exportAllEditsPerformedByUSer(userId, fileWriter);
	}

	public void exportAllEditsPerformedByUSer(ExportFormatType format, long userId)
			throws FailedToCompleteQueryException {
		String fileName = defaultWorkingDirectory + "changes-by-user-" + userId + "." + format.toString();
		OsmObjectFileWriter fileWriter = getFileWriterForExportType(format, fileName);
		exportAllEditsPerformedByUSer(userId, fileWriter);
	}

	private void exportAllEditsPerformedByUSer(long userId, final OsmObjectFileWriter geoJsonWriter)
			throws FailedToCompleteQueryException {

		try {
			persistence.queryEditsByUser(userId, new OsmObjectsReadFromDatabaseCallback() {

				@Override
				public void osmObjectRead(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException {
					geoJsonWriter.writeObject(object, isFirst);
				}

			});

			geoJsonWriter.finishWritingFile();
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
