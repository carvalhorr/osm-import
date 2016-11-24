package carvalhorr.cs654.business;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import carvalhorr.cs654.exception.ErrorWritingToFileException;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.geojson.model.GeoJsonObject;
import carvalhorr.cs654.model.EditRank;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.persistence.OshQueryPersistence;

public class AllQueryBusinessLogicStubs {

	private String schema;

	private String workingDirectory;

	private OshQueryPersistence persistence = null;

	public AllQueryBusinessLogicStubs(OshQueryPersistence persistence, String workingDirectory, String schema) {
		this.persistence = persistence;
		this.workingDirectory = workingDirectory;
		this.schema = schema;
	}

	/**
	 * FR 9.1
	 * 
	 * Given an object type and it's ID returns all the versions of this object
	 * in GeoJSON format.
	 * 
	 * @param type
	 *            the type of the object to query.
	 * @param id
	 *            the id of the object to query.
	 * @return The GeoJson data for all versions of the specified object.
	 * @throws NotConnectedToDatabase
	 * @throws SQLException
	 * 
	 */
	public void queryObjectsById(OsmObjectType type, long id, final String fileName)
			throws SQLException, NotConnectedToDatabase, ErrorWritingToFileException {
	}

	/**
	 * FR 9.2
	 * 
	 * Given an object type and it's ID returns it's first and last versions in
	 * GeoJSON format.
	 * 
	 * @param type
	 *            the type of the object to query.
	 * @param id
	 *            the id of the object to query.
	 * @return The GeoJson data for the versions of the objects.
	 */
	public GeoJsonObject queryFirstAndLastVersionsOfObjectById(OsmObjectType type, long id) {
		return null;
	}

	/**
	 * FR 9.3
	 * 
	 * Given an object type and it's ID returns a list of tags for all object
	 * versions.
	 * 
	 * @param type
	 *            the type of the object to query.
	 * @param id
	 *            the id of the object to query.
	 * @return List of tags
	 */
	public List<String> queryAllTagsForAllVersionsOfObject(OsmObjectType type, long id) {
		return null;
	}

	/**
	 * FR 9.4
	 * 
	 * Given a valid USER ID, returns all edits performed by that user.
	 * 
	 * @param userId
	 *            the user id for which to get all the edits.
	 * @return List of EditInfo performed by the user.
	 */
	/*public List<EditInfo> queryEditsByUser(String userId) {
		return null;
	}*/

	/**
	 * RF 9.5
	 * 
	 * Return list of all the user that edited the area (identified by the
	 * schema) ranked by the number of edits.
	 * 
	 * @return list of users who edited the area
	 */
	public List<EditRank> queryEditsByUserRanks() {
		return null;
	}

	/**
	 * FR 9.6
	 * 
	 * Given a period, return a summary of all edits that happened in that
	 * period.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return list of summary information
	 */
	public List<String> queryEditingSummaryForPeriod(Date startDate, Date endDate) {
		return null;
	}

	/**
	 * FR 9.7
	 * 
	 * Returns all edits that matches a provided key-value pair.
	 * 
	 * @param key
	 *            the key to look for edits.
	 * @param value
	 *            the value that the key was edit.
	 * @return list of edits matching the key-value pair
	 */
	/*public List<EditInfo> queryAllEditsByKeyValue(String key, String value) {
		return null;
	}*/

	/**
	 * FR 9.8
	 * 
	 * Returns rank of most edited objects.
	 * 
	 * @return rank of most edited objects.
	 */
	public List<String> queryRankDistinctUsersEditedObject() {
		return null;
	}

}
