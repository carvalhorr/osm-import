package carvalhorr.cs654.business;

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
import carvalhorr.cs654.model.OsmObjectsReadFromDatabaseCallback;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.4
 * 
 * 
 * @author carvalhorr
 *
 */
public class QueryAllEditsPerformedByUserBusinessLogic extends BaseBusinessLogic {

	private OshQueryPersistence persistence = null;

	public QueryAllEditsPerformedByUserBusinessLogic(OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
		super(progressIndicator);
		this.persistence = persistence;
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
		OsmObjectFileWriter fileWriter = OsmObjectWriterFactory.getOsmObjectWriter(format, fileName);
		exportAllEditsPerformedByUSer(userId, fileWriter);
	}

	public void exportAllEditsPerformedByUSer(ExportFormatType format, long userId)
			throws FailedToCompleteQueryException {
		String fileName = "changes-by-user-" + userId + "." + format.toString();
		exportAllEditsPerformedByUSer(format, userId, fileName);
	}

	private void exportAllEditsPerformedByUSer(long userId, final OsmObjectFileWriter fileWriter)
			throws FailedToCompleteQueryException {

		try {
			fileWriter.startWritinFile();
			persistence.queryEditsByUser(userId, new OsmObjectsReadFromDatabaseCallback() {

				@Override
				public void osmObjectRead(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException {
					fileWriter.writeObject(object, isFirst);
				}

				@Override
				public void osmObjectReadWithAdditionalInfo(OsmObject object, Map<String, Object> additionalInfo,
						boolean isFirst) throws ErrorProcessingReadObjectException {
					// TODO Auto-generated method stub
				}

			});

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

}
