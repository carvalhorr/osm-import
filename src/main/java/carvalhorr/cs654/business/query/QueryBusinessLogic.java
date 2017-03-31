package carvalhorr.cs654.business.query;

import carvalhorr.cs654.business.BaseBusinessLogic;
import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.OsmObjectFileWriter;

public abstract class QueryBusinessLogic extends BaseBusinessLogic {

	protected OsmObjectFileWriter writer;

	public QueryBusinessLogic(ProgressIndicator progressIndicator) {
		super(progressIndicator);
	}

	public void queryDataAndExportToFile() throws FailedToCompleteQueryException {

		try {

			writer.startWritingFile();

			executeQuery();

			writer.finishWritingFile();

			sendMessage("Query finished.");
			sendMessage("File saved in: " + writer.getFullFileName());

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

	protected abstract void executeQuery() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException, ErrorWritingToFileException;

}
