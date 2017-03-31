package carvalhorr.cs654.business.query;

import java.util.Map;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;
import carvalhorr.cs654.persistence.OsmObjectsReadFromDatabaseCallback;

/**
 * FR 9.1
 * 
 * Given an object type and it's ID returns all the versions of this object in
 * GeoJSON format.
 * 
 * @author carvalhorr
 *
 */
public class QueryObjectsByIdBusinessLogic extends QueryBusinessLogic {

	private OsmObjectType type;
	private long objectId;

	private OshQueryPersistence persistence = null;

	public QueryObjectsByIdBusinessLogic(OsmObjectType type, long objectId, OsmObjectFileWriter writer,
			OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
		super(progressIndicator);
		this.type = type;
		this.objectId = objectId;
		super.writer = writer;
		this.persistence = persistence;
	}

	@Override
	protected void executeQuery() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException, ErrorWritingToFileException {
		persistence.queryObjectsById(type, objectId, new OsmObjectsReadFromDatabaseCallback() {

			@Override
			public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
					throws ErrorProcessingReadObjectException {
				writer.writeObject(object, isFirst);
			}

		});

	}

}
