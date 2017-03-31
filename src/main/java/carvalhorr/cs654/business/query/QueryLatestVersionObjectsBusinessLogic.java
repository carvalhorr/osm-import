package carvalhorr.cs654.business.query;

import java.util.HashMap;
import java.util.Map;

import carvalhorr.cs654.business.ProgressIndicator;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.files.OsmObjectFileWriter;
import carvalhorr.cs654.files.OsmObjectLatestVersionWithNumberUsersCsvWriter;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.persistence.OshQueryPersistence;
import carvalhorr.cs654.persistence.OsmObjectsReadFromDatabaseCallback;

/**
 * FR 9.8
 * 
 * Given an object type and it's ID returns all the versions of this object in
 * GeoJSON format.
 * 
 * @author carvalhorr
 *
 */
public class QueryLatestVersionObjectsBusinessLogic extends QueryBusinessLogic {

	private OshQueryPersistence persistence = null;

	public QueryLatestVersionObjectsBusinessLogic(OsmObjectFileWriter writer, OshQueryPersistence persistence,
			ProgressIndicator progressIndicator) {
		super(progressIndicator);
		super.writer = writer;
		this.persistence = persistence;
	}

	@Override
	protected void executeQuery() throws ErrorReadingDataFromDatabase, NotConnectedToDatabase,
			ErrorProcessingReadObjectException, ErrorWritingToFileException {

		persistence.queryAllObjectCurrentVersion(new OsmObjectsReadFromDatabaseCallback() {

			@Override
			public void osmObjectRead(OsmObject object, Map<String, Object> additionalInfo, boolean isFirst)
					throws ErrorProcessingReadObjectException {
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

	}

}
