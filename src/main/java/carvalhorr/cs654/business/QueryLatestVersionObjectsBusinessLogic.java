package carvalhorr.cs654.business;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.ExportFormatType;
import carvalhorr.cs654.files.OsmObjectLatestVersionWithNumberUsersCsvWriter;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectWriterFactory;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectsReadFromDatabaseCallback;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.8
 * 
 * Given an object type and it's ID returns all the versions of this object in
 * GeoJSON format.
 * 
 * @author carvalhorr
 *
 */
public class QueryLatestVersionObjectsBusinessLogic extends BaseBusinessLogic {

	private OshQueryPersistence persistence = null;

	public QueryLatestVersionObjectsBusinessLogic(OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
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
	public void queryLatestVersionAllObjects(ExportFormatType format, final String fileName)
			throws FailedToCompleteQueryException {
		OsmObjectFileWriter writer = null;
		// For CSV format, not the default CSV writer is used.
		if (format.equals(ExportFormatType.CSV)) {
			writer = new OsmObjectLatestVersionWithNumberUsersCsvWriter(fileName);
		} else {
			writer = OsmObjectWriterFactory.getOsmObjectWriter(format, fileName);
		}

		queryLatestVersionAllObjects(writer);
	}

	private void queryLatestVersionAllObjects(final OsmObjectFileWriter writer) throws FailedToCompleteQueryException {

		try {
			writer.startWritinFile();

			persistence.queryAllObjectCurrentVersion(new OsmObjectsReadFromDatabaseCallback() {

				@Override
				public void osmObjectRead(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException {
				}

				@Override
				public void osmObjectReadWithAdditionalInfo(OsmObject object, Map<String, Object> additionalInfo,
						boolean isFirst) throws ErrorProcessingReadObjectException {
					if (writer instanceof OsmObjectLatestVersionWithNumberUsersCsvWriter) {
						Map<String, Object> osmObjectWithExtraInfo = new HashMap<String, Object>();
						osmObjectWithExtraInfo.put("osmObject", object);
						osmObjectWithExtraInfo.put("totalUsers", additionalInfo.get("totalUsers"));
						writer.writeObject(osmObjectWithExtraInfo, isFirst);
					} else {
						writer.writeObject(object, isFirst);
					}
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
	 * @throws FailedToCompleteQueryException
	 * 
	 * @throws SQLException
	 * @throws NotConnectedToDatabase
	 * @throws ErrorWritingToFileException
	 */
	public void queryLatestVersionAllObjects(ExportFormatType format) throws FailedToCompleteQueryException {
		String fileName = "all-objects-latest-version";
		queryLatestVersionAllObjects(format, fileName);
	}

}
