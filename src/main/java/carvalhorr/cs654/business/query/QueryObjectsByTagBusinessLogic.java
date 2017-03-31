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
 * FR 9.7
 * 
 * @author carvalhorr
 *
 */
public class QueryObjectsByTagBusinessLogic extends QueryBusinessLogic {

	private String tagName;
	private String tagValue;

	private OshQueryPersistence persistence = null;

	public QueryObjectsByTagBusinessLogic(String tagName, String tagValue, OsmObjectFileWriter writer,
			OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
		super(progressIndicator);
		this.tagName = tagName;
		this.tagValue = tagValue;
		super.writer = writer;
		this.persistence = persistence;
	}

	@Override
	protected void executeQuery() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException, ErrorWritingToFileException {

		persistence.queryObjectsByTagValue(tagName, tagValue, new OsmObjectsReadFromDatabaseCallback() {

			@Override
			public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
					throws ErrorProcessingReadObjectException {
				writer.writeObject(object, isFirst);
			}

		});

	}

}
