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
import carvalhorr.cs654.files.UserEditsRankingCsvWriter;
import carvalhorr.cs654.model.DataReadFromDatabaseCallback;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.5
 * 
 * @author carvalhorr
 *
 */

public class QueryRankingUserEditsBusinessLogic {

	private OshQueryPersistence persistence = null;
	
	private String defaultWorkingDirectory = "";

	public QueryRankingUserEditsBusinessLogic(OshQueryPersistence persistence, String defaultWorkingDirectory) {
		this.persistence = persistence;
		this.defaultWorkingDirectory = defaultWorkingDirectory;
	}

	public void queryRankingUserEdits(String fileName)
			throws FailedToCompleteQueryException {
		try {

			final UserEditsRankingCsvWriter writer = new UserEditsRankingCsvWriter(fileName);
			
			writer.startWritinFile();
			persistence.queryRankingEditsByUser(new DataReadFromDatabaseCallback() {
				
				@Override
				public void dataRead(Map<String, Object> properties, boolean isFirst) throws ErrorProcessingReadObjectException {
					writer.writeObject(properties, isFirst);
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

	public void queryRankingUserEdits() throws FailedToCompleteQueryException {
		String fileName = defaultWorkingDirectory + "ranking-user-edits.csv";
		queryRankingUserEdits(fileName);
	}

}
