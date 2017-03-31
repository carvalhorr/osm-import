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
 * FR 9.2
 * 
 * Given an object type and ID returns its first and last versions in GeoJSON
 * format.
 * 
 * @author carvalhorr
 *
 */
public class QueryFirstAndLastVersionOfObjectBusinessLogic extends QueryBusinessLogic {

	private OsmObjectType type;
	private long id;

	private OsmObject firstVersion = null;
	private OsmObject lastVersion = null;

	private OshQueryPersistence persistence = null;

	public QueryFirstAndLastVersionOfObjectBusinessLogic(OsmObjectType type, long id, OsmObjectFileWriter writer,
			OshQueryPersistence persistence, ProgressIndicator progressIndicator) {
		super(progressIndicator);
		this.type = type;
		this.id = id;
		super.writer = writer;
		this.persistence = persistence;
	}

	@Override
	protected void executeQuery() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException, ErrorWritingToFileException {

		persistence.queryObjectsById(type, id, new OsmObjectsReadFromDatabaseCallback() {

			@Override
			public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst) {

				if (isFirst) {
					firstVersion = object;
					lastVersion = object;
				} else {
					if (object.getVersion() > lastVersion.getVersion()) {
						lastVersion = object;
					} else if (object.getVersion() < firstVersion.getVersion()) {
						firstVersion = object;
					}
				}
			}
		});
		if (firstVersion != null) {
			writer.writeObject(firstVersion, true);
		}
		if (lastVersion != null) {
			writer.writeObject(lastVersion, false);
		}

	}

}
