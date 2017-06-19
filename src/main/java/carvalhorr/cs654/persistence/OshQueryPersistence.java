package carvalhorr.cs654.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carvalhorr.cs654.exception.ErrorConnectingToDatabase;
import carvalhorr.cs654.exception.ErrorProcessingReadObjectException;
import carvalhorr.cs654.exception.ErrorReadingDataFromDatabase;
import carvalhorr.cs654.exception.NotConnectedToDatabase;
import carvalhorr.cs654.exception.PostgresqlDriverNotFound;
import carvalhorr.cs654.exception.SchemaDoesNotExistException;
import carvalhorr.cs654.model.GeoJsonObjectType;
import carvalhorr.cs654.model.NodeOsmObject;
import carvalhorr.cs654.model.OsmObject;
import carvalhorr.cs654.model.OsmObjectType;
import carvalhorr.cs654.model.OsmUser;
import carvalhorr.cs654.model.WayOsmObject;

/**
 * Persistence class to execute all the queries in the database
 * 
 * @author carvalhorr
 *
 */
public class OshQueryPersistence extends BaseOshDatabasePersistence {

	/**
	 * Constructor
	 * 
	 * @param jdbcString
	 * @param user
	 * @param password
	 * @param schemaName
	 * @throws SQLException
	 * @throws PostgresqlDriverNotFound
	 * @throws ErrorConnectingToDatabase
	 * @throws SchemaDoesNotExistException
	 *             if the schema name provided does not exist
	 */
	public OshQueryPersistence(String jdbcString, String user, String password, String schemaName)
			throws SQLException, PostgresqlDriverNotFound, ErrorConnectingToDatabase, SchemaDoesNotExistException {
		super(jdbcString, user, password, schemaName);
		if (!schemaExists()) {
			throw new SchemaDoesNotExistException();
		}
	}

	/**
	 * Implement functional requirement FR 9.5 at the persistence level.
	 * 
	 * "Provide a CSV file with a ranked list of all users which have edited the
	 * OSM data in the area represented by the imported OSH file"
	 * 
	 * @param callback
	 *            to notify caller as objects are read from database
	 * @throws NotConnectedToDatabase
	 * @throws ErrorReadingDataFromDatabase
	 * @throws ErrorProcessingReadObjectException
	 */
	public void queryRankingEditsByUser(DataReadFromDatabaseCallback callback)
			throws NotConnectedToDatabase, ErrorReadingDataFromDatabase, ErrorProcessingReadObjectException {

		// check for connection
		if (connection == null) {
			throw new NotConnectedToDatabase();
		}

		// execute query
		try {
			ResultSet result = statement.executeQuery("select u.*, (select count(*) from " + schemaName
					+ ".osm_object where user_id = u.user_id) total_edits, " + "(select count(*) from " + schemaName
					+ ".osm_object where user_id = u.user_id and geojson_type = 'P') total_edits_points,"
					+ "(select count(*) from " + schemaName
					+ ".osm_object where user_id = u.user_id and geojson_type = 'L') total_edits_linestrings,"
					+ "(select count(*) from " + schemaName
					+ ".osm_object where user_id = u.user_id and geojson_type = 'Y') total_edits_polygons,"
					+ "(select count(*) from " + schemaName
					+ ".osm_object where user_id = u.user_id and geojson_type = 'M') total_edits_multilines from "
					+ schemaName + ".osm_user u order by total_edits desc;");

			// keep track of the first record in order to report to the callback
			boolean first = true;
			while (result.next()) {
				Map<String, Object> userEdits = new HashMap<String, Object>();
				// User id
				userEdits.put("user_id", result.getInt(1));

				// User name
				userEdits.put("user_name", result.getString(2));

				// Total number of edits performed by user
				userEdits.put("total_edits", result.getInt(3));

				// Total number of points edited by user
				userEdits.put("total_edits_points", result.getInt(4));

				// Total number of linestrings edited by user
				userEdits.put("total_edits_linestrings", result.getInt(5));

				// Total number of polygons edited by user
				userEdits.put("total_edits_polygons", result.getInt(6));

				// Total number of multilines edited by user
				userEdits.put("total_edits_multilines", result.getInt(7));

				// Notify caller of the function of new object read
				callback.dataRead(userEdits, first);

				first = false;
			}
		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase("Error while reading rank of edits by user", ex);
		}
	}

	public List<String> queryAllTagsForObject(OsmObjectType type, long id) throws ErrorReadingDataFromDatabase {
		List<String> tags = new ArrayList<>();
		try {
			// Execute the query
			ResultSet result = statement.executeQuery("select distinct tag_key from " + schemaName + ".osm_tag t, "
					+ schemaName + ".osm_object o where o.object_key = t.object_key and o.osm_type = '"
					+ type.toString() + "' and o.osm_id = " + id + ";");

			// The query return only one record
			while (result.next()) {
				tags.add(result.getString(1));
			}
		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase("Error while tags for " + type.name() + " " + id, ex);
		}
		return tags;
	}

	/**
	 * Implement functional requirement 9.8 at the persistence level.
	 * 
	 * "Provide a ranked list of all distinct objects (nodes, polygons,
	 * polylines) with their current version number (as per the input OSH file)
	 * and the total number of distinct users which edited these objects."
	 * 
	 * @param callback
	 *            to notify as objects are read from the database.
	 * 
	 * @throws ErrorReadingDataFromDatabase
	 * @throws NotConnectedToDatabase
	 * @throws ErrorProcessingReadObjectException
	 */
	public void queryAllObjectCurrentVersion(final OsmObjectsReadFromDatabaseCallback callback)
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {
		try {

			// SQL query
			String sql = "select o.object_key, o.osm_type, o.osm_id, o.osm_version, o.coordinates, o.timestamp, " + ""
					+ "o.user_id, o.visible, o.geojson_type, u.user_name, o.changeset, "
					+ "(select count(distinct o3.user_id) from " + schemaName
					+ ".osm_object o3 where o3.osm_id = o.osm_id) editors from " + schemaName + ".osm_object o, "
					+ schemaName
					+ ".osm_user u where o.user_id = u.user_id and (osm_id, osm_version) in (select o2.osm_id, max(o2.osm_version) from "
					+ schemaName + ".osm_object o2 group by o2.osm_id)" + " order by timestamp;";

			// Execute the query and return for each OSM object additional info
			// regarding the total number of users that edited the object
			baseQueryOsmObjectWhithSql(sql, null, new CustomOsmObjectPropertiesReader() {

				@Override
				public void readExtraInformationForObject(OsmObject object, ResultSet result, boolean isFirst)
						throws ErrorProcessingReadObjectException, SQLException {
					Map<String, Object> additionalInfo = new HashMap<String, Object>();

					// Total number of user who edited the object
					additionalInfo.put("totalUsers", result.getInt(11));

					// Notify caller each object read with its additional info
					callback.osmObjectRead(object, additionalInfo, isFirst);

				}
			});

		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase("Error while reading current versions for all objects", ex);
		}

	}

	/**
	 * Implements functional requirement 9.4 at the persistence level
	 * 
	 * "Given a valid USER ID the software will return a JSON or CSV file
	 * containing each edit performed by that user."
	 * 
	 * @param userId
	 *            user id to query edits
	 * @param callback
	 *            to notify caller as objects are read from database.
	 * @throws ErrorReadingDataFromDatabase
	 * @throws NotConnectedToDatabase
	 * @throws ErrorProcessingReadObjectException
	 */
	public void queryEditsByUser(long userId, OsmObjectsReadFromDatabaseCallback callback)
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {
		try {
			// execute base osm object query with custom where clase
			baseQueryOsmObjectWhithAdditionalWhereClause(" and o.user_id = " + userId, callback);
		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase("Error while reading edits for user " + userId, ex);
		}
	}

	/**
	 * Implements functional requirement 9.6 at the persistence level. This
	 * method implements the total number of edits by object type.
	 * 
	 * "Given a valid date range (start date and end date) the software will
	 * return a file (CSV or structured text) containing a summary of the
	 * editing which has happened during this period."
	 * 
	 * @param type
	 *            type of object to count ... the query is called for each
	 *            object type.
	 * @param startDate
	 * @param finishDate
	 * @return the total number of edits for the specified object type on the
	 *         period informed.
	 * @throws NotConnectedToDatabase
	 * @throws ErrorReadingDataFromDatabase
	 */
	public Long queryEditingSummaryTotalObjectsByTypeAndPeriod(GeoJsonObjectType type, Date startDate, Date finishDate)
			throws NotConnectedToDatabase, ErrorReadingDataFromDatabase {

		// check if connected to the databaase
		if (connection == null) {
			throw new NotConnectedToDatabase();
		}

		// Total number of edits to be returned
		Long totalEdits = 0l;
		try {
			// Execute the query
			ResultSet result = statement.executeQuery("select count(*) totalEdits from " + schemaName
					+ ".osm_object where geojson_type = '" + type.getDatabaseType() + "' and timestamp between '"
					+ startDate.toString() + "' and '" + finishDate.toString() + "';");

			// The query return only one record
			while (result.next()) {
				totalEdits = result.getLong(1);
			}
		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase(
					"Error while reading number of edits of type '" + type.toString() + "'", ex);
		}

		return totalEdits;
	}

	/**
	 * Implements the functional requirement 9.6 at the persistence level. This
	 * method queries the total number of users that edited during the period.
	 * 
	 * "Given a valid date range (start date and end date) the software will
	 * return a file (CSV or structured text) containing a summary of the
	 * editing which has happened during this period."
	 *
	 * @param startDate
	 * @param finishDate
	 * @return the total number of users that edited the area on the period
	 *         informed
	 * @throws NotConnectedToDatabase
	 * @throws ErrorReadingDataFromDatabase
	 */
	public Long queryEditingSummaryTotalDistinctUsersByPeriod(Date startDate, Date finishDate)
			throws NotConnectedToDatabase, ErrorReadingDataFromDatabase {

		// check if connected to the database
		if (connection == null) {
			throw new NotConnectedToDatabase();
		}

		// Total number of users to be returned
		Long totalNumberUsers = 0l;
		try {
			// Execute the query
			ResultSet result = statement.executeQuery("select count(distinct user_id) totalEdits from " + schemaName
					+ ".osm_object " + "where timestamp between '" + startDate.toString() + "' and '"
					+ finishDate.toString() + "';");

			// The query return only one record
			while (result.next()) {
				totalNumberUsers = result.getLong(1);
			}
		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase("Error while reading number of users that edited by period.", ex);
		}

		return totalNumberUsers;

	}

	/**
	 * Implements functional requirement 9.7 at the persistence level.
	 * 
	 * "Given a valid OSM tag (key-value combination) return a CSV file
	 * containing a record of ALL objects (and their versions) where this tag
	 * exists in the history data for this region."
	 * 
	 * @param tagName
	 *            name of the tag to query objects
	 * @param tagValue
	 *            value of the tag to query objects
	 * @param callback
	 *            to totify caller as each object is read from the database
	 * @throws ErrorReadingDataFromDatabase
	 * @throws NotConnectedToDatabase
	 * @throws ErrorProcessingReadObjectException
	 */
	public void queryObjectsByTagValue(String tagName, String tagValue, OsmObjectsReadFromDatabaseCallback callback)
			throws ErrorReadingDataFromDatabase, NotConnectedToDatabase, ErrorProcessingReadObjectException {
		try {
			// sql query string
			String sql = "select o.object_key, o.osm_type, o.osm_id, o.osm_version, o.coordinates, o.timestamp, o.user_id, o.visible, o.geojson_type, u.user_name, o.changeset from "
					+ schemaName + ".osm_object o, " + schemaName + ".osm_user u, " + schemaName
					+ ".osm_tag t where o.user_id = u.user_id " + " and o.object_key = t.object_key and t.tag_key = '"
					+ tagName + "' and tag_value = '" + tagValue + "'" + " order by timestamp;";

			// execute the sql query
			baseQueryOsmObjectWhithSql(sql, callback, null);
		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase("Error while reading edits for for tag " + tagName + "=" + tagValue,
					ex);
		}
	}

	/**
	 * Implements functional requirements 9.1 and 9.2 at the persistence level.
	 * The same query is executed and the business logic layer filter the data
	 * to accomplish functional requirement 9.1 or 9.2.
	 * 
	 * "Given a valid object ID the software will automatically return a GeoJSON
	 * file with all versions of that object contained."
	 * 
	 * @param type
	 * @param id
	 * @param callback
	 * @throws NotConnectedToDatabase
	 * @throws ErrorProcessingReadObjectException
	 * @throws ErrorReadingDataFromDatabase
	 */
	public void queryObjectsById(OsmObjectType type, long id, OsmObjectsReadFromDatabaseCallback callback)
			throws NotConnectedToDatabase, ErrorProcessingReadObjectException, ErrorReadingDataFromDatabase {
		try {
			// Execute the base OSM Object query with the specific where clause
			// to get a specific object.
			baseQueryOsmObjectWhithAdditionalWhereClause("and osm_type = '" + type.toString() + "' and osm_id = " + id,
					callback);
		} catch (SQLException ex) {
			throw new ErrorReadingDataFromDatabase(
					"Error while reading objects of type '" + type.toString() + "' and id = " + id, ex);
		}
	}

	/**
	 * Method to query OSM objects that is reused by other methods.
	 * 
	 * @param additionalWhereClause
	 * @param callback
	 *            to notify caller as each object is read.
	 * @throws NotConnectedToDatabase
	 * @throws SQLException
	 * @throws ErrorProcessingReadObjectException
	 */
	protected void baseQueryOsmObjectWhithAdditionalWhereClause(String additionalWhereClause,
			OsmObjectsReadFromDatabaseCallback callback)
			throws NotConnectedToDatabase, SQLException, ErrorProcessingReadObjectException {

		// Concatenate the base sql query to get OSM objects with the additional
		// where clause.
		String sql = "select o.object_key, o.osm_type, o.osm_id, o.osm_version, o.coordinates, o.timestamp, o.user_id, o.visible, o.geojson_type, u.user_name, o.changeset from "
				+ schemaName + ".osm_object o, " + schemaName + ".osm_user u where o.user_id = u.user_id "
				+ additionalWhereClause + " order by timestamp;";

		// Execute the query
		baseQueryOsmObjectWhithSql(sql, callback, null);
	}

	/**
	 * Base query to read osm objects from the database.
	 * 
	 * @param sql
	 * @param callback
	 *            to notify the caller as each object is read from the datatase.
	 * 
	 * @param customPropertiesReader
	 * @throws NotConnectedToDatabase
	 * @throws SQLException
	 * @throws ErrorProcessingReadObjectException
	 */
	protected void baseQueryOsmObjectWhithSql(String sql, OsmObjectsReadFromDatabaseCallback callback,
			CustomOsmObjectPropertiesReader customPropertiesReader)
			throws NotConnectedToDatabase, SQLException, ErrorProcessingReadObjectException {

		// Check if connected to the database
		if (connection == null) {
			throw new NotConnectedToDatabase();
		}

		// Execute the query to retrieve the objects
		ResultSet result = statement.executeQuery(sql);

		// Create statement to query tags associated with object
		Statement statementTags = connection.createStatement();

		// Keep track of the first read object in order to report this
		// information to the caller.
		boolean first = true;
		while (result.next()) {

			// Create the appropriate object (node or way based on the type)
			OsmObject object = null;
			String object_type = result.getString(2);
			switch (object_type) {
			case "N": {
				object = new NodeOsmObject();
				break;
			}
			case "W": {
				object = new WayOsmObject();
				break;
			}
			default:
				break;
			}
			// read values from database
			object.setId(result.getLong(3));
			object.setVersion(result.getInt(4));
			object.setTimestamp(result.getString(6));
			OsmUser user = new OsmUser(result.getInt(7), result.getString(10));
			object.setUser(user);
			object.setVisible(result.getBoolean(8));
			object.setCoordinates(result.getString(5));
			object.setGeoJsonType(GeoJsonObjectType.getGeoJsonTypeFromDatabaseType(result.getString(9)));
			object.setChangeset(result.getLong(11));

			// Read tags for object
			ResultSet resultTags = statementTags.executeQuery("select tag_key, tag_value from " + schemaName
					+ ".osm_tag where object_key = " + result.getLong(1) + ";");

			Map<String, String> tags = new HashMap<String, String>();
			while (resultTags.next()) {
				tags.put(resultTags.getString(1), resultTags.getString(2));
			}
			object.setTags(tags);

			// If a custom properties reader was provided, use it to read the
			// additional info
			if (customPropertiesReader != null) {
				customPropertiesReader.readExtraInformationForObject(object, result, first);
			}

			// notify the caller of the object read
			if (callback != null) {
				callback.osmObjectRead(object, new HashMap<String, Object>(), first);
			}
			first = false;
		}

	}

	// Some queries require additional information to be read together with each
	// OSM object. In these cases a custom property reader must be provided in
	// order to read the custom properties.
	interface CustomOsmObjectPropertiesReader {
		public void readExtraInformationForObject(OsmObject object, ResultSet result, boolean isFirst)
				throws ErrorProcessingReadObjectException, SQLException;
	}

}
