package carvalhorr.cs654.business.query;

import java.util.Map;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.persistence.OshQueryPersistence;
import carvalhorr.cs654.persistence.OsmObjectsReadFromDatabaseCallback;

/**
 * FR 9.4
 * 
 * 
 * @author carvalhorr
 *
 */
public class QueryAllEditsPerformedByUserBusinessLogic extends QueryBusinessLogic {

	private long userId;

	private OshQueryPersistence persistence = null;

	public QueryAllEditsPerformedByUserBusinessLogic(long userId, OsmObjectFileWriter writer,
			OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
		super(progressIndicator);
		super.writer = writer;
		this.userId = userId;
		this.persistence = persistence;
	}

	@Override
	public void executeQuery() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException, ErrorWritingToFileException {
		persistence.queryEditsByUser(userId, new OsmObjectsReadFromDatabaseCallback() {

			@Override
			public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
					throws ErrorProcessingReadObjectException {
				writer.writeObject(object, isFirst);
			}

		});
	}

}
