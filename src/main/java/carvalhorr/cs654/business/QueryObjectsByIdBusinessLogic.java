package carvalhorr.cs654.business;

import java.sql.SQLException;

import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.FailedToCompleteQueryException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.geojson.GeoJsonWriter;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.model.OsmObjectsReadFromDatabaseCallback;
import carvalhorr.cs654.persistence.OshQueryPersistence;

/**
 * FR 9.1
 * 
 * Given an object type and it's ID returns all the versions of this object in
 * GeoJSON format.
 * 
 * @author carvalhorr
 *
 */
public class QueryObjectsByIdBusinessLogic {

	private String workingDirectory;

	private OshQueryPersistence persistence = null;

	public QueryObjectsByIdBusinessLogic(OshQueryPersistence persistence, String workingDirectory) {
		this.persistence = persistence;
		this.workingDirectory = workingDirectory;
	}

	/**
	 * 
	 * Calls the persistence to retrieve the objects from the database and use
	 * the GeoJsonWriter to write to the file name specified.
	 * 
	 * @param type
	 *            the type of the object to query.
	 * @param id
	 *            the id of the object to query.
	 * @return The GeoJson data for all versions of the specified object.
	 * @throws FailedToCompleteQueryException
	 * @throws NotConnectedToDatabase
	 * @throws SQLException
	 * 
	 */
	public void queryObjectsById(OsmObjectType type, long id, final String fileName)
			throws FailedToCompleteQueryException {

		try {
			final GeoJsonWriter geoJsonWriter = new GeoJsonWriter(workingDirectory, fileName);

			persistence.queryObjectsById(type, id, new OsmObjectsReadFromDatabaseCallback() {

				@Override
				public void osmObjectRead(OsmObject object, boolean isFirst) throws ErrorProcessingReadObjectException {
					geoJsonWriter.writeGeoJsonObject(object, isFirst);
				}

			});

			geoJsonWriter.finishWritingFile();
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

	/**
	 * 
	 * Generates a file name based on the id and type and calls the method to
	 * retrieve the data and write to the file.
	 * 
	 * @param type
	 * @param id
	 * @throws FailedToCompleteQueryException
	 * 
	 * @throws SQLException
	 * @throws NotConnectedToDatabase
	 * @throws ErrorWritingToFileException
	 */
	public void queryObjectsById(OsmObjectType type, long id) throws FailedToCompleteQueryException {
		String fileName = "";
		switch (type) {
		case NODE: {
			fileName = "nodes-" + id + ".geojson";
			break;
		}
		case WAY: {
			fileName = "ways-" + id + ".geojson";
			break;
		}
		default:
			break;
		}
		queryObjectsById(type, id, fileName);
	}

}
