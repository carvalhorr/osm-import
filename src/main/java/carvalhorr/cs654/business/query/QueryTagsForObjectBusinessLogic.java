package carvalhorr.cs654.business.query;

import java.util.List;
import java.util.Map;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectTagsWriter;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;
import carvalhorr.cs654.persistence.OsmObjectsReadFromDatabaseCallback;

/**
 * FR 9.3
 * 
 * Given a valid object ID the software will automatically return a JSON file
 * containing the tags for all versions of that object as contained in the
 * history file.
 * 
 * @author carvalhorr
 *
 */
public class QueryTagsForObjectBusinessLogic extends QueryBusinessLogic {

	private OsmObjectType type;
	private long objectId;

	private OshQueryPersistence persistence = null;

	public QueryTagsForObjectBusinessLogic(OsmObjectType type, long objectId, OsmObjectFileWriter writer,
			OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
		super(progressIndicator);
		this.type = type;
		this.objectId = objectId;
		super.writer = writer;
		this.persistence = persistence;

		List<String> allTags;
		try {
			allTags = persistence.queryAllTagsForObject(type, objectId);
			((OsmObjectTagsWriter) writer).setTagList(allTags);
		} catch (ErrorReadingDataFromDatabase e) {
			e.printStackTrace();
		}
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
