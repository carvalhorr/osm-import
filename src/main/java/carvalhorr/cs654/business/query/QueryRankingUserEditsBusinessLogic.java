package carvalhorr.cs654.business.query;

import java.util.Map;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.persistence.DataReadFromDatabaseCallback;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.5
 * 
 * @author carvalhorr
 *
 */

public class QueryRankingUserEditsBusinessLogic extends QueryBusinessLogic {

	private OshQueryPersistence persistence = null;

	public QueryRankingUserEditsBusinessLogic(OsmObjectFileWriter writer, OshQueryPersistence persistence,
			ProgressIndicator progressIndicator) {
		super(progressIndicator);
		super.writer = writer;
		this.persistence = persistence;
	}

	@Override
	protected void executeQuery() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException, ErrorWritingToFileException {
		persistence.queryRankingEditsByUser(new DataReadFromDatabaseCallback() {

			@Override
			public void dataRead(Map<String, Object> properties, boolean isFirst)
					throws ErrorProcessingReadObjectException {
				writer.writeObject(properties, isFirst);
			}
		});

	}

}
